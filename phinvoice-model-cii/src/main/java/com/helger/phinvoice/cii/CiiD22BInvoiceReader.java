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
package com.helger.phinvoice.cii;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.base.equals.EqualsHelper;
import com.helger.base.string.StringHelper;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.datetime.xml.XMLOffsetDateTime;
import com.helger.diagnostics.error.SingleError;
import com.helger.diagnostics.error.list.ErrorList;
import com.helger.en16931.basics.EEN16931DateFormatCode;
import com.helger.en16931.basics.EEN16931Edition;
import com.helger.en16931.basics.codelist.EN16931CodeLists;
import com.helger.phinvoice.PhinvoiceLogHelper;
import com.helger.phinvoice.model.EPhinvoiceDocumentKind;
import com.helger.phinvoice.model.PhinvoiceAdditionalTerm;
import com.helger.phinvoice.model.PhinvoiceAdvancePayment;
import com.helger.phinvoice.model.PhinvoiceAttachment;
import com.helger.phinvoice.model.PhinvoiceBuyer;
import com.helger.phinvoice.model.PhinvoiceContact;
import com.helger.phinvoice.model.PhinvoiceCreditTransfer;
import com.helger.phinvoice.model.PhinvoiceDeliveryInformation;
import com.helger.phinvoice.model.PhinvoiceDeliveryTerms;
import com.helger.phinvoice.model.PhinvoiceDirectDebit;
import com.helger.phinvoice.model.PhinvoiceDocumentAllowance;
import com.helger.phinvoice.model.PhinvoiceDocumentCharge;
import com.helger.phinvoice.model.PhinvoiceDocumentTotals;
import com.helger.phinvoice.model.PhinvoiceEarlyPaymentDiscount;
import com.helger.phinvoice.model.PhinvoiceIdentifier;
import com.helger.phinvoice.model.PhinvoiceIncludedProduct;
import com.helger.phinvoice.model.PhinvoiceIncludedTax;
import com.helger.phinvoice.model.PhinvoiceInvoice;
import com.helger.phinvoice.model.PhinvoiceInvoiceLine;
import com.helger.phinvoice.model.PhinvoiceInvoiceNote;
import com.helger.phinvoice.model.PhinvoiceInvoicingPeriod;
import com.helger.phinvoice.model.PhinvoiceItem;
import com.helger.phinvoice.model.PhinvoiceItemAttribute;
import com.helger.phinvoice.model.PhinvoiceItemClassification;
import com.helger.phinvoice.model.PhinvoiceLatePaymentPenalty;
import com.helger.phinvoice.model.PhinvoiceLineAllowance;
import com.helger.phinvoice.model.PhinvoiceLineCharge;
import com.helger.phinvoice.model.PhinvoiceLineDelivery;
import com.helger.phinvoice.model.PhinvoiceLinePrecedingInvoiceRef;
import com.helger.phinvoice.model.PhinvoiceLineVat;
import com.helger.phinvoice.model.PhinvoiceLogisticsServiceCharge;
import com.helger.phinvoice.model.PhinvoicePayee;
import com.helger.phinvoice.model.PhinvoicePaymentCard;
import com.helger.phinvoice.model.PhinvoicePaymentInstructions;
import com.helger.phinvoice.model.PhinvoicePaymentTerms;
import com.helger.phinvoice.model.PhinvoicePostalAddress;
import com.helger.phinvoice.model.PhinvoicePrecedingInvoiceRef;
import com.helger.phinvoice.model.PhinvoicePriceAllowanceCharge;
import com.helger.phinvoice.model.PhinvoicePriceDetails;
import com.helger.phinvoice.model.PhinvoiceProductInstance;
import com.helger.phinvoice.model.PhinvoiceReferencedDocument;
import com.helger.phinvoice.model.PhinvoiceSeller;
import com.helger.phinvoice.model.PhinvoiceSellerTaxRepresentative;
import com.helger.phinvoice.model.PhinvoiceSupportingDocument;
import com.helger.phinvoice.model.PhinvoiceTaxCurrencyExchange;
import com.helger.phinvoice.model.PhinvoiceThirdPartyCharge;
import com.helger.phinvoice.model.PhinvoiceTradeParty;
import com.helger.phinvoice.model.PhinvoiceVatBreakdown;
import com.helger.phinvoice.read.PhinvoiceReaderHelper;

import un.unece.uncefact.data.standard.cii.d22b.CrossIndustryInvoiceType;
import un.unece.uncefact.data.standard.cii.d22b.qdt.AllowanceChargeReasonCodeType;
import un.unece.uncefact.data.standard.cii.d22b.qdt.CurrencyCodeType;
import un.unece.uncefact.data.standard.cii.d22b.qdt.DocumentCodeType;
import un.unece.uncefact.data.standard.cii.d22b.qdt.FormattedDateTimeType;
import un.unece.uncefact.data.standard.cii.d22b.qdt.PaymentMeansCodeType;
import un.unece.uncefact.data.standard.cii.d22b.qdt.ReferenceCodeType;
import un.unece.uncefact.data.standard.cii.d22b.qdt.TaxCategoryCodeType;
import un.unece.uncefact.data.standard.cii.d22b.qdt.TimeReferenceCodeType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.AdvancePaymentType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.CreditorFinancialAccountType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.CreditorFinancialInstitutionType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.DebtorFinancialAccountType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.DebtorFinancialInstitutionType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.DocumentLineDocumentType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.ExchangedDocumentType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.FinancialAdjustmentType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.HeaderTradeAgreementType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.HeaderTradeDeliveryType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.HeaderTradeSettlementType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.LegalOrganizationType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.LineTradeAgreementType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.LineTradeDeliveryType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.LineTradeSettlementType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.LogisticsServiceChargeType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.NoteType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.ProcuringProjectType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.ProductCharacteristicType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.ProductClassificationType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.ReferencedDocumentType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.ReferencedProductType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.SpecifiedPeriodType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.SupplyChainConsignmentType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.SupplyChainEventType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.SupplyChainTradeLineItemType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.SupplyChainTradeTransactionType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.TaxRegistrationType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.TradeAddressType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.TradeAllowanceChargeType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.TradeContactType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.TradeCurrencyExchangeType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.TradeDeliveryTermsType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.TradePartyType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.TradePaymentDiscountTermsType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.TradePaymentPenaltyTermsType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.TradePaymentTermsType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.TradePriceType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.TradeProductInstanceType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.TradeProductType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.TradeSettlementHeaderMonetarySummationType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.TradeSettlementLineMonetarySummationType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.TradeSettlementPaymentMeansType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.TradeTaxType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.UniversalCommunicationType;
import un.unece.uncefact.data.standard.cii.d22b.udt.AmountType;
import un.unece.uncefact.data.standard.cii.d22b.udt.BinaryObjectType;
import un.unece.uncefact.data.standard.cii.d22b.udt.CodeType;
import un.unece.uncefact.data.standard.cii.d22b.udt.DateTimeType;
import un.unece.uncefact.data.standard.cii.d22b.udt.DateType;
import un.unece.uncefact.data.standard.cii.d22b.udt.IDType;
import un.unece.uncefact.data.standard.cii.d22b.udt.QuantityType;
import un.unece.uncefact.data.standard.cii.d22b.udt.TextType;

/**
 * Reads CII D16B and D22B {@code CrossIndustryInvoice} documents (EN 16931:2017 and the Factur-X
 * profiles, which are based on it) with the CII D22B JAXB classes into a
 * {@link PhinvoiceInvoice}. Package-private worker of {@link CiiInvoiceReader}.
 *
 * @author Philip Helger
 */
final class CiiD22BInvoiceReader extends AbstractCiiInvoiceReader
{
  /** UNTDID 5153 list identifier that marks BT-177 / BT-193 non-VAT tax codes. */
  private static final String NON_VAT_TAX_CODE_LIST_ID = "5153";

  /** Format code of a date with time and offset (UN/EDIFACT 2379), used for BT-166. */
  private static final String FORMAT_CODE_DATE_TIME = EEN16931DateFormatCode.CCYYMMDDHHMMSSZHHMM.getID ();

  CiiD22BInvoiceReader ()
  {}

  /**
   * Check if the reason code is a non-VAT tax code.
   *
   * @return {@code true} if the reason code carries {@code @listID="5153"} (UNTDID 5153 duty or tax
   *         type), i.e. it is a non-VAT tax code (BT-177 / BT-193) and not a charge reason code.
   *         The CII schema declares a default for {@code @listID}, so only the fixed value counts.
   */
  private static boolean _isNonVatTaxCodeList (@Nullable final AllowanceChargeReasonCodeType aCode)
  {
    return aCode != null && NON_VAT_TAX_CODE_LIST_ID.equals (aCode.getListID ());
  }

  @NonNull
  private static ICommonsList <BigDecimal> _getAmounts (@NonNull final List <AmountType> aAmounts)
  {
    return new CommonsArrayList <> (aAmounts, AmountType::getValue);
  }

  private static void _readTotals (@NonNull final TradeSettlementHeaderMonetarySummationType aS,
                                   @NonNull final PhinvoiceInvoice aModel)
  {
    final PhinvoiceDocumentTotals aTotals = aModel.getDocumentTotals ();
    final ICommonsList <PhinvoiceAdditionalTerm> aTerms = aModel.additionalTerms ();
    PhinvoiceReaderHelper.setAndAddRepeated (_getAmounts (aS.getLineTotalAmount ()), "BT-106", aTotals::setLineNetTotal, aTerms);
    PhinvoiceReaderHelper.setAndAddRepeated (_getAmounts (aS.getAllowanceTotalAmount ()),
                                         "BT-107",
                                         aTotals::setAllowanceTotal,
                                         aTerms);
    PhinvoiceReaderHelper.setAndAddRepeated (_getAmounts (aS.getChargeTotalAmount ()), "BT-108", aTotals::setChargeTotal, aTerms);
    PhinvoiceReaderHelper.setAndAddRepeated (_getAmounts (aS.getTaxBasisTotalAmount ()),
                                         "BT-109",
                                         aTotals::setTaxExclusiveTotal,
                                         aTerms);
    PhinvoiceReaderHelper.setAndAddRepeated (_getAmounts (aS.getGrandTotalAmount ()),
                                         "BT-112",
                                         aTotals::setTaxInclusiveTotal,
                                         aTerms);
    PhinvoiceReaderHelper.setAndAddRepeated (_getAmounts (aS.getTotalPrepaidAmount ()), "BT-113", aTotals::setPrepaidAmount, aTerms);
    PhinvoiceReaderHelper.setAndAddRepeated (_getAmounts (aS.getRoundingAmount ()), "BT-114", aTotals::setRoundingAmount, aTerms);
    PhinvoiceReaderHelper.setAndAddRepeated (_getAmounts (aS.getDuePayableAmount ()), "BT-115", aTotals::setPayableAmount, aTerms);

    // BT-110 (main) / BT-111 (accounting currency). A further tax total keeps its currency, because
    // it may be any
    final String sAcct = aModel.getVatAccountingCurrency ();
    for (final AmountType aA : aS.getTaxTotalAmount ())
    {
      final boolean bAccounting = isVatAccountingAmount (aModel.getCurrency (),
                                                         sAcct,
                                                         aA.getCurrencyID (),
                                                         aTotals.getTaxTotal () != null);
      if (bAccounting && aTotals.getTaxTotalAccountingCurrency () == null)
        aTotals.setTaxTotalAccountingCurrency (aA.getValue ());
      else
        if (!bAccounting && aTotals.getTaxTotal () == null)
          aTotals.setTaxTotal (aA.getValue ());
        else
          aTerms.add (new PhinvoiceAdditionalTerm (bAccounting ? "BT-111" : "BT-110",
                                               aA.getValue ().toPlainString () + " " + aA.getCurrencyID ()));
    }
  }

  @NonNull
  private static PhinvoicePriceDetails _readPrice (@NonNull final LineTradeAgreementType aLTA,
                                               @NonNull final PhinvoiceInvoiceLine aLine)
  {
    final PhinvoicePriceDetails aOut = new PhinvoicePriceDetails ();
    final ICommonsList <PhinvoiceAdditionalTerm> aTerms = aLine.additionalTerms ();
    final TradePriceType aNet = aLTA.getNetPriceProductTradePrice ();
    if (aNet != null)
    {
      PhinvoiceReaderHelper.setAndAddRepeated (_getAmounts (aNet.getChargeAmount ()), "BT-146", aOut::setNetPrice, aTerms);
      if (aNet.getBasisQuantity () != null)
      {
        aOut.setBaseQuantity (aNet.getBasisQuantity ().getValue ());
        aOut.setBaseQuantityUnit (str (aNet.getBasisQuantity ().getUnitCode ()));
      }
    }
    final TradePriceType aGross = aLTA.getGrossPriceProductTradePrice ();
    if (aGross != null)
    {
      PhinvoiceReaderHelper.setAndAddRepeated (_getAmounts (aGross.getChargeAmount ()), "BT-148", aOut::setGrossPrice, aTerms);
      // BT-147 is the amount of the first allowance; the others and the charges are Factur-X EXTENDED
      for (final TradeAllowanceChargeType aAC : aGross.getAppliedTradeAllowanceCharge ())
      {
        final boolean bCharge = aAC.getChargeIndicator () != null &&
                                Boolean.TRUE.equals (aAC.getChargeIndicator ().isIndicator ());
        final PhinvoicePriceAllowanceCharge aPAC = new PhinvoicePriceAllowanceCharge ();
        PhinvoiceReaderHelper.setAndAddRepeated (_getAmounts (aAC.getActualAmount ()),
                                                 bCharge ? TERM_PRICE_CHARGE : "BT-147",
                                                 aPAC::setAmount,
                                                 aTerms);
        (bCharge ? aOut.charges () : aOut.allowances ()).add (aPAC);
      }
      // BT-149 / BT-150 — since EN 16931:2026 the base quantity may sit on the gross price as well
      if (aGross.getBasisQuantity () != null)
      {
        final QuantityType aGrossQuantity = aGross.getBasisQuantity ();
        if (aOut.getBaseQuantity () == null)
        {
          aOut.setBaseQuantity (aGrossQuantity.getValue ());
          aOut.setBaseQuantityUnit (str (aGrossQuantity.getUnitCode ()));
        }
        else
          if (aGrossQuantity.getValue () != null &&
              (aOut.getBaseQuantity ().compareTo (aGrossQuantity.getValue ()) != 0 ||
               !EqualsHelper.equals (aOut.getBaseQuantityUnit (), str (aGrossQuantity.getUnitCode ()))))
            aTerms.add (new PhinvoiceAdditionalTerm ("BT-149",
                                                 aGrossQuantity.getValue ().toPlainString () +
                                                           " " +
                                                           StringHelper.getNotNull (str (aGrossQuantity.getUnitCode ()))));
      }
    }
    return aOut;
  }

  @Nullable
  private static String _codeValue (@Nullable final AllowanceChargeReasonCodeType aCode)
  {
    return aCode == null ? null : str (aCode.getValue ());
  }

  @Nullable
  private static String _codeValue (@Nullable final CurrencyCodeType aCode)
  {
    return aCode == null ? null : str (aCode.getValue ());
  }

  @Nullable
  private static String _codeValue (@Nullable final DocumentCodeType aCode)
  {
    return aCode == null ? null : str (aCode.getValue ());
  }

  @Nullable
  private static String _codeValue (@Nullable final PaymentMeansCodeType aCode)
  {
    return aCode == null ? null : str (aCode.getValue ());
  }

  @Nullable
  private static String _codeValue (@Nullable final ReferenceCodeType aCode)
  {
    return aCode == null ? null : str (aCode.getValue ());
  }

