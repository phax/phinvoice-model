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
 * BG-10 PAYEE (0..1). Carries BT-59 (payee name), BT-60/BT-60-1 (payee identifier with scheme)
 * and BT-61/BT-61-1 (payee legal registration with scheme).
 *
 * @author Philip Helger
 */
public class PhinvoicePayee
{
  private String m_sName;
  private PhinvoiceIdentifier m_aIdentifier;
  private PhinvoiceIdentifier m_aLegalRegistration;
  private String m_sRoleCode;
  private String m_sTradingName;
  private PhinvoiceIdentifier m_aElectronicAddress;
  private final ICommonsList <PhinvoiceIdentifier> m_aTaxRegistrations = new CommonsArrayList <> ();
  private PhinvoicePostalAddress m_aLegalAddress;
  private PhinvoiceContact m_aContact;
  private PhinvoicePostalAddress m_aPostalAddress;

  /** Create an empty payee. */
  public PhinvoicePayee ()
  {}

  /**
   * Get the payee name (BT-59).
   *
   * @return Payee name (BT-59). May be {@code null}.
   */
  @Nullable
  public String getName ()
  {
    return m_sName;
  }

  /**
   * Set the payee name (BT-59).
   *
   * @param sName
   *        Name; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoicePayee setName (@Nullable final String sName)
  {
    m_sName = sName;
    return this;
  }

  /**
   * Get the payee identifier (BT-60 with BT-60-1 scheme ID).
   *
   * @return Payee identifier (BT-60 with BT-60-1 scheme ID). May be {@code null}.
   */
  @Nullable
  public PhinvoiceIdentifier getIdentifier ()
  {
    return m_aIdentifier;
  }

  /**
   * Set the payee identifier (BT-60 / BT-60-1).
   *
   * @param aIdentifier
   *        Identifier; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoicePayee setIdentifier (@Nullable final PhinvoiceIdentifier aIdentifier)
  {
    m_aIdentifier = aIdentifier;
    return this;
  }

  /**
   * Get the payee legal registration (BT-61 with BT-61-1 scheme ID).
   *
   * @return Payee legal registration (BT-61 with BT-61-1 scheme ID). May be {@code null}.
   */
  @Nullable
  public PhinvoiceIdentifier getLegalRegistration ()
  {
    return m_aLegalRegistration;
  }

  /**
   * Set the payee legal registration (BT-61 / BT-61-1).
   *
   * @param aLegalRegistration
   *        Identifier; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoicePayee setLegalRegistration (@Nullable final PhinvoiceIdentifier aLegalRegistration)
  {
    m_aLegalRegistration = aLegalRegistration;
    return this;
  }

  /**
   * Get the role code (BT-X-468).
   *
   * @return Role code (BT-X-468). May be {@code null}.
   */
  @Nullable
  public String getRoleCode ()
  {
    return m_sRoleCode;
  }

  /**
   * Set the role code (BT-X-468).
   *
   * @param sRoleCode
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoicePayee setRoleCode (@Nullable final String sRoleCode)
  {
    m_sRoleCode = sRoleCode;
    return this;
  }

  /**
   * Get the trading name (BT-X-243).
   *
   * @return Trading name (BT-X-243). May be {@code null}.
   */
  @Nullable
  public String getTradingName ()
  {
    return m_sTradingName;
  }

  /**
   * Set the trading name (BT-X-243).
   *
   * @param sTradingName
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoicePayee setTradingName (@Nullable final String sTradingName)
  {
    m_sTradingName = sTradingName;
    return this;
  }

  /**
   * Get the electronic address (BT-X-256).
   *
   * @return Electronic address (BT-X-256). May be {@code null}.
   */
  @Nullable
  public PhinvoiceIdentifier getElectronicAddress ()
  {
    return m_aElectronicAddress;
  }

  /**
   * Set the electronic address (BT-X-256).
   *
   * @param aElectronicAddress
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoicePayee setElectronicAddress (@Nullable final PhinvoiceIdentifier aElectronicAddress)
  {
    m_aElectronicAddress = aElectronicAddress;
    return this;
  }

  /**
   * Get the mutable list of tax registrations (BT-X-257).
   *
   * @return Mutable list of tax registrations (BT-X-257). Never {@code null}; empty when none are present.
   */
  @NonNull
  @ReturnsMutableObject
  public ICommonsList <PhinvoiceIdentifier> taxRegistrations ()
  {
    return m_aTaxRegistrations;
  }

  /**
   * Get the legal postal address (BG-X-72).
   *
   * @return Legal postal address (BG-X-72). May be {@code null}.
   */
  @Nullable
  public PhinvoicePostalAddress getLegalAddress ()
  {
    return m_aLegalAddress;
  }

  /**
   * Set the legal postal address (BG-X-72).
   *
   * @param aLegalAddress
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoicePayee setLegalAddress (@Nullable final PhinvoicePostalAddress aLegalAddress)
  {
    m_aLegalAddress = aLegalAddress;
    return this;
  }

  /**
   * Get the contact (BG-X-39).
   *
   * @return Contact (BG-X-39). May be {@code null}.
   */
  @Nullable
  public PhinvoiceContact getContact ()
  {
    return m_aContact;
  }

  /**
   * Set the contact (BG-X-39).
   *
   * @param aContact
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoicePayee setContact (@Nullable final PhinvoiceContact aContact)
  {
    m_aContact = aContact;
    return this;
  }

  /**
   * Get the postal address (BG-X-40).
   *
   * @return Postal address (BG-X-40). May be {@code null}.
   */
  @Nullable
  public PhinvoicePostalAddress getPostalAddress ()
  {
    return m_aPostalAddress;
  }

  /**
   * Set the postal address (BG-X-40).
   *
   * @param aPostalAddress
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoicePayee setPostalAddress (@Nullable final PhinvoicePostalAddress aPostalAddress)
  {
    m_aPostalAddress = aPostalAddress;
    return this;
  }
}
