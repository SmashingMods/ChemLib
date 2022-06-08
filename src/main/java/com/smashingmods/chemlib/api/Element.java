package com.smashingmods.chemlib.api;

public interface Element extends Chemical {

    int getAtomicNumber();
    int getGroup();
    int getPeriod();
    MetalType getMetalType();
    boolean isArtificial();
    String getGroupName();
}