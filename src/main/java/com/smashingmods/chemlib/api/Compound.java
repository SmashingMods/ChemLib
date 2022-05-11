package com.smashingmods.chemlib.api;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface Compound extends Chemical {

    List<ItemStack> getComponents();
}
