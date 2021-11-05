package al132.chemlib.items;

import al132.chemlib.client.GuiPeriodicTable;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class PeriodicTableItem extends BaseItem {
    public PeriodicTableItem() {
        super("periodic_table", new Item.Properties());
    }


    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (world.isClientSide) {
            //NetworkHooks.openGui(player,);
            //Minecraft.getInstance().displayGuiScreen(new GuiPeriodicTable());
        }
        return ActionResult.pass(player.getItemInHand(hand));
    }
}