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
import java.time.OffsetTime;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.en16931.basics.EEN16931Edition;

/**
 * Root of the syntax neutral invoice model. One instance represents one Invoice or one Credit Note,
 * created by a reader or built by the caller and consumed by a writer. All optional fields are
 * {@code null} when not present in the source; all collections are non-{@code null} and empty when
 * not populated.
 * <p>
 * Header-level BTs sit directly on this class (BT-1 through BT-19, BT-166, BT-167, BT-197). Each
 * business group (BG-1 through BG-25 and BG-33) is exposed as either a single nested object or a mutable list of nested objects.
 * <p>
 * A field holds the first occurrence of a term. Every other value of the source, for which the model
 * has no place, is in {@link #additionalTerms()}, so that no value of the invoice gets lost.
 *
 * @author Philip Helger
 */
public class PhinvoiceInvoice
{
  private EPhinvoiceDocumentKind m_eKind = EPhinvoiceDocumentKind.INVOICE;

  private String m_sNumber;
  private LocalDate m_aIssueDate;
  private String m_sTypeCode;
  private String m_sCurrency;
  private String m_sVatAccountingCurrency;
  private LocalDate m_aVatPointDate;
  private String m_sVatPointDateCode;
  private LocalDate m_aPaymentDueDate;
  private String m_sProjectReference;
  private String m_sProjectName;
  private String m_sContractReference;
  private String m_sPurchaseOrderReference;
  private String m_sSalesOrderReference;
  private String m_sReceivingAdviceReference;
  private String m_sDespatchAdviceReference;
  private String m_sTenderOrLotReference;
  private PhinvoiceIdentifier m_aInvoicedObjectIdentifier;
  private String m_sBuyerAccountingReference;

  private final ICommonsList <PhinvoiceInvoiceNote> m_aInvoiceNotes = new CommonsArrayList <> ();
  private PhinvoiceProcessControl m_aProcessControl = new PhinvoiceProcessControl ();
  private final ICommonsList <PhinvoicePrecedingInvoiceRef> m_aPrecedingInvoiceRefs = new CommonsArrayList <> ();
  private PhinvoiceSeller m_aSeller = new PhinvoiceSeller ();
  private PhinvoiceBuyer m_aBuyer = new PhinvoiceBuyer ();
  private PhinvoicePayee m_aPayee;
  private PhinvoiceSellerTaxRepresentative m_aSellerTaxRepresentative;
  private PhinvoiceDeliveryInformation m_aDeliveryInformation;
  private PhinvoiceInvoicingPeriod m_aInvoicingPeriod;
  private PhinvoicePaymentInstructions m_aPaymentInstructions;
  private final ICommonsList <PhinvoiceDocumentAllowance> m_aDocumentAllowances = new CommonsArrayList <> ();
  private final ICommonsList <PhinvoiceDocumentCharge> m_aDocumentCharges = new CommonsArrayList <> ();
  private PhinvoiceDocumentTotals m_aDocumentTotals = new PhinvoiceDocumentTotals ();
  private final ICommonsList <PhinvoiceVatBreakdown> m_aVatBreakdowns = new CommonsArrayList <> ();
  private final ICommonsList <PhinvoiceSupportingDocument> m_aSupportingDocuments = new CommonsArrayList <> ();
  private final ICommonsList <PhinvoiceInvoiceLine> m_aLines = new CommonsArrayList <> ();
  private EEN16931Edition m_eEdition;
  private OffsetTime m_aIssueTime;
  private BigDecimal m_aVatExchangeRate;
  private String m_sVatExchangeRateTargetCurrency;
  private String m_sVatExchangeRateSourceCurrency;
  private String m_sDeliveryNoteReference;
  private Boolean m_aTestIndicator;
  private String m_sDocumentName;
  private Boolean m_aCopyIndicator;
  private String m_sLanguageCode;
  private LocalDate m_aContractualDueDate;
  private LocalDate m_aSalesOrderDate;
  private LocalDate m_aPurchaseOrderDate;
  private String m_sContractReferenceTypeCode;
  private LocalDate m_aContractDate;
  private LocalDate m_aTenderOrLotReferenceDate;
  private LocalDate m_aInvoicedObjectDate;
  private LocalDate m_aDespatchAdviceDate;
  private LocalDate m_aReceivingAdviceDate;
  private LocalDate m_aDeliveryNoteDate;
  private String m_sSellerReference;
  private String m_sBuyerAccountingReferenceTypeCode;
  private PhinvoiceReferencedDocument m_aQuotationReference;
  private final ICommonsList <PhinvoiceReferencedDocument> m_aUltimateCustomerOrderReferences = new CommonsArrayList <> ();
  private PhinvoiceTradeParty m_aSalesAgent;
  private PhinvoiceTradeParty m_aBuyerTaxRepresentative;
  private PhinvoiceTradeParty m_aProductEndUser;
  private PhinvoiceTradeParty m_aBuyerAgent;
  private PhinvoiceTradeParty m_aUltimateShipTo;
  private PhinvoiceTradeParty m_aShipFrom;
  private PhinvoiceTradeParty m_aInvoicer;
  private PhinvoiceTradeParty m_aInvoicee;
  private PhinvoiceTradeParty m_aPayer;
  private PhinvoiceDeliveryTerms m_aDeliveryTerms;
  private String m_sTransportModeCode;
  private PhinvoiceTaxCurrencyExchange m_aTaxCurrencyExchange;
  private final ICommonsList <PhinvoiceLogisticsServiceCharge> m_aLogisticsServiceCharges = new CommonsArrayList <> ();
  private final ICommonsList <PhinvoiceAdvancePayment> m_aAdvancePayments = new CommonsArrayList <> ();
  private final ICommonsList <PhinvoiceIdentifier> m_aBuyerReferences = new CommonsArrayList <> ();
  private final ICommonsList <PhinvoicePaymentTerms> m_aPaymentTerms = new CommonsArrayList <> ();
  private final ICommonsList <PhinvoiceAdditionalTerm> m_aAdditionalTerms = new CommonsArrayList <> ();

