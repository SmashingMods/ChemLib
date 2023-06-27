package com.smashingmods.chemlib.registry;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.smashingmods.chemlib.ChemLib;
import net.minecraftforge.eventbus.api.IEventBus;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;

public class Registry {

    public static void register(IEventBus pModEventBus) {
        ChemicalRegistry.register();
        BlockRegistry.register(pModEventBus);
        FluidRegistry.register(pModEventBus);
        ItemRegistry.register(pModEventBus);
        TabsRegistry.register(pModEventBus);
        PaintingsRegistry.register(pModEventBus);
    }

    public static JsonObject getStreamAsJsonObject(String pPath) {
        return JsonParser.parseReader(new BufferedReader(new InputStreamReader(Objects.requireNonNull(ChemLib.class.getResourceAsStream(pPath))))).getAsJsonObject();
    }
}
