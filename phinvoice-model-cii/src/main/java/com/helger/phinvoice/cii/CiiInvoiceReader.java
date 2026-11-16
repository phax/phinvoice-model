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

import java.io.InputStream;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.w3c.dom.Document;

import com.helger.cii.d22b.CIID22BCrossIndustryInvoiceTypeMarshaller;
import com.helger.cii.d25a.CIID25ACrossIndustryInvoiceTypeMarshaller;
import com.helger.diagnostics.error.SingleError;
import com.helger.diagnostics.error.list.ErrorList;
import com.helger.en16931.basics.EEN16931Edition;
import com.helger.phinvoice.model.PhinvoiceInvoice;
import com.helger.phinvoice.read.IPhinvoiceReader;
import com.helger.xml.serialize.read.DOMReader;

import un.unece.uncefact.data.standard.cii.d25a.CrossIndustryInvoiceType;

/**
 * Reads UN/CEFACT {@code CrossIndustryInvoice} documents into a {@link PhinvoiceInvoice}. Two EN 16931
 * editions are supported:
 * <ul>
 * <li>EN 16931:2017 — CII D16B, and the Factur-X profiles based on it — CII D22B, see
 * {@link #fromD22B()}</li>
 * <li>EN 16931:2026 — CII D25A, see {@link #fromD25A()}</li>
 * </ul>
 * All CII releases share the same XML namespaces and a D16B document also validates against the
 * D25A schema, so the edition cannot be told from the XML itself. {@link #autoDetect()} (also the
 * behaviour of the public constructor) therefore uses BT-24 (Specification identifier) and fails
 * with an error if it is missing or not an EN 16931 identifier. The explicit factories skip the
 * detection.
 * <p>
 * {@link #read(InputStream, ErrorList)} adds every value of the source that the model has no field
 * for as {@link com.helger.phinvoice.model.PhinvoiceAdditionalTerm} with the XPath of the value
 * (prefix <code>cii:</code>), so that reading never loses data. The methods that take a JAXB object
 * do not do that, because they have no source document.
 * <p>
 * The reader sets no limit on the size of its input. An application that reads invoices from
 * untrusted sources must cap the size of the XML before it calls the reader: the time and the memory
 * of reading and rendering grow with it.
 *
 * @author Philip Helger
 */
public class CiiInvoiceReader implements IPhinvoiceReader <InputStream>
{
  // null means "detect from BT-24"
  private final EEN16931Edition m_eEdition;

  private CiiInvoiceReader (@Nullable final EEN16931Edition eEdition)
  {
    m_eEdition = eEdition;
  }

  /** Create a reader that determines the edition of each document from its BT-24. */
  public CiiInvoiceReader ()
  {
    this (null);
  }

  /**
   * Get the reader for EN 16931:2017 documents (CII D16B and CII D22B).
   *
   * @return A reader for EN 16931:2017 documents (CII D16B and CII D22B). BT-24 is not inspected. Never
   *         {@code null}.
   */
  @NonNull
  public static CiiInvoiceReader fromD22B ()
  {
    return new CiiInvoiceReader (EEN16931Edition.EN2017);
  }

  /**
   * Get the reader for EN 16931:2026 documents (CII D25A).
   *
   * @return A reader for EN 16931:2026 documents (CII D25A). BT-24 is not inspected. Never
   *         {@code null}.
   */
  @NonNull
  public static CiiInvoiceReader fromD25A ()
  {
    return new CiiInvoiceReader (EEN16931Edition.EN2026);
  }

  /**
   * Get a reader that determines the edition of each document from BT-24.
   *
   * @return A reader that determines the edition of each document from BT-24 (Specification
   *         identifier) and reports an error if that is not possible. Never {@code null}.
   */
  @NonNull
  public static CiiInvoiceReader autoDetect ()
  {
    return new CiiInvoiceReader (null);
  }

  /**
   * Get the fixed edition of this reader or {@code null} if the edition is detected per.
   *
   * @return The fixed edition of this reader or {@code null} if the edition is detected per
   *         document.
   */
  @Nullable
  public EEN16931Edition getEdition ()
  {
    return m_eEdition;
  }

  /**
   * Read an already-parsed CII D16B / D22B document (EN 16931:2017) into a {@link PhinvoiceInvoice}.
   * Convenience overload that discards any collected warnings.
   *
   * @param aCii
   *        The JAXB invoice. Never {@code null}.
   * @return The populated model.
   */
  @NonNull
  public PhinvoiceInvoice readCrossIndustryInvoice (final un.unece.uncefact.data.standard.cii.d22b.@NonNull CrossIndustryInvoiceType aCii)
  {
    return readCrossIndustryInvoice (aCii, new ErrorList ());
  }

