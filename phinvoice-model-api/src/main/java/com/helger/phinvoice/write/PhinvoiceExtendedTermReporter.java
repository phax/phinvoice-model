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

import java.util.List;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.phinvoice.model.PhinvoiceAdvancePayment;
import com.helger.phinvoice.model.PhinvoiceBuyer;
import com.helger.phinvoice.model.PhinvoiceContact;
import com.helger.phinvoice.model.PhinvoiceDeliveryInformation;
import com.helger.phinvoice.model.PhinvoiceDeliveryTerms;
import com.helger.phinvoice.model.PhinvoiceDocumentAllowance;
import com.helger.phinvoice.model.PhinvoiceDocumentCharge;
import com.helger.phinvoice.model.PhinvoiceEarlyPaymentDiscount;
import com.helger.phinvoice.model.PhinvoiceIdentifier;
import com.helger.phinvoice.model.PhinvoiceIncludedTax;
import com.helger.phinvoice.model.PhinvoiceInvoice;
import com.helger.phinvoice.model.PhinvoiceInvoiceLine;
import com.helger.phinvoice.model.PhinvoiceInvoiceNote;
import com.helger.phinvoice.model.PhinvoiceItem;
import com.helger.phinvoice.model.PhinvoiceItemClassification;
import com.helger.phinvoice.model.PhinvoiceLatePaymentPenalty;
import com.helger.phinvoice.model.PhinvoiceLineDelivery;
import com.helger.phinvoice.model.PhinvoiceLineVat;
import com.helger.phinvoice.model.PhinvoiceLogisticsServiceCharge;
import com.helger.phinvoice.model.PhinvoicePayee;
import com.helger.phinvoice.model.PhinvoicePaymentTerms;
import com.helger.phinvoice.model.PhinvoicePostalAddress;
import com.helger.phinvoice.model.PhinvoicePriceAllowanceCharge;
import com.helger.phinvoice.model.PhinvoicePriceDetails;
import com.helger.phinvoice.model.PhinvoiceReferencedDocument;
import com.helger.phinvoice.model.PhinvoiceSeller;
import com.helger.phinvoice.model.PhinvoiceSellerTaxRepresentative;
import com.helger.phinvoice.model.PhinvoiceSupportingDocument;
import com.helger.phinvoice.model.PhinvoiceTaxCurrencyExchange;
import com.helger.phinvoice.model.PhinvoiceTradeParty;
import com.helger.phinvoice.model.PhinvoiceVatBreakdown;
import com.helger.phinvoice.term.PhinvoiceExtendedTerms;
import com.helger.phinvoice.term.PhinvoiceExtendedTerms.AddressTerms;
import com.helger.phinvoice.term.PhinvoiceExtendedTerms.ContactTerms;
import com.helger.phinvoice.term.PhinvoiceExtendedTerms.DocumentTerms;
import com.helger.phinvoice.term.PhinvoiceExtendedTerms.PartyTerms;

/**
 * Reports the values of the model that only the Factur-X profile EXTENDED has, for the writers of
 * targets that cannot carry any of them (UBL and CII of EN 16931:2026). Every value goes through
 * {@link PhinvoiceTermFilter#use(String, Object)} with its explicit term ID (decision D11), so the
 * filter reports it. A structure of the profile EXTENDED (a party, a referenced document, delivery
 * terms and the like) is reported once with the ID of its group.
 *
 * @author Philip Helger
 */
public final class PhinvoiceExtendedTermReporter
{
  private static final String TAX_SCHEME_VAT = "VA";

  private PhinvoiceExtendedTermReporter ()
  {}

  private static void _group (@NonNull final PhinvoiceTermFilter aFilter,
                              @Nullable final String sGroupID,
                              final boolean bHasValue,
                              @NonNull final String sLabel)
  {
    if (bHasValue && sGroupID != null)
      aFilter.use (sGroupID, sLabel);
  }

  @NonNull
  private static String _label (@Nullable final String sValue, @NonNull final String sDefault)
  {
    return sValue != null && !sValue.isEmpty () ? sValue : sDefault;
  }

  private static void _party (@NonNull final PhinvoiceTermFilter aFilter,
                              @Nullable final PhinvoiceTradeParty aParty,
                              @NonNull final PartyTerms aTerms)
  {
    if (aParty != null)
      _group (aFilter, aTerms.group (), !aParty.isEmpty (), _label (aParty.getName (), _label (aParty.getID (), "trade party")));
  }