  @Nullable
  private static String _codeValue (@Nullable final TaxCategoryCodeType aCode)
  {
    return aCode == null ? null : str (aCode.getValue ());
  }

  @Nullable
  private static String _codeValue (@Nullable final TimeReferenceCodeType aCode)
  {
    return aCode == null ? null : str (aCode.getValue ());
  }

  @Nullable
  private static String _codeValue (@Nullable final CodeType aCode)
  {
    return aCode == null ? null : str (aCode.getValue ());
  }

  @Nullable
  private static String _idValue (@Nullable final IDType aID)
  {
    return aID == null ? null : str (aID.getValue ());
  }

  @Nullable
  private static PhinvoiceIdentifier _identifier (@Nullable final IDType aID)
  {
    return aID == null ? null : new PhinvoiceIdentifier (_idValue (aID), str (aID.getSchemeID ()));
  }

  private static void _addIdentifier (@Nullable final IDType aID, @NonNull final List <PhinvoiceIdentifier> aTarget)
  {
    if (_idValue (aID) != null)
      aTarget.add (_identifier (aID));
  }

  /**
   * Read the party identifiers (BT-29, BT-46, BT-60, BT-71 and their scheme identifiers).
   */
  private static void _readPartyIdentifiers (@NonNull final TradePartyType aTP,
                                             @NonNull final List <PhinvoiceIdentifier> aTarget)
  {
    for (final IDType aID : aTP.getGlobalID ())
      _addIdentifier (aID, aTarget);
    for (final IDType aID : aTP.getID ())
      _addIdentifier (aID, aTarget);
  }

  @Nullable
  private static String _text (@Nullable final TextType aT)
  {
    return aT == null ? null : str (aT.getValue ());
  }

  /**
   * Join the non-blank values of a list of {@link TextType} elements with newlines. Returns
   * {@code null} when the list is empty or contains no usable values.
   */
  @Nullable
  private static String _joinTexts (@Nullable final List <TextType> aTexts)
  {
    if (aTexts == null || aTexts.isEmpty ())
      return null;

    final StringBuilder aSB = new StringBuilder ();
    for (final TextType aText : aTexts)
    {
      final String sVal = _text (aText);
      if (sVal == null)
        continue;
      if (aSB.length () > 0)
        aSB.append ('\n');
      aSB.append (sVal);
    }
    return aSB.length () == 0 ? null : aSB.toString ();
  }

  /** Get the VAT identifiers of a party (BT-31, BT-48, BT-63). */
  @NonNull
  private static ICommonsList <String> _getVatIdentifiers (@NonNull final TradePartyType aTP)
  {
    final ICommonsList <String> ret = new CommonsArrayList <> ();
    for (final TaxRegistrationType aTR : aTP.getSpecifiedTaxRegistration ())
      if (aTR.getID () != null && TAX_SCHEME_VAT.equalsIgnoreCase (str (aTR.getID ().getSchemeID ())))
        ret.add (_idValue (aTR.getID ()));
    return ret;
  }

  /**
   * Get the tax registrations of a party that are no VAT identifiers (BT-32 for the seller). The
   * scheme "FC" is the default, special schemes from ISO/IEC 6523 may be used as well.
   */
  @NonNull
  private static ICommonsList <PhinvoiceIdentifier> _getTaxRegistrations (@NonNull final TradePartyType aTP)
  {
    final ICommonsList <PhinvoiceIdentifier> ret = new CommonsArrayList <> ();
    for (final TaxRegistrationType aTR : aTP.getSpecifiedTaxRegistration ())
      if (aTR.getID () != null && !TAX_SCHEME_VAT.equalsIgnoreCase (str (aTR.getID ().getSchemeID ())))
        _addIdentifier (aTR.getID (), ret);
    return ret;
  }

  @NonNull
  private static PhinvoicePayee _readPayee (@NonNull final TradePartyType aTP, @NonNull final PhinvoiceInvoice aModel)
  {
    final PhinvoicePayee aOut = new PhinvoicePayee ();
    aOut.setName (_text (aTP.getName ()));
    final ICommonsList <PhinvoiceIdentifier> aIDs = new CommonsArrayList <> ();
    _readPartyIdentifiers (aTP, aIDs);
    PhinvoiceReaderHelper.setAndAddRepeated (aIDs, "BT-60", aOut::setIdentifier, aModel.additionalTerms ());
    final LegalOrganizationType aLO = aTP.getSpecifiedLegalOrganization ();
    if (aLO != null)
    {
      if (aLO.getID () != null)
        aOut.setLegalRegistration (_identifier (aLO.getID ()));
      // EN 16931 has no trading name of the payee, but it must not get lost
      PhinvoiceReaderHelper.addIfPresent ("BT-59", _text (aLO.getTradingBusinessName ()), aModel.additionalTerms ());
    }
    return aOut;
  }

  @NonNull
  private static PhinvoicePostalAddress _readAddress (@NonNull final TradeAddressType aAddr)
  {
    final PhinvoicePostalAddress aOut = new PhinvoicePostalAddress ();
    aOut.setLine1 (_text (aAddr.getLineOne ()));
    aOut.setLine2 (_text (aAddr.getLineTwo ()));
    aOut.setLine3 (_text (aAddr.getLineThree ()));
    aOut.setCity (_text (aAddr.getCityName ()));
    aOut.setPostCode (_codeValue (aAddr.getPostcodeCode ()));
    if (aAddr.getCountryID () != null)
      aOut.setCountryCode (str (aAddr.getCountryID ().getValue ()));
    aOut.setCountrySubdivision (_joinTexts (aAddr.getCountrySubDivisionName ()));
    return aOut;
  }

  @NonNull
  private static PhinvoiceSellerTaxRepresentative _readTaxRep (@NonNull final TradePartyType aTP,
                                                           @NonNull final PhinvoiceInvoice aModel)
  {
    final PhinvoiceSellerTaxRepresentative aOut = new PhinvoiceSellerTaxRepresentative ();
    final ICommonsList <PhinvoiceAdditionalTerm> aTerms = aModel.additionalTerms ();
    aOut.setName (_text (aTP.getName ()));
    PhinvoiceReaderHelper.setAndAddRepeated (_getVatIdentifiers (aTP), "BT-63", aOut::setVatIdentifier, aTerms);
    for (final PhinvoiceIdentifier aID : _getTaxRegistrations (aTP))
      aTerms.add (new PhinvoiceAdditionalTerm (TERM_TAX_REGISTRATION, aID));
    if (aTP.getPostalTradeAddress () != null)
      aOut.setPostalAddress (_readAddress (aTP.getPostalTradeAddress ()));
    return aOut;
  }

  @NonNull
  private static PhinvoiceContact _readContact (@NonNull final TradeContactType aTC)
  {
    final PhinvoiceContact aOut = new PhinvoiceContact ();
    // BT-41 / BT-56 — PersonName and DepartmentName
    final String sPersonName = _text (aTC.getPersonName ());
    final String sDepartmentName = _text (aTC.getDepartmentName ());
    aOut.setName (sPersonName);
    aOut.setDepartmentName (sDepartmentName);
    if (aTC.getTelephoneUniversalCommunication () != null)
      aOut.setTelephone (_text (aTC.getTelephoneUniversalCommunication ().getCompleteNumber ()));
    if (aTC.getEmailURIUniversalCommunication () != null &&
        aTC.getEmailURIUniversalCommunication ().getURIID () != null)
      aOut.setEmail (str (aTC.getEmailURIUniversalCommunication ().getURIID ().getValue ()));
    // Factur-X EXTENDED
    if (aTC.getFaxUniversalCommunication () != null)
      aOut.setFax (_text (aTC.getFaxUniversalCommunication ().getCompleteNumber ()));
    if (aTC.getTypeCode () != null)
      aOut.setTypeCode (str (aTC.getTypeCode ().getValue ()));
    return aOut;
  }

  /**
   * Get the electronic addresses of a party (BT-34, BT-49 with the scheme identifiers).
   */
  @NonNull
  private static ICommonsList <PhinvoiceIdentifier> _getElectronicAddresses (@NonNull final TradePartyType aTP)
  {
    final ICommonsList <PhinvoiceIdentifier> ret = new CommonsArrayList <> ();
    for (final UniversalCommunicationType aUC : aTP.getURIUniversalCommunication ())
      if (aUC.getURIID () != null)
        ret.add (_identifier (aUC.getURIID ()));
    return ret;
  }

  private static void _readContacts (@NonNull final TradePartyType aTP,
                                     @NonNull final Consumer <PhinvoiceContact> aSetter,
                                     @NonNull final String sNameTerm,
                                     @NonNull final String sTelephoneTerm,
                                     @NonNull final String sEmailTerm,
                                     @NonNull final List <PhinvoiceAdditionalTerm> aTerms)
  {
    boolean bFirst = true;
    for (final TradeContactType aTC : aTP.getDefinedTradeContact ())
    {
      final PhinvoiceContact aContact = _readContact (aTC);
      if (bFirst)
        aSetter.accept (aContact);
      else
        PhinvoiceReaderHelper.addContact (aContact, sNameTerm, sTelephoneTerm, sEmailTerm, aTerms);
      bFirst = false;
    }
  }

  private static void _readSeller (@NonNull final TradePartyType aTP, @NonNull final PhinvoiceSeller aSeller)
  {
    final ICommonsList <PhinvoiceAdditionalTerm> aTerms = aSeller.additionalTerms ();
    aSeller.setName (_text (aTP.getName ())); // BT-27
    final LegalOrganizationType aLO = aTP.getSpecifiedLegalOrganization ();
    if (aLO != null)
    {
      aSeller.setTradingName (_text (aLO.getTradingBusinessName ())); // BT-28
      if (aLO.getID () != null)
        aSeller.setLegalRegistration (_identifier (aLO.getID ()));
    }
    // BT-29 / BT-29-1
    _readPartyIdentifiers (aTP, aSeller.identifiers ());
    // BT-31 / BT-32 / BT-32-1 via SpecifiedTaxRegistration schemeID
    PhinvoiceReaderHelper.setAndAddRepeated (_getVatIdentifiers (aTP), "BT-31", aSeller::setVatIdentifier, aTerms);
    PhinvoiceReaderHelper.setAndAddRepeated (_getTaxRegistrations (aTP),
                                         "BT-32",
                                         aID -> aSeller.setTaxRegistration (aID.getValue ())
                                                       .setTaxRegistrationSchemeID (aID.getSchemeID ()),
                                         aTerms);
    // BT-33
    aSeller.setAdditionalLegalInfo (_joinTexts (aTP.getDescription ()));
    // BT-34 / BT-34-1
    PhinvoiceReaderHelper.setAndAddRepeated (_getElectronicAddresses (aTP),
                                         "BT-34",
                                         aSeller::setElectronicAddress,
                                         aTerms);
    // BG-5
    if (aTP.getPostalTradeAddress () != null)
      aSeller.setPostalAddress (_readAddress (aTP.getPostalTradeAddress ()));
    // BG-6
    _readContacts (aTP, aSeller::setContact, "BT-41", "BT-42", "BT-43", aTerms);
  }

  private static void _readBuyer (@NonNull final TradePartyType aTP, @NonNull final PhinvoiceBuyer aBuyer)
  {
    final ICommonsList <PhinvoiceAdditionalTerm> aTerms = aBuyer.additionalTerms ();
    aBuyer.setName (_text (aTP.getName ())); // BT-44
    final LegalOrganizationType aLO = aTP.getSpecifiedLegalOrganization ();
    if (aLO != null)
    {
      aBuyer.setTradingName (_text (aLO.getTradingBusinessName ()));
      if (aLO.getID () != null)
        aBuyer.setLegalRegistration (_identifier (aLO.getID ()));
    }
    _readPartyIdentifiers (aTP, aBuyer.identifiers ());
    PhinvoiceReaderHelper.setAndAddRepeated (_getVatIdentifiers (aTP), "BT-48", aBuyer::setVatIdentifier, aTerms);
    // EN 16931 has no other tax registration of the buyer, but it must not get lost
    for (final PhinvoiceIdentifier aID : _getTaxRegistrations (aTP))
      aTerms.add (new PhinvoiceAdditionalTerm (TERM_TAX_REGISTRATION, aID));
    PhinvoiceReaderHelper.setAndAddRepeated (_getElectronicAddresses (aTP),
                                         "BT-49",
                                         aBuyer::setElectronicAddress,
                                         aTerms);
    if (aTP.getPostalTradeAddress () != null)
      aBuyer.setPostalAddress (_readAddress (aTP.getPostalTradeAddress ()));
    _readContacts (aTP, aBuyer::setContact, "BT-56", "BT-57", "BT-58", aTerms);
  }

  /** BT-95 / BT-102 and their rates are 0..1, the other tax categories are shown as well. */
  private static void _addFurtherTaxCategories (@NonNull final List <TradeTaxType> aTTs,
                                                @NonNull final String sCategoryTerm,
                                                @NonNull final String sRateTerm,
                                                @NonNull final List <PhinvoiceAdditionalTerm> aTerms)
  {
    for (int i = 1; i < aTTs.size (); ++i)
    {
      final TradeTaxType aTT = aTTs.get (i);
      PhinvoiceReaderHelper.addIfPresent (sCategoryTerm, _codeValue (aTT.getCategoryCode ()), aTerms);
      if (aTT.getRateApplicablePercent () != null)
        aTerms.add (new PhinvoiceAdditionalTerm (sRateTerm, aTT.getRateApplicablePercent ().getValue ().toPlainString ()));
    }
  }

  @NonNull
  private static PhinvoiceDocumentAllowance _readDocAllowance (@NonNull final TradeAllowanceChargeType aAC,
                                                           @NonNull final List <PhinvoiceAdditionalTerm> aTerms)
  {
    final PhinvoiceDocumentAllowance aOut = new PhinvoiceDocumentAllowance ();
    PhinvoiceReaderHelper.setAndAddRepeated (_getAmounts (aAC.getActualAmount ()), "BT-92", aOut::setAmount, aTerms);
    if (aAC.getBasisAmount () != null)
      aOut.setBaseAmount (aAC.getBasisAmount ().getValue ());
    if (aAC.getCalculationPercent () != null)
      aOut.setPercentage (aAC.getCalculationPercent ().getValue ());
    aOut.setReason (_text (aAC.getReason ()));
    aOut.setReasonCode (_codeValue (aAC.getReasonCode ()));
    if (!aAC.getCategoryTradeTax ().isEmpty ())
    {
      final TradeTaxType aTT = aAC.getCategoryTradeTax ().get (0);
      aOut.setVatCategoryCode (_codeValue (aTT.getCategoryCode ()));
      if (aTT.getRateApplicablePercent () != null)
        aOut.setVatRate (aTT.getRateApplicablePercent ().getValue ());
      aOut.setExemptionReasonText (_text (aTT.getExemptionReason ())); // BT-173
      aOut.setExemptionReasonCode (_codeValue (aTT.getExemptionReasonCode ())); // BT-174
    }
    _addFurtherTaxCategories (aAC.getCategoryTradeTax (), "BT-95", "BT-96", aTerms);
    return aOut;
  }

