package com.smashingmods.chemlib.datagen;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.api.Chemical;
import com.smashingmods.chemlib.api.ChemicalBlockType;
import com.smashingmods.chemlib.common.registry.BlockRegistry;
import com.smashingmods.chemlib.common.registry.FluidRegistry;
import com.smashingmods.chemlib.common.registry.ItemRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.BucketItem;
import net.minecraftforge.common.data.LanguageProvider;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.util.stream.Collectors;

public class LocalizationGenerator extends LanguageProvider {

    public LocalizationGenerator(DataGenerator gen, String locale) {
        super(gen, ChemLib.MODID, locale);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void addTranslations() {

        ItemRegistry.getElements().forEach(element -> {
            add(String.format("item.chemlib.%s", element.getChemicalName()), StringUtils.capitalize(element.getChemicalName()));
            if (!element.getChemicalDescription().isEmpty()) {
                add(String.format("%s.jei.element.%s.description", ChemLib.MODID, element.getChemicalName()), element.getChemicalDescription());
            }
        });

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

        FluidRegistry.getLiquidBlocks().forEach(liquidBlock -> {
            String name = liquidBlock.getFluid().getRegistryName().getPath().replace("_source", "").replace("_", " ");
            int density = liquidBlock.getFluid().getAttributes().getDensity();
            add(String.format("fluid.chemlib.%s_source", name), WordUtils.capitalize(String.format("%s%s", name, density < 0 ? " gas" : "")));
        });

        FluidRegistry.getBuckets().forEach(bucket -> {
            String name = bucket.getRegistryName().getPath();
            add(String.format("item.chemlib.%s", name), WordUtils.capitalize(name.replace("_", " ")));
        });

        add("item.chemlib.periodic_table", "Periodic Table of the Elements");
        add("item.chemlib.periodic_table.tooltip", "Use this to see a full periodic table.");
        
        add("itemGroup.chemlib.elements", "Elements");
        add("itemGroup.chemlib.compounds", "Compounds");
        add("itemGroup.chemlib.metals", "Metals");
        add("itemGroup.chemlib.misc", "Misc Items");

        add("chemlib.screen.periodic_table", "Periodic Table of the Elements");
    }
}
