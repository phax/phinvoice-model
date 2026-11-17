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
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.base.equals.EqualsHelper;
import com.helger.en16931.basics.EEN16931DateFormatCode;
import com.helger.en16931.basics.codelist.EN16931CodeLists;
import com.helger.phinvoice.model.PhinvoiceAttachment;
import com.helger.phinvoice.model.PhinvoiceBuyer;
import com.helger.phinvoice.model.PhinvoiceContact;
import com.helger.phinvoice.model.PhinvoiceCreditTransfer;
import com.helger.phinvoice.model.PhinvoiceDeliveryInformation;
import com.helger.phinvoice.model.PhinvoiceDirectDebit;
import com.helger.phinvoice.model.PhinvoiceDocumentAllowance;
import com.helger.phinvoice.model.PhinvoiceDocumentCharge;
import com.helger.phinvoice.model.PhinvoiceDocumentTotals;
import com.helger.phinvoice.model.PhinvoiceEarlyPaymentDiscount;
import com.helger.phinvoice.model.PhinvoiceIdentifier;
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
import com.helger.phinvoice.model.PhinvoicePayee;
import com.helger.phinvoice.model.PhinvoicePaymentCard;
import com.helger.phinvoice.model.PhinvoicePaymentInstructions;
import com.helger.phinvoice.model.PhinvoicePaymentTerms;
import com.helger.phinvoice.model.PhinvoicePostalAddress;
import com.helger.phinvoice.model.PhinvoicePrecedingInvoiceRef;
import com.helger.phinvoice.model.PhinvoicePriceDetails;
import com.helger.phinvoice.model.PhinvoiceSeller;
import com.helger.phinvoice.model.PhinvoiceSellerTaxRepresentative;
import com.helger.phinvoice.model.PhinvoiceSupportingDocument;
import com.helger.phinvoice.model.PhinvoiceThirdPartyCharge;
import com.helger.phinvoice.model.PhinvoiceVatBreakdown;
import com.helger.phinvoice.write.PhinvoiceExtendedTermReporter;
import com.helger.phinvoice.write.PhinvoiceTermFilter;

import un.unece.uncefact.data.standard.cii.d25a.CrossIndustryInvoiceType;
import un.unece.uncefact.data.standard.cii.d25a.qdt.AllowanceChargeReasonCodeType;
import un.unece.uncefact.data.standard.cii.d25a.qdt.FormattedDateTimeType;
import un.unece.uncefact.data.standard.cii.d25a.rabie.CreditorFinancialAccountType;
import un.unece.uncefact.data.standard.cii.d25a.rabie.CreditorFinancialInstitutionType;
import un.unece.uncefact.data.standard.cii.d25a.rabie.DebtorFinancialAccountType;
import un.unece.uncefact.data.standard.cii.d25a.rabie.DebtorFinancialInstitutionType;
import un.unece.uncefact.data.standard.cii.d25a.rabie.DocumentContextParameterType;
import un.unece.uncefact.data.standard.cii.d25a.rabie.DocumentLineDocumentType;
import un.unece.uncefact.data.standard.cii.d25a.rabie.ExchangedDocumentContextType;
import un.unece.uncefact.data.standard.cii.d25a.rabie.ExchangedDocumentType;
import un.unece.uncefact.data.standard.cii.d25a.rabie.FinancialAdjustmentType;
import un.unece.uncefact.data.standard.cii.d25a.rabie.HeaderTradeAgreementType;
import un.unece.uncefact.data.standard.cii.d25a.rabie.HeaderTradeDeliveryType;
import un.unece.uncefact.data.standard.cii.d25a.rabie.HeaderTradeSettlementType;
import un.unece.uncefact.data.standard.cii.d25a.rabie.LegalOrganizationType;
import un.unece.uncefact.data.standard.cii.d25a.rabie.LineTradeAgreementType;
import un.unece.uncefact.data.standard.cii.d25a.rabie.LineTradeDeliveryType;
import un.unece.uncefact.data.standard.cii.d25a.rabie.LineTradeSettlementType;
import un.unece.uncefact.data.standard.cii.d25a.rabie.NoteType;
import un.unece.uncefact.data.standard.cii.d25a.rabie.ProcuringProjectType;
import un.unece.uncefact.data.standard.cii.d25a.rabie.ProductCharacteristicType;
import un.unece.uncefact.data.standard.cii.d25a.rabie.ProductClassificationType;
import un.unece.uncefact.data.standard.cii.d25a.rabie.ReferencedDocumentType;
import un.unece.uncefact.data.standard.cii.d25a.rabie.SpecifiedPeriodType;
import un.unece.uncefact.data.standard.cii.d25a.rabie.SupplyChainEventType;
import un.unece.uncefact.data.standard.cii.d25a.rabie.SupplyChainTradeLineItemType;
import un.unece.uncefact.data.standard.cii.d25a.rabie.SupplyChainTradeTransactionType;
import un.unece.uncefact.data.standard.cii.d25a.rabie.TaxRegistrationType;
import un.unece.uncefact.data.standard.cii.d25a.rabie.TradeAccountingAccountType;
import un.unece.uncefact.data.standard.cii.d25a.rabie.TradeAddressType;
import un.unece.uncefact.data.standard.cii.d25a.rabie.TradeAllowanceChargeType;
import un.unece.uncefact.data.standard.cii.d25a.rabie.TradeContactType;
import un.unece.uncefact.data.standard.cii.d25a.rabie.TradeCountryType;
import un.unece.uncefact.data.standard.cii.d25a.rabie.TradeCurrencyExchangeType;
import un.unece.uncefact.data.standard.cii.d25a.rabie.TradePartyType;
import un.unece.uncefact.data.standard.cii.d25a.rabie.TradePaymentDiscountTermsType;
import un.unece.uncefact.data.standard.cii.d25a.rabie.TradePaymentPenaltyTermsType;
import un.unece.uncefact.data.standard.cii.d25a.rabie.TradePaymentTermsType;
import un.unece.uncefact.data.standard.cii.d25a.rabie.TradePriceType;
import un.unece.uncefact.data.standard.cii.d25a.rabie.TradeProductType;
import un.unece.uncefact.data.standard.cii.d25a.rabie.TradeSettlementFinancialCardType;
import un.unece.uncefact.data.standard.cii.d25a.rabie.TradeSettlementHeaderMonetarySummationType;
import un.unece.uncefact.data.standard.cii.d25a.rabie.TradeSettlementLineMonetarySummationType;
import un.unece.uncefact.data.standard.cii.d25a.rabie.TradeSettlementPaymentMeansType;
import un.unece.uncefact.data.standard.cii.d25a.rabie.TradeTaxType;
import un.unece.uncefact.data.standard.cii.d25a.rabie.UniversalCommunicationType;
import un.unece.uncefact.data.standard.cii.d25a.udt.AmountType;
import un.unece.uncefact.data.standard.cii.d25a.udt.BinaryObjectType;
import un.unece.uncefact.data.standard.cii.d25a.udt.CodeType;
import un.unece.uncefact.data.standard.cii.d25a.udt.DateTimeType;
import un.unece.uncefact.data.standard.cii.d25a.udt.DateType;
import un.unece.uncefact.data.standard.cii.d25a.udt.IDType;
import un.unece.uncefact.data.standard.cii.d25a.udt.IndicatorType;
import un.unece.uncefact.data.standard.cii.d25a.udt.MeasureType;
import un.unece.uncefact.data.standard.cii.d25a.udt.PercentType;
import un.unece.uncefact.data.standard.cii.d25a.udt.QuantityType;
import un.unece.uncefact.data.standard.cii.d25a.udt.TextType;

/**
 * Writes a {@link PhinvoiceInvoice} as CII D25A, for the target EN 16931:2026. It is the inverse of
 * {@link CiiD25AInvoiceReader}. Every value goes through the {@link PhinvoiceTermFilter}, so only
 * the terms that the target allows are written and all other values are reported.
 * Package-private worker of {@link CiiInvoiceWriter}.
 *
 * @author Philip Helger
 */
final class CiiD25AInvoiceWriter
{
  /** The list agency of the UNTDID code lists (UN/ECE), a syntax constant of the non-VAT tax codes */
  private static final String LIST_AGENCY_ID_UNECE = "6";

  /** The tax type code of all VAT taxes (BT-95-1, BT-102-1, BT-118-1, BT-151-1) */
  private static final String TAX_TYPE_CODE_VAT = "VAT";

  /** The scheme identifier of a tax registration that is no VAT identifier (BT-32) */
  private static final String TAX_SCHEME_FC = "FC";

  /** The formatter of format code 208 (BT-2 with BT-166) */
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern (EEN16931DateFormatCode.CCYYMMDDHHMMSSZHHMM.getJavaPattern (),
                                                                                           Locale.ROOT);

  private final PhinvoiceTermFilter m_aFilter;
  private final boolean m_bRaw;

  /**
   * Constructor.
   *
   * @param aFilter
   *        The term filter. May not be <code>null</code>.
   * @param bRaw
   *        <code>true</code> to skip the syntax specific rules that drop values of the model (BT-6
   *        equal to BT-5). Only for {@link CiiUnmappedValueCollector}.
   */
  CiiD25AInvoiceWriter (@NonNull final PhinvoiceTermFilter aFilter, final boolean bRaw)
  {
    m_aFilter = aFilter;
    m_bRaw = bRaw;
  }

  @NonNull
  private static IDType _id (@NonNull final String sValue, @Nullable final String sSchemeID)
  {
    final IDType ret = new IDType (sValue);
    ret.setSchemeID (sSchemeID);
    return ret;
  }

  @NonNull
  private static IDType _id (@NonNull final PhinvoiceIdentifier aID)
  {
    return _id (aID.getValue (), aID.getSchemeID ());
  }

  @NonNull
  private static AmountType _amount (@NonNull final BigDecimal aValue)
  {
    return new AmountType (aValue);
  }

  @NonNull
  private static String _date102 (@NonNull final LocalDate aDate)
  {
    return EEN16931DateFormatCode.CCYYMMDD.getAsString (aDate);
  }

  @NonNull
  private static DateTimeType _dateTime (@NonNull final LocalDate aDate)
  {
    final DateTimeType.DateTimeString aDTS = new DateTimeType.DateTimeString (_date102 (aDate));
    aDTS.setFormat (EEN16931DateFormatCode.CCYYMMDD.getID ());
    final DateTimeType ret = new DateTimeType ();
    ret.setDateTimeString (aDTS);
    return ret;
  }