  @NonNull
  private static PhinvoiceDocumentCharge _readDocCharge (@NonNull final TradeAllowanceChargeType aAC,
                                                     @NonNull final List <PhinvoiceAdditionalTerm> aTerms)
  {
    final PhinvoiceDocumentCharge aOut = new PhinvoiceDocumentCharge ();
    PhinvoiceReaderHelper.setAndAddRepeated (_getAmounts (aAC.getActualAmount ()), "BT-99", aOut::setAmount, aTerms);
    if (aAC.getBasisAmount () != null)
      aOut.setBaseAmount (aAC.getBasisAmount ().getValue ());
    if (aAC.getCalculationPercent () != null)
      aOut.setPercentage (aAC.getCalculationPercent ().getValue ());
    aOut.setReason (_text (aAC.getReason ()));
    // BT-105 and BT-177 share ram:ReasonCode and are told apart by @listID
    final String sReasonCode = _codeValue (aAC.getReasonCode ());
    if (_isNonVatTaxCodeList (aAC.getReasonCode ()))
    {
      aOut.setNonVatTaxCode (sReasonCode);
      aOut.setNonVatTaxCodeListID (aAC.getReasonCode ().getListID ());
    }
    else
      aOut.setReasonCode (sReasonCode);
    if (!aAC.getCategoryTradeTax ().isEmpty ())
    {
      final TradeTaxType aTT = aAC.getCategoryTradeTax ().get (0);
      aOut.setVatCategoryCode (_codeValue (aTT.getCategoryCode ()));
      if (aTT.getRateApplicablePercent () != null)
        aOut.setVatRate (aTT.getRateApplicablePercent ().getValue ());
      aOut.setExemptionReasonText (_text (aTT.getExemptionReason ())); // BT-175
      aOut.setExemptionReasonCode (_codeValue (aTT.getExemptionReasonCode ())); // BT-176
    }
    _addFurtherTaxCategories (aAC.getCategoryTradeTax (), "BT-102", "BT-103", aTerms);
    return aOut;
  }

  @NonNull
  private static PhinvoiceSupportingDocument _readSupportingDocument (@NonNull final ReferencedDocumentType aRD,
                                                                  @NonNull final List <PhinvoiceAdditionalTerm> aTerms)
  {
    final PhinvoiceSupportingDocument aOut = new PhinvoiceSupportingDocument ();
    aOut.setReference (_idValue (aRD.getIssuerAssignedID ()));
    // BT-122-1 — the binding only knows the fixed value 916
    final String sTypeCode = _codeValue (aRD.getTypeCode ());
    if (!EN16931CodeLists.DOCUMENT_TYPE_CODE_SUPPORTING_DOCUMENT.equals (sTypeCode))
      aOut.setDocumentTypeCode (sTypeCode);
    aOut.setDescription (_joinTexts (aRD.getName ()));
    if (aRD.getURIID () != null)
      aOut.setExternalLocation (str (aRD.getURIID ().getValue ()));
    boolean bFirst = true;
    for (final BinaryObjectType aBinary : aRD.getAttachmentBinaryObject ())
    {
      final PhinvoiceAttachment aBin = new PhinvoiceAttachment ();
      aBin.setBinary (aBinary.getValue ());
      aBin.setMimeCode (str (aBinary.getMimeCode ()));
      aBin.setFilename (str (aBinary.getFilename ()));
      if (bFirst)
        aOut.setAttachment (aBin);
      else
      {
        // BT-125 is 0..1: the content of a further attachment cannot be embedded
        PhinvoiceReaderHelper.addIfPresent ("BT-125-2", aBin.getFilename (), aTerms);
        PhinvoiceReaderHelper.addIfPresent ("BT-125-1", aBin.getMimeCode (), aTerms);
      }
      bFirst = false;
    }
    return aOut;
  }

  @NonNull
  private static PhinvoiceItem _readItem (@NonNull final TradeProductType aP)
  {
    final PhinvoiceItem aOut = new PhinvoiceItem ();
    aOut.setName (_joinTexts (aP.getName ()));
    aOut.setDescription (_joinTexts (aP.getDescription ()));
    if (aP.getSellerAssignedID () != null)
      aOut.setSellerItemID (str (aP.getSellerAssignedID ().getValue ()));
    if (aP.getBuyerAssignedID () != null)
      aOut.setBuyerItemID (str (aP.getBuyerAssignedID ().getValue ()));
    if (aP.getGlobalID () != null)
      aOut.setStandardItemID (_identifier (aP.getGlobalID ()));
    if (aP.getOriginTradeCountry () != null && aP.getOriginTradeCountry ().getID () != null)
      aOut.setCountryOfOrigin (str (aP.getOriginTradeCountry ().getID ().getValue ()));
    for (final ProductClassificationType aPC : aP.getDesignatedProductClassification ())
      if (aPC.getClassCode () != null && str (aPC.getClassCode ().getValue ()) != null)
      {
        final PhinvoiceItemClassification aIC = new PhinvoiceItemClassification ();
        aIC.setCode (str (aPC.getClassCode ().getValue ()));
        aIC.setListID (str (aPC.getClassCode ().getListID ()));
        aIC.setListVersionID (str (aPC.getClassCode ().getListVersionID ()));
        aOut.classifications ().add (aIC);
      }
    for (final ProductCharacteristicType aPC : aP.getApplicableProductCharacteristic ())
    {
      final PhinvoiceItemAttribute aAttr = new PhinvoiceItemAttribute ();
      aAttr.setName (_joinTexts (aPC.getDescription ())); // BT-160
      aAttr.setCode (_codeValue (aPC.getTypeCode ())); // BT-211
      // BT-161a text and BT-161b numeric value with BT-212 as its unit (CII-SR-504)
      aAttr.setValue (_joinTexts (aPC.getValue ()));
      if (aPC.getValueMeasure () != null)
      {
        aAttr.setNumericValue (aPC.getValueMeasure ().getValue ());
        aAttr.setValueUnit (str (aPC.getValueMeasure ().getUnitCode ()));
      }
      if (aAttr.getName () != null ||
          aAttr.getCode () != null ||
          aAttr.getValue () != null ||
          aAttr.getNumericValue () != null)
        aOut.attributes ().add (aAttr);
    }
    return aOut;
  }

  /**
   * Parse a CII {@code DateTimeType} into a {@link LocalDate}. Reads the {@code @format} attribute
   * on the inner {@code DateTimeString} and dispatches to the right pattern; missing format
   * defaults to "102" (yyyyMMdd) per EN 16931.
   */
  @Nullable
  private static LocalDate _dateTime (@Nullable final DateTimeType aDT,
                                      @NonNull final String sTerm,
                                      @NonNull final List <PhinvoiceAdditionalTerm> aTerms,
                                      @NonNull final ErrorList aErrors)
  {
    if (aDT == null || aDT.getDateTimeString () == null)
      return null;

    return parseFormattedDate (aDT.getDateTimeString ().getValue (),
                               aDT.getDateTimeString ().getFormat (),
                               sTerm,
                               aTerms,
                               aErrors);
  }

  @NonNull
  private static PhinvoiceInvoicingPeriod _readPeriod (@NonNull final SpecifiedPeriodType aSP,
                                                   @NonNull final String sStartTerm,
                                                   @NonNull final String sEndTerm,
                                                   @NonNull final List <PhinvoiceAdditionalTerm> aTerms,
                                                   @NonNull final ErrorList aErrors)
  {
    final PhinvoiceInvoicingPeriod aOut = new PhinvoiceInvoicingPeriod ();
    aOut.setStartDate (_dateTime (aSP.getStartDateTime (), sStartTerm, aTerms, aErrors));
    aOut.setEndDate (_dateTime (aSP.getEndDateTime (), sEndTerm, aTerms, aErrors));
    return aOut;
  }

  /**
   * Parse a CII {@code DateType} into a {@link LocalDate}. Honours the {@code @format} attribute on
   * the inner {@code DateString}; if {@code DateString} is absent the {@code DateLocal} value is
   * used.
   */
  @Nullable
  private static LocalDate _dateOnly (@Nullable final DateType aDT,
                                      @NonNull final String sTerm,
                                      @NonNull final List <PhinvoiceAdditionalTerm> aTerms,
                                      @NonNull final ErrorList aErrors)
  {
    if (aDT == null)
      return null;

    if (aDT.getDateString () != null)
      return parseFormattedDate (aDT.getDateString ().getValue (), aDT.getDateString ().getFormat (), sTerm, aTerms, aErrors);
    return aDT.getDateLocal ();
  }

  /**
   * Read the combined BT-2 issue date and BT-166 issue time. Format code 208 carries date, time and
   * offset; every other format code only carries the date.
   */
  private static void _readIssueDateTime (@Nullable final DateTimeType aDT,
                                          @NonNull final PhinvoiceInvoice aModel,
                                          @NonNull final ErrorList aErrors)
  {
    if (aDT == null || aDT.getDateTimeString () == null)
      return;

    final DateTimeType.DateTimeString aDTS = aDT.getDateTimeString ();
    if (FORMAT_CODE_DATE_TIME.equals (str (aDTS.getFormat ())))
    {
      final XMLOffsetDateTime aIssueDateTime = EEN16931DateFormatCode.CCYYMMDDHHMMSSZHHMM.parseDateTime (str (aDTS.getValue ()));
      if (aIssueDateTime == null)
      {
        aErrors.add (SingleError.builderWarn ()
                                .errorText ("Could not parse the invoice issue date and time '" +
                                            PhinvoiceLogHelper.sanitise (aDTS.getValue ()) +
                                            "' (BT-2 / BT-166) with format code 208")
                                .build ());
        // A value that cannot be read is shown as it is
        PhinvoiceReaderHelper.addIfPresent ("BT-2", str (aDTS.getValue ()), aModel.additionalTerms ());
        return;
      }
      aModel.setIssueDate (aIssueDateTime.toLocalDate ());
      aModel.setIssueTime (aIssueDateTime.toOffsetTime ());
    }
    else
      aModel.setIssueDate (parseFormattedDate (aDTS.getValue (),
                                               aDTS.getFormat (),
                                               "BT-2",
                                               aModel.additionalTerms (),
                                               aErrors));
  }

  /**
   * Parse a CII {@code FormattedDateTimeType} (qualified namespace) into a {@link LocalDate}.
   * Format codes follow the same UN/EDIFACT 2379 dispatch as {@code _dateTime}.
   */
  @Nullable
  private static LocalDate _formattedDateTime (@Nullable final FormattedDateTimeType aFDT,
                                               @NonNull final String sTerm,
                                               @NonNull final List <PhinvoiceAdditionalTerm> aTerms,
                                               @NonNull final ErrorList aErrors)
  {
    if (aFDT == null || aFDT.getDateTimeString () == null)
      return null;

    return parseFormattedDate (aFDT.getDateTimeString ().getValue (),
                               aFDT.getDateTimeString ().getFormat (),
                               sTerm,
                               aTerms,
                               aErrors);
  }

  /**
   * Read the BG-33 payment terms (BT-20) with the nested BG-35 early payment discount and BG-36
   * late payment penalty. One {@link PhinvoicePaymentTerms} is created per CII {@code
   * SpecifiedTradePaymentTerms} that carries a text, a discount or a penalty.
   */
  /** The index of the payment terms whose due date is the payment due date of the invoice (BT-9). */
  private static int _getDueDateIndex (@NonNull final List <TradePaymentTermsType> aTerms)
  {
    for (int i = 0; i < aTerms.size (); ++i)
      if (aTerms.get (i).getDueDateDateTime () != null)
        return i;
    return -1;
  }

  /**
   * Is the payment terms a payment terms of the model? The due date counts only if it is not the
   * payment due date of the invoice.
   */
  private static boolean _isModelPaymentTerms (@NonNull final TradePaymentTermsType aPT, final boolean bOwnDueDate)
  {
    return _joinTexts (aPT.getDescription ()) != null ||
           aPT.getApplicableTradePaymentDiscountTerms () != null ||
           aPT.getApplicableTradePaymentPenaltyTerms () != null ||
           !aPT.getPartialPaymentAmount ().isEmpty () ||
           !aPT.getPayeeTradeParty ().isEmpty () ||
           (bOwnDueDate && aPT.getDueDateDateTime () != null);
  }

  private static void _readPaymentTerms (@NonNull final List <TradePaymentTermsType> aTerms,
                                         @NonNull final PhinvoiceInvoice aModel,
                                         @NonNull final ErrorList aErrors)
  {
    final int nDueDateIndex = _getDueDateIndex (aTerms);
    for (int nIndex = 0; nIndex < aTerms.size (); ++nIndex)
    {
      final TradePaymentTermsType aPT = aTerms.get (nIndex);
      if (!_isModelPaymentTerms (aPT, nIndex != nDueDateIndex))
        continue;
      final PhinvoicePaymentTerms aOut = new PhinvoicePaymentTerms ();
      aOut.setText (_joinTexts (aPT.getDescription ())); // BT-20
      if (nIndex != nDueDateIndex)
        aOut.setDueDate (_dateTime (aPT.getDueDateDateTime (), "BT-9", aModel.additionalTerms (), aErrors)); // BT-9 of Factur-X EXTENDED

      final TradePaymentDiscountTermsType aDiscount = aPT.getApplicableTradePaymentDiscountTerms ();
      if (aDiscount != null)
      {
        final PhinvoiceEarlyPaymentDiscount aED = new PhinvoiceEarlyPaymentDiscount ();
        aED.setEndDate (_dateTime (aDiscount.getBasisDateTime (), "BT-170", aModel.additionalTerms (), aErrors)); // BT-170
        if (aDiscount.getCalculationPercent () != null)
          aED.setPercentage (aDiscount.getCalculationPercent ().getValue ()); // BT-171
        if (aDiscount.getActualDiscountAmount () != null)
          aED.setAmount (aDiscount.getActualDiscountAmount ().getValue ()); // BT-172
        if (aED.getEndDate () != null || aED.getPercentage () != null || aED.getAmount () != null)
          aOut.earlyDiscounts ().add (aED);
      }

      final TradePaymentPenaltyTermsType aPenalty = aPT.getApplicableTradePaymentPenaltyTerms ();
      if (aPenalty != null)
      {
        final PhinvoiceLatePaymentPenalty aLP = new PhinvoiceLatePaymentPenalty ();
        aLP.setStartDate (_dateTime (aPenalty.getBasisDateTime (), "BT-181", aModel.additionalTerms (), aErrors)); // BT-181
        if (aPenalty.getCalculationPercent () != null)
          aLP.setYearlyInterestPercentage (aPenalty.getCalculationPercent ().getValue ()); // BT-182
        if (aPenalty.getActualPenaltyAmount () != null)
          aLP.setAmount (aPenalty.getActualPenaltyAmount ().getValue ()); // BT-183
        if (aLP.getStartDate () != null || aLP.getYearlyInterestPercentage () != null || aLP.getAmount () != null)
          aOut.latePenalties ().add (aLP);
      }

      aModel.paymentTerms ().add (aOut);
    }
  }

  /** Get the identifiers of an account: IBANID if applicable and ProprietaryID, each 0..1 */
  @NonNull
  private static ICommonsList <String> _getAccountIDs (@Nullable final IDType aIBANID, @Nullable final IDType aProprietaryID)
  {
    final ICommonsList <String> ret = new CommonsArrayList <> ();
    ret.add (_idValue (aIBANID));
    ret.add (_idValue (aProprietaryID));
    return ret;
  }

