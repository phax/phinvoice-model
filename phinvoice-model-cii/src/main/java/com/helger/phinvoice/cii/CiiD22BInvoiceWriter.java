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
import java.util.Map;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.base.equals.EqualsHelper;
import com.helger.en16931.basics.EEN16931DateFormatCode;
import com.helger.en16931.basics.codelist.EN16931CodeLists;
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
import com.helger.phinvoice.term.PhinvoiceExtendedTerms;
import com.helger.phinvoice.term.PhinvoiceExtendedTerms.AddressTerms;
import com.helger.phinvoice.term.PhinvoiceExtendedTerms.ContactTerms;
import com.helger.phinvoice.term.PhinvoiceExtendedTerms.DocumentTerms;
import com.helger.phinvoice.term.PhinvoiceExtendedTerms.PartyTerms;
import com.helger.phinvoice.write.PhinvoiceTermFilter;

import un.unece.uncefact.data.standard.cii.d22b.CrossIndustryInvoiceType;
import un.unece.uncefact.data.standard.cii.d22b.qdt.AllowanceChargeReasonCodeType;
import un.unece.uncefact.data.standard.cii.d22b.qdt.FormattedDateTimeType;
import un.unece.uncefact.data.standard.cii.d22b.qdt.PartyRoleCodeType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.AdvancePaymentType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.CreditorFinancialAccountType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.CreditorFinancialInstitutionType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.DebtorFinancialAccountType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.DebtorFinancialInstitutionType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.DocumentContextParameterType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.DocumentLineDocumentType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.ExchangedDocumentContextType;
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
import un.unece.uncefact.data.standard.cii.d22b.rabie.LogisticsTransportMovementType;
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
import un.unece.uncefact.data.standard.cii.d22b.rabie.TradeAccountingAccountType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.TradeAddressType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.TradeAllowanceChargeType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.TradeContactType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.TradeCountryType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.TradeCurrencyExchangeType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.TradeDeliveryTermsType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.TradeLocationType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.TradePartyType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.TradePaymentDiscountTermsType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.TradePaymentPenaltyTermsType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.TradePaymentTermsType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.TradePriceType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.TradeProductInstanceType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.TradeProductType;
import un.unece.uncefact.data.standard.cii.d22b.rabie.TradeSettlementFinancialCardType;
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
import un.unece.uncefact.data.standard.cii.d22b.udt.IndicatorType;
import un.unece.uncefact.data.standard.cii.d22b.udt.MeasureType;
import un.unece.uncefact.data.standard.cii.d22b.udt.PercentType;
import un.unece.uncefact.data.standard.cii.d22b.udt.QuantityType;
import un.unece.uncefact.data.standard.cii.d22b.udt.TextType;

/**
 * Writes a {@link PhinvoiceInvoice} as CII with the CII D22B JAXB classes, for the targets of EN
 * 16931:2017 (all profiles). It is the inverse of {@link CiiD22BInvoiceReader}. Every value goes
 * through the {@link PhinvoiceTermFilter}, so only the terms that the target allows are written
 * and all other values are reported. With the profiles up to EN 16931 only elements of the CII
 * D16B schema are written. Package-private worker of {@link CiiInvoiceWriter}.
 *
 * @author Philip Helger
 */
final class CiiD22BInvoiceWriter
{
  /** The tax type code of all VAT taxes (BT-95-1, BT-102-1, BT-118-1, BT-151-1) */
  private static final String TAX_TYPE_CODE_VAT = "VAT";

  /** The scheme identifier of a tax registration that is no VAT identifier (BT-32) */
  private static final String TAX_SCHEME_FC = "FC";

