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
 * BG-37 INVOICE LINE DELIVERY INFORMATION (0..1 on a line) plus nested BG-38 (line deliver-to
 * address). Line-level equivalent of BG-13: deliver-to party name (BT-185), deliver-to location
 * identifier with scheme (BT-186 / BT-186-1), actual delivery date (BT-187) and the deliver-to
 * address (BT-203 to BT-209, see {@link PhinvoicePostalAddress}). Introduced by EN 16931:2026. The
 * line period (BG-26) is nested under BG-37 in the 2026 data model but stays on
 * {@link PhinvoiceInvoiceLine#getPeriod()} here.
 *
 * @author Philip Helger
 */
public class PhinvoiceLineDelivery
{
  private String m_sPartyName;
  private PhinvoiceIdentifier m_aLocationIdentifier;
  private LocalDate m_aActualDeliveryDate;
  private PhinvoicePostalAddress m_aPostalAddress;
  private String m_sPartyID;
  private String m_sRoleCode;
  private PhinvoiceIdentifier m_aLegalRegistration;
  private String m_sTradingName;
  private PhinvoiceContact m_aContact;
  private PhinvoiceIdentifier m_aElectronicAddress;
  private final ICommonsList <PhinvoiceIdentifier> m_aTaxRegistrations = new CommonsArrayList <> ();

  /** Create an empty instance. */
  public PhinvoiceLineDelivery ()
  {}

  /**
   * Get the invoice line deliver to party name (BT-185).
   *
   * @return Invoice line deliver to party name (BT-185). May be {@code null}.
   */
  @Nullable
  public String getPartyName ()
  {
    return m_sPartyName;
  }

  /**
   * Set the invoice line deliver to party name (BT-185).
   *
   * @param sPartyName
   *        Invoice line deliver to party name; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceLineDelivery setPartyName (@Nullable final String sPartyName)
  {
    m_sPartyName = sPartyName;
    return this;
  }

  /**
   * Get the invoice line deliver to location identifier (BT-186 / BT-186-1).
   *
   * @return Invoice line deliver to location identifier (BT-186 / BT-186-1). May be {@code null}.
   */
  @Nullable
  public PhinvoiceIdentifier getLocationIdentifier ()
  {
    return m_aLocationIdentifier;
  }

  /**
   * Set the invoice line deliver to location identifier (BT-186 / BT-186-1).
   *
   * @param aLocationIdentifier
   *        Invoice line deliver to location identifier; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceLineDelivery setLocationIdentifier (@Nullable final PhinvoiceIdentifier aLocationIdentifier)
  {
    m_aLocationIdentifier = aLocationIdentifier;
    return this;
  }

  /**
   * Get the invoice line actual delivery date (BT-187).
   *
   * @return Invoice line actual delivery date (BT-187). May be {@code null}.
   */
  @Nullable
  public LocalDate getActualDeliveryDate ()
  {
    return m_aActualDeliveryDate;
  }

  /**
   * Set the invoice line actual delivery date (BT-187).
   *
   * @param aActualDeliveryDate
   *        Invoice line actual delivery date; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceLineDelivery setActualDeliveryDate (@Nullable final LocalDate aActualDeliveryDate)
  {
    m_aActualDeliveryDate = aActualDeliveryDate;
    return this;
  }

  /**
   * Get the invoice line deliver to address (BG-38).
   *
   * @return Invoice line deliver to address (BG-38). May be {@code null}.
   */
  @Nullable
  public PhinvoicePostalAddress getPostalAddress ()
  {
    return m_aPostalAddress;
  }

  /**
   * Set the invoice line deliver to address (BG-38).
   *
   * @param aPostalAddress
   *        Invoice line deliver to address; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceLineDelivery setPostalAddress (@Nullable final PhinvoicePostalAddress aPostalAddress)
  {
    m_aPostalAddress = aPostalAddress;
    return this;
  }

  /**
   * Get the party identifier.
   *
   * @return Party identifier. May be {@code null}.
   */
  @Nullable
  public String getPartyID ()
  {
    return m_sPartyID;
  }

  /**
   * Set the party identifier.
   *
   * @param sPartyID
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceLineDelivery setPartyID (@Nullable final String sPartyID)
  {
    m_sPartyID = sPartyID;
    return this;
  }

  /**
   * Get the role code (BT-X-541).
   *
   * @return Role code (BT-X-541). May be {@code null}.
   */
  @Nullable
  public String getRoleCode ()
  {
    return m_sRoleCode;
  }

  /**
   * Set the role code (BT-X-541).
   *
   * @param sRoleCode
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceLineDelivery setRoleCode (@Nullable final String sRoleCode)
  {
    m_sRoleCode = sRoleCode;
    return this;
  }

  /**
   * Get the legal registration identifier (BT-X-51).
   *
   * @return Legal registration identifier (BT-X-51). May be {@code null}.
   */
  @Nullable
  public PhinvoiceIdentifier getLegalRegistration ()
  {
    return m_aLegalRegistration;
  }

  /**
   * Set the legal registration identifier (BT-X-51).
   *
   * @param aLegalRegistration
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceLineDelivery setLegalRegistration (@Nullable final PhinvoiceIdentifier aLegalRegistration)
  {
    m_aLegalRegistration = aLegalRegistration;
    return this;
  }

  /**
   * Get the trading name (BT-X-52).
   *
   * @return Trading name (BT-X-52). May be {@code null}.
   */
  @Nullable
  public String getTradingName ()
  {
    return m_sTradingName;
  }

  /**
   * Set the trading name (BT-X-52).
   *
   * @param sTradingName
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceLineDelivery setTradingName (@Nullable final String sTradingName)
  {
    m_sTradingName = sTradingName;
    return this;
  }

  /**
   * Get the contact (BG-X-8).
   *
   * @return Contact (BG-X-8). May be {@code null}.
   */
  @Nullable
  public PhinvoiceContact getContact ()
  {
    return m_aContact;
  }

  /**
   * Set the contact (BG-X-8).
   *
   * @param aContact
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceLineDelivery setContact (@Nullable final PhinvoiceContact aContact)
  {
    m_aContact = aContact;
    return this;
  }

  /**
   * Get the electronic address (BT-X-65).
   *
   * @return Electronic address (BT-X-65). May be {@code null}.
   */
  @Nullable
  public PhinvoiceIdentifier getElectronicAddress ()
  {
    return m_aElectronicAddress;
  }

  /**
   * Set the electronic address (BT-X-65).
   *
   * @param aElectronicAddress
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceLineDelivery setElectronicAddress (@Nullable final PhinvoiceIdentifier aElectronicAddress)
  {
    m_aElectronicAddress = aElectronicAddress;
    return this;
  }

  /**
   * Get the mutable list of tax registrations (BT-X-66).
   *
   * @return Mutable list of tax registrations (BT-X-66). Never {@code null}; empty when none are present.
   */
  @NonNull
  @ReturnsMutableObject
  public ICommonsList <PhinvoiceIdentifier> taxRegistrations ()
  {
    return m_aTaxRegistrations;
  }

  /**
   * Check if no part of the line delivery information is set.
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
           m_sPartyID == null &&
           m_sRoleCode == null &&
           m_aLegalRegistration == null &&
           m_sTradingName == null &&
           m_aContact == null &&
           m_aElectronicAddress == null &&
           m_aTaxRegistrations.isEmpty ();
  }
}
