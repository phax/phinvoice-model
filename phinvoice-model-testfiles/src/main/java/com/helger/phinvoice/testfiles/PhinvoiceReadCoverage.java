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
package com.helger.phinvoice.testfiles;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.jspecify.annotations.NonNull;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.base.numeric.mutable.MutableInt;
import com.helger.base.string.StringHelper;
import com.helger.base.string.StringReplace;
import com.helger.collection.commons.CommonsHashSet;
import com.helger.collection.commons.CommonsTreeMap;
import com.helger.collection.commons.ICommonsSet;
import com.helger.collection.commons.ICommonsSortedMap;
import com.helger.diagnostics.error.list.ErrorList;
import com.helger.io.resource.ClassPathResource;
import com.helger.phinvoice.model.PhinvoiceAdditionalTerm;
import com.helger.phinvoice.model.PhinvoiceInvoice;
import com.helger.phinvoice.read.IPhinvoiceReader;
import com.helger.xml.serialize.read.DOMReader;

/**
 * Read coverage of a reader: every text and attribute value of a source document must be found
 * somewhere in the model that the reader creates (including the additional terms, where the safety
 * net of the reader puts everything it does not map). The result has the paths of the values that
 * are not found and the terms of the values that only the safety net caught.
 *
 * @author Philip Helger
 */
public final class PhinvoiceReadCoverage
{
  private static final Pattern WHITESPACE = Pattern.compile ("\\s+");
  private static final Pattern ISO_DATE = Pattern.compile ("\\d{4}-\\d{2}-\\d{2}");

  private final String m_sName;
  private final int m_nFiles;
  private int m_nValues;
  private int m_nLostValues;
  private final ICommonsSortedMap <String, Integer> m_aLostPaths = new CommonsTreeMap <> ();
  private final ICommonsSortedMap <String, Integer> m_aSafetyNetTerms = new CommonsTreeMap <> ();

  private PhinvoiceReadCoverage (@NonNull final String sName, final int nFiles)
  {
    m_sName = sName;
    m_nFiles = nFiles;
  }

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

  private static void _increment (@NonNull final Map <String, Integer> aMap, @NonNull final String sKey)
  {
    aMap.merge (sKey, Integer.valueOf (1), (a, b) -> Integer.valueOf (a.intValue () + b.intValue ()));
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

  /**
   * Check the read coverage of a set of files.
   *
   * @param sName
   *        The name of the file set for the log. May not be <code>null</code>.
   * @param aFiles
   *        The files to read. May not be <code>null</code>.
   * @param aReader
   *        The reader to use. May not be <code>null</code>.
   * @param sTermPrefix
   *        The term prefix of the safety net of the reader. May not be <code>null</code>.
   * @param aSyntaxConstants
   *        Values that the binding fixes and that are not checked (e.g. <code>VAT</code>). May not
   *        be <code>null</code>.
   * @return The result. Never <code>null</code>.
   * @throws IllegalStateException
   *         if a file cannot be read
   */
  @NonNull
  public static PhinvoiceReadCoverage check (@NonNull final String sName,
                                             @NonNull final List <ClassPathResource> aFiles,
                                             @NonNull final IPhinvoiceReader <InputStream> aReader,
                                             @NonNull final String sTermPrefix,
                                             @NonNull final Set <String> aSyntaxConstants)
  {
    final PhinvoiceReadCoverage ret = new PhinvoiceReadCoverage (sName, aFiles.size ());
    for (final ClassPathResource aRes : aFiles)
    {
      final ErrorList aErrors = new ErrorList ();
      final PhinvoiceInvoice aModel = aReader.read (aRes.getInputStream (), aErrors);
      if (aModel == null)
        throw new IllegalStateException (aRes.getPath () + ": " + aErrors.getAllErrors ());
      // The values that only the safety net of the reader caught
      for (final PhinvoiceAdditionalTerm aTerm : aModel.additionalTerms ())
        if (aTerm.getTerm ().startsWith (sTermPrefix))
          _increment (ret.m_aSafetyNetTerms, aTerm.getTerm ());

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
        if (aSyntaxConstants.contains (sValue))
          continue;
        ret.m_nValues++;
        boolean bFound = aModelValues.contains (sValue);
        if (!bFound)
        {
          // A text that was joined with others
          final List <String> aParts = StringHelper.getExploded (' ', sValue);
          bFound = aParts.size () > 1 && aModelValues.containsAll (aParts);
        }
        if (!bFound)
        {
          ret.m_nLostValues++;
          _increment (ret.m_aLostPaths, aEntry.getKey ().substring (0, aEntry.getKey ().indexOf ('#')));
        }
      }
    }
    return ret;
  }

  /**
   * @return The paths of the values that are not in the model, with their number. Never
   *         <code>null</code>.
   */
  @NonNull
  @ReturnsMutableObject
  public ICommonsSortedMap <String, Integer> getLostPaths ()
  {
    return m_aLostPaths;
  }

  /**
   * @return The terms of the values that only the safety net of the reader caught, with their
   *         number. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableObject
  public ICommonsSortedMap <String, Integer> getSafetyNetTerms ()
  {
    return m_aSafetyNetTerms;
  }

  /**
   * @return A summary for the log. Never <code>null</code>.
   */
  @NonNull
  public String getAsString ()
  {
    final StringBuilder aSB = new StringBuilder ();
    aSB.append (m_sName)
       .append (": ")
       .append (m_nFiles)
       .append (" files, ")
       .append (m_nValues)
       .append (" values, ")
       .append (m_nLostValues)
       .append (" not found in the model, ")
       .append (m_aLostPaths.size ())
       .append (" paths:");
    for (final Map.Entry <String, Integer> aEntry : m_aLostPaths.entrySet ())
      aSB.append ("\n  ").append (aEntry.getValue ()).append (' ').append (aEntry.getKey ());
    aSB.append ("\n caught by the safety net of the reader:");
    for (final Map.Entry <String, Integer> aEntry : m_aSafetyNetTerms.entrySet ())
      aSB.append ("\n  ").append (aEntry.getValue ()).append (' ').append (aEntry.getKey ());
    return aSB.toString ();
  }
}
