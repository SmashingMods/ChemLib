package com.smashingmods.chemlib.client;

import com.mojang.blaze3d.MethodsReturnNonnullByDefault;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.api.Element;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class PeriodicTableScreen extends Screen {

    public PeriodicTableScreen() {
        super(new TextComponent("Periodic Table"));
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pPoseStack);

        int imageWidth = 2000;
        int imageHeight = 1016;
        int displayWidth = imageWidth / 4;
        int displayHeight = imageHeight / 4;
        int leftPos = (this.width - displayWidth) / 2;
        int topPos = (this.height - displayHeight) / 2;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, new ResourceLocation(ChemLib.MODID, "textures/gui/periodic_table.png"));
        blit(pPoseStack, leftPos, topPos, displayWidth, displayHeight, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
        drawCenteredString(pPoseStack, Minecraft.getInstance().font, new TranslatableComponent("chemlib.screen.periodic_table").withStyle(ChatFormatting.BOLD), width / 2, 24, 0xFFFFFF);

        double boxWidth = 27.75f;
        double boxHeight = 26.9f;
        double startX = (this.width - (18 * boxWidth)) / 2;
        double startY = ((this.height - (7 * boxHeight)) / 2) - 33.0f;
        int count = 0;

        for (Element element : ItemRegistry.getElements()) {
            double x = startX;
            double y = startY;
            int group = element.getGroup();
            int period = element.getPeriod();

            for (int row = 1; row <= 7; row++) {
                if (row == period) {
                    for (int col = 1; col <= 18; col++) {
                        if (col == group) {
                            if (!((period == 6 || period == 7) && group == 3)) {
                                if (pMouseX >= x && pMouseX < x + boxWidth && pMouseY >= y && pMouseY < y + boxHeight) {
                                    drawElementTip(pPoseStack, element);
                                }
                            } else {
                                double resetX = x;
                                double resetY = y;
                                if (period == 6) {
                                    y = (boxHeight * 7.45f) + startY;
                                    x = (boxWidth * count) + startX + boxWidth * 2;
                                    if (pMouseX >= x && pMouseX < x + boxWidth && pMouseY >= y && pMouseY < y + boxHeight) {
                                        drawElementTip(pPoseStack, element);
                                    }
                                    count++;
                                }
                                if (period == 7) {
                                    y = (boxHeight * 8.45f) + startY;
                                    x = (boxWidth * (count - 15)) + startX + boxWidth * 2;
                                    if (pMouseX >= x && pMouseX < x + boxWidth && pMouseY >= y && pMouseY < y + boxHeight) {
                                        drawElementTip(pPoseStack, element);
                                    }
                                    count++;
                                }
                                x = resetX;
                                y = resetY;
                            }
                        }
                        x += boxWidth;
                    }
                }
                x = startX;
                y += boxHeight;
            }
        }
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }

    private void drawElementTip(PoseStack pPoseStack, Element pElement) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, new ResourceLocation(ChemLib.MODID, String.format("textures/gui/elements/%s_tooltip.png", pElement.getChemicalName())));
        blit(pPoseStack, ((this.width - 276) / 2) - 55, ((this.height - (7 * 28)) / 2) - 30, 274, 80, 0, 0, 40, 40, 40, 40);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}