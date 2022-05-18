package com.smashingmods.chemlib.common.items;

import com.smashingmods.chemlib.api.Element;
import com.smashingmods.chemlib.api.MatterState;
import com.smashingmods.chemlib.api.MetalType;
import com.smashingmods.chemlib.client.ElementRenderer;
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
import net.minecraftforge.client.IItemRenderProperties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Consumer;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ElementItem extends Item implements Element {

    private final String elementName;
    private final int atomicNumber;
    private final String abbreviation;
    private final int group;
    private final int period;
    private final MatterState matterState;
    private final MetalType metalType;
    private final String description;
    private final int color;

    public ElementItem(String pChemicalName, int pAtomicNumber, String pAbbreviation, int pGroup, int pPeriod, MatterState pMatterState, MetalType pMetalType, String pDescription, String pColor) {
        super(new Item.Properties().tab(ItemRegistry.ELEMENT_TAB));
        this.elementName = pChemicalName;
        this.atomicNumber = pAtomicNumber;
        this.abbreviation = pAbbreviation;
        this.group = pGroup;
        this.period = pPeriod;
        this.matterState = pMatterState;
        this.metalType = pMetalType;
        this.description = pDescription;
        this.color = (int) Long.parseLong(pColor, 16);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(new TextComponent(String.format("%s (%d)", getAbbreviation(), atomicNumber)).withStyle(ChatFormatting.DARK_AQUA));
        if (!getGroupName().isEmpty()) {
            pTooltipComponents.add(new TextComponent(getGroupName()).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public String getChemicalName() {
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
    public int getPeriod() {
        return period;
    }

    @Override
    public int getGroup() {
        return group;
    }

    public String getGroupName() {
        return switch(group) {
            case 1 -> atomicNumber != 1 ? "Alkali Metal" : "";
            case 2 -> "Aklaline Earth Metal";
            case 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 -> switch(period) {
                case 6 -> "Lanthanide";
                case 7 -> "Actinide";
                default -> "Transition Metal";
            };
            case 13 -> "Boron Group";
            case 14 -> "Carbon Group";
            case 15 -> "Nitrogen Group";
            case 16 -> "Chalcogen";
            case 17 -> "Halogen";
            case 18 -> "Noble Gas";
            default -> "";
        };
    }

    @Override
    public MatterState getMatterState() {
        return matterState;
    }

    @Override
    public MetalType getMetalType() {
        return metalType;
    }

    @Override
    public String getChemicalDescription() {
        return description;
    }

    @Override
    public int getColor() {
        return this.color;
    }

    public int getColor(ItemStack pItemStack, int pTintIndex) {
        return pTintIndex > 0 ? -1 : color;
    }

	@Override
	public void initializeClient(@Nonnull Consumer<IItemRenderProperties> consumer) {
		super.initializeClient(consumer);
		consumer.accept(ElementRenderer.RENDERER);
	}
}