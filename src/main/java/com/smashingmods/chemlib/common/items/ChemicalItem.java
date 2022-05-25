package com.smashingmods.chemlib.common.items;

import com.smashingmods.chemlib.api.*;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ChemicalItem extends Item implements Chemical {

    private final Chemical chemical;
    private final ChemicalItemType itemType;

    public ChemicalItem(Chemical pChemical, ChemicalItemType pChemicalItemType, Item.Properties pProperties) {
        super(pProperties);
        this.chemical = pChemical;
        this.itemType = pChemicalItemType;
    }

    public ChemicalItem(ResourceLocation pResourceLocation, ChemicalItemType pChemicalItemType, Item.Properties pProperties) {
        this((Chemical) Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(pResourceLocation)), pChemicalItemType, pProperties);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {

        if (getChemical() instanceof Element element) {
            pTooltipComponents.add(new TextComponent(String.format("%s (%d)", getAbbreviation(), element.getAtomicNumber())).withStyle(ChatFormatting.DARK_AQUA));
            pTooltipComponents.add(new TextComponent(element.getGroupName()).withStyle(ChatFormatting.GRAY));
        } else {
            pTooltipComponents.add(new TextComponent(getAbbreviation()).withStyle(ChatFormatting.DARK_AQUA));
        }
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
    public int getColor() {
        return chemical.getColor();
    }

    public int getColor(ItemStack pItemStack, int pTintIndex) {
        return pTintIndex == 0 ? getColor() : -1;
    }
}