  private static void _readPaymentInstructions (@NonNull final HeaderTradeSettlementType aHTS,
                                                @NonNull final PhinvoiceInvoice aModel,
                                                @NonNull final ErrorList aErrors)
  {
    final List <TradeSettlementPaymentMeansType> aMeans = aHTS.getSpecifiedTradeSettlementPaymentMeans ();
    final List <TradePaymentTermsType> aTerms = aHTS.getSpecifiedTradePaymentTerms ();

    // BG-33 / BG-35 / BG-36
    _readPaymentTerms (aTerms, aModel, aErrors);

    // BT-9 — payment due date; the due dates of the other payment terms are kept there
    final int nDueDateIndex = _getDueDateIndex (aTerms);
    if (nDueDateIndex >= 0)
      aModel.setPaymentDueDate (_dateTime (aTerms.get (nDueDateIndex).getDueDateDateTime (),
                                           "BT-9",
                                           aModel.additionalTerms (),
                                           aErrors));

    final String sCreditorID = _idValue (aHTS.getCreditorReferenceID ());
    if (aMeans.isEmpty () && aTerms.isEmpty () && aHTS.getPaymentReference ().isEmpty () && sCreditorID == null)
      return;

    final PhinvoicePaymentInstructions aPI = new PhinvoicePaymentInstructions ();
    final ICommonsList <PhinvoiceAdditionalTerm> aPITerms = aPI.additionalTerms ();
    // BT-83 — remittance information lives on the parent. Multiple references are joined
    // with newlines so multi-part instructions (e.g. invoice number + PO reference) survive.
    aPI.setRemittanceInformation (_joinTexts (aHTS.getPaymentReference ()));

    // BG-17 / BG-18 may sit in any of the payment means, not only in the first one
    boolean bDirectDebitMeans = false;
    boolean bFirst = true;
    for (final TradeSettlementPaymentMeansType aM : aMeans)
    {
      final String sCode = _codeValue (aM.getTypeCode ());
      final String sText = _joinTexts (aM.getInformation ());
      if (bFirst)
      {
        aPI.setPaymentMeansCode (sCode);
        aPI.setPaymentMeansText (sText);
      }
      else
      {
        // BT-81 and BT-82 are 0..1, but each payment means of the document has them
        if (sCode != null)
          aPITerms.add (new PhinvoiceAdditionalTerm ("BT-81", sCode, "paymentmeans"));
        PhinvoiceReaderHelper.addIfPresent ("BT-82", sText, aPITerms);
      }
      bFirst = false;
      if (EN16931CodeLists.isPaymentMeansCodeDirectDebit (sCode))
        bDirectDebitMeans = true;

      // BG-17 credit transfers across all payment means
        for (final CreditorFinancialAccountType aFA : aM.getPayeePartyCreditorFinancialAccount ())
        {
          final PhinvoiceCreditTransfer aCT = new PhinvoiceCreditTransfer ();
          aCT.setPaymentMeansCode (sCode);
          // BT-84 — IBANID if applicable, ProprietaryID else
          PhinvoiceReaderHelper.setAndAddRepeated (_getAccountIDs (aFA.getIBANID (), aFA.getProprietaryID ()),
                                               "BT-84",
                                               aCT::setAccountIdentifier,
                                               aPITerms);
          aCT.setAccountName (_text (aFA.getAccountName ()));
          final CreditorFinancialInstitutionType aFI = aM.getPayeeSpecifiedCreditorFinancialInstitution ();
          if (aFI != null)
            aCT.setServiceProviderID (_idValue (aFI.getBICID ()));
          if (!aCT.isEmpty ())
            aPI.creditTransfers ().add (aCT);
        }

      // BG-18 — card
      if (aM.getApplicableTradeSettlementFinancialCard () != null)
      {
        final PhinvoicePaymentCard aPC = new PhinvoicePaymentCard ();
        aPC.setPan (_idValue (aM.getApplicableTradeSettlementFinancialCard ().getID ()));
        aPC.setHolderName (_text (aM.getApplicableTradeSettlementFinancialCard ().getCardholderName ()));
        if (aPI.getPaymentCard () == null)
          aPI.setPaymentCard (aPC);
        else
          PhinvoiceReaderHelper.addPaymentCard (aPC, aPITerms);
      }
    }

    // BG-19 + BT-89 .. BT-91 + BT-215 + BT-216. BT-89 is optional, so the direct debit is also
    // recognised by the payer account or, for the creditor reference, by a direct debit payment
    // means
    final PhinvoiceDirectDebit aDD = new PhinvoiceDirectDebit ();
    boolean bDirectDebit = false;
    final ICommonsList <String> aMandateIDs = new CommonsArrayList <> ();
    for (final TradePaymentTermsType aPT : aTerms)
      for (final IDType aMandateID : aPT.getDirectDebitMandateID ())
      {
        aMandateIDs.add (_idValue (aMandateID));
        bDirectDebit = true;
      }
    PhinvoiceReaderHelper.setAndAddRepeated (aMandateIDs, "BT-89", aDD::setMandateReferenceID, aPITerms);
    // BT-91 / BT-216 from the payer financial account, BT-215 from the payer institution
    boolean bFirstPayer = true;
    for (final TradeSettlementPaymentMeansType aM : aMeans)
    {
      final DebtorFinancialAccountType aFA = aM.getPayerPartyDebtorFinancialAccount ();
      final DebtorFinancialInstitutionType aFI = aM.getPayerSpecifiedDebtorFinancialInstitution ();
      if (aFA != null || aFI != null)
      {
        final PhinvoiceDirectDebit aPayer = bFirstPayer ? aDD : new PhinvoiceDirectDebit ();
        if (aFA != null)
        {
          // IBANID if applicable, ProprietaryID else
          PhinvoiceReaderHelper.setAndAddRepeated (_getAccountIDs (aFA.getIBANID (), aFA.getProprietaryID ()),
                                               "BT-91",
                                               aPayer::setDebitedAccountID,
                                               aPITerms);
          aPayer.setDebitedAccountName (_text (aFA.getAccountName ()));
        }
        if (aFI != null)
          aPayer.setDebitedAccountProviderID (_idValue (aFI.getBICID ()));
        if (!bFirstPayer)
          PhinvoiceReaderHelper.addDirectDebit (aPayer, aPITerms);
        bFirstPayer = false;
        bDirectDebit = true;
      }
    }
    if (bDirectDebit)
      aPI.setDirectDebit (aDD);
    // BT-90 — creditor reference id
    PhinvoiceReaderHelper.addCreditorIDs (new CommonsArrayList <> (sCreditorID), bDirectDebitMeans, aPI);

    if (aPI.getPaymentMeansCode () != null ||
        aPI.getPaymentMeansText () != null ||
        aPI.getRemittanceInformation () != null ||
        !aPI.creditTransfers ().isEmpty () ||
        aPI.getPaymentCard () != null ||
        aPI.getDirectDebit () != null ||
        !aPITerms.isEmpty ())
      aModel.setPaymentInstructions (aPI);
  }

  @NonNull
  private static PhinvoiceLineDelivery _readLineDelivery (@NonNull final LineTradeDeliveryType aLTD,
                                                      @NonNull final PhinvoiceInvoiceLine aLine,
                                                      @NonNull final ErrorList aErrors)
  {
    final PhinvoiceLineDelivery aOut = new PhinvoiceLineDelivery ();
    final TradePartyType aShipTo = aLTD.getShipToTradeParty ();
    if (aShipTo != null)
    {
      aOut.setPartyName (_text (aShipTo.getName ())); // BT-185
      // BT-186 / BT-186-1 is the global identifier; the identifier is Factur-X EXTENDED (BT-186-00)
      final ICommonsList <PhinvoiceIdentifier> aIDs = new CommonsArrayList <> ();
      for (final IDType aID : aShipTo.getGlobalID ())
        _addIdentifier (aID, aIDs);
      PhinvoiceReaderHelper.setAndAddRepeated (aIDs, "BT-186", aOut::setLocationIdentifier, aLine.additionalTerms ());
      PhinvoiceReaderHelper.setAndAddRepeated (new CommonsArrayList <> (aShipTo.getID (), CiiD22BInvoiceReader::_idValue),
                                               "BT-186-00",
                                               aOut::setPartyID,
                                               aLine.additionalTerms ());
      if (aShipTo.getPostalTradeAddress () != null)
        aOut.setPostalAddress (_readAddress (aShipTo.getPostalTradeAddress ())); // BG-38
    }
    // BT-187
    if (aLTD.getActualDeliverySupplyChainEvent () != null)
      aOut.setActualDeliveryDate (_dateTime (aLTD.getActualDeliverySupplyChainEvent ().getOccurrenceDateTime (),
                                             "BT-187",
                                             aLine.additionalTerms (),
                                             aErrors));
    return aOut;
  }

  private static void _readLineVat (@NonNull final List <TradeTaxType> aTTs, @NonNull final PhinvoiceInvoiceLine aOut)
  {
    // BG-30 — line VAT. BT-151 and the following are 0..1, the other taxes are shown as well
    final ICommonsList <PhinvoiceAdditionalTerm> aTerms = aOut.additionalTerms ();
    for (final TradeTaxType aTT : aTTs)
    {
      final PhinvoiceLineVat aVat = new PhinvoiceLineVat ();
      aVat.setCategoryCode (_codeValue (aTT.getCategoryCode ()));
      if (aTT.getRateApplicablePercent () != null)
        aVat.setRate (aTT.getRateApplicablePercent ().getValue ());
      aVat.setExemptionReasonText (_text (aTT.getExemptionReason ())); // BT-194
      aVat.setExemptionReasonCode (_codeValue (aTT.getExemptionReasonCode ())); // BT-195
      if (aOut.getLineVat () == null)
      {
        aOut.setLineVat (aVat);
      }
      else
      {
        PhinvoiceReaderHelper.addIfPresent ("BT-151", aVat.getCategoryCode (), aTerms);
        PhinvoiceReaderHelper.addIfPresent ("BT-152", aVat.getRate () == null ? null : aVat.getRate ().toPlainString (), aTerms);
        PhinvoiceReaderHelper.addIfPresent ("BT-194", aVat.getExemptionReasonText (), aTerms);
        PhinvoiceReaderHelper.addIfPresent ("BT-195", aVat.getExemptionReasonCode (), aTerms);
      }
    }
  }

  /** A document reference of a line that is no BT-128: the identifier, its scheme and the type code. */
  @NonNull
  private static String _getLineDocumentText (@NonNull final IDType aID, @Nullable final String sTypeCode)
  {
    final String sScheme = str (aID.getSchemeID ());
    return (sScheme == null ? "" : sScheme + ":") +
           StringHelper.getNotNull (_idValue (aID)) +
           (sTypeCode == null ? "" : " (" + sTypeCode + ")");
  }

  private static void _readLineDocumentReferences (@NonNull final List <ReferencedDocumentType> aRDs,
                                                   @NonNull final PhinvoiceInvoiceLine aOut)
  {
    // BT-128 — line object identifier. The other documents have no term, and show their identifier
    // and their type code
    final ICommonsList <PhinvoiceIdentifier> aObjectIDs = new CommonsArrayList <> ();
    for (final ReferencedDocumentType aRD : aRDs)
      if (aRD.getIssuerAssignedID () != null)
      {
        final String sTypeCode = _codeValue (aRD.getTypeCode ());
        final String sScheme = _codeValue (aRD.getReferenceTypeCode ());
        if ("130".equals (sTypeCode))
          aObjectIDs.add (new PhinvoiceIdentifier (_idValue (aRD.getIssuerAssignedID ()),
                                               sScheme != null ? sScheme : str (aRD.getIssuerAssignedID ().getSchemeID ())));
        else
          aOut.additionalTerms ()
              .add (new PhinvoiceAdditionalTerm (TERM_LINE_DOCUMENT,
                                             _getLineDocumentText (aRD.getIssuerAssignedID (), sTypeCode)));
      }
    PhinvoiceReaderHelper.setAndAddRepeated (aObjectIDs, "BT-128", aOut::setObjectIdentifier, aOut.additionalTerms ());
  }