  @NonNull
  private static DateType _date (@NonNull final LocalDate aDate)
  {
    final DateType.DateString aDS = new DateType.DateString (_date102 (aDate));
    aDS.setFormat (EEN16931DateFormatCode.CCYYMMDD.getID ());
    final DateType ret = new DateType ();
    ret.setDateString (aDS);
    return ret;
  }

  @NonNull
  private static FormattedDateTimeType _formattedDateTime (@NonNull final LocalDate aDate)
  {
    final FormattedDateTimeType.DateTimeString aDTS = new FormattedDateTimeType.DateTimeString (_date102 (aDate));
    aDTS.setFormat (EEN16931DateFormatCode.CCYYMMDD.getID ());
    final FormattedDateTimeType ret = new FormattedDateTimeType ();
    ret.setDateTimeString (aDTS);
    return ret;
  }

  @NonNull
  private static IndicatorType _indicator (final boolean bValue)
  {
    final IndicatorType ret = new IndicatorType ();
    ret.setIndicator (Boolean.valueOf (bValue));
    return ret;
  }

  @NonNull
  private static TradeTaxType _vatTax ()
  {
    final TradeTaxType ret = new TradeTaxType ();
    ret.setTypeCode (TAX_TYPE_CODE_VAT);
    return ret;
  }

  @NonNull
  private static ReferencedDocumentType _referencedDocument (@NonNull final String sIssuerAssignedID)
  {
    final ReferencedDocumentType ret = new ReferencedDocumentType ();
    ret.setIssuerAssignedID (sIssuerAssignedID);
    return ret;
  }

  /**
   * Report a value that CII cannot carry (a UBL only term). The filter never allows such a term for
   * a CII target.
   */
  private void _reportUnsupported (@NonNull final String sTermID, @Nullable final Object aValue)
  {
    if (m_aFilter.use (sTermID, aValue))
      throw new IllegalStateException ("The term " + sTermID + " cannot be written as CII D25A");
  }

  /**
   * Check if an identifier is to be written. An identifier without value but with a scheme cannot
   * be written, and its scheme is reported.
   */
  private boolean _useIdentifier (@Nullable final PhinvoiceIdentifier aID,
                                  @NonNull final String sTermID,
                                  @NonNull final String sSchemeTermID)
  {
    if (aID == null)
      return false;
    if (m_aFilter.use (sTermID, aID.getValue ()))
      return true;
    if (aID.getValue () == null && aID.getSchemeID () != null)
      m_aFilter.drop (sSchemeTermID, aID.getSchemeID (), "the identifier (" + sTermID + ") itself is missing");
    return false;
  }

  @Nullable
  private TradeAddressType _address (@Nullable final PhinvoicePostalAddress aAddr,
                                     @NonNull final String sGroupID,
                                     @NonNull final String sLine1,
                                     @NonNull final String sLine2,
                                     @NonNull final String sLine3,
                                     @NonNull final String sCity,
                                     @NonNull final String sPostCode,
                                     @NonNull final String sSubdivision,
                                     @NonNull final String sCountry)
  {
    if (aAddr == null || aAddr.isEmpty () || !m_aFilter.useGroup (sGroupID, "postal address"))
      return null;

    final TradeAddressType ret = new TradeAddressType ();
    if (m_aFilter.use (sPostCode, aAddr.getPostCode ()))
      ret.setPostcodeCode (aAddr.getPostCode ());
    if (m_aFilter.use (sLine1, aAddr.getLine1 ()))
      ret.setLineOne (aAddr.getLine1 ());
    if (m_aFilter.use (sLine2, aAddr.getLine2 ()))
      ret.setLineTwo (aAddr.getLine2 ());
    if (m_aFilter.use (sLine3, aAddr.getLine3 ()))
      ret.setLineThree (aAddr.getLine3 ());
    if (m_aFilter.use (sCity, aAddr.getCity ()))
      ret.setCityName (aAddr.getCity ());
    if (m_aFilter.use (sCountry, aAddr.getCountryCode ()))
      ret.setCountryID (aAddr.getCountryCode ());
    if (m_aFilter.use (sSubdivision, aAddr.getCountrySubdivision ()))
      ret.addCountrySubDivisionName (new TextType (aAddr.getCountrySubdivision ()));
    return ret;
  }

  @Nullable
  private TradeContactType _contact (@Nullable final PhinvoiceContact aContact,
                                     @NonNull final String sGroupID,
                                     @NonNull final String sName,
                                     @NonNull final String sDepartment,
                                     @NonNull final String sTelephone,
                                     @NonNull final String sEmail)
  {
    if (aContact == null || aContact.isEmpty () || !m_aFilter.useGroup (sGroupID, "contact"))
      return null;

    final TradeContactType ret = new TradeContactType ();
    if (m_aFilter.use (sName, aContact.getName ()))
      ret.setPersonName (aContact.getName ());
    if (m_aFilter.use (sDepartment, aContact.getDepartmentName ()))
      ret.setDepartmentName (aContact.getDepartmentName ());
    if (m_aFilter.use (sTelephone, aContact.getTelephone ()))
    {
      final UniversalCommunicationType aUC = new UniversalCommunicationType ();
      aUC.setCompleteNumber (aContact.getTelephone ());
      ret.setTelephoneUniversalCommunication (aUC);
    }
    if (m_aFilter.use (sEmail, aContact.getEmail ()))
    {
      final UniversalCommunicationType aUC = new UniversalCommunicationType ();
      aUC.setURIID (aContact.getEmail ());
      ret.setEmailURIUniversalCommunication (aUC);
    }
    return ret;
  }

  private void _partyIdentifiers (@NonNull final List <PhinvoiceIdentifier> aIDs,
                                  @NonNull final String sTermID,
                                  @NonNull final TradePartyType aTP)
  {
    // Since EN 16931:2026 only ram:GlobalID carries the party identifiers
    for (final PhinvoiceIdentifier aID : aIDs)
      if (_useIdentifier (aID, sTermID, sTermID + "-1"))
        aTP.addGlobalID (_id (aID));
  }

  private void _electronicAddress (@Nullable final PhinvoiceIdentifier aID,
                                   @NonNull final String sTermID,
                                   @NonNull final TradePartyType aTP)
  {
    if (_useIdentifier (aID, sTermID, sTermID + "-1"))
    {
      final UniversalCommunicationType aUC = new UniversalCommunicationType ();
      aUC.setURIID (_id (aID));
      aTP.addURIUniversalCommunication (aUC);
    }
  }

  private static void _taxRegistration (@NonNull final String sValue,
                                        @NonNull final String sSchemeID,
                                        @NonNull final TradePartyType aTP)
  {
    final TaxRegistrationType aTR = new TaxRegistrationType ();
    aTR.setID (_id (sValue, sSchemeID));
    aTP.addSpecifiedTaxRegistration (aTR);
  }

  @NonNull
  private LegalOrganizationType _legalOrganisation (@NonNull final TradePartyType aTP)
  {
    if (aTP.getSpecifiedLegalOrganization () == null)
      aTP.setSpecifiedLegalOrganization (new LegalOrganizationType ());
    return aTP.getSpecifiedLegalOrganization ();
  }

  @NonNull
  private TradePartyType _seller (@NonNull final PhinvoiceSeller aSeller)
  {
    final TradePartyType ret = new TradePartyType ();
    _partyIdentifiers (aSeller.identifiers (), "BT-29", ret);
    if (m_aFilter.use ("BT-27", aSeller.getName ()))
      ret.setName (aSeller.getName ());
    if (m_aFilter.use ("BT-33", aSeller.getAdditionalLegalInfo ()))
      ret.addDescription (new TextType (aSeller.getAdditionalLegalInfo ()));
    final PhinvoiceIdentifier aLegal = aSeller.getLegalRegistration ();
    if (_useIdentifier (aLegal, "BT-30", "BT-30-1"))
      _legalOrganisation (ret).setID (_id (aLegal));
    if (m_aFilter.use ("BT-28", aSeller.getTradingName ()))
      _legalOrganisation (ret).setTradingBusinessName (aSeller.getTradingName ());
    final TradeContactType aContact = _contact (aSeller.getContact (), "BG-6", "BT-41", "BT-41", "BT-42", "BT-43");
    if (aContact != null)
      ret.addDefinedTradeContact (aContact);
    ret.setPostalTradeAddress (_address (aSeller.getPostalAddress (),
                                         "BG-5",
                                         "BT-35",
                                         "BT-36",
                                         "BT-162",
                                         "BT-37",
                                         "BT-38",
                                         "BT-39",
                                         "BT-40"));
    _electronicAddress (aSeller.getElectronicAddress (), "BT-34", ret);
    if (m_aFilter.use ("BT-31", aSeller.getVatIdentifier ()))
      _taxRegistration (aSeller.getVatIdentifier (), CiiD25AInvoiceReader.TAX_SCHEME_VAT, ret);
    if (m_aFilter.use ("BT-32", aSeller.getTaxRegistration ()))
    {
      // BT-32-1 - "FC" if there is none
      final String sSchemeID = m_aFilter.use ("BT-32-1", aSeller.getTaxRegistrationSchemeID ()) ? aSeller.getTaxRegistrationSchemeID ()
                                                                                                 : TAX_SCHEME_FC;
      _taxRegistration (aSeller.getTaxRegistration (), sSchemeID, ret);
    }
    m_aFilter.dropAdditionalTerms (aSeller.additionalTerms ());
    return ret;
  }

