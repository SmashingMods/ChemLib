package com.smashingmods.chemlib.api;

import net.minecraft.util.StringRepresentable;

import javax.annotation.Nonnull;

public enum MetalType implements StringRepresentable {
    METAL("metal"),
    METALLOID("metalloid"),
    NONMETAL("nonmetal");

    private final String state;

    MetalType(String pState) {
        this.state = pState;
    }

    @Override
    @Nonnull
    public String getSerializedName() {
        return state;
    }
}
