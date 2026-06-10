package com.smashingmods.chemlib.common.items;

import com.smashingmods.chemlib.api.Chemical;
import com.smashingmods.chemlib.api.Element;
import com.smashingmods.chemlib.api.MatterState;
import com.smashingmods.chemlib.common.blocks.ChemicalBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.List;
import java.util.function.Consumer;

public class ChemicalBlockItem extends BlockItem implements Chemical {

    private final ChemicalBlock block;

    public ChemicalBlockItem(ChemicalBlock pBlock, Properties pProperties) {
        super(pBlock, pProperties);
        this.block = pBlock;
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, TooltipDisplay pTooltipDisplay, Consumer<Component> pTooltipAdder, TooltipFlag pIsAdvanced) {
        if (getChemical() instanceof Element element) {
            pTooltipAdder.accept(MutableComponent.create(PlainTextContents.create(String.format("%s (%d)", getAbbreviation(), element.getAtomicNumber()))).withStyle(ChatFormatting.DARK_AQUA));
            pTooltipAdder.accept(MutableComponent.create(PlainTextContents.create(element.getGroupName())).withStyle(ChatFormatting.GRAY));
        }
    }

    public Chemical getChemical() {
        return block.getChemical();
    }

    @Override
    public String getChemicalName() {
        return block.getChemicalName();
    }

    @Override
    public String getAbbreviation() {
        return getChemical().getAbbreviation();
    }

    @Override
    public MatterState getMatterState() {
        return getChemical().getMatterState();
    }

    @Override
    public String getChemicalDescription() {
        return getChemical().getChemicalDescription();
    }

    @Override
    public List<MobEffectInstance> getEffects() {
        return getChemical().getEffects();
    }

    @Override
    public int getColor() {
        return clampMinColorValue(getChemical().getColor(), 0x44);
    }

    @SuppressWarnings("unused")
    public int getColor(ItemStack pItemStack, int pTintIndex) {
        // Force full opacity on the tint: clampMinColorValue produces a 24-bit RGB value (high byte 0),
        // and since 1.21 the item renderer honours the tint's alpha channel (FastColor.ARGB32.alpha),
        // so a 0 high byte renders the tinted block-item layer fully transparent. ElementItem carries
        // 0xFF via parseColorHex for the same reason. (Placed blocks use the opaque getBlockColor path.)
        return pTintIndex == 0 ? getColor() | 0xFF000000 : -1;
    }
}
