package com.smashingmods.chemlib.common.registry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.api.Chemical;
import com.smashingmods.chemlib.api.MatterState;
import com.smashingmods.chemlib.api.MetalType;
import com.smashingmods.chemlib.common.items.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class ItemRegistry {

    public static final CreativeModeTab ELEMENT_TAB = new CreativeModeTab(ChemLib.MODID) {
        @Override
        @Nonnull
        public ItemStack makeIcon() {
            return getElementByName("hydrogen").map(ItemStack::new).orElseGet(() -> new ItemStack(Items.AIR));
        }
    };

    public static final CreativeModeTab COMPOUND_TAB = new CreativeModeTab(ChemLib.MODID) {
        @Override
        @Nonnull
        public ItemStack makeIcon() {
            return getCompoundByName("water").map(ItemStack::new).orElseGet(() -> new ItemStack(Items.AIR));
        }
    };

    public static final CreativeModeTab ITEMS_TAB = new CreativeModeTab(ChemLib.MODID) {
        @Override
        @Nonnull
        public ItemStack makeIcon() {
            return getIngotByName("titanium").map(ItemStack::new).orElseGet(() -> new ItemStack(Items.IRON_INGOT));
        }
    };

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ChemLib.MODID);

    /*
        These lists can be used to refer to each type of item more easily.
     */
    public static final List<ElementItem> ELEMENTS = new ArrayList<>();
    public static final List<CompoundItem> COMPOUNDS = new ArrayList<>();
    public static final List<ChemicalItem> DUSTS = new ArrayList<>();
    public static final List<ChemicalItem> NUGGETS = new ArrayList<>();
    public static final List<ChemicalItem> PLATES = new ArrayList<>();
    public static final List<StorageBlock> STORAGE_BLOCKS = new ArrayList<>();
    public static final List<ChemicalItem> INGOTS = new ArrayList<>();
    public static final List<BlockItem> BLOCK_ITEMS = new ArrayList<>();

    private static void registerElements() {
        InputStream stream = ChemLib.class.getResourceAsStream("/data/chemlib/elements.json");
        BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(stream)));

        JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

        for (JsonElement jsonElement : jsonObject.getAsJsonArray("elements")) {
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

            if (matterState == MatterState.SOLID && !hasItem) {
                if (metalType == MetalType.NONMETAL || metalType == MetalType.METALLOID) {
                    registerDust(filterAndReturn(ITEMS, elementName));
                } else {
                    registerNugget(filterAndReturn(ITEMS, elementName));
                    registerIngot(filterAndReturn(ITEMS, elementName));
                    registerPlate(filterAndReturn(ITEMS, elementName));
                }
            } else if (matterState == MatterState.LIQUID) {

            } else if (matterState == MatterState.GAS) {

            }
        }
    }

    private static void registerCompounds() {
        InputStream stream = ChemLib.class.getResourceAsStream("/data/chemlib/compounds.json");
        BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(stream)));

        JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

        for (JsonElement jsonElement : jsonObject.getAsJsonArray("compounds")) {
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

            if (matterState == MatterState.SOLID && !hasItem) {
                registerDust(filterAndReturn(ITEMS, compoundName));
            } else if (matterState == MatterState.LIQUID) {

            } else if (matterState == MatterState.GAS) {

            }
        }
    }

    public static void registerLampItems() {
        BlockRegistry.BLOCKS.getEntries().forEach(ItemRegistry::fromBlock);
    }

    public static Optional<ElementItem> getElementByName(String pName) {
        return ELEMENTS.stream().filter(elementItem -> elementItem.getName().equals(pName)).findFirst();
    }

    public static Optional<ElementItem> getElementByAtomicNumber(int pAtomicNumber) {
        return ELEMENTS.stream().filter(elementItem -> elementItem.getAtomicNumber() == pAtomicNumber).findFirst();
    }

    public static Optional<CompoundItem> getCompoundByName(String pName) {
        return COMPOUNDS.stream().filter(compoundItem -> compoundItem.getName().equals(pName.toLowerCase())).findFirst();
    }

    public static Optional<ChemicalItem> getDustByName(String pName) {
        return DUSTS.stream().filter(chemicalNameMatches(pName)).findFirst();
    }

    public static Optional<ChemicalItem> getNuggetByName(String pName) {
        return NUGGETS.stream().filter(chemicalNameMatches(pName)).findFirst();
    }

    public static Optional<ChemicalItem> getIngotByName(String pName) {
        return INGOTS.stream().filter(chemicalNameMatches(pName)).findFirst();
    }

    public static Optional<ChemicalItem> getPlateByName(String pName) {
        return PLATES.stream().filter(chemicalNameMatches(pName)).findFirst();
    }

    public static <I extends Item> void registerDust(RegistryObject<?> item) {
        ITEMS.register(String.format("%s_dust", item.getId().getPath()), () -> new ChemicalItem((Chemical) item.get(), new Item.Properties().tab(ITEMS_TAB), DUSTS));
    }

    public static <I extends Item> void registerNugget(RegistryObject<?> item) {
        ITEMS.register(String.format("%s_nugget", item.getId().getPath()), () -> new ChemicalItem((Chemical) item.get(), new Item.Properties().tab(ITEMS_TAB), NUGGETS));
    }

    public static <I extends Item> void registerIngot(RegistryObject<?> item) {
        ITEMS.register(String.format("%s_ingot", item.getId().getPath()), () -> new ChemicalItem((Chemical) item.get(), new Item.Properties().tab(ITEMS_TAB), INGOTS));
    }

    public static <I extends Item> void registerPlate(RegistryObject<?> item) {
        ITEMS.register(String.format("%s_plate", item.getId().getPath()), () -> new ChemicalItem((Chemical) item.get(), new Item.Properties().tab(ITEMS_TAB), PLATES));
    }

    public static <B extends Block> void fromBlock(RegistryObject<B> block) {
        ITEMS.register(block.getId().getPath(), () -> {
            BlockItem temp = new BlockItem(block.get(), new Item.Properties().tab(ITEMS_TAB));
            BLOCK_ITEMS.add(temp);
            return temp;
        });
    }

    private static RegistryObject<?> filterAndReturn(DeferredRegister<?> pRegister, String pName) {
        return pRegister.getEntries().stream().filter(elementNameMatches(pName)).findFirst().get();
    }

    private static Predicate<RegistryObject<?>> elementNameMatches(String pName) {
        return item -> item.getId().getPath().equals(pName);
    }

    private static Predicate<? super ChemicalItem> chemicalNameMatches(String pName) {
        return chemical -> Objects.requireNonNull(chemical.getRegistryName()).getPath().equals(pName);
    }

    public static void register(IEventBus eventBus) {
        registerElements();
        registerCompounds();
        registerLampItems();
        ITEMS.register(eventBus);
    }
}
