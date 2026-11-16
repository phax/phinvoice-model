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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * The term IDs of the repeated structures of the Factur-X profile EXTENDED, per position: parties,
 * their contacts and postal addresses, and referenced documents (decisions D10 and D11). The CII
 * reader and writer use them, and the writers of targets without the profile EXTENDED use them to
 * report what they cannot write.
 * The IDs were taken from the Factur-X 1.09.2 workbook (sheet "Factur-X CII D22B EXTENDED") by the
 * XPath of each position. A <code>null</code> ID means that the position has no such term. The
 * EN 16931 terms of the EN 16931 parties are listed for completeness; the readers and writers handle
 * them explicitly.
 *
 * @author Philip Helger
 */
public final class PhinvoiceExtendedTerms
{
  /**
   * The terms of a postal address: the group, post code, line 1, line 2, line 3, city, country code
   * and country subdivision.
   */
  public record AddressTerms (@Nullable String group,
                       @Nullable String postCode,
                       @Nullable String line1,
                       @Nullable String line2,
                       @Nullable String line3,
                       @Nullable String city,
                       @Nullable String country,
                       @Nullable String subdivision)
  {
    /** @return The terms in the order of the address helpers: line 1 to 3, city, post code, subdivision, country */
    @NonNull
    public String [] asArray ()
    {
      return new String [] { line1, line2, line3, city, postCode, subdivision, country };
    }
  }

  /**
   * The terms of a contact: the group, person name, department name, type code, telephone, fax and
   * email.
   */
  public record ContactTerms (@Nullable String group,
                       @Nullable String personName,
                       @Nullable String departmentName,
                       @Nullable String typeCode,
                       @Nullable String telephone,
                       @Nullable String fax,
                       @Nullable String email)
  {}

  /**
   * The terms of a trade party: the group, identifier, global identifier, name, role code,
   * description, legal registration identifier, trading name, legal address, contact, postal
   * address, electronic address, VAT identifier and tax number.
   */
  public record PartyTerms (@Nullable String group,
                     @Nullable String id,
                     @Nullable String globalID,
                     @Nullable String name,
                     @Nullable String roleCode,
                     @Nullable String description,
                     @Nullable String legalID,
                     @Nullable String tradingName,
                     @Nullable AddressTerms legalAddress,
                     @Nullable ContactTerms contact,
                     @Nullable AddressTerms address,
                     @Nullable String electronicAddress,
                     @Nullable String vatID,
                     @Nullable String taxNumber)
  {}

  /**
   * The terms of a referenced document: the group, document reference, line, type code, reference
   * type code, description, external location, document date and attachment.
   */
  public record DocumentTerms (@Nullable String group,
                        @Nullable String issuerAssignedID,
                        @Nullable String lineID,
                        @Nullable String typeCode,
                        @Nullable String referenceTypeCode,
                        @Nullable String name,
                        @Nullable String uri,
                        @Nullable String issueDate,
                        @Nullable String attachment)
  {}

  /** SellerTradeParty */
  public static final PartyTerms SELLER = new PartyTerms ("BG-4",
                                       "BT-29",
                                       null,
                                       "BT-27",
                                       "BT-X-543",
                                       "BT-33",
                                       "BT-30",
                                       "BT-28",
                                       new AddressTerms ("BG-X-14", "BT-X-100", "BT-X-101", "BT-X-102", "BT-X-103", "BT-X-104", "BT-X-105", "BT-X-106"),
                                       new ContactTerms ("BG-6", "BT-41", null, "BT-X-317", "BT-42", "BT-X-107", "BT-43"),
                                       new AddressTerms ("BG-5", "BT-38", "BT-35", "BT-36", "BT-162", "BT-37", "BT-40", "BT-39"),
                                       "BT-34",
                                       "BT-31",
                                       "BT-32");

