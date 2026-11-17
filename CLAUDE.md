# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this project is

**phinvoice-model** is a syntax neutral, writable Java model for electronic invoices and credit
notes. It covers the EN 16931 semantic model in both editions (EN 16931:2017 with UBL 2.1 / CII D16B,
EN 16931:2026 with UBL 2.5 / CII D25A) plus all terms of ZUGFeRD 2.5.2 / Factur-X 1.09.2 EXTENDED
(CII D22B). Readers and writers for CII and UBL, a re-calculation module and an anonymiser are
planned.

`PLAN.md` is the binding plan: scope, decisions (D1 ... D13), module layout, phases and open points.
Read it before starting work and update its phase table and open points when something changes.

The project folder is `~/dev/git/phinvoice`; the Maven coordinates are `com.helger.phinvoice` /
`phinvoice-model-*`.

## Architecture

Modules (only the ones that have content are in the reactor):
- `phinvoice-model-bom` - Bill of Materials
- `phinvoice-model-api` - the model in `com.helger.phinvoice.model`; depends on `en16931-basics` for
  `EEN16931Edition` / `EEN16931SyntaxKind`. `CPhinvoiceModelVersion` (package `com.helger.phinvoice`)
  reads the filtered `phinvoice-model-version.properties`.
  - `com.helger.phinvoice.target`: `EPhinvoiceProfile` (Factur-X profiles MINIMUM ... EXTENDED) and
    `PhinvoiceExportTarget` (syntax kind × edition × profile, with the validity rules).
  - `com.helger.phinvoice.term`: `PhinvoiceTermRegistry` with `PhinvoiceTerm` /
    `PhinvoiceTermUsage` - all BG / BT / BG-X / BT-X with per edition cardinality, lowest profile
    and UBL / CII availability; `isAllowed (term, target)` is the basis of the implicit purification.
    The data is the generated resource `src/main/resources/phinvoice-model/terms.tsv` - never edit
    it by hand. Regenerate with `MainCreateTermRegistry` (test sources, package
    `com.helger.phinvoice.supplementary.tools`), run from the module directory; it needs the sibling
    checkouts `../../phive-rules` (Factur-X workbook in `ZUGFeRD_2.5.2_EN.zip`) and
    `../../en16931-cii2ubl/docs` (the 2017 and 2026 syntax bindings). The generator rules are in
    `PLAN.md` section 5.
  - `PhinvoiceExtendedTerms` (same package): the term IDs of the repeated Factur-X EXTENDED
    structures per position (parties with contact and addresses, referenced documents), as
    records. Used by the CII D22B writer and by `PhinvoiceExtendedTermReporter`.

  - `com.helger.phinvoice.write`: `PhinvoiceTermFilter` - the implicit purification of all
    writers: `use (termID, value)` decides with the registry and reports every value that is not
    written as a warning (term ID as error field name); `useGroup` reports a whole group once;
    `dropAdditionalTerms` reports the `PhinvoiceAdditionalTerm` values. A term ID that is not in
    the registry throws, so typos fail in the tests. `PhinvoiceExtendedTermReporter` reports all
    Factur-X EXTENDED values of a model for the writers that cannot write any of them (UBL 2.1,
    UBL 2.5, CII D25A); a new EXTENDED field must be added there as well.
  - `com.helger.phinvoice.read`: `IPhinvoiceReader`, `PhinvoiceReaderHelper`; `PhinvoiceLogHelper`
    (package `com.helger.phinvoice`) - taken over from phlox (decision D9).
    `PhinvoiceUnmappedValueCollector` is the syntax neutral part of the safety net of the readers
    (decision D12): it compares the source DOM with the model written again, adds every value
    that is not written again as `PhinvoiceAdditionalTerm` (term = prefix + XPath) and reports it
    as warning in the `ErrorList` of the reader (error field name = term).
- `phinvoice-model-testfiles` - the test corpus taken over from `phlox-testfiles`
  (`MockPhinvoiceTestFiles`, resources under `external/en16931/`) plus `PhinvoiceModelDumper`
  (reflection based `path = value` dump of a model, for round trip comparisons) and
  `PhinvoiceReadCoverage` (every source value must be in the model; used by the read coverage
  tests of CII and UBL). Depends on `phinvoice-model-api`; consumed in scope `test`.
