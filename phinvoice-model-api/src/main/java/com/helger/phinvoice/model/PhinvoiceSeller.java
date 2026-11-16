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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;

/**
 * BG-4 SELLER plus nested BG-5 (postal address) and BG-6 (contact). Carries BT-27 (name),
 * BT-28 (trading name), BT-29/BT-29-1 (identifiers), BT-30/BT-30-1 (legal registration),
 * BT-31 (VAT identifier), BT-32 (tax registration), BT-33 (additional legal info) and
 * BT-34/BT-34-1 (electronic address).
 *
 * @author Philip Helger
 */
public class PhinvoiceSeller
{
  private String m_sName;
  private String m_sTradingName;
  private final ICommonsList <PhinvoiceIdentifier> m_aIdentifiers = new CommonsArrayList <> ();
  private PhinvoiceIdentifier m_aLegalRegistration;
  private String m_sVatIdentifier;
  private String m_sTaxRegistration;
  private String m_sAdditionalLegalInfo;
  private PhinvoiceIdentifier m_aElectronicAddress;
  private PhinvoicePostalAddress m_aPostalAddress;
  private PhinvoiceContact m_aContact;
  private String m_sTaxRegistrationSchemeID;
  private String m_sRoleCode;
  private PhinvoicePostalAddress m_aLegalAddress;
  private final ICommonsList <PhinvoiceAdditionalTerm> m_aAdditionalTerms = new CommonsArrayList <> ();

  /** Create an empty seller. */
  public PhinvoiceSeller ()
  {}

  /**
   * Get the seller name (BT-27).
   *
   * @return Seller name (BT-27). May be {@code null}.
   */
  @Nullable
  public String getName ()
  {
    return m_sName;
  }

  /**
   * Set the seller name (BT-27).
   *
   * @param sName
   *        Name; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceSeller setName (@Nullable final String sName)
  {
    m_sName = sName;
    return this;
  }

  /**
   * Get the seller trading name (BT-28).
   *
   * @return Seller trading name (BT-28). May be {@code null}.
   */
  @Nullable
  public String getTradingName ()
  {
    return m_sTradingName;
  }

  /**
   * Set the seller trading name (BT-28).
   *
   * @param sTradingName
   *        Trading name; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceSeller setTradingName (@Nullable final String sTradingName)
  {
    m_sTradingName = sTradingName;
    return this;
  }

  /**
   * Get the mutable list of the values of the seller that the model has no place for.
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
   * Get the mutable list of seller identifiers (BT-29 with BT-29-1 scheme IDs).
   *
   * @return The mutable list of seller identifiers (BT-29 with BT-29-1 scheme IDs). Never
   *         {@code null}; empty when none are present.
   */
  @NonNull
  @ReturnsMutableObject
  public ICommonsList <PhinvoiceIdentifier> identifiers ()
  {
    return m_aIdentifiers;
  }

  /**
   * Get the legal registration identifier (BT-30 with BT-30-1 scheme ID).
   *
   * @return The legal registration identifier (BT-30 with BT-30-1 scheme ID). May be {@code null}.
   */
  @Nullable
  public PhinvoiceIdentifier getLegalRegistration ()
  {
    return m_aLegalRegistration;
  }

  /**
   * Set the legal registration identifier (BT-30 / BT-30-1).
   *
   * @param aLegalRegistration
   *        Identifier; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceSeller setLegalRegistration (@Nullable final PhinvoiceIdentifier aLegalRegistration)
  {
    m_aLegalRegistration = aLegalRegistration;
    return this;
  }

  /**
   * Get the seller VAT identifier (BT-31).
   *
   * @return Seller VAT identifier (BT-31). May be {@code null}.
   */
  @Nullable
  public String getVatIdentifier ()
  {
    return m_sVatIdentifier;
  }

  /**
   * Set the seller VAT identifier (BT-31).
   *
   * @param sVatIdentifier
   *        VAT ID; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceSeller setVatIdentifier (@Nullable final String sVatIdentifier)
  {
    m_sVatIdentifier = sVatIdentifier;
    return this;
  }

  /**
   * Get the seller tax registration ID (BT-32).
   *
   * @return Seller tax registration ID (BT-32). May be {@code null}.
   */
  @Nullable
  public String getTaxRegistration ()
  {
    return m_sTaxRegistration;
  }

