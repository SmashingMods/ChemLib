package com.smashingmods.chemlib;

import net.minecraft.DetectedVersion;
import net.minecraft.SharedConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pins that the {@code test} source set sees the Minecraft classpath and that
 * {@link Bootstrap#bootStrap()} populates the built-in registries, so plain-JUnit tests can exercise
 * registry-backed code.
 */
class BootstrapGuardTest {

    @BeforeAll
    static void boot() {
        SharedConstants.setVersion(DetectedVersion.BUILT_IN);
        Bootstrap.bootStrap();
    }

    @Test
    void bootstrapPopulatesItemRegistry() {
        assertTrue(BuiltInRegistries.ITEM.containsKey(ResourceLocation.fromNamespaceAndPath("minecraft", "stone")));
    }
}
