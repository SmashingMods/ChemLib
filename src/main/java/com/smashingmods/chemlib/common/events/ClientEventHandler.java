package com.smashingmods.chemlib.events;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.common.registry.ItemRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT, modid = ChemLib.MODID)
public class ClientEventHandler {

    @SubscribeEvent
    public static void onColorHandlerEvent(final ColorHandlerEvent.Item colorHandlerEvent) {
        ItemRegistry.ELEMENTS.stream().forEach(elementItem -> colorHandlerEvent.getItemColors().register(elementItem::getColor, elementItem));
        ItemRegistry.COMPOUNDS.stream().forEach(compoundItem -> colorHandlerEvent.getItemColors().register(compoundItem::getColor, compoundItem));
        ItemRegistry.INGOTS.stream().forEach(ingotItem -> colorHandlerEvent.getItemColors().register(ingotItem::getColor, ingotItem));
    }
}