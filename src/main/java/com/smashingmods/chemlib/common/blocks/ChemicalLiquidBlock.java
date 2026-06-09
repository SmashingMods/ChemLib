package com.smashingmods.chemlib.common.blocks;

import com.smashingmods.chemlib.api.Chemical;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class ChemicalLiquidBlock extends LiquidBlock {

    private final String chemicalName;

    public ChemicalLiquidBlock(FlowingFluid pFluid, String pChemicalName, BlockBehaviour.Properties pProperties) {
        super(pFluid, pProperties);
        this.chemicalName = pChemicalName;
    }

    private Optional<Chemical> getChemical() {
        AtomicReference<Chemical> atomicChemical = new AtomicReference<>();
        ItemRegistry.getElementByName(chemicalName).ifPresent(atomicChemical::set);
        ItemRegistry.getCompoundByName(chemicalName).ifPresent(atomicChemical::set);
        return Optional.ofNullable(atomicChemical.get());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        if (pEntity instanceof LivingEntity livingEntity) {
            getChemical().ifPresent(chemical -> {
                for (MobEffectInstance effectInstance : chemical.getEffects()) {
                    MobEffectInstance copyEffect = new MobEffectInstance(effectInstance.getEffect(), effectInstance.getDuration(), effectInstance.getAmplifier());
                    livingEntity.addEffect(copyEffect);
                }
            });

        }
        super.entityInside(pState, pLevel, pPos, pEntity);
    }
}
