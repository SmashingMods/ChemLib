package com.smashingmods.chemlib.datagen;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.api.Chemical;
import com.smashingmods.chemlib.api.ChemicalItemType;
import com.smashingmods.chemlib.api.MatterState;
import com.smashingmods.chemlib.client.AbbreviationRenderer;
import com.smashingmods.chemlib.common.blocks.ChemicalBlock;
import com.smashingmods.chemlib.common.items.ChemicalBlockItem;
import com.smashingmods.chemlib.common.items.ChemicalItem;
import com.smashingmods.chemlib.common.items.CompoundItem;
import com.smashingmods.chemlib.common.items.ElementItem;
import com.smashingmods.chemlib.registry.FluidRegistry;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.color.item.FluidContentsTint;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

/**
 * Generates the mod's item models and item-model definitions. 1.21.4 replaced the NeoForge
 * {@code client.model.generators.ItemModelProvider} with the vanilla {@link ModelProvider}: the flat
 * {@code models/item/*.json} are built from {@link ModelTemplate}s ({@code parent} + {@code textures}),
 * while the new {@code items/<id>.json} layer (the item-model definitions emitted through
 * {@link ItemModelOutput}) carries the per-item render behaviour that used to live in runtime client
 * extensions.
 *
 * <p>The BEWLR that drew the element/chemical abbreviation and the {@code RegisterColorHandlersEvent.Item}
 * tints are gone; their behaviour is now data-driven here. Element and metal-chemical (dust/nugget/ingot/
 * plate) items become a {@link ItemModelUtils#composite composite} of a tinted flat-model layer (the
 * per-chemical {@code minecraft:constant} tint) and a {@code minecraft:special} layer referencing the
 * {@code chemlib:abbreviation} renderer; compounds, compound-dusts and chemical block-items keep only the
 * tinted flat-model layer; buckets use the {@code neoforge:fluid_contents} tint. The constant tint value
 * is the same packed ARGB the runtime {@code getColor} handlers produced ({@code minecraft:constant}
 * forces opacity, matching the {@code | 0xFF000000} the handlers applied).
 *
 * <p>Block models are emitted by {@link BlockStateGenerator}, so {@link #getKnownBlocks()} is empty here
 * and only the item side of {@code ModelProvider}'s completeness check runs.
 */
public class ItemModelGenerator extends ModelProvider {

    private static final ResourceLocation GENERATED = ResourceLocation.withDefaultNamespace("item/generated");

