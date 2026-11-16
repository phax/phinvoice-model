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
 * BG-25 INVOICE LINE (1..n) plus all the nested line-level groups (BG-26 invoice line period,
 * BG-27 line allowances, BG-28 line charges, BG-29 price details, BG-30 line VAT, BG-31 item
 * information with BG-32 item attributes). Carries BT-126 (line identifier), BT-127 (line note),
 * BT-128/BT-128-1 (line object identifier), BT-129 (quantity), BT-130 (quantity unit),
 * BT-131 (line net amount), BT-132 (referenced purchase order line) and BT-133 (line buyer
 * accounting reference).
 *
 * @author Philip Helger
 */
public class PhinvoiceInvoiceLine
{
  private String m_sID;
  private String m_sNote;
  private PhinvoiceIdentifier m_aObjectIdentifier;
  private BigDecimal m_aQuantity;
  private String m_sQuantityUnit;
  private BigDecimal m_aNetAmount;
  private String m_sPurchaseOrderLineRef;
  private String m_sAccountingCost;
  private PhinvoiceInvoicingPeriod m_aPeriod;
  private final ICommonsList <PhinvoiceLineAllowance> m_aAllowances = new CommonsArrayList <> ();
  private final ICommonsList <PhinvoiceLineCharge> m_aCharges = new CommonsArrayList <> ();
  private PhinvoicePriceDetails m_aPrice;
  private PhinvoiceLineVat m_aLineVat;
  private PhinvoiceItem m_aItem;
  private String m_sPurchaseOrderReference;
  private String m_sSalesOrderReference;
  private String m_sSalesOrderLineReference;
  private String m_sDespatchAdviceReference;
  private String m_sDespatchAdviceLineReference;
  private String m_sReceivingAdviceReference;
  private String m_sReceivingAdviceLineReference;
  private String m_sDeliveryNoteReference;
  private String m_sDeliveryNoteLineReference;
  private PhinvoiceLineDelivery m_aDelivery;
  private String m_sParentLineID;
  private String m_sLineStatusCode;
  private String m_sLineStatusReasonCode;
  private String m_sNoteContentCode;
  private String m_sNoteSubjectCode;
  private BigDecimal m_aChargeFreeQuantity;
  private String m_sChargeFreeQuantityUnit;
  private BigDecimal m_aPackageQuantity;
  private String m_sPackageQuantityUnit;
  private BigDecimal m_aPerPackageUnitQuantity;
  private String m_sPerPackageUnitQuantityUnit;
  private LocalDate m_aPurchaseOrderDate;
  private LocalDate m_aSalesOrderDate;
  private LocalDate m_aDespatchAdviceDate;
  private LocalDate m_aReceivingAdviceDate;
  private LocalDate m_aDeliveryNoteDate;
  private PhinvoiceReferencedDocument m_aQuotationReference;
  private PhinvoiceReferencedDocument m_aContractReference;
  private final ICommonsList <PhinvoiceReferencedDocument> m_aAdditionalReferencedDocuments = new CommonsArrayList <> ();
  private final ICommonsList <PhinvoiceReferencedDocument> m_aUltimateCustomerOrderReferences = new CommonsArrayList <> ();
  private PhinvoiceTradeParty m_aLineSeller;
  private PhinvoiceTradeParty m_aUltimateShipTo;
  private PhinvoiceIncludedTax m_aIncludedTax;
  private PhinvoiceDeliveryTerms m_aDeliveryTerms;
  private BigDecimal m_aChargeTotal;
  private BigDecimal m_aAllowanceTotal;
  private BigDecimal m_aTaxTotal;
  private BigDecimal m_aTaxTotalAccountingCurrency;
  private BigDecimal m_aGrandTotal;
  private BigDecimal m_aTotalAllowanceCharge;
  private String m_sAccountingCostTypeCode;
  private final ICommonsList <PhinvoiceLinePrecedingInvoiceRef> m_aPrecedingInvoiceRefs = new CommonsArrayList <> ();
  private final ICommonsList <PhinvoiceAdditionalTerm> m_aAdditionalTerms = new CommonsArrayList <> ();

  /** Create an empty invoice line. */
  public PhinvoiceInvoiceLine ()
  {}

  /**
   * Get the mutable list of the values of the line that the model has no place for.
   *
   * @return Mutable list of the additional terms. Never {@code null}; empty when there are none.
   */
  @NonNull
  @ReturnsMutableObject
  public ICommonsList <PhinvoiceAdditionalTerm> additionalTerms ()
  {
    return m_aAdditionalTerms;
  }

