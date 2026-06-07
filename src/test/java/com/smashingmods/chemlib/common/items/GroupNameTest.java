package com.smashingmods.chemlib.common.items;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test for {@link ElementItem#groupName(int)}.
 *
 * <p>{@code groupName} is a pure static {@code switch} over the atomic number; it reads no instance
 * state, so it is exercised without constructing an {@code ElementItem} (which extends {@code Item}
 * and would otherwise drag in a registry bootstrap). Calling the static class-loads
 * {@code ElementItem}, which is a plain class-load.
 *
 * <p>One assertion per branch uses a boundary/representative atomic number. The Noble Gases case
 * asserts the misspelled {@code "Noble Gasses"} string that the production switch intentionally returns.
 */
class GroupNameTest {

    @Test
    void groupName_reactiveNonMetals() {
        assertEquals("Reactive Non-Metals", ElementItem.groupName(1));
    }

    @Test
    void groupName_alkaliMetals() {
        assertEquals("Alkali Metals", ElementItem.groupName(3));
    }

    @Test
    void groupName_transitionMetals() {
        assertEquals("Transition Metals", ElementItem.groupName(21));
    }

    @Test
    void groupName_postTransitionMetals() {
        assertEquals("Post-Transition Metals", ElementItem.groupName(13));
    }

    @Test
    void groupName_metalloids() {
        assertEquals("Metalloids", ElementItem.groupName(5));
    }

    @Test
    void groupName_halogens() {
        assertEquals("Halogens", ElementItem.groupName(9));
    }

    @Test
    void groupName_nobleGasesKeepsExistingMisspelling() {
        assertEquals("Noble Gasses", ElementItem.groupName(2)); // misspelling preserved by design
    }

    @Test
    void groupName_lanthanides() {
        assertEquals("Lanthanides", ElementItem.groupName(57));
    }

    @Test
    void groupName_actinides() {
        assertEquals("Actinides", ElementItem.groupName(89));
    }

    @Test
    void groupName_unknownProperties() {
        assertEquals("Unknown Properties", ElementItem.groupName(118));
    }

    @Test
    void groupName_belowRangeFallsToDefaultEmpty() {
        assertEquals("", ElementItem.groupName(0));
    }

    @Test
    void groupName_aboveRangeFallsToDefaultEmpty() {
        assertEquals("", ElementItem.groupName(200));
    }
}
