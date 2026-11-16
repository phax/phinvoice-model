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
package com.helger.phinvoice.term;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.en16931.basics.EEN16931Edition;
import com.helger.en16931.basics.EEN16931SyntaxKind;
import com.helger.phinvoice.target.EPhinvoiceProfile;
import com.helger.phinvoice.target.PhinvoiceExportTarget;

/**
 * Test class for class {@link PhinvoiceTermRegistry}.
 *
 * @author Philip Helger
 */
public final class PhinvoiceTermRegistryTest
{
  private static final PhinvoiceExportTarget CII_2017_MINIMUM = new PhinvoiceExportTarget (EEN16931SyntaxKind.CII,
                                                                                           EEN16931Edition.EN2017,
                                                                                           EPhinvoiceProfile.MINIMUM);
  private static final PhinvoiceExportTarget CII_2017_EN16931 = new PhinvoiceExportTarget (EEN16931SyntaxKind.CII,
                                                                                           EEN16931Edition.EN2017);
  private static final PhinvoiceExportTarget CII_2017_EXTENDED = new PhinvoiceExportTarget (EEN16931SyntaxKind.CII,
                                                                                            EEN16931Edition.EN2017,
                                                                                            EPhinvoiceProfile.EXTENDED);
  private static final PhinvoiceExportTarget CII_2026 = new PhinvoiceExportTarget (EEN16931SyntaxKind.CII,
                                                                                   EEN16931Edition.EN2026);
  private static final PhinvoiceExportTarget UBL_2017 = new PhinvoiceExportTarget (EEN16931SyntaxKind.UBL_INVOICE,
                                                                                   EEN16931Edition.EN2017);
  private static final PhinvoiceExportTarget UBL_2026 = new PhinvoiceExportTarget (EEN16931SyntaxKind.UBL_CREDIT_NOTE,
                                                                                   EEN16931Edition.EN2026);

  @Test
  public void testBasic ()
  {
    assertTrue (PhinvoiceTermRegistry.getTermCount () > 1000);
    assertEquals (PhinvoiceTermRegistry.getTermCount (), PhinvoiceTermRegistry.getAllTerms ().size ());
    assertNull (PhinvoiceTermRegistry.getTermOfID (null));
    assertNull (PhinvoiceTermRegistry.getTermOfID ("BT-0"));
    assertFalse (PhinvoiceTermRegistry.isAllowed ("BT-0", CII_2017_EXTENDED));
    assertFalse (PhinvoiceTermRegistry.isAllowed ((String) null, CII_2017_EXTENDED));
  }

  @Test
  public void testInvariants ()
  {
    int nCount2017 = 0;
    int nCount2026 = 0;
    for (final PhinvoiceTerm aTerm : PhinvoiceTermRegistry.getAllTerms ())
    {
      assertSame (aTerm, PhinvoiceTermRegistry.getTermOfID (aTerm.getID ()));
      if (aTerm.isGroup ())
        assertNull (aTerm.getID (), aTerm.getDataType ());

      final PhinvoiceTermUsage aUsage2017 = aTerm.getUsage (EEN16931Edition.EN2017);
      final PhinvoiceTermUsage aUsage2026 = aTerm.getUsage (EEN16931Edition.EN2026);
      if (aUsage2017 != null)
      {
        nCount2017++;
        // Everything in the Factur-X workbook can be expressed in CII
        assertTrue (aTerm.getID (), aUsage2017.isCII ());
        // UBL only carries EN 16931 terms
        if (aUsage2017.isUBL ())
          assertTrue (aTerm.getID (), EPhinvoiceProfile.EN16931.includes (aUsage2017.getLowestProfile ()));
      }
      if (aUsage2026 != null)
      {
        nCount2026++;
        assertSame (aTerm.getID (), EPhinvoiceProfile.EN16931, aUsage2026.getLowestProfile ());
      }

      if (aTerm.isExtension ())
      {
        assertNotNull (aTerm.getID (), aUsage2017);
        assertNull (aTerm.getID (), aUsage2026);
        assertSame (aTerm.getID (), EPhinvoiceProfile.EXTENDED, aUsage2017.getLowestProfile ());
        assertFalse (aTerm.getID (), aUsage2017.isUBL ());
      }
    }
    assertEquals (nCount2017, PhinvoiceTermRegistry.getAllTermsOfEdition (EEN16931Edition.EN2017).size ());
    assertEquals (nCount2026, PhinvoiceTermRegistry.getAllTermsOfEdition (EEN16931Edition.EN2026).size ());
  }