  /**
   * Get the invoice line identifier (BT-126).
   *
   * @return Invoice line identifier (BT-126). May be {@code null}.
   */
  @Nullable
  public String getID ()
  {
    return m_sID;
  }

  /**
   * Set the invoice line identifier (BT-126).
   *
   * @param sID
   *        Line ID; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setID (@Nullable final String sID)
  {
    m_sID = sID;
    return this;
  }

  /**
   * Get the invoice line note (BT-127).
   *
   * @return Invoice line note (BT-127). May be {@code null}.
   */
  @Nullable
  public String getNote ()
  {
    return m_sNote;
  }

  /**
   * Set the invoice line note (BT-127).
   *
   * @param sNote
   *        Note text; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setNote (@Nullable final String sNote)
  {
    m_sNote = sNote;
    return this;
  }

  /**
   * Get the invoice line object identifier (BT-128 with BT-128-1 scheme ID).
   *
   * @return Invoice line object identifier (BT-128 with BT-128-1 scheme ID). May be {@code null}.
   */
  @Nullable
  public PhinvoiceIdentifier getObjectIdentifier ()
  {
    return m_aObjectIdentifier;
  }

  /**
   * Set the invoice line object identifier (BT-128 / BT-128-1).
   *
   * @param aObjectIdentifier
   *        Identifier; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setObjectIdentifier (@Nullable final PhinvoiceIdentifier aObjectIdentifier)
  {
    m_aObjectIdentifier = aObjectIdentifier;
    return this;
  }

  /**
   * Get the invoiced quantity (BT-129) — credit-note credited quantity also stored here.
   *
   * @return Invoiced quantity (BT-129) — credit-note credited quantity also stored here. May be {@code null}.
   */
  @Nullable
  public BigDecimal getQuantity ()
  {
    return m_aQuantity;
  }

  /**
   * Set the invoiced quantity (BT-129).
   *
   * @param aQuantity
   *        Quantity; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setQuantity (@Nullable final BigDecimal aQuantity)
  {
    m_aQuantity = aQuantity;
    return this;
  }

  /**
   * Get the invoiced quantity unit of measure (BT-130) — UN/ECE Rec 20/21. May be {@code null}.
   *
   * @return Invoiced quantity unit of measure (BT-130) — UN/ECE Rec 20/21. May be {@code null}.
   */
  @Nullable
  public String getQuantityUnit ()
  {
    return m_sQuantityUnit;
  }

  /**
   * Set the invoiced quantity unit of measure (BT-130).
   *
   * @param sQuantityUnit
   *        Unit code; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setQuantityUnit (@Nullable final String sQuantityUnit)
  {
    m_sQuantityUnit = sQuantityUnit;
    return this;
  }

  /**
   * Get the invoice line net amount (BT-131).
   *
   * @return Invoice line net amount (BT-131). May be {@code null}.
   */
  @Nullable
  public BigDecimal getNetAmount ()
  {
    return m_aNetAmount;
  }

  /**
   * Set the invoice line net amount (BT-131).
   *
   * @param aNetAmount
   *        Net amount; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setNetAmount (@Nullable final BigDecimal aNetAmount)
  {
    m_aNetAmount = aNetAmount;
    return this;
  }

  /**
   * Get the referenced purchase order line reference (BT-132).
   *
   * @return Referenced purchase order line reference (BT-132). May be {@code null}.
   */
  @Nullable
  public String getPurchaseOrderLineRef ()
  {
    return m_sPurchaseOrderLineRef;
  }

  /**
   * Set the referenced purchase order line reference (BT-132).
   *
   * @param sPurchaseOrderLineRef
   *        PO line reference; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setPurchaseOrderLineRef (@Nullable final String sPurchaseOrderLineRef)
  {
    m_sPurchaseOrderLineRef = sPurchaseOrderLineRef;
    return this;
  }

  /**
   * Get the invoice line buyer accounting reference (BT-133).
   *
   * @return Invoice line buyer accounting reference (BT-133). May be {@code null}.
   */
  @Nullable
  public String getAccountingCost ()
  {
    return m_sAccountingCost;
  }

