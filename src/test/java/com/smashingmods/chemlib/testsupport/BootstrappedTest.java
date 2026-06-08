package com.smashingmods.chemlib.testsupport;

import net.minecraft.DetectedVersion;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Shared base for tests that need Minecraft's built-in registries populated. Extend this and the
 * {@code SharedConstants.setVersion(...)} + {@link Bootstrap#bootStrap()} incantation runs once before the
 * subclass's tests, instead of every bootstrap-needing test class redeclaring its own {@code @BeforeAll}.
 *
 * <p>{@code @BeforeAll} is inherited, so each subclass runs {@link #bootstrap()} once for its own container.
 * {@link Bootstrap#bootStrap()} guards itself against re-entry, so inheriting it per subclass is harmless.</p>
 *
 * <p>Subclasses use vanilla registry entries (e.g. {@code Items.STONE}, {@code MobEffects.POISON}) that
 * {@link Bootstrap#bootStrap()} registers; constructing a mod {@code Item} would instead trigger an
 * intrusive-holder registry write that needs the registry unfrozen, which these tests neither need nor want.</p>
 */
public abstract class BootstrappedTest {

    @BeforeAll
    static void bootstrap() {
        primeLoadingModList();
        SharedConstants.setVersion(DetectedVersion.BUILT_IN);
        Bootstrap.bootStrap();
    }

    /**
     * Seeds {@code LoadingModList} with an empty instance before {@link Bootstrap#bootStrap()} runs. At 1.21.1
     * NeoForge patches {@code FeatureFlags.<clinit>} (which {@link Bootstrap#bootStrap()} triggers) to call
     * {@code FeatureFlagLoader.loadModdedFlags(...)}, which dereferences {@code LoadingModList.get()}. That
     * static is null in a plain-JUnit JVM (no FML launch sets it), so the bare bootstrap NPEs during the
     * {@code FeatureFlags} static initialiser; the resulting {@code ExceptionInInitializerError} poisons
     * {@code FeatureFlags} for the whole JVM and leaves the built-in registries empty. {@code LoadingModList.of(...)}
     * is the factory a launch uses to populate {@code INSTANCE}; an all-empty call gives a non-null list whose
     * {@code getModFiles()} is empty, so {@code loadModdedFlags} registers no modded flags and does not NPE.
     * The factory is reached reflectively to avoid pinning the test source to FML's internal mod-discovery
     * types; that {@code of(...)} sets the static is fixed by the platform.
     */
    private static void primeLoadingModList() {
        try {
            Class<?> loadingModList = Class.forName("net.neoforged.fml.loading.LoadingModList");
            Method of = loadingModList.getDeclaredMethod("of", List.class, List.class, List.class, List.class, Map.class);
            of.setAccessible(true);
            of.invoke(null, List.of(), List.of(), List.of(), List.of(), Map.of());
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Failed to prime LoadingModList for the test bootstrap", exception);
        }
    }
}
