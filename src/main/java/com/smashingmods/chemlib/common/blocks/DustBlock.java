package com.smashingmods.chemlib.common.blocks;

import com.smashingmods.chemlib.api.ChemicalBlockType;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class DustBlock extends ChemicalBlock {

    private static final VoxelShape SHAPE = Block.box(5.0D, 0.0D, 5.0D, 12.0D, 1.0D, 12.0D);

    ResourceLocation chemical;

    public DustBlock(ResourceLocation pChemical, ChemicalBlockType pBlockType, List<ChemicalBlock> pList, Properties pProperties) {
        super(pChemical, pBlockType, pList, pProperties);
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockPos blockpos = pPos.below();
        BlockState blockstate = pLevel.getBlockState(blockpos);
        return this.canSurviveOn(pLevel, blockpos, blockstate);
    }

    private boolean canSurviveOn(BlockGetter pReader, BlockPos pPos, BlockState pState) {
        return pState.isFaceSturdy(pReader, pPos, Direction.UP) || pState.is(Blocks.HOPPER);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

}
