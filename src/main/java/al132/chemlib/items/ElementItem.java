package al132.chemlib.items;

import al132.chemlib.ChemLib;
import al132.chemlib.chemistry.ElementRegistry;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class ElementItem extends BaseItem implements IChemical {

    public final int atomicNumber;
    private final String abbreviation;
    public final String internalName;

    public ElementItem(String name, int atomicNumber, String abbreviation) {
        super("element_" + name, new Item.Properties());
        this.internalName = name;
        this.atomicNumber = atomicNumber;
        this.abbreviation = abbreviation;
        ElementRegistry.elements.put(atomicNumber, this);
    }


    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltips, ITooltipFlag flag) {
        tooltips.add(new StringTextComponent(getAbbreviation()).withStyle(ChemLib.CHEM_TOOLTIP_COLOR));
        tooltips.add(new StringTextComponent("(" + atomicNumber + ")"));
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