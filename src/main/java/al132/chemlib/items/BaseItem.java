package al132.chemlib.items;

import al132.chemlib.ChemLib;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class BaseItem extends Item {


    public BaseItem(String name, Properties properties) {
        super(properties.group(ChemLib.ITEM_GROUP));
        setRegistryName(new ResourceLocation(ChemLib.MODID, name));
        ModItems.items.add(this);
    }
}