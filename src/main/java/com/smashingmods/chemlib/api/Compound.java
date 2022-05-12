package com.smashingmods.chemlib.api;
import java.util.Map;

public interface Compound extends Chemical {

    Map<String, Integer> getComponents();
}
