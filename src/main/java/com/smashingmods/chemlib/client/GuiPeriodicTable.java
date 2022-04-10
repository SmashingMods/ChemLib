package com.smashingmods.chemlib.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class GuiPeriodicTable extends Screen {
    public GuiPeriodicTable() {
        super(new TextComponent("periodic_table"));

    }

    @Override
    public void render(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
        super.render(ms, mouseX, mouseY, partialTicks);
        Minecraft.getInstance().textureManager.bindForSetup(new ResourceLocation("chemlib", "textures/gui/periodic_table.png"));
        int relX = 0;
        int relY = 0;
        int xSize = 20;
        int ySize = 30;

        this.blit(ms, relX, relY, 0, 0, xSize, ySize);

    }
}
