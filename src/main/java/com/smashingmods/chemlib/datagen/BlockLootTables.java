package com.smashingmods.chemlib.datagen;

import com.smashingmods.chemlib.registry.BlockRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;

import javax.annotation.Nonnull;
import java.util.Set;

public class BlockLootTables extends BlockLootSubProvider {

    public BlockLootTables(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        BlockRegistry.BLOCKS.getEntries().stream().forEach(block -> dropSelf(block.get()));
    }

    @Override
    @Nonnull
    protected Iterable<Block> getKnownBlocks() {
        return BlockRegistry.BLOCKS.getEntries().stream().<Block>map(DeferredHolder::get)::iterator;
    }
}