  /**
   * Set the seller tax registration ID (BT-32).
   *
   * @param sTaxRegistration
   *        Tax registration ID; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceSeller setTaxRegistration (@Nullable final String sTaxRegistration)
  {
    m_sTaxRegistration = sTaxRegistration;
    return this;
  }

  /**
   * Get the additional seller legal information (BT-33).
   *
   * @return Additional seller legal information (BT-33). May be {@code null}.
   */
  @Nullable
  public String getAdditionalLegalInfo ()
  {
    return m_sAdditionalLegalInfo;
  }

  /**
   * Set the additional seller legal information (BT-33).
   *
   * @param sAdditionalLegalInfo
   *        Free-form text; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceSeller setAdditionalLegalInfo (@Nullable final String sAdditionalLegalInfo)
  {
    m_sAdditionalLegalInfo = sAdditionalLegalInfo;
    return this;
  }

  /**
   * Get the seller electronic address (BT-34 with BT-34-1 scheme ID).
   *
   * @return Seller electronic address (BT-34 with BT-34-1 scheme ID). May be {@code null}.
   */
  @Nullable
  public PhinvoiceIdentifier getElectronicAddress ()
  {
    return m_aElectronicAddress;
  }

  /**
   * Set the seller electronic address (BT-34 / BT-34-1).
   *
   * @param aElectronicAddress
   *        Electronic address identifier; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceSeller setElectronicAddress (@Nullable final PhinvoiceIdentifier aElectronicAddress)
  {
    m_aElectronicAddress = aElectronicAddress;
    return this;
  }

  /**
   * Get the seller postal address (BG-5).
   *
   * @return Seller postal address (BG-5). May be {@code null}.
   */
  @Nullable
  public PhinvoicePostalAddress getPostalAddress ()
  {
    return m_aPostalAddress;
  }

  /**
   * Set the seller postal address (BG-5).
   *
   * @param aPostalAddress
   *        Address; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceSeller setPostalAddress (@Nullable final PhinvoicePostalAddress aPostalAddress)
  {
    m_aPostalAddress = aPostalAddress;
    return this;
  }

  /**
   * Get the seller contact (BG-6).
   *
   * @return Seller contact (BG-6). May be {@code null}.
   */
  @Nullable
  public PhinvoiceContact getContact ()
  {
    return m_aContact;
  }

  /**
   * Set the seller contact (BG-6).
   *
   * @param aContact
   *        Contact; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceSeller setContact (@Nullable final PhinvoiceContact aContact)
  {
    m_aContact = aContact;
    return this;
  }

  /**
   * Get the seller tax registration identifier scheme identifier (BT-32-1).
   *
   * @return Seller tax registration identifier scheme identifier (BT-32-1). May be {@code null}.
   */
  @Nullable
  public String getTaxRegistrationSchemeID ()
  {
    return m_sTaxRegistrationSchemeID;
  }

  /**
   * Set the seller tax registration identifier scheme identifier (BT-32-1).
   *
   * @param sTaxRegistrationSchemeID
   *        Seller tax registration identifier scheme identifier; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceSeller setTaxRegistrationSchemeID (@Nullable final String sTaxRegistrationSchemeID)
  {
    m_sTaxRegistrationSchemeID = sTaxRegistrationSchemeID;
    return this;
  }

  /**
   * Get the role code (BT-X-543).
   *
   * @return Role code (BT-X-543). May be {@code null}.
   */
  @Nullable
  public String getRoleCode ()
  {
    return m_sRoleCode;
  }

  /**
   * Set the role code (BT-X-543).
   *
   * @param sRoleCode
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceSeller setRoleCode (@Nullable final String sRoleCode)
  {
    m_sRoleCode = sRoleCode;
    return this;
  }

  /**
   * Get the legal postal address (BG-X-14).
   *
   * @return Legal postal address (BG-X-14). May be {@code null}.
   */
  @Nullable
  public PhinvoicePostalAddress getLegalAddress ()
  {
    return m_aLegalAddress;
  }

  /**
   * Set the legal postal address (BG-X-14).
   *
   * @param aLegalAddress
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceSeller setLegalAddress (@Nullable final PhinvoicePostalAddress aLegalAddress)
  {
    m_aLegalAddress = aLegalAddress;
    return this;
  }
}
