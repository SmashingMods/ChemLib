package com.smashingmods.chemlib.api.modadditions.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockStateGenerator extends BlockStateProvider {
    private final DeferredRegister<Block> blocks;
    private final ExistingFileHelper existingFileHelper;
    public ModBlockStateGenerator(DataGenerator gen, String modid, DeferredRegister<Block> pBlocks, ExistingFileHelper exFileHelper) {
        super(gen, modid, exFileHelper);
        blocks = pBlocks;
        existingFileHelper = exFileHelper;
    }

    @Override
    protected void registerStatesAndModels() {
        for(LiquidBlock block : blocks.getEntries().stream().map(RegistryObject::get).map(block -> (LiquidBlock) block).toList()) {
            ModelFile modelFile = new ModelFile.ExistingModelFile(mcLoc("block/water"), existingFileHelper);
            getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder().modelFile(modelFile).build());
        }
    }
}
