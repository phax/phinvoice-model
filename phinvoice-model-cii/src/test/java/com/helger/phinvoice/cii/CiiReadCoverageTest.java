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
package com.helger.phinvoice.cii;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.jspecify.annotations.NonNull;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.helger.base.numeric.mutable.MutableInt;
import com.helger.base.string.StringHelper;
import com.helger.base.string.StringReplace;
import com.helger.collection.commons.CommonsHashSet;
import com.helger.collection.commons.CommonsTreeMap;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsSet;
import com.helger.diagnostics.error.list.ErrorList;
import com.helger.io.resource.ClassPathResource;
import com.helger.phinvoice.model.PhinvoiceAdditionalTerm;
import com.helger.phinvoice.model.PhinvoiceInvoice;
import com.helger.phinvoice.testfiles.MockPhinvoiceTestFiles;
import com.helger.phinvoice.testfiles.PhinvoiceModelDumper;
import com.helger.xml.serialize.read.DOMReader;

/**
 * Read coverage: every text and attribute value of a source document must be found somewhere in
 * the model that the reader creates (including the additional terms, where the safety net of the
 * reader puts everything it does not map). The paths of the values that are not found, and the
 * values that only the safety net caught, are logged per test file set. Values that the binding fixes (format codes, tax type
 * code, charge indicators, the scheme identifiers VA / FC) are not checked.
 *
 * @author Philip Helger
 */
