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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.w3c.dom.Document;

import com.helger.cii.d22b.CIID22BCrossIndustryInvoiceTypeMarshaller;
import com.helger.cii.d25a.CIID25ACrossIndustryInvoiceTypeMarshaller;
import com.helger.diagnostics.error.list.ErrorList;
import com.helger.en16931.basics.EEN16931Edition;
import com.helger.en16931.basics.EEN16931SyntaxKind;
import com.helger.phinvoice.model.PhinvoiceAdditionalTerm;
import com.helger.phinvoice.model.PhinvoiceInvoice;
import com.helger.phinvoice.read.PhinvoiceUnmappedValueCollector;
import com.helger.phinvoice.target.EPhinvoiceProfile;
import com.helger.phinvoice.target.PhinvoiceExportTarget;
import com.helger.phinvoice.write.PhinvoiceTermFilter;

/**
 * The safety net of the CII reader (decision D12), see {@link PhinvoiceUnmappedValueCollector}. The
 * model is written again with the widest target of the edition (EN 16931:2017 EXTENDED or
 * EN 16931:2026), with the raw writer and without XML Schema validation.
 *
 * @author Philip Helger
 */
final class CiiUnmappedValueCollector
{
  /** The prefix of the term of an additional term that holds an unmapped value */
  static final String TERM_PREFIX = "cii:";

  private CiiUnmappedValueCollector ()
  {}

  @Nullable
  private static Document _write (@NonNull final PhinvoiceInvoice aModel, @NonNull final EEN16931Edition eEdition)
  {
    // The widest target of the edition, so that the writer drops nothing that the model has
    final EPhinvoiceProfile eProfile = eEdition == EEN16931Edition.EN2017 ? EPhinvoiceProfile.EXTENDED
                                                                         : EPhinvoiceProfile.EN16931;
    final PhinvoiceExportTarget aTarget = new PhinvoiceExportTarget (EEN16931SyntaxKind.CII, eEdition, eProfile);
    final PhinvoiceTermFilter aFilter = new PhinvoiceTermFilter (aTarget, new ErrorList ());
    // Raw: BT-24 and BT-6 as in the model
    final String sSpecificationIdentifier = aModel.getProcessControl ().getSpecificationIdentifier ();
    final String sBT24 = sSpecificationIdentifier != null ? sSpecificationIdentifier : aTarget.getSpecificationIdentifier ();
    if (eEdition == EEN16931Edition.EN2017)
      return new CIID22BCrossIndustryInvoiceTypeMarshaller ().setUseSchema (false)
                                                             .getAsDocument (new CiiD22BInvoiceWriter (aFilter, true).createCrossIndustryInvoice (aModel,
                                                                                                                                                  sBT24));
    return new CIID25ACrossIndustryInvoiceTypeMarshaller ().setUseSchema (false)
                                                           .getAsDocument (new CiiD25AInvoiceWriter (aFilter, true).createCrossIndustryInvoice (aModel,
                                                                                                                                                sBT24));
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
   * @see PhinvoiceAdditionalTerm
   */
  static void addUnmappedValues (@NonNull final Document aSource,
                                 @NonNull final PhinvoiceInvoice aModel,
                                 @NonNull final EEN16931Edition eEdition,
                                 @NonNull final ErrorList aErrors)
  {
    PhinvoiceUnmappedValueCollector.addUnmappedValues (aSource, _write (aModel, eEdition), aModel, TERM_PREFIX, aErrors);
  }
}
