package com.smashingmods.chemlib.common.items;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tier-1 unit test for {@link CompoundItem#getSubscript(String)}.
 *
 * <p>{@code getSubscript} is a pure static that maps each character to the Unicode subscript digit
 * at {@code U+2080 + numericValue}, i.e. {@code '0'} to {@code U+2080} ('₀') through {@code '9'}
 * to {@code U+2089} ('₉'). Calling the static class-loads {@code CompoundItem extends Item};
 * that is a plain class-load (not a registry bootstrap), so no {@code Bootstrap.bootStrap()} is
 * needed here.
 *
 * <p>The expected strings are the literal subscript characters (the file is compiled UTF-8); each
 * assertion names the exact codepoint it expects in a trailing comment.
 */
class GetSubscriptTest {

    @Test
    void getSubscript_singleDigitMapsToSubscript() {
        assertEquals("₃", CompoundItem.getSubscript("3")); // U+2083 == '₃'
    }

    @Test
    void getSubscript_multiDigitMapsDigitByDigit() {
        assertEquals("₁₂", CompoundItem.getSubscript("12")); // U+2081 U+2082 == '₁₂'
    }

    @Test
    void getSubscript_zeroMapsToSubscriptZero() {
        assertEquals("₀", CompoundItem.getSubscript("0")); // U+2080 == '₀'
    }

    @Test
    void getSubscript_emptyStringIsEmpty() {
        assertEquals("", CompoundItem.getSubscript(""));
    }
}
