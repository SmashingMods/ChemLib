package com.smashingmods.chemlib;

import com.smashingmods.chemlib.api.Chemical;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tier-0 unit test for {@link Chemical#parseColorHex(String)} -- the shared colour-hex rule
 * {@code Integer.parseInt(color, 16) | 0xFF000000} used by the {@code ElementItem}/{@code CompoundItem}
 * constructors and the fluid-colour argument in {@code ChemicalRegistry}.
 *
 * <p>The rule is a pure static, so this asserts on it directly -- no {@code Item} construction, no
 * {@code Bootstrap.bootStrap()}, and no unfreezing of {@code BuiltInRegistries.ITEM}. The rule
 * force-sets full opacity (the top byte) regardless of the parsed value, so a regression at any of
 * the routed call sites (e.g. dropping the {@code | 0xFF000000} alpha-forcing) is caught here.
 */
class ColorHexTest {

    @Test
    void colorHex_blackGetsFullAlpha() {
        assertEquals(0xFF000000, Chemical.parseColorHex("000000"));
    }

    @Test
    void colorHex_whiteIsFullyOpaqueWhite() {
        // 0xFFFFFFFF is -1 as a signed int -- assert both forms to make the equality explicit.
        assertEquals(0xFFFFFFFF, Chemical.parseColorHex("ffffff"));
        assertEquals(-1, Chemical.parseColorHex("ffffff"));
    }

    @Test
    void colorHex_promethiumSevenDigitValueStillFitsAnIntAndKeepsLowBytes() {
        // QUIRK: promethium's colour in elements.json is the 7-hex-digit "62af0a7" (not 6 digits).
        // Integer.parseInt("62af0a7", 16) == 0x062af0a7, which still fits a 32-bit int, so OR-ing
        // 0xFF000000 overwrites the high byte (0x06 -> 0xFF) and yields 0xFF2af0a7. It does NOT throw.
        assertEquals(0xFF2af0a7, Chemical.parseColorHex("62af0a7"));
    }
}
