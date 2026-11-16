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
package com.helger.phinvoice.supplementary.tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.io.stream.StreamHelper;
import com.helger.base.io.streamprovider.ByteArrayInputStreamProvider;
import com.helger.base.string.StringHelper;
import com.helger.collection.commons.CommonsHashSet;
import com.helger.collection.commons.CommonsLinkedHashMap;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsOrderedMap;
import com.helger.collection.commons.ICommonsSet;
import com.helger.io.file.SimpleFileIO;
import com.helger.phinvoice.target.EPhinvoiceProfile;
import com.helger.phinvoice.term.EPhinvoiceDataType;
import com.helger.phinvoice.term.PhinvoiceTermRegistry;
import com.helger.poi.excel.ExcelReadHelper;

/**
 * Create the resource of {@link PhinvoiceTermRegistry} from these sources, which are expected in
 * sibling checkouts of this project:
 * <ul>
 * <li>The Factur-X 1.09.2 workbook in the ZUGFeRD 2.5.2 distribution (phive-rules), sheet
 * {@value #SHEET_NAME}: BT / BT-X / BG / BG-X of EN 16931:2017 and the profile EXTENDED, with the
 * lowest profile, the cardinality and the data type</li>
 * <li>The CEN syntax bindings of EN 16931:2017 and EN 16931:2026 (en16931-cii2ubl): names,
 * cardinalities and UBL / CII availability of the EN 16931 terms</li>
 * </ul>
 * Syntax constants that need no value in the model are skipped: date format codes, the fixed tax
 * scheme identifiers ("VA" / "FC"), the fixed VAT tax type code, the currency of BT-110 / BT-111
 * (which is BT-5 / BT-6 by definition), fixed document type codes and the allowance / charge
 * indicators.
 * <p>
 * Run it with the module directory as the working directory.
 *
 * @author Philip Helger
 */
public final class MainCreateTermRegistry
{
  private static final Logger LOGGER = LoggerFactory.getLogger (MainCreateTermRegistry.class);

  private static final File FACTURX_ZIP = new File ("../../phive-rules/phive-rules-zugferd/docs/2.5.2/ZUGFeRD_2.5.2_EN.zip");
  private static final String FACTURX_ZIP_ENTRY = "Documentation/1_FACTUR-X 1.09.2 - EN FR.xlsx";
  private static final String SHEET_NAME = "Factur-X CII D22B EXTENDED";
  private static final File SYNTAX_2017 = new File ("../../en16931-cii2ubl/docs/en16931-2017-syntax.md");
  private static final File SYNTAX_2026 = new File ("../../en16931-cii2ubl/docs/en16931-2026-syntax.md");
  private static final File TARGET = new File ("src/main/resources/" + PhinvoiceTermRegistry.RESOURCE_PATH);

  // Workbook columns (0-based)
  private static final int COL_IS_BT = 0;
  private static final int COL_ID = 5;
  private static final int COL_CARDINALITY = 8;
  private static final int COL_NAME = 9;
  private static final int COL_DATA_TYPE = 14;
  private static final int COL_XPATH = 18;
  private static final int COL_LOWEST_PROFILE = 33;

  private static final Pattern WB_GROUP = Pattern.compile ("BG-(X-)?\\d+");
  private static final Pattern WB_TERM = Pattern.compile ("BT-(X-)?\\d+(-\\d+)?");
  private static final Pattern MD_ROW = Pattern.compile ("^\\| ((?:BT|BG)-\\d+(?:-\\d+)*) \\|.*");
  private static final Pattern MD_GROUP_HEADING = Pattern.compile ("^## (BG-\\d+) (.+) \\((\\d\\.\\.[\\dn])\\)$");
  private static final Pattern CARDINALITY = Pattern.compile ("\\d\\.\\.[\\dn]");
  private static final Pattern WHITESPACE = Pattern.compile ("[\\s\\u00a0]+");

  // The workbook terms that are syntax constants (see class Javadoc)
  private static final ICommonsSet <String> EXCLUDED_WB = new CommonsHashSet <> ("BT-110-0", "BT-111-0");

  // The workbook rows that are not marked as business term, but are a value: the ID of the ship to
  // party of a line is in a row with the ID of a structural row
  private static final ICommonsSet <String> INCLUDED_WB = new CommonsHashSet <> ("BT-186-00");

