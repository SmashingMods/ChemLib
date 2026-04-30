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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class ChemicalItem extends Item implements Chemical {

    private final Chemical chemical;
    private final ChemicalItemType itemType;

    public ChemicalItem(Chemical chemical, ChemicalItemType chemicalItemType, Item.Properties properties) {
        super(properties);
        this.chemical = chemical;
        this.itemType = chemicalItemType;
    }

    public ChemicalItem(ResourceLocation resourceLocation, ChemicalItemType chemicalItemType, Item.Properties properties) {
        this((Chemical) Objects.requireNonNull(BuiltInRegistries.ITEM.get(resourceLocation)), chemicalItemType, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (getChemical() instanceof Element element) {
            tooltipComponents.add(Component.literal(String.format("%s (%d)", getAbbreviation(), element.getAtomicNumber())).withStyle(ChatFormatting.DARK_AQUA));
            tooltipComponents.add(Component.literal(element.getGroupName()).withStyle(ChatFormatting.GRAY));
        } else {
            tooltipComponents.add(Component.literal(getAbbreviation()).withStyle(ChatFormatting.DARK_AQUA));
            tooltipComponents.add(Component.literal(StringUtils.capitalize(getNamespace())).withStyle(ChemLib.MOD_ID_TEXT_STYLE));
        }
    }

    public String getNamespace() {
        return BuiltInRegistries.ITEM.getKey(this).getNamespace();
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

    public int getColor(ItemStack stack, int tintIndex) {
        return tintIndex == 0 ? FastColor.ARGB32.opaque(getColor()) : -1;
    }

    @SuppressWarnings("removal")
    @Override
    public void initializeClient(@Nonnull Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(AbbreviationRenderer.RENDERER);
    }
}