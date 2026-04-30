package com.smashingmods.chemlib.datagen;

import com.smashingmods.chemlib.ChemLib;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.PaintingVariantTags;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static com.smashingmods.chemlib.registry.PaintingsRegistry.*;

public class PaintingVariantTagsGenerator extends TagsProvider<PaintingVariant> {

    protected PaintingVariantTagsGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper exFileHelper) {
        super(output, Registries.PAINTING_VARIANT, lookupProvider, ChemLib.MODID, exFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
//        this.tag(PaintingVariantTags.PLACEABLE)
//                .add(PERIODIC_TABLE);
    }
}
