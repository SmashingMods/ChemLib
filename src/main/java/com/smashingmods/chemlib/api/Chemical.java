package com.smashingmods.chemlib.api;

import net.minecraft.world.level.ItemLike;

public interface Chemical extends ItemLike {

    String getChemicalName();

    String getAbbreviation();

    MatterState getMatterState();

    String getChemicalDescription();

    int getColor();
}
