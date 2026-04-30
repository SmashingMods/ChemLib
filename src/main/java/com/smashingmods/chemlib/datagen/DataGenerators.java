package com.smashingmods.chemlib.datagen;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.registry.PaintingsRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = ChemLib.MODID)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        event.createDatapackRegistryObjects(createDatapackEntriesBuilder());

        DataGenerator generator = event.getGenerator();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        BlockTagGenerator blockTags = new BlockTagGenerator(packOutput, lookupProvider, fileHelper);

        generator.addProvider(event.includeServer(), blockTags, fileHelper);
        generator.addProvider(event.includeClient(), new BlockStateGenerator(packOutput, fileHelper));
        generator.addProvider(event.includeClient(), new ItemModelGenerator(packOutput, fileHelper));
        generator.addProvider(event.includeServer(), new ItemTagGenerator(packOutput, lookupProvider, blockTags, fileHelper));
        generator.addProvider(event.includeServer(), new RecipeGenerator(packOutput));
        generator.addProvider(event.includeServer(), LootTableGenerator.create(packOutput));
        generator.addProvider(event.includeClient(), new LocalizationGenerator(packOutput, "en_us"));
        generator.addProvider(event.includeServer(), new PaintingVariantTagsGenerator(packOutput, lookupProvider, fileHelper));
    }

    private static RegistrySetBuilder createDatapackEntriesBuilder() {
        return new RegistrySetBuilder()
                .add(Registries.PAINTING_VARIANT, PaintingsRegistry::bootstrap);
    }
}
