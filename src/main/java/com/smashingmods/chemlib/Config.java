package com.smashingmods.chemlib;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.fml.event.config.ModConfigEvent;

@EventBusSubscriber(modid = ChemLib.MODID)
public class Config {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final Common COMMON = new Common(BUILDER);
    public static final ModConfigSpec COMMON_SPEC = BUILDER.build();

    public static class Common {

        private static final String categoryRendering = "Rendering Options";

        public static BooleanValue renderElementAbbreviations;
        public static BooleanValue renderDustAbbreviations;
        public static BooleanValue renderNuggetAbbreviations;
        public static BooleanValue renderIngotAbbreviations;
        public static BooleanValue renderPlateAbbreviations;

        public Common(ModConfigSpec.Builder builder) {

            builder.comment("""
                    
                        These options are for rendering element abbreviations on different items in your inventory
                        Disable rendering abbreviations on a per item type basis.
                    """)
                    .push(categoryRendering);

            renderElementAbbreviations = builder.define("element abbreviations", true);
            renderDustAbbreviations = builder.define("dust abbreviations", false);
            renderNuggetAbbreviations = builder.define("nugget abbreviations", false);
            renderIngotAbbreviations = builder.define("ingot abbreviations", false);
            renderPlateAbbreviations = builder.define("plate abbreviations", false);

            builder.pop();
        }
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        // Config loaded - values are now available via COMMON.*
    }
}