  @NonNull
  private static PhinvoiceInvoiceLine _readLine (@NonNull final SupplyChainTradeLineItemType aLine,
                                             @NonNull final ErrorList aErrors)
  {
    final PhinvoiceInvoiceLine aOut = new PhinvoiceInvoiceLine ();
    final ICommonsList <PhinvoiceAdditionalTerm> aTerms = aOut.additionalTerms ();
    final DocumentLineDocumentType aLD = aLine.getAssociatedDocumentLineDocument ();
    if (aLD != null)
    {
      aOut.setID (_idValue (aLD.getLineID ()));
      // BT-127 — all notes with all their content
      final StringBuilder aNote = new StringBuilder ();
      for (final NoteType aLineNote : aLD.getIncludedNote ())
      {
        final String sContent = _joinTexts (aLineNote.getContent ());
        if (sContent != null)
          aNote.append (aNote.length () > 0 ? "\n" : "").append (sContent);
      }
      aOut.setNote (str (aNote.toString ()));
    }
    final TradeProductType aTPr = aLine.getSpecifiedTradeProduct ();
    if (aTPr != null)
      aOut.setItem (_readItem (aTPr));
    final LineTradeAgreementType aLTA = aLine.getSpecifiedLineTradeAgreement ();
    if (aLTA != null)
    {
      aOut.setPrice (_readPrice (aLTA, aOut));
      final ReferencedDocumentType aBuyerOrder = aLTA.getBuyerOrderReferencedDocument ();
      if (aBuyerOrder != null)
      {
        aOut.setPurchaseOrderLineRef (_idValue (aBuyerOrder.getLineID ())); // BT-132
        aOut.setPurchaseOrderReference (_idValue (aBuyerOrder.getIssuerAssignedID ())); // BT-188
      }
      final ReferencedDocumentType aSellerOrder = aLTA.getSellerOrderReferencedDocument ();
      if (aSellerOrder != null)
      {
        aOut.setSalesOrderReference (_idValue (aSellerOrder.getIssuerAssignedID ())); // BT-200
        aOut.setSalesOrderLineReference (_idValue (aSellerOrder.getLineID ())); // BT-201
      }
    }
    final LineTradeDeliveryType aLTD = aLine.getSpecifiedLineTradeDelivery ();
    if (aLTD != null)
    {
      if (aLTD.getBilledQuantity () != null)
      {
        final QuantityType aQ = aLTD.getBilledQuantity ();
        aOut.setQuantity (aQ.getValue ());
        aOut.setQuantityUnit (str (aQ.getUnitCode ()));
      }
      // BT-189 / BT-190
      if (aLTD.getDespatchAdviceReferencedDocument () != null)
      {
        aOut.setDespatchAdviceReference (_idValue (aLTD.getDespatchAdviceReferencedDocument ().getIssuerAssignedID ()));
        aOut.setDespatchAdviceLineReference (_idValue (aLTD.getDespatchAdviceReferencedDocument ().getLineID ()));
      }
      // BT-191 / BT-192
      if (aLTD.getReceivingAdviceReferencedDocument () != null)
      {
        aOut.setReceivingAdviceReference (_idValue (aLTD.getReceivingAdviceReferencedDocument ()
                                                        .getIssuerAssignedID ()));
        aOut.setReceivingAdviceLineReference (_idValue (aLTD.getReceivingAdviceReferencedDocument ().getLineID ()));
      }
      // BT-198 / BT-199
      if (aLTD.getDeliveryNoteReferencedDocument () != null)
      {
        aOut.setDeliveryNoteReference (_idValue (aLTD.getDeliveryNoteReferencedDocument ().getIssuerAssignedID ()));
        aOut.setDeliveryNoteLineReference (_idValue (aLTD.getDeliveryNoteReferencedDocument ().getLineID ()));
      }
      // BG-37 / BG-38
      if (aLTD.getShipToTradeParty () != null || aLTD.getActualDeliverySupplyChainEvent () != null)
      {
        final PhinvoiceLineDelivery aLineDelivery = _readLineDelivery (aLTD, aOut, aErrors);
        if (!aLineDelivery.isEmpty ())
          aOut.setDelivery (aLineDelivery);
      }
    }
    final LineTradeSettlementType aLTS = aLine.getSpecifiedLineTradeSettlement ();
    if (aLTS != null)
    {
      if (aLTS.getSpecifiedTradeSettlementLineMonetarySummation () != null)
      {
        PhinvoiceReaderHelper.setAndAddRepeated (_getAmounts (aLTS.getSpecifiedTradeSettlementLineMonetarySummation ()
                                                              .getLineTotalAmount ()),
                                             "BT-131",
                                             aOut::setNetAmount,
                                             aTerms);
        PhinvoiceReaderHelper.setAndAddRepeated (_getAmounts (aLTS.getSpecifiedTradeSettlementLineMonetarySummation ()
                                                              .getTotalAllowanceChargeAmount ()),
                                                 "BT-X-98",
                                                 aOut::setTotalAllowanceCharge,
                                                 aTerms);
      }
      PhinvoiceReaderHelper.setAndAddRepeated (new CommonsArrayList <> (aLTS.getReceivableSpecifiedTradeAccountingAccount (),
                                                                    aAccount -> _idValue (aAccount.getID ())),
                                           "BT-133",
                                           aOut::setAccountingCost,
                                           aTerms);
      if (aLTS.getBillingSpecifiedPeriod () != null)
      {
        final PhinvoiceInvoicingPeriod aP = _readPeriod (aLTS.getBillingSpecifiedPeriod (),
                                                     "BT-134",
                                                     "BT-135",
                                                     aTerms,
                                                     aErrors);
        if (!aP.isEmpty ())
          aOut.setPeriod (aP);
      }
      // BG-39 — line-level preceding invoice references
      for (final ReferencedDocumentType aRD : aLTS.getInvoiceReferencedDocument ())
      {
        final PhinvoiceLinePrecedingInvoiceRef aRef = new PhinvoiceLinePrecedingInvoiceRef ();
        aRef.setNumber (_idValue (aRD.getIssuerAssignedID ())); // BT-217
        aRef.setIssueDate (_formattedDateTime (aRD.getFormattedIssueDateTime (), "BT-218", aTerms, aErrors)); // BT-218
        aRef.setTypeCode (_codeValue (aRD.getTypeCode ())); // BT-219
        aRef.setLineReference (_idValue (aRD.getLineID ())); // BT-220
        if (aRef.getNumber () != null ||
            aRef.getIssueDate () != null ||
            aRef.getTypeCode () != null ||
            aRef.getLineReference () != null)
          aOut.precedingInvoiceRefs ().add (aRef);
      }
      _readLineDocumentReferences (aLTS.getAdditionalReferencedDocument (), aOut);
      _readLineVat (aLTS.getApplicableTradeTax (), aOut);
      // BG-27 / BG-28 — line allowances / charges
      for (final TradeAllowanceChargeType aAC : aLTS.getSpecifiedTradeAllowanceCharge ())
        if (aAC.getChargeIndicator () != null && Boolean.TRUE.equals (aAC.getChargeIndicator ().isIndicator ()))
        {
          final PhinvoiceLineCharge aC = new PhinvoiceLineCharge ();
          PhinvoiceReaderHelper.setAndAddRepeated (_getAmounts (aAC.getActualAmount ()), "BT-141", aC::setAmount, aTerms);
          if (aAC.getBasisAmount () != null)
            aC.setBaseAmount (aAC.getBasisAmount ().getValue ());
          if (aAC.getCalculationPercent () != null)
            aC.setPercentage (aAC.getCalculationPercent ().getValue ());
          aC.setReason (_text (aAC.getReason ()));
          // BT-145 and BT-193 share ram:ReasonCode and are told apart by @listID
          final String sReasonCode = _codeValue (aAC.getReasonCode ());
          if (_isNonVatTaxCodeList (aAC.getReasonCode ()))
          {
            aC.setNonVatTaxTypeCode (sReasonCode);
            aC.setNonVatTaxTypeCodeListID (aAC.getReasonCode ().getListID ());
          }
          else
            aC.setReasonCode (sReasonCode);
          aOut.charges ().add (aC);
        }
        else
        {
          final PhinvoiceLineAllowance aA = new PhinvoiceLineAllowance ();
          PhinvoiceReaderHelper.setAndAddRepeated (_getAmounts (aAC.getActualAmount ()), "BT-136", aA::setAmount, aTerms);
          if (aAC.getBasisAmount () != null)
            aA.setBaseAmount (aAC.getBasisAmount ().getValue ());
          if (aAC.getCalculationPercent () != null)
            aA.setPercentage (aAC.getCalculationPercent ().getValue ());
          aA.setReason (_text (aAC.getReason ()));
          aA.setReasonCode (_codeValue (aAC.getReasonCode ()));
          aOut.allowances ().add (aA);
        }
    }
    return aOut;
  }


  @Nullable
  private static String _roleCode (@NonNull final TradePartyType aTP)
  {
    return aTP.getRoleCode ().isEmpty () ? null : str (aTP.getRoleCode ().get (0).getValue ());
  }

  @Nullable
  private static PhinvoiceContact _readFirstContact (@NonNull final TradePartyType aTP)
  {
    return aTP.getDefinedTradeContact ().isEmpty () ? null : _readContact (aTP.getDefinedTradeContact ().get (0));
  }

  @Nullable
  private static PhinvoiceIdentifier _readFirstElectronicAddress (@NonNull final TradePartyType aTP)
  {
    final ICommonsList <PhinvoiceIdentifier> aList = _getElectronicAddresses (aTP);
    return aList.getFirstOrNull ();
  }

  @NonNull
  private static ICommonsList <PhinvoiceIdentifier> _getAllTaxRegistrations (@NonNull final TradePartyType aTP)
  {
    final ICommonsList <PhinvoiceIdentifier> ret = new CommonsArrayList <> ();
    for (final TaxRegistrationType aTR : aTP.getSpecifiedTaxRegistration ())
      _addIdentifier (aTR.getID (), ret);
    return ret;
  }

  /**
   * Read a trade party of the profile EXTENDED (a BG-X).
   */
  @NonNull
  private static PhinvoiceTradeParty _readTradeParty (@NonNull final TradePartyType aTP)
  {
    final PhinvoiceTradeParty ret = new PhinvoiceTradeParty ();
    if (!aTP.getID ().isEmpty ())
      ret.setID (_idValue (aTP.getID ().get (0)));
    for (final IDType aID : aTP.getGlobalID ())
      _addIdentifier (aID, ret.globalIdentifiers ());
    ret.setName (_text (aTP.getName ()));
    ret.setRoleCode (_roleCode (aTP));
    ret.setDescription (_joinTexts (aTP.getDescription ()));
    final LegalOrganizationType aLO = aTP.getSpecifiedLegalOrganization ();
    if (aLO != null)
    {
      ret.setLegalRegistration (_identifier (aLO.getID ()));
      ret.setTradingName (_text (aLO.getTradingBusinessName ()));
      if (aLO.getPostalTradeAddress () != null)
        ret.setLegalAddress (_readAddress (aLO.getPostalTradeAddress ()));
    }
    ret.setContact (_readFirstContact (aTP));
    if (aTP.getPostalTradeAddress () != null)
      ret.setPostalAddress (_readAddress (aTP.getPostalTradeAddress ()));
    ret.setElectronicAddress (_readFirstElectronicAddress (aTP));
    ret.taxRegistrations ().addAll (_getAllTaxRegistrations (aTP));
    return ret;
  }

  @Nullable
  private static PhinvoiceTradeParty _readOptionalTradeParty (@Nullable final TradePartyType aTP)
  {
    return aTP == null ? null : _readTradeParty (aTP);
  }

  /**
   * Read a referenced document of the profile EXTENDED.
   */
  @NonNull
  private static PhinvoiceReferencedDocument _readReferencedDocument (@NonNull final ReferencedDocumentType aRD,
                                                                      @NonNull final List <PhinvoiceAdditionalTerm> aTerms,
                                                                      @NonNull final ErrorList aErrors)
  {
    final PhinvoiceReferencedDocument ret = new PhinvoiceReferencedDocument ();
    ret.setIssuerAssignedID (_idValue (aRD.getIssuerAssignedID ()));
    ret.setLineID (_idValue (aRD.getLineID ()));
    ret.setTypeCode (_codeValue (aRD.getTypeCode ()));
    ret.setReferenceTypeCode (_codeValue (aRD.getReferenceTypeCode ()));
    ret.setName (_joinTexts (aRD.getName ()));
    if (aRD.getURIID () != null)
      ret.setUri (str (aRD.getURIID ().getValue ()));
    ret.setIssueDate (_formattedDateTime (aRD.getFormattedIssueDateTime (), "BT-X", aTerms, aErrors));
    if (!aRD.getAttachmentBinaryObject ().isEmpty ())
    {
      final BinaryObjectType aBinary = aRD.getAttachmentBinaryObject ().get (0);
      final PhinvoiceAttachment aAttachment = new PhinvoiceAttachment ();
      aAttachment.setBinary (aBinary.getValue ());
      aAttachment.setMimeCode (str (aBinary.getMimeCode ()));
      aAttachment.setFilename (str (aBinary.getFilename ()));
      ret.setAttachment (aAttachment);
    }
    return ret;
  }

  @Nullable
  private static PhinvoiceReferencedDocument _readOptionalReferencedDocument (@Nullable final ReferencedDocumentType aRD,
                                                                              @NonNull final List <PhinvoiceAdditionalTerm> aTerms,
                                                                              @NonNull final ErrorList aErrors)
  {
    return aRD == null ? null : _readReferencedDocument (aRD, aTerms, aErrors);
  }

  @NonNull
  private static PhinvoiceIncludedTax _readIncludedTax (@NonNull final TradeTaxType aTT)
  {
    final PhinvoiceIncludedTax ret = new PhinvoiceIncludedTax ();
    if (!aTT.getCalculatedAmount ().isEmpty ())
      ret.setCalculatedAmount (aTT.getCalculatedAmount ().get (0).getValue ());
    if (aTT.getTypeCode () != null)
      ret.setTypeCode (str (aTT.getTypeCode ().getValue ()));
    ret.setCategoryCode (_codeValue (aTT.getCategoryCode ()));
    if (aTT.getRateApplicablePercent () != null)
      ret.setRate (aTT.getRateApplicablePercent ().getValue ());
    ret.setExemptionReason (_text (aTT.getExemptionReason ()));
    ret.setExemptionReasonCode (_codeValue (aTT.getExemptionReasonCode ()));
    return ret;
  }

  @Nullable
  private static PhinvoiceDeliveryTerms _readDeliveryTerms (@Nullable final TradeDeliveryTermsType aTDT)
  {
    if (aTDT == null)
      return null;
    final PhinvoiceDeliveryTerms ret = new PhinvoiceDeliveryTerms ();
    if (aTDT.getDeliveryTypeCode () != null)
      ret.setDeliveryTypeCode (str (aTDT.getDeliveryTypeCode ().getValue ()));
    if (aTDT.getRelevantTradeLocation () != null)
    {
      if (aTDT.getRelevantTradeLocation ().getCountryID () != null)
        ret.setLocationCountryCode (str (aTDT.getRelevantTradeLocation ().getCountryID ().getValue ()));
      ret.setLocationName (_text (aTDT.getRelevantTradeLocation ().getName ()));
    }
    return ret;
  }

  @Nullable
  private static LocalDate _documentDate (@Nullable final ReferencedDocumentType aRD,
                                          @NonNull final String sTerm,
                                          @NonNull final List <PhinvoiceAdditionalTerm> aTerms,
                                          @NonNull final ErrorList aErrors)
  {
    return aRD == null ? null : _formattedDateTime (aRD.getFormattedIssueDateTime (), sTerm, aTerms, aErrors);
  }

