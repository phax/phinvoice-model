# phinvoice-model - Plan

A syntax neutral, writable Java model for electronic invoices and credit notes that covers the
EN 16931 semantic model plus all terms of ZUGFeRD / Factur-X EXTENDED, with readers and writers
for CII and UBL, a re-calculation module and an anonymiser.

Status: phases 0 to 4 done (2026-11-15), phase 5 in progress.

## 1. Goals and non-goals

| In scope | Out of scope |
|----------|--------------|
| EN 16931:2017 (UBL 2.1, CII D16B) and EN 16931:2026 (UBL 2.5, CII D25A) | Other syntaxes (ebInterface, fatturaPA, Facturae, KSeF, ...) |
| All terms of ZUGFeRD 2.5.2 / Factur-X 1.09.2 EXTENDED (CII D22B) | Order-X, Deliver-X, ZUGFeRD 1.0, ZUGFeRD 1 to 2 upgrade |
| Read and write CII and UBL (Invoice and Credit Note) | PDF and hybrid handling - that is done by `kaltblut` (read) and `phlox` (write) |
| Writing to a target format implies the reduction to that format ("implicit purification") | Business rule validation at runtime - `phive-rules` is used in tests only |
| Re-calculation of all totals with configurable rounding and aggregation | Replacing `en16931-cii2ubl` / `en16931-ubl2cii` - they stay independent (used in production, plain EN 16931 only) |
| Anonymisation of the model (like `ph-redact`) | |
| Becoming the model used by `phlox` eventually | |

The leading use case:

```
EXTENDED CII invoice ──read──▶ model ──re-calculate──▶ model ──write (target: EN 16931:2017, UBL 2.1 Invoice)──▶ UBL
                                                                 └─ every term the target cannot carry is reported
```

## 2. Decisions taken

| # | Decision |
|---|----------|
| D1 | Both EN 16931 editions (2017 and 2026) are covered by one model - a superset, like `phlox`. Terms that only exist in 2026 stay empty for 2017 documents. |
| D2 | The extension basis is plain EXTENDED of ZUGFeRD 2.5.2 / Factur-X 1.09.2. The term registry is built so that EXTENDED-CTC-FR and EXTENDED-B2B-FR can be added later. |
| D3 | Re-calculation is a separate module, with selectable rounding modes and aggregation modes, depending on the currency. |
| D4 | Hand-written model classes like in `phlox`: mutable beans with fluent setters (`setX (...)` returning `this`), no code generator. |
| D5 | `en16931-cii2ubl` and `en16931-ubl2cii` stay independent. phinvoice-model has its own readers and writers for both syntaxes in both directions (model → CII and model → UBL). |
| D6 | No separate purifier step: a writer is created for a *target* (syntax × edition × profile) and only writes what the target allows. Everything it drops is reported. A reduction of the model in memory (`reduceTo (target)`) is offered as well, based on the same term registry. |
| D7 | Maven coordinates: groupId `com.helger.phinvoice`, artefacts `phinvoice-model-*`. Folder `~/dev/git/phinvoice`. |
| D8 | The EN 16931 part of the model starts as a copy of the `phlox-api` model (`com.helger.phlox.api.model`, v0.6.2-SNAPSHOT), renamed from `Phlox*` to `Phinvoice*`. This keeps the model a superset of the phlox model from day one, which is the precondition for the later migration of phlox. |
| D10 | EXTENDED in the model with reusable classes (decision of 2026-11-16): a generic `PhinvoiceTradeParty` for the 14 additional parties (ship-from, invoicer, invoicee, payer, sales agent, buyer agent, line seller, manufacturer, ...), `PhinvoiceContact` (+ fax, + type code) and `PhinvoicePostalAddress` reused, a generic `PhinvoiceReferencedDocument`, own classes only for the one-off groups (advance payment, logistics service charge, delivery terms, product instance, included product, ...); the BT-X on EN 16931 groups become fields of the existing classes. This replaces the literal "one class per BG-X" of D4 for the EXTENDED part. |
| D11 | The term IDs are explicit in the reader and writer code, also for the EXTENDED terms (decision of 2026-11-16). |
| D12 | Safety net of the readers (decision of 2026-11-16): every value of the source that the model has no field for becomes a `PhinvoiceAdditionalTerm` with its XPath (prefix `cii:`), so that reading never loses data, also beyond EXTENDED. CII: `CiiUnmappedValueCollector` writes the model again (raw writer, widest target of the edition, no XSD validation) and takes every source value that is not written at the same path or below the same grandparent element. `CiiReadCoverageTest` asserts that every source value is in the model. |
| D13 | Where the official Factur-X EXTENDED samples repeat what the workbook gives as `0..1` (decision of 2026-11-16): a payment terms has its own due date (`PhinvoicePaymentTerms.dueDate`; the due date of the first payment terms with one stays the invoice BT-9), and the gross price has lists of allowances and charges (`PhinvoicePriceDetails.allowances ()` / `charges ()` of the reusable `PhinvoicePriceAllowanceCharge`; BT-147 is the amount of the first allowance, `getDiscount` / `setDiscount` delegate to it). |
| D9 | Like D8, the CII and UBL readers, the read helpers (`IPhinvoiceReader`, `PhinvoiceReaderHelper`, `PhinvoiceLogHelper`) and the test corpus (`phlox-testfiles` → `phinvoice-model-testfiles`) are taken over from phlox. The readers are made faithful where phlox merged values for display: contact person and department (`PhinvoiceContact.departmentName`, BT-41-0 / BT-56-0) and text and numeric item attribute value (`PhinvoiceItemAttribute.numericValue`) are kept apart. phlox has to merge them itself after the migration. |

