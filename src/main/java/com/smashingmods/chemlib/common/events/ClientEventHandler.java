package com.smashingmods.chemlib.common.events;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.api.Chemical;
import com.smashingmods.chemlib.api.ChemicalItemType;
import com.smashingmods.chemlib.common.items.ChemicalBlockItem;
import com.smashingmods.chemlib.common.registry.BlockRegistry;
import com.smashingmods.chemlib.common.registry.ItemRegistry;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT, modid = ChemLib.MODID)
public class ClientEventHandler {

    @SubscribeEvent
    public static void onColorHandlerEvent(final ColorHandlerEvent.Item colorHandlerEvent) {
        ItemRegistry.getElements().forEach(elementItem -> colorHandlerEvent.getItemColors().register(elementItem::getColor, elementItem));
        ItemRegistry.getCompounds().forEach(compoundItem -> colorHandlerEvent.getItemColors().register(compoundItem::getColor, compoundItem));
        ItemRegistry.getChemicalItemsByType(ChemicalItemType.DUST).forEach(dust -> colorHandlerEvent.getItemColors().register(dust::getColor, dust));
        ItemRegistry.getChemicalItemsByType(ChemicalItemType.NUGGET).forEach(nugget -> colorHandlerEvent.getItemColors().register(nugget::getColor, nugget));
        ItemRegistry.getChemicalItemsByType(ChemicalItemType.INGOT).forEach(ingot -> colorHandlerEvent.getItemColors().register(ingot::getColor, ingot));
        ItemRegistry.getChemicalItemsByType(ChemicalItemType.PLATE).forEach(plate -> colorHandlerEvent.getItemColors().register(plate::getColor, plate));

        ItemRegistry.BLOCK_ITEMS.forEach(item -> colorHandlerEvent.getItemColors().register(item::getColor, item));

        BlockRegistry.getAllChemicalBlocks().forEach(block -> colorHandlerEvent.getBlockColors().register(block.getBlockColor(new ItemStack(block.asItem()), 0), block));
    }
}