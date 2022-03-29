package al132.chemlib;

import al132.chemlib.capability.CapabilityDrugInfo;
import al132.chemlib.capability.ChemLibDrugDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class Events {

    @SubscribeEvent
    public void onEntityConstructing(AttachCapabilitiesEvent<Entity> e) {
        if (e.getObject() instanceof Player) {
            e.getObject().getCapability(CapabilityDrugInfo.DRUG_INFO).ifPresent(cap ->
                    e.addCapability(new ResourceLocation(ChemLib.MODID, "DrugInfo"), new ChemLibDrugDispatcher()));
        }
    }
}