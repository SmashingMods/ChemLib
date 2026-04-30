package com.smashingmods.chemlib.common.items;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.api.Compound;
import com.smashingmods.chemlib.api.MatterState;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.apache.commons.lang3.StringUtils;

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
     * @param compoundName
     * @param matterState
     * @param components
     * @param description
     * @param color
     * @param effects
     */
    public CompoundItem(String compoundName, MatterState matterState, Map<String, Integer> components, String description, String color, List<MobEffectInstance> effects) {
        super(new Item.Properties());
        this.compoundName = compoundName;
        this.matterState = matterState;
        this.components = components;
        this.description = description;
        this.color = Integer.parseInt(color, 16) | 0xFF000000;
        this.effects = effects;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal(getAbbreviation()).withStyle(ChatFormatting.DARK_AQUA));
        tooltipComponents.add(Component.literal(StringUtils.capitalize(getNamespace())).withStyle(ChemLib.MOD_ID_TEXT_STYLE));
    }

    public String getNamespace() {
        return BuiltInRegistries.ITEM.getKey(this).getNamespace();
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
    public int getColor(ItemStack stack, int tintIndex) {
        return tintIndex > 0 ? -1 : color;
    }

    public static String getSubscript(String string) {
        //subscriptZeroCodepoint is subscript 0 unicode char, adding 1-9 gives the subscript for that num
        //i.e. ₀ + 3 = ₃
        final int subscriptZeroCodepoint = 0x2080;
        StringBuilder builder = new StringBuilder();
        for (char character : string.toCharArray()) {
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