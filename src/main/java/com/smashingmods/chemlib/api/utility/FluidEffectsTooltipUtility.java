package com.smashingmods.chemlib.api.utility;

import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class FluidEffectsTooltipUtility {

    public static List<Component> getBucketEffectTooltipComponents(ItemStack pStack) {
        List<Component> componentList = new ArrayList<>();

        BuiltInRegistries.FLUID.getResourceKey(((BucketItem) pStack.getItem()).content).ifPresent(fluidResourceKey -> {
            String chemicalName = StringUtils.removeEnd(fluidResourceKey.location().getPath(), "_fluid");
            AtomicReference<List<MobEffectInstance>> effectList = new AtomicReference<>();
            ItemRegistry.getElementByName(chemicalName).ifPresent(element -> effectList.set(element.getEffects()));
            ItemRegistry.getCompoundByName(chemicalName).ifPresent(compound -> effectList.set(compound.getEffects()));
            addTooltipEffects(effectList.get(), componentList);
        });
        return componentList;
    }

    public static void addTooltipEffects(List<MobEffectInstance> pEffects, List<Component> pTooltips) {
        if (pEffects.isEmpty()) {
            pTooltips.add(MutableComponent.create(new PlainTextContents.LiteralContents(" ")));
            pTooltips.add(MutableComponent.create(new TranslatableContents("chemlib.effect.on_hit", null, TranslatableContents.NO_ARGS)).withStyle(ChatFormatting.UNDERLINE).append(":"));
            pTooltips.add(Component.translatable("effect.none").withStyle(ChatFormatting.GRAY));
        }
    }
}
