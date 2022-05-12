package com.smashingmods.chemlib.common.items;

import com.smashingmods.chemlib.api.Element;
import com.smashingmods.chemlib.api.MatterState;
import com.smashingmods.chemlib.common.registry.ItemRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ElementItem extends Item implements Element {

    private final String elementName;
    private final int atomicNumber;
    private final String abbreviation;
    private final MatterState matterState;
    private final int color;

    public ElementItem(String pChemicalName, int pAtomicNumber, String pAbbreviation, MatterState pMatterState, String pColor) {
        super(new Item.Properties().tab(ItemRegistry.CHEMISTRY_TAB));
        this.elementName = pChemicalName;
        this.atomicNumber = pAtomicNumber;
        this.abbreviation = pAbbreviation;
        this.matterState = pMatterState;
        this.color = (int) Long.parseLong(pColor, 16);

        ItemRegistry.ELEMENTS.add(this);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltips, TooltipFlag flag) {
        tooltips.add(new TextComponent(getAbbreviation()).withStyle(ChatFormatting.DARK_AQUA));
        tooltips.add(new TextComponent(String.format("(%d)", atomicNumber)));
    }

    @Override
    public String getName() {
        return this.elementName;
    }

    @Override
    public int getAtomicNumber() {
        return this.atomicNumber;
    }

    @Override
    public String getAbbreviation() {
        return this.abbreviation;
    }

    @Override
    public MatterState getMatterState() {
        return matterState;
    }

    @Override
    public int getColor() {
        return this.color;
    }

    public int getColor(ItemStack pItemStack, int pTintIndex) {
        return pTintIndex > 0 ? -1 : color;
    }
}