package al132.chemlib.handlers;

import al132.chemlib.ChemLib;
import al132.chemlib.items.ModItems;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT, modid = ChemLib.MODID)
public class ClientEventHandler {

    @SubscribeEvent
    public static void colorStuff(ColorHandlerEvent.Item e) {
        ModItems.items.stream()
                .filter(item -> item instanceof IItemColor)
                .forEach(item -> e.getItemColors().register(((IItemColor) item), item));
    }
}