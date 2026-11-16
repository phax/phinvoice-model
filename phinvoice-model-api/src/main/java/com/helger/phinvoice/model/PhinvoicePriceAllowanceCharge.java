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
 * An allowance or a charge on the gross price of a line. EN 16931 has only the amount of the first
 * allowance (BT-147); the rest is Factur-X EXTENDED: BT-X-34 to BT-X-36 and BT-X-313 for an
 * allowance, BT-X-300 to BT-X-303, BT-X-314 and BT-X-616 for a charge.
 *
 * @author Philip Helger
 */
public class PhinvoicePriceAllowanceCharge
{
  private BigDecimal m_aAmount;
  private BigDecimal m_aPercentage;
  private BigDecimal m_aBaseAmount;
  private String m_sReasonCode;
  private String m_sReason;
  private String m_sNonVatTaxCode;
  private String m_sNonVatTaxCodeListID;

  /** Create an empty instance. */
  public PhinvoicePriceAllowanceCharge ()
  {}

  /**
   * Get the amount (BT-147 or BT-X-302).
   *
   * @return Amount (BT-147 or BT-X-302). May be {@code null}.
   */
  @Nullable
  public BigDecimal getAmount ()
  {
    return m_aAmount;
  }

  /**
   * Set the amount (BT-147 or BT-X-302).
   *
   * @param aAmount
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoicePriceAllowanceCharge setAmount (@Nullable final BigDecimal aAmount)
  {
    m_aAmount = aAmount;
    return this;
  }

  /**
   * Get the percentage (BT-X-34 or BT-X-300).
   *
   * @return Percentage (BT-X-34 or BT-X-300). May be {@code null}.
   */
  @Nullable
  public BigDecimal getPercentage ()
  {
    return m_aPercentage;
  }

  /**
   * Set the percentage (BT-X-34 or BT-X-300).
   *
   * @param aPercentage
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoicePriceAllowanceCharge setPercentage (@Nullable final BigDecimal aPercentage)
  {
    m_aPercentage = aPercentage;
    return this;
  }

  /**
   * Get the base amount (BT-X-35 or BT-X-301).
   *
   * @return Base amount (BT-X-35 or BT-X-301). May be {@code null}.
   */
  @Nullable
  public BigDecimal getBaseAmount ()
  {
    return m_aBaseAmount;
  }

  /**
   * Set the base amount (BT-X-35 or BT-X-301).
   *
   * @param aBaseAmount
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoicePriceAllowanceCharge setBaseAmount (@Nullable final BigDecimal aBaseAmount)
  {
    m_aBaseAmount = aBaseAmount;
    return this;
  }

  /**
   * Get the reason code (BT-X-313 or BT-X-314).
   *
   * @return Reason code (BT-X-313 or BT-X-314). May be {@code null}.
   */
  @Nullable
  public String getReasonCode ()
  {
    return m_sReasonCode;
  }

  /**
   * Set the reason code (BT-X-313 or BT-X-314).
   *
   * @param sReasonCode
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoicePriceAllowanceCharge setReasonCode (@Nullable final String sReasonCode)
  {
    m_sReasonCode = sReasonCode;
    return this;
  }

  /**
   * Get the reason (BT-X-36 or BT-X-303).
   *
   * @return Reason (BT-X-36 or BT-X-303). May be {@code null}.
   */
  @Nullable
  public String getReason ()
  {
    return m_sReason;
  }

  /**
   * Set the reason (BT-X-36 or BT-X-303).
   *
   * @param sReason
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoicePriceAllowanceCharge setReason (@Nullable final String sReason)
  {
    m_sReason = sReason;
    return this;
  }

  /**
   * Get the non-VAT tax code of a charge (BT-X-616).
   *
   * @return Non-VAT tax code of a charge (BT-X-616). May be {@code null}.
   */
  @Nullable
  public String getNonVatTaxCode ()
  {
    return m_sNonVatTaxCode;
  }

  /**
   * Set the non-VAT tax code of a charge (BT-X-616).
   *
   * @param sNonVatTaxCode
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoicePriceAllowanceCharge setNonVatTaxCode (@Nullable final String sNonVatTaxCode)
  {
    m_sNonVatTaxCode = sNonVatTaxCode;
    return this;
  }

  /**
   * Get the list identifier of the non-VAT tax code (BT-X-616-1).
   *
   * @return List identifier of the non-VAT tax code (BT-X-616-1). May be {@code null}.
   */
  @Nullable
  public String getNonVatTaxCodeListID ()
  {
    return m_sNonVatTaxCodeListID;
  }

  /**
   * Set the list identifier of the non-VAT tax code (BT-X-616-1).
   *
   * @param sNonVatTaxCodeListID
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoicePriceAllowanceCharge setNonVatTaxCodeListID (@Nullable final String sNonVatTaxCodeListID)
  {
    m_sNonVatTaxCodeListID = sNonVatTaxCodeListID;
    return this;
  }

  /**
   * Check if no value is set.
   *
   * @return {@code true} when every field is {@code null} or empty.
   */
  public boolean isEmpty ()
  {
    return m_aAmount == null &&
           m_aPercentage == null &&
           m_aBaseAmount == null &&
           m_sReasonCode == null &&
           m_sReason == null &&
           m_sNonVatTaxCode == null &&
           m_sNonVatTaxCodeListID == null;
  }
}
