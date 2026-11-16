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

import com.helger.base.enforce.ValueEnforcer;
import com.helger.cii.d22b.CIID22BCrossIndustryInvoiceTypeMarshaller;
import com.helger.cii.d25a.CIID25ACrossIndustryInvoiceTypeMarshaller;
import com.helger.diagnostics.error.list.ErrorList;
import com.helger.en16931.basics.EEN16931Edition;
import com.helger.en16931.basics.EEN16931SyntaxKind;
import com.helger.jaxb.GenericJAXBMarshaller;
import com.helger.phinvoice.model.PhinvoiceInvoice;
import com.helger.phinvoice.target.PhinvoiceExportTarget;
import com.helger.phinvoice.write.PhinvoiceTermFilter;

/**
 * Writes a {@link PhinvoiceInvoice} as UN/CEFACT {@code CrossIndustryInvoice} for one
 * {@link PhinvoiceExportTarget}. Only the terms that the target allows are written; every other
 * value is reported as a warning in the error list (see {@link PhinvoiceTermFilter}). Nothing is
 * calculated or corrected: the values of the model are written as they are.
 * <p>
 * BT-24 (Specification identifier) of the model is kept if it fits the target: for the profile
 * EN 16931 it must be the identifier of the edition or a CIUS of it
 * (<code>&lt;edition&gt;#compliant#...</code>), for the other profiles it must be the Factur-X
 * identifier of the profile. Otherwise the identifier of the target is written and the change is
 * reported.
 *
 * @author Philip Helger
 */
public class CiiInvoiceWriter
{
  private final PhinvoiceExportTarget m_aTarget;

  /**
   * Constructor.
   *
   * @param aTarget
   *        The target to write. Its syntax kind must be CII. May not be <code>null</code>.
   */
  public CiiInvoiceWriter (@NonNull final PhinvoiceExportTarget aTarget)
  {
    ValueEnforcer.notNull (aTarget, "Target");
    ValueEnforcer.isTrue (aTarget.getSyntaxKind () == EEN16931SyntaxKind.CII, "The target must be CII");
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
   * Create the JAXB object of the CII D22B document. Only for EN 16931:2017 targets.
   *
   * @param aInvoice
   *        The invoice to write. May not be <code>null</code>.
   * @param aErrors
   *        The error list that receives the report of all values that are not written. May not be
   *        <code>null</code>.
   * @return The JAXB object. Never <code>null</code>.
   * @throws IllegalStateException
   *         if the target is not of EN 16931:2017
   */
  public un.unece.uncefact.data.standard.cii.d22b.@NonNull CrossIndustryInvoiceType createCrossIndustryInvoiceD22B (@NonNull final PhinvoiceInvoice aInvoice,
                                                                                                                       @NonNull final ErrorList aErrors)
  {
    ValueEnforcer.notNull (aInvoice, "Invoice");
    ValueEnforcer.notNull (aErrors, "Errors");
    if (m_aTarget.getEdition () != EEN16931Edition.EN2017)
      throw new IllegalStateException ("CII D22B is only written for EN 16931:2017 targets");

    final PhinvoiceTermFilter aFilter = new PhinvoiceTermFilter (m_aTarget, aErrors);
    return new CiiD22BInvoiceWriter (aFilter, false).createCrossIndustryInvoice (aInvoice,
                                                                          aFilter.getSpecificationIdentifier (aInvoice.getProcessControl ()
                                                                                                                      .getSpecificationIdentifier ()));
  }

  /**
   * Create the JAXB object of the CII D25A document. Only for EN 16931:2026 targets.
   *
   * @param aInvoice
   *        The invoice to write. May not be <code>null</code>.
   * @param aErrors
   *        The error list that receives the report of all values that are not written. May not be
   *        <code>null</code>.
   * @return The JAXB object. Never <code>null</code>.
   * @throws IllegalStateException
   *         if the target is not of EN 16931:2026
   */
  public un.unece.uncefact.data.standard.cii.d25a.@NonNull CrossIndustryInvoiceType createCrossIndustryInvoiceD25A (@NonNull final PhinvoiceInvoice aInvoice,
                                                                                                                       @NonNull final ErrorList aErrors)
  {
    ValueEnforcer.notNull (aInvoice, "Invoice");
    ValueEnforcer.notNull (aErrors, "Errors");
    if (m_aTarget.getEdition () != EEN16931Edition.EN2026)
      throw new IllegalStateException ("CII D25A is only written for EN 16931:2026 targets");

    final PhinvoiceTermFilter aFilter = new PhinvoiceTermFilter (m_aTarget, aErrors);
    return new CiiD25AInvoiceWriter (aFilter, false).createCrossIndustryInvoice (aInvoice,
                                                                          aFilter.getSpecificationIdentifier (aInvoice.getProcessControl ()
                                                                                                                      .getSpecificationIdentifier ()));
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
    switch (m_aTarget.getEdition ())
    {
      case EN2017:
        return _marshal (new CIID22BCrossIndustryInvoiceTypeMarshaller (),
                         createCrossIndustryInvoiceD22B (aInvoice, aErrors),
                         aErrors);
      case EN2026:
        return _marshal (new CIID25ACrossIndustryInvoiceTypeMarshaller (),
                         createCrossIndustryInvoiceD25A (aInvoice, aErrors),
                         aErrors);
      default:
        throw new IllegalStateException ("Unsupported EN 16931 edition " + m_aTarget.getEdition ());
    }
  }
}
