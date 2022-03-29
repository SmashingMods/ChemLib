package al132.chemlib.handlers;

import al132.chemlib.ChemLib;
import al132.chemlib.items.CompoundItem;
import al132.chemlib.items.IngotItem;
import al132.chemlib.items.ModItems;
import net.minecraft.client.color.item.ItemColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT, modid = ChemLib.MODID)
public class ClientEventHandler {


    @SubscribeEvent
    public static void colorStuff(final ColorHandlerEvent.Item e) {
        ItemColors colors = e.getItemColors();
        ModItems.items.stream()
                .filter(item -> item instanceof CompoundItem)
                .forEach(item -> colors.register(((CompoundItem) item)::getColor, item));
        ModItems.items.stream()
                .filter(item -> item instanceof IngotItem)
                .forEach(item -> colors.register((stack, tintIndex) -> ((IngotItem) item).color, item));
    }
}