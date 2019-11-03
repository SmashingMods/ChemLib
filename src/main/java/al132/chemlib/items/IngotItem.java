package al132.chemlib.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.awt.*;

public class IngotItem extends BaseItem {

    public int color;

    public IngotItem(String name, Color color) {
        super("ingot_" + name, new Item.Properties());
        this.color = color.getRGB();
    }

    public int getColor(ItemStack stack, int tintIndex) {
        return color;
    }
}