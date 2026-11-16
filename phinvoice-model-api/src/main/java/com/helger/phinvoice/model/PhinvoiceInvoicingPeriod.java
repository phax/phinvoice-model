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

/**
 * BG-14 INVOICING PERIOD (also reused for BG-26 invoice-line period). Carries the start date
 * (BT-73 / BT-134) and end date (BT-74 / BT-135).
 *
 * @author Philip Helger
 */
public class PhinvoiceInvoicingPeriod
{
  private LocalDate m_aStartDate;
  private LocalDate m_aEndDate;
  private String m_sDescription;

  /** Create an empty period. */
  public PhinvoiceInvoicingPeriod ()
  {}

  /**
   * Get the period start date (BT-73 / BT-134).
   *
   * @return Period start date (BT-73 / BT-134). May be {@code null}.
   */
  @Nullable
  public LocalDate getStartDate ()
  {
    return m_aStartDate;
  }

  /**
   * Set the period start date (BT-73 / BT-134).
   *
   * @param aStartDate
   *        Start date; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoicingPeriod setStartDate (@Nullable final LocalDate aStartDate)
  {
    m_aStartDate = aStartDate;
    return this;
  }

  /**
   * Get the period end date (BT-74 / BT-135).
   *
   * @return Period end date (BT-74 / BT-135). May be {@code null}.
   */
  @Nullable
  public LocalDate getEndDate ()
  {
    return m_aEndDate;
  }

  /**
   * Set the period end date (BT-74 / BT-135).
   *
   * @param aEndDate
   *        End date; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoicingPeriod setEndDate (@Nullable final LocalDate aEndDate)
  {
    m_aEndDate = aEndDate;
    return this;
  }

  /**
   * Get the period description (BT-X-264).
   *
   * @return Period description (BT-X-264). May be {@code null}.
   */
  @Nullable
  public String getDescription ()
  {
    return m_sDescription;
  }

  /**
   * Set the period description (BT-X-264).
   *
   * @param sDescription
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceInvoicingPeriod setDescription (@Nullable final String sDescription)
  {
    m_sDescription = sDescription;
    return this;
  }

  /**
   * Get the {@code true} when both dates are {@code null}.
   *
   * @return {@code true} when both dates are {@code null}.
   */
  public boolean isEmpty ()
  {
    return m_aStartDate == null && m_aEndDate == null &&
           m_sDescription == null;
  }
}
