package com.smashingmods.chemlib.registry;

import com.smashingmods.chemlib.api.ChemicalItemType;
import com.smashingmods.chemlib.api.MatterState;
import com.smashingmods.chemlib.api.MetalType;
import com.smashingmods.chemlib.registry.ContentDerivation.DerivedContent;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test for {@link ContentDerivation}, the pure decision layer split out of
 * {@link ChemicalRegistry}. Both {@code forElement} and {@code forCompound} take only parsed
 * primitives and hold no Minecraft/NeoForge references, so each truth-table row is exercised with
 * synthetic primitive inputs -- no {@code Bootstrap.bootStrap()}, no {@code Item} construction.
 *
 * <p>Every case asserts the full {@link DerivedContent} descriptor: the exact {@code itemTypes()}
 * set plus all three boolean flags ({@code metalBlock}, {@code lampBlock}, {@code fluidSet}).
 */
class ContentDerivationTest {

    /** Asserts every field of {@code actual} against the expected item-type set and three flags. */
    private static void assertDerived(DerivedContent actual, EnumSet<ChemicalItemType> itemTypes,
                                      boolean metalBlock, boolean lampBlock, boolean fluidSet) {
        assertEquals(itemTypes, actual.itemTypes());
        assertEquals(metalBlock, actual.metalBlock());
        assertEquals(lampBlock, actual.lampBlock());
        assertEquals(fluidSet, actual.fluidSet());
    }

    // --- forElement -------------------------------------------------------------------------------

    @Test
    void derive_element_artificial_empty() {
        // Artificial elements derive nothing regardless of the other primitives.
        assertDerived(
                ContentDerivation.forElement(MatterState.SOLID, MetalType.METAL, true, false, false, 1),
                EnumSet.noneOf(ChemicalItemType.class), false, false, false);
    }

    @Test
    void derive_element_solidMetalHasItem_plateDust() {
        // SOLID METAL with an existing item: PLATE + DUST only, no nugget/ingot/block.
        assertDerived(
                ContentDerivation.forElement(MatterState.SOLID, MetalType.METAL, false, true, false, 1),
                EnumSet.of(ChemicalItemType.PLATE, ChemicalItemType.DUST), false, false, false);
    }

    @Test
    void derive_element_solidMetalNoHasItem_metalBlock() {
        // SOLID METAL without an existing item: full metal set + a metal block.
        assertDerived(
                ContentDerivation.forElement(MatterState.SOLID, MetalType.METAL, false, false, false, 1),
                EnumSet.of(ChemicalItemType.PLATE, ChemicalItemType.NUGGET, ChemicalItemType.INGOT, ChemicalItemType.DUST),
                true, false, false);
    }

    @Test
    void derive_element_solidNonMetal_dustOnly() {
        // SOLID non-METAL derives DUST only -- PLATE is METAL-only.
        assertDerived(
                ContentDerivation.forElement(MatterState.SOLID, MetalType.METALLOID, false, false, false, 1),
                EnumSet.of(ChemicalItemType.DUST), false, false, false);
        assertDerived(
                ContentDerivation.forElement(MatterState.SOLID, MetalType.NONMETAL, false, false, false, 1),
                EnumSet.of(ChemicalItemType.DUST), false, false, false);
    }

    @Test
    void derive_element_gasNoFluid_fluid() {
        // LIQUID/GAS without an existing fluid, outside group 18: a fluid, no lamp.
        assertDerived(
                ContentDerivation.forElement(MatterState.GAS, MetalType.NONMETAL, false, false, false, 1),
                EnumSet.noneOf(ChemicalItemType.class), false, false, true);
        assertDerived(
                ContentDerivation.forElement(MatterState.LIQUID, MetalType.NONMETAL, false, false, false, 1),
                EnumSet.noneOf(ChemicalItemType.class), false, false, true);
    }

    @Test
    void derive_element_gasGroup18_lamp_synthetic() {
        // SYNTHETIC: the lamp branch (LIQUID/GAS, no fluid, group == 18) is dead in real data -- no
        // real element reaches it -- but the classifier is pure, so it is exercised directly here
        // with an explicit group=18 rather than any real element.
        assertDerived(
                ContentDerivation.forElement(MatterState.GAS, MetalType.NONMETAL, false, false, false, 18),
                EnumSet.noneOf(ChemicalItemType.class), false, true, true);
        assertDerived(
                ContentDerivation.forElement(MatterState.LIQUID, MetalType.NONMETAL, false, false, false, 18),
                EnumSet.noneOf(ChemicalItemType.class), false, true, true);
    }

    @Test
    void derive_element_liquidHasFluid_empty() {
        // LIQUID/GAS that already has a fluid derives nothing.
        assertDerived(
                ContentDerivation.forElement(MatterState.LIQUID, MetalType.NONMETAL, false, false, true, 1),
                EnumSet.noneOf(ChemicalItemType.class), false, false, false);
        assertDerived(
                ContentDerivation.forElement(MatterState.GAS, MetalType.NONMETAL, false, false, true, 1),
                EnumSet.noneOf(ChemicalItemType.class), false, false, false);
    }

    // --- forCompound ------------------------------------------------------------------------------

    @Test
    void derive_compound_solidNoHasItem_compound() {
        // SOLID compound without an existing item: a COMPOUND item, nothing else.
        assertDerived(
                ContentDerivation.forCompound(MatterState.SOLID, false, false, "sodium_chloride"),
                EnumSet.of(ChemicalItemType.COMPOUND), false, false, false);
    }

    @Test
    void derive_compound_pvc_plate() {
        // polyvinyl_chloride is the sole SOLID compound that also derives a PLATE.
        assertDerived(
                ContentDerivation.forCompound(MatterState.SOLID, false, false, "polyvinyl_chloride"),
                EnumSet.of(ChemicalItemType.COMPOUND, ChemicalItemType.PLATE), false, false, false);
    }

    @Test
    void derive_compound_solidHasItem_empty() {
        // SOLID compound that already has an item derives nothing.
        assertDerived(
                ContentDerivation.forCompound(MatterState.SOLID, true, false, "polyvinyl_chloride"),
                EnumSet.noneOf(ChemicalItemType.class), false, false, false);
    }

    @Test
    void derive_compound_liquidNoFluid_fluid() {
        // LIQUID/GAS compound without an existing fluid registers a fluid.
        assertDerived(
                ContentDerivation.forCompound(MatterState.LIQUID, false, false, "water"),
                EnumSet.noneOf(ChemicalItemType.class), false, false, true);
        assertDerived(
                ContentDerivation.forCompound(MatterState.GAS, false, false, "carbon_dioxide"),
                EnumSet.noneOf(ChemicalItemType.class), false, false, true);
    }

    @Test
    void derive_compound_liquidHasFluid_empty() {
        // LIQUID/GAS compound that already has a fluid derives nothing.
        assertDerived(
                ContentDerivation.forCompound(MatterState.LIQUID, false, true, "water"),
                EnumSet.noneOf(ChemicalItemType.class), false, false, false);
        assertDerived(
                ContentDerivation.forCompound(MatterState.GAS, false, true, "carbon_dioxide"),
                EnumSet.noneOf(ChemicalItemType.class), false, false, false);
    }
}