  /**
   * Read an already-parsed CII D16B / D22B document (EN 16931:2017) into a {@link PhinvoiceInvoice},
   * collecting non-fatal issues (unknown type codes, unsupported date formats) into the supplied
   * error list. The simple name {@code CrossIndustryInvoiceType} exists in the D22B and the D25A
   * binding, so the D22B one is qualified here.
   *
   * @param aCii
   *        The JAXB invoice. Never {@code null}.
   * @param aErrors
   *        Error list for warnings. Never {@code null}.
   * @return The populated model.
   */
  @NonNull
  public PhinvoiceInvoice readCrossIndustryInvoice (final un.unece.uncefact.data.standard.cii.d22b.@NonNull CrossIndustryInvoiceType aCii,
                                                @NonNull final ErrorList aErrors)
  {
    return new CiiD22BInvoiceReader ().readCrossIndustryInvoice (aCii, aErrors);
  }

  /**
   * Read an already-parsed CII D25A document (EN 16931:2026) into a {@link PhinvoiceInvoice}.
   * Convenience overload that discards any collected warnings.
   *
   * @param aCii
   *        The JAXB invoice. Never {@code null}.
   * @return The populated model.
   */
  @NonNull
  public PhinvoiceInvoice readCrossIndustryInvoice (@NonNull final CrossIndustryInvoiceType aCii)
  {
    return readCrossIndustryInvoice (aCii, new ErrorList ());
  }

  /**
   * Read an already-parsed CII D25A document (EN 16931:2026) into a {@link PhinvoiceInvoice},
   * collecting non-fatal issues (unknown type codes, unsupported date formats, unparsable issue
   * time) into the supplied error list.
   *
   * @param aCii
   *        The JAXB invoice. Never {@code null}.
   * @param aErrors
   *        Error list for warnings. Never {@code null}.
   * @return The populated model.
   */
  @NonNull
  public PhinvoiceInvoice readCrossIndustryInvoice (@NonNull final CrossIndustryInvoiceType aCii,
                                                @NonNull final ErrorList aErrors)
  {
    return new CiiD25AInvoiceReader ().readCrossIndustryInvoice (aCii, aErrors);
  }

  @Nullable
  public PhinvoiceInvoice read (@NonNull final InputStream aSource, @NonNull final ErrorList aErrors)
  {
    final Document aDoc = DOMReader.readXMLDOM (aSource);
    if (aDoc == null)
    {
      aErrors.add (SingleError.builderError ().errorText ("Source could not be parsed as XML").build ());
      return null;
    }

    EEN16931Edition eEdition = m_eEdition;
    if (eEdition == null)
    {
      eEdition = EEN16931Edition.detect (aDoc);
      if (eEdition == null)
      {
        aErrors.add (SingleError.builderError ()
                                .errorText ("The EN 16931 edition could not be determined because BT-24 (Specification identifier) is missing or is not an EN 16931 identifier. Use CiiInvoiceReader.fromD22B() or CiiInvoiceReader.fromD25A() to select the edition explicitly.")
                                .build ());
        return null;
      }
    }

    switch (eEdition)
    {
      case EN2017:
      {
        final un.unece.uncefact.data.standard.cii.d22b.CrossIndustryInvoiceType aCii = AbstractCiiInvoiceReader.unmarshal (new CIID22BCrossIndustryInvoiceTypeMarshaller (), aDoc, aErrors);
        if (aCii == null)
        {
          aErrors.add (SingleError.builderError ().errorText ("Source could not be parsed as CII D16B / D22B").build ());
          return null;
        }
        final PhinvoiceInvoice ret = readCrossIndustryInvoice (aCii, aErrors);
        CiiUnmappedValueCollector.addUnmappedValues (aDoc, ret, eEdition);
        return ret;
      }
      case EN2026:
      {
        final CrossIndustryInvoiceType aCii = AbstractCiiInvoiceReader.unmarshal (new CIID25ACrossIndustryInvoiceTypeMarshaller (), aDoc, aErrors);
        if (aCii == null)
        {
          aErrors.add (SingleError.builderError ().errorText ("Source could not be parsed as CII D25A").build ());
          return null;
        }
        final PhinvoiceInvoice ret = readCrossIndustryInvoice (aCii, aErrors);
        CiiUnmappedValueCollector.addUnmappedValues (aDoc, ret, eEdition);
        return ret;
      }
      default:
        throw new IllegalStateException ("Unsupported EN 16931 edition " + eEdition);
    }
  }
}
