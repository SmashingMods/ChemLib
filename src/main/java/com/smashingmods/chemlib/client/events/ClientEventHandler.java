package com.smashingmods.chemlib.client.events;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.registry.BlockRegistry;
import com.smashingmods.chemlib.registry.FluidRegistry;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.model.DynamicFluidContainerModel;

@EventBusSubscriber(value = Dist.CLIENT, modid = ChemLib.MODID)
public class ClientEventHandler {

    @SubscribeEvent
    public static void onClientSetupEvent(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            FluidRegistry.getFluidsAsStream().forEach(fluid -> ItemBlockRenderTypes.setRenderLayer(fluid, RenderType.solid()));
            FluidRegistry.getLiquidBlocks().forEach(liquidBlock -> ItemBlockRenderTypes.setRenderLayer(liquidBlock, RenderType.solid()));
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