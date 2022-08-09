package com.smashingmods.chemlib.registry;

import com.smashingmods.chemlib.ChemLib;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class PaintingsRegistry {
    private static final DeferredRegister<PaintingVariant> PAINTINGS = DeferredRegister.create(ForgeRegistries.PAINTING_VARIANTS, ChemLib.MODID);

    public static void register(IEventBus eventBus) {
        PAINTINGS.register("periodic_table", () -> new PaintingVariant(80, 48));
        PAINTINGS.register(eventBus);
    }
}
