package com.smashingmods.chemlib.datagen;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.api.ChemicalBlockType;
import com.smashingmods.chemlib.common.blocks.ChemicalBlock;
import com.smashingmods.chemlib.registry.BlockRegistry;
import com.smashingmods.chemlib.registry.FluidRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class BlockStateGenerator extends BlockStateProvider {

    private final ExistingFileHelper fileHelper;

    public BlockStateGenerator(PackOutput packOutput, ExistingFileHelper exFileHelper) {
        super(packOutput, ChemLib.MODID, exFileHelper);
        this.fileHelper = exFileHelper;
    }

    @Override
    protected void registerStatesAndModels() {
        generateBlockModel("metal", "metal_block");
        generateBlockModel("lamp_off", "lamp_block");
        generateBlockModel("lamp_on", "lamp_on");

        BlockRegistry.getChemicalBlocksByType(ChemicalBlockType.METAL).forEach(this::registerMetalBlock);
        BlockRegistry.getChemicalBlocksByType(ChemicalBlockType.LAMP).forEach(this::registerLampBlock);
        FluidRegistry.getLiquidBlocks().forEach(this::registerLiquidBlock);
    }

    private void generateBlockModel(String name, String texture) {
        models().withExistingParent(String.format("block/%s_model", name), mcLoc("block/block"))
                .texture("all", modLoc(String.format("block/%s", texture)))
                .texture("particle", "#all")
                .element()
                .cube("#all")
                .faces((direction, faceBuilder) ->
                        faceBuilder.uvs(0f, 0f, 16f, 16f)
                                .tintindex(0)
                                .end())
                .end();
    }

    private void registerMetalBlock(ChemicalBlock block) {
        String name = String.format("block/%s_metal_block", block.getChemicalName());

        models().withExistingParent(name, modLoc("block/metal_model"))
                .texture("all", modLoc("block/metal_block"));

        ModelFile modelFile = new ModelFile.ExistingModelFile(modLoc("block/metal_model"), fileHelper);
        getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder().modelFile(modelFile).build());
    }

    private void registerLampBlock(ChemicalBlock block) {
        String off = String.format("block/%s_lamp_block", block.getChemicalName());
        String on = String.format("block/%s_lamp_block_on", block.getChemicalName());

        models().withExistingParent(off, modLoc("block/lamp_off_model"));
        models().withExistingParent(on, modLoc("block/lamp_on_model"));

        getVariantBuilder(block).forAllStates(state -> {
            ModelFile modelFile = new ModelFile.ExistingModelFile(modLoc(String.format("block/lamp_%s_model", state.getValue(BlockStateProperties.LIT) ? "on" : "off")), fileHelper);
            return ConfiguredModel.builder()
                    .modelFile(modelFile)
                    .build();
        });
    }

    private void registerLiquidBlock(LiquidBlock block) {
        ModelFile modelFile = new ModelFile.ExistingModelFile(mcLoc("block/water"), fileHelper);
        getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder().modelFile(modelFile).build());
    }
}