public final class CiiReadCoverageTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (CiiReadCoverageTest.class);
  private static final Pattern WHITESPACE = Pattern.compile ("\\s+");
  private static final Pattern ISO_DATE = Pattern.compile ("\\d{4}-\\d{2}-\\d{2}");
  private static final Set <String> SYNTAX_CONSTANTS = Set.of ("VAT", "VA", "FC", "true", "false", "102", "916");

  // Paths whose value is not in the model in the same form: the type codes 50 / 130 of BT-17, BT-18
  // and BT-128 are fixed by the binding, the CII code of BT-8 is kept as UBL code, BT-2 with BT-166
  // is one value in CII
  private static final Set <String> ALLOWED_LOST_PATHS = Set.of ("/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:AdditionalReferencedDocument/ram:TypeCode",
                                                                 "/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeSettlement/ram:AdditionalReferencedDocument/ram:TypeCode",
                                                                 "/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:ApplicableTradeTax/ram:DueDateTypeCode",
                                                                 "/rsm:CrossIndustryInvoice/rsm:ExchangedDocument/ram:IssueDateTime/udt:DateTimeString");

  @NonNull
  private static String _normalise (@NonNull final String s)
  {
    final String ret = WHITESPACE.matcher (s).replaceAll (" ").trim ();
    try
    {
      return new BigDecimal (ret).stripTrailingZeros ().toPlainString ();
    }
    catch (final NumberFormatException ex)
    {
      return ret;
    }
  }

  private static void _collectRecursive (@NonNull final Element aElement,
                                         @NonNull final String sPath,
                                         @NonNull final Map <String, String> aTarget,
                                         @NonNull final MutableInt aCounter)
  {
    final String sMyPath = sPath + "/" + aElement.getTagName ();
    final NamedNodeMap aAttrs = aElement.getAttributes ();
    for (int i = 0; i < aAttrs.getLength (); ++i)
    {
      final Attr aAttr = (Attr) aAttrs.item (i);
      if (!aAttr.getName ().startsWith ("xmlns") && !aAttr.getName ().startsWith ("xsi:") && !"format".equals (aAttr.getName ()))
        aTarget.put (sMyPath + "/@" + aAttr.getName () + "#" + aCounter.getAndInc (), aAttr.getValue ());
    }
    boolean bHasChildElement = false;
    for (Node aChild = aElement.getFirstChild (); aChild != null; aChild = aChild.getNextSibling ())
      if (aChild instanceof final Element aChildElement)
      {
        bHasChildElement = true;
        _collectRecursive (aChildElement, sMyPath, aTarget, aCounter);
      }
    if (!bHasChildElement && aElement.getTextContent () != null && !aElement.getTextContent ().isBlank ())
      aTarget.put (sMyPath + "#" + aCounter.getAndInc (), aElement.getTextContent ());
  }

  private static void _check (@NonNull final String sName,
                              @NonNull final ICommonsList <ClassPathResource> aFiles,
                              @NonNull final CiiInvoiceReader aReader)
  {
    final Map <String, Integer> aLost = new CommonsTreeMap <> ();
    final Map <String, Integer> aUnmapped = new CommonsTreeMap <> ();
    int nValues = 0;
    int nLostValues = 0;
    for (final ClassPathResource aRes : aFiles)
    {
      final ErrorList aErrors = new ErrorList ();
      final PhinvoiceInvoice aModel = aReader.read (aRes.getInputStream (), aErrors);
      assertNotNull (aRes.getPath () + ": " + aErrors.getAllErrors (), aModel);
      // The values that only the safety net of the reader caught
      for (final PhinvoiceAdditionalTerm aTerm : aModel.additionalTerms ())
        if (aTerm.getTerm ().startsWith (CiiUnmappedValueCollector.TERM_PREFIX))
          aUnmapped.merge (aTerm.getTerm (), Integer.valueOf (1), (a, b) -> Integer.valueOf (a.intValue () + b.intValue ()));

      // All values of the model, also in the form of a CII date
      final ICommonsSet <String> aModelValues = new CommonsHashSet <> ();
      for (final String sLine : PhinvoiceModelDumper.dump (aModel, Set.of ()))
      {
        final String sValue = _normalise (sLine.substring (sLine.indexOf (" = ") + 3));
        aModelValues.add (sValue);
        if (ISO_DATE.matcher (sValue).matches ())
          aModelValues.add (StringReplace.replaceAll (sValue, "-", ""));
        // Joined texts and additional terms: every part counts
        aModelValues.addAll (StringHelper.getExploded (' ', sValue));
      }

      final Document aDoc = DOMReader.readXMLDOM (aRes);
      final Map <String, String> aValues = new CommonsTreeMap <> ();
      _collectRecursive (aDoc.getDocumentElement (), "", aValues, new MutableInt (0));
      for (final Map.Entry <String, String> aEntry : aValues.entrySet ())
      {
        final String sValue = _normalise (aEntry.getValue ());
        if (SYNTAX_CONSTANTS.contains (sValue))
          continue;
        nValues++;
        boolean bFound = aModelValues.contains (sValue);
        if (!bFound)
        {
          // A text that was joined with others
          final List <String> aParts = StringHelper.getExploded (' ', sValue);
          bFound = aParts.size () > 1 && aModelValues.containsAll (aParts);
        }
        if (!bFound)
        {
          nLostValues++;
          final String sPath = aEntry.getKey ().substring (0, aEntry.getKey ().indexOf ('#'));
          aLost.merge (sPath, Integer.valueOf (1), (a, b) -> Integer.valueOf (a.intValue () + b.intValue ()));
        }
      }
    }
    final StringBuilder aSB = new StringBuilder ();
    for (final Map.Entry <String, Integer> aEntry : aLost.entrySet ())
      aSB.append ("\n  ").append (aEntry.getValue ()).append (' ').append (aEntry.getKey ());
    aSB.append ("\n caught by the safety net of the reader:");
    for (final Map.Entry <String, Integer> aEntry : aUnmapped.entrySet ())
      aSB.append ("\n  ").append (aEntry.getValue ()).append (' ').append (aEntry.getKey ());
    LOGGER.info (sName +
                 ": " +
                 aFiles.size () +
                 " files, " +
                 nValues +
                 " values, " +
                 nLostValues +
                 " not found in the model, " +
                 aLost.size () +
                 " paths:" +
                 aSB);
    final ICommonsSet <String> aUnexpected = new CommonsHashSet <> (aLost.keySet ());
    aUnexpected.removeAll (ALLOWED_LOST_PATHS);
    assertTrue (sName + ": values not found in the model: " + aUnexpected, aUnexpected.isEmpty ());
  }

  @Test
  public void testEN16931 ()
  {
    _check ("CII D16B", MockPhinvoiceTestFiles.getCiiD16BFiles (), CiiInvoiceReader.fromD22B ());
  }

  @Test
  public void testZugferdExtended ()
  {
    _check ("ZUGFeRD EXTENDED", MockPhinvoiceTestFiles.getCiiZugferd252Files ("extended"), CiiInvoiceReader.fromD22B ());
  }

  @Test
  public void testD25A ()
  {
    _check ("CII D25A", MockPhinvoiceTestFiles.getCiiD25AFiles (), CiiInvoiceReader.fromD25A ());
  }
}
