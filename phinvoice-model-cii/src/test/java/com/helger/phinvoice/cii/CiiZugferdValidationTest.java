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

import java.util.Map;
import java.util.Set;

import org.jspecify.annotations.NonNull;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.helger.base.string.StringImplode;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsHashMap;
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
import com.helger.phive.api.execute.ValidationExecutionManager;
import com.helger.phive.api.executorset.ValidationExecutorSetRegistry;
import com.helger.phive.api.result.ValidationResult;
import com.helger.phive.api.result.ValidationResultList;
import com.helger.phive.api.validity.IValidityDeterminator;
import com.helger.phive.en16931.EN16931Validation;
import com.helger.phive.xml.source.IValidationSourceXML;
import com.helger.phive.xml.source.ValidationSourceXML;
import com.helger.phive.zugferd.ZugferdValidation;
import com.helger.xml.serialize.write.XMLWriter;

/**
 * Checks the CII output for the Factur-X profiles with the ZUGFeRD 2.5.2 rules of
 * <code>phive-rules-zugferd</code>: the ZUGFeRD test files of every profile are written to the
 * target of their own profile, and the EXTENDED test files to the targets of all lower profiles
 * (the implicit purification). The output must not fail a rule that the source does not fail,
 * except for the failures listed in {@link #EXPECTED_FAILURES}.
 *
 * @author Philip Helger
 */
public final class CiiZugferdValidationTest
{
  /** The ID of an XML Schema failure, whose text depends on the locale */
  private static final String XSD = "XSD";

  private static final Logger LOGGER = LoggerFactory.getLogger (CiiZugferdValidationTest.class);

  private static final ValidationExecutorSetRegistry <IValidationSourceXML> VES_REGISTRY = new ValidationExecutorSetRegistry <> ();
  static
  {
    // The ZUGFeRD rules build on the EN 16931 rules
    EN16931Validation.initEN16931 (VES_REGISTRY);
    ZugferdValidation.initZugferd (VES_REGISTRY);
  }

  private static final String DIR = "external/en16931/2017/cii/zugferd/2.5.2/extended/";

  // The failures that the purification causes and that the writer cannot avoid without calculating
  // or converting: the dropped values are reported. Key: target profile ID and path.
  // - Sub-lines (BT-X-304, BT-X-8) become ordinary lines: BR-CO-10, BR-S-08
  // - Information lines without VAT (BG-30 is mandatory below EXTENDED): XML Schema
  // - Logistics service charges (BG-X-42) are part of the totals: BR-CO-12, BR-S-*
  // - Seller identifiers that the profile does not have (BT-29, BT-32): BR-CO-26, BR-E-02, BR-S-02
  private static final Map <String, Set <String>> EXPECTED_FAILURES;
  static
  {
    final Set <String> aSubLines = Set.of ("BR-CO-10", "BR-S-08");
    final Map <String, Set <String>> aMap = new CommonsHashMap <> ();
    aMap.put ("minimum " + DIR + "factur-x-14.xml", Set.of ("BR-CO-26"));
    aMap.put ("minimum " + DIR + "factur-x-15.xml", Set.of ("BR-CO-26"));
    aMap.put ("basicwl " + DIR + "factur-x-15.xml", Set.of ("BR-CO-12", "BR-S-03"));
    aMap.put ("basicwl " + DIR + "factur-x-24.xml", Set.of ("BR-CO-12"));
    aMap.put ("basic " + DIR + "factur-x-14.xml", Set.of ("BR-E-02"));
    aMap.put ("basic " + DIR + "factur-x-15.xml", Set.of ("BR-CO-12", "BR-S-02", "BR-S-03", "BR-S-08"));
    for (final String sProfile : new String [] { "basic", "en16931" })
    {
      for (final String sFile : new String [] { "factur-x-4.xml", "factur-x-5.xml", "factur-x-22.xml", "factur-x-25.xml" })
        aMap.put (sProfile + " " + DIR + sFile, aSubLines);
      aMap.put (sProfile + " " + DIR + "factur-x-23.xml", Set.of (XSD));
      aMap.put (sProfile + " " + DIR + "factur-x-24.xml", Set.of ("BR-CO-12", "BR-S-08"));
    }
    aMap.put ("en16931 " + DIR + "factur-x-15.xml", Set.of ("BR-CO-12", "BR-S-08"));
    EXPECTED_FAILURES = aMap;
  }