  /** BuyerTradeParty */
  public static final PartyTerms BUYER = new PartyTerms ("BG-7",
                                       "BT-46",
                                       null,
                                       "BT-44",
                                       "BT-X-544",
                                       "BT-X-334",
                                       "BT-47",
                                       "BT-45",
                                       new AddressTerms ("BG-X-15", "BT-X-108", "BT-X-109", "BT-X-110", "BT-X-111", "BT-X-112", "BT-X-113", "BT-X-114"),
                                       new ContactTerms ("BG-9", "BT-56", null, "BT-X-318", "BT-57", "BT-X-115", "BT-58"),
                                       new AddressTerms ("BG-8", "BT-53", "BT-50", "BT-51", "BT-163", "BT-52", "BT-55", "BT-54"),
                                       "BT-49",
                                       "BT-48",
                                       null);

  /** SellerTaxRepresentativeTradeParty */
  public static final PartyTerms SELLER_TAX_REPRESENTATIVE = new PartyTerms ("BG-11",
                                       "BT-X-116",
                                       "BT-X-117",
                                       "BT-62",
                                       "BT-X-547",
                                       null,
                                       "BT-X-118",
                                       "BT-X-119",
                                       new AddressTerms ("BG-X-59", "BT-X-389", "BT-X-390", "BT-X-391", "BT-X-392", "BT-X-393", "BT-X-394", "BT-X-395"),
                                       new ContactTerms ("BG-X-17", "BT-X-120", "BT-X-121", "BT-X-319", "BT-X-122", "BT-X-123", "BT-X-124"),
                                       new AddressTerms ("BG-12", "BT-67", "BT-64", "BT-65", "BT-164", "BT-66", "BT-69", "BT-68"),
                                       "BT-X-125",
                                       "BT-63",
                                       null);

  /** SalesAgentTradeParty */
  public static final PartyTerms SALES_AGENT = new PartyTerms ("BG-X-49",
                                       "BT-X-337",
                                       "BT-X-338",
                                       "BT-X-335",
                                       "BT-X-545",
                                       null,
                                       "BT-X-339",
                                       "BT-X-336",
                                       new AddressTerms ("BG-X-53", "BT-X-355", "BT-X-356", "BT-X-357", "BT-X-358", "BT-X-359", "BT-X-360", "BT-X-361"),
                                       new ContactTerms ("BG-X-51", "BT-X-342", "BT-X-343", "BT-X-347", "BT-X-344", "BT-X-345", "BT-X-346"),
                                       new AddressTerms ("BG-X-52", "BT-X-348", "BT-X-349", "BT-X-350", "BT-X-351", "BT-X-352", "BT-X-353", "BT-X-354"),
                                       "BT-X-341",
                                       "BT-X-340",
                                       null);

  /** BuyerTaxRepresentativeTradeParty */
  public static final PartyTerms BUYER_TAX_REPRESENTATIVE = new PartyTerms ("BG-X-54",
                                       "BT-X-364",
                                       "BT-X-365",
                                       "BT-X-362",
                                       "BT-X-546",
                                       null,
                                       "BT-X-366",
                                       "BT-X-363",
                                       new AddressTerms ("BG-X-57", "BT-X-382", "BT-X-383", "BT-X-384", "BT-X-385", "BT-X-386", "BT-X-387", "BT-X-388"),
                                       new ContactTerms ("BG-X-55", "BT-X-369", "BT-X-370", "BT-X-371", "BT-X-372", "BT-X-373", "BT-X-374"),
                                       new AddressTerms ("BG-X-56", "BT-X-375", "BT-X-376", "BT-X-377", "BT-X-378", "BT-X-379", "BT-X-380", "BT-X-381"),
                                       "BT-X-368",
                                       "BT-X-367",
                                       null);