  @Test
  public void testInvoiceNumber ()
  {
    final PhinvoiceTerm aTerm = PhinvoiceTermRegistry.getTermOfID ("BT-1");
    assertNotNull (aTerm);
    assertEquals ("Invoice number", aTerm.getName ());
    assertSame (EPhinvoiceDataType.IDENTIFIER, aTerm.getDataType ());
    assertFalse (aTerm.isGroup ());
    assertFalse (aTerm.isExtension ());
    final PhinvoiceTermUsage aUsage = aTerm.getUsage (EEN16931Edition.EN2017);
    assertNotNull (aUsage);
    assertEquals ("1..1", aUsage.getCardinality ());
    assertTrue (aUsage.isMandatory ());
    assertFalse (aUsage.isRepeatable ());
    assertSame (EPhinvoiceProfile.MINIMUM, aUsage.getLowestProfile ());

    // Allowed everywhere
    for (final PhinvoiceExportTarget aTarget : new PhinvoiceExportTarget [] { CII_2017_MINIMUM,
                                                                              CII_2017_EN16931,
                                                                              CII_2017_EXTENDED,
                                                                              CII_2026,
                                                                              UBL_2017,
                                                                              UBL_2026 })
      assertTrue (aTarget.toString (), PhinvoiceTermRegistry.isAllowed (aTerm, aTarget));
  }

  @Test
  public void testProfileFiltering ()
  {
    // BT-158 Item classification identifier is EN 16931 and higher
    assertFalse (PhinvoiceTermRegistry.isAllowed ("BT-158", CII_2017_MINIMUM));
    assertTrue (PhinvoiceTermRegistry.isAllowed ("BT-158", CII_2017_EN16931));
    assertTrue (PhinvoiceTermRegistry.isAllowed ("BT-158", CII_2017_EXTENDED));
    assertTrue (PhinvoiceTermRegistry.isAllowed ("BT-158", UBL_2017));
  }

  @Test
  public void testExtension ()
  {
    // BT-X-75 ULTIMATE SHIP TO contact email - EXTENDED and CII only
    final PhinvoiceTerm aTerm = PhinvoiceTermRegistry.getTermOfID ("BT-X-75");
    assertNotNull (aTerm);
    assertTrue (aTerm.isExtension ());
    assertTrue (PhinvoiceTermRegistry.isAllowed (aTerm, CII_2017_EXTENDED));
    assertFalse (PhinvoiceTermRegistry.isAllowed (aTerm, CII_2017_EN16931));
    assertFalse (PhinvoiceTermRegistry.isAllowed (aTerm, UBL_2017));
    assertFalse (PhinvoiceTermRegistry.isAllowed (aTerm, CII_2026));

    final PhinvoiceTerm aGroup = PhinvoiceTermRegistry.getTermOfID ("BG-X-12");
    assertNotNull (aGroup);
    assertTrue (aGroup.isGroup ());
    assertTrue (aGroup.isExtension ());
  }

  @Test
  public void testNewIn2026 ()
  {
    // BT-166 Invoice issue time
    final PhinvoiceTerm aTerm = PhinvoiceTermRegistry.getTermOfID ("BT-166");
    assertNotNull (aTerm);
    assertFalse (aTerm.existsIn (EEN16931Edition.EN2017));
    assertTrue (aTerm.existsIn (EEN16931Edition.EN2026));
    assertFalse (PhinvoiceTermRegistry.isAllowed (aTerm, CII_2017_EXTENDED));
    assertTrue (PhinvoiceTermRegistry.isAllowed (aTerm, CII_2026));
    assertTrue (PhinvoiceTermRegistry.isAllowed (aTerm, UBL_2026));

    // BT-173 exists in EN 16931:2026 and, ahead of it, in the Factur-X profile EXTENDED
    assertFalse (PhinvoiceTermRegistry.isAllowed ("BT-173", CII_2017_EN16931));
    assertTrue (PhinvoiceTermRegistry.isAllowed ("BT-173", CII_2017_EXTENDED));
    assertFalse (PhinvoiceTermRegistry.isAllowed ("BT-173", UBL_2017));
    assertTrue (PhinvoiceTermRegistry.isAllowed ("BT-173", UBL_2026));
  }

  @Test
  public void testSyntaxSpecific ()
  {
    // BT-116-1 is UBL only, BT-11-1 is CII only
    assertTrue (PhinvoiceTermRegistry.isAllowed ("BT-116-1", UBL_2026));
    assertFalse (PhinvoiceTermRegistry.isAllowed ("BT-116-1", CII_2026));
    assertTrue (PhinvoiceTermRegistry.isAllowed ("BT-11-1", CII_2026));
    assertFalse (PhinvoiceTermRegistry.isAllowed ("BT-11-1", UBL_2026));

    // Syntax constants are not part of the registry
    assertNull (PhinvoiceTermRegistry.getTermOfID ("BT-2-0"));
    assertNull (PhinvoiceTermRegistry.getTermOfID ("BT-2-1"));
    assertNull (PhinvoiceTermRegistry.getTermOfID ("BT-31-0"));
    assertNull (PhinvoiceTermRegistry.getTermOfID ("BG-20-1"));
  }
}
