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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.w3c.dom.Document;

import com.helger.base.enforce.ValueEnforcer;
import com.helger.diagnostics.error.SingleError;
import com.helger.diagnostics.error.list.ErrorList;
import com.helger.en16931.basics.EEN16931SyntaxKind;
import com.helger.jaxb.GenericJAXBMarshaller;
import com.helger.phinvoice.model.PhinvoiceInvoice;
import com.helger.phinvoice.target.PhinvoiceExportTarget;
import com.helger.phinvoice.write.PhinvoiceTermFilter;
import com.helger.ubl21.UBL21Marshaller;
import com.helger.ubl25.UBL25Marshaller;

/**
 * Writes a {@link PhinvoiceInvoice} as UBL Invoice or Credit Note for one
 * {@link PhinvoiceExportTarget}: UBL 2.1 for EN 16931:2017 and UBL 2.5 for EN 16931:2026. The root
 * element comes from the syntax kind of the target (see {@link #getSyntaxKind(PhinvoiceInvoice)} to
 * choose it from the document kind). Only the terms that the target allows are written; every other
 * value is reported as a warning in the error list (see {@link PhinvoiceTermFilter}). Nothing is
 * calculated or corrected. BT-24 is handled by
 * {@link PhinvoiceTermFilter#getSpecificationIdentifier(String)}.
 *
 * @author Philip Helger
 */
public class UblInvoiceWriter
{
  private final PhinvoiceExportTarget m_aTarget;

  /**
   * Constructor.
   *
   * @param aTarget
   *        The target to write. Its syntax kind must be UBL Invoice or UBL Credit Note. May not be
   *        <code>null</code>.
   */
  public UblInvoiceWriter (@NonNull final PhinvoiceExportTarget aTarget)
  {
    ValueEnforcer.notNull (aTarget, "Target");
    ValueEnforcer.isTrue (aTarget.getSyntaxKind () != EEN16931SyntaxKind.CII, "The target must be UBL");
    m_aTarget = aTarget;
  }

  /**
   * @return The target of this writer. Never <code>null</code>.
   */
  @NonNull
  public PhinvoiceExportTarget getTarget ()
  {
    return m_aTarget;
  }

  /**
   * Get the UBL syntax kind that matches the document kind of an invoice.
   *
   * @param aInvoice
   *        The invoice. May not be <code>null</code>.
   * @return {@link EEN16931SyntaxKind#UBL_CREDIT_NOTE} for a credit note,
   *         {@link EEN16931SyntaxKind#UBL_INVOICE} otherwise.
   */
  @NonNull
  public static EEN16931SyntaxKind getSyntaxKind (@NonNull final PhinvoiceInvoice aInvoice)
  {
    ValueEnforcer.notNull (aInvoice, "Invoice");
    return aInvoice.isCreditNote () ? EEN16931SyntaxKind.UBL_CREDIT_NOTE : EEN16931SyntaxKind.UBL_INVOICE;
  }

  @Nullable
  private static <T> Document _marshal (@NonNull final GenericJAXBMarshaller <T> aMarshaller,
                                        @NonNull final T aObject,
                                        @NonNull final ErrorList aErrors)
  {
    aMarshaller.setCollectErrors (aErrors);
    aMarshaller.writeExceptionCallbacks ().removeAll ();
    return aMarshaller.getAsDocument (aObject);
  }

  /**
   * Write the invoice as DOM document. The document is validated against the XML Schema of the
   * JAXB classes while it is created.
   *
   * @param aInvoice
   *        The invoice to write. May not be <code>null</code>.
   * @param aErrors
   *        The error list that receives the report of all values that are not written and the
   *        errors of the XML Schema validation. May not be <code>null</code>.
   * @return <code>null</code> if the document could not be created, e.g. because the model lacks a
   *         value that the XML Schema requires.
   */
  @Nullable
  public Document write (@NonNull final PhinvoiceInvoice aInvoice, @NonNull final ErrorList aErrors)
  {
    ValueEnforcer.notNull (aInvoice, "Invoice");
    ValueEnforcer.notNull (aErrors, "Errors");

    final boolean bCreditNote = m_aTarget.getSyntaxKind () == EEN16931SyntaxKind.UBL_CREDIT_NOTE;
    if (getSyntaxKind (aInvoice) != m_aTarget.getSyntaxKind ())
      aErrors.add (SingleError.builderWarn ()
                              .errorFieldName ("BT-3")
                              .errorText ("The document kind " +
                                          aInvoice.getKind () +
                                          " does not match the target " +
                                          m_aTarget.getSyntaxKind ().getDisplayName ())
                              .build ());

    final PhinvoiceTermFilter aFilter = new PhinvoiceTermFilter (m_aTarget, aErrors);
    final String sSpecificationIdentifier = aFilter.getSpecificationIdentifier (aInvoice.getProcessControl ()
                                                                                        .getSpecificationIdentifier ());
    switch (m_aTarget.getEdition ())
    {
      case EN2017:
      {
        final Ubl21InvoiceWriter aWriter = new Ubl21InvoiceWriter (aFilter);
        if (bCreditNote)
          return _marshal (UBL21Marshaller.creditNote (),
                           aWriter.createCreditNote (aInvoice, sSpecificationIdentifier),
                           aErrors);
        return _marshal (UBL21Marshaller.invoice (), aWriter.createInvoice (aInvoice, sSpecificationIdentifier), aErrors);
      }
      case EN2026:
      {
        final Ubl25InvoiceWriter aWriter = new Ubl25InvoiceWriter (aFilter);
        if (bCreditNote)
          return _marshal (UBL25Marshaller.creditNote (),
                           aWriter.createCreditNote (aInvoice, sSpecificationIdentifier),
                           aErrors);
        return _marshal (UBL25Marshaller.invoice (), aWriter.createInvoice (aInvoice, sSpecificationIdentifier), aErrors);
      }
      default:
        throw new IllegalStateException ("Unsupported EN 16931 edition " + m_aTarget.getEdition ());
    }
  }
}
