package com.smashingmods.chemlib.common.items;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.api.Element;
import com.smashingmods.chemlib.api.MatterState;
import com.smashingmods.chemlib.api.MetalType;
import com.smashingmods.chemlib.client.AbbreviationRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Consumer;

public class ElementItem extends Item implements Element {

    private final String elementName;
    private final int atomicNumber;
    private final String abbreviation;
    private final int group;
    private final int period;
    private final MatterState matterState;
    private final MetalType metalType;
    private final boolean artificial;
    private final int color;
    private final List<MobEffectInstance> effects;

    public ElementItem(String pChemicalName, int pAtomicNumber, String pAbbreviation, int pGroup, int pPeriod, MatterState pMatterState, MetalType pMetalType, boolean pArtificial, String pColor, List<MobEffectInstance> pEffects) {
        super(new Item.Properties());
        this.elementName = pChemicalName;
        this.atomicNumber = pAtomicNumber;
        this.abbreviation = pAbbreviation;
        this.group = pGroup;
        this.period = pPeriod;
        this.matterState = pMatterState;
        this.metalType = pMetalType;
        this.artificial = pArtificial;
        this.color = Integer.parseInt(pColor, 16) | 0xFF000000;
        this.effects = pEffects;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.literal(String.format("%s (%d)", getAbbreviation(), atomicNumber)).withStyle(ChatFormatting.DARK_AQUA));
        String groupKey = getGroupKey();
        if (!groupKey.isEmpty()) {
            pTooltipComponents.add(Component.translatable(groupKey).withStyle(ChatFormatting.GRAY));
        }
        pTooltipComponents.add(Component.literal(StringUtils.capitalize(getNamespace())).withStyle(ChemLib.MOD_ID_TEXT_STYLE));
    }

    public String getNamespace() {
        return BuiltInRegistries.ITEM.getResourceKey(this).get().location().getNamespace();
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

    public String getGroupKey() {
        return switch(atomicNumber) {
            case 1, 6, 7, 8, 15, 16, 34 -> "chemlib.group.reactive_non_metals";
            case 3, 11, 19, 37, 55, 87 -> "chemlib.group.alkali_metals";
            case 4, 12, 20, 38, 56, 88 -> "chemlib.group.alkaline_earth_metals";
            case 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 72, 73, 74, 75, 76, 77, 78, 79, 80, 104, 105, 106, 107, 108 -> "chemlib.group.transition_metals";
            case 13, 31, 49, 50, 81, 82, 83, 84 -> "chemlib.group.post_transition_metals";
            case 109, 110, 111, 112, 113, 114, 115, 116, 117, 118 -> "chemlib.group.unknown_properties";
            case 5, 14, 32, 33, 51, 52 -> "chemlib.group.metalloids";
            case 9, 17, 35, 53, 85 -> "chemlib.group.halogens";
            case 2, 10, 18, 36, 54, 86 -> "chemlib.group.noble_gases";
            case 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71 -> "chemlib.group.lanthanides";
            case 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103 -> "chemlib.group.actinides";
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
    public boolean isArtificial() {
        return artificial;
    }

    @Override
    public String getChemicalDescription() {
        return "";
    }

    @Override
    public List<MobEffectInstance> getEffects() {
        return this.effects;
    }

    @Override
    public int getColor() {
        return this.color;
    }

    @SuppressWarnings("unused")
    public int getColor(ItemStack pItemStack, int pTintIndex) {
        return pTintIndex > 0 ? -1 : color;
    }

    @Override
    public void initializeClient(@Nonnull Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(AbbreviationRenderer.RENDERER);
    }
}
