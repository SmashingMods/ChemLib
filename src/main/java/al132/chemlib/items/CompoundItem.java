package al132.chemlib.items;

import al132.chemlib.ChemLib;
import al132.chemlib.Utils;
import al132.chemlib.chemistry.ChemicalStack;
import al132.chemlib.chemistry.CompoundRegistry;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.UseAction;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class CompoundItem extends BaseItem implements IChemical {
    private List<ChemicalStack> components;
    public int color;
    public final String internalName;
    public final int shiftedSlots;
    public int burnTime = 0;
    private String abbreviation = "";

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


    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltips, ITooltipFlag flag) {
        tooltips.add(new StringTextComponent(getAbbreviation()).withStyle(ChemLib.CHEM_TOOLTIP_COLOR));
        if (DankMolecule.hasDankMolecule(stack)) {
            tooltips.add(new StringTextComponent(I18n.get("tooltip.chemlib.generic_potion_compound")));
        }
    }

    public String getAbbreviation() {
        if (abbreviation.isEmpty()) abbreviation = Utils.getAbbreviation(components);
        return abbreviation;
    }

    public String getASCIIAbbreviation() {
        return this.getAbbreviation().chars()
                .map(x -> (x >= 0x2080) ? x - 0x2080 + 0x0030 : x)
                .filter(x -> x != 0x0028 && x != 0x0029)
                .collect(StringBuilder::new,
                        StringBuilder::appendCodePoint,
                        StringBuilder::append).toString();
    }

    @Override
    public String getChemicalName() {
        return this.internalName;
    }

    public List<ChemicalStack> getComponents() {
        return this.components;
    }

    public List<ItemStack> getComponentStacks() {
        return this.components.stream().map(ChemicalStack::toItemStack).collect(Collectors.toList());
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, World world, LivingEntity entity) {
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
    public boolean isEdible() {
        if (DankMolecule.hasDankMolecule(new ItemStack(this))) return true;
        else return super.isEdible();
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        if (DankMolecule.hasDankMolecule(stack)) return 32;
        else return super.getUseDuration(stack);
    }

    @Override
    public UseAction getUseAnimation(ItemStack stack) {
        if (DankMolecule.hasDankMolecule(stack)) return UseAction.DRINK;
        else return super.getUseAnimation(stack);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (DankMolecule.hasDankMolecule(stack)) {
            return DrinkHelper.useDrink(world, player, hand);
        } else return super.use(world, player, hand);
    }

    @Override
    public int getBurnTime(ItemStack itemStack) {
        return burnTime;
    }

    public int getColor(ItemStack stack, int tintIndex) {
        return tintIndex > 0 ? -1 : color;
    }
}