  /** Create an empty invoice (defaults to {@link EPhinvoiceDocumentKind#INVOICE}). */
  public PhinvoiceInvoice ()
  {}

  /**
   * Get the document kind (Invoice or Credit Note), derived from BT-3. Never {@code null}.
   *
   * @return The document kind (Invoice or Credit Note), derived from BT-3. Never {@code null}.
   */
  @NonNull
  public EPhinvoiceDocumentKind getKind ()
  {
    return m_eKind;
  }

  /**
   * Set the document kind.
   *
   * @param eKind
   *        Kind; never {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setKind (@NonNull final EPhinvoiceDocumentKind eKind)
  {
    m_eKind = eKind;
    return this;
  }

  /**
   * Get the {@code true} when {@link #getKind()} is {@link EPhinvoiceDocumentKind#INVOICE}.
   *
   * @return {@code true} when {@link #getKind()} is {@link EPhinvoiceDocumentKind#INVOICE}.
   */
  public boolean isInvoice ()
  {
    return m_eKind.isInvoice ();
  }

  /**
   * Get the {@code true} when {@link #getKind()} is {@link EPhinvoiceDocumentKind#CREDIT_NOTE}.
   *
   * @return {@code true} when {@link #getKind()} is {@link EPhinvoiceDocumentKind#CREDIT_NOTE}.
   */
  public boolean isCreditNote ()
  {
    return m_eKind.isCreditNote ();
  }

  /**
   * Get the invoice number (BT-1).
   *
   * @return Invoice number (BT-1). May be {@code null}.
   */
  @Nullable
  public String getNumber ()
  {
    return m_sNumber;
  }

  /**
   * Set the invoice number (BT-1).
   *
   * @param sNumber
   *        Invoice number; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setNumber (@Nullable final String sNumber)
  {
    m_sNumber = sNumber;
    return this;
  }

  /**
   * Get the invoice issue date (BT-2).
   *
   * @return Invoice issue date (BT-2). May be {@code null}.
   */
  @Nullable
  public LocalDate getIssueDate ()
  {
    return m_aIssueDate;
  }

  /**
   * Set the invoice issue date (BT-2).
   *
   * @param aIssueDate
   *        Issue date; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setIssueDate (@Nullable final LocalDate aIssueDate)
  {
    m_aIssueDate = aIssueDate;
    return this;
  }

  /**
   * Get the invoice type code (BT-3) — UNTDID 1001. May be {@code null}.
   *
   * @return Invoice type code (BT-3) — UNTDID 1001. May be {@code null}.
   */
  @Nullable
  public String getTypeCode ()
  {
    return m_sTypeCode;
  }

  /**
   * Set the invoice type code (BT-3).
   *
   * @param sTypeCode
   *        UNTDID 1001 code; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setTypeCode (@Nullable final String sTypeCode)
  {
    m_sTypeCode = sTypeCode;
    return this;
  }

  /**
   * Get the invoice currency code (BT-5) — ISO 4217. May be {@code null}.
   *
   * @return Invoice currency code (BT-5) — ISO 4217. May be {@code null}.
   */
  @Nullable
  public String getCurrency ()
  {
    return m_sCurrency;
  }

  /**
   * Set the invoice currency code (BT-5).
   *
   * @param sCurrency
   *        ISO 4217 currency code; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setCurrency (@Nullable final String sCurrency)
  {
    m_sCurrency = sCurrency;
    return this;
  }

  /**
   * Get the VAT accounting currency code (BT-6) — ISO 4217. May be {@code null}.
   *
   * @return VAT accounting currency code (BT-6) — ISO 4217. May be {@code null}.
   */
  @Nullable
  public String getVatAccountingCurrency ()
  {
    return m_sVatAccountingCurrency;
  }

  /**
   * Set the VAT accounting currency code (BT-6).
   *
   * @param sVatAccountingCurrency
   *        ISO 4217 currency code; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setVatAccountingCurrency (@Nullable final String sVatAccountingCurrency)
  {
    m_sVatAccountingCurrency = sVatAccountingCurrency;
    return this;
  }

  /**
   * Get the value added tax point date (BT-7).
   *
   * @return Value added tax point date (BT-7). May be {@code null}.
   */
  @Nullable
  public LocalDate getVatPointDate ()
  {
    return m_aVatPointDate;
  }

  /**
   * Set the value added tax point date (BT-7).
   *
   * @param aVatPointDate
   *        VAT point date; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setVatPointDate (@Nullable final LocalDate aVatPointDate)
  {
    m_aVatPointDate = aVatPointDate;
    return this;
  }

  /**
   * Get the value added tax point date code (BT-8). The readers deliver the code of UNTDID 2005 that
   * the UBL binding uses; the CII readers map the UNTDID 2475 code of their binding to it.
   *
   * @return Value added tax point date code (BT-8). May be {@code null}.
   */
  @Nullable
  public String getVatPointDateCode ()
  {
    return m_sVatPointDateCode;
  }

  /**
   * Set the value added tax point date code (BT-8).
   *
   * @param sVatPointDateCode
   *        Code; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setVatPointDateCode (@Nullable final String sVatPointDateCode)
  {
    m_sVatPointDateCode = sVatPointDateCode;
    return this;
  }

  /**
   * Get the payment due date (BT-9).
   *
   * @return Payment due date (BT-9). May be {@code null}.
   */
  @Nullable
  public LocalDate getPaymentDueDate ()
  {
    return m_aPaymentDueDate;
  }

