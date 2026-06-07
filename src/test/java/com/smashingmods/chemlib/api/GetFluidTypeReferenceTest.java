package com.smashingmods.chemlib.api;

import net.minecraft.DetectedVersion;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.fluids.FluidType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tier-2 test for {@link Chemical#getFluidTypeReference()} -- the {@code default} method that resolves
 * a chemical's name to a {@link FluidType} across three registries (the {@code chemlib:} fluid-type
 * register, {@code NeoForgeRegistries.FLUID_TYPES}, then {@code BuiltInRegistries.FLUID}).
 *
 * <p>Exercised through an anonymous {@link Chemical} whose {@code getChemicalName()} returns a name
 * that resolves to no registered fluid -- so NO {@code Item} construction and no unfreezing of
 * {@code BuiltInRegistries.ITEM}. The real method runs its three real registry lookups, so this is
 * Tier-2 ({@code Bootstrap.bootStrap()} is needed for the final {@code BuiltInRegistries.FLUID} probe).
 *
 * <p>This pins the fix that a true miss yields {@code Optional.empty()}. The final branch reads from
 * {@code BuiltInRegistries.FLUID}, a {@code DefaultedRegistry} whose {@code get(...)} returns the
 * non-null {@code Fluids.EMPTY} on a miss; the earlier implementation wrapped that in a present
 * {@code Optional} (of {@code EMPTY}'s fluid type), so a non-fluid chemical falsely reported present
 * to callers probing {@code isPresent()}. The fix switched to the non-defaulted {@code getOptional},
 * so this assertion fails if that regression returns.
 */
class GetFluidTypeReferenceTest {

    @BeforeAll
    static void boot() {
        SharedConstants.setVersion(DetectedVersion.BUILT_IN);
        Bootstrap.bootStrap();
    }

    /**
     * Minimal {@link Chemical} reporting {@code name} as its chemical name; only
     * {@code getFluidTypeReference} (which reads {@code getChemicalName()}) is exercised, so the other
     * abstract methods return dummy values.
     */
    private static Chemical named(String name) {
        return new Chemical() {
            @Override
            public String getChemicalName() {
                return name;
            }

            @Override
            public String getAbbreviation() {
                return "T";
            }

            @Override
            public MatterState getMatterState() {
                return MatterState.SOLID;
            }

            @Override
            public String getChemicalDescription() {
                return "";
            }

            @Override
            public List<MobEffectInstance> getEffects() {
                return List.of();
            }

            @Override
            public int getColor() {
                return 0;
            }

            @Override
            public Item asItem() {
                return null;
            }
        };
    }

    @Test
    void getFluidTypeReference_unresolvedChemicalNameIsEmpty() {
        // A name no register resolves to a fluid -- the BuiltInRegistries.FLUID fallback returns the
        // defaulted EMPTY fluid, but the method must still report empty rather than EMPTY's fluid type.
        assertTrue(named("definitely_not_a_real_fluid_xyz").getFluidTypeReference().isEmpty());
    }

    @Test
    void getFluidTypeReference_unparseableChemicalNameIsEmpty() {
        // An uppercase name is not a valid ResourceLocation path, so tryParse returns null; the guarded
        // final branch must yield empty rather than NPE on the null ResourceLocation.
        assertTrue(named("NotAValidResourceLocation").getFluidTypeReference().isEmpty());
    }
}
