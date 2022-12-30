package com.smashingmods.chemlib.datagen;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.api.Chemical;
import com.smashingmods.chemlib.api.ChemicalItemType;
import com.smashingmods.chemlib.api.Element;
import com.smashingmods.chemlib.api.MatterState;
import com.smashingmods.chemlib.common.blocks.ChemicalBlock;
import com.smashingmods.chemlib.common.items.ChemicalBlockItem;
import com.smashingmods.chemlib.common.items.ChemicalItem;
import com.smashingmods.chemlib.common.items.CompoundItem;
import com.smashingmods.chemlib.common.items.ElementItem;
import com.smashingmods.chemlib.registry.FluidRegistry;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

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
        generateChemicalItemModels();

        ItemRegistry.getElements().forEach(this::registerElement);
        ItemRegistry.getCompounds().forEach(this::registerCompound);

        ItemRegistry.getChemicalItemsByTypeAsStream(ChemicalItemType.COMPOUND).forEach(this::registerCompoundDust);
        ItemRegistry.getChemicalItemsByTypeAsStream(ChemicalItemType.DUST).forEach(dust -> registerItem(dust.getChemicalName(), "dust"));
        ItemRegistry.getChemicalItemsByTypeAsStream(ChemicalItemType.NUGGET).forEach(nugget -> registerItem(nugget.getChemicalName(), "nugget"));
        ItemRegistry.getChemicalItemsByTypeAsStream(ChemicalItemType.INGOT).forEach(ingot -> registerItem(ingot.getChemicalName(), "ingot"));
        ItemRegistry.getChemicalItemsByTypeAsStream(ChemicalItemType.PLATE).forEach(plate -> {
            if(!plate.getChemicalName().equals("polyvinyl_chloride")) {
                registerItem(plate.getChemicalName(), "plate");
            }
        });

        FluidRegistry.getBuckets().forEach(this::registerBucket);
        ItemRegistry.getChemicalBlockItems().forEach(this::registerChemicalBlockItems);
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

    private void generateChemicalItemModels() {
        Arrays.stream(ChemicalItemType.values())
                .map(ChemicalItemType::getSerializedName)
                .forEach(type ->
                        withExistingParent(String.format("item/chemical_%s_model", type), mcLoc("item/generated"))
                                .texture("layer0", modLoc(String.format("items/%s", type))));
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

    private void registerItem(String pName, String pType) {
        withExistingParent(String.format("item/%s_%s", pName, pType), modLoc("item/builtin_entity"));
    }

    private void registerChemicalBlockItems(ChemicalBlockItem pBlockItem) {
        ChemicalBlock block = (ChemicalBlock) pBlockItem.getBlock();
        String type = block.getBlockType().getSerializedName();
        String name = String.format("item/%s_%s_block", block.getChemicalName(), type);
        ResourceLocation parent = modLoc(String.format("block/%s_%s_block", block.getChemicalName(), type));
        ResourceLocation texture = modLoc(String.format("block/%s_block", type));
        withExistingParent(name, parent).texture("layer0", texture);
    }

    private void registerBucket(BucketItem pBucket) {
        String path = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(pBucket)).getPath();
        int pieces = path.split("_").length;
        String chemicalName = "";

        for (int i = 0; i < pieces - 1; i++) {
            chemicalName = String.format("%s%s%s", chemicalName, chemicalName.isEmpty() ? "" : "_", path.split("_")[i]);
        }

        Chemical chemical = null;
        Optional<ElementItem> optionalElement = ItemRegistry.getElementByName(chemicalName);
        Optional<CompoundItem> optionalCompound = ItemRegistry.getCompoundByName(chemicalName);

        if (optionalElement.isPresent()) {
            chemical = optionalElement.get();
        } else if (optionalCompound.isPresent()) {
            chemical = optionalCompound.get();
        }

        MatterState matterState = Objects.requireNonNull(chemical).getMatterState();

        switch (matterState) {
            case LIQUID -> withExistingParent(String.format("item/%s", path), mcLoc("item/generated"))
                    .texture("layer0", modLoc("items/bucket_layer_0"))
                    .texture("layer1", modLoc("items/bucket_layer_1"));
            case GAS -> withExistingParent(String.format("item/%s", path), mcLoc("item/generated"))
                    .texture("layer0", modLoc("items/gas_bucket_layer_0"))
                    .texture("layer1", modLoc("items/gas_bucket_layer_1"));
        }
    }
}
