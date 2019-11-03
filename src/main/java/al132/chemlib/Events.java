package al132.chemlib;

import al132.chemlib.capability.CapabilityDrugInfo;
import al132.chemlib.capability.ChemLibDrugDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class Events {

    @SubscribeEvent
    public void onEntityConstructing(AttachCapabilitiesEvent<Entity> e) {
        if (e.getObject() instanceof PlayerEntity) {
            e.getObject().getCapability(CapabilityDrugInfo.DRUG_INFO).ifPresent(cap ->
                    e.addCapability(new ResourceLocation(ChemLib.MODID, "DrugInfo"), new ChemLibDrugDispatcher()));
        }
    }
}