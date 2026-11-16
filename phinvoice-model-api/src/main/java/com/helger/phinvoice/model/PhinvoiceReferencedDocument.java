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
 * A referenced document of the Factur-X profile EXTENDED, e.g. a quotation, an ultimate customer order or
 * an additional document on line level. The term IDs of the values depend on the position.
 *
 * @author Philip Helger
 */
public class PhinvoiceReferencedDocument
{
  private String m_sIssuerAssignedID;
  private String m_sLineID;
  private String m_sTypeCode;
  private String m_sReferenceTypeCode;
  private String m_sName;
  private String m_sUri;
  private LocalDate m_aIssueDate;
  private PhinvoiceAttachment m_aAttachment;

  /** Create an empty instance. */
  public PhinvoiceReferencedDocument ()
  {}

  /**
   * Get the document reference.
   *
   * @return Document reference. May be {@code null}.
   */
  @Nullable
  public String getIssuerAssignedID ()
  {
    return m_sIssuerAssignedID;
  }

  /**
   * Set the document reference.
   *
   * @param sIssuerAssignedID
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceReferencedDocument setIssuerAssignedID (@Nullable final String sIssuerAssignedID)
  {
    m_sIssuerAssignedID = sIssuerAssignedID;
    return this;
  }

  /**
   * Get the referenced line.
   *
   * @return Referenced line. May be {@code null}.
   */
  @Nullable
  public String getLineID ()
  {
    return m_sLineID;
  }

  /**
   * Set the referenced line.
   *
   * @param sLineID
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceReferencedDocument setLineID (@Nullable final String sLineID)
  {
    m_sLineID = sLineID;
    return this;
  }

  /**
   * Get the document type code.
   *
   * @return Document type code. May be {@code null}.
   */
  @Nullable
  public String getTypeCode ()
  {
    return m_sTypeCode;
  }

  /**
   * Set the document type code.
   *
   * @param sTypeCode
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceReferencedDocument setTypeCode (@Nullable final String sTypeCode)
  {
    m_sTypeCode = sTypeCode;
    return this;
  }

  /**
   * Get the reference type code.
   *
   * @return Reference type code. May be {@code null}.
   */
  @Nullable
  public String getReferenceTypeCode ()
  {
    return m_sReferenceTypeCode;
  }

  /**
   * Set the reference type code.
   *
   * @param sReferenceTypeCode
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceReferencedDocument setReferenceTypeCode (@Nullable final String sReferenceTypeCode)
  {
    m_sReferenceTypeCode = sReferenceTypeCode;
    return this;
  }

  /**
   * Get the document description.
   *
   * @return Document description. May be {@code null}.
   */
  @Nullable
  public String getName ()
  {
    return m_sName;
  }

  /**
   * Set the document description.
   *
   * @param sName
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceReferencedDocument setName (@Nullable final String sName)
  {
    m_sName = sName;
    return this;
  }

  /**
   * Get the external document location.
   *
   * @return External document location. May be {@code null}.
   */
  @Nullable
  public String getUri ()
  {
    return m_sUri;
  }

  /**
   * Set the external document location.
   *
   * @param sUri
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceReferencedDocument setUri (@Nullable final String sUri)
  {
    m_sUri = sUri;
    return this;
  }

  /**
   * Get the document date.
   *
   * @return Document date. May be {@code null}.
   */
  @Nullable
  public LocalDate getIssueDate ()
  {
    return m_aIssueDate;
  }

  /**
   * Set the document date.
   *
   * @param aIssueDate
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceReferencedDocument setIssueDate (@Nullable final LocalDate aIssueDate)
  {
    m_aIssueDate = aIssueDate;
    return this;
  }

  /**
   * Get the attached document.
   *
   * @return Attached document. May be {@code null}.
   */
  @Nullable
  public PhinvoiceAttachment getAttachment ()
  {
    return m_aAttachment;
  }

  /**
   * Set the attached document.
   *
   * @param aAttachment
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceReferencedDocument setAttachment (@Nullable final PhinvoiceAttachment aAttachment)
  {
    m_aAttachment = aAttachment;
    return this;
  }

  /**
   * Check if no value is set.
   *
   * @return {@code true} when every field is {@code null} or empty.
   */
  public boolean isEmpty ()
  {
    return m_sIssuerAssignedID == null &&
           m_sLineID == null &&
           m_sTypeCode == null &&
           m_sReferenceTypeCode == null &&
           m_sName == null &&
           m_sUri == null &&
           m_aIssueDate == null &&
           m_aAttachment == null;
  }
}
