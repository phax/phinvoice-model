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

/**
 * BG-32 ITEM ATTRIBUTE (0..n on an item). Carries BT-160 (item attribute name, optional since EN
 * 16931:2026), BT-161 (item attribute value) and, since EN 16931:2026, BT-211 (item attribute
 * code) and BT-212 (unit of measure code of a numeric value). The text value (BT-161a) and the
 * numeric value (BT-161b, CII <code>ram:ValueMeasure</code>) are kept apart.
 *
 * @author Philip Helger
 */
public class PhinvoiceItemAttribute
{
  private String m_sName;
  private String m_sValue;
  private String m_sCode;
  private BigDecimal m_aNumericValue;
  private String m_sValueUnit;

  /** Create an empty item attribute. */
  public PhinvoiceItemAttribute ()
  {}

  /**
   * Create an item attribute with name and value set.
   *
   * @param sName
   *        Attribute name (BT-160); may be {@code null}.
   * @param sValue
   *        Attribute value (BT-161); may be {@code null}.
   */
  public PhinvoiceItemAttribute (@Nullable final String sName, @Nullable final String sValue)
  {
    m_sName = sName;
    m_sValue = sValue;
  }

  /**
   * Get the item attribute name (BT-160).
   *
   * @return Item attribute name (BT-160). May be {@code null}.
   */
  @Nullable
  public String getName ()
  {
    return m_sName;
  }

  /**
   * Set the item attribute name (BT-160).
   *
   * @param sName
   *        Attribute name; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceItemAttribute setName (@Nullable final String sName)
  {
    m_sName = sName;
    return this;
  }

  /**
   * Get the item attribute value (BT-161).
   *
   * @return Item attribute value (BT-161). May be {@code null}.
   */
  @Nullable
  public String getValue ()
  {
    return m_sValue;
  }

  /**
   * Set the item attribute value (BT-161).
   *
   * @param sValue
   *        Attribute value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceItemAttribute setValue (@Nullable final String sValue)
  {
    m_sValue = sValue;
    return this;
  }

  /**
   * Get the item attribute code (BT-211).
   *
   * @return Item attribute code (BT-211). May be {@code null}.
   */
  @Nullable
  public String getCode ()
  {
    return m_sCode;
  }

  /**
   * Set the item attribute code (BT-211).
   *
   * @param sCode
   *        Item attribute code; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceItemAttribute setCode (@Nullable final String sCode)
  {
    m_sCode = sCode;
    return this;
  }

  /**
   * Get the numeric item attribute value (BT-161b), whose unit is BT-212.
   *
   * @return Numeric item attribute value. May be {@code null}.
   */
  @Nullable
  public BigDecimal getNumericValue ()
  {
    return m_aNumericValue;
  }

  /**
   * Set the numeric item attribute value (BT-161b), whose unit is BT-212.
   *
   * @param aNumericValue
   *        Numeric item attribute value; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceItemAttribute setNumericValue (@Nullable final BigDecimal aNumericValue)
  {
    m_aNumericValue = aNumericValue;
    return this;
  }

  /**
   * Get the item attribute value unit of measure code (BT-212).
   *
   * @return Item attribute value unit of measure code (BT-212). May be {@code null}.
   */
  @Nullable
  public String getValueUnit ()
  {
    return m_sValueUnit;
  }

  /**
   * Set the item attribute value unit of measure code (BT-212).
   *
   * @param sValueUnit
   *        Item attribute value unit of measure code; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoiceItemAttribute setValueUnit (@Nullable final String sValueUnit)
  {
    m_sValueUnit = sValueUnit;
    return this;
  }
}
