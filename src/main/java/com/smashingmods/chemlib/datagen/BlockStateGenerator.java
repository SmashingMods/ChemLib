package com.smashingmods.chemlib.datagen;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.api.ChemicalBlockType;
import com.smashingmods.chemlib.common.blocks.ChemicalBlock;
import com.smashingmods.chemlib.registry.BlockRegistry;
import com.smashingmods.chemlib.registry.FluidRegistry;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.blockstates.Variant;
import net.minecraft.client.data.models.blockstates.VariantProperties;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Generates the mod's blockstates and block models. 1.21.4 replaced the NeoForge
 * {@code client.model.generators.BlockStateProvider} with the vanilla {@link ModelProvider}; the
 * blockstate/model JSON is now assembled from {@link MultiVariantGenerator} and
 * {@link ModelTemplate}/{@link ExtendedModelTemplateBuilder} instead of the removed builder DSL.
 * Item-model definitions are emitted by {@link ItemModelGenerator}, so {@link #getKnownItems()} is empty
 * here and only the block side of {@code ModelProvider}'s completeness check runs.
 */
public class BlockStateGenerator extends ModelProvider {

    public BlockStateGenerator(PackOutput pOutput) {
        super(pOutput, ChemLib.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators pBlockModels, ItemModelGenerators pItemModels) {
        Consumer<net.minecraft.client.data.models.blockstates.BlockStateGenerator> blockStateOutput = pBlockModels.blockStateOutput;
        BiConsumer<ResourceLocation, ModelInstance> modelOutput = pBlockModels.modelOutput;

        generateBlockModel("metal", "metal_block", modelOutput);
        generateBlockModel("lamp_off", "lamp_block", modelOutput);
        generateBlockModel("lamp_on", "lamp_on", modelOutput);

        BlockRegistry.getChemicalBlocksByType(ChemicalBlockType.METAL).forEach(block -> registerMetalBlock(block, blockStateOutput, modelOutput));
        BlockRegistry.getChemicalBlocksByType(ChemicalBlockType.LAMP).forEach(block -> registerLampBlock(block, blockStateOutput, modelOutput));
        FluidRegistry.getLiquidBlocks().forEach(block -> registerLiquidBlock(block, blockStateOutput));
    }

    private void generateBlockModel(String pName, String pTexture, BiConsumer<ResourceLocation, ModelInstance> pModelOutput) {
        ResourceLocation texture = modLocation(String.format("block/%s", pTexture));
        TextureMapping textures = new TextureMapping()
                .put(TextureSlot.ALL, texture)
                .put(TextureSlot.PARTICLE, texture);
        ExtendedModelTemplateBuilder.builder()
                .parent(mcLocation("block/block"))
                .requiredTextureSlot(TextureSlot.ALL)
                .requiredTextureSlot(TextureSlot.PARTICLE)
                .element(element -> element.cube(TextureSlot.ALL)
                        .faces((direction, face) -> face.uvs(0f, 0f, 16f, 16f).tintindex(0)))
                .build()
                .create(modLocation(String.format("block/%s_model", pName)), textures, pModelOutput);
    }

    private void registerMetalBlock(ChemicalBlock pBlock, Consumer<net.minecraft.client.data.models.blockstates.BlockStateGenerator> pBlockStateOutput, BiConsumer<ResourceLocation, ModelInstance> pModelOutput) {
        ResourceLocation parent = modLocation("block/metal_model");
        new ModelTemplate(Optional.of(parent), Optional.empty(), TextureSlot.ALL)
                .create(modLocation(String.format("block/%s_metal_block", pBlock.getChemicalName())),
                        new TextureMapping().put(TextureSlot.ALL, modLocation("block/metal_block")), pModelOutput);

        pBlockStateOutput.accept(MultiVariantGenerator.multiVariant(pBlock, Variant.variant().with(VariantProperties.MODEL, parent)));
    }

    private void registerLampBlock(ChemicalBlock pBlock, Consumer<net.minecraft.client.data.models.blockstates.BlockStateGenerator> pBlockStateOutput, BiConsumer<ResourceLocation, ModelInstance> pModelOutput) {
        ResourceLocation off = modLocation("block/lamp_off_model");
        ResourceLocation on = modLocation("block/lamp_on_model");
        new ModelTemplate(Optional.of(off), Optional.empty())
                .create(modLocation(String.format("block/%s_lamp_block", pBlock.getChemicalName())), new TextureMapping(), pModelOutput);
        new ModelTemplate(Optional.of(on), Optional.empty())
                .create(modLocation(String.format("block/%s_lamp_block_on", pBlock.getChemicalName())), new TextureMapping(), pModelOutput);

        pBlockStateOutput.accept(MultiVariantGenerator.multiVariant(pBlock).with(
                PropertyDispatch.property(BlockStateProperties.LIT)
                        .select(false, Variant.variant().with(VariantProperties.MODEL, off))
                        .select(true, Variant.variant().with(VariantProperties.MODEL, on))));
    }

    private void registerLiquidBlock(LiquidBlock pBlock, Consumer<net.minecraft.client.data.models.blockstates.BlockStateGenerator> pBlockStateOutput) {
        pBlockStateOutput.accept(MultiVariantGenerator.multiVariant(pBlock, Variant.variant().with(VariantProperties.MODEL, mcLocation("block/water"))));
    }

    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return Stream.empty();
    }

    @Override
    public String getName() {
        return "Block State Definitions - " + ChemLib.MODID;
    }
}
