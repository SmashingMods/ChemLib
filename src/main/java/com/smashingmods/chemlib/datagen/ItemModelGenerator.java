package com.smashingmods.chemlib.datagen;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.api.Chemical;
import com.smashingmods.chemlib.api.ChemicalItemType;
import com.smashingmods.chemlib.api.Element;
import com.smashingmods.chemlib.common.blocks.ChemicalBlock;
import com.smashingmods.chemlib.common.blocks.LampBlock;
import com.smashingmods.chemlib.common.items.ChemicalItem;
import com.smashingmods.chemlib.common.registry.ItemRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemModelGenerator extends ItemModelProvider {

    public ItemModelGenerator(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, ChemLib.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        generateElementModels();
        ItemRegistry.getElements().stream().forEach(this::registerElement);
        ItemRegistry.getCompounds().stream().map(Chemical::getChemicalName).forEach(this::registerCompound);

        ItemRegistry.getChemicalItemsByType(ChemicalItemType.COMPOUND).forEach(this::registerDust);
        ItemRegistry.getChemicalItemsByType(ChemicalItemType.DUST).forEach(this::registerDust);
        ItemRegistry.getChemicalItemsByType(ChemicalItemType.NUGGET).forEach(nugget -> registerItem(nugget.getChemicalName(), "nugget"));
        ItemRegistry.getChemicalItemsByType(ChemicalItemType.INGOT).forEach(ingot -> registerItem(ingot.getChemicalName(), "ingot"));
        ItemRegistry.getChemicalItemsByType(ChemicalItemType.PLATE).forEach(plate -> registerItem(plate.getChemicalName(), "plate"));

        ItemRegistry.BLOCK_ITEMS.stream().forEach(this::registerBlockItem);
    }

    public void generateElementModels() {
        withExistingParent("item/element_solid_model", mcLoc("item/generated"))
                .texture("layer0", modLoc("items/element_solid_layer_0"))
                .texture("layer1", modLoc("items/element_solid_layer_1"));

        withExistingParent("item/element_liquid_model", mcLoc("item/generated"))
                .texture("layer0", modLoc("items/element_liquid_layer_0"))
                .texture("layer1", modLoc("items/element_liquid_layer_1"));

        withExistingParent("item/element_gas_model", mcLoc("item/generated"))
                .texture("layer0", modLoc("items/element_gas_layer_0"))
                .texture("layer1", modLoc("items/element_gas_layer_1"));
    }

    public void registerElement(Element pElement) {
        switch (pElement.getMatterState()) {
            case SOLID -> withExistingParent(String.format("item/%s", pElement.getChemicalName()), modLoc("item/element_solid_model"));
            case LIQUID -> withExistingParent(String.format("item/%s", pElement.getChemicalName()), modLoc("item/element_liquid_model"));
            case GAS -> withExistingParent(String.format("item/%s", pElement.getChemicalName()), modLoc("item/element_gas_model"));
        }
    }

    public void registerCompound(String pName) {
        withExistingParent(String.format("item/%s", pName), mcLoc("item/generated"))
                .texture("layer0", modLoc("items/compound"))
                .texture("layer1", modLoc("items/compound_overlay_vial"));
    }

    public void registerDust(ChemicalItem pItem) {
        withExistingParent(String.format("item/%s_dust", pItem.getChemicalName()), mcLoc("item/generated"))
                .texture("layer0", modLoc("items/dust_layer_0"))
                .texture("layer1", modLoc("items/dust_layer_1"));
    }

    public void registerItem(String pName, String pSuffix) {
        withExistingParent(String.format("item/%s_%s", pName, pSuffix), mcLoc("item/generated"))
                .texture("layer0", modLoc(String.format("items/%s", pSuffix)));
    }

    public void registerBlockItem(BlockItem pBlockItem) {
        ChemicalBlock block = (ChemicalBlock) pBlockItem.getBlock();
        String type = block.getBlockType().getSerializedName();
        String name = String.format("item/%s_%s_block", block.getChemicalName(), type);
        ResourceLocation parent = modLoc(String.format("block/%s_%s_block", block.getChemicalName(), type));

        if (pBlockItem.getBlock() instanceof LampBlock) {
            ResourceLocation texture = modLoc(String.format("block/%s_block", type));
            withExistingParent(name, parent).texture("block", texture);
        } else {
            ResourceLocation texture = modLoc(String.format("block/%s_block", type));
            withExistingParent(name, parent).texture("block", texture);
        }

    }
}
