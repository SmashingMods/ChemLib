package com.smashingmods.chemlib.client.events;

import com.mojang.datafixers.util.Either;
import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.api.utility.FluidEffectsTooltipUtility;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BucketItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Function;

@Mod.EventBusSubscriber(modid = ChemLib.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ForgeEventHandler {
    @SubscribeEvent
    public static void onRenderTooltip(RenderTooltipEvent.GatherComponents event) {
        if (event.getItemStack().getItem() instanceof BucketItem bucket
                && ForgeRegistries.FLUIDS.getResourceKey(bucket.getFluid()).isPresent()
                && ForgeRegistries.FLUIDS.getResourceKey(bucket.getFluid()).get().location().getNamespace().equals(ChemLib.MODID)) {
            Function<FormattedText, Either<FormattedText, TooltipComponent>> formattedTextFunction = Either::left;

            for (FormattedText textElement : FluidEffectsTooltipUtility.getBucketEffectTooltipComponents(event.getItemStack())) {
                event.getTooltipElements().add(formattedTextFunction.apply(textElement));
            }
        }
    }
}