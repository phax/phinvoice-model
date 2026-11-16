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
 * BG-31 ITEM INFORMATION (1..1 on a line) plus nested BG-32 (item attributes). Carries
 * BT-153 (item name), BT-154 (item description), BT-155 (seller's item identifier), BT-156
 * (buyer's item identifier), BT-157/BT-157-1 (standard item identifier and scheme),
 * BT-158/BT-158-1/BT-158-2 (classifications) and BT-159 (country of origin).
 *
 * @author Philip Helger
 */
public class PhinvoiceItem
{
  private String m_sName;
  private String m_sDescription;
  private String m_sSellerItemID;
  private String m_sBuyerItemID;
  private PhinvoiceIdentifier m_aStandardItemID;
  private final ICommonsList <PhinvoiceItemClassification> m_aClassifications = new CommonsArrayList <> ();
  private String m_sCountryOfOrigin;
  private final ICommonsList <PhinvoiceItemAttribute> m_aAttributes = new CommonsArrayList <> ();
  private String m_sGoodsServicesCode;
  private String m_sID;
  private String m_sIndustryAssignedID;
  private String m_sModelID;
  private String m_sBatchID;
  private String m_sBrandName;
  private String m_sModelName;
  private PhinvoiceTradeParty m_aManufacturer;
  private final ICommonsList <PhinvoiceProductInstance> m_aProductInstances = new CommonsArrayList <> ();
  private final ICommonsList <PhinvoiceIncludedProduct> m_aIncludedProducts = new CommonsArrayList <> ();

  /** Create an empty item. */
  public PhinvoiceItem ()
  {}

  /**
   * Get the item name (BT-153).
   *
   * @return Item name (BT-153). May be {@code null}.
   */
  @Nullable
  public String getName ()
  {
    return m_sName;
  }

  /**
   * Set the item name (BT-153).
   *
   * @param sName
   *        Name; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceItem setName (@Nullable final String sName)
  {
    m_sName = sName;
    return this;
  }

  /**
   * Get the item description (BT-154).
   *
   * @return Item description (BT-154). May be {@code null}.
   */
  @Nullable
  public String getDescription ()
  {
    return m_sDescription;
  }

  /**
   * Set the item description (BT-154).
   *
   * @param sDescription
   *        Description; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceItem setDescription (@Nullable final String sDescription)
  {
    m_sDescription = sDescription;
    return this;
  }

  /**
   * Get the item seller's identifier (BT-155).
   *
   * @return Item seller's identifier (BT-155). May be {@code null}.
   */
  @Nullable
  public String getSellerItemID ()
  {
    return m_sSellerItemID;
  }

  /**
   * Set the item seller's identifier (BT-155).
   *
   * @param sSellerItemID
   *        Seller's item identifier; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceItem setSellerItemID (@Nullable final String sSellerItemID)
  {
    m_sSellerItemID = sSellerItemID;
    return this;
  }

  /**
   * Get the item buyer's identifier (BT-156).
   *
   * @return Item buyer's identifier (BT-156). May be {@code null}.
   */
  @Nullable
  public String getBuyerItemID ()
  {
    return m_sBuyerItemID;
  }

  /**
   * Set the item buyer's identifier (BT-156).
   *
   * @param sBuyerItemID
   *        Buyer's item identifier; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceItem setBuyerItemID (@Nullable final String sBuyerItemID)
  {
    m_sBuyerItemID = sBuyerItemID;
    return this;
  }

  /**
   * Get the item standard identifier (BT-157 with BT-157-1 scheme ID).
   *
   * @return Item standard identifier (BT-157 with BT-157-1 scheme ID). May be {@code null}.
   */
  @Nullable
  public PhinvoiceIdentifier getStandardItemID ()
  {
    return m_aStandardItemID;
  }

  /**
   * Set the item standard identifier (BT-157 / BT-157-1).
   *
   * @param aStandardItemID
   *        Identifier; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceItem setStandardItemID (@Nullable final PhinvoiceIdentifier aStandardItemID)
  {
    m_aStandardItemID = aStandardItemID;
    return this;
  }

  /**
   * Get the mutable list of item classifications (BT-158 / BT-158-1 / BT-158-2).
   *
   * @return Mutable list of item classifications (BT-158 / BT-158-1 / BT-158-2). Never
   *         {@code null}; empty when none are present.
   */
  @NonNull
  @ReturnsMutableObject
  public ICommonsList <PhinvoiceItemClassification> classifications ()
  {
    return m_aClassifications;
  }

  /**
   * Get the item country of origin (BT-159) — ISO 3166-1 alpha-2. May be {@code null}.
   *
   * @return Item country of origin (BT-159) — ISO 3166-1 alpha-2. May be {@code null}.
   */
  @Nullable
  public String getCountryOfOrigin ()
  {
    return m_sCountryOfOrigin;
  }

  /**
   * Set the item country of origin (BT-159).
   *
   * @param sCountryOfOrigin
   *        Country code; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceItem setCountryOfOrigin (@Nullable final String sCountryOfOrigin)
  {
    m_sCountryOfOrigin = sCountryOfOrigin;
    return this;
  }

  /**
   * Get the mutable list of item attributes (BG-32 — BT-160 / BT-161).
   *
   * @return Mutable list of item attributes (BG-32 — BT-160 / BT-161). Never {@code null};
   *         empty when none are present.
   */
  @NonNull
  @ReturnsMutableObject
  public ICommonsList <PhinvoiceItemAttribute> attributes ()
  {
    return m_aAttributes;
  }

  /**
   * Get the goods/services code (BT-196).
   *
   * @return Goods/services code (BT-196). May be {@code null}.
   */
  @Nullable
  public String getGoodsServicesCode ()
  {
    return m_sGoodsServicesCode;
  }

  /**
   * Set the goods/services code (BT-196).
   *
   * @param sGoodsServicesCode
   *        Goods/services code; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceItem setGoodsServicesCode (@Nullable final String sGoodsServicesCode)
  {
    m_sGoodsServicesCode = sGoodsServicesCode;
    return this;
  }

  /**
   * Get the item identifier (BT-X-305).
   *
   * @return Item identifier (BT-X-305). May be {@code null}.
   */
  @Nullable
  public String getID ()
  {
    return m_sID;
  }

  /**
   * Set the item identifier (BT-X-305).
   *
   * @param sID
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceItem setID (@Nullable final String sID)
  {
    m_sID = sID;
    return this;
  }

  /**
   * Get the industry assigned identifier (BT-X-532).
   *
   * @return Industry assigned identifier (BT-X-532). May be {@code null}.
   */
  @Nullable
  public String getIndustryAssignedID ()
  {
    return m_sIndustryAssignedID;
  }

  /**
   * Set the industry assigned identifier (BT-X-532).
   *
   * @param sIndustryAssignedID
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceItem setIndustryAssignedID (@Nullable final String sIndustryAssignedID)
  {
    m_sIndustryAssignedID = sIndustryAssignedID;
    return this;
  }

  /**
   * Get the model identifier (BT-X-533).
   *
   * @return Model identifier (BT-X-533). May be {@code null}.
   */
  @Nullable
  public String getModelID ()
  {
    return m_sModelID;
  }

  /**
   * Set the model identifier (BT-X-533).
   *
   * @param sModelID
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceItem setModelID (@Nullable final String sModelID)
  {
    m_sModelID = sModelID;
    return this;
  }

  /**
   * Get the batch identifier (BT-X-534).
   *
   * @return Batch identifier (BT-X-534). May be {@code null}.
   */
  @Nullable
  public String getBatchID ()
  {
    return m_sBatchID;
  }

  /**
   * Set the batch identifier (BT-X-534).
   *
   * @param sBatchID
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceItem setBatchID (@Nullable final String sBatchID)
  {
    m_sBatchID = sBatchID;
    return this;
  }

  /**
   * Get the brand name (BT-X-535).
   *
   * @return Brand name (BT-X-535). May be {@code null}.
   */
  @Nullable
  public String getBrandName ()
  {
    return m_sBrandName;
  }

  /**
   * Set the brand name (BT-X-535).
   *
   * @param sBrandName
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceItem setBrandName (@Nullable final String sBrandName)
  {
    m_sBrandName = sBrandName;
    return this;
  }

  /**
   * Get the model name (BT-X-536).
   *
   * @return Model name (BT-X-536). May be {@code null}.
   */
  @Nullable
  public String getModelName ()
  {
    return m_sModelName;
  }

  /**
   * Set the model name (BT-X-536).
   *
   * @param sModelName
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceItem setModelName (@Nullable final String sModelName)
  {
    m_sModelName = sModelName;
    return this;
  }

  /**
   * Get the manufacturer (BG-X-93).
   *
   * @return Manufacturer (BG-X-93). May be {@code null}.
   */
  @Nullable
  public PhinvoiceTradeParty getManufacturer ()
  {
    return m_aManufacturer;
  }

  /**
   * Set the manufacturer (BG-X-93).
   *
   * @param aManufacturer
   *        The new value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceItem setManufacturer (@Nullable final PhinvoiceTradeParty aManufacturer)
  {
    m_aManufacturer = aManufacturer;
    return this;
  }

  /**
   * Get the mutable list of product instances (BG-X-84).
   *
   * @return Mutable list of product instances (BG-X-84). Never {@code null}; empty when none are present.
   */
  @NonNull
  @ReturnsMutableObject
  public ICommonsList <PhinvoiceProductInstance> productInstances ()
  {
    return m_aProductInstances;
  }

  /**
   * Get the mutable list of included items (BG-X-1).
   *
   * @return Mutable list of included items (BG-X-1). Never {@code null}; empty when none are present.
   */
  @NonNull
  @ReturnsMutableObject
  public ICommonsList <PhinvoiceIncludedProduct> includedProducts ()
  {
    return m_aIncludedProducts;
  }
}