  private static void _document (@NonNull final PhinvoiceTermFilter aFilter,
                                 @Nullable final PhinvoiceReferencedDocument aDoc,
                                 @NonNull final DocumentTerms aTerms)
  {
    if (aDoc != null)
      _group (aFilter, aTerms.group (), !aDoc.isEmpty (), _label (aDoc.getIssuerAssignedID (), "referenced document"));
  }

  private static void _documents (@NonNull final PhinvoiceTermFilter aFilter,
                                  @NonNull final List <PhinvoiceReferencedDocument> aDocs,
                                  @NonNull final DocumentTerms aTerms)
  {
    for (final PhinvoiceReferencedDocument aDoc : aDocs)
      _document (aFilter, aDoc, aTerms);
  }

  private static void _address (@NonNull final PhinvoiceTermFilter aFilter,
                                @Nullable final PhinvoicePostalAddress aAddress,
                                @NonNull final AddressTerms aTerms)
  {
    if (aAddress != null)
      _group (aFilter, aTerms.group (), !aAddress.isEmpty (), _label (aAddress.getCity (), "postal address"));
  }

  private static void _contact (@NonNull final PhinvoiceTermFilter aFilter,
                                @Nullable final PhinvoiceContact aContact,
                                @NonNull final ContactTerms aTerms)
  {
    if (aContact != null)
      _group (aFilter, aTerms.group (), !aContact.isEmpty (), _label (aContact.getName (), "contact"));
  }

  /** The values of the profile EXTENDED of a contact of EN 16931 */
  private static void _contactExtras (@NonNull final PhinvoiceTermFilter aFilter,
                                      @Nullable final PhinvoiceContact aContact,
                                      @NonNull final ContactTerms aTerms)
  {
    if (aContact != null)
    {
      aFilter.use (aTerms.fax (), aContact.getFax ());
      aFilter.use (aTerms.typeCode (), aContact.getTypeCode ());
    }
  }

  private static void _identifier (@NonNull final PhinvoiceTermFilter aFilter,
                                   @Nullable final String sTermID,
                                   @Nullable final PhinvoiceIdentifier aID)
  {
    if (aID != null && sTermID != null)
      aFilter.use (sTermID, aID.getValue ());
  }

  private static void _taxRegistrations (@NonNull final PhinvoiceTermFilter aFilter,
                                         @NonNull final List <PhinvoiceIdentifier> aTaxRegistrations,
                                         @NonNull final PartyTerms aTerms)
  {
    for (final PhinvoiceIdentifier aID : aTaxRegistrations)
      _identifier (aFilter, TAX_SCHEME_VAT.equalsIgnoreCase (aID.getSchemeID ()) ? aTerms.vatID () : aTerms.taxNumber (), aID);
  }

  private static void _includedTax (@NonNull final PhinvoiceTermFilter aFilter,
                                    @Nullable final PhinvoiceIncludedTax aTax,
                                    @NonNull final String sGroupID)
  {
    if (aTax != null)
      _group (aFilter, sGroupID, !aTax.isEmpty (), "included tax");
  }

  private static void _deliveryTerms (@NonNull final PhinvoiceTermFilter aFilter,
                                      @Nullable final PhinvoiceDeliveryTerms aTerms,
                                      @NonNull final String sGroupID)
  {
    if (aTerms != null)
      _group (aFilter, sGroupID, !aTerms.isEmpty (), _label (aTerms.getDeliveryTypeCode (), "delivery terms"));
  }