- `phinvoice-model-cii` - `CiiInvoiceReader` (`CiiD22BInvoiceReader` for D16B / D22B,
  `CiiD25AInvoiceReader`; taken over from phlox) and `CiiInvoiceWriter` (`CiiD22BInvoiceWriter` for
  all EN 16931:2017 targets, `CiiD25AInvoiceWriter` for EN 16931:2026). Each writer is the exact
  inverse of the reader of the same release; a change of a reader needs the same change in the
  writer. Tests: `CiiInvoiceWriterRoundTripTest` (read → write → read, model compared with
  `PhinvoiceModelDumper`, `phive-rules-en16931` must show no new rule failures; intended
  differences are listed per file; the EXTENDED files round trip to the EXTENDED target),
  `CiiInvoiceWriterTargetTest` (all targets), `CiiReadCoverageTest` (every source value is in the
  model; logs what only the safety net caught).
  - The D22B writer writes all EXTENDED terms. A term of EN 16931:2026 that Factur-X has as BT-X
    is written with the BT-X ID and reported with the 2026 ID if the target has no BT-X
    (`EN2026_TERM_OF_EXTENDED_TERM`).
  - Safety net (decision D12): `CiiUnmappedValueCollector` writes the model again with the raw
    writer for `PhinvoiceUnmappedValueCollector` (term prefix `cii:`). A value that the reader
    does not map shows up there and as reader warning - map it.
  - `CiiZugferdValidationTest` checks the output of all Factur-X profiles with
    `phive-rules-zugferd` 2.5.2 (which needs the EN 16931 rules registered first); the failures
    that reported drops cause are listed per target profile and file.
  - `autoDetect ()` also recognises the BT-24 of Factur-X MINIMUM and BASIC WL (EN 16931:2017).

- `phinvoice-model-ubl` - `UblInvoiceReader` (`Ubl21InvoiceReader`, `Ubl25InvoiceReader`; taken
  over from phlox) and `UblInvoiceWriter` (`Ubl21InvoiceWriter` for EN 16931:2017,
  `Ubl25InvoiceWriter` for EN 16931:2026; Invoice and Credit Note). Same inverse rule as for CII.
  Safety net: `UblUnmappedValueCollector` (term prefix `ubl:`), checked by `UblReadCoverageTest`.
  Tests: `UblInvoiceWriterRoundTripTest`, `UblInvoiceWriterTargetTest`, `CrossSyntaxTest` (CII ↔
  UBL, needs `phinvoice-model-cii` in scope `test`), `CompareWithConvertersTest` (logs the
  differences to en16931-cii2ubl / en16931-ubl2cii, scope `test`).
- The BT-24 rule is shared: `PhinvoiceExportTarget.isCompatibleSpecificationIdentifier` and
  `PhinvoiceTermFilter.getSpecificationIdentifier`.
- New writer code: the imports of the large writers are explicit (no wildcards); the readers taken
  over from phlox still use wildcard imports.

Planned (see `PLAN.md`): `-calc`, `-redact`, optionally `-cli`.
Dependency rules: readers and writers depend only on `-api`, never on each other; `calc` and
`redact` only work on the model; only a CLI wires modules together.

## Model rules

- The EN 16931 part of the model was taken over from the phlox intermediate model
  (`../phlox/phlox-api`, package `com.helger.phlox.api.model`) with `Phlox*` renamed to
  `Phinvoice*` (decision D8). It must stay a superset of the phlox model, because phlox is to use
  this model eventually. When phlox gets a new BT, add it here as well.
- Hand-written classes (decision D4): mutable beans, fluent setters returning `this`, one class per
  BG / BG-X, one field per BT / BT-X. The Javadoc of each getter and setter names the BT IDs.
- Collections are `ICommonsList` and never `null`, only empty. Absent single values and absent
  optional groups are `null`.
- The model does not validate. Validation is done with `phive-rules`, and only in tests.
- `PhinvoiceAdditionalTerm` holds values for which the model has no field, so that reading never
  loses data. Writers never write them, they report them.
- Readers are faithful: they do not merge values for display (unlike phlox). Additions beyond phlox
  so far: `PhinvoiceContact.departmentName`, `PhinvoiceItemAttribute.numericValue`, all Factur-X
  EXTENDED terms (decision D10: reusable `PhinvoiceTradeParty`, `PhinvoiceReferencedDocument`
  and the other EXTENDED classes), `PhinvoicePaymentTerms.dueDate` and the allowance / charge lists
  of `PhinvoicePriceDetails` (decision D13; `getDiscount` / `setDiscount` are BT-147, the amount of
  the first allowance).
- JSpecify annotations (`@NonNull` / `@Nullable`) on public types.

## Build / test

Standard Maven (Java 17+, Maven 3.x). The compiler release is 17 through `java.version` in
`pom.xml`, so Java 21+ language features do not compile.
- Build: `mvn clean install`
- Single module: `mvn -pl phinvoice-model-api test`
- JUnit 4. forbiddenapis runs with the `ph-forbidden-apis` signatures - fix the call site, do not
  exclude the rule.
- CI: `.github/workflows/maven.yml` with Java 17, 21 and 25.
- Style comes from `~/.claude/rules/` (Hungarian notation, spaces before parentheses, Apache licence
  header, ph-commons collections, British English in texts).

## Release notes

The "News and noteworthy" section is at the end of `README.md`. Unreleased entries are titled
`vX.Y.Z - work in progress`.
