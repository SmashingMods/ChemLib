package com.smashingmods.chemlib.api;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test for {@link Chemical#clampMinColorValue(int, int)}.
 *
 * <p>{@code clampMinColorValue} is a pure default method: it ORs each of the three colour bytes
 * with {@code minValue} and reassembles them in place. The {@code green}/{@code red} locals in the
 * implementation are mislabeled versus their actual RGB positions, but the maths is
 * position-preserving (every byte stays in its own byte slot), so the result is correct.
 *
 * <p>Exercised through a minimal anonymous {@link Chemical} stub; only {@code clampMinColorValue} is
 * called, so the abstract methods return dummy values.
 */
class ClampMinColorValueTest {

    /**
     * Minimal {@link Chemical} whose abstract methods are stubbed -- only the pure default
     * {@code clampMinColorValue} is exercised.
     */
    private static Chemical stub() {
        return new Chemical() {
            @Override
            public String getChemicalName() {
                return "test";
            }

            @Override
            public String getAbbreviation() {
                return "T";
            }

            @Override
            public MatterState getMatterState() {
                return MatterState.SOLID;
            }

            @Override
            public String getChemicalDescription() {
                return "";
            }

            @Override
            public List<MobEffectInstance> getEffects() {
                return List.of();
            }

            @Override
            public int getColor() {
                return 0;
            }

            @Override
            public Item asItem() {
                return null;
            }
        };
    }

    @Test
    void clampMinColorValue_blackFloorsToMinValueOnEveryByte() {
        assertEquals(0x202020, stub().clampMinColorValue(0x000000, 0x20));
    }

    @Test
    void clampMinColorValue_whiteIsUnchangedBecauseEveryByteIsAlreadySaturated() {
        assertEquals(0xFFFFFF, stub().clampMinColorValue(0xFFFFFF, 0x20));
    }

    @Test
    void clampMinColorValue_minValueZeroIsIdentity() {
        assertEquals(0x123456, stub().clampMinColorValue(0x123456, 0x00));
    }

    @Test
    void clampMinColorValue_minValueFullSaturatesEveryByteIndependently() {
        // Each byte is OR'd with 0xFF in isolation, so black becomes white regardless of input bits.
        assertEquals(0xFFFFFF, stub().clampMinColorValue(0x000000, 0xFF));
    }
}
