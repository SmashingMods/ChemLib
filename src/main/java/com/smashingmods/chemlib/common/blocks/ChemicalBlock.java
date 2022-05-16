package com.smashingmods.chemlib.common.blocks;

import com.smashingmods.chemlib.api.Chemical;
import com.smashingmods.chemlib.api.ChemicalBlockType;
import com.smashingmods.chemlib.api.MatterState;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class ChemicalBlock extends Block implements Chemical {

    private final ResourceLocation chemical;
    private final ChemicalBlockType blockType;

    public ChemicalBlock(ResourceLocation pChemical, ChemicalBlockType pBlockType, List<ChemicalBlock> pList, BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.chemical = pChemical;
        this.blockType = pBlockType;
        pList.add(this);
    }

    public Chemical getChemical() {
        return (Chemical) ForgeRegistries.ITEMS.getValue(chemical);
    }

    public ChemicalBlockType getBlockType() {
        return blockType;
    }

    @Override
    public String getChemicalName() {
        return getChemical().getChemicalName();
    }

    @Override
    public String getAbbreviation() {
        return getChemical().getAbbreviation();
    }

    @Override
    public MatterState getMatterState() {
        return MatterState.SOLID;
    }

    @Override
    public String getChemicalDescription() {
        return "";
    }

    @Override
    public int getColor() {
        return getChemical().getColor();
    }

    public BlockColor getBlockColor(ItemStack pItemStack, int pTintIndex) {
        return (pState, pLevel, pPos, pTintIndex1) -> getChemical().getColor();
    }
}
