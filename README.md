# phinvoice-model

A syntax neutral, writable Java model for electronic invoices and credit notes.

> **Work in progress.** The project is at an early stage and not released yet. See [PLAN.md](PLAN.md)
> for the scope, the decisions and the phases.

* Covers the EN 16931 semantic model in both editions (EN 16931:2017 and EN 16931:2026) plus all
  terms of ZUGFeRD 2.5.2 / Factur-X 1.09.2 EXTENDED.
* Reads and writes CII (D16B, D22B, D25A) and UBL (2.1, 2.5; Invoice and Credit Note).
* Writing to a target format (syntax, edition, profile) only writes what the target allows and
  reports everything else.
* Re-calculation of all totals with configurable rounding and aggregation modes - planned.
* Anonymisation of invoices - planned.

This project is part of my e-invoicing stack. See https://github.com/phax/peppol for other components
and libraries in that area.

## Modules

| Module | Purpose |
|--------|---------|
| `phinvoice-model-bom` | Bill of Materials |
| `phinvoice-model-api` | The syntax neutral invoice model, the term registry, the export target and the write filter |
| `phinvoice-model-testfiles` | Example invoices for the tests |
| `phinvoice-model-cii` | Reader and writer for CII (D16B, D22B, D25A) |
| `phinvoice-model-ubl` | Reader and writer for UBL (2.1, 2.5; Invoice and Credit Note) |

## Building

Requires Java 17 or higher and Maven 3.x.

```bash
mvn clean install
```

# News and noteworthy

v0.1.0 - work in progress
* Initial version with the EN 16931 model (2017 and 2026 editions) in `phinvoice-model-api`, taken
  over from the phlox intermediate model
* Added the term registry `PhinvoiceTermRegistry` with all BG / BT of EN 16931:2017 and EN 16931:2026 and all BG-X / BT-X of Factur-X 1.09.2 EXTENDED
* Added the export target `PhinvoiceExportTarget` (syntax × edition × profile) and the Factur-X profiles `EPhinvoiceProfile`
* Added the CII reader (taken over from phlox, reading D16B and D22B with the D22B classes) and the CII writer `CiiInvoiceWriter` for all EN 16931:2017 profiles (D22B) and for EN 16931:2026 (D25A)
* Added `PhinvoiceTermFilter`, which reports every value that a writer does not write for its target
* Added `PhinvoiceContact.departmentName` and `PhinvoiceItemAttribute.numericValue`
* Added the UBL reader (taken over from phlox) and the UBL writer `UblInvoiceWriter` for UBL 2.1 (EN 16931:2017) and UBL 2.5 (EN 16931:2026), Invoice and Credit Note
* Added all terms of Factur-X 1.09.2 EXTENDED to the model, with the reusable classes `PhinvoiceTradeParty` and `PhinvoiceReferencedDocument`; the CII D22B reader and writer read and write them
* The CII and UBL readers keep every value of the source: a value without model field becomes an additional term with its XPath and is reported as warning
* The CII reader auto-detects Factur-X MINIMUM and BASIC WL documents as EN 16931:2017
* The CII D25A writer writes the list agency of the non-VAT tax code (BT-177, BT-193), and the UBL 2.5 writer the list of the supporting document type code (BT-122-1-1)
* The UBL writers and the CII D25A writer report all values of Factur-X EXTENDED (`PhinvoiceExtendedTermReporter`)
* `PhinvoicePriceDetails` has lists of allowances and charges on the gross price (`PhinvoicePriceAllowanceCharge`); BT-147 is the amount of the first allowance
* Added `PhinvoicePaymentTerms.dueDate` for the due dates of further payment terms in Factur-X EXTENDED

---

My personal [Coding Styleguide](https://github.com/phax/meta/blob/master/CodingStyleguide.md) |
It is appreciated if you star the GitHub project if you like it.
