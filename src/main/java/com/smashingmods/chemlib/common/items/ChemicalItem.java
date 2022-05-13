package com.smashingmods.chemlib.common.items;

import com.smashingmods.chemlib.api.Chemical;
import com.smashingmods.chemlib.api.MatterState;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ChemicalItem extends Item implements Chemical {

    private final Chemical chemical;

    public ChemicalItem(Chemical pChemical, Item.Properties pProperties, List<ChemicalItem> pList) {
        super(pProperties);
        this.chemical = pChemical;
        pList.add(this);
    }

    @Override
    public String getName() {
        return chemical.getName();
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
    public int getColor() {
        return chemical.getColor();
    }

    @Override
    public int getColor(ItemStack pItemStack, int pTintIndex) {
        return getColor();
    }
}