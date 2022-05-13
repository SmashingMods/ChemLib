package com.smashingmods.chemlib.datagen;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.common.registry.ItemRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeRegistryTagsProvider;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.Objects;

public class ItemTagGenerator extends ForgeRegistryTagsProvider<Item> {

    public ItemTagGenerator(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, ForgeRegistries.ITEMS, ChemLib.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        ItemRegistry.DUSTS.stream().forEach(dust -> {
            TagKey<Item> ingotKey = Objects.requireNonNull(ForgeRegistries.ITEMS.tags()).createTagKey(new ResourceLocation("forge", String.format("dusts/%s", dust.getName())));
            tag(ingotKey).add(dust);
        });
        ItemRegistry.INGOTS.stream().forEach(nugget -> {
            TagKey<Item> ingotKey = Objects.requireNonNull(ForgeRegistries.ITEMS.tags()).createTagKey(new ResourceLocation("forge", String.format("nuggets/%s", nugget.getName())));
            tag(ingotKey).add(nugget);
        });
        ItemRegistry.INGOTS.stream().forEach(ingot -> {
            TagKey<Item> ingotKey = Objects.requireNonNull(ForgeRegistries.ITEMS.tags()).createTagKey(new ResourceLocation("forge", String.format("ingots/%s", ingot.getName())));
            tag(ingotKey).add(ingot);
        });
        ItemRegistry.INGOTS.stream().forEach(plate -> {
            TagKey<Item> ingotKey = Objects.requireNonNull(ForgeRegistries.ITEMS.tags()).createTagKey(new ResourceLocation("forge", String.format("plates/%s", plate.getName())));
            tag(ingotKey).add(plate);
        });
    }

    @Override
    @Nonnull
    public String getName() {
        return ChemLib.MODID + ":tags";
    }
}
