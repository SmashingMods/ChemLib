package com.smashingmods.chemlib.common.registry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.api.*;
import com.smashingmods.chemlib.common.blocks.ChemicalBlock;
import com.smashingmods.chemlib.common.items.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Predicate;
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
            return getCompoundByName("water")
                    .map(ItemStack::new)
                    .orElseGet(() -> new ItemStack(Items.AIR));
        }
    };

    public static final CreativeModeTab ITEMS_TAB = new CreativeModeTab(String.format("%s.items", ChemLib.MODID)) {
        @Override
        @Nonnull
        public ItemStack makeIcon() {
            return getItemByNameAndType("titanium", ChemicalItemType.INGOT)
                    .map(ItemStack::new)
                    .orElseGet(() -> new ItemStack(Items.IRON_INGOT));
        }
    };

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ChemLib.MODID);

    public static final List<ElementItem> ELEMENTS = new ArrayList<>();
    public static final List<CompoundItem> COMPOUNDS = new ArrayList<>();
    public static final List<ChemicalItem> CHEMICAL_ITEMS = new ArrayList<>();
    public static final List<ChemicalBlockItem> BLOCK_ITEMS = new ArrayList<>();

    private static void registerElements() {

        for (JsonElement jsonElement : Registry.ELEMENTS_JSON.getAsJsonArray("elements")) {
            JsonObject object = jsonElement.getAsJsonObject();
            String elementName = object.get("name").getAsString();
            int atomicNumber = object.get("atomic_number").getAsInt();
            String abbreviation = object.get("abbreviation").getAsString();
            MatterState matterState = MatterState.valueOf(object.get("matter_state").getAsString().toUpperCase());
            MetalType metalType = MetalType.valueOf(object.get("metal_type").getAsString().toUpperCase());
            boolean hasItem = object.get("has_item").getAsBoolean();
            String description = object.get("description").getAsString();
            String color = object.get("color").getAsString();

            ITEMS.register(elementName, () -> new ElementItem(elementName, atomicNumber, abbreviation, matterState, metalType, description, color));

            switch (matterState) {
                case SOLID -> {
                    if (!hasItem) {
                        if (metalType == MetalType.NONMETAL || metalType == MetalType.METALLOID) {
                            registerItemByType(filterAndReturn(elementName), ChemicalItemType.DUST);
                        } else {
                            registerItemByType(filterAndReturn(elementName), ChemicalItemType.NUGGET);
                            registerItemByType(filterAndReturn(elementName), ChemicalItemType.INGOT);
                            registerItemByType(filterAndReturn(elementName), ChemicalItemType.PLATE);
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

            ITEMS.register(compoundName, () -> new CompoundItem(compoundName, matterState, componentMap, /* TODO: fill in chemical descriptions */"", color));

            switch (matterState) {
                case SOLID -> {
                    if (!hasItem) {
                        registerItemByType(filterAndReturn(compoundName), ChemicalItemType.DUST);
                    }
                }
                case LIQUID, GAS -> {}
            }
        }
    }

    public static Stream<RegistryObject<Item>> getRegistryItems() {
        return ItemRegistry.ITEMS.getEntries().stream();
    }

    public static List<ElementItem> getElements() {
        return ELEMENTS;
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

    public static List<CompoundItem> getCompounds() {
        return COMPOUNDS;
    }

    public static Optional<CompoundItem> getCompoundByName(String pName) {
        return getCompounds().stream().filter(compound -> compound.getChemicalName().equals(pName)).findFirst();
    }

    public static List<ChemicalItem> getChemicalItems() {
        return CHEMICAL_ITEMS;
    }

    public static Stream<ChemicalItem> getChemicalItemsByType(ChemicalItemType pChemicalItemType) {
        return getChemicalItems().stream().filter(item -> item.getItemType().equals(pChemicalItemType));
    }

    public static Optional<ChemicalItem> getItemByNameAndType(String pName, ChemicalItemType pChemicalItemType) {
        return getChemicalItems().stream()
                .filter(item -> item.getItemType().equals(pChemicalItemType))
                .filter(item -> item.getChemical().getChemicalName().equals(pName))
                .findFirst();
    }

    public static void registerItemByType(RegistryObject<?> pRegistryObject, ChemicalItemType pChemicalItemType) {
        ITEMS.register(String.format("%s_%s", pRegistryObject.getId().getPath(), pChemicalItemType.getSerializedName()), () -> new ChemicalItem(pRegistryObject.getId(), pChemicalItemType, new Item.Properties().tab(ITEMS_TAB)));
    }

    private static RegistryObject<?> filterAndReturn(String pName) {
        return ITEMS.getEntries().stream().filter(elementNameMatches(pName)).findFirst().get();
    }

    private static Predicate<RegistryObject<?>> elementNameMatches(String pName) {
        return item -> item.getId().getPath().equals(pName);
    }

    public static <B extends Block> void fromBlock(RegistryObject<B> block, Item.Properties pProperties) {
        ITEMS.register(block.getId().getPath(), () -> {
            ChemicalBlockItem temp = new ChemicalBlockItem((ChemicalBlock) block.get(), pProperties);
            BLOCK_ITEMS.add(temp);
            return temp;
        });
    }

    public static void register(IEventBus eventBus) {
        registerElements();
        registerCompounds();
        ITEMS.register(eventBus);
    }
}
