package com.smashingmods.chemlib.common.items;

import com.smashingmods.chemlib.api.Chemical;
import com.smashingmods.chemlib.api.MatterState;
import com.smashingmods.chemlib.common.blocks.ChemicalBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

public class ChemicalBlockItem extends BlockItem implements Chemical {

    private final ChemicalBlock block;

    public ChemicalBlockItem(ChemicalBlock pBlock, Properties pProperties) {
        super(pBlock, pProperties);
        this.block = pBlock;
    }

    public Chemical getChemical() {
        return block.getChemical();
    }

    @Override
    public String getChemicalName() {
        return block.getChemicalName();
    }

    @Override
    public String getAbbreviation() {
        return getChemical().getAbbreviation();
    }

    @Override
    public MatterState getMatterState() {
        return getChemical().getMatterState();
    }

    @Override
    public String getChemicalDescription() {
        return getChemical().getChemicalDescription();
    }

    @Override
    public int getColor() {
        return getChemical().getColor();
    }

    public int getColor(ItemStack pItemStack, int pTintIndex) {
        return getColor();
    }
}
