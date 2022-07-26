package com.smashingmods.chemlib;

import com.smashingmods.chemlib.registry.Registry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod(ChemLib.MODID)
public class ChemLib {
    public static final String MODID = "chemlib";

    public ChemLib() {
        MinecraftForge.EVENT_BUS.register(this);
        Registry.register();
    }
}