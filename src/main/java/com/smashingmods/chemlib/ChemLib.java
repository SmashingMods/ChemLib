package com.smashingmods.chemlib;

import com.smashingmods.chemlib.registry.Registry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ChemLib.MODID)
public class ChemLib {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "chemlib";

    public ChemLib() {
        MinecraftForge.EVENT_BUS.register(this);
        Registry.register();
    }
}