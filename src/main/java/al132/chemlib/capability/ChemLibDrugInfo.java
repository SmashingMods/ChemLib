package al132.chemlib.capability;

import net.minecraft.nbt.CompoundNBT;

//Peeking off https://github.com/McJtyMods/TheOneProbe/blob/1.12/src/main/java/mcjty/theoneprobe/playerdata/PlayerGotNote.java
public class ChemLibDrugInfo {

    public int psilocybinTicks = 0;
    public float cumulativeFOVModifier = 1.0f;

    public ChemLibDrugInfo() {
    }

    public void saveNBTData(CompoundNBT compound) {
        compound.putInt("psilocybinTicks", psilocybinTicks);
        compound.putFloat("cumulativeFOVModifier", cumulativeFOVModifier);
    }

    public void loadNBTData(CompoundNBT compound) {
        psilocybinTicks = compound.getInt("psilocybinTicks");
        cumulativeFOVModifier = compound.getFloat("cumulativeFOVModifier");
    }
}
