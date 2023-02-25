package com.smashingmods.chemlib.api.addons.registry;

import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class ModTracker {
    public static CopyOnWriteArrayList<ResourceLocation> compounds = new CopyOnWriteArrayList<>();
    public static CopyOnWriteArrayList<AddonRegistry> addonRegistryList = new CopyOnWriteArrayList<>();

    public static boolean ChemlibRegistered = false;

    /**
     * Facilitates efficiency in tooltip creation
     */
    public static CopyOnWriteArrayList<String> addonList = new CopyOnWriteArrayList<>();

    public static boolean compoundNotExist(String pCompoundName) {
        for (ResourceLocation resourceLocation : compounds) {
            return !resourceLocation.getPath().equals(pCompoundName);
        }
        return true;
    }

    public static void addCompound(ResourceLocation pCompound) {
        if (compoundNotExist(pCompound.getPath())) {
            compounds.add(pCompound);
        }
    }

    public static boolean addonRegistered (AddonRegistry pAddonRegistry) {
        return addonRegistryList.stream().anyMatch(addonRegistry -> Objects.equals(addonRegistry.getModID(), pAddonRegistry.getModID()));
    }

    public static void addModRegistries(AddonRegistry pAddonRegistry) {
        if (!addonRegistered(pAddonRegistry)) {
            addonList.add(pAddonRegistry.getModID());
            addonRegistryList.add(pAddonRegistry);
        } else {
            throw new RuntimeException("Mod ID already used to create and register chemical compounds");
        }
    }
}