  @NonNull
  private static DVRCoordinate _getVID (@NonNull final EPhinvoiceProfile eProfile)
  {
    return switch (eProfile)
    {
      case MINIMUM -> ZugferdValidation.VID_ZUGFERD_2_5_2_MINIMUM;
      case BASIC_WL -> ZugferdValidation.VID_ZUGFERD_2_5_2_BASIC_WL;
      case BASIC -> ZugferdValidation.VID_ZUGFERD_2_5_2_BASIC;
      case EN16931 -> ZugferdValidation.VID_ZUGFERD_2_5_2_EN16931;
      case EXTENDED -> ZugferdValidation.VID_ZUGFERD_2_5_2_EXTENDED;
    };
  }

  /** The IDs of all failed rules; an XML Schema failure is {@link #XSD}. */
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
          ret.add (aError.getErrorID () != null ? aError.getErrorID () : XSD);
    return ret;
  }

  private static void _check (@NonNull final EPhinvoiceProfile eSourceProfile,
                              @NonNull final EPhinvoiceProfile eTargetProfile,
                              @NonNull final ICommonsList <String> aProblems)
  {
    final ICommonsList <ClassPathResource> aFiles = MockPhinvoiceTestFiles.getCiiZugferd252Files (eSourceProfile.getID ());
    final PhinvoiceExportTarget aTarget = new PhinvoiceExportTarget (EEN16931SyntaxKind.CII, EEN16931Edition.EN2017, eTargetProfile);
    final DVRCoordinate aVID = _getVID (eTargetProfile);
    final CiiInvoiceWriter aWriter = new CiiInvoiceWriter (aTarget);
    int nDrops = 0;
    for (final ClassPathResource aRes : aFiles)
    {
      final String sPath = aRes.getPath ();
      final ErrorList aReadErrors = new ErrorList ();
      final PhinvoiceInvoice aModel = CiiInvoiceReader.fromD22B ().read (aRes.getInputStream (), aReadErrors);
      assertNotNull (sPath + ": " + aReadErrors.getAllErrors (), aModel);

      final ErrorList aWriteErrors = new ErrorList ();
      final Document aDoc = aWriter.write (aModel, aWriteErrors);
      assertNotNull (sPath + ": " + aWriteErrors.getAllErrors (), aDoc);
      nDrops += aWriteErrors.size ();

      final ICommonsSortedSet <String> aNewFailures = _getAllFailedRuleIDs (aVID,
                                                                           new ReadableResourceByteArray (sPath,
                                                                                                          XMLWriter.getNodeAsBytes (aDoc)));
      aNewFailures.removeAll (_getAllFailedRuleIDs (_getVID (eSourceProfile), aRes));
      final Set <String> aExpected = EXPECTED_FAILURES.getOrDefault (eTargetProfile.getID () + " " + sPath, Set.of ());
      if (!aExpected.equals (aNewFailures))
        aProblems.add (eTargetProfile.getID () + " " + sPath + ": new rule failures differ from the expected " + aExpected + ": " + aNewFailures + " / reported: " + aWriteErrors.getAllMapped (IError::getErrorFieldName));
    }
    LOGGER.info (eSourceProfile.getDisplayName () +
                 " -> " +
                 eTargetProfile.getDisplayName () +
                 ": " +
                 aFiles.size () +
                 " files, " +
                 nDrops +
                 " values reported");
  }

  private static void _assertNoProblems (@NonNull final ICommonsList <String> aProblems)
  {
    assertTrue (aProblems.size () + " problems:\n" + StringImplode.getImploded ('\n', aProblems), aProblems.isEmpty ());
  }

  @Test
  public void testOwnProfile ()
  {
    final ICommonsList <String> aProblems = new CommonsArrayList <> ();
    for (final EPhinvoiceProfile eProfile : EPhinvoiceProfile.values ())
      _check (eProfile, eProfile, aProblems);
    _assertNoProblems (aProblems);
  }

  @Test
  public void testExtendedToLowerProfiles ()
  {
    final ICommonsList <String> aProblems = new CommonsArrayList <> ();
    for (final EPhinvoiceProfile eProfile : EPhinvoiceProfile.values ())
    {
      if (eProfile != EPhinvoiceProfile.EXTENDED)
        _check (EPhinvoiceProfile.EXTENDED, eProfile, aProblems);
    }
    _assertNoProblems (aProblems);
  }
}
