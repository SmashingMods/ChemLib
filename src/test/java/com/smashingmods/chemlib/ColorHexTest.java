package com.smashingmods.chemlib;

import com.smashingmods.chemlib.api.MatterState;
import com.smashingmods.chemlib.api.MetalType;
import com.smashingmods.chemlib.common.items.CompoundItem;
import com.smashingmods.chemlib.common.items.ElementItem;
import net.minecraft.DetectedVersion;
import net.minecraft.SharedConstants;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tier-2 unit test for the colour-hex parse rule {@code Integer.parseInt(color, 16) | 0xFF000000}
 * used in {@link CompoundItem} and {@link ElementItem}'s constructors.
 *
 * <p>The rule is exercised through the real production code -- the items are constructed with a
 * colour string and the parsed value is read back via {@code getColor()} -- so a regression at
 * either call site (e.g. dropping the {@code | 0xFF000000} alpha-forcing) is caught. The two
 * constructors carry independent copies of the expression, so both are tested. The rule
 * force-sets full opacity (the top byte) regardless of the parsed value.
 *
 * <p>Constructing any {@code Item} on this Minecraft version writes an intrusive holder into the
 * built-in ITEM registry ({@code Item.<init>} -> {@code BuiltInRegistries.ITEM.createIntrusiveHolder}).
 * That write needs the registries bootstrapped <em>and</em> the ITEM registry unfrozen, so this test
 * runs {@link Bootstrap#bootStrap()} and then unfreezes ITEM (the NeoForge-internal hook the engine
 * itself uses to register late) before any item is built. The registry-backed methods such as
 * {@code getNamespace()} are never called here. This makes the test Tier-2, like {@code BootstrapGuardTest}.
 */
class ColorHexTest {

    @BeforeAll
    static void boot() {
        SharedConstants.setVersion(DetectedVersion.BUILT_IN);
        Bootstrap.bootStrap();
        // Item construction registers an intrusive holder into ITEM, which Bootstrap leaves frozen;
        // unfreeze it so the real CompoundItem/ElementItem constructors can run in plain JUnit.
        ((MappedRegistry<?>) BuiltInRegistries.ITEM).unfreeze();
    }

    /** Builds a {@link CompoundItem} with dummy non-colour params, exercising {@code CompoundItem}'s ctor rule. */
    private static int compoundColor(String color) {
        return new CompoundItem("test", MatterState.SOLID, Map.of(), "", color, List.of()).getColor();
    }

    /** Builds an {@link ElementItem} with dummy non-colour params, exercising {@code ElementItem}'s ctor rule. */
    private static int elementColor(String color) {
        return new ElementItem("test", 0, "Te", 1, 1, MatterState.SOLID, MetalType.NONMETAL, false, color, List.of()).getColor();
    }

    @Test
    void colorHex_blackGetsFullAlpha() {
        assertEquals(0xFF000000, compoundColor("000000"));
    }

    @Test
    void colorHex_whiteIsFullyOpaqueWhite() {
        // 0xFFFFFFFF is -1 as a signed int -- assert both forms to make the equality explicit.
        assertEquals(0xFFFFFFFF, compoundColor("ffffff"));
        assertEquals(-1, compoundColor("ffffff"));
    }

    @Test
    void colorHex_promethiumSevenDigitValueStillFitsAnIntAndKeepsLowBytes() {
        // QUIRK: promethium's colour in elements.json is the 7-hex-digit "62af0a7" (not 6 digits).
        // Integer.parseInt("62af0a7", 16) == 0x062af0a7, which still fits a 32-bit int, so OR-ing
        // 0xFF000000 overwrites the high byte (0x06 -> 0xFF) and yields 0xFF2af0a7. It does NOT throw.
        assertEquals(0xFF2af0a7, compoundColor("62af0a7"));
    }

    @Test
    void colorHex_elementConstructorAppliesTheSameRule() {
        // ElementItem carries an independent copy of the expression, so cover it too -- the
        // promethium 7-digit value also reaches ElementItem in production (elements.json).
        assertEquals(0xFF2af0a7, elementColor("62af0a7"));
    }
}