  private static void _parties (@NonNull final PhinvoiceTermFilter aFilter, @NonNull final PhinvoiceInvoice aInvoice)
  {
    final PhinvoiceSeller aSeller = aInvoice.getSeller ();
    aFilter.use (PhinvoiceExtendedTerms.SELLER.roleCode (), aSeller.getRoleCode ());
    _address (aFilter, aSeller.getLegalAddress (), PhinvoiceExtendedTerms.SELLER.legalAddress ());
    _contactExtras (aFilter, aSeller.getContact (), PhinvoiceExtendedTerms.SELLER.contact ());

    final PhinvoiceBuyer aBuyer = aInvoice.getBuyer ();
    aFilter.use (PhinvoiceExtendedTerms.BUYER.roleCode (), aBuyer.getRoleCode ());
    aFilter.use (PhinvoiceExtendedTerms.BUYER.description (), aBuyer.getAdditionalLegalInfo ());
    _address (aFilter, aBuyer.getLegalAddress (), PhinvoiceExtendedTerms.BUYER.legalAddress ());
    _contactExtras (aFilter, aBuyer.getContact (), PhinvoiceExtendedTerms.BUYER.contact ());

    final PhinvoiceSellerTaxRepresentative aTaxRep = aInvoice.getSellerTaxRepresentative ();
    if (aTaxRep != null)
    {
      final PartyTerms aTerms = PhinvoiceExtendedTerms.SELLER_TAX_REPRESENTATIVE;
      aFilter.use (aTerms.id (), aTaxRep.getID ());
      for (final PhinvoiceIdentifier aID : aTaxRep.globalIdentifiers ())
        _identifier (aFilter, aTerms.globalID (), aID);
      aFilter.use (aTerms.roleCode (), aTaxRep.getRoleCode ());
      _identifier (aFilter, aTerms.electronicAddress (), aTaxRep.getElectronicAddress ());
      _identifier (aFilter, aTerms.legalID (), aTaxRep.getLegalRegistration ());
      aFilter.use (aTerms.tradingName (), aTaxRep.getTradingName ());
      _address (aFilter, aTaxRep.getLegalAddress (), aTerms.legalAddress ());
      _contact (aFilter, aTaxRep.getContact (), aTerms.contact ());
    }

    final PhinvoiceDeliveryInformation aDI = aInvoice.getDeliveryInformation ();
    if (aDI != null)
    {
      final PartyTerms aTerms = PhinvoiceExtendedTerms.SHIP_TO;
      aFilter.use (aTerms.roleCode (), aDI.getRoleCode ());
      _identifier (aFilter, aTerms.electronicAddress (), aDI.getElectronicAddress ());
      _taxRegistrations (aFilter, aDI.taxRegistrations (), aTerms);
      _identifier (aFilter, aTerms.legalID (), aDI.getLegalRegistration ());
      aFilter.use (aTerms.tradingName (), aDI.getTradingName ());
      _address (aFilter, aDI.getLegalAddress (), aTerms.legalAddress ());
      _contact (aFilter, aDI.getContact (), aTerms.contact ());
    }

    final PhinvoicePayee aPayee = aInvoice.getPayee ();
    if (aPayee != null)
    {
      final PartyTerms aTerms = PhinvoiceExtendedTerms.PAYEE;
      aFilter.use (aTerms.roleCode (), aPayee.getRoleCode ());
      aFilter.use (aTerms.tradingName (), aPayee.getTradingName ());
      _identifier (aFilter, aTerms.electronicAddress (), aPayee.getElectronicAddress ());
      _taxRegistrations (aFilter, aPayee.taxRegistrations (), aTerms);
      _address (aFilter, aPayee.getLegalAddress (), aTerms.legalAddress ());
      _contact (aFilter, aPayee.getContact (), aTerms.contact ());
      _address (aFilter, aPayee.getPostalAddress (), aTerms.address ());
    }

    _party (aFilter, aInvoice.getSalesAgent (), PhinvoiceExtendedTerms.SALES_AGENT);
    _party (aFilter, aInvoice.getBuyerTaxRepresentative (), PhinvoiceExtendedTerms.BUYER_TAX_REPRESENTATIVE);
    _party (aFilter, aInvoice.getProductEndUser (), PhinvoiceExtendedTerms.PRODUCT_END_USER);
    _party (aFilter, aInvoice.getBuyerAgent (), PhinvoiceExtendedTerms.BUYER_AGENT);
    _party (aFilter, aInvoice.getUltimateShipTo (), PhinvoiceExtendedTerms.ULTIMATE_SHIP_TO);
    _party (aFilter, aInvoice.getShipFrom (), PhinvoiceExtendedTerms.SHIP_FROM);
    _party (aFilter, aInvoice.getInvoicer (), PhinvoiceExtendedTerms.INVOICER);
    _party (aFilter, aInvoice.getInvoicee (), PhinvoiceExtendedTerms.INVOICEE);
    _party (aFilter, aInvoice.getPayer (), PhinvoiceExtendedTerms.PAYER);
  }

