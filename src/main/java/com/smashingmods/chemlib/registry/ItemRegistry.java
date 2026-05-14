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
import net.neoforged.neoforge.registries.*;

import java.util.*;
import java.util.function.Supplier;
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

    public static List<ElementItem> getElements() {
        return REGISTRY_ELEMENTS.getEntries().stream().map(DeferredHolder::get).map(item -> (ElementItem) item).collect(Collectors.toList());
    }

    public static List<CompoundItem> getCompounds() {
        return REGISTRY_COMPOUNDS.getEntries().stream().map(DeferredHolder::get).map(item -> (CompoundItem) item).collect(Collectors.toList());
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

    public static DeferredRegister<Item> getChemicalItemRegistryByType(ChemicalItemType chemicalItemType) {
        return switch (chemicalItemType) {
            case COMPOUND -> REGISTRY_COMPOUND_DUSTS;
            case DUST -> REGISTRY_METAL_DUSTS;
            case NUGGET -> REGISTRY_NUGGETS;
            case INGOT -> REGISTRY_INGOTS;
            case PLATE -> REGISTRY_PLATES;
        };
    }

    public static Stream<ElementItem> getElementsByMatterState(MatterState matterState) {
        return getElements().stream().filter(element -> element.getMatterState().equals(matterState));
    }

    public static Stream<ElementItem> getElementsByMetalType(MetalType metalType) {
        return getElements().stream().filter(element -> element.getMetalType().equals(metalType));
    }

    public static Optional<ElementItem> getElementByName(String name) {
        return getElements().stream().filter(element -> element.getChemicalName().equals(name)).findFirst();
    }

    public static Optional<ElementItem> getElementByAtomicNumber(int atomicNumber) {
        return getElements().stream().filter(element -> element.getAtomicNumber() == atomicNumber).findFirst();
    }

    public static Optional<CompoundItem> getCompoundByName(String name) {
        return getCompounds().stream().filter(compound -> compound.getChemicalName().equals(name)).findFirst();
    }

    public static List<ChemicalItem> getChemicalItemsByType(ChemicalItemType chemicalItemType) {
        return getChemicalItemsByTypeAsStream(chemicalItemType).collect(Collectors.toList());
    }

    public static Stream<ChemicalItem> getChemicalItemsByTypeAsStream(ChemicalItemType chemicalItemType) {
        return getChemicalItemRegistryByType(chemicalItemType).getEntries().stream().map(DeferredHolder::get).map(item -> (ChemicalItem) item);
    }

    public static Optional<ChemicalItem> getChemicalItemByNameAndType(String name, ChemicalItemType chemicalItemType) {
        return getChemicalItemsByTypeAsStream(chemicalItemType)
                .filter(item -> item.getItemType().equals(chemicalItemType))
                .filter(item -> item.getChemical().getChemicalName().equals(name))
                .findFirst();
    }

    public static Optional<? extends Item> getChemicalBlockItemByName(String name) {
        return REGISTRY_BLOCK_ITEMS.getEntries().stream().map(DeferredHolder::get).filter(item -> Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item)).getPath().equals(name)).findFirst();
    }

    /*
        Helper methods for registering items.
     */

    public static void registerItemByType(DeferredHolder<Item, ? extends Item> deferredHolder, ChemicalItemType chemicalItemType) {

        String registryName = String.format("%s_%s", deferredHolder.getId().getPath(), chemicalItemType.getSerializedName());
        Supplier<ChemicalItem> supplier = () -> new ChemicalItem(deferredHolder.getId(), chemicalItemType, new Item.Properties());

        switch (chemicalItemType) {
            case COMPOUND -> REGISTRY_COMPOUND_DUSTS.register(registryName, supplier);
            case DUST -> REGISTRY_METAL_DUSTS.register(registryName, supplier);
            case NUGGET -> REGISTRY_NUGGETS.register(registryName, supplier);
            case INGOT -> REGISTRY_INGOTS.register(registryName, supplier);
            case PLATE -> REGISTRY_PLATES.register(registryName, supplier);
        }
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static DeferredHolder<Item, ? extends Item> getRegistryObject(DeferredRegister.Items register, String name) {
        return register.getEntries().stream().filter(item -> item.getId().getPath().equals(name)).findFirst().get();
    }

    public static <B extends Block> void fromChemicalBlock(DeferredHolder<Block, B> deferredHolder, Item.Properties properties) {
        REGISTRY_BLOCK_ITEMS.register(deferredHolder.getId().getPath(), () -> new ChemicalBlockItem((ChemicalBlock) deferredHolder.get(), properties));
    }

    public static <B extends Block> void fromBlock(DeferredHolder<Block, B> deferredHolder, Item.Properties properties) {
        REGISTRY_BLOCK_ITEMS.register(deferredHolder.getId().getPath(), () -> new BlockItem(deferredHolder.get(), properties));
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
