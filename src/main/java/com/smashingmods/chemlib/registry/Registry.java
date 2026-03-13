package com.smashingmods.chemlib.registry;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.smashingmods.chemlib.ChemLib;
import net.neoforged.bus.api.IEventBus;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;

public class Registry {

    public static void register(IEventBus pModEventBus) {
        BlockRegistry.register(pModEventBus);
        FluidRegistry.register(pModEventBus);
        ItemRegistry.register(pModEventBus);
        TabsRegistry.register(pModEventBus);
        PaintingsRegistry.register(pModEventBus);

        ChemicalRegistry.register();
    }

    public static JsonObject getStreamAsJsonObject(String pPath) {
        return JsonParser.parseReader(new BufferedReader(new InputStreamReader(Objects.requireNonNull(ChemLib.class.getResourceAsStream(pPath))))).getAsJsonObject();
    }
}
