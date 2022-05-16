package com.smashingmods.chemlib.common.items;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class PeriodicTableItem extends Item {

    public PeriodicTableItem() {
        super(new Item.Properties().tab(CreativeModeTab.TAB_MISC));
        setRegistryName("periodic_table");
    }

    @Override
    @Nonnull
    public InteractionResultHolder<ItemStack> use(Level pLevel, @Nonnull Player pPlayer, @Nonnull InteractionHand pUsedHand) {
        if (pLevel.isClientSide) {
            //NetworkHooks.openGui(player,);
            //Minecraft.getInstance().displayGuiScreen(new GuiPeriodicTable());
        }
        return InteractionResultHolder.pass(pPlayer.getItemInHand(pUsedHand));
    }
}