## 3. Size of the model (measured)

Source: `phive-rules/phive-rules-zugferd/docs/2.5.2/ZUGFeRD_2.5.2_EN.zip` →
`Documentation/1_FACTUR-X 1.09.2 - EN FR.xlsx`, sheet `Factur-X CII D22B EXTENDED`, column `ID`:

| | Count |
|---|---|
| EN 16931 business groups (BG) | 33 |
| EN 16931 business terms (BT-n, leaf) | ~175 |
| EXTENDED-only business groups (BG-X) | 93 |
| EXTENDED-only business terms (BT-X, leaf) | ~609 |

The EXTENDED part is roughly 3.5 times the EN 16931 core. The workbook also holds per term the
CII XPath, the cardinality, the data type and the profile membership (columns MINIMUM ... EXTENDED),
and the parent / child relation of the groups. It is the primary source for the BG-X / BT-X part of
the model and for the term registry.

Further sources:

* EN 16931 2017 and 2026 syntax bindings: `en16931-cii2ubl/docs/en16931-2017-syntax.md` and
  `en16931-cii2ubl/docs/en16931-2026-syntax.md`
* The phlox domain model: `phlox.wiki/Domain-Model.md`
* Code lists: `en16931-basics` (`EEN16931…Code` enums), `ph-codelists-unit` (units)

## 4. Module layout

Conventions like `phlox`: `com.helger:parent-pom` 3.2.0, Java 17, JUnit 4, ph-commons 12,
JSpecify, forbiddenapis with `ph-forbidden-apis`.

| Module | Content | Depends on |
|--------|---------|------------|
| `phinvoice-model-parent-pom` | Root POM | - |
| `phinvoice-model-bom` | Bill of Materials | - |
| `phinvoice-model-api` | The model (BG / BT and BG-X / BT-X), `EPhinvoiceProfile`, the export target, the term registry, `PhinvoiceAdditionalTerm` | `en16931-basics`, `ph-codelists-unit` |
| `phinvoice-model-testfiles` | Test invoices (regular jar, consumed in scope `test`) | - |
| `phinvoice-model-cii` | Reader and writer CII D16B / D22B / D25A | `ph-cii`, api |
| `phinvoice-model-ubl` | Reader and writer UBL 2.1 / 2.5, Invoice and Credit Note | `ph-ubl`, api |
| `phinvoice-model-calc` | Re-calculation (line amounts, allowances and charges, VAT breakdown, document totals) | api, `ph-masterdata` (currency scales) |
| `phinvoice-model-redact` | Anonymisation of the model | api |
| `phinvoice-model-cli` (optional, later) | picocli: `convert`, `recalc`, `redact` | all |

Dependency rules: readers and writers only depend on `-api`, never on each other; `calc` and
`redact` only work on the model; only the CLI wires modules together. Modules are added to the
reactor when they get content.

## 5. Model design

