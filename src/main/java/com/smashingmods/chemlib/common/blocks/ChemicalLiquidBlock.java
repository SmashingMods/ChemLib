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
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class ChemicalLiquidBlock extends LiquidBlock {

    private final String chemicalName;

    public ChemicalLiquidBlock(Supplier<? extends FlowingFluid> pFluid, String pChemicalName) {
        super(pFluid, BlockBehaviour.Properties.of().mapColor(MapColor.WATER).replaceable().pushReaction(PushReaction.DESTROY).liquid());
        this.chemicalName = pChemicalName;
    }

    private Optional<Chemical> getChemical() {
        AtomicReference<Chemical> atomicChemical = new AtomicReference<>();
        ItemRegistry.getElementByName(chemicalName).ifPresent(atomicChemical::set);
        ItemRegistry.getCompoundByName(chemicalName).ifPresent(atomicChemical::set);
        return Optional.of(atomicChemical.get());
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
