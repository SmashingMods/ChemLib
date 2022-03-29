package al132.chemlib.items;

import net.minecraft.world.level.ItemLike;

public interface IChemical extends ItemLike {

    String getAbbreviation();

    String getChemicalName();
}