  /**
   * Set the invoice line buyer accounting reference (BT-133).
   *
   * @param sAccountingCost
   *        Accounting cost reference; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setAccountingCost (@Nullable final String sAccountingCost)
  {
    m_sAccountingCost = sAccountingCost;
    return this;
  }

  /**
   * Get the invoice line period (BG-26 — BT-134 / BT-135).
   *
   * @return Invoice line period (BG-26 — BT-134 / BT-135). May be {@code null}.
   */
  @Nullable
  public PhinvoiceInvoicingPeriod getPeriod ()
  {
    return m_aPeriod;
  }

  /**
   * Set the invoice line period (BG-26).
   *
   * @param aPeriod
   *        Period; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setPeriod (@Nullable final PhinvoiceInvoicingPeriod aPeriod)
  {
    m_aPeriod = aPeriod;
    return this;
  }

  /**
   * Get the mutable list of line allowances (BG-27).
   *
   * @return Mutable list of line allowances (BG-27). Never {@code null}; empty when none are
   *         present.
   */
  @NonNull
  @ReturnsMutableObject
  public ICommonsList <PhinvoiceLineAllowance> allowances ()
  {
    return m_aAllowances;
  }

  /**
   * Get the mutable list of line charges (BG-28).
   *
   * @return Mutable list of line charges (BG-28). Never {@code null}; empty when none are
   *         present.
   */
  @NonNull
  @ReturnsMutableObject
  public ICommonsList <PhinvoiceLineCharge> charges ()
  {
    return m_aCharges;
  }

  /**
   * Get the price details (BG-29).
   *
   * @return Price details (BG-29). May be {@code null}.
   */
  @Nullable
  public PhinvoicePriceDetails getPrice ()
  {
    return m_aPrice;
  }

  /**
   * Set the price details (BG-29).
   *
   * @param aPrice
   *        Price details; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setPrice (@Nullable final PhinvoicePriceDetails aPrice)
  {
    m_aPrice = aPrice;
    return this;
  }

  /**
   * Get the line VAT information (BG-30).
   *
   * @return Line VAT information (BG-30). May be {@code null}.
   */
  @Nullable
  public PhinvoiceLineVat getLineVat ()
  {
    return m_aLineVat;
  }

  /**
   * Set the line VAT information (BG-30).
   *
   * @param aLineVat
   *        Line VAT; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setLineVat (@Nullable final PhinvoiceLineVat aLineVat)
  {
    m_aLineVat = aLineVat;
    return this;
  }

  /**
   * Get the item information (BG-31).
   *
   * @return Item information (BG-31). May be {@code null}.
   */
  @Nullable
  public PhinvoiceItem getItem ()
  {
    return m_aItem;
  }

  /**
   * Set the item information (BG-31).
   *
   * @param aItem
   *        Item; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setItem (@Nullable final PhinvoiceItem aItem)
  {
    m_aItem = aItem;
    return this;
  }

  /**
   * Get the invoice line purchase order reference (BT-188).
   *
   * @return Invoice line purchase order reference (BT-188). May be {@code null}.
   */
  @Nullable
  public String getPurchaseOrderReference ()
  {
    return m_sPurchaseOrderReference;
  }

  /**
   * Set the invoice line purchase order reference (BT-188).
   *
   * @param sPurchaseOrderReference
   *        Invoice line purchase order reference; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setPurchaseOrderReference (@Nullable final String sPurchaseOrderReference)
  {
    m_sPurchaseOrderReference = sPurchaseOrderReference;
    return this;
  }

  /**
   * Get the invoice line sales order reference (BT-200).
   *
   * @return Invoice line sales order reference (BT-200). May be {@code null}.
   */
  @Nullable
  public String getSalesOrderReference ()
  {
    return m_sSalesOrderReference;
  }

  /**
   * Set the invoice line sales order reference (BT-200).
   *
   * @param sSalesOrderReference
   *        Invoice line sales order reference; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setSalesOrderReference (@Nullable final String sSalesOrderReference)
  {
    m_sSalesOrderReference = sSalesOrderReference;
    return this;
  }

  /**
   * Get the invoice line sales order line reference (BT-201).
   *
   * @return Invoice line sales order line reference (BT-201). May be {@code null}.
   */
  @Nullable
  public String getSalesOrderLineReference ()
  {
    return m_sSalesOrderLineReference;
  }

