package com.smashingmods.chemlib.common.items;

import com.smashingmods.chemlib.api.Chemical;
import com.smashingmods.chemlib.api.Element;
import com.smashingmods.chemlib.api.MatterState;
import com.smashingmods.chemlib.common.blocks.ChemicalBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class ChemicalBlockItem extends BlockItem implements Chemical {

    private final ChemicalBlock block;

    public ChemicalBlockItem(ChemicalBlock pBlock, Properties pProperties) {
        super(pBlock, pProperties);
        this.block = pBlock;
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (getChemical() instanceof Element element) {
            pTooltipComponents.add(Component.literal(String.format("%s (%d)", getAbbreviation(), element.getAtomicNumber())).withStyle(ChatFormatting.DARK_AQUA));
            pTooltipComponents.add(Component.literal(element.getGroupName()).withStyle(ChatFormatting.GRAY));
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
        // ItemColors expects ARGB; alpha 0 would make the item render fully transparent
        return clampMinColorValue(getChemical().getColor(), 0x44) | 0xFF000000;
    }

    @SuppressWarnings("unused")
    public int getColor(ItemStack pItemStack, int pTintIndex) {
        return getColor();
    }
}
