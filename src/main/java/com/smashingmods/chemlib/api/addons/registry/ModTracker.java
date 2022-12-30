package com.smashingmods.chemlib.api.addons.registry;

import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class ModTracker {
    public static CopyOnWriteArrayList<ResourceLocation> compounds = new CopyOnWriteArrayList<>();
    public static CopyOnWriteArrayList<AddonRegisters> addonRegistersList = new CopyOnWriteArrayList<>();

    /**
     * facilitates efficiency in tooltip creation
     */
    public static CopyOnWriteArrayList<String> modsList = new CopyOnWriteArrayList<>();

    public static boolean compoundExists(String pCompoundName) {
        for (ResourceLocation loc : compounds) {
            if (loc.getPath().equals(pCompoundName)) {
                return true;
            }
        }
        return false;
    }

    public static void addCompound(ResourceLocation pCompound) {
        if (!compoundExists(pCompound.getPath())) {
            compounds.add(pCompound);
        }
    }

    public static boolean addModRegisters(AddonRegisters addOnRegisters) {
        if (addonRegistersList.stream().anyMatch(register -> Objects.equals(register.getModID(), addOnRegisters.getModID()))) {
            return false;
        }
        modsList.add(addOnRegisters.getModID());
        addonRegistersList.add(addOnRegisters);
        return true;
    }
}