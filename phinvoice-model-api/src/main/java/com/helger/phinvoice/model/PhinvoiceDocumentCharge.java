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
 * BG-21 DOCUMENT LEVEL CHARGE (0..n; "charges and taxes" since EN 16931:2026). Carries BT-99
 * (charge amount), BT-100 (base amount), BT-101 (percentage), BT-102 (VAT category code), BT-103
 * (VAT rate), BT-104 (reason text) and BT-105 (reason code). Since EN 16931:2026 also BT-175 /
 * BT-176 (exemption reason text and code), BT-177 / BT-177-1 (non-VAT tax code and its list
 * identifier) and BT-214 (goods/services code). Mirror of {@link PhinvoiceDocumentAllowance}.
 *
 * @author Philip Helger
 */
public class PhinvoiceDocumentCharge
{
  private BigDecimal m_aAmount;
  private BigDecimal m_aBaseAmount;
  private BigDecimal m_aPercentage;
  private String m_sVatCategoryCode;
  private BigDecimal m_aVatRate;
  private String m_sReason;
  private String m_sReasonCode;
  private String m_sExemptionReasonText;
  private String m_sExemptionReasonCode;
  private String m_sNonVatTaxCode;
  private String m_sNonVatTaxCodeListID;
  private String m_sGoodsServicesCode;
  private BigDecimal m_aSequenceNumeric;
  private BigDecimal m_aBasisQuantity;
  private String m_sBasisQuantityUnit;

  /** Create an empty document-level charge. */
  public PhinvoiceDocumentCharge ()
  {}

  /**
   * Get the charge amount (BT-99).
   *
   * @return Charge amount (BT-99). May be {@code null}.
   */
  @Nullable
  public BigDecimal getAmount ()
  {
    return m_aAmount;
  }

  /**
   * Set the charge amount (BT-99).
   *
   * @param aAmount
   *        Amount; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceDocumentCharge setAmount (@Nullable final BigDecimal aAmount)
  {
    m_aAmount = aAmount;
    return this;
  }

  /**
   * Get the charge base amount (BT-100).
   *
   * @return Charge base amount (BT-100). May be {@code null}.
   */
  @Nullable
  public BigDecimal getBaseAmount ()
  {
    return m_aBaseAmount;
  }

  /**
   * Set the charge base amount (BT-100).
   *
   * @param aBaseAmount
   *        Base amount; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceDocumentCharge setBaseAmount (@Nullable final BigDecimal aBaseAmount)
  {
    m_aBaseAmount = aBaseAmount;
    return this;
  }

  /**
   * Get the charge percentage (BT-101).
   *
   * @return Charge percentage (BT-101). May be {@code null}.
   */
  @Nullable
  public BigDecimal getPercentage ()
  {
    return m_aPercentage;
  }

  /**
   * Set the charge percentage (BT-101).
   *
   * @param aPercentage
   *        Percentage as a {@link BigDecimal} (e.g. {@code 5} for 5 %); may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceDocumentCharge setPercentage (@Nullable final BigDecimal aPercentage)
  {
    m_aPercentage = aPercentage;
    return this;
  }

  /**
   * Get the VAT category code (BT-102) — UNTDID 5305. May be {@code null}.
   *
   * @return VAT category code (BT-102) — UNTDID 5305. May be {@code null}.
   */
  @Nullable
  public String getVatCategoryCode ()
  {
    return m_sVatCategoryCode;
  }

  /**
   * Set the VAT category code (BT-102).
   *
   * @param sVatCategoryCode
   *        UNTDID 5305 code; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceDocumentCharge setVatCategoryCode (@Nullable final String sVatCategoryCode)
  {
    m_sVatCategoryCode = sVatCategoryCode;
    return this;
  }

  /**
   * Get the VAT rate (BT-103).
   *
   * @return VAT rate (BT-103). May be {@code null}.
   */
  @Nullable
  public BigDecimal getVatRate ()
  {
    return m_aVatRate;
  }

  /**
   * Set the VAT rate (BT-103).
   *
   * @param aVatRate
   *        Rate as a {@link BigDecimal} (e.g. {@code 19} for 19 %); may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceDocumentCharge setVatRate (@Nullable final BigDecimal aVatRate)
  {
    m_aVatRate = aVatRate;
    return this;
  }

  /**
   * Get the charge reason text (BT-104).
   *
   * @return Charge reason text (BT-104). May be {@code null}.
   */
  @Nullable
  public String getReason ()
  {
    return m_sReason;
  }

  /**
   * Set the charge reason text (BT-104).
   *
   * @param sReason
   *        Reason text; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceDocumentCharge setReason (@Nullable final String sReason)
  {
    m_sReason = sReason;
    return this;
  }

  /**
   * Get the charge reason code (BT-105) — UNTDID 7161. May be {@code null}.
   *
   * @return Charge reason code (BT-105) — UNTDID 7161. May be {@code null}.
   */
  @Nullable
  public String getReasonCode ()
  {
    return m_sReasonCode;
  }

