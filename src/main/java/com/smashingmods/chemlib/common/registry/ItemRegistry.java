package com.smashingmods.chemlib.common.registry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.api.*;
import com.smashingmods.chemlib.common.blocks.ChemicalBlock;
import com.smashingmods.chemlib.common.items.*;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
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
            return getChemicalItemByNameAndType("technetium", ChemicalItemType.INGOT)
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

    public static final DeferredRegister<Item> REGISTRY_ELEMENTS = DeferredRegister.create(ForgeRegistries.ITEMS, ChemLib.MODID);
    public static final DeferredRegister<Item> REGISTRY_COMPOUNDS = DeferredRegister.create(ForgeRegistries.ITEMS, ChemLib.MODID);
    public static final DeferredRegister<Item> REGISTRY_COMPOUND_DUSTS = DeferredRegister.create(ForgeRegistries.ITEMS, ChemLib.MODID);
    public static final DeferredRegister<Item> REGISTRY_METAL_DUSTS = DeferredRegister.create(ForgeRegistries.ITEMS, ChemLib.MODID);
    public static final DeferredRegister<Item> REGISTRY_NUGGETS = DeferredRegister.create(ForgeRegistries.ITEMS, ChemLib.MODID);
    public static final DeferredRegister<Item> REGISTRY_INGOTS = DeferredRegister.create(ForgeRegistries.ITEMS, ChemLib.MODID);
    public static final DeferredRegister<Item> REGISTRY_PLATES = DeferredRegister.create(ForgeRegistries.ITEMS, ChemLib.MODID);
    public static final DeferredRegister<Item> REGISTRY_BLOCK_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ChemLib.MODID);

    private static void registerElements() {

        for (JsonElement jsonElement : Registry.ELEMENTS_JSON.getAsJsonArray("elements")) {
            JsonObject object = jsonElement.getAsJsonObject();
            String elementName = object.get("name").getAsString();
            int atomicNumber = object.get("atomic_number").getAsInt();
            String abbreviation = object.get("abbreviation").getAsString();
            int group = object.get("group").getAsInt();
            int period = object.get("period").getAsInt();
            MatterState matterState = MatterState.valueOf(object.get("matter_state").getAsString().toUpperCase());
            MetalType metalType = MetalType.valueOf(object.get("metal_type").getAsString().toUpperCase());
            boolean artificial = object.has("artificial") && object.get("artificial").getAsBoolean();
            boolean hasItem = object.has("has_item") && object.get("has_item").getAsBoolean();
            String description = object.get("description").getAsString();
            String color = object.get("color").getAsString();

            REGISTRY_ELEMENTS.register(elementName, () -> new ElementItem(elementName, atomicNumber, abbreviation, group, period, matterState, metalType, description, color));
            RegistryObject<Item> registryObject = getRegistryObject(REGISTRY_ELEMENTS, elementName);

            switch (matterState) {
                case SOLID -> {
                    if (!hasItem) {
                        if (metalType == MetalType.NONMETAL || metalType == MetalType.METALLOID) {
                            registerItemByType(registryObject, ChemicalItemType.DUST, METALS_TAB);
                        } else {
                            registerItemByType(registryObject, ChemicalItemType.NUGGET, METALS_TAB);
                            registerItemByType(registryObject, ChemicalItemType.INGOT, METALS_TAB);
                            registerItemByType(registryObject, ChemicalItemType.PLATE, METALS_TAB);
                        }
                    }
                }
                case LIQUID, GAS -> {}
            }
        }
    }

    private static void registerCompounds() {

        for (JsonElement jsonElement : Registry.COMPOUNDS_JSON.getAsJsonArray("compounds")) {
            JsonObject object = jsonElement.getAsJsonObject();
            String compoundName = object.get("name").getAsString();
            MatterState matterState = MatterState.valueOf(object.get("matter_state").getAsString().toUpperCase());
            boolean hasItem = object.get("has_item").getAsBoolean();
            // TODO: add description
            String color = object.get("color").getAsString();

            JsonArray components = object.getAsJsonArray("components");
            HashMap<String, Integer> componentMap = new LinkedHashMap<>();
            for (JsonElement component : components) {
                JsonObject componentObject = component.getAsJsonObject();
                String componentName = componentObject.get("name").getAsString();
                int count = componentObject.has("count") ? componentObject.get("count").getAsInt() : 1;
                componentMap.put(componentName, count);
            }

            REGISTRY_COMPOUNDS.register(compoundName, () -> new CompoundItem(compoundName, matterState, componentMap, /* TODO: fill in chemical descriptions */"", color));

            switch (matterState) {
                case SOLID -> {
                    if (!hasItem) {
                        registerItemByType(getRegistryObject(REGISTRY_COMPOUNDS, compoundName), ChemicalItemType.COMPOUND, COMPOUND_TAB);
                    }
                }
                case LIQUID, GAS -> {}
            }
        }
    }

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
        return REGISTRY_BLOCK_ITEMS.getEntries().stream().map(RegistryObject::get).map(item -> (ChemicalBlockItem) item).collect(Collectors.toList());
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

    public static Optional<ChemicalBlockItem> getChemicalBlockItemByName(String pName) {
        return REGISTRY_BLOCK_ITEMS.getEntries().stream().map(RegistryObject::get).map(item -> (ChemicalBlockItem) item).filter(item -> item.getChemicalName().equals(pName)).findFirst();
    }

    public static void registerItemByType(RegistryObject<Item> pRegistryObject, ChemicalItemType pChemicalItemType, CreativeModeTab pTab) {

        String registryName = String.format("%s_%s", pRegistryObject.getId().getPath(), pChemicalItemType.getSerializedName());
        Supplier supplier = () -> new ChemicalItem(pRegistryObject.getId(), pChemicalItemType, new Item.Properties().tab(pTab));

        switch(pChemicalItemType) {
            case COMPOUND -> REGISTRY_COMPOUND_DUSTS.register(registryName, supplier);
            case DUST -> REGISTRY_METAL_DUSTS.register(registryName, supplier);
            case NUGGET -> REGISTRY_NUGGETS.register(registryName, supplier);
            case INGOT -> REGISTRY_INGOTS.register(registryName, supplier);
            case PLATE -> REGISTRY_PLATES.register(registryName, supplier);
        }
    }

    private static RegistryObject<Item> getRegistryObject(DeferredRegister<Item> pRegister, String pName) {
        return pRegister.getEntries().stream().filter(item -> item.getId().getPath().equals(pName)).findFirst().get();
    }

    public static <B extends Block> void fromBlock(RegistryObject<B> block, Item.Properties pProperties) {
        REGISTRY_BLOCK_ITEMS.register(block.getId().getPath(), () -> new ChemicalBlockItem((ChemicalBlock) block.get(), pProperties));
    }

    public static void register(IEventBus eventBus) {
        registerElements();
        registerCompounds();
        REGISTRY_ELEMENTS.register(eventBus);
        REGISTRY_COMPOUNDS.register(eventBus);
        REGISTRY_COMPOUND_DUSTS.register(eventBus);
        REGISTRY_METAL_DUSTS.register(eventBus);
        REGISTRY_NUGGETS.register(eventBus);
        REGISTRY_INGOTS.register(eventBus);
        REGISTRY_PLATES.register(eventBus);
        REGISTRY_BLOCK_ITEMS.register(eventBus);
    }
}
