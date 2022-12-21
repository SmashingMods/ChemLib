package com.smashingmods.chemlib.api;

import com.smashingmods.chemlib.registry.FluidRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Optional;

public interface Chemical extends ItemLike {

    String getChemicalName();

    String getAbbreviation();

    MatterState getMatterState();

    String getChemicalDescription();

    List<MobEffectInstance> getEffects();

    int getColor();

    default Optional<Fluid> getFluidReference() {
        Optional<Fluid> toReturn = FluidRegistry.getFluidByName(getChemicalName());
        if (toReturn.isEmpty()) {
            toReturn = Optional.ofNullable(ForgeRegistries.FLUIDS.getValue(ResourceLocation.tryParse(getChemicalName())));
        }
        if (toReturn.isEmpty()) {
            //noinspection deprecation
            toReturn = Optional.of(Registry.FLUID.get(ResourceLocation.tryParse(getChemicalName())));
        }
        return toReturn;
    }

    default int clampMinColorValue(int pColor, int minValue) {
        int green = ((pColor >> 16) & 0xFF) | minValue;
        int red = ((pColor >> 8) & 0xFF) | minValue;
        int blue = (pColor & 0xFF) | minValue;
        return  green << 16 | red << 8 | blue;
    }
}
