package com.smashingmods.chemlib.datagen;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.common.registry.ItemRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.Objects;

public class ItemModelGenerator extends ItemModelProvider {

    public ItemModelGenerator(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, ChemLib.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        ItemRegistry.ELEMENTS.stream().forEach(elementItem -> registerElement(elementItem.getName()));
        ItemRegistry.COMPOUNDS.stream().forEach(compoundItem -> registerCompound(compoundItem.getName()));
        ItemRegistry.DUSTS.stream().forEach(dust -> registerItem(dust.getName(), "dust"));
        ItemRegistry.NUGGETS.stream().forEach(nugget -> registerItem(nugget.getName(), "nugget"));
        ItemRegistry.INGOTS.stream().forEach(ingot -> registerItem(ingot.getName(), "ingot"));
        ItemRegistry.PLATES.stream().forEach(plate -> registerItem(plate.getName(), "plate"));
        ItemRegistry.BLOCK_ITEMS.stream().forEach(blockItem -> {
            Objects.requireNonNull(blockItem.getRegistryName());
            String name = blockItem.getRegistryName().getPath();
            withExistingParent(name, new ResourceLocation(ChemLib.MODID, String.format("block/%s", blockItem.getRegistryName().getPath())));
        });
    }

    public void registerElement(String pElementName) {
        withExistingParent(pElementName, new ResourceLocation("item/generated"))
                .texture("layer0", new ResourceLocation(ChemLib.MODID, "items/element"))
                .texture("layer1", new ResourceLocation(ChemLib.MODID, "items/element_overlay_vial"));
    }

    public void registerCompound(String pCompoundName) {
        withExistingParent(pCompoundName, new ResourceLocation("item/generated"))
                .texture("layer0", new ResourceLocation(ChemLib.MODID, "items/compound"))
                .texture("layer1", new ResourceLocation(ChemLib.MODID, "items/compound_overlay_vial"));
    }

    public void registerItem(String pName, String pSuffix) {
        withExistingParent(String.format("%s_%s", pName, pSuffix), new ResourceLocation("item/generated"))
                .texture("layer0", new ResourceLocation(ChemLib.MODID, String.format("items/%s", pSuffix)));
    }
}
