package com.smashingmods.chemlib.registry;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.smashingmods.chemlib.ChemLib;
import net.neoforged.bus.api.IEventBus;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;

public class Registry {

    public static void register(IEventBus eventBus) {
        ChemicalRegistry.register();
        BlockRegistry.register(eventBus);
        FluidRegistry.register(eventBus);
        ItemRegistry.register(eventBus);
        TabsRegistry.register(eventBus);
    }

    public static JsonObject getStreamAsJsonObject(String path) {
        return JsonParser.parseReader(new BufferedReader(new InputStreamReader(Objects.requireNonNull(ChemLib.class.getResourceAsStream(path))))).getAsJsonObject();
    }
}
