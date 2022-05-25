package com.smashingmods.chemlib.common.registry;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.smashingmods.chemlib.ChemLib;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;

public class Registry {

    public static void register() {
        ChemicalRegistry.register();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        BlockRegistry.register(modEventBus);
        FluidRegistry.register(modEventBus);
        ItemRegistry.register(modEventBus);
        PaintingsRegistry.register(modEventBus);
    }

    public static JsonObject getStreamAsJsonObject(String pPath) {
        return JsonParser.parseReader(new BufferedReader(new InputStreamReader(Objects.requireNonNull(ChemLib.class.getResourceAsStream(pPath))))).getAsJsonObject();
    }
}
