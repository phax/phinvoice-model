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

/**
 * BG-36 LATE PAYMENT PENALTY (0..n within BG-33). Structured late-payment penalty: the first
 * date on which it applies (BT-181) and the penalty as yearly interest percentage (BT-182)
 * and/or fixed amount (BT-183). Introduced by EN 16931:2026.
 *
 * @author Philip Helger
 */
public class PhinvoiceLatePaymentPenalty
{
  private LocalDate m_aStartDate;
  private BigDecimal m_aYearlyInterestPercentage;
  private BigDecimal m_aAmount;
  private BigDecimal m_aBasisPeriodMeasure;
  private String m_sBasisPeriodUnit;
  private BigDecimal m_aBaseAmount;

  /** Create an empty instance. */
  public PhinvoiceLatePaymentPenalty ()
  {}

  /**
   * Get the penalty start date (BT-181).
   *
   * @return Penalty start date (BT-181). May be {@code null}.
   */
  @Nullable
  public LocalDate getStartDate ()
  {
    return m_aStartDate;
  }

  /**
   * Set the penalty start date (BT-181).
   *
   * @param aStartDate
   *        Penalty start date; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceLatePaymentPenalty setStartDate (@Nullable final LocalDate aStartDate)
  {
    m_aStartDate = aStartDate;
    return this;
  }

  /**
   * Get the penalty yearly interest percentage (BT-182).
   *
   * @return Penalty yearly interest percentage (BT-182). May be {@code null}.
   */
  @Nullable
  public BigDecimal getYearlyInterestPercentage ()
  {
    return m_aYearlyInterestPercentage;
  }

  /**
   * Set the penalty yearly interest percentage (BT-182).
   *
   * @param aYearlyInterestPercentage
   *        Penalty yearly interest percentage; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceLatePaymentPenalty setYearlyInterestPercentage (@Nullable final BigDecimal aYearlyInterestPercentage)
  {
    m_aYearlyInterestPercentage = aYearlyInterestPercentage;
    return this;
  }

  /**
   * Get the penalty amount (BT-183).
   *
   * @return Penalty amount (BT-183). May be {@code null}.
   */
  @Nullable
  public BigDecimal getAmount ()
  {
    return m_aAmount;
  }

  /**
   * Set the penalty amount (BT-183).
   *
   * @param aAmount
   *        Penalty amount; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceLatePaymentPenalty setAmount (@Nullable final BigDecimal aAmount)
  {
    m_aAmount = aAmount;
    return this;
  }

  /**
   * Get the due date period (BT-X-277).
   *
   * @return Due date period (BT-X-277). May be {@code null}.
   */
  @Nullable
  public BigDecimal getBasisPeriodMeasure ()
  {
    return m_aBasisPeriodMeasure;
  }

  /**
   * Set the due date period (BT-X-277).
   *
   * @param aBasisPeriodMeasure
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceLatePaymentPenalty setBasisPeriodMeasure (@Nullable final BigDecimal aBasisPeriodMeasure)
  {
    m_aBasisPeriodMeasure = aBasisPeriodMeasure;
    return this;
  }

  /**
   * Get the unit of the due date period (BT-X-278).
   *
   * @return Unit of the due date period (BT-X-278). May be {@code null}.
   */
  @Nullable
  public String getBasisPeriodUnit ()
  {
    return m_sBasisPeriodUnit;
  }

  /**
   * Set the unit of the due date period (BT-X-278).
   *
   * @param sBasisPeriodUnit
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceLatePaymentPenalty setBasisPeriodUnit (@Nullable final String sBasisPeriodUnit)
  {
    m_sBasisPeriodUnit = sBasisPeriodUnit;
    return this;
  }

  /**
   * Get the base amount (BT-X-279).
   *
   * @return Base amount (BT-X-279). May be {@code null}.
   */
  @Nullable
  public BigDecimal getBaseAmount ()
  {
    return m_aBaseAmount;
  }

  /**
   * Set the base amount (BT-X-279).
   *
   * @param aBaseAmount
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceLatePaymentPenalty setBaseAmount (@Nullable final BigDecimal aBaseAmount)
  {
    m_aBaseAmount = aBaseAmount;
    return this;
  }
}