  /**
   * Set the invoice line sales order line reference (BT-201).
   *
   * @param sSalesOrderLineReference
   *        Invoice line sales order line reference; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setSalesOrderLineReference (@Nullable final String sSalesOrderLineReference)
  {
    m_sSalesOrderLineReference = sSalesOrderLineReference;
    return this;
  }

  /**
   * Get the invoice line despatch advice reference (BT-189).
   *
   * @return Invoice line despatch advice reference (BT-189). May be {@code null}.
   */
  @Nullable
  public String getDespatchAdviceReference ()
  {
    return m_sDespatchAdviceReference;
  }

  /**
   * Set the invoice line despatch advice reference (BT-189).
   *
   * @param sDespatchAdviceReference
   *        Invoice line despatch advice reference; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setDespatchAdviceReference (@Nullable final String sDespatchAdviceReference)
  {
    m_sDespatchAdviceReference = sDespatchAdviceReference;
    return this;
  }

  /**
   * Get the invoice line despatch advice line reference (BT-190).
   *
   * @return Invoice line despatch advice line reference (BT-190). May be {@code null}.
   */
  @Nullable
  public String getDespatchAdviceLineReference ()
  {
    return m_sDespatchAdviceLineReference;
  }

  /**
   * Set the invoice line despatch advice line reference (BT-190).
   *
   * @param sDespatchAdviceLineReference
   *        Invoice line despatch advice line reference; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setDespatchAdviceLineReference (@Nullable final String sDespatchAdviceLineReference)
  {
    m_sDespatchAdviceLineReference = sDespatchAdviceLineReference;
    return this;
  }

  /**
   * Get the invoice line receiving advice reference (BT-191).
   *
   * @return Invoice line receiving advice reference (BT-191). May be {@code null}.
   */
  @Nullable
  public String getReceivingAdviceReference ()
  {
    return m_sReceivingAdviceReference;
  }

  /**
   * Set the invoice line receiving advice reference (BT-191).
   *
   * @param sReceivingAdviceReference
   *        Invoice line receiving advice reference; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setReceivingAdviceReference (@Nullable final String sReceivingAdviceReference)
  {
    m_sReceivingAdviceReference = sReceivingAdviceReference;
    return this;
  }

  /**
   * Get the invoice line receiving advice line reference (BT-192).
   *
   * @return Invoice line receiving advice line reference (BT-192). May be {@code null}.
   */
  @Nullable
  public String getReceivingAdviceLineReference ()
  {
    return m_sReceivingAdviceLineReference;
  }

  /**
   * Set the invoice line receiving advice line reference (BT-192).
   *
   * @param sReceivingAdviceLineReference
   *        Invoice line receiving advice line reference; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setReceivingAdviceLineReference (@Nullable final String sReceivingAdviceLineReference)
  {
    m_sReceivingAdviceLineReference = sReceivingAdviceLineReference;
    return this;
  }

  /**
   * Get the invoice line delivery note reference (BT-198).
   *
   * @return Invoice line delivery note reference (BT-198). May be {@code null}.
   */
  @Nullable
  public String getDeliveryNoteReference ()
  {
    return m_sDeliveryNoteReference;
  }

  /**
   * Set the invoice line delivery note reference (BT-198).
   *
   * @param sDeliveryNoteReference
   *        Invoice line delivery note reference; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setDeliveryNoteReference (@Nullable final String sDeliveryNoteReference)
  {
    m_sDeliveryNoteReference = sDeliveryNoteReference;
    return this;
  }

  /**
   * Get the invoice line delivery note line reference (BT-199).
   *
   * @return Invoice line delivery note line reference (BT-199). May be {@code null}.
   */
  @Nullable
  public String getDeliveryNoteLineReference ()
  {
    return m_sDeliveryNoteLineReference;
  }

  /**
   * Set the invoice line delivery note line reference (BT-199).
   *
   * @param sDeliveryNoteLineReference
   *        Invoice line delivery note line reference; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setDeliveryNoteLineReference (@Nullable final String sDeliveryNoteLineReference)
  {
    m_sDeliveryNoteLineReference = sDeliveryNoteLineReference;
    return this;
  }

  /**
   * Get the invoice line delivery information (BG-37).
   *
   * @return Invoice line delivery information (BG-37). May be {@code null}.
   */
  @Nullable
  public PhinvoiceLineDelivery getDelivery ()
  {
    return m_aDelivery;
  }

