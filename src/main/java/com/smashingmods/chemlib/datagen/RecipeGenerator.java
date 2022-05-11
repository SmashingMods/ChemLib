package com.smashingmods.chemlib.datagen;

import com.smashingmods.chemlib.common.items.ElementItem;
import com.smashingmods.chemlib.common.registry.BlockRegistry;
import com.smashingmods.chemlib.common.registry.ItemRegistry;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class RecipeGenerator extends RecipeProvider {
    public RecipeGenerator(DataGenerator pGenerator) {
        super(pGenerator);
    }

    @Override
    protected void buildCraftingRecipes(@Nonnull Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
        BlockRegistry.BLOCKS.getEntries().stream().forEach(block -> {
            ElementItem element = ItemRegistry.getElementByName(block.get().getRegistryName().getPath().split("_")[0]).get();
            ShapedRecipeBuilder.shaped(block.get())
                    .define('G', Tags.Items.GLASS)
                    .define('E', element)
                    .pattern("GEG")
                    .pattern("EEE")
                    .pattern("GEG")
                    .unlockedBy(String.format("has_%s", element.getName()), inventoryTrigger(ItemPredicate.Builder.item().of(element).build()))
                    .save(pFinishedRecipeConsumer);
        });
    }
}