  /**
   * Set the payment due date (BT-9).
   *
   * @param aPaymentDueDate
   *        Due date; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setPaymentDueDate (@Nullable final LocalDate aPaymentDueDate)
  {
    m_aPaymentDueDate = aPaymentDueDate;
    return this;
  }

  /**
   * Get the project reference (BT-11).
   *
   * @return Project reference (BT-11). May be {@code null}.
   */
  @Nullable
  public String getProjectReference ()
  {
    return m_sProjectReference;
  }

  /**
   * Set the project reference (BT-11).
   *
   * @param sProjectReference
   *        Project reference; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setProjectReference (@Nullable final String sProjectReference)
  {
    m_sProjectReference = sProjectReference;
    return this;
  }

  /**
   * Get the project name (BT-11-1). Only CII has it. It is {@code null} if it just repeats the project
   * reference (BT-11), as the binding asks for.
   *
   * @return Project name (BT-11-1). May be {@code null}.
   */
  @Nullable
  public String getProjectName ()
  {
    return m_sProjectName;
  }

  /**
   * Set the project name (BT-11-1).
   *
   * @param sProjectName
   *        The name; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setProjectName (@Nullable final String sProjectName)
  {
    m_sProjectName = sProjectName;
    return this;
  }

  /**
   * Get the contract reference (BT-12).
   *
   * @return Contract reference (BT-12). May be {@code null}.
   */
  @Nullable
  public String getContractReference ()
  {
    return m_sContractReference;
  }

  /**
   * Set the contract reference (BT-12).
   *
   * @param sContractReference
   *        Contract reference; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setContractReference (@Nullable final String sContractReference)
  {
    m_sContractReference = sContractReference;
    return this;
  }

  /**
   * Get the purchase order reference (BT-13).
   *
   * @return Purchase order reference (BT-13). May be {@code null}.
   */
  @Nullable
  public String getPurchaseOrderReference ()
  {
    return m_sPurchaseOrderReference;
  }

  /**
   * Set the purchase order reference (BT-13).
   *
   * @param sPurchaseOrderReference
   *        PO reference; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setPurchaseOrderReference (@Nullable final String sPurchaseOrderReference)
  {
    m_sPurchaseOrderReference = sPurchaseOrderReference;
    return this;
  }

  /**
   * Get the sales order reference (BT-14).
   *
   * @return Sales order reference (BT-14). May be {@code null}.
   */
  @Nullable
  public String getSalesOrderReference ()
  {
    return m_sSalesOrderReference;
  }

  /**
   * Set the sales order reference (BT-14).
   *
   * @param sSalesOrderReference
   *        Sales order reference; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setSalesOrderReference (@Nullable final String sSalesOrderReference)
  {
    m_sSalesOrderReference = sSalesOrderReference;
    return this;
  }

  /**
   * Get the receiving advice reference (BT-15).
   *
   * @return Receiving advice reference (BT-15). May be {@code null}.
   */
  @Nullable
  public String getReceivingAdviceReference ()
  {
    return m_sReceivingAdviceReference;
  }

  /**
   * Set the receiving advice reference (BT-15).
   *
   * @param sReceivingAdviceReference
   *        Receiving advice reference; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setReceivingAdviceReference (@Nullable final String sReceivingAdviceReference)
  {
    m_sReceivingAdviceReference = sReceivingAdviceReference;
    return this;
  }

  /**
   * Get the despatch advice reference (BT-16).
   *
   * @return Despatch advice reference (BT-16). May be {@code null}.
   */
  @Nullable
  public String getDespatchAdviceReference ()
  {
    return m_sDespatchAdviceReference;
  }

  /**
   * Set the despatch advice reference (BT-16).
   *
   * @param sDespatchAdviceReference
   *        Despatch advice reference; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setDespatchAdviceReference (@Nullable final String sDespatchAdviceReference)
  {
    m_sDespatchAdviceReference = sDespatchAdviceReference;
    return this;
  }

  /**
   * Get the tender or lot reference (BT-17).
   *
   * @return Tender or lot reference (BT-17). May be {@code null}.
   */
  @Nullable
  public String getTenderOrLotReference ()
  {
    return m_sTenderOrLotReference;
  }

  /**
   * Set the tender or lot reference (BT-17).
   *
   * @param sTenderOrLotReference
   *        Tender / lot reference; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setTenderOrLotReference (@Nullable final String sTenderOrLotReference)
  {
    m_sTenderOrLotReference = sTenderOrLotReference;
    return this;
  }

  /**
   * Get the invoiced object identifier (BT-18 with BT-18-1 scheme ID).
   *
   * @return Invoiced object identifier (BT-18 with BT-18-1 scheme ID). May be {@code null}.
   */
  @Nullable
  public PhinvoiceIdentifier getInvoicedObjectIdentifier ()
  {
    return m_aInvoicedObjectIdentifier;
  }

  /**
   * Set the invoiced object identifier (BT-18 / BT-18-1).
   *
   * @param aInvoicedObjectIdentifier
   *        Identifier; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setInvoicedObjectIdentifier (@Nullable final PhinvoiceIdentifier aInvoicedObjectIdentifier)
  {
    m_aInvoicedObjectIdentifier = aInvoicedObjectIdentifier;
    return this;
  }

  /**
   * Get the buyer accounting reference (BT-19).
   *
   * @return Buyer accounting reference (BT-19). May be {@code null}.
   */
  @Nullable
  public String getBuyerAccountingReference ()
  {
    return m_sBuyerAccountingReference;
  }

  /**
   * Set the buyer accounting reference (BT-19).
   *
   * @param sBuyerAccountingReference
   *        Accounting cost reference; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setBuyerAccountingReference (@Nullable final String sBuyerAccountingReference)
  {
    m_sBuyerAccountingReference = sBuyerAccountingReference;
    return this;
  }

  /**
   * Get the mutable list of the values of the document that the model has no place for.
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
   * Get the mutable list of invoice notes (BG-1).
   *
   * @return Mutable list of invoice notes (BG-1). Never {@code null}; empty when none are present.
   */
  @NonNull
  @ReturnsMutableObject
  public ICommonsList <PhinvoiceInvoiceNote> invoiceNotes ()
  {
    return m_aInvoiceNotes;
  }

