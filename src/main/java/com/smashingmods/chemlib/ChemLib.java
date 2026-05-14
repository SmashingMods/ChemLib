package com.smashingmods.chemlib;

import com.smashingmods.chemlib.registry.Registry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ChemLib.MODID)
public class ChemLib {
    public static final String MODID = "chemlib";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Style MOD_ID_TEXT_STYLE = Style.EMPTY.withItalic(true).withColor(ChatFormatting.BLUE);

    public ChemLib(IEventBus modEventBus, ModContainer modContainer) {
        Registry.register(modEventBus);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC);
    }

    public static String getModDisplayName(String modid) {
        return ModList.get().getModContainerById(modid)
                .map(c -> c.getModInfo().getDisplayName())
                .orElse(StringUtils.capitalize(modid));
    }
}
