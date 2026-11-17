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
package com.helger.phinvoice.read;

import java.math.BigDecimal;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.helger.base.string.StringHelper;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsHashSet;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsSet;
import com.helger.diagnostics.error.SingleError;
import com.helger.diagnostics.error.list.ErrorList;
import com.helger.phinvoice.PhinvoiceLogHelper;
import com.helger.phinvoice.model.PhinvoiceAdditionalTerm;
import com.helger.phinvoice.model.PhinvoiceIdentifier;
import com.helger.phinvoice.model.PhinvoiceInvoice;
import com.helger.phinvoice.model.PhinvoiceInvoiceLine;

/**
 * The safety net of the readers (decision D12): it finds the values of a source document that the
 * reader did not take over and adds them as {@link PhinvoiceAdditionalTerm} with the XPath of the
 * value (plus a prefix of the syntax) as term, so that reading never loses data. Each such value is
 * also reported as a warning, with the term as error field name. A value counts as taken over if
 * the writer, which is the exact inverse of the reader, writes it again: at the same path, below the
 * same grandparent element, or as part of an additional term that the reader created itself. The
 * syntax modules create the written document (widest target of the edition, no XML Schema
 * validation).
 *
 * @author Philip Helger
 */
public final class PhinvoiceUnmappedValueCollector
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PhinvoiceUnmappedValueCollector.class);
  private static final Pattern WHITESPACE = Pattern.compile ("\\s+");

  private PhinvoiceUnmappedValueCollector ()
  {}

  @NonNull
  private static String _normalise (@NonNull final String sValue)
  {
    final String ret = WHITESPACE.matcher (sValue).replaceAll (" ").trim ();
    try
    {
      return new BigDecimal (ret).stripTrailingZeros ().toPlainString ();
    }
    catch (final NumberFormatException ex)
    {
      return ret;
    }
  }

  /** The path of the grandparent: two steps up, for an attribute two steps up from its element. */
  @NonNull
  private static String _getGrandParent (@NonNull final String sAnyPath)
  {
    final int nAttr = sAnyPath.indexOf ("/@");
    final String sPath = nAttr < 0 ? sAnyPath : sAnyPath.substring (0, nAttr);
    final int nParent = sPath.lastIndexOf ('/');
    final int nGrandParent = nParent <= 0 ? -1 : sPath.lastIndexOf ('/', nParent - 1);
    return nGrandParent <= 0 ? "" : sPath.substring (0, nGrandParent);
  }

  private static boolean _isIgnoredAttribute (@NonNull final String sName)
  {
    return sName.startsWith ("xmlns") || sName.startsWith ("xsi:");
  }

  /**
   * Call the consumer with the path (without indices) and the value of every attribute and of
   * every element that has text and no child elements.
   */
  private static void _visitRecursive (@NonNull final Element aElement,
                                       @NonNull final String sParentPath,
                                       @NonNull final BiConsumer <String, String> aConsumer)
  {
    final String sPath = sParentPath + "/" + aElement.getTagName ();
    final NamedNodeMap aAttrs = aElement.getAttributes ();
    for (int i = 0; i < aAttrs.getLength (); ++i)
    {
      final Attr aAttr = (Attr) aAttrs.item (i);
      if (!_isIgnoredAttribute (aAttr.getName ()))
        aConsumer.accept (sPath + "/@" + aAttr.getName (), aAttr.getValue ());
    }
    boolean bHasChildElement = false;
    for (Node aChild = aElement.getFirstChild (); aChild != null; aChild = aChild.getNextSibling ())
      if (aChild instanceof final Element aChildElement)
      {
        bHasChildElement = true;
        _visitRecursive (aChildElement, sPath, aConsumer);
      }
    if (!bHasChildElement && StringHelper.isNotEmpty (aElement.getTextContent ()) && !aElement.getTextContent ().isBlank ())
      aConsumer.accept (sPath, aElement.getTextContent ());
  }

  /**
   * Add the values of the source that the reader did not take over to the additional terms of the
   * model, and report each of them.
   *
   * @param aSource
   *        The source document. May not be <code>null</code>.
   * @param aWritten
   *        The model written again by the writer of the same syntax and edition. If it is
   *        <code>null</code> (the model could not be written), nothing can be determined and a
   *        warning is logged.
   * @param aModel
   *        The model created from the source. May not be <code>null</code>.
   * @param sTermPrefix
   *        The prefix of the term of the additional terms, e.g. <code>cii:</code>. May not be
   *        <code>null</code>.
   * @param aErrors
   *        The error list of the reader, to report the values. May not be <code>null</code>.
   */
  public static void addUnmappedValues (@NonNull final Document aSource,
                                        @Nullable final Document aWritten,
                                        @NonNull final PhinvoiceInvoice aModel,
                                        @NonNull final String sTermPrefix,
                                        @NonNull final ErrorList aErrors)
  {
    if (aWritten == null)
    {
      LOGGER.warn ("The model could not be written again, so the unmapped values of the source cannot be determined");
      return;
    }

    // Path and value of everything the writer writes, also below the grandparent element, because the
    // model does not keep every syntax variant (e.g. ram:ID or ram:GlobalID, the base quantity of the
    // gross or the net price); a text with several lines also line by line, because the readers join
    // repeated texts with a newline
    final ICommonsSet <String> aWrittenValues = new CommonsHashSet <> ();
    _visitRecursive (aWritten.getDocumentElement (), "", (sPath, sValue) -> {
      for (final String sLine : StringHelper.getExploded ('\n', sValue))
      {
        aWrittenValues.add (sPath + "=" + _normalise (sLine));
        aWrittenValues.add (_getGrandParent (sPath) + "|" + _normalise (sLine));
      }
      aWrittenValues.add (sPath + "=" + _normalise (sValue));
      aWrittenValues.add (_getGrandParent (sPath) + "|" + _normalise (sValue));
      // The other lexical forms of an XML Schema boolean
      if ("true".equals (sValue) || "false".equals (sValue))
        aWrittenValues.add (sPath + "=" + ("true".equals (sValue) ? "1" : "0"));
    });

    // The values that the reader already keeps as additional terms
    final ICommonsSet <String> aAdditionalValues = new CommonsHashSet <> ();
    final ICommonsList <PhinvoiceAdditionalTerm> aAllTerms = new CommonsArrayList <> (aModel.additionalTerms ());
    aAllTerms.addAll (aModel.getSeller ().additionalTerms ());
    aAllTerms.addAll (aModel.getBuyer ().additionalTerms ());
    if (aModel.getPaymentInstructions () != null)
      aAllTerms.addAll (aModel.getPaymentInstructions ().additionalTerms ());
    for (final PhinvoiceInvoiceLine aLine : aModel.lines ())
      aAllTerms.addAll (aLine.additionalTerms ());
    for (final PhinvoiceAdditionalTerm aTerm : aAllTerms)
    {
      final Object aValue = aTerm.getValue ();
      if (aValue instanceof final PhinvoiceIdentifier aID)
      {
        if (aID.getValue () != null)
          aAdditionalValues.add (_normalise (aID.getValue ()));
        if (aID.getSchemeID () != null)
          aAdditionalValues.add (_normalise (aID.getSchemeID ()));
      }
      else
        if (aValue != null)
          for (final String sPart : StringHelper.getExploded (' ', _normalise (String.valueOf (aValue))))
            aAdditionalValues.add (sPart);
    }

    _visitRecursive (aSource.getDocumentElement (), "", (sPath, sValue) -> {
      final String sNormalised = _normalise (sValue);
      if (sNormalised.length () > 0 &&
          !aAdditionalValues.contains (sNormalised) &&
          !aWrittenValues.contains (sPath + "=" + sNormalised) &&
          !aWrittenValues.contains (_getGrandParent (sPath) + "|" + sNormalised))
      {
        final String sTerm = sTermPrefix + sPath;
        aModel.additionalTerms ().add (new PhinvoiceAdditionalTerm (sTerm, sValue.trim ()));
        aErrors.add (SingleError.builderWarn ()
                                .errorFieldName (sTerm)
                                .errorText ("The value '" +
                                            PhinvoiceLogHelper.sanitise (sValue.trim ()) +
                                            "' has no field in the model and is kept as additional term")
                                .build ());
      }
    });
  }
}
