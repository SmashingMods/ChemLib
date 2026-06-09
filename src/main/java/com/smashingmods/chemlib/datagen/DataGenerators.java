package com.smashingmods.chemlib.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber
public class DataGenerators {

    // 1.21.4 dropped GatherDataEvent#includeServer/#includeClient/#getExistingFileHelper: providers are now
    // added unconditionally via the event and the client/server split is driven by the run type. The event is
    // abstract and the NeoForge bus rejects listeners on it, so we subscribe to the concrete Client subclass
    // fired by this mod's single "clientData --all" run, which runs every provider registered here.
    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        BlockTagGenerator blockTags = event.addProvider(new BlockTagGenerator(packOutput, lookupProvider));

        event.addProvider(new BlockStateGenerator(packOutput));
        event.addProvider(new ItemModelGenerator(packOutput));
        event.addProvider(new ItemTagGenerator(packOutput, lookupProvider, blockTags));
        event.addProvider(new RecipeGenerator.Runner(packOutput, lookupProvider));
        event.addProvider(LootTableGenerator.create(packOutput, lookupProvider));
        event.addProvider(new LocalizationGenerator(packOutput, "en_us"));
    }
}
