package com.smashingmods.chemlib.registry;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.api.ChemicalItemType;
import com.smashingmods.chemlib.api.MatterState;
import com.smashingmods.chemlib.api.MetalType;
import com.smashingmods.chemlib.common.blocks.ChemicalBlock;
import com.smashingmods.chemlib.common.items.ChemicalBlockItem;
import com.smashingmods.chemlib.common.items.ChemicalItem;
import com.smashingmods.chemlib.common.items.CompoundItem;
import com.smashingmods.chemlib.common.items.ElementItem;
import com.smashingmods.chemlib.common.items.PeriodicTableItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class ItemRegistry {

    public static final DeferredRegister<Item> REGISTRY_ELEMENTS = DeferredRegister.create(BuiltInRegistries.ITEM, ChemLib.MODID);
    public static final DeferredRegister<Item> REGISTRY_COMPOUNDS = DeferredRegister.create(BuiltInRegistries.ITEM, ChemLib.MODID);
    public static final DeferredRegister<Item> REGISTRY_COMPOUND_DUSTS = DeferredRegister.create(BuiltInRegistries.ITEM, ChemLib.MODID);
    public static final DeferredRegister<Item> REGISTRY_METAL_DUSTS = DeferredRegister.create(BuiltInRegistries.ITEM, ChemLib.MODID);
    public static final DeferredRegister<Item> REGISTRY_NUGGETS = DeferredRegister.create(BuiltInRegistries.ITEM, ChemLib.MODID);
    public static final DeferredRegister<Item> REGISTRY_INGOTS = DeferredRegister.create(BuiltInRegistries.ITEM, ChemLib.MODID);
    public static final DeferredRegister<Item> REGISTRY_PLATES = DeferredRegister.create(BuiltInRegistries.ITEM, ChemLib.MODID);
    public static final DeferredRegister<Item> REGISTRY_BLOCK_ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, ChemLib.MODID);
    public static final DeferredRegister<Item> REGISTRY_MISC_ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, ChemLib.MODID);

    public static Stream<DeferredHolder<Item, ? extends Item>> getRegistryItems() {
        return REGISTRY_ELEMENTS.getEntries().stream();
    }

    public static List<ElementItem> getElements() {
        return REGISTRY_ELEMENTS.getEntries().stream()
                .map(DeferredHolder::get)
                .map(item -> (ElementItem) item)
                .collect(Collectors.toList());
    }

    public static List<CompoundItem> getCompounds() {
        return REGISTRY_COMPOUNDS.getEntries().stream()
                .map(DeferredHolder::get)
                .map(item -> (CompoundItem) item)
                .collect(Collectors.toList());
    }

    public static List<CompoundItem> getAllCompounds() {
        return new LinkedList<>(REGISTRY_COMPOUNDS.getEntries().stream()
                .map(DeferredHolder::get)
                .map(item -> (CompoundItem) item)
                .toList());
    }

    public static Stream<ChemicalItem> getChemicalItems() {
        List<ChemicalItem> items = new ArrayList<>();
        for (ChemicalItemType type : ChemicalItemType.values()) {
            items.addAll(getChemicalItemsByTypeAsStream(type).toList());
        }
        return items.stream();
    }

    public static List<ChemicalBlockItem> getChemicalBlockItems() {
        return REGISTRY_BLOCK_ITEMS.getEntries().stream()
                .map(DeferredHolder::get)
                .filter(item -> item instanceof ChemicalBlockItem)
                .map(item -> (ChemicalBlockItem) item)
                .collect(Collectors.toList());
    }

    public static List<BlockItem> getLiquidBlockItems() {
        return REGISTRY_BLOCK_ITEMS.getEntries().stream()
                .map(DeferredHolder::get)
                .filter(item -> item instanceof BlockItem)
                .map(item -> (BlockItem) item)
                .filter(blockItem -> blockItem.getBlock() instanceof LiquidBlock)
                .collect(Collectors.toList());
    }

    public static DeferredRegister<Item> getChemicalItemRegistryByType(ChemicalItemType pChemicalItemType) {
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
        return getChemicalItemRegistryByType(pChemicalItemType).getEntries().stream()
                .map(DeferredHolder::get)
                .map(item -> (ChemicalItem) item);
    }

    public static Optional<ChemicalItem> getChemicalItemByNameAndType(String pName, ChemicalItemType pChemicalItemType) {
        return getChemicalItemsByTypeAsStream(pChemicalItemType)
                .filter(item -> item.getItemType().equals(pChemicalItemType))
                .filter(item -> item.getChemical().getChemicalName().equals(pName))
                .findFirst();
    }

    public static Optional<Item> getChemicalBlockItemByName(String pName) {
        return REGISTRY_BLOCK_ITEMS.getEntries().stream()
                .map(DeferredHolder::get)
                .map(item -> (Item) item)
                .filter(item -> Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item)).getPath().equals(pName))
                .findFirst();
    }

    public static void registerItemByType(DeferredHolder<Item, ? extends Item> pHolder, ChemicalItemType pChemicalItemType) {
        String registryName = String.format("%s_%s", pHolder.getId().getPath(), pChemicalItemType.getSerializedName());
        Supplier<ChemicalItem> supplier = () -> new ChemicalItem(pHolder.getId(), pChemicalItemType, new Item.Properties());

        switch (pChemicalItemType) {
            case COMPOUND -> REGISTRY_COMPOUND_DUSTS.register(registryName, supplier);
            case DUST -> REGISTRY_METAL_DUSTS.register(registryName, supplier);
            case NUGGET -> REGISTRY_NUGGETS.register(registryName, supplier);
            case INGOT -> REGISTRY_INGOTS.register(registryName, supplier);
            case PLATE -> REGISTRY_PLATES.register(registryName, supplier);
        }
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static DeferredHolder<Item, ? extends Item> getRegistryObject(DeferredRegister<Item> pRegister, String pName) {
        return pRegister.getEntries().stream()
                .filter(holder -> holder.getId().getPath().equals(pName))
                .findFirst()
                .get();
    }

    public static <B extends Block> void fromChemicalBlock(DeferredHolder<Block, ? extends B> pBlock, Item.Properties pProperties) {
        REGISTRY_BLOCK_ITEMS.register(pBlock.getId().getPath(), () -> new ChemicalBlockItem((ChemicalBlock) pBlock.get(), pProperties));
    }

    public static <B extends Block> void fromBlock(DeferredHolder<Block, ? extends B> pBlock, Item.Properties pProperties) {
        REGISTRY_BLOCK_ITEMS.register(pBlock.getId().getPath(), () -> new BlockItem(pBlock.get(), pProperties));
    }

    public static void register(IEventBus eventBus) {
        REGISTRY_MISC_ITEMS.register("periodic_table", PeriodicTableItem::new);

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
