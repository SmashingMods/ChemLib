package com.smashingmods.chemlib.api.addons.datagen;

import com.smashingmods.chemlib.api.addons.registry.AddonRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.text.WordUtils;

@SuppressWarnings("deprecation")
public class ModLocalizationGenerator extends LanguageProvider {
    private final AddonRegistry addonRegistry;

    public ModLocalizationGenerator(DataGenerator gen, AddonRegistry pAddonRegistry, String locale) {
        super(gen, pAddonRegistry.getModID(), locale);
        this.addonRegistry = pAddonRegistry;
    }

    @Override
    protected void addTranslations() {
        addonRegistry.getCompounds().forEach(compound -> {
            add(String.format("item.%s.%s", addonRegistry.getModID(), compound.getChemicalName()), WordUtils.capitalize(compound.getChemicalName().replace("_", " ")));
            if (!compound.getChemicalDescription().isEmpty()) {
                add(String.format("%s.jei.compound.%s.description", addonRegistry.getModID(), compound.getChemicalName()), compound.getChemicalDescription());
            }
        });

        addonRegistry.getCompoundItemsAsStream().forEach(item -> {
            String name = item.getChemicalName();
            String itemType = item.getItemType().getSerializedName();
            add(String.format("item.%s.%s_%s", addonRegistry.getModID(), name, itemType), WordUtils.capitalize(String.format("%s %s", name.replace("_", " "), itemType)));
        });

        addonRegistry.FLUID_TYPES.getEntries().stream().map(RegistryObject::get).forEach(fluidType -> {
            int density = fluidType.getDensity();
            String key = fluidType.getDescriptionId();
            String value = key.split("\\.")[key.split("\\.").length - 1];
            String translation = WordUtils.capitalize(String.format("%s%s", value.replace("_", " "), density < 0 ? " gas" : ""));
            add(key, translation);
            add(String.format("item.%s.%s_bucket", addonRegistry.getModID(), value), translation + " Bucket");
        });

        if (!addonRegistry.usedCustomCompoundsTab()) {
            String rawName = addonRegistry.getCompoundsTab().getDisplayName().getString();
            if (rawName.startsWith("itemGroup.")) {
                add(rawName, String.format("%s Compounds", WordUtils.capitalize(addonRegistry.getModID())));
            }
        }

        if (!addonRegistry.usedCustomCompoundsTab()) {
            String rawName = addonRegistry.getBucketsTab().getDisplayName().getString();
            if (rawName.startsWith("itemGroup.")) {
                add(rawName, String.format("%s Fluids", WordUtils.capitalize(addonRegistry.getModID())));
            }
        }
    }
}