  @NonNull
  private TradePartyType _buyer (@NonNull final PhinvoiceBuyer aBuyer)
  {
    final TradePartyType ret = new TradePartyType ();
    _partyIdentifiers (aBuyer.identifiers (), "BT-46", ret);
    if (m_aFilter.use ("BT-44", aBuyer.getName ()))
      ret.setName (aBuyer.getName ());
    final PhinvoiceIdentifier aLegal = aBuyer.getLegalRegistration ();
    if (_useIdentifier (aLegal, "BT-47", "BT-47-1"))
      _legalOrganisation (ret).setID (_id (aLegal));
    if (m_aFilter.use ("BT-45", aBuyer.getTradingName ()))
      _legalOrganisation (ret).setTradingBusinessName (aBuyer.getTradingName ());
    final TradeContactType aContact = _contact (aBuyer.getContact (), "BG-9", "BT-56", "BT-56", "BT-57", "BT-58");
    if (aContact != null)
      ret.addDefinedTradeContact (aContact);
    ret.setPostalTradeAddress (_address (aBuyer.getPostalAddress (),
                                         "BG-8",
                                         "BT-50",
                                         "BT-51",
                                         "BT-163",
                                         "BT-52",
                                         "BT-53",
                                         "BT-54",
                                         "BT-55"));
    _electronicAddress (aBuyer.getElectronicAddress (), "BT-49", ret);
    if (m_aFilter.use ("BT-48", aBuyer.getVatIdentifier ()))
      _taxRegistration (aBuyer.getVatIdentifier (), CiiD25AInvoiceReader.TAX_SCHEME_VAT, ret);
    m_aFilter.dropAdditionalTerms (aBuyer.additionalTerms ());
    return ret;
  }

  @Nullable
  private TradePartyType _payee (@Nullable final PhinvoicePayee aPayee)
  {
    if (aPayee == null || !m_aFilter.useGroup ("BG-10", "payee"))
      return null;

    final TradePartyType ret = new TradePartyType ();
    final PhinvoiceIdentifier aID = aPayee.getIdentifier ();
    if (aID != null)
      _partyIdentifiers (List.of (aID), "BT-60", ret);
    if (m_aFilter.use ("BT-59", aPayee.getName ()))
      ret.setName (aPayee.getName ());
    final PhinvoiceIdentifier aLegal = aPayee.getLegalRegistration ();
    if (_useIdentifier (aLegal, "BT-61", "BT-61-1"))
      _legalOrganisation (ret).setID (_id (aLegal));
    return ret;
  }

  @Nullable
  private TradePartyType _taxRepresentative (@Nullable final PhinvoiceSellerTaxRepresentative aTaxRep)
  {
    if (aTaxRep == null || !m_aFilter.useGroup ("BG-11", "seller tax representative"))
      return null;

    final TradePartyType ret = new TradePartyType ();
    if (m_aFilter.use ("BT-62", aTaxRep.getName ()))
      ret.setName (aTaxRep.getName ());
    ret.setPostalTradeAddress (_address (aTaxRep.getPostalAddress (),
                                         "BG-12",
                                         "BT-64",
                                         "BT-65",
                                         "BT-164",
                                         "BT-66",
                                         "BT-67",
                                         "BT-68",
                                         "BT-69"));
    if (m_aFilter.use ("BT-63", aTaxRep.getVatIdentifier ()))
      _taxRegistration (aTaxRep.getVatIdentifier (), CiiD25AInvoiceReader.TAX_SCHEME_VAT, ret);
    return ret;
  }

  @NonNull
  private ExchangedDocumentContextType _documentContext (@NonNull final PhinvoiceInvoice aInvoice,
                                                         @NonNull final String sSpecificationIdentifier)
  {
    final ExchangedDocumentContextType ret = new ExchangedDocumentContextType ();
    final String sProcessType = aInvoice.getProcessControl ().getBusinessProcessType ();
    if (m_aFilter.use ("BT-23", sProcessType))
    {
      final DocumentContextParameterType aParam = new DocumentContextParameterType ();
      aParam.setID (sProcessType);
      ret.addBusinessProcessSpecifiedDocumentContextParameter (aParam);
    }
    final DocumentContextParameterType aGuideline = new DocumentContextParameterType ();
    aGuideline.setID (sSpecificationIdentifier);
    ret.addGuidelineSpecifiedDocumentContextParameter (aGuideline);
    return ret;
  }

  @NonNull
  private ExchangedDocumentType _document (@NonNull final PhinvoiceInvoice aInvoice)
  {
    final ExchangedDocumentType ret = new ExchangedDocumentType ();
    if (m_aFilter.use ("BT-1", aInvoice.getNumber ()))
      ret.setID (aInvoice.getNumber ());
    if (m_aFilter.use ("BT-3", aInvoice.getTypeCode ()))
      ret.setTypeCode (aInvoice.getTypeCode ());
    if (m_aFilter.use ("BT-2", aInvoice.getIssueDate ()))
    {
      if (m_aFilter.use ("BT-166", aInvoice.getIssueTime ()))
      {
        // BT-2 and BT-166 share one element with format code 208
        final OffsetTime aTime = aInvoice.getIssueTime ();
        final OffsetDateTime aDT = OffsetDateTime.of (aInvoice.getIssueDate (), aTime.toLocalTime (), aTime.getOffset ());
        final DateTimeType.DateTimeString aDTS = new DateTimeType.DateTimeString (DATE_TIME_FORMATTER.format (aDT));
        aDTS.setFormat (EEN16931DateFormatCode.CCYYMMDDHHMMSSZHHMM.getID ());
        final DateTimeType aDTT = new DateTimeType ();
        aDTT.setDateTimeString (aDTS);
        ret.setIssueDateTime (aDTT);
      }
      else
        ret.setIssueDateTime (_dateTime (aInvoice.getIssueDate ()));
    }
    else
      if (aInvoice.getIssueTime () != null)
        m_aFilter.drop ("BT-166", aInvoice.getIssueTime (), "the issue date (BT-2) is missing");
    if (m_aFilter.useGroup ("BG-1", aInvoice.invoiceNotes ()))
      for (final PhinvoiceInvoiceNote aNote : aInvoice.invoiceNotes ())
      {
        final NoteType aNT = new NoteType ();
        if (m_aFilter.use ("BT-22", aNote.getContent ()))
          aNT.addContent (new TextType (aNote.getContent ()));
        if (m_aFilter.use ("BT-21", aNote.getSubjectCode ()))
          aNT.addSubjectCode (new CodeType (aNote.getSubjectCode ()));
        if (!aNT.getContent ().isEmpty () || !aNT.getSubjectCode ().isEmpty ())
          ret.addIncludedNote (aNT);
      }
    return ret;
  }

  private void _addDocumentReference (@Nullable final String sValue,
                                      @NonNull final String sTypeCode,
                                      @Nullable final String sReferenceTypeCode,
                                      @NonNull final HeaderTradeAgreementType aHTA)
  {
    final ReferencedDocumentType aRD = _referencedDocument (sValue);
    aRD.setTypeCode (sTypeCode);
    if (sReferenceTypeCode != null)
      aRD.setReferenceTypeCode (sReferenceTypeCode);
    aHTA.addAdditionalReferencedDocument (aRD);
  }

  private void _supportingDocument (@NonNull final PhinvoiceSupportingDocument aDoc,
                                    @NonNull final HeaderTradeAgreementType aHTA)
  {
    if (!m_aFilter.use ("BT-122", aDoc.getReference ()))
    {
      if (aDoc.getReference () == null && !aDoc.isEmpty ())
        m_aFilter.drop ("BG-24", aDoc.getDescription (), "the mandatory supporting document reference (BT-122) is missing");
      return;
    }

    final ReferencedDocumentType aRD = _referencedDocument (aDoc.getReference ());
    if (m_aFilter.use ("BT-124", aDoc.getExternalLocation ()))
      aRD.setURIID (aDoc.getExternalLocation ());
    // BT-122-1 - 916 if there is none
    aRD.setTypeCode (m_aFilter.use ("BT-122-1", aDoc.getDocumentTypeCode ()) ? aDoc.getDocumentTypeCode ()
                                                                             : EN16931CodeLists.DOCUMENT_TYPE_CODE_SUPPORTING_DOCUMENT);
    if (m_aFilter.use ("BT-123", aDoc.getDescription ()))
      aRD.addName (new TextType (aDoc.getDescription ()));
    final PhinvoiceAttachment aAttachment = aDoc.getAttachment ();
    if (aAttachment != null && m_aFilter.use ("BT-125", aAttachment.getBinary ()))
    {
      final BinaryObjectType aBO = new BinaryObjectType (aAttachment.getBinary ());
      if (m_aFilter.use ("BT-125-1", aAttachment.getMimeCode ()))
        aBO.setMimeCode (aAttachment.getMimeCode ());
      if (m_aFilter.use ("BT-125-2", aAttachment.getFilename ()))
        aBO.setFilename (aAttachment.getFilename ());
      aRD.addAttachmentBinaryObject (aBO);
    }
    aHTA.addAdditionalReferencedDocument (aRD);
  }

  @NonNull
  private HeaderTradeAgreementType _agreement (@NonNull final PhinvoiceInvoice aInvoice)
  {
    final HeaderTradeAgreementType ret = new HeaderTradeAgreementType ();
    // BT-10 / BT-10-1 are repeatable since EN 16931:2026
    for (final PhinvoiceIdentifier aBuyerRef : aInvoice.buyerReferences ())
      if (_useIdentifier (aBuyerRef, "BT-10", "BT-10-1"))
        ret.addBuyerReferenceID (_id (aBuyerRef.getValue (),
                                      m_aFilter.use ("BT-10-1", aBuyerRef.getSchemeID ()) ? aBuyerRef.getSchemeID () : null));
    ret.setSellerTradeParty (_seller (aInvoice.getSeller ()));
    ret.setBuyerTradeParty (_buyer (aInvoice.getBuyer ()));
    ret.setSellerTaxRepresentativeTradeParty (_taxRepresentative (aInvoice.getSellerTaxRepresentative ()));
    if (m_aFilter.use ("BT-14", aInvoice.getSalesOrderReference ()))
      ret.setSellerOrderReferencedDocument (_referencedDocument (aInvoice.getSalesOrderReference ()));
    if (m_aFilter.use ("BT-13", aInvoice.getPurchaseOrderReference ()))
      ret.setBuyerOrderReferencedDocument (_referencedDocument (aInvoice.getPurchaseOrderReference ()));
    if (m_aFilter.use ("BT-12", aInvoice.getContractReference ()))
      ret.setContractReferencedDocument (_referencedDocument (aInvoice.getContractReference ()));
    if (m_aFilter.useGroup ("BG-24", aInvoice.supportingDocuments ()))
      for (final PhinvoiceSupportingDocument aDoc : aInvoice.supportingDocuments ())
        _supportingDocument (aDoc, ret);
    if (m_aFilter.use ("BT-17", aInvoice.getTenderOrLotReference ()))
      _addDocumentReference (aInvoice.getTenderOrLotReference (),
                             EN16931CodeLists.DOCUMENT_TYPE_CODE_ORIGINATOR_DOCUMENT,
                             null,
                             ret);
    final PhinvoiceIdentifier aObjectID = aInvoice.getInvoicedObjectIdentifier ();
    if (_useIdentifier (aObjectID, "BT-18", "BT-18-1"))
      _addDocumentReference (aObjectID.getValue (),
                             EN16931CodeLists.DOCUMENT_TYPE_CODE_OBJECT_DOCUMENT,
                             m_aFilter.use ("BT-18-1", aObjectID.getSchemeID ()) ? aObjectID.getSchemeID () : null,
                             ret);
    if (m_aFilter.use ("BT-11", aInvoice.getProjectReference ()))
    {
      // BT-11-1 is mandatory in CII: without a project name BT-11 is repeated
      final ProcuringProjectType aProject = new ProcuringProjectType ();
      aProject.setID (aInvoice.getProjectReference ());
      aProject.setName (m_aFilter.use ("BT-11-1", aInvoice.getProjectName ()) ? aInvoice.getProjectName ()
                                                                              : aInvoice.getProjectReference ());
      ret.setSpecifiedProcuringProject (aProject);
    }
    else
      if (aInvoice.getProjectName () != null)
        m_aFilter.drop ("BT-11-1", aInvoice.getProjectName (), "the project reference (BT-11) is missing");
    return ret;
  }

