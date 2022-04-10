package com.smashingmods.chemlib.items;

import com.smashingmods.chemlib.ChemLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class BaseItem extends Item {

    public BaseItem(String name, Properties properties) {
        super(properties.tab(ChemLib.ITEM_GROUP));
        setRegistryName(new ResourceLocation(ChemLib.MODID, name));
        ModItems.items.add(this);
    }
}