  // The EN 16931:2026 terms that are syntax constants (see class Javadoc)
  private static final ICommonsSet <String> EXCLUDED_2026 = new CommonsHashSet <> ("BT-2-1",
                                                                                     "BT-7-1",
                                                                                     "BT-9-1",
                                                                                     "BT-17-1",
                                                                                     "BT-18-2",
                                                                                     "BT-26-1",
                                                                                     "BT-31-1",
                                                                                     "BT-31-2",
                                                                                     "BT-32-2",
                                                                                     "BT-48-1",
                                                                                     "BT-48-2",
                                                                                     "BT-63-1",
                                                                                     "BT-63-2",
                                                                                     "BT-72-1",
                                                                                     "BT-73-1",
                                                                                     "BT-74-1",
                                                                                     "BT-90-1",
                                                                                     "BT-95-1",
                                                                                     "BT-102-1",
                                                                                     "BT-110-1",
                                                                                     "BT-111-1",
                                                                                     "BT-118-1",
                                                                                     "BT-122-1-1",
                                                                                     "BT-128-2",
                                                                                     "BT-134-1",
                                                                                     "BT-135-1",
                                                                                     "BT-147-1",
                                                                                     "BT-148-1",
                                                                                     "BT-151-1",
                                                                                     "BT-166-1",
                                                                                     "BT-170-1",
                                                                                     "BT-181-1",
                                                                                     "BT-187-1",
                                                                                     "BT-218-1",
                                                                                     "BG-20-1",
                                                                                     "BG-21-1",
                                                                                     "BG-27-1",
                                                                                     "BG-28-1");

  private MainCreateTermRegistry ()
  {}

  /**
   * One row of the Factur-X workbook.
   */
  private record WorkbookTerm (String id,
                               String name,
                               String cardinality,
                               EPhinvoiceDataType dataType,
                               EPhinvoiceProfile lowestProfile)
  {}

  /**
   * One term of a CEN syntax binding.
   */
  private record SyntaxTerm (String id, String name, String cardinality, boolean ubl, boolean cii)
  {}

  @NonNull
  private static String _normalise (@Nullable final String s)
  {
    if (s == null)
      return "";
    return WHITESPACE.matcher (s).replaceAll (" ").trim ();
  }

  @NonNull
  private static String _cell (@NonNull final Row aRow, final int nCol)
  {
    final Cell aCell = aRow.getCell (nCol);
    return _normalise (aCell == null ? null : ExcelReadHelper.getCellValueString (aCell));
  }

  @Nullable
  private static EPhinvoiceDataType _getDataType (@NonNull final String sID, @NonNull final String s)
  {
    return switch (s.toLowerCase (Locale.ROOT))
    {
      case "" -> null;
      case "amount" -> EPhinvoiceDataType.AMOUNT;
      case "unit price_ amount" -> EPhinvoiceDataType.UNIT_PRICE_AMOUNT;
      case "percentage" -> EPhinvoiceDataType.PERCENTAGE;
      case "quantity" -> EPhinvoiceDataType.QUANTITY;
      case "rate" -> EPhinvoiceDataType.RATE;
      case "code" -> EPhinvoiceDataType.CODE;
      case "identifier" -> EPhinvoiceDataType.IDENTIFIER;
      case "document reference" -> EPhinvoiceDataType.DOCUMENT_REFERENCE;
      case "date" -> EPhinvoiceDataType.DATE;
      case "text" -> EPhinvoiceDataType.TEXT;
      case "string" -> EPhinvoiceDataType.STRING;
      case "indicator" -> EPhinvoiceDataType.INDICATOR;
      case "binary object" -> EPhinvoiceDataType.BINARY_OBJECT;
      default -> throw new IllegalStateException ("Unknown data type '" + s + "' of " + sID);
    };
  }

  private static boolean _isWorkbookSyntaxConstant (@NonNull final String sID,
                                                    @NonNull final String sName,
                                                    @NonNull final String sXPath)
  {
    if (EXCLUDED_WB.contains (sID))
      return true;
    // Date format codes
    if (sXPath.endsWith ("/@format"))
      return true;
    // "VA" or "FC"
    return sName.equalsIgnoreCase ("Tax Scheme identifier");
  }