  @Nullable
  private TradePartyType _shipTo (@Nullable final String sName,
                                  @Nullable final PhinvoiceIdentifier aLocationID,
                                  @Nullable final PhinvoicePostalAddress aAddress,
                                  @NonNull final String sNameTerm,
                                  @NonNull final String sLocationTerm,
                                  @NonNull final String sAddressGroup,
                                  @NonNull final String [] aAddressTerms)
  {
    final TradePartyType ret = new TradePartyType ();
    if (aLocationID != null)
      _partyIdentifiers (List.of (aLocationID), sLocationTerm, ret);
    if (m_aFilter.use (sNameTerm, sName))
      ret.setName (sName);
    ret.setPostalTradeAddress (_address (aAddress,
                                         sAddressGroup,
                                         aAddressTerms[0],
                                         aAddressTerms[1],
                                         aAddressTerms[2],
                                         aAddressTerms[3],
                                         aAddressTerms[4],
                                         aAddressTerms[5],
                                         aAddressTerms[6]));
    if (ret.getID ().isEmpty () && ret.getGlobalID ().isEmpty () && ret.getName () == null && ret.getPostalTradeAddress () == null)
      return null;
    return ret;
  }

  @NonNull
  private HeaderTradeDeliveryType _delivery (@NonNull final PhinvoiceInvoice aInvoice)
  {
    // The element is mandatory in CII, even if it is empty
    final HeaderTradeDeliveryType ret = new HeaderTradeDeliveryType ();
    final PhinvoiceDeliveryInformation aDI = aInvoice.getDeliveryInformation ();
    if (aDI != null && !aDI.isEmpty () && m_aFilter.useGroup ("BG-13", "delivery information"))
    {
      ret.setShipToTradeParty (_shipTo (aDI.getPartyName (),
                                        aDI.getLocationIdentifier (),
                                        aDI.getPostalAddress (),
                                        "BT-70",
                                        "BT-71",
                                        "BG-15",
                                        new String [] { "BT-75", "BT-76", "BT-165", "BT-77", "BT-78", "BT-79", "BT-80" }));
      if (m_aFilter.use ("BT-72", aDI.getActualDeliveryDate ()))
      {
        final SupplyChainEventType aEvent = new SupplyChainEventType ();
        aEvent.setOccurrenceDateTime (_dateTime (aDI.getActualDeliveryDate ()));
        ret.setActualDeliverySupplyChainEvent (aEvent);
      }
    }
    if (m_aFilter.use ("BT-16", aInvoice.getDespatchAdviceReference ()))
      ret.setDespatchAdviceReferencedDocument (_referencedDocument (aInvoice.getDespatchAdviceReference ()));
    if (m_aFilter.use ("BT-15", aInvoice.getReceivingAdviceReference ()))
      ret.setReceivingAdviceReferencedDocument (_referencedDocument (aInvoice.getReceivingAdviceReference ()));
    if (m_aFilter.use ("BT-197", aInvoice.getDeliveryNoteReference ()))
      ret.setDeliveryNoteReferencedDocument (_referencedDocument (aInvoice.getDeliveryNoteReference ()));
    return ret;
  }

  @NonNull
  private static String _dueDateTypeCode (@NonNull final String sUBLCode)
  {
    // The model has the UBL code (UNTDID 2005), CII uses UNTDID 2475
    final String sMapped = EN16931CodeLists.mapDueDateTypeCodeUBLToCII (sUBLCode);
    return sMapped != null ? sMapped : sUBLCode;
  }

  private void _vatBreakdowns (@NonNull final PhinvoiceInvoice aInvoice, @NonNull final HeaderTradeSettlementType aHTS)
  {
    final boolean bVatPointDate = m_aFilter.use ("BT-7", aInvoice.getVatPointDate ());
    final boolean bVatPointDateCode = m_aFilter.use ("BT-8", aInvoice.getVatPointDateCode ());
    if (!m_aFilter.useGroup ("BG-23", aInvoice.vatBreakdowns ()))
    {
      if (aInvoice.vatBreakdowns ().isEmpty ())
      {
        // CII carries BT-7 and BT-8 in the VAT breakdown
        if (bVatPointDate)
          m_aFilter.drop ("BT-7", aInvoice.getVatPointDate (), "there is no VAT breakdown to carry it");
        if (bVatPointDateCode)
          m_aFilter.drop ("BT-8", aInvoice.getVatPointDateCode (), "there is no VAT breakdown to carry it");
      }
      return;
    }

    boolean bFirst = true;
    for (final PhinvoiceVatBreakdown aBD : aInvoice.vatBreakdowns ())
    {
      final TradeTaxType aTT = _vatTax ();
      if (m_aFilter.use ("BT-117", aBD.getTaxAmount ()))
        aTT.addCalculatedAmount (_amount (aBD.getTaxAmount ()));
      if (m_aFilter.use ("BT-120", aBD.getExemptionReason ()))
        aTT.setExemptionReason (aBD.getExemptionReason ());
      if (m_aFilter.use ("BT-116", aBD.getTaxableAmount ()))
        aTT.addBasisAmount (_amount (aBD.getTaxableAmount ()));
      if (m_aFilter.use ("BT-118", aBD.getCategoryCode ()))
        aTT.setCategoryCode (aBD.getCategoryCode ());
      if (m_aFilter.use ("BT-121", aBD.getExemptionReasonCode ()))
        aTT.setExemptionReasonCode (aBD.getExemptionReasonCode ());
      // BT-7 / BT-8 are on document level, CII carries them in the first VAT breakdown only
      // (CII-SR-461)
      if (bFirst && bVatPointDate)
        aTT.setTaxPointDate (_date (aInvoice.getVatPointDate ()));
      if (bFirst && bVatPointDateCode)
        aTT.setDueDateTypeCode (_dueDateTypeCode (aInvoice.getVatPointDateCode ()));
      bFirst = false;
      if (m_aFilter.use ("BT-119", aBD.getRate ()))
        aTT.setRateApplicablePercent (aBD.getRate ());
      // BT-116-1 is UBL only
      _reportUnsupported ("BT-116-1", aBD.getTaxableAmountCurrency ());
      if (m_aFilter.use ("BT-184", aBD.getCurrency ()))
        aTT.setCurrencyCode (aBD.getCurrency ());
      if (m_aFilter.use ("BT-210", aBD.getGoodsServicesCode ()))
        aTT.setSupplyTypeCode (aBD.getGoodsServicesCode ());
      aHTS.addApplicableTradeTax (aTT);
    }
  }

  @NonNull
  private static TradeAllowanceChargeType _allowanceCharge (final boolean bCharge)
  {
    final TradeAllowanceChargeType ret = new TradeAllowanceChargeType ();
    ret.setChargeIndicator (_indicator (bCharge));
    return ret;
  }

  @NonNull
  private TradeTaxType _categoryTax (@Nullable final String sCategory,
                                     @Nullable final BigDecimal aRate,
                                     @Nullable final String sExemptionReason,
                                     @Nullable final String sExemptionReasonCode,
                                     @NonNull final String sCategoryTerm,
                                     @NonNull final String sRateTerm,
                                     @NonNull final String sExemptionReasonTerm,
                                     @NonNull final String sExemptionReasonCodeTerm)
  {
    final TradeTaxType ret = _vatTax ();
    if (m_aFilter.use (sExemptionReasonTerm, sExemptionReason))
      ret.setExemptionReason (sExemptionReason);
    if (m_aFilter.use (sCategoryTerm, sCategory))
      ret.setCategoryCode (sCategory);
    if (m_aFilter.use (sExemptionReasonCodeTerm, sExemptionReasonCode))
      ret.setExemptionReasonCode (sExemptionReasonCode);
    if (m_aFilter.use (sRateTerm, aRate))
      ret.setRateApplicablePercent (aRate);
    return ret;
  }

  @NonNull
  private TradeAllowanceChargeType _documentAllowance (@NonNull final PhinvoiceDocumentAllowance aDA)
  {
    final TradeAllowanceChargeType ret = _allowanceCharge (false);
    if (m_aFilter.use ("BT-94", aDA.getPercentage ()))
      ret.setCalculationPercent (aDA.getPercentage ());
    if (m_aFilter.use ("BT-93", aDA.getBaseAmount ()))
      ret.setBasisAmount (aDA.getBaseAmount ());
    if (m_aFilter.use ("BT-92", aDA.getAmount ()))
      ret.addActualAmount (_amount (aDA.getAmount ()));
    if (m_aFilter.use ("BT-98", aDA.getReasonCode ()))
      ret.setReasonCode (aDA.getReasonCode ());
    if (m_aFilter.use ("BT-97", aDA.getReason ()))
      ret.setReason (aDA.getReason ());
    final TradeTaxType aTT = _categoryTax (aDA.getVatCategoryCode (),
                                           aDA.getVatRate (),
                                           aDA.getExemptionReasonText (),
                                           aDA.getExemptionReasonCode (),
                                           "BT-95",
                                           "BT-96",
                                           "BT-173",
                                           "BT-174");
    if (m_aFilter.use ("BT-213", aDA.getGoodsServicesCode ()))
      aTT.setSupplyTypeCode (aDA.getGoodsServicesCode ());
    ret.addCategoryTradeTax (aTT);
    return ret;
  }