  /**
   * Read the terms of the profile EXTENDED on document level.
   */
  private static void _readHeaderExtended (@NonNull final CrossIndustryInvoiceType aCii,
                                           @NonNull final PhinvoiceInvoice aModel,
                                           @NonNull final ErrorList aErrors)
  {
    final ICommonsList <PhinvoiceAdditionalTerm> aTerms = aModel.additionalTerms ();
    if (aCii.getExchangedDocumentContext () != null && aCii.getExchangedDocumentContext ().getTestIndicator () != null)
      aModel.setTestIndicator (aCii.getExchangedDocumentContext ().getTestIndicator ().isIndicator ()); // BT-X-1
    final ExchangedDocumentType aED = aCii.getExchangedDocument ();
    if (aED != null)
    {
      aModel.setDocumentName (_joinTexts (aED.getName ())); // BT-X-2
      if (aED.getCopyIndicator () != null)
        aModel.setCopyIndicator (aED.getCopyIndicator ().isIndicator ()); // BT-X-3
      if (!aED.getLanguageID ().isEmpty ())
        aModel.setLanguageCode (_idValue (aED.getLanguageID ().get (0))); // BT-X-4
      if (aED.getEffectiveSpecifiedPeriod () != null)
        aModel.setContractualDueDate (_dateTime (aED.getEffectiveSpecifiedPeriod ().getCompleteDateTime (),
                                                 "BT-X-6",
                                                 aTerms,
                                                 aErrors)); // BT-X-6
      // BT-X-5 - the notes are read in the same order
      int nIndex = 0;
      for (final NoteType aNote : aED.getIncludedNote ())
      {
        final String sContentCode = _codeValue (aNote.getContentCode ());
        if (nIndex < aModel.invoiceNotes ().size ())
          aModel.invoiceNotes ().get (nIndex).setContentCode (sContentCode);
        nIndex++;
      }
    }
    final SupplyChainTradeTransactionType aSCTT = aCii.getSupplyChainTradeTransaction ();
    if (aSCTT == null)
      return;
    final HeaderTradeAgreementType aHTA = aSCTT.getApplicableHeaderTradeAgreement ();
    if (aHTA != null)
    {
      aModel.setSalesOrderDate (_documentDate (aHTA.getSellerOrderReferencedDocument (), "BT-X-146", aTerms, aErrors));
      aModel.setPurchaseOrderDate (_documentDate (aHTA.getBuyerOrderReferencedDocument (), "BT-X-147", aTerms, aErrors));
      if (aHTA.getContractReferencedDocument () != null)
      {
        aModel.setContractReferenceTypeCode (_codeValue (aHTA.getContractReferencedDocument ().getReferenceTypeCode ())); // BT-X-405
        aModel.setContractDate (_documentDate (aHTA.getContractReferencedDocument (), "BT-X-148", aTerms, aErrors));
      }
      aModel.setQuotationReference (_readOptionalReferencedDocument (aHTA.getQuotationReferencedDocument (), aTerms, aErrors));
      for (final ReferencedDocumentType aRD : aHTA.getUltimateCustomerOrderReferencedDocument ())
        aModel.ultimateCustomerOrderReferences ().add (_readReferencedDocument (aRD, aTerms, aErrors));
      aModel.setSalesAgent (_readOptionalTradeParty (aHTA.getSalesAgentTradeParty ()));
      aModel.setBuyerTaxRepresentative (_readOptionalTradeParty (aHTA.getBuyerTaxRepresentativeTradeParty ()));
      aModel.setProductEndUser (_readOptionalTradeParty (aHTA.getProductEndUserTradeParty ()));
      aModel.setBuyerAgent (_readOptionalTradeParty (aHTA.getBuyerAgentTradeParty ()));
      aModel.setDeliveryTerms (_readDeliveryTerms (aHTA.getApplicableTradeDeliveryTerms ()));
      // The dates of BT-17 (type code 50), BT-18 (130) and BG-24 (916)
      for (final ReferencedDocumentType aRD : aHTA.getAdditionalReferencedDocument ())
      {
        final String sTypeCode = _codeValue (aRD.getTypeCode ());
        if (EN16931CodeLists.DOCUMENT_TYPE_CODE_ORIGINATOR_DOCUMENT.equals (sTypeCode))
          aModel.setTenderOrLotReferenceDate (_documentDate (aRD, "BT-X-556", aTerms, aErrors));
        else
          if (EN16931CodeLists.DOCUMENT_TYPE_CODE_OBJECT_DOCUMENT.equals (sTypeCode))
            aModel.setInvoicedObjectDate (_documentDate (aRD, "BT-X-557", aTerms, aErrors));
          else
          {
            final String sReference = _idValue (aRD.getIssuerAssignedID ());
            for (final PhinvoiceSupportingDocument aDoc : aModel.supportingDocuments ())
              if (EqualsHelper.equals (aDoc.getReference (), sReference) && aDoc.getIssueDate () == null)
              {
                aDoc.setIssueDate (_documentDate (aRD, "BT-X-149", aTerms, aErrors));
                break;
              }
          }
      }
      // Further parts of the EN 16931 parties
      if (aHTA.getSellerTradeParty () != null)
      {
        final TradePartyType aTP = aHTA.getSellerTradeParty ();
        aModel.getSeller ().setRoleCode (_roleCode (aTP));
        if (aTP.getSpecifiedLegalOrganization () != null && aTP.getSpecifiedLegalOrganization ().getPostalTradeAddress () != null)
          aModel.getSeller ().setLegalAddress (_readAddress (aTP.getSpecifiedLegalOrganization ().getPostalTradeAddress ()));
      }
      if (aHTA.getBuyerTradeParty () != null)
      {
        final TradePartyType aTP = aHTA.getBuyerTradeParty ();
        aModel.getBuyer ().setRoleCode (_roleCode (aTP));
        aModel.getBuyer ().setAdditionalLegalInfo (_joinTexts (aTP.getDescription ()));
        if (aTP.getSpecifiedLegalOrganization () != null && aTP.getSpecifiedLegalOrganization ().getPostalTradeAddress () != null)
          aModel.getBuyer ().setLegalAddress (_readAddress (aTP.getSpecifiedLegalOrganization ().getPostalTradeAddress ()));
      }
      if (aHTA.getSellerTaxRepresentativeTradeParty () != null && aModel.getSellerTaxRepresentative () != null)
      {
        final TradePartyType aTP = aHTA.getSellerTaxRepresentativeTradeParty ();
        final PhinvoiceSellerTaxRepresentative aTaxRep = aModel.getSellerTaxRepresentative ();
        if (!aTP.getID ().isEmpty ())
          aTaxRep.setID (_idValue (aTP.getID ().get (0)));
        for (final IDType aID : aTP.getGlobalID ())
          _addIdentifier (aID, aTaxRep.globalIdentifiers ());
        aTaxRep.setRoleCode (_roleCode (aTP));
        aTaxRep.setElectronicAddress (_readFirstElectronicAddress (aTP));
        final LegalOrganizationType aLO = aTP.getSpecifiedLegalOrganization ();
        if (aLO != null)
        {
          aTaxRep.setLegalRegistration (_identifier (aLO.getID ()));
          aTaxRep.setTradingName (_text (aLO.getTradingBusinessName ()));
          if (aLO.getPostalTradeAddress () != null)
            aTaxRep.setLegalAddress (_readAddress (aLO.getPostalTradeAddress ()));
        }
        aTaxRep.setContact (_readFirstContact (aTP));
      }
    }
    final HeaderTradeDeliveryType aHTD = aSCTT.getApplicableHeaderTradeDelivery ();
    if (aHTD != null)
    {
      aModel.setUltimateShipTo (_readOptionalTradeParty (aHTD.getUltimateShipToTradeParty ()));
      aModel.setShipFrom (_readOptionalTradeParty (aHTD.getShipFromTradeParty ()));
      final SupplyChainConsignmentType aConsignment = aHTD.getRelatedSupplyChainConsignment ();
      if (aConsignment != null && !aConsignment.getSpecifiedLogisticsTransportMovement ().isEmpty ())
      {
        final var aModeCode = aConsignment.getSpecifiedLogisticsTransportMovement ().get (0).getModeCode ();
        if (aModeCode != null)
          aModel.setTransportModeCode (str (aModeCode.getValue ())); // BT-X-152
      }
      aModel.setDespatchAdviceDate (_documentDate (aHTD.getDespatchAdviceReferencedDocument (), "BT-X-200", aTerms, aErrors));
      aModel.setReceivingAdviceDate (_documentDate (aHTD.getReceivingAdviceReferencedDocument (), "BT-X-201", aTerms, aErrors));
      aModel.setDeliveryNoteDate (_documentDate (aHTD.getDeliveryNoteReferencedDocument (), "BT-X-203", aTerms, aErrors));
      final TradePartyType aShipTo = aHTD.getShipToTradeParty ();
      final PhinvoiceDeliveryInformation aDI = aModel.getDeliveryInformation ();
      if (aShipTo != null && aDI != null)
      {
        aDI.setRoleCode (_roleCode (aShipTo));
        aDI.setElectronicAddress (_readFirstElectronicAddress (aShipTo));
        aDI.taxRegistrations ().addAll (_getAllTaxRegistrations (aShipTo));
        final LegalOrganizationType aLO = aShipTo.getSpecifiedLegalOrganization ();
        if (aLO != null)
        {
          aDI.setLegalRegistration (_identifier (aLO.getID ()));
          aDI.setTradingName (_text (aLO.getTradingBusinessName ()));
          if (aLO.getPostalTradeAddress () != null)
            aDI.setLegalAddress (_readAddress (aLO.getPostalTradeAddress ()));
        }
        aDI.setContact (_readFirstContact (aShipTo));
      }
    }
    final HeaderTradeSettlementType aHTS = aSCTT.getApplicableHeaderTradeSettlement ();
    if (aHTS != null)
    {
      aModel.setSellerReference (_text (aHTS.getInvoiceIssuerReference ())); // BT-X-204
      aModel.setInvoicer (_readOptionalTradeParty (aHTS.getInvoicerTradeParty ()));
      aModel.setInvoicee (_readOptionalTradeParty (aHTS.getInvoiceeTradeParty ()));
      aModel.setPayer (_readOptionalTradeParty (aHTS.getPayerTradeParty ()));
      if (!aHTS.getReceivableSpecifiedTradeAccountingAccount ().isEmpty () &&
          aHTS.getReceivableSpecifiedTradeAccountingAccount ().get (0).getTypeCode () != null)
        aModel.setBuyerAccountingReferenceTypeCode (str (aHTS.getReceivableSpecifiedTradeAccountingAccount ()
                                                             .get (0)
                                                             .getTypeCode ()
                                                             .getValue ())); // BT-X-290
      final TradeCurrencyExchangeType aTCE = aHTS.getTaxApplicableTradeCurrencyExchange ();
      if (aTCE != null)
      {
        final PhinvoiceTaxCurrencyExchange aExchange = new PhinvoiceTaxCurrencyExchange ();
        aExchange.setSourceCurrency (_codeValue (aTCE.getSourceCurrencyCode ()));
        aExchange.setTargetCurrency (_codeValue (aTCE.getTargetCurrencyCode ()));
        if (aTCE.getConversionRate () != null)
          aExchange.setRate (aTCE.getConversionRate ().getValue ());
        aExchange.setRateDate (_dateTime (aTCE.getConversionRateDateTime (), "BT-X-261", aTerms, aErrors));
        aModel.setTaxCurrencyExchange (aExchange);
      }
      for (final LogisticsServiceChargeType aLSC : aHTS.getSpecifiedLogisticsServiceCharge ())
      {
        final PhinvoiceLogisticsServiceCharge aCharge = new PhinvoiceLogisticsServiceCharge ();
        aCharge.setDescription (_joinTexts (aLSC.getDescription ()));
        if (!aLSC.getAppliedAmount ().isEmpty ())
          aCharge.setAmount (aLSC.getAppliedAmount ().get (0).getValue ());
        if (!aLSC.getAppliedTradeTax ().isEmpty ())
        {
          final TradeTaxType aTT = aLSC.getAppliedTradeTax ().get (0);
          aCharge.setTaxCategoryCode (_codeValue (aTT.getCategoryCode ()));
          if (aTT.getRateApplicablePercent () != null)
            aCharge.setTaxRate (aTT.getRateApplicablePercent ().getValue ());
          aCharge.setTaxExemptionReason (_text (aTT.getExemptionReason ()));
          aCharge.setTaxExemptionReasonCode (_codeValue (aTT.getExemptionReasonCode ()));
        }
        aModel.logisticsServiceCharges ().add (aCharge);
      }
      for (final AdvancePaymentType aAP : aHTS.getSpecifiedAdvancePayment ())
      {
        final PhinvoiceAdvancePayment aPayment = new PhinvoiceAdvancePayment ();
        if (aAP.getPaidAmount () != null)
          aPayment.setPaidAmount (aAP.getPaidAmount ().getValue ()); // BT-X-291
        aPayment.setReceivedDate (_formattedDateTime (aAP.getFormattedReceivedDateTime (), "BT-X-292", aTerms, aErrors));
        for (final TradeTaxType aTT : aAP.getIncludedTradeTax ())
          aPayment.includedTaxes ().add (_readIncludedTax (aTT));
        if (!aAP.getInvoiceSpecifiedReferencedDocument ().isEmpty ())
          aPayment.setPrecedingInvoice (_readReferencedDocument (aAP.getInvoiceSpecifiedReferencedDocument ().get (0), aTerms, aErrors));
        aModel.advancePayments ().add (aPayment);
      }
      // Further parts of the payee (BG-10)
      final TradePartyType aPayeeTP = aHTS.getPayeeTradeParty ();
      if (aPayeeTP != null && aModel.getPayee () != null)
      {
        final PhinvoicePayee aPayee = aModel.getPayee ();
        aPayee.setRoleCode (_roleCode (aPayeeTP));
        if (aPayeeTP.getSpecifiedLegalOrganization () != null)
        {
          aPayee.setTradingName (_text (aPayeeTP.getSpecifiedLegalOrganization ().getTradingBusinessName ()));
          if (aPayeeTP.getSpecifiedLegalOrganization ().getPostalTradeAddress () != null)
            aPayee.setLegalAddress (_readAddress (aPayeeTP.getSpecifiedLegalOrganization ().getPostalTradeAddress ()));
        }
        aPayee.setElectronicAddress (_readFirstElectronicAddress (aPayeeTP));
        aPayee.taxRegistrations ().addAll (_getAllTaxRegistrations (aPayeeTP));
        aPayee.setContact (_readFirstContact (aPayeeTP));
        if (aPayeeTP.getPostalTradeAddress () != null)
          aPayee.setPostalAddress (_readAddress (aPayeeTP.getPostalTradeAddress ()));
      }
      // BG-14
      if (aHTS.getBillingSpecifiedPeriod () != null && aModel.getInvoicingPeriod () != null)
        aModel.getInvoicingPeriod ().setDescription (_joinTexts (aHTS.getBillingSpecifiedPeriod ().getDescription ())); // BT-X-264
      // BG-23 - in the same order
      int nIndex = 0;
      for (final TradeTaxType aTT : aHTS.getApplicableTradeTax ())
      {
        if (nIndex < aModel.vatBreakdowns ().size ())
        {
          final PhinvoiceVatBreakdown aBD = aModel.vatBreakdowns ().get (nIndex);
          if (!aTT.getLineTotalBasisAmount ().isEmpty ())
            aBD.setLineTotalBasisAmount (aTT.getLineTotalBasisAmount ().get (0).getValue ()); // BT-X-262
          if (!aTT.getAllowanceChargeBasisAmount ().isEmpty ())
            aBD.setAllowanceChargeBasisAmount (aTT.getAllowanceChargeBasisAmount ().get (0).getValue ()); // BT-X-263
        }
        nIndex++;
      }
      // BG-20 / BG-21 - in the same order
      int nAllowance = 0;
      int nCharge = 0;
      for (final TradeAllowanceChargeType aAC : aHTS.getSpecifiedTradeAllowanceCharge ())
      {
        final boolean bCharge = aAC.getChargeIndicator () != null && Boolean.TRUE.equals (aAC.getChargeIndicator ().isIndicator ());
        final BigDecimal aSequence = aAC.getSequenceNumeric () == null ? null : aAC.getSequenceNumeric ().getValue ();
        final BigDecimal aBasisQuantity = aAC.getBasisQuantity () == null ? null : aAC.getBasisQuantity ().getValue ();
        final String sBasisQuantityUnit = aAC.getBasisQuantity () == null ? null : str (aAC.getBasisQuantity ().getUnitCode ());
        if (bCharge)
        {
          if (nCharge < aModel.documentCharges ().size ())
            aModel.documentCharges ()
                  .get (nCharge)
                  .setSequenceNumeric (aSequence)
                  .setBasisQuantity (aBasisQuantity)
                  .setBasisQuantityUnit (sBasisQuantityUnit);
          nCharge++;
        }
        else
        {
          if (nAllowance < aModel.documentAllowances ().size ())
            aModel.documentAllowances ()
                  .get (nAllowance)
                  .setSequenceNumeric (aSequence)
                  .setBasisQuantity (aBasisQuantity)
                  .setBasisQuantityUnit (sBasisQuantityUnit);
          nAllowance++;
        }
      }
      // Payment terms - in the same order as _readPaymentTerms
      int nTerms = 0;
      final List <TradePaymentTermsType> aAllTerms = aHTS.getSpecifiedTradePaymentTerms ();
      final int nDueDateIndex = _getDueDateIndex (aAllTerms);
      for (int nTermsIndex = 0; nTermsIndex < aAllTerms.size (); ++nTermsIndex)
      {
        final TradePaymentTermsType aPT = aAllTerms.get (nTermsIndex);
        final boolean bCounted = _isModelPaymentTerms (aPT, nTermsIndex != nDueDateIndex);
        final BigDecimal aPartial = aPT.getPartialPaymentAmount ().isEmpty () ? null : aPT.getPartialPaymentAmount ().get (0).getValue ();
        final PhinvoiceTradeParty aTermsPayee = _readOptionalTradeParty (aPT.getPayeeTradeParty ().isEmpty () ? null : aPT.getPayeeTradeParty ().get (0));
        if (bCounted && nTerms < aModel.paymentTerms ().size ())
        {
          final PhinvoicePaymentTerms aTerms2 = aModel.paymentTerms ().get (nTerms);
          aTerms2.setPartialPaymentAmount (aPartial); // BT-X-275
          aTerms2.setPayee (aTermsPayee); // BG-X-77
          final TradePaymentDiscountTermsType aDiscount = aPT.getApplicableTradePaymentDiscountTerms ();
          if (aDiscount != null && !aTerms2.earlyDiscounts ().isEmpty ())
          {
            final PhinvoiceEarlyPaymentDiscount aEarly = aTerms2.earlyDiscounts ().get (0);
            if (aDiscount.getBasisPeriodMeasure () != null)
            {
              aEarly.setBasisPeriodMeasure (aDiscount.getBasisPeriodMeasure ().getValue ()); // BT-X-283
              aEarly.setBasisPeriodUnit (str (aDiscount.getBasisPeriodMeasure ().getUnitCode ())); // BT-X-284
            }
            if (aDiscount.getBasisAmount () != null)
              aEarly.setBaseAmount (aDiscount.getBasisAmount ().getValue ()); // BT-X-285
          }
          final TradePaymentPenaltyTermsType aPenalty = aPT.getApplicableTradePaymentPenaltyTerms ();
          if (aPenalty != null && !aTerms2.latePenalties ().isEmpty ())
          {
            final PhinvoiceLatePaymentPenalty aLP = aTerms2.latePenalties ().get (0);
            if (aPenalty.getBasisPeriodMeasure () != null)
            {
              aLP.setBasisPeriodMeasure (aPenalty.getBasisPeriodMeasure ().getValue ()); // BT-X-277
              aLP.setBasisPeriodUnit (str (aPenalty.getBasisPeriodMeasure ().getUnitCode ())); // BT-X-278
            }
            if (aPenalty.getBasisAmount () != null)
              aLP.setBaseAmount (aPenalty.getBasisAmount ().getValue ()); // BT-X-279
          }
        }
        if (bCounted)
          nTerms++;
      }
    }
  }

