package com.smashingmods.chemlib.common.registry;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class Registry {
    public static void register() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ItemRegistry.register(modEventBus);
        BlockRegistry.register(modEventBus);
    }
}