  @NonNull
  private TradeAllowanceChargeType _documentCharge (@NonNull final PhinvoiceDocumentCharge aDC)
  {
    final TradeAllowanceChargeType ret = _allowanceCharge (true);
    if (m_aFilter.use ("BT-101", aDC.getPercentage ()))
      ret.setCalculationPercent (aDC.getPercentage ());
    if (m_aFilter.use ("BT-100", aDC.getBaseAmount ()))
      ret.setBasisAmount (aDC.getBaseAmount ());
    if (m_aFilter.use ("BT-99", aDC.getAmount ()))
      ret.addActualAmount (_amount (aDC.getAmount ()));
    // BT-105 and BT-177 share ram:ReasonCode
    if (m_aFilter.use ("BT-177", aDC.getNonVatTaxCode ()))
    {
      final AllowanceChargeReasonCodeType aCode = new AllowanceChargeReasonCodeType (aDC.getNonVatTaxCode ());
      aCode.setListID (aDC.getNonVatTaxCodeListID ());
      // Required by CII-DT-100 to CII-DT-103
      aCode.setListAgencyID (LIST_AGENCY_ID_UNECE);
      ret.setReasonCode (aCode);
      if (m_aFilter.isAllowed ("BT-105") && aDC.getReasonCode () != null)
        m_aFilter.drop ("BT-105", aDC.getReasonCode (), "BT-105 and BT-177 share the same element");
    }
    else
      if (m_aFilter.use ("BT-105", aDC.getReasonCode ()))
        ret.setReasonCode (aDC.getReasonCode ());
    if (m_aFilter.use ("BT-104", aDC.getReason ()))
      ret.setReason (aDC.getReason ());
    final TradeTaxType aTT = _categoryTax (aDC.getVatCategoryCode (),
                                           aDC.getVatRate (),
                                           aDC.getExemptionReasonText (),
                                           aDC.getExemptionReasonCode (),
                                           "BT-102",
                                           "BT-103",
                                           "BT-175",
                                           "BT-176");
    if (m_aFilter.use ("BT-214", aDC.getGoodsServicesCode ()))
      aTT.setSupplyTypeCode (aDC.getGoodsServicesCode ());
    ret.addCategoryTradeTax (aTT);
    return ret;
  }

  private void _paymentTerms (@NonNull final PhinvoiceInvoice aInvoice,
                              @Nullable final PhinvoiceDirectDebit aDirectDebit,
                              @NonNull final HeaderTradeSettlementType aHTS)
  {
    // One CII payment terms per BG-33; each carries at most one BG-35 and one BG-36
    for (final PhinvoicePaymentTerms aPT : aInvoice.paymentTerms ())
    {
      final TradePaymentTermsType aTPT = new TradePaymentTermsType ();
      if (m_aFilter.use ("BT-20", aPT.getText ()))
        aTPT.addDescription (new TextType (aPT.getText ()));
      boolean bFirst = true;
      for (final PhinvoiceEarlyPaymentDiscount aED : aPT.earlyDiscounts ())
      {
        if (bFirst && m_aFilter.useGroup ("BG-35", "early payment discount"))
        {
          final TradePaymentDiscountTermsType aDT = new TradePaymentDiscountTermsType ();
          if (m_aFilter.use ("BT-170", aED.getEndDate ()))
            aDT.setBasisDateTime (_dateTime (aED.getEndDate ()));
          if (m_aFilter.use ("BT-171", aED.getPercentage ()))
            aDT.setCalculationPercent (aED.getPercentage ());
          if (m_aFilter.use ("BT-172", aED.getAmount ()))
            aDT.setActualDiscountAmount (aED.getAmount ());
          aTPT.setApplicableTradePaymentDiscountTerms (aDT);
        }
        else
          if (!bFirst)
            m_aFilter.drop ("BG-35", aED.getEndDate (), "CII has only one early payment discount per payment terms");
        bFirst = false;
      }
      bFirst = true;
      for (final PhinvoiceLatePaymentPenalty aLP : aPT.latePenalties ())
      {
        if (bFirst && m_aFilter.useGroup ("BG-36", "late payment penalty"))
        {
          final TradePaymentPenaltyTermsType aPen = new TradePaymentPenaltyTermsType ();
          if (m_aFilter.use ("BT-181", aLP.getStartDate ()))
            aPen.setBasisDateTime (_dateTime (aLP.getStartDate ()));
          if (m_aFilter.use ("BT-182", aLP.getYearlyInterestPercentage ()))
            aPen.setCalculationPercent (aLP.getYearlyInterestPercentage ());
          if (m_aFilter.use ("BT-183", aLP.getAmount ()))
            aPen.setActualPenaltyAmount (aLP.getAmount ());
          aTPT.setApplicableTradePaymentPenaltyTerms (aPen);
        }
        else
          if (!bFirst)
            m_aFilter.drop ("BG-36", aLP.getStartDate (), "CII has only one late payment penalty per payment terms");
        bFirst = false;
      }
      aHTS.addSpecifiedTradePaymentTerms (aTPT);
    }

    // BT-9 and BT-89 go into the first payment terms
    final boolean bDueDate = m_aFilter.use ("BT-9", aInvoice.getPaymentDueDate ());
    final boolean bMandate = aDirectDebit != null && m_aFilter.use ("BT-89", aDirectDebit.getMandateReferenceID ());
    if (bDueDate || bMandate)
    {
      if (aHTS.getSpecifiedTradePaymentTerms ().isEmpty ())
        aHTS.addSpecifiedTradePaymentTerms (new TradePaymentTermsType ());
      final TradePaymentTermsType aFirst = aHTS.getSpecifiedTradePaymentTerms ().get (0);
      if (bDueDate)
        aFirst.setDueDateDateTime (_dateTime (aInvoice.getPaymentDueDate ()));
      if (bMandate)
        aFirst.addDirectDebitMandateID (new IDType (aDirectDebit.getMandateReferenceID ()));
    }
  }

  @NonNull
  private TradeSettlementPaymentMeansType _paymentMeans (@Nullable final String sCode, @Nullable final String sText)
  {
    final TradeSettlementPaymentMeansType ret = new TradeSettlementPaymentMeansType ();
    if (m_aFilter.use ("BT-81", sCode))
      ret.setTypeCode (sCode);
    if (m_aFilter.use ("BT-82", sText))
      ret.addInformation (new TextType (sText));
    return ret;
  }

  private void _paymentInstructions (@Nullable final PhinvoicePaymentInstructions aPI,
                                     @NonNull final HeaderTradeSettlementType aHTS)
  {
    if (aPI == null || !m_aFilter.useGroup ("BG-16", "payment instructions"))
      return;

    if (m_aFilter.use ("BT-83", aPI.getRemittanceInformation ()))
      aHTS.addPaymentReference (new TextType (aPI.getRemittanceInformation ()));

    // The first payment means carries BT-81 / BT-82, the card, the payer account and the first
    // credit transfer. Each further credit transfer gets its own payment means with its code.
    final TradeSettlementPaymentMeansType aFirst = _paymentMeans (aPI.getPaymentMeansCode (), aPI.getPaymentMeansText ());
    aHTS.addSpecifiedTradeSettlementPaymentMeans (aFirst);

    final PhinvoicePaymentCard aCard = aPI.getPaymentCard ();
    if (aCard != null && m_aFilter.useGroup ("BG-18", "payment card"))
    {
      final TradeSettlementFinancialCardType aFC = new TradeSettlementFinancialCardType ();
      if (m_aFilter.use ("BT-87", aCard.getPan ()))
        aFC.setID (aCard.getPan ());
      if (m_aFilter.use ("BT-88", aCard.getHolderName ()))
        aFC.setCardholderName (aCard.getHolderName ());
      aFirst.setApplicableTradeSettlementFinancialCard (aFC);
    }

    final PhinvoiceDirectDebit aDD = aPI.getDirectDebit ();
    if (aDD != null && m_aFilter.useGroup ("BG-19", "direct debit"))
    {
      if (m_aFilter.use ("BT-91", aDD.getDebitedAccountID ()) || m_aFilter.use ("BT-216", aDD.getDebitedAccountName ()))
      {
        final DebtorFinancialAccountType aFA = new DebtorFinancialAccountType ();
        if (m_aFilter.isAllowed ("BT-91") && aDD.getDebitedAccountID () != null)
          aFA.setIBANID (aDD.getDebitedAccountID ());
        if (m_aFilter.isAllowed ("BT-216") && aDD.getDebitedAccountName () != null)
          aFA.setAccountName (aDD.getDebitedAccountName ());
        aFirst.setPayerPartyDebtorFinancialAccount (aFA);
      }
      if (m_aFilter.use ("BT-215", aDD.getDebitedAccountProviderID ()))
      {
        final DebtorFinancialInstitutionType aFI = new DebtorFinancialInstitutionType ();
        aFI.setBICID (aDD.getDebitedAccountProviderID ());
        aFirst.setPayerSpecifiedDebtorFinancialInstitution (aFI);
      }
      if (m_aFilter.use ("BT-90", aDD.getBankAssignedCreditorID ()))
        aHTS.setCreditorReferenceID (aDD.getBankAssignedCreditorID ());
    }

    if (m_aFilter.useGroup ("BG-17", aPI.creditTransfers ()))
    {
      boolean bFirst = true;
      for (final PhinvoiceCreditTransfer aCT : aPI.creditTransfers ())
      {
        final TradeSettlementPaymentMeansType aPM;
        if (bFirst && EqualsHelper.equals (aCT.getPaymentMeansCode (), aPI.getPaymentMeansCode ()))
          aPM = aFirst;
        else
        {
          aPM = _paymentMeans (aCT.getPaymentMeansCode (), null);
          aHTS.addSpecifiedTradeSettlementPaymentMeans (aPM);
        }
        bFirst = false;

        final CreditorFinancialAccountType aFA = new CreditorFinancialAccountType ();
        if (m_aFilter.use ("BT-84", aCT.getAccountIdentifier ()))
          aFA.setIBANID (aCT.getAccountIdentifier ());
        if (m_aFilter.use ("BT-85", aCT.getAccountName ()))
          aFA.setAccountName (aCT.getAccountName ());
        aPM.addPayeePartyCreditorFinancialAccount (aFA);
        if (m_aFilter.use ("BT-86", aCT.getServiceProviderID ()))
        {
          final CreditorFinancialInstitutionType aFI = new CreditorFinancialInstitutionType ();
          aFI.setBICID (aCT.getServiceProviderID ());
          aPM.setPayeeSpecifiedCreditorFinancialInstitution (aFI);
        }
      }
    }
    // The type code is mandatory: a payment means that got nothing is not written
    if (aFirst.getTypeCode () == null &&
        aFirst.getInformation ().isEmpty () &&
        aFirst.getApplicableTradeSettlementFinancialCard () == null &&
        aFirst.getPayerPartyDebtorFinancialAccount () == null &&
        aFirst.getPayerSpecifiedDebtorFinancialInstitution () == null &&
        aFirst.getPayeePartyCreditorFinancialAccount ().isEmpty ())
      aHTS.getSpecifiedTradeSettlementPaymentMeans ().remove (aFirst);
    m_aFilter.dropAdditionalTerms (aPI.additionalTerms ());
  }

