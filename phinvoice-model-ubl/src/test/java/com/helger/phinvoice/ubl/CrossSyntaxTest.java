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
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.Set;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;
import com.helger.base.string.StringImplode;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsTreeSet;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsSortedSet;
import com.helger.diagnostics.error.IError;
import com.helger.diagnostics.error.list.ErrorList;
import com.helger.diver.api.coord.DVRCoordinate;
import com.helger.en16931.basics.EEN16931Edition;
import com.helger.en16931.basics.EEN16931SyntaxKind;
import com.helger.io.resource.ClassPathResource;
import com.helger.io.resource.IReadableResource;
import com.helger.io.resource.inmemory.ReadableResourceByteArray;
import com.helger.phinvoice.cii.CiiInvoiceReader;
import com.helger.phinvoice.cii.CiiInvoiceWriter;
import com.helger.phinvoice.model.PhinvoiceInvoice;
import com.helger.phinvoice.read.IPhinvoiceReader;
import com.helger.phinvoice.target.PhinvoiceExportTarget;
import com.helger.phinvoice.testfiles.MockPhinvoiceTestFiles;
import com.helger.phinvoice.testfiles.PhinvoiceModelDumper;
import com.helger.phive.api.execute.ValidationExecutionManager;
import com.helger.phive.api.executorset.ValidationExecutorSetRegistry;
import com.helger.phive.api.result.ValidationResult;
import com.helger.phive.api.result.ValidationResultList;
import com.helger.phive.api.validity.IValidityDeterminator;
import com.helger.phive.en16931.EN16931Validation;
import com.helger.phive.xml.source.IValidationSourceXML;
import com.helger.phive.xml.source.ValidationSourceXML;
import com.helger.xml.serialize.write.XMLWriter;

/**
 * Cross syntax tests: read a CII invoice, write it as UBL, read it again (and vice versa) and compare
 * the models. For EN 16931:2017 the written document must not fail syntax neutral EN 16931 rules
 * (BR-*) that the source does not fail.
 *
 * @author Philip Helger
 */
