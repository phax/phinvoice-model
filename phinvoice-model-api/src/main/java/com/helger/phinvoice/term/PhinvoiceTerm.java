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
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.Immutable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.id.IHasID;
import com.helger.base.name.IHasName;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.en16931.basics.EEN16931Edition;

/**
 * A business group (BG / BG-X) or business term (BT / BT-X) of the term registry, with its usage
 * in each EN 16931 edition it exists in.
 *
 * @author Philip Helger
 * @see PhinvoiceTermRegistry
 */
@Immutable
public final class PhinvoiceTerm implements IHasID <String>, IHasName
{
  private final String m_sID;
  private final String m_sName;
  private final EPhinvoiceDataType m_eDataType;
  private final PhinvoiceTermUsage m_aUsage2017;
  private final PhinvoiceTermUsage m_aUsage2026;

  /**
   * Constructor.
   *
   * @param sID
   *        The term ID, e.g. <code>BT-1</code> or <code>BG-X-12</code>. May neither be
   *        <code>null</code> nor empty.
   * @param sName
   *        The term name. May neither be <code>null</code> nor empty.
   * @param eDataType
   *        The semantic data type. Is <code>null</code> for groups and for the terms whose data
   *        type is not in the sources (the terms new in EN 16931:2026).
   * @param aUsage2017
   *        The usage in EN 16931:2017. <code>null</code> if the term does not exist there.
   * @param aUsage2026
   *        The usage in EN 16931:2026. <code>null</code> if the term does not exist there.
   */
  public PhinvoiceTerm (@NonNull @Nonempty final String sID,
                        @NonNull @Nonempty final String sName,
                        @Nullable final EPhinvoiceDataType eDataType,
                        @Nullable final PhinvoiceTermUsage aUsage2017,
                        @Nullable final PhinvoiceTermUsage aUsage2026)
  {
    ValueEnforcer.notEmpty (sID, "ID");
    ValueEnforcer.notEmpty (sName, "Name");
    ValueEnforcer.isTrue (aUsage2017 != null || aUsage2026 != null, "The term must exist in at least one edition");
    m_sID = sID;
    m_sName = sName;
    m_eDataType = eDataType;
    m_aUsage2017 = aUsage2017;
    m_aUsage2026 = aUsage2026;
  }

  /**
   * @return The term ID, e.g. <code>BT-1</code> or <code>BG-X-12</code>. Neither <code>null</code>
   *         nor empty.
   */
  @NonNull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  /**
   * @return The term name. For EN 16931 terms the name from the CEN syntax binding, for BT-X / BG-X
   *         the name from the Factur-X workbook. Neither <code>null</code> nor empty.
   */
  @NonNull
  @Nonempty
  public String getName ()
  {
    return m_sName;
  }

  /**
   * @return <code>true</code> for a business group (BG / BG-X), <code>false</code> for a business
   *         term (BT / BT-X).
   */
  public boolean isGroup ()
  {
    return m_sID.startsWith ("BG-");
  }

  /**
   * @return <code>true</code> for a BT-X / BG-X, i.e. a term that only exists in the Factur-X
   *         profile EXTENDED.
   */
  public boolean isExtension ()
  {
    return m_sID.startsWith ("BT-X-") || m_sID.startsWith ("BG-X-");
  }

  /**
   * @return The semantic data type. Is <code>null</code> for groups and for the terms whose data
   *         type is not in the sources (the terms new in EN 16931:2026).
   */
  @Nullable
  public EPhinvoiceDataType getDataType ()
  {
    return m_eDataType;
  }

  /**
   * Get the usage of this term in the provided edition.
   *
   * @param eEdition
   *        The EN 16931 edition. May not be <code>null</code>.
   * @return <code>null</code> if the term does not exist in the provided edition.
   */
  @Nullable
  public PhinvoiceTermUsage getUsage (@NonNull final EEN16931Edition eEdition)
  {
    ValueEnforcer.notNull (eEdition, "Edition");
    return switch (eEdition)
    {
      case EN2017 -> m_aUsage2017;
      case EN2026 -> m_aUsage2026;
    };
  }

  /**
   * Check if this term exists in the provided edition.
   *
   * @param eEdition
   *        The EN 16931 edition. May not be <code>null</code>.
   * @return <code>true</code> if it exists.
   */
  public boolean existsIn (@NonNull final EEN16931Edition eEdition)
  {
    return getUsage (eEdition) != null;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final PhinvoiceTerm rhs = (PhinvoiceTerm) o;
    return m_sID.equals (rhs.m_sID) &&
           m_sName.equals (rhs.m_sName) &&
           m_eDataType == rhs.m_eDataType &&
           EqualsHelper.equals (m_aUsage2017, rhs.m_aUsage2017) &&
           EqualsHelper.equals (m_aUsage2026, rhs.m_aUsage2026);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sID)
                                       .append (m_sName)
                                       .append (m_eDataType)
                                       .append (m_aUsage2017)
                                       .append (m_aUsage2026)
                                       .getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("ID", m_sID)
                                       .append ("Name", m_sName)
                                       .appendIfNotNull ("DataType", m_eDataType)
                                       .appendIfNotNull ("Usage2017", m_aUsage2017)
                                       .appendIfNotNull ("Usage2026", m_aUsage2026)
                                       .getToString ();
  }
}
