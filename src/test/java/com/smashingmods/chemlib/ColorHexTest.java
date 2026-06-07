package com.smashingmods.chemlib;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tier-0 unit test for the colour-hex parse rule {@code Integer.parseInt(color, 16) | 0xFF000000}
 * used in {@link com.smashingmods.chemlib.common.items.CompoundItem}'s constructor and in
 * {@code ChemicalRegistry}'s fluid-colour registration.
 *
 * <p>The expression is tested directly -- no items are constructed -- so this stays Tier-0 and
 * independent of the Minecraft classpath. The rule force-sets full opacity (the top byte) regardless
 * of the parsed value.
 */
class ColorHexTest {

    /** Mirrors the production expression exactly. */
    private static int parseColor(String color) {
        return Integer.parseInt(color, 16) | 0xFF000000;
    }

    @Test
    void colorHex_blackGetsFullAlpha() {
        assertEquals(0xFF000000, parseColor("000000"));
    }

    @Test
    void colorHex_whiteIsFullyOpaqueWhite() {
        // 0xFFFFFFFF is -1 as a signed int -- assert both forms to make the equality explicit.
        assertEquals(0xFFFFFFFF, parseColor("ffffff"));
        assertEquals(-1, parseColor("ffffff"));
    }

    @Test
    void colorHex_promethiumSevenDigitValueStillFitsAnIntAndKeepsLowBytes() {
        // QUIRK: promethium's colour in elements.json is the 7-hex-digit "62af0a7" (not 6 digits).
        // Integer.parseInt("62af0a7", 16) == 0x062af0a7, which still fits a 32-bit int, so OR-ing
        // 0xFF000000 overwrites the high byte (0x06 -> 0xFF) and yields 0xFF2af0a7. It does NOT throw.
        assertEquals(0xFF2af0a7, parseColor("62af0a7"));
    }
}