  private static void _settlement (@NonNull final PhinvoiceTermFilter aFilter, @NonNull final PhinvoiceInvoice aInvoice)
  {
    if (aInvoice.getInvoicingPeriod () != null)
      aFilter.use ("BT-X-264", aInvoice.getInvoicingPeriod ().getDescription ());
    for (final PhinvoiceDocumentAllowance aDA : aInvoice.documentAllowances ())
    {
      aFilter.use ("BT-X-265", aDA.getSequenceNumeric ());
      aFilter.use ("BT-X-266", aDA.getBasisQuantity ());
      aFilter.use ("BT-X-267", aDA.getBasisQuantityUnit ());
    }
    for (final PhinvoiceDocumentCharge aDC : aInvoice.documentCharges ())
    {
      aFilter.use ("BT-X-268", aDC.getSequenceNumeric ());
      aFilter.use ("BT-X-269", aDC.getBasisQuantity ());
      aFilter.use ("BT-X-270", aDC.getBasisQuantityUnit ());
    }
    for (final PhinvoiceVatBreakdown aBD : aInvoice.vatBreakdowns ())
    {
      aFilter.use ("BT-X-262", aBD.getLineTotalBasisAmount ());
      aFilter.use ("BT-X-263", aBD.getAllowanceChargeBasisAmount ());
    }
    for (final PhinvoicePaymentTerms aPT : aInvoice.paymentTerms ())
    {
      aFilter.use ("BT-X-275", aPT.getPartialPaymentAmount ());
      _party (aFilter, aPT.getPayee (), PhinvoiceExtendedTerms.PAYMENT_TERMS_PAYEE);
      if (aPT.getDueDate () != null)
        aFilter.drop ("BT-9", aPT.getDueDate (), "EN 16931 has only one payment due date");
      for (final PhinvoiceEarlyPaymentDiscount aED : aPT.earlyDiscounts ())
      {
        aFilter.use ("BT-X-283", aED.getBasisPeriodMeasure ());
        aFilter.use ("BT-X-284", aED.getBasisPeriodUnit ());
        aFilter.use ("BT-X-285", aED.getBaseAmount ());
      }
      for (final PhinvoiceLatePaymentPenalty aLP : aPT.latePenalties ())
      {
        aFilter.use ("BT-X-277", aLP.getBasisPeriodMeasure ());
        aFilter.use ("BT-X-278", aLP.getBasisPeriodUnit ());
        aFilter.use ("BT-X-279", aLP.getBaseAmount ());
      }
    }
    aFilter.use ("BT-X-204", aInvoice.getSellerReference ());
    aFilter.use ("BT-X-290", aInvoice.getBuyerAccountingReferenceTypeCode ());
    final PhinvoiceTaxCurrencyExchange aExchange = aInvoice.getTaxCurrencyExchange ();
    if (aExchange != null)
      _group (aFilter, "BG-X-41", !aExchange.isEmpty (), _label (aExchange.getSourceCurrency (), "tax currency exchange"));
    for (final PhinvoiceLogisticsServiceCharge aCharge : aInvoice.logisticsServiceCharges ())
      _group (aFilter, "BG-X-42", !aCharge.isEmpty (), _label (aCharge.getDescription (), "logistics service charge"));
    for (final PhinvoiceAdvancePayment aPayment : aInvoice.advancePayments ())
      _group (aFilter,
              "BG-X-45",
              !aPayment.isEmpty (),
              aPayment.getPaidAmount () != null ? aPayment.getPaidAmount ().toPlainString () : "advance payment");
  }

  private static void _price (@NonNull final PhinvoiceTermFilter aFilter, @Nullable final PhinvoicePriceDetails aPrice)
  {
    if (aPrice == null)
      return;
    int nIndex = 0;
    for (final PhinvoicePriceAllowanceCharge aPAC : aPrice.allowances ())
    {
      if (nIndex++ == 0)
      {
        // The amount is BT-147
        aFilter.use ("BT-X-34", aPAC.getPercentage ());
        aFilter.use ("BT-X-35", aPAC.getBaseAmount ());
        aFilter.use ("BT-X-313", aPAC.getReasonCode ());
        aFilter.use ("BT-X-36", aPAC.getReason ());
      }
      else
        if (!aPAC.isEmpty ())
          aFilter.drop ("BT-147",
                        aPAC.getAmount () != null ? aPAC.getAmount () : "allowance on the gross price",
                        "EN 16931 has only one item price discount");
    }
    for (final PhinvoicePriceAllowanceCharge aPAC : aPrice.charges ())
      if (!aPAC.isEmpty ())
        aFilter.use ("BT-X-302", aPAC.getAmount () != null ? aPAC.getAmount () : "charge on the gross price");
  }

