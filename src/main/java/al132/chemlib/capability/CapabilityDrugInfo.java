package al132.chemlib.capability;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;

public class CapabilityDrugInfo {

   //@CapabilityInject(ChemLibDrugInfo.class)
    public static Capability<ChemLibDrugInfo> DRUG_INFO = CapabilityManager.get(new CapabilityToken<>(){});

    public static LazyOptional<ChemLibDrugInfo> getPlayerDrugInfo(Player player) {
        return player.getCapability(DRUG_INFO);
    }
}