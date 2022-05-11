package com.smashingmods.chemlib.datagen;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.common.registry.BlockRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockStateGenerator extends BlockStateProvider {

    public BlockStateGenerator(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, ChemLib.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        BlockRegistry.BLOCKS.getEntries().forEach(blockRegistryObject ->
                simpleBlock(blockRegistryObject.get())
        );
    }
}