  /**
   * The Factur-X EXTENDED terms that are a term of EN 16931:2026: if the target has not the Factur-X
   * term, a value is reported with the term of EN 16931:2026, which is the term of the model.
   */
  private static final Map <String, String> EN2026_TERM_OF_EXTENDED_TERM = Map.ofEntries (Map.entry ("BT-X-11", "BT-211"),
Map.entry ("BT-X-12", "BT-212"),
Map.entry ("BT-X-21", "BT-188"),
Map.entry ("BT-X-85", "BT-187"),
Map.entry ("BT-X-86", "BT-189"),
Map.entry ("BT-X-87", "BT-190"),
Map.entry ("BT-X-89", "BT-191"),
Map.entry ("BT-X-90", "BT-192"),
Map.entry ("BT-X-92", "BT-198"),
Map.entry ("BT-X-93", "BT-199"),
Map.entry ("BT-X-96", "BT-194"),
Map.entry ("BT-X-97", "BT-195"),
Map.entry ("BT-X-202", "BT-197"),
Map.entry ("BT-X-276", "BT-181"),
Map.entry ("BT-X-280", "BT-182"),
Map.entry ("BT-X-281", "BT-183"),
Map.entry ("BT-X-282", "BT-170"),
Map.entry ("BT-X-286", "BT-171"),
Map.entry ("BT-X-287", "BT-172"),
Map.entry ("BT-X-331", "BT-217"),
Map.entry ("BT-X-332", "BT-219"),
Map.entry ("BT-X-333", "BT-218"),
Map.entry ("BT-X-537", "BT-200"),
Map.entry ("BT-X-538", "BT-201"),
Map.entry ("BT-X-540", "BT-220"),
Map.entry ("BT-X-555", "BT-202"));

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
  CiiD22BInvoiceWriter (@NonNull final PhinvoiceTermFilter aFilter, final boolean bRaw)
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
   * Report an EN 16931:2026 only value, which this writer cannot write. The filter never allows
   * such a term for an EN 16931:2017 target.
   */
  private void _reportUnsupported (@NonNull final String sTermID, @Nullable final Object aValue)
  {
    if (m_aFilter.use (sTermID, aValue))
      throw new IllegalStateException ("The term " + sTermID + " cannot be written as CII D22B");
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

  /**
   * The term to use for a Factur-X EXTENDED term: the term itself if the target allows it, otherwise
   * the term of EN 16931:2026 if there is one, so that a dropped value is reported with that.
   */
  @NonNull
  private String _extendedTerm (@NonNull final String sTermID)
  {
    final String sEN2026TermID = EN2026_TERM_OF_EXTENDED_TERM.get (sTermID);
    return sEN2026TermID == null || m_aFilter.isAllowed (sTermID) ? sTermID : sEN2026TermID;
  }

  private boolean _useExtended (@NonNull final String sTermID, @Nullable final Object aValue)
  {
    return m_aFilter.use (_extendedTerm (sTermID), aValue);
  }

  /**
   * Like {@link PhinvoiceTermFilter#use(String, Object)}, but for a position of the profile EXTENDED
   * that may have no such term: a value for a missing term is reported as not written.
   */
  private boolean _useSlot (@Nullable final String sTermID, @Nullable final Object aValue, @Nullable final String sGroupID)
  {
    if (sTermID != null)
      return _useExtended (sTermID, aValue);
    if (aValue != null && !(aValue instanceof final String sValue && sValue.isEmpty ()))
      m_aFilter.drop (sGroupID != null ? sGroupID : "BG-X", aValue, "this position has no such term");
    return false;
  }

  @Nullable
  private TradeAddressType _address (@Nullable final PhinvoicePostalAddress aAddr,
                                     @NonNull final String sGroupID,
                                     @Nullable final String sLine1,
                                     @Nullable final String sLine2,
                                     @Nullable final String sLine3,
                                     @Nullable final String sCity,
                                     @Nullable final String sPostCode,
                                     @Nullable final String sSubdivision,
                                     @Nullable final String sCountry)
  {
    if (aAddr == null || aAddr.isEmpty () || !m_aFilter.useGroup (sGroupID, "postal address"))
      return null;

    final TradeAddressType ret = new TradeAddressType ();
    if (_useSlot (sPostCode, aAddr.getPostCode (), sGroupID))
      ret.setPostcodeCode (aAddr.getPostCode ());
    if (_useSlot (sLine1, aAddr.getLine1 (), sGroupID))
      ret.setLineOne (aAddr.getLine1 ());
    if (_useSlot (sLine2, aAddr.getLine2 (), sGroupID))
      ret.setLineTwo (aAddr.getLine2 ());
    if (_useSlot (sLine3, aAddr.getLine3 (), sGroupID))
      ret.setLineThree (aAddr.getLine3 ());
    if (_useSlot (sCity, aAddr.getCity (), sGroupID))
      ret.setCityName (aAddr.getCity ());
    if (_useSlot (sCountry, aAddr.getCountryCode (), sGroupID))
      ret.setCountryID (aAddr.getCountryCode ());
    if (_useSlot (sSubdivision, aAddr.getCountrySubdivision (), sGroupID))
      ret.addCountrySubDivisionName (new TextType (aAddr.getCountrySubdivision ()));
    return ret;
  }

  @Nullable
  private TradeContactType _contact (@Nullable final PhinvoiceContact aContact,
                                     @NonNull final String sGroupID,
                                     @Nullable final String sName,
                                     @Nullable final String sDepartment,
                                     @Nullable final String sTelephone,
                                     @Nullable final String sEmail)
  {
    if (aContact == null || aContact.isEmpty () || !m_aFilter.useGroup (sGroupID, "contact"))
      return null;

    final TradeContactType ret = new TradeContactType ();
    if (_useSlot (sName, aContact.getName (), sGroupID))
      ret.setPersonName (aContact.getName ());
    if (_useSlot (sDepartment, aContact.getDepartmentName (), sGroupID))
      ret.setDepartmentName (aContact.getDepartmentName ());
    if (_useSlot (sTelephone, aContact.getTelephone (), sGroupID))
    {
      final UniversalCommunicationType aUC = new UniversalCommunicationType ();
      aUC.setCompleteNumber (aContact.getTelephone ());
      ret.setTelephoneUniversalCommunication (aUC);
    }
    if (_useSlot (sEmail, aContact.getEmail (), sGroupID))
    {
      final UniversalCommunicationType aUC = new UniversalCommunicationType ();
      aUC.setURIID (aContact.getEmail ());
      ret.setEmailURIUniversalCommunication (aUC);
    }
    return ret;
  }

  @Nullable
  private TradeAddressType _address (@Nullable final PhinvoicePostalAddress aAddr, @Nullable final AddressTerms aTerms)
  {
    if (aTerms == null)
    {
      if (aAddr != null && !aAddr.isEmpty ())
        m_aFilter.drop ("BG-X", "postal address", "this position has no postal address");
      return null;
    }
    final String [] aT = aTerms.asArray ();
    return _address (aAddr, aTerms.group (), aT[0], aT[1], aT[2], aT[3], aT[4], aT[5], aT[6]);
  }

  /** Add fax and type code of the profile EXTENDED to a written contact. */
  private void _contactExtras (@Nullable final TradeContactType aTC,
                               @Nullable final PhinvoiceContact aContact,
                               @NonNull final ContactTerms aTerms)
  {
    if (aTC == null || aContact == null)
      return;
    if (_useSlot (aTerms.fax (), aContact.getFax (), aTerms.group ()))
    {
      final UniversalCommunicationType aUC = new UniversalCommunicationType ();
      aUC.setCompleteNumber (aContact.getFax ());
      aTC.setFaxUniversalCommunication (aUC);
    }
    if (_useSlot (aTerms.typeCode (), aContact.getTypeCode (), aTerms.group ()))
      aTC.setTypeCode (aContact.getTypeCode ());
  }

  @Nullable
  private TradeContactType _contact (@Nullable final PhinvoiceContact aContact, @Nullable final ContactTerms aTerms)
  {
    if (aTerms == null)
    {
      if (aContact != null && !aContact.isEmpty ())
        m_aFilter.drop ("BG-X", "contact", "this position has no contact");
      return null;
    }
    final TradeContactType ret = _contact (aContact,
                                           aTerms.group (),
                                           aTerms.personName (),
                                           aTerms.departmentName (),
                                           aTerms.telephone (),
                                           aTerms.email ());
    _contactExtras (ret, aContact, aTerms);
    return ret;
  }

  private void _taxRegistrations (@NonNull final List <PhinvoiceIdentifier> aTaxRegistrations,
                                  @NonNull final PartyTerms aTerms,
                                  @NonNull final TradePartyType aTP)
  {
    for (final PhinvoiceIdentifier aID : aTaxRegistrations)
    {
      final boolean bVat = CiiD22BInvoiceReader.TAX_SCHEME_VAT.equalsIgnoreCase (aID.getSchemeID ());
      if (_useSlot (bVat ? aTerms.vatID () : aTerms.taxNumber (), aID.getValue (), aTerms.group ()))
        _taxRegistration (aID.getValue (), aID.getSchemeID () != null ? aID.getSchemeID () : TAX_SCHEME_FC, aTP);
    }
  }

  private void _partyIdentifiers (@NonNull final List <PhinvoiceIdentifier> aIDs,
                                  @NonNull final String sTermID,
                                  @NonNull final TradePartyType aTP)
  {
    // An identifier with a scheme is a global identifier, see _readPartyIdentifiers
    for (final PhinvoiceIdentifier aID : aIDs)
      if (_useIdentifier (aID, sTermID, sTermID + "-1"))
      {
        if (aID.getSchemeID () != null)
          aTP.addGlobalID (_id (aID));
        else
          aTP.addID (_id (aID));
      }
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

  /**
   * Write a trade party of the profile EXTENDED (a BG-X).
   */
  @Nullable
  private TradePartyType _tradeParty (@Nullable final PhinvoiceTradeParty aParty, @NonNull final PartyTerms aTerms)
  {
    if (aParty == null || aParty.isEmpty () || !m_aFilter.useGroup (aTerms.group (), "trade party"))
      return null;

    final String sGroup = aTerms.group ();
    final TradePartyType ret = new TradePartyType ();
    if (_useSlot (aTerms.id (), aParty.getID (), sGroup))
      ret.addID (new IDType (aParty.getID ()));
    for (final PhinvoiceIdentifier aID : aParty.globalIdentifiers ())
      if (_useSlot (aTerms.globalID (), aID.getValue (), sGroup))
        ret.addGlobalID (_id (aID));
    if (_useSlot (aTerms.name (), aParty.getName (), sGroup))
      ret.setName (aParty.getName ());
    if (_useSlot (aTerms.roleCode (), aParty.getRoleCode (), sGroup))
      ret.addRoleCode (new PartyRoleCodeType (aParty.getRoleCode ()));
    if (_useSlot (aTerms.description (), aParty.getDescription (), sGroup))
      ret.addDescription (new TextType (aParty.getDescription ()));
    final PhinvoiceIdentifier aLegal = aParty.getLegalRegistration ();
    if (aLegal != null && _useSlot (aTerms.legalID (), aLegal.getValue (), sGroup))
      _legalOrganisation (ret).setID (_id (aLegal));
    if (_useSlot (aTerms.tradingName (), aParty.getTradingName (), sGroup))
      _legalOrganisation (ret).setTradingBusinessName (aParty.getTradingName ());
    final TradeAddressType aLegalAddress = _address (aParty.getLegalAddress (), aTerms.legalAddress ());
    if (aLegalAddress != null)
      _legalOrganisation (ret).setPostalTradeAddress (aLegalAddress);
    final TradeContactType aContact = _contact (aParty.getContact (), aTerms.contact ());
    if (aContact != null)
      ret.addDefinedTradeContact (aContact);
    ret.setPostalTradeAddress (_address (aParty.getPostalAddress (), aTerms.address ()));
    final PhinvoiceIdentifier aEA = aParty.getElectronicAddress ();
    if (aEA != null && _useSlot (aTerms.electronicAddress (), aEA.getValue (), sGroup))
    {
      final UniversalCommunicationType aUC = new UniversalCommunicationType ();
      aUC.setURIID (_id (aEA));
      ret.addURIUniversalCommunication (aUC);
    }
    _taxRegistrations (aParty.taxRegistrations (), aTerms, ret);
    return ret;
  }

  /**
   * Write a referenced document of the profile EXTENDED.
   */
  @Nullable
  private ReferencedDocumentType _referencedDocument (@Nullable final PhinvoiceReferencedDocument aDoc,
                                                      @NonNull final DocumentTerms aTerms)
  {
    if (aDoc == null || aDoc.isEmpty ())
      return null;
    if (aTerms.group () != null && !m_aFilter.useGroup (aTerms.group (), "referenced document"))
      return null;

    final String sGroup = aTerms.group ();
    final ReferencedDocumentType ret = new ReferencedDocumentType ();
    if (_useSlot (aTerms.issuerAssignedID (), aDoc.getIssuerAssignedID (), sGroup))
      ret.setIssuerAssignedID (aDoc.getIssuerAssignedID ());
    if (_useSlot (aTerms.uri (), aDoc.getUri (), sGroup))
      ret.setURIID (aDoc.getUri ());
    if (_useSlot (aTerms.lineID (), aDoc.getLineID (), sGroup))
      ret.setLineID (aDoc.getLineID ());
    if (_useSlot (aTerms.typeCode (), aDoc.getTypeCode (), sGroup))
      ret.setTypeCode (aDoc.getTypeCode ());
    if (_useSlot (aTerms.name (), aDoc.getName (), sGroup))
      ret.addName (new TextType (aDoc.getName ()));
    final PhinvoiceAttachment aAttachment = aDoc.getAttachment ();
    if (aAttachment != null && _useSlot (aTerms.attachment (), aAttachment.getBinary (), sGroup))
    {
      final BinaryObjectType aBO = new BinaryObjectType (aAttachment.getBinary ());
      aBO.setMimeCode (aAttachment.getMimeCode ());
      aBO.setFilename (aAttachment.getFilename ());
      ret.addAttachmentBinaryObject (aBO);
    }
    if (_useSlot (aTerms.referenceTypeCode (), aDoc.getReferenceTypeCode (), sGroup))
      ret.setReferenceTypeCode (aDoc.getReferenceTypeCode ());
    if (_useSlot (aTerms.issueDate (), aDoc.getIssueDate (), sGroup))
      ret.setFormattedIssueDateTime (_formattedDateTime (aDoc.getIssueDate ()));
    if (ret.getIssuerAssignedID () == null &&
        ret.getURIID () == null &&
        ret.getLineID () == null &&
        ret.getTypeCode () == null &&
        ret.getName ().isEmpty () &&
        ret.getAttachmentBinaryObject ().isEmpty () &&
        ret.getReferenceTypeCode () == null &&
        ret.getFormattedIssueDateTime () == null)
      return null;
    return ret;
  }

  @Nullable
  private static ReferencedDocumentType _getLast (@NonNull final List <ReferencedDocumentType> aList)
  {
    return aList.isEmpty () ? null : aList.get (aList.size () - 1);
  }

  /** Set the date of a referenced document; the document is created if needed. */
  @Nullable
  private ReferencedDocumentType _documentWithDate (@Nullable final ReferencedDocumentType aRD,
                                                    @Nullable final LocalDate aDate,
                                                    @NonNull final String sDateTerm)
  {
    if (!m_aFilter.use (sDateTerm, aDate))
      return aRD;
    final ReferencedDocumentType ret = aRD != null ? aRD : new ReferencedDocumentType ();
    ret.setFormattedIssueDateTime (_formattedDateTime (aDate));
    return ret;
  }

  @Nullable
  private TradeTaxType _includedTax (@Nullable final PhinvoiceIncludedTax aTax,
                                     @NonNull final String sGroup,
                                     @NonNull final String [] aTerms)
  {
    // Terms: calculated amount, type code, exemption reason, category code, exemption reason code, rate
    if (aTax == null || aTax.isEmpty () || !m_aFilter.useGroup (sGroup, "included tax"))
      return null;
    final TradeTaxType ret = new TradeTaxType ();
    if (m_aFilter.use (aTerms[0], aTax.getCalculatedAmount ()))
      ret.addCalculatedAmount (_amount (aTax.getCalculatedAmount ()));
    if (m_aFilter.use (aTerms[1], aTax.getTypeCode ()))
      ret.setTypeCode (aTax.getTypeCode ());
    if (m_aFilter.use (aTerms[2], aTax.getExemptionReason ()))
      ret.setExemptionReason (aTax.getExemptionReason ());
    if (m_aFilter.use (aTerms[3], aTax.getCategoryCode ()))
      ret.setCategoryCode (aTax.getCategoryCode ());
    if (m_aFilter.use (aTerms[4], aTax.getExemptionReasonCode ()))
      ret.setExemptionReasonCode (aTax.getExemptionReasonCode ());
    if (m_aFilter.use (aTerms[5], aTax.getRate ()))
      ret.setRateApplicablePercent (aTax.getRate ());
    return ret;
  }

  @Nullable
  private TradeDeliveryTermsType _deliveryTerms (@Nullable final PhinvoiceDeliveryTerms aTerms,
                                                 @NonNull final String sGroup,
                                                 @NonNull final String sTypeCodeTerm,
                                                 @NonNull final String sLocationGroup,
                                                 @NonNull final String sCountryTerm,
                                                 @NonNull final String sNameTerm)
  {
    if (aTerms == null || aTerms.isEmpty () || !m_aFilter.useGroup (sGroup, "delivery terms"))
      return null;
    final TradeDeliveryTermsType ret = new TradeDeliveryTermsType ();
    if (m_aFilter.use (sTypeCodeTerm, aTerms.getDeliveryTypeCode ()))
      ret.setDeliveryTypeCode (aTerms.getDeliveryTypeCode ());
    final boolean bCountry = m_aFilter.use (sCountryTerm, aTerms.getLocationCountryCode ());
    final boolean bName = m_aFilter.use (sNameTerm, aTerms.getLocationName ());
    if ((bCountry || bName) && m_aFilter.useGroup (sLocationGroup, "relevant trade location"))
    {
      final TradeLocationType aTL = new TradeLocationType ();
      if (bCountry)
        aTL.setCountryID (aTerms.getLocationCountryCode ());
      if (bName)
        aTL.setName (aTerms.getLocationName ());
      ret.setRelevantTradeLocation (aTL);
    }
    return ret;
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
    final TradeContactType aContact = _contact (aSeller.getContact (), "BG-6", "BT-41", "BT-41-0", "BT-42", "BT-43");
    _contactExtras (aContact, aSeller.getContact (), PhinvoiceExtendedTerms.SELLER.contact ());
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
      _taxRegistration (aSeller.getVatIdentifier (), CiiD22BInvoiceReader.TAX_SCHEME_VAT, ret);
    if (m_aFilter.use ("BT-32", aSeller.getTaxRegistration ()))
    {
      // BT-32-1 is new in EN 16931:2026 - CII D22B always uses "FC"
      if (!TAX_SCHEME_FC.equals (aSeller.getTaxRegistrationSchemeID ()))
        _reportUnsupported ("BT-32-1", aSeller.getTaxRegistrationSchemeID ());
      _taxRegistration (aSeller.getTaxRegistration (), TAX_SCHEME_FC, ret);
    }
    // Factur-X EXTENDED
    if (m_aFilter.use ("BT-X-543", aSeller.getRoleCode ()))
      ret.addRoleCode (new PartyRoleCodeType (aSeller.getRoleCode ()));
    final TradeAddressType aLegalAddress = _address (aSeller.getLegalAddress (), PhinvoiceExtendedTerms.SELLER.legalAddress ());
    if (aLegalAddress != null)
      _legalOrganisation (ret).setPostalTradeAddress (aLegalAddress);
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
    final TradeContactType aContact = _contact (aBuyer.getContact (), "BG-9", "BT-56", "BT-56-0", "BT-57", "BT-58");
    _contactExtras (aContact, aBuyer.getContact (), PhinvoiceExtendedTerms.BUYER.contact ());
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
      _taxRegistration (aBuyer.getVatIdentifier (), CiiD22BInvoiceReader.TAX_SCHEME_VAT, ret);
    // Factur-X EXTENDED
    if (m_aFilter.use ("BT-X-544", aBuyer.getRoleCode ()))
      ret.addRoleCode (new PartyRoleCodeType (aBuyer.getRoleCode ()));
    if (m_aFilter.use ("BT-X-334", aBuyer.getAdditionalLegalInfo ()))
      ret.addDescription (new TextType (aBuyer.getAdditionalLegalInfo ()));
    final TradeAddressType aLegalAddress = _address (aBuyer.getLegalAddress (), PhinvoiceExtendedTerms.BUYER.legalAddress ());
    if (aLegalAddress != null)
      _legalOrganisation (ret).setPostalTradeAddress (aLegalAddress);
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
    // Factur-X EXTENDED
    final PartyTerms aTerms = PhinvoiceExtendedTerms.PAYEE;
    if (m_aFilter.use (aTerms.roleCode (), aPayee.getRoleCode ()))
      ret.addRoleCode (new PartyRoleCodeType (aPayee.getRoleCode ()));
    if (m_aFilter.use (aTerms.tradingName (), aPayee.getTradingName ()))
      _legalOrganisation (ret).setTradingBusinessName (aPayee.getTradingName ());
    final TradeAddressType aLegalAddress = _address (aPayee.getLegalAddress (), aTerms.legalAddress ());
    if (aLegalAddress != null)
      _legalOrganisation (ret).setPostalTradeAddress (aLegalAddress);
    final TradeContactType aContact = _contact (aPayee.getContact (), aTerms.contact ());
    if (aContact != null)
      ret.addDefinedTradeContact (aContact);
    ret.setPostalTradeAddress (_address (aPayee.getPostalAddress (), aTerms.address ()));
    final PhinvoiceIdentifier aEA = aPayee.getElectronicAddress ();
    if (aEA != null && m_aFilter.use (aTerms.electronicAddress (), aEA.getValue ()))
    {
      final UniversalCommunicationType aUC = new UniversalCommunicationType ();
      aUC.setURIID (_id (aEA));
      ret.addURIUniversalCommunication (aUC);
    }
    _taxRegistrations (aPayee.taxRegistrations (), aTerms, ret);
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
      _taxRegistration (aTaxRep.getVatIdentifier (), CiiD22BInvoiceReader.TAX_SCHEME_VAT, ret);
    // Factur-X EXTENDED
    final PartyTerms aTerms = PhinvoiceExtendedTerms.SELLER_TAX_REPRESENTATIVE;
    if (m_aFilter.use (aTerms.id (), aTaxRep.getID ()))
      ret.addID (new IDType (aTaxRep.getID ()));
    for (final PhinvoiceIdentifier aID : aTaxRep.globalIdentifiers ())
      if (m_aFilter.use (aTerms.globalID (), aID.getValue ()))
        ret.addGlobalID (_id (aID));
    if (m_aFilter.use (aTerms.roleCode (), aTaxRep.getRoleCode ()))
      ret.addRoleCode (new PartyRoleCodeType (aTaxRep.getRoleCode ()));
    final PhinvoiceIdentifier aLegal = aTaxRep.getLegalRegistration ();
    if (aLegal != null && m_aFilter.use (aTerms.legalID (), aLegal.getValue ()))
      _legalOrganisation (ret).setID (_id (aLegal));
    if (m_aFilter.use (aTerms.tradingName (), aTaxRep.getTradingName ()))
      _legalOrganisation (ret).setTradingBusinessName (aTaxRep.getTradingName ());
    final TradeAddressType aLegalAddress = _address (aTaxRep.getLegalAddress (), aTerms.legalAddress ());
    if (aLegalAddress != null)
      _legalOrganisation (ret).setPostalTradeAddress (aLegalAddress);
    final TradeContactType aContact = _contact (aTaxRep.getContact (), aTerms.contact ());
    if (aContact != null)
      ret.addDefinedTradeContact (aContact);
    final PhinvoiceIdentifier aEA = aTaxRep.getElectronicAddress ();
    if (aEA != null && m_aFilter.use (aTerms.electronicAddress (), aEA.getValue ()))
    {
      final UniversalCommunicationType aUC = new UniversalCommunicationType ();
      aUC.setURIID (_id (aEA));
      ret.addURIUniversalCommunication (aUC);
    }
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
    if (m_aFilter.use ("BT-X-1", aInvoice.getTestIndicator ()))
      ret.setTestIndicator (_indicator (aInvoice.getTestIndicator ().booleanValue ()));
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
      ret.setIssueDateTime (_dateTime (aInvoice.getIssueDate ()));
    _reportUnsupported ("BT-166", aInvoice.getIssueTime ());
    // Factur-X EXTENDED
    if (m_aFilter.use ("BT-X-2", aInvoice.getDocumentName ()))
      ret.addName (new TextType (aInvoice.getDocumentName ()));
    if (m_aFilter.use ("BT-X-3", aInvoice.getCopyIndicator ()))
      ret.setCopyIndicator (_indicator (aInvoice.getCopyIndicator ().booleanValue ()));
    if (m_aFilter.use ("BT-X-4", aInvoice.getLanguageCode ()))
      ret.addLanguageID (new IDType (aInvoice.getLanguageCode ()));
    if (m_aFilter.use ("BT-X-6", aInvoice.getContractualDueDate ()))
    {
      final SpecifiedPeriodType aPeriod = new SpecifiedPeriodType ();
      aPeriod.setCompleteDateTime (_dateTime (aInvoice.getContractualDueDate ()));
      ret.setEffectiveSpecifiedPeriod (aPeriod);
    }
    if (m_aFilter.useGroup ("BG-1", aInvoice.invoiceNotes ()))
      for (final PhinvoiceInvoiceNote aNote : aInvoice.invoiceNotes ())
      {
        final NoteType aNT = new NoteType ();
        if (m_aFilter.use ("BT-22", aNote.getContent ()))
          aNT.addContent (new TextType (aNote.getContent ()));
        if (m_aFilter.use ("BT-21", aNote.getSubjectCode ()))
          aNT.addSubjectCode (new CodeType (aNote.getSubjectCode ()));
        if (m_aFilter.use ("BT-X-5", aNote.getContentCode ()))
          aNT.setContentCode (aNote.getContentCode ());
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
    // BT-122-1 is new in EN 16931:2026, the binding of EN 16931:2017 has the fixed value 916
    aRD.setTypeCode (EN16931CodeLists.DOCUMENT_TYPE_CODE_SUPPORTING_DOCUMENT);
    if (!EN16931CodeLists.DOCUMENT_TYPE_CODE_SUPPORTING_DOCUMENT.equals (aDoc.getDocumentTypeCode ()))
      _reportUnsupported ("BT-122-1", aDoc.getDocumentTypeCode ());
    if (m_aFilter.use ("BT-123", aDoc.getDescription ()))
      aRD.addName (new TextType (aDoc.getDescription ()));
    _documentWithDate (aRD, aDoc.getIssueDate (), "BT-X-149");
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
    // BT-10 is 0..1 in EN 16931:2017 and has no scheme
    boolean bFirst = true;
    for (final PhinvoiceIdentifier aBuyerRef : aInvoice.buyerReferences ())
    {
      if (bFirst)
      {
        if (m_aFilter.use ("BT-10", aBuyerRef.getValue ()))
          ret.setBuyerReference (aBuyerRef.getValue ());
        _reportUnsupported ("BT-10-1", aBuyerRef.getSchemeID ());
      }
      else
        m_aFilter.drop ("BT-10", aBuyerRef.getValue (), "BT-10 may occur only once in EN 16931:2017");
      bFirst = false;
    }
    ret.setSellerTradeParty (_seller (aInvoice.getSeller ()));
    ret.setBuyerTradeParty (_buyer (aInvoice.getBuyer ()));
    ret.setSellerTaxRepresentativeTradeParty (_taxRepresentative (aInvoice.getSellerTaxRepresentative ()));
    if (m_aFilter.use ("BT-14", aInvoice.getSalesOrderReference ()))
      ret.setSellerOrderReferencedDocument (_referencedDocument (aInvoice.getSalesOrderReference ()));
    ret.setSellerOrderReferencedDocument (_documentWithDate (ret.getSellerOrderReferencedDocument (),
                                                             aInvoice.getSalesOrderDate (),
                                                             "BT-X-146"));
    if (m_aFilter.use ("BT-13", aInvoice.getPurchaseOrderReference ()))
      ret.setBuyerOrderReferencedDocument (_referencedDocument (aInvoice.getPurchaseOrderReference ()));
    ret.setBuyerOrderReferencedDocument (_documentWithDate (ret.getBuyerOrderReferencedDocument (),
                                                            aInvoice.getPurchaseOrderDate (),
                                                            "BT-X-147"));
    if (m_aFilter.use ("BT-12", aInvoice.getContractReference ()))
      ret.setContractReferencedDocument (_referencedDocument (aInvoice.getContractReference ()));
    ret.setContractReferencedDocument (_documentWithDate (ret.getContractReferencedDocument (),
                                                          aInvoice.getContractDate (),
                                                          "BT-X-148"));
    if (ret.getContractReferencedDocument () != null && m_aFilter.use ("BT-X-405", aInvoice.getContractReferenceTypeCode ()))
      ret.getContractReferencedDocument ().setReferenceTypeCode (aInvoice.getContractReferenceTypeCode ());
    // Factur-X EXTENDED
    ret.setQuotationReferencedDocument (_referencedDocument (aInvoice.getQuotationReference (), PhinvoiceExtendedTerms.QUOTATION));
    for (final PhinvoiceReferencedDocument aDoc : aInvoice.ultimateCustomerOrderReferences ())
    {
      final ReferencedDocumentType aRD = _referencedDocument (aDoc, PhinvoiceExtendedTerms.ULTIMATE_CUSTOMER_ORDER);
      if (aRD != null)
        ret.addUltimateCustomerOrderReferencedDocument (aRD);
    }
    ret.setSalesAgentTradeParty (_tradeParty (aInvoice.getSalesAgent (), PhinvoiceExtendedTerms.SALES_AGENT));
    ret.setBuyerTaxRepresentativeTradeParty (_tradeParty (aInvoice.getBuyerTaxRepresentative (),
                                                         PhinvoiceExtendedTerms.BUYER_TAX_REPRESENTATIVE));
    ret.setProductEndUserTradeParty (_tradeParty (aInvoice.getProductEndUser (), PhinvoiceExtendedTerms.PRODUCT_END_USER));
    ret.setBuyerAgentTradeParty (_tradeParty (aInvoice.getBuyerAgent (), PhinvoiceExtendedTerms.BUYER_AGENT));
    ret.setApplicableTradeDeliveryTerms (_deliveryTerms (aInvoice.getDeliveryTerms (),
                                                         "BG-X-22",
                                                         "BT-X-145",
                                                         "BG-X-88",
                                                         "BT-X-563",
                                                         "BT-X-564"));
    if (m_aFilter.useGroup ("BG-24", aInvoice.supportingDocuments ()))
      for (final PhinvoiceSupportingDocument aDoc : aInvoice.supportingDocuments ())
        _supportingDocument (aDoc, ret);
    if (m_aFilter.use ("BT-17", aInvoice.getTenderOrLotReference ()))
    {
      _addDocumentReference (aInvoice.getTenderOrLotReference (),
                             EN16931CodeLists.DOCUMENT_TYPE_CODE_ORIGINATOR_DOCUMENT,
                             null,
                             ret);
      _documentWithDate (_getLast (ret.getAdditionalReferencedDocument ()), aInvoice.getTenderOrLotReferenceDate (), "BT-X-556");
    }
    final PhinvoiceIdentifier aObjectID = aInvoice.getInvoicedObjectIdentifier ();
    if (_useIdentifier (aObjectID, "BT-18", "BT-18-1"))
    {
      _addDocumentReference (aObjectID.getValue (),
                             EN16931CodeLists.DOCUMENT_TYPE_CODE_OBJECT_DOCUMENT,
                             m_aFilter.use ("BT-18-1", aObjectID.getSchemeID ()) ? aObjectID.getSchemeID () : null,
                             ret);
      _documentWithDate (_getLast (ret.getAdditionalReferencedDocument ()), aInvoice.getInvoicedObjectDate (), "BT-X-557");
    }
    if (m_aFilter.use ("BT-11", aInvoice.getProjectReference ()))
    {
      // The name is mandatory in CII. BT-11-1 is new in EN 16931:2026, but the element is there in
      // any case, so the project name of the model is kept; without one BT-11 is repeated
      final ProcuringProjectType aProject = new ProcuringProjectType ();
      aProject.setID (aInvoice.getProjectReference ());
      aProject.setName (aInvoice.getProjectName () != null ? aInvoice.getProjectName () : aInvoice.getProjectReference ());
      ret.setSpecifiedProcuringProject (aProject);
    }
    else
      _reportUnsupported ("BT-11-1", aInvoice.getProjectName ());
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
      // Factur-X EXTENDED
      final PartyTerms aTerms = PhinvoiceExtendedTerms.SHIP_TO;
      final PhinvoiceTradeParty aExtras = new PhinvoiceTradeParty ().setRoleCode (aDI.getRoleCode ())
                                                                     .setLegalRegistration (aDI.getLegalRegistration ())
                                                                     .setTradingName (aDI.getTradingName ())
                                                                     .setLegalAddress (aDI.getLegalAddress ())
                                                                     .setContact (aDI.getContact ())
                                                                     .setElectronicAddress (aDI.getElectronicAddress ());
      aExtras.taxRegistrations ().addAll (aDI.taxRegistrations ());
      if (!aExtras.isEmpty ())
      {
        final TradePartyType aExtraTP = _tradeParty (aExtras, aTerms);
        if (aExtraTP != null)
        {
          if (ret.getShipToTradeParty () == null)
            ret.setShipToTradeParty (new TradePartyType ());
          final TradePartyType aTP = ret.getShipToTradeParty ();
          aTP.getRoleCode ().addAll (aExtraTP.getRoleCode ());
          if (aExtraTP.getSpecifiedLegalOrganization () != null)
            aTP.setSpecifiedLegalOrganization (aExtraTP.getSpecifiedLegalOrganization ());
          aTP.getDefinedTradeContact ().addAll (aExtraTP.getDefinedTradeContact ());
          aTP.getURIUniversalCommunication ().addAll (aExtraTP.getURIUniversalCommunication ());
          aTP.getSpecifiedTaxRegistration ().addAll (aExtraTP.getSpecifiedTaxRegistration ());
        }
      }
    }
    if (m_aFilter.use ("BT-16", aInvoice.getDespatchAdviceReference ()))
      ret.setDespatchAdviceReferencedDocument (_referencedDocument (aInvoice.getDespatchAdviceReference ()));
    ret.setDespatchAdviceReferencedDocument (_documentWithDate (ret.getDespatchAdviceReferencedDocument (),
                                                                aInvoice.getDespatchAdviceDate (),
                                                                "BT-X-200"));
    if (m_aFilter.use ("BT-15", aInvoice.getReceivingAdviceReference ()))
      ret.setReceivingAdviceReferencedDocument (_referencedDocument (aInvoice.getReceivingAdviceReference ()));
    ret.setReceivingAdviceReferencedDocument (_documentWithDate (ret.getReceivingAdviceReferencedDocument (),
                                                                 aInvoice.getReceivingAdviceDate (),
                                                                 "BT-X-201"));
    // BT-X-202 (BT-197 in EN 16931:2026)
    if (_useExtended ("BT-X-202", aInvoice.getDeliveryNoteReference ()))
      ret.setDeliveryNoteReferencedDocument (_referencedDocument (aInvoice.getDeliveryNoteReference ()));
    ret.setDeliveryNoteReferencedDocument (_documentWithDate (ret.getDeliveryNoteReferencedDocument (),
                                                              aInvoice.getDeliveryNoteDate (),
                                                              "BT-X-203"));
    // Factur-X EXTENDED
    ret.setUltimateShipToTradeParty (_tradeParty (aInvoice.getUltimateShipTo (), PhinvoiceExtendedTerms.ULTIMATE_SHIP_TO));
    ret.setShipFromTradeParty (_tradeParty (aInvoice.getShipFrom (), PhinvoiceExtendedTerms.SHIP_FROM));
    if (m_aFilter.use ("BT-X-152", aInvoice.getTransportModeCode ()) && m_aFilter.useGroup ("BG-X-24", "consignment"))
    {
      final LogisticsTransportMovementType aLTM = new LogisticsTransportMovementType ();
      aLTM.setModeCode (aInvoice.getTransportModeCode ());
      final SupplyChainConsignmentType aSCC = new SupplyChainConsignmentType ();
      aSCC.addSpecifiedLogisticsTransportMovement (aLTM);
      ret.setRelatedSupplyChainConsignment (aSCC);
    }
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
      if (m_aFilter.use ("BT-X-262", aBD.getLineTotalBasisAmount ()))
        aTT.addLineTotalBasisAmount (_amount (aBD.getLineTotalBasisAmount ()));
      if (m_aFilter.use ("BT-X-263", aBD.getAllowanceChargeBasisAmount ()))
        aTT.addAllowanceChargeBasisAmount (_amount (aBD.getAllowanceChargeBasisAmount ()));
      // BT-116-1 is UBL only
      _reportUnsupported ("BT-116-1", aBD.getTaxableAmountCurrency ());
      _reportUnsupported ("BT-184", aBD.getCurrency ());
      _reportUnsupported ("BT-210", aBD.getGoodsServicesCode ());
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

  private void _sequenceAndBasisQuantity (@NonNull final TradeAllowanceChargeType aAC,
                                          @Nullable final BigDecimal aSequence,
                                          @Nullable final BigDecimal aBasisQuantity,
                                          @Nullable final String sBasisQuantityUnit,
                                          @NonNull final String sSequenceTerm,
                                          @NonNull final String sQuantityTerm,
                                          @NonNull final String sUnitTerm)
  {
    if (m_aFilter.use (sSequenceTerm, aSequence))
      aAC.setSequenceNumeric (aSequence);
    aAC.setBasisQuantity (_quantity (aBasisQuantity, sBasisQuantityUnit, sQuantityTerm, sUnitTerm));
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
    ret.addCategoryTradeTax (_categoryTax (aDA.getVatCategoryCode (),
                                           aDA.getVatRate (),
                                           aDA.getExemptionReasonText (),
                                           aDA.getExemptionReasonCode (),
                                           "BT-95",
                                           "BT-96",
                                           "BT-173",
                                           "BT-174"));
    _reportUnsupported ("BT-213", aDA.getGoodsServicesCode ());
    _sequenceAndBasisQuantity (ret,
                               aDA.getSequenceNumeric (),
                               aDA.getBasisQuantity (),
                               aDA.getBasisQuantityUnit (),
                               "BT-X-265",
                               "BT-X-266",
                               "BT-X-267");
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
      ret.setReasonCode (aCode);
      if (m_aFilter.isAllowed ("BT-105") && aDC.getReasonCode () != null)
        m_aFilter.drop ("BT-105", aDC.getReasonCode (), "BT-105 and BT-177 share the same element");
    }
    else
      if (m_aFilter.use ("BT-105", aDC.getReasonCode ()))
        ret.setReasonCode (aDC.getReasonCode ());
    if (m_aFilter.use ("BT-104", aDC.getReason ()))
      ret.setReason (aDC.getReason ());
    ret.addCategoryTradeTax (_categoryTax (aDC.getVatCategoryCode (),
                                           aDC.getVatRate (),
                                           aDC.getExemptionReasonText (),
                                           aDC.getExemptionReasonCode (),
                                           "BT-102",
                                           "BT-103",
                                           "BT-175",
                                           "BT-176"));
    _reportUnsupported ("BT-214", aDC.getGoodsServicesCode ());
    _sequenceAndBasisQuantity (ret,
                               aDC.getSequenceNumeric (),
                               aDC.getBasisQuantity (),
                               aDC.getBasisQuantityUnit (),
                               "BT-X-268",
                               "BT-X-269",
                               "BT-X-270");
    return ret;
  }

  @Nullable
  private TradePaymentDiscountTermsType _discountTerms (@NonNull final PhinvoiceEarlyPaymentDiscount aED)
  {
    final TradePaymentDiscountTermsType ret = new TradePaymentDiscountTermsType ();
    if (_useExtended ("BT-X-282", aED.getEndDate ()))
      ret.setBasisDateTime (_dateTime (aED.getEndDate ()));
    if (m_aFilter.use ("BT-X-283", aED.getBasisPeriodMeasure ()))
    {
      final MeasureType aMeasure = new MeasureType (aED.getBasisPeriodMeasure ());
      if (m_aFilter.use ("BT-X-284", aED.getBasisPeriodUnit ()))
        aMeasure.setUnitCode (aED.getBasisPeriodUnit ());
      ret.setBasisPeriodMeasure (aMeasure);
    }
    if (m_aFilter.use ("BT-X-285", aED.getBaseAmount ()))
      ret.setBasisAmount (aED.getBaseAmount ());
    if (_useExtended ("BT-X-286", aED.getPercentage ()))
      ret.setCalculationPercent (aED.getPercentage ());
    if (_useExtended ("BT-X-287", aED.getAmount ()))
      ret.setActualDiscountAmount (aED.getAmount ());
    return ret;
  }

  @Nullable
  private TradePaymentPenaltyTermsType _penaltyTerms (@NonNull final PhinvoiceLatePaymentPenalty aLP)
  {
    final TradePaymentPenaltyTermsType ret = new TradePaymentPenaltyTermsType ();
    if (_useExtended ("BT-X-276", aLP.getStartDate ()))
      ret.setBasisDateTime (_dateTime (aLP.getStartDate ()));
    if (m_aFilter.use ("BT-X-277", aLP.getBasisPeriodMeasure ()))
    {
      final MeasureType aMeasure = new MeasureType (aLP.getBasisPeriodMeasure ());
      if (m_aFilter.use ("BT-X-278", aLP.getBasisPeriodUnit ()))
        aMeasure.setUnitCode (aLP.getBasisPeriodUnit ());
      ret.setBasisPeriodMeasure (aMeasure);
    }
    if (m_aFilter.use ("BT-X-279", aLP.getBaseAmount ()))
      ret.setBasisAmount (aLP.getBaseAmount ());
    if (_useExtended ("BT-X-280", aLP.getYearlyInterestPercentage ()))
      ret.setCalculationPercent (aLP.getYearlyInterestPercentage ());
    if (_useExtended ("BT-X-281", aLP.getAmount ()))
      ret.setActualPenaltyAmount (aLP.getAmount ());
    return ret;
  }

  private void _paymentTerms (@NonNull final PhinvoiceInvoice aInvoice,
                              @Nullable final PhinvoiceDirectDebit aDirectDebit,
                              @NonNull final HeaderTradeSettlementType aHTS)
  {
    if (m_aFilter.isAllowed ("BT-X-275"))
    {
      // Factur-X EXTENDED: one CII payment terms per payment terms of the model, each with at most one
      // discount and one penalty
      for (final PhinvoicePaymentTerms aPT : aInvoice.paymentTerms ())
      {
        final TradePaymentTermsType aTPT = new TradePaymentTermsType ();
        if (m_aFilter.use ("BT-20", aPT.getText ()))
          aTPT.addDescription (new TextType (aPT.getText ()));
        if (m_aFilter.use ("BT-X-275", aPT.getPartialPaymentAmount ()))
          aTPT.addPartialPaymentAmount (_amount (aPT.getPartialPaymentAmount ()));
        if (m_aFilter.use ("BT-9", aPT.getDueDate ()))
          aTPT.setDueDateDateTime (_dateTime (aPT.getDueDate ()));
        boolean bFirst = true;
        for (final PhinvoiceEarlyPaymentDiscount aED : aPT.earlyDiscounts ())
        {
          if (bFirst && m_aFilter.useGroup ("BG-X-44", "early payment discount"))
            aTPT.setApplicableTradePaymentDiscountTerms (_discountTerms (aED));
          else
            if (!bFirst)
              m_aFilter.drop ("BG-X-44", aED.getEndDate (), "CII has only one payment discount per payment terms");
          bFirst = false;
        }
        bFirst = true;
        for (final PhinvoiceLatePaymentPenalty aLP : aPT.latePenalties ())
        {
          if (bFirst && m_aFilter.useGroup ("BG-X-43", "late payment penalty"))
            aTPT.setApplicableTradePaymentPenaltyTerms (_penaltyTerms (aLP));
          else
            if (!bFirst)
              m_aFilter.drop ("BG-X-43", aLP.getStartDate (), "CII has only one payment penalty per payment terms");
          bFirst = false;
        }
        final TradePartyType aPayee = _tradeParty (aPT.getPayee (), PhinvoiceExtendedTerms.PAYMENT_TERMS_PAYEE);
        if (aPayee != null)
          aTPT.addPayeeTradeParty (aPayee);
        aHTS.addSpecifiedTradePaymentTerms (aTPT);
      }
    }
    else
    {
      // EN 16931:2017 has one payment terms text (BT-20); the rest is Factur-X EXTENDED
      final StringBuilder aText = new StringBuilder ();
      for (final PhinvoicePaymentTerms aPT : aInvoice.paymentTerms ())
      {
        if (aPT.getText () != null)
          aText.append (aText.length () > 0 ? "\n" : "").append (aPT.getText ());
        m_aFilter.use ("BT-X-275", aPT.getPartialPaymentAmount ());
        if (aPT.getDueDate () != null)
          m_aFilter.drop ("BT-9", aPT.getDueDate (), "EN 16931 has only one payment due date");
        if (aPT.getPayee () != null && !aPT.getPayee ().isEmpty ())
          m_aFilter.useGroup ("BG-X-77", "payee of the payment terms");
        // Only reported, the target has no such terms
        for (final PhinvoiceEarlyPaymentDiscount aED : aPT.earlyDiscounts ())
          _discountTerms (aED);
        for (final PhinvoiceLatePaymentPenalty aLP : aPT.latePenalties ())
          _penaltyTerms (aLP);
      }
      final TradePaymentTermsType aTPT = new TradePaymentTermsType ();
      if (m_aFilter.use ("BT-20", aText.toString ()))
        aTPT.addDescription (new TextType (aText.toString ()));
      aHTS.addSpecifiedTradePaymentTerms (aTPT);
    }

    // BT-9 and BT-89 go into the first payment terms
    final boolean bDueDate = m_aFilter.use ("BT-9", aInvoice.getPaymentDueDate ());
    final boolean bMandate = aDirectDebit != null && m_aFilter.use ("BT-89", aDirectDebit.getMandateReferenceID ());
    if (bDueDate || bMandate)
    {
      // A first payment terms with a due date of its own gets a payment terms before it, because
      // the first due date is the payment due date of the invoice
      final List <TradePaymentTermsType> aAllTerms = aHTS.getSpecifiedTradePaymentTerms ();
      if (aAllTerms.isEmpty () || (bDueDate && aAllTerms.get (0).getDueDateDateTime () != null))
        aAllTerms.add (0, new TradePaymentTermsType ());
      final TradePaymentTermsType aFirst = aAllTerms.get (0);
      if (bDueDate)
        aFirst.setDueDateDateTime (_dateTime (aInvoice.getPaymentDueDate ()));
      if (bMandate)
        aFirst.addDirectDebitMandateID (new IDType (aDirectDebit.getMandateReferenceID ()));
    }
    // An empty payment terms is not written
    aHTS.getSpecifiedTradePaymentTerms ()
        .removeIf (x -> x.getDescription ().isEmpty () &&
                        x.getDueDateDateTime () == null &&
                        x.getDirectDebitMandateID ().isEmpty () &&
                        x.getPartialPaymentAmount ().isEmpty () &&
                        x.getApplicableTradePaymentDiscountTerms () == null &&
                        x.getApplicableTradePaymentPenaltyTerms () == null &&
                        x.getPayeeTradeParty ().isEmpty ());
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
    {
      ret.setBillingSpecifiedPeriod (_period (aPeriod, "BT-73", "BT-74"));
      if (m_aFilter.use ("BT-X-264", aPeriod.getDescription ()))
        ret.getBillingSpecifiedPeriod ().addDescription (new TextType (aPeriod.getDescription ()));
    }
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
          // BT-X-555 (BT-202 in EN 16931:2026)
          if (_useExtended ("BT-X-555", aRef.getTypeCode ()))
            aRD.setTypeCode (aRef.getTypeCode ());
          ret.addInvoiceReferencedDocument (aRD);
        }
    if (m_aFilter.use ("BT-19", aInvoice.getBuyerAccountingReference ()))
    {
      final TradeAccountingAccountType aTAA = new TradeAccountingAccountType ();
      aTAA.setID (aInvoice.getBuyerAccountingReference ());
      if (m_aFilter.use ("BT-X-290", aInvoice.getBuyerAccountingReferenceTypeCode ()))
        aTAA.setTypeCode (aInvoice.getBuyerAccountingReferenceTypeCode ());
      ret.addReceivableSpecifiedTradeAccountingAccount (aTAA);
    }
    // Factur-X EXTENDED
    if (m_aFilter.use ("BT-X-204", aInvoice.getSellerReference ()))
      ret.setInvoiceIssuerReference (aInvoice.getSellerReference ());
    ret.setInvoicerTradeParty (_tradeParty (aInvoice.getInvoicer (), PhinvoiceExtendedTerms.INVOICER));
    ret.setInvoiceeTradeParty (_tradeParty (aInvoice.getInvoicee (), PhinvoiceExtendedTerms.INVOICEE));
    ret.setPayerTradeParty (_tradeParty (aInvoice.getPayer (), PhinvoiceExtendedTerms.PAYER));
    final PhinvoiceTaxCurrencyExchange aExchange = aInvoice.getTaxCurrencyExchange ();
    if (aExchange != null && !aExchange.isEmpty () && m_aFilter.useGroup ("BG-X-41", "tax currency exchange"))
    {
      final TradeCurrencyExchangeType aTCE = new TradeCurrencyExchangeType ();
      if (m_aFilter.use ("BT-X-258", aExchange.getSourceCurrency ()))
        aTCE.setSourceCurrencyCode (aExchange.getSourceCurrency ());
      if (m_aFilter.use ("BT-X-259", aExchange.getTargetCurrency ()))
        aTCE.setTargetCurrencyCode (aExchange.getTargetCurrency ());
      if (m_aFilter.use ("BT-X-260", aExchange.getRate ()))
        aTCE.setConversionRate (aExchange.getRate ());
      if (m_aFilter.use ("BT-X-261", aExchange.getRateDate ()))
        aTCE.setConversionRateDateTime (_dateTime (aExchange.getRateDate ()));
      ret.setTaxApplicableTradeCurrencyExchange (aTCE);
    }
    if (m_aFilter.useGroup ("BG-X-42", aInvoice.logisticsServiceCharges ()))
      for (final PhinvoiceLogisticsServiceCharge aCharge : aInvoice.logisticsServiceCharges ())
      {
        final LogisticsServiceChargeType aLSC = new LogisticsServiceChargeType ();
        if (m_aFilter.use ("BT-X-271", aCharge.getDescription ()))
          aLSC.addDescription (new TextType (aCharge.getDescription ()));
        if (m_aFilter.use ("BT-X-272", aCharge.getAmount ()))
          aLSC.addAppliedAmount (_amount (aCharge.getAmount ()));
        final TradeTaxType aTT = _vatTax ();
        if (m_aFilter.use ("BT-X-591", aCharge.getTaxExemptionReason ()))
          aTT.setExemptionReason (aCharge.getTaxExemptionReason ());
        if (m_aFilter.use ("BT-X-273", aCharge.getTaxCategoryCode ()))
          aTT.setCategoryCode (aCharge.getTaxCategoryCode ());
        if (m_aFilter.use ("BT-X-592", aCharge.getTaxExemptionReasonCode ()))
          aTT.setExemptionReasonCode (aCharge.getTaxExemptionReasonCode ());
        if (m_aFilter.use ("BT-X-274", aCharge.getTaxRate ()))
          aTT.setRateApplicablePercent (aCharge.getTaxRate ());
        aLSC.addAppliedTradeTax (aTT);
        ret.addSpecifiedLogisticsServiceCharge (aLSC);
      }
    if (m_aFilter.useGroup ("BG-X-45", aInvoice.advancePayments ()))
      for (final PhinvoiceAdvancePayment aPayment : aInvoice.advancePayments ())
      {
        final AdvancePaymentType aAP = new AdvancePaymentType ();
        if (m_aFilter.use ("BT-X-291", aPayment.getPaidAmount ()))
          aAP.setPaidAmount (aPayment.getPaidAmount ());
        if (m_aFilter.use ("BT-X-292", aPayment.getReceivedDate ()))
          aAP.setFormattedReceivedDateTime (_formattedDateTime (aPayment.getReceivedDate ()));
        for (final PhinvoiceIncludedTax aTax : aPayment.includedTaxes ())
        {
          final TradeTaxType aTT = _includedTax (aTax,
                                                 "BG-X-46",
                                                 new String [] { "BT-X-293", "BT-X-294", "BT-X-295", "BT-X-296", "BT-X-297", "BT-X-298" });
          if (aTT != null)
            aAP.addIncludedTradeTax (aTT);
        }
        final ReferencedDocumentType aRD = _referencedDocument (aPayment.getPrecedingInvoice (),
                                                                PhinvoiceExtendedTerms.ADVANCE_PAYMENT_INVOICE);
        if (aRD != null)
          aAP.addInvoiceSpecifiedReferencedDocument (aRD);
        ret.addSpecifiedAdvancePayment (aAP);
      }
    _reportUnsupported ("BT-167", aInvoice.getVatExchangeRate ());
    _reportUnsupported ("BT-167-1", aInvoice.getVatExchangeRateTargetCurrency ());
    _reportUnsupported ("BT-167-2", aInvoice.getVatExchangeRateSourceCurrency ());
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
        // BT-X-11 / BT-X-12 (BT-211 / BT-212 in EN 16931:2026)
        if (_useExtended ("BT-X-11", aAttr.getCode ()))
          aPC.setTypeCode (aAttr.getCode ());
        if (_useExtended ("BT-X-12", aAttr.getNumericValue ()))
        {
          final MeasureType aMeasure = new MeasureType (aAttr.getNumericValue ());
          if (m_aFilter.use ("BT-X-12-0", aAttr.getValueUnit ()))
            aMeasure.setUnitCode (aAttr.getValueUnit ());
          aPC.setValueMeasure (aMeasure);
        }
        else
          if (aAttr.getValueUnit () != null)
            m_aFilter.drop ("BT-X-12-0", aAttr.getValueUnit (), "the numeric item attribute value is missing");
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
        if (m_aFilter.use ("BT-X-13", aIC.getName ()))
          aPC.addClassName (new TextType (aIC.getName ()));
        ret.addDesignatedProductClassification (aPC);
      }
    if (m_aFilter.use ("BT-159", aItem.getCountryOfOrigin ()))
    {
      final TradeCountryType aTC = new TradeCountryType ();
      aTC.setID (aItem.getCountryOfOrigin ());
      ret.setOriginTradeCountry (aTC);
    }
    _reportUnsupported ("BT-196", aItem.getGoodsServicesCode ());
    // Factur-X EXTENDED
    if (m_aFilter.use ("BT-X-305", aItem.getID ()))
      ret.setID (aItem.getID ());
    if (m_aFilter.use ("BT-X-532", aItem.getIndustryAssignedID ()))
      ret.setIndustryAssignedID (aItem.getIndustryAssignedID ());
    if (m_aFilter.use ("BT-X-533", aItem.getModelID ()))
      ret.setModelID (aItem.getModelID ());
    if (m_aFilter.use ("BT-X-534", aItem.getBatchID ()))
      ret.addBatchID (new IDType (aItem.getBatchID ()));
    if (m_aFilter.use ("BT-X-535", aItem.getBrandName ()))
      ret.setBrandName (aItem.getBrandName ());
    if (m_aFilter.use ("BT-X-536", aItem.getModelName ()))
      ret.setModelName (aItem.getModelName ());
    ret.setManufacturerTradeParty (_tradeParty (aItem.getManufacturer (), PhinvoiceExtendedTerms.MANUFACTURER));
    if (m_aFilter.useGroup ("BG-X-84", aItem.productInstances ()))
      for (final PhinvoiceProductInstance aInstance : aItem.productInstances ())
      {
        final TradeProductInstanceType aTPI = new TradeProductInstanceType ();
        if (m_aFilter.use ("BT-X-306", aInstance.getBatchID ()))
          aTPI.setBatchID (aInstance.getBatchID ());
        if (m_aFilter.use ("BT-X-307", aInstance.getSerialID ()))
          aTPI.setSupplierAssignedSerialID (aInstance.getSerialID ());
        ret.addIndividualTradeProductInstance (aTPI);
      }
    if (m_aFilter.useGroup ("BG-X-1", aItem.includedProducts ()))
      for (final PhinvoiceIncludedProduct aProduct : aItem.includedProducts ())
      {
        final ReferencedProductType aRP = new ReferencedProductType ();
        if (m_aFilter.use ("BT-X-308", aProduct.getID ()))
          aRP.addID (new IDType (aProduct.getID ()));
        for (final PhinvoiceIdentifier aID : aProduct.globalIdentifiers ())
          if (m_aFilter.use ("BT-X-15", aID.getValue ()))
            aRP.addGlobalID (_id (aID));
        if (m_aFilter.use ("BT-X-16", aProduct.getSellerAssignedID ()))
          aRP.setSellerAssignedID (aProduct.getSellerAssignedID ());
        if (m_aFilter.use ("BT-X-17", aProduct.getBuyerAssignedID ()))
          aRP.setBuyerAssignedID (aProduct.getBuyerAssignedID ());
        if (m_aFilter.use ("BT-X-309", aProduct.getIndustryAssignedID ()))
          aRP.addIndustryAssignedID (new IDType (aProduct.getIndustryAssignedID ()));
        if (m_aFilter.use ("BT-X-18", aProduct.getName ()))
          aRP.addName (new TextType (aProduct.getName ()));
        if (m_aFilter.use ("BT-X-19", aProduct.getDescription ()))
          aRP.addDescription (new TextType (aProduct.getDescription ()));
        if (m_aFilter.use ("BT-X-20", aProduct.getUnitQuantity ()))
        {
          final QuantityType aQ = new QuantityType (aProduct.getUnitQuantity ());
          if (m_aFilter.use ("BT-X-20-1", aProduct.getUnitQuantityUnit ()))
            aQ.setUnitCode (aProduct.getUnitQuantityUnit ());
          aRP.addUnitQuantity (aQ);
        }
        ret.addIncludedReferencedProduct (aRP);
      }
    return ret;
  }

