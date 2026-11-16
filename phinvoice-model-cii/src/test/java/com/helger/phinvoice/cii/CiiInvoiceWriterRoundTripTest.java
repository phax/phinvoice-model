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
package com.helger.phinvoice.cii;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.Map;
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
import com.helger.phinvoice.target.EPhinvoiceProfile;
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
 * Round trip tests of {@link CiiInvoiceWriter}: read a CII invoice, write it for an EN 16931
 * target, read the result again and compare the two models. The written document must not
 * introduce EN 16931 rule failures that the source does not have.
 *
 * @author Philip Helger
 */
public final class CiiInvoiceWriterRoundTripTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (CiiInvoiceWriterRoundTripTest.class);

  private static final ValidationExecutorSetRegistry <IValidationSourceXML> VES_REGISTRY = new ValidationExecutorSetRegistry <> ();
  static
  {
    EN16931Validation.initEN16931 (VES_REGISTRY);
  }

  // The values for which the model has no field are reported by the writer and not written
  private static final Set <String> IGNORED = Set.of ("additionalTerms");

  // The differences that the writer causes on purpose and reports: "-" is a line that is only in
  // the source model, "+" a line that is only in the model of the written document
  private static final Map <String, Set <String>> EXPECTED_DIFFERENCES = Map.of (
                                                                                 // BT-32 scheme "VAT"
                                                                                 // is invalid, CII
                                                                                 // 2017 has "FC"
                                                                                 "external/en16931/2017/cii/CII_example1a.xml",
                                                                                 Set.of ("-invoice.seller.taxRegistrationSchemeID = VAT",
                                                                                         "+invoice.seller.taxRegistrationSchemeID = FC"),
                                                                                 // BT-49-1 without
                                                                                 // BT-49
                                                                                 "external/en16931/2017/cii/XRechnung-O.xml",
                                                                                 Set.of ("-invoice.buyer.electronicAddress.schemeID = EM"),
                                                                                 // BT-194 is new in EN
                                                                                 // 16931:2026
                                                                                 "external/en16931/2017/cii/xrechnung/3.0.2/01.21a-INVOICE_uncefact.xml",
                                                                                 Set.of ("-invoice.lines[0].lineVat.exemptionReasonText = Umkehrung der Steuerschuldnerschaft"),
                                                                                 // BT-X-98 is Factur-X
                                                                                 // EXTENDED only
                                                                                 "external/en16931/2017/cii/issues/issue1121.xml",
                                                                                 Set.of ("-invoice.lines[0].totalAllowanceCharge = 0"));

  @NonNull
  private static PhinvoiceInvoice _read (@NonNull final CiiInvoiceReader aReader,
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
                                  @NonNull final CiiInvoiceReader aReader,
                                  @NonNull final PhinvoiceExportTarget aTarget,
                                  @Nullable final DVRCoordinate aVID)
  {
    final CiiInvoiceWriter aWriter = new CiiInvoiceWriter (aTarget);
    final ICommonsList <String> aProblems = new CommonsArrayList <> ();
    for (final ClassPathResource aRes : aFiles)
    {
      final String sPath = aRes.getPath ();
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
      // BT-6 equal to BT-5 is not written (the CII version of BR-53), and so neither is BT-111
      if (aModel1.getVatAccountingCurrency () != null && aModel1.getVatAccountingCurrency ().equals (aModel1.getCurrency ()))
      {
        aModel1.setVatAccountingCurrency (null);
        aModel1.getDocumentTotals ().setTaxTotalAccountingCurrency (null);
      }
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
  public void testCiiD16BToEN2017 ()
  {
    _roundTrip (MockPhinvoiceTestFiles.getCiiD16BFiles (),
                CiiInvoiceReader.fromD22B (),
                new PhinvoiceExportTarget (EEN16931SyntaxKind.CII, EEN16931Edition.EN2017),
                EN16931Validation.VID_CII_1317.getWithVersionLatestRelease ());
  }

  @Test
  public void testZugferdExtendedToExtended ()
  {
    // The EN 16931 rules only show regressions here - phive-rules-zugferd is not used
    _roundTrip (MockPhinvoiceTestFiles.getCiiZugferd252Files ("extended"),
                CiiInvoiceReader.fromD22B (),
                new PhinvoiceExportTarget (EEN16931SyntaxKind.CII, EEN16931Edition.EN2017, EPhinvoiceProfile.EXTENDED),
                EN16931Validation.VID_CII_1317.getWithVersionLatestRelease ());
  }

  @Test
  public void testCiiD25AToEN2026 ()
  {
    // phive-rules has no validation artefacts for EN 16931:2026 yet - the XML Schema of CII D25A is
    // checked when writing and reading
    _roundTrip (MockPhinvoiceTestFiles.getCiiD25AFiles (),
                CiiInvoiceReader.fromD25A (),
                new PhinvoiceExportTarget (EEN16931SyntaxKind.CII, EEN16931Edition.EN2026),
                null);
  }
}
