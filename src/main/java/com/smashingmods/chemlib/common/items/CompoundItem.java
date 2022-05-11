package com.smashingmods.chemlib.common.items;

import com.smashingmods.chemlib.api.Compound;
import com.smashingmods.chemlib.api.MatterState;
import com.smashingmods.chemlib.common.registry.ItemRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CompoundItem extends Item implements Compound {

    private final String compoundName;
    private final String abbreviation;
    private final MatterState matterState;
    private final List<ItemStack> components;
    private final int color;

    public CompoundItem(String pCompoundName, MatterState pMatterState, List<ItemStack> pComponents, String pColor) {
        super(new Item.Properties().tab(ItemRegistry.CHEMISTRY_TAB));
        this.compoundName = pCompoundName;
        this.abbreviation = buildAbbreviation(pComponents);
        this.matterState = pMatterState;
        this.components = pComponents;
        this.color = (int) Long.parseLong(pColor, 16);

        ItemRegistry.COMPOUNDS.add(this);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(new TextComponent(getAbbreviation()).withStyle(ChatFormatting.DARK_AQUA));
    }

    @Override
    public String getName() {
        return this.compoundName;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    @Override
    public MatterState getMatterState() {
        return matterState;
    }

    @Override
    public int getColor() {
        return this.color;
    }

    public List<ItemStack> getComponents() {
        return this.components;
    }

    @Override
    public int getColor(ItemStack pItemStack, int pTintIndex) {
        return pTintIndex > 0 ? -1 : color;
    }

    public static String getSubscript(String pString) {
        //subscriptZeroCodepoint is subscript 0 unicode char, adding 1-9 gives the subscript for that num
        //i.e. ₀ + 3 = ₃
        final int subscriptZeroCodepoint = 0x2080;//Character.codePointAt("₀",0) + Character.codePointAt("₀",1);//Character.codePointAt("₀", 0);
        StringBuilder builder = new StringBuilder();
        for (char character : pString.toCharArray()) {
            builder.append(Character.toChars(subscriptZeroCodepoint + Character.getNumericValue(character)));
        }
        return builder.toString();
    }

    public static String buildAbbreviation(List<ItemStack> components) {
        StringBuilder builder = new StringBuilder();

        for (ItemStack component : components) {

            if (component.getItem() instanceof ElementItem elementItem) {
                builder.append(elementItem.getAbbreviation());
            } else if (component.getItem() instanceof CompoundItem compoundItem) {
                builder.append(String.format("(%s)", compoundItem.getAbbreviation()));
            }

            if (component.getCount() > 1) {
                builder.append(getSubscript(Integer.toString(component.getCount())));
            }
        }
        return builder.toString();
    }
}