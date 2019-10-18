package al132.chemlib.items;

import net.minecraft.util.IItemProvider;

public interface IChemical extends IItemProvider {

    String getAbbreviation();

    String getChemicalName();
}