  /**
   * An allowance or a charge on the gross price.
   *
   * @param aTerms
   *        The terms of amount, percentage, base amount, reason code and reason
   */
  @Nullable
  private TradeAllowanceChargeType _priceAllowanceCharge (@NonNull final PhinvoicePriceAllowanceCharge aPAC,
                                                          final boolean bCharge,
                                                          @NonNull final String [] aTerms)
  {
    final TradeAllowanceChargeType ret = _allowanceCharge (bCharge);
    if (m_aFilter.use (aTerms[1], aPAC.getPercentage ()))
      ret.setCalculationPercent (aPAC.getPercentage ());
    if (m_aFilter.use (aTerms[2], aPAC.getBaseAmount ()))
      ret.setBasisAmount (aPAC.getBaseAmount ());
    if (m_aFilter.use (aTerms[0], aPAC.getAmount ()))
      ret.addActualAmount (_amount (aPAC.getAmount ()));
    if (bCharge && m_aFilter.use ("BT-X-616", aPAC.getNonVatTaxCode ()))
    {
      final AllowanceChargeReasonCodeType aCode = new AllowanceChargeReasonCodeType (aPAC.getNonVatTaxCode ());
      if (m_aFilter.use ("BT-X-616-1", aPAC.getNonVatTaxCodeListID ()))
        aCode.setListID (aPAC.getNonVatTaxCodeListID ());
      ret.setReasonCode (aCode);
      if (aPAC.getReasonCode () != null)
        m_aFilter.drop (aTerms[3], aPAC.getReasonCode (), "BT-X-314 and BT-X-616 share the same element");
    }
    else
    {
      if (!bCharge && aPAC.getNonVatTaxCode () != null)
        m_aFilter.drop ("BT-X-616", aPAC.getNonVatTaxCode (), "an allowance on the gross price has no non-VAT tax code");
      if (m_aFilter.use (aTerms[3], aPAC.getReasonCode ()))
        ret.setReasonCode (aPAC.getReasonCode ());
    }
    if (m_aFilter.use (aTerms[4], aPAC.getReason ()))
      ret.setReason (aPAC.getReason ());
    if (ret.getCalculationPercent () == null &&
        ret.getBasisAmount () == null &&
        ret.getActualAmount ().isEmpty () &&
        ret.getReasonCode () == null &&
        ret.getReason () == null)
      return null;
    return ret;
  }

