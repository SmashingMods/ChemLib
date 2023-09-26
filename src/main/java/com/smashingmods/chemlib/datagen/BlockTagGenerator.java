package com.smashingmods.chemlib.datagen;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.api.MatterState;
import com.smashingmods.chemlib.registry.BlockRegistry;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeRegistryTagsProvider;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.Objects;

public class BlockTagGenerator extends ForgeRegistryTagsProvider<Block> {

    public BlockTagGenerator(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, ForgeRegistries.BLOCKS, ChemLib.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        BlockRegistry.BLOCKS.getEntries().forEach(blockRegistryObject -> {
            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(blockRegistryObject.get());
            tag(BlockTags.NEEDS_STONE_TOOL).add(blockRegistryObject.get());
        });

        ItemRegistry.getChemicalBlockItems().forEach(item -> {
            if (item.getMatterState().equals(MatterState.SOLID)) {
                String name = item.getChemicalName();
                TagKey<Block> key = Objects.requireNonNull(ForgeRegistries.BLOCKS.tags()).createTagKey(new ResourceLocation("forge", String.format("storage_blocks/%s", name)));
                tag(key).add(item.getBlock());
            }
        });
    }

    @Override
    @Nonnull
    public String getName() {
        return "";
    }
}