  /**
   * Get the process control (BG-2).
   *
   * @return Process control (BG-2). Never {@code null} (initialised to an empty record).
   */
  @NonNull
  public PhinvoiceProcessControl getProcessControl ()
  {
    return m_aProcessControl;
  }

  /**
   * Set the process control record (BG-2).
   *
   * @param aProcessControl
   *        Process control; never {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setProcessControl (@NonNull final PhinvoiceProcessControl aProcessControl)
  {
    m_aProcessControl = aProcessControl;
    return this;
  }

  /**
   * Get the mutable list of preceding invoice references (BG-3).
   *
   * @return Mutable list of preceding invoice references (BG-3). Never {@code null}; empty when
   *         none are present.
   */
  @NonNull
  @ReturnsMutableObject
  public ICommonsList <PhinvoicePrecedingInvoiceRef> precedingInvoiceRefs ()
  {
    return m_aPrecedingInvoiceRefs;
  }

  /**
   * Get the seller (BG-4 with nested BG-5 and BG-6).
   *
   * @return Seller (BG-4 with nested BG-5 and BG-6). Never {@code null} (initialised).
   */
  @NonNull
  public PhinvoiceSeller getSeller ()
  {
    return m_aSeller;
  }

  /**
   * Set the seller (BG-4).
   *
   * @param aSeller
   *        Seller; never {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setSeller (@NonNull final PhinvoiceSeller aSeller)
  {
    m_aSeller = aSeller;
    return this;
  }

  /**
   * Get the buyer (BG-7 with nested BG-8 and BG-9).
   *
   * @return Buyer (BG-7 with nested BG-8 and BG-9). Never {@code null} (initialised).
   */
  @NonNull
  public PhinvoiceBuyer getBuyer ()
  {
    return m_aBuyer;
  }

  /**
   * Set the buyer (BG-7).
   *
   * @param aBuyer
   *        Buyer; never {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setBuyer (@NonNull final PhinvoiceBuyer aBuyer)
  {
    m_aBuyer = aBuyer;
    return this;
  }

  /**
   * Get the payee (BG-10).
   *
   * @return Payee (BG-10). May be {@code null}.
   */
  @Nullable
  public PhinvoicePayee getPayee ()
  {
    return m_aPayee;
  }

  /**
   * Set the payee (BG-10).
   *
   * @param aPayee
   *        Payee; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setPayee (@Nullable final PhinvoicePayee aPayee)
  {
    m_aPayee = aPayee;
    return this;
  }

  /**
   * Get the seller tax representative (BG-11 with nested BG-12).
   *
   * @return Seller tax representative (BG-11 with nested BG-12). May be {@code null}.
   */
  @Nullable
  public PhinvoiceSellerTaxRepresentative getSellerTaxRepresentative ()
  {
    return m_aSellerTaxRepresentative;
  }

  /**
   * Set the seller tax representative (BG-11).
   *
   * @param aSellerTaxRepresentative
   *        Tax representative; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setSellerTaxRepresentative (@Nullable final PhinvoiceSellerTaxRepresentative aSellerTaxRepresentative)
  {
    m_aSellerTaxRepresentative = aSellerTaxRepresentative;
    return this;
  }

  /**
   * Get the delivery information (BG-13 with nested BG-15).
   *
   * @return Delivery information (BG-13 with nested BG-15). May be {@code null}.
   */
  @Nullable
  public PhinvoiceDeliveryInformation getDeliveryInformation ()
  {
    return m_aDeliveryInformation;
  }

  /**
   * Set the delivery information (BG-13).
   *
   * @param aDeliveryInformation
   *        Delivery information; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setDeliveryInformation (@Nullable final PhinvoiceDeliveryInformation aDeliveryInformation)
  {
    m_aDeliveryInformation = aDeliveryInformation;
    return this;
  }

  /**
   * Get the invoicing period (BG-14).
   *
   * @return Invoicing period (BG-14). May be {@code null}.
   */
  @Nullable
  public PhinvoiceInvoicingPeriod getInvoicingPeriod ()
  {
    return m_aInvoicingPeriod;
  }

  /**
   * Set the invoicing period (BG-14).
   *
   * @param aInvoicingPeriod
   *        Invoicing period; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setInvoicingPeriod (@Nullable final PhinvoiceInvoicingPeriod aInvoicingPeriod)
  {
    m_aInvoicingPeriod = aInvoicingPeriod;
    return this;
  }

  /**
   * Get the payment instructions (BG-16 with nested BG-17/BG-18/BG-19).
   *
   * @return Payment instructions (BG-16 with nested BG-17/BG-18/BG-19). May be {@code null}.
   */
  @Nullable
  public PhinvoicePaymentInstructions getPaymentInstructions ()
  {
    return m_aPaymentInstructions;
  }

  /**
   * Set the payment instructions (BG-16).
   *
   * @param aPaymentInstructions
   *        Payment instructions; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setPaymentInstructions (@Nullable final PhinvoicePaymentInstructions aPaymentInstructions)
  {
    m_aPaymentInstructions = aPaymentInstructions;
    return this;
  }

  /**
   * Get the mutable list of document-level allowances (BG-20).
   *
   * @return Mutable list of document-level allowances (BG-20). Never {@code null}; empty when none
   *         are present.
   */
  @NonNull
  @ReturnsMutableObject
  public ICommonsList <PhinvoiceDocumentAllowance> documentAllowances ()
  {
    return m_aDocumentAllowances;
  }

