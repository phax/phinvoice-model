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
 * BG-33 PAYMENT TERMS (0..n). Wraps the payment term text (BT-20) together with the structured
 * early payment discounts (BG-35) and late payment penalties (BG-36). Introduced by EN 16931:2026;
 * readers of EN 16931:2017 documents create one entry that only carries the text.
 *
 * @author Philip Helger
 */
public class PhinvoicePaymentTerms
{
  private String m_sText;
  private BigDecimal m_aPartialPaymentAmount;
  private LocalDate m_aDueDate;
  private PhinvoiceTradeParty m_aPayee;
  private final ICommonsList <PhinvoiceEarlyPaymentDiscount> m_aEarlyDiscounts = new CommonsArrayList <> ();
  private final ICommonsList <PhinvoiceLatePaymentPenalty> m_aLatePenalties = new CommonsArrayList <> ();

  /** Create an empty instance. */
  public PhinvoicePaymentTerms ()
  {}

  /**
   * Get the payment term text (BT-20).
   *
   * @return Payment term text (BT-20). May be {@code null}.
   */
  @Nullable
  public String getText ()
  {
    return m_sText;
  }

  /**
   * Set the payment term text (BT-20).
   *
   * @param sText
   *        Payment term text; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoicePaymentTerms setText (@Nullable final String sText)
  {
    m_sText = sText;
    return this;
  }

  /**
   * Get the mutable list of early payment discounts (BG-35).
   *
   * @return Mutable list of early payment discounts (BG-35). Never {@code null}; empty when none are present.
   */
  @NonNull
  @ReturnsMutableObject
  public ICommonsList <PhinvoiceEarlyPaymentDiscount> earlyDiscounts ()
  {
    return m_aEarlyDiscounts;
  }

  /**
   * Get the mutable list of late payment penalties (BG-36).
   *
   * @return Mutable list of late payment penalties (BG-36). Never {@code null}; empty when none are present.
   */
  @NonNull
  @ReturnsMutableObject
  public ICommonsList <PhinvoiceLatePaymentPenalty> latePenalties ()
  {
    return m_aLatePenalties;
  }

  /**
   * Get the partial payment amount (BT-X-275).
   *
   * @return Partial payment amount (BT-X-275). May be {@code null}.
   */
  @Nullable
  public BigDecimal getPartialPaymentAmount ()
  {
    return m_aPartialPaymentAmount;
  }

  /**
   * Set the partial payment amount (BT-X-275).
   *
   * @param aPartialPaymentAmount
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoicePaymentTerms setPartialPaymentAmount (@Nullable final BigDecimal aPartialPaymentAmount)
  {
    m_aPartialPaymentAmount = aPartialPaymentAmount;
    return this;
  }

  /**
   * Get the payee of this payment (BG-X-77).
   *
   * @return Payee of this payment (BG-X-77). May be {@code null}.
   */
  @Nullable
  public PhinvoiceTradeParty getPayee ()
  {
    return m_aPayee;
  }

  /**
   * Set the payee of this payment (BG-X-77).
   *
   * @param aPayee
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoicePaymentTerms setPayee (@Nullable final PhinvoiceTradeParty aPayee)
  {
    m_aPayee = aPayee;
    return this;
  }

  /**
   * Get the due date of these payment terms (BT-9 of a further payment terms in Factur-X EXTENDED).
   * The due date of the first payment terms is the payment due date of the invoice (BT-9), see
   * {@link PhinvoiceInvoice#getPaymentDueDate()}.
   *
   * @return Due date of these payment terms. May be {@code null}.
   */
  @Nullable
  public LocalDate getDueDate ()
  {
    return m_aDueDate;
  }

  /**
   * Set the due date of these payment terms (BT-9 of a further payment terms in Factur-X EXTENDED).
   *
   * @param aDueDate
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoicePaymentTerms setDueDate (@Nullable final LocalDate aDueDate)
  {
    m_aDueDate = aDueDate;
    return this;
  }
}
