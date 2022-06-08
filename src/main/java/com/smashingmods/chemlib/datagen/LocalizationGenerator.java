package com.smashingmods.chemlib.datagen;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.api.ChemicalBlockType;
import com.smashingmods.chemlib.api.MatterState;
import com.smashingmods.chemlib.common.items.CompoundItem;
import com.smashingmods.chemlib.common.items.ElementItem;
import com.smashingmods.chemlib.registry.BlockRegistry;
import com.smashingmods.chemlib.registry.FluidRegistry;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

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
            Objects.requireNonNull(liquidBlock.getFluid().getRegistryName());
            String name = liquidBlock.getFluid().getRegistryName().getPath().replace("_source", "").replace("_", " ");
            int density = liquidBlock.getFluid().getAttributes().getDensity();
            add(String.format("fluid.chemlib.%s_source", name), WordUtils.capitalize(String.format("%s%s", name, density < 0 ? " gas" : "")));
        });

        FluidRegistry.getBuckets().forEach(bucket -> {
            Objects.requireNonNull(bucket.getRegistryName());
            String name = bucket.getRegistryName().getPath();
            add(String.format("item.chemlib.%s", name), WordUtils.capitalize(name.replace("_", " ")));
        });

        ItemRegistry.getLiquidBlockItems().forEach(item -> {
            Objects.requireNonNull(item.getRegistryName());
            String name = item.getRegistryName().getPath().replace("_liquid_block", "");
            Optional<ElementItem> optionalElement = ItemRegistry.getElementByName(name);
            Optional<CompoundItem> optionalCompound = ItemRegistry.getCompoundByName(name);
            AtomicReference<MatterState> matterState = new AtomicReference<>();
            optionalElement.ifPresent(element -> matterState.set(element.getMatterState()));
            optionalCompound.ifPresent(compound -> matterState.set(compound.getMatterState()));
            if (matterState.get() != null) {
                add(String.format("block.chemlib.%s", item.getRegistryName().getPath()), WordUtils.capitalize(String.format("%s %s", name.replace("_", " "), matterState.get().getSerializedName())));
            } else {
                add(String.format("block.chemlib.%s", item.getRegistryName().getPath()), WordUtils.capitalize(name.replace("_", " ")));
            }
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
