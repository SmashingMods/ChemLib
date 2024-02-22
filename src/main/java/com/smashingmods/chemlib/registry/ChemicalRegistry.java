package com.smashingmods.chemlib.registry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.api.ChemicalBlockType;
import com.smashingmods.chemlib.api.ChemicalItemType;
import com.smashingmods.chemlib.api.MatterState;
import com.smashingmods.chemlib.api.MetalType;
import com.smashingmods.chemlib.common.blocks.ChemicalBlock;
import com.smashingmods.chemlib.common.blocks.LampBlock;
import com.smashingmods.chemlib.common.items.CompoundItem;
import com.smashingmods.chemlib.common.items.ElementItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.registries.RegistryObject;

import java.util.*;

import static net.minecraftforge.registries.ForgeRegistries.MOB_EFFECTS;

public class ChemicalRegistry {

    public static final JsonObject ELEMENTS_JSON = Registry.getStreamAsJsonObject("/data/chemlib/elements.json");
    public static final JsonObject COMPOUNDS_JSON = Registry.getStreamAsJsonObject("/data/chemlib/compounds.json");

    /*
        Elements are built from the Elements json and then everything is registered based on that information.
     */

    private static void registerElements() {
        for (JsonElement jsonElement : ELEMENTS_JSON.getAsJsonArray("elements")) {
            JsonObject object = jsonElement.getAsJsonObject();
            String elementName = object.get("name").getAsString();
            int atomicNumber = object.get("atomic_number").getAsInt();
            String abbreviation = object.get("abbreviation").getAsString();
            int group = object.get("group").getAsInt();
            int period = object.get("period").getAsInt();
            MatterState matterState = MatterState.valueOf(object.get("matter_state").getAsString().toUpperCase(Locale.ROOT));
            MetalType metalType = MetalType.valueOf(object.get("metal_type").getAsString().toUpperCase(Locale.ROOT));
            boolean artificial = object.has("artificial") && object.get("artificial").getAsBoolean();
            String color = object.get("color").getAsString();

            ItemRegistry.REGISTRY_ELEMENTS.register(elementName, () -> new ElementItem(elementName, atomicNumber, abbreviation, group, period, matterState, metalType, artificial, color, mobEffectsFactory(object)));
            RegistryObject<Item> registryObject = ItemRegistry.getRegistryObject(ItemRegistry.REGISTRY_ELEMENTS, elementName);

            if (!artificial) {
                switch (matterState) {
                    case SOLID -> {
                        boolean hasItem = object.has("has_item") && object.get("has_item").getAsBoolean();

                        if (metalType == MetalType.METAL) {
                            ItemRegistry.registerItemByType(registryObject, ChemicalItemType.PLATE, ItemRegistry.METALS_TAB);
                            if (!hasItem) {
                                ItemRegistry.registerItemByType(registryObject, ChemicalItemType.NUGGET, ItemRegistry.METALS_TAB);
                                ItemRegistry.registerItemByType(registryObject, ChemicalItemType.INGOT, ItemRegistry.METALS_TAB);
                                BlockRegistry.BLOCKS.register(String.format("%s_metal_block", elementName), () -> new ChemicalBlock(new ResourceLocation(ChemLib.MODID, elementName), ChemicalBlockType.METAL, BlockRegistry.METAL_BLOCKS, BlockRegistry.METAL_PROPERTIES));
                                BlockRegistry.getRegistryObjectByName(String.format("%s_metal_block", elementName)).ifPresent(block -> ItemRegistry.fromChemicalBlock(block, new Item.Properties().tab(ItemRegistry.METALS_TAB)));
                            }
                        }
                        ItemRegistry.registerItemByType(registryObject, ChemicalItemType.DUST, ItemRegistry.METALS_TAB);
                    }
                    case LIQUID, GAS -> {
                        boolean hasFluid = object.has("has_fluid") && object.get("has_fluid").getAsBoolean();
                        if (!hasFluid) {
                            JsonObject fluidAttributes = object.get("fluid_attributes").getAsJsonObject();
                            int density = fluidAttributes.get("density").getAsInt();
                            int luminosity = fluidAttributes.get("luminosity").getAsInt();
                            int viscosity = fluidAttributes.get("viscosity").getAsInt();
                            int slopeFindDistance = fluidAttributes.has("slope_find_distance") ? fluidAttributes.get("slope_find_distance").getAsInt() : 5;
                            int decreasePerBlock = fluidAttributes.has("decrease_per_block") ? fluidAttributes.get("decrease_per_block").getAsInt() : 2;

                            FluidAttributes.Builder attributes = FluidAttributes.builder(FluidRegistry.STILL, FluidRegistry.FLOWING)
                                    .density(density)
                                    .luminosity(luminosity)
                                    .viscosity(viscosity)
                                    .sound(SoundEvents.BUCKET_FILL)
                                    .overlay(FluidRegistry.OVERLAY)
                                    .color(Integer.parseInt(color, 16) | 0xFF000000);

                            switch (matterState) {
                                case LIQUID, GAS -> {
                                    if (group == 18) {
                                        BlockRegistry.BLOCKS.register(String.format("%s_lamp_block", elementName), () -> new LampBlock(new ResourceLocation(ChemLib.MODID, elementName), ChemicalBlockType.LAMP, BlockRegistry.LAMP_BLOCKS, BlockRegistry.LAMP_PROPERTIES));
                                        BlockRegistry.getRegistryObjectByName(String.format("%s_lamp_block", elementName)).ifPresent(block -> ItemRegistry.fromChemicalBlock(block, new Item.Properties().tab(ItemRegistry.MISC_TAB)));
                                    }
                                    attributes.gaseous();
                                    FluidRegistry.registerFluid(elementName, attributes, slopeFindDistance, decreasePerBlock);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /*
        Compounds are built from the Compounds json and then everything is registered based on that information.
     */

    private static void registerCompounds() {

        for (JsonElement jsonElement : COMPOUNDS_JSON.getAsJsonArray("compounds")) {
            JsonObject object = jsonElement.getAsJsonObject();
            String compoundName = object.get("name").getAsString();
            MatterState matterState = MatterState.valueOf(object.get("matter_state").getAsString().toUpperCase(Locale.ROOT));
            String description = object.has("description") ? object.get("description").getAsString() : "";
            String color = object.get("color").getAsString();

            JsonArray components = object.getAsJsonArray("components");
            HashMap<String, Integer> componentMap = new LinkedHashMap<>();
            for (JsonElement component : components) {
                JsonObject componentObject = component.getAsJsonObject();
                String componentName = componentObject.get("name").getAsString();
                int count = componentObject.has("count") ? componentObject.get("count").getAsInt() : 1;
                componentMap.put(componentName, count);
            }

            ItemRegistry.REGISTRY_COMPOUNDS.register(compoundName, () -> new CompoundItem(compoundName, matterState, componentMap, description, color, mobEffectsFactory(object)));

            switch (matterState) {
                case SOLID -> {
                    boolean hasItem = object.get("has_item").getAsBoolean();
                    if (!hasItem) {
                        ItemRegistry.registerItemByType(ItemRegistry.getRegistryObject(ItemRegistry.REGISTRY_COMPOUNDS, compoundName), ChemicalItemType.COMPOUND, ItemRegistry.COMPOUND_TAB);
                        if (compoundName.equals("polyvinyl_chloride")) {
                            ItemRegistry.registerItemByType(ItemRegistry.getRegistryObject(ItemRegistry.REGISTRY_COMPOUNDS, compoundName), ChemicalItemType.PLATE, ItemRegistry.COMPOUND_TAB);
                        }
                    }
                }
                case LIQUID, GAS -> {
                    boolean hasFluid = object.has("has_fluid") && object.get("has_fluid").getAsBoolean();
                    if (!hasFluid) {
                        JsonObject fluidAttributes = object.get("fluid_attributes").getAsJsonObject();
                        int density = fluidAttributes.get("density").getAsInt();
                        int luminosity = fluidAttributes.get("luminosity").getAsInt();
                        int viscosity = fluidAttributes.get("viscosity").getAsInt();
                        int slopeFindDistance = fluidAttributes.has("slope_find_distance") ? fluidAttributes.get("slope_find_distance").getAsInt() : 5;
                        int decreasePerBlock = fluidAttributes.has("decrease_per_block") ? fluidAttributes.get("decrease_per_block").getAsInt() : 2;

                        FluidAttributes.Builder attributes = FluidAttributes.builder(FluidRegistry.STILL, FluidRegistry.FLOWING)
                                .density(density)
                                .luminosity(luminosity)
                                .viscosity(viscosity)
                                .sound(SoundEvents.BUCKET_FILL)
                                .overlay(FluidRegistry.OVERLAY)
                                .color(Integer.parseInt(color, 16) | 0xFF000000);

                        switch (matterState) {
                            case LIQUID -> FluidRegistry.registerFluid(compoundName, attributes, slopeFindDistance, decreasePerBlock);
                            case GAS -> {
                                attributes.gaseous();
                                FluidRegistry.registerFluid(compoundName, attributes, slopeFindDistance, decreasePerBlock);
                            }
                        }
                    }
                }
            }
        }
    }

    private static List<MobEffectInstance> mobEffectsFactory(JsonObject object) {
        List<MobEffectInstance> effectsList = new ArrayList<>();
        JsonArray effects = object.getAsJsonArray("effect");
        if (effects != null) {
            for (JsonElement effect : effects) {
                JsonObject effectObject = effect.getAsJsonObject();
                String effectLocation = effectObject.get("location").getAsString();
                int effectDuration = effectObject.get("duration").getAsInt();
                int effectAmplifier = effectObject.get("amplifier").getAsInt();
                MobEffect mobEffect = MOB_EFFECTS.getValue(new ResourceLocation(effectLocation));
                if (mobEffect != null) {
                    effectsList.add(new MobEffectInstance(mobEffect, effectDuration, effectAmplifier));
                }
            }
        }
        return effectsList;
    }

    public static void register() {
        registerElements();
        registerCompounds();
    }
}
