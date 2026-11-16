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

import java.math.BigDecimal;
import java.time.LocalDate;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * BG-X-41 TAX CURRENCY EXCHANGE of the Factur-X profile EXTENDED.
 *
 * @author Philip Helger
 */
public class PhinvoiceTaxCurrencyExchange
{
  private String m_sSourceCurrency;
  private String m_sTargetCurrency;
  private BigDecimal m_aRate;
  private LocalDate m_aRateDate;

  /** Create an empty instance. */
  public PhinvoiceTaxCurrencyExchange ()
  {}

  /**
   * Get the source currency code.
   *
   * @return Source currency code. May be {@code null}.
   */
  @Nullable
  public String getSourceCurrency ()
  {
    return m_sSourceCurrency;
  }

  /**
   * Set the source currency code.
   *
   * @param sSourceCurrency
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceTaxCurrencyExchange setSourceCurrency (@Nullable final String sSourceCurrency)
  {
    m_sSourceCurrency = sSourceCurrency;
    return this;
  }

  /**
   * Get the target currency code.
   *
   * @return Target currency code. May be {@code null}.
   */
  @Nullable
  public String getTargetCurrency ()
  {
    return m_sTargetCurrency;
  }

  /**
   * Set the target currency code.
   *
   * @param sTargetCurrency
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceTaxCurrencyExchange setTargetCurrency (@Nullable final String sTargetCurrency)
  {
    m_sTargetCurrency = sTargetCurrency;
    return this;
  }

  /**
   * Get the conversion rate.
   *
   * @return Conversion rate. May be {@code null}.
   */
  @Nullable
  public BigDecimal getRate ()
  {
    return m_aRate;
  }

  /**
   * Set the conversion rate.
   *
   * @param aRate
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceTaxCurrencyExchange setRate (@Nullable final BigDecimal aRate)
  {
    m_aRate = aRate;
    return this;
  }

  /**
   * Get the date of the conversion rate.
   *
   * @return Date of the conversion rate. May be {@code null}.
   */
  @Nullable
  public LocalDate getRateDate ()
  {
    return m_aRateDate;
  }

  /**
   * Set the date of the conversion rate.
   *
   * @param aRateDate
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceTaxCurrencyExchange setRateDate (@Nullable final LocalDate aRateDate)
  {
    m_aRateDate = aRateDate;
    return this;
  }

  /**
   * Check if no value is set.
   *
   * @return {@code true} when every field is {@code null} or empty.
   */
  public boolean isEmpty ()
  {
    return m_sSourceCurrency == null &&
           m_sTargetCurrency == null &&
           m_aRate == null &&
           m_aRateDate == null;
  }
}
