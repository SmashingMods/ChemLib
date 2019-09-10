package al132.chemlib.handlers;

import al132.chemlib.ChemLib;
import al132.chemlib.items.CompoundItem;
import al132.chemlib.items.IngotItem;
import al132.chemlib.items.ModItems;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT, modid = ChemLib.MODID)
public class ClientEventHandler {

    @SubscribeEvent
    public static void colorStuff(final FMLClientSetupEvent e) {
        ItemColors colors = e.getMinecraftSupplier().get().getItemColors();
        ModItems.items.stream()
                .filter(item -> item instanceof CompoundItem)
                .forEach(item -> colors.register((x, y) -> ((CompoundItem) item).color, item));
        ModItems.items.stream()
                .filter(item -> item instanceof IngotItem)
                .forEach(item -> colors.register((x, y) -> ((IngotItem) item).color, item));
    }
}