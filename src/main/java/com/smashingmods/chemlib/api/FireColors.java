package com.smashingmods.chemlib.api;

import net.minecraft.util.StringRepresentable;

import javax.annotation.Nonnull;

public enum FireColors implements StringRepresentable {
    RED("red"),
    ORANGE("orange"),
    YELLOW("yellow"),
    GREEN("green"),
    BLUE("blue"),
    PURPLE("purple"),
    WHITE("white"),
    INFRARED("infrared");

    private final String color;

    FireColors(String pColor) {
        this.color = pColor;
    }

    @Override
    @Nonnull
    public String getSerializedName() {
        return color;
    }
}
