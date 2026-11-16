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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * Test class for class {@link PhinvoiceAdditionalTerm}.
 *
 * @author Philip Helger
 */
public final class PhinvoiceAdditionalTermTest
{
  @Test
  public void testBasic ()
  {
    final PhinvoiceAdditionalTerm aTerm = new PhinvoiceAdditionalTerm ("BT-12", "CONTRACT-2");
    assertEquals ("BT-12", aTerm.getTerm ());
    assertEquals ("CONTRACT-2", aTerm.getValue ());
    assertNull (aTerm.getCodeList ());

    assertEquals ("paymentmeans", new PhinvoiceAdditionalTerm ("BT-81", "58", "paymentmeans").getCodeList ());
  }

  @Test (expected = IllegalArgumentException.class)
  public void testEmptyTerm ()
  {
    new PhinvoiceAdditionalTerm ("", "value");
  }

  @Test (expected = NullPointerException.class)
  public void testNullValue ()
  {
    new PhinvoiceAdditionalTerm ("BT-12", null);
  }
}
