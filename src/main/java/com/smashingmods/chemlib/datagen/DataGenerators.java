package com.smashingmods.chemlib.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        generator.addProvider(new BlockStateGenerator(generator, event.getExistingFileHelper()));
        generator.addProvider(new ItemModelGenerator(generator, event.getExistingFileHelper()));
        generator.addProvider(new ItemTagGenerator(generator, event.getExistingFileHelper()));
        generator.addProvider(new BlockTagGenerator(generator, event.getExistingFileHelper()));
        generator.addProvider(new RecipeGenerator(generator));
        generator.addProvider(new LootTableGenerator(generator));
        generator.addProvider(new LocalizationGenerator(generator, "en_us"));
    }
}
