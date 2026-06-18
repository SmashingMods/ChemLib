package com.smashingmods.chemlib.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        BlockTagGenerator blockTags = new BlockTagGenerator(packOutput, lookupProvider, fileHelper);

        generator.addProvider(event.includeServer(), blockTags);
        generator.addProvider(event.includeClient(), new BlockStateGenerator(packOutput, fileHelper));
        generator.addProvider(event.includeClient(), new ItemModelGenerator(packOutput, fileHelper));
        generator.addProvider(event.includeServer(), new ItemTagGenerator(packOutput, lookupProvider, blockTags, fileHelper));
        generator.addProvider(event.includeServer(), new RecipeGenerator(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), LootTableGenerator.create(packOutput, lookupProvider));
        generator.addProvider(event.includeClient(), new LocalizationGenerator(packOutput, "en_us"));
    }
}
