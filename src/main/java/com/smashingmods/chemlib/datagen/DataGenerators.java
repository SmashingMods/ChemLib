package com.smashingmods.chemlib.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        generator.addProvider(event.includeClient(), new BlockStateGenerator(generator, event.getExistingFileHelper()));
        generator.addProvider(event.includeClient(), new ItemModelGenerator(generator, event.getExistingFileHelper()));
        generator.addProvider(event.includeServer(), new ItemTagGenerator(generator, event.getExistingFileHelper()));
        generator.addProvider(event.includeServer(), new BlockTagGenerator(generator, event.getExistingFileHelper()));
        generator.addProvider(event.includeServer(), new RecipeGenerator(generator));
        generator.addProvider(event.includeServer(), new LootTableGenerator(generator));
        generator.addProvider(event.includeClient(), new LocalizationGenerator(generator, "en_us"));
    }
}