  /**
   * Read the terms of the profile EXTENDED of a line.
   */
  private static void _readLineExtended (@NonNull final SupplyChainTradeLineItemType aLine,
                                         @NonNull final PhinvoiceInvoiceLine aOut,
                                         @NonNull final ErrorList aErrors)
  {
    final ICommonsList <PhinvoiceAdditionalTerm> aTerms = aOut.additionalTerms ();
    final DocumentLineDocumentType aLD = aLine.getAssociatedDocumentLineDocument ();
    if (aLD != null)
    {
      aOut.setParentLineID (_idValue (aLD.getParentLineID ())); // BT-X-304
      if (aLD.getLineStatusCode () != null)
        aOut.setLineStatusCode (str (aLD.getLineStatusCode ().getValue ())); // BT-X-7
      aOut.setLineStatusReasonCode (_codeValue (aLD.getLineStatusReasonCode ())); // BT-X-8
      if (!aLD.getIncludedNote ().isEmpty ())
      {
        final NoteType aNote = aLD.getIncludedNote ().get (0);
        aOut.setNoteContentCode (_codeValue (aNote.getContentCode ())); // BT-X-9
        if (!aNote.getSubjectCode ().isEmpty ())
          aOut.setNoteSubjectCode (_codeValue (aNote.getSubjectCode ().get (0))); // BT-X-10
      }
    }
    final TradeProductType aTPr = aLine.getSpecifiedTradeProduct ();
    if (aTPr != null && aOut.getItem () != null)
    {
      final PhinvoiceItem aItem = aOut.getItem ();
      aItem.setID (_idValue (aTPr.getID ())); // BT-X-305
      aItem.setIndustryAssignedID (_idValue (aTPr.getIndustryAssignedID ())); // BT-X-532
      aItem.setModelID (_idValue (aTPr.getModelID ())); // BT-X-533
      if (!aTPr.getBatchID ().isEmpty ())
        aItem.setBatchID (_idValue (aTPr.getBatchID ().get (0))); // BT-X-534
      aItem.setBrandName (_text (aTPr.getBrandName ())); // BT-X-535
      aItem.setModelName (_text (aTPr.getModelName ())); // BT-X-536
      aItem.setManufacturer (_readOptionalTradeParty (aTPr.getManufacturerTradeParty ())); // BG-X-93
      for (final TradeProductInstanceType aTPI : aTPr.getIndividualTradeProductInstance ())
      {
        final PhinvoiceProductInstance aInstance = new PhinvoiceProductInstance ();
        aInstance.setBatchID (_idValue (aTPI.getBatchID ()));
        aInstance.setSerialID (_idValue (aTPI.getSupplierAssignedSerialID ()));
        aItem.productInstances ().add (aInstance);
      }
      for (final ReferencedProductType aRP : aTPr.getIncludedReferencedProduct ())
      {
        final PhinvoiceIncludedProduct aProduct = new PhinvoiceIncludedProduct ();
        if (!aRP.getID ().isEmpty ())
          aProduct.setID (_idValue (aRP.getID ().get (0)));
        for (final IDType aID : aRP.getGlobalID ())
          _addIdentifier (aID, aProduct.globalIdentifiers ());
        aProduct.setSellerAssignedID (_idValue (aRP.getSellerAssignedID ()));
        aProduct.setBuyerAssignedID (_idValue (aRP.getBuyerAssignedID ()));
        if (!aRP.getIndustryAssignedID ().isEmpty ())
          aProduct.setIndustryAssignedID (_idValue (aRP.getIndustryAssignedID ().get (0)));
        aProduct.setName (_joinTexts (aRP.getName ()));
        aProduct.setDescription (_joinTexts (aRP.getDescription ()));
        if (!aRP.getUnitQuantity ().isEmpty ())
        {
          aProduct.setUnitQuantity (aRP.getUnitQuantity ().get (0).getValue ());
          aProduct.setUnitQuantityUnit (str (aRP.getUnitQuantity ().get (0).getUnitCode ()));
        }
        aItem.includedProducts ().add (aProduct);
      }
      // BT-X-13 - in the same order as the classifications
      int nIndex = 0;
      for (final ProductClassificationType aPC : aTPr.getDesignatedProductClassification ())
        if (aPC.getClassCode () != null && str (aPC.getClassCode ().getValue ()) != null)
        {
          if (nIndex < aItem.classifications ().size ())
            aItem.classifications ().get (nIndex).setName (_joinTexts (aPC.getClassName ()));
          nIndex++;
        }
    }
    final LineTradeAgreementType aLTA = aLine.getSpecifiedLineTradeAgreement ();
    if (aLTA != null)
    {
      aOut.setPurchaseOrderDate (_documentDate (aLTA.getBuyerOrderReferencedDocument (), "BT-X-22", aTerms, aErrors));
      aOut.setSalesOrderDate (_documentDate (aLTA.getSellerOrderReferencedDocument (), "BT-X-539", aTerms, aErrors));
      aOut.setQuotationReference (_readOptionalReferencedDocument (aLTA.getQuotationReferencedDocument (), aTerms, aErrors));
      aOut.setContractReference (_readOptionalReferencedDocument (aLTA.getContractReferencedDocument (), aTerms, aErrors));
      for (final ReferencedDocumentType aRD : aLTA.getAdditionalReferencedDocument ())
        aOut.additionalReferencedDocuments ().add (_readReferencedDocument (aRD, aTerms, aErrors));
      for (final ReferencedDocumentType aRD : aLTA.getUltimateCustomerOrderReferencedDocument ())
        aOut.ultimateCustomerOrderReferences ().add (_readReferencedDocument (aRD, aTerms, aErrors));
      aOut.setLineSeller (_readOptionalTradeParty (aLTA.getItemSellerTradeParty ()));
      aOut.setDeliveryTerms (_readDeliveryTerms (aLTA.getApplicableTradeDeliveryTerms ()));
      final TradePriceType aGross = aLTA.getGrossPriceProductTradePrice ();
      if (aGross != null && aOut.getPrice () != null)
      {
        // The same order as in _readPrice
        final PhinvoicePriceDetails aPrice = aOut.getPrice ();
        int nAllowance = 0;
        int nCharge = 0;
        for (final TradeAllowanceChargeType aAC : aGross.getAppliedTradeAllowanceCharge ())
        {
          final boolean bCharge = aAC.getChargeIndicator () != null && Boolean.TRUE.equals (aAC.getChargeIndicator ().isIndicator ());
          final ICommonsList <PhinvoicePriceAllowanceCharge> aList = bCharge ? aPrice.charges () : aPrice.allowances ();
          final int nPos = bCharge ? nCharge++ : nAllowance++;
          if (nPos >= aList.size ())
            continue;
          final PhinvoicePriceAllowanceCharge aPAC = aList.get (nPos);
          if (aAC.getCalculationPercent () != null)
            aPAC.setPercentage (aAC.getCalculationPercent ().getValue ()); // BT-X-34 / BT-X-300
          if (aAC.getBasisAmount () != null)
            aPAC.setBaseAmount (aAC.getBasisAmount ().getValue ()); // BT-X-35 / BT-X-301
          aPAC.setReason (_text (aAC.getReason ())); // BT-X-36 / BT-X-303
          final String sReasonCode = _codeValue (aAC.getReasonCode ());
          if (bCharge && _isNonVatTaxCodeList (aAC.getReasonCode ()))
          {
            aPAC.setNonVatTaxCode (sReasonCode); // BT-X-616
            aPAC.setNonVatTaxCodeListID (aAC.getReasonCode ().getListID ()); // BT-X-616-1
          }
          else
            aPAC.setReasonCode (sReasonCode); // BT-X-313 / BT-X-314
        }
      }
      final TradePriceType aNet = aLTA.getNetPriceProductTradePrice ();
      if (aNet != null && !aNet.getIncludedTradeTax ().isEmpty ())
        aOut.setIncludedTax (_readIncludedTax (aNet.getIncludedTradeTax ().get (0))); // BG-X-4
    }
    final LineTradeDeliveryType aLTD = aLine.getSpecifiedLineTradeDelivery ();
    if (aLTD != null)
    {
      if (aLTD.getChargeFreeQuantity () != null)
      {
        aOut.setChargeFreeQuantity (aLTD.getChargeFreeQuantity ().getValue ()); // BT-X-46
        aOut.setChargeFreeQuantityUnit (str (aLTD.getChargeFreeQuantity ().getUnitCode ()));
      }
      if (aLTD.getPackageQuantity () != null)
      {
        aOut.setPackageQuantity (aLTD.getPackageQuantity ().getValue ()); // BT-X-47
        aOut.setPackageQuantityUnit (str (aLTD.getPackageQuantity ().getUnitCode ()));
      }
      if (aLTD.getPerPackageUnitQuantity () != null)
      {
        aOut.setPerPackageUnitQuantity (aLTD.getPerPackageUnitQuantity ().getValue ()); // BT-X-561
        aOut.setPerPackageUnitQuantityUnit (str (aLTD.getPerPackageUnitQuantity ().getUnitCode ()));
      }
      aOut.setUltimateShipTo (_readOptionalTradeParty (aLTD.getUltimateShipToTradeParty ()));
      aOut.setDespatchAdviceDate (_documentDate (aLTD.getDespatchAdviceReferencedDocument (), "BT-X-88", aTerms, aErrors));
      aOut.setReceivingAdviceDate (_documentDate (aLTD.getReceivingAdviceReferencedDocument (), "BT-X-91", aTerms, aErrors));
      aOut.setDeliveryNoteDate (_documentDate (aLTD.getDeliveryNoteReferencedDocument (), "BT-X-94", aTerms, aErrors));
      final TradePartyType aShipTo = aLTD.getShipToTradeParty ();
      if (aShipTo != null && aOut.getDelivery () != null)
      {
        final PhinvoiceLineDelivery aDelivery = aOut.getDelivery ();
        aDelivery.setRoleCode (_roleCode (aShipTo));
        final LegalOrganizationType aLO = aShipTo.getSpecifiedLegalOrganization ();
        if (aLO != null)
        {
          aDelivery.setLegalRegistration (_identifier (aLO.getID ()));
          aDelivery.setTradingName (_text (aLO.getTradingBusinessName ()));
        }
        aDelivery.setContact (_readFirstContact (aShipTo));
        aDelivery.setElectronicAddress (_readFirstElectronicAddress (aShipTo));
        aDelivery.taxRegistrations ().addAll (_getAllTaxRegistrations (aShipTo));
      }
    }
    final LineTradeSettlementType aLTS = aLine.getSpecifiedLineTradeSettlement ();
    if (aLTS != null)
    {
      if (!aLTS.getApplicableTradeTax ().isEmpty () && aOut.getLineVat () != null)
      {
        final TradeTaxType aTT = aLTS.getApplicableTradeTax ().get (0);
        if (!aTT.getCalculatedAmount ().isEmpty ())
          aOut.getLineVat ().setCalculatedAmount (aTT.getCalculatedAmount ().get (0).getValue ()); // BT-X-95
        aOut.getLineVat ().setVatPointDateCode (_codeValue (aTT.getDueDateTypeCode ())); // BT-X-589
      }
      final TradeSettlementLineMonetarySummationType aSum = aLTS.getSpecifiedTradeSettlementLineMonetarySummation ();
      if (aSum != null)
      {
        if (!aSum.getChargeTotalAmount ().isEmpty ())
          aOut.setChargeTotal (aSum.getChargeTotalAmount ().get (0).getValue ()); // BT-X-327
        if (!aSum.getAllowanceTotalAmount ().isEmpty ())
          aOut.setAllowanceTotal (aSum.getAllowanceTotalAmount ().get (0).getValue ()); // BT-X-328
        for (final AmountType aTax : aSum.getTaxTotalAmount ())
          if (aOut.getTaxTotal () == null)
            aOut.setTaxTotal (aTax.getValue ()); // BT-X-329
          else
            aOut.setTaxTotalAccountingCurrency (aTax.getValue ()); // BT-X-590
        if (!aSum.getGrandTotalAmount ().isEmpty ())
          aOut.setGrandTotal (aSum.getGrandTotalAmount ().get (0).getValue ()); // BT-X-330
      }
      if (!aLTS.getReceivableSpecifiedTradeAccountingAccount ().isEmpty () &&
          aLTS.getReceivableSpecifiedTradeAccountingAccount ().get (0).getTypeCode () != null)
        aOut.setAccountingCostTypeCode (str (aLTS.getReceivableSpecifiedTradeAccountingAccount ().get (0).getTypeCode ().getValue ())); // BT-X-99
    }
  }

  /**
   * Read an already-parsed CII document into a {@link PhinvoiceInvoice}. Convenience overload that
   * discards any collected warnings.
   *
   * @param aCii
   *        The JAXB invoice. Never {@code null}.
   * @return The populated model.
   */
  @NonNull
  PhinvoiceInvoice readCrossIndustryInvoice (@NonNull final CrossIndustryInvoiceType aCii)
  {
    return readCrossIndustryInvoice (aCii, new ErrorList ());
  }

