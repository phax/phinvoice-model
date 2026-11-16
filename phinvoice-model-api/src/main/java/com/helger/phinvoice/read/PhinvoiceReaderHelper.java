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

import java.util.List;
import java.util.function.Consumer;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.Immutable;
import com.helger.diagnostics.error.SingleError;
import com.helger.diagnostics.error.list.ErrorList;
import com.helger.en16931.basics.codelist.EEN16931InvoiceTypeCode;
import com.helger.en16931.basics.codelist.EEN16931InvoiceTypeCodeRole;
import com.helger.phinvoice.PhinvoiceLogHelper;
import com.helger.phinvoice.model.EPhinvoiceDocumentKind;
import com.helger.phinvoice.model.PhinvoiceAdditionalTerm;
import com.helger.phinvoice.model.PhinvoiceContact;
import com.helger.phinvoice.model.PhinvoiceDeliveryInformation;
import com.helger.phinvoice.model.PhinvoiceDirectDebit;
import com.helger.phinvoice.model.PhinvoiceInvoicingPeriod;
import com.helger.phinvoice.model.PhinvoiceLineDelivery;
import com.helger.phinvoice.model.PhinvoicePaymentCard;
import com.helger.phinvoice.model.PhinvoicePaymentInstructions;

/**
 * Helpers that all {@link IPhinvoiceReader} implementations share, so that the same value leads to
 * the same result in every syntax.
 *
 * @author Philip Helger
 */
@Immutable
public final class PhinvoiceReaderHelper
{
  private PhinvoiceReaderHelper ()
  {}

  /**
   * Take over the occurrences of a term that the model has only once. The first value goes into the
   * model and every other one becomes an additional term, so that no value gets lost.
   *
   * @param <T>
   *        The type of the values.
   * @param aValues
   *        All values in the order of the document. A {@code null} value is skipped. Never
   *        {@code null}.
   * @param sTerm
   *        The key of the label of the additional terms. Never {@code null}.
   * @param aSetter
   *        Receives the first value. Never {@code null}.
   * @param aAdditionalTerms
   *        Receives the other values. Never {@code null}.
   */
  public static <T> void setAndAddRepeated (@NonNull final Iterable <? extends T> aValues,
                                            @NonNull final String sTerm,
                                            @NonNull final Consumer <? super T> aSetter,
                                            @NonNull final List <PhinvoiceAdditionalTerm> aAdditionalTerms)
  {
    boolean bFirst = true;
    for (final T aValue : aValues)
      if (aValue != null)
      {
        if (bFirst)
          aSetter.accept (aValue);
        else
          aAdditionalTerms.add (new PhinvoiceAdditionalTerm (sTerm, aValue));
        bFirst = false;
      }
  }

  /**
   * Add a value as additional term if it is present.
   *
   * @param sTerm
   *        The key of the label. Never {@code null}.
   * @param aValue
   *        The value. May be {@code null}.
   * @param aAdditionalTerms
   *        The list to add to. Never {@code null}.
   */
  public static void addIfPresent (@NonNull final String sTerm,
                                   @Nullable final Object aValue,
                                   @NonNull final List <PhinvoiceAdditionalTerm> aAdditionalTerms)
  {
    if (aValue != null)
      aAdditionalTerms.add (new PhinvoiceAdditionalTerm (sTerm, aValue));
  }

  /**
   * Add an invoicing period that does not fit into the model, because there is one already.
   *
   * @param aPeriod
   *        The period. Never {@code null}.
   * @param sStartTerm
   *        The key of the label of the start date. Never {@code null}.
   * @param sEndTerm
   *        The key of the label of the end date. Never {@code null}.
   * @param aAdditionalTerms
   *        The list to add to. Never {@code null}.
   */
  public static void addPeriod (@NonNull final PhinvoiceInvoicingPeriod aPeriod,
                                @NonNull final String sStartTerm,
                                @NonNull final String sEndTerm,
                                @NonNull final List <PhinvoiceAdditionalTerm> aAdditionalTerms)
  {
    addIfPresent (sStartTerm, aPeriod.getStartDate (), aAdditionalTerms);
    addIfPresent (sEndTerm, aPeriod.getEndDate (), aAdditionalTerms);
  }

