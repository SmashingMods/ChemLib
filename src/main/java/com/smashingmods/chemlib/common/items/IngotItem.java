package com.smashingmods.chemlib.common.items;

import com.smashingmods.chemlib.api.Element;
import com.smashingmods.chemlib.api.Ingot;
import com.smashingmods.chemlib.api.MatterState;
import com.smashingmods.chemlib.common.registry.ItemRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class IngotItem extends Item implements Ingot {

    private final Element element;
    private final String name;
    private final int color;

    public IngotItem(Element pElement) {
        super(new Item.Properties().tab(ItemRegistry.CHEMISTRY_TAB));
        this.element = pElement;
        this.name = pElement.getName();
        this.color = pElement.getColor();
        ItemRegistry.INGOTS.add(this);
    }

    @Override
    public Element getElement() {
        return this.element;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getAbbreviation() {
        return null;
    }

    @Override
    public MatterState getMatterState() {
        return MatterState.SOLID;
    }

    @Override
    public int getColor() {
        return color;
    }

    public int getColor(ItemStack pStack, int pTintColor) {
        return color;
    }
}