* One class per BG and BG-X, one field per BT and BT-X. BG-X groups are nested where the workbook
  puts them (parent / child columns), not in a separate "extension" object, so that a consumer like
  phlox can address them in the same way as EN 16931 terms.
* The Javadoc of every getter and setter names the BT IDs (as in phlox).
* Collections are `ICommonsList`, never `null`. Absent single values and absent optional groups are
  `null`.
* Data types (from phlox): identifier = `String` or `PhinvoiceIdentifier` (value + scheme), amount /
  quantity / percentage = `BigDecimal`, date = `LocalDate`, codes = `String`, binary =
  `byte []` + MIME type + file name.
* The model does not validate. It may hold an invalid invoice - validation is the job of
  `phive-rules`.
* `PhinvoiceAdditionalTerm` holds values that have no field (e.g. the second occurrence of a 0..1
  element in a broken input), so that nothing gets lost on reading. Writers do not write them and
  report them.
* The term registry (`phinvoice-model-api`, `PhinvoiceTermRegistry`): BT / BG ID → name, data
  type, and per edition the cardinality, the lowest profile and whether UBL / CII can carry it.
  Purpose: the implicit purification of the writers, `reduceTo (target)`, the drop report and the
  test coverage. Implemented as a generated resource `phinvoice-model/terms.tsv`, created by
  `MainCreateTermRegistry` (test sources, uses `ph-poi` in scope `test`) from the Factur-X workbook
  and the two CEN syntax binding documents. Rules of the generator:
  * Workbook rows: all BG / BG-X, and all BT / BT-X marked "BT = YES". Skipped are the syntax
    constants: date `@format` codes, the tax scheme identifiers (`VA` / `FC`), BT-110-0 / BT-111-0.
    Included although not marked: BT-186-00, the ID of the ship to party of a line, which the
    workbook puts in a row with the ID of a structural row.
  * EN 16931:2017: the workbook is the list of terms (every term of the 2017 syntax binding is in
    it); cardinality and name come from the syntax binding where present; UBL = the term is in the
    2017 syntax binding; CII = always.
  * EN 16931:2026: the terms of the 2026 syntax binding minus an explicit list of syntax constants
    (format codes, fixed type codes, fixed scheme identifiers, the allowance / charge indicators);
    lowest profile is always EN 16931.
  * Data types only exist for terms in the workbook; the terms new in 2026 have none.
  * Result: 1052 terms, 997 in EN 16931:2017 (incl. EXTENDED), 282 in EN 16931:2026.

### Export target

```
PhinvoiceExportTarget = syntax (CII | UBL Invoice | UBL Credit Note)
                      × edition (EN 16931:2017 | EN 16931:2026)
                      × profile (MINIMUM | BASIC WL | BASIC | EN 16931 | EXTENDED)
```

Not every combination is valid: UBL and the 2026 edition only allow the EN 16931 profile; MINIMUM
and BASIC WL are CII only. Implemented as `PhinvoiceExportTarget` (package `com.helger.phinvoice.target`),
using `EEN16931SyntaxKind` and `EEN16931Edition` from `en16931-basics` and the new
`EPhinvoiceProfile`. BT-24 of a target: the edition's identifier for the profile EN 16931, the
Factur-X identifier of the profile otherwise. XRechnung is a CIUS on EN 16931 and is expressed by BT-24 and is no
separate profile.

## 6. Reading and writing

