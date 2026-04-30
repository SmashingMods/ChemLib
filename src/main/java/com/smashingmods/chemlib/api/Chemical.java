package com.smashingmods.chemlib.api;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.ItemLike;

import java.util.List;

public interface Chemical extends ItemLike {
    String getChemicalName();

    String getAbbreviation();

    MatterState getMatterState();

    String getChemicalDescription();

    List<MobEffectInstance> getEffects();
    int getColor();

    default int clampMinColorValue(int pColor, int minValue) {
        int green = ((pColor >> 16) & 0xFF) | minValue;
        int red = ((pColor >> 8) & 0xFF) | minValue;
        int blue = (pColor & 0xFF) | minValue;
        return  green << 16 | red << 8 | blue;
    }
}
