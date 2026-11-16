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
 * A trade party of the Factur-X profile EXTENDED (a BG-X), e.g. the ship-from party, the invoicer, the
 * invoicee, the payer or a sales agent. The term IDs of the values depend on the position of the party
 * in the invoice; the CII reader and writer know them.
 *
 * @author Philip Helger
 */
public class PhinvoiceTradeParty
{
  private String m_sID;
  private final ICommonsList <PhinvoiceIdentifier> m_aGlobalIdentifiers = new CommonsArrayList <> ();
  private String m_sName;
  private String m_sRoleCode;
  private String m_sDescription;
  private PhinvoiceIdentifier m_aLegalRegistration;
  private String m_sTradingName;
  private PhinvoicePostalAddress m_aLegalAddress;
  private PhinvoiceContact m_aContact;
  private PhinvoicePostalAddress m_aPostalAddress;
  private PhinvoiceIdentifier m_aElectronicAddress;
  private final ICommonsList <PhinvoiceIdentifier> m_aTaxRegistrations = new CommonsArrayList <> ();

  /** Create an empty instance. */
  public PhinvoiceTradeParty ()
  {}

  /**
   * Get the party identifier (ram:ID).
   *
   * @return Party identifier (ram:ID). May be {@code null}.
   */
  @Nullable
  public String getID ()
  {
    return m_sID;
  }

  /**
   * Set the party identifier (ram:ID).
   *
   * @param sID
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceTradeParty setID (@Nullable final String sID)
  {
    m_sID = sID;
    return this;
  }

  /**
   * Get the mutable list of global identifiers with their scheme.
   *
   * @return Mutable list of global identifiers with their scheme. Never {@code null}; empty when none are present.
   */
  @NonNull
  @ReturnsMutableObject
  public ICommonsList <PhinvoiceIdentifier> globalIdentifiers ()
  {
    return m_aGlobalIdentifiers;
  }

  /**
   * Get the name.
   *
   * @return Name. May be {@code null}.
   */
  @Nullable
  public String getName ()
  {
    return m_sName;
  }

  /**
   * Set the name.
   *
   * @param sName
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceTradeParty setName (@Nullable final String sName)
  {
    m_sName = sName;
    return this;
  }

  /**
   * Get the role code.
   *
   * @return Role code. May be {@code null}.
   */
  @Nullable
  public String getRoleCode ()
  {
    return m_sRoleCode;
  }

  /**
   * Set the role code.
   *
   * @param sRoleCode
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceTradeParty setRoleCode (@Nullable final String sRoleCode)
  {
    m_sRoleCode = sRoleCode;
    return this;
  }

  /**
   * Get the description.
   *
   * @return Description. May be {@code null}.
   */
  @Nullable
  public String getDescription ()
  {
    return m_sDescription;
  }

  /**
   * Set the description.
   *
   * @param sDescription
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceTradeParty setDescription (@Nullable final String sDescription)
  {
    m_sDescription = sDescription;
    return this;
  }

  /**
   * Get the legal registration identifier.
   *
   * @return Legal registration identifier. May be {@code null}.
   */
  @Nullable
  public PhinvoiceIdentifier getLegalRegistration ()
  {
    return m_aLegalRegistration;
  }

  /**
   * Set the legal registration identifier.
   *
   * @param aLegalRegistration
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceTradeParty setLegalRegistration (@Nullable final PhinvoiceIdentifier aLegalRegistration)
  {
    m_aLegalRegistration = aLegalRegistration;
    return this;
  }

  /**
   * Get the trading name.
   *
   * @return Trading name. May be {@code null}.
   */
  @Nullable
  public String getTradingName ()
  {
    return m_sTradingName;
  }

  /**
   * Set the trading name.
   *
   * @param sTradingName
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceTradeParty setTradingName (@Nullable final String sTradingName)
  {
    m_sTradingName = sTradingName;
    return this;
  }

  /**
   * Get the postal address of the legal organisation.
   *
   * @return Postal address of the legal organisation. May be {@code null}.
   */
  @Nullable
  public PhinvoicePostalAddress getLegalAddress ()
  {
    return m_aLegalAddress;
  }

  /**
   * Set the postal address of the legal organisation.
   *
   * @param aLegalAddress
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceTradeParty setLegalAddress (@Nullable final PhinvoicePostalAddress aLegalAddress)
  {
    m_aLegalAddress = aLegalAddress;
    return this;
  }

  /**
   * Get the contact.
   *
   * @return Contact. May be {@code null}.
   */
  @Nullable
  public PhinvoiceContact getContact ()
  {
    return m_aContact;
  }

  /**
   * Set the contact.
   *
   * @param aContact
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceTradeParty setContact (@Nullable final PhinvoiceContact aContact)
  {
    m_aContact = aContact;
    return this;
  }

  /**
   * Get the postal address.
   *
   * @return Postal address. May be {@code null}.
   */
  @Nullable
  public PhinvoicePostalAddress getPostalAddress ()
  {
    return m_aPostalAddress;
  }

  /**
   * Set the postal address.
   *
   * @param aPostalAddress
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceTradeParty setPostalAddress (@Nullable final PhinvoicePostalAddress aPostalAddress)
  {
    m_aPostalAddress = aPostalAddress;
    return this;
  }

  /**
   * Get the electronic address.
   *
   * @return Electronic address. May be {@code null}.
   */
  @Nullable
  public PhinvoiceIdentifier getElectronicAddress ()
  {
    return m_aElectronicAddress;
  }

  /**
   * Set the electronic address.
   *
   * @param aElectronicAddress
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceTradeParty setElectronicAddress (@Nullable final PhinvoiceIdentifier aElectronicAddress)
  {
    m_aElectronicAddress = aElectronicAddress;
    return this;
  }

  /**
   * Get the mutable list of tax registrations (scheme VA for a VAT identifier, FC for a tax number).
   *
   * @return Mutable list of tax registrations (scheme VA for a VAT identifier, FC for a tax number). Never {@code null}; empty when none are present.
   */
  @NonNull
  @ReturnsMutableObject
  public ICommonsList <PhinvoiceIdentifier> taxRegistrations ()
  {
    return m_aTaxRegistrations;
  }

  /**
   * Check if no value is set.
   *
   * @return {@code true} when every field is {@code null} or empty.
   */
  public boolean isEmpty ()
  {
    return m_sID == null &&
           m_aGlobalIdentifiers.isEmpty () &&
           m_sName == null &&
           m_sRoleCode == null &&
           m_sDescription == null &&
           m_aLegalRegistration == null &&
           m_sTradingName == null &&
           m_aLegalAddress == null &&
           m_aContact == null &&
           m_aPostalAddress == null &&
           m_aElectronicAddress == null &&
           m_aTaxRegistrations.isEmpty ();
  }
}
