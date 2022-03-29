package al132.chemlib.chemistry;

import al132.chemlib.items.ElementItem;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.Optional;

public class ElementRegistry {

    public static BiMap<Integer, ElementItem> elements = HashBiMap.create();

    public static Optional<ElementItem> getByName(String name) {
        return elements.values().stream().filter(x -> x.internalName.equals(name)).findFirst();
    }
}