  /** ProductEndUserTradeParty */
  public static final PartyTerms PRODUCT_END_USER = new PartyTerms ("BG-X-18",
                                       "BT-X-126",
                                       "BT-X-127",
                                       "BT-X-128",
                                       "BT-X-548",
                                       null,
                                       "BT-X-129",
                                       "BT-X-130",
                                       new AddressTerms ("BG-X-60", "BT-X-396", "BT-X-397", "BT-X-398", "BT-X-399", "BT-X-400", "BT-X-401", "BT-X-402"),
                                       new ContactTerms ("BG-X-20", "BT-X-131", "BT-X-132", "BT-X-320", "BT-X-133", "BT-X-134", "BT-X-135"),
                                       new AddressTerms ("BG-X-21", "BT-X-136", "BT-X-137", "BT-X-138", "BT-X-139", "BT-X-140", "BT-X-141", "BT-X-142"),
                                       "BT-X-143",
                                       "BT-X-144",
                                       null);

  /** BuyerAgentTradeParty */
  public static final PartyTerms BUYER_AGENT = new PartyTerms ("BG-X-62",
                                       "BT-X-408",
                                       "BT-X-409",
                                       "BT-X-406",
                                       "BT-X-549",
                                       null,
                                       "BT-X-410",
                                       "BT-X-407",
                                       new AddressTerms ("BG-X-66", "BT-X-426", "BT-X-427", "BT-X-428", "BT-X-429", "BT-X-430", "BT-X-431", "BT-X-432"),
                                       new ContactTerms ("BG-X-64", "BT-X-413", "BT-X-414", "BT-X-415", "BT-X-416", "BT-X-417", "BT-X-418"),
                                       new AddressTerms ("BG-X-65", "BT-X-419", "BT-X-420", "BT-X-421", "BT-X-422", "BT-X-423", "BT-X-424", "BT-X-425"),
                                       "BT-X-412",
                                       "BT-X-411",
                                       null);

  /** ShipToTradeParty */
  public static final PartyTerms SHIP_TO = new PartyTerms ("BG-13",
                                       "BT-71",
                                       null,
                                       "BT-70",
                                       "BT-X-550",
                                       null,
                                       "BT-X-153",
                                       "BT-X-154",
                                       new AddressTerms ("BG-X-67", "BT-X-433", "BT-X-434", "BT-X-435", "BT-X-436", "BT-X-437", "BT-X-438", "BT-X-439"),
                                       new ContactTerms ("BG-X-26", "BT-X-155", "BT-X-156", "BT-X-321", "BT-X-157", "BT-X-158", "BT-X-159"),
                                       new AddressTerms ("BG-15", "BT-78", "BT-75", "BT-76", "BT-165", "BT-77", "BT-80", "BT-79"),
                                       "BT-X-160",
                                       "BT-X-161",
                                       null);

  /** UltimateShipToTradeParty */
  public static final PartyTerms ULTIMATE_SHIP_TO = new PartyTerms ("BG-X-27",
                                       "BT-X-162",
                                       "BT-X-163",
                                       "BT-X-164",
                                       "BT-X-551",
                                       null,
                                       "BT-X-165",
                                       "BT-X-166",
                                       new AddressTerms ("BG-X-68", "BT-X-440", "BT-X-441", "BT-X-442", "BT-X-443", "BT-X-444", "BT-X-445", "BT-X-446"),
                                       new ContactTerms ("BG-X-28", "BT-X-167", "BT-X-168", "BT-X-322", "BT-X-169", "BT-X-170", "BT-X-171"),
                                       new AddressTerms ("BG-X-29", "BT-X-172", "BT-X-173", "BT-X-174", "BT-X-175", "BT-X-176", "BT-X-177", "BT-X-178"),
                                       "BT-X-179",
                                       "BT-X-180",
                                       null);

