package com.smashingmods.chemlib.registry;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.common.blocks.ChemicalLiquidBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FluidRegistry {

    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, ChemLib.MODID);
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, ChemLib.MODID);
    public static final DeferredRegister<Block> LIQUID_BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, ChemLib.MODID);
    public static final DeferredRegister<Item> BUCKETS = DeferredRegister.create(BuiltInRegistries.ITEM, ChemLib.MODID);

    protected static void registerFluid(String name, FluidType.Properties fluidProperties, int color, int slopeFindDistance, int decreasePerBlock) {

        var ref = new Object() {
            BaseFlowingFluid.Properties properties = null;
        };

        Supplier<FluidType> fluidType = FLUID_TYPES.register(name, () -> new FluidType(fluidProperties) {
            @SuppressWarnings("removal")
            @Override
            public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                consumer.accept(new IClientFluidTypeExtensions() {
                    @Override
                    public ResourceLocation getStillTexture() {
                        return ResourceLocation.withDefaultNamespace("block/water_still");
                    }

                    @Override
                    public ResourceLocation getFlowingTexture() {
                        return ResourceLocation.withDefaultNamespace("block/water_flow");
                    }

                    @Override
                    public ResourceLocation getOverlayTexture() {
                        return ResourceLocation.withDefaultNamespace("block/water_overlay");
                    }

                    @Override
                    public ResourceLocation getRenderOverlayTexture(Minecraft mc) {
                        return ResourceLocation.withDefaultNamespace("textures/misc/underwater.png");
                    }
                    @Override
                    public int getTintColor() {
                        return color;
                    }

                    @Override
                    public int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
                        return color;
                    }
                });
            }
        });

        DeferredHolder<Fluid, FlowingFluid> fluidSource = FLUIDS.register(String.format("%s_fluid", name), () -> new BaseFlowingFluid.Source(ref.properties));
        DeferredHolder<Fluid, FlowingFluid> fluidFlowing = FLUIDS.register(String.format("%s_flowing", name), () -> new BaseFlowingFluid.Flowing(ref.properties));
        DeferredHolder<Block, LiquidBlock> liquidBlock = LIQUID_BLOCKS.register(name, () -> new ChemicalLiquidBlock(fluidSource.get(), name));
        DeferredHolder<Item, Item> bucket = BUCKETS.register(String.format("%s_bucket", name), () -> new BucketItem(fluidSource.get(), new Item.Properties().stacksTo(1)));

        ref.properties = new BaseFlowingFluid.Properties(fluidType, fluidSource, fluidFlowing)
                .slopeFindDistance(slopeFindDistance)
                .levelDecreasePerBlock(decreasePerBlock)
                .block(liquidBlock)
                .bucket(bucket);
    }

    /*
        This section defines helper methods for accessing fluids and fluid types from the registry.

        The first set of helper methods provide streams of fluid objects.
     */

    public static Stream<Fluid> getFluidsAsStream() {
        return FLUIDS.getEntries().stream().map(DeferredHolder::get);
    }

    public static Stream<FluidType> getFluidTypesAsStream() {
        return getFluidsAsStream().map(Fluid::getFluidType);
    }

    public static Stream<BaseFlowingFluid.Source> getSourceFluidsAsStream() {
        return getFluidsAsStream().filter(fluid -> fluid instanceof BaseFlowingFluid.Source).map(fluid -> (BaseFlowingFluid.Source) fluid);
    }

    public static Stream<BaseFlowingFluid.Source> getLiquidSourceFluidsAsStream() {
        return getSourceFluidsAsStream().filter(source -> !source.getFluidType().isLighterThanAir());
    }

    public static Stream<BaseFlowingFluid.Source> getGasSourceFluidsAsStream() {
        return getSourceFluidsAsStream().filter(source -> source.getFluidType().isLighterThanAir());
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

    public static Optional<FluidType> getFluidTypeByName(String name) {
        return getFluidTypesAsStream().filter(fluidType -> Objects.requireNonNull(NeoForgeRegistries.FLUID_TYPES.getKey(fluidType)).getPath().equals(name)).findFirst();
    }

    public static Optional<BaseFlowingFluid.Source> getSourceFluidByName(String name) {
        return getSourceFluidsAsStream().filter(source -> Objects.requireNonNull(NeoForgeRegistries.FLUID_TYPES.getKey(source.getFluidType())).getPath().equals(name)).findFirst();
    }

    public static Optional<BaseFlowingFluid.Source> getLiquidSourceFluidByName(String name) {
        return getLiquidSourceFluidsAsStream().filter(source -> Objects.requireNonNull(NeoForgeRegistries.FLUID_TYPES.getKey(source.getFluidType())).getPath().equals(name)).findFirst();
    }

    public static Optional<BaseFlowingFluid.Source> getGasSourceFluidByName(String name) {
        return getGasSourceFluidsAsStream().filter(source -> Objects.requireNonNull(NeoForgeRegistries.FLUID_TYPES.getKey(source.getFluidType())).getPath().equals(name)).findFirst();
    }

    /*
        This set of helpers define methods to get blocks and items from the fluid registry.
     */

    public static Stream<LiquidBlock> getLiquidBlocks() {
        return LIQUID_BLOCKS.getEntries().stream().map(DeferredHolder::get).map(block -> (LiquidBlock) block);
    }

    public static Stream<BucketItem> getBuckets() {
        return BUCKETS.getEntries().stream().map(DeferredHolder::get).map(item -> (BucketItem) item);
    }

    public static List<BucketItem> getAllSortedBuckets() {
        LinkedList<BucketItem> buckets = new LinkedList<>(getElementBuckets());
        buckets.addAll(getSortedCompoundBuckets());
        return buckets;
    }

    public static List<BucketItem> getElementBuckets() {
        Map<Integer, BucketItem> bucketMap = new TreeMap<>();
        for(BucketItem bucket : BUCKETS.getEntries().stream().map(DeferredHolder::get).map(item -> (BucketItem) item).toList()) {
            String path = StringUtils.removeEnd(BuiltInRegistries.FLUID.getResourceKey(bucket.content).get().location().getPath(), "_fluid");
            ItemRegistry.getElementByName(path).ifPresent(elementItem -> bucketMap.put(elementItem.getAtomicNumber(), bucket));
        }
        return bucketMap.values().stream().toList();
    }

    public static List<BucketItem> getCompoundBuckets() {
        ArrayList<BucketItem> buckets = new ArrayList<>();
        for(BucketItem bucket : BUCKETS.getEntries().stream().map(DeferredHolder::get).map(item -> (BucketItem) item).toList()) {
            String path = StringUtils.removeEnd(BuiltInRegistries.FLUID.getResourceKey(bucket.content).get().location().getPath(), "_fluid");
            ItemRegistry.getCompoundByName(path).ifPresent(compoundItem -> buckets.add(bucket));
        }
        return buckets;
    }

    public static List<BucketItem> getSortedCompoundBuckets() {
        List<BucketItem> buckets = getCompoundBuckets();
        buckets.sort((BucketItem b1, BucketItem b2) -> b1.content.getFluidType().toString().compareToIgnoreCase(b2.content.getFluidType().toString()));
        return buckets;
    }

    public static void register(IEventBus eventBus) {
        FLUIDS.register(eventBus);
        FLUID_TYPES.register(eventBus);
        LIQUID_BLOCKS.register(eventBus);
        BUCKETS.register(eventBus);
    }
}
