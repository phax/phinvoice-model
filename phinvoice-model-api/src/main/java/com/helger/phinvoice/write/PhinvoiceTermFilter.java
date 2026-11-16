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

import java.util.Collection;
import java.util.List;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnegative;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.diagnostics.error.SingleError;
import com.helger.diagnostics.error.list.ErrorList;
import com.helger.phinvoice.PhinvoiceLogHelper;
import com.helger.phinvoice.model.PhinvoiceAdditionalTerm;
import com.helger.phinvoice.target.PhinvoiceExportTarget;
import com.helger.phinvoice.term.PhinvoiceTerm;
import com.helger.phinvoice.term.PhinvoiceTermRegistry;

/**
 * The implicit purification of the writers: it decides with the {@link PhinvoiceTermRegistry}
 * whether the {@link PhinvoiceExportTarget} of a writer allows a term, and reports every value that
 * is not written as a warning, so that a writer never drops a value silently.
 * <p>
 * The filter only knows term IDs. A writer asks it before it writes a value, e.g.
 * <code>if (aFilter.use ("BT-12", aInvoice.getContractReference ()))</code>. The values of
 * {@link PhinvoiceAdditionalTerm} have no field in the model and are never written - see
 * {@link #dropAdditionalTerms(List)}.
 *
 * @author Philip Helger
 */
public final class PhinvoiceTermFilter
{
  private final PhinvoiceExportTarget m_aTarget;
  private final ErrorList m_aErrors;
  private int m_nDropCount;

  /**
   * Constructor.
   *
   * @param aTarget
   *        The target of the writer. May not be <code>null</code>.
   * @param aErrors
   *        The error list that receives a warning for every value that is not written. May not be
   *        <code>null</code>.
   */
  public PhinvoiceTermFilter (@NonNull final PhinvoiceExportTarget aTarget, @NonNull final ErrorList aErrors)
  {
    ValueEnforcer.notNull (aTarget, "Target");
    ValueEnforcer.notNull (aErrors, "Errors");
    m_aTarget = aTarget;
    m_aErrors = aErrors;
  }

  @NonNull
  private static PhinvoiceTerm _getTerm (@NonNull final String sTermID)
  {
    final PhinvoiceTerm aTerm = PhinvoiceTermRegistry.getTermOfID (sTermID);
    if (aTerm == null)
      throw new IllegalArgumentException ("The term ID '" + sTermID + "' is not in the term registry");
    return aTerm;
  }

  private static boolean _hasValue (@Nullable final Object aValue)
  {
    if (aValue == null)
      return false;
    if (aValue instanceof final CharSequence aCS)
      return aCS.length () > 0;
    if (aValue instanceof final Collection <?> aColl)
      return !aColl.isEmpty ();
    return true;
  }

  /**
   * @return The target of this filter. Never <code>null</code>.
   */
  @NonNull
  public PhinvoiceExportTarget getTarget ()
  {
    return m_aTarget;
  }

  /**
   * @return The number of values that were reported as not written so far. Always &ge; 0.
   */
  @Nonnegative
  public int getDropCount ()
  {
    return m_nDropCount;
  }

  /**
   * Check if the target allows the term, without reporting anything.
   *
   * @param sTermID
   *        The term ID, e.g. <code>BT-12</code>. Must be in the term registry.
   * @return <code>true</code> if the target allows the term.
   * @throws IllegalArgumentException
   *         if the term ID is not in the registry, because that is a programming error
   */
  public boolean isAllowed (@NonNull final String sTermID)
  {
    return PhinvoiceTermRegistry.isAllowed (_getTerm (sTermID), m_aTarget);
  }

  /**
   * Report a value as not written.
   *
   * @param sTermID
   *        The term ID of the value. May not be <code>null</code>.
   * @param aValue
   *        The value. May be <code>null</code>.
   * @param sReason
   *        The reason why it is not written. May not be <code>null</code>.
   */
  public void drop (@NonNull final String sTermID, @Nullable final Object aValue, @NonNull final String sReason)
  {
    m_nDropCount++;
    m_aErrors.add (SingleError.builderWarn ()
                              .errorFieldName (sTermID)
                              .errorText ("The value '" +
                                          PhinvoiceLogHelper.sanitise (String.valueOf (aValue)) +
                                          "' of " +
                                          sTermID +
                                          " is not written: " +
                                          sReason)
                              .build ());
  }