  /** ShipFromTradeParty */
  public static final PartyTerms SHIP_FROM = new PartyTerms ("BG-X-30",
                                       "BT-X-181",
                                       "BT-X-182",
                                       "BT-X-183",
                                       "BT-X-552",
                                       null,
                                       "BT-X-184",
                                       "BT-X-185",
                                       new AddressTerms ("BG-X-69", "BT-X-447", "BT-X-448", "BT-X-449", "BT-X-450", "BT-X-451", "BT-X-452", "BT-X-453"),
                                       new ContactTerms ("BG-X-31", "BT-X-186", "BT-X-187", "BT-X-323", "BT-X-188", "BT-X-189", "BT-X-190"),
                                       new AddressTerms ("BG-X-32", "BT-X-191", "BT-X-192", "BT-X-193", "BT-X-194", "BT-X-195", "BT-X-196", "BT-X-197"),
                                       "BT-X-198",
                                       "BT-X-199",
                                       null);

  /** InvoicerTradeParty */
  public static final PartyTerms INVOICER = new PartyTerms ("BG-X-33",
                                       "BT-X-205",
                                       "BT-X-206",
                                       "BT-X-207",
                                       "BT-X-553",
                                       null,
                                       "BT-X-208",
                                       "BT-X-209",
                                       new AddressTerms ("BG-X-70", "BT-X-454", "BT-X-455", "BT-X-456", "BT-X-457", "BT-X-458", "BT-X-459", "BT-X-460"),
                                       new ContactTerms ("BG-X-34", "BT-X-210", "BT-X-211", "BT-X-324", "BT-X-212", "BT-X-213", "BT-X-214"),
                                       new AddressTerms ("BG-X-35", "BT-X-215", "BT-X-216", "BT-X-217", "BT-X-218", "BT-X-219", "BT-X-220", "BT-X-221"),
                                       "BT-X-222",
                                       "BT-X-223",
                                       null);

  /** InvoiceeTradeParty */
  public static final PartyTerms INVOICEE = new PartyTerms ("BG-X-36",
                                       "BT-X-224",
                                       "BT-X-225",
                                       "BT-X-226",
                                       "BT-X-554",
                                       null,
                                       "BT-X-227",
                                       "BT-X-228",
                                       new AddressTerms ("BG-X-71", "BT-X-461", "BT-X-462", "BT-X-463", "BT-X-464", "BT-X-465", "BT-X-466", "BT-X-467"),
                                       new ContactTerms ("BG-X-37", "BT-X-229", "BT-X-230", "BT-X-325", "BT-X-231", "BT-X-232", "BT-X-233"),
                                       new AddressTerms ("BG-X-38", "BT-X-234", "BT-X-235", "BT-X-236", "BT-X-237", "BT-X-238", "BT-X-239", "BT-X-240"),
                                       "BT-X-241",
                                       "BT-X-242",
                                       null);

  /** PayeeTradeParty */
  public static final PartyTerms PAYEE = new PartyTerms ("BG-10",
                                       "BT-60",
                                       null,
                                       "BT-59",
                                       "BT-X-468",
                                       null,
                                       "BT-61",
                                       "BT-X-243",
                                       new AddressTerms ("BG-X-72", "BT-X-469", "BT-X-470", "BT-X-471", "BT-X-472", "BT-X-473", "BT-X-474", "BT-X-475"),
                                       new ContactTerms ("BG-X-39", "BT-X-244", "BT-X-245", "BT-X-326", "BT-X-246", "BT-X-247", "BT-X-248"),
                                       new AddressTerms ("BG-X-40", "BT-X-249", "BT-X-250", "BT-X-251", "BT-X-252", "BT-X-253", "BT-X-254", "BT-X-255"),
                                       "BT-X-256",
                                       "BT-X-257",
                                       null);

  /** PayerTradeParty */
  public static final PartyTerms PAYER = new PartyTerms ("BG-X-73",
                                       "BT-X-478",
                                       "BT-X-479",
                                       "BT-X-476",
                                       "BT-X-483",
                                       null,
                                       "BT-X-480",
                                       "BT-X-477",
                                       new AddressTerms ("BG-X-76", "BT-X-497", "BT-X-498", "BT-X-499", "BT-X-500", "BT-X-501", "BT-X-502", "BT-X-503"),
                                       new ContactTerms ("BG-X-74", "BT-X-484", "BT-X-485", "BT-X-486", "BT-X-487", "BT-X-488", "BT-X-489"),
                                       new AddressTerms ("BG-X-75", "BT-X-490", "BT-X-491", "BT-X-492", "BT-X-493", "BT-X-494", "BT-X-495", "BT-X-496"),
                                       "BT-X-482",
                                       "BT-X-481",
                                       null);

