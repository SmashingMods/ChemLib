package com.smashingmods.chemlib;

import com.mojang.logging.LogUtils;
import com.smashingmods.chemlib.registry.Registry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(ChemLib.MODID)
public class ChemLib {
    public static final String MODID = "chemlib";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Style MOD_ID_TEXT_STYLE = Style.EMPTY.withFont(Style.DEFAULT_FONT).withItalic(true).withColor(ChatFormatting.BLUE);

    public ChemLib(IEventBus modEventBus, ModContainer modContainer) {
        Registry.register(modEventBus);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC);
    }
}