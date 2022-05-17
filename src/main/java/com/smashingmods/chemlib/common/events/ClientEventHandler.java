package com.smashingmods.chemlib.common.events;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.common.registry.BlockRegistry;
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
    public static void onColorHandlerEvent(final ColorHandlerEvent.Item colorHandlerEvent) {
        ItemRegistry.getElements().forEach(elementItem -> colorHandlerEvent.getItemColors().register(elementItem::getColor, elementItem));
        ItemRegistry.getCompounds().forEach(compoundItem -> colorHandlerEvent.getItemColors().register(compoundItem::getColor, compoundItem));
        ItemRegistry.getChemicalItems().forEach(item -> colorHandlerEvent.getItemColors().register(item::getColor, item));
        ItemRegistry.BLOCK_ITEMS.forEach(item -> colorHandlerEvent.getItemColors().register(item::getColor, item));
        BlockRegistry.getAllChemicalBlocks().forEach(block -> colorHandlerEvent.getBlockColors().register(block.getBlockColor(new ItemStack(block.asItem()), 0), block));
    }

	@SubscribeEvent
	public static void onModelRegister(ModelRegistryEvent event) {
		ForgeModelBakery.addSpecialModel(new ModelResourceLocation(new ResourceLocation(ChemLib.MODID, "element_model"), "inventory"));
	}
}