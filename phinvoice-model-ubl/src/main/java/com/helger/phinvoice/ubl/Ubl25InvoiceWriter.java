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
package com.helger.phinvoice.ubl;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.base.equals.EqualsHelper;
import com.helger.base.string.StringHelper;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.datetime.xml.XMLOffsetTime;
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

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.AddressLineType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.AddressType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.AllowanceChargeType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.AnnotationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.AttachmentType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.BillingReferenceLineType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.BillingReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.BranchType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.BuyerAssignedReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.CardAccountType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.CommodityClassificationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.ContactType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.CountryType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.CreditNoteLineType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.CustomerPartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.DeliveryType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.DocumentReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.ExchangeRateType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.ExternalReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.FinancialAccountType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.InterestRateType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.InvoiceLineType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.ItemIdentificationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.ItemPropertyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.ItemType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.LineReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.LocationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.MonetaryTotalType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.OrderLineReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.OrderReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.PartyIdentificationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.PartyLegalEntityType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.PartyNameType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.PartyTaxSchemeType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.PaymentMandateType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.PaymentMeansType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.PaymentTermsType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.PeriodType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.PriceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.ProjectReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.SupplierPartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.TaxCategoryType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.TaxSchemeType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.TaxSubtotalType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_25.TaxTotalType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_25.AdditionalStreetNameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_25.AllowanceChargeReasonCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_25.AllowanceChargeReasonType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_25.AnnotationContentType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_25.BaseQuantityType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_25.BuyerReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_25.CityNameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_25.CompanyIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_25.CompanyLegalFormType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_25.CountrySubentityType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_25.CreditedQuantityType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_25.DescriptionCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_25.DescriptionType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_25.DocumentDescriptionType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_25.EmbeddedDocumentBinaryObjectType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_25.EndpointIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_25.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_25.InvoicedQuantityType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_25.ItemClassificationCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_25.LineType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_25.NameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_25.NoteType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_25.PaymentIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_25.PaymentMeansCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_25.PostalZoneType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_25.StreetNameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_25.TaxAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_25.TaxExemptionReasonType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_25.ValueQuantityType;
import oasis.names.specification.ubl.schema.xsd.creditnote_25.CreditNoteType;
import oasis.names.specification.ubl.schema.xsd.invoice_25.InvoiceType;
import oasis.names.specification.ubl.schema.xsd.unqualifieddatatypes_25.AmountType;

/**
 * Writes a {@link PhinvoiceInvoice} as UBL 2.5 Invoice or Credit Note, for the target EN 16931:2026.
 * It is the inverse of {@link Ubl25InvoiceReader}. Every value goes through the
 * {@link PhinvoiceTermFilter}, so only the terms that the target allows are written and all other
 * values are reported. Package-private worker of {@link UblInvoiceWriter}.
 *
 * @author Philip Helger
 */
final class Ubl25InvoiceWriter
{
  /** The list of the supporting document type code (BT-122-1-1): UNTDID 1001, the only allowed value */
  private static final String DOCUMENT_TYPE_CODE_LIST_ID = "1001";

  /** The tax scheme of all VAT taxes */
  private static final String TAX_SCHEME_VAT = AbstractUblInvoiceReader.TAX_SCHEME_VAT;

  /** The tax scheme of a tax registration without scheme (BT-32) */
  private static final String TAX_SCHEME_LOCAL = AbstractUblInvoiceReader.TAX_SCHEME_LOCAL;

  /** The filler of a mandatory UBL element without a value, which the readers skip */
  private static final String FILLER = "NA";

  private final PhinvoiceTermFilter m_aFilter;
  private String m_sCurrency;

  Ubl25InvoiceWriter (@NonNull final PhinvoiceTermFilter aFilter)
  {
    m_aFilter = aFilter;
  }

