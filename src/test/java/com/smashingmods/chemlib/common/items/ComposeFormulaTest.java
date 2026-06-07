package com.smashingmods.chemlib.common.items;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test for {@link CompoundItem#composeFormula(Map, Function, Function)}.
 *
 * <p>{@code composeFormula} is the formula-composition core used by {@code buildAbbreviation}: it walks the
 * component map in iteration order, appending element abbreviations verbatim and compound
 * abbreviations wrapped in {@code (...)}, followed by a subscript (via {@code getSubscript}) when the
 * count exceeds one. The two registry lookups are injected as {@code Function<String,
 * Optional<String>>}, so these tests feed FAKE lookups and never touch {@code ItemRegistry} or
 * construct an {@code Item}.
 *
 * <p>{@link LinkedHashMap} pins iteration order so the expected formulas are deterministic. Expected
 * subscripts are the literal Unicode subscript characters (the file is compiled UTF-8); each
 * assertion names the exact codepoint it expects in a trailing comment.
 */
class ComposeFormulaTest {

    /** Lookup that resolves the given name to the given abbreviation and everything else to empty. */
    private static Function<String, Optional<String>> lookup(String name, String abbreviation) {
        return key -> key.equals(name) ? Optional.of(abbreviation) : Optional.empty();
    }

    /** Lookup that resolves nothing -- the "no match in this registry" case. */
    private static Function<String, Optional<String>> empty() {
        return key -> Optional.empty();
    }

    @Test
    void composeFormula_elementsWithSubscriptOnlyWhenCountExceedsOne() {
        Map<String, Integer> components = new LinkedHashMap<>();
        components.put("hydrogen", 2);
        components.put("oxygen", 1);

        Function<String, Optional<String>> elementAbbrev = key -> switch (key) {
            case "hydrogen" -> Optional.of("H");
            case "oxygen" -> Optional.of("O");
            default -> Optional.empty();
        };

        // H + subscript 2, then O with no subscript because its count is 1.
        assertEquals("H₂O", CompoundItem.composeFormula(components, elementAbbrev, empty())); // U+2082 == '₂'
    }

    @Test
    void composeFormula_compoundComponentIsWrappedInParensAndSubscripted() {
        Map<String, Integer> components = new LinkedHashMap<>();
        components.put("hydroxide", 2);

        // No element match; compound abbrev "OH" wrapped in parens, then subscript 2.
        assertEquals("(OH)₂", CompoundItem.composeFormula(components, empty(), lookup("hydroxide", "OH"))); // U+2082 == '₂'
    }

    @Test
    void composeFormula_mixedComponentsPreserveIterationOrder() {
        Map<String, Integer> components = new LinkedHashMap<>();
        components.put("calcium", 1);
        components.put("hydroxide", 2);

        Function<String, Optional<String>> elementAbbrev = lookup("calcium", "Ca");
        Function<String, Optional<String>> compoundAbbrev = lookup("hydroxide", "OH");

        // Ca (count 1, no subscript) then (OH) with subscript 2, in insertion order.
        assertEquals("Ca(OH)₂", CompoundItem.composeFormula(components, elementAbbrev, compoundAbbrev)); // U+2082 == '₂'
    }

    @Test
    void composeFormula_countOneEmitsNoSubscript() {
        Map<String, Integer> components = new LinkedHashMap<>();
        components.put("sodium", 1);
        components.put("chlorine", 1);

        Function<String, Optional<String>> elementAbbrev = key -> switch (key) {
            case "sodium" -> Optional.of("Na");
            case "chlorine" -> Optional.of("Cl");
            default -> Optional.empty();
        };

        assertEquals("NaCl", CompoundItem.composeFormula(components, elementAbbrev, empty()));
    }
}
