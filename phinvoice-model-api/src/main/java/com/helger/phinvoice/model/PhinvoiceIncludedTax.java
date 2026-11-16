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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * A tax included in an amount: in an advance payment (BG-X-46) or, for B2C, in the line price (BG-X-4) of
 * the Factur-X profile EXTENDED.
 *
 * @author Philip Helger
 */
public class PhinvoiceIncludedTax
{
  private BigDecimal m_aCalculatedAmount;
  private String m_sTypeCode;
  private String m_sCategoryCode;
  private BigDecimal m_aRate;
  private String m_sExemptionReason;
  private String m_sExemptionReasonCode;

  /** Create an empty instance. */
  public PhinvoiceIncludedTax ()
  {}

  /**
   * Get the tax amount.
   *
   * @return Tax amount. May be {@code null}.
   */
  @Nullable
  public BigDecimal getCalculatedAmount ()
  {
    return m_aCalculatedAmount;
  }

  /**
   * Set the tax amount.
   *
   * @param aCalculatedAmount
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceIncludedTax setCalculatedAmount (@Nullable final BigDecimal aCalculatedAmount)
  {
    m_aCalculatedAmount = aCalculatedAmount;
    return this;
  }

  /**
   * Get the tax type code.
   *
   * @return Tax type code. May be {@code null}.
   */
  @Nullable
  public String getTypeCode ()
  {
    return m_sTypeCode;
  }

  /**
   * Set the tax type code.
   *
   * @param sTypeCode
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceIncludedTax setTypeCode (@Nullable final String sTypeCode)
  {
    m_sTypeCode = sTypeCode;
    return this;
  }

  /**
   * Get the tax category code.
   *
   * @return Tax category code. May be {@code null}.
   */
  @Nullable
  public String getCategoryCode ()
  {
    return m_sCategoryCode;
  }

  /**
   * Set the tax category code.
   *
   * @param sCategoryCode
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceIncludedTax setCategoryCode (@Nullable final String sCategoryCode)
  {
    m_sCategoryCode = sCategoryCode;
    return this;
  }

  /**
   * Get the tax rate.
   *
   * @return Tax rate. May be {@code null}.
   */
  @Nullable
  public BigDecimal getRate ()
  {
    return m_aRate;
  }

  /**
   * Set the tax rate.
   *
   * @param aRate
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceIncludedTax setRate (@Nullable final BigDecimal aRate)
  {
    m_aRate = aRate;
    return this;
  }

  /**
   * Get the exemption reason.
   *
   * @return Exemption reason. May be {@code null}.
   */
  @Nullable
  public String getExemptionReason ()
  {
    return m_sExemptionReason;
  }

  /**
   * Set the exemption reason.
   *
   * @param sExemptionReason
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceIncludedTax setExemptionReason (@Nullable final String sExemptionReason)
  {
    m_sExemptionReason = sExemptionReason;
    return this;
  }

  /**
   * Get the exemption reason code.
   *
   * @return Exemption reason code. May be {@code null}.
   */
  @Nullable
  public String getExemptionReasonCode ()
  {
    return m_sExemptionReasonCode;
  }

  /**
   * Set the exemption reason code.
   *
   * @param sExemptionReasonCode
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceIncludedTax setExemptionReasonCode (@Nullable final String sExemptionReasonCode)
  {
    m_sExemptionReasonCode = sExemptionReasonCode;
    return this;
  }

  /**
   * Check if no value is set.
   *
   * @return {@code true} when every field is {@code null} or empty.
   */
  public boolean isEmpty ()
  {
    return m_aCalculatedAmount == null &&
           m_sTypeCode == null &&
           m_sCategoryCode == null &&
           m_aRate == null &&
           m_sExemptionReason == null &&
           m_sExemptionReasonCode == null;
  }
}
