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

import com.helger.diagnostics.error.list.ErrorList;
import com.helger.en16931.basics.EEN16931Edition;
import com.helger.en16931.basics.EEN16931SyntaxKind;
import com.helger.phinvoice.model.PhinvoiceInvoice;
import com.helger.phinvoice.read.PhinvoiceUnmappedValueCollector;
import com.helger.phinvoice.target.PhinvoiceExportTarget;
import com.helger.phinvoice.write.PhinvoiceTermFilter;
import com.helger.ubl21.UBL21Marshaller;
import com.helger.ubl25.UBL25Marshaller;

/**
 * The safety net of the UBL reader (decision D12), see {@link PhinvoiceUnmappedValueCollector}. The
 * model is written again as the same document kind and edition as the source, without XML Schema
 * validation.
 *
 * @author Philip Helger
 */
final class UblUnmappedValueCollector
{
  /** The prefix of the term of an additional term that holds an unmapped value */
  static final String TERM_PREFIX = "ubl:";

  private UblUnmappedValueCollector ()
  {}

  @Nullable
  private static Document _write (@NonNull final Document aSource,
                                  @NonNull final PhinvoiceInvoice aModel,
                                  @NonNull final EEN16931Edition eEdition)
  {
    final boolean bCreditNote = AbstractUblInvoiceReader.ROOT_CREDIT_NOTE.equals (aSource.getDocumentElement ().getLocalName ());
    final PhinvoiceExportTarget aTarget = new PhinvoiceExportTarget (bCreditNote ? EEN16931SyntaxKind.UBL_CREDIT_NOTE
                                                                                 : EEN16931SyntaxKind.UBL_INVOICE,
                                                                     eEdition);
    final PhinvoiceTermFilter aFilter = new PhinvoiceTermFilter (aTarget, new ErrorList ());
    // BT-24 as in the model
    final String sSpecificationIdentifier = aModel.getProcessControl ().getSpecificationIdentifier ();
    final String sBT24 = sSpecificationIdentifier != null ? sSpecificationIdentifier : aTarget.getSpecificationIdentifier ();
    if (eEdition == EEN16931Edition.EN2017)
    {
      final Ubl21InvoiceWriter aWriter = new Ubl21InvoiceWriter (aFilter);
      if (bCreditNote)
        return UBL21Marshaller.creditNote ().setUseSchema (false).getAsDocument (aWriter.createCreditNote (aModel, sBT24));
      return UBL21Marshaller.invoice ().setUseSchema (false).getAsDocument (aWriter.createInvoice (aModel, sBT24));
    }
    final Ubl25InvoiceWriter aWriter = new Ubl25InvoiceWriter (aFilter);
    if (bCreditNote)
      return UBL25Marshaller.creditNote ().setUseSchema (false).getAsDocument (aWriter.createCreditNote (aModel, sBT24));
    return UBL25Marshaller.invoice ().setUseSchema (false).getAsDocument (aWriter.createInvoice (aModel, sBT24));
  }

  /**
   * Add the values of the source that the reader did not take over to the additional terms of the
   * model and report them.
   *
   * @param aSource
   *        The source document. May not be <code>null</code>.
   * @param aModel
   *        The model created from it. May not be <code>null</code>.
   * @param eEdition
   *        The edition the source was read with. May not be <code>null</code>.
   * @param aErrors
   *        The error list of the reader. May not be <code>null</code>.
   */
  static void addUnmappedValues (@NonNull final Document aSource,
                                 @NonNull final PhinvoiceInvoice aModel,
                                 @NonNull final EEN16931Edition eEdition,
                                 @NonNull final ErrorList aErrors)
  {
    PhinvoiceUnmappedValueCollector.addUnmappedValues (aSource, _write (aSource, aModel, eEdition), aModel, TERM_PREFIX, aErrors);
  }
}
