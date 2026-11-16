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
package com.helger.phinvoice.term;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.Immutable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.phinvoice.target.EPhinvoiceProfile;

/**
 * How a business term is used in one EN 16931 edition: its cardinality, the lowest profile that
 * contains it and the syntaxes that can carry it.
 *
 * @author Philip Helger
 */
@Immutable
public final class PhinvoiceTermUsage
{
  private final String m_sCardinality;
  private final EPhinvoiceProfile m_eLowestProfile;
  private final boolean m_bUBL;
  private final boolean m_bCII;

  /**
   * Constructor.
   *
   * @param sCardinality
   *        The semantic cardinality, e.g. <code>0..1</code>. May neither be <code>null</code> nor
   *        empty.
   * @param eLowestProfile
   *        The lowest profile that contains the term. May not be <code>null</code>.
   * @param bUBL
   *        <code>true</code> if UBL can carry the term.
   * @param bCII
   *        <code>true</code> if CII can carry the term.
   */
  public PhinvoiceTermUsage (@NonNull @Nonempty final String sCardinality,
                             @NonNull final EPhinvoiceProfile eLowestProfile,
                             final boolean bUBL,
                             final boolean bCII)
  {
    ValueEnforcer.notEmpty (sCardinality, "Cardinality");
    ValueEnforcer.notNull (eLowestProfile, "LowestProfile");
    m_sCardinality = sCardinality;
    m_eLowestProfile = eLowestProfile;
    m_bUBL = bUBL;
    m_bCII = bCII;
  }

  /**
   * @return The semantic cardinality, e.g. <code>0..1</code> or <code>1..n</code>. Neither
   *         <code>null</code> nor empty.
   */
  @NonNull
  @Nonempty
  public String getCardinality ()
  {
    return m_sCardinality;
  }

  /**
   * @return <code>true</code> if the minimum cardinality is not 0.
   */
  public boolean isMandatory ()
  {
    return !m_sCardinality.startsWith ("0");
  }

  /**
   * @return <code>true</code> if the maximum cardinality is <code>n</code>.
   */
  public boolean isRepeatable ()
  {
    return m_sCardinality.endsWith ("n");
  }

  /**
   * @return The lowest profile that contains the term. Never <code>null</code>.
   */
  @NonNull
  public EPhinvoiceProfile getLowestProfile ()
  {
    return m_eLowestProfile;
  }

  /**
   * @return <code>true</code> if UBL (Invoice and Credit Note) can carry the term.
   */
  public boolean isUBL ()
  {
    return m_bUBL;
  }

  /**
   * @return <code>true</code> if CII can carry the term.
   */
  public boolean isCII ()
  {
    return m_bCII;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final PhinvoiceTermUsage rhs = (PhinvoiceTermUsage) o;
    return m_sCardinality.equals (rhs.m_sCardinality) &&
           m_eLowestProfile == rhs.m_eLowestProfile &&
           m_bUBL == rhs.m_bUBL &&
           m_bCII == rhs.m_bCII;
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sCardinality)
                                       .append (m_eLowestProfile)
                                       .append (m_bUBL)
                                       .append (m_bCII)
                                       .getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("Cardinality", m_sCardinality)
                                       .append ("LowestProfile", m_eLowestProfile)
                                       .append ("UBL", m_bUBL)
                                       .append ("CII", m_bCII)
                                       .getToString ();
  }
}
