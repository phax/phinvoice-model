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
package com.helger.phinvoice;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test class for class {@link PhinvoiceLogHelper}.
 *
 * @author Philip Helger
 */
public final class PhinvoiceLogHelperTest
{
  @Test
  public void testUnchanged ()
  {
    assertEquals ("null", PhinvoiceLogHelper.sanitise (null));
    assertEquals ("", PhinvoiceLogHelper.sanitise (""));
    assertEquals ("Rechnung 1 € ä", PhinvoiceLogHelper.sanitise ("Rechnung 1 € ä"));
    // A supplementary character is no control character
    assertEquals ("😀", PhinvoiceLogHelper.sanitise ("😀"));
  }

  @Test
  public void testControlCharacters ()
  {
    assertEquals ("a_b_c_d_e", PhinvoiceLogHelper.sanitise ("a\nb\rc\td\u001Be"));
    // NEL, the line and paragraph separator, DEL
    assertEquals ("____", PhinvoiceLogHelper.sanitise ("\u0085  \u007F"));
    // The bidi controls can reorder a line
    assertEquals ("__", PhinvoiceLogHelper.sanitise ("‮⁦"));
    // A lone surrogate
    assertEquals ("a_", PhinvoiceLogHelper.sanitise ("a\uD83D"));
  }

  @Test
  public void testLength ()
  {
    assertEquals ("x".repeat (PhinvoiceLogHelper.DEFAULT_MAX_LENGTH),
                  PhinvoiceLogHelper.sanitise ("x".repeat (PhinvoiceLogHelper.DEFAULT_MAX_LENGTH)));
    assertEquals ("x".repeat (PhinvoiceLogHelper.DEFAULT_MAX_LENGTH) + "...",
                  PhinvoiceLogHelper.sanitise ("x".repeat (PhinvoiceLogHelper.DEFAULT_MAX_LENGTH + 1)));
    assertEquals ("abc...", PhinvoiceLogHelper.sanitise ("abcdef", 3));
    assertEquals ("...", PhinvoiceLogHelper.sanitise ("abcdef", 0));
    // A surrogate pair is not cut in two
    assertEquals ("a...", PhinvoiceLogHelper.sanitise ("a😀", 2));
  }
}