  @NonNull
  private static ICommonsOrderedMap <String, WorkbookTerm> _readWorkbook () throws IOException
  {
    final byte [] aBytes;
    try (final ZipFile aZip = new ZipFile (FACTURX_ZIP))
    {
      final ZipEntry aEntry = aZip.getEntry (FACTURX_ZIP_ENTRY);
      if (aEntry == null)
        throw new IllegalStateException ("Entry '" + FACTURX_ZIP_ENTRY + "' not found in " + FACTURX_ZIP);
      try (final InputStream aIS = aZip.getInputStream (aEntry))
      {
        aBytes = StreamHelper.getAllBytes (aIS);
      }
    }

    final ICommonsOrderedMap <String, WorkbookTerm> ret = new CommonsLinkedHashMap <> ();
    try (final Workbook aWB = ExcelReadHelper.readWorkbookFromInputStream (new ByteArrayInputStreamProvider (aBytes)))
    {
      final Sheet aSheet = aWB.getSheet (SHEET_NAME);
      if (aSheet == null)
        throw new IllegalStateException ("Sheet '" + SHEET_NAME + "' not found");
      boolean bHeaderFound = false;
      for (final Row aRow : aSheet)
      {
        final String sID = _cell (aRow, COL_ID);
        if (!bHeaderFound)
        {
          bHeaderFound = "ID".equals (sID);
          continue;
        }
        if (sID.isEmpty ())
          continue;

        final boolean bGroup = WB_GROUP.matcher (sID).matches ();
        String sName = _cell (aRow, COL_NAME);
        final String sXPath = _cell (aRow, COL_XPATH);
        if (!bGroup)
        {
          if ((!"YES".equals (_cell (aRow, COL_IS_BT)) && !INCLUDED_WB.contains (sID)) || !WB_TERM.matcher (sID).matches ())
            continue;
          if (_isWorkbookSyntaxConstant (sID, sName, sXPath))
            continue;
        }
        if (sName.isEmpty ())
        {
          // The workbook has no name for a few groups - use the CII element name instead
          sName = sXPath.substring (sXPath.lastIndexOf ('/') + 1);
          LOGGER.warn ("The workbook has no name for " + sID + " - using '" + sName + "'");
        }

        final String sCardinality = _cell (aRow, COL_CARDINALITY);
        if (!CARDINALITY.matcher (sCardinality).matches ())
          throw new IllegalStateException ("Invalid cardinality '" + sCardinality + "' of " + sID);
        final String sProfile = _cell (aRow, COL_LOWEST_PROFILE);
        final EPhinvoiceProfile eProfile = EPhinvoiceProfile.getFromDisplayNameOrNull (sProfile);
        if (eProfile == null)
          throw new IllegalStateException ("Unknown profile '" + sProfile + "' of " + sID);
        final EPhinvoiceDataType eDataType = bGroup ? null : _getDataType (sID, _cell (aRow, COL_DATA_TYPE));

        if (ret.put (sID, new WorkbookTerm (sID, sName, sCardinality, eDataType, eProfile)) != null)
          throw new IllegalStateException ("Duplicate workbook ID " + sID);
      }
    }
    LOGGER.info ("Read " + ret.size () + " terms from the Factur-X workbook");
    return ret;
  }

  private static boolean _isAvailable (@NonNull final String sCell)
  {
    return !sCell.isEmpty () && !"—".equals (sCell) && !"-".equals (sCell);
  }

  @NonNull
  private static ICommonsOrderedMap <String, SyntaxTerm> _readSyntax (@NonNull final File aFile)
  {
    final ICommonsList <String> aLines = SimpleFileIO.getAllFileLines (aFile, StandardCharsets.UTF_8);
    if (aLines == null)
      throw new IllegalStateException ("Failed to read " + aFile);

    final ICommonsOrderedMap <String, SyntaxTerm> ret = new CommonsLinkedHashMap <> ();
    boolean bTermSection = false;
    for (final String sLine : aLines)
    {
      if (sLine.startsWith ("## "))
      {
        bTermSection = sLine.equals ("## Header Level Fields") || sLine.startsWith ("## BG-");
        final Matcher m = MD_GROUP_HEADING.matcher (sLine);
        if (m.matches () && !EXCLUDED_2026.contains (m.group (1)))
          ret.putIfAbsent (m.group (1), new SyntaxTerm (m.group (1), _normalise (m.group (2)), m.group (3), true, true));
        continue;
      }
      if (!bTermSection)
        continue;

      final Matcher m = MD_ROW.matcher (sLine);
      if (!m.matches ())
        continue;
      // ID, name, cardinality, UBL Invoice, UBL Credit Note, CII, notes
      final List <String> aCells = StringHelper.getExploded ('|', sLine);
      final String sID = _normalise (aCells.get (1));
      if (EXCLUDED_2026.contains (sID))
        continue;
      final String sCardinality = _normalise (aCells.get (3));
      if (!CARDINALITY.matcher (sCardinality).matches ())
        throw new IllegalStateException ("Invalid cardinality '" + sCardinality + "' of " + sID + " in " + aFile);
      final boolean bUBL = _isAvailable (_normalise (aCells.get (4))) || _isAvailable (_normalise (aCells.get (5)));
      final boolean bCII = _isAvailable (_normalise (aCells.get (6)));
      ret.putIfAbsent (sID, new SyntaxTerm (sID, _normalise (aCells.get (2)), sCardinality, bUBL, bCII));
    }
    LOGGER.info ("Read " + ret.size () + " terms from " + aFile.getName ());
    return ret;
  }

