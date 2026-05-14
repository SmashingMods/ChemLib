package com.smashingmods.chemlib.datagen;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.registry.FluidRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.tags.FluidTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class FluidTagGenerator extends FluidTagsProvider {

    public FluidTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper exFileHelper) {
        super(output, lookupProvider, ChemLib.MODID, exFileHelper);
    }

    @Override
    public void addTags(HolderLookup.Provider provider) {
        FluidRegistry.getFluidsAsStream().forEach(fluid -> tag(FluidTags.WATER).add(fluid));
    }

    @Override
    public String getName() {
        return "";
    }
}
