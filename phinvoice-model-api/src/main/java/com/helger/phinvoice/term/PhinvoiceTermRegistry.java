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

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.string.StringHelper;
import com.helger.collection.commons.CommonsLinkedHashMap;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsOrderedMap;
import com.helger.en16931.basics.EEN16931Edition;
import com.helger.en16931.basics.EEN16931SyntaxKind;
import com.helger.io.resource.ClassPathResource;
import com.helger.io.stream.StreamHelperExt;
import com.helger.phinvoice.target.EPhinvoiceProfile;
import com.helger.phinvoice.target.PhinvoiceExportTarget;

/**
 * The registry of all business groups and business terms of EN 16931:2017, EN 16931:2026 and the
 * Factur-X 1.09.2 profile EXTENDED. It is the basis for the implicit purification of the writers
 * (which terms a {@link PhinvoiceExportTarget} allows), the drop report and the test coverage.
 * <p>
 * The data is read from the class path resource {@value #RESOURCE_PATH}, which is generated from
 * the Factur-X workbook and the CEN syntax bindings by
 * <code>MainCreateTermRegistry</code> (test sources). Syntax constants that need no value in the
 * model - date format codes, fixed scheme identifiers, fixed type codes and the allowance / charge
 * indicators - are not part of the registry.
 *
 * @author Philip Helger
 */
@Immutable
public final class PhinvoiceTermRegistry
{
  /** The class path resource with the registry data */
  public static final String RESOURCE_PATH = "phinvoice-model/terms.tsv";

  private static final int COLUMN_COUNT = 11;
  private static final ICommonsOrderedMap <String, PhinvoiceTerm> TERMS = new CommonsLinkedHashMap <> ();

  static
  {
    final ICommonsList <String> aLines = StreamHelperExt.readStreamLines (new ClassPathResource (RESOURCE_PATH,
                                                                                                 PhinvoiceTermRegistry.class.getClassLoader ()),
                                                                          StandardCharsets.UTF_8);
    if (aLines == null)
      throw new IllegalStateException ("Failed to read the term registry from '" + RESOURCE_PATH + "'");
    int nLineNumber = 0;
    for (final String sLine : aLines)
    {
      nLineNumber++;
      if (StringHelper.isEmpty (sLine) || sLine.startsWith ("#"))
        continue;
      final PhinvoiceTerm aTerm = _parseLine (sLine, nLineNumber);
      if (TERMS.put (aTerm.getID (), aTerm) != null)
        throw new IllegalStateException ("The term registry contains the term ID '" +
                                         aTerm.getID () +
                                         "' more than once (line " +
                                         nLineNumber +
                                         ")");
    }
  }

  private PhinvoiceTermRegistry ()
  {}

  private static boolean _parseBoolean (@NonNull final String sValue, final int nLineNumber)
  {
    if ("true".equals (sValue))
      return true;
    if ("false".equals (sValue))
      return false;
    throw new IllegalStateException ("Invalid boolean value '" + sValue + "' in term registry line " + nLineNumber);
  }

  @Nullable
  private static PhinvoiceTermUsage _parseUsage (@NonNull final List <String> aCells,
                                                 final int nFirstIndex,
                                                 final int nLineNumber)
  {
    final String sCardinality = aCells.get (nFirstIndex);
    if (sCardinality.isEmpty ())
      return null;
    final String sProfile = aCells.get (nFirstIndex + 1);
    final EPhinvoiceProfile eProfile = EPhinvoiceProfile.getFromIDOrNull (sProfile);
    if (eProfile == null)
      throw new IllegalStateException ("Invalid profile '" + sProfile + "' in term registry line " + nLineNumber);
    return new PhinvoiceTermUsage (sCardinality,
                                   eProfile,
                                   _parseBoolean (aCells.get (nFirstIndex + 2), nLineNumber),
                                   _parseBoolean (aCells.get (nFirstIndex + 3), nLineNumber));
  }

