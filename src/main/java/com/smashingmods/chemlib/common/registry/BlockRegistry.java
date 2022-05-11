package com.smashingmods.chemlib.common.registry;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.blocks.BlockLamp;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockRegistry {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ChemLib.MODID);

    public static final RegistryObject<Block> NEON_LAMP = BLOCKS.register("neon_lamp", BlockLamp::new);
    public static final RegistryObject<Block> HELIUM_LAMP = BLOCKS.register("helium_lamp", BlockLamp::new);
    public static final RegistryObject<Block> ARGON_LAMP = BLOCKS.register("argon_lamp", BlockLamp::new);
    public static final RegistryObject<Block> KRYPTON_LAMP = BLOCKS.register("krypton_lamp", BlockLamp::new);
    public static final RegistryObject<Block> XENON_LAMP = BLOCKS.register("xenon_lamp", BlockLamp::new);

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