  /**
   * Get the mutable list of document-level charges (BG-21).
   *
   * @return Mutable list of document-level charges (BG-21). Never {@code null}; empty when none are
   *         present.
   */
  @NonNull
  @ReturnsMutableObject
  public ICommonsList <PhinvoiceDocumentCharge> documentCharges ()
  {
    return m_aDocumentCharges;
  }

  /**
   * Get the document totals (BG-22).
   *
   * @return Document totals (BG-22). Never {@code null} (initialised to an empty record).
   */
  @NonNull
  public PhinvoiceDocumentTotals getDocumentTotals ()
  {
    return m_aDocumentTotals;
  }

  /**
   * Set the document totals (BG-22).
   *
   * @param aDocumentTotals
   *        Totals; never {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setDocumentTotals (@NonNull final PhinvoiceDocumentTotals aDocumentTotals)
  {
    m_aDocumentTotals = aDocumentTotals;
    return this;
  }

  /**
   * Get the mutable list of VAT breakdowns (BG-23).
   *
   * @return Mutable list of VAT breakdowns (BG-23). Never {@code null}; empty when none are
   *         present.
   */
  @NonNull
  @ReturnsMutableObject
  public ICommonsList <PhinvoiceVatBreakdown> vatBreakdowns ()
  {
    return m_aVatBreakdowns;
  }

  /**
   * Get the mutable list of supporting documents (BG-24).
   *
   * @return Mutable list of supporting documents (BG-24). Never {@code null}; empty when none are
   *         present.
   */
  @NonNull
  @ReturnsMutableObject
  public ICommonsList <PhinvoiceSupportingDocument> supportingDocuments ()
  {
    return m_aSupportingDocuments;
  }

  /**
   * Get the mutable list of invoice lines (BG-25).
   *
   * @return Mutable list of invoice lines (BG-25). Never {@code null}; the EN 16931 model requires
   *         at least one entry for a valid invoice.
   */
  @NonNull
  @ReturnsMutableObject
  public ICommonsList <PhinvoiceInvoiceLine> lines ()
  {
    return m_aLines;
  }

  /**
   * Get the EN 16931 edition the document was read as.
   *
   * @return EN 16931 edition the document was read as. May be {@code null}.
   */
  @Nullable
  public EEN16931Edition getEdition ()
  {
    return m_eEdition;
  }

  /**
   * Set the EN 16931 edition the document was read as.
   *
   * @param eEdition
   *        EN 16931 edition the document was read as; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setEdition (@Nullable final EEN16931Edition eEdition)
  {
    m_eEdition = eEdition;
    return this;
  }

  /**
   * Get the invoice issue time (BT-166).
   *
   * @return Invoice issue time (BT-166). May be {@code null}.
   */
  @Nullable
  public OffsetTime getIssueTime ()
  {
    return m_aIssueTime;
  }

  /**
   * Set the invoice issue time (BT-166).
   *
   * @param aIssueTime
   *        Invoice issue time; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setIssueTime (@Nullable final OffsetTime aIssueTime)
  {
    m_aIssueTime = aIssueTime;
    return this;
  }

  /**
   * Get the VAT accounting currency exchange rate (BT-167).
   *
   * @return VAT accounting currency exchange rate (BT-167). May be {@code null}.
   */
  @Nullable
  public BigDecimal getVatExchangeRate ()
  {
    return m_aVatExchangeRate;
  }

  /**
   * Set the VAT accounting currency exchange rate (BT-167).
   *
   * @param aVatExchangeRate
   *        VAT accounting currency exchange rate; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setVatExchangeRate (@Nullable final BigDecimal aVatExchangeRate)
  {
    m_aVatExchangeRate = aVatExchangeRate;
    return this;
  }

  /**
   * Get the VAT accounting currency exchange rate target currency code (BT-167-1).
   *
   * @return VAT accounting currency exchange rate target currency code (BT-167-1). May be {@code null}.
   */
  @Nullable
  public String getVatExchangeRateTargetCurrency ()
  {
    return m_sVatExchangeRateTargetCurrency;
  }

  /**
   * Set the VAT accounting currency exchange rate target currency code (BT-167-1).
   *
   * @param sVatExchangeRateTargetCurrency
   *        VAT accounting currency exchange rate target currency code; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setVatExchangeRateTargetCurrency (@Nullable final String sVatExchangeRateTargetCurrency)
  {
    m_sVatExchangeRateTargetCurrency = sVatExchangeRateTargetCurrency;
    return this;
  }

  /**
   * Get the VAT accounting currency exchange rate source currency code (BT-167-2).
   *
   * @return VAT accounting currency exchange rate source currency code (BT-167-2). May be {@code null}.
   */
  @Nullable
  public String getVatExchangeRateSourceCurrency ()
  {
    return m_sVatExchangeRateSourceCurrency;
  }

  /**
   * Set the VAT accounting currency exchange rate source currency code (BT-167-2).
   *
   * @param sVatExchangeRateSourceCurrency
   *        VAT accounting currency exchange rate source currency code; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setVatExchangeRateSourceCurrency (@Nullable final String sVatExchangeRateSourceCurrency)
  {
    m_sVatExchangeRateSourceCurrency = sVatExchangeRateSourceCurrency;
    return this;
  }

  /**
   * Get the delivery note reference (BT-197).
   *
   * @return Delivery note reference (BT-197). May be {@code null}.
   */
  @Nullable
  public String getDeliveryNoteReference ()
  {
    return m_sDeliveryNoteReference;
  }

  /**
   * Set the delivery note reference (BT-197).
   *
   * @param sDeliveryNoteReference
   *        Delivery note reference; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setDeliveryNoteReference (@Nullable final String sDeliveryNoteReference)
  {
    m_sDeliveryNoteReference = sDeliveryNoteReference;
    return this;
  }

  /**
   * Get the mutable list of buyer references (BT-10 / BT-10-1).
   *
   * @return Mutable list of buyer references (BT-10 / BT-10-1). Never {@code null}; empty when none are present.
   */
  @NonNull
  @ReturnsMutableObject
  public ICommonsList <PhinvoiceIdentifier> buyerReferences ()
  {
    return m_aBuyerReferences;
  }