  /**
   * Set the invoice line delivery information (BG-37).
   *
   * @param aDelivery
   *        Invoice line delivery information; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setDelivery (@Nullable final PhinvoiceLineDelivery aDelivery)
  {
    m_aDelivery = aDelivery;
    return this;
  }

  /**
   * Get the mutable list of line-level preceding invoice references (BG-39).
   *
   * @return Mutable list of line-level preceding invoice references (BG-39). Never {@code null}; empty when none are present.
   */
  @NonNull
  @ReturnsMutableObject
  public ICommonsList <PhinvoiceLinePrecedingInvoiceRef> precedingInvoiceRefs ()
  {
    return m_aPrecedingInvoiceRefs;
  }

  /**
   * Get the parent line identifier (BT-X-304).
   *
   * @return Parent line identifier (BT-X-304). May be {@code null}.
   */
  @Nullable
  public String getParentLineID ()
  {
    return m_sParentLineID;
  }

  /**
   * Set the parent line identifier (BT-X-304).
   *
   * @param sParentLineID
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setParentLineID (@Nullable final String sParentLineID)
  {
    m_sParentLineID = sParentLineID;
    return this;
  }

  /**
   * Get the line status code (BT-X-7).
   *
   * @return Line status code (BT-X-7). May be {@code null}.
   */
  @Nullable
  public String getLineStatusCode ()
  {
    return m_sLineStatusCode;
  }

  /**
   * Set the line status code (BT-X-7).
   *
   * @param sLineStatusCode
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setLineStatusCode (@Nullable final String sLineStatusCode)
  {
    m_sLineStatusCode = sLineStatusCode;
    return this;
  }

  /**
   * Get the line status reason code (BT-X-8).
   *
   * @return Line status reason code (BT-X-8). May be {@code null}.
   */
  @Nullable
  public String getLineStatusReasonCode ()
  {
    return m_sLineStatusReasonCode;
  }

  /**
   * Set the line status reason code (BT-X-8).
   *
   * @param sLineStatusReasonCode
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setLineStatusReasonCode (@Nullable final String sLineStatusReasonCode)
  {
    m_sLineStatusReasonCode = sLineStatusReasonCode;
    return this;
  }

  /**
   * Get the line note content code (BT-X-9).
   *
   * @return Line note content code (BT-X-9). May be {@code null}.
   */
  @Nullable
  public String getNoteContentCode ()
  {
    return m_sNoteContentCode;
  }

  /**
   * Set the line note content code (BT-X-9).
   *
   * @param sNoteContentCode
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setNoteContentCode (@Nullable final String sNoteContentCode)
  {
    m_sNoteContentCode = sNoteContentCode;
    return this;
  }

  /**
   * Get the line note subject code (BT-X-10).
   *
   * @return Line note subject code (BT-X-10). May be {@code null}.
   */
  @Nullable
  public String getNoteSubjectCode ()
  {
    return m_sNoteSubjectCode;
  }

  /**
   * Set the line note subject code (BT-X-10).
   *
   * @param sNoteSubjectCode
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setNoteSubjectCode (@Nullable final String sNoteSubjectCode)
  {
    m_sNoteSubjectCode = sNoteSubjectCode;
    return this;
  }

  /**
   * Get the charge free quantity (BT-X-46).
   *
   * @return Charge free quantity (BT-X-46). May be {@code null}.
   */
  @Nullable
  public BigDecimal getChargeFreeQuantity ()
  {
    return m_aChargeFreeQuantity;
  }

  /**
   * Set the charge free quantity (BT-X-46).
   *
   * @param aChargeFreeQuantity
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setChargeFreeQuantity (@Nullable final BigDecimal aChargeFreeQuantity)
  {
    m_aChargeFreeQuantity = aChargeFreeQuantity;
    return this;
  }

  /**
   * Get the unit of the charge free quantity (BT-X-46-0).
   *
   * @return Unit of the charge free quantity (BT-X-46-0). May be {@code null}.
   */
  @Nullable
  public String getChargeFreeQuantityUnit ()
  {
    return m_sChargeFreeQuantityUnit;
  }

  /**
   * Set the unit of the charge free quantity (BT-X-46-0).
   *
   * @param sChargeFreeQuantityUnit
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setChargeFreeQuantityUnit (@Nullable final String sChargeFreeQuantityUnit)
  {
    m_sChargeFreeQuantityUnit = sChargeFreeQuantityUnit;
    return this;
  }

  /**
   * Get the package quantity (BT-X-47).
   *
   * @return Package quantity (BT-X-47). May be {@code null}.
   */
  @Nullable
  public BigDecimal getPackageQuantity ()
  {
    return m_aPackageQuantity;
  }