  /**
   * Read an already-parsed CII document into a {@link PhinvoiceInvoice}, collecting non-fatal issues
   * (unknown type codes, unsupported date formats) into the supplied error list.
   *
   * @param aCii
   *        The JAXB invoice. Never {@code null}.
   * @param aErrors
   *        Error list for warnings. Never {@code null}.
   * @return The populated model.
   */
  @NonNull
  PhinvoiceInvoice readCrossIndustryInvoice (@NonNull final CrossIndustryInvoiceType aCii, @NonNull final ErrorList aErrors)
  {
    final PhinvoiceInvoice aModel = new PhinvoiceInvoice ();
    aModel.setEdition (EEN16931Edition.EN2017);

    // BG-2 — Process control
    if (aCii.getExchangedDocumentContext () != null)
    {
      final var aBP = aCii.getExchangedDocumentContext ().getBusinessProcessSpecifiedDocumentContextParameter ();
      if (aBP != null && !aBP.isEmpty ())
        PhinvoiceReaderHelper.setAndAddRepeated (new CommonsArrayList <> (aBP, aParam -> _idValue (aParam.getID ())),
                                             "BT-23",
                                             aModel.getProcessControl ()::setBusinessProcessType,
                                             aModel.additionalTerms ());
      final var aGL = aCii.getExchangedDocumentContext ().getGuidelineSpecifiedDocumentContextParameter ();
      if (aGL != null && !aGL.isEmpty ())
        PhinvoiceReaderHelper.setAndAddRepeated (new CommonsArrayList <> (aGL, aParam -> _idValue (aParam.getID ())),
                                             "BT-24",
                                             aModel.getProcessControl ()::setSpecificationIdentifier,
                                             aModel.additionalTerms ());
    }

    // BG-1 + header BTs
    final ExchangedDocumentType aED = aCii.getExchangedDocument ();
    if (aED != null)
    {
      aModel.setNumber (_idValue (aED.getID ()));
      // BT-2 / BT-166 — one element; @format 102 = date only, 208 = date and time
      _readIssueDateTime (aED.getIssueDateTime (), aModel, aErrors);
      // BT-3 — type code routing.
      final String sTypeCode = str (aED.getTypeCodeValue ());
      aModel.setTypeCode (sTypeCode);
      aModel.setKind (PhinvoiceReaderHelper.getDocumentKind (sTypeCode, EPhinvoiceDocumentKind.INVOICE, aErrors));
      // BG-1 — notes. Multiple Content lines per note are joined with newlines to preserve
      // every line; the EN 16931 model exposes a single BT-22 string per note.
      for (final NoteType aNote : aED.getIncludedNote ())
      {
        final PhinvoiceInvoiceNote aOut = new PhinvoiceInvoiceNote ();
        PhinvoiceReaderHelper.setAndAddRepeated (new CommonsArrayList <> (aNote.getSubjectCode (), aCode -> _codeValue (aCode)),
                                                 "BT-21",
                                                 aOut::setSubjectCode,
                                                 aModel.additionalTerms ());
        aOut.setContent (_joinTexts (aNote.getContent ()));
        if (aOut.getContent () != null || aOut.getSubjectCode () != null)
          aModel.invoiceNotes ().add (aOut);
      }
    }

    final SupplyChainTradeTransactionType aSCTT = aCii.getSupplyChainTradeTransaction ();
    if (aSCTT == null)
      return aModel;

    final HeaderTradeAgreementType aHTA = aSCTT.getApplicableHeaderTradeAgreement ();
    if (aHTA != null)
    {
      // BT-10
      final String sBuyerReference = _text (aHTA.getBuyerReference ());
      if (sBuyerReference != null)
        aModel.buyerReferences ().add (new PhinvoiceIdentifier (sBuyerReference, null));
      // BT-12 — contract
      if (aHTA.getContractReferencedDocument () != null)
        aModel.setContractReference (_idValue (aHTA.getContractReferencedDocument ().getIssuerAssignedID ()));
      // BT-13 — purchase order
      if (aHTA.getBuyerOrderReferencedDocument () != null)
        aModel.setPurchaseOrderReference (_idValue (aHTA.getBuyerOrderReferencedDocument ().getIssuerAssignedID ()));
      // BT-14 — sales order
      if (aHTA.getSellerOrderReferencedDocument () != null)
        aModel.setSalesOrderReference (_idValue (aHTA.getSellerOrderReferencedDocument ().getIssuerAssignedID ()));
      // BT-11 — project reference
      final ProcuringProjectType aProj = aHTA.getSpecifiedProcuringProject ();
      if (aProj != null)
      {
        aModel.setProjectReference (_idValue (aProj.getID ()));
        // BT-11-1 — mandatory in CII, so it repeats BT-11 or is the filler of the binding
        final String sProjectName = _text (aProj.getName ());
        if (sProjectName != null &&
            !sProjectName.equals (aModel.getProjectReference ()) &&
            !EN16931CodeLists.MISSING_VALUE_PLACEHOLDER.equals (sProjectName))
          aModel.setProjectName (sProjectName);
      }

      // BG-4 + BG-5 + BG-6
      if (aHTA.getSellerTradeParty () != null)
        _readSeller (aHTA.getSellerTradeParty (), aModel.getSeller ());
      // BG-7 + BG-8 + BG-9
      if (aHTA.getBuyerTradeParty () != null)
        _readBuyer (aHTA.getBuyerTradeParty (), aModel.getBuyer ());
      // BG-11 + BG-12
      if (aHTA.getSellerTaxRepresentativeTradeParty () != null)
        aModel.setSellerTaxRepresentative (_readTaxRep (aHTA.getSellerTaxRepresentativeTradeParty (), aModel));

      // BT-17 (TypeCode="50") / BT-18 (TypeCode="130") / BG-24 (TypeCode="916")
      final ICommonsList <String> aTenderIDs = new CommonsArrayList <> ();
      final ICommonsList <PhinvoiceIdentifier> aObjectIDs = new CommonsArrayList <> ();
      for (final ReferencedDocumentType aRD : aHTA.getAdditionalReferencedDocument ())
      {
        final String sTypeCode = _codeValue (aRD.getTypeCode ());
        if (EN16931CodeLists.DOCUMENT_TYPE_CODE_ORIGINATOR_DOCUMENT.equals (sTypeCode))
          aTenderIDs.add (_idValue (aRD.getIssuerAssignedID ()));
        else
          if (EN16931CodeLists.DOCUMENT_TYPE_CODE_OBJECT_DOCUMENT.equals (sTypeCode))
          {
            final String sScheme = _codeValue (aRD.getReferenceTypeCode ());
            if (_idValue (aRD.getIssuerAssignedID ()) != null)
              aObjectIDs.add (new PhinvoiceIdentifier (_idValue (aRD.getIssuerAssignedID ()), sScheme));
          }
          else
          {
            // BG-24 — the binding has type code 916, UBL takes everything that is not BT-18
            final PhinvoiceSupportingDocument aDoc = _readSupportingDocument (aRD, aModel.additionalTerms ());
            if (!aDoc.isEmpty ())
              aModel.supportingDocuments ().add (aDoc);
          }
      }
      PhinvoiceReaderHelper.setAndAddRepeated (aTenderIDs,
                                           "BT-17",
                                           aModel::setTenderOrLotReference,
                                           aModel.additionalTerms ());
      PhinvoiceReaderHelper.setAndAddRepeated (aObjectIDs,
                                           "BT-18",
                                           aModel::setInvoicedObjectIdentifier,
                                           aModel.additionalTerms ());
    }

    final HeaderTradeDeliveryType aHTD = aSCTT.getApplicableHeaderTradeDelivery ();
    if (aHTD != null)
    {
      // BT-15 — receiving advice
      if (aHTD.getReceivingAdviceReferencedDocument () != null)
        aModel.setReceivingAdviceReference (_idValue (aHTD.getReceivingAdviceReferencedDocument ()
                                                          .getIssuerAssignedID ()));
      // BT-16 — despatch advice
      if (aHTD.getDespatchAdviceReferencedDocument () != null)
        aModel.setDespatchAdviceReference (_idValue (aHTD.getDespatchAdviceReferencedDocument ()
                                                         .getIssuerAssignedID ()));
      // BT-197 — delivery note
      if (aHTD.getDeliveryNoteReferencedDocument () != null)
        aModel.setDeliveryNoteReference (_idValue (aHTD.getDeliveryNoteReferencedDocument ().getIssuerAssignedID ()));
      // BG-13 + BG-15 — Delivery
      if (aHTD.getShipToTradeParty () != null || aHTD.getActualDeliverySupplyChainEvent () != null)
      {
        final PhinvoiceDeliveryInformation aDI = new PhinvoiceDeliveryInformation ();
        final TradePartyType aShipTo = aHTD.getShipToTradeParty ();
        if (aShipTo != null)
        {
          aDI.setPartyName (_text (aShipTo.getName ()));
          final ICommonsList <PhinvoiceIdentifier> aLocationIDs = new CommonsArrayList <> ();
          _readPartyIdentifiers (aShipTo, aLocationIDs);
          PhinvoiceReaderHelper.setAndAddRepeated (aLocationIDs,
                                               "BT-71",
                                               aDI::setLocationIdentifier,
                                               aModel.additionalTerms ());
          if (aShipTo.getPostalTradeAddress () != null)
            aDI.setPostalAddress (_readAddress (aShipTo.getPostalTradeAddress ()));
        }
        final SupplyChainEventType aEvent = aHTD.getActualDeliverySupplyChainEvent ();
        if (aEvent != null)
          aDI.setActualDeliveryDate (_dateTime (aEvent.getOccurrenceDateTime (), "BT-72", aModel.additionalTerms (), aErrors));
        if (!aDI.isEmpty ())
          aModel.setDeliveryInformation (aDI);
      }
    }

    final HeaderTradeSettlementType aHTS = aSCTT.getApplicableHeaderTradeSettlement ();
    if (aHTS != null)
    {
      // BT-5 / BT-6
      if (aHTS.getInvoiceCurrencyCode () != null)
        aModel.setCurrency (str (aHTS.getInvoiceCurrencyCode ().getValue ()));
      if (aHTS.getTaxCurrencyCode () != null)
        aModel.setVatAccountingCurrency (str (aHTS.getTaxCurrencyCode ().getValue ()));

      // BT-19 — buyer accounting reference
      PhinvoiceReaderHelper.setAndAddRepeated (new CommonsArrayList <> (aHTS.getReceivableSpecifiedTradeAccountingAccount (),
                                                                    aAccount -> _idValue (aAccount.getID ())),
                                           "BT-19",
                                           aModel::setBuyerAccountingReference,
                                           aModel.additionalTerms ());

      // BG-10 — Payee
      if (aHTS.getPayeeTradeParty () != null)
        aModel.setPayee (_readPayee (aHTS.getPayeeTradeParty (), aModel));

      // BG-3 — preceding invoice references (repeatable since CII D22B)
      for (final ReferencedDocumentType aRD : aHTS.getInvoiceReferencedDocument ())
      {
        final PhinvoicePrecedingInvoiceRef aRef = new PhinvoicePrecedingInvoiceRef ();
        aRef.setNumber (_idValue (aRD.getIssuerAssignedID ()));
        aRef.setIssueDate (_formattedDateTime (aRD.getFormattedIssueDateTime (),
                                               "BT-26",
                                               aModel.additionalTerms (),
                                               aErrors));
        aRef.setTypeCode (_codeValue (aRD.getTypeCode ())); // BT-202
        if (aRef.getNumber () != null || aRef.getIssueDate () != null || aRef.getTypeCode () != null)
          aModel.precedingInvoiceRefs ().add (aRef);
      }

      // BT-167 / BT-167-1 / BT-167-2 — VAT accounting currency exchange rate
      final TradeCurrencyExchangeType aExchange = aHTS.getInvoiceApplicableTradeCurrencyExchange ();
      if (aExchange != null)
      {
        if (aExchange.getConversionRate () != null)
          aModel.setVatExchangeRate (aExchange.getConversionRate ().getValue ());
        aModel.setVatExchangeRateTargetCurrency (_codeValue (aExchange.getTargetCurrencyCode ()));
        aModel.setVatExchangeRateSourceCurrency (_codeValue (aExchange.getSourceCurrencyCode ()));
      }

      // BG-14 — invoicing period
      if (aHTS.getBillingSpecifiedPeriod () != null)
      {
        final PhinvoiceInvoicingPeriod aP = _readPeriod (aHTS.getBillingSpecifiedPeriod (),
                                                     "BT-73",
                                                     "BT-74",
                                                     aModel.additionalTerms (),
                                                     aErrors);
        if (!aP.isEmpty ())
          aModel.setInvoicingPeriod (aP);
      }

      // BG-23 — VAT breakdown + BT-7/BT-8 + main tax total
      for (final TradeTaxType aTT : aHTS.getApplicableTradeTax ())
      {
        final PhinvoiceVatBreakdown aBD = new PhinvoiceVatBreakdown ();
        PhinvoiceReaderHelper.setAndAddRepeated (_getAmounts (aTT.getBasisAmount ()),
                                             "BT-116",
                                             aBD::setTaxableAmount,
                                             aModel.additionalTerms ());
        PhinvoiceReaderHelper.setAndAddRepeated (_getAmounts (aTT.getCalculatedAmount ()),
                                             "BT-117",
                                             aBD::setTaxAmount,
                                             aModel.additionalTerms ());
        aBD.setCategoryCode (_codeValue (aTT.getCategoryCode ()));
        if (aTT.getRateApplicablePercent () != null)
          aBD.setRate (aTT.getRateApplicablePercent ().getValue ());
        aBD.setExemptionReason (_text (aTT.getExemptionReason ()));
        aBD.setExemptionReasonCode (_codeValue (aTT.getExemptionReasonCode ()));
        // BT-184 — only meaningful if it differs from the invoice currency BT-5
        final String sBreakdownCurrency = _codeValue (aTT.getCurrencyCode ());
        if (sBreakdownCurrency != null && !sBreakdownCurrency.equals (aModel.getCurrency ()))
          aBD.setCurrency (sBreakdownCurrency);
        aModel.vatBreakdowns ().add (aBD);

        // BT-7 / BT-8 (carried on the first applicable trade tax, another value is shown as well)
        final LocalDate aVatPointDate = _dateOnly (aTT.getTaxPointDate (), "BT-7", aModel.additionalTerms (), aErrors);
        if (aVatPointDate != null)
          if (aModel.getVatPointDate () == null)
            aModel.setVatPointDate (aVatPointDate);
          else
            if (!aVatPointDate.equals (aModel.getVatPointDate ()))
              aModel.additionalTerms ().add (new PhinvoiceAdditionalTerm ("BT-7", aVatPointDate));
        final String sDueDateTypeCode = _codeValue (aTT.getDueDateTypeCode ());
        if (sDueDateTypeCode != null)
        {
          // A code that the list does not know stays as it is
          final String sMappedCode = EN16931CodeLists.mapDueDateTypeCodeCIIToUBL (sDueDateTypeCode);
          final String sVatPointDateCode = sMappedCode != null ? sMappedCode : sDueDateTypeCode;
          if (aModel.getVatPointDateCode () == null)
            aModel.setVatPointDateCode (sVatPointDateCode);
          else
            if (!sVatPointDateCode.equals (aModel.getVatPointDateCode ()))
              aModel.additionalTerms ().add (new PhinvoiceAdditionalTerm ("BT-8", sVatPointDateCode, "vatpointdate"));
        }
      }

      // BG-20 + BG-21 — document-level allowances/charges
      for (final TradeAllowanceChargeType aAC : aHTS.getSpecifiedTradeAllowanceCharge ())
      {
        if (aAC.getChargeIndicator () != null && Boolean.TRUE.equals (aAC.getChargeIndicator ().isIndicator ()))
          aModel.documentCharges ().add (_readDocCharge (aAC, aModel.additionalTerms ()));
        else
          aModel.documentAllowances ().add (_readDocAllowance (aAC, aModel.additionalTerms ()));
      }

      // BG-16/17/18/19 + BT-9 + BT-20 — payment instructions and terms
      _readPaymentInstructions (aHTS, aModel, aErrors);

      // BG-34 — charges on behalf of a third party (EN 16931:2026)
      for (final FinancialAdjustmentType aFA : aHTS.getSpecifiedFinancialAdjustment ())
      {
        final PhinvoiceThirdPartyCharge aTPC = new PhinvoiceThirdPartyCharge ();
        PhinvoiceReaderHelper.setAndAddRepeated (_getAmounts (aFA.getActualAmount ()),
                                             "BT-179",
                                             aTPC::setAmount,
                                             aModel.additionalTerms ());
        aTPC.setSpecification (_joinTexts (aFA.getReason ())); // BT-180
        if (aTPC.getAmount () != null || aTPC.getSpecification () != null)
          aModel.getDocumentTotals ().thirdPartyCharges ().add (aTPC);
      }

      // BG-22 — totals
      if (aHTS.getSpecifiedTradeSettlementHeaderMonetarySummation () != null)
        _readTotals (aHTS.getSpecifiedTradeSettlementHeaderMonetarySummation (), aModel);
    }

    // BG-25 — lines
    for (final SupplyChainTradeLineItemType aLine : aSCTT.getIncludedSupplyChainTradeLineItem ())
    {
      final PhinvoiceInvoiceLine aModelLine = _readLine (aLine, aErrors);
      _readLineExtended (aLine, aModelLine, aErrors);
      aModel.lines ().add (aModelLine);
    }

    // The terms of the Factur-X profile EXTENDED
    _readHeaderExtended (aCii, aModel, aErrors);
    return aModel;
  }
}
