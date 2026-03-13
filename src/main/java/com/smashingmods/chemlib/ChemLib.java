package com.smashingmods.chemlib;

import com.smashingmods.chemlib.registry.Registry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ChemLib.MODID)
public class ChemLib {
    public static final String MODID = "chemlib";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Style MOD_ID_TEXT_STYLE = Style.EMPTY.withFont(Style.DEFAULT_FONT).withItalic(true).withColor(ChatFormatting.BLUE);

    public ChemLib(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::onCommonSetup);

        NeoForge.EVENT_BUS.register(this);

        Registry.register(modEventBus);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC);
    }

    private void onCommonSetup(final net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent event) {
        // No-op for now; placeholder for future common setup if needed.
    }

    @net.neoforged.bus.api.SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Placeholder for potential server start logic under NeoForge.
    }
}
