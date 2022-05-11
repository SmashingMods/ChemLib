package com.smashingmods.chemlib.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

public class BlockLamp extends Block {
    public BlockLamp() {
        super(Block.Properties.of(Material.GLASS).strength(2.0f, 2.0f).sound(SoundType.GLASS).lightLevel(state -> 15));
    }
}
