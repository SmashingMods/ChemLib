package com.smashingmods.chemlib.common.events;

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
        ItemRegistry.DUSTS.stream().forEach(dust -> colorHandlerEvent.getItemColors().register(dust::getColor, dust));
        ItemRegistry.NUGGETS.stream().forEach(nugget -> colorHandlerEvent.getItemColors().register(nugget::getColor, nugget));
        ItemRegistry.INGOTS.stream().forEach(ingot -> colorHandlerEvent.getItemColors().register(ingot::getColor, ingot));
        ItemRegistry.PLATES.stream().forEach(plate -> colorHandlerEvent.getItemColors().register(plate::getColor, plate));
    }
}