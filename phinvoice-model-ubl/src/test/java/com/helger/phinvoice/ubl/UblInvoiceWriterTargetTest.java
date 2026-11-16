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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.jspecify.annotations.NonNull;
import org.junit.Test;
import org.w3c.dom.Document;

import com.helger.collection.commons.ICommonsList;
import com.helger.diagnostics.error.list.ErrorList;
import com.helger.en16931.basics.EEN16931Edition;
import com.helger.en16931.basics.EEN16931SyntaxKind;
import com.helger.io.resource.ClassPathResource;
import com.helger.phinvoice.model.PhinvoiceInvoice;
import com.helger.phinvoice.target.PhinvoiceExportTarget;
import com.helger.phinvoice.testfiles.MockPhinvoiceTestFiles;

/**
 * Write all UBL test files to the UBL targets of both editions: the result must be valid against
 * the XML Schema, and every value that the target does not allow must be reported.
 *
 * @author Philip Helger
 */
public final class UblInvoiceWriterTargetTest
{
  @NonNull
  private static PhinvoiceInvoice _read (@NonNull final UblInvoiceReader aReader, @NonNull final ClassPathResource aRes)
  {
    final ErrorList aErrors = new ErrorList ();
    final PhinvoiceInvoice ret = aReader.read (aRes.getInputStream (), aErrors);
    assertNotNull (aRes.getPath () + ": " + aErrors.getAllErrors (), ret);
    return ret;
  }

  private static void _writeAll (@NonNull final ICommonsList <ClassPathResource> aFiles,
                                 @NonNull final UblInvoiceReader aReader,
                                 @NonNull final EEN16931Edition eEdition)
  {
    for (final ClassPathResource aRes : aFiles)
    {
      final PhinvoiceInvoice aInvoice = _read (aReader, aRes);
      final PhinvoiceExportTarget aTarget = new PhinvoiceExportTarget (UblInvoiceWriter.getSyntaxKind (aInvoice), eEdition);
      final ErrorList aErrors = new ErrorList ();
      final Document aDoc = new UblInvoiceWriter (aTarget).write (aInvoice, aErrors);
      assertNotNull (aRes.getPath () + " " + aTarget + ": " + aErrors.getAllErrors (), aDoc);
      assertTrue (aRes.getPath () + " " + aTarget + ": " + aErrors.getAllErrors (), aErrors.getAllErrors ().isEmpty ());
    }
  }

  @Test
  public void testEdition2017To2026 ()
  {
    _writeAll (MockPhinvoiceTestFiles.getAllUbl21Files (), UblInvoiceReader.fromUbl21 (), EEN16931Edition.EN2026);
  }

  @Test
  public void testEdition2026To2017 ()
  {
    _writeAll (MockPhinvoiceTestFiles.getAllUbl25Files (), UblInvoiceReader.fromUbl25 (), EEN16931Edition.EN2017);
  }

  @Test
  public void testDropReport ()
  {
    final ClassPathResource aRes = MockPhinvoiceTestFiles.getUbl25InvoiceFiles ().getFirstOrNull ();
    assertNotNull (aRes);
    final PhinvoiceInvoice aInvoice = _read (UblInvoiceReader.fromUbl25 (), aRes);
    // A term that is new in EN 16931:2026 and a CII only term
    aInvoice.setDeliveryNoteReference ("DN-4711");
    aInvoice.setProjectReference ("PRJ").setProjectName ("Project");

    final ErrorList aErrors = new ErrorList ();
    assertNotNull (new UblInvoiceWriter (new PhinvoiceExportTarget (EEN16931SyntaxKind.UBL_INVOICE,
                                                                    EEN16931Edition.EN2017)).write (aInvoice, aErrors));
    assertTrue (aErrors.containsAny (x -> "BT-197".equals (x.getErrorFieldName ())));
    assertTrue (aErrors.containsAny (x -> "BT-11-1".equals (x.getErrorFieldName ())));

    aErrors.clear ();
    assertNotNull (new UblInvoiceWriter (new PhinvoiceExportTarget (EEN16931SyntaxKind.UBL_INVOICE,
                                                                    EEN16931Edition.EN2026)).write (aInvoice, aErrors));
    assertFalse (aErrors.containsAny (x -> "BT-197".equals (x.getErrorFieldName ())));
    assertTrue (aErrors.containsAny (x -> "BT-11-1".equals (x.getErrorFieldName ())));
  }

  @Test
  public void testDocumentKindMismatch ()
  {
    final ClassPathResource aRes = MockPhinvoiceTestFiles.getUbl21CreditNoteFiles ().getFirstOrNull ();
    assertNotNull (aRes);
    final PhinvoiceInvoice aInvoice = _read (UblInvoiceReader.fromUbl21 (), aRes);
    assertSame (EEN16931SyntaxKind.UBL_CREDIT_NOTE, UblInvoiceWriter.getSyntaxKind (aInvoice));

    final ErrorList aErrors = new ErrorList ();
    assertNotNull (new UblInvoiceWriter (new PhinvoiceExportTarget (EEN16931SyntaxKind.UBL_INVOICE,
                                                                    EEN16931Edition.EN2017)).write (aInvoice, aErrors));
    assertTrue (aErrors.containsAny (x -> "BT-3".equals (x.getErrorFieldName ())));
  }

  @Test
  public void testNoCIITarget ()
  {
    try
    {
      new UblInvoiceWriter (new PhinvoiceExportTarget (EEN16931SyntaxKind.CII, EEN16931Edition.EN2017));
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }
}
