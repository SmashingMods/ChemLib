package com.smashingmods.chemlib.api;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public interface Chemical extends ItemLike {

    String getName();

    String getAbbreviation();

    MatterState getMatterState();

    int getColor();

    int getColor(ItemStack pItemStack, int pTintIndex);
}
