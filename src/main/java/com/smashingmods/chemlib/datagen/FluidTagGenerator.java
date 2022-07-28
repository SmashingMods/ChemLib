package com.smashingmods.chemlib.datagen;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.registry.FluidRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeRegistryTagsProvider;
import net.minecraftforge.registries.ForgeRegistries;

public class FluidTagGenerator extends ForgeRegistryTagsProvider<Fluid> {

    public FluidTagGenerator(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, ForgeRegistries.FLUIDS, ChemLib.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        FluidRegistry.getFluids().forEach(fluid -> tag(FluidTags.WATER).add(fluid));
    }

    @Override
    public String getName() {
        return null;
    }
}
