package com.smashingmods.chemlib.datagen;

import com.ibm.icu.text.Normalizer2;
import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.api.ChemicalBlockType;
import com.smashingmods.chemlib.common.blocks.ChemicalBlock;
import com.smashingmods.chemlib.registry.BlockRegistry;
import com.smashingmods.chemlib.registry.FluidRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class BlockStateGenerator extends BlockStateProvider {

    private final ExistingFileHelper existingFileHelper;

    public BlockStateGenerator(DataGenerator pGenerator, ExistingFileHelper pExistingFileHelper) {
        super(pGenerator, ChemLib.MODID, pExistingFileHelper);
        this.existingFileHelper = pExistingFileHelper;
    }

    @Override
    protected void registerStatesAndModels() {
        ModelFile dustBlock = new ModelFile.ExistingModelFile(modLoc("block/dust_block"),models().existingFileHelper);
        generateBlockModel("metal", "metal_block");
        generateBlockModel("lamp_off", "lamp_block");
        generateBlockModel("lamp_on", "lamp_on");

        BlockRegistry.getChemicalBlocksByType(ChemicalBlockType.METAL).forEach(this::registerMetalBlock);
        BlockRegistry.getChemicalBlocksByType(ChemicalBlockType.DUST).forEach(this::registerDustBlock);
        BlockRegistry.getChemicalBlocksByType(ChemicalBlockType.LAMP).forEach(this::registerLampBlock);
        FluidRegistry.getLiquidBlocks().forEach(this::registerLiquidBlock);
    }

    private void generateBlockModel(String pName, String pTexture) {
        models().withExistingParent(String.format("block/%s_model", pName), mcLoc("block/block"))
                .texture("all", modLoc(String.format("block/%s", pTexture)))
                .texture("particle", "#all")
                .element()
                .cube("#all")
                .faces((direction, faceBuilder) ->
                        faceBuilder.uvs(0f, 0f, 16f, 16f)
                                .tintindex(0)
                                .end())
                .end();
    }

    private void registerMetalBlock(ChemicalBlock pBlock) {
        String name = String.format("block/%s_metal_block", pBlock.getChemicalName());

        models().withExistingParent(name, modLoc("block/metal_model"))
                .texture("all", modLoc("block/metal_block"));

        ModelFile modelFile = new ModelFile.ExistingModelFile(modLoc("block/metal_model"), existingFileHelper);
        getVariantBuilder(pBlock).forAllStates(state -> ConfiguredModel.builder().modelFile(modelFile).build());
    }

    private void registerDustBlock(ChemicalBlock pBlock) {
        String name = String.format("block/%s_dust_block", pBlock.getChemicalName());

        models().withExistingParent(name, modLoc("block/dust_block"))
                .texture("all", modLoc("block/dust_block"));

        ModelFile modelFile = new ModelFile.ExistingModelFile(modLoc("block/dust_block"), existingFileHelper);
        getVariantBuilder(pBlock).forAllStates(state -> ConfiguredModel.builder().modelFile(modelFile).build());
    }

    private void registerLampBlock(ChemicalBlock pBlock) {
        String off = String.format("block/%s_lamp_block", pBlock.getChemicalName());
        String on = String.format("block/%s_lamp_block_on", pBlock.getChemicalName());

        models().withExistingParent(off, modLoc("block/lamp_off_model"));
        models().withExistingParent(on, modLoc("block/lamp_on_model"));

        getVariantBuilder(pBlock).forAllStates(state -> {
            ModelFile modelFile = new ModelFile.ExistingModelFile(modLoc(String.format("block/lamp_%s_model", state.getValue(BlockStateProperties.LIT) ? "on" : "off")), existingFileHelper);
            return ConfiguredModel.builder()
                    .modelFile(modelFile)
                    .build();
        });
    }

    private void registerLiquidBlock(LiquidBlock pBlock) {
        ModelFile modelFile = new ModelFile.ExistingModelFile(mcLoc("block/water"), existingFileHelper);
        getVariantBuilder(pBlock).forAllStates(state -> ConfiguredModel.builder().modelFile(modelFile).build());
    }
}