  @NonNull
  private TradeSettlementHeaderMonetarySummationType _totals (@NonNull final PhinvoiceInvoice aInvoice)
  {
    final PhinvoiceDocumentTotals aT = aInvoice.getDocumentTotals ();
    final TradeSettlementHeaderMonetarySummationType ret = new TradeSettlementHeaderMonetarySummationType ();
    if (m_aFilter.use ("BT-106", aT.getLineNetTotal ()))
      ret.addLineTotalAmount (_amount (aT.getLineNetTotal ()));
    if (m_aFilter.use ("BT-108", aT.getChargeTotal ()))
      ret.addChargeTotalAmount (_amount (aT.getChargeTotal ()));
    if (m_aFilter.use ("BT-107", aT.getAllowanceTotal ()))
      ret.addAllowanceTotalAmount (_amount (aT.getAllowanceTotal ()));
    if (m_aFilter.use ("BT-109", aT.getTaxExclusiveTotal ()))
      ret.addTaxBasisTotalAmount (_amount (aT.getTaxExclusiveTotal ()));
    if (m_aFilter.use ("BT-110", aT.getTaxTotal ()))
    {
      final AmountType aA = _amount (aT.getTaxTotal ());
      aA.setCurrencyID (aInvoice.getCurrency ());
      ret.addTaxTotalAmount (aA);
    }
    if (m_aFilter.use ("BT-111", aT.getTaxTotalAccountingCurrency ()))
    {
      if (aInvoice.getVatAccountingCurrency () == null ||
          (!m_bRaw && aInvoice.getVatAccountingCurrency ().equals (aInvoice.getCurrency ())))
        m_aFilter.drop ("BT-111", aT.getTaxTotalAccountingCurrency (), "BT-6 is missing or not written");
      else
      {
        final AmountType aA = _amount (aT.getTaxTotalAccountingCurrency ());
        aA.setCurrencyID (aInvoice.getVatAccountingCurrency ());
        ret.addTaxTotalAmount (aA);
      }
    }
    if (m_aFilter.use ("BT-114", aT.getRoundingAmount ()))
      ret.addRoundingAmount (_amount (aT.getRoundingAmount ()));
    if (m_aFilter.use ("BT-112", aT.getTaxInclusiveTotal ()))
      ret.addGrandTotalAmount (_amount (aT.getTaxInclusiveTotal ()));
    if (m_aFilter.use ("BT-113", aT.getPrepaidAmount ()))
      ret.addTotalPrepaidAmount (_amount (aT.getPrepaidAmount ()));
    if (m_aFilter.use ("BT-115", aT.getPayableAmount ()))
      ret.addDuePayableAmount (_amount (aT.getPayableAmount ()));
    return ret;
  }

  private void _thirdPartyCharges (@NonNull final PhinvoiceInvoice aInvoice, @NonNull final HeaderTradeSettlementType aHTS)
  {
    final List <PhinvoiceThirdPartyCharge> aCharges = aInvoice.getDocumentTotals ().thirdPartyCharges ();
    if (!m_aFilter.useGroup ("BG-34", aCharges))
      return;
    for (final PhinvoiceThirdPartyCharge aTPC : aCharges)
    {
      final FinancialAdjustmentType aFA = new FinancialAdjustmentType ();
      if (m_aFilter.use ("BT-180", aTPC.getSpecification ()))
        aFA.addReason (new TextType (aTPC.getSpecification ()));
      if (m_aFilter.use ("BT-179", aTPC.getAmount ()))
        aFA.addActualAmount (_amount (aTPC.getAmount ()));
      // BT-179-1 is UBL only
      _reportUnsupported ("BT-179-1", aTPC.getLineID ());
      aHTS.addSpecifiedFinancialAdjustment (aFA);
    }
  }

  @NonNull
  private SpecifiedPeriodType _period (@NonNull final PhinvoiceInvoicingPeriod aPeriod,
                                       @NonNull final String sStartTerm,
                                       @NonNull final String sEndTerm)
  {
    final SpecifiedPeriodType ret = new SpecifiedPeriodType ();
    if (m_aFilter.use (sStartTerm, aPeriod.getStartDate ()))
      ret.setStartDateTime (_dateTime (aPeriod.getStartDate ()));
    if (m_aFilter.use (sEndTerm, aPeriod.getEndDate ()))
      ret.setEndDateTime (_dateTime (aPeriod.getEndDate ()));
    return ret;
  }

  @NonNull
  private HeaderTradeSettlementType _settlement (@NonNull final PhinvoiceInvoice aInvoice)
  {
    final HeaderTradeSettlementType ret = new HeaderTradeSettlementType ();
    if (m_aFilter.use ("BT-6", aInvoice.getVatAccountingCurrency ()))
    {
      // The CII version of BR-53 does not allow BT-6 to be the same as BT-5 (like en16931-ubl2cii)
      if (!m_bRaw && aInvoice.getVatAccountingCurrency ().equals (aInvoice.getCurrency ()))
        m_aFilter.drop ("BT-6", aInvoice.getVatAccountingCurrency (), "CII does not allow it to be equal to BT-5 (BR-53)");
      else
        ret.setTaxCurrencyCode (aInvoice.getVatAccountingCurrency ());
    }
    if (m_aFilter.use ("BT-5", aInvoice.getCurrency ()))
      ret.setInvoiceCurrencyCode (aInvoice.getCurrency ());
    ret.setPayeeTradeParty (_payee (aInvoice.getPayee ()));
    _paymentInstructions (aInvoice.getPaymentInstructions (), ret);
    _vatBreakdowns (aInvoice, ret);
    final PhinvoiceInvoicingPeriod aPeriod = aInvoice.getInvoicingPeriod ();
    if (aPeriod != null && !aPeriod.isEmpty () && m_aFilter.useGroup ("BG-14", "invoicing period"))
      ret.setBillingSpecifiedPeriod (_period (aPeriod, "BT-73", "BT-74"));
    if (m_aFilter.useGroup ("BG-20", aInvoice.documentAllowances ()))
      for (final PhinvoiceDocumentAllowance aDA : aInvoice.documentAllowances ())
        ret.addSpecifiedTradeAllowanceCharge (_documentAllowance (aDA));
    if (m_aFilter.useGroup ("BG-21", aInvoice.documentCharges ()))
      for (final PhinvoiceDocumentCharge aDC : aInvoice.documentCharges ())
        ret.addSpecifiedTradeAllowanceCharge (_documentCharge (aDC));
    final PhinvoicePaymentInstructions aPI = aInvoice.getPaymentInstructions ();
    _paymentTerms (aInvoice,
                   aPI != null && m_aFilter.isAllowed ("BG-19") ? aPI.getDirectDebit () : null,
                   ret);
    _thirdPartyCharges (aInvoice, ret);
    ret.setSpecifiedTradeSettlementHeaderMonetarySummation (_totals (aInvoice));
    if (m_aFilter.useGroup ("BG-3", aInvoice.precedingInvoiceRefs ()))
      for (final PhinvoicePrecedingInvoiceRef aRef : aInvoice.precedingInvoiceRefs ())
        if (!m_aFilter.use ("BT-25", aRef.getNumber ()))
        {
          if (aRef.getNumber () == null)
            m_aFilter.drop ("BG-3", aRef.getIssueDate (), "the mandatory preceding invoice reference (BT-25) is missing");
        }
        else
        {
          final ReferencedDocumentType aRD = _referencedDocument (aRef.getNumber ());
          if (m_aFilter.use ("BT-26", aRef.getIssueDate ()))
            aRD.setFormattedIssueDateTime (_formattedDateTime (aRef.getIssueDate ()));
          if (m_aFilter.use ("BT-202", aRef.getTypeCode ()))
            aRD.setTypeCode (aRef.getTypeCode ());
          ret.addInvoiceReferencedDocument (aRD);
        }
    if (m_aFilter.use ("BT-19", aInvoice.getBuyerAccountingReference ()))
    {
      final TradeAccountingAccountType aTAA = new TradeAccountingAccountType ();
      aTAA.setID (aInvoice.getBuyerAccountingReference ());
      ret.addReceivableSpecifiedTradeAccountingAccount (aTAA);
    }
    if (m_aFilter.use ("BT-167", aInvoice.getVatExchangeRate ()))
    {
      final TradeCurrencyExchangeType aTCE = new TradeCurrencyExchangeType ();
      if (m_aFilter.use ("BT-167-2", aInvoice.getVatExchangeRateSourceCurrency ()))
        aTCE.setSourceCurrencyCode (aInvoice.getVatExchangeRateSourceCurrency ());
      if (m_aFilter.use ("BT-167-1", aInvoice.getVatExchangeRateTargetCurrency ()))
        aTCE.setTargetCurrencyCode (aInvoice.getVatExchangeRateTargetCurrency ());
      aTCE.setConversionRate (aInvoice.getVatExchangeRate ());
      ret.setInvoiceApplicableTradeCurrencyExchange (aTCE);
    }
    else
    {
      if (aInvoice.getVatExchangeRateTargetCurrency () != null)
        m_aFilter.drop ("BT-167-1", aInvoice.getVatExchangeRateTargetCurrency (), "the exchange rate (BT-167) is missing");
      if (aInvoice.getVatExchangeRateSourceCurrency () != null)
        m_aFilter.drop ("BT-167-2", aInvoice.getVatExchangeRateSourceCurrency (), "the exchange rate (BT-167) is missing");
    }
    return ret;
  }

