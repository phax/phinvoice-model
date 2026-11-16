/*
 * Copyright (C) 2026 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.phinvoice.ubl;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.jspecify.annotations.NonNull;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;
import com.helger.base.io.nonblocking.NonBlockingByteArrayOutputStream;
import com.helger.base.io.stream.StreamHelper;
import com.helger.base.string.StringReplace;
import com.helger.collection.commons.CommonsTreeMap;
import com.helger.collection.commons.ICommonsList;
import com.helger.diagnostics.error.list.ErrorList;
import com.helger.en16931.basics.EEN16931Edition;
import com.helger.en16931.basics.EEN16931SyntaxKind;
import com.helger.en16931.cii2ubl.CIIToUBLDispatcher;
import com.helger.en16931.ubl2cii.UBLToCIIDispatcher;
import com.helger.io.file.SimpleFileIO;
import com.helger.io.resource.ClassPathResource;
import com.helger.phinvoice.cii.CiiInvoiceReader;
import com.helger.phinvoice.cii.CiiInvoiceWriter;
import com.helger.phinvoice.model.PhinvoiceInvoice;
import com.helger.phinvoice.read.IPhinvoiceReader;
import com.helger.phinvoice.target.PhinvoiceExportTarget;
import com.helger.phinvoice.testfiles.MockPhinvoiceTestFiles;
import com.helger.phinvoice.testfiles.PhinvoiceModelDumper;
import com.helger.ubl21.UBL21Marshaller;
import com.helger.ubl25.UBL25Marshaller;
import com.helger.xml.serialize.read.DOMReader;
import com.helger.xml.serialize.write.XMLWriter;

/**
 * Compare the cross syntax conversion of phinvoice-model with the production converters
 * en16931-cii2ubl and en16931-ubl2cii: both results are read with the same reader and the models
 * are compared. The differences are logged per property, so that they can be reviewed; the test
 * only fails if a conversion fails.
 *
 * @author Philip Helger
 */
