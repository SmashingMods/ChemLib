package com.smashingmods.chemlib.datagen;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.api.Chemical;
import com.smashingmods.chemlib.api.ChemicalBlockType;
import com.smashingmods.chemlib.api.ChemicalItemType;
import com.smashingmods.chemlib.registry.BlockRegistry;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class RecipeGenerator extends RecipeProvider {
    public RecipeGenerator(HolderLookup.Provider pRegistries, RecipeOutput pOutput) {
        super(pRegistries, pOutput);
    }

    @Override
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    protected void buildRecipes() {
        HolderGetter<Item> items = this.registries.lookupOrThrow(Registries.ITEM);

        // Register lamp recipes
        BlockRegistry.getChemicalBlocksByType(ChemicalBlockType.LAMP).forEach(block -> {
            Chemical chemical = block.getChemical();
            ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, block)
                    .define('G', Tags.Items.GLASS_BLOCKS)
                    .define('E', chemical)
                    .pattern("GEG")
                    .pattern("EEE")
                    .pattern("GEG")
                    .unlockedBy(String.format("has_%s", chemical), has(chemical))
                    .save(this.output);
        });

        // register ingot -> block recipes
        BlockRegistry.getChemicalBlocksByType(ChemicalBlockType.METAL).forEach(block -> {
            Chemical chemical = block.getChemical();
            ItemRegistry.getChemicalItemByNameAndType(chemical.getChemicalName(), ChemicalItemType.INGOT).ifPresent(ingot -> ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, block)
                    .define('I', ingot)
                    .pattern("III")
                    .pattern("III")
                    .pattern("III")
                    .unlockedBy(String.format("has_%s", chemical), has(chemical))
                    .save(this.output, String.format("%s:%s_ingot_to_block", ChemLib.MODID, chemical.getChemicalName())));
        });

        // register nugget -> ingot
        ItemRegistry.getChemicalItemsByTypeAsStream(ChemicalItemType.INGOT).forEach(ingot -> {
            Chemical chemical = ingot.getChemical();
            ItemRegistry.getChemicalItemByNameAndType(chemical.getChemicalName(), ChemicalItemType.NUGGET).ifPresent(nugget -> ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ingot)
                    .define('N', nugget)
                    .pattern("NNN")
                    .pattern("NNN")
                    .pattern("NNN")
                    .unlockedBy(String.format("has_%s", chemical), has(chemical))
                    .save(this.output, String.format("%s:%s_nugget_to_ingot", ChemLib.MODID, chemical.getChemicalName())));
        });

        // register block -> ingot recipes
        ItemRegistry.getChemicalItemsByTypeAsStream(ChemicalItemType.INGOT).forEach(ingot -> {
            Chemical chemical = ingot.getChemical();
            BlockRegistry.getChemicalBlockByNameAndType(chemical.getChemicalName(), ChemicalBlockType.METAL).ifPresent(block -> ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ingot, 9)
                    .requires(block)
                    .unlockedBy(String.format("has_%s", chemical), has(chemical))
                    .save(this.output, String.format("%s:%s_block_to_ingot", ChemLib.MODID, chemical.getChemicalName())));
        });

        // register ingot -> nugget
        ItemRegistry.getChemicalItemsByTypeAsStream(ChemicalItemType.INGOT).forEach(ingot -> {
            Chemical chemical = ingot.getChemical();
            ItemRegistry.getChemicalItemByNameAndType(chemical.getChemicalName(), ChemicalItemType.NUGGET).ifPresent(nugget -> ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, nugget, 9)
                    .requires(ingot)
                    .unlockedBy(String.format("has_%s", chemical), has(chemical))
                    .save(this.output, String.format("%s:%s_ingot_to_nugget", ChemLib.MODID, chemical.getChemicalName())));
        });

        // register dust -> ingot
        ItemRegistry.getChemicalItemsByTypeAsStream(ChemicalItemType.DUST)
                .forEach(dust -> ItemRegistry.getElementByName(dust.getChemicalName())
                .flatMap(elementItem -> ItemRegistry.getChemicalItemByNameAndType(elementItem.getChemicalName(), ChemicalItemType.INGOT))
                .ifPresent(chemicalItem -> {
                    String chemicalName = chemicalItem.getChemicalName();
                    SimpleCookingRecipeBuilder.smelting(Ingredient.of(dust), RecipeCategory.MISC, chemicalItem, 0.7f, 200)
                            .unlockedBy(String.format("has_%s", chemicalItem.getChemical()), has(chemicalItem.getChemical()))
                            .save(this.output, String.format("%s:%s_ingot_from_smelting_%s_dust", ChemLib.MODID, chemicalName, chemicalName));
                    SimpleCookingRecipeBuilder.blasting(Ingredient.of(dust), RecipeCategory.MISC, chemicalItem, 0.7f, 100)
                            .unlockedBy(String.format("has_%s", chemicalItem.getChemical()), has(chemicalItem.getChemical()))
                            .save(this.output, String.format("%s:%s_ingot_from_blasting_%s_dust", ChemLib.MODID, chemicalName, chemicalName));
                }));

        // hard-code vanilla dust to ingot smelting/blasting recipes
        ItemRegistry.getChemicalItemByNameAndType("copper", ChemicalItemType.DUST).ifPresent(dust -> {
            SimpleCookingRecipeBuilder.smelting(Ingredient.of(dust), RecipeCategory.MISC, Items.COPPER_INGOT, 0.7f, 200)
                    .unlockedBy("has_copper", has(dust))
                    .save(this.output, String.format("%s:copper_ingot_from_smelting_copper_dust", ChemLib.MODID));
            SimpleCookingRecipeBuilder.blasting(Ingredient.of(dust), RecipeCategory.MISC, Items.COPPER_INGOT, 0.7f, 100)
                    .unlockedBy("has_copper", has(dust))
                    .save(this.output, String.format("%s:copper_ingot_from_blasting_copper_dust", ChemLib.MODID));
        });
        ItemRegistry.getChemicalItemByNameAndType("iron", ChemicalItemType.DUST).ifPresent(dust -> {
            SimpleCookingRecipeBuilder.smelting(Ingredient.of(dust), RecipeCategory.MISC, Items.IRON_INGOT, 0.7f, 200)
                    .unlockedBy("has_iron", has(dust))
                    .save(this.output, String.format("%s:iron_ingot_from_smelting_iron_dust", ChemLib.MODID));
            SimpleCookingRecipeBuilder.blasting(Ingredient.of(dust), RecipeCategory.MISC, Items.IRON_INGOT, 0.7f, 100)
                    .unlockedBy("has_iron", has(dust))
                    .save(this.output, String.format("%s:iron_ingot_from_blasting_iron_dust", ChemLib.MODID));
        });
        ItemRegistry.getChemicalItemByNameAndType("gold", ChemicalItemType.DUST).ifPresent(dust -> {
            SimpleCookingRecipeBuilder.smelting(Ingredient.of(dust), RecipeCategory.MISC, Items.GOLD_INGOT, 0.7f, 200)
                    .unlockedBy("has_gold", has(dust))
                    .save(this.output, String.format("%s:gold_ingot_from_smelting_gold_dust", ChemLib.MODID));
            SimpleCookingRecipeBuilder.blasting(Ingredient.of(dust), RecipeCategory.MISC, Items.GOLD_INGOT, 0.7f, 100)
                    .unlockedBy("has_gold", has(dust))
                    .save(this.output, String.format("%s:gold_ingot_from_blasting_gold_dust", ChemLib.MODID));
        });

        // periodic table
        Item periodicTable = ItemRegistry.getRegistryObject(ItemRegistry.REGISTRY_MISC_ITEMS, "periodic_table").get();
        Item hydrogen = ItemRegistry.getElementByName("hydrogen").get();
        Item paper = Items.PAPER;
        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, periodicTable)
                .define('H', hydrogen)
                .define('P', paper)
                .pattern("HHH")
                .pattern("HPH")
                .pattern("HHH")
                .unlockedBy("has_hydrogen", has(hydrogen))
                .save(this.output);
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pRegistries) {
            super(pOutput, pRegistries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider pRegistries, RecipeOutput pOutput) {
            return new RecipeGenerator(pRegistries, pOutput);
        }

        @Override
        public String getName() {
            return "ChemLib Recipes";
        }
    }
}
