package com.smashingmods.chemlib.registry;

import com.smashingmods.chemlib.api.ChemicalItemType;
import com.smashingmods.chemlib.api.MatterState;
import com.smashingmods.chemlib.api.MetalType;

import java.util.EnumSet;

/**
 * Pure decision layer for {@link ChemicalRegistry}: given the already-parsed primitives of an
 * element or compound spec, decides which derived items/blocks/fluid that spec produces. Holds no
 * Minecraft/NeoForge references on purpose -- the registration side effects (ResourceLocation,
 * block/fluid properties, color/fluid_properties parsing, the actual DeferredRegister calls) stay
 * in {@link ChemicalRegistry}; only the branching decision lives here so it can be unit-tested
 * without a registry bootstrap.
 */
public final class ContentDerivation {

    private ContentDerivation() {
    }

    /**
     * What a single spec derives. {@code itemTypes} are the {@link ChemicalItemType}s to register
     * against the spec's holder; {@code metalBlock}/{@code lampBlock} request the corresponding
     * block + block item; {@code fluidSet} requests the fluid registration.
     */
    public record DerivedContent(EnumSet<ChemicalItemType> itemTypes, boolean metalBlock, boolean lampBlock, boolean fluidSet) {
    }

    /**
     * Derivation for an element. Mirrors the original {@code registerElements} branching: artificial
     * elements derive nothing; SOLID always derives DUST (METAL adds PLATE, and a non-{@code hasItem}
     * METAL additionally derives NUGGET/INGOT and a metal block); LIQUID/GAS without an existing fluid
     * register a fluid (and a lamp block when in group 18).
     */
    public static DerivedContent forElement(MatterState matterState, MetalType metalType, boolean artificial, boolean hasItem, boolean hasFluid, int group) {
        EnumSet<ChemicalItemType> itemTypes = EnumSet.noneOf(ChemicalItemType.class);
        boolean metalBlock = false;
        boolean lampBlock = false;
        boolean fluidSet = false;

        if (!artificial) {
            switch (matterState) {
                case SOLID -> {
                    if (metalType == MetalType.METAL) {
                        itemTypes.add(ChemicalItemType.PLATE);
                        if (!hasItem) {
                            itemTypes.add(ChemicalItemType.NUGGET);
                            itemTypes.add(ChemicalItemType.INGOT);
                            metalBlock = true;
                        }
                    }
                    itemTypes.add(ChemicalItemType.DUST);
                }
                case LIQUID, GAS -> {
                    if (!hasFluid) {
                        fluidSet = true;
                        if (group == 18) {
                            lampBlock = true;
                        }
                    }
                }
            }
        }

        return new DerivedContent(itemTypes, metalBlock, lampBlock, fluidSet);
    }

    /**
     * Derivation for a compound. Mirrors the original {@code registerCompounds} branching: a SOLID
     * without an existing item derives a COMPOUND item (plus a PLATE for polyvinyl_chloride);
     * LIQUID/GAS without an existing fluid registers a fluid.
     */
    public static DerivedContent forCompound(MatterState matterState, boolean hasItem, boolean hasFluid, String compoundName) {
        EnumSet<ChemicalItemType> itemTypes = EnumSet.noneOf(ChemicalItemType.class);
        boolean fluidSet = false;

        switch (matterState) {
            case SOLID -> {
                if (!hasItem) {
                    itemTypes.add(ChemicalItemType.COMPOUND);
                    if (compoundName.equals("polyvinyl_chloride")) {
                        itemTypes.add(ChemicalItemType.PLATE);
                    }
                }
            }
            case LIQUID, GAS -> {
                if (!hasFluid) {
                    fluidSet = true;
                }
            }
        }

        return new DerivedContent(itemTypes, false, false, fluidSet);
    }
}
