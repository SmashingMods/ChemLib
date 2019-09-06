package al132.chemlib.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

//Peeking off https://github.com/McJtyMods/TheOneProbe/blob/1.12/src/main/java/mcjty/theoneprobe/playerdata/PropertiesDispatcher.java
public class ChemLibDrugDispatcher implements ICapabilityProvider, INBTSerializable<CompoundNBT> {

    private ChemLibDrugInfo drugInfo = new ChemLibDrugInfo();


    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull final Capability<T> capability, Direction direction) {
        if (capability == CapabilityDrugInfo.DRUG_INFO) {
            return LazyOptional.of(() -> drugInfo).cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        drugInfo.saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        drugInfo.loadNBTData(nbt);
    }
}