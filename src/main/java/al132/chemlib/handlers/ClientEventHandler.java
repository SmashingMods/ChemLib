package al132.chemlib.handlers;

import al132.chemlib.ChemLib;
import al132.chemlib.items.CompoundItem;
import al132.chemlib.items.IngotItem;
import al132.chemlib.items.ModItems;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT, modid = ChemLib.MODID)
public class ClientEventHandler {


    @SubscribeEvent
    public static void colorStuff(final ColorHandlerEvent.Item e) {
        ItemColors colors = e.getItemColors();
        //         return p_210238_1_ > 0 ? -1 : PotionUtils.getColor(p_210238_0_);
        ModItems.items.stream()
                .filter(item -> item instanceof CompoundItem)
                .forEach(item -> colors.register((stack, tintIndex) -> ((CompoundItem) item).getColor(stack,tintIndex), item));
        ModItems.items.stream()
                .filter(item -> item instanceof IngotItem)
                .forEach(item -> colors.register((stack, tintIndex) -> ((IngotItem) item).color, item));
        //final IItemColor handler = ((stack, tintIndex)-> colors.getColor(stack,tintIndex));
    }
}