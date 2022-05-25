package com.smashingmods.chemlib.common.items;

import com.mojang.blaze3d.MethodsReturnNonnullByDefault;
import com.smashingmods.chemlib.client.PeriodicTableScreen;
import com.smashingmods.chemlib.common.registry.ItemRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PeriodicTableItem extends Item {

    public PeriodicTableItem() {
        super(new Item.Properties().tab(ItemRegistry.MISC_TAB).stacksTo(1));
    }

    @Override
    @Nonnull
    @OnlyIn(Dist.CLIENT)
    public InteractionResultHolder<ItemStack> use(Level pLevel, @Nonnull Player pPlayer, @Nonnull InteractionHand pUsedHand) {
        if (pLevel.isClientSide()) {
            Minecraft.getInstance().setScreen(new PeriodicTableScreen());
        }
        return InteractionResultHolder.success(pPlayer.getItemInHand(pUsedHand));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public InteractionResult useOn(UseOnContext pContext) {
        if (pContext.getLevel().isClientSide()) {
            Minecraft.getInstance().setScreen(new PeriodicTableScreen());
        }
        return InteractionResult.SUCCESS;
    }
}