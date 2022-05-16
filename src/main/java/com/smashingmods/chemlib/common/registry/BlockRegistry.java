package com.smashingmods.chemlib.common.registry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.api.ChemicalBlockType;
import com.smashingmods.chemlib.api.MatterState;
import com.smashingmods.chemlib.api.MetalType;
import com.smashingmods.chemlib.common.blocks.ChemicalBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.*;
import java.util.stream.Stream;

import static com.smashingmods.chemlib.common.registry.ItemRegistry.ITEMS_TAB;

public class BlockRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ChemLib.MODID);
    public static final List<ChemicalBlock> METAL_BLOCKS = new ArrayList<>();
    public static final List<ChemicalBlock> LAMP_BLOCKS = new ArrayList<>();

    private static final BlockBehaviour.Properties METAL_PROPERTIES = BlockBehaviour.Properties.of(Material.METAL).strength(5.0f, 12.0f).sound(SoundType.METAL);
    private static final BlockBehaviour.Properties LAMP_PROPERTIES = BlockBehaviour.Properties.of(Material.GLASS).strength(2.0f, 2.0f).sound(SoundType.GLASS).lightLevel(state -> 15);

    private static void registerBlocks() {

        for (JsonElement jsonElement : Registry.ELEMENTS_JSON.getAsJsonArray("elements")) {
            JsonObject object = jsonElement.getAsJsonObject();
            String elementName = object.get("name").getAsString();
            MatterState matterState = MatterState.valueOf(object.get("matter_state").getAsString().toUpperCase());
            MetalType metalType = MetalType.valueOf(object.get("metal_type").getAsString().toUpperCase());

            switch (matterState) {
                case SOLID -> {
                    if (metalType.equals(MetalType.METAL)) {
                        List<String> vanillaBlocks = Arrays.asList("copper", "iron", "gold");
                        if (!vanillaBlocks.contains(elementName)) {
                            String registryName = String.format("%s_metal_block", elementName);
                            BLOCKS.register(registryName, () -> new ChemicalBlock(new ResourceLocation(ChemLib.MODID, elementName), ChemicalBlockType.METAL, METAL_BLOCKS, METAL_PROPERTIES));
                            ItemRegistry.fromBlock(getRegistryObjectByName(registryName).get(), new Item.Properties().tab(ITEMS_TAB));
                        }
                    }
                }
                case GAS -> {
                    List<String> nobleGasses = Arrays.asList("helium", "neon", "argon", "krypton", "xenon", "oganesson");
                    if (nobleGasses.contains(elementName)) {
                        String registryName = String.format("%s_lamp_block", elementName);
                        BLOCKS.register(registryName, () -> new ChemicalBlock(new ResourceLocation(ChemLib.MODID, elementName), ChemicalBlockType.LAMP, LAMP_BLOCKS, LAMP_PROPERTIES));
                        ItemRegistry.fromBlock(getRegistryObjectByName(registryName).get(), new Item.Properties().tab(ITEMS_TAB));
                    }
                }
            }
        }
    }

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
        registerBlocks();
        BLOCKS.register(eventBus);
    }
}
