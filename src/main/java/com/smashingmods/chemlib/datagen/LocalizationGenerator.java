package com.smashingmods.chemlib.datagen;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.api.ChemicalBlockType;
import com.smashingmods.chemlib.registry.BlockRegistry;
import com.smashingmods.chemlib.registry.FluidRegistry;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

public class LocalizationGenerator extends LanguageProvider {

    public LocalizationGenerator(PackOutput pOutput, String locale) {
        super(pOutput, ChemLib.MODID, locale);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void addTranslations() {

        ItemRegistry.getElements().forEach(element -> add(String.format("item.chemlib.%s", element.getChemicalName()), StringUtils.capitalize(element.getChemicalName())));

        ItemRegistry.getCompounds().forEach(compound -> {
            add(String.format("item.chemlib.%s", compound.getChemicalName()), WordUtils.capitalize(compound.getChemicalName().replace("_", " ")));
            if (!compound.getChemicalDescription().isEmpty()) {
                add(String.format("%s.jei.compound.%s.description", ChemLib.MODID, compound.getChemicalName()), compound.getChemicalDescription());
            }
        });

        ItemRegistry.getChemicalItems().forEach(item -> {
            String name = item.getChemicalName();
            String itemType = item.getItemType().getSerializedName();
            add(String.format("item.chemlib.%s_%s", name, itemType), WordUtils.capitalize(String.format("%s %s", name.replace("_", " "), itemType)));
        });

        for (ChemicalBlockType type : ChemicalBlockType.values()) {
            BlockRegistry.getChemicalBlocksStreamByType(type).forEach(block -> {
                String name = block.getChemicalName();
                String displayType = type.equals(ChemicalBlockType.METAL) ? "block" : "lamp";
                add(String.format("block.chemlib.%s_%s_block", name, type.getSerializedName()), WordUtils.capitalize(String.format("%s %s", name, displayType)));
            });
        }

        FluidRegistry.FLUID_TYPES.getEntries().stream().map(RegistryObject::get).forEach(fluidType -> {
            int density = fluidType.getDensity();
            String key = fluidType.getDescriptionId();
            String value = key.split("\\.")[key.split("\\.").length - 1];
            String translation = WordUtils.capitalize(String.format("%s%s", value.replace("_", " "), density < 0 ? " gas" : ""));

            // translation for the fluid
            add(key, translation);
            // translation for the bucket
            add(String.format("item.chemlib.%s_bucket", value), translation + " Bucket");
        });

        add("item.chemlib.periodic_table", "Periodic Table of the Elements");
        add("item.chemlib.periodic_table.tooltip", "Use this to see a full periodic table.");

        add("itemGroup.chemlib.elements", "Elements");
        add("itemGroup.chemlib.compounds", "Compounds");
        add("itemGroup.chemlib.metals", "Metals");
        add("itemGroup.chemlib.misc", "Misc Items");
        add("chemlib.effect.on_hit", "Effects on Hit");

        add("chemlib.screen.periodic_table", "Periodic Table of the Elements");

        add("chemlib.jei.element.description", "Use the Periodic Table of the Elements to learn more about this element.");
    }
}
