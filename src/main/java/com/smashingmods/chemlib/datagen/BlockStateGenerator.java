package com.smashingmods.chemlib.datagen;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.api.ChemicalBlockType;
import com.smashingmods.chemlib.common.blocks.ChemicalBlock;
import com.smashingmods.chemlib.common.registry.BlockRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockStateGenerator extends BlockStateProvider {

    private final ExistingFileHelper existingFileHelper;

    public BlockStateGenerator(DataGenerator pGenerator, ExistingFileHelper pExistingFileHelper) {
        super(pGenerator, ChemLib.MODID, pExistingFileHelper);
        this.existingFileHelper = pExistingFileHelper;
    }

    @Override
    protected void registerStatesAndModels() {
        for (ChemicalBlockType type : ChemicalBlockType.values()) {
            BlockRegistry.getChemicalBlocksByType(type).forEach(this::registerChemicalBlock);
        }
    }

    public void registerChemicalBlock(ChemicalBlock pBlock) {
        String type = pBlock.getBlockType().getSerializedName();
        itemModels().withExistingParent(String.format("block/%s_%s_block", pBlock.getChemicalName(), type), modLoc(String.format("block/%s_block", type)))
                .texture("all", modLoc(String.format("block/%s_block", type)));
        ModelFile modelFile = new ModelFile.ExistingModelFile(modLoc(String.format("block/%s_block", type)), existingFileHelper);
        getVariantBuilder(pBlock).forAllStates(state -> ConfiguredModel.builder().modelFile(modelFile).build());
    }
}
