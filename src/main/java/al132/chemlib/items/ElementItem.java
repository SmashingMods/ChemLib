package al132.chemlib.items;

import al132.chemlib.chemistry.ElementRegistry;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ElementItem extends BaseItem implements IChemical {

    public final int atomicNumber;
    private final String abbreviation;
    public final String internalName;

    public ElementItem(String name, int atomicNumber, String abbreviation) {
        super("element_" + name, new Item.Properties());
        //setRegistryName(new ResourceLocation(ChemLib.MODID, "element_" + name));
        // ModItems.items.add(this);
        this.internalName = name;
        this.atomicNumber = atomicNumber;
        this.abbreviation = abbreviation;
        ElementRegistry.elements.put(atomicNumber, this);
    }


    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new StringTextComponent(abbreviation + " - " + atomicNumber));
    }

    @Override
    public String getAbbreviation() {
        return this.abbreviation;
    }

    @Override
    public String getChemicalName() {
        return this.internalName;
    }

    @Override
    public Item getItem() {
        return this;
    }

    @Override
    public int getBurnTime(ItemStack itemStack) {
        switch (this.atomicNumber) {
            case 1:
                return 20;
            case 6:
                return 200;
            default:
                return 0;
        }
    }
}