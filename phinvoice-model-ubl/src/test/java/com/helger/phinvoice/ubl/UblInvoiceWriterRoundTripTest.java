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
import java.util.Map;
import java.util.Set;

import org.jspecify.annotations.NonNull;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;
import com.helger.base.string.StringImplode;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsHashSet;
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
import com.helger.phinvoice.model.PhinvoiceInvoice;
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
import com.helger.xml.serialize.read.DOMReader;
import com.helger.xml.serialize.write.XMLWriter;

/**
 * Round trip tests of {@link UblInvoiceWriter}: read a UBL invoice or credit note, write it for an EN 16931
 * target, read the result again and compare the two models. The written document must not
 * introduce EN 16931 rule failures that the source does not have.
 *
 * @author Philip Helger
 */
public final class UblInvoiceWriterRoundTripTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (UblInvoiceWriterRoundTripTest.class);

  private static final ValidationExecutorSetRegistry <IValidationSourceXML> VES_REGISTRY = new ValidationExecutorSetRegistry <> ();
  static
  {
    EN16931Validation.initEN16931 (VES_REGISTRY);
  }

  // The values for which the model has no field are reported by the writer and not written
  private static final Set <String> IGNORED = Set.of ("additionalTerms");

  // The differences that the writer causes on purpose and reports: "-" is a line that is only in
  // the source model, "+" a line that is only in the model of the written document
  private static final Map <String, Set <String>> EXPECTED_DIFFERENCES = Map.of ();

  @NonNull
  private static PhinvoiceInvoice _read (@NonNull final UblInvoiceReader aReader,
                                         @NonNull final InputStream aIS,
                                         @NonNull final String sContext)
  {
    final ErrorList aErrors = new ErrorList ();
    final PhinvoiceInvoice ret = aReader.read (aIS, aErrors);
    assertNotNull (sContext + ": " + aErrors.getAllErrors (), ret);
    assertTrue (sContext + ": " + aErrors.getAllErrors (), aErrors.getAllErrors ().isEmpty ());
    return ret;
  }

  @NonNull
  private static ICommonsSortedSet <String> _getAllFailedRuleIDs (@NonNull final DVRCoordinate aVID,
                                                                  @NonNull final IReadableResource aRes)
  {
    final ValidationResultList aResultList = ValidationExecutionManager.executeValidation (IValidityDeterminator.createDefault (),
                                                                                           VES_REGISTRY.getOfID (aVID),
                                                                                           ValidationSourceXML.create (aRes));
    assertNotNull (aResultList);
    final ICommonsSortedSet <String> ret = new CommonsTreeSet <> ();
    for (final ValidationResult aResult : aResultList)
      for (final IError aError : aResult.getErrorList ())
        if (aError.isError ())
          ret.add (aError.getErrorID () != null ? aError.getErrorID () : aError.getErrorText (null));
    return ret;
  }

  /**
   * @param aVID
   *        The phive validation of the target or <code>null</code> if there is none (yet).
   */
  private static void _roundTrip (@NonNull final ICommonsList <ClassPathResource> aFiles,
                                  @NonNull final UblInvoiceReader aReader,
                                  @NonNull final EEN16931Edition eEdition,
                                  final boolean bValidate)
  {
    final ICommonsList <String> aProblems = new CommonsArrayList <> ();
    for (final ClassPathResource aRes : aFiles)
    {
      final String sPath = aRes.getPath ();
      // The root element of the source decides
      final EEN16931SyntaxKind eSyntaxKind = EEN16931SyntaxKind.getFromNodeOrNull (DOMReader.readXMLDOM (aRes));
      assertNotNull (sPath, eSyntaxKind);
      final PhinvoiceExportTarget aTarget = new PhinvoiceExportTarget (eSyntaxKind, eEdition);
      final UblInvoiceWriter aWriter = new UblInvoiceWriter (aTarget);
      final DVRCoordinate aVID = !bValidate ? null
                                            : eSyntaxKind == EEN16931SyntaxKind.UBL_CREDIT_NOTE ? EN16931Validation.VID_UBL_CREDIT_NOTE_1317.getWithVersionLatestRelease ()
                                                                                                : EN16931Validation.VID_UBL_INVOICE_1317.getWithVersionLatestRelease ();
      final PhinvoiceInvoice aModel1 = _read (aReader, aRes.getInputStream (), sPath);

      final ErrorList aWriteErrors = new ErrorList ();
      final Document aDoc = aWriter.write (aModel1, aWriteErrors);
      assertNotNull (sPath + ": " + aWriteErrors.getAllErrors (), aDoc);
      assertTrue (sPath + ": " + aWriteErrors.getAllErrors (), aWriteErrors.getAllErrors ().isEmpty ());
      final byte [] aBytes = XMLWriter.getNodeAsBytes (aDoc);
      assertNotNull (aBytes);

      final PhinvoiceInvoice aModel2 = _read (aReader, new NonBlockingByteArrayInputStream (aBytes), sPath + " (written)");
      // A BT-24 that does not fit the target is replaced by the writer
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
      final Set <String> aExpected = EXPECTED_DIFFERENCES.getOrDefault (sPath, Set.of ());
      if (!aExpected.equals (new CommonsHashSet <> (aDifferences)))
        aProblems.add (sPath + ": unexpected model differences " + aDifferences);

      if (aVID != null)
      {
        final ICommonsSortedSet <String> aSrcFailures = _getAllFailedRuleIDs (aVID, aRes);
        final ICommonsSortedSet <String> aNewFailures = _getAllFailedRuleIDs (aVID,
                                                                             new ReadableResourceByteArray (sPath, aBytes));
        aNewFailures.removeAll (aSrcFailures);
        if (aNewFailures.isNotEmpty ())
          aProblems.add (sPath + ": new EN 16931 rule failures " + aNewFailures);
      }

      if (aWriteErrors.isNotEmpty ())
        LOGGER.info (sPath +
                     ": " +
                     aWriteErrors.size () +
                     " values not written: " +
                     aWriteErrors.getAllMapped (IError::getErrorFieldName));
    }
    assertTrue (aProblems.size () + " of " + aFiles.size () + " files have problems:\n" + StringImplode.getImploded ('\n', aProblems),
                aProblems.isEmpty ());
  }

  @Test
  public void testUbl21ToEN2017 ()
  {
    final ICommonsList <ClassPathResource> aFiles = MockPhinvoiceTestFiles.getAllUbl21Files ();
    aFiles.addAll (MockPhinvoiceTestFiles.getSyntheticUbl21Files ());
    _roundTrip (aFiles, UblInvoiceReader.fromUbl21 (), EEN16931Edition.EN2017, true);
  }

  @Test
  public void testUbl25ToEN2026 ()
  {
    // phive-rules has no validation artefacts for EN 16931:2026 yet - the XML Schema of UBL 2.5 is
    // checked when writing and reading
    _roundTrip (MockPhinvoiceTestFiles.getAllUbl25Files (), UblInvoiceReader.fromUbl25 (), EEN16931Edition.EN2026, false);
  }
}