  private static void _appendUsage (@NonNull final StringBuilder aSB,
                                    @Nullable final String sCardinality,
                                    @Nullable final EPhinvoiceProfile eProfile,
                                    final boolean bUBL,
                                    final boolean bCII)
  {
    if (sCardinality == null)
      aSB.append ("\t\t\t\t");
    else
      aSB.append ('\t')
         .append (sCardinality)
         .append ('\t')
         .append (eProfile.getID ())
         .append ('\t')
         .append (bUBL)
         .append ('\t')
         .append (bCII);
  }

  public static void main (final String [] args) throws IOException
  {
    final ICommonsOrderedMap <String, WorkbookTerm> aWB = _readWorkbook ();
    final ICommonsOrderedMap <String, SyntaxTerm> a2017 = _readSyntax (SYNTAX_2017);
    final ICommonsOrderedMap <String, SyntaxTerm> a2026 = _readSyntax (SYNTAX_2026);

    // Every EN 16931:2017 term must be in the workbook
    for (final String sID : a2017.keySet ())
      if (!aWB.containsKey (sID))
        throw new IllegalStateException ("The EN 16931:2017 term " + sID + " is not in the Factur-X workbook");

    // All IDs: workbook order first, then the EN 16931:2026 terms that are not in the workbook
    final ICommonsOrderedMap <String, String> aAllIDs = new CommonsLinkedHashMap <> ();
    aWB.keySet ().forEach (x -> aAllIDs.put (x, x));
    a2026.keySet ().forEach (x -> aAllIDs.putIfAbsent (x, x));

    final StringBuilder aSB = new StringBuilder ();
    aSB.append ("# Generated by MainCreateTermRegistry - do not edit\n")
       .append ("# Sources: ")
       .append (FACTURX_ZIP.getName ())
       .append (" / ")
       .append (FACTURX_ZIP_ENTRY)
       .append (" (sheet ")
       .append (SHEET_NAME)
       .append ("), ")
       .append (SYNTAX_2017.getName ())
       .append (", ")
       .append (SYNTAX_2026.getName ())
       .append ('\n')
       .append ("# Columns: ID, name, data type, EN 16931:2017 (cardinality, lowest profile, UBL, CII), EN 16931:2026 (cardinality, lowest profile, UBL, CII)\n");
    int nCount2017 = 0;
    int nCount2026 = 0;
    for (final String sID : aAllIDs.keySet ())
    {
      final WorkbookTerm aWBTerm = aWB.get (sID);
      final SyntaxTerm a2017Term = a2017.get (sID);
      final SyntaxTerm a2026Term = a2026.get (sID);

      // Prefer the CEN names, the newest edition first
      final String sName;
      if (a2026Term != null)
        sName = a2026Term.name ();
      else
        sName = a2017Term != null ? a2017Term.name () : aWBTerm.name ();
      final EPhinvoiceDataType eDataType = aWBTerm != null ? aWBTerm.dataType () : null;
      aSB.append (sID).append ('\t').append (sName).append ('\t');
      if (eDataType != null)
        aSB.append (eDataType.getID ());

      if (aWBTerm != null)
      {
        // In the workbook means CII can carry it; only the EN 16931 terms have a UBL binding
        final String sCardinality = a2017Term != null ? a2017Term.cardinality () : aWBTerm.cardinality ();
        _appendUsage (aSB, sCardinality, aWBTerm.lowestProfile (), a2017Term != null && a2017Term.ubl (), true);
        nCount2017++;
      }
      else
        _appendUsage (aSB, null, null, false, false);

      if (a2026Term != null)
      {
        _appendUsage (aSB, a2026Term.cardinality (), EPhinvoiceProfile.EN16931, a2026Term.ubl (), a2026Term.cii ());
        nCount2026++;
      }
      else
        _appendUsage (aSB, null, null, false, false);
      aSB.append ('\n');
    }

    if (SimpleFileIO.writeFile (TARGET, aSB.toString (), StandardCharsets.UTF_8).isFailure ())
      throw new IllegalStateException ("Failed to write " + TARGET);
    LOGGER.info ("Wrote " +
                 aAllIDs.size () +
                 " terms (" +
                 nCount2017 +
                 " in EN 16931:2017, " +
                 nCount2026 +
                 " in EN 16931:2026) to " +
                 TARGET.getAbsolutePath ());
  }
}
