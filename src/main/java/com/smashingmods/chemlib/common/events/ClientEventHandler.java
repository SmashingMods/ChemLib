package com.smashingmods.chemlib.common.events;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.common.registry.BlockRegistry;
import com.smashingmods.chemlib.common.registry.FluidRegistry;
import com.smashingmods.chemlib.common.registry.ItemRegistry;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT, modid = ChemLib.MODID)
public class ClientEventHandler {

    @SubscribeEvent
    public static void onItemColorHandlerEvent(final ColorHandlerEvent.Item colorHandlerEvent) {
        ItemRegistry.getElements().forEach(element -> colorHandlerEvent.getItemColors().register(element::getColor, element));
        ItemRegistry.getCompounds().forEach(compound -> colorHandlerEvent.getItemColors().register(compound::getColor, compound));
        ItemRegistry.getChemicalItems().forEach(item -> colorHandlerEvent.getItemColors().register(item::getColor, item));
        ItemRegistry.getChemicalBlockItems().forEach(item -> colorHandlerEvent.getItemColors().register(item::getColor, item));
        FluidRegistry.getBuckets().forEach(bucket -> colorHandlerEvent.getItemColors().register((pStack, pTintIndex) -> pTintIndex == 0 ? bucket.getFluid().getAttributes().getColor() : -1, bucket.asItem()));
    }

    @SubscribeEvent
    public static void onBlockColorHandlerEvent(final ColorHandlerEvent.Block colorHandlerEvent) {
        BlockRegistry.getAllChemicalBlocks().forEach(block -> colorHandlerEvent.getBlockColors().register(block.getBlockColor(new ItemStack(block.asItem()), 0), block));
    }

	@SubscribeEvent
	public static void onModelRegister(ModelRegistryEvent event) {
        ForgeModelBakery.addSpecialModel(new ModelResourceLocation(new ResourceLocation(ChemLib.MODID, "element_solid_model"), "inventory"));
        ForgeModelBakery.addSpecialModel(new ModelResourceLocation(new ResourceLocation(ChemLib.MODID, "element_liquid_model"), "inventory"));
		ForgeModelBakery.addSpecialModel(new ModelResourceLocation(new ResourceLocation(ChemLib.MODID, "element_gas_model"), "inventory"));
	}
}