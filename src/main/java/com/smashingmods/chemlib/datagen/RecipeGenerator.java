package com.smashingmods.chemlib.datagen;

import com.smashingmods.chemlib.api.Chemical;
import com.smashingmods.chemlib.api.ChemicalBlockType;
import com.smashingmods.chemlib.api.ChemicalItemType;
import com.smashingmods.chemlib.common.blocks.ChemicalBlock;
import com.smashingmods.chemlib.common.items.ChemicalItem;
import com.smashingmods.chemlib.common.registry.BlockRegistry;
import com.smashingmods.chemlib.common.registry.ItemRegistry;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class RecipeGenerator extends RecipeProvider {
    public RecipeGenerator(DataGenerator pGenerator) {
        super(pGenerator);
    }

    @Override
    protected void buildCraftingRecipes(@Nonnull Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
        // Register lamp recipes
        BlockRegistry.getChemicalBlocksByType(ChemicalBlockType.LAMP).forEach(block -> {
            Chemical chemical = block.getChemical();
            ShapedRecipeBuilder.shaped(block)
                    .define('G', Tags.Items.GLASS)
                    .define('E', chemical)
                    .pattern("GEG")
                    .pattern("EEE")
                    .pattern("GEG")
                    .unlockedBy(String.format("has_%s", chemical), inventoryTrigger(ItemPredicate.Builder.item().of(chemical).build()))
                    .save(pFinishedRecipeConsumer);
        });

        // register ingot -> block recipes
        BlockRegistry.getChemicalBlocksByType(ChemicalBlockType.METAL).forEach(block -> {
            Chemical chemical = block.getChemical();
            ChemicalItem item = ItemRegistry.getItemByNameAndType(chemical.getChemicalName(), ChemicalItemType.INGOT).get();
            ShapedRecipeBuilder.shaped(block)
                    .define('I', item)
                    .pattern("III")
                    .pattern("III")
                    .pattern("III")
                    .unlockedBy(String.format("has_%s", chemical), inventoryTrigger(ItemPredicate.Builder.item().of(chemical).build()))
                    .save(pFinishedRecipeConsumer);
        });

        // register block -> ingot recipes
        ItemRegistry.getChemicalItemsByType(ChemicalItemType.INGOT).forEach(item -> {
            Chemical chemical = item.getChemical();
            ChemicalBlock block = BlockRegistry.getChemicalBlockByNameAndType(chemical.getChemicalName(), ChemicalBlockType.METAL).get();
            ShapelessRecipeBuilder.shapeless(item, 9)
                    .requires(block)
                    .unlockedBy(String.format("has_%s", chemical), inventoryTrigger(ItemPredicate.Builder.item().of(chemical).build()))
                    .save(pFinishedRecipeConsumer);
        });

        // register ingot -> nugget
        ItemRegistry.getChemicalItemsByType(ChemicalItemType.INGOT).forEach(ingot -> {
            Chemical chemical = ingot.getChemical();
            ChemicalItem nugget = ItemRegistry.getItemByNameAndType(chemical.getChemicalName(), ChemicalItemType.NUGGET).get();
            ShapelessRecipeBuilder.shapeless(nugget, 9)
                    .requires(ingot)
                    .unlockedBy(String.format("has_%s", chemical), inventoryTrigger(ItemPredicate.Builder.item().of(chemical).build()))
                    .save(pFinishedRecipeConsumer);
        });
    }
}
