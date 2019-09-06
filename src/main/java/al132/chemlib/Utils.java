package al132.chemlib;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

public class Utils {

    public static Optional<Item> getItem(String namespace, String path) {
        return Optional.ofNullable(ForgeRegistries.ITEMS.getValue(new ResourceLocation(namespace, path)));
    }

    public static Optional<Item> getChemItem(String name) {
        return getItem("chemlib", name);
    }
}
