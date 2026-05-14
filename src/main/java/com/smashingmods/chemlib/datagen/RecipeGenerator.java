package com.smashingmods.chemlib.datagen;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.api.Chemical;
import com.smashingmods.chemlib.api.ChemicalBlockType;
import com.smashingmods.chemlib.api.ChemicalItemType;
import com.smashingmods.chemlib.registry.BlockRegistry;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class RecipeGenerator extends RecipeProvider {

    public RecipeGenerator(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pProvider) {
        super(pOutput, pProvider);
    }

    @Override
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    protected void buildRecipes(@Nonnull RecipeOutput pOutput) {
        // Register lamp recipes
        BlockRegistry.getChemicalBlocksByType(ChemicalBlockType.LAMP).forEach(block -> {
            Chemical chemical = block.getChemical();
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, block)
                    .define('G', Tags.Items.GLASS_BLOCKS)
                    .define('E', chemical)
                    .pattern("GEG")
                    .pattern("EEE")
                    .pattern("GEG")
                    .unlockedBy(String.format("has_%s", chemical), has(chemical))
                    .save(pOutput);
        });

        // register ingot -> block recipes
        BlockRegistry.getChemicalBlocksByType(ChemicalBlockType.METAL).forEach(block -> {
            Chemical chemical = block.getChemical();
            ItemRegistry.getChemicalItemByNameAndType(chemical.getChemicalName(), ChemicalItemType.INGOT).ifPresent(ingot -> ShapedRecipeBuilder.shaped(RecipeCategory.MISC, block)
                    .define('I', ingot)
                    .pattern("III")
                    .pattern("III")
                    .pattern("III")
                    .unlockedBy(String.format("has_%s", chemical), has(chemical))
                    .save(pOutput, ResourceLocation.fromNamespaceAndPath(ChemLib.MODID, String.format("%s_ingot_to_block", chemical.getChemicalName()))));
        });

        // register nugget -> ingot
        ItemRegistry.getChemicalItemsByTypeAsStream(ChemicalItemType.INGOT).forEach(ingot -> {
            Chemical chemical = ingot.getChemical();
            ItemRegistry.getChemicalItemByNameAndType(chemical.getChemicalName(), ChemicalItemType.NUGGET).ifPresent(nugget -> ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ingot)
                    .define('N', nugget)
                    .pattern("NNN")
                    .pattern("NNN")
                    .pattern("NNN")
                    .unlockedBy(String.format("has_%s", chemical), has(chemical))
                    .save(pOutput, ResourceLocation.fromNamespaceAndPath(ChemLib.MODID, String.format("%s_nugget_to_ingot", chemical.getChemicalName()))));
        });

        // register block -> ingot recipes
        ItemRegistry.getChemicalItemsByTypeAsStream(ChemicalItemType.INGOT).forEach(ingot -> {
            Chemical chemical = ingot.getChemical();
            BlockRegistry.getChemicalBlockByNameAndType(chemical.getChemicalName(), ChemicalBlockType.METAL).ifPresent(block -> ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ingot, 9)
                    .requires(block)
                    .unlockedBy(String.format("has_%s", chemical), has(chemical))
                    .save(pOutput, ResourceLocation.fromNamespaceAndPath(ChemLib.MODID, String.format("%s_block_to_ingot", chemical.getChemicalName()))));
        });

        // register ingot -> nugget
        ItemRegistry.getChemicalItemsByTypeAsStream(ChemicalItemType.INGOT).forEach(ingot -> {
            Chemical chemical = ingot.getChemical();
            ItemRegistry.getChemicalItemByNameAndType(chemical.getChemicalName(), ChemicalItemType.NUGGET).ifPresent(nugget -> ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, nugget, 9)
                    .requires(ingot)
                    .unlockedBy(String.format("has_%s", chemical), has(chemical))
                    .save(pOutput, ResourceLocation.fromNamespaceAndPath(ChemLib.MODID, String.format("%s_ingot_to_nugget", chemical.getChemicalName()))));
        });

        // register dust -> ingot
        ItemRegistry.getChemicalItemsByTypeAsStream(ChemicalItemType.DUST)
                .forEach(dust -> ItemRegistry.getElementByName(dust.getChemicalName())
                .flatMap(elementItem -> ItemRegistry.getChemicalItemByNameAndType(elementItem.getChemicalName(), ChemicalItemType.INGOT))
                .ifPresent(chemicalItem -> {
                    String chemicalName = chemicalItem.getChemicalName();
                    SimpleCookingRecipeBuilder.smelting(Ingredient.of(dust), RecipeCategory.MISC, chemicalItem, 0.7f, 200)
                            .unlockedBy(String.format("has_%s", chemicalItem.getChemical()), has(chemicalItem.getChemical()))
                            .save(pOutput, ResourceLocation.fromNamespaceAndPath(ChemLib.MODID, String.format("%s_ingot_from_smelting_%s_dust", chemicalName, chemicalName)));
                    SimpleCookingRecipeBuilder.blasting(Ingredient.of(dust), RecipeCategory.MISC, chemicalItem, 0.7f, 100)
                            .unlockedBy(String.format("has_%s", chemicalItem.getChemical()), has(chemicalItem.getChemical()))
                            .save(pOutput, ResourceLocation.fromNamespaceAndPath(ChemLib.MODID, String.format("%s_ingot_from_blasting_%s_dust", chemicalName, chemicalName)));
                }));

        // hard-code vanilla dust to ingot smelting/blasting recipes
        ItemRegistry.getChemicalItemByNameAndType("copper", ChemicalItemType.DUST).ifPresent(dust -> {
            SimpleCookingRecipeBuilder.smelting(Ingredient.of(dust), RecipeCategory.MISC, Items.COPPER_INGOT, 0.7f, 200)
                    .unlockedBy("has_copper", has(dust))
                    .save(pOutput, ResourceLocation.fromNamespaceAndPath(ChemLib.MODID, "copper_ingot_from_smelting_copper_dust"));
            SimpleCookingRecipeBuilder.blasting(Ingredient.of(dust), RecipeCategory.MISC, Items.COPPER_INGOT, 0.7f, 100)
                    .unlockedBy("has_copper", has(dust))
                    .save(pOutput, ResourceLocation.fromNamespaceAndPath(ChemLib.MODID, "copper_ingot_from_blasting_copper_dust"));
        });
        ItemRegistry.getChemicalItemByNameAndType("iron", ChemicalItemType.DUST).ifPresent(dust -> {
            SimpleCookingRecipeBuilder.smelting(Ingredient.of(dust), RecipeCategory.MISC, Items.IRON_INGOT, 0.7f, 200)
                    .unlockedBy("has_iron", has(dust))
                    .save(pOutput, ResourceLocation.fromNamespaceAndPath(ChemLib.MODID, "iron_ingot_from_smelting_iron_dust"));
            SimpleCookingRecipeBuilder.blasting(Ingredient.of(dust), RecipeCategory.MISC, Items.IRON_INGOT, 0.7f, 100)
                    .unlockedBy("has_iron", has(dust))
                    .save(pOutput, ResourceLocation.fromNamespaceAndPath(ChemLib.MODID, "iron_ingot_from_blasting_iron_dust"));
        });
        ItemRegistry.getChemicalItemByNameAndType("gold", ChemicalItemType.DUST).ifPresent(dust -> {
            SimpleCookingRecipeBuilder.smelting(Ingredient.of(dust), RecipeCategory.MISC, Items.GOLD_INGOT, 0.7f, 200)
                    .unlockedBy("has_gold", has(dust))
                    .save(pOutput, ResourceLocation.fromNamespaceAndPath(ChemLib.MODID, "gold_ingot_from_smelting_gold_dust"));
            SimpleCookingRecipeBuilder.blasting(Ingredient.of(dust), RecipeCategory.MISC, Items.GOLD_INGOT, 0.7f, 100)
                    .unlockedBy("has_gold", has(dust))
                    .save(pOutput, ResourceLocation.fromNamespaceAndPath(ChemLib.MODID, "gold_ingot_from_blasting_gold_dust"));
        });

        // periodic table
        Item periodicTable = ItemRegistry.getRegistryObject(ItemRegistry.REGISTRY_MISC_ITEMS, "periodic_table").get();
        Item hydrogen = ItemRegistry.getElementByName("hydrogen").get();
        Item paper = Items.PAPER;
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, periodicTable)
                .define('H', hydrogen)
                .define('P', paper)
                .pattern("HHH")
                .pattern("HPH")
                .pattern("HHH")
                .unlockedBy("has_hydrogen", has(hydrogen))
                .save(pOutput);
    }
}
