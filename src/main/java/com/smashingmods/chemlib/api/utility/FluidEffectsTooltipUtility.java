package com.smashingmods.chemlib.api.utility;

import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class FluidEffectsTooltipUtility {

    public static List<Component> getBucketEffectTooltipComponents(ItemStack pStack) {
        List<Component> componentList = new ArrayList<>();

        BuiltInRegistries.FLUID.getResourceKey(((BucketItem) pStack.getItem()).content).ifPresent(fluidResourceKey -> {
            String chemicalName = StringUtils.removeEnd(fluidResourceKey.location().getPath(), "_fluid");
            AtomicReference<List<MobEffectInstance>> effectList = new AtomicReference<>(List.of());
            ItemRegistry.getElementByName(chemicalName).ifPresent(element -> effectList.set(element.getEffects()));
            ItemRegistry.getCompoundByName(chemicalName).ifPresent(compound -> effectList.set(compound.getEffects()));
            addTooltipEffects(effectList.get(), componentList);
        });
        return componentList;
    }

    public static void addTooltipEffects(List<MobEffectInstance> pEffects, List<Component> pTooltips) {
        pTooltips.add(Component.literal(" "));
        pTooltips.add(Component.translatable("chemlib.effect.on_hit").withStyle(ChatFormatting.UNDERLINE).append(":"));

        if (pEffects.isEmpty()) {
            pTooltips.add(Component.translatable("effect.none").withStyle(ChatFormatting.GRAY));
            return;
        }

        List<Pair<Holder<Attribute>, AttributeModifier>> attributeModifierPairList = new ArrayList<>();

        for (MobEffectInstance effectInstance : pEffects) {
            MutableComponent line = Component.translatable(effectInstance.getDescriptionId());
            if (effectInstance.getAmplifier() > 0 && effectInstance.getAmplifier() <= 20) {
                line = Component.translatable("potion.withAmplifier", line, Component.translatable("potion.potency." + effectInstance.getAmplifier()));
            } else {
                line = Component.translatable("potion.withDuration", line, MobEffectUtil.formatDuration(effectInstance, 1.0F, 20));
            }
            pTooltips.add(line.withStyle(effectInstance.getEffect().value().getCategory().getTooltipFormatting()));

            effectInstance.getEffect().value().createModifiers(effectInstance.getAmplifier(),
                    (attributeHolder, modifier) -> attributeModifierPairList.add(Pair.of(attributeHolder, modifier)));
        }

        if (!attributeModifierPairList.isEmpty()) {
            for (Pair<Holder<Attribute>, AttributeModifier> attributeModifierPair : attributeModifierPairList) {
                Holder<Attribute> attributeHolder = attributeModifierPair.getKey();
                AttributeModifier modifier = attributeModifierPair.getValue();

                double baseModifierAmount = modifier.amount();
                double finalModifierAmount;

                if (modifier.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                        || modifier.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
                    finalModifierAmount = baseModifierAmount * 100.0D;
                } else if (attributeHolder.is(Attributes.KNOCKBACK_RESISTANCE)) {
                    finalModifierAmount = baseModifierAmount * 10.0D;
                } else {
                    finalModifierAmount = baseModifierAmount;
                }

                if (baseModifierAmount > 0.0D) {
                    pTooltips.add(Component.translatable(
                            String.format("attribute.modifier.plus.%d", modifier.operation().id()),
                            ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(finalModifierAmount),
                            Component.translatable(attributeHolder.value().getDescriptionId()))
                            .withStyle(ChatFormatting.BLUE));
                } else if (baseModifierAmount < 0.0D) {
                    finalModifierAmount *= -1.0D;
                    pTooltips.add(Component.translatable(
                            String.format("attribute.modifier.take.%d", modifier.operation().id()),
                            ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(finalModifierAmount),
                            Component.translatable(attributeHolder.value().getDescriptionId()))
                            .withStyle(ChatFormatting.RED));
                }
            }
        }
    }
}