  /** PayeeTradeParty */
  public static final PartyTerms PAYMENT_TERMS_PAYEE = new PartyTerms ("BG-X-77",
                                       "BT-X-506",
                                       "BT-X-507",
                                       "BT-X-504",
                                       "BT-X-511",
                                       null,
                                       "BT-X-508",
                                       "BT-X-505",
                                       new AddressTerms ("BG-X-80", "BT-X-525", "BT-X-526", "BT-X-527", "BT-X-528", "BT-X-529", "BT-X-530", "BT-X-531"),
                                       new ContactTerms ("BG-X-78", "BT-X-512", "BT-X-513", "BT-X-514", "BT-X-515", "BT-X-516", "BT-X-517"),
                                       new AddressTerms ("BG-X-79", "BT-X-518", "BT-X-519", "BT-X-520", "BT-X-521", "BT-X-522", "BT-X-523", "BT-X-524"),
                                       "BT-X-510",
                                       "BT-X-509",
                                       null);

  /** ItemSellerTradeParty */
  public static final PartyTerms LINE_SELLER = new PartyTerms ("BG-X-90",
                                       "BT-X-567",
                                       "BT-X-568",
                                       "BT-X-569",
                                       "BT-X-570",
                                       "BT-X-571",
                                       "BT-X-572",
                                       "BT-X-573",
                                       null,
                                       new ContactTerms ("BG-X-91", "BT-X-574", "BT-X-574-1", "BT-X-575", "BT-X-576", "BT-X-577", "BT-X-578"),
                                       new AddressTerms ("BG-X-92", "BT-X-579", "BT-X-580", "BT-X-581", "BT-X-582", "BT-X-583", "BT-X-584", "BT-X-585"),
                                       "BT-X-586",
                                       "BT-X-587",
                                       "BT-X-588");

  /** ShipToTradeParty */
  public static final PartyTerms LINE_SHIP_TO = new PartyTerms ("BG-X-7",
                                       "BT-186-00",
                                       "BT-186",
                                       "BT-X-50",
                                       "BT-X-541",
                                       null,
                                       "BT-X-51",
                                       "BT-X-52",
                                       null,
                                       new ContactTerms ("BG-X-8", "BT-X-54", "BT-X-54-1", "BT-X-315", "BT-X-55", "BT-X-56", "BT-X-57"),
                                       new AddressTerms ("BG-X-9", "BT-X-58", "BT-X-59", "BT-X-60", "BT-X-61", "BT-X-62", "BT-X-63", "BT-X-64"),
                                       "BT-X-65",
                                       "BT-X-66",
                                       null);

  /** UltimateShipToTradeParty */
  public static final PartyTerms LINE_ULTIMATE_SHIP_TO = new PartyTerms ("BG-X-10",
                                       "BT-X-67",
                                       "BT-X-68",
                                       "BT-X-69",
                                       "BT-X-542",
                                       null,
                                       "BT-X-70",
                                       "BT-X-71",
                                       null,
                                       new ContactTerms ("BG-X-11", "BT-X-72", "BT-X-72-1", "BT-X-316", "BT-X-73", "BT-X-74", "BT-X-75"),
                                       new AddressTerms ("BG-X-12", "BT-X-76", "BT-X-77", "BT-X-78", "BT-X-79", "BT-X-80", "BT-X-81", "BT-X-82"),
                                       "BT-X-83",
                                       "BT-X-84",
                                       null);

