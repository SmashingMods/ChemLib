package com.smashingmods.chemlib.api;

import net.minecraft.util.StringRepresentable;

import javax.annotation.Nonnull;

public enum MatterState implements StringRepresentable {
    SOLID("solid"),
    LIQUID("liquid"),
    GAS("gas");

    private final String state;

    MatterState(String pState) {
        this.state = pState;
    }

    @Override
    @Nonnull
    public String getSerializedName() {
        return this.state;
    }
}
