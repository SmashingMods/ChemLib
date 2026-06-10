package com.smashingmods.chemlib.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.Config;
import com.smashingmods.chemlib.common.items.ChemicalItem;
import com.smashingmods.chemlib.common.items.ElementItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Draws the element/chemical abbreviation (e.g. {@code Fe}, {@code Au}) over the item model in inventory and
 * item-frame views. 1.21.4 removed the {@code BlockEntityWithoutLevelRenderer}
 * stack this used to extend; the abbreviation is now an item-model overlay registered as a
 * {@link SpecialModelRenderer}. The flat element/chemical texture and its per-chemical tint are supplied by the
 * model layer of the item-model definition (a {@code neoforge:composite} of the flat model and this renderer),
 * so the per-display-context and per-tint-index transforms the engine applies before {@link #render} are already
 * in effect here; this renderer only adds the abbreviation glyphs on top.
 */
public class AbbreviationRenderer implements SpecialModelRenderer<AbbreviationRenderer.Abbreviation> {

	public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(ChemLib.MODID, "abbreviation");

	/**
	 * The per-stack data the overlay needs, resolved once per frame by {@link #extractArgument(ItemStack)}: the
	 * abbreviation text and whether the matching {@code Config.render*Abbreviations} toggle is enabled for the
	 * stack's item type. The text is still drawn unconditionally when enabled, matching the previous behaviour.
	 */
	public record Abbreviation(String text, boolean enabled) {}

	@Nullable
	@Override
	public Abbreviation extractArgument(ItemStack pStack) {
		if (pStack.getItem() instanceof ElementItem elementItem) {
			return new Abbreviation(elementItem.getAbbreviation(), Config.Common.renderElementAbbreviations.get());
		} else if (pStack.getItem() instanceof ChemicalItem chemicalItem) {
			boolean enabled = switch (chemicalItem.getItemType()) {
				case DUST -> Config.Common.renderDustAbbreviations.get();
				case NUGGET -> Config.Common.renderNuggetAbbreviations.get();
				case INGOT -> Config.Common.renderIngotAbbreviations.get();
				case PLATE -> Config.Common.renderPlateAbbreviations.get();
				default -> false;
			};
			return new Abbreviation(chemicalItem.getAbbreviation(), enabled);
		}
		return null;
	}

	@Override
	public void render(@Nullable Abbreviation pAbbreviation, ItemDisplayContext pItemDisplayContext, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay, boolean pHasFoil) {
		if (pAbbreviation == null || !pAbbreviation.enabled()) {
			return;
		}

		boolean isGui = pItemDisplayContext == ItemDisplayContext.GUI;
		boolean isFrame = pItemDisplayContext == ItemDisplayContext.FIXED;

		// The abbreviation is only legible head-on, so it is drawn in inventory (GUI) and item-frame (FIXED) views.
		if (!isGui && !isFrame) {
			return;
		}

		pPoseStack.pushPose();

		// At 1.21.4 the engine pre-applies the base layer's display transform plus the (-0.5, -0.5, -0.5)
		// centering before invoking a special renderer, while the 1.21.1 BEWLR stack handed renderByItem the
		// centered but display-untransformed pose (the old item model was a bare builtin/entity) and the
		// renderer then translated by (0.5, 0.5, 0). The hand-tuned constants below are calibrated to that
		// 1.21.1 base pose, so restore it first: C(ctx) = T(0.5, 0.5, 0.5) * D(ctx)^-1 * T(0, 0, -0.5), where
		// D is item/generated's display transform - identity in GUI, rotation [0, 180, 0] (self-inverse,
		// scale 1) in FIXED. First, though, bias the glyphs toward the viewer (+Z here in both contexts): the
		// legacy 180-radian X-rotation tilts the GUI glyph plane across z -0.17..+0.34 vs the base quad's front
		// face at +0.03125, sinking the upper ink behind the item texture's depth, and FIXED (viewer at -Z)
		// sits only ~0.003 proud of its face (-0.035 vs -0.03125), inside z-fighting range.
		pPoseStack.translate(0.0F, 0.0F, isGui ? 0.25F : 0.03F);
		if (isGui) {
			pPoseStack.translate(0.5F, 0.5F, 0.0F);
		} else {
			pPoseStack.translate(0.5F, 0.5F, 0.5F);
			pPoseStack.mulPose(Axis.YN.rotationDegrees(180));
			pPoseStack.translate(0.0F, 0.0F, -0.5F);
		}

		pPoseStack.mulPose(Axis.XN.rotation(180));
		pPoseStack.translate(-0.16D, 0, -0.55D);
		pPoseStack.scale(0.05F, 0.08F, 0.08F);

		if (isFrame) {
			pPoseStack.mulPose(Axis.YN.rotationDegrees(180));
			pPoseStack.mulPose(Axis.XN.rotationDegrees(53));
			pPoseStack.translate(-8D, -1D, 1.7D);
			pPoseStack.scale(1F, 0.65F, 1F);
		}

		Font font = Minecraft.getInstance().font;
		font.drawInBatch(pAbbreviation.text(),
				-4,
				0,
				0x333333,
				false,
				pPoseStack.last().pose(),
				pBuffer,
				Font.DisplayMode.NORMAL,
				0,
				pPackedLight);
		font.drawInBatch(pAbbreviation.text(),
				-5,
				0,
				0xFFFFFF,
				false,
				pPoseStack.last().pose(),
				pBuffer,
				Font.DisplayMode.NORMAL,
				0,
				pPackedLight);

		pPoseStack.popPose();
	}

	public record Unbaked() implements SpecialModelRenderer.Unbaked {

		public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

		@Override
		public MapCodec<Unbaked> type() {
			return MAP_CODEC;
		}

		@Override
		public SpecialModelRenderer<?> bake(EntityModelSet pEntityModelSet) {
			return new AbbreviationRenderer();
		}
	}
}