| Direction | Approach |
|-----------|----------|
| CII → model | `ph-cii`; `CiiD22BInvoiceReader` on the D22B JAXB classes reads D16B and D22B (same namespace, D22B is a superset - the whole D16B corpus reads unchanged); `CiiD25AInvoiceReader` for 2026. The edition comes from BT-24 (`EEN16931Edition`), or is fixed with `CiiInvoiceReader.fromD22B ()` / `fromD25A ()`. |
| model → CII | `CiiInvoiceWriter` for a CII target. EN 16931:2017 (all profiles): `CiiD22BInvoiceWriter` on the D22B JAXB classes; up to the profile EN 16931 the output is valid against the D16B XSD (checked with `phive-rules-en16931` in the tests). EN 16931:2026: `CiiD25AInvoiceWriter`. Every value goes through `PhinvoiceTermFilter`. For the profile EXTENDED the D22B writer writes all BG-X / BT-X, with the term IDs of `PhinvoiceExtendedTerms` (package `com.helger.phinvoice.term`) for the repeated structures; the terms of EN 16931:2026 that Factur-X EXTENDED has as BT-X (e.g. BT-197 as BT-X-202, BG-39 as BG-X-48) are written as such, and reported with the 2026 term ID for the other profiles. BT-24 of the model is kept if it fits the target (the edition identifier or a CIUS of it for EN 16931, the Factur-X identifier for the other profiles), otherwise it is replaced and the change is reported. |
| UBL → model | `ph-ubl` 2.1 and 2.5, Invoice and Credit Note; `UblInvoiceReader` (`Ubl21InvoiceReader`, `Ubl25InvoiceReader`; taken over from phlox, D9) |
| model → UBL | `UblInvoiceWriter` with `Ubl21InvoiceWriter` (2017) and `Ubl25InvoiceWriter` (2026). Invoice or Credit Note from the syntax kind of the target; `UblInvoiceWriter.getSyntaxKind (invoice)` chooses it from BT-3, a mismatch is reported. BT-X terms that UBL cannot carry are dropped and reported (`PhinvoiceExtendedTermReporter`, package `com.helger.phinvoice.write`, also used by the CII D25A writer: leaf values with their term ID, an EXTENDED structure once with the ID of its group); where UBL 2.x has a schema valid and semantically matching element for a BT-X, a later phase may map it (opt-in, because the result is no longer pure EN 16931). |
| Fillers | UBL and CII require some elements that the model may not have. The writers then write a filler, never a calculated value: UBL `TaxTotal/TaxAmount` 0 without BT-110 (like en16931-cii2ubl), `CardAccount/NetworkID`, `OrderReference/ID` and the line IDs of line references `NA` (the readers skip `NA`), the position as BT-179-1 of a UBL 2.5 collection line, BT-11 as `ProjectName` in CII without BT-11-1, a price allowance amount of 0 in UBL for a gross price without discount. |
| Syntax specific rules | CII: BT-6 equal to BT-5 is not written (the CII version of BR-53, like en16931-ubl2cii), and then neither BT-111; BT-147 without BT-148 cannot be written. UBL: a BT-32 scheme `VAT` is not written as tax scheme. The contact point: CII has person name and department name (BT-41-0 / BT-56-0), UBL one name - the department name is used if there is no person name (like en16931-cii2ubl), otherwise it is reported. |
| Comparison with the converters | Intended differences to en16931-cii2ubl / en16931-ubl2cii: nothing is calculated or corrected (the converters swap the signs of a negative price, and calculate the gross price from net price and discount); we keep values the converters drop (all seller identifiers, BT-89 / BT-90, rounding amount 0, tax total 0, BT-32-1, the BG-33 structure); the net price base quantity is BT-149 (cii2ubl takes the gross one). |
| Drop report | An `ErrorList` entry per dropped value: level warning, the term ID as field name, the value and the reason (`PhinvoiceTermFilter`, package `com.helger.phinvoice.write`). A group that the target profile does not contain (e.g. BG-25 for MINIMUM) is reported once. The values of `PhinvoiceAdditionalTerm` are always reported. The writers never drop silently. |

## 7. Re-calculation (`phinvoice-model-calc`)

Calculates from the line level upwards and overwrites the calculated values in the model:

* line: BT-131 (line net amount) from quantity, net price, base quantity, line allowances and charges
* document: BG-23 VAT breakdown (BT-116, BT-117), BG-22 totals (BT-106 ... BT-115)
* EXTENDED: the additional line and document totals of EXTENDED (BT-X terms) where they are derived
  values

Configuration (`PhinvoiceCalcSettings`):

| Setting | Values | Remarks |
|---------|--------|---------|
| Rounding mode | `java.math.RoundingMode` (default `HALF_UP`) | |
| Amount scale | from the currency (BT-5) via `ph-masterdata` `CurrencyHelper.getScale (ECurrency)`, optionally overridden | EN 16931 restricts amounts to 2 decimals (`BR-DEC-*`); for currencies with 3 decimals the effective scale is capped to 2 unless the target is EXTENDED - to be verified against the EXTENDED rules |
| Unit price scale | separate (default: unchanged) | unit prices may carry more decimals |
| VAT aggregation | `PER_LINE` (round the VAT of every line, then sum), `PER_CATEGORY` (sum the net amounts per VAT category and rate, then calculate and round - the EN 16931 way, `BR-CO-17`) | |
| VAT accounting currency | BT-6 / BT-111 with an exchange rate (BT-167 in 2026) | |
| Tolerance | max. deviation before a recalculated value is reported as different from the original | |