  /**
   * Get the mutable list of payment terms (BG-33).
   *
   * @return Mutable list of payment terms (BG-33). Never {@code null}; empty when none are present.
   */
  @NonNull
  @ReturnsMutableObject
  public ICommonsList <PhinvoicePaymentTerms> paymentTerms ()
  {
    return m_aPaymentTerms;
  }

  /**
   * Get the test indicator (BT-X-1).
   *
   * @return Test indicator (BT-X-1). May be {@code null}.
   */
  @Nullable
  public Boolean getTestIndicator ()
  {
    return m_aTestIndicator;
  }

  /**
   * Set the test indicator (BT-X-1).
   *
   * @param aTestIndicator
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setTestIndicator (@Nullable final Boolean aTestIndicator)
  {
    m_aTestIndicator = aTestIndicator;
    return this;
  }

  /**
   * Get the document name (BT-X-2).
   *
   * @return Document name (BT-X-2). May be {@code null}.
   */
  @Nullable
  public String getDocumentName ()
  {
    return m_sDocumentName;
  }

  /**
   * Set the document name (BT-X-2).
   *
   * @param sDocumentName
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setDocumentName (@Nullable final String sDocumentName)
  {
    m_sDocumentName = sDocumentName;
    return this;
  }

  /**
   * Get the copy indicator (BT-X-3).
   *
   * @return Copy indicator (BT-X-3). May be {@code null}.
   */
  @Nullable
  public Boolean getCopyIndicator ()
  {
    return m_aCopyIndicator;
  }

  /**
   * Set the copy indicator (BT-X-3).
   *
   * @param aCopyIndicator
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setCopyIndicator (@Nullable final Boolean aCopyIndicator)
  {
    m_aCopyIndicator = aCopyIndicator;
    return this;
  }

  /**
   * Get the invoice language code (BT-X-4).
   *
   * @return Invoice language code (BT-X-4). May be {@code null}.
   */
  @Nullable
  public String getLanguageCode ()
  {
    return m_sLanguageCode;
  }

  /**
   * Set the invoice language code (BT-X-4).
   *
   * @param sLanguageCode
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setLanguageCode (@Nullable final String sLanguageCode)
  {
    m_sLanguageCode = sLanguageCode;
    return this;
  }

  /**
   * Get the contractual due date (BT-X-6).
   *
   * @return Contractual due date (BT-X-6). May be {@code null}.
   */
  @Nullable
  public LocalDate getContractualDueDate ()
  {
    return m_aContractualDueDate;
  }

  /**
   * Set the contractual due date (BT-X-6).
   *
   * @param aContractualDueDate
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setContractualDueDate (@Nullable final LocalDate aContractualDueDate)
  {
    m_aContractualDueDate = aContractualDueDate;
    return this;
  }

  /**
   * Get the sales order date (BT-X-146).
   *
   * @return Sales order date (BT-X-146). May be {@code null}.
   */
  @Nullable
  public LocalDate getSalesOrderDate ()
  {
    return m_aSalesOrderDate;
  }

  /**
   * Set the sales order date (BT-X-146).
   *
   * @param aSalesOrderDate
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setSalesOrderDate (@Nullable final LocalDate aSalesOrderDate)
  {
    m_aSalesOrderDate = aSalesOrderDate;
    return this;
  }

  /**
   * Get the purchase order date (BT-X-147).
   *
   * @return Purchase order date (BT-X-147). May be {@code null}.
   */
  @Nullable
  public LocalDate getPurchaseOrderDate ()
  {
    return m_aPurchaseOrderDate;
  }

  /**
   * Set the purchase order date (BT-X-147).
   *
   * @param aPurchaseOrderDate
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setPurchaseOrderDate (@Nullable final LocalDate aPurchaseOrderDate)
  {
    m_aPurchaseOrderDate = aPurchaseOrderDate;
    return this;
  }

  /**
   * Get the contract reference type code (BT-X-405).
   *
   * @return Contract reference type code (BT-X-405). May be {@code null}.
   */
  @Nullable
  public String getContractReferenceTypeCode ()
  {
    return m_sContractReferenceTypeCode;
  }

  /**
   * Set the contract reference type code (BT-X-405).
   *
   * @param sContractReferenceTypeCode
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setContractReferenceTypeCode (@Nullable final String sContractReferenceTypeCode)
  {
    m_sContractReferenceTypeCode = sContractReferenceTypeCode;
    return this;
  }

  /**
   * Get the contract date (BT-X-148).
   *
   * @return Contract date (BT-X-148). May be {@code null}.
   */
  @Nullable
  public LocalDate getContractDate ()
  {
    return m_aContractDate;
  }

  /**
   * Set the contract date (BT-X-148).
   *
   * @param aContractDate
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setContractDate (@Nullable final LocalDate aContractDate)
  {
    m_aContractDate = aContractDate;
    return this;
  }

  /**
   * Get the tender or lot reference date (BT-X-556).
   *
   * @return Tender or lot reference date (BT-X-556). May be {@code null}.
   */
  @Nullable
  public LocalDate getTenderOrLotReferenceDate ()
  {
    return m_aTenderOrLotReferenceDate;
  }

  /**
   * Set the tender or lot reference date (BT-X-556).
   *
   * @param aTenderOrLotReferenceDate
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setTenderOrLotReferenceDate (@Nullable final LocalDate aTenderOrLotReferenceDate)
  {
    m_aTenderOrLotReferenceDate = aTenderOrLotReferenceDate;
    return this;
  }

  /**
   * Get the invoiced object document date (BT-X-557).
   *
   * @return Invoiced object document date (BT-X-557). May be {@code null}.
   */
  @Nullable
  public LocalDate getInvoicedObjectDate ()
  {
    return m_aInvoicedObjectDate;
  }

