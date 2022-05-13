package com.smashingmods.chemlib.api;

public interface Element extends Chemical {

    int getAtomicNumber();
    MetalType getMetalType();
}