  /**
   * Report a value that UBL cannot carry (a CII only term). The filter never allows such a term for
   * a UBL target.
   */
  private void _reportUnsupported (@NonNull final String sTermID, @Nullable final Object aValue)
  {
    if (m_aFilter.use (sTermID, aValue))
      throw new IllegalStateException ("The term " + sTermID + " cannot be written as UBL 2.5");
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

  @NonNull
  private <T extends AmountType> T _currency (@NonNull final T aAmount)
  {
    aAmount.setCurrencyID (m_sCurrency);
    return aAmount;
  }

  @NonNull
  private static IDType _id (@NonNull final PhinvoiceIdentifier aID)
  {
    final IDType ret = new IDType (aID.getValue ());
    ret.setSchemeID (aID.getSchemeID ());
    return ret;
  }

  @NonNull
  private static DocumentReferenceType _documentReference (@NonNull final String sID)
  {
    final DocumentReferenceType ret = new DocumentReferenceType ();
    ret.setID (sID);
    return ret;
  }

  @NonNull
  private static TaxSchemeType _taxScheme (@NonNull final String sID)
  {
    final TaxSchemeType ret = new TaxSchemeType ();
    ret.setID (sID);
    return ret;
  }

  @NonNull
  private static PartyNameType _partyName (@NonNull final String sName)
  {
    final PartyNameType ret = new PartyNameType ();
    ret.setName (sName);
    return ret;
  }

  @NonNull
  private static PartyIdentificationType _partyIdentification (@NonNull final IDType aID)
  {
    final PartyIdentificationType ret = new PartyIdentificationType ();
    ret.setID (aID);
    return ret;
  }

  @Nullable
  private AddressType _address (@Nullable final PhinvoicePostalAddress aAddr,
                                @NonNull final String sGroupID,
                                @NonNull final String [] aTerms)
  {
    if (aAddr == null || aAddr.isEmpty () || !m_aFilter.useGroup (sGroupID, "postal address"))
      return null;

    // Terms: line 1, line 2, line 3, city, post code, subdivision, country
    final AddressType ret = new AddressType ();
    if (m_aFilter.use (aTerms[0], aAddr.getLine1 ()))
      ret.addStreetName (new StreetNameType (aAddr.getLine1 ()));
    if (m_aFilter.use (aTerms[1], aAddr.getLine2 ()))
      ret.addAdditionalStreetName (new AdditionalStreetNameType (aAddr.getLine2 ()));
    if (m_aFilter.use (aTerms[3], aAddr.getCity ()))
      ret.addCityName (new CityNameType (aAddr.getCity ()));
    if (m_aFilter.use (aTerms[4], aAddr.getPostCode ()))
      ret.addPostalZone (new PostalZoneType (aAddr.getPostCode ()));
    if (m_aFilter.use (aTerms[5], aAddr.getCountrySubdivision ()))
      ret.addCountrySubentity (new CountrySubentityType (aAddr.getCountrySubdivision ()));
    // The reader joins several address lines with a newline
    if (m_aFilter.use (aTerms[2], aAddr.getLine3 ()))
      for (final String sLine : StringHelper.getExploded ('\n', aAddr.getLine3 ()))
      {
        final AddressLineType aAL = new AddressLineType ();
        aAL.addLine (new LineType (sLine));
        ret.addAddressLine (aAL);
      }
    if (m_aFilter.use (aTerms[6], aAddr.getCountryCode ()))
    {
      final CountryType aCountry = new CountryType ();
      aCountry.setIdentificationCode (aAddr.getCountryCode ());
      ret.setCountry (aCountry);
    }
    return ret;
  }

  @Nullable
  private ContactType _contact (@Nullable final PhinvoiceContact aContact,
                                @NonNull final String sGroupID,
                                @NonNull final String sName,
                                @NonNull final String sDepartment,
                                @NonNull final String sTelephone,
                                @NonNull final String sEmail)
  {
    if (aContact == null || aContact.isEmpty () || !m_aFilter.useGroup (sGroupID, "contact"))
      return null;

    final ContactType ret = new ContactType ();
    // CII has the contact point as person name or as department name (see the XRechnung examples and
    // the 2026 binding); UBL has one name, so the department is the name if there is no person name
    if (aContact.getName () == null)
    {
      if (m_aFilter.use (sName, aContact.getDepartmentName ()))
        ret.setName (aContact.getDepartmentName ());
    }
    else
    {
      if (m_aFilter.use (sName, aContact.getName ()))
        ret.setName (aContact.getName ());
      if (aContact.getDepartmentName () != null)
        m_aFilter.drop (sDepartment, aContact.getDepartmentName (), "UBL has only one name of the contact point");
    }
    if (m_aFilter.use (sTelephone, aContact.getTelephone ()))
      ret.setTelephone (aContact.getTelephone ());
    if (m_aFilter.use (sEmail, aContact.getEmail ()))
      ret.setElectronicMail (aContact.getEmail ());
    return ret;
  }

  private void _partyIdentifiers (@NonNull final List <PhinvoiceIdentifier> aIDs,
                                  @NonNull final String sTermID,
                                  @NonNull final PartyType aParty)
  {
    for (final PhinvoiceIdentifier aID : aIDs)
      if (_useIdentifier (aID, sTermID, sTermID + "-1"))
        aParty.addPartyIdentification (_partyIdentification (_id (aID)));
  }

  private void _electronicAddress (@Nullable final PhinvoiceIdentifier aID,
                                   @NonNull final String sTermID,
                                   @NonNull final PartyType aParty)
  {
    if (_useIdentifier (aID, sTermID, sTermID + "-1"))
    {
      final EndpointIDType aEndpointID = aParty.setEndpointID (aID.getValue ());
      aEndpointID.setSchemeID (aID.getSchemeID ());
    }
  }

  private static void _partyTaxScheme (@NonNull final String sCompanyID,
                                       @NonNull final String sTaxScheme,
                                       @NonNull final PartyType aParty)
  {
    final PartyTaxSchemeType aPTS = new PartyTaxSchemeType ();
    aPTS.setCompanyID (sCompanyID);
    aPTS.setTaxScheme (_taxScheme (sTaxScheme));
    aParty.addPartyTaxScheme (aPTS);
  }

  @NonNull
  private static PartyLegalEntityType _legalEntity (@NonNull final PartyType aParty)
  {
    if (aParty.getPartyLegalEntity ().isEmpty ())
      aParty.addPartyLegalEntity (new PartyLegalEntityType ());
    return aParty.getPartyLegalEntity ().get (0);
  }

  @NonNull
  private SupplierPartyType _seller (@NonNull final PhinvoiceSeller aSeller, @Nullable final String sSepaCreditorID)
  {
    final PartyType aParty = new PartyType ();
    _electronicAddress (aSeller.getElectronicAddress (), "BT-34", aParty);
    _partyIdentifiers (aSeller.identifiers (), "BT-29", aParty);
    if (sSepaCreditorID != null)
    {
      final IDType aID = new IDType (sSepaCreditorID);
      aID.setSchemeID (AbstractUblInvoiceReader.SEPA_SCHEME_ID);
      aParty.addPartyIdentification (_partyIdentification (aID));
    }
    if (m_aFilter.use ("BT-28", aSeller.getTradingName ()))
      aParty.addPartyName (_partyName (aSeller.getTradingName ()));
    aParty.setPostalAddress (_address (aSeller.getPostalAddress (),
                                       "BG-5",
                                       new String [] { "BT-35", "BT-36", "BT-162", "BT-37", "BT-38", "BT-39", "BT-40" }));
    if (m_aFilter.use ("BT-31", aSeller.getVatIdentifier ()))
      _partyTaxScheme (aSeller.getVatIdentifier (), TAX_SCHEME_VAT, aParty);
    if (m_aFilter.use ("BT-32", aSeller.getTaxRegistration ()))
    {
      // BT-32-1 is the scheme identifier of the company identifier
      final PartyTaxSchemeType aPTS = new PartyTaxSchemeType ();
      final CompanyIDType aCompanyID = aPTS.setCompanyID (aSeller.getTaxRegistration ());
      if (m_aFilter.use ("BT-32-1", aSeller.getTaxRegistrationSchemeID ()))
        aCompanyID.setSchemeID (aSeller.getTaxRegistrationSchemeID ());
      aPTS.setTaxScheme (_taxScheme (TAX_SCHEME_LOCAL));
      aParty.addPartyTaxScheme (aPTS);
    }
    if (m_aFilter.use ("BT-27", aSeller.getName ()))
      _legalEntity (aParty).setRegistrationName (aSeller.getName ());
    final PhinvoiceIdentifier aLegal = aSeller.getLegalRegistration ();
    if (_useIdentifier (aLegal, "BT-30", "BT-30-1"))
      _legalEntity (aParty).setCompanyID (_companyID (aLegal));
    if (m_aFilter.use ("BT-33", aSeller.getAdditionalLegalInfo ()))
      _legalEntity (aParty).addCompanyLegalForm (new CompanyLegalFormType (aSeller.getAdditionalLegalInfo ()));
    aParty.setContact (_contact (aSeller.getContact (), "BG-6", "BT-41", "BT-41-0", "BT-42", "BT-43"));
    m_aFilter.dropAdditionalTerms (aSeller.additionalTerms ());

    final SupplierPartyType ret = new SupplierPartyType ();
    ret.setParty (aParty);
    return ret;
  }

  @NonNull
  private static CompanyIDType _companyID (@NonNull final PhinvoiceIdentifier aID)
  {
    final CompanyIDType ret = new CompanyIDType (aID.getValue ());
    ret.setSchemeID (aID.getSchemeID ());
    return ret;
  }

  @NonNull
  private CustomerPartyType _buyer (@NonNull final PhinvoiceBuyer aBuyer)
  {
    final PartyType aParty = new PartyType ();
    _electronicAddress (aBuyer.getElectronicAddress (), "BT-49", aParty);
    _partyIdentifiers (aBuyer.identifiers (), "BT-46", aParty);
    if (m_aFilter.use ("BT-45", aBuyer.getTradingName ()))
      aParty.addPartyName (_partyName (aBuyer.getTradingName ()));
    aParty.setPostalAddress (_address (aBuyer.getPostalAddress (),
                                       "BG-8",
                                       new String [] { "BT-50", "BT-51", "BT-163", "BT-52", "BT-53", "BT-54", "BT-55" }));
    if (m_aFilter.use ("BT-48", aBuyer.getVatIdentifier ()))
      _partyTaxScheme (aBuyer.getVatIdentifier (), TAX_SCHEME_VAT, aParty);
    if (m_aFilter.use ("BT-44", aBuyer.getName ()))
      _legalEntity (aParty).setRegistrationName (aBuyer.getName ());
    final PhinvoiceIdentifier aLegal = aBuyer.getLegalRegistration ();
    if (_useIdentifier (aLegal, "BT-47", "BT-47-1"))
      _legalEntity (aParty).setCompanyID (_companyID (aLegal));
    aParty.setContact (_contact (aBuyer.getContact (), "BG-9", "BT-56", "BT-56-0", "BT-57", "BT-58"));
    m_aFilter.dropAdditionalTerms (aBuyer.additionalTerms ());

    final CustomerPartyType ret = new CustomerPartyType ();
    ret.setParty (aParty);
    return ret;
  }

  @Nullable
  private PartyType _payee (@Nullable final PhinvoicePayee aPayee, @Nullable final String sSepaCreditorID)
  {
    if (aPayee == null || !m_aFilter.useGroup ("BG-10", "payee"))
      return null;

    final PartyType ret = new PartyType ();
    final PhinvoiceIdentifier aID = aPayee.getIdentifier ();
    if (aID != null)
      _partyIdentifiers (List.of (aID), "BT-60", ret);
    if (sSepaCreditorID != null)
    {
      final IDType aSepaID = new IDType (sSepaCreditorID);
      aSepaID.setSchemeID (AbstractUblInvoiceReader.SEPA_SCHEME_ID);
      ret.addPartyIdentification (_partyIdentification (aSepaID));
    }
    if (m_aFilter.use ("BT-59", aPayee.getName ()))
      ret.addPartyName (_partyName (aPayee.getName ()));
    final PhinvoiceIdentifier aLegal = aPayee.getLegalRegistration ();
    if (_useIdentifier (aLegal, "BT-61", "BT-61-1"))
      _legalEntity (ret).setCompanyID (_companyID (aLegal));
    return ret;
  }

  @Nullable
  private PartyType _taxRepresentative (@Nullable final PhinvoiceSellerTaxRepresentative aTaxRep)
  {
    if (aTaxRep == null || !m_aFilter.useGroup ("BG-11", "seller tax representative"))
      return null;

    final PartyType ret = new PartyType ();
    if (m_aFilter.use ("BT-62", aTaxRep.getName ()))
      ret.addPartyName (_partyName (aTaxRep.getName ()));
    ret.setPostalAddress (_address (aTaxRep.getPostalAddress (),
                                    "BG-12",
                                    new String [] { "BT-64", "BT-65", "BT-164", "BT-66", "BT-67", "BT-68", "BT-69" }));
    if (m_aFilter.use ("BT-63", aTaxRep.getVatIdentifier ()))
      _partyTaxScheme (aTaxRep.getVatIdentifier (), TAX_SCHEME_VAT, ret);
    return ret;
  }

  @Nullable
  private DeliveryType _delivery (@Nullable final PhinvoiceDeliveryInformation aDI)
  {
    if (aDI == null || aDI.isEmpty () || !m_aFilter.useGroup ("BG-13", "delivery information"))
      return null;

    final DeliveryType ret = new DeliveryType ();
    if (m_aFilter.use ("BT-72", aDI.getActualDeliveryDate ()))
      ret.setActualDeliveryDate (aDI.getActualDeliveryDate ());
    final LocationType aLocation = new LocationType ();
    if (_useIdentifier (aDI.getLocationIdentifier (), "BT-71", "BT-71-1"))
      aLocation.setID (_id (aDI.getLocationIdentifier ()));
    aLocation.setAddress (_address (aDI.getPostalAddress (),
                                    "BG-15",
                                    new String [] { "BT-75", "BT-76", "BT-165", "BT-77", "BT-78", "BT-79", "BT-80" }));
    if (aLocation.getID () != null || aLocation.getAddress () != null)
      ret.setDeliveryLocation (aLocation);
    if (m_aFilter.use ("BT-70", aDI.getPartyName ()))
    {
      final PartyType aParty = new PartyType ();
      aParty.addPartyName (_partyName (aDI.getPartyName ()));
      ret.setDeliveryParty (aParty);
    }
    return ret;
  }

  @Nullable
  private PeriodType _period (@Nullable final PhinvoiceInvoicingPeriod aPeriod,
                              @NonNull final String sGroupID,
                              @NonNull final String sStartTerm,
                              @NonNull final String sEndTerm)
  {
    if (aPeriod == null || aPeriod.isEmpty () || !m_aFilter.useGroup (sGroupID, "period"))
      return null;
    final PeriodType ret = new PeriodType ();
    if (m_aFilter.use (sStartTerm, aPeriod.getStartDate ()))
      ret.setStartDate (aPeriod.getStartDate ());
    if (m_aFilter.use (sEndTerm, aPeriod.getEndDate ()))
      ret.setEndDate (aPeriod.getEndDate ());
    return ret;
  }

  @NonNull
  private TaxCategoryType _taxCategory (@Nullable final String sCategory,
                                        @Nullable final BigDecimal aRate,
                                        @Nullable final String sExemptionReason,
                                        @Nullable final String sExemptionReasonCode,
                                        @NonNull final String [] aTerms)
  {
    // Terms: category, rate, exemption reason, exemption reason code
    final TaxCategoryType ret = new TaxCategoryType ();
    if (m_aFilter.use (aTerms[0], sCategory))
      ret.setID (sCategory);
    if (m_aFilter.use (aTerms[1], aRate))
      ret.setPercent (aRate);
    if (m_aFilter.use (aTerms[3], sExemptionReasonCode))
      ret.setTaxExemptionReasonCode (sExemptionReasonCode);
    if (m_aFilter.use (aTerms[2], sExemptionReason))
      ret.addTaxExemptionReason (new TaxExemptionReasonType (sExemptionReason));
    ret.setTaxScheme (_taxScheme (TAX_SCHEME_VAT));
    return ret;
  }

  @NonNull
  private AllowanceChargeType _allowanceCharge (final boolean bCharge,
                                                @Nullable final String sReasonCode,
                                                @Nullable final String sReason,
                                                @Nullable final BigDecimal aPercentage,
                                                @Nullable final BigDecimal aAmount,
                                                @Nullable final BigDecimal aBaseAmount,
                                                @NonNull final String [] aTerms)
  {
    // Terms: reason code, reason, percentage, amount, base amount
    final AllowanceChargeType ret = new AllowanceChargeType ();
    ret.setChargeIndicator (bCharge);
    if (m_aFilter.use (aTerms[0], sReasonCode))
      ret.setAllowanceChargeReasonCode (sReasonCode);
    if (m_aFilter.use (aTerms[1], sReason))
      ret.addAllowanceChargeReason (new AllowanceChargeReasonType (sReason));
    if (m_aFilter.use (aTerms[2], aPercentage))
      ret.setMultiplierFactorNumeric (aPercentage);
    if (m_aFilter.use (aTerms[3], aAmount))
      _currency (ret.setAmount (aAmount));
    if (m_aFilter.use (aTerms[4], aBaseAmount))
      _currency (ret.setBaseAmount (aBaseAmount));
    return ret;
  }

  @NonNull
  private AllowanceChargeType _documentAllowance (@NonNull final PhinvoiceDocumentAllowance aDA)
  {
    final AllowanceChargeType ret = _allowanceCharge (false,
                                                      aDA.getReasonCode (),
                                                      aDA.getReason (),
                                                      aDA.getPercentage (),
                                                      aDA.getAmount (),
                                                      aDA.getBaseAmount (),
                                                      new String [] { "BT-98", "BT-97", "BT-94", "BT-92", "BT-93" });
    final TaxCategoryType aTC = _taxCategory (aDA.getVatCategoryCode (),
                                              aDA.getVatRate (),
                                              aDA.getExemptionReasonText (),
                                              aDA.getExemptionReasonCode (),
                                              new String [] { "BT-95", "BT-96", "BT-173", "BT-174" });
    if (m_aFilter.use ("BT-213", aDA.getGoodsServicesCode ()))
      aTC.setSupplyTypeCode (aDA.getGoodsServicesCode ());
    ret.addTaxCategory (aTC);
    return ret;
  }

  @NonNull
  private AllowanceChargeType _documentCharge (@NonNull final PhinvoiceDocumentCharge aDC)
  {
    final AllowanceChargeType ret = _allowanceCharge (true,
                                                      aDC.getReasonCode (),
                                                      aDC.getReason (),
                                                      aDC.getPercentage (),
                                                      aDC.getAmount (),
                                                      aDC.getBaseAmount (),
                                                      new String [] { "BT-105", "BT-104", "BT-101", "BT-99", "BT-100" });
    final TaxCategoryType aTC = _taxCategory (aDC.getVatCategoryCode (),
                                              aDC.getVatRate (),
                                              aDC.getExemptionReasonText (),
                                              aDC.getExemptionReasonCode (),
                                              new String [] { "BT-102", "BT-103", "BT-175", "BT-176" });
    if (m_aFilter.use ("BT-214", aDC.getGoodsServicesCode ()))
      aTC.setSupplyTypeCode (aDC.getGoodsServicesCode ());
    ret.addTaxCategory (aTC);
    // BT-105 and BT-177 share cbc:AllowanceChargeReasonCode and are told apart by @listID
    if (m_aFilter.use ("BT-177", aDC.getNonVatTaxCode ()))
    {
      if (ret.getAllowanceChargeReasonCode () != null)
        m_aFilter.drop ("BT-105", aDC.getReasonCode (), "BT-105 and BT-177 share the same element");
      final AllowanceChargeReasonCodeType aCode = ret.setAllowanceChargeReasonCode (aDC.getNonVatTaxCode ());
      aCode.setListID (m_aFilter.use ("BT-177-1", aDC.getNonVatTaxCodeListID ()) ? aDC.getNonVatTaxCodeListID ()
                                                                                 : EN16931CodeLists.NON_VAT_TAX_CODE_LIST_ID);
    }
    return ret;
  }

  @NonNull
  private PaymentMeansType _paymentMeans (@Nullable final String sCode, @Nullable final String sText)
  {
    final PaymentMeansType ret = new PaymentMeansType ();
    if (m_aFilter.use ("BT-81", sCode))
    {
      final PaymentMeansCodeType aCode = ret.setPaymentMeansCode (sCode);
      if (m_aFilter.use ("BT-82", sText))
        aCode.setName (sText);
    }
    else
      if (sText != null)
        m_aFilter.drop ("BT-82", sText, "the payment means type code (BT-81) is missing");
    return ret;
  }

  @NonNull
  private static FinancialAccountType _financialAccount (@Nullable final String sID,
                                                         @Nullable final String sName,
                                                         @Nullable final String sBranchID)
  {
    final FinancialAccountType ret = new FinancialAccountType ();
    if (sID != null)
      ret.setID (sID);
    if (sName != null)
      ret.setName (sName);
    if (sBranchID != null)
    {
      final BranchType aBranch = new BranchType ();
      aBranch.setID (sBranchID);
      ret.setFinancialInstitutionBranch (aBranch);
    }
    return ret;
  }

  /**
   * @return The payment means. The first one carries BT-81 / BT-82 / BT-83, the card, the mandate
   *         and the first credit transfer; each further credit transfer gets its own one.
   */
  @NonNull
  private ICommonsList <PaymentMeansType> _paymentInstructions (@Nullable final PhinvoicePaymentInstructions aPI)
  {
    final ICommonsList <PaymentMeansType> ret = new CommonsArrayList <> ();
    if (aPI == null || !m_aFilter.useGroup ("BG-16", "payment instructions"))
      return ret;

    final PaymentMeansType aFirst = _paymentMeans (aPI.getPaymentMeansCode (), aPI.getPaymentMeansText ());
    ret.add (aFirst);
    if (m_aFilter.use ("BT-83", aPI.getRemittanceInformation ()))
      aFirst.addPaymentID (new PaymentIDType (aPI.getRemittanceInformation ()));

    final PhinvoicePaymentCard aCard = aPI.getPaymentCard ();
    if (aCard != null && m_aFilter.useGroup ("BG-18", "payment card"))
    {
      final CardAccountType aCA = new CardAccountType ();
      if (m_aFilter.use ("BT-87", aCard.getPan ()))
        aCA.setPrimaryAccountNumberID (aCard.getPan ());
      // Mandatory in UBL, but no term of EN 16931
      aCA.setNetworkID (FILLER);
      if (m_aFilter.use ("BT-88", aCard.getHolderName ()))
        aCA.setHolderName (aCard.getHolderName ());
      aFirst.addCardAccount (aCA);
    }

    final PhinvoiceDirectDebit aDD = aPI.getDirectDebit ();
    if (aDD != null && m_aFilter.useGroup ("BG-19", "direct debit"))
    {
      final boolean bMandate = m_aFilter.use ("BT-89", aDD.getMandateReferenceID ());
      final boolean bAccount = m_aFilter.use ("BT-91", aDD.getDebitedAccountID ());
      final boolean bProvider = m_aFilter.use ("BT-215", aDD.getDebitedAccountProviderID ());
      final boolean bName = m_aFilter.use ("BT-216", aDD.getDebitedAccountName ());
      if (bMandate || bAccount || bProvider || bName)
      {
        final PaymentMandateType aPM = new PaymentMandateType ();
        if (bMandate)
          aPM.setID (aDD.getMandateReferenceID ());
        if (bAccount || bProvider || bName)
          aPM.setPayerFinancialAccount (_financialAccount (bAccount ? aDD.getDebitedAccountID () : null,
                                                           bName ? aDD.getDebitedAccountName () : null,
                                                           bProvider ? aDD.getDebitedAccountProviderID () : null));
        aFirst.setPaymentMandate (aPM);
      }
      // BT-90 is written with the parties
    }

    if (m_aFilter.useGroup ("BG-17", aPI.creditTransfers ()))
    {
      boolean bFirst = true;
      for (final PhinvoiceCreditTransfer aCT : aPI.creditTransfers ())
      {
        final PaymentMeansType aPM;
        if (bFirst && EqualsHelper.equals (aCT.getPaymentMeansCode (), aPI.getPaymentMeansCode ()))
          aPM = aFirst;
        else
        {
          aPM = _paymentMeans (aCT.getPaymentMeansCode (), null);
          ret.add (aPM);
        }
        bFirst = false;
        aPM.setPayeeFinancialAccount (_financialAccount (m_aFilter.use ("BT-84", aCT.getAccountIdentifier ()) ? aCT.getAccountIdentifier ()
                                                                                                             : null,
                                                         m_aFilter.use ("BT-85", aCT.getAccountName ()) ? aCT.getAccountName ()
                                                                                                       : null,
                                                         m_aFilter.use ("BT-86", aCT.getServiceProviderID ()) ? aCT.getServiceProviderID ()
                                                                                                             : null));
      }
    }
    m_aFilter.dropAdditionalTerms (aPI.additionalTerms ());
    return ret;
  }

  /**
   * @return The SEPA creditor identifier (BT-90) to write, or <code>null</code>.
   */
  @Nullable
  private String _getSepaCreditorID (@Nullable final PhinvoicePaymentInstructions aPI)
  {
    if (aPI == null || aPI.getDirectDebit () == null || !m_aFilter.isAllowed ("BG-16") || !m_aFilter.isAllowed ("BG-19"))
      return null;
    final String sCreditorID = aPI.getDirectDebit ().getBankAssignedCreditorID ();
    return m_aFilter.use ("BT-90", sCreditorID) ? sCreditorID : null;
  }

  @NonNull
  private ICommonsList <PaymentTermsType> _paymentTerms (@NonNull final PhinvoiceInvoice aInvoice)
  {
    // One UBL payment terms per BG-33; each carries at most one BG-35 and one BG-36
    final ICommonsList <PaymentTermsType> ret = new CommonsArrayList <> ();
    for (final PhinvoicePaymentTerms aPT : aInvoice.paymentTerms ())
    {
      final PaymentTermsType aPTT = new PaymentTermsType ();
      if (m_aFilter.use ("BT-20", aPT.getText ()))
        aPTT.addNote (new NoteType (aPT.getText ()));
      boolean bFirst = true;
      for (final PhinvoiceEarlyPaymentDiscount aED : aPT.earlyDiscounts ())
      {
        if (bFirst && m_aFilter.useGroup ("BG-35", "early payment discount"))
        {
          if (m_aFilter.use ("BT-170", aED.getEndDate ()))
          {
            final PeriodType aPeriod = new PeriodType ();
            aPeriod.setEndDate (aED.getEndDate ());
            aPTT.setSettlementPeriod (aPeriod);
          }
          if (m_aFilter.use ("BT-171", aED.getPercentage ()))
            aPTT.setSettlementDiscountPercent (aED.getPercentage ());
          if (m_aFilter.use ("BT-172", aED.getAmount ()))
            _currency (aPTT.setSettlementDiscountAmount (aED.getAmount ()));
        }
        else
          if (!bFirst)
            m_aFilter.drop ("BG-35", aED.getEndDate (), "UBL has only one early payment discount per payment terms");
        bFirst = false;
      }
      bFirst = true;
      for (final PhinvoiceLatePaymentPenalty aLP : aPT.latePenalties ())
      {
        if (bFirst && m_aFilter.useGroup ("BG-36", "late payment penalty"))
        {
          if (m_aFilter.use ("BT-181", aLP.getStartDate ()))
          {
            final PeriodType aPeriod = new PeriodType ();
            aPeriod.setStartDate (aLP.getStartDate ());
            aPTT.setPenaltyPeriod (aPeriod);
          }
          if (m_aFilter.use ("BT-182", aLP.getYearlyInterestPercentage ()))
          {
            final InterestRateType aIR = new InterestRateType ();
            aIR.setInterestRatePercent (aLP.getYearlyInterestPercentage ());
            aPTT.setPenaltyInterestRate (aIR);
          }
          if (m_aFilter.use ("BT-183", aLP.getAmount ()))
            _currency (aPTT.setPenaltyAmount (aLP.getAmount ()));
        }
        else
          if (!bFirst)
            m_aFilter.drop ("BG-36", aLP.getStartDate (), "UBL has only one late payment penalty per payment terms");
        bFirst = false;
      }
      ret.add (aPTT);
    }
    return ret;
  }

  private void _taxTotals (@NonNull final PhinvoiceInvoice aInvoice, @NonNull final List <TaxTotalType> aTarget)
  {
    final PhinvoiceDocumentTotals aT = aInvoice.getDocumentTotals ();
    final boolean bTaxTotal = m_aFilter.use ("BT-110", aT.getTaxTotal ());
    final boolean bBreakdowns = m_aFilter.useGroup ("BG-23", aInvoice.vatBreakdowns ());
    if (bTaxTotal || bBreakdowns)
    {
      final TaxTotalType aTT = new TaxTotalType ();
      // The tax amount is mandatory in UBL: without BT-110 it is 0, like in en16931-cii2ubl
      _currency (aTT.setTaxAmount (bTaxTotal ? aT.getTaxTotal () : BigDecimal.ZERO));
      if (bBreakdowns)
        for (final PhinvoiceVatBreakdown aBD : aInvoice.vatBreakdowns ())
        {
          final TaxSubtotalType aSub = new TaxSubtotalType ();
          if (m_aFilter.use ("BT-116", aBD.getTaxableAmount ()))
            _currency (aSub.setTaxableAmount (aBD.getTaxableAmount ()));
          if (m_aFilter.use ("BT-117", aBD.getTaxAmount ()))
          {
            final TaxAmountType aTaxAmount = _currency (aSub.setTaxAmount (aBD.getTaxAmount ()));
            // BT-184 is new in EN 16931:2026
            if (m_aFilter.use ("BT-184", aBD.getCurrency ()))
              aTaxAmount.setCurrencyID (aBD.getCurrency ());
          }
          if (m_aFilter.use ("BT-116-1", aBD.getTaxableAmountCurrency ()) && aSub.getTaxableAmount () != null)
            aSub.getTaxableAmount ().setCurrencyID (aBD.getTaxableAmountCurrency ());
          final TaxCategoryType aTC = _taxCategory (aBD.getCategoryCode (),
                                                    aBD.getRate (),
                                                    aBD.getExemptionReason (),
                                                    aBD.getExemptionReasonCode (),
                                                    new String [] { "BT-118", "BT-119", "BT-120", "BT-121" });
          if (m_aFilter.use ("BT-210", aBD.getGoodsServicesCode ()))
            aTC.setSupplyTypeCode (aBD.getGoodsServicesCode ());
          aSub.setTaxCategory (aTC);
          aTT.addTaxSubtotal (aSub);
        }
      aTarget.add (aTT);
    }
    if (m_aFilter.use ("BT-111", aT.getTaxTotalAccountingCurrency ()))
    {
      final TaxTotalType aTT = new TaxTotalType ();
      final TaxAmountType aTaxAmount = aTT.setTaxAmount (aT.getTaxTotalAccountingCurrency ());
      aTaxAmount.setCurrencyID (aInvoice.getVatAccountingCurrency ());
      aTarget.add (aTT);
    }
  }

  @NonNull
  private MonetaryTotalType _totals (@NonNull final PhinvoiceDocumentTotals aT)
  {
    final MonetaryTotalType ret = new MonetaryTotalType ();
    if (m_aFilter.use ("BT-106", aT.getLineNetTotal ()))
      _currency (ret.setLineExtensionAmount (aT.getLineNetTotal ()));
    if (m_aFilter.use ("BT-109", aT.getTaxExclusiveTotal ()))
      _currency (ret.setTaxExclusiveAmount (aT.getTaxExclusiveTotal ()));
    if (m_aFilter.use ("BT-112", aT.getTaxInclusiveTotal ()))
      _currency (ret.setTaxInclusiveAmount (aT.getTaxInclusiveTotal ()));
    if (m_aFilter.use ("BT-107", aT.getAllowanceTotal ()))
      _currency (ret.setAllowanceTotalAmount (aT.getAllowanceTotal ()));
    if (m_aFilter.use ("BT-108", aT.getChargeTotal ()))
      _currency (ret.setChargeTotalAmount (aT.getChargeTotal ()));
    if (m_aFilter.use ("BT-113", aT.getPrepaidAmount ()))
      _currency (ret.setPrepaidAmount (aT.getPrepaidAmount ()));
    if (m_aFilter.use ("BT-114", aT.getRoundingAmount ()))
      _currency (ret.setPayableRoundingAmount (aT.getRoundingAmount ()));
    if (m_aFilter.use ("BT-115", aT.getPayableAmount ()))
      _currency (ret.setPayableAmount (aT.getPayableAmount ()));
    return ret;
  }

  @Nullable
  private ItemType _item (@Nullable final PhinvoiceItem aItem, @Nullable final PhinvoiceLineVat aVat)
  {
    final ItemType ret = new ItemType ();
    if (aItem != null && m_aFilter.useGroup ("BG-31", "item information"))
    {
      if (m_aFilter.use ("BT-154", aItem.getDescription ()))
        ret.addDescription (new DescriptionType (aItem.getDescription ()));
      if (m_aFilter.use ("BT-153", aItem.getName ()))
        ret.addName (new NameType (aItem.getName ()));
      if (m_aFilter.use ("BT-156", aItem.getBuyerItemID ()))
      {
        final ItemIdentificationType aII = new ItemIdentificationType ();
        aII.setID (aItem.getBuyerItemID ());
        ret.setBuyersItemIdentification (aII);
      }
      if (m_aFilter.use ("BT-155", aItem.getSellerItemID ()))
      {
        final ItemIdentificationType aII = new ItemIdentificationType ();
        aII.setID (aItem.getSellerItemID ());
        ret.setSellersItemIdentification (aII);
      }
      if (_useIdentifier (aItem.getStandardItemID (), "BT-157", "BT-157-1"))
      {
        final ItemIdentificationType aII = new ItemIdentificationType ();
        aII.setID (_id (aItem.getStandardItemID ()));
        ret.setStandardItemIdentification (aII);
      }
      if (m_aFilter.use ("BT-159", aItem.getCountryOfOrigin ()))
      {
        final CountryType aCountry = new CountryType ();
        aCountry.setIdentificationCode (aItem.getCountryOfOrigin ());
        ret.setOriginCountry (aCountry);
      }
      for (final PhinvoiceItemClassification aIC : aItem.classifications ())
        if (m_aFilter.use ("BT-158", aIC.getCode ()))
        {
          final ItemClassificationCodeType aCode = new ItemClassificationCodeType (aIC.getCode ());
          if (m_aFilter.use ("BT-158-1", aIC.getListID ()))
            aCode.setListID (aIC.getListID ());
          if (m_aFilter.use ("BT-158-2", aIC.getListVersionID ()))
            aCode.setListVersionID (aIC.getListVersionID ());
          final CommodityClassificationType aCC = new CommodityClassificationType ();
          aCC.setItemClassificationCode (aCode);
          ret.addCommodityClassification (aCC);
        }
      // BT-196 is written with the line VAT information
    }
    if (aVat != null && m_aFilter.useGroup ("BG-30", "line VAT information"))
    {
      final TaxCategoryType aTC = new TaxCategoryType ();
      if (m_aFilter.use ("BT-151", aVat.getCategoryCode ()))
        aTC.setID (aVat.getCategoryCode ());
      if (m_aFilter.use ("BT-152", aVat.getRate ()))
        aTC.setPercent (aVat.getRate ());
      if (m_aFilter.use ("BT-195", aVat.getExemptionReasonCode ()))
        aTC.setTaxExemptionReasonCode (aVat.getExemptionReasonCode ());
      if (m_aFilter.use ("BT-194", aVat.getExemptionReasonText ()))
        aTC.addTaxExemptionReason (new TaxExemptionReasonType (aVat.getExemptionReasonText ()));
      // BT-196 belongs to the item (BG-31), UBL carries it in the line VAT information
      if (aItem != null && m_aFilter.use ("BT-196", aItem.getGoodsServicesCode ()))
        aTC.setSupplyTypeCode (aItem.getGoodsServicesCode ());
      aTC.setTaxScheme (_taxScheme (TAX_SCHEME_VAT));
      ret.addClassifiedTaxCategory (aTC);
    }
    if (aItem != null && m_aFilter.useGroup ("BG-32", aItem.attributes ()))
      for (final PhinvoiceItemAttribute aAttr : aItem.attributes ())
      {
        final ItemPropertyType aIP = new ItemPropertyType ();
        if (m_aFilter.use ("BT-160", aAttr.getName ()))
          aIP.setName (aAttr.getName ());
        if (m_aFilter.use ("BT-161", aAttr.getValue ()))
          aIP.setValue (aAttr.getValue ());
        if (m_aFilter.use ("BT-211", aAttr.getCode ()))
          aIP.setNameCode (aAttr.getCode ());
        // The numeric value is BT-161b with BT-212 as its unit
        if (m_aFilter.use ("BT-161", aAttr.getNumericValue ()))
        {
          final ValueQuantityType aVQ = aIP.setValueQuantity (aAttr.getNumericValue ());
          if (m_aFilter.use ("BT-212", aAttr.getValueUnit ()))
            aVQ.setUnitCode (aAttr.getValueUnit ());
        }
        else
          if (aAttr.getValueUnit () != null)
            m_aFilter.drop ("BT-212", aAttr.getValueUnit (), "the numeric item attribute value is missing");
        if (aIP.getName () != null || aIP.getValue () != null || aIP.getNameCode () != null || aIP.getValueQuantity () != null)
          ret.addAdditionalItemProperty (aIP);
      }
    return ret;
  }

  @Nullable
  private PriceType _price (@Nullable final PhinvoicePriceDetails aPrice)
  {
    if (aPrice == null || !m_aFilter.useGroup ("BG-29", "price details"))
      return null;

    final PriceType ret = new PriceType ();
    if (m_aFilter.use ("BT-146", aPrice.getNetPrice ()))
      _currency (ret.setPriceAmount (aPrice.getNetPrice ()));
    if (m_aFilter.use ("BT-149", aPrice.getBaseQuantity ()))
    {
      final BaseQuantityType aQ = ret.setBaseQuantity (aPrice.getBaseQuantity ());
      if (m_aFilter.use ("BT-150", aPrice.getBaseQuantityUnit ()))
        aQ.setUnitCode (aPrice.getBaseQuantityUnit ());
    }
    final boolean bDiscount = m_aFilter.use ("BT-147", aPrice.getDiscount ());
    final boolean bGross = m_aFilter.use ("BT-148", aPrice.getGrossPrice ());
    if (bDiscount || bGross)
    {
      final AllowanceChargeType aAC = new AllowanceChargeType ();
      aAC.setChargeIndicator (false);
      // The amount is mandatory
      _currency (aAC.setAmount (bDiscount ? aPrice.getDiscount () : BigDecimal.ZERO));
      if (bGross)
        _currency (aAC.setBaseAmount (aPrice.getGrossPrice ()));
      ret.addAllowanceCharge (aAC);
    }
    return ret;
  }

  @Nullable
  private LineReferenceType _lineReference (@Nullable final String sDocumentID,
                                            @Nullable final String sLineID,
                                            @NonNull final String sDocumentTerm,
                                            @NonNull final String sLineTerm)
  {
    final boolean bDocument = m_aFilter.use (sDocumentTerm, sDocumentID);
    final boolean bLine = m_aFilter.use (sLineTerm, sLineID);
    if (!bDocument && !bLine)
      return null;
    final LineReferenceType ret = new LineReferenceType ();
    // The line ID is mandatory
    ret.setLineID (bLine ? sLineID : FILLER);
    if (bDocument)
      ret.setDocumentReference (_documentReference (sDocumentID));
    return ret;
  }

  @Nullable
  private DeliveryType _lineDelivery (@NonNull final PhinvoiceInvoiceLine aLine)
  {
    final DeliveryType ret = new DeliveryType ();
    final PhinvoiceLineDelivery aLD = aLine.getDelivery ();
    if (aLD != null && !aLD.isEmpty ())
    {
      // BG-37 / BG-38
      if (m_aFilter.use ("BT-187", aLD.getActualDeliveryDate ()))
        ret.setActualDeliveryDate (aLD.getActualDeliveryDate ());
      final LocationType aLocation = new LocationType ();
      if (_useIdentifier (aLD.getLocationIdentifier (), "BT-186", "BT-186-1"))
        aLocation.setID (_id (aLD.getLocationIdentifier ()));
      aLocation.setAddress (_address (aLD.getPostalAddress (),
                                      "BG-38",
                                      new String [] { "BT-203", "BT-204", "BT-205", "BT-206", "BT-207", "BT-208", "BT-209" }));
      if (aLocation.getID () != null || aLocation.getAddress () != null)
        ret.setDeliveryLocation (aLocation);
      if (m_aFilter.use ("BT-185", aLD.getPartyName ()))
      {
        final PartyType aParty = new PartyType ();
        aParty.addPartyName (_partyName (aLD.getPartyName ()));
        ret.setDeliveryParty (aParty);
      }
    }
    // BT-198 / BT-199
    if (m_aFilter.use ("BT-198", aLine.getDeliveryNoteReference ()))
      ret.addDeliveryNoteDocumentReference (_documentReference (aLine.getDeliveryNoteReference ()));
    if (m_aFilter.use ("BT-199", aLine.getDeliveryNoteLineReference ()))
    {
      final LineReferenceType aLR = new LineReferenceType ();
      aLR.setLineID (aLine.getDeliveryNoteLineReference ());
      ret.addDeliveryNoteLineReference (aLR);
    }
    if (ret.getActualDeliveryDate () == null &&
        ret.getDeliveryLocation () == null &&
        ret.getDeliveryParty () == null &&
        ret.getDeliveryNoteDocumentReference ().isEmpty () &&
        ret.getDeliveryNoteLineReference ().isEmpty ())
      return null;
    return ret;
  }

  /**
   * Fill the parts that the invoice line and the credit note line have in common.
   */
  private void _lineCommon (@NonNull final PhinvoiceInvoiceLine aLine,
                            @NonNull final List <NoteType> aNotes,
                            @NonNull final List <PeriodType> aPeriods,
                            @NonNull final List <OrderLineReferenceType> aOrderLineRefs,
                            @NonNull final List <LineReferenceType> aDespatchLineRefs,
                            @NonNull final List <LineReferenceType> aReceiptLineRefs,
                            @NonNull final List <BillingReferenceType> aBillingRefs,
                            @NonNull final List <DeliveryType> aDeliveries,
                            @NonNull final List <DocumentReferenceType> aDocRefs,
                            @NonNull final List <AllowanceChargeType> aACs)
  {
    if (m_aFilter.use ("BT-127", aLine.getNote ()))
      aNotes.add (new NoteType (aLine.getNote ()));
    final PeriodType aPeriod = _period (aLine.getPeriod (), "BG-26", "BT-134", "BT-135");
    if (aPeriod != null)
      aPeriods.add (aPeriod);
    if (m_aFilter.use ("BT-132", aLine.getPurchaseOrderLineRef ()))
    {
      final OrderLineReferenceType aOLR = new OrderLineReferenceType ();
      aOLR.setLineID (aLine.getPurchaseOrderLineRef ());
      aOrderLineRefs.add (aOLR);
    }
    final boolean bPurchaseOrder = m_aFilter.use ("BT-188", aLine.getPurchaseOrderReference ());
    final boolean bSalesOrder = m_aFilter.use ("BT-200", aLine.getSalesOrderReference ());
    final boolean bSalesOrderLine = m_aFilter.use ("BT-201", aLine.getSalesOrderLineReference ());
    if (bPurchaseOrder || bSalesOrder || bSalesOrderLine)
    {
      final OrderLineReferenceType aOLR = aOrderLineRefs.isEmpty () ? new OrderLineReferenceType () : aOrderLineRefs.get (0);
      if (aOrderLineRefs.isEmpty ())
      {
        // The line ID is mandatory
        aOLR.setLineID (FILLER);
        aOrderLineRefs.add (aOLR);
      }
      if (bSalesOrderLine)
        aOLR.setSalesOrderLineID (aLine.getSalesOrderLineReference ());
      if (bPurchaseOrder || bSalesOrder)
      {
        final OrderReferenceType aOR = new OrderReferenceType ();
        // The ID is mandatory
        aOR.setID (bPurchaseOrder ? aLine.getPurchaseOrderReference () : FILLER);
        if (bSalesOrder)
          aOR.setSalesOrderID (aLine.getSalesOrderReference ());
        aOLR.setOrderReference (aOR);
      }
    }
    final LineReferenceType aDespatch = _lineReference (aLine.getDespatchAdviceReference (),
                                                        aLine.getDespatchAdviceLineReference (),
                                                        "BT-189",
                                                        "BT-190");
    if (aDespatch != null)
      aDespatchLineRefs.add (aDespatch);
    final LineReferenceType aReceipt = _lineReference (aLine.getReceivingAdviceReference (),
                                                       aLine.getReceivingAdviceLineReference (),
                                                       "BT-191",
                                                       "BT-192");
    if (aReceipt != null)
      aReceiptLineRefs.add (aReceipt);
    if (m_aFilter.useGroup ("BG-39", aLine.precedingInvoiceRefs ()))
      for (final PhinvoiceLinePrecedingInvoiceRef aRef : aLine.precedingInvoiceRefs ())
        if (!m_aFilter.use ("BT-217", aRef.getNumber ()))
        {
          if (aRef.getNumber () == null)
            m_aFilter.drop ("BG-39", aRef.getLineReference (), "the mandatory line-level preceding invoice reference (BT-217) is missing");
        }
        else
        {
          final DocumentReferenceType aDR = _documentReference (aRef.getNumber ());
          if (m_aFilter.use ("BT-218", aRef.getIssueDate ()))
            aDR.setIssueDate (aRef.getIssueDate ());
          if (m_aFilter.use ("BT-219", aRef.getTypeCode ()))
            aDR.setDocumentTypeCode (aRef.getTypeCode ());
          final BillingReferenceType aBR = new BillingReferenceType ();
          aBR.setInvoiceDocumentReference (aDR);
          if (m_aFilter.use ("BT-220", aRef.getLineReference ()))
          {
            final BillingReferenceLineType aBRL = new BillingReferenceLineType ();
            aBRL.setID (aRef.getLineReference ());
            aBR.addBillingReferenceLine (aBRL);
          }
          aBillingRefs.add (aBR);
        }
    final DeliveryType aDelivery = _lineDelivery (aLine);
    if (aDelivery != null)
      aDeliveries.add (aDelivery);
    if (_useIdentifier (aLine.getObjectIdentifier (), "BT-128", "BT-128-1"))
    {
      final DocumentReferenceType aDR = new DocumentReferenceType ();
      aDR.setID (_id (aLine.getObjectIdentifier ()));
      aDR.setDocumentTypeCode (EN16931CodeLists.DOCUMENT_TYPE_CODE_OBJECT_DOCUMENT);
      aDocRefs.add (aDR);
    }
    if (m_aFilter.useGroup ("BG-27", aLine.allowances ()))
      for (final PhinvoiceLineAllowance aLA : aLine.allowances ())
        aACs.add (_allowanceCharge (false,
                                    aLA.getReasonCode (),
                                    aLA.getReason (),
                                    aLA.getPercentage (),
                                    aLA.getAmount (),
                                    aLA.getBaseAmount (),
                                    new String [] { "BT-140", "BT-139", "BT-138", "BT-136", "BT-137" }));
    if (m_aFilter.useGroup ("BG-28", aLine.charges ()))
      for (final PhinvoiceLineCharge aLC : aLine.charges ())
      {
        aACs.add (_allowanceCharge (true,
                                    aLC.getReasonCode (),
                                    aLC.getReason (),
                                    aLC.getPercentage (),
                                    aLC.getAmount (),
                                    aLC.getBaseAmount (),
                                    new String [] { "BT-145", "BT-144", "BT-143", "BT-141", "BT-142" }));
        // BT-145 and BT-193 share cbc:AllowanceChargeReasonCode and are told apart by @listID
        if (m_aFilter.use ("BT-193", aLC.getNonVatTaxTypeCode ()))
        {
          final AllowanceChargeType aAC = aACs.get (aACs.size () - 1);
          if (aAC.getAllowanceChargeReasonCode () != null)
            m_aFilter.drop ("BT-145", aLC.getReasonCode (), "BT-145 and BT-193 share the same element");
          final AllowanceChargeReasonCodeType aCode = aAC.setAllowanceChargeReasonCode (aLC.getNonVatTaxTypeCode ());
          aCode.setListID (EN16931CodeLists.NON_VAT_TAX_CODE_LIST_ID);
        }
      }
    m_aFilter.dropAdditionalTerms (aLine.additionalTerms ());
  }

  @NonNull
  private InvoiceLineType _invoiceLine (@NonNull final PhinvoiceInvoiceLine aLine)
  {
    final InvoiceLineType ret = new InvoiceLineType ();
    if (m_aFilter.use ("BT-126", aLine.getID ()))
      ret.setID (aLine.getID ());
    if (m_aFilter.use ("BT-129", aLine.getQuantity ()))
    {
      final InvoicedQuantityType aQ = ret.setInvoicedQuantity (aLine.getQuantity ());
      if (m_aFilter.use ("BT-130", aLine.getQuantityUnit ()))
        aQ.setUnitCode (aLine.getQuantityUnit ());
    }
    if (m_aFilter.use ("BT-131", aLine.getNetAmount ()))
      _currency (ret.setLineExtensionAmount (aLine.getNetAmount ()));
    if (m_aFilter.use ("BT-133", aLine.getAccountingCost ()))
      ret.setAccountingCost (aLine.getAccountingCost ());
    _lineCommon (aLine,
                 ret.getNote (),
                 ret.getInvoicePeriod (),
                 ret.getOrderLineReference (),
                 ret.getDespatchLineReference (),
                 ret.getReceiptLineReference (),
                 ret.getBillingReference (),
                 ret.getDelivery (),
                 ret.getDocumentReference (),
                 ret.getAllowanceCharge ());
    ret.setItem (_item (aLine.getItem (), aLine.getLineVat ()));
    ret.setPrice (_price (aLine.getPrice ()));
    return ret;
  }

  @NonNull
  private CreditNoteLineType _creditNoteLine (@NonNull final PhinvoiceInvoiceLine aLine)
  {
    final CreditNoteLineType ret = new CreditNoteLineType ();
    if (m_aFilter.use ("BT-126", aLine.getID ()))
      ret.setID (aLine.getID ());
    if (m_aFilter.use ("BT-129", aLine.getQuantity ()))
    {
      final CreditedQuantityType aQ = ret.setCreditedQuantity (aLine.getQuantity ());
      if (m_aFilter.use ("BT-130", aLine.getQuantityUnit ()))
        aQ.setUnitCode (aLine.getQuantityUnit ());
    }
    if (m_aFilter.use ("BT-131", aLine.getNetAmount ()))
      _currency (ret.setLineExtensionAmount (aLine.getNetAmount ()));
    if (m_aFilter.use ("BT-133", aLine.getAccountingCost ()))
      ret.setAccountingCost (aLine.getAccountingCost ());
    _lineCommon (aLine,
                 ret.getNote (),
                 ret.getInvoicePeriod (),
                 ret.getOrderLineReference (),
                 ret.getDespatchLineReference (),
                 ret.getReceiptLineReference (),
                 ret.getBillingReference (),
                 ret.getDelivery (),
                 ret.getDocumentReference (),
                 ret.getAllowanceCharge ());
    ret.setItem (_item (aLine.getItem (), aLine.getLineVat ()));
    ret.setPrice (_price (aLine.getPrice ()));
    return ret;
  }

  @NonNull
  private ICommonsList <AnnotationType> _annotations (@NonNull final PhinvoiceInvoice aInvoice)
  {
    final ICommonsList <AnnotationType> ret = new CommonsArrayList <> ();
    if (m_aFilter.useGroup ("BG-1", aInvoice.invoiceNotes ()))
      for (final PhinvoiceInvoiceNote aNote : aInvoice.invoiceNotes ())
      {
        final AnnotationType aAnnotation = new AnnotationType ();
        if (m_aFilter.use ("BT-21", aNote.getSubjectCode ()))
          aAnnotation.setSubjectCode (aNote.getSubjectCode ());
        if (m_aFilter.use ("BT-22", aNote.getContent ()))
          aAnnotation.addAnnotationContent (new AnnotationContentType (aNote.getContent ()));
        if (aAnnotation.getSubjectCode () != null || !aAnnotation.getAnnotationContent ().isEmpty ())
          ret.add (aAnnotation);
      }
    return ret;
  }

  @Nullable
  private OrderReferenceType _orderReference (@NonNull final PhinvoiceInvoice aInvoice)
  {
    final boolean bPurchaseOrder = m_aFilter.use ("BT-13", aInvoice.getPurchaseOrderReference ());
    final boolean bSalesOrder = m_aFilter.use ("BT-14", aInvoice.getSalesOrderReference ());
    if (!bPurchaseOrder && !bSalesOrder)
      return null;
    final OrderReferenceType ret = new OrderReferenceType ();
    // The ID is mandatory
    ret.setID (bPurchaseOrder ? aInvoice.getPurchaseOrderReference () : FILLER);
    if (bSalesOrder)
      ret.setSalesOrderID (aInvoice.getSalesOrderReference ());
    return ret;
  }

  @NonNull
  private ICommonsList <BillingReferenceType> _billingReferences (@NonNull final PhinvoiceInvoice aInvoice)
  {
    final ICommonsList <BillingReferenceType> ret = new CommonsArrayList <> ();
    if (m_aFilter.useGroup ("BG-3", aInvoice.precedingInvoiceRefs ()))
      for (final PhinvoicePrecedingInvoiceRef aRef : aInvoice.precedingInvoiceRefs ())
        if (!m_aFilter.use ("BT-25", aRef.getNumber ()))
        {
          if (aRef.getNumber () == null)
            m_aFilter.drop ("BG-3", aRef.getIssueDate (), "the mandatory preceding invoice reference (BT-25) is missing");
        }
        else
        {
          final DocumentReferenceType aDR = _documentReference (aRef.getNumber ());
          if (m_aFilter.use ("BT-26", aRef.getIssueDate ()))
            aDR.setIssueDate (aRef.getIssueDate ());
          if (m_aFilter.use ("BT-202", aRef.getTypeCode ()))
            aDR.setDocumentTypeCode (aRef.getTypeCode ());
          final BillingReferenceType aBR = new BillingReferenceType ();
          aBR.setInvoiceDocumentReference (aDR);
          ret.add (aBR);
        }
    return ret;
  }

  @NonNull
  private DocumentReferenceType _supportingDocument (@NonNull final PhinvoiceSupportingDocument aDoc)
  {
    final DocumentReferenceType ret = _documentReference (aDoc.getReference ());
    // BT-122-1 - 916 if there is none; BT-122-1-1 has only one allowed value
    ret.setDocumentTypeCode (m_aFilter.use ("BT-122-1", aDoc.getDocumentTypeCode ()) ? aDoc.getDocumentTypeCode ()
                                                                                     : EN16931CodeLists.DOCUMENT_TYPE_CODE_SUPPORTING_DOCUMENT)
       .setListID (DOCUMENT_TYPE_CODE_LIST_ID);
    if (m_aFilter.use ("BT-123", aDoc.getDescription ()))
      ret.addDocumentDescription (new DocumentDescriptionType (aDoc.getDescription ()));
    final AttachmentType aAttachment = new AttachmentType ();
    final PhinvoiceAttachment aBin = aDoc.getAttachment ();
    if (aBin != null && m_aFilter.use ("BT-125", aBin.getBinary ()))
    {
      final EmbeddedDocumentBinaryObjectType aEDBO = new EmbeddedDocumentBinaryObjectType (aBin.getBinary ());
      // Both are mandatory in UBL
      if (m_aFilter.use ("BT-125-1", aBin.getMimeCode ()))
        aEDBO.setMimeCode (aBin.getMimeCode ());
      if (m_aFilter.use ("BT-125-2", aBin.getFilename ()))
        aEDBO.setFilename (aBin.getFilename ());
      aAttachment.setEmbeddedDocumentBinaryObject (aEDBO);
    }
    if (m_aFilter.use ("BT-124", aDoc.getExternalLocation ()))
    {
      final ExternalReferenceType aER = new ExternalReferenceType ();
      aER.setURI (aDoc.getExternalLocation ());
      aAttachment.setExternalReference (aER);
    }
    if (aAttachment.getEmbeddedDocumentBinaryObject () != null || aAttachment.getExternalReference () != null)
      ret.setAttachment (aAttachment);
    return ret;
  }

  /**
   * @return The additional document references: BT-18 and BG-24.
   */
  @NonNull
  private ICommonsList <DocumentReferenceType> _additionalDocumentReferences (@NonNull final PhinvoiceInvoice aInvoice)
  {
    final ICommonsList <DocumentReferenceType> ret = new CommonsArrayList <> ();
    final PhinvoiceIdentifier aObjectID = aInvoice.getInvoicedObjectIdentifier ();
    if (_useIdentifier (aObjectID, "BT-18", "BT-18-1"))
    {
      final DocumentReferenceType aDR = new DocumentReferenceType ();
      final IDType aID = new IDType (aObjectID.getValue ());
      if (m_aFilter.use ("BT-18-1", aObjectID.getSchemeID ()))
        aID.setSchemeID (aObjectID.getSchemeID ());
      aDR.setID (aID);
      aDR.setDocumentTypeCode (EN16931CodeLists.DOCUMENT_TYPE_CODE_OBJECT_DOCUMENT);
      ret.add (aDR);
    }
    if (m_aFilter.useGroup ("BG-24", aInvoice.supportingDocuments ()))
      for (final PhinvoiceSupportingDocument aDoc : aInvoice.supportingDocuments ())
        if (m_aFilter.use ("BT-122", aDoc.getReference ()))
          ret.add (_supportingDocument (aDoc));
        else
          if (aDoc.getReference () == null && !aDoc.isEmpty ())
            m_aFilter.drop ("BG-24", aDoc.getDescription (), "the mandatory supporting document reference (BT-122) is missing");
    return ret;
  }

  /**
   * @return The exchange rate (BT-167) or <code>null</code>.
   */
  @Nullable
  private ExchangeRateType _exchangeRate (@NonNull final PhinvoiceInvoice aInvoice)
  {
    if (!m_aFilter.use ("BT-167", aInvoice.getVatExchangeRate ()))
    {
      if (aInvoice.getVatExchangeRateTargetCurrency () != null)
        m_aFilter.drop ("BT-167-1", aInvoice.getVatExchangeRateTargetCurrency (), "the exchange rate (BT-167) is missing");
      if (aInvoice.getVatExchangeRateSourceCurrency () != null)
        m_aFilter.drop ("BT-167-2", aInvoice.getVatExchangeRateSourceCurrency (), "the exchange rate (BT-167) is missing");
      return null;
    }
    final ExchangeRateType ret = new ExchangeRateType ();
    // Both currencies are mandatory in UBL
    if (m_aFilter.use ("BT-167-2", aInvoice.getVatExchangeRateSourceCurrency ()))
      ret.setSourceCurrencyCode (aInvoice.getVatExchangeRateSourceCurrency ());
    if (m_aFilter.use ("BT-167-1", aInvoice.getVatExchangeRateTargetCurrency ()))
      ret.setTargetCurrencyCode (aInvoice.getVatExchangeRateTargetCurrency ());
    ret.setCalculationRate (aInvoice.getVatExchangeRate ());
    return ret;
  }

  private void _reportHeaderUnsupported (@NonNull final PhinvoiceInvoice aInvoice)
  {
    // CII only
    _reportUnsupported ("BT-11-1", aInvoice.getProjectName ());
    if (aInvoice.getSeller ().getTaxRegistration () == null && aInvoice.getSeller ().getTaxRegistrationSchemeID () != null)
      m_aFilter.drop ("BT-32-1", aInvoice.getSeller ().getTaxRegistrationSchemeID (), "the tax registration (BT-32) is missing");
  }

  /**
   * Fill a collection line (BG-34): BT-179-1, BT-179 and BT-180.
   */
  private void _collectionLine (@NonNull final PhinvoiceThirdPartyCharge aTPC,
                                final int nIndex,
                                @NonNull final Consumer <String> aIDSetter,
                                @NonNull final Consumer <BigDecimal> aAmountSetter,
                                @NonNull final Consumer <ItemType> aItemSetter)
  {
    // The line ID is mandatory in UBL; CII has no BT-179-1, so the position is used then
    aIDSetter.accept (m_aFilter.use ("BT-179-1", aTPC.getLineID ()) ? aTPC.getLineID () : Integer.toString (nIndex + 1));
    if (m_aFilter.use ("BT-179", aTPC.getAmount ()))
      aAmountSetter.accept (aTPC.getAmount ());
    final ItemType aItem = new ItemType ();
    if (m_aFilter.use ("BT-180", aTPC.getSpecification ()))
      aItem.addDescription (new DescriptionType (aTPC.getSpecification ()));
    aItemSetter.accept (aItem);
  }

  @NonNull
  private ICommonsList <BuyerAssignedReferenceType> _buyerReferences (@NonNull final PhinvoiceInvoice aInvoice)
  {
    // BT-10 with BT-10-1 is repeatable since EN 16931:2026
    final ICommonsList <BuyerAssignedReferenceType> ret = new CommonsArrayList <> ();
    for (final PhinvoiceIdentifier aBuyerRef : aInvoice.buyerReferences ())
      if (_useIdentifier (aBuyerRef, "BT-10", "BT-10-1"))
      {
        final BuyerAssignedReferenceType aBAR = new BuyerAssignedReferenceType ();
        if (m_aFilter.use ("BT-10-1", aBuyerRef.getSchemeID ()))
          aBAR.setBuyerReferenceCode (aBuyerRef.getSchemeID ());
        aBAR.addBuyerReference (new BuyerReferenceType (aBuyerRef.getValue ()));
        ret.add (aBAR);
      }
    return ret;
  }

  @Nullable
  private PeriodType _invoicePeriod (@NonNull final PhinvoiceInvoice aInvoice)
  {
    PeriodType ret = _period (aInvoice.getInvoicingPeriod (), "BG-14", "BT-73", "BT-74");
    // BT-8 lives in the invoice period
    if (m_aFilter.use ("BT-8", aInvoice.getVatPointDateCode ()))
    {
      if (ret == null)
        ret = new PeriodType ();
      ret.addDescriptionCode (new DescriptionCodeType (aInvoice.getVatPointDateCode ()));
    }
    return ret;
  }

  /**
   * Create the UBL 2.5 Invoice.
   *
   * @param aInvoice
   *        The invoice to write. May not be <code>null</code>.
   * @param sSpecificationIdentifier
   *        The BT-24 value to write. May not be <code>null</code>.
   * @return The JAXB object. Never <code>null</code>.
   */
  @NonNull
  InvoiceType createInvoice (@NonNull final PhinvoiceInvoice aInvoice, @NonNull final String sSpecificationIdentifier)
  {
    m_sCurrency = aInvoice.getCurrency ();
    final InvoiceType ret = new InvoiceType ();
    ret.setCustomizationID (sSpecificationIdentifier);
    if (m_aFilter.use ("BT-23", aInvoice.getProcessControl ().getBusinessProcessType ()))
      ret.setProfileID (aInvoice.getProcessControl ().getBusinessProcessType ());
    if (m_aFilter.use ("BT-1", aInvoice.getNumber ()))
      ret.setID (aInvoice.getNumber ());
    if (m_aFilter.use ("BT-2", aInvoice.getIssueDate ()))
      ret.setIssueDate (aInvoice.getIssueDate ());
    if (m_aFilter.use ("BT-166", aInvoice.getIssueTime ()))
      ret.setIssueTime (XMLOffsetTime.of (aInvoice.getIssueTime ()));
    if (m_aFilter.use ("BT-9", aInvoice.getPaymentDueDate ()))
      ret.setDueDate (aInvoice.getPaymentDueDate ());
    if (m_aFilter.use ("BT-3", aInvoice.getTypeCode ()))
      ret.setInvoiceTypeCode (aInvoice.getTypeCode ());
    ret.setAnnotation (_annotations (aInvoice));
    if (m_aFilter.use ("BT-7", aInvoice.getVatPointDate ()))
      ret.setTaxPointDate (aInvoice.getVatPointDate ());
    if (m_aFilter.use ("BT-5", aInvoice.getCurrency ()))
      ret.setDocumentCurrencyCode (aInvoice.getCurrency ());
    if (m_aFilter.use ("BT-6", aInvoice.getVatAccountingCurrency ()))
      ret.setTaxCurrencyCode (aInvoice.getVatAccountingCurrency ());
    ret.setTaxExchangeRate (_exchangeRate (aInvoice));
    if (m_aFilter.use ("BT-19", aInvoice.getBuyerAccountingReference ()))
      ret.setAccountingCost (aInvoice.getBuyerAccountingReference ());
    ret.setBuyerAssignedReference (_buyerReferences (aInvoice));
    final PeriodType aPeriod = _invoicePeriod (aInvoice);
    if (aPeriod != null)
      ret.addInvoicePeriod (aPeriod);
    ret.setOrderReference (_orderReference (aInvoice));
    ret.setBillingReference (_billingReferences (aInvoice));
    if (m_aFilter.use ("BT-16", aInvoice.getDespatchAdviceReference ()))
      ret.addDespatchDocumentReference (_documentReference (aInvoice.getDespatchAdviceReference ()));
    if (m_aFilter.use ("BT-15", aInvoice.getReceivingAdviceReference ()))
      ret.addReceiptDocumentReference (_documentReference (aInvoice.getReceivingAdviceReference ()));
    if (m_aFilter.use ("BT-197", aInvoice.getDeliveryNoteReference ()))
      ret.addDeliveryNoteDocumentReference (_documentReference (aInvoice.getDeliveryNoteReference ()));
    if (m_aFilter.use ("BT-17", aInvoice.getTenderOrLotReference ()))
      ret.addOriginatorDocumentReference (_documentReference (aInvoice.getTenderOrLotReference ()));
    if (m_aFilter.use ("BT-12", aInvoice.getContractReference ()))
      ret.addContractDocumentReference (_documentReference (aInvoice.getContractReference ()));
    ret.setAdditionalDocumentReference (_additionalDocumentReferences (aInvoice));
    if (m_aFilter.use ("BT-11", aInvoice.getProjectReference ()))
    {
      final ProjectReferenceType aPR = new ProjectReferenceType ();
      aPR.setID (aInvoice.getProjectReference ());
      ret.addProjectReference (aPR);
    }
    _reportHeaderUnsupported (aInvoice);

    final PhinvoicePaymentInstructions aPI = aInvoice.getPaymentInstructions ();
    final String sSepaCreditorID = _getSepaCreditorID (aPI);
    final boolean bPayee = aInvoice.getPayee () != null && m_aFilter.isAllowed ("BG-10");
    ret.setAccountingSupplierParty (_seller (aInvoice.getSeller (), bPayee ? null : sSepaCreditorID));
    ret.setAccountingCustomerParty (_buyer (aInvoice.getBuyer ()));
    ret.setPayeeParty (_payee (aInvoice.getPayee (), sSepaCreditorID));
    ret.setTaxRepresentativeParty (_taxRepresentative (aInvoice.getSellerTaxRepresentative ()));
    final DeliveryType aDelivery = _delivery (aInvoice.getDeliveryInformation ());
    if (aDelivery != null)
      ret.addDelivery (aDelivery);
    ret.setPaymentMeans (_paymentInstructions (aPI));
    ret.setPaymentTerms (_paymentTerms (aInvoice));
    if (m_aFilter.useGroup ("BG-20", aInvoice.documentAllowances ()))
      for (final PhinvoiceDocumentAllowance aDA : aInvoice.documentAllowances ())
        ret.addAllowanceCharge (_documentAllowance (aDA));
    if (m_aFilter.useGroup ("BG-21", aInvoice.documentCharges ()))
      for (final PhinvoiceDocumentCharge aDC : aInvoice.documentCharges ())
        ret.addAllowanceCharge (_documentCharge (aDC));
    _taxTotals (aInvoice, ret.getTaxTotal ());
    ret.setLegalMonetaryTotal (_totals (aInvoice.getDocumentTotals ()));
    if (m_aFilter.useGroup ("BG-25", aInvoice.lines ().size () + " invoice lines"))
      for (final PhinvoiceInvoiceLine aLine : aInvoice.lines ())
        ret.addInvoiceLine (_invoiceLine (aLine));
    if (m_aFilter.useGroup ("BG-34", aInvoice.getDocumentTotals ().thirdPartyCharges ()))
    {
      int nIndex = 0;
      for (final PhinvoiceThirdPartyCharge aTPC : aInvoice.getDocumentTotals ().thirdPartyCharges ())
      {
        final InvoiceLineType aLine = new InvoiceLineType ();
        _collectionLine (aTPC, nIndex++, aLine::setID, x -> _currency (aLine.setTaxInclusiveLineExtensionAmount (x)), aLine::setItem);
        ret.addCollectionInvoiceLine (aLine);
      }
    }
    m_aFilter.dropAdditionalTerms (aInvoice.additionalTerms ());
    // The values of Factur-X EXTENDED cannot be written
    PhinvoiceExtendedTermReporter.reportExtendedTerms (aInvoice, m_aFilter);
    return ret;
  }

  /**
   * Create the UBL 2.5 Credit Note.
   *
   * @param aInvoice
   *        The invoice to write. May not be <code>null</code>.
   * @param sSpecificationIdentifier
   *        The BT-24 value to write. May not be <code>null</code>.
   * @return The JAXB object. Never <code>null</code>.
   */
  @NonNull
  CreditNoteType createCreditNote (@NonNull final PhinvoiceInvoice aInvoice, @NonNull final String sSpecificationIdentifier)
  {
    m_sCurrency = aInvoice.getCurrency ();
    final CreditNoteType ret = new CreditNoteType ();
    ret.setCustomizationID (sSpecificationIdentifier);
    if (m_aFilter.use ("BT-23", aInvoice.getProcessControl ().getBusinessProcessType ()))
      ret.setProfileID (aInvoice.getProcessControl ().getBusinessProcessType ());
    if (m_aFilter.use ("BT-1", aInvoice.getNumber ()))
      ret.setID (aInvoice.getNumber ());
    if (m_aFilter.use ("BT-2", aInvoice.getIssueDate ()))
      ret.setIssueDate (aInvoice.getIssueDate ());
    if (m_aFilter.use ("BT-166", aInvoice.getIssueTime ()))
      ret.setIssueTime (XMLOffsetTime.of (aInvoice.getIssueTime ()));
    if (m_aFilter.use ("BT-9", aInvoice.getPaymentDueDate ()))
      ret.setDueDate (aInvoice.getPaymentDueDate ());
    if (m_aFilter.use ("BT-3", aInvoice.getTypeCode ()))
      ret.setCreditNoteTypeCode (aInvoice.getTypeCode ());
    ret.setAnnotation (_annotations (aInvoice));
    if (m_aFilter.use ("BT-7", aInvoice.getVatPointDate ()))
      ret.setTaxPointDate (aInvoice.getVatPointDate ());
    if (m_aFilter.use ("BT-5", aInvoice.getCurrency ()))
      ret.setDocumentCurrencyCode (aInvoice.getCurrency ());
    if (m_aFilter.use ("BT-6", aInvoice.getVatAccountingCurrency ()))
      ret.setTaxCurrencyCode (aInvoice.getVatAccountingCurrency ());
    ret.setTaxExchangeRate (_exchangeRate (aInvoice));
    if (m_aFilter.use ("BT-19", aInvoice.getBuyerAccountingReference ()))
      ret.setAccountingCost (aInvoice.getBuyerAccountingReference ());
    ret.setBuyerAssignedReference (_buyerReferences (aInvoice));
    final PeriodType aPeriod = _invoicePeriod (aInvoice);
    if (aPeriod != null)
      ret.addInvoicePeriod (aPeriod);
    ret.setOrderReference (_orderReference (aInvoice));
    ret.setBillingReference (_billingReferences (aInvoice));
    if (m_aFilter.use ("BT-16", aInvoice.getDespatchAdviceReference ()))
      ret.addDespatchDocumentReference (_documentReference (aInvoice.getDespatchAdviceReference ()));
    if (m_aFilter.use ("BT-15", aInvoice.getReceivingAdviceReference ()))
      ret.addReceiptDocumentReference (_documentReference (aInvoice.getReceivingAdviceReference ()));
    if (m_aFilter.use ("BT-197", aInvoice.getDeliveryNoteReference ()))
      ret.addDeliveryNoteDocumentReference (_documentReference (aInvoice.getDeliveryNoteReference ()));
    if (m_aFilter.use ("BT-12", aInvoice.getContractReference ()))
      ret.addContractDocumentReference (_documentReference (aInvoice.getContractReference ()));
    ret.setAdditionalDocumentReference (_additionalDocumentReferences (aInvoice));
    if (m_aFilter.use ("BT-11", aInvoice.getProjectReference ()))
    {
      final ProjectReferenceType aPR = new ProjectReferenceType ();
      aPR.setID (aInvoice.getProjectReference ());
      ret.addProjectReference (aPR);
    }
    if (m_aFilter.use ("BT-17", aInvoice.getTenderOrLotReference ()))
      ret.addOriginatorDocumentReference (_documentReference (aInvoice.getTenderOrLotReference ()));
    _reportHeaderUnsupported (aInvoice);

    final PhinvoicePaymentInstructions aPI = aInvoice.getPaymentInstructions ();
    final String sSepaCreditorID = _getSepaCreditorID (aPI);
    final boolean bPayee = aInvoice.getPayee () != null && m_aFilter.isAllowed ("BG-10");
    ret.setAccountingSupplierParty (_seller (aInvoice.getSeller (), bPayee ? null : sSepaCreditorID));
    ret.setAccountingCustomerParty (_buyer (aInvoice.getBuyer ()));
    ret.setPayeeParty (_payee (aInvoice.getPayee (), sSepaCreditorID));
    ret.setTaxRepresentativeParty (_taxRepresentative (aInvoice.getSellerTaxRepresentative ()));
    final DeliveryType aDelivery = _delivery (aInvoice.getDeliveryInformation ());
    if (aDelivery != null)
      ret.addDelivery (aDelivery);
    ret.setPaymentMeans (_paymentInstructions (aPI));
    ret.setPaymentTerms (_paymentTerms (aInvoice));
    if (m_aFilter.useGroup ("BG-20", aInvoice.documentAllowances ()))
      for (final PhinvoiceDocumentAllowance aDA : aInvoice.documentAllowances ())
        ret.addAllowanceCharge (_documentAllowance (aDA));
    if (m_aFilter.useGroup ("BG-21", aInvoice.documentCharges ()))
      for (final PhinvoiceDocumentCharge aDC : aInvoice.documentCharges ())
        ret.addAllowanceCharge (_documentCharge (aDC));
    _taxTotals (aInvoice, ret.getTaxTotal ());
    ret.setLegalMonetaryTotal (_totals (aInvoice.getDocumentTotals ()));
    if (m_aFilter.useGroup ("BG-25", aInvoice.lines ().size () + " invoice lines"))
      for (final PhinvoiceInvoiceLine aLine : aInvoice.lines ())
        ret.addCreditNoteLine (_creditNoteLine (aLine));
    if (m_aFilter.useGroup ("BG-34", aInvoice.getDocumentTotals ().thirdPartyCharges ()))
    {
      int nIndex = 0;
      for (final PhinvoiceThirdPartyCharge aTPC : aInvoice.getDocumentTotals ().thirdPartyCharges ())
      {
        final CreditNoteLineType aLine = new CreditNoteLineType ();
        _collectionLine (aTPC, nIndex++, aLine::setID, x -> _currency (aLine.setTaxInclusiveLineExtensionAmount (x)), aLine::setItem);
        ret.addCollectionCreditNoteLine (aLine);
      }
    }
    m_aFilter.dropAdditionalTerms (aInvoice.additionalTerms ());
    // The values of Factur-X EXTENDED cannot be written
    PhinvoiceExtendedTermReporter.reportExtendedTerms (aInvoice, m_aFilter);
    return ret;
  }
}
