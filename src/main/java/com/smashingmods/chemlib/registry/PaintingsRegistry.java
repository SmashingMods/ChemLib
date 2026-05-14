package com.smashingmods.chemlib.registry;

import com.smashingmods.chemlib.ChemLib;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.PaintingVariant;

public class PaintingsRegistry {
    public static final ResourceKey<PaintingVariant> PERIODIC_TABLE = createKey("periodic_table");

    public static void bootstrap(BootstrapContext<PaintingVariant> context) {
        register(context, PERIODIC_TABLE, 5, 3);
    }

    private static ResourceKey<PaintingVariant> createKey(String name) {
        return ResourceKey.create(Registries.PAINTING_VARIANT, ResourceLocation.fromNamespaceAndPath(ChemLib.MODID, name));
    }

    private static void register(BootstrapContext<PaintingVariant> context, ResourceKey<PaintingVariant> key, int width, int height) {
        context.register(key, new PaintingVariant(width, height, key.location()));
    }
}
