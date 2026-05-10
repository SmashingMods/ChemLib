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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class ItemModelGenerator extends ItemModelProvider {

    public ItemModelGenerator(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, ChemLib.MODID, exFileHelper);
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
                    .texture("layer0", modLoc(String.format("item/element_%s_layer_0", type)))
                    .texture("layer1", modLoc(String.format("item/element_%s_layer_1", type)));
        }
    }

    private void generateCompoundModels() {
        for (String type : Arrays.asList("solid", "liquid", "gas","dust")) {
            withExistingParent(String.format("item/compound_%s_model", type), mcLoc("item/generated"))
                    .texture("layer0", modLoc(String.format("item/compound_%s_layer_0", type)))
                    .texture("layer1", modLoc(String.format("item/compound_%s_layer_1", type)));
        }
    }

    private void generateChemicalItemModels() {
        Arrays.stream(ChemicalItemType.values())
                .map(ChemicalItemType::getSerializedName)
                .forEach(type ->
                        withExistingParent(String.format("item/chemical_%s_model", type), mcLoc("item/generated"))
                                .texture("layer0", modLoc(String.format("item/%s", type))));
    }

    private void registerElement(Element element) {
        withExistingParent(String.format("item/%s", element.getChemicalName()), modLoc("item/builtin_entity"));
    }

    private void registerCompound(CompoundItem compound) {
        switch (compound.getMatterState()) {
            case SOLID -> withExistingParent(String.format("item/%s", compound.getChemicalName()), modLoc("item/compound_solid_model"));
            case LIQUID -> withExistingParent(String.format("item/%s", compound.getChemicalName()), modLoc("item/compound_liquid_model"));
            case GAS -> withExistingParent(String.format("item/%s", compound.getChemicalName()), modLoc("item/compound_gas_model"));
        }
    }

    private void registerCompoundDust(ChemicalItem chemical) {
        withExistingParent(String.format("item/%s_dust", chemical.getChemicalName()), modLoc("item/compound_dust_model"));
    }

    private void registerItem(String name, String type) {
        withExistingParent(String.format("item/%s_%s", name, type), modLoc("item/builtin_entity"));
    }

    private void registerChemicalBlockItems(ChemicalBlockItem blockItem) {
        ChemicalBlock block = (ChemicalBlock) blockItem.getBlock();
        String type = block.getBlockType().getSerializedName();
        String name = String.format("item/%s_%s_block", block.getChemicalName(), type);
        ResourceLocation parent = modLoc(String.format("block/%s_%s_block", block.getChemicalName(), type));
        ResourceLocation texture = modLoc(String.format("block/%s_block", type));
        withExistingParent(name, parent).texture("layer0", texture);
    }

    private void registerBucket(BucketItem bucket) {
        String path = Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(bucket)).getPath();
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
        // TODO 1.21: Use DynamicFluidContainerModel https://docs.neoforged.net/docs/1.21.1/resources/client/models/modelloaders#dynamic-fluid-container-model
        switch (matterState) {
            case LIQUID -> withExistingParent(String.format("item/%s", path), mcLoc("item/generated"))
                    .texture("layer0", modLoc("item/bucket_layer_0"))
                    .texture("layer1", modLoc("item/bucket_layer_1"));
            case GAS -> withExistingParent(String.format("item/%s", path), mcLoc("item/generated"))
                    .texture("layer0", modLoc("item/gas_bucket_layer_0"))
                    .texture("layer1", modLoc("item/gas_bucket_layer_1"));
        }
    }
}
