package com.smashingmods.chemlib.registry;

import com.smashingmods.chemlib.ChemLib;
import net.minecraft.world.entity.decoration.Motive;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class PaintingsRegistry {
    private static final DeferredRegister<Motive> PAINTINGS = DeferredRegister.create(ForgeRegistries.PAINTING_TYPES, ChemLib.MODID);

    public static void register(IEventBus eventBus) {
        PAINTINGS.register("periodic_table", () -> new Motive(80, 48));
        PAINTINGS.register(eventBus);
    }
}