public final class CompareWithConvertersTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (CompareWithConvertersTest.class);
  private static final File TEMP_DIR = new File ("target/test-output/compare");

  // BG-2 is set by the converters themselves
  private static final Set <String> IGNORED = Set.of ("additionalTerms", "processControl");
  private static final Pattern INDEX = Pattern.compile ("\\[\\d+\\]");

  /** The property of a dump line without the list indices, e.g. <code>invoice.lines[].note</code> */
  @NonNull
  private static String _getProperty (@NonNull final String sLine)
  {
    final int nIndex = sLine.indexOf (" = ");
    return INDEX.matcher (nIndex < 0 ? sLine : sLine.substring (0, nIndex)).replaceAll ("[]");
  }

  @NonNull
  private static PhinvoiceInvoice _read (@NonNull final IPhinvoiceReader <InputStream> aReader,
                                         final byte @NonNull [] aBytes,
                                         @NonNull final String sContext)
  {
    final ErrorList aErrors = new ErrorList ();
    final PhinvoiceInvoice ret = aReader.read (new NonBlockingByteArrayInputStream (aBytes), aErrors);
    assertNotNull (sContext + ": " + aErrors.getAllErrors (), ret);
    return ret;
  }

  private static void _compare (@NonNull final String sPath,
                                @NonNull final PhinvoiceInvoice aOurs,
                                @NonNull final PhinvoiceInvoice aTheirs,
                                @NonNull final Map <String, Integer> aCounts)
  {
    final ICommonsList <String> aDump1 = PhinvoiceModelDumper.dump (aOurs, IGNORED);
    final ICommonsList <String> aDump2 = PhinvoiceModelDumper.dump (aTheirs, IGNORED);
    for (final String sLine : aDump1)
      if (!aDump2.contains (sLine))
      {
        final String sKey = "ours only: " + _getProperty (sLine);
        aCounts.merge (sKey, Integer.valueOf (1), (a, b) -> Integer.valueOf (a.intValue () + b.intValue ()));
        LOGGER.debug (sPath + ": ours only " + sLine);
      }
    for (final String sLine : aDump2)
      if (!aDump1.contains (sLine))
      {
        final String sKey = "theirs only: " + _getProperty (sLine);
        aCounts.merge (sKey, Integer.valueOf (1), (a, b) -> Integer.valueOf (a.intValue () + b.intValue ()));
        LOGGER.debug (sPath + ": theirs only " + sLine);
      }
  }

  private static void _ciiToUbl (@NonNull final ICommonsList <ClassPathResource> aFiles,
                                 @NonNull final CiiInvoiceReader aCiiReader,
                                 @NonNull final UblInvoiceReader aUblReader,
                                 @NonNull final EEN16931Edition eEdition)
  {
    final Map <String, Integer> aCounts = new CommonsTreeMap <> ();
    for (final ClassPathResource aRes : aFiles)
    {
      final String sPath = aRes.getPath ();
      final ErrorList aErrors = new ErrorList ();
      final PhinvoiceInvoice aModel = aCiiReader.read (aRes.getInputStream (), aErrors);
      assertNotNull (sPath, aModel);

      // Ours
      final Document aOurDoc = new UblInvoiceWriter (new PhinvoiceExportTarget (UblInvoiceWriter.getSyntaxKind (aModel),
                                                                                eEdition)).write (aModel, aErrors);
      assertNotNull (sPath + ": " + aErrors.getAllErrors (), aOurDoc);

      // Theirs - the converter needs a file
      final File aFile = new File (TEMP_DIR, StringReplace.replaceAll (sPath, "/", "_"));
      SimpleFileIO.writeFile (aFile, StreamHelper.getAllBytes (aRes));
      final Serializable aTheirUBL = new CIIToUBLDispatcher ().setEdition (eEdition).convertCIItoUBL (aFile, aErrors);
      assertNotNull (sPath + ": " + aErrors.getAllErrors (), aTheirUBL);
      final byte [] aTheirBytes;
      if (aTheirUBL instanceof final oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType aInv)
        aTheirBytes = UBL21Marshaller.invoice ().getAsBytes (aInv);
      else
        if (aTheirUBL instanceof final oasis.names.specification.ubl.schema.xsd.creditnote_21.CreditNoteType aCN)
          aTheirBytes = UBL21Marshaller.creditNote ().getAsBytes (aCN);
        else
          if (aTheirUBL instanceof final oasis.names.specification.ubl.schema.xsd.invoice_25.InvoiceType aInv)
            aTheirBytes = UBL25Marshaller.invoice ().getAsBytes (aInv);
          else
            aTheirBytes = UBL25Marshaller.creditNote ()
                                         .getAsBytes ((oasis.names.specification.ubl.schema.xsd.creditnote_25.CreditNoteType) aTheirUBL);
      assertNotNull (sPath, aTheirBytes);

      _compare (sPath,
                _read (aUblReader, XMLWriter.getNodeAsBytes (aOurDoc), sPath + " (ours)"),
                _read (aUblReader, aTheirBytes, sPath + " (en16931-cii2ubl)"),
                aCounts);
    }
    LOGGER.info ("CII to UBL " + eEdition.getID () + ", " + aFiles.size () + " files, differences: " + aCounts);
  }

  private static void _ublToCii (@NonNull final ICommonsList <ClassPathResource> aFiles,
                                 @NonNull final UblInvoiceReader aUblReader,
                                 @NonNull final CiiInvoiceReader aCiiReader,
                                 @NonNull final EEN16931Edition eEdition)
  {
    final Map <String, Integer> aCounts = new CommonsTreeMap <> ();
    for (final ClassPathResource aRes : aFiles)
    {
      final String sPath = aRes.getPath ();
      final ErrorList aErrors = new ErrorList ();
      final PhinvoiceInvoice aModel = aUblReader.read (aRes.getInputStream (), aErrors);
      assertNotNull (sPath, aModel);

      // Ours
      final Document aOurDoc = new CiiInvoiceWriter (new PhinvoiceExportTarget (EEN16931SyntaxKind.CII, eEdition)).write (aModel,
                                                                                                                          aErrors);
      assertNotNull (sPath + ": " + aErrors.getAllErrors (), aOurDoc);

      // Theirs
      final Serializable aTheirCII = UBLToCIIDispatcher.convertUBLtoCII (DOMReader.readXMLDOM (aRes), eEdition, aErrors);
      assertNotNull (sPath + ": " + aErrors.getAllErrors (), aTheirCII);
      try (final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ())
      {
        UBLToCIIDispatcher.writeCII (aTheirCII, aBAOS, aErrors);
        _compare (sPath,
                  _read (aCiiReader, XMLWriter.getNodeAsBytes (aOurDoc), sPath + " (ours)"),
                  _read (aCiiReader, aBAOS.toByteArray (), sPath + " (en16931-ubl2cii)"),
                  aCounts);
      }
    }
    LOGGER.info ("UBL to CII " + eEdition.getID () + ", " + aFiles.size () + " files, differences: " + aCounts);
  }

  @Test
  public void testCiiToUbl2017 ()
  {
    _ciiToUbl (MockPhinvoiceTestFiles.getCiiD16BFiles (),
               CiiInvoiceReader.fromD22B (),
               UblInvoiceReader.fromUbl21 (),
               EEN16931Edition.EN2017);
  }

  @Test
  public void testCiiToUbl2026 ()
  {
    _ciiToUbl (MockPhinvoiceTestFiles.getCiiD25AFiles (),
               CiiInvoiceReader.fromD25A (),
               UblInvoiceReader.fromUbl25 (),
               EEN16931Edition.EN2026);
  }

  @Test
  public void testUblToCii2017 ()
  {
    _ublToCii (MockPhinvoiceTestFiles.getAllUbl21Files (),
               UblInvoiceReader.fromUbl21 (),
               CiiInvoiceReader.fromD22B (),
               EEN16931Edition.EN2017);
  }

  @Test
  public void testUblToCii2026 ()
  {
    _ublToCii (MockPhinvoiceTestFiles.getAllUbl25Files (),
               UblInvoiceReader.fromUbl25 (),
               CiiInvoiceReader.fromD25A (),
               EEN16931Edition.EN2026);
  }
}
