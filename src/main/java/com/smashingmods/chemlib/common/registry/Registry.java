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
    public static final JsonObject ELEMENTS_JSON = Registry.getStreamAsJsonObject("/data/chemlib/elements.json");
    public static final JsonObject COMPOUNDS_JSON = Registry.getStreamAsJsonObject("/data/chemlib/compounds.json");

    public static void register() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        BlockRegistry.register(modEventBus);
        ItemRegistry.register(modEventBus);
    }

    public static JsonObject getStreamAsJsonObject(String pPath) {
        return JsonParser.parseReader(new BufferedReader(new InputStreamReader(Objects.requireNonNull(ChemLib.class.getResourceAsStream(pPath))))).getAsJsonObject();
    }
}