  /**
   * Set the charge reason code (BT-105).
   *
   * @param sReasonCode
   *        UNTDID 7161 code; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceDocumentCharge setReasonCode (@Nullable final String sReasonCode)
  {
    m_sReasonCode = sReasonCode;
    return this;
  }

  /**
   * Get the document level charge or tax exemption reason text (BT-175).
   *
   * @return Document level charge or tax exemption reason text (BT-175). May be {@code null}.
   */
  @Nullable
  public String getExemptionReasonText ()
  {
    return m_sExemptionReasonText;
  }

  /**
   * Set the document level charge or tax exemption reason text (BT-175).
   *
   * @param sExemptionReasonText
   *        Document level charge or tax exemption reason text; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceDocumentCharge setExemptionReasonText (@Nullable final String sExemptionReasonText)
  {
    m_sExemptionReasonText = sExemptionReasonText;
    return this;
  }

  /**
   * Get the VAT exemption reason and specification code of the document level charge or tax (BT-176).
   *
   * @return VAT exemption reason and specification code of the document level charge or tax (BT-176). May be {@code null}.
   */
  @Nullable
  public String getExemptionReasonCode ()
  {
    return m_sExemptionReasonCode;
  }

  /**
   * Set the VAT exemption reason and specification code of the document level charge or tax (BT-176).
   *
   * @param sExemptionReasonCode
   *        VAT exemption reason and specification code of the document level charge or tax; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceDocumentCharge setExemptionReasonCode (@Nullable final String sExemptionReasonCode)
  {
    m_sExemptionReasonCode = sExemptionReasonCode;
    return this;
  }

  /**
   * Get the document level non-VAT tax code (BT-177).
   *
   * @return Document level non-VAT tax code (BT-177). May be {@code null}.
   */
  @Nullable
  public String getNonVatTaxCode ()
  {
    return m_sNonVatTaxCode;
  }

  /**
   * Set the document level non-VAT tax code (BT-177).
   *
   * @param sNonVatTaxCode
   *        Document level non-VAT tax code; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceDocumentCharge setNonVatTaxCode (@Nullable final String sNonVatTaxCode)
  {
    m_sNonVatTaxCode = sNonVatTaxCode;
    return this;
  }

  /**
   * Get the document level non-VAT tax code list identifier (BT-177-1).
   *
   * @return Document level non-VAT tax code list identifier (BT-177-1). May be {@code null}.
   */
  @Nullable
  public String getNonVatTaxCodeListID ()
  {
    return m_sNonVatTaxCodeListID;
  }

  /**
   * Set the document level non-VAT tax code list identifier (BT-177-1).
   *
   * @param sNonVatTaxCodeListID
   *        Document level non-VAT tax code list identifier; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceDocumentCharge setNonVatTaxCodeListID (@Nullable final String sNonVatTaxCodeListID)
  {
    m_sNonVatTaxCodeListID = sNonVatTaxCodeListID;
    return this;
  }

  /**
   * Get the document level charge goods/services code (BT-214).
   *
   * @return Document level charge goods/services code (BT-214). May be {@code null}.
   */
  @Nullable
  public String getGoodsServicesCode ()
  {
    return m_sGoodsServicesCode;
  }

  /**
   * Set the document level charge goods/services code (BT-214).
   *
   * @param sGoodsServicesCode
   *        Document level charge goods/services code; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceDocumentCharge setGoodsServicesCode (@Nullable final String sGoodsServicesCode)
  {
    m_sGoodsServicesCode = sGoodsServicesCode;
    return this;
  }

  /**
   * Get the calculation sequence (BT-X-268).
   *
   * @return Calculation sequence (BT-X-268). May be {@code null}.
   */
  @Nullable
  public BigDecimal getSequenceNumeric ()
  {
    return m_aSequenceNumeric;
  }

  /**
   * Set the calculation sequence (BT-X-268).
   *
   * @param aSequenceNumeric
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceDocumentCharge setSequenceNumeric (@Nullable final BigDecimal aSequenceNumeric)
  {
    m_aSequenceNumeric = aSequenceNumeric;
    return this;
  }

  /**
   * Get the base quantity (BT-X-269).
   *
   * @return Base quantity (BT-X-269). May be {@code null}.
   */
  @Nullable
  public BigDecimal getBasisQuantity ()
  {
    return m_aBasisQuantity;
  }

  /**
   * Set the base quantity (BT-X-269).
   *
   * @param aBasisQuantity
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceDocumentCharge setBasisQuantity (@Nullable final BigDecimal aBasisQuantity)
  {
    m_aBasisQuantity = aBasisQuantity;
    return this;
  }

  /**
   * Get the unit of the base quantity (BT-X-270).
   *
   * @return Unit of the base quantity (BT-X-270). May be {@code null}.
   */
  @Nullable
  public String getBasisQuantityUnit ()
  {
    return m_sBasisQuantityUnit;
  }

  /**
   * Set the unit of the base quantity (BT-X-270).
   *
   * @param sBasisQuantityUnit
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceDocumentCharge setBasisQuantityUnit (@Nullable final String sBasisQuantityUnit)
  {
    m_sBasisQuantityUnit = sBasisQuantityUnit;
    return this;
  }
}
