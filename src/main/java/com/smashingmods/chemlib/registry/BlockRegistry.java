package com.smashingmods.chemlib.registry;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.api.ChemicalBlockType;
import com.smashingmods.chemlib.common.blocks.ChemicalBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class BlockRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ChemLib.MODID);
    public static final List<ChemicalBlock> METAL_BLOCKS = new ArrayList<>();
    public static final List<ChemicalBlock> LAMP_BLOCKS = new ArrayList<>();

    public static final BlockBehaviour.Properties METAL_PROPERTIES = BlockBehaviour.Properties.of(Material.METAL).strength(5.0f, 12.0f).sound(SoundType.METAL);
    public static final BlockBehaviour.Properties LAMP_PROPERTIES = BlockBehaviour.Properties.of(Material.GLASS).strength(2.0f, 2.0f).sound(SoundType.GLASS).lightLevel(state -> state.getValue(BlockStateProperties.LIT) ? 15 : 0);

    public static Optional<RegistryObject<Block>> getRegistryObjectByName(String pName) {
        return BLOCKS.getEntries().stream().filter(blockRegistryObject -> blockRegistryObject.getId().getPath().equals(pName)).findFirst();
    }

    public static List<ChemicalBlock> getAllChemicalBlocks() {
        List<ChemicalBlock> all = new ArrayList<>();
        all.addAll(METAL_BLOCKS);
        all.addAll(LAMP_BLOCKS);
        return all;
    }

    public static List<ChemicalBlock> getChemicalBlocksByType(ChemicalBlockType pChemicalBlockType) {
        return switch (pChemicalBlockType) {
            case METAL -> METAL_BLOCKS;
            case LAMP -> LAMP_BLOCKS;
        };
    }

    public static Stream<ChemicalBlock> getChemicalBlocksStreamByType(ChemicalBlockType pChemicalBlockType) {
        return getChemicalBlocksByType(pChemicalBlockType)
                .stream().filter(block -> block.getBlockType().equals(pChemicalBlockType));
    }

    public static Optional<ChemicalBlock> getChemicalBlockByNameAndType(String pName, ChemicalBlockType pChemicalBlockType) {
        return getChemicalBlocksStreamByType(pChemicalBlockType)
                .filter(block -> block.getChemical().getChemicalName().equals(pName))
                .findFirst();
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
