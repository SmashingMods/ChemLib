package com.smashingmods.chemlib.common.items;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.api.Chemical;
import com.smashingmods.chemlib.api.ChemicalItemType;
import com.smashingmods.chemlib.api.Element;
import com.smashingmods.chemlib.api.MatterState;
import com.smashingmods.chemlib.client.AbbreviationRenderer;
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
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class ChemicalItem extends Item implements Chemical {

    private final Chemical chemical;
    private final ChemicalItemType itemType;

    public ChemicalItem(Chemical pChemical, ChemicalItemType pChemicalItemType, Item.Properties pProperties) {
        super(pProperties);
        this.chemical = pChemical;
        this.itemType = pChemicalItemType;
    }

    public ChemicalItem(ResourceLocation pResourceLocation, ChemicalItemType pChemicalItemType, Item.Properties pProperties) {
        this((Chemical) Objects.requireNonNull(BuiltInRegistries.ITEM.get(pResourceLocation)), pChemicalItemType, pProperties);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {

        if (getChemical() instanceof Element element) {
            pTooltipComponents.add(MutableComponent.create(new PlainTextContents.LiteralContents(String.format("%s (%d)", getAbbreviation(), element.getAtomicNumber()))).withStyle(ChatFormatting.DARK_AQUA));
            pTooltipComponents.add(MutableComponent.create(new PlainTextContents.LiteralContents(element.getGroupName())).withStyle(ChatFormatting.GRAY));
        } else {
            pTooltipComponents.add(MutableComponent.create(new PlainTextContents.LiteralContents(getAbbreviation())).withStyle(ChatFormatting.DARK_AQUA));
            pTooltipComponents.add(MutableComponent.create(
                    new PlainTextContents.LiteralContents(StringUtils.capitalize(getNamespace()))).withStyle(ChemLib.MOD_ID_TEXT_STYLE));
        }
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
        return chemical.getColor();
    }

    @SuppressWarnings("unused")
    public int getColor(ItemStack pItemStack, int pTintIndex) {
        return pTintIndex == 0 ? getColor() : -1;
    }

    @Override
    public void initializeClient(@Nonnull Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(AbbreviationRenderer.RENDERER);
    }
}