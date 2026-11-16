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
 * Trade contact details. Shape shared by BG-6 (Seller contact, BT-41/42/43) and BG-9 (Buyer
 * contact, BT-56/57/58). The department name is the Factur-X term BT-41-0 / BT-56-0 (CII only) in
 * EN 16931:2017; in EN 16931:2026 it is the second CII binding of BT-41 / BT-56.
 *
 * @author Philip Helger
 */
public class PhinvoiceContact
{
  private String m_sName;
  private String m_sDepartmentName;
  private String m_sTelephone;
  private String m_sEmail;
  private String m_sFax;
  private String m_sTypeCode;

  /** Create an empty contact. */
  public PhinvoiceContact ()
  {}

  /**
   * Get the contact point name (BT-41 / BT-56).
   *
   * @return Contact point name (BT-41 / BT-56). May be {@code null}.
   */
  @Nullable
  public String getName ()
  {
    return m_sName;
  }

  /**
   * Set the contact point name (BT-41 / BT-56).
   *
   * @param sName
   *        Contact name; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceContact setName (@Nullable final String sName)
  {
    m_sName = sName;
    return this;
  }

  /**
   * Get the department name of the contact point (BT-41-0 / BT-56-0, CII only).
   *
   * @return Department name (BT-41-0 / BT-56-0). May be {@code null}.
   */
  @Nullable
  public String getDepartmentName ()
  {
    return m_sDepartmentName;
  }

  /**
   * Set the department name of the contact point (BT-41-0 / BT-56-0, CII only).
   *
   * @param sDepartmentName
   *        Department name; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceContact setDepartmentName (@Nullable final String sDepartmentName)
  {
    m_sDepartmentName = sDepartmentName;
    return this;
  }

  /**
   * Get the telephone number (BT-42 / BT-57).
   *
   * @return Telephone number (BT-42 / BT-57). May be {@code null}.
   */
  @Nullable
  public String getTelephone ()
  {
    return m_sTelephone;
  }

  /**
   * Set the telephone number (BT-42 / BT-57).
   *
   * @param sTelephone
   *        Telephone string; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceContact setTelephone (@Nullable final String sTelephone)
  {
    m_sTelephone = sTelephone;
    return this;
  }

  /**
   * Get the email address (BT-43 / BT-58).
   *
   * @return Email address (BT-43 / BT-58). May be {@code null}.
   */
  @Nullable
  public String getEmail ()
  {
    return m_sEmail;
  }

  /**
   * Set the email address (BT-43 / BT-58).
   *
   * @param sEmail
   *        Email; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceContact setEmail (@Nullable final String sEmail)
  {
    m_sEmail = sEmail;
    return this;
  }

  /**
   * Get the fax number (BT-X-107 / BT-X-115).
   *
   * @return Fax number (BT-X-107 / BT-X-115). May be {@code null}.
   */
  @Nullable
  public String getFax ()
  {
    return m_sFax;
  }

  /**
   * Set the fax number (BT-X-107 / BT-X-115).
   *
   * @param sFax
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceContact setFax (@Nullable final String sFax)
  {
    m_sFax = sFax;
    return this;
  }

  /**
   * Get the contact type code (BT-X-317 / BT-X-318).
   *
   * @return Contact type code (BT-X-317 / BT-X-318). May be {@code null}.
   */
  @Nullable
  public String getTypeCode ()
  {
    return m_sTypeCode;
  }

  /**
   * Set the contact type code (BT-X-317 / BT-X-318).
   *
   * @param sTypeCode
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceContact setTypeCode (@Nullable final String sTypeCode)
  {
    m_sTypeCode = sTypeCode;
    return this;
  }

  /**
   * Get the {@code true} when every field is {@code null}.
   *
   * @return {@code true} when every field is {@code null}.
   */
  public boolean isEmpty ()
  {
    return m_sName == null && m_sDepartmentName == null && m_sTelephone == null && m_sEmail == null &&
           m_sFax == null &&
           m_sTypeCode == null;
  }
}