public final class CrossSyntaxTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (CrossSyntaxTest.class);

  private static final ValidationExecutorSetRegistry <IValidationSourceXML> VES_REGISTRY = new ValidationExecutorSetRegistry <> ();
  static
  {
    EN16931Validation.initEN16931 (VES_REGISTRY);
  }

  private static final Set <String> IGNORED = Set.of ("additionalTerms");

  @NonNull
  private static PhinvoiceInvoice _read (@NonNull final IPhinvoiceReader <InputStream> aReader,
                                         @NonNull final InputStream aIS,
                                         @NonNull final String sContext)
  {
    final ErrorList aErrors = new ErrorList ();
    final PhinvoiceInvoice ret = aReader.read (aIS, aErrors);
    assertNotNull (sContext + ": " + aErrors.getAllErrors (), ret);
    assertTrue (sContext + ": " + aErrors.getAllErrors (), aErrors.getAllErrors ().isEmpty ());
    return ret;
  }

  /** The syntax neutral rules (BR-*) that fail. */
  @NonNull
  private static ICommonsSortedSet <String> _getFailedBusinessRules (@NonNull final DVRCoordinate aVID,
                                                                     @NonNull final IReadableResource aRes)
  {
    final ValidationResultList aResultList = ValidationExecutionManager.executeValidation (IValidityDeterminator.createDefault (),
                                                                                           VES_REGISTRY.getOfID (aVID),
                                                                                           ValidationSourceXML.create (aRes));
    final ICommonsSortedSet <String> ret = new CommonsTreeSet <> ();
    for (final ValidationResult aResult : aResultList)
      for (final IError aError : aResult.getErrorList ())
        if (aError.isError ())
        {
          final String sID = aError.getErrorID ();
          if (sID == null)
            ret.add ("XSD: " + aError.getErrorText (null));
          else
            if (sID.startsWith ("BR-"))
              ret.add (sID);
        }
    return ret;
  }

  @Nullable
  private static DVRCoordinate _getVID (@NonNull final PhinvoiceExportTarget aTarget)
  {
    if (aTarget.getEdition () != EEN16931Edition.EN2017)
      return null;
    return switch (aTarget.getSyntaxKind ())
    {
      case CII -> EN16931Validation.VID_CII_1317.getWithVersionLatestRelease ();
      case UBL_INVOICE -> EN16931Validation.VID_UBL_INVOICE_1317.getWithVersionLatestRelease ();
      case UBL_CREDIT_NOTE -> EN16931Validation.VID_UBL_CREDIT_NOTE_1317.getWithVersionLatestRelease ();
    };
  }

  private static void _crossSyntax (@NonNull final ICommonsList <ClassPathResource> aFiles,
                                    @NonNull final IPhinvoiceReader <InputStream> aSrcReader,
                                    @NonNull final EEN16931SyntaxKind eSrcSyntaxKind,
                                    @NonNull final IPhinvoiceReader <InputStream> aDstReader,
                                    final boolean bToUBL,
                                    @NonNull final EEN16931Edition eEdition)
  {
    final ICommonsList <String> aProblems = new CommonsArrayList <> ();
    int nDifferentFiles = 0;
    for (final ClassPathResource aRes : aFiles)
    {
      final String sPath = aRes.getPath ();
      final PhinvoiceInvoice aModel1 = _read (aSrcReader, aRes.getInputStream (), sPath);
      final PhinvoiceExportTarget aTarget = new PhinvoiceExportTarget (bToUBL ? UblInvoiceWriter.getSyntaxKind (aModel1)
                                                                              : EEN16931SyntaxKind.CII,
                                                                       eEdition);
      final ErrorList aWriteErrors = new ErrorList ();
      final Document aDoc = bToUBL ? new UblInvoiceWriter (aTarget).write (aModel1, aWriteErrors)
                                   : new CiiInvoiceWriter (aTarget).write (aModel1, aWriteErrors);
      if (aDoc == null)
      {
        aProblems.add (sPath + ": not written: " + aWriteErrors.getAllErrors ());
        continue;
      }
      final byte [] aBytes = XMLWriter.getNodeAsBytes (aDoc);
      final PhinvoiceInvoice aModel2 = _read (aDstReader, new NonBlockingByteArrayInputStream (aBytes), sPath + " (written)");

      // Lines that differ: the values the target cannot carry are reported by the writer
      if (!aTarget.isCompatibleSpecificationIdentifier (aModel1.getProcessControl ().getSpecificationIdentifier ()))
        aModel1.getProcessControl ().setSpecificationIdentifier (aTarget.getSpecificationIdentifier ());
      final ICommonsList <String> aDump1 = PhinvoiceModelDumper.dump (aModel1, IGNORED);
      final ICommonsList <String> aDump2 = PhinvoiceModelDumper.dump (aModel2, IGNORED);
      final ICommonsList <String> aDifferences = new CommonsArrayList <> ();
      for (final String sLine : aDump1)
        if (!aDump2.contains (sLine))
          aDifferences.add ("-" + sLine);
      for (final String sLine : aDump2)
        if (!aDump1.contains (sLine))
          aDifferences.add ("+" + sLine);
      if (aDifferences.isNotEmpty ())
      {
        nDifferentFiles++;
        LOGGER.info (sPath +
                     ": " +
                     aDifferences +
                     " / reported: " +
                     aWriteErrors.getAllMapped (IError::getErrorFieldName));
      }

      final DVRCoordinate aSrcVID = _getVID (new PhinvoiceExportTarget (eSrcSyntaxKind == EEN16931SyntaxKind.CII ? EEN16931SyntaxKind.CII
                                                                                                                 : UblInvoiceWriter.getSyntaxKind (aModel1),
                                                                        eEdition));
      final DVRCoordinate aDstVID = _getVID (aTarget);
      if (aSrcVID != null && aDstVID != null)
      {
        final ICommonsSortedSet <String> aNewFailures = _getFailedBusinessRules (aDstVID,
                                                                                new ReadableResourceByteArray (sPath, aBytes));
        aNewFailures.removeAll (_getFailedBusinessRules (aSrcVID, aRes));
        if (aNewFailures.isNotEmpty ())
          aProblems.add (sPath + ": new EN 16931 rule failures " + aNewFailures);
      }
    }
    LOGGER.info (nDifferentFiles + " of " + aFiles.size () + " files have model differences");
    assertTrue (aProblems.size () + " of " + aFiles.size () + " files have problems:\n" + StringImplode.getImploded ('\n', aProblems),
                aProblems.isEmpty ());
  }

  @Test
  public void testCiiToUbl2017 ()
  {
    _crossSyntax (MockPhinvoiceTestFiles.getCiiD16BFiles (),
                  CiiInvoiceReader.fromD22B (),
                  EEN16931SyntaxKind.CII,
                  UblInvoiceReader.fromUbl21 (),
                  true,
                  EEN16931Edition.EN2017);
  }

  @Test
  public void testZugferdExtendedToUbl2017 ()
  {
    // All values of Factur-X EXTENDED are reported
    _crossSyntax (MockPhinvoiceTestFiles.getCiiZugferd252Files ("extended"),
                  CiiInvoiceReader.fromD22B (),
                  EEN16931SyntaxKind.CII,
                  UblInvoiceReader.fromUbl21 (),
                  true,
                  EEN16931Edition.EN2017);
  }

  @Test
  public void testZugferdExtendedToCii2026 ()
  {
    _crossSyntax (MockPhinvoiceTestFiles.getCiiZugferd252Files ("extended"),
                  CiiInvoiceReader.fromD22B (),
                  EEN16931SyntaxKind.CII,
                  CiiInvoiceReader.fromD25A (),
                  false,
                  EEN16931Edition.EN2026);
  }

  @Test
  public void testUblToCii2017 ()
  {
    _crossSyntax (MockPhinvoiceTestFiles.getAllUbl21Files (),
                  UblInvoiceReader.fromUbl21 (),
                  EEN16931SyntaxKind.UBL_INVOICE,
                  CiiInvoiceReader.fromD22B (),
                  false,
                  EEN16931Edition.EN2017);
  }

  @Test
  public void testCiiToUbl2026 ()
  {
    _crossSyntax (MockPhinvoiceTestFiles.getCiiD25AFiles (),
                  CiiInvoiceReader.fromD25A (),
                  EEN16931SyntaxKind.CII,
                  UblInvoiceReader.fromUbl25 (),
                  true,
                  EEN16931Edition.EN2026);
  }

  @Test
  public void testUblToCii2026 ()
  {
    _crossSyntax (MockPhinvoiceTestFiles.getAllUbl25Files (),
                  UblInvoiceReader.fromUbl25 (),
                  EEN16931SyntaxKind.UBL_INVOICE,
                  CiiInvoiceReader.fromD25A (),
                  false,
                  EEN16931Edition.EN2026);
  }
}
