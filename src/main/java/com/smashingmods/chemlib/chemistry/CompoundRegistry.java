package com.smashingmods.chemlib.chemistry;

import com.smashingmods.chemlib.items.CompoundItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompoundRegistry {

    public static List<CompoundItem> compounds = new ArrayList<>();

    public static Optional<CompoundItem> getByName(String name) {
        return compounds.stream().filter(x -> x.internalName.equals(name)).findFirst();
    }
}
