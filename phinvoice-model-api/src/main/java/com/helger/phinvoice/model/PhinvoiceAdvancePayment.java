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
package com.helger.phinvoice.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;

/**
 * BG-X-45 ADVANCE PAYMENT of the Factur-X profile EXTENDED.
 *
 * @author Philip Helger
 */
public class PhinvoiceAdvancePayment
{
  private BigDecimal m_aPaidAmount;
  private LocalDate m_aReceivedDate;
  private final ICommonsList <PhinvoiceIncludedTax> m_aIncludedTaxes = new CommonsArrayList <> ();
  private PhinvoiceReferencedDocument m_aPrecedingInvoice;

  /** Create an empty instance. */
  public PhinvoiceAdvancePayment ()
  {}

  /**
   * Get the paid amount (BT-X-291).
   *
   * @return Paid amount (BT-X-291). May be {@code null}.
   */
  @Nullable
  public BigDecimal getPaidAmount ()
  {
    return m_aPaidAmount;
  }

  /**
   * Set the paid amount (BT-X-291).
   *
   * @param aPaidAmount
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceAdvancePayment setPaidAmount (@Nullable final BigDecimal aPaidAmount)
  {
    m_aPaidAmount = aPaidAmount;
    return this;
  }

  /**
   * Get the date the payment was received (BT-X-292).
   *
   * @return Date the payment was received (BT-X-292). May be {@code null}.
   */
  @Nullable
  public LocalDate getReceivedDate ()
  {
    return m_aReceivedDate;
  }

  /**
   * Set the date the payment was received (BT-X-292).
   *
   * @param aReceivedDate
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceAdvancePayment setReceivedDate (@Nullable final LocalDate aReceivedDate)
  {
    m_aReceivedDate = aReceivedDate;
    return this;
  }

  /**
   * Get the mutable list of included taxes (BG-X-46).
   *
   * @return Mutable list of included taxes (BG-X-46). Never {@code null}; empty when none are present.
   */
  @NonNull
  @ReturnsMutableObject
  public ICommonsList <PhinvoiceIncludedTax> includedTaxes ()
  {
    return m_aIncludedTaxes;
  }

  /**
   * Get the preceding invoice of the advance payment (BG-X-85).
   *
   * @return Preceding invoice of the advance payment (BG-X-85). May be {@code null}.
   */
  @Nullable
  public PhinvoiceReferencedDocument getPrecedingInvoice ()
  {
    return m_aPrecedingInvoice;
  }

  /**
   * Set the preceding invoice of the advance payment (BG-X-85).
   *
   * @param aPrecedingInvoice
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceAdvancePayment setPrecedingInvoice (@Nullable final PhinvoiceReferencedDocument aPrecedingInvoice)
  {
    m_aPrecedingInvoice = aPrecedingInvoice;
    return this;
  }

  /**
   * Check if no value is set.
   *
   * @return {@code true} when every field is {@code null} or empty.
   */
  public boolean isEmpty ()
  {
    return m_aPaidAmount == null &&
           m_aReceivedDate == null &&
           m_aIncludedTaxes.isEmpty () &&
           m_aPrecedingInvoice == null;
  }
}
