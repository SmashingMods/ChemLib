package com.smashingmods.chemlib.api.utility;

import com.google.common.collect.Lists;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
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

        // BucketItem#getFluid was removed in 1.20.6, so the chemical name is taken from the bucket's own
        // registry id ("<chemical>_bucket") rather than from its fluid's id ("<chemical>_fluid").
        String chemicalName = StringUtils.removeEnd(BuiltInRegistries.ITEM.getKey(pStack.getItem()).getPath(), "_bucket");
        AtomicReference<List<MobEffectInstance>> effectList = new AtomicReference<>(List.of());
        ItemRegistry.getElementByName(chemicalName).ifPresent(element -> effectList.set(element.getEffects()));
        ItemRegistry.getCompoundByName(chemicalName).ifPresent(compound -> effectList.set(compound.getEffects()));
        addTooltipEffects(effectList.get(), componentList);
        return componentList;
    }

    public static void addTooltipEffects(List<MobEffectInstance> pEffects, List<Component> pTooltips) {
        List<Pair<Holder<Attribute>, AttributeModifier>> attributeModifierPairList = Lists.newArrayList();
        if (pEffects.isEmpty()) {
            pTooltips.add(MutableComponent.create(PlainTextContents.create(" ")));
            pTooltips.add(MutableComponent.create(new TranslatableContents("chemlib.effect.on_hit", null, TranslatableContents.NO_ARGS)).withStyle(ChatFormatting.UNDERLINE).append(":"));
            pTooltips.add(Component.translatable("effect.none").withStyle(ChatFormatting.GRAY));
        } else {
            pTooltips.add(MutableComponent.create(PlainTextContents.create(" ")));
            pTooltips.add(MutableComponent.create(new TranslatableContents("chemlib.effect.on_hit", null, TranslatableContents.NO_ARGS)).withStyle(ChatFormatting.UNDERLINE).append(":"));
            for (MobEffectInstance effectInstance : pEffects) {
                MutableComponent mutableComponent = Component.translatable(effectInstance.getDescriptionId());
                MobEffect effect = effectInstance.getEffect().value();
                effect.createModifiers(effectInstance.getAmplifier(), (attribute, attributeModifier) -> attributeModifierPairList.add(Pair.of(attribute, attributeModifier)));

                if (effectInstance.getAmplifier() > 0 && effectInstance.getAmplifier() <= 20) {
                    mutableComponent = Component.translatable("potion.withAmplifier", mutableComponent, Component.translatable("potion.potency." + effectInstance.getAmplifier()));
                } else {
                    mutableComponent = Component.translatable("potion.withDuration", mutableComponent, MobEffectUtil.formatDuration(effectInstance, 1.0F, 20.0F));
                }
                pTooltips.add(mutableComponent.withStyle(effect.getCategory().getTooltipFormatting()));
            }
        }

        if (!attributeModifierPairList.isEmpty()) {
            for (Pair<Holder<Attribute>, AttributeModifier> attributeModifierPair : attributeModifierPairList) {
                AttributeModifier attributeModifier = attributeModifierPair.getValue();

                double baseModifierAmount = attributeModifier.amount();
                double finalModiferAmount;

                if (attributeModifier.operation() != AttributeModifier.Operation.ADD_MULTIPLIED_BASE && attributeModifier.operation() != AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
                    finalModiferAmount = attributeModifier.amount();
                } else {
                    finalModiferAmount = attributeModifier.amount() * 100.0D;
                }
                if (baseModifierAmount > 0.0D) {
                    pTooltips.add(Component.translatable(String.format("attribute.modifier.plus.%s", attributeModifier.operation().id()),
                            ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(finalModiferAmount),
                            Component.translatable(attributeModifierPair.getKey().value().getDescriptionId()))
                            .withStyle(ChatFormatting.BLUE));

                } else if (baseModifierAmount < 0.0D) {
                    finalModiferAmount *= -1.0D;
                    pTooltips.add(Component.translatable(String.format("attribute.modifier.take.%s", attributeModifier.operation().id()),
                            ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(finalModiferAmount),
                            Component.translatable(attributeModifierPair.getKey().value().getDescriptionId()))
                            .withStyle(ChatFormatting.RED));
                }
            }
        }
    }
}
