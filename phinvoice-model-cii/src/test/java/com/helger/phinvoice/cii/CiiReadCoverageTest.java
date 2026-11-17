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

import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.jspecify.annotations.NonNull;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.collection.commons.CommonsHashSet;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsSet;
import com.helger.io.resource.ClassPathResource;
import com.helger.phinvoice.testfiles.MockPhinvoiceTestFiles;
import com.helger.phinvoice.testfiles.PhinvoiceReadCoverage;

/**
 * Read coverage of the CII reader with {@link PhinvoiceReadCoverage}: every value of a source
 * document must be in the model. Values that the binding fixes (format codes, tax type code, charge
 * indicators, the scheme identifiers VA / FC) are not checked.
 *
 * @author Philip Helger
 */
public final class CiiReadCoverageTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (CiiReadCoverageTest.class);
  private static final Set <String> SYNTAX_CONSTANTS = Set.of ("VAT", "VA", "FC", "true", "false", "102", "916");

  // Paths whose value is not in the model in the same form: the type codes 50 / 130 of BT-17, BT-18
  // and BT-128 are fixed by the binding, the CII code of BT-8 is kept as UBL code, BT-2 with BT-166
  // is one value in CII, the list agency of a non-VAT tax code (BT-177, BT-193) is fixed
  private static final Set <String> ALLOWED_LOST_PATHS = Set.of ("/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeAllowanceCharge/ram:ReasonCode/@listAgencyID",
                                                                 "/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeSettlement/ram:SpecifiedTradeAllowanceCharge/ram:ReasonCode/@listAgencyID",
                                                                 "/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:AdditionalReferencedDocument/ram:TypeCode",
                                                                 "/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeSettlement/ram:AdditionalReferencedDocument/ram:TypeCode",
                                                                 "/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:ApplicableTradeTax/ram:DueDateTypeCode",
                                                                 "/rsm:CrossIndustryInvoice/rsm:ExchangedDocument/ram:IssueDateTime/udt:DateTimeString");

  @NonNull
  private static PhinvoiceReadCoverage _check (@NonNull final String sName,
                                               @NonNull final ICommonsList <ClassPathResource> aFiles,
                                               @NonNull final CiiInvoiceReader aReader)
  {
    final PhinvoiceReadCoverage aCoverage = PhinvoiceReadCoverage.check (sName,
                                                                         aFiles,
                                                                         aReader,
                                                                         CiiUnmappedValueCollector.TERM_PREFIX,
                                                                         SYNTAX_CONSTANTS);
    LOGGER.info (aCoverage.getAsString ());
    final ICommonsSet <String> aUnexpected = new CommonsHashSet <> (aCoverage.getLostPaths ().keySet ());
    aUnexpected.removeAll (ALLOWED_LOST_PATHS);
    assertTrue (sName + ": values not found in the model: " + aUnexpected, aUnexpected.isEmpty ());
    return aCoverage;
  }

  @Test
  public void testEN16931 ()
  {
    _check ("CII D16B", MockPhinvoiceTestFiles.getCiiD16BFiles (), CiiInvoiceReader.fromD22B ());
  }

  @Test
  public void testZugferd ()
  {
    for (final String sProfile : new String [] { "minimum", "basicwl", "basic", "en16931", "extended" })
    {
      final PhinvoiceReadCoverage aCoverage = _check ("ZUGFeRD " + sProfile,
                                                      MockPhinvoiceTestFiles.getCiiZugferd252Files (sProfile),
                                                      CiiInvoiceReader.fromD22B ());
      // Factur-X is mapped completely
      assertTrue (sProfile + ": " + aCoverage.getSafetyNetTerms (), aCoverage.getSafetyNetTerms ().isEmpty ());
    }
  }

  @Test
  public void testD25A ()
  {
    _check ("CII D25A", MockPhinvoiceTestFiles.getCiiD25AFiles (), CiiInvoiceReader.fromD25A ());
  }
}
