package al132.chemlib.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class GuiPeriodicTable extends Screen {
    public GuiPeriodicTable() {
        super(new StringTextComponent("periodic_table"));

    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        super.render(ms, mouseX, mouseY, partialTicks);
        Minecraft.getInstance().textureManager.bind(new ResourceLocation("chemlib", "textures/gui/periodic_table.png"));
        int relX = 0;
        int relY = 0;
        int xSize = 20;
        int ySize = 30;

        this.blit(ms, relX, relY, 0, 0, xSize, ySize);

    }
}
