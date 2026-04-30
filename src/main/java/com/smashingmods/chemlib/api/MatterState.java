package com.smashingmods.chemlib.api;

import net.minecraft.util.StringRepresentable;

public enum MatterState implements StringRepresentable {
    SOLID("solid"),
    LIQUID("liquid"),
    GAS("gas");

    private final String state;

    MatterState(String state) {
        this.state = state;
    }

    @Override
    public String getSerializedName() {
        return state;
    }
}
