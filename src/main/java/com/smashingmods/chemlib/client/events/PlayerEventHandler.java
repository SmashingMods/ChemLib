package com.smashingmods.chemlib.client.events;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.api.ChemicalBlockType;
import com.smashingmods.chemlib.api.ChemicalItemType;
import com.smashingmods.chemlib.api.FireColors;
import com.smashingmods.chemlib.common.blocks.ChemicalBlock;
import com.smashingmods.chemlib.registry.BlockRegistry;
import com.smashingmods.chemlib.registry.ChemicalRegistry;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;

@Mod.EventBusSubscriber(modid = ChemLib.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class PlayerEventHandler {

    @SubscribeEvent
    public static void placeDust(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        ItemStack item = event.getItemStack();
        ArrayList<Item> burnableItems = new ArrayList<>();
        BlockPos pos = event.getPos();
        Block block = level.getBlockState(pos).getBlock();

        for (String burnableItem:ChemicalRegistry.BURNABLES) {
            if (ItemRegistry.getChemicalItemByNameAndType(burnableItem,ChemicalItemType.DUST).isPresent()) {
                burnableItems.add(ItemRegistry.getChemicalItemByNameAndType(burnableItem,ChemicalItemType.DUST).get());
            } else if (ItemRegistry.getChemicalItemByNameAndType(burnableItem,ChemicalItemType.COMPOUND).isPresent()) {
                burnableItems.add(ItemRegistry.getChemicalItemByNameAndType(burnableItem,ChemicalItemType.COMPOUND).get());
            }
        }

        if (burnableItems.contains(item.getItem())) {
            if (event.getFace() == Direction.UP && level.getBlockState(pos.above()).is(Blocks.AIR) && !BlockRegistry.getChemicalBlocksByType(ChemicalBlockType.DUST).contains(block)) {
                if (!level.isClientSide()) {
                    Block dust_block = BlockRegistry.getChemicalBlockByNameAndType(item.getItem().asItem().toString().replace("_dust",""),ChemicalBlockType.DUST).get();

                    level.setBlock(pos.above(), dust_block.defaultBlockState(),1);

                    if (!event.getEntity().isCreative()) {
                        item.setCount(item.getCount()-1);
                    }
                } else {
                    event.getEntity().swing(InteractionHand.MAIN_HAND);
                    level.playLocalSound(pos.getX(),pos.getY(),pos.getZ(),SoundEvents.SAND_PLACE, SoundSource.BLOCKS,1.0f,1.0f,false);
                }
            } else {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void burnDust(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        ItemStack item = event.getItemStack();
        BlockPos pos = event.getPos();
        Block block = level.getBlockState(pos).getBlock();
        String block_name = block.toString().replace("Block{chemlib:","").replace("}","");

        boolean isFlintAndSteel = item.getItem() == Items.FLINT_AND_STEEL;
        boolean isFireCharge = item.getItem() == Items.FIRE_CHARGE;


        if (ChemicalRegistry.fireColorMap.containsKey(block_name) && (isFlintAndSteel || isFireCharge)) {
            // consume / damage the igniters unless the player is in creative mode
            SoundEvent soundEvent = isFlintAndSteel ? SoundEvents.FLINTANDSTEEL_USE : SoundEvents.FIRECHARGE_USE;
            FireColors color = FireColors.valueOf(ChemicalRegistry.fireColorMap.get(block_name).toUpperCase());

            if (!event.getEntity().isCreative()) {
                if (isFlintAndSteel) {
                    item.hurtAndBreak(1,event.getEntity(),null);
                } else if (isFireCharge) {
                    item.setCount(item.getCount()-1);
                }
            }

            level.playLocalSound(pos.getX(),pos.getY(),pos.getZ(),soundEvent,SoundSource.PLAYERS,1.0f,1.0f,false);

            if (!level.isClientSide()) {
                // cancel the normal flint and steel / fire charge event and set the colored fire block accordingly
                event.setCanceled(true);
                BlockRegistry.getFireByColor(color).ifPresent(fire_block -> level.setBlock(pos,fire_block.get().defaultBlockState(),16));

            } else {
                // client side effects (sounds/particles...)
                event.getEntity().swing(InteractionHand.MAIN_HAND);
                level.playLocalSound(pos.getX(),pos.getY(),pos.getZ(),SoundEvents.LAVA_EXTINGUISH,SoundSource.BLOCKS,1.0f,1.0f,false);
            }
        }
    }

    @SubscribeEvent
    public static void onBreakDust(BlockEvent.BreakEvent event) {
        Level level = event.getPlayer().getLevel();
        ArrayList<Item> burnableItems = new ArrayList<>();
        BlockPos pos = event.getPos();
        String block_name = event.getState().getBlock().toString().replace("Block{chemlib:","").replace("_block}","");

        for (String burnableItem:ChemicalRegistry.BURNABLES) {
            if (ItemRegistry.getChemicalItemByNameAndType(burnableItem,ChemicalItemType.DUST).isPresent()) {
                burnableItems.add(ItemRegistry.getChemicalItemByNameAndType(burnableItem,ChemicalItemType.DUST).get());
            } else if (ItemRegistry.getChemicalItemByNameAndType(burnableItem,ChemicalItemType.COMPOUND).isPresent()) {
                burnableItems.add(ItemRegistry.getChemicalItemByNameAndType(burnableItem,ChemicalItemType.COMPOUND).get());
            }
        }

        if (ChemicalRegistry.fireColorMap.containsKey(block_name+"_block")) {

            for (Item item:burnableItems) {

                System.out.println(item.toString());
                System.out.println(block_name);

                boolean isSame = block_name.toString().equals(item.toString());

                System.out.println(isSame);

                if (isSame) {
                    ItemEntity dust = new ItemEntity(level,
                            pos.getX()+0.5f,
                            pos.getY(),
                            pos.getZ()+0.5f,
                            new ItemStack(item)
                    );
                    level.addFreshEntity(dust);
                }
            }
        }
    }
}
