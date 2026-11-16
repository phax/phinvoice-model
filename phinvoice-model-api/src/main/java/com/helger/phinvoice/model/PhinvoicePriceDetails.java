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
 * BG-29 PRICE DETAILS (1..1 on a line). Carries BT-146 (item net price), BT-147 (item price
 * discount), BT-148 (item gross price), BT-149 (item price base quantity) and BT-150 (item
 * price base quantity unit of measure). The allowances and charges on the gross price are
 * lists, because Factur-X EXTENDED documents may have several; BT-147 is the amount of the first
 * allowance.
 *
 * @author Philip Helger
 */
public class PhinvoicePriceDetails
{
  private BigDecimal m_aNetPrice;
  private BigDecimal m_aGrossPrice;
  private BigDecimal m_aBaseQuantity;
  private String m_sBaseQuantityUnit;
  private final ICommonsList <PhinvoicePriceAllowanceCharge> m_aAllowances = new CommonsArrayList <> ();
  private final ICommonsList <PhinvoicePriceAllowanceCharge> m_aCharges = new CommonsArrayList <> ();

  /** Create an empty price-details record. */
  public PhinvoicePriceDetails ()
  {}

  /**
   * Get the item net price (BT-146).
   *
   * @return Item net price (BT-146). May be {@code null}.
   */
  @Nullable
  public BigDecimal getNetPrice ()
  {
    return m_aNetPrice;
  }

  /**
   * Set the item net price (BT-146).
   *
   * @param aNetPrice
   *        Net price; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoicePriceDetails setNetPrice (@Nullable final BigDecimal aNetPrice)
  {
    m_aNetPrice = aNetPrice;
    return this;
  }

  /**
   * Get the item price discount (BT-147): the amount of the first allowance on the gross price.
   *
   * @return Item price discount (BT-147). May be {@code null}.
   */
  @Nullable
  public BigDecimal getDiscount ()
  {
    return m_aAllowances.isEmpty () ? null : m_aAllowances.getFirstOrNull ().getAmount ();
  }

  /**
   * Set the item price discount (BT-147): the amount of the first allowance on the gross price,
   * which is created if needed.
   *
   * @param aDiscount
   *        Discount amount; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoicePriceDetails setDiscount (@Nullable final BigDecimal aDiscount)
  {
    if (m_aAllowances.isNotEmpty ())
      m_aAllowances.getFirstOrNull ().setAmount (aDiscount);
    else
      if (aDiscount != null)
        m_aAllowances.add (new PhinvoicePriceAllowanceCharge ().setAmount (aDiscount));
    return this;
  }

  /**
   * Get the item gross price (BT-148).
   *
   * @return Item gross price (BT-148). May be {@code null}.
   */
  @Nullable
  public BigDecimal getGrossPrice ()
  {
    return m_aGrossPrice;
  }

  /**
   * Set the item gross price (BT-148).
   *
   * @param aGrossPrice
   *        Gross price; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoicePriceDetails setGrossPrice (@Nullable final BigDecimal aGrossPrice)
  {
    m_aGrossPrice = aGrossPrice;
    return this;
  }

  /**
   * Get the item price base quantity (BT-149).
   *
   * @return Item price base quantity (BT-149). May be {@code null}.
   */
  @Nullable
  public BigDecimal getBaseQuantity ()
  {
    return m_aBaseQuantity;
  }

  /**
   * Set the item price base quantity (BT-149).
   *
   * @param aBaseQuantity
   *        Base quantity; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoicePriceDetails setBaseQuantity (@Nullable final BigDecimal aBaseQuantity)
  {
    m_aBaseQuantity = aBaseQuantity;
    return this;
  }

  /**
   * Get the item price base quantity unit of measure (BT-150) — UN/ECE Rec 20/21. May be {@code null}.
   *
   * @return Item price base quantity unit of measure (BT-150) — UN/ECE Rec 20/21. May be {@code null}.
   */
  @Nullable
  public String getBaseQuantityUnit ()
  {
    return m_sBaseQuantityUnit;
  }

  /**
   * Set the item price base quantity unit of measure (BT-150).
   *
   * @param sBaseQuantityUnit
   *        Unit code; may be {@code null}.
   * @return this for chaining.
   */
  @NonNull
  public PhinvoicePriceDetails setBaseQuantityUnit (@Nullable final String sBaseQuantityUnit)
  {
    m_sBaseQuantityUnit = sBaseQuantityUnit;
    return this;
  }

  /**
   * Get the mutable list of allowances on the gross price (BT-147, BT-X-34 to BT-X-36, BT-X-313).
   *
   * @return Mutable list of allowances on the gross price. Never {@code null}; empty when none are
   *         present.
   */
  @NonNull
  @ReturnsMutableObject
  public ICommonsList <PhinvoicePriceAllowanceCharge> allowances ()
  {
    return m_aAllowances;
  }

  /**
   * Get the mutable list of charges on the gross price (BT-X-300 to BT-X-303, BT-X-314, BT-X-616).
   *
   * @return Mutable list of charges on the gross price. Never {@code null}; empty when none are
   *         present.
   */
  @NonNull
  @ReturnsMutableObject
  public ICommonsList <PhinvoicePriceAllowanceCharge> charges ()
  {
    return m_aCharges;
  }
}
