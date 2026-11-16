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
 * BG-X-84 Item (Trade Product) Instances of the Factur-X profile EXTENDED.
 *
 * @author Philip Helger
 */
public class PhinvoiceProductInstance
{
  private String m_sBatchID;
  private String m_sSerialID;

  /** Create an empty instance. */
  public PhinvoiceProductInstance ()
  {}

  /**
   * Get the batch (lot) identifier.
   *
   * @return Batch (lot) identifier. May be {@code null}.
   */
  @Nullable
  public String getBatchID ()
  {
    return m_sBatchID;
  }

  /**
   * Set the batch (lot) identifier.
   *
   * @param sBatchID
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceProductInstance setBatchID (@Nullable final String sBatchID)
  {
    m_sBatchID = sBatchID;
    return this;
  }

  /**
   * Get the supplier assigned serial identifier.
   *
   * @return Supplier assigned serial identifier. May be {@code null}.
   */
  @Nullable
  public String getSerialID ()
  {
    return m_sSerialID;
  }

  /**
   * Set the supplier assigned serial identifier.
   *
   * @param sSerialID
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceProductInstance setSerialID (@Nullable final String sSerialID)
  {
    m_sSerialID = sSerialID;
    return this;
  }

  /**
   * Check if no value is set.
   *
   * @return {@code true} when every field is {@code null} or empty.
   */
  public boolean isEmpty ()
  {
    return m_sBatchID == null &&
           m_sSerialID == null;
  }
}
