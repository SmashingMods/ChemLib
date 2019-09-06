package al132.chemlib.capability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;

public class CapabilityDrugInfo {

    @CapabilityInject(ChemLibDrugInfo.class)
    public static Capability<ChemLibDrugInfo> DRUG_INFO = null;

    public static LazyOptional<ChemLibDrugInfo> getPlayerDrugInfo(PlayerEntity player) {
        return player.getCapability(DRUG_INFO);
    }
}