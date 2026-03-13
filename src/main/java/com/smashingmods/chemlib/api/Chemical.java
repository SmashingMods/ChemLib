package com.smashingmods.chemlib.api;

import com.smashingmods.chemlib.registry.FluidRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.List;
import java.util.Optional;

public interface Chemical extends ItemLike {
    String getChemicalName();

    String getAbbreviation();

    MatterState getMatterState();

    String getChemicalDescription();

    List<MobEffectInstance> getEffects();
    int getColor();

    default Optional<FluidType> getFluidTypeReference() {
        // NeoForge fluids are already registered via FluidRegistry; resolve via our helper.
        return FluidRegistry.getFluidTypeByName(getChemicalName());
    }

    default int clampMinColorValue(int pColor, int minValue) {
        int green = ((pColor >> 16) & 0xFF) | minValue;
        int red = ((pColor >> 8) & 0xFF) | minValue;
        int blue = (pColor & 0xFF) | minValue;
        return  green << 16 | red << 8 | blue;
    }
}
