package al132.chemlib.chemistry;

import al132.chemlib.Utils;
import al132.chemlib.items.IChemical;
import al132.chemlib.items.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Optional;

public class ChemicalStack implements IChemical {

    public IChemical chemical;
    public int quantity;

    public ChemicalStack(IChemical chemical, int quantity) {
        this.chemical = chemical;
        this.quantity = quantity;
    }

    public ChemicalStack(IChemical chemical) {
        this(chemical, 1);
    }

    public ChemicalStack(String name) {
        this(name, 1);
    }

    public ChemicalStack(String name, int quantity) {
        this(lookup(name).get(), quantity);
    }

    public static Optional<IChemical> lookup(String name) {
        return ModItems.items.stream()
                .filter(x -> x instanceof IChemical && ((IChemical) x).getChemicalName().equals(name))
                .map(x -> (IChemical) x)
                .findFirst();
    }

    public ItemStack toItemStack() {
        return new ItemStack(chemical.asItem(), quantity);
    }

    @Override
    public String getAbbreviation() {
        return Utils.getAbbreviation(this);
    }

    @Override
    public String getChemicalName() {
        return null;
    }

    @Override
    public Item asItem() {
        return chemical.asItem();
    }
}
