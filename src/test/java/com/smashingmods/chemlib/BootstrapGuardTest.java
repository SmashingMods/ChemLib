package com.smashingmods.chemlib;

import com.smashingmods.chemlib.testsupport.BootstrappedTest;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pins that the {@code test} source set sees the Minecraft classpath and that
 * {@link Bootstrap#bootStrap()} populates the built-in registries, so plain-JUnit tests can exercise
 * registry-backed code.
 */
class BootstrapGuardTest extends BootstrappedTest {

    @Test
    void bootstrapPopulatesItemRegistry() {
        assertTrue(BuiltInRegistries.ITEM.containsKey(ResourceLocation.fromNamespaceAndPath("minecraft", "stone")));
    }
}
