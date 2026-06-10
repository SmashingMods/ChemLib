package com.smashingmods.chemlib.common.items;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.api.Chemical;
import com.smashingmods.chemlib.api.ChemicalItemType;
import com.smashingmods.chemlib.api.Element;
import com.smashingmods.chemlib.api.MatterState;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

public class ChemicalItem extends Item implements Chemical {

    private final Chemical chemical;
    private final ChemicalItemType itemType;

    public ChemicalItem(Chemical pChemical, ChemicalItemType pChemicalItemType, Item.Properties pProperties) {
        super(pProperties);
        this.chemical = pChemical;
        this.itemType = pChemicalItemType;
    }

    public ChemicalItem(ResourceLocation pResourceLocation, ChemicalItemType pChemicalItemType, Item.Properties pProperties) {
        this((Chemical) Objects.requireNonNull(BuiltInRegistries.ITEM.getValue(pResourceLocation)), pChemicalItemType, pProperties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {

        if (getChemical() instanceof Element element) {
            pTooltipComponents.add(MutableComponent.create(PlainTextContents.create(String.format("%s (%d)", getAbbreviation(), element.getAtomicNumber()))).withStyle(ChatFormatting.DARK_AQUA));
            pTooltipComponents.add(MutableComponent.create(PlainTextContents.create(element.getGroupName())).withStyle(ChatFormatting.GRAY));
        } else {
            pTooltipComponents.add(MutableComponent.create(PlainTextContents.create(getAbbreviation())).withStyle(ChatFormatting.DARK_AQUA));
        }
        pTooltipComponents.add(MutableComponent.create(
                PlainTextContents.create(StringUtils.capitalize(getNamespace()))).withStyle(ChemLib.MOD_ID_TEXT_STYLE));
    }

    public String getNamespace() {
        return BuiltInRegistries.ITEM.getResourceKey(this).get().location().getNamespace();
    }

    public Chemical getChemical() {
        return chemical;
    }

    public ChemicalItemType getItemType() {
        return itemType;
    }

    @Override
    public String getChemicalName() {
        return chemical.getChemicalName();
    }

    @Override
    public String getAbbreviation() {
        return chemical.getAbbreviation();
    }

    @Override
    public MatterState getMatterState() {
        return chemical.getMatterState();
    }

    @Override
    public String getChemicalDescription() {
        return "";
    }

    @Override
    public List<MobEffectInstance> getEffects() {
        return getChemical().getEffects();
    }

    @Override
    public int getColor() {
        return clampMinColorValue(chemical.getColor(), 0x44);
    }

    @SuppressWarnings("unused")
    public int getColor(ItemStack pItemStack, int pTintIndex) {
        // Force full opacity on the tint: clampMinColorValue produces a 24-bit RGB value (high byte 0),
        // and since 1.21 the item renderer honours the tint's alpha channel (FastColor.ARGB32.alpha),
        // so a 0 high byte renders the tinted layer fully transparent. ElementItem carries 0xFF via
        // parseColorHex for the same reason.
        return pTintIndex == 0 ? getColor() | 0xFF000000 : -1;
    }
}