  /**
   * Add a delivery that does not fit into the model, because there is one already.
   *
   * @param aDelivery
   *        The delivery. Never {@code null}.
   * @param aAdditionalTerms
   *        The list to add to. Never {@code null}.
   */
  public static void addDelivery (@NonNull final PhinvoiceDeliveryInformation aDelivery,
                                  @NonNull final List <PhinvoiceAdditionalTerm> aAdditionalTerms)
  {
    addIfPresent ("BT-70", aDelivery.getPartyName (), aAdditionalTerms);
    addIfPresent ("BT-71", aDelivery.getLocationIdentifier (), aAdditionalTerms);
    addIfPresent ("BT-72", aDelivery.getActualDeliveryDate (), aAdditionalTerms);
    addIfPresent ("BG-15", aDelivery.getPostalAddress (), aAdditionalTerms);
  }

  /**
   * Add a line delivery that does not fit into the model, because there is one already.
   *
   * @param aDelivery
   *        The delivery. Never {@code null}.
   * @param aAdditionalTerms
   *        The list to add to. Never {@code null}.
   */
  public static void addLineDelivery (@NonNull final PhinvoiceLineDelivery aDelivery,
                                      @NonNull final List <PhinvoiceAdditionalTerm> aAdditionalTerms)
  {
    addIfPresent ("BT-185", aDelivery.getPartyName (), aAdditionalTerms);
    addIfPresent ("BT-186", aDelivery.getLocationIdentifier (), aAdditionalTerms);
    addIfPresent ("BT-187", aDelivery.getActualDeliveryDate (), aAdditionalTerms);
    addIfPresent ("BG-38", aDelivery.getPostalAddress (), aAdditionalTerms);
  }

  /**
   * Add a contact that does not fit into the model, because there is one already.
   *
   * @param aContact
   *        The contact. Never {@code null}.
   * @param sNameTerm
   *        The key of the label of the name. Never {@code null}.
   * @param sTelephoneTerm
   *        The key of the label of the telephone number. Never {@code null}.
   * @param sEmailTerm
   *        The key of the label of the email address. Never {@code null}.
   * @param aAdditionalTerms
   *        The list to add to. Never {@code null}.
   */
  public static void addContact (@NonNull final PhinvoiceContact aContact,
                                 @NonNull final String sNameTerm,
                                 @NonNull final String sTelephoneTerm,
                                 @NonNull final String sEmailTerm,
                                 @NonNull final List <PhinvoiceAdditionalTerm> aAdditionalTerms)
  {
    addIfPresent (sNameTerm, aContact.getName (), aAdditionalTerms);
    addIfPresent (sTelephoneTerm, aContact.getTelephone (), aAdditionalTerms);
    addIfPresent (sEmailTerm, aContact.getEmail (), aAdditionalTerms);
  }

  /**
   * Add a payment card that does not fit into the model, because there is one already.
   *
   * @param aCard
   *        The payment card. Never {@code null}.
   * @param aAdditionalTerms
   *        The list to add to. Never {@code null}.
   */
  public static void addPaymentCard (@NonNull final PhinvoicePaymentCard aCard,
                                     @NonNull final List <PhinvoiceAdditionalTerm> aAdditionalTerms)
  {
    addIfPresent ("BT-87", aCard.getPan (), aAdditionalTerms);
    addIfPresent ("BT-88", aCard.getHolderName (), aAdditionalTerms);
  }

