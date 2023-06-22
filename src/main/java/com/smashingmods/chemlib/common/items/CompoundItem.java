package com.smashingmods.chemlib.common.items;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.api.Compound;
import com.smashingmods.chemlib.api.MatterState;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class CompoundItem extends Item implements Compound {

    private final String compoundName;
    private String abbreviation = "";
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
     */
    public CompoundItem(String pCompoundName, MatterState pMatterState, Map<String, Integer> pComponents, String pDescription, String pColor, List<MobEffectInstance> pEffects) {
        super(new Item.Properties());
        this.compoundName = pCompoundName;
        this.matterState = pMatterState;
        this.components = pComponents;
        this.description = pDescription;
        this.color = Integer.parseInt(pColor, 16) | 0xFF000000;
        this.effects = pEffects;
    }

    /**
     * Compound Item constructor with supplied creative mode tab
     * @param pCompoundName
     * @param pMatterState
     * @param pComponents
     * @param pDescription
     * @param pColor
     * @param pEffects
     * @param pTab
     */
    public CompoundItem(String pCompoundName, MatterState pMatterState, Map<String, Integer> pComponents, String pDescription, String pColor, List<MobEffectInstance> pEffects, CreativeModeTab pTab) {
        super(new Item.Properties());
        this.compoundName = pCompoundName;
        this.matterState = pMatterState;
        this.components = pComponents;
        this.description = pDescription;
        this.color = (int) Long.parseLong(pColor, 16);
        this.effects = pEffects;
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(MutableComponent.create(new LiteralContents(getAbbreviation())).withStyle(ChatFormatting.DARK_AQUA));
        pTooltipComponents.add(MutableComponent.create(
                new LiteralContents(StringUtils.capitalize(getNamespace()))).withStyle(ChemLib.MOD_ID_TEXT_STYLE));
    }

    public String getNamespace() {
        return ForgeRegistries.ITEMS.getResourceKey(this).get().location().getNamespace();
    }

    @Override
    public String getChemicalName() {
        return this.compoundName;
    }

    public String getAbbreviation() {
        if (abbreviation.isEmpty()) {
            abbreviation = buildAbbreviation();
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

    public String buildAbbreviation() {
        StringBuilder builder = new StringBuilder();

        for (String name : components.keySet()) {
            ItemRegistry.getElementByName(name).ifPresent(elementItem -> builder.append(elementItem.getAbbreviation()));
            ItemRegistry.getCompoundByName(name).ifPresent(compoundItem -> builder.append(String.format("(%s)", compoundItem.getAbbreviation())));

            Integer count = components.get(name);
            if (count > 1) {
                builder.append(getSubscript(Integer.toString(count)));
            }
        }
        return builder.toString();
    }
}