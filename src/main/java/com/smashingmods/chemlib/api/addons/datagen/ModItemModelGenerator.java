package com.smashingmods.chemlib.api.addons.datagen;

import com.smashingmods.chemlib.api.MatterState;
import com.smashingmods.chemlib.api.addons.registry.AddonRegisters;
import com.smashingmods.chemlib.common.items.ChemicalItem;
import com.smashingmods.chemlib.common.items.CompoundItem;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.BucketItem;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class ModItemModelGenerator extends ItemModelProvider {
    private final AddonRegisters addonRegisters;

    public ModItemModelGenerator(DataGenerator generator, AddonRegisters pAddonRegisters, ExistingFileHelper existingFileHelper) {
        super(generator, pAddonRegisters.getModID(), makeNewHelper(existingFileHelper));
        addonRegisters = pAddonRegisters;
    }

    private static ExistingFileHelper makeNewHelper(ExistingFileHelper pExistingFileHelper) {
        return pExistingFileHelper;
    }

    @Override
    protected void registerModels() {
        generateCompoundModels();
        addonRegisters.getCompounds().forEach(this::registerCompound);
        addonRegisters.getCompoundItemsAsStream().forEach(this::registerCompoundDust);
        addonRegisters.getBucketsAsStream().forEach(this::registerBucket);
    }

    private void generateCompoundModels() {
        for (String type : Arrays.asList("solid", "liquid", "gas", "dust")) {
            withExistingParent(String.format("item/compound_%s_model", type), mcLoc("item/generated"))
                    .texture("layer0", String.format("chemlib:items/compound_%s_layer_0", type))
                    .texture("layer1", String.format("chemlib:items/compound_%s_layer_1", type));
            ;
        }
        withExistingParent("item/chemical_dust_model", mcLoc("item/generated"))
                .texture("layer0", "chemlib:items/dust")
        ;
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

    private void registerBucket(BucketItem pBucket) {
        String path = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(pBucket)).getPath();
        int pieces = path.split("_").length;
        String chemicalName = "";
        for (int i = 0; i < pieces - 1; i++) {
            chemicalName = String.format("%s%s%s", chemicalName, chemicalName.isEmpty() ? "" : "_", path.split("_")[i]);
        }
        Optional<CompoundItem> optionalCompound = addonRegisters.getCompoundByName(chemicalName);
        if (optionalCompound.isEmpty()) {
            return;
        }
        MatterState matterState = Objects.requireNonNull(optionalCompound.get()).getMatterState();
        switch (matterState) {
            case LIQUID -> withExistingParent(String.format("item/%s", path), mcLoc("item/generated"))
                    .texture("layer0", "chemlib:items/bucket_layer_0")
                    .texture("layer1", "chemlib:items/bucket_layer_1");
            case GAS -> withExistingParent(String.format("item/%s", path), mcLoc("item/generated"))
                    .texture("layer0", "chemlib:items/gas_bucket_layer_0")
                    .texture("layer1", "chemlib:items/gas_bucket_layer_1");
        }
    }
}