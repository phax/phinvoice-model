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

/**
 * APPLICABLE TRADE DELIVERY TERMS of the Factur-X profile EXTENDED, on document level (BG-X-22 with
 * BG-X-88) or on line level (BG-X-87 with BG-X-89).
 *
 * @author Philip Helger
 */
public class PhinvoiceDeliveryTerms
{
  private String m_sDeliveryTypeCode;
  private String m_sLocationCountryCode;
  private String m_sLocationName;

  /** Create an empty instance. */
  public PhinvoiceDeliveryTerms ()
  {}

  /**
   * Get the delivery type code (Incoterms).
   *
   * @return Delivery type code (Incoterms). May be {@code null}.
   */
  @Nullable
  public String getDeliveryTypeCode ()
  {
    return m_sDeliveryTypeCode;
  }

  /**
   * Set the delivery type code (Incoterms).
   *
   * @param sDeliveryTypeCode
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceDeliveryTerms setDeliveryTypeCode (@Nullable final String sDeliveryTypeCode)
  {
    m_sDeliveryTypeCode = sDeliveryTypeCode;
    return this;
  }

  /**
   * Get the country code of the relevant location.
   *
   * @return Country code of the relevant location. May be {@code null}.
   */
  @Nullable
  public String getLocationCountryCode ()
  {
    return m_sLocationCountryCode;
  }

  /**
   * Set the country code of the relevant location.
   *
   * @param sLocationCountryCode
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceDeliveryTerms setLocationCountryCode (@Nullable final String sLocationCountryCode)
  {
    m_sLocationCountryCode = sLocationCountryCode;
    return this;
  }

  /**
   * Get the name of the relevant location.
   *
   * @return Name of the relevant location. May be {@code null}.
   */
  @Nullable
  public String getLocationName ()
  {
    return m_sLocationName;
  }

  /**
   * Set the name of the relevant location.
   *
   * @param sLocationName
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceDeliveryTerms setLocationName (@Nullable final String sLocationName)
  {
    m_sLocationName = sLocationName;
    return this;
  }

  /**
   * Check if no value is set.
   *
   * @return {@code true} when every field is {@code null} or empty.
   */
  public boolean isEmpty ()
  {
    return m_sDeliveryTypeCode == null &&
           m_sLocationCountryCode == null &&
           m_sLocationName == null;
  }
}
