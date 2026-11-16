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
package com.helger.phinvoice.write;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import com.helger.diagnostics.error.list.ErrorList;
import com.helger.en16931.basics.EEN16931Edition;
import com.helger.en16931.basics.EEN16931SyntaxKind;
import com.helger.phinvoice.model.PhinvoiceAdditionalTerm;
import com.helger.phinvoice.target.EPhinvoiceProfile;
import com.helger.phinvoice.target.PhinvoiceExportTarget;

/**
 * Test class for class {@link PhinvoiceTermFilter}.
 *
 * @author Philip Helger
 */
public final class PhinvoiceTermFilterTest
{
  @Test
  public void testUse ()
  {
    final ErrorList aErrors = new ErrorList ();
    final PhinvoiceTermFilter aFilter = new PhinvoiceTermFilter (new PhinvoiceExportTarget (EEN16931SyntaxKind.UBL_INVOICE,
                                                                                            EEN16931Edition.EN2017),
                                                                 aErrors);
    // Missing values are never written and never reported
    assertFalse (aFilter.use ("BT-12", null));
    assertFalse (aFilter.use ("BT-12", ""));
    assertFalse (aFilter.use ("BT-12", List.of ()));
    assertTrue (aErrors.isEmpty ());

    assertTrue (aFilter.use ("BT-12", "CONTRACT"));
    assertTrue (aErrors.isEmpty ());

    // New in EN 16931:2026
    assertFalse (aFilter.use ("BT-197", "DN-1"));
    // CII only
    assertFalse (aFilter.use ("BT-41-0", "Sales"));
    // Factur-X EXTENDED only
    assertFalse (aFilter.use ("BT-X-75", "a@b.c"));
    assertEquals (3, aErrors.size ());
    assertEquals (3, aFilter.getDropCount ());
    assertTrue (aErrors.getAllErrors ().isEmpty ());
    assertEquals ("BT-197", aErrors.getFirstOrNull ().getErrorFieldName ());
  }

  @Test
  public void testUseGroup ()
  {
    final ErrorList aErrors = new ErrorList ();
    final PhinvoiceTermFilter aFilter = new PhinvoiceTermFilter (new PhinvoiceExportTarget (EEN16931SyntaxKind.CII,
                                                                                            EEN16931Edition.EN2017,
                                                                                            EPhinvoiceProfile.MINIMUM),
                                                                 aErrors);
    // BG-25 exists in EN 16931:2017 but not in MINIMUM
    assertFalse (aFilter.useGroup ("BG-25", "3 invoice lines"));
    assertEquals (1, aErrors.size ());

    // BG-37 does not exist in EN 16931:2017 - its terms decide
    assertTrue (aFilter.useGroup ("BG-37", "line delivery"));
    assertFalse (aFilter.useGroup ("BG-37", null));
    assertEquals (1, aErrors.size ());
  }

  @Test
  public void testAdditionalTerms ()
  {
    final ErrorList aErrors = new ErrorList ();
    final PhinvoiceTermFilter aFilter = new PhinvoiceTermFilter (new PhinvoiceExportTarget (EEN16931SyntaxKind.CII,
                                                                                            EEN16931Edition.EN2026),
                                                                 aErrors);
    aFilter.dropAdditionalTerms (List.of (new PhinvoiceAdditionalTerm ("BT-9", "2026-11-15"),
                                          new PhinvoiceAdditionalTerm ("additional.pricecharge", "1.00")));
    assertEquals (2, aErrors.size ());
    assertEquals (2, aFilter.getDropCount ());
  }

  @Test
  public void testUnknownTerm ()
  {
    final PhinvoiceTermFilter aFilter = new PhinvoiceTermFilter (new PhinvoiceExportTarget (EEN16931SyntaxKind.CII,
                                                                                            EEN16931Edition.EN2026),
                                                                 new ErrorList ());
    try
    {
      aFilter.use ("BT-9999", "x");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }
}
