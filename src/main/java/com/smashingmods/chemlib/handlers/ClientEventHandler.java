package com.smashingmods.chemlib.handlers;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.items.CompoundItem;
import com.smashingmods.chemlib.items.IngotItem;
import com.smashingmods.chemlib.items.ModItems;
import net.minecraft.client.color.item.ItemColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT, modid = ChemLib.MODID)
public class ClientEventHandler {


    @SubscribeEvent
    public static void colorStuff(final ColorHandlerEvent.Item e) {
        ItemColors colors = e.getItemColors();
        ModItems.items.stream()
                .filter(item -> item instanceof CompoundItem)
                .forEach(item -> colors.register(((CompoundItem) item)::getColor, item));
        ModItems.items.stream()
                .filter(item -> item instanceof IngotItem)
                .forEach(item -> colors.register((stack, tintIndex) -> ((IngotItem) item).color, item));
    }
}