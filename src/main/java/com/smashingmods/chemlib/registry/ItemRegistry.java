package com.smashingmods.chemlib.registry;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.api.ChemicalItemType;
import com.smashingmods.chemlib.api.MatterState;
import com.smashingmods.chemlib.api.MetalType;
import com.smashingmods.chemlib.common.blocks.ChemicalBlock;
import com.smashingmods.chemlib.common.items.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class ItemRegistry {

    /*
        Each item type has a separate registry to make understanding and organizing them simpler.
     */

    public static final DeferredRegister.Items REGISTRY_ELEMENTS = DeferredRegister.createItems(ChemLib.MODID);
    public static final DeferredRegister.Items REGISTRY_COMPOUNDS = DeferredRegister.createItems(ChemLib.MODID);
    public static final DeferredRegister.Items REGISTRY_COMPOUND_DUSTS = DeferredRegister.createItems(ChemLib.MODID);
    public static final DeferredRegister.Items REGISTRY_METAL_DUSTS = DeferredRegister.createItems(ChemLib.MODID);
    public static final DeferredRegister.Items REGISTRY_NUGGETS = DeferredRegister.createItems(ChemLib.MODID);
    public static final DeferredRegister.Items REGISTRY_INGOTS = DeferredRegister.createItems(ChemLib.MODID);
    public static final DeferredRegister.Items REGISTRY_PLATES = DeferredRegister.createItems(ChemLib.MODID);
    public static final DeferredRegister.Items REGISTRY_BLOCK_ITEMS = DeferredRegister.createItems(ChemLib.MODID);
    public static final DeferredRegister.Items REGISTRY_MISC_ITEMS = DeferredRegister.createItems(ChemLib.MODID);

    /*
        This section defines helper methods for getting specific objects out of the registry.
     */

    public static Stream<DeferredHolder<Item, ? extends Item>> getRegistryItems() {
        return ItemRegistry.REGISTRY_ELEMENTS.getEntries().stream();
    }

    public static List<ElementItem> getElements() {
        return REGISTRY_ELEMENTS.getEntries().stream().map(DeferredHolder::get).map(item -> (ElementItem) item).collect(Collectors.toList());
    }

    public static List<CompoundItem> getCompounds() {
        return REGISTRY_COMPOUNDS.getEntries().stream().map(DeferredHolder::get).map(item -> (CompoundItem) item).collect(Collectors.toList());
    }

    public static List<CompoundItem> getAllCompounds() {
        return new LinkedList<>(REGISTRY_COMPOUNDS.getEntries().stream().map(DeferredHolder::get).map(item -> (CompoundItem) item).toList());
    }

    public static Stream<ChemicalItem> getChemicalItems() {
        List<ChemicalItem> items = new ArrayList<>();
        for (ChemicalItemType type : ChemicalItemType.values()) {
            items.addAll(getChemicalItemsByTypeAsStream(type).toList());
        }
        return items.stream();
    }

    public static List<ChemicalBlockItem> getChemicalBlockItems() {
        return REGISTRY_BLOCK_ITEMS.getEntries().stream().map(DeferredHolder::get).filter(item -> item instanceof ChemicalBlockItem).map(item -> (ChemicalBlockItem) item).collect(Collectors.toList());
    }

    public static List<BlockItem> getLiquidBlockItems() {
        return REGISTRY_BLOCK_ITEMS.getEntries().stream().map(DeferredHolder::get).filter(item -> item instanceof BlockItem).map(item -> (BlockItem) item).filter(blockItem -> blockItem.getBlock() instanceof LiquidBlock).collect(Collectors.toList());
    }

    public static DeferredRegister.Items getChemicalItemRegistryByType(ChemicalItemType pChemicalItemType) {
        return switch (pChemicalItemType) {
            case COMPOUND -> REGISTRY_COMPOUND_DUSTS;
            case DUST -> REGISTRY_METAL_DUSTS;
            case NUGGET -> REGISTRY_NUGGETS;
            case INGOT -> REGISTRY_INGOTS;
            case PLATE -> REGISTRY_PLATES;
        };
    }

    public static Stream<ElementItem> getElementsByMatterState(MatterState pMatterState) {
        return getElements().stream().filter(element -> element.getMatterState().equals(pMatterState));
    }

    public static Stream<ElementItem> getElementsByMetalType(MetalType pMetalType) {
        return getElements().stream().filter(element -> element.getMetalType().equals(pMetalType));
    }

    public static Optional<ElementItem> getElementByName(String pName) {
        return getElements().stream().filter(element -> element.getChemicalName().equals(pName)).findFirst();
    }

    public static Optional<ElementItem> getElementByAtomicNumber(int pAtomicNumber) {
        return getElements().stream().filter(element -> element.getAtomicNumber() == pAtomicNumber).findFirst();
    }

    public static Optional<CompoundItem> getCompoundByName(String pName) {
        return getAllCompounds().stream().filter(compound -> compound.getChemicalName().equals(pName)).findFirst();
    }

    public static List<ChemicalItem> getChemicalItemsByType(ChemicalItemType pChemicalItemType) {
        return getChemicalItemsByTypeAsStream(pChemicalItemType).collect(Collectors.toList());
    }

    public static Stream<ChemicalItem> getChemicalItemsByTypeAsStream(ChemicalItemType pChemicalItemType) {
        return getChemicalItemRegistryByType(pChemicalItemType).getEntries().stream().map(DeferredHolder::get).map(item -> (ChemicalItem) item);
    }

    public static Optional<ChemicalItem> getChemicalItemByNameAndType(String pName, ChemicalItemType pChemicalItemType) {
        return getChemicalItemsByTypeAsStream(pChemicalItemType)
                .filter(item -> item.getItemType().equals(pChemicalItemType))
                .filter(item -> item.getChemical().getChemicalName().equals(pName))
                .findFirst();
    }

    public static Optional<Item> getChemicalBlockItemByName(String pName) {
        return REGISTRY_BLOCK_ITEMS.getEntries().stream().<Item>map(DeferredHolder::get).filter(item -> Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item)).getPath().equals(pName)).findFirst();
    }

    /*
        Helper methods for registering items.
     */

    public static void registerItemByType(DeferredHolder<Item, ? extends Item> pRegistryObject, ChemicalItemType pChemicalItemType) {

        String registryName = String.format("%s_%s", pRegistryObject.getId().getPath(), pChemicalItemType.getSerializedName());
        Function<Item.Properties, ChemicalItem> factory = properties -> new ChemicalItem(pRegistryObject.getId(), pChemicalItemType, properties);

        switch (pChemicalItemType) {
            case COMPOUND -> REGISTRY_COMPOUND_DUSTS.registerItem(registryName, factory, new Item.Properties());
            case DUST -> REGISTRY_METAL_DUSTS.registerItem(registryName, factory, new Item.Properties());
            case NUGGET -> REGISTRY_NUGGETS.registerItem(registryName, factory, new Item.Properties());
            case INGOT -> REGISTRY_INGOTS.registerItem(registryName, factory, new Item.Properties());
            case PLATE -> REGISTRY_PLATES.registerItem(registryName, factory, new Item.Properties());
        }
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static DeferredHolder<Item, ? extends Item> getRegistryObject(DeferredRegister<Item> pRegister, String pName) {
        return pRegister.getEntries().stream().filter(item -> item.getId().getPath().equals(pName)).findFirst().get();
    }

    /*
        Since 1.21.2 an item's description id is baked from its Item.Properties at construction, so a
        BlockItem defaults to "item.<namespace>.<path>" unless the properties opt into the "block." prefix.
        The generated lang defines "block.chemlib.*" for these, hence useBlockDescriptionPrefix() here.
     */

    public static void fromChemicalBlock(DeferredHolder<Block, ? extends Block> pBlock, Item.Properties pProperties) {
        REGISTRY_BLOCK_ITEMS.registerItem(pBlock.getId().getPath(), properties -> new ChemicalBlockItem((ChemicalBlock) pBlock.get(), properties), pProperties.useBlockDescriptionPrefix());
    }

    public static void fromBlock(DeferredHolder<Block, ? extends Block> pBlock, Item.Properties pProperties) {
        REGISTRY_BLOCK_ITEMS.registerItem(pBlock.getId().getPath(), properties -> new BlockItem(pBlock.get(), properties), pProperties.useBlockDescriptionPrefix());
    }

    public static void register(IEventBus eventBus) {
        REGISTRY_MISC_ITEMS.registerItem("periodic_table", PeriodicTableItem::new, new Item.Properties().stacksTo(1));

        REGISTRY_ELEMENTS.register(eventBus);
        REGISTRY_COMPOUNDS.register(eventBus);
        REGISTRY_COMPOUND_DUSTS.register(eventBus);
        REGISTRY_METAL_DUSTS.register(eventBus);
        REGISTRY_NUGGETS.register(eventBus);
        REGISTRY_INGOTS.register(eventBus);
        REGISTRY_PLATES.register(eventBus);
        REGISTRY_BLOCK_ITEMS.register(eventBus);
        REGISTRY_MISC_ITEMS.register(eventBus);
    }
}