  private static void _line (@NonNull final PhinvoiceTermFilter aFilter, @NonNull final PhinvoiceInvoiceLine aLine)
  {
    aFilter.use ("BT-X-304", aLine.getParentLineID ());
    aFilter.use ("BT-X-7", aLine.getLineStatusCode ());
    aFilter.use ("BT-X-8", aLine.getLineStatusReasonCode ());
    aFilter.use ("BT-X-9", aLine.getNoteContentCode ());
    aFilter.use ("BT-X-10", aLine.getNoteSubjectCode ());
    aFilter.use ("BT-X-46", aLine.getChargeFreeQuantity ());
    aFilter.use ("BT-X-46-0", aLine.getChargeFreeQuantityUnit ());
    aFilter.use ("BT-X-47", aLine.getPackageQuantity ());
    aFilter.use ("BT-X-47-0", aLine.getPackageQuantityUnit ());
    aFilter.use ("BT-X-561", aLine.getPerPackageUnitQuantity ());
    aFilter.use ("BT-X-561-0", aLine.getPerPackageUnitQuantityUnit ());
    aFilter.use (PhinvoiceExtendedTerms.LINE_BUYER_ORDER.issueDate (), aLine.getPurchaseOrderDate ());
    aFilter.use (PhinvoiceExtendedTerms.LINE_SALES_ORDER.issueDate (), aLine.getSalesOrderDate ());
    aFilter.use (PhinvoiceExtendedTerms.LINE_DESPATCH_ADVICE.issueDate (), aLine.getDespatchAdviceDate ());
    aFilter.use (PhinvoiceExtendedTerms.LINE_RECEIVING_ADVICE.issueDate (), aLine.getReceivingAdviceDate ());
    aFilter.use (PhinvoiceExtendedTerms.LINE_DELIVERY_NOTE.issueDate (), aLine.getDeliveryNoteDate ());
    _document (aFilter, aLine.getQuotationReference (), PhinvoiceExtendedTerms.LINE_QUOTATION);
    _document (aFilter, aLine.getContractReference (), PhinvoiceExtendedTerms.LINE_CONTRACT);
    _documents (aFilter, aLine.additionalReferencedDocuments (), PhinvoiceExtendedTerms.LINE_ADDITIONAL);
    _documents (aFilter, aLine.ultimateCustomerOrderReferences (), PhinvoiceExtendedTerms.LINE_ULTIMATE_CUSTOMER_ORDER);
    _party (aFilter, aLine.getLineSeller (), PhinvoiceExtendedTerms.LINE_SELLER);
    _party (aFilter, aLine.getUltimateShipTo (), PhinvoiceExtendedTerms.LINE_ULTIMATE_SHIP_TO);
    _includedTax (aFilter, aLine.getIncludedTax (), "BG-X-4");
    _deliveryTerms (aFilter, aLine.getDeliveryTerms (), "BG-X-87");
    aFilter.use ("BT-X-327", aLine.getChargeTotal ());
    aFilter.use ("BT-X-328", aLine.getAllowanceTotal ());
    aFilter.use ("BT-X-329", aLine.getTaxTotal ());
    aFilter.use ("BT-X-590", aLine.getTaxTotalAccountingCurrency ());
    aFilter.use ("BT-X-330", aLine.getGrandTotal ());
    aFilter.use ("BT-X-98", aLine.getTotalAllowanceCharge ());
    aFilter.use ("BT-X-99", aLine.getAccountingCostTypeCode ());

    final PhinvoiceLineDelivery aLD = aLine.getDelivery ();
    if (aLD != null)
    {
      final PartyTerms aTerms = PhinvoiceExtendedTerms.LINE_SHIP_TO;
      aFilter.use (aTerms.id (), aLD.getPartyID ());
      aFilter.use (aTerms.roleCode (), aLD.getRoleCode ());
      _identifier (aFilter, aTerms.legalID (), aLD.getLegalRegistration ());
      aFilter.use (aTerms.tradingName (), aLD.getTradingName ());
      _contact (aFilter, aLD.getContact (), aTerms.contact ());
      _identifier (aFilter, aTerms.electronicAddress (), aLD.getElectronicAddress ());
      _taxRegistrations (aFilter, aLD.taxRegistrations (), aTerms);
    }

    final PhinvoiceItem aItem = aLine.getItem ();
    if (aItem != null)
    {
      aFilter.use ("BT-X-305", aItem.getID ());
      aFilter.use ("BT-X-532", aItem.getIndustryAssignedID ());
      aFilter.use ("BT-X-533", aItem.getModelID ());
      aFilter.use ("BT-X-534", aItem.getBatchID ());
      aFilter.use ("BT-X-535", aItem.getBrandName ());
      aFilter.use ("BT-X-536", aItem.getModelName ());
      _party (aFilter, aItem.getManufacturer (), PhinvoiceExtendedTerms.MANUFACTURER);
      _group (aFilter, "BG-X-84", aItem.productInstances ().isNotEmpty (), "product instance");
      _group (aFilter, "BG-X-1", aItem.includedProducts ().isNotEmpty (), "included item");
      for (final PhinvoiceItemClassification aIC : aItem.classifications ())
        aFilter.use ("BT-X-13", aIC.getName ());
    }

    final PhinvoiceLineVat aVat = aLine.getLineVat ();
    if (aVat != null)
    {
      aFilter.use ("BT-X-95", aVat.getCalculatedAmount ());
      aFilter.use ("BT-X-589", aVat.getVatPointDateCode ());
    }
    _price (aFilter, aLine.getPrice ());
  }

