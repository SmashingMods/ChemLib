package com.smashingmods.chemlib;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.file.Path;

public class Config {

    public static final Common COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON = specPair.getLeft();
        COMMON_SPEC = specPair.getRight();
    }

    public static class Common {

        private static final String categoryRendering = "Rendering Options";

        public static BooleanValue renderElementAbbreviations;
        public static BooleanValue renderDustAbbreviations;
        public static BooleanValue renderNuggetAbbreviations;
        public static BooleanValue renderIngotAbbreviations;
        public static BooleanValue renderPlateAbbreviations;

        public Common(ForgeConfigSpec.Builder builder) {

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

    public static void loadConfig(ForgeConfigSpec spec, Path path) {
        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();
        configData.load();
        spec.setConfig(configData);
    }
}
