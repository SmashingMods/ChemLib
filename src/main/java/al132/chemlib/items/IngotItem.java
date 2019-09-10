package al132.chemlib.items;

import net.minecraft.item.Item;

import java.awt.*;

public class IngotItem extends BaseItem {

    public int color;

    public IngotItem(String name, Color color) {
        super("ingot_" + name, new Item.Properties());
        //setRegistryName(new ResourceLocation(ChemLib.MODID,"ingot_" + name));
        //ModItems.items.add(this);
        this.color = color.getRGB();
        //setCreativeTab(Reference.creativeTab);
    }
}