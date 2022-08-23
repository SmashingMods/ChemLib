package com.smashingmods.chemlib.registry;

import com.smashingmods.chemlib.ChemLib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;
import java.util.stream.Stream;

public class FluidRegistry {

    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, ChemLib.MODID);
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, ChemLib.MODID);
    public static final DeferredRegister<Block> LIQUID_BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ChemLib.MODID);
    public static final DeferredRegister<Item> BUCKETS = DeferredRegister.create(ForgeRegistries.ITEMS, ChemLib.MODID);

    protected static void registerFluid(String pName, FluidType.Properties pFluidProperties, int pColor, int pSlopeFindDistance, int pDecreasePerBlock) {

        var ref = new Object() {
            ForgeFlowingFluid.Properties properties = null;
        };

        RegistryObject<FluidType> fluidType = FLUID_TYPES.register(pName, () -> new FluidType(pFluidProperties) {
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

        RegistryObject<FlowingFluid> fluidSource = FLUIDS.register(String.format("%s_fluid", pName), () -> new ForgeFlowingFluid.Source(ref.properties));
        RegistryObject<FlowingFluid> fluidFlowing = FLUIDS.register(String.format("%s_flowing", pName), () -> new ForgeFlowingFluid.Flowing(ref.properties));
        RegistryObject<LiquidBlock> liquidBlock = LIQUID_BLOCKS.register(pName, () -> new LiquidBlock(fluidSource, BlockBehaviour.Properties.of(Material.WATER)));
        RegistryObject<Item> bucket = BUCKETS.register(String.format("%s_bucket", pName), () -> new BucketItem(fluidSource, new Item.Properties().tab(ItemRegistry.MISC_TAB).stacksTo(1)));

        ref.properties = new ForgeFlowingFluid.Properties(fluidType, fluidSource, fluidFlowing)
                .slopeFindDistance(pSlopeFindDistance)
                .levelDecreasePerBlock(pDecreasePerBlock)
                .block(liquidBlock)
                .bucket(bucket);
    }

    public static Stream<Fluid> getFluids() {
        return FLUIDS.getEntries().stream().map(RegistryObject::get);
    }

    public static Stream<LiquidBlock> getLiquidBlocks() {
        return LIQUID_BLOCKS.getEntries().stream().map(RegistryObject::get).map(block -> (LiquidBlock) block);
    }

    public static Stream<BucketItem> getBuckets() {
        return BUCKETS.getEntries().stream().map(RegistryObject::get).map(item -> (BucketItem) item);
    }

    public static void register(IEventBus eventBus) {
        FLUIDS.register(eventBus);
        FLUID_TYPES.register(eventBus);
        LIQUID_BLOCKS.register(eventBus);
        BUCKETS.register(eventBus);
    }
}
