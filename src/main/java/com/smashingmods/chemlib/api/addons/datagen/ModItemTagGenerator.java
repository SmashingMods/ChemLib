package com.smashingmods.chemlib.api.addons.datagen;

import com.smashingmods.chemlib.api.addons.registry.AddonRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeRegistryTagsProvider;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Objects;

public class ModItemTagGenerator extends ForgeRegistryTagsProvider<Item> {
    private final AddonRegistry addonRegistry;

    public ModItemTagGenerator(DataGenerator generator, AddonRegistry pAddonRegistry, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, ForgeRegistries.ITEMS, pAddonRegistry.getModID(), existingFileHelper);
        addonRegistry = pAddonRegistry;
    }

    @Override
    protected void addTags() {
        addonRegistry.getCompoundItemsAsStream().forEach(item -> {
            String type = item.getItemType().getSerializedName();
            String name = item.getChemicalName();
            TagKey<Item> key = Objects.requireNonNull(ForgeRegistries.ITEMS.tags()).createTagKey(new ResourceLocation("forge", String.format("%ss/%s", type, name)));
            tag(key).add(item);
        });
    }

    @Override
    @Nonnull
    public String getName() {
        return addonRegistry.getModID() + ":tags";
    }
}
