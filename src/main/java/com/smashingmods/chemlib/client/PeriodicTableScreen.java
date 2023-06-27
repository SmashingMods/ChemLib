package com.smashingmods.chemlib.client;

import com.mojang.blaze3d.MethodsReturnNonnullByDefault;
import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.api.Element;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class PeriodicTableScreen extends Screen {

    private static final ResourceLocation PERIODIC_TABLE = new ResourceLocation(ChemLib.MODID, "textures/gui/periodic_table.png");

    public PeriodicTableScreen() {
        super(MutableComponent.create(new TranslatableContents("item.chemlib.periodic_table", null, TranslatableContents.NO_ARGS)));
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {

        int imageWidth = 2000;
        int imageHeight = 1016;
        int displayWidth = imageWidth / 4;
        int displayHeight = imageHeight / 4;
        int leftPos = (this.width - displayWidth) / 2;
        int topPos = (this.height - displayHeight) / 2;

        pGuiGraphics.blit(PERIODIC_TABLE, leftPos, topPos, displayWidth, displayHeight, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
        pGuiGraphics.drawCenteredString(Minecraft.getInstance().font, MutableComponent.create(new TranslatableContents("chemlib.screen.periodic_table", null, TranslatableContents.NO_ARGS)).withStyle(ChatFormatting.BOLD), width / 2, 24, 0xFFFFFF);

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
                                    drawElementTip(pGuiGraphics, element);
                                }
                            } else {
                                double resetX = x;
                                double resetY = y;
                                if (period == 6) {
                                    y = (boxHeight * 7.45f) + startY;
                                    x = (boxWidth * count) + startX + boxWidth * 2;
                                    if (pMouseX >= x && pMouseX < x + boxWidth && pMouseY >= y && pMouseY < y + boxHeight) {
                                        drawElementTip(pGuiGraphics, element);
                                    }
                                    count++;
                                }
                                if (period == 7) {
                                    y = (boxHeight * 8.45f) + startY;
                                    x = (boxWidth * (count - 15)) + startX + boxWidth * 2;
                                    if (pMouseX >= x && pMouseX < x + boxWidth && pMouseY >= y && pMouseY < y + boxHeight) {
                                        drawElementTip(pGuiGraphics, element);
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
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    private void drawElementTip(GuiGraphics pGuiGraphics, Element pElement) {
        pGuiGraphics.blit(new ResourceLocation(ChemLib.MODID, String.format("textures/gui/elements/%s_tooltip.png", pElement.getChemicalName())), ((this.width - 276) / 2) - 55, ((this.height - (7 * 28)) / 2) - 30, 274, 80, 0, 0, 40, 40, 40, 40);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}