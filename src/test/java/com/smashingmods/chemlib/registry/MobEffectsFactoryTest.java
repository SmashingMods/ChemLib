package com.smashingmods.chemlib.registry;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.smashingmods.chemlib.testsupport.BootstrappedTest;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Test for {@link ChemicalRegistry#mobEffectsFactory(JsonObject)} -- the public factory that
 * turns the {@code effect} JSON array into a list of {@link MobEffectInstance}.
 *
 * <p>It is a {@code public static} method taking a hand-built {@link JsonObject} and returning the
 * list, so it is exercised directly with synthetic JSON -- no {@code Item} construction and no
 * mod-content registration path. It resolves each effect id against {@code BuiltInRegistries.MOB_EFFECT}
 * (vanilla effects only here), so {@code Bootstrap.bootStrap()} is needed to populate that registry.
 *
 * <p>Pins the dedup invariant: a duplicate effect id (e.g. {@code nitric_oxide} listing
 * {@code minecraft:nausea} twice in the real data) collapses to a single instance keeping the FIRST
 * occurrence, while a distinct effect survives -- dedup is per-id, not a global cap.
 */
class MobEffectsFactoryTest extends BootstrappedTest {

    private static final ResourceLocation POISON = ResourceLocation.fromNamespaceAndPath("minecraft", "poison");
    private static final ResourceLocation WEAKNESS = ResourceLocation.fromNamespaceAndPath("minecraft", "weakness");

    /** Builds a single {@code effect} JSON entry in the production shape. */
    private static JsonObject effect(ResourceLocation location, int duration, int amplifier) {
        JsonObject object = new JsonObject();
        object.addProperty("location", location.toString());
        object.addProperty("duration", duration);
        object.addProperty("amplifier", amplifier);
        return object;
    }

    /** Wraps the given effect entries in a chemical object with a {@code name} and {@code effect} array. */
    private static JsonObject chemicalWithEffects(JsonObject... effects) {
        JsonArray array = new JsonArray();
        for (JsonObject effect : effects) {
            array.add(effect);
        }
        JsonObject object = new JsonObject();
        object.addProperty("name", "synthetic_chemical");
        object.add("effect", array);
        return object;
    }

    @Test
    void mobEffectsFactory_duplicateEffectIdCollapsesToFirstOccurrence() {
        // minecraft:poison appears twice with different duration/amplifier, followed by a distinct
        // minecraft:weakness. The duplicate must collapse to one (keeping the FIRST poison's values),
        // while the distinct weakness survives -- proving dedup is per-id, not a global cap.
        List<MobEffectInstance> result = ChemicalRegistry.mobEffectsFactory(chemicalWithEffects(
                effect(POISON, 600, 1),
                effect(POISON, 200, 3),
                effect(WEAKNESS, 600, 0)));

        assertEquals(2, result.size());

        MobEffect expectedPoison = BuiltInRegistries.MOB_EFFECT.getValue(POISON);
        MobEffect expectedWeakness = BuiltInRegistries.MOB_EFFECT.getValue(WEAKNESS);

        MobEffectInstance poison = result.get(0);
        assertSame(expectedPoison, poison.getEffect().value());
        assertEquals(600, poison.getDuration());
        assertEquals(1, poison.getAmplifier());

        MobEffectInstance weakness = result.get(1);
        assertSame(expectedWeakness, weakness.getEffect().value());
    }

    @Test
    void mobEffectsFactory_missingEffectArrayIsEmpty() {
        // No "effect" array at all: getAsJsonArray returns null and the factory yields an empty list
        // rather than NPEing.
        assertEquals(List.of(), ChemicalRegistry.mobEffectsFactory(new JsonObject()));
    }
}
