package com.smashingmods.chemlib.api.utility;

import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
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

        if (pStack.getItem() instanceof BucketItem bucket) {
            var key = BuiltInRegistries.ITEM.getKey(bucket);
            String chemicalName = StringUtils.removeEnd(key.getPath(), "_bucket");
            AtomicReference<List<MobEffectInstance>> effectList = new AtomicReference<>();
            ItemRegistry.getElementByName(chemicalName).ifPresent(element -> effectList.set(element.getEffects()));
            ItemRegistry.getCompoundByName(chemicalName).ifPresent(compound -> effectList.set(compound.getEffects()));
            addTooltipEffects(effectList.get(), componentList);
        }
        return componentList;
    }

    public static void addTooltipEffects(List<MobEffectInstance> pEffects, List<Component> pTooltips) {
        if (pEffects.isEmpty()) {
            pTooltips.add(Component.literal(" "));
            pTooltips.add(Component.translatable("chemlib.effect.on_hit").withStyle(ChatFormatting.UNDERLINE).append(":"));
            pTooltips.add(Component.translatable("effect.none").withStyle(ChatFormatting.GRAY));
        } else {
            pTooltips.add(Component.literal(" "));
            pTooltips.add(Component.translatable("chemlib.effect.on_hit").withStyle(ChatFormatting.UNDERLINE).append(":"));
            for (MobEffectInstance effectInstance : pEffects) {
                MutableComponent mutableComponent = Component.translatable(effectInstance.getDescriptionId());
                MobEffect effect = effectInstance.getEffect().value();

                if (effectInstance.getAmplifier() > 0 && effectInstance.getAmplifier() <= 20) {
                    mutableComponent = Component.translatable("potion.withAmplifier", mutableComponent, Component.translatable("potion.potency." + effectInstance.getAmplifier()));
                } else {
                    // describeDuration() is now private, so we construct the duration text manually
                    int duration = effectInstance.getDuration();
                    String durationStr = duration >= 20 ? String.format("%.0f", duration / 20.0) : "<0.5";
                    mutableComponent = Component.translatable("potion.withDuration", mutableComponent, Component.literal(durationStr));
                }
                pTooltips.add(mutableComponent.withStyle(effect.getCategory().getTooltipFormatting()));
            }
        }
    }
}
