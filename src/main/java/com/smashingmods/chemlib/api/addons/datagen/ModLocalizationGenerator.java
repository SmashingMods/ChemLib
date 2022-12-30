package com.smashingmods.chemlib.api.addons.datagen;

import com.smashingmods.chemlib.api.addons.registry.AddonRegisters;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.text.WordUtils;

public class ModLocalizationGenerator extends LanguageProvider {
    private final AddonRegisters addonRegisters;

    public ModLocalizationGenerator(DataGenerator gen, AddonRegisters pAddonRegisters, String locale) {
        super(gen, pAddonRegisters.getModID(), locale);
        this.addonRegisters = pAddonRegisters;
    }

    @Override
    protected void addTranslations() {
        addonRegisters.getCompounds().forEach(compound -> {
            add(String.format("item.%s.%s", addonRegisters.getModID(), compound.getChemicalName()), WordUtils.capitalize(compound.getChemicalName().replace("_", " ")));
            if (!compound.getChemicalDescription().isEmpty()) {
                add(String.format("%s.jei.compound.%s.description", addonRegisters.getModID(), compound.getChemicalName()), compound.getChemicalDescription());
            }
        });

        addonRegisters.getCompoundItemsAsStream().forEach(item -> {
            String name = item.getChemicalName();
            String itemType = item.getItemType().getSerializedName();
            add(String.format("item.%s.%s_%s", addonRegisters.getModID(), name, itemType), WordUtils.capitalize(String.format("%s %s", name.replace("_", " "), itemType)));
        });

        addonRegisters.FLUID_TYPES.getEntries().stream().map(RegistryObject::get).forEach(fluidType -> {
            int density = fluidType.getDensity();
            String key = fluidType.getDescriptionId();
            String value = key.split("\\.")[key.split("\\.").length - 1];
            String translation = WordUtils.capitalize(String.format("%s%s", value.replace("_", " "), density < 0 ? " gas" : ""));

            add(key, translation);
            add(String.format("item.%s.%s_bucket", addonRegisters.getModID(), value), translation + " Bucket");
        });
    }
}