Each value that changes is reported (term ID, old value, new value), so the re-calculation is
auditable. The rules are checked in tests with `phive-rules-en16931` / `phive-rules-zugferd`
(arithmetic rules `BR-CO-*`).

## 8. Anonymisation (`phinvoice-model-redact`)

The categories and the replacement values of `ph-redact` (party names, contacts, addresses, party
and tax identifiers, IBAN / BIC, payment cards, document IDs, payment references, notes, URIs,
binary attachments, document descriptions, accounting references) with a configurable prefix.
Amounts, tax data, codes, quantities and dates are kept. In addition all BT-X terms of the same
categories are covered, which the XSLT of `ph-redact` does not know.

Difference to `ph-redact`: works on the model, so the output syntax is free; the term registry
carries the category of each term.

## 9. Tests (`phive-rules` only in scope `test`)

| Test | Checks |
|------|--------|
| Round trip CII → model → CII | semantically equal (normalised XML diff); valid against the XSD and against `phive-rules-zugferd` for the profile of the source |
| Round trip UBL → model → UBL | semantically equal; valid against `phive-rules-en16931` |
| Cross syntax CII ↔ UBL | valid against `phive-rules-en16931`; the drop report contains exactly the expected terms; output compared with `en16931-cii2ubl` / `en16931-ubl2cii` for plain EN 16931 input |
| No data lost on reading | every value of every test file is found in the model (pattern of `PdfAllDataShownTest` in phlox); every XPath is proven once with a changed value |
| Implicit purification | EXTENDED input written to each target is valid for that target |
| Re-calculation | recalculated test files stay valid; deliberately broken totals are repaired; both aggregation modes |
| Anonymisation | no original value of a sensitive term in the output; amounts unchanged; output still valid |
| XRechnung | selected files against `phive-rules-xrechnung` |

Test files: the examples of ZUGFeRD 2.5.2 (all profiles), `phlox-testfiles` (EN 16931 2017 / 2026,
UBL and CII), the test files of `en16931-cii2ubl` / `en16931-ubl2cii`, synthetic files with every
BT-X filled.

## 10. Phases

| Phase | Result | Status |
|-------|--------|--------|
| 0 | Repository skeleton: parent POM, BOM, `phinvoice-model-api`, `CLAUDE.md`, `README.md` with "News and noteworthy", licence files, CI workflow | done |
| 1 | EN 16931 model (2017 + 2026) in `phinvoice-model-api`, taken over from phlox (D8) | done: all 2017 terms are in the model; the 2026 terms not named in the model are syntax constants (format codes, fixed type codes, fixed scheme identifiers), BT-204 ... BT-208 are covered by `PhinvoicePostalAddress` |
| 2 | Term registry and export target; `EPhinvoiceProfile` with the profile matrix from the workbook | done (43 tests green) |
| 3 | `phinvoice-model-testfiles`; CII reader and writer for EN 16931; round trip tests | done (D9): 107 D16B files round trip to EN 16931:2017 without model change and without new EN 16931 rule failures (3 files with expected, reported drops); all D25A files round trip to EN 16931:2026; all files written to all CII targets (5 profiles, both edition directions) are XSD valid; 103 tests green |
| 4 | UBL reader and writer for EN 16931; cross syntax tests; comparison with cii2ubl / ubl2cii | done: UBL 2.1 corpus round trips to EN 16931:2017 without model change and without new EN 16931 rule failures, the UBL 2.5 corpus to EN 16931:2026 (XSD only); cross syntax CII ↔ UBL in both editions without new BR-* failures (`CrossSyntaxTest`); `CompareWithConvertersTest` lists the differences to en16931-cii2ubl 4.0.3 / en16931-ubl2cii 3.0.1 (see section 6); 158 tests green |
| 5 | EXTENDED: BG-X / BT-X in the model, CII reader and writer, drop report of the UBL writer | in progress: ZUGFeRD 2.5.2 test files of all profiles taken over (`getCiiZugferd252Files`); safety net (D12) and read coverage test done; all EXTENDED terms in the model (D10, D11, D13), read and written by the CII D22B reader and writer - the safety net catches nothing in the 25 EXTENDED files (was 88 paths), and they round trip to the EXTENDED target without model change and without new EN 16931 rule failures; the UBL writers and the CII D25A writer report all EXTENDED values (cross syntax tests of the EXTENDED files); 164 tests green. Open: safety net of the UBL reader, `phive-rules-zugferd` check of the output |
| 6 | `phinvoice-model-calc` | open |
| 7 | `phinvoice-model-redact` | open |
| 8 | Optional CLI; first release | open |
| 9 | phlox migration (separate work in phlox): the phlox readers use `phinvoice-model-cii` / `-ubl`, `PhloxInvoice` becomes an adapter or is removed, BT-X labels in five languages in the template | open |