  /**
   * Set the invoiced object document date (BT-X-557).
   *
   * @param aInvoicedObjectDate
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setInvoicedObjectDate (@Nullable final LocalDate aInvoicedObjectDate)
  {
    m_aInvoicedObjectDate = aInvoicedObjectDate;
    return this;
  }

  /**
   * Get the despatch advice date (BT-X-200).
   *
   * @return Despatch advice date (BT-X-200). May be {@code null}.
   */
  @Nullable
  public LocalDate getDespatchAdviceDate ()
  {
    return m_aDespatchAdviceDate;
  }

  /**
   * Set the despatch advice date (BT-X-200).
   *
   * @param aDespatchAdviceDate
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setDespatchAdviceDate (@Nullable final LocalDate aDespatchAdviceDate)
  {
    m_aDespatchAdviceDate = aDespatchAdviceDate;
    return this;
  }

  /**
   * Get the receiving advice date (BT-X-201).
   *
   * @return Receiving advice date (BT-X-201). May be {@code null}.
   */
  @Nullable
  public LocalDate getReceivingAdviceDate ()
  {
    return m_aReceivingAdviceDate;
  }

  /**
   * Set the receiving advice date (BT-X-201).
   *
   * @param aReceivingAdviceDate
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setReceivingAdviceDate (@Nullable final LocalDate aReceivingAdviceDate)
  {
    m_aReceivingAdviceDate = aReceivingAdviceDate;
    return this;
  }

  /**
   * Get the delivery note date (BT-X-203).
   *
   * @return Delivery note date (BT-X-203). May be {@code null}.
   */
  @Nullable
  public LocalDate getDeliveryNoteDate ()
  {
    return m_aDeliveryNoteDate;
  }

  /**
   * Set the delivery note date (BT-X-203).
   *
   * @param aDeliveryNoteDate
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setDeliveryNoteDate (@Nullable final LocalDate aDeliveryNoteDate)
  {
    m_aDeliveryNoteDate = aDeliveryNoteDate;
    return this;
  }

  /**
   * Get the seller reference number (BT-X-204).
   *
   * @return Seller reference number (BT-X-204). May be {@code null}.
   */
  @Nullable
  public String getSellerReference ()
  {
    return m_sSellerReference;
  }

  /**
   * Set the seller reference number (BT-X-204).
   *
   * @param sSellerReference
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setSellerReference (@Nullable final String sSellerReference)
  {
    m_sSellerReference = sSellerReference;
    return this;
  }

  /**
   * Get the buyer accounting reference type code (BT-X-290).
   *
   * @return Buyer accounting reference type code (BT-X-290). May be {@code null}.
   */
  @Nullable
  public String getBuyerAccountingReferenceTypeCode ()
  {
    return m_sBuyerAccountingReferenceTypeCode;
  }

  /**
   * Set the buyer accounting reference type code (BT-X-290).
   *
   * @param sBuyerAccountingReferenceTypeCode
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setBuyerAccountingReferenceTypeCode (@Nullable final String sBuyerAccountingReferenceTypeCode)
  {
    m_sBuyerAccountingReferenceTypeCode = sBuyerAccountingReferenceTypeCode;
    return this;
  }

  /**
   * Get the quotation reference (BG-X-61).
   *
   * @return Quotation reference (BG-X-61). May be {@code null}.
   */
  @Nullable
  public PhinvoiceReferencedDocument getQuotationReference ()
  {
    return m_aQuotationReference;
  }

  /**
   * Set the quotation reference (BG-X-61).
   *
   * @param aQuotationReference
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setQuotationReference (@Nullable final PhinvoiceReferencedDocument aQuotationReference)
  {
    m_aQuotationReference = aQuotationReference;
    return this;
  }

  /**
   * Get the mutable list of ultimate customer order references (BG-X-23).
   *
   * @return Mutable list of ultimate customer order references (BG-X-23). Never {@code null}; empty when none are present.
   */
  @NonNull
  @ReturnsMutableObject
  public ICommonsList <PhinvoiceReferencedDocument> ultimateCustomerOrderReferences ()
  {
    return m_aUltimateCustomerOrderReferences;
  }

  /**
   * Get the sales agent (BG-X-49).
   *
   * @return Sales agent (BG-X-49). May be {@code null}.
   */
  @Nullable
  public PhinvoiceTradeParty getSalesAgent ()
  {
    return m_aSalesAgent;
  }

  /**
   * Set the sales agent (BG-X-49).
   *
   * @param aSalesAgent
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setSalesAgent (@Nullable final PhinvoiceTradeParty aSalesAgent)
  {
    m_aSalesAgent = aSalesAgent;
    return this;
  }

  /**
   * Get the buyer tax representative (BG-X-54).
   *
   * @return Buyer tax representative (BG-X-54). May be {@code null}.
   */
  @Nullable
  public PhinvoiceTradeParty getBuyerTaxRepresentative ()
  {
    return m_aBuyerTaxRepresentative;
  }

  /**
   * Set the buyer tax representative (BG-X-54).
   *
   * @param aBuyerTaxRepresentative
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setBuyerTaxRepresentative (@Nullable final PhinvoiceTradeParty aBuyerTaxRepresentative)
  {
    m_aBuyerTaxRepresentative = aBuyerTaxRepresentative;
    return this;
  }

  /**
   * Get the product end user (BG-X-18).
   *
   * @return Product end user (BG-X-18). May be {@code null}.
   */
  @Nullable
  public PhinvoiceTradeParty getProductEndUser ()
  {
    return m_aProductEndUser;
  }

