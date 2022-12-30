package com.smashingmods.chemlib.datagen;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.api.modadditions.registry.AddonRegisters;
import com.smashingmods.chemlib.common.items.ChemicalItem;
import com.smashingmods.chemlib.common.items.CompoundItem;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ModItemModelGenerator extends ItemModelProvider {
    private final List<String> COMPOUND_TYPES = Arrays.asList("solid", "liquid", "gas","dust");
    private final AddonRegisters modRegister;
    public ModItemModelGenerator(AddonRegisters pModRegister, DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, pModRegister.getModID(), existingFileHelper);
        modRegister = pModRegister;
    }

    @Override
    protected void registerModels() {
        generateCompoundModels();
        getCompounds(modRegister).forEach(this::registerCompound);
//        getChemicalItemsByModAndTypeAsStream(this.modid, ChemicalItemType.COMPOUND).forEach(chemical -> registerModCompoundDust(modid, chemical));
//        FluidRegistry.getModBuckets(this.modid).forEach(this::registerBucket);
    }

    private void generateCompoundModels() {
        for (String type : COMPOUND_TYPES) {
            withExistingParent(String.format("item/compound_%s_model", type), mcLoc("item/generated"))
                    .texture("layer0", String.format("%s:items/compound_%s_layer_0", ChemLib.MODID, type))//new ResourceLocation(ChemLib.MODID, String.format("items/compound_%s_layer_0", type)))
                    .texture("layer1", String.format("%s:items/compound_%s_layer_1", ChemLib.MODID, type));//new ResourceLocation(ChemLib.MODID, String.format("items/compound_%s_layer_1", type)));
        }
    }

    private void registerCompound(CompoundItem pCompound) {
        switch (pCompound.getMatterState()) {
            case SOLID -> withExistingParent(String.format("item/%s", pCompound.getChemicalName()), modLoc("item/compound_solid_model"));
            case LIQUID -> withExistingParent(String.format("item/%s", pCompound.getChemicalName()), modLoc("item/compound_liquid_model"));
            case GAS -> withExistingParent(String.format("item/%s", pCompound.getChemicalName()), modLoc("item/compound_gas_model"));
        }
    }

    private Stream<CompoundItem> getCompounds(AddonRegisters pAddonRegisters) {
        return pAddonRegisters.COMPOUNDS.getEntries().stream().map(RegistryObject::get).map(item -> (CompoundItem) item);
    }

    private void registerModCompoundDust(String pModId, ChemicalItem pItem) {
        withExistingParent(String.format("item/%s_dust", pItem.getChemicalName()), new ResourceLocation(pModId, "item/compound_dust_model"));
    }

//    private void registerBucket(BucketItem pBucket) {
//        String path = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(pBucket)).getPath();
//        int pieces = path.split("_").length;
//        String chemicalName = "";
//
//        for (int i = 0; i < pieces - 1; i++) {
//            chemicalName = String.format("%s%s%s", chemicalName, chemicalName.isEmpty() ? "" : "_", path.split("_")[i]);
//        }
//
//        Chemical chemical = null;
//        Optional<ElementItem> optionalElement = ItemRegistry.getElementByName(chemicalName);
//        Optional<CompoundItem> optionalCompound = ItemRegistry.getCompoundByName(chemicalName);
//
//        if (optionalElement.isPresent()) {
//            chemical = optionalElement.get();
//        } else if (optionalCompound.isPresent()) {
//            chemical = optionalCompound.get();
//        }
//
//        MatterState matterState = Objects.requireNonNull(chemical).getMatterState();
//
//        switch (matterState) {
//            case LIQUID -> withExistingParent(String.format("item/%s", path), mcLoc("item/generated"))
//                    .texture("layer0", modLoc("items/bucket_layer_0"))
//                    .texture("layer1", modLoc("items/bucket_layer_1"));
//            case GAS -> withExistingParent(String.format("item/%s", path), mcLoc("item/generated"))
//                    .texture("layer0", modLoc("items/gas_bucket_layer_0"))
//                    .texture("layer1", modLoc("items/gas_bucket_layer_1"));
//        }
//    }
}