  @NonNull
  private TradeProductType _item (@NonNull final PhinvoiceItem aItem)
  {
    final TradeProductType ret = new TradeProductType ();
    final PhinvoiceIdentifier aStandardID = aItem.getStandardItemID ();
    if (_useIdentifier (aStandardID, "BT-157", "BT-157-1"))
      ret.setGlobalID (_id (aStandardID));
    if (m_aFilter.use ("BT-155", aItem.getSellerItemID ()))
      ret.setSellerAssignedID (aItem.getSellerItemID ());
    if (m_aFilter.use ("BT-156", aItem.getBuyerItemID ()))
      ret.setBuyerAssignedID (aItem.getBuyerItemID ());
    if (m_aFilter.use ("BT-153", aItem.getName ()))
      ret.addName (new TextType (aItem.getName ()));
    if (m_aFilter.use ("BT-154", aItem.getDescription ()))
      ret.addDescription (new TextType (aItem.getDescription ()));
    if (m_aFilter.useGroup ("BG-32", aItem.attributes ()))
      for (final PhinvoiceItemAttribute aAttr : aItem.attributes ())
      {
        final ProductCharacteristicType aPC = new ProductCharacteristicType ();
        if (m_aFilter.use ("BT-160", aAttr.getName ()))
          aPC.addDescription (new TextType (aAttr.getName ()));
        if (m_aFilter.use ("BT-161", aAttr.getValue ()))
          aPC.addValue (new TextType (aAttr.getValue ()));
        if (m_aFilter.use ("BT-211", aAttr.getCode ()))
          aPC.setTypeCode (aAttr.getCode ());
        // The numeric value is BT-161b with BT-212 as its unit
        if (m_aFilter.use ("BT-161", aAttr.getNumericValue ()))
        {
          final MeasureType aMeasure = new MeasureType (aAttr.getNumericValue ());
          if (m_aFilter.use ("BT-212", aAttr.getValueUnit ()))
            aMeasure.setUnitCode (aAttr.getValueUnit ());
          aPC.setValueMeasure (aMeasure);
        }
        else
          if (aAttr.getValueUnit () != null)
            m_aFilter.drop ("BT-212", aAttr.getValueUnit (), "the numeric item attribute value is missing");
        if (!aPC.getDescription ().isEmpty () || !aPC.getValue ().isEmpty () || aPC.getTypeCode () != null || aPC.getValueMeasure () != null)
          ret.addApplicableProductCharacteristic (aPC);
      }
    for (final PhinvoiceItemClassification aIC : aItem.classifications ())
      if (m_aFilter.use ("BT-158", aIC.getCode ()))
      {
        final CodeType aCode = new CodeType (aIC.getCode ());
        if (m_aFilter.use ("BT-158-1", aIC.getListID ()))
          aCode.setListID (aIC.getListID ());
        if (m_aFilter.use ("BT-158-2", aIC.getListVersionID ()))
          aCode.setListVersionID (aIC.getListVersionID ());
        final ProductClassificationType aPC = new ProductClassificationType ();
        aPC.setClassCode (aCode);
        ret.addDesignatedProductClassification (aPC);
      }
    if (m_aFilter.use ("BT-159", aItem.getCountryOfOrigin ()))
    {
      final TradeCountryType aTC = new TradeCountryType ();
      aTC.setID (aItem.getCountryOfOrigin ());
      ret.setOriginTradeCountry (aTC);
    }
    // BT-196 is written with the line VAT information
    return ret;
  }

  @NonNull
  private static ReferencedDocumentType _lineReference (@Nullable final String sDocumentID, @Nullable final String sLineID)
  {
    final ReferencedDocumentType ret = new ReferencedDocumentType ();
    if (sDocumentID != null)
      ret.setIssuerAssignedID (sDocumentID);
    if (sLineID != null)
      ret.setLineID (sLineID);
    return ret;
  }

  @NonNull
  private LineTradeAgreementType _lineAgreement (@NonNull final PhinvoiceInvoiceLine aLine)
  {
    final LineTradeAgreementType ret = new LineTradeAgreementType ();
    final boolean bSalesOrder = m_aFilter.use ("BT-200", aLine.getSalesOrderReference ());
    final boolean bSalesOrderLine = m_aFilter.use ("BT-201", aLine.getSalesOrderLineReference ());
    if (bSalesOrder || bSalesOrderLine)
      ret.setSellerOrderReferencedDocument (_lineReference (bSalesOrder ? aLine.getSalesOrderReference () : null,
                                                            bSalesOrderLine ? aLine.getSalesOrderLineReference () : null));
    final boolean bPurchaseOrder = m_aFilter.use ("BT-188", aLine.getPurchaseOrderReference ());
    final boolean bPurchaseOrderLine = m_aFilter.use ("BT-132", aLine.getPurchaseOrderLineRef ());
    if (bPurchaseOrder || bPurchaseOrderLine)
      ret.setBuyerOrderReferencedDocument (_lineReference (bPurchaseOrder ? aLine.getPurchaseOrderReference () : null,
                                                           bPurchaseOrderLine ? aLine.getPurchaseOrderLineRef () : null));
    final PhinvoicePriceDetails aPrice = aLine.getPrice ();
    if (aPrice != null && m_aFilter.useGroup ("BG-29", "price details"))
    {
      if (m_aFilter.use ("BT-148", aPrice.getGrossPrice ()))
      {
        final TradePriceType aGross = new TradePriceType ();
        aGross.addChargeAmount (_amount (aPrice.getGrossPrice ()));
        if (m_aFilter.use ("BT-147", aPrice.getDiscount ()))
        {
          final TradeAllowanceChargeType aAC = _allowanceCharge (false);
          aAC.addActualAmount (_amount (aPrice.getDiscount ()));
          aGross.addAppliedTradeAllowanceCharge (aAC);
        }
        ret.setGrossPriceProductTradePrice (aGross);
      }
      else
        if (m_aFilter.use ("BT-147", aPrice.getDiscount ()))
          m_aFilter.drop ("BT-147", aPrice.getDiscount (), "CII carries the discount only with the gross price (BT-148)");
      final TradePriceType aNet = new TradePriceType ();
      if (m_aFilter.use ("BT-146", aPrice.getNetPrice ()))
        aNet.addChargeAmount (_amount (aPrice.getNetPrice ()));
      if (m_aFilter.use ("BT-149", aPrice.getBaseQuantity ()))
      {
        final QuantityType aQ = new QuantityType (aPrice.getBaseQuantity ());
        if (m_aFilter.use ("BT-150", aPrice.getBaseQuantityUnit ()))
          aQ.setUnitCode (aPrice.getBaseQuantityUnit ());
        aNet.setBasisQuantity (aQ);
      }
      ret.setNetPriceProductTradePrice (aNet);
    }
    return ret;
  }

  @NonNull
  private LineTradeDeliveryType _lineDelivery (@NonNull final PhinvoiceInvoiceLine aLine)
  {
    final LineTradeDeliveryType ret = new LineTradeDeliveryType ();
    if (m_aFilter.use ("BT-129", aLine.getQuantity ()))
    {
      final QuantityType aQ = new QuantityType (aLine.getQuantity ());
      if (m_aFilter.use ("BT-130", aLine.getQuantityUnit ()))
        aQ.setUnitCode (aLine.getQuantityUnit ());
      ret.setBilledQuantity (aQ);
    }
    final PhinvoiceLineDelivery aLD = aLine.getDelivery ();
    if (aLD != null && !aLD.isEmpty ())
    {
      // BG-37 / BG-38
      ret.setShipToTradeParty (_shipTo (aLD.getPartyName (),
                                        aLD.getLocationIdentifier (),
                                        aLD.getPostalAddress (),
                                        "BT-185",
                                        "BT-186",
                                        "BG-38",
                                        new String [] { "BT-203", "BT-204", "BT-205", "BT-206", "BT-207", "BT-208", "BT-209" }));
      if (m_aFilter.use ("BT-187", aLD.getActualDeliveryDate ()))
      {
        final SupplyChainEventType aEvent = new SupplyChainEventType ();
        aEvent.setOccurrenceDateTime (_dateTime (aLD.getActualDeliveryDate ()));
        ret.setActualDeliverySupplyChainEvent (aEvent);
      }
    }
    final boolean bDespatch = m_aFilter.use ("BT-189", aLine.getDespatchAdviceReference ());
    final boolean bDespatchLine = m_aFilter.use ("BT-190", aLine.getDespatchAdviceLineReference ());
    if (bDespatch || bDespatchLine)
      ret.setDespatchAdviceReferencedDocument (_lineReference (bDespatch ? aLine.getDespatchAdviceReference () : null,
                                                               bDespatchLine ? aLine.getDespatchAdviceLineReference () : null));
    final boolean bReceiving = m_aFilter.use ("BT-191", aLine.getReceivingAdviceReference ());
    final boolean bReceivingLine = m_aFilter.use ("BT-192", aLine.getReceivingAdviceLineReference ());
    if (bReceiving || bReceivingLine)
      ret.setReceivingAdviceReferencedDocument (_lineReference (bReceiving ? aLine.getReceivingAdviceReference () : null,
                                                                bReceivingLine ? aLine.getReceivingAdviceLineReference () : null));
    final boolean bDeliveryNote = m_aFilter.use ("BT-198", aLine.getDeliveryNoteReference ());
    final boolean bDeliveryNoteLine = m_aFilter.use ("BT-199", aLine.getDeliveryNoteLineReference ());
    if (bDeliveryNote || bDeliveryNoteLine)
      ret.setDeliveryNoteReferencedDocument (_lineReference (bDeliveryNote ? aLine.getDeliveryNoteReference () : null,
                                                             bDeliveryNoteLine ? aLine.getDeliveryNoteLineReference () : null));
    return ret;
  }