  /**
   * Set the package quantity (BT-X-47).
   *
   * @param aPackageQuantity
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setPackageQuantity (@Nullable final BigDecimal aPackageQuantity)
  {
    m_aPackageQuantity = aPackageQuantity;
    return this;
  }

  /**
   * Get the unit of the package quantity (BT-X-47-0).
   *
   * @return Unit of the package quantity (BT-X-47-0). May be {@code null}.
   */
  @Nullable
  public String getPackageQuantityUnit ()
  {
    return m_sPackageQuantityUnit;
  }

  /**
   * Set the unit of the package quantity (BT-X-47-0).
   *
   * @param sPackageQuantityUnit
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setPackageQuantityUnit (@Nullable final String sPackageQuantityUnit)
  {
    m_sPackageQuantityUnit = sPackageQuantityUnit;
    return this;
  }

  /**
   * Get the number of units per package (BT-X-561).
   *
   * @return Number of units per package (BT-X-561). May be {@code null}.
   */
  @Nullable
  public BigDecimal getPerPackageUnitQuantity ()
  {
    return m_aPerPackageUnitQuantity;
  }

  /**
   * Set the number of units per package (BT-X-561).
   *
   * @param aPerPackageUnitQuantity
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setPerPackageUnitQuantity (@Nullable final BigDecimal aPerPackageUnitQuantity)
  {
    m_aPerPackageUnitQuantity = aPerPackageUnitQuantity;
    return this;
  }

  /**
   * Get the unit of the number of units per package (BT-X-561-0).
   *
   * @return Unit of the number of units per package (BT-X-561-0). May be {@code null}.
   */
  @Nullable
  public String getPerPackageUnitQuantityUnit ()
  {
    return m_sPerPackageUnitQuantityUnit;
  }

  /**
   * Set the unit of the number of units per package (BT-X-561-0).
   *
   * @param sPerPackageUnitQuantityUnit
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setPerPackageUnitQuantityUnit (@Nullable final String sPerPackageUnitQuantityUnit)
  {
    m_sPerPackageUnitQuantityUnit = sPerPackageUnitQuantityUnit;
    return this;
  }

  /**
   * Get the purchase order date (BT-X-22).
   *
   * @return Purchase order date (BT-X-22). May be {@code null}.
   */
  @Nullable
  public LocalDate getPurchaseOrderDate ()
  {
    return m_aPurchaseOrderDate;
  }

  /**
   * Set the purchase order date (BT-X-22).
   *
   * @param aPurchaseOrderDate
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setPurchaseOrderDate (@Nullable final LocalDate aPurchaseOrderDate)
  {
    m_aPurchaseOrderDate = aPurchaseOrderDate;
    return this;
  }

  /**
   * Get the sales order date (BG-X-81).
   *
   * @return Sales order date (BG-X-81). May be {@code null}.
   */
  @Nullable
  public LocalDate getSalesOrderDate ()
  {
    return m_aSalesOrderDate;
  }

  /**
   * Set the sales order date (BG-X-81).
   *
   * @param aSalesOrderDate
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setSalesOrderDate (@Nullable final LocalDate aSalesOrderDate)
  {
    m_aSalesOrderDate = aSalesOrderDate;
    return this;
  }

  /**
   * Get the despatch advice date (BT-X-88).
   *
   * @return Despatch advice date (BT-X-88). May be {@code null}.
   */
  @Nullable
  public LocalDate getDespatchAdviceDate ()
  {
    return m_aDespatchAdviceDate;
  }

  /**
   * Set the despatch advice date (BT-X-88).
   *
   * @param aDespatchAdviceDate
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setDespatchAdviceDate (@Nullable final LocalDate aDespatchAdviceDate)
  {
    m_aDespatchAdviceDate = aDespatchAdviceDate;
    return this;
  }

  /**
   * Get the receiving advice date (BG-X-82).
   *
   * @return Receiving advice date (BG-X-82). May be {@code null}.
   */
  @Nullable
  public LocalDate getReceivingAdviceDate ()
  {
    return m_aReceivingAdviceDate;
  }

