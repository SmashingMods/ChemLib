package com.smashingmods.chemlib.api.utility;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class FluidEffectsTooltipUtility {

    public static List<Component> getBucketEffectTooltipComponents(ItemStack stack, Item.TooltipContext context) {
        List<Component> componentList = new ArrayList<>();

        BuiltInRegistries.FLUID.getResourceKey(((BucketItem) stack.getItem()).content).ifPresent(fluidResourceKey -> {
            String chemicalName = StringUtils.removeEnd(fluidResourceKey.location().getPath(), "_fluid");
            AtomicReference<List<MobEffectInstance>> effectList = new AtomicReference<>();
            ItemRegistry.getElementByName(chemicalName).ifPresent(element -> effectList.set(element.getEffects()));
            ItemRegistry.getCompoundByName(chemicalName).ifPresent(compound -> effectList.set(compound.getEffects()));
            addTooltipEffects(effectList.get(), componentList, context);
        });
        return componentList;
    }

    private static void addTooltipEffects(List<MobEffectInstance> effects, List<Component> tooltips, Item.TooltipContext context) {
        List<Pair<Holder<Attribute>, AttributeModifier>> list = Lists.newArrayList();
        if (effects.isEmpty()) {
            tooltips.add(CommonComponents.EMPTY);
            tooltips.add(Component.translatable("chemlib.effect.on_hit").withStyle(ChatFormatting.UNDERLINE).append(":"));
            tooltips.add(Component.translatable("effect.none").withStyle(ChatFormatting.GRAY));
        } else {
            tooltips.add(CommonComponents.EMPTY);
            tooltips.add(Component.translatable("chemlib.effect.on_hit").withStyle(ChatFormatting.UNDERLINE).append(":"));
            for (MobEffectInstance effectInstance : effects) {
                MutableComponent mutableComponent = Component.translatable(effectInstance.getDescriptionId());
                MobEffect effect = effectInstance.getEffect().value();
                effect.createModifiers(effectInstance.getAmplifier(), (attribute, attributeModifier) -> list.add(new Pair<>(attribute, attributeModifier)));

                if (effectInstance.getAmplifier() > 0) {
                    mutableComponent = Component.translatable("potion.withAmplifier", mutableComponent, Component.translatable("potion.potency." + effectInstance.getAmplifier()));
                }
                if (effectInstance.endsWithin(20)) {
                    mutableComponent = Component.translatable("potion.withDuration", mutableComponent, MobEffectUtil.formatDuration(effectInstance, 1.0F, context.tickRate()));
                }
                tooltips.add(mutableComponent.withStyle(effect.getCategory().getTooltipFormatting()));
            }
        }

        if (!list.isEmpty()) {
            for (Pair<Holder<Attribute>, AttributeModifier> pair : list) {
                AttributeModifier attributeModifier = pair.getSecond();

                double baseModifierAmount = attributeModifier.amount();
                double finalModifierAmount;

                if (attributeModifier.operation() != AttributeModifier.Operation.ADD_MULTIPLIED_BASE && attributeModifier.operation() != AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
                    finalModifierAmount = attributeModifier.amount();
                } else {
                    finalModifierAmount = attributeModifier.amount() * 100.0D;
                }

                if (baseModifierAmount > 0.0) {
                    tooltips.add(
                            Component.translatable(
                                    String.format("attribute.modifier.plus.%s", attributeModifier.operation().id()),
                                    ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(finalModifierAmount),
                                    Component.translatable(pair.getFirst().value().getDescriptionId())
                            ).withStyle(ChatFormatting.BLUE)
                    );
                } else if (baseModifierAmount < 0.0) {
                    finalModifierAmount *= -1.0;
                    tooltips.add(
                            Component.translatable(
                                    String.format("attribute.modifier.take.%s", attributeModifier.operation().id()),
                                    ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(finalModifierAmount),
                                    Component.translatable(pair.getFirst().value().getDescriptionId())
                            ).withStyle(ChatFormatting.RED)
                    );
                }
            }
        }
    }
}
