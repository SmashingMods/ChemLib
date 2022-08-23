package com.smashingmods.chemlib;

import com.smashingmods.chemlib.registry.Registry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(ChemLib.MODID)
public class ChemLib {
    public static final String MODID = "chemlib";

    public ChemLib() {
        MinecraftForge.EVENT_BUS.register(this);
        Registry.register();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC);
        Config.loadConfig(Config.COMMON_SPEC, FMLPaths.CONFIGDIR.get().resolve("chemlib-common.toml"));
    }
}