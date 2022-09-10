package com.smashingmods.chemlib.registry;

import com.smashingmods.chemlib.ChemLib;
import net.minecraft.resources.ResourceLocation;
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

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FluidRegistry {

    public static final ResourceLocation STILL = new ResourceLocation("block/water_still");
    public static final ResourceLocation FLOWING = new ResourceLocation("block/water_flow");
    public static final ResourceLocation OVERLAY = new ResourceLocation("block/water_overlay");

    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, ChemLib.MODID);
    public static final DeferredRegister<Block> LIQUID_BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ChemLib.MODID);
    public static final DeferredRegister<Item> BUCKETS = DeferredRegister.create(ForgeRegistries.ITEMS, ChemLib.MODID);

    protected static void registerFluid(String pName, FluidAttributes.Builder pFluidBuilder, int pSlopeFindDistance, int pDecreasePerBlock) {

        var ref = new Object() {
            ForgeFlowingFluid.Properties properties = null;
        };

        RegistryObject<FlowingFluid> fluidSource = FLUIDS.register(String.format("%s_fluid", pName), () -> new ForgeFlowingFluid.Source(ref.properties));
        RegistryObject<FlowingFluid> fluidFlowing = FLUIDS.register(String.format("%s_flowing", pName), () -> new ForgeFlowingFluid.Flowing(ref.properties));
        RegistryObject<LiquidBlock> liquidBlock = LIQUID_BLOCKS.register(pName, () -> new LiquidBlock(fluidSource, BlockBehaviour.Properties.of(Material.WATER).noDrops()));
        RegistryObject<Item> bucket = BUCKETS.register(String.format("%s_bucket", pName), () -> new BucketItem(fluidSource, new Item.Properties().tab(ItemRegistry.MISC_TAB).stacksTo(1)));

        ref.properties = new ForgeFlowingFluid.Properties(fluidSource, fluidFlowing, pFluidBuilder)
                .slopeFindDistance(pSlopeFindDistance)
                .levelDecreasePerBlock(pDecreasePerBlock)
                .block(liquidBlock)
                .bucket(bucket);
    }/*
       This section defines helper methods for accessing fluids and fluid types from the registry.

       The first set of helper methods provide streams of fluid objects.
    */

    public static Stream<Fluid> getFluidsAsStream() {
        return FLUIDS.getEntries().stream().map(RegistryObject::get);
    }

    public static Stream<ForgeFlowingFluid.Source> getSourceFluidsAsStream() {
        return getFluidsAsStream().filter(fluid -> fluid instanceof ForgeFlowingFluid.Source).map(fluid -> (ForgeFlowingFluid.Source) fluid);
    }

    public static Stream<ForgeFlowingFluid.Source> getLiquidSourceFluidsAsStream() {
        return getSourceFluidsAsStream().filter(source -> !source.getAttributes().isLighterThanAir());
    }

    public static Stream<ForgeFlowingFluid.Source> getGasSourceFluidsAsStream() {
        return getSourceFluidsAsStream().filter(source -> source.getAttributes().isLighterThanAir());
    }

    /*
        This set of helper methods provide lists of fluid objects.
     */

    public static List<Fluid> getFluids() {
        return getFluidsAsStream().collect(Collectors.toList());
    }

    public static List<Fluid> getSourceFluids() {
        return getSourceFluidsAsStream().collect(Collectors.toList());
    }

    public static List<Fluid> getLiquidSourceFluids() {
        return getLiquidSourceFluidsAsStream().collect(Collectors.toList());
    }

    public static List<Fluid> getGasSourceFluids() {
        return getGasSourceFluidsAsStream().collect(Collectors.toList());
    }

    /*
        Get a single object by filtering a registry stream.
     */

    public static Optional<Fluid> getFluidByName(String pName) {
        return getFluidsAsStream().filter(fluid -> Objects.requireNonNull(ForgeRegistries.FLUIDS.getKey(fluid)).getPath().equals(String.format("%s_fluid", pName))).findFirst();
    }

    public static Optional<ForgeFlowingFluid.Source> getSourceFluidByName(String pName) {
        return getSourceFluidsAsStream().filter(source -> Objects.requireNonNull(ForgeRegistries.FLUIDS.getKey(source)).getPath().equals(String.format("%s_fluid", pName))).findFirst();
    }

    public static Optional<ForgeFlowingFluid.Source> getLiquidSourceFluidByName(String pName) {
        return getLiquidSourceFluidsAsStream().filter(source -> Objects.requireNonNull(ForgeRegistries.FLUIDS.getKey(source)).getPath().equals(String.format("%s_fluid", pName))).findFirst();
    }

    public static Optional<ForgeFlowingFluid.Source> getGasSourceFluidByName(String pName) {
        return getGasSourceFluidsAsStream().filter(source -> Objects.requireNonNull(ForgeRegistries.FLUIDS.getKey(source)).getPath().equals(String.format("%s_fluid", pName))).findFirst();
    }

    /*
        This set of helpers define methods to get blocks and items from the fluid registry.
     */

    public static Stream<LiquidBlock> getLiquidBlocks() {
        return LIQUID_BLOCKS.getEntries().stream().map(RegistryObject::get).map(block -> (LiquidBlock) block);
    }

    public static Stream<BucketItem> getBuckets() {
        return BUCKETS.getEntries().stream().map(RegistryObject::get).map(item -> (BucketItem) item);
    }

    public static void register(IEventBus eventBus) {
        FLUIDS.register(eventBus);
        LIQUID_BLOCKS.register(eventBus);
        BUCKETS.register(eventBus);
    }
}