  /**
   * Add a direct debit that does not fit into the model, because there is one already.
   *
   * @param aDirectDebit
   *        The direct debit. Never {@code null}.
   * @param aAdditionalTerms
   *        The list to add to. Never {@code null}.
   */
  public static void addDirectDebit (@NonNull final PhinvoiceDirectDebit aDirectDebit,
                                     @NonNull final List <PhinvoiceAdditionalTerm> aAdditionalTerms)
  {
    addIfPresent ("BT-89", aDirectDebit.getMandateReferenceID (), aAdditionalTerms);
    addIfPresent ("BT-91", aDirectDebit.getDebitedAccountID (), aAdditionalTerms);
    addIfPresent ("BT-215", aDirectDebit.getDebitedAccountProviderID (), aAdditionalTerms);
    addIfPresent ("BT-216", aDirectDebit.getDebitedAccountName (), aAdditionalTerms);
  }

  /**
   * Take over the bank assigned creditor identifiers (BT-90). The first one belongs to the direct
   * debit if there is one, or if a payment means asks for it. Otherwise, as on a credit transfer
   * invoice, BT-90 alone does not make a direct debit, so it is an additional term of the payment.
   *
   * @param aCreditorIDs
   *        All creditor identifiers of the document. A {@code null} value is skipped. Never
   *        {@code null}.
   * @param bDirectDebitMeans
   *        {@code true} if a payment means is a direct debit.
   * @param aPayment
   *        The payment instructions to fill. Never {@code null}.
   */
  public static void addCreditorIDs (@NonNull final Iterable <String> aCreditorIDs,
                                     final boolean bDirectDebitMeans,
                                     @NonNull final PhinvoicePaymentInstructions aPayment)
  {
    boolean bFirst = true;
    for (final String sCreditorID : aCreditorIDs)
      if (sCreditorID != null)
      {
        if (bFirst && (bDirectDebitMeans || aPayment.getDirectDebit () != null))
        {
          if (aPayment.getDirectDebit () == null)
            aPayment.setDirectDebit (new PhinvoiceDirectDebit ());
          aPayment.getDirectDebit ().setBankAssignedCreditorID (sCreditorID);
        }
        else
          aPayment.additionalTerms ().add (new PhinvoiceAdditionalTerm ("BT-90", sCreditorID));
        bFirst = false;
      }
  }

  /**
   * Determine the document kind from BT-3 (Invoice type code). The EN 16931 code list
   * ({@link EEN16931InvoiceTypeCode}) assigns every code to exactly one kind.
   *
   * @param sTypeCode
   *        BT-3. May be {@code null}.
   * @param eDefaultKind
   *        The kind to use if BT-3 is missing or unknown. For UBL that is the kind of the root
   *        element, CII has only {@link EPhinvoiceDocumentKind#INVOICE} as an option. Never
   *        {@code null}.
   * @param aErrors
   *        The error list that receives a warning for an unknown code. Never {@code null}.
   * @return The document kind. Never {@code null}.
   */
  @NonNull
  public static EPhinvoiceDocumentKind getDocumentKind (@Nullable final String sTypeCode,
                                                    @NonNull final EPhinvoiceDocumentKind eDefaultKind,
                                                    @NonNull final ErrorList aErrors)
  {
    if (sTypeCode == null || sTypeCode.isEmpty ())
      return eDefaultKind;

    final EEN16931InvoiceTypeCode eTypeCode = EEN16931InvoiceTypeCode.getFromIDOrNull (sTypeCode);
    if (eTypeCode != null)
      return eTypeCode.getRole () == EEN16931InvoiceTypeCodeRole.CREDIT_NOTE ? EPhinvoiceDocumentKind.CREDIT_NOTE
                                                                             : EPhinvoiceDocumentKind.INVOICE;

    aErrors.add (SingleError.builderWarn ()
                            .errorText ("Unknown BT-3 invoice type code '" +
                                        PhinvoiceLogHelper.sanitise (sTypeCode) +
                                        "' — defaulting to " +
                                        eDefaultKind)
                            .build ());
    return eDefaultKind;
  }
}
