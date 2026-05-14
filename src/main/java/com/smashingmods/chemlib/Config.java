package com.smashingmods.chemlib;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import org.apache.commons.lang3.tuple.Pair;

public class Config {

    public static final Common COMMON;
    public static final ModConfigSpec COMMON_SPEC;

    static {
        final Pair<Common, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Common::new);
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
}
