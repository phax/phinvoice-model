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
import com.helger.cii.d22b.CIID22BCrossIndustryInvoiceTypeMarshaller;
import com.helger.cii.d25a.CIID25ACrossIndustryInvoiceTypeMarshaller;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsHashSet;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsSet;
import com.helger.diagnostics.error.list.ErrorList;
import com.helger.en16931.basics.EEN16931Edition;
import com.helger.en16931.basics.EEN16931SyntaxKind;
import com.helger.phinvoice.model.PhinvoiceAdditionalTerm;
import com.helger.phinvoice.model.PhinvoiceIdentifier;
import com.helger.phinvoice.model.PhinvoiceInvoice;
import com.helger.phinvoice.model.PhinvoiceInvoiceLine;
import com.helger.phinvoice.target.EPhinvoiceProfile;
import com.helger.phinvoice.target.PhinvoiceExportTarget;
import com.helger.phinvoice.write.PhinvoiceTermFilter;

/**
 * The safety net of the CII reader (decision D12): it finds the values of a source document that
 * the reader did not take over and adds them as {@link PhinvoiceAdditionalTerm} with the XPath of
 * the value as term, so that reading never loses data. A value counts as taken over if the writer,
 * which is the exact inverse of the reader, writes it again at the same path. For that the model is
 * written with the widest target of the edition (EN 16931:2017 EXTENDED or EN 16931:2026) and
 * without XML Schema validation.
 *
 * @author Philip Helger
 */
final class CiiUnmappedValueCollector
{
  /** The prefix of the term of an additional term that holds an unmapped value */
  static final String TERM_PREFIX = "cii:";

  private static final Logger LOGGER = LoggerFactory.getLogger (CiiUnmappedValueCollector.class);
  private static final Pattern WHITESPACE = Pattern.compile ("\\s+");

  private CiiUnmappedValueCollector ()
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

  @Nullable
  private static Document _write (@NonNull final PhinvoiceInvoice aModel, @NonNull final EEN16931Edition eEdition)
  {
    // The widest target of the edition, so that the writer drops nothing that the model has
    final EPhinvoiceProfile eProfile = eEdition == EEN16931Edition.EN2017 ? EPhinvoiceProfile.EXTENDED
                                                                         : EPhinvoiceProfile.EN16931;
    final PhinvoiceExportTarget aTarget = new PhinvoiceExportTarget (EEN16931SyntaxKind.CII, eEdition, eProfile);
    final PhinvoiceTermFilter aFilter = new PhinvoiceTermFilter (aTarget, new ErrorList ());
    // Raw: BT-24 and BT-6 as in the model
    final String sSpecificationIdentifier = aModel.getProcessControl ().getSpecificationIdentifier ();
    final String sBT24 = sSpecificationIdentifier != null ? sSpecificationIdentifier : aTarget.getSpecificationIdentifier ();
    if (eEdition == EEN16931Edition.EN2017)
      return new CIID22BCrossIndustryInvoiceTypeMarshaller ().setUseSchema (false)
                                                             .getAsDocument (new CiiD22BInvoiceWriter (aFilter, true).createCrossIndustryInvoice (aModel,
                                                                                                                                                  sBT24));
    return new CIID25ACrossIndustryInvoiceTypeMarshaller ().setUseSchema (false)
                                                           .getAsDocument (new CiiD25AInvoiceWriter (aFilter, true).createCrossIndustryInvoice (aModel,
                                                                                                                                                sBT24));
  }

  /**
   * Add the values of the source that the reader did not take over to the additional terms of the
   * model.
   *
   * @param aSource
   *        The source document. May not be <code>null</code>.
   * @param aModel
   *        The model created from it. May not be <code>null</code>.
   * @param eEdition
   *        The edition the source was read with. May not be <code>null</code>.
   */
  static void addUnmappedValues (@NonNull final Document aSource,
                                 @NonNull final PhinvoiceInvoice aModel,
                                 @NonNull final EEN16931Edition eEdition)
  {
    final Document aWritten = _write (aModel, eEdition);
    if (aWritten == null)
    {
      LOGGER.warn ("The model could not be written again, so the unmapped values of the source cannot be determined");
      return;
    }

    // Path and value of everything the writer writes, also below the grandparent element, because the
    // model does not keep every syntax variant (ram:ID or ram:GlobalID, ram:IBANID or
    // ram:ProprietaryID, the base quantity of the gross or the net price); a text with several lines
    // also line by line, because the readers join repeated texts with a newline
    final ICommonsSet <String> aWrittenValues = new CommonsHashSet <> ();
    _visitRecursive (aWritten.getDocumentElement (), "", (sPath, sValue) -> {
      for (final String sLine : StringHelper.getExploded ('\n', sValue))
      {
        aWrittenValues.add (sPath + "=" + _normalise (sLine));
        aWrittenValues.add (_getGrandParent (sPath) + "|" + _normalise (sLine));
      }
      aWrittenValues.add (sPath + "=" + _normalise (sValue));
      aWrittenValues.add (_getGrandParent (sPath) + "|" + _normalise (sValue));
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
        aModel.additionalTerms ().add (new PhinvoiceAdditionalTerm (TERM_PREFIX + sPath, sValue.trim ()));
    });
  }
}