  /**
   * Report all values of the profile EXTENDED of the invoice.
   *
   * @param aInvoice
   *        The invoice to report. May not be <code>null</code>.
   * @param aFilter
   *        The filter of the writer. May not be <code>null</code>.
   */
  public static void reportExtendedTerms (@NonNull final PhinvoiceInvoice aInvoice, @NonNull final PhinvoiceTermFilter aFilter)
  {
    aFilter.use ("BT-X-1", aInvoice.getTestIndicator ());
    aFilter.use ("BT-X-2", aInvoice.getDocumentName ());
    aFilter.use ("BT-X-3", aInvoice.getCopyIndicator ());
    aFilter.use ("BT-X-4", aInvoice.getLanguageCode ());
    aFilter.use ("BT-X-6", aInvoice.getContractualDueDate ());
    for (final PhinvoiceInvoiceNote aNote : aInvoice.invoiceNotes ())
      aFilter.use ("BT-X-5", aNote.getContentCode ());
    aFilter.use ("BT-X-146", aInvoice.getSalesOrderDate ());
    aFilter.use ("BT-X-147", aInvoice.getPurchaseOrderDate ());
    aFilter.use ("BT-X-405", aInvoice.getContractReferenceTypeCode ());
    aFilter.use ("BT-X-148", aInvoice.getContractDate ());
    aFilter.use ("BT-X-556", aInvoice.getTenderOrLotReferenceDate ());
    aFilter.use ("BT-X-557", aInvoice.getInvoicedObjectDate ());
    aFilter.use ("BT-X-200", aInvoice.getDespatchAdviceDate ());
    aFilter.use ("BT-X-201", aInvoice.getReceivingAdviceDate ());
    aFilter.use ("BT-X-203", aInvoice.getDeliveryNoteDate ());
    aFilter.use ("BT-X-152", aInvoice.getTransportModeCode ());
    for (final PhinvoiceSupportingDocument aDoc : aInvoice.supportingDocuments ())
      aFilter.use ("BT-X-149", aDoc.getIssueDate ());
    _document (aFilter, aInvoice.getQuotationReference (), PhinvoiceExtendedTerms.QUOTATION);
    _documents (aFilter, aInvoice.ultimateCustomerOrderReferences (), PhinvoiceExtendedTerms.ULTIMATE_CUSTOMER_ORDER);
    _deliveryTerms (aFilter, aInvoice.getDeliveryTerms (), "BG-X-22");
    _parties (aFilter, aInvoice);
    _settlement (aFilter, aInvoice);
    for (final PhinvoiceInvoiceLine aLine : aInvoice.lines ())
      _line (aFilter, aLine);
  }
}
