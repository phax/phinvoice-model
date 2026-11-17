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
 * Read coverage of the UBL reader with {@link PhinvoiceReadCoverage}: every value of a source
 * document must be in the model. Values that the binding fixes are not checked.
 *
 * @author Philip Helger
 */
public final class UblReadCoverageTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (UblReadCoverageTest.class);
  // The tax schemes VAT and LOC (the default of BT-32), the boolean values, the Peppol filler NA
  // that the readers skip
  private static final Set <String> SYNTAX_CONSTANTS = Set.of ("VAT", "LOC", "true", "false", "NA");

  // Paths whose value is not in the model: the type codes 130 / 916 of BT-18, BT-122 and BT-128 and
  // the list 1001 of BT-122-1 (BT-122-1-1) are fixed by the binding
  private static final Set <String> ALLOWED_LOST_PATHS = Set.of ("/Invoice/cac:AdditionalDocumentReference/cbc:DocumentTypeCode/@listID",
                                                                 "/CreditNote/cac:AdditionalDocumentReference/cbc:DocumentTypeCode/@listID",
                                                                 "/Invoice/cac:AdditionalDocumentReference/cbc:DocumentTypeCode",
                                                                 "/CreditNote/cac:AdditionalDocumentReference/cbc:DocumentTypeCode",
                                                                 "/Invoice/cac:InvoiceLine/cac:DocumentReference/cbc:DocumentTypeCode",
                                                                 "/CreditNote/cac:CreditNoteLine/cac:DocumentReference/cbc:DocumentTypeCode");

  private static void _check (@NonNull final String sName,
                              @NonNull final ICommonsList <ClassPathResource> aFiles,
                              @NonNull final UblInvoiceReader aReader)
  {
    final PhinvoiceReadCoverage aCoverage = PhinvoiceReadCoverage.check (sName,
                                                                         aFiles,
                                                                         aReader,
                                                                         UblUnmappedValueCollector.TERM_PREFIX,
                                                                         SYNTAX_CONSTANTS);
    LOGGER.info (aCoverage.getAsString ());
    final ICommonsSet <String> aUnexpected = new CommonsHashSet <> (aCoverage.getLostPaths ().keySet ());
    aUnexpected.removeAll (ALLOWED_LOST_PATHS);
    assertTrue (sName + ": values not found in the model: " + aUnexpected, aUnexpected.isEmpty ());
  }

  @Test
  public void testUbl21 ()
  {
    _check ("UBL 2.1", MockPhinvoiceTestFiles.getAllUbl21Files (), UblInvoiceReader.fromUbl21 ());
  }

  @Test
  public void testUbl25 ()
  {
    _check ("UBL 2.5", MockPhinvoiceTestFiles.getAllUbl25Files (), UblInvoiceReader.fromUbl25 ());
  }
}
