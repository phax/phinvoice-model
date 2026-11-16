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
 * BG-X-42 LOGISTICS SERVICE CHARGE of the Factur-X profile EXTENDED.
 *
 * @author Philip Helger
 */
public class PhinvoiceLogisticsServiceCharge
{
  private String m_sDescription;
  private BigDecimal m_aAmount;
  private String m_sTaxCategoryCode;
  private BigDecimal m_aTaxRate;
  private String m_sTaxExemptionReason;
  private String m_sTaxExemptionReasonCode;

  /** Create an empty instance. */
  public PhinvoiceLogisticsServiceCharge ()
  {}

  /**
   * Get the description.
   *
   * @return Description. May be {@code null}.
   */
  @Nullable
  public String getDescription ()
  {
    return m_sDescription;
  }

  /**
   * Set the description.
   *
   * @param sDescription
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceLogisticsServiceCharge setDescription (@Nullable final String sDescription)
  {
    m_sDescription = sDescription;
    return this;
  }

  /**
   * Get the amount.
   *
   * @return Amount. May be {@code null}.
   */
  @Nullable
  public BigDecimal getAmount ()
  {
    return m_aAmount;
  }

  /**
   * Set the amount.
   *
   * @param aAmount
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceLogisticsServiceCharge setAmount (@Nullable final BigDecimal aAmount)
  {
    m_aAmount = aAmount;
    return this;
  }

  /**
   * Get the VAT category code.
   *
   * @return VAT category code. May be {@code null}.
   */
  @Nullable
  public String getTaxCategoryCode ()
  {
    return m_sTaxCategoryCode;
  }

  /**
   * Set the VAT category code.
   *
   * @param sTaxCategoryCode
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceLogisticsServiceCharge setTaxCategoryCode (@Nullable final String sTaxCategoryCode)
  {
    m_sTaxCategoryCode = sTaxCategoryCode;
    return this;
  }

  /**
   * Get the VAT rate.
   *
   * @return VAT rate. May be {@code null}.
   */
  @Nullable
  public BigDecimal getTaxRate ()
  {
    return m_aTaxRate;
  }

  /**
   * Set the VAT rate.
   *
   * @param aTaxRate
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceLogisticsServiceCharge setTaxRate (@Nullable final BigDecimal aTaxRate)
  {
    m_aTaxRate = aTaxRate;
    return this;
  }

  /**
   * Get the VAT exemption reason.
   *
   * @return VAT exemption reason. May be {@code null}.
   */
  @Nullable
  public String getTaxExemptionReason ()
  {
    return m_sTaxExemptionReason;
  }

  /**
   * Set the VAT exemption reason.
   *
   * @param sTaxExemptionReason
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceLogisticsServiceCharge setTaxExemptionReason (@Nullable final String sTaxExemptionReason)
  {
    m_sTaxExemptionReason = sTaxExemptionReason;
    return this;
  }

  /**
   * Get the VAT exemption reason code.
   *
   * @return VAT exemption reason code. May be {@code null}.
   */
  @Nullable
  public String getTaxExemptionReasonCode ()
  {
    return m_sTaxExemptionReasonCode;
  }

  /**
   * Set the VAT exemption reason code.
   *
   * @param sTaxExemptionReasonCode
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceLogisticsServiceCharge setTaxExemptionReasonCode (@Nullable final String sTaxExemptionReasonCode)
  {
    m_sTaxExemptionReasonCode = sTaxExemptionReasonCode;
    return this;
  }

  /**
   * Check if no value is set.
   *
   * @return {@code true} when every field is {@code null} or empty.
   */
  public boolean isEmpty ()
  {
    return m_sDescription == null &&
           m_aAmount == null &&
           m_sTaxCategoryCode == null &&
           m_aTaxRate == null &&
           m_sTaxExemptionReason == null &&
           m_sTaxExemptionReasonCode == null;
  }
}
