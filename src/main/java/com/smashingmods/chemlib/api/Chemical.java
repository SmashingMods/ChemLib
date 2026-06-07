package com.smashingmods.chemlib.api;

import com.smashingmods.chemlib.registry.FluidRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

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
            toReturn = Optional.ofNullable(NeoForgeRegistries.FLUID_TYPES.get(ResourceLocation.tryParse(getChemicalName())));
        }
        if (toReturn.isEmpty()) {
            toReturn = Optional.of(Objects.requireNonNull(BuiltInRegistries.FLUID.get(ResourceLocation.tryParse(getChemicalName()))).getFluidType());
        }
        return toReturn;
    }

    default int clampMinColorValue(int pColor, int minValue) {
        int green = ((pColor >> 16) & 0xFF) | minValue;
        int red = ((pColor >> 8) & 0xFF) | minValue;
        int blue = (pColor & 0xFF) | minValue;
        return  green << 16 | red << 8 | blue;
    }

    /**
     * Parses a hex colour string from the chemical JSON into a packed ARGB int with full opacity.
     * The high byte is force-set to {@code 0xFF} via {@code | 0xFF000000}, so the alpha is always
     * fully opaque regardless of the parsed value (and overwrites any high byte already present,
     * e.g. promethium's 7-digit {@code "62af0a7"} -> {@code 0xFF2af0a7}). Shared by the
     * {@link com.smashingmods.chemlib.common.items.ElementItem}/{@link com.smashingmods.chemlib.common.items.CompoundItem}
     * constructors and the fluid-colour argument in {@code ChemicalRegistry}.
     */
    static int parseColorHex(String pColor) {
        return Integer.parseInt(pColor, 16) | 0xFF000000;
    }
}
