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
 * BG-7 BUYER plus nested BG-8 (postal address) and BG-9 (contact). Carries BT-44 (name),
 * BT-45 (trading name), BT-46/BT-46-1 (identifiers), BT-47/BT-47-1 (legal registration),
 * BT-48 (VAT identifier) and BT-49/BT-49-1 (electronic address).
 *
 * @author Philip Helger
 */
public class PhinvoiceBuyer
{
  private String m_sName;
  private String m_sTradingName;
  private final ICommonsList <PhinvoiceIdentifier> m_aIdentifiers = new CommonsArrayList <> ();
  private PhinvoiceIdentifier m_aLegalRegistration;
  private String m_sVatIdentifier;
  private PhinvoiceIdentifier m_aElectronicAddress;
  private PhinvoicePostalAddress m_aPostalAddress;
  private PhinvoiceContact m_aContact;
  private String m_sRoleCode;
  private String m_sAdditionalLegalInfo;
  private PhinvoicePostalAddress m_aLegalAddress;
  private final ICommonsList <PhinvoiceAdditionalTerm> m_aAdditionalTerms = new CommonsArrayList <> ();

  /** Create an empty buyer. */
  public PhinvoiceBuyer ()
  {}

  /**
   * Get the buyer name (BT-44).
   *
   * @return Buyer name (BT-44). May be {@code null}.
   */
  @Nullable
  public String getName ()
  {
    return m_sName;
  }

  /**
   * Set the buyer name (BT-44).
   *
   * @param sName
   *        Name; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceBuyer setName (@Nullable final String sName)
  {
    m_sName = sName;
    return this;
  }

  /**
   * Get the buyer trading name (BT-45).
   *
   * @return Buyer trading name (BT-45). May be {@code null}.
   */
  @Nullable
  public String getTradingName ()
  {
    return m_sTradingName;
  }

  /**
   * Set the buyer trading name (BT-45).
   *
   * @param sTradingName
   *        Trading name; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceBuyer setTradingName (@Nullable final String sTradingName)
  {
    m_sTradingName = sTradingName;
    return this;
  }

  /**
   * Get the mutable list of the values of the buyer that the model has no place for.
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
   * Get the mutable list of buyer identifiers (BT-46 with BT-46-1 scheme IDs).
   *
   * @return The mutable list of buyer identifiers (BT-46 with BT-46-1 scheme IDs). Never
   *         {@code null}; empty when none are present.
   */
  @NonNull
  @ReturnsMutableObject
  public ICommonsList <PhinvoiceIdentifier> identifiers ()
  {
    return m_aIdentifiers;
  }

  /**
   * Get the buyer legal registration identifier (BT-47 with BT-47-1 scheme ID).
   *
   * @return Buyer legal registration identifier (BT-47 with BT-47-1 scheme ID). May be {@code null}.
   */
  @Nullable
  public PhinvoiceIdentifier getLegalRegistration ()
  {
    return m_aLegalRegistration;
  }

  /**
   * Set the buyer legal registration identifier (BT-47 / BT-47-1).
   *
   * @param aLegalRegistration
   *        Identifier; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceBuyer setLegalRegistration (@Nullable final PhinvoiceIdentifier aLegalRegistration)
  {
    m_aLegalRegistration = aLegalRegistration;
    return this;
  }

  /**
   * Get the buyer VAT identifier (BT-48).
   *
   * @return Buyer VAT identifier (BT-48). May be {@code null}.
   */
  @Nullable
  public String getVatIdentifier ()
  {
    return m_sVatIdentifier;
  }

  /**
   * Set the buyer VAT identifier (BT-48).
   *
   * @param sVatIdentifier
   *        VAT ID; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceBuyer setVatIdentifier (@Nullable final String sVatIdentifier)
  {
    m_sVatIdentifier = sVatIdentifier;
    return this;
  }

  /**
   * Get the buyer electronic address (BT-49 with BT-49-1 scheme ID).
   *
   * @return Buyer electronic address (BT-49 with BT-49-1 scheme ID). May be {@code null}.
   */
  @Nullable
  public PhinvoiceIdentifier getElectronicAddress ()
  {
    return m_aElectronicAddress;
  }

  /**
   * Set the buyer electronic address (BT-49 / BT-49-1).
   *
   * @param aElectronicAddress
   *        Electronic address identifier; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceBuyer setElectronicAddress (@Nullable final PhinvoiceIdentifier aElectronicAddress)
  {
    m_aElectronicAddress = aElectronicAddress;
    return this;
  }

  /**
   * Get the buyer postal address (BG-8).
   *
   * @return Buyer postal address (BG-8). May be {@code null}.
   */
  @Nullable
  public PhinvoicePostalAddress getPostalAddress ()
  {
    return m_aPostalAddress;
  }

  /**
   * Set the buyer postal address (BG-8).
   *
   * @param aPostalAddress
   *        Address; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceBuyer setPostalAddress (@Nullable final PhinvoicePostalAddress aPostalAddress)
  {
    m_aPostalAddress = aPostalAddress;
    return this;
  }

  /**
   * Get the buyer contact (BG-9).
   *
   * @return Buyer contact (BG-9). May be {@code null}.
   */
  @Nullable
  public PhinvoiceContact getContact ()
  {
    return m_aContact;
  }

  /**
   * Set the buyer contact (BG-9).
   *
   * @param aContact
   *        Contact; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceBuyer setContact (@Nullable final PhinvoiceContact aContact)
  {
    m_aContact = aContact;
    return this;
  }

  /**
   * Get the role code (BT-X-544).
   *
   * @return Role code (BT-X-544). May be {@code null}.
   */
  @Nullable
  public String getRoleCode ()
  {
    return m_sRoleCode;
  }

  /**
   * Set the role code (BT-X-544).
   *
   * @param sRoleCode
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceBuyer setRoleCode (@Nullable final String sRoleCode)
  {
    m_sRoleCode = sRoleCode;
    return this;
  }

  /**
   * Get the additional legal information (BT-X-334).
   *
   * @return Additional legal information (BT-X-334). May be {@code null}.
   */
  @Nullable
  public String getAdditionalLegalInfo ()
  {
    return m_sAdditionalLegalInfo;
  }

  /**
   * Set the additional legal information (BT-X-334).
   *
   * @param sAdditionalLegalInfo
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceBuyer setAdditionalLegalInfo (@Nullable final String sAdditionalLegalInfo)
  {
    m_sAdditionalLegalInfo = sAdditionalLegalInfo;
    return this;
  }

  /**
   * Get the legal postal address (BG-X-15).
   *
   * @return Legal postal address (BG-X-15). May be {@code null}.
   */
  @Nullable
  public PhinvoicePostalAddress getLegalAddress ()
  {
    return m_aLegalAddress;
  }

  /**
   * Set the legal postal address (BG-X-15).
   *
   * @param aLegalAddress
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceBuyer setLegalAddress (@Nullable final PhinvoicePostalAddress aLegalAddress)
  {
    m_aLegalAddress = aLegalAddress;
    return this;
  }
}