  /**
   * Set the product end user (BG-X-18).
   *
   * @param aProductEndUser
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setProductEndUser (@Nullable final PhinvoiceTradeParty aProductEndUser)
  {
    m_aProductEndUser = aProductEndUser;
    return this;
  }

  /**
   * Get the buyer agent (BG-X-62).
   *
   * @return Buyer agent (BG-X-62). May be {@code null}.
   */
  @Nullable
  public PhinvoiceTradeParty getBuyerAgent ()
  {
    return m_aBuyerAgent;
  }

  /**
   * Set the buyer agent (BG-X-62).
   *
   * @param aBuyerAgent
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setBuyerAgent (@Nullable final PhinvoiceTradeParty aBuyerAgent)
  {
    m_aBuyerAgent = aBuyerAgent;
    return this;
  }

  /**
   * Get the ultimate ship to party (BG-X-27).
   *
   * @return Ultimate ship to party (BG-X-27). May be {@code null}.
   */
  @Nullable
  public PhinvoiceTradeParty getUltimateShipTo ()
  {
    return m_aUltimateShipTo;
  }

  /**
   * Set the ultimate ship to party (BG-X-27).
   *
   * @param aUltimateShipTo
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setUltimateShipTo (@Nullable final PhinvoiceTradeParty aUltimateShipTo)
  {
    m_aUltimateShipTo = aUltimateShipTo;
    return this;
  }

  /**
   * Get the ship from party (BG-X-30).
   *
   * @return Ship from party (BG-X-30). May be {@code null}.
   */
  @Nullable
  public PhinvoiceTradeParty getShipFrom ()
  {
    return m_aShipFrom;
  }

  /**
   * Set the ship from party (BG-X-30).
   *
   * @param aShipFrom
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setShipFrom (@Nullable final PhinvoiceTradeParty aShipFrom)
  {
    m_aShipFrom = aShipFrom;
    return this;
  }

  /**
   * Get the invoicer (BG-X-33).
   *
   * @return Invoicer (BG-X-33). May be {@code null}.
   */
  @Nullable
  public PhinvoiceTradeParty getInvoicer ()
  {
    return m_aInvoicer;
  }

  /**
   * Set the invoicer (BG-X-33).
   *
   * @param aInvoicer
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setInvoicer (@Nullable final PhinvoiceTradeParty aInvoicer)
  {
    m_aInvoicer = aInvoicer;
    return this;
  }

  /**
   * Get the invoicee (BG-X-36).
   *
   * @return Invoicee (BG-X-36). May be {@code null}.
   */
  @Nullable
  public PhinvoiceTradeParty getInvoicee ()
  {
    return m_aInvoicee;
  }

  /**
   * Set the invoicee (BG-X-36).
   *
   * @param aInvoicee
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setInvoicee (@Nullable final PhinvoiceTradeParty aInvoicee)
  {
    m_aInvoicee = aInvoicee;
    return this;
  }

  /**
   * Get the payer (BG-X-73).
   *
   * @return Payer (BG-X-73). May be {@code null}.
   */
  @Nullable
  public PhinvoiceTradeParty getPayer ()
  {
    return m_aPayer;
  }

  /**
   * Set the payer (BG-X-73).
   *
   * @param aPayer
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setPayer (@Nullable final PhinvoiceTradeParty aPayer)
  {
    m_aPayer = aPayer;
    return this;
  }

  /**
   * Get the delivery terms (BG-X-22).
   *
   * @return Delivery terms (BG-X-22). May be {@code null}.
   */
  @Nullable
  public PhinvoiceDeliveryTerms getDeliveryTerms ()
  {
    return m_aDeliveryTerms;
  }

  /**
   * Set the delivery terms (BG-X-22).
   *
   * @param aDeliveryTerms
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setDeliveryTerms (@Nullable final PhinvoiceDeliveryTerms aDeliveryTerms)
  {
    m_aDeliveryTerms = aDeliveryTerms;
    return this;
  }

  /**
   * Get the delivery method code (BT-X-152).
   *
   * @return Delivery method code (BT-X-152). May be {@code null}.
   */
  @Nullable
  public String getTransportModeCode ()
  {
    return m_sTransportModeCode;
  }

  /**
   * Set the delivery method code (BT-X-152).
   *
   * @param sTransportModeCode
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setTransportModeCode (@Nullable final String sTransportModeCode)
  {
    m_sTransportModeCode = sTransportModeCode;
    return this;
  }

  /**
   * Get the tax currency exchange (BG-X-41).
   *
   * @return Tax currency exchange (BG-X-41). May be {@code null}.
   */
  @Nullable
  public PhinvoiceTaxCurrencyExchange getTaxCurrencyExchange ()
  {
    return m_aTaxCurrencyExchange;
  }

  /**
   * Set the tax currency exchange (BG-X-41).
   *
   * @param aTaxCurrencyExchange
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoice setTaxCurrencyExchange (@Nullable final PhinvoiceTaxCurrencyExchange aTaxCurrencyExchange)
  {
    m_aTaxCurrencyExchange = aTaxCurrencyExchange;
    return this;
  }

  /**
   * Get the mutable list of logistics service charges (BG-X-42).
   *
   * @return Mutable list of logistics service charges (BG-X-42). Never {@code null}; empty when none are present.
   */
  @NonNull
  @ReturnsMutableObject
  public ICommonsList <PhinvoiceLogisticsServiceCharge> logisticsServiceCharges ()
  {
    return m_aLogisticsServiceCharges;
  }

  /**
   * Get the mutable list of advance payments (BG-X-45).
   *
   * @return Mutable list of advance payments (BG-X-45). Never {@code null}; empty when none are present.
   */
  @NonNull
  @ReturnsMutableObject
  public ICommonsList <PhinvoiceAdvancePayment> advancePayments ()
  {
    return m_aAdvancePayments;
  }
}
