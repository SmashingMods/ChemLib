package com.smashingmods.chemlib.datagen;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.api.Chemical;
import com.smashingmods.chemlib.api.ChemicalBlockType;
import com.smashingmods.chemlib.api.ChemicalItemType;
import com.smashingmods.chemlib.common.blocks.ChemicalBlock;
import com.smashingmods.chemlib.common.items.ChemicalItem;
import com.smashingmods.chemlib.registry.BlockRegistry;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

@SuppressWarnings("OptionalGetWithoutIsPresent")
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
            ChemicalItem ingot = ItemRegistry.getChemicalItemByNameAndType(chemical.getChemicalName(), ChemicalItemType.INGOT).get();
            ShapedRecipeBuilder.shaped(block)
                    .define('I', ingot)
                    .pattern("III")
                    .pattern("III")
                    .pattern("III")
                    .unlockedBy(String.format("has_%s", chemical), inventoryTrigger(ItemPredicate.Builder.item().of(chemical).build()))
                    .save(pFinishedRecipeConsumer, String.format("%s:%s_ingot_to_block", ChemLib.MODID, chemical.getChemicalName()));
        });

        // register nugget -> ingot
        ItemRegistry.getChemicalItemsByTypeAsStream(ChemicalItemType.INGOT).forEach(ingot -> {
            Chemical chemical = ingot.getChemical();
            ChemicalItem nugget = ItemRegistry.getChemicalItemByNameAndType(chemical.getChemicalName(), ChemicalItemType.NUGGET).get();
            ShapedRecipeBuilder.shaped(ingot)
                    .define('N', nugget)
                    .pattern("NNN")
                    .pattern("NNN")
                    .pattern("NNN")
                    .unlockedBy(String.format("has_%s", chemical), inventoryTrigger(ItemPredicate.Builder.item().of(chemical).build()))
                    .save(pFinishedRecipeConsumer, String.format("%s:%s_nugget_to_ingot", ChemLib.MODID, chemical.getChemicalName()));
        });

        // register block -> ingot recipes
        ItemRegistry.getChemicalItemsByTypeAsStream(ChemicalItemType.INGOT).forEach(ingot -> {
            Chemical chemical = ingot.getChemical();
            ChemicalBlock block = BlockRegistry.getChemicalBlockByNameAndType(chemical.getChemicalName(), ChemicalBlockType.METAL).get();
            ShapelessRecipeBuilder.shapeless(ingot, 9)
                    .requires(block)
                    .unlockedBy(String.format("has_%s", chemical), inventoryTrigger(ItemPredicate.Builder.item().of(chemical).build()))
                    .save(pFinishedRecipeConsumer, String.format("%s:%s_block_to_ingot", ChemLib.MODID, chemical.getChemicalName()));
        });

        // register ingot -> nugget
        ItemRegistry.getChemicalItemsByTypeAsStream(ChemicalItemType.INGOT).forEach(ingot -> {
            Chemical chemical = ingot.getChemical();
            ChemicalItem nugget = ItemRegistry.getChemicalItemByNameAndType(chemical.getChemicalName(), ChemicalItemType.NUGGET).get();
            ShapelessRecipeBuilder.shapeless(nugget, 9)
                    .requires(ingot)
                    .unlockedBy(String.format("has_%s", chemical), inventoryTrigger(ItemPredicate.Builder.item().of(chemical).build()))
                    .save(pFinishedRecipeConsumer, String.format("%s:%s_ingot_to_nugget", ChemLib.MODID, chemical.getChemicalName()));
        });

        // register dust -> ingot
        ItemRegistry.getChemicalItemsByTypeAsStream(ChemicalItemType.DUST)
                .forEach(dust -> ItemRegistry.getElementByName(dust.getChemicalName())
                .flatMap(elementItem -> ItemRegistry.getChemicalItemByNameAndType(elementItem.getChemicalName(), ChemicalItemType.INGOT))
                .ifPresent(chemicalItem -> {
                    String chemicalName = chemicalItem.getChemicalName();
                    SimpleCookingRecipeBuilder.smelting(Ingredient.of(dust), chemicalItem, 0.7f, 200)
                            .unlockedBy(String.format("has_%s", chemicalItem.getChemical()), inventoryTrigger(ItemPredicate.Builder.item().of(chemicalItem.getChemical()).build()))
                            .save(pFinishedRecipeConsumer, String.format("%s:%s_ingot_from_smelting_%s_dust", ChemLib.MODID, chemicalName, chemicalName));
                    SimpleCookingRecipeBuilder.blasting(Ingredient.of(dust), chemicalItem, 0.7f, 100)
                            .unlockedBy(String.format("has_%s", chemicalItem.getChemical()), inventoryTrigger(ItemPredicate.Builder.item().of(chemicalItem.getChemical()).build()))
                            .save(pFinishedRecipeConsumer, String.format("%s:%s_ingot_from_blasting_%s_dust", ChemLib.MODID, chemicalName, chemicalName));
                }));

        // periodic table
        Item periodicTable = ItemRegistry.getRegistryObject(ItemRegistry.REGISTRY_MISC_ITEMS, "periodic_table").get();
        Item hydrogen = ItemRegistry.getElementByName("hydrogen").get();
        Item paper = Items.PAPER;
        ShapedRecipeBuilder.shaped(periodicTable)
                .define('H', hydrogen)
                .define('P', paper)
                .pattern("HHH")
                .pattern("HPH")
                .pattern("HHH")
                .unlockedBy("has_hydrogen", inventoryTrigger(ItemPredicate.Builder.item().of(hydrogen).build()))
                .save(pFinishedRecipeConsumer);
    }
}
