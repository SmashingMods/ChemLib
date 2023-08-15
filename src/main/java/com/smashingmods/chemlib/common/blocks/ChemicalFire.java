package com.smashingmods.chemlib.common.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FireBlock;

import java.util.List;

public class ChemicalFire extends FireBlock {
    public ChemicalFire(List<Block> pList, Properties pProperties) {
        super(pProperties);
        pList.add(this);
    }
}
