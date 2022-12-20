package com.smashingmods.chemlib.api.utility;

import com.google.common.collect.Lists;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class FluidEffectsTooltipUtility {

    public static List<Component> getBucketEffectTooltipComponents(ItemStack pStack) {
        List<Component> componentList = new ArrayList<>();

        ForgeRegistries.FLUIDS.getResourceKey(((BucketItem) pStack.getItem()).getFluid()).ifPresent(fluidResourceKey -> {
            String chemicalName = StringUtils.removeEnd(fluidResourceKey.location().getPath(), "_fluid");
            AtomicReference<List<MobEffectInstance>> effectList = new AtomicReference<>();
            ItemRegistry.getElementByName(chemicalName).ifPresent(element -> effectList.set(element.getEffects()));
            ItemRegistry.getCompoundByName(chemicalName).ifPresent(compound -> effectList.set(compound.getEffects()));
            addTooltipEffects(effectList.get(), componentList);
        });
        return componentList;
    }

    public static void addTooltipEffects(List<MobEffectInstance> pEffects, List<Component> pTooltips) {
        List<Pair<Attribute, AttributeModifier>> attributeModifierPairList = Lists.newArrayList();
        if (pEffects.isEmpty()) {
            pTooltips.add(MutableComponent.create(new LiteralContents(" ")));
            pTooltips.add(MutableComponent.create(new TranslatableContents("chemlib.effect.on_hit")).withStyle(ChatFormatting.UNDERLINE).append(":"));
            pTooltips.add(Component.translatable("effect.none").withStyle(ChatFormatting.GRAY));
        } else {
            pTooltips.add(MutableComponent.create(new LiteralContents(" ")));
            pTooltips.add(MutableComponent.create(new TranslatableContents("chemlib.effect.on_hit")).withStyle(ChatFormatting.UNDERLINE).append(":"));
            for (MobEffectInstance effectInstance : pEffects) {
                MutableComponent mutableComponent = Component.translatable(effectInstance.getDescriptionId());
                MobEffect effect = effectInstance.getEffect();
                Map<Attribute, AttributeModifier> attributeModifierMap = effect.getAttributeModifiers();

                if (!attributeModifierMap.isEmpty()) {
                    for (Map.Entry<Attribute, AttributeModifier> attributeModifierEntry : attributeModifierMap.entrySet()) {
                        AttributeModifier entryValue = attributeModifierEntry.getValue();
                        AttributeModifier attributeModifier = new AttributeModifier(entryValue.getName(), effect.getAttributeModifierValue(effectInstance.getAmplifier(), entryValue), entryValue.getOperation());
                        attributeModifierPairList.add(Pair.of(attributeModifierEntry.getKey(), attributeModifier));
                    }
                }

                if (effectInstance.getAmplifier() > 0 && effectInstance.getAmplifier() <= 20) {
                    mutableComponent = Component.translatable("potion.withAmplifier", mutableComponent, Component.translatable("potion.potency." + effectInstance.getAmplifier()));
                } else {
                    mutableComponent = Component.translatable("potion.withDuration", mutableComponent, MobEffectUtil.formatDuration(effectInstance, 1.0F));
                }
                pTooltips.add(mutableComponent.withStyle(effect.getCategory().getTooltipFormatting()));
            }
        }

        if (!attributeModifierPairList.isEmpty()) {
            for (Pair<Attribute, AttributeModifier> attributeModifierPair : attributeModifierPairList) {
                AttributeModifier attributeModifier = attributeModifierPair.getValue();

                double baseModifierAmount = attributeModifier.getAmount();
                double finalModiferAmount;

                if (attributeModifier.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && attributeModifier.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
                    finalModiferAmount = attributeModifier.getAmount();
                } else {
                    finalModiferAmount = attributeModifier.getAmount() * 100.0D;
                }
                if (baseModifierAmount > 0.0D) {
                    pTooltips.add(Component.translatable(String.format("attribute.modifier.plus.%s", attributeModifier.getOperation().toValue()),
                            ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(finalModiferAmount),
                            Component.translatable(attributeModifierPair.getKey().getDescriptionId()))
                            .withStyle(ChatFormatting.BLUE));

                } else if (baseModifierAmount < 0.0D) {
                    finalModiferAmount *= -1.0D;
                    pTooltips.add(Component.translatable(String.format("attribute.modifier.take.%s", attributeModifier.getOperation().toValue()),
                            ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(finalModiferAmount),
                            Component.translatable(attributeModifierPair.getKey().getDescriptionId()))
                            .withStyle(ChatFormatting.RED));
                }
            }
        }
    }
}