  /**
   * A line level document reference of the profile EXTENDED that is carried by fields of the line:
   * document reference, line and date.
   */
  @Nullable
  private ReferencedDocumentType _lineDocument (@Nullable final String sID,
                                                @Nullable final String sLineID,
                                                @Nullable final LocalDate aDate,
                                                @NonNull final DocumentTerms aTerms)
  {
    final PhinvoiceReferencedDocument aDoc = new PhinvoiceReferencedDocument ().setIssuerAssignedID (sID)
                                                                                .setLineID (sLineID)
                                                                                .setIssueDate (aDate);
    return _referencedDocument (aDoc, aTerms);
  }

  @NonNull
  private LineTradeAgreementType _lineAgreement (@NonNull final PhinvoiceInvoiceLine aLine)
  {
    final LineTradeAgreementType ret = new LineTradeAgreementType ();
    // BT-X-537 to BT-X-539 (BT-200 / BT-201 in EN 16931:2026)
    ret.setSellerOrderReferencedDocument (_lineDocument (aLine.getSalesOrderReference (),
                                                         aLine.getSalesOrderLineReference (),
                                                         aLine.getSalesOrderDate (),
                                                         PhinvoiceExtendedTerms.LINE_SALES_ORDER));
    // BT-X-21 (BT-188 in EN 16931:2026), BT-132, BT-X-22
    ret.setBuyerOrderReferencedDocument (_lineDocument (aLine.getPurchaseOrderReference (),
                                                        aLine.getPurchaseOrderLineRef (),
                                                        aLine.getPurchaseOrderDate (),
                                                        PhinvoiceExtendedTerms.LINE_BUYER_ORDER));
    ret.setQuotationReferencedDocument (_referencedDocument (aLine.getQuotationReference (), PhinvoiceExtendedTerms.LINE_QUOTATION));
    ret.setContractReferencedDocument (_referencedDocument (aLine.getContractReference (), PhinvoiceExtendedTerms.LINE_CONTRACT));
    for (final PhinvoiceReferencedDocument aDoc : aLine.additionalReferencedDocuments ())
    {
      final ReferencedDocumentType aRD = _referencedDocument (aDoc, PhinvoiceExtendedTerms.LINE_ADDITIONAL);
      if (aRD != null)
        ret.addAdditionalReferencedDocument (aRD);
    }
    for (final PhinvoiceReferencedDocument aDoc : aLine.ultimateCustomerOrderReferences ())
    {
      final ReferencedDocumentType aRD = _referencedDocument (aDoc, PhinvoiceExtendedTerms.LINE_ULTIMATE_CUSTOMER_ORDER);
      if (aRD != null)
        ret.addUltimateCustomerOrderReferencedDocument (aRD);
    }
    ret.setItemSellerTradeParty (_tradeParty (aLine.getLineSeller (), PhinvoiceExtendedTerms.LINE_SELLER));
    ret.setApplicableTradeDeliveryTerms (_deliveryTerms (aLine.getDeliveryTerms (),
                                                         "BG-X-87",
                                                         "BT-X-562",
                                                         "BG-X-89",
                                                         "BT-X-565",
                                                         "BT-X-566"));
    final PhinvoicePriceDetails aPrice = aLine.getPrice ();
    if (aPrice != null && m_aFilter.useGroup ("BG-29", "price details"))
    {
      if (m_aFilter.use ("BT-148", aPrice.getGrossPrice ()))
      {
        final TradePriceType aGross = new TradePriceType ();
        aGross.addChargeAmount (_amount (aPrice.getGrossPrice ()));
        // EN 16931 has one allowance (BT-147), Factur-X EXTENDED any number of allowances and charges
        final boolean bExtended = m_aFilter.isAllowed ("BT-X-34");
        int nIndex = 0;
        for (final PhinvoicePriceAllowanceCharge aPAC : aPrice.allowances ())
        {
          if (nIndex++ > 0 && !bExtended)
            m_aFilter.drop ("BT-147", aPAC.getAmount (), "EN 16931 has only one item price discount");
          else
          {
            final TradeAllowanceChargeType aAC = _priceAllowanceCharge (aPAC,
                                                                        false,
                                                                        new String [] { "BT-147", "BT-X-34", "BT-X-35", "BT-X-313", "BT-X-36" });
            if (aAC != null)
              aGross.addAppliedTradeAllowanceCharge (aAC);
          }
        }
        for (final PhinvoicePriceAllowanceCharge aPAC : aPrice.charges ())
        {
          final TradeAllowanceChargeType aAC = _priceAllowanceCharge (aPAC,
                                                                      true,
                                                                      new String [] { "BT-X-302", "BT-X-300", "BT-X-301", "BT-X-314", "BT-X-303" });
          if (aAC != null)
            aGross.addAppliedTradeAllowanceCharge (aAC);
        }
        ret.setGrossPriceProductTradePrice (aGross);
      }
      else
      {
        // CII carries the allowances and charges only with the gross price
        for (final PhinvoicePriceAllowanceCharge aPAC : aPrice.allowances ())
          if (!aPAC.isEmpty ())
            m_aFilter.drop ("BT-147",
                            aPAC.getAmount () != null ? aPAC.getAmount () : "allowance on the gross price",
                            "CII carries the discount only with the gross price (BT-148)");
        for (final PhinvoicePriceAllowanceCharge aPAC : aPrice.charges ())
          if (!aPAC.isEmpty ())
            m_aFilter.drop ("BT-X-302",
                            aPAC.getAmount () != null ? aPAC.getAmount () : "charge on the gross price",
                            "CII carries the charge only with the gross price (BT-148)");
      }
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
      // BG-X-4
      final TradeTaxType aIncludedTax = _includedTax (aLine.getIncludedTax (),
                                                      "BG-X-4",
                                                      new String [] { "BT-X-37", "BT-X-38", "BT-X-39", "BT-X-40", "BT-X-41", "BT-X-42" });
      if (aIncludedTax != null)
        aNet.addIncludedTradeTax (aIncludedTax);
      ret.setNetPriceProductTradePrice (aNet);
    }
    return ret;
  }

