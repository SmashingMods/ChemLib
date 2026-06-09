package com.smashingmods.chemlib.common.items;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.api.Chemical;
import com.smashingmods.chemlib.api.Compound;
import com.smashingmods.chemlib.api.MatterState;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class CompoundItem extends Item implements Compound {

    private final String compoundName;
    private String abbreviation = "";
    private boolean buildingAbbreviation = false;
    private final MatterState matterState;
    private final Map<String, Integer> components;
    private final String description;
    private final int color;
    private final List<MobEffectInstance> effects;

    /**
     * Default Compound Item constructor
     * @param pCompoundName
     * @param pMatterState
     * @param pComponents
     * @param pDescription
     * @param pColor
     * @param pEffects
     * @param pProperties
     */
    public CompoundItem(String pCompoundName, MatterState pMatterState, Map<String, Integer> pComponents, String pDescription, String pColor, List<MobEffectInstance> pEffects, Item.Properties pProperties) {
        super(pProperties);
        this.compoundName = pCompoundName;
        this.matterState = pMatterState;
        this.components = pComponents;
        this.description = pDescription;
        this.color = Chemical.parseColorHex(pColor);
        this.effects = pEffects;
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(MutableComponent.create(PlainTextContents.create(getAbbreviation())).withStyle(ChatFormatting.DARK_AQUA));
        pTooltipComponents.add(MutableComponent.create(
                PlainTextContents.create(StringUtils.capitalize(getNamespace()))).withStyle(ChemLib.MOD_ID_TEXT_STYLE));
    }

    public String getNamespace() {
        return BuiltInRegistries.ITEM.getResourceKey(this).get().location().getNamespace();
    }

    @Override
    public String getChemicalName() {
        return this.compoundName;
    }

    public String getAbbreviation() {
        if (abbreviation.isEmpty() && !buildingAbbreviation) {
            // Guard against a cyclic compound definition (A referencing B referencing A): a component lookup
            // re-entering this compound while it is mid-build returns the empty value-so-far instead of recursing.
            buildingAbbreviation = true;
            try {
                abbreviation = buildAbbreviation();
            } finally {
                buildingAbbreviation = false;
            }
        }
        return abbreviation;
    }

    @Override
    public MatterState getMatterState() {
        return matterState;
    }

    @Override
    public String getChemicalDescription() {
        return description;
    }

    @Override
    public int getColor() {
        return this.color;
    }

    @Override
    public List<MobEffectInstance> getEffects() {
        return this.effects;
    }

    public Map<String, Integer> getComponents() {
        return this.components;
    }

    @SuppressWarnings("unused")
    public int getColor(ItemStack pItemStack, int pTintIndex) {
        return pTintIndex > 0 ? -1 : color;
    }

    public static String getSubscript(String pString) {
        //subscriptZeroCodepoint is subscript 0 unicode char, adding 1-9 gives the subscript for that num
        //i.e. ₀ + 3 = ₃
        final int subscriptZeroCodepoint = 0x2080;
        StringBuilder builder = new StringBuilder();
        for (char character : pString.toCharArray()) {
            builder.append(Character.toChars(subscriptZeroCodepoint + Character.getNumericValue(character)));
        }
        return builder.toString();
    }

    public static String composeFormula(Map<String, Integer> components, Function<String, Optional<String>> elementAbbrev, Function<String, Optional<String>> compoundAbbrev) {
        StringBuilder builder = new StringBuilder();

        for (String name : components.keySet()) {
            elementAbbrev.apply(name).ifPresent(builder::append);
            compoundAbbrev.apply(name).ifPresent(abbreviation -> builder.append(String.format("(%s)", abbreviation)));

            Integer count = components.get(name);
            if (count > 1) {
                builder.append(getSubscript(Integer.toString(count)));
            }
        }
        return builder.toString();
    }

    public String buildAbbreviation() {
        return composeFormula(components,
                name -> ItemRegistry.getElementByName(name).map(ElementItem::getAbbreviation),
                name -> ItemRegistry.getCompoundByName(name).map(CompoundItem::getAbbreviation));
    }
}