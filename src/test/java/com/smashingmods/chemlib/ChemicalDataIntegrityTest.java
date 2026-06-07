package com.smashingmods.chemlib;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.smashingmods.chemlib.registry.Registry;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Data-integrity test over the bundled {@code data/chemlib/elements.json} and
 * {@code data/chemlib/compounds.json}.
 *
 * <p>The JSON is parsed through the SAME production path the registry uses --
 * {@link Registry#getStreamAsJsonObject(String)} (GSON over the classpath resource) -- so these
 * tests exercise the real read. That static only does a {@code getResourceAsStream} + {@code JsonParser}
 * and the {@code Registry} class itself triggers no registry bootstrap, so no
 * {@code Bootstrap.bootStrap()} is required.
 *
 * <p>The assertions cover what {@code ChemicalRegistry} relies on: the full 118-element periodic
 * table with unique atomic numbers, the required per-element keys, the string-to-int coercion of
 * {@code group}/{@code period}, and component-reference closure for compounds (no dangling refs,
 * which is what {@code buildAbbreviation} walks).
 */
class ChemicalDataIntegrityTest {

    private static final String[] REQUIRED_ELEMENT_KEYS = {
            "name", "atomic_number", "abbreviation", "group", "period",
            "matter_state", "metal_type", "color"
    };

    private static JsonArray elements() {
        return Registry.getStreamAsJsonObject("/data/chemlib/elements.json").getAsJsonArray("elements");
    }

    private static JsonArray compounds() {
        return Registry.getStreamAsJsonObject("/data/chemlib/compounds.json").getAsJsonArray("compounds");
    }

    @Test
    void elementsJson_hasOneHundredEighteenEntries() {
        assertEquals(118, elements().size());
    }

    @Test
    void elementsJson_atomicNumbersAreUniqueAndCoverOneThroughOneHundredEighteen() {
        Set<Integer> seen = new HashSet<>();
        for (JsonElement element : elements()) {
            int atomicNumber = element.getAsJsonObject().get("atomic_number").getAsInt();
            assertTrue(seen.add(atomicNumber),
                    () -> String.format("duplicate atomic_number %d", atomicNumber));
        }
        for (int z = 1; z <= 118; z++) {
            int atomicNumber = z;
            assertTrue(seen.contains(atomicNumber),
                    () -> String.format("missing atomic_number %d", atomicNumber));
        }
        assertEquals(118, seen.size());
    }

    @Test
    void elementsJson_everyEntryHasTheRequiredKeys() {
        for (JsonElement element : elements()) {
            JsonObject object = element.getAsJsonObject();
            String name = object.has("name") ? object.get("name").getAsString() : "<unnamed>";
            for (String key : REQUIRED_ELEMENT_KEYS) {
                assertTrue(object.has(key),
                        () -> String.format("element '%s' is missing required key '%s'", name, key));
            }
        }
    }

    @Test
    void elementsJson_groupAndPeriodAreJsonStringsThatCoerceToInt() {
        for (JsonElement element : elements()) {
            JsonObject object = element.getAsJsonObject();
            String name = object.get("name").getAsString();

            JsonElement group = object.get("group");
            JsonElement period = object.get("period");

            // The values are stored as JSON strings (e.g. "1", "18") yet ChemicalRegistry reads
            // them with getAsInt(); assert both the string typing and that the coercion succeeds.
            assertTrue(group.getAsJsonPrimitive().isString(),
                    () -> String.format("element '%s' group is not a JSON string", name));
            assertTrue(period.getAsJsonPrimitive().isString(),
                    () -> String.format("element '%s' period is not a JSON string", name));

            int groupValue = group.getAsInt();
            int periodValue = period.getAsInt();
            assertTrue(groupValue >= 1, () -> String.format("element '%s' group < 1", name));
            assertTrue(periodValue >= 1, () -> String.format("element '%s' period < 1", name));
        }
    }

    @Test
    void compoundsJson_componentsClose() {
        // Collect every declared element name and compound name, then assert each component
        // reference resolves to one of them -- i.e. no dangling component refs (the closure that
        // CompoundItem.buildAbbreviation depends on to look up abbreviations).
        Set<String> knownNames = new HashSet<>();
        for (JsonElement element : elements()) {
            knownNames.add(element.getAsJsonObject().get("name").getAsString());
        }
        for (JsonElement compound : compounds()) {
            knownNames.add(compound.getAsJsonObject().get("name").getAsString());
        }

        for (JsonElement compound : compounds()) {
            JsonObject object = compound.getAsJsonObject();
            String compoundName = object.get("name").getAsString();
            for (JsonElement component : object.getAsJsonArray("components")) {
                String componentName = component.getAsJsonObject().get("name").getAsString();
                assertTrue(knownNames.contains(componentName),
                        () -> String.format("compound '%s' references unknown component '%s'",
                                compoundName, componentName));
            }
        }

        // Sanity: there is at least one compound, so the closure check actually ran.
        assertFalse(compounds().isEmpty());
    }
}
