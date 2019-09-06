package al132.chemlib.items;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.awt.*;

public class IngotItem extends BaseItem implements IItemColor {

    int color;

    public IngotItem(String name, Color color) {
        super("ingot_" + name, new Item.Properties());
        //setRegistryName(new ResourceLocation(ChemLib.MODID,"ingot_" + name));
        //ModItems.items.add(this);
        this.color = color.getRGB();
        //setCreativeTab(Reference.creativeTab);
    }

    @Override
    public int getColor(ItemStack stack, int color) {
        return this.color;
    }
}