package com.smashingmods.chemlib.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

public class BlockLamp extends Block {
    public BlockLamp(String name) {
        super(Block.Properties
                .of(Material.DIRT)
                .strength(2.0f)
                //.hardnessAndResistance(2.0f)
                .sound(SoundType.GLASS)
                .lightLevel((x) -> 15)); //light level
        setRegistryName("lamp_" + name);
        ModBlocks.blocks.add(this);
    }
}
