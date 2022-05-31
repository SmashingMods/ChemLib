package com.smashingmods.chemlib.datagen;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.api.*;
import com.smashingmods.chemlib.common.blocks.ChemicalBlock;
import com.smashingmods.chemlib.common.blocks.LampBlock;
import com.smashingmods.chemlib.common.items.ChemicalItem;
import com.smashingmods.chemlib.common.items.CompoundItem;
import com.smashingmods.chemlib.common.items.ElementItem;
import com.smashingmods.chemlib.registry.FluidRegistry;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class ItemModelGenerator extends ItemModelProvider {

    public ItemModelGenerator(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, ChemLib.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        generateElementModels();
        generateCompoundModels();

        ItemRegistry.getElements().stream().forEach(this::registerElement);
        ItemRegistry.getCompounds().stream().forEach(this::registerCompound);

        ItemRegistry.getChemicalItemsByTypeAsStream(ChemicalItemType.COMPOUND).forEach(this::registerCompoundDust);
        ItemRegistry.getChemicalItemsByTypeAsStream(ChemicalItemType.DUST).forEach(dust -> registerItem(dust.getChemicalName(), "dust"));
        ItemRegistry.getChemicalItemsByTypeAsStream(ChemicalItemType.NUGGET).forEach(nugget -> registerItem(nugget.getChemicalName(), "nugget"));
        ItemRegistry.getChemicalItemsByTypeAsStream(ChemicalItemType.INGOT).forEach(ingot -> registerItem(ingot.getChemicalName(), "ingot"));
        ItemRegistry.getChemicalItemsByTypeAsStream(ChemicalItemType.PLATE).forEach(plate -> registerItem(plate.getChemicalName(), "plate"));
        FluidRegistry.getBuckets().forEach(this::registerBucket);
        ItemRegistry.getChemicalBlockItems().stream().forEach(this::registerBlockItem);
    }

    private void generateElementModels() {
        for (String type : Arrays.asList("solid", "liquid", "gas")) {
            withExistingParent(String.format("item/element_%s_model", type), mcLoc("item/generated"))
                    .texture("layer0", modLoc(String.format("items/element_%s_layer_0", type)))
                    .texture("layer1", modLoc(String.format("items/element_%s_layer_1", type)));
        }
    }

    private void generateCompoundModels() {
        for (String type : Arrays.asList("solid", "liquid", "gas","dust")) {
            withExistingParent(String.format("item/compound_%s_model", type), mcLoc("item/generated"))
                    .texture("layer0", modLoc(String.format("items/compound_%s_layer_0", type)))
                    .texture("layer1", modLoc(String.format("items/compound_%s_layer_1", type)));
        }
    }

    private void registerElement(Element pElement) {
        withExistingParent(String.format("item/%s", pElement.getChemicalName()), modLoc("item/builtin_entity"));
    }

    private void registerCompound(CompoundItem pCompound) {
        switch (pCompound.getMatterState()) {
            case SOLID -> withExistingParent(String.format("item/%s", pCompound.getChemicalName()), modLoc("item/compound_solid_model"));
            case LIQUID -> withExistingParent(String.format("item/%s", pCompound.getChemicalName()), modLoc("item/compound_liquid_model"));
            case GAS -> withExistingParent(String.format("item/%s", pCompound.getChemicalName()), modLoc("item/compound_gas_model"));
        }
    }

    private void registerCompoundDust(ChemicalItem pItem) {
        withExistingParent(String.format("item/%s_dust", pItem.getChemicalName()), modLoc("item/compound_dust_model"));
    }

    private void registerItem(String pName, String pSuffix) {
        withExistingParent(String.format("item/%s_%s", pName, pSuffix), mcLoc("item/generated"))
                .texture("layer0", modLoc(String.format("items/%s", pSuffix)));
    }

    private void registerBlockItem(BlockItem pBlockItem) {
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

    private void registerBucket(BucketItem pBucket) {
        String path = pBucket.getRegistryName().getPath();
        int pieces = path.split("_").length;
        String chemicalName = "";

        for (int i = 0; i < pieces - 1; i++) {
            chemicalName = String.format("%s%s%s", chemicalName, chemicalName.isEmpty() ? "" : "_", path.split("_")[i]);
        }

        Chemical chemical = null;
        Optional<ElementItem> element = ItemRegistry.getElementByName(chemicalName);
        Optional<CompoundItem> compound = ItemRegistry.getCompoundByName(chemicalName);

        if (element.isPresent()) {
            chemical = element.get();
        } else if (compound.isPresent()) {
            chemical = compound.get();
        }

        MatterState matterState = chemical.getMatterState();

        switch (matterState) {
            case LIQUID -> withExistingParent(String.format("item/%s", Objects.requireNonNull(pBucket.getRegistryName()).getPath()), mcLoc("item/generated"))
                    .texture("layer0", modLoc("items/bucket_layer_0"))
                    .texture("layer1", modLoc("items/bucket_layer_1"));
            case GAS -> withExistingParent(String.format("item/%s", Objects.requireNonNull(pBucket.getRegistryName()).getPath()), mcLoc("item/generated"))
                    .texture("layer0", modLoc("items/gas_bucket_layer_0"))
                    .texture("layer1", modLoc("items/gas_bucket_layer_1"));
        }
    }
}