  @NonNull
  private TradeAllowanceChargeType _lineAllowance (@NonNull final PhinvoiceLineAllowance aLA)
  {
    final TradeAllowanceChargeType ret = _allowanceCharge (false);
    if (m_aFilter.use ("BT-138", aLA.getPercentage ()))
      ret.setCalculationPercent (aLA.getPercentage ());
    if (m_aFilter.use ("BT-137", aLA.getBaseAmount ()))
      ret.setBasisAmount (aLA.getBaseAmount ());
    if (m_aFilter.use ("BT-136", aLA.getAmount ()))
      ret.addActualAmount (_amount (aLA.getAmount ()));
    if (m_aFilter.use ("BT-140", aLA.getReasonCode ()))
      ret.setReasonCode (aLA.getReasonCode ());
    if (m_aFilter.use ("BT-139", aLA.getReason ()))
      ret.setReason (aLA.getReason ());
    return ret;
  }

  @NonNull
  private TradeAllowanceChargeType _lineCharge (@NonNull final PhinvoiceLineCharge aLC)
  {
    final TradeAllowanceChargeType ret = _allowanceCharge (true);
    if (m_aFilter.use ("BT-143", aLC.getPercentage ()))
      ret.setCalculationPercent (aLC.getPercentage ());
    if (m_aFilter.use ("BT-142", aLC.getBaseAmount ()))
      ret.setBasisAmount (aLC.getBaseAmount ());
    if (m_aFilter.use ("BT-141", aLC.getAmount ()))
      ret.addActualAmount (_amount (aLC.getAmount ()));
    // BT-145 and BT-193 share ram:ReasonCode
    if (m_aFilter.use ("BT-193", aLC.getNonVatTaxTypeCode ()))
    {
      final AllowanceChargeReasonCodeType aCode = new AllowanceChargeReasonCodeType (aLC.getNonVatTaxTypeCode ());
      aCode.setListID (aLC.getNonVatTaxTypeCodeListID ());
      // Required by CII-DT-100 to CII-DT-103
      aCode.setListAgencyID (LIST_AGENCY_ID_UNECE);
      ret.setReasonCode (aCode);
      if (m_aFilter.isAllowed ("BT-145") && aLC.getReasonCode () != null)
        m_aFilter.drop ("BT-145", aLC.getReasonCode (), "BT-145 and BT-193 share the same element");
    }
    else
      if (m_aFilter.use ("BT-145", aLC.getReasonCode ()))
        ret.setReasonCode (aLC.getReasonCode ());
    if (m_aFilter.use ("BT-144", aLC.getReason ()))
      ret.setReason (aLC.getReason ());
    return ret;
  }

  @NonNull
  private LineTradeSettlementType _lineSettlement (@NonNull final PhinvoiceInvoiceLine aLine)
  {
    final LineTradeSettlementType ret = new LineTradeSettlementType ();
    final PhinvoiceLineVat aVat = aLine.getLineVat ();
    if (aVat != null && m_aFilter.useGroup ("BG-30", "line VAT information"))
    {
      final TradeTaxType aTT = _vatTax ();
      if (m_aFilter.use ("BT-151", aVat.getCategoryCode ()))
        aTT.setCategoryCode (aVat.getCategoryCode ());
      if (m_aFilter.use ("BT-152", aVat.getRate ()))
        aTT.setRateApplicablePercent (aVat.getRate ());
      if (m_aFilter.use ("BT-194", aVat.getExemptionReasonText ()))
        aTT.setExemptionReason (aVat.getExemptionReasonText ());
      if (m_aFilter.use ("BT-195", aVat.getExemptionReasonCode ()))
        aTT.setExemptionReasonCode (aVat.getExemptionReasonCode ());
      // BT-196 belongs to the item (BG-31), CII carries it in the line VAT information
      final PhinvoiceItem aItem = aLine.getItem ();
      if (aItem != null && m_aFilter.use ("BT-196", aItem.getGoodsServicesCode ()))
        aTT.setSupplyTypeCode (aItem.getGoodsServicesCode ());
      ret.addApplicableTradeTax (aTT);
    }
    final PhinvoiceInvoicingPeriod aPeriod = aLine.getPeriod ();
    if (aPeriod != null && !aPeriod.isEmpty () && m_aFilter.useGroup ("BG-26", "invoice line period"))
      ret.setBillingSpecifiedPeriod (_period (aPeriod, "BT-134", "BT-135"));
    if (m_aFilter.useGroup ("BG-27", aLine.allowances ()))
      for (final PhinvoiceLineAllowance aLA : aLine.allowances ())
        ret.addSpecifiedTradeAllowanceCharge (_lineAllowance (aLA));
    if (m_aFilter.useGroup ("BG-28", aLine.charges ()))
      for (final PhinvoiceLineCharge aLC : aLine.charges ())
        ret.addSpecifiedTradeAllowanceCharge (_lineCharge (aLC));
    final TradeSettlementLineMonetarySummationType aSum = new TradeSettlementLineMonetarySummationType ();
    if (m_aFilter.use ("BT-131", aLine.getNetAmount ()))
      aSum.addLineTotalAmount (_amount (aLine.getNetAmount ()));
    ret.setSpecifiedTradeSettlementLineMonetarySummation (aSum);
    if (m_aFilter.useGroup ("BG-39", aLine.precedingInvoiceRefs ()))
      for (final PhinvoiceLinePrecedingInvoiceRef aRef : aLine.precedingInvoiceRefs ())
        if (!m_aFilter.use ("BT-217", aRef.getNumber ()))
        {
          if (aRef.getNumber () == null)
            m_aFilter.drop ("BG-39", aRef.getLineReference (), "the mandatory line-level preceding invoice reference (BT-217) is missing");
        }
        else
        {
          final ReferencedDocumentType aRD = _referencedDocument (aRef.getNumber ());
          if (m_aFilter.use ("BT-220", aRef.getLineReference ()))
            aRD.setLineID (aRef.getLineReference ());
          if (m_aFilter.use ("BT-219", aRef.getTypeCode ()))
            aRD.setTypeCode (aRef.getTypeCode ());
          if (m_aFilter.use ("BT-218", aRef.getIssueDate ()))
            aRD.setFormattedIssueDateTime (_formattedDateTime (aRef.getIssueDate ()));
          ret.addInvoiceReferencedDocument (aRD);
        }
    final PhinvoiceIdentifier aObjectID = aLine.getObjectIdentifier ();
    if (_useIdentifier (aObjectID, "BT-128", "BT-128-1"))
    {
      final ReferencedDocumentType aRD = _referencedDocument (aObjectID.getValue ());
      aRD.setTypeCode (EN16931CodeLists.DOCUMENT_TYPE_CODE_OBJECT_DOCUMENT);
      if (m_aFilter.use ("BT-128-1", aObjectID.getSchemeID ()))
        aRD.setReferenceTypeCode (aObjectID.getSchemeID ());
      ret.addAdditionalReferencedDocument (aRD);
    }
    if (m_aFilter.use ("BT-133", aLine.getAccountingCost ()))
    {
      final TradeAccountingAccountType aTAA = new TradeAccountingAccountType ();
      aTAA.setID (aLine.getAccountingCost ());
      ret.addReceivableSpecifiedTradeAccountingAccount (aTAA);
    }
    return ret;
  }

  @NonNull
  private SupplyChainTradeLineItemType _line (@NonNull final PhinvoiceInvoiceLine aLine)
  {
    final SupplyChainTradeLineItemType ret = new SupplyChainTradeLineItemType ();
    final DocumentLineDocumentType aLD = new DocumentLineDocumentType ();
    if (m_aFilter.use ("BT-126", aLine.getID ()))
      aLD.setLineID (aLine.getID ());
    if (m_aFilter.use ("BT-127", aLine.getNote ()))
    {
      final NoteType aNote = new NoteType ();
      aNote.addContent (new TextType (aLine.getNote ()));
      aLD.addIncludedNote (aNote);
    }
    ret.setAssociatedDocumentLineDocument (aLD);
    final PhinvoiceItem aItem = aLine.getItem ();
    if (aItem != null && m_aFilter.useGroup ("BG-31", "item information"))
      ret.setSpecifiedTradeProduct (_item (aItem));
    ret.setSpecifiedLineTradeAgreement (_lineAgreement (aLine));
    ret.setSpecifiedLineTradeDelivery (_lineDelivery (aLine));
    ret.setSpecifiedLineTradeSettlement (_lineSettlement (aLine));
    m_aFilter.dropAdditionalTerms (aLine.additionalTerms ());
    return ret;
  }

  /**
   * Create the CII D25A document.
   *
   * @param aInvoice
   *        The invoice to write. May not be <code>null</code>.
   * @param sSpecificationIdentifier
   *        The BT-24 value to write. May not be <code>null</code>.
   * @return The JAXB object. Never <code>null</code>.
   */
  @NonNull
  CrossIndustryInvoiceType createCrossIndustryInvoice (@NonNull final PhinvoiceInvoice aInvoice,
                                                       @NonNull final String sSpecificationIdentifier)
  {
    final CrossIndustryInvoiceType ret = new CrossIndustryInvoiceType ();
    ret.setExchangedDocumentContext (_documentContext (aInvoice, sSpecificationIdentifier));
    ret.setExchangedDocument (_document (aInvoice));

    final SupplyChainTradeTransactionType aSCTT = new SupplyChainTradeTransactionType ();
    if (m_aFilter.useGroup ("BG-25", aInvoice.lines ().size () + " invoice lines"))
      for (final PhinvoiceInvoiceLine aLine : aInvoice.lines ())
        aSCTT.addIncludedSupplyChainTradeLineItem (_line (aLine));
    aSCTT.setApplicableHeaderTradeAgreement (_agreement (aInvoice));
    aSCTT.setApplicableHeaderTradeDelivery (_delivery (aInvoice));
    aSCTT.setApplicableHeaderTradeSettlement (_settlement (aInvoice));
    ret.setSupplyChainTradeTransaction (aSCTT);

    m_aFilter.dropAdditionalTerms (aInvoice.additionalTerms ());
    // The values of Factur-X EXTENDED cannot be written
    PhinvoiceExtendedTermReporter.reportExtendedTerms (aInvoice, m_aFilter);
    return ret;
  }
}
