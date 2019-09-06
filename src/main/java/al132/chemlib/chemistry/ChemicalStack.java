package al132.chemlib.chemistry;

import al132.chemlib.items.IChemical;
import al132.chemlib.items.ModItems;
import net.minecraft.item.ItemStack;

import java.util.Optional;

public class ChemicalStack {

    public IChemical chemical;
    public int quantity;

    public ChemicalStack(IChemical chemical, int quantity) {
        this.chemical = chemical;
        this.quantity = quantity;
    }

    public static Optional<IChemical> lookup(String name) {
        return ModItems.items.stream()
                .filter(x -> x instanceof IChemical && ((IChemical) x).getChemicalName().equals(name))
                .map(x -> (IChemical) x)
                .findFirst();
    }

    public ItemStack toItemStack() {
        return new ItemStack(chemical.getItem(), quantity);
    }
}
