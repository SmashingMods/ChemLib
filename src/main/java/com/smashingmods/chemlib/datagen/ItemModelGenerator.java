package com.smashingmods.chemlib.datagen;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.api.Chemical;
import com.smashingmods.chemlib.api.ChemicalItemType;
import com.smashingmods.chemlib.common.blocks.ChemicalBlock;
import com.smashingmods.chemlib.common.registry.ItemRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.BlockItem;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemModelGenerator extends ItemModelProvider {

    public ItemModelGenerator(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, ChemLib.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        ItemRegistry.getElements().stream().map(Chemical::getChemicalName).forEach(this::registerElement);
        ItemRegistry.getCompounds().stream().map(Chemical::getChemicalName).forEach(this::registerCompound);
        ItemRegistry.getChemicalItemsByType(ChemicalItemType.COMPOUND).forEach(dust -> registerItem(dust.getChemicalName(), "dust"));
        ItemRegistry.getChemicalItemsByType(ChemicalItemType.DUST).forEach(dust -> registerItem(dust.getChemicalName(), "dust"));
        ItemRegistry.getChemicalItemsByType(ChemicalItemType.NUGGET).forEach(nugget -> registerItem(nugget.getChemicalName(), "nugget"));
        ItemRegistry.getChemicalItemsByType(ChemicalItemType.INGOT).forEach(ingot -> registerItem(ingot.getChemicalName(), "ingot"));
        ItemRegistry.getChemicalItemsByType(ChemicalItemType.PLATE).forEach(plate -> registerItem(plate.getChemicalName(), "plate"));
        ItemRegistry.BLOCK_ITEMS.stream().forEach(this::registerBlockItem);
    }

    public void registerElement(String pName) {
        withExistingParent(String.format("item/%s", pName), mcLoc("item/generated"))
                .texture("layer0", modLoc("items/element"))
                .texture("layer1", modLoc("items/element_overlay_vial"));
    }

    public void registerCompound(String pName) {
        withExistingParent(String.format("item/%s", pName), mcLoc("item/generated"))
                .texture("layer0", modLoc("items/compound"))
                .texture("layer1", modLoc("items/compound_overlay_vial"));
    }

    public void registerItem(String pName, String pSuffix) {
        withExistingParent(String.format("item/%s_%s", pName, pSuffix), mcLoc("item/generated"))
                .texture("layer0", modLoc(String.format("items/%s", pSuffix)));
    }

    public void registerBlockItem(BlockItem pBlockItem) {
        ChemicalBlock block = (ChemicalBlock) pBlockItem.getBlock();
        withExistingParent(String.format("item/%s_%s_block", block.getChemicalName(), block.getBlockType().getSerializedName()), modLoc(String.format("block/%s_%s_block", block.getChemicalName(), block.getBlockType().getSerializedName())))
                .texture("block", modLoc(String.format("block/%s_block", block.getBlockType().getSerializedName())));
    }
}
