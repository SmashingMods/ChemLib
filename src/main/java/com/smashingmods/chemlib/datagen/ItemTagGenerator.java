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

import java.util.Objects;

public class ItemTagGenerator extends ForgeRegistryTagsProvider<Item> {

    public ItemTagGenerator(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, ForgeRegistries.ITEMS, ChemLib.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        ItemRegistry.INGOTS.stream().forEach(ingotItem -> {
            Objects.requireNonNull(ForgeRegistries.ITEMS.tags());
            TagKey<Item> tagKey = ForgeRegistries.ITEMS.tags().createTagKey(new ResourceLocation("forge", String.format("ingots/%s",ingotItem.getName())));
            tag(tagKey).add(ingotItem);
        });
    }

    @Override
    public String getName() {
        return null;
    }
}