## 11. Open points

* The effective amount scale for currencies with 3 decimals under EXTENDED (see section 7) - to be
  verified against the EXTENDED business rules before phase 6.
* Whether EXTENDED terms are allowed in an EN 16931:2026 document at all (Factur-X 1.09.2 is based
  on EN 16931:2017) - to be verified before phase 5; until then EXTENDED is only a 2017 target.
* Which UBL 2.x elements match BT-X terms semantically (opt-in mapping, section 6) - after phase 5.
* The official Factur-X EXTENDED samples repeat values that the workbook gives as `0..1`: several
  payment terms with a due date each and several allowances on the gross price (both in the
  model, D13), and two invoiced object identifiers (BT-18, `factur-x-18.xml`; BT-18 is `0..1` in
  EN 16931 as well, so the second one stays an additional term and is reported on writing).
* A model without invoice BT-9 but with a due date on a later payment terms is written as is; on
  reading again, that due date becomes BT-9 (the first due date is always BT-9).
* Cardinality differences between the Factur-X workbook and the EN 16931:2017 syntax binding:
  BT-34 and BT-49 are `1..1` in the binding but `0..1` in the workbook; BT-46-1, BT-60-1 and
  BT-125-2 are `0..1` in the binding but `1..1` in the workbook. The registry uses the binding.
* Six workbook terms in the profile EN 16931 are no CEN terms: BT-149-1, BT-150-1, BT-41-0,
  BT-46-0, BT-56-0, BT-71-0 (CII only additions of Factur-X). The registry treats them as
  EN 16931:2017 CII only terms; whether they correspond to EN 16931:2026 terms is not verified.
* The EN 16931:2026 syntax binding gives a header level CII path
  (`ApplicableHeaderTradeDelivery/ram:ShipToTradeParty/ram:GlobalID`) for BT-186, which is a line
  level term (BG-37); the Factur-X workbook has it on line level. To be verified against
  CEN/TS 16931-3-3:2026 before the CII writer for 2026 (phase 3).
* The workbook has no names for BG-X-88 and BG-X-89; the registry uses the CII element name
  `ram:RelevantTradeLocation`.
* `phive-rules` 4.7.0 has no validation artefacts for EN 16931:2026 yet. The 2026 round trip only
  checks the D25A XSD and the model equality; add the phive check when the artefacts exist.
* The output for the Factur-X profiles MINIMUM ... EXTENDED is only checked against the XSD, not
  yet against `phive-rules-zugferd` (planned in section 9, needs the Factur-X test files).
* Factur-X MINIMUM and BASIC WL documents have a BT-24 that is no EN 16931 identifier, so
  `CiiInvoiceReader.autoDetect ()` rejects them; they must be read with `fromD22B ()`. To be
  decided in phase 5 whether the detection learns the Factur-X identifiers.
* Taken over from phlox, the readers still map some values that EN 16931 does not allow more than
  once into `PhinvoiceAdditionalTerm` (e.g. a second BT-81 payment means code). The writers report
  them as not written. The writer joins several BG-33 texts into one BT-20 for EN 16931:2017.
* The UBL writers write fillers for mandatory elements without model value (section 6). Whether a
  filler should rather be a writer setting (like `setCardAccountNetworkID` in en16931-cii2ubl) is
  to be decided.
* The cross syntax and converter comparison tests log the model differences but only fail on
  conversion errors and new BR-* failures; the logged differences were reviewed once (2026-11-15).
