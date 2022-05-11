package com.smashingmods.chemlib.items;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PeriodicTableItem extends BaseItem {
    public PeriodicTableItem() {
        super("periodic_table", new Item.Properties());
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        if (world.isClientSide) {
            //NetworkHooks.openGui(player,);
            //Minecraft.getInstance().displayGuiScreen(new GuiPeriodicTable());
        }
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }
}