  /**
   * Set the receiving advice date (BG-X-82).
   *
   * @param aReceivingAdviceDate
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setReceivingAdviceDate (@Nullable final LocalDate aReceivingAdviceDate)
  {
    m_aReceivingAdviceDate = aReceivingAdviceDate;
    return this;
  }

  /**
   * Get the delivery note date (BG-X-83).
   *
   * @return Delivery note date (BG-X-83). May be {@code null}.
   */
  @Nullable
  public LocalDate getDeliveryNoteDate ()
  {
    return m_aDeliveryNoteDate;
  }

  /**
   * Set the delivery note date (BG-X-83).
   *
   * @param aDeliveryNoteDate
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setDeliveryNoteDate (@Nullable final LocalDate aDeliveryNoteDate)
  {
    m_aDeliveryNoteDate = aDeliveryNoteDate;
    return this;
  }

  /**
   * Get the quotation reference (BG-X-47).
   *
   * @return Quotation reference (BG-X-47). May be {@code null}.
   */
  @Nullable
  public PhinvoiceReferencedDocument getQuotationReference ()
  {
    return m_aQuotationReference;
  }

  /**
   * Set the quotation reference (BG-X-47).
   *
   * @param aQuotationReference
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setQuotationReference (@Nullable final PhinvoiceReferencedDocument aQuotationReference)
  {
    m_aQuotationReference = aQuotationReference;
    return this;
  }

  /**
   * Get the contract reference (BG-X-2).
   *
   * @return Contract reference (BG-X-2). May be {@code null}.
   */
  @Nullable
  public PhinvoiceReferencedDocument getContractReference ()
  {
    return m_aContractReference;
  }

  /**
   * Set the contract reference (BG-X-2).
   *
   * @param aContractReference
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setContractReference (@Nullable final PhinvoiceReferencedDocument aContractReference)
  {
    m_aContractReference = aContractReference;
    return this;
  }

  /**
   * Get the mutable list of additional referenced documents (BG-X-3).
   *
   * @return Mutable list of additional referenced documents (BG-X-3). Never {@code null}; empty when none are present.
   */
  @NonNull
  @ReturnsMutableObject
  public ICommonsList <PhinvoiceReferencedDocument> additionalReferencedDocuments ()
  {
    return m_aAdditionalReferencedDocuments;
  }

  /**
   * Get the mutable list of ultimate customer order references (BG-X-5).
   *
   * @return Mutable list of ultimate customer order references (BG-X-5). Never {@code null}; empty when none are present.
   */
  @NonNull
  @ReturnsMutableObject
  public ICommonsList <PhinvoiceReferencedDocument> ultimateCustomerOrderReferences ()
  {
    return m_aUltimateCustomerOrderReferences;
  }

  /**
   * Get the seller on line level (BG-X-90).
   *
   * @return Seller on line level (BG-X-90). May be {@code null}.
   */
  @Nullable
  public PhinvoiceTradeParty getLineSeller ()
  {
    return m_aLineSeller;
  }

  /**
   * Set the seller on line level (BG-X-90).
   *
   * @param aLineSeller
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setLineSeller (@Nullable final PhinvoiceTradeParty aLineSeller)
  {
    m_aLineSeller = aLineSeller;
    return this;
  }

  /**
   * Get the ultimate ship to party (BG-X-10).
   *
   * @return Ultimate ship to party (BG-X-10). May be {@code null}.
   */
  @Nullable
  public PhinvoiceTradeParty getUltimateShipTo ()
  {
    return m_aUltimateShipTo;
  }

  /**
   * Set the ultimate ship to party (BG-X-10).
   *
   * @param aUltimateShipTo
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setUltimateShipTo (@Nullable final PhinvoiceTradeParty aUltimateShipTo)
  {
    m_aUltimateShipTo = aUltimateShipTo;
    return this;
  }

  /**
   * Get the included tax for B2C (BG-X-4).
   *
   * @return Included tax for B2C (BG-X-4). May be {@code null}.
   */
  @Nullable
  public PhinvoiceIncludedTax getIncludedTax ()
  {
    return m_aIncludedTax;
  }

  /**
   * Set the included tax for B2C (BG-X-4).
   *
   * @param aIncludedTax
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setIncludedTax (@Nullable final PhinvoiceIncludedTax aIncludedTax)
  {
    m_aIncludedTax = aIncludedTax;
    return this;
  }

  /**
   * Get the delivery terms (BG-X-87).
   *
   * @return Delivery terms (BG-X-87). May be {@code null}.
   */
  @Nullable
  public PhinvoiceDeliveryTerms getDeliveryTerms ()
  {
    return m_aDeliveryTerms;
  }

  /**
   * Set the delivery terms (BG-X-87).
   *
   * @param aDeliveryTerms
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setDeliveryTerms (@Nullable final PhinvoiceDeliveryTerms aDeliveryTerms)
  {
    m_aDeliveryTerms = aDeliveryTerms;
    return this;
  }

  /**
   * Get the line total charges amount (BT-X-327).
   *
   * @return Line total charges amount (BT-X-327). May be {@code null}.
   */
  @Nullable
  public BigDecimal getChargeTotal ()
  {
    return m_aChargeTotal;
  }

  /**
   * Set the line total charges amount (BT-X-327).
   *
   * @param aChargeTotal
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setChargeTotal (@Nullable final BigDecimal aChargeTotal)
  {
    m_aChargeTotal = aChargeTotal;
    return this;
  }

  /**
   * Get the line total allowances amount (BT-X-328).
   *
   * @return Line total allowances amount (BT-X-328). May be {@code null}.
   */
  @Nullable
  public BigDecimal getAllowanceTotal ()
  {
    return m_aAllowanceTotal;
  }

  /**
   * Set the line total allowances amount (BT-X-328).
   *
   * @param aAllowanceTotal
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setAllowanceTotal (@Nullable final BigDecimal aAllowanceTotal)
  {
    m_aAllowanceTotal = aAllowanceTotal;
    return this;
  }

  /**
   * Get the line total tax amount (BT-X-329).
   *
   * @return Line total tax amount (BT-X-329). May be {@code null}.
   */
  @Nullable
  public BigDecimal getTaxTotal ()
  {
    return m_aTaxTotal;
  }

  /**
   * Set the line total tax amount (BT-X-329).
   *
   * @param aTaxTotal
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setTaxTotal (@Nullable final BigDecimal aTaxTotal)
  {
    m_aTaxTotal = aTaxTotal;
    return this;
  }

  /**
   * Get the line total tax amount in accounting currency (BT-X-590).
   *
   * @return Line total tax amount in accounting currency (BT-X-590). May be {@code null}.
   */
  @Nullable
  public BigDecimal getTaxTotalAccountingCurrency ()
  {
    return m_aTaxTotalAccountingCurrency;
  }

  /**
   * Set the line total tax amount in accounting currency (BT-X-590).
   *
   * @param aTaxTotalAccountingCurrency
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setTaxTotalAccountingCurrency (@Nullable final BigDecimal aTaxTotalAccountingCurrency)
  {
    m_aTaxTotalAccountingCurrency = aTaxTotalAccountingCurrency;
    return this;
  }

  /**
   * Get the line grand total amount (BT-X-330).
   *
   * @return Line grand total amount (BT-X-330). May be {@code null}.
   */
  @Nullable
  public BigDecimal getGrandTotal ()
  {
    return m_aGrandTotal;
  }

  /**
   * Set the line grand total amount (BT-X-330).
   *
   * @param aGrandTotal
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setGrandTotal (@Nullable final BigDecimal aGrandTotal)
  {
    m_aGrandTotal = aGrandTotal;
    return this;
  }

  /**
   * Get the total allowance and charge amount (BT-X-98).
   *
   * @return Total allowance and charge amount (BT-X-98). May be {@code null}.
   */
  @Nullable
  public BigDecimal getTotalAllowanceCharge ()
  {
    return m_aTotalAllowanceCharge;
  }

  /**
   * Set the total allowance and charge amount (BT-X-98).
   *
   * @param aTotalAllowanceCharge
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setTotalAllowanceCharge (@Nullable final BigDecimal aTotalAllowanceCharge)
  {
    m_aTotalAllowanceCharge = aTotalAllowanceCharge;
    return this;
  }

  /**
   * Get the accounting reference type code (BT-X-99).
   *
   * @return Accounting reference type code (BT-X-99). May be {@code null}.
   */
  @Nullable
  public String getAccountingCostTypeCode ()
  {
    return m_sAccountingCostTypeCode;
  }

  /**
   * Set the accounting reference type code (BT-X-99).
   *
   * @param sAccountingCostTypeCode
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoiceLine setAccountingCostTypeCode (@Nullable final String sAccountingCostTypeCode)
  {
    m_sAccountingCostTypeCode = sAccountingCostTypeCode;
    return this;
  }
}
