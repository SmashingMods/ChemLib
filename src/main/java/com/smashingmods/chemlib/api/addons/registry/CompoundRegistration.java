package com.smashingmods.chemlib.api.addons.registry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.smashingmods.chemlib.api.ChemicalItemType;
import com.smashingmods.chemlib.api.MatterState;
import com.smashingmods.chemlib.common.blocks.ChemicalLiquidBlock;
import com.smashingmods.chemlib.common.items.ChemicalItem;
import com.smashingmods.chemlib.common.items.CompoundItem;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.function.Consumer;

import static com.smashingmods.chemlib.registry.ChemicalRegistry.fluidTypePropertiesFactory;
import static com.smashingmods.chemlib.registry.ChemicalRegistry.mobEffectsFactory;

public class CompoundRegistration {

    static void registerCompounds(AddonRegistry pRegisters, JsonObject pCompoundsJson) {
        for (JsonElement jsonElement : pCompoundsJson.getAsJsonArray("compounds")) {
            JsonObject object = jsonElement.getAsJsonObject();
            String compoundName = object.get("name").getAsString();
            MatterState matterState = MatterState.valueOf(object.get("matter_state").getAsString().toUpperCase());
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

            if (ModTracker.compoundNotExist(compoundName)) {
                RegistryObject<Item> registryObject = pRegisters.COMPOUNDS.register(compoundName,
                    () -> new CompoundItem(compoundName,
                        matterState,
                        componentMap,
                        description,
                        color,
                        mobEffectsFactory(object),
                        pRegisters.getCompoundsTab()));

                switch (matterState) {
                    case SOLID -> {
                        boolean hasItem = object.get("has_item").getAsBoolean();
                        if (!hasItem) {
                            String registryName = String.format("%s_%s", registryObject.getId().getPath(), ChemicalItemType.COMPOUND.getSerializedName());
                            pRegisters.COMPOUND_DUSTS.register(registryName,
                                () -> new ChemicalItem(registryObject.getId(),
                                    ChemicalItemType.COMPOUND,
                                    new Item.Properties().tab(pRegisters.getCompoundsTab())));
                        }
                    }
                    case LIQUID, GAS -> {
                        boolean hasFluid = object.has("has_fluid") && object.get("has_fluid").getAsBoolean();
                        if (!hasFluid) {
                            JsonObject properties = object.get("fluid_properties").getAsJsonObject();
                            int slopeFindDistance = properties.has("slope_find_distance") ? properties.get("slope_find_distance").getAsInt() : 4;
                            int decreasePerBlock = properties.has("decrease_per_block") ? properties.get("decrease_per_block").getAsInt() : 1;

                            switch (matterState) {
                                case LIQUID, GAS -> registerFluid(pRegisters, compoundName, fluidTypePropertiesFactory(properties, pRegisters.getModID(), compoundName), (int) Long.parseLong(color, 16), slopeFindDistance, decreasePerBlock);
                            }
                        }
                    }
                }
                ModTracker.addCompound(new ResourceLocation(pRegisters.getModID(), compoundName));
            }
        }
    }

    static void registerFluid(AddonRegistry pRegisters, String pName, FluidType.Properties pFluidProperties, int pColor, int pSlopeFindDistance, int pDecreasePerBlock) {
        var ref = new Object() {
            ForgeFlowingFluid.Properties properties = null;
        };

        RegistryObject<FluidType> fluidType = pRegisters.FLUID_TYPES.register(pName, () -> new FluidType(pFluidProperties) {
            @Override
            public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                consumer.accept(new IClientFluidTypeExtensions() {
                    @Override
                    public ResourceLocation getStillTexture() {
                        return new ResourceLocation("block/water_still");
                    }

                    @Override
                    public ResourceLocation getFlowingTexture() {
                        return new ResourceLocation("block/water_flow");
                    }

                    @Override
                    public ResourceLocation getOverlayTexture() {
                        return new ResourceLocation("block/water_overlay");
                    }

                    @Override
                    public ResourceLocation getRenderOverlayTexture(Minecraft mc) {
                        return new ResourceLocation("minecraft", "textures/misc/underwater.png");
                    }

                    @Override
                    public int getTintColor() {
                        return pColor;
                    }

                    @Override
                    public int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
                        return pColor;
                    }
                });
            }
        });

        RegistryObject<FlowingFluid> fluidSource = pRegisters.FLUIDS.register(String.format("%s_fluid", pName), () -> new ForgeFlowingFluid.Source(ref.properties));
        RegistryObject<FlowingFluid> fluidFlowing = pRegisters.FLUIDS.register(String.format("%s_flowing", pName), () -> new ForgeFlowingFluid.Flowing(ref.properties));
        RegistryObject<LiquidBlock> liquidBlock = pRegisters.LIQUID_BLOCKS.register(pName, () -> new ChemicalLiquidBlock(fluidSource, pName));
        RegistryObject<Item> bucket = pRegisters.BUCKETS.register(String.format("%s_bucket", pName)
                , () -> new BucketItem(fluidSource, new Item.Properties().tab(pRegisters.getBucketsTab()).stacksTo(1)));

        ref.properties = new ForgeFlowingFluid.Properties(fluidType, fluidSource, fluidFlowing)
                .slopeFindDistance(pSlopeFindDistance)
                .levelDecreasePerBlock(pDecreasePerBlock)
                .block(liquidBlock)
                .bucket(bucket);
    }
}