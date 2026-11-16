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
 * BG-11 SELLER TAX REPRESENTATIVE PARTY plus nested BG-12 postal address. Carries BT-62
 * (tax representative name), BT-63 (tax representative VAT ID) and the BG-12 address fields
 * (BT-64..BT-69).
 *
 * @author Philip Helger
 */
public class PhinvoiceSellerTaxRepresentative
{
  private String m_sName;
  private String m_sVatIdentifier;
  private PhinvoicePostalAddress m_aPostalAddress;
  private String m_sID;
  private final ICommonsList <PhinvoiceIdentifier> m_aGlobalIdentifiers = new CommonsArrayList <> ();
  private String m_sRoleCode;
  private PhinvoiceIdentifier m_aElectronicAddress;
  private PhinvoiceIdentifier m_aLegalRegistration;
  private String m_sTradingName;
  private PhinvoicePostalAddress m_aLegalAddress;
  private PhinvoiceContact m_aContact;

  /** Create an empty tax representative. */
  public PhinvoiceSellerTaxRepresentative ()
  {}

  /**
   * Get the tax representative name (BT-62).
   *
   * @return Tax representative name (BT-62). May be {@code null}.
   */
  @Nullable
  public String getName ()
  {
    return m_sName;
  }

  /**
   * Set the tax representative name (BT-62).
   *
   * @param sName
   *        Name; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceSellerTaxRepresentative setName (@Nullable final String sName)
  {
    m_sName = sName;
    return this;
  }

  /**
   * Get the tax representative VAT identifier (BT-63).
   *
   * @return Tax representative VAT identifier (BT-63). May be {@code null}.
   */
  @Nullable
  public String getVatIdentifier ()
  {
    return m_sVatIdentifier;
  }

  /**
   * Set the tax representative VAT identifier (BT-63).
   *
   * @param sVatIdentifier
   *        VAT ID; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceSellerTaxRepresentative setVatIdentifier (@Nullable final String sVatIdentifier)
  {
    m_sVatIdentifier = sVatIdentifier;
    return this;
  }

  /**
   * Get the tax representative postal address (BG-12).
   *
   * @return Tax representative postal address (BG-12). May be {@code null}.
   */
  @Nullable
  public PhinvoicePostalAddress getPostalAddress ()
  {
    return m_aPostalAddress;
  }

  /**
   * Set the tax representative postal address (BG-12).
   *
   * @param aPostalAddress
   *        Address; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceSellerTaxRepresentative setPostalAddress (@Nullable final PhinvoicePostalAddress aPostalAddress)
  {
    m_aPostalAddress = aPostalAddress;
    return this;
  }

  /**
   * Get the identifier (BT-X-116).
   *
   * @return Identifier (BT-X-116). May be {@code null}.
   */
  @Nullable
  public String getID ()
  {
    return m_sID;
  }

  /**
   * Set the identifier (BT-X-116).
   *
   * @param sID
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceSellerTaxRepresentative setID (@Nullable final String sID)
  {
    m_sID = sID;
    return this;
  }

  /**
   * Get the mutable list of global identifiers (BT-X-117).
   *
   * @return Mutable list of global identifiers (BT-X-117). Never {@code null}; empty when none are present.
   */
  @NonNull
  @ReturnsMutableObject
  public ICommonsList <PhinvoiceIdentifier> globalIdentifiers ()
  {
    return m_aGlobalIdentifiers;
  }

  /**
   * Get the role code (BT-X-547).
   *
   * @return Role code (BT-X-547). May be {@code null}.
   */
  @Nullable
  public String getRoleCode ()
  {
    return m_sRoleCode;
  }

  /**
   * Set the role code (BT-X-547).
   *
   * @param sRoleCode
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceSellerTaxRepresentative setRoleCode (@Nullable final String sRoleCode)
  {
    m_sRoleCode = sRoleCode;
    return this;
  }

  /**
   * Get the electronic address (BT-X-125).
   *
   * @return Electronic address (BT-X-125). May be {@code null}.
   */
  @Nullable
  public PhinvoiceIdentifier getElectronicAddress ()
  {
    return m_aElectronicAddress;
  }

  /**
   * Set the electronic address (BT-X-125).
   *
   * @param aElectronicAddress
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceSellerTaxRepresentative setElectronicAddress (@Nullable final PhinvoiceIdentifier aElectronicAddress)
  {
    m_aElectronicAddress = aElectronicAddress;
    return this;
  }

  /**
   * Get the legal registration identifier (BG-X-16).
   *
   * @return Legal registration identifier (BG-X-16). May be {@code null}.
   */
  @Nullable
  public PhinvoiceIdentifier getLegalRegistration ()
  {
    return m_aLegalRegistration;
  }

  /**
   * Set the legal registration identifier (BG-X-16).
   *
   * @param aLegalRegistration
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceSellerTaxRepresentative setLegalRegistration (@Nullable final PhinvoiceIdentifier aLegalRegistration)
  {
    m_aLegalRegistration = aLegalRegistration;
    return this;
  }

  /**
   * Get the trading name (BG-X-16).
   *
   * @return Trading name (BG-X-16). May be {@code null}.
   */
  @Nullable
  public String getTradingName ()
  {
    return m_sTradingName;
  }

  /**
   * Set the trading name (BG-X-16).
   *
   * @param sTradingName
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceSellerTaxRepresentative setTradingName (@Nullable final String sTradingName)
  {
    m_sTradingName = sTradingName;
    return this;
  }

  /**
   * Get the legal postal address (BG-X-59).
   *
   * @return Legal postal address (BG-X-59). May be {@code null}.
   */
  @Nullable
  public PhinvoicePostalAddress getLegalAddress ()
  {
    return m_aLegalAddress;
  }

  /**
   * Set the legal postal address (BG-X-59).
   *
   * @param aLegalAddress
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceSellerTaxRepresentative setLegalAddress (@Nullable final PhinvoicePostalAddress aLegalAddress)
  {
    m_aLegalAddress = aLegalAddress;
    return this;
  }

  /**
   * Get the contact (BG-X-17).
   *
   * @return Contact (BG-X-17). May be {@code null}.
   */
  @Nullable
  public PhinvoiceContact getContact ()
  {
    return m_aContact;
  }

  /**
   * Set the contact (BG-X-17).
   *
   * @param aContact
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceSellerTaxRepresentative setContact (@Nullable final PhinvoiceContact aContact)
  {
    m_aContact = aContact;
    return this;
  }
}
