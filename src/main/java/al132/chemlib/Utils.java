package al132.chemlib;

import al132.chemlib.chemistry.ChemicalStack;
import al132.chemlib.items.CompoundItem;
import com.google.common.collect.Lists;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Optional;

public class Utils {

    public static Optional<Item> getItem(String namespace, String path) {
        return Optional.ofNullable(ForgeRegistries.ITEMS.getValue(new ResourceLocation(namespace, path)));
    }

    public static Optional<Item> getChemItem(String name) {
        return getItem("chemlib", name);
    }

    public static String getAbbreviation(ChemicalStack component) {
        return getAbbreviation(Lists.newArrayList(component));
    }

    public static String getSubscript(String n) {
        //subscriptZeroCodepoint is subscript 0 unicode char, adding 1-9 gives the subscript for that num
        //i.e. ₀ + 3 = ₃
        final int subscriptZeroCodepoint = 0x2080;//Character.codePointAt("₀",0) + Character.codePointAt("₀",1);//Character.codePointAt("₀", 0);
        StringBuilder builder = new StringBuilder();
        for (char c : n.toCharArray()) {
            builder.append(Character.toChars(subscriptZeroCodepoint + Character.getNumericValue(c)));
        }
        return builder.toString();
    }

    public static String getSubscript(int n) {
        return getSubscript(Integer.toString(n));
    }

    public static String getAbbreviation(List<ChemicalStack> components) {
        StringBuilder builder = new StringBuilder();
        for (ChemicalStack component : components) {
            String abbreviation = component.getChemical().getAbbreviation();
            if (component.getChemical() instanceof CompoundItem) {
                builder.append("(" + abbreviation + ")");
            } else {
                builder.append(abbreviation);
            }
            if (component.quantity > 1) {
                builder.append(getSubscript(component.quantity));
            }
        }
        return builder.toString();
    }
}
