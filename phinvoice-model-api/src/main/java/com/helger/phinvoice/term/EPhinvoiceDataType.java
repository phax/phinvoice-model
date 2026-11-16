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
import com.helger.base.id.IHasID;
import com.helger.base.lang.EnumHelper;

/**
 * The semantic data type of a business term, as given in the column "Semantic data type" of the
 * Factur-X workbook.
 *
 * @author Philip Helger
 */
public enum EPhinvoiceDataType implements IHasID <String>
{
  AMOUNT ("amount"),
  UNIT_PRICE_AMOUNT ("unit-price-amount"),
  PERCENTAGE ("percentage"),
  QUANTITY ("quantity"),
  RATE ("rate"),
  CODE ("code"),
  IDENTIFIER ("identifier"),
  DOCUMENT_REFERENCE ("document-reference"),
  DATE ("date"),
  TEXT ("text"),
  /** Used by the workbook for attributes such as scheme identifiers */
  STRING ("string"),
  INDICATOR ("indicator"),
  BINARY_OBJECT ("binary-object");

  private final String m_sID;

  EPhinvoiceDataType (@NonNull @Nonempty final String sID)
  {
    m_sID = sID;
  }

  @NonNull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  @Nullable
  public static EPhinvoiceDataType getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (EPhinvoiceDataType.class, sID);
  }
}
