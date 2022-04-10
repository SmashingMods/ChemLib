package com.smashingmods.chemlib.capability;

import net.minecraft.nbt.CompoundTag;

//Peeking off https://github.com/McJtyMods/TheOneProbe/blob/1.12/src/main/java/mcjty/theoneprobe/playerdata/PlayerGotNote.java
public class ChemLibDrugInfo {

    public int psilocybinTicks = 0;
    public float cumulativeFOVModifier = 1.0f;

    public ChemLibDrugInfo() {
    }

    public void saveNBTData(CompoundTag compound) {
        compound.putInt("psilocybinTicks", psilocybinTicks);
        compound.putFloat("cumulativeFOVModifier", cumulativeFOVModifier);
    }

    public void loadNBTData(CompoundTag compound) {
        psilocybinTicks = compound.getInt("psilocybinTicks");
        cumulativeFOVModifier = compound.getFloat("cumulativeFOVModifier");
    }
}
