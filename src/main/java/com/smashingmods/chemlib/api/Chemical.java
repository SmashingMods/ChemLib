package com.smashingmods.chemlib.api;

import com.smashingmods.chemlib.registry.FluidRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

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
        Optional<FluidType> toReturn = FluidRegistry.getFluidTypeByName(getChemicalName());
        if (toReturn.isEmpty()) {
            ResourceLocation rl = ResourceLocation.tryParse(getChemicalName());
            if (rl != null) {
                toReturn = NeoForgeRegistries.FLUID_TYPES.getOptional(rl);
            }
        }
        if (toReturn.isEmpty()) {
            ResourceLocation rl = ResourceLocation.tryParse(getChemicalName());
            if (rl != null) {
                toReturn = BuiltInRegistries.FLUID.getOptional(rl).map(fluid -> fluid.getFluidType());
            }
        }
        return toReturn;
    }

    default int clampMinColorValue(int pColor, int minValue) {
        int green = ((pColor >> 16) & 0xFF) | minValue;
        int red = ((pColor >> 8) & 0xFF) | minValue;
        int blue = (pColor & 0xFF) | minValue;
        return 0xFF000000 | green << 16 | red << 8 | blue;
    }
}
