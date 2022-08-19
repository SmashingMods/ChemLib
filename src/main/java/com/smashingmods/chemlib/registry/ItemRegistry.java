package com.smashingmods.chemlib.registry;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.api.*;
import com.smashingmods.chemlib.common.blocks.ChemicalBlock;
import com.smashingmods.chemlib.common.items.*;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class ItemRegistry {

    /*
        Each item type has a separate registry to make understanding and organizing them simpler.
     */

    public static final DeferredRegister<Item> REGISTRY_ELEMENTS = DeferredRegister.create(ForgeRegistries.ITEMS, ChemLib.MODID);
    public static final DeferredRegister<Item> REGISTRY_COMPOUNDS = DeferredRegister.create(ForgeRegistries.ITEMS, ChemLib.MODID);
    public static final DeferredRegister<Item> REGISTRY_COMPOUND_DUSTS = DeferredRegister.create(ForgeRegistries.ITEMS, ChemLib.MODID);
    public static final DeferredRegister<Item> REGISTRY_METAL_DUSTS = DeferredRegister.create(ForgeRegistries.ITEMS, ChemLib.MODID);
    public static final DeferredRegister<Item> REGISTRY_NUGGETS = DeferredRegister.create(ForgeRegistries.ITEMS, ChemLib.MODID);
    public static final DeferredRegister<Item> REGISTRY_INGOTS = DeferredRegister.create(ForgeRegistries.ITEMS, ChemLib.MODID);
    public static final DeferredRegister<Item> REGISTRY_PLATES = DeferredRegister.create(ForgeRegistries.ITEMS, ChemLib.MODID);
    public static final DeferredRegister<Item> REGISTRY_BLOCK_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ChemLib.MODID);
    public static final DeferredRegister<Item> REGISTRY_MISC_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ChemLib.MODID);

    /*
        Registry objects are sorted into creative mode tabs depending on their type.
     */

    public static final CreativeModeTab ELEMENT_TAB = new CreativeModeTab(String.format("%s.elements", ChemLib.MODID)) {
        @Override
        @Nonnull
        public ItemStack makeIcon() {
            return getElementByName("hydrogen")
                    .map(ItemStack::new)
                    .orElseGet(() -> new ItemStack(Items.AIR));
        }
    };

    public static final CreativeModeTab COMPOUND_TAB = new CreativeModeTab(String.format("%s.compounds", ChemLib.MODID)) {
        @Override
        @Nonnull
        public ItemStack makeIcon() {
            return getCompoundByName("cellulose")
                    .map(ItemStack::new)
                    .orElseGet(() -> new ItemStack(Items.AIR));
        }

        @Override
        public void fillItemList(@Nonnull NonNullList<ItemStack> pItems) {
            super.fillItemList(pItems);
            pItems.clear();
            List<ItemStack> compounds = getCompounds().stream().map(ItemStack::new).collect(Collectors.toList());
            List<ItemStack> compoundDusts = getChemicalItemsByTypeAsStream(ChemicalItemType.COMPOUND).map(ItemStack::new).collect(Collectors.toList());
            pItems.addAll(compounds);
            pItems.addAll(compoundDusts);
        }
    };

    public static final CreativeModeTab METALS_TAB = new CreativeModeTab(String.format("%s.metals", ChemLib.MODID)) {
        @Override
        @Nonnull
        public ItemStack makeIcon() {
            return getChemicalItemByNameAndType("barium", ChemicalItemType.INGOT)
                    .map(ItemStack::new)
                    .orElseGet(() -> new ItemStack(Items.AIR));
        }

        @Override
        public void fillItemList(@Nonnull NonNullList<ItemStack> pItems) {
            super.fillItemList(pItems);
            pItems.clear();

            List<ItemStack> dustStacks = getChemicalItemsByType(ChemicalItemType.DUST).stream().map(ItemStack::new).collect(Collectors.toList());
            List<ItemStack> nuggetStacks = getChemicalItemsByType(ChemicalItemType.NUGGET).stream().map(ItemStack::new).collect(Collectors.toList());
            List<ItemStack> ingotStacks = getChemicalItemsByType(ChemicalItemType.INGOT).stream().map(ItemStack::new).collect(Collectors.toList());
            List<ItemStack> plateStacks = getChemicalItemsByType(ChemicalItemType.PLATE).stream().map(ItemStack::new).collect(Collectors.toList());

            List<ItemStack> blockItemStacks = getChemicalBlockItems().stream().filter(item -> ((ChemicalBlock) item.getBlock()).getBlockType().getSerializedName().equals("metal")).map(ItemStack::new).collect(Collectors.toList());

            pItems.addAll(ingotStacks);
            pItems.addAll(blockItemStacks);
            pItems.addAll(nuggetStacks);
            pItems.addAll(dustStacks);
            pItems.addAll(plateStacks);
        }
    };

    public static final CreativeModeTab MISC_TAB = new CreativeModeTab(String.format("%s.misc", ChemLib.MODID)) {
        @Override
        @Nonnull
        public ItemStack makeIcon() {
            return getChemicalBlockItemByName("radon_lamp_block").map(ItemStack::new).orElseGet(() -> new ItemStack(Items.AIR));
        }
    };

    /*
        This section defines helper methods for getting specific objects out of the registry.
     */

    public static Stream<RegistryObject<Item>> getRegistryItems() {
        return ItemRegistry.REGISTRY_ELEMENTS.getEntries().stream();
    }

    public static List<ElementItem> getElements() {
        return REGISTRY_ELEMENTS.getEntries().stream().map(RegistryObject::get).map(item -> (ElementItem) item).collect(Collectors.toList());
    }

    public static List<CompoundItem> getCompounds() {
        return REGISTRY_COMPOUNDS.getEntries().stream().map(RegistryObject::get).map(item -> (CompoundItem) item).collect(Collectors.toList());
    }

    public static Stream<ChemicalItem> getChemicalItems() {
        List<ChemicalItem> items = new ArrayList<>();
        for (ChemicalItemType type : ChemicalItemType.values()) {
            items.addAll(getChemicalItemsByTypeAsStream(type).collect(Collectors.toList()));
        }
        return items.stream();
    }

    public static List<ChemicalBlockItem> getChemicalBlockItems() {
        return REGISTRY_BLOCK_ITEMS.getEntries().stream().map(RegistryObject::get).filter(item -> item instanceof ChemicalBlockItem).map(item -> (ChemicalBlockItem) item).collect(Collectors.toList());
    }

    public static List<BlockItem> getLiquidBlockItems() {
        return REGISTRY_BLOCK_ITEMS.getEntries().stream().map(RegistryObject::get).filter(item -> item instanceof BlockItem).map(item -> (BlockItem) item).filter(blockItem -> blockItem.getBlock() instanceof LiquidBlock).collect(Collectors.toList());
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
        return getCompounds().stream().filter(compound -> compound.getChemicalName().equals(pName)).findFirst();
    }

    public static List<ChemicalItem> getChemicalItemsByType(ChemicalItemType pChemicalItemType) {
        return getChemicalItemsByTypeAsStream(pChemicalItemType).collect(Collectors.toList());
    }

    public static Stream<ChemicalItem> getChemicalItemsByTypeAsStream(ChemicalItemType pChemicalItemType) {
        return getChemicalItemRegistryByType(pChemicalItemType).getEntries().stream().map(RegistryObject::get).map(item -> (ChemicalItem) item);
    }

    public static Optional<ChemicalItem> getChemicalItemByNameAndType(String pName, ChemicalItemType pChemicalItemType) {
        return getChemicalItemsByTypeAsStream(pChemicalItemType)
                .filter(item -> item.getItemType().equals(pChemicalItemType))
                .filter(item -> item.getChemical().getChemicalName().equals(pName))
                .findFirst();
    }

    public static Optional<Item> getChemicalBlockItemByName(String pName) {
        return REGISTRY_BLOCK_ITEMS.getEntries().stream().map(RegistryObject::get).filter(item -> Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)).getPath().equals(pName)).findFirst();
    }

    /*
        Helper methods for registering items.
     */

    public static void registerItemByType(RegistryObject<Item> pRegistryObject, ChemicalItemType pChemicalItemType, CreativeModeTab pTab) {

        String registryName = String.format("%s_%s", pRegistryObject.getId().getPath(), pChemicalItemType.getSerializedName());
        Supplier<ChemicalItem> supplier = () -> new ChemicalItem(pRegistryObject.getId(), pChemicalItemType, new Item.Properties().tab(pTab));

        switch(pChemicalItemType) {
            case COMPOUND -> REGISTRY_COMPOUND_DUSTS.register(registryName, supplier);
            case DUST -> REGISTRY_METAL_DUSTS.register(registryName, supplier);
            case NUGGET -> REGISTRY_NUGGETS.register(registryName, supplier);
            case INGOT -> REGISTRY_INGOTS.register(registryName, supplier);
            case PLATE -> REGISTRY_PLATES.register(registryName, supplier);
        }
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static RegistryObject<Item> getRegistryObject(DeferredRegister<Item> pRegister, String pName) {
        return pRegister.getEntries().stream().filter(item -> item.getId().getPath().equals(pName)).findFirst().get();
    }

    public static <B extends Block> void fromChemicalBlock(RegistryObject<B> pBlock, Item.Properties pProperties) {
        REGISTRY_BLOCK_ITEMS.register(pBlock.getId().getPath(), () -> new ChemicalBlockItem((ChemicalBlock) pBlock.get(), pProperties));
    }

    public static <B extends Block> void fromBlock(RegistryObject<B> pBlock, Item.Properties pProperties) {
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
