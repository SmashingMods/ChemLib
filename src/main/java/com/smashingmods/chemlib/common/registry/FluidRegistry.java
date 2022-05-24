package com.smashingmods.chemlib.common.registry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.api.MatterState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Arrays;
import java.util.stream.Stream;

public class FluidRegistry {

    public static final ResourceLocation STILL = new ResourceLocation("block/water_still");
    public static final ResourceLocation FLOWING = new ResourceLocation("block/water_flow");
    public static final ResourceLocation OVERLAY = new ResourceLocation("block/water_overlay");

    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, ChemLib.MODID);
    public static final DeferredRegister<Block> LIQUID_BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ChemLib.MODID);
    public static final DeferredRegister<Item> BUCKETS = DeferredRegister.create(ForgeRegistries.ITEMS, ChemLib.MODID);

    private static void registerFluids() {

        for (JsonElement jsonElement : Registry.ELEMENTS_JSON.getAsJsonArray("elements")) {
            JsonObject object = jsonElement.getAsJsonObject();
            MatterState matterState = MatterState.valueOf(object.get("matter_state").getAsString().toUpperCase());

            if (Arrays.asList(MatterState.LIQUID, MatterState.GAS).contains(matterState)) {
                String elementName = object.get("name").getAsString();
                JsonObject fluidAttributes = object.get("fluid_attributes").getAsJsonObject();
                int density = fluidAttributes.get("density").getAsInt();
                int luminosity = fluidAttributes.get("luminosity").getAsInt();
                int viscosity = fluidAttributes.get("viscosity").getAsInt();
                int slopeFindDistance = fluidAttributes.has("slope_find_distance") ? fluidAttributes.get("slope_find_distance").getAsInt() : 5;
                int decreasePerBlock = fluidAttributes.has("decrease_per_block") ? fluidAttributes.get("decrease_per_block").getAsInt() : 2;
                int color = (int) Long.parseLong(object.get("color").getAsString(), 16);
                boolean artificial = object.has("artificial") && object.get("artificial").getAsBoolean();

                FluidAttributes.Builder attributes = FluidAttributes.builder(STILL, FLOWING).density(density).luminosity(luminosity).viscosity(viscosity).sound(SoundEvents.BUCKET_FILL).overlay(OVERLAY).color(color);

                if (!artificial) {
                    switch (matterState) {
                        case LIQUID -> registerFluid(elementName, attributes, slopeFindDistance, decreasePerBlock);
                        case GAS -> {
                            attributes.gaseous();
                            registerFluid(elementName, attributes, slopeFindDistance, decreasePerBlock);
                        }
                    }
                }
            }
        }

        for (JsonElement jsonElement : Registry.COMPOUNDS_JSON.getAsJsonArray("compounds")) {
            JsonObject object = jsonElement.getAsJsonObject();
            MatterState matterState = MatterState.valueOf(object.get("matter_state").getAsString().toUpperCase());

            if (Arrays.asList(MatterState.LIQUID, MatterState.GAS).contains(matterState)) {
                String compoundName = object.get("name").getAsString();
                JsonObject fluidAttributes = object.get("fluid_attributes").getAsJsonObject();
                int density = fluidAttributes.get("density").getAsInt();
                int luminosity = fluidAttributes.get("luminosity").getAsInt();
                int viscosity = fluidAttributes.get("viscosity").getAsInt();
                int slopeFindDistance = fluidAttributes.has("slope_find_distance") ? fluidAttributes.get("slope_find_distance").getAsInt() : 5;
                int decreasePerBlock = fluidAttributes.has("decrease_per_block") ? fluidAttributes.get("decrease_per_block").getAsInt() : 2;
                int color = (int) Long.parseLong(object.get("color").getAsString(), 16);

                FluidAttributes.Builder attributes = FluidAttributes.builder(STILL, FLOWING).density(density).luminosity(luminosity).viscosity(viscosity).sound(SoundEvents.BUCKET_FILL).overlay(OVERLAY).color(color);

                switch(matterState) {
                    case LIQUID -> registerFluid(compoundName, attributes, slopeFindDistance, decreasePerBlock);
                    case GAS -> {
                        attributes.gaseous();
                        registerFluid(compoundName, attributes, slopeFindDistance, decreasePerBlock);
                    }
                }
            }
        }
    }

    private static void registerFluid(String pName, FluidAttributes.Builder pFluidBuilder, int pSlopeFindDistance, int pDecreasePerBlock) {

        var ref = new Object() {
            ForgeFlowingFluid.Properties properties = null;
        };

        RegistryObject<FlowingFluid> fluidSource = FLUIDS.register(String.format("%s_source", pName), () -> new ForgeFlowingFluid.Source(ref.properties));
        RegistryObject<FlowingFluid> fluidFlowing = FLUIDS.register(String.format("%s_flowing", pName), () -> new ForgeFlowingFluid.Flowing(ref.properties));
        RegistryObject<LiquidBlock> liquidBlock = LIQUID_BLOCKS.register(String.format("%s_liquid_block", pName), () -> new LiquidBlock(fluidSource::get, BlockBehaviour.Properties.of(Material.WATER).noCollission().strength(100f).noDrops()));
        RegistryObject<Item> bucket = BUCKETS.register(String.format("%s_bucket", pName), () -> new BucketItem(fluidSource, new Item.Properties().tab(ItemRegistry.MISC_TAB).stacksTo(1)));

        ref.properties = new ForgeFlowingFluid.Properties(fluidSource::get, fluidFlowing::get, pFluidBuilder)
                .slopeFindDistance(pSlopeFindDistance)
                .levelDecreasePerBlock(pDecreasePerBlock)
                .block(liquidBlock::get)
                .bucket(bucket::get);
    }

    public static Stream<LiquidBlock> getLiquidBlocks() {
        return LIQUID_BLOCKS.getEntries().stream().map(RegistryObject::get).map(block -> (LiquidBlock) block);
    }

    public static Stream<BucketItem> getBuckets() {
        return BUCKETS.getEntries().stream().map(RegistryObject::get).map(item -> (BucketItem) item);
    }

    public static void register(IEventBus eventBus) {
        registerFluids();
        FLUIDS.register(eventBus);
        LIQUID_BLOCKS.register(eventBus);
        BUCKETS.register(eventBus);
    }
}