    public ItemModelGenerator(PackOutput pOutput) {
        super(pOutput, ChemLib.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators pBlockModels, ItemModelGenerators pItemModels) {
        ItemModelOutput itemOutput = pItemModels.itemModelOutput;
        BiConsumer<ResourceLocation, ModelInstance> modelOutput = pItemModels.modelOutput;

        generateElementModels(modelOutput);
        generateCompoundModels(modelOutput);
        generateChemicalItemModels(modelOutput);

        ItemRegistry.getElements().forEach(element -> registerElement(element, itemOutput));
        ItemRegistry.getCompounds().forEach(compound -> registerCompound(compound, itemOutput));

        ItemRegistry.getChemicalItemsByTypeAsStream(ChemicalItemType.COMPOUND).forEach(item -> registerCompoundDust(item, itemOutput));
        ItemRegistry.getChemicalItemsByTypeAsStream(ChemicalItemType.DUST).forEach(dust -> registerChemicalItem(dust, "dust", itemOutput));
        ItemRegistry.getChemicalItemsByTypeAsStream(ChemicalItemType.NUGGET).forEach(nugget -> registerChemicalItem(nugget, "nugget", itemOutput));
        ItemRegistry.getChemicalItemsByTypeAsStream(ChemicalItemType.INGOT).forEach(ingot -> registerChemicalItem(ingot, "ingot", itemOutput));
        ItemRegistry.getChemicalItemsByTypeAsStream(ChemicalItemType.PLATE).forEach(plate -> registerChemicalItem(plate, "plate", itemOutput));

        FluidRegistry.getBuckets().forEach(bucket -> registerBucket(bucket, itemOutput, modelOutput));
        ItemRegistry.getChemicalBlockItems().forEach(blockItem -> registerChemicalBlockItem(blockItem, itemOutput, modelOutput));

        ItemRegistry.REGISTRY_MISC_ITEMS.getEntries().forEach(entry -> registerMiscItem(entry.get(), itemOutput));
    }

    private void generateElementModels(BiConsumer<ResourceLocation, ModelInstance> pModelOutput) {
        for (String type : Arrays.asList("solid", "liquid", "gas")) {
            layeredModel(String.format("item/element_%s_model", type),
                    modLocation(String.format("item/element_%s_layer_0", type)),
                    modLocation(String.format("item/element_%s_layer_1", type)), pModelOutput);
        }
    }

    private void generateCompoundModels(BiConsumer<ResourceLocation, ModelInstance> pModelOutput) {
        for (String type : Arrays.asList("solid", "liquid", "gas", "dust")) {
            layeredModel(String.format("item/compound_%s_model", type),
                    modLocation(String.format("item/compound_%s_layer_0", type)),
                    modLocation(String.format("item/compound_%s_layer_1", type)), pModelOutput);
        }
    }

    private void generateChemicalItemModels(BiConsumer<ResourceLocation, ModelInstance> pModelOutput) {
        // COMPOUND and DUST share the "dust" serialized name, so the flat model is emitted once per
        // distinct name (the vanilla model collector rejects duplicate model definitions).
        Arrays.stream(ChemicalItemType.values())
                .map(ChemicalItemType::getSerializedName)
                .distinct()
                .forEach(type -> new ModelTemplate(Optional.of(GENERATED), Optional.empty(), TextureSlot.LAYER0)
                        .create(modLocation(String.format("item/chemical_%s_model", type)),
                                new TextureMapping().put(TextureSlot.LAYER0, modLocation(String.format("item/%s", type))), pModelOutput));
    }

    private void registerElement(ElementItem pElement, ItemModelOutput pItemOutput) {
        ResourceLocation model = modLocation(String.format("item/element_%s_model", pElement.getMatterState().getSerializedName()));
        pItemOutput.accept(pElement, abbreviatedModel(model, pElement.getColor()));
    }

    private void registerCompound(CompoundItem pCompound, ItemModelOutput pItemOutput) {
        ResourceLocation model = modLocation(String.format("item/compound_%s_model", pCompound.getMatterState().getSerializedName()));
        pItemOutput.accept(pCompound, ItemModelUtils.tintedModel(model, ItemModelUtils.constantTint(pCompound.getColor())));
    }

    private void registerCompoundDust(ChemicalItem pItem, ItemModelOutput pItemOutput) {
        pItemOutput.accept(pItem, ItemModelUtils.tintedModel(modLocation("item/compound_dust_model"), ItemModelUtils.constantTint(pItem.getColor())));
    }

    private void registerChemicalItem(ChemicalItem pItem, String pType, ItemModelOutput pItemOutput) {
        pItemOutput.accept(pItem, abbreviatedModel(modLocation(String.format("item/chemical_%s_model", pType)), pItem.getColor()));
    }

    private void registerChemicalBlockItem(ChemicalBlockItem pBlockItem, ItemModelOutput pItemOutput, BiConsumer<ResourceLocation, ModelInstance> pModelOutput) {
        ChemicalBlock block = (ChemicalBlock) pBlockItem.getBlock();
        String type = block.getBlockType().getSerializedName();
        ResourceLocation model = modLocation(String.format("item/%s_%s_block", block.getChemicalName(), type));
        new ModelTemplate(Optional.of(modLocation(String.format("block/%s_%s_block", block.getChemicalName(), type))), Optional.empty(), TextureSlot.LAYER0)
                .create(model, new TextureMapping().put(TextureSlot.LAYER0, modLocation(String.format("block/%s_block", type))), pModelOutput);
        pItemOutput.accept(pBlockItem, ItemModelUtils.tintedModel(model, ItemModelUtils.constantTint(pBlockItem.getColor())));
    }

    private void registerBucket(BucketItem pBucket, ItemModelOutput pItemOutput, BiConsumer<ResourceLocation, ModelInstance> pModelOutput) {
        String path = Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(pBucket)).getPath();
        int pieces = path.split("_").length;
        String chemicalName = "";

        for (int i = 0; i < pieces - 1; i++) {
            chemicalName = String.format("%s%s%s", chemicalName, chemicalName.isEmpty() ? "" : "_", path.split("_")[i]);
        }

        Chemical chemical = null;
        Optional<ElementItem> optionalElement = ItemRegistry.getElementByName(chemicalName);
        Optional<CompoundItem> optionalCompound = ItemRegistry.getCompoundByName(chemicalName);

        if (optionalElement.isPresent()) {
            chemical = optionalElement.get();
        } else if (optionalCompound.isPresent()) {
            chemical = optionalCompound.get();
        }

        MatterState matterState = Objects.requireNonNull(chemical).getMatterState();
        ResourceLocation model = modLocation(String.format("item/%s", path));

        switch (matterState) {
            case LIQUID -> layeredModel(String.format("item/%s", path), modLocation("item/bucket_layer_0"), modLocation("item/bucket_layer_1"), pModelOutput);
            case GAS -> layeredModel(String.format("item/%s", path), modLocation("item/gas_bucket_layer_0"), modLocation("item/gas_bucket_layer_1"), pModelOutput);
            default -> { /* solid chemicals have no bucket */ }
        }

        // The tints array maps by index onto the model's tintindex layers, so layer0 (the bucket itself)
        // needs an explicit no-op white before the fluid tint can land on layer1 (the contents overlay) -
        // the same idiom vanilla uses for filled_map's untinted base + tinted markings.
        pItemOutput.accept(pBucket, ItemModelUtils.tintedModel(model, ItemModelUtils.constantTint(-1), FluidContentsTint.INSTANCE));
    }

    private void registerMiscItem(Item pItem, ItemModelOutput pItemOutput) {
        pItemOutput.accept(pItem, ItemModelUtils.plainModel(modLocation(String.format("item/%s", Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(pItem)).getPath()))));
    }

    /**
     * The flat tinted base layer plus the {@code chemlib:abbreviation} special overlay, matching the old
     * BEWLR which drew the tinted flat model and the abbreviation glyphs over it. The tint sits on the
     * model layer because {@code minecraft:special} carries no tints; the special layer's base supplies
     * the same flat model so the renderer only adds the glyphs.
     */
    private static ItemModel.Unbaked abbreviatedModel(ResourceLocation pFlatModel, int pColor) {
        return ItemModelUtils.composite(
                ItemModelUtils.tintedModel(pFlatModel, ItemModelUtils.constantTint(pColor)),
                ItemModelUtils.specialModel(pFlatModel, new AbbreviationRenderer.Unbaked()));
    }

    private void layeredModel(String pName, ResourceLocation pLayer0, ResourceLocation pLayer1, BiConsumer<ResourceLocation, ModelInstance> pModelOutput) {
        new ModelTemplate(Optional.of(GENERATED), Optional.empty(), TextureSlot.LAYER0, TextureSlot.LAYER1)
                .create(modLocation(pName), TextureMapping.layered(pLayer0, pLayer1), pModelOutput);
    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return Stream.empty();
    }

    @Override
    public String getName() {
        return "Item Model Definitions - " + ChemLib.MODID;
    }
}