  /**
   * Check if a value of the term is to be written. A missing value is never written. A value that
   * the target does not allow is reported and not written.
   *
   * @param sTermID
   *        The term ID, e.g. <code>BT-12</code>. Must be in the term registry.
   * @param aValue
   *        The value. May be <code>null</code>. Empty strings and empty collections count as
   *        missing.
   * @return <code>true</code> if the value is present and the target allows the term.
   * @throws IllegalArgumentException
   *         if the term ID is not in the registry, because that is a programming error
   */
  public boolean use (@NonNull final String sTermID, @Nullable final Object aValue)
  {
    if (!_hasValue (aValue))
      return false;
    final PhinvoiceTerm aTerm = _getTerm (sTermID);
    if (PhinvoiceTermRegistry.isAllowed (aTerm, m_aTarget))
      return true;
    drop (sTermID,
          aValue,
          "the target " +
                    m_aTarget.getSyntaxKind ().getDisplayName () +
                    " " +
                    m_aTarget.getEdition ().getDisplayName () +
                    " " +
                    m_aTarget.getProfile ().getDisplayName () +
                    " does not allow '" +
                    aTerm.getName () +
                    "'");
    return false;
  }

  /**
   * Check if a business group is to be written. A group that does not exist in the edition of the
   * target is not decided here (only its terms are), so the result is <code>true</code> for any
   * present value. A group that exists in the edition but not in the profile of the target is
   * reported once and not written, so its terms do not have to be reported one by one.
   *
   * @param sGroupID
   *        The group ID, e.g. <code>BG-25</code>. Must be in the term registry.
   * @param aValue
   *        The value used for the report, e.g. a description of the group content. May be
   *        <code>null</code>. Empty strings and empty collections count as missing.
   * @return <code>true</code> if the value is present and the group is to be written.
   * @throws IllegalArgumentException
   *         if the group ID is not in the registry, because that is a programming error
   */
  public boolean useGroup (@NonNull final String sGroupID, @Nullable final Object aValue)
  {
    if (!_hasValue (aValue))
      return false;
    if (!_getTerm (sGroupID).existsIn (m_aTarget.getEdition ()))
      return true;
    return use (sGroupID, aValue);
  }

  /**
   * Get the BT-24 value to write. The value of the model is kept if it fits the target (see
   * {@link PhinvoiceExportTarget#isCompatibleSpecificationIdentifier(String)}), otherwise the
   * specification identifier of the target is used and the change is reported.
   *
   * @param sModelValue
   *        The BT-24 value of the model. May be <code>null</code>.
   * @return The BT-24 value to write. Neither <code>null</code> nor empty.
   */
  @NonNull
  @Nonempty
  public String getSpecificationIdentifier (@Nullable final String sModelValue)
  {
    if (m_aTarget.isCompatibleSpecificationIdentifier (sModelValue))
      return sModelValue;

    final String ret = m_aTarget.getSpecificationIdentifier ();
    if (sModelValue != null)
      m_aErrors.add (SingleError.builderWarn ()
                                .errorFieldName ("BT-24")
                                .errorText ("The specification identifier (BT-24) '" +
                                            PhinvoiceLogHelper.sanitise (sModelValue) +
                                            "' does not fit the target and is replaced with '" +
                                            ret +
                                            "'")
                                .build ());
    return ret;
  }

  /**
   * Report all additional terms as not written. They hold values for which the model has no field.
   *
   * @param aTerms
   *        The additional terms. May not be <code>null</code>.
   */
  public void dropAdditionalTerms (@NonNull final List <PhinvoiceAdditionalTerm> aTerms)
  {
    for (final PhinvoiceAdditionalTerm aTerm : aTerms)
      drop (aTerm.getTerm (), aTerm.getValue (), "the model has no field for it");
  }
}
