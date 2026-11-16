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

import java.time.LocalDate;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;

/**
 * BG-13 DELIVERY INFORMATION plus nested BG-15 (Deliver-to address). Carries BT-70 (deliver-to
 * party name), BT-71/BT-71-1 (deliver-to location identifier), BT-72 (actual delivery date) and
 * the BG-15 address fields (BT-75..BT-80, BT-165).
 *
 * @author Philip Helger
 */
public class PhinvoiceDeliveryInformation
{
  private String m_sPartyName;
  private PhinvoiceIdentifier m_aLocationIdentifier;
  private LocalDate m_aActualDeliveryDate;
  private PhinvoicePostalAddress m_aPostalAddress;
  private String m_sRoleCode;
  private PhinvoiceIdentifier m_aElectronicAddress;
  private final ICommonsList <PhinvoiceIdentifier> m_aTaxRegistrations = new CommonsArrayList <> ();
  private PhinvoiceIdentifier m_aLegalRegistration;
  private String m_sTradingName;
  private PhinvoicePostalAddress m_aLegalAddress;
  private PhinvoiceContact m_aContact;

  /** Create an empty delivery information. */
  public PhinvoiceDeliveryInformation ()
  {}

  /**
   * Get the deliver-to party name (BT-70).
   *
   * @return Deliver-to party name (BT-70). May be {@code null}.
   */
  @Nullable
  public String getPartyName ()
  {
    return m_sPartyName;
  }

  /**
   * Set the deliver-to party name (BT-70).
   *
   * @param sPartyName
   *        Party name; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceDeliveryInformation setPartyName (@Nullable final String sPartyName)
  {
    m_sPartyName = sPartyName;
    return this;
  }

  /**
   * Get the deliver-to location identifier (BT-71 with BT-71-1 scheme ID).
   *
   * @return Deliver-to location identifier (BT-71 with BT-71-1 scheme ID). May be {@code null}.
   */
  @Nullable
  public PhinvoiceIdentifier getLocationIdentifier ()
  {
    return m_aLocationIdentifier;
  }

  /**
   * Set the deliver-to location identifier (BT-71 / BT-71-1).
   *
   * @param aLocationIdentifier
   *        Identifier; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceDeliveryInformation setLocationIdentifier (@Nullable final PhinvoiceIdentifier aLocationIdentifier)
  {
    m_aLocationIdentifier = aLocationIdentifier;
    return this;
  }

  /**
   * Get the actual delivery date (BT-72).
   *
   * @return Actual delivery date (BT-72). May be {@code null}.
   */
  @Nullable
  public LocalDate getActualDeliveryDate ()
  {
    return m_aActualDeliveryDate;
  }

  /**
   * Set the actual delivery date (BT-72).
   *
   * @param aActualDeliveryDate
   *        Delivery date; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceDeliveryInformation setActualDeliveryDate (@Nullable final LocalDate aActualDeliveryDate)
  {
    m_aActualDeliveryDate = aActualDeliveryDate;
    return this;
  }

  /**
   * Get the deliver-to address (BG-15).
   *
   * @return Deliver-to address (BG-15). May be {@code null}.
   */
  @Nullable
  public PhinvoicePostalAddress getPostalAddress ()
  {
    return m_aPostalAddress;
  }

  /**
   * Set the deliver-to address (BG-15).
   *
   * @param aPostalAddress
   *        Address; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceDeliveryInformation setPostalAddress (@Nullable final PhinvoicePostalAddress aPostalAddress)
  {
    m_aPostalAddress = aPostalAddress;
    return this;
  }

  /**
   * Get the role code (BT-X-550).
   *
   * @return Role code (BT-X-550). May be {@code null}.
   */
  @Nullable
  public String getRoleCode ()
  {
    return m_sRoleCode;
  }

  /**
   * Set the role code (BT-X-550).
   *
   * @param sRoleCode
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceDeliveryInformation setRoleCode (@Nullable final String sRoleCode)
  {
    m_sRoleCode = sRoleCode;
    return this;
  }

  /**
   * Get the electronic address (BT-X-160).
   *
   * @return Electronic address (BT-X-160). May be {@code null}.
   */
  @Nullable
  public PhinvoiceIdentifier getElectronicAddress ()
  {
    return m_aElectronicAddress;
  }

  /**
   * Set the electronic address (BT-X-160).
   *
   * @param aElectronicAddress
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceDeliveryInformation setElectronicAddress (@Nullable final PhinvoiceIdentifier aElectronicAddress)
  {
    m_aElectronicAddress = aElectronicAddress;
    return this;
  }

  /**
   * Get the mutable list of tax registrations (BT-X-161).
   *
   * @return Mutable list of tax registrations (BT-X-161). Never {@code null}; empty when none are present.
   */
  @NonNull
  @ReturnsMutableObject
  public ICommonsList <PhinvoiceIdentifier> taxRegistrations ()
  {
    return m_aTaxRegistrations;
  }

  /**
   * Get the legal registration identifier (BG-X-25).
   *
   * @return Legal registration identifier (BG-X-25). May be {@code null}.
   */
  @Nullable
  public PhinvoiceIdentifier getLegalRegistration ()
  {
    return m_aLegalRegistration;
  }

  /**
   * Set the legal registration identifier (BG-X-25).
   *
   * @param aLegalRegistration
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceDeliveryInformation setLegalRegistration (@Nullable final PhinvoiceIdentifier aLegalRegistration)
  {
    m_aLegalRegistration = aLegalRegistration;
    return this;
  }

  /**
   * Get the trading name (BG-X-25).
   *
   * @return Trading name (BG-X-25). May be {@code null}.
   */
  @Nullable
  public String getTradingName ()
  {
    return m_sTradingName;
  }

  /**
   * Set the trading name (BG-X-25).
   *
   * @param sTradingName
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceDeliveryInformation setTradingName (@Nullable final String sTradingName)
  {
    m_sTradingName = sTradingName;
    return this;
  }

  /**
   * Get the legal postal address (BG-X-67).
   *
   * @return Legal postal address (BG-X-67). May be {@code null}.
   */
  @Nullable
  public PhinvoicePostalAddress getLegalAddress ()
  {
    return m_aLegalAddress;
  }

  /**
   * Set the legal postal address (BG-X-67).
   *
   * @param aLegalAddress
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceDeliveryInformation setLegalAddress (@Nullable final PhinvoicePostalAddress aLegalAddress)
  {
    m_aLegalAddress = aLegalAddress;
    return this;
  }

  /**
   * Get the contact (BG-X-26).
   *
   * @return Contact (BG-X-26). May be {@code null}.
   */
  @Nullable
  public PhinvoiceContact getContact ()
  {
    return m_aContact;
  }

  /**
   * Set the contact (BG-X-26).
   *
   * @param aContact
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceDeliveryInformation setContact (@Nullable final PhinvoiceContact aContact)
  {
    m_aContact = aContact;
    return this;
  }

  /**
   * Check if no part of the delivery information is set.
   *
   * @return {@code true} if there is no party name, no location identifier, no delivery date and no
   *         address (or an empty address).
   */
  public boolean isEmpty ()
  {
    return m_sPartyName == null &&
           m_aLocationIdentifier == null &&
           m_aActualDeliveryDate == null &&
           (m_aPostalAddress == null || m_aPostalAddress.isEmpty ()) &&
           m_sRoleCode == null &&
           m_aElectronicAddress == null &&
           m_aTaxRegistrations.isEmpty () &&
           m_aLegalRegistration == null &&
           m_sTradingName == null &&
           m_aLegalAddress == null &&
           m_aContact == null;
  }
}
