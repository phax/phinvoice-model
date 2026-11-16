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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.jspecify.annotations.NonNull;
import org.junit.Test;
import org.w3c.dom.Document;

import com.helger.collection.commons.ICommonsList;
import com.helger.diagnostics.error.IError;
import com.helger.diagnostics.error.list.ErrorList;
import com.helger.en16931.basics.EEN16931Edition;
import com.helger.en16931.basics.EEN16931SyntaxKind;
import com.helger.io.resource.ClassPathResource;
import com.helger.phinvoice.model.PhinvoiceInvoice;
import com.helger.phinvoice.target.EPhinvoiceProfile;
import com.helger.phinvoice.target.PhinvoiceExportTarget;
import com.helger.phinvoice.testfiles.MockPhinvoiceTestFiles;

/**
 * Write all CII test files to all CII targets: the result must be valid against the XML Schema,
 * and every value that the target does not allow must be reported.
 *
 * @author Philip Helger
 */
public final class CiiInvoiceWriterTargetTest
{
  @NonNull
  private static PhinvoiceInvoice _read (@NonNull final CiiInvoiceReader aReader, @NonNull final ClassPathResource aRes)
  {
    final ErrorList aErrors = new ErrorList ();
    final PhinvoiceInvoice ret = aReader.read (aRes.getInputStream (), aErrors);
    assertNotNull (aRes.getPath () + ": " + aErrors.getAllErrors (), ret);
    return ret;
  }

  private static void _writeAll (@NonNull final ICommonsList <ClassPathResource> aFiles,
                                 @NonNull final CiiInvoiceReader aReader,
                                 @NonNull final PhinvoiceExportTarget aTarget)
  {
    final CiiInvoiceWriter aWriter = new CiiInvoiceWriter (aTarget);
    for (final ClassPathResource aRes : aFiles)
    {
      final PhinvoiceInvoice aInvoice = _read (aReader, aRes);
      final ErrorList aErrors = new ErrorList ();
      final Document aDoc = aWriter.write (aInvoice, aErrors);
      assertNotNull (aRes.getPath () + " " + aTarget + ": " + aErrors.getAllErrors (), aDoc);
      assertTrue (aRes.getPath () + " " + aTarget + ": " + aErrors.getAllErrors (), aErrors.getAllErrors ().isEmpty ());

      if (aTarget.getProfile () == EPhinvoiceProfile.MINIMUM && aInvoice.lines ().isNotEmpty ())
      {
        // The lines are reported once as a group, not term by term
        assertEquals (1, aErrors.getAllFailures ().getAll (x -> "BG-25".equals (x.getErrorFieldName ())).size ());
        assertFalse (aErrors.getAllFailures ().containsAny (x -> "BT-126".equals (x.getErrorFieldName ())));
      }
    }
  }

  @Test
  public void testAllProfiles2017 ()
  {
    for (final EPhinvoiceProfile eProfile : EPhinvoiceProfile.values ())
      _writeAll (MockPhinvoiceTestFiles.getCiiD16BFiles (),
                 CiiInvoiceReader.fromD22B (),
                 new PhinvoiceExportTarget (EEN16931SyntaxKind.CII, EEN16931Edition.EN2017, eProfile));
  }

  @Test
  public void testEdition2017To2026 ()
  {
    _writeAll (MockPhinvoiceTestFiles.getCiiD16BFiles (),
               CiiInvoiceReader.fromD22B (),
               new PhinvoiceExportTarget (EEN16931SyntaxKind.CII, EEN16931Edition.EN2026));
  }

  @Test
  public void testEdition2026To2017 ()
  {
    _writeAll (MockPhinvoiceTestFiles.getCiiD25AFiles (),
               CiiInvoiceReader.fromD25A (),
               new PhinvoiceExportTarget (EEN16931SyntaxKind.CII, EEN16931Edition.EN2017));
  }

  @Test
  public void testDropReport ()
  {
    final ClassPathResource aRes = MockPhinvoiceTestFiles.getCiiD25AFiles ().getFirstOrNull ();
    assertNotNull (aRes);
    final PhinvoiceInvoice aInvoice = _read (CiiInvoiceReader.fromD25A (), aRes);
    // A term that is new in EN 16931:2026
    aInvoice.setDeliveryNoteReference ("DN-4711");

    final ErrorList aErrors = new ErrorList ();
    assertNotNull (new CiiInvoiceWriter (new PhinvoiceExportTarget (EEN16931SyntaxKind.CII,
                                                                    EEN16931Edition.EN2017)).write (aInvoice, aErrors));
    final IError aDrop = aErrors.findFirst (x -> "BT-197".equals (x.getErrorFieldName ()));
    assertNotNull (aErrors.getAllFailures ().toString (), aDrop);
    assertTrue (aDrop.getErrorText (null).contains ("DN-4711"));

    aErrors.clear ();
    assertNotNull (new CiiInvoiceWriter (new PhinvoiceExportTarget (EEN16931SyntaxKind.CII,
                                                                    EEN16931Edition.EN2026)).write (aInvoice, aErrors));
    assertFalse (aErrors.containsAny (x -> "BT-197".equals (x.getErrorFieldName ())));
  }

  @Test
  public void testNoUBLTarget ()
  {
    try
    {
      new CiiInvoiceWriter (new PhinvoiceExportTarget (EEN16931SyntaxKind.UBL_INVOICE, EEN16931Edition.EN2017));
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }
}