  @Nullable
  private QuantityType _quantity (@Nullable final BigDecimal aValue,
                                  @Nullable final String sUnit,
                                  @NonNull final String sTerm,
                                  @NonNull final String sUnitTerm)
  {
    if (!m_aFilter.use (sTerm, aValue))
      return null;
    final QuantityType ret = new QuantityType (aValue);
    if (m_aFilter.use (sUnitTerm, sUnit))
      ret.setUnitCode (sUnit);
    return ret;
  }

  @NonNull
  private LineTradeDeliveryType _lineDelivery (@NonNull final PhinvoiceInvoiceLine aLine)
  {
    final LineTradeDeliveryType ret = new LineTradeDeliveryType ();
    ret.setBilledQuantity (_quantity (aLine.getQuantity (), aLine.getQuantityUnit (), "BT-129", "BT-130"));
    ret.setChargeFreeQuantity (_quantity (aLine.getChargeFreeQuantity (), aLine.getChargeFreeQuantityUnit (), "BT-X-46", "BT-X-46-0"));
    ret.setPackageQuantity (_quantity (aLine.getPackageQuantity (), aLine.getPackageQuantityUnit (), "BT-X-47", "BT-X-47-0"));
    ret.setPerPackageUnitQuantity (_quantity (aLine.getPerPackageUnitQuantity (),
                                              aLine.getPerPackageUnitQuantityUnit (),
                                              "BT-X-561",
                                              "BT-X-561-0"));
    final PhinvoiceLineDelivery aLD = aLine.getDelivery ();
    if (aLD != null && !aLD.isEmpty ())
    {
      // BG-37 / BG-38 are new in EN 16931:2026; Factur-X EXTENDED has the ship to party of the line
      // (BG-X-7)
      final PartyTerms aTerms = PhinvoiceExtendedTerms.LINE_SHIP_TO;
      final PhinvoiceTradeParty aParty = new PhinvoiceTradeParty ().setID (aLD.getPartyID ())
                                                                    .setName (aLD.getPartyName ())
                                                                    .setRoleCode (aLD.getRoleCode ())
                                                                    .setLegalRegistration (aLD.getLegalRegistration ())
                                                                    .setTradingName (aLD.getTradingName ())
                                                                    .setContact (aLD.getContact ())
                                                                    .setPostalAddress (aLD.getPostalAddress ())
                                                                    .setElectronicAddress (aLD.getElectronicAddress ());
      if (aLD.getLocationIdentifier () != null)
        aParty.globalIdentifiers ().add (aLD.getLocationIdentifier ());
      aParty.taxRegistrations ().addAll (aLD.taxRegistrations ());
      ret.setShipToTradeParty (_tradeParty (aParty, aTerms));
      if (_useExtended ("BT-X-85", aLD.getActualDeliveryDate ()))
      {
        final SupplyChainEventType aEvent = new SupplyChainEventType ();
        aEvent.setOccurrenceDateTime (_dateTime (aLD.getActualDeliveryDate ()));
        ret.setActualDeliverySupplyChainEvent (aEvent);
      }
    }
    ret.setUltimateShipToTradeParty (_tradeParty (aLine.getUltimateShipTo (), PhinvoiceExtendedTerms.LINE_ULTIMATE_SHIP_TO));
    // BT-X-86 to BT-X-94 (BT-189 to BT-199 in EN 16931:2026)
    ret.setDespatchAdviceReferencedDocument (_lineDocument (aLine.getDespatchAdviceReference (),
                                                            aLine.getDespatchAdviceLineReference (),
                                                            aLine.getDespatchAdviceDate (),
                                                            PhinvoiceExtendedTerms.LINE_DESPATCH_ADVICE));
    ret.setReceivingAdviceReferencedDocument (_lineDocument (aLine.getReceivingAdviceReference (),
                                                             aLine.getReceivingAdviceLineReference (),
                                                             aLine.getReceivingAdviceDate (),
                                                             PhinvoiceExtendedTerms.LINE_RECEIVING_ADVICE));
    ret.setDeliveryNoteReferencedDocument (_lineDocument (aLine.getDeliveryNoteReference (),
                                                          aLine.getDeliveryNoteLineReference (),
                                                          aLine.getDeliveryNoteDate (),
                                                          PhinvoiceExtendedTerms.LINE_DELIVERY_NOTE));
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
      // BT-X-95 to BT-X-97 (BT-194 / BT-195 in EN 16931:2026), BT-X-589
      if (m_aFilter.use ("BT-X-95", aVat.getCalculatedAmount ()))
        aTT.addCalculatedAmount (_amount (aVat.getCalculatedAmount ()));
      if (_useExtended ("BT-X-96", aVat.getExemptionReasonText ()))
        aTT.setExemptionReason (aVat.getExemptionReasonText ());
      if (_useExtended ("BT-X-97", aVat.getExemptionReasonCode ()))
        aTT.setExemptionReasonCode (aVat.getExemptionReasonCode ());
      if (m_aFilter.use ("BT-X-589", aVat.getVatPointDateCode ()))
        aTT.setDueDateTypeCode (aVat.getVatPointDateCode ());
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
    if (m_aFilter.use ("BT-X-327", aLine.getChargeTotal ()))
      aSum.addChargeTotalAmount (_amount (aLine.getChargeTotal ()));
    if (m_aFilter.use ("BT-X-328", aLine.getAllowanceTotal ()))
      aSum.addAllowanceTotalAmount (_amount (aLine.getAllowanceTotal ()));
    if (m_aFilter.use ("BT-X-329", aLine.getTaxTotal ()))
      aSum.addTaxTotalAmount (_amount (aLine.getTaxTotal ()));
    if (m_aFilter.use ("BT-X-590", aLine.getTaxTotalAccountingCurrency ()))
      aSum.addTaxTotalAmount (_amount (aLine.getTaxTotalAccountingCurrency ()));
    if (m_aFilter.use ("BT-X-330", aLine.getGrandTotal ()))
      aSum.addGrandTotalAmount (_amount (aLine.getGrandTotal ()));
    if (m_aFilter.use ("BT-X-98", aLine.getTotalAllowanceCharge ()))
      aSum.addTotalAllowanceChargeAmount (_amount (aLine.getTotalAllowanceCharge ()));
    ret.setSpecifiedTradeSettlementLineMonetarySummation (aSum);
    // BG-X-48 (BG-39 in EN 16931:2026)
    for (final PhinvoiceLinePrecedingInvoiceRef aRef : aLine.precedingInvoiceRefs ())
    {
      final PhinvoiceReferencedDocument aDoc = new PhinvoiceReferencedDocument ().setIssuerAssignedID (aRef.getNumber ())
                                                                                  .setLineID (aRef.getLineReference ())
                                                                                  .setTypeCode (aRef.getTypeCode ())
                                                                                  .setIssueDate (aRef.getIssueDate ());
      final ReferencedDocumentType aRD = _referencedDocument (aDoc, PhinvoiceExtendedTerms.LINE_PRECEDING_INVOICE);
      if (aRD != null)
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
      if (m_aFilter.use ("BT-X-99", aLine.getAccountingCostTypeCode ()))
        aTAA.setTypeCode (aLine.getAccountingCostTypeCode ());
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
    final boolean bNote = m_aFilter.use ("BT-127", aLine.getNote ());
    final boolean bContentCode = m_aFilter.use ("BT-X-9", aLine.getNoteContentCode ());
    final boolean bSubjectCode = m_aFilter.use ("BT-X-10", aLine.getNoteSubjectCode ());
    if (bNote || bContentCode || bSubjectCode)
    {
      final NoteType aNote = new NoteType ();
      if (bContentCode)
        aNote.setContentCode (aLine.getNoteContentCode ());
      if (bNote)
        aNote.addContent (new TextType (aLine.getNote ()));
      if (bSubjectCode)
        aNote.addSubjectCode (new CodeType (aLine.getNoteSubjectCode ()));
      aLD.addIncludedNote (aNote);
    }
    if (m_aFilter.use ("BT-X-304", aLine.getParentLineID ()))
      aLD.setParentLineID (aLine.getParentLineID ());
    if (m_aFilter.use ("BT-X-7", aLine.getLineStatusCode ()))
      aLD.setLineStatusCode (aLine.getLineStatusCode ());
    if (m_aFilter.use ("BT-X-8", aLine.getLineStatusReasonCode ()))
      aLD.setLineStatusReasonCode (aLine.getLineStatusReasonCode ());
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
   * Create the CII D22B document.
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
    return ret;
  }
}