  @NonNull
  private static PhinvoiceTerm _parseLine (@NonNull final String sLine, final int nLineNumber)
  {
    // ID, name, data type, 2017: cardinality, profile, UBL, CII, 2026: cardinality, profile, UBL, CII
    final List <String> aCells = StringHelper.getExploded ('\t', sLine);
    if (aCells.size () != COLUMN_COUNT)
      throw new IllegalStateException ("Term registry line " +
                                       nLineNumber +
                                       " has " +
                                       aCells.size () +
                                       " columns instead of " +
                                       COLUMN_COUNT);
    final String sDataType = aCells.get (2);
    EPhinvoiceDataType eDataType = null;
    if (!sDataType.isEmpty ())
    {
      eDataType = EPhinvoiceDataType.getFromIDOrNull (sDataType);
      if (eDataType == null)
        throw new IllegalStateException ("Invalid data type '" + sDataType + "' in term registry line " + nLineNumber);
    }
    return new PhinvoiceTerm (aCells.get (0),
                              aCells.get (1),
                              eDataType,
                              _parseUsage (aCells, 3, nLineNumber),
                              _parseUsage (aCells, 7, nLineNumber));
  }

  /**
   * @return The number of terms in the registry. Always &gt; 0.
   */
  @Nonnegative
  public static int getTermCount ()
  {
    return TERMS.size ();
  }

  /**
   * Get the term with the provided ID.
   *
   * @param sID
   *        The term ID, e.g. <code>BT-1</code>. May be <code>null</code>.
   * @return <code>null</code> if no such term exists.
   */
  @Nullable
  public static PhinvoiceTerm getTermOfID (@Nullable final String sID)
  {
    return sID == null ? null : TERMS.get (sID);
  }

  /**
   * @return All terms in the order of the generated resource. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static ICommonsList <PhinvoiceTerm> getAllTerms ()
  {
    return TERMS.copyOfValues ();
  }

  /**
   * Check if the provided export target allows the provided term: the term must exist in the
   * edition of the target, the syntax of the target must be able to carry it, and the profile of
   * the target must contain it.
   *
   * @param aTerm
   *        The term to check. May not be <code>null</code>.
   * @param aTarget
   *        The export target. May not be <code>null</code>.
   * @return <code>true</code> if a writer for the target may write the term.
   */
  public static boolean isAllowed (@NonNull final PhinvoiceTerm aTerm, @NonNull final PhinvoiceExportTarget aTarget)
  {
    ValueEnforcer.notNull (aTerm, "Term");
    ValueEnforcer.notNull (aTarget, "Target");

    final PhinvoiceTermUsage aUsage = aTerm.getUsage (aTarget.getEdition ());
    if (aUsage == null)
      return false;
    final boolean bSyntaxOK = aTarget.getSyntaxKind () == EEN16931SyntaxKind.CII ? aUsage.isCII () : aUsage.isUBL ();
    return bSyntaxOK && aTarget.getProfile ().includes (aUsage.getLowestProfile ());
  }

  /**
   * Check if the provided export target allows the term with the provided ID.
   *
   * @param sTermID
   *        The ID of the term to check. May be <code>null</code>.
   * @param aTarget
   *        The export target. May not be <code>null</code>.
   * @return <code>true</code> if a writer for the target may write the term, <code>false</code> if
   *         not or if the term is unknown.
   * @see #isAllowed(PhinvoiceTerm, PhinvoiceExportTarget)
   */
  public static boolean isAllowed (@Nullable final String sTermID, @NonNull final PhinvoiceExportTarget aTarget)
  {
    final PhinvoiceTerm aTerm = getTermOfID (sTermID);
    return aTerm != null && isAllowed (aTerm, aTarget);
  }

  /**
   * Get all terms that exist in the provided edition.
   *
   * @param eEdition
   *        The EN 16931 edition. May not be <code>null</code>.
   * @return The terms in the order of the generated resource. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static ICommonsList <PhinvoiceTerm> getAllTermsOfEdition (@NonNull final EEN16931Edition eEdition)
  {
    ValueEnforcer.notNull (eEdition, "Edition");
    return TERMS.copyOfValues (x -> x.existsIn (eEdition));
  }
}
