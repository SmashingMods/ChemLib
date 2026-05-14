package com.smashingmods.chemlib.api;

import net.minecraft.util.StringRepresentable;

public enum MetalType implements StringRepresentable {
    METAL("metal"),
    METALLOID("metalloid"),
    NONMETAL("nonmetal");

    private final String state;

    MetalType(String state) {
        this.state = state;
    }

    @Override
    public String getSerializedName() {
        return state;
    }
}
