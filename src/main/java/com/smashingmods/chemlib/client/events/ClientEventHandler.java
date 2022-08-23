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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.model.DynamicFluidContainerModel;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT, modid = ChemLib.MODID)
public class ClientEventHandler {

    @SubscribeEvent
    public static void onClientSetupEvent(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            FluidRegistry.getFluids().forEach(fluid -> ItemBlockRenderTypes.setRenderLayer(fluid, RenderType.solid()));
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
        event.register(new ModelResourceLocation(new ResourceLocation(ChemLib.MODID, "element_solid_model"), "inventory"));
        event.register(new ModelResourceLocation(new ResourceLocation(ChemLib.MODID, "element_liquid_model"), "inventory"));
        event.register(new ModelResourceLocation(new ResourceLocation(ChemLib.MODID, "element_gas_model"), "inventory"));
        event.register(new ModelResourceLocation(new ResourceLocation(ChemLib.MODID, "chemical_dust_model"), "inventory"));
        event.register(new ModelResourceLocation(new ResourceLocation(ChemLib.MODID, "chemical_nugget_model"), "inventory"));
        event.register(new ModelResourceLocation(new ResourceLocation(ChemLib.MODID, "chemical_ingot_model"), "inventory"));
        event.register(new ModelResourceLocation(new ResourceLocation(ChemLib.MODID, "chemical_plate_model"), "inventory"));
	}
}