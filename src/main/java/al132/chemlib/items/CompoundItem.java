package al132.chemlib.items;

import al132.chemlib.chemistry.ChemicalStack;
import al132.chemlib.chemistry.CompoundRegistry;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class CompoundItem extends BaseItem implements IChemical, IItemColor {
    private List<ChemicalStack> components;
    private int color;
    public final String internalName;
    public final int shiftedSlots;
    public int burnTime = 0;

    /*public CompoundItem(String name, Color color, List<ChemicalStack> components) {
        this(name, color, components, 0);
    }*/

    public CompoundItem(String name, Color color, List<ChemicalStack> components, int shiftedSlots) {
        super("compound_" + name, new Item.Properties());
        this.components = components;
        this.color = color.getRGB();
        this.internalName = name;
        CompoundRegistry.compounds.add(this);
        this.shiftedSlots = shiftedSlots;
    }


    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new StringTextComponent(getAbbreviation()));
        if (DankMolecule.hasDankMolecule(stack)) {
            tooltip.add(new StringTextComponent(I18n.format("tooltip.chemlib.generic_potion_compound")));
        }
    }

    public String getAbbreviation() {
        StringBuilder builder = new StringBuilder();
        for (ChemicalStack component : components) {
            String abbreviation = ((IChemical) component.chemical).getAbbreviation();
            if (component.chemical instanceof CompoundItem) {
                builder.append("(" + abbreviation + ")");
            } else {
                builder.append(abbreviation);
            }
            if (component.quantity > 1) {
                //subscriptZeroCodepoint is subscript 0 unicode char, adding 1-9 gives the subscript for that num
                //i.e. ₀ + 3 = ₃
                int subscriptZeroCodepoint = 0x2080;//Character.codePointAt("₀",0) + Character.codePointAt("₀",1);//Character.codePointAt("₀", 0);

                for (char c : Integer.toString(component.quantity).toCharArray()) {
                    builder.append(Character.toChars(subscriptZeroCodepoint + Character.getNumericValue(c)));
                }

            }
        }
        return builder.toString();
    }

    @Override
    public String getChemicalName() {
        return this.internalName;
    }

    @Override
    public int getColor(ItemStack stack, int color) {
        return this.color;
    }

    public List<ChemicalStack> getComponents() {
        return this.components;
    }

    public List<ItemStack> getComponentStacks() {
        return this.components.stream().map(ChemicalStack::toItemStack).collect(Collectors.toList());
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, LivingEntity entity) {
        if (entity instanceof PlayerEntity) {
            DankMolecule.getDankMolecule(stack)
                    .ifPresent(molecule -> {
                        molecule.activateForPlayer((PlayerEntity) entity);
                        stack.shrink(1);
                    });
        }
        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        if (DankMolecule.hasDankMolecule(stack)) return 36;
        else return super.getUseDuration(stack);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        if (DankMolecule.hasDankMolecule(stack)) return UseAction.DRINK;
        else return super.getUseAction(stack);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity playerIn, Hand handIn) {
        playerIn.setActiveHand(handIn);
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (DankMolecule.hasDankMolecule(stack)) {
            return new ActionResult<>(ActionResultType.SUCCESS, playerIn.getHeldItem(handIn));
        } else return new ActionResult<>(ActionResultType.PASS, stack);
    }

    @Override
    public Item getItem() {
        return this;
    }

    @Override
    public int getBurnTime(ItemStack itemStack) {
        return burnTime;
    }
}
