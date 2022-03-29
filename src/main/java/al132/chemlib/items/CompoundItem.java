package al132.chemlib.items;

import al132.chemlib.ChemLib;
import al132.chemlib.Utils;
import al132.chemlib.chemistry.ChemicalStack;
import al132.chemlib.chemistry.CompoundRegistry;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
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
    //public void appendHoverText(ItemStack stack, @Nullable Level world, List<net.minecraft.network.chat.Component> tooltips, TooltipFlag p_41424_
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltips, TooltipFlag flag) {
        tooltips.add(new TextComponent(getAbbreviation()).withStyle(ChemLib.CHEM_TOOLTIP_COLOR));
        if (DankMolecule.hasDankMolecule(stack)) {
            tooltips.add(new TextComponent(I18n.get("tooltip.chemlib.generic_potion_compound")));
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
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {
        if (entity instanceof Player) {
            DankMolecule.getDankMolecule(stack)
                    .ifPresent(molecule -> {
                        molecule.activateForPlayer((Player) entity);
                        stack.shrink(1);
                    });
        }
        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        if (DankMolecule.hasDankMolecule(stack)) return 32;
        else return super.getUseDuration(stack);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        if (DankMolecule.hasDankMolecule(stack)) return UseAnim.DRINK;
        else return super.getUseAnimation(stack);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player playerIn, InteractionHand handIn) {
   //     public ActionResult<ItemStack> onItemRightClick(Level world, Player playerIn, InteractionHand handIn) {
       // playerIn.setActiveHand(handIn);
        ItemStack stack = playerIn.getItemInHand(handIn);
        if (DankMolecule.hasDankMolecule(stack)) {
            return ItemUtils.startUsingInstantly(world,playerIn,handIn);
            //return new InteractionResultHolder(InteractionResult.SUCCESS, playerIn.getItemInHand(handIn));
        } else return new InteractionResultHolder<>(InteractionResult.PASS, stack);
    }

    //@Override
    // public int getBurnTime(ItemStack itemStack) {
    //    return burnTime;
    // }

    public int getColor(ItemStack stack, int tintIndex) {
        return tintIndex > 0 ? -1 : color;
    }
}