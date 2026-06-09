package com.smashingmods.chemlib.client.events;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.client.AbbreviationRenderer;
import com.smashingmods.chemlib.registry.BlockRegistry;
import com.smashingmods.chemlib.registry.FluidRegistry;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.model.DynamicFluidContainerModel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(value = Dist.CLIENT, modid = ChemLib.MODID)
public class ClientEventHandler {

    @SubscribeEvent
    public static void onClientSetupEvent(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            FluidRegistry.getFluidsAsStream().forEach(fluid -> ItemBlockRenderTypes.setRenderLayer(fluid, RenderType.solid()));
            FluidRegistry.getLiquidBlocks().forEach(liquidBlock -> ItemBlockRenderTypes.setRenderLayer(liquidBlock, RenderType.solid()));
        });
    }

    // Item#initializeClient and FluidType#initializeClient were removed in 1.21.3; the client extensions they
    // used to carry are now registered here. The BEWLR that draws element/chemical abbreviations was attached
    // to every ElementItem/ChemicalItem; each chemical FluidType still tints the shared water textures with its
    // own colour, recorded by FluidRegistry at registration time.
    @SubscribeEvent
    public static void onRegisterClientExtensions(final RegisterClientExtensionsEvent event) {
        List<Item> abbreviationItems = new ArrayList<>(ItemRegistry.getElements());
        ItemRegistry.getChemicalItems().forEach(abbreviationItems::add);
        event.registerItem(AbbreviationRenderer.RENDERER, abbreviationItems.toArray(new Item[0]));

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

    @SubscribeEvent
    public static void onItemColorHandlerEvent(final RegisterColorHandlersEvent.Item event) {
        ItemRegistry.getElements().forEach(element -> event.register(element::getColor, element));
        ItemRegistry.getCompounds().forEach(compound -> event.register(compound::getColor, compound));
        ItemRegistry.getChemicalItems().forEach(item -> event.register(item::getColor, item));
        ItemRegistry.getChemicalBlockItems().forEach(item -> event.register(item::getColor, item));
        FluidRegistry.getBuckets().forEach(bucket -> event.register(new DynamicFluidContainerModel.Colors(), bucket));
    }

    @SubscribeEvent
    public static void onBlockColorHandlerEvent(final RegisterColorHandlersEvent.Block event) {
        BlockRegistry.getAllChemicalBlocks().forEach(block -> event.register(block.getBlockColor(new ItemStack(block.asItem()), 0), block));
    }

	@SubscribeEvent
	public static void onModelRegister(ModelEvent.RegisterAdditional event) {
        event.register(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(ChemLib.MODID, "item/element_solid_model")));
        event.register(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(ChemLib.MODID, "item/element_liquid_model")));
        event.register(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(ChemLib.MODID, "item/element_gas_model")));
        event.register(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(ChemLib.MODID, "item/chemical_dust_model")));
        event.register(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(ChemLib.MODID, "item/chemical_nugget_model")));
        event.register(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(ChemLib.MODID, "item/chemical_ingot_model")));
        event.register(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(ChemLib.MODID, "item/chemical_plate_model")));
	}
}