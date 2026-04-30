package com.smashingmods.chemlib.client;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.Config;
import com.smashingmods.chemlib.api.Chemical;
import com.smashingmods.chemlib.common.items.ChemicalItem;
import com.smashingmods.chemlib.common.items.ElementItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class AbbreviationRenderer extends BlockEntityWithoutLevelRenderer {

	public static final Supplier<BlockEntityWithoutLevelRenderer> INSTANCE = Suppliers.memoize(
			() -> new AbbreviationRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels())
	);
	public static final IClientItemExtensions RENDERER = new IClientItemExtensions() {
		@Override
		public BlockEntityWithoutLevelRenderer getCustomRenderer() {
			return INSTANCE.get();
		}
	};

	public AbbreviationRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher, EntityModelSet entityModelSet) {
		super(blockEntityRenderDispatcher, entityModelSet);
	}

	@Override
	public void renderByItem(ItemStack stack, ItemDisplayContext itemDisplayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {

		boolean isGui = itemDisplayContext == ItemDisplayContext.GUI;
		boolean isFrame = itemDisplayContext == ItemDisplayContext.FIXED;

		ModelResourceLocation modelResourceLocation = null;

		if (stack.getItem() instanceof ElementItem elementItem) {
			switch (elementItem.getMatterState()) {
				case LIQUID ->
						modelResourceLocation = new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(ChemLib.MODID, "item/element_liquid_model"), "standalone");
				case GAS ->
						modelResourceLocation = new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(ChemLib.MODID, "item/element_gas_model"), "standalone");
				default ->
						modelResourceLocation = new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(ChemLib.MODID, "item/element_solid_model"), "standalone");
			}
		} else if (stack.getItem() instanceof ChemicalItem chemicalItem) {
			switch (chemicalItem.getItemType()) {
				case DUST -> modelResourceLocation = new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(ChemLib.MODID, "item/chemical_dust_model"), "standalone");
				case NUGGET -> modelResourceLocation = new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(ChemLib.MODID, "item/chemical_nugget_model"), "standalone");
				case INGOT -> modelResourceLocation = new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(ChemLib.MODID, "item/chemical_ingot_model"), "standalone");
				case PLATE -> modelResourceLocation = new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(ChemLib.MODID, "item/chemical_plate_model"), "standalone");
			}
		}

		if (modelResourceLocation != null) {

			BakedModel bakedModel = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getModelManager().getModel(modelResourceLocation);

			poseStack.pushPose();
			poseStack.translate(0.5D, 0.5D, 0D);
			if (isGui) {
				Lighting.setupForFlatItems();
				buffer = Minecraft.getInstance().renderBuffers().bufferSource();
			}
			poseStack.pushPose();

			switch (itemDisplayContext) {
				case THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND -> {
					poseStack.translate(0, -0.2D, 0.45D);
				}
				case FIRST_PERSON_LEFT_HAND -> {
					poseStack.translate(-0.025D, -0.025D, 0.75D);
					poseStack.mulPose(Axis.ZP.rotationDegrees(25));
					poseStack.mulPose(Axis.XN.rotationDegrees(45));
					poseStack.mulPose(Axis.YN.rotationDegrees(80));
				}
				case FIRST_PERSON_RIGHT_HAND -> {
					poseStack.translate(-0.20D, -0.05D, 0.75D);
					poseStack.mulPose(Axis.ZN.rotationDegrees(25));
					poseStack.mulPose(Axis.XP.rotationDegrees(45));
					poseStack.mulPose(Axis.YP.rotationDegrees(100));
					poseStack.mulPose(Axis.ZN.rotationDegrees(45));
				}
				case HEAD -> {
					poseStack.mulPose(Axis.YP.rotationDegrees(180));
					poseStack.translate(0, -0.75D, -0.75D);
				}
				case GROUND -> {
					poseStack.translate(0, -0.25D, 0.5D);
					poseStack.scale(1.5F, 1.5F, 1.5F);
				}
				case FIXED -> {
					poseStack.mulPose(Axis.YN.rotationDegrees(180));
					poseStack.translate(0, 0, -0.5D);
				}
			}

			//noinspection UnstableApiUsage
			Minecraft.getInstance().getItemRenderer().render(
					stack,
					itemDisplayContext,
					false,
					poseStack,
					buffer,
					isGui ? 0xF000F0 : packedLight,
					isGui ? OverlayTexture.NO_OVERLAY : packedOverlay,
					bakedModel);
			if (isGui) {
				((MultiBufferSource.BufferSource) buffer).endBatch();
			}
			poseStack.popPose();

			if (isGui || isFrame) {
				poseStack.pushPose();
				poseStack.mulPose(Axis.XN.rotation(180));
				poseStack.translate(-0.16D, 0, -0.55D);
				poseStack.scale(0.05F, 0.08F, 0.08F);

				if (isFrame) {
					poseStack.mulPose(Axis.YN.rotationDegrees(180));
					poseStack.mulPose(Axis.XN.rotationDegrees(53));
					poseStack.translate(-8D, -1D, 1.7D);
					poseStack.scale(1F, 0.65F, 1F);
				}

				Consumer<Chemical> renderAbbreviation = (chemical) -> {
					Minecraft.getInstance().font.drawInBatch(chemical.getAbbreviation(),
							-4,
							0,
							0x333333,
							false,
							poseStack.last().pose(),
							Minecraft.getInstance().renderBuffers().bufferSource(),
							Font.DisplayMode.NORMAL,
							0,
							packedLight);
					Minecraft.getInstance().font.drawInBatch(chemical.getAbbreviation(),
							-5,
							0,
							0xFFFFFF,
							false,
							poseStack.last().pose(),
							Minecraft.getInstance().renderBuffers().bufferSource(),
							Font.DisplayMode.NORMAL,
							0,
							packedLight);
				};

				if (stack.getItem() instanceof ElementItem elementItem) {
					if (Config.Common.renderElementAbbreviations.get()) {
						renderAbbreviation.accept(elementItem);
					}
				} else if (stack.getItem() instanceof ChemicalItem chemicalItem) {
					switch (chemicalItem.getItemType()) {
						case DUST -> {
							if (Config.Common.renderDustAbbreviations.get()) {
								renderAbbreviation.accept(chemicalItem);
							}
						}
						case NUGGET -> {
							if (Config.Common.renderNuggetAbbreviations.get()) {
								renderAbbreviation.accept(chemicalItem);
							}
						}
						case INGOT -> {
							if (Config.Common.renderIngotAbbreviations.get()) {
								renderAbbreviation.accept(chemicalItem);
							}
						}
						case PLATE -> {
							if (Config.Common.renderPlateAbbreviations.get()) {
								renderAbbreviation.accept(chemicalItem);
							}
						}
					}
				}
				if (isGui) {
					Lighting.setupFor3DItems();
				}
				poseStack.popPose();
			}
			poseStack.popPose();
		}
	}
}
