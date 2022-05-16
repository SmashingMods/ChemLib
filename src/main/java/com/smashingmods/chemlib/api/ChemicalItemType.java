package com.smashingmods.chemlib.api;

import net.minecraft.util.StringRepresentable;

import javax.annotation.Nonnull;

public enum ChemicalItemType implements StringRepresentable {
    COMPOUND("dust"),
    DUST("dust"),
    NUGGET("nugget"),
    INGOT("ingot"),
    PLATE("plate");

    private final String type;

    ChemicalItemType(String pType) {
        this.type = pType;
    }

    @Override
    @Nonnull
    public String getSerializedName() {
        return type;
    }
}
