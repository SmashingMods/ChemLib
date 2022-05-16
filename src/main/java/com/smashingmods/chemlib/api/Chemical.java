package com.smashingmods.chemlib.api;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public interface Chemical extends ItemLike {

    String getChemicalName();

    String getAbbreviation();

    MatterState getMatterState();

    String getChemicalDescription();

    int getColor();
}
