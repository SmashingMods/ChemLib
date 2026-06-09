package com.smashingmods.chemlib.client.events;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.client.AbbreviationRenderer;
import com.smashingmods.chemlib.registry.BlockRegistry;
import com.smashingmods.chemlib.registry.FluidRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = ChemLib.MODID)
public class ClientEventHandler {

    @SubscribeEvent
    public static void onClientSetupEvent(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            FluidRegistry.getFluidsAsStream().forEach(fluid -> ItemBlockRenderTypes.setRenderLayer(fluid, RenderType.solid()));
            FluidRegistry.getLiquidBlocks().forEach(liquidBlock -> ItemBlockRenderTypes.setRenderLayer(liquidBlock, RenderType.solid()));
        });
    }

    // FluidType#initializeClient was removed in 1.21.3; the client extensions it used to carry are registered here.
    // Each chemical FluidType tints the shared water textures with its own colour, recorded by FluidRegistry at
    // registration time. The element/chemical abbreviation overlay that used to be a per-item client extension is
    // now an item-model renderer registered in onRegisterSpecialModelRenderers.
    @SubscribeEvent
    public static void onRegisterClientExtensions(final RegisterClientExtensionsEvent event) {
        FluidRegistry.getFluidTypeColors().forEach(entry -> {
            int color = entry.color();
            event.registerFluidType(new IClientFluidTypeExtensions() {
                @Override
                public ResourceLocation getStillTexture() {
                    return ResourceLocation.parse("block/water_still");
                }

                @Override
                public ResourceLocation getFlowingTexture() {
                    return ResourceLocation.parse("block/water_flow");
                }

                @Override
                public ResourceLocation getOverlayTexture() {
                    return ResourceLocation.parse("block/water_overlay");
                }

                @Override
                public ResourceLocation getRenderOverlayTexture(Minecraft mc) {
                    return ResourceLocation.fromNamespaceAndPath("minecraft", "textures/misc/underwater.png");
                }

                @Override
                public int getTintColor() {
                    return color;
                }

                @Override
                public int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
                    return color;
                }
            }, entry.fluidType().get());
        });
    }

    // 1.21.4 replaced the BEWLR item renderer with item-model definitions; the abbreviation overlay is registered
    // as a SpecialModelRenderer codec here and referenced from each element/chemical item-model definition.
    @SubscribeEvent
    public static void onRegisterSpecialModelRenderers(final RegisterSpecialModelRendererEvent event) {
        event.register(AbbreviationRenderer.ID, AbbreviationRenderer.Unbaked.MAP_CODEC);
    }

    @SubscribeEvent
    public static void onBlockColorHandlerEvent(final RegisterColorHandlersEvent.Block event) {
        BlockRegistry.getAllChemicalBlocks().forEach(block -> event.register(block.getBlockColor(new ItemStack(block.asItem()), 0), block));
    }
}