  /** ManufacturerTradeParty */
  public static final PartyTerms MANUFACTURER = new PartyTerms ("BG-X-93",
                                       "BT-X-593",
                                       "BT-X-594",
                                       "BT-X-595",
                                       "BT-X-596",
                                       "BT-X-597",
                                       "BT-X-598",
                                       "BT-X-599",
                                       null,
                                       new ContactTerms ("BG-X-94", "BT-X-600", "BT-X-601", "BT-X-602", "BT-X-603", "BT-X-604", "BT-X-605"),
                                       new AddressTerms ("BG-X-95", "BT-X-606", "BT-X-607", "BT-X-608", "BT-X-609", "BT-X-610", "BT-X-611", "BT-X-612"),
                                       "BT-X-613",
                                       "BT-X-614",
                                       "BT-X-615");

  /** QuotationReferencedDocument */
  public static final DocumentTerms QUOTATION = new DocumentTerms ("BG-X-61", "BT-X-403", null, null, null, null, null, "BT-X-404", null);

  /** UltimateCustomerOrderReferencedDocument */
  public static final DocumentTerms ULTIMATE_CUSTOMER_ORDER = new DocumentTerms ("BG-X-23", "BT-X-150", null, null, null, null, null, "BT-X-151", null);

  /** QuotationReferencedDocument (line) */
  public static final DocumentTerms LINE_QUOTATION = new DocumentTerms ("BG-X-47", "BT-X-310", "BT-X-311", null, null, null, null, "BT-X-312", null);

  /** ContractReferencedDocument (line) */
  public static final DocumentTerms LINE_CONTRACT = new DocumentTerms ("BG-X-2", "BT-X-24", "BT-X-25", null, null, null, null, "BT-X-26", null);

  /** AdditionalReferencedDocument (line) */
  public static final DocumentTerms LINE_ADDITIONAL = new DocumentTerms ("BG-X-3", "BT-X-27", "BT-X-29", "BT-X-30", "BT-X-32", "BT-X-299", "BT-X-28", "BT-X-33", "BT-X-31");

  /** UltimateCustomerOrderReferencedDocument (line) */
  public static final DocumentTerms LINE_ULTIMATE_CUSTOMER_ORDER = new DocumentTerms ("BG-X-5", "BT-X-43", "BT-X-44", null, null, null, null, "BT-X-45", null);

  /** SellerOrderReferencedDocument (line) */
  public static final DocumentTerms LINE_SALES_ORDER = new DocumentTerms ("BG-X-81", "BT-X-537", "BT-X-538", null, null, null, null, "BT-X-539", null);

  /** BuyerOrderReferencedDocument (line) */
  public static final DocumentTerms LINE_BUYER_ORDER = new DocumentTerms (null, "BT-X-21", "BT-132", null, null, null, null, "BT-X-22", null);

  /** DespatchAdviceReferencedDocument (line) */
  public static final DocumentTerms LINE_DESPATCH_ADVICE = new DocumentTerms ("BG-X-13", "BT-X-86", "BT-X-87", null, null, null, null, "BT-X-88", null);

  /** ReceivingAdviceReferencedDocument (line) */
  public static final DocumentTerms LINE_RECEIVING_ADVICE = new DocumentTerms ("BG-X-82", "BT-X-89", "BT-X-90", null, null, null, null, "BT-X-91", null);

  /** DeliveryNoteReferencedDocument (line) */
  public static final DocumentTerms LINE_DELIVERY_NOTE = new DocumentTerms ("BG-X-83", "BT-X-92", "BT-X-93", null, null, null, null, "BT-X-94", null);

  /** InvoiceReferencedDocument (line) */
  public static final DocumentTerms LINE_PRECEDING_INVOICE = new DocumentTerms ("BG-X-48", "BT-X-331", "BT-X-540", "BT-X-332", null, null, null, "BT-X-333", null);

  /** InvoiceSpecifiedReferencedDocument */
  public static final DocumentTerms ADVANCE_PAYMENT_INVOICE = new DocumentTerms ("BG-X-85", "BT-X-558", null, "BT-X-559", null, null, null, "BT-X-560", null);

  private PhinvoiceExtendedTerms ()
  {}
}
