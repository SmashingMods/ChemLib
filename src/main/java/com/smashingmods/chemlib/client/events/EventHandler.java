package com.smashingmods.chemlib.client.events;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.api.utility.FluidEffectsTooltipUtility;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BucketItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.apache.commons.lang3.StringUtils;

@EventBusSubscriber(modid = ChemLib.MODID)
public class EventHandler {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        if (event.getItemStack().getItem() instanceof BucketItem bucket
                && BuiltInRegistries.FLUID.getResourceKey(bucket.content).isPresent()
                && BuiltInRegistries.FLUID.getKey(bucket.content).getNamespace().equals(ChemLib.MODID)) {

            gatherTooltipComponents(event, bucket);
        }
    }

    public static void gatherTooltipComponents(ItemTooltipEvent event, BucketItem bucket) {
        for (Component component : FluidEffectsTooltipUtility.getBucketEffectTooltipComponents(event.getItemStack(), event.getContext())) {
            event.getToolTip().add(component);
        }
        String namespace = BuiltInRegistries.FLUID.getKey(bucket.content).getNamespace();
        event.getToolTip().add(Component.literal(StringUtils.capitalize(namespace)).withStyle(ChemLib.MOD_ID_TEXT_STYLE));
    }
}