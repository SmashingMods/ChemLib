package com.smashingmods.chemlib.api;

import com.smashingmods.chemlib.registry.FluidRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public interface Chemical extends ItemLike {
    String getChemicalName();

    String getAbbreviation();

    MatterState getMatterState();

    String getChemicalDescription();

    List<MobEffectInstance> getEffects();
    int getColor();

    default Optional<FluidType> getFluidTypeReference() {
        Optional<FluidType> toReturn = FluidRegistry.getFluidTypeByName(getChemicalName());
        if (toReturn.isEmpty()) {
            toReturn = Optional.ofNullable(ForgeRegistries.FLUID_TYPES.get().getValue(ResourceLocation.tryParse(getChemicalName())));
        }
        if (toReturn.isEmpty()) {
            toReturn = Optional.of(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(ResourceLocation.tryParse(getChemicalName()))).getFluidType());
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
