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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;

/**
 * BG-X-1 Included Item of the Factur-X profile EXTENDED.
 *
 * @author Philip Helger
 */
public class PhinvoiceIncludedProduct
{
  private String m_sID;
  private final ICommonsList <PhinvoiceIdentifier> m_aGlobalIdentifiers = new CommonsArrayList <> ();
  private String m_sSellerAssignedID;
  private String m_sBuyerAssignedID;
  private String m_sIndustryAssignedID;
  private String m_sName;
  private String m_sDescription;
  private BigDecimal m_aUnitQuantity;
  private String m_sUnitQuantityUnit;

  /** Create an empty instance. */
  public PhinvoiceIncludedProduct ()
  {}

  /**
   * Get the identifier.
   *
   * @return Identifier. May be {@code null}.
   */
  @Nullable
  public String getID ()
  {
    return m_sID;
  }

  /**
   * Set the identifier.
   *
   * @param sID
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceIncludedProduct setID (@Nullable final String sID)
  {
    m_sID = sID;
    return this;
  }

  /**
   * Get the mutable list of global identifiers with their scheme.
   *
   * @return Mutable list of global identifiers with their scheme. Never {@code null}; empty when none are present.
   */
  @NonNull
  @ReturnsMutableObject
  public ICommonsList <PhinvoiceIdentifier> globalIdentifiers ()
  {
    return m_aGlobalIdentifiers;
  }

  /**
   * Get the seller assigned identifier.
   *
   * @return Seller assigned identifier. May be {@code null}.
   */
  @Nullable
  public String getSellerAssignedID ()
  {
    return m_sSellerAssignedID;
  }

  /**
   * Set the seller assigned identifier.
   *
   * @param sSellerAssignedID
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceIncludedProduct setSellerAssignedID (@Nullable final String sSellerAssignedID)
  {
    m_sSellerAssignedID = sSellerAssignedID;
    return this;
  }

  /**
   * Get the buyer assigned identifier.
   *
   * @return Buyer assigned identifier. May be {@code null}.
   */
  @Nullable
  public String getBuyerAssignedID ()
  {
    return m_sBuyerAssignedID;
  }

  /**
   * Set the buyer assigned identifier.
   *
   * @param sBuyerAssignedID
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceIncludedProduct setBuyerAssignedID (@Nullable final String sBuyerAssignedID)
  {
    m_sBuyerAssignedID = sBuyerAssignedID;
    return this;
  }

  /**
   * Get the industry assigned identifier.
   *
   * @return Industry assigned identifier. May be {@code null}.
   */
  @Nullable
  public String getIndustryAssignedID ()
  {
    return m_sIndustryAssignedID;
  }

  /**
   * Set the industry assigned identifier.
   *
   * @param sIndustryAssignedID
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceIncludedProduct setIndustryAssignedID (@Nullable final String sIndustryAssignedID)
  {
    m_sIndustryAssignedID = sIndustryAssignedID;
    return this;
  }

  /**
   * Get the name.
   *
   * @return Name. May be {@code null}.
   */
  @Nullable
  public String getName ()
  {
    return m_sName;
  }

  /**
   * Set the name.
   *
   * @param sName
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceIncludedProduct setName (@Nullable final String sName)
  {
    m_sName = sName;
    return this;
  }

  /**
   * Get the description.
   *
   * @return Description. May be {@code null}.
   */
  @Nullable
  public String getDescription ()
  {
    return m_sDescription;
  }

  /**
   * Set the description.
   *
   * @param sDescription
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceIncludedProduct setDescription (@Nullable final String sDescription)
  {
    m_sDescription = sDescription;
    return this;
  }

  /**
   * Get the quantity per unit.
   *
   * @return Quantity per unit. May be {@code null}.
   */
  @Nullable
  public BigDecimal getUnitQuantity ()
  {
    return m_aUnitQuantity;
  }

  /**
   * Set the quantity per unit.
   *
   * @param aUnitQuantity
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceIncludedProduct setUnitQuantity (@Nullable final BigDecimal aUnitQuantity)
  {
    m_aUnitQuantity = aUnitQuantity;
    return this;
  }

  /**
   * Get the unit of measure of the quantity per unit.
   *
   * @return Unit of measure of the quantity per unit. May be {@code null}.
   */
  @Nullable
  public String getUnitQuantityUnit ()
  {
    return m_sUnitQuantityUnit;
  }

  /**
   * Set the unit of measure of the quantity per unit.
   *
   * @param sUnitQuantityUnit
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceIncludedProduct setUnitQuantityUnit (@Nullable final String sUnitQuantityUnit)
  {
    m_sUnitQuantityUnit = sUnitQuantityUnit;
    return this;
  }

  /**
   * Check if no value is set.
   *
   * @return {@code true} when every field is {@code null} or empty.
   */
  public boolean isEmpty ()
  {
    return m_sID == null &&
           m_aGlobalIdentifiers.isEmpty () &&
           m_sSellerAssignedID == null &&
           m_sBuyerAssignedID == null &&
           m_sIndustryAssignedID == null &&
           m_sName == null &&
           m_sDescription == null &&
           m_aUnitQuantity == null &&
           m_sUnitQuantityUnit == null;
  }
}
