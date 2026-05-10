package com.smashingmods.chemlib.common.fluids;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.shorts.Short2BooleanMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

import java.util.Map;

public abstract class GaseousFlowingFluid extends BaseFlowingFluid {
    protected GaseousFlowingFluid(Properties properties) {
        super(properties);
    }

    @Override
    public void tick(Level level, BlockPos pos, FluidState state) {
        //float up
        boolean floated = false;
        if (state.isSource()) {
            BlockState blockState = level.getBlockState(pos);
            BlockPos blockpos = pos.above();
            BlockState aboveBlockState = level.getBlockState(blockpos);
            FluidState aboveFluidState = level.getFluidState(blockpos);
            if (this.canPassThrough(level, state.getType(), pos, blockState, Direction.UP, blockpos, aboveBlockState, aboveFluidState)) {
                level.setBlock(blockpos, blockState, 3);
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                floated = true;
            }
        }
        if (!floated) {
            super.tick(level, pos, state);
        }
    }

    @Override
    public Vec3 getFlow(BlockGetter blockReader, BlockPos pos, FluidState fluidState) {
        double d0 = 0.0;
        double d1 = 0.0;
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            blockpos$mutableblockpos.setWithOffset(pos, direction);
            FluidState fluidstate = blockReader.getFluidState(blockpos$mutableblockpos);
            if (this.affectsFlow(fluidstate)) {
                float f = fluidstate.getOwnHeight();
                float f1 = 0.0F;
                if (f == 0.0F) {
                    if (!blockReader.getBlockState(blockpos$mutableblockpos).blocksMotion()) {
                        BlockPos blockpos = blockpos$mutableblockpos.above();
                        FluidState fluidstate1 = blockReader.getFluidState(blockpos);
                        if (this.affectsFlow(fluidstate1)) {
                            f = fluidstate1.getOwnHeight();
                            if (f > 0.0F) {
                                f1 = fluidState.getOwnHeight() - (f - 8/9.F);
                            }
                        }
                    }
                } else if (f > 0.0F) {
                    f1 = fluidState.getOwnHeight() - f;
                }

                if (f1 != 0.0F) {
                    d0 += (float)direction.getStepX() * f1;
                    d1 += (float)direction.getStepZ() * f1;
                }
            }
        }

        Vec3 vec3 = new Vec3(d0, 0.0, d1);
        if (fluidState.getValue(FALLING)) {
            for (Direction direction1 : Direction.Plane.HORIZONTAL) {
                blockpos$mutableblockpos.setWithOffset(pos, direction1);
                if (this.isSolidFace(blockReader, blockpos$mutableblockpos, direction1)
                        || this.isSolidFace(blockReader, blockpos$mutableblockpos.below(), direction1)) {
                    vec3 = vec3.normalize().add(0.0, -6.0, 0.0);
                    break;
                }
            }
        }

        return vec3.normalize();
    }

    @Override
    protected boolean isSolidFace(BlockGetter level, BlockPos neighborPos, Direction side) {
        BlockState blockstate = level.getBlockState(neighborPos);
        FluidState fluidstate = level.getFluidState(neighborPos);
        if (fluidstate.getType().isSame(this)) {
            return false;
        } else if (side == Direction.DOWN) {
            return true;
        } else {
            return blockstate.getBlock() instanceof IceBlock ? false : blockstate.isFaceSturdy(level, neighborPos, side);
        }
    }

    @Override
    protected void spread(Level level, BlockPos pos, FluidState state) {
        if (!state.isEmpty()) {
            BlockState blockstate = level.getBlockState(pos);
            BlockPos blockpos = pos.above();
            BlockState blockstate1 = level.getBlockState(blockpos);
            FluidState fluidstate = this.getNewLiquid(level, blockpos, blockstate1);
            if (this.canSpreadTo(
                    level, pos, blockstate, Direction.UP, blockpos, blockstate1, level.getFluidState(blockpos), fluidstate.getType()
            )) {
                this.spreadTo(level, blockpos, blockstate1, Direction.UP, fluidstate);
                if (this.sourceNeighborCount(level, pos) >= 3) {
                    this.spreadToSides(level, pos, state, blockstate);
                }
            } else if (state.isSource() || !this.isWaterHole(level, fluidstate.getType(), pos, blockstate, blockpos, blockstate1)) {
                this.spreadToSides(level, pos, state, blockstate);
            }
        }
    }

    @Override
    protected FluidState getNewLiquid(Level level, BlockPos pos, BlockState blockState) {
        int i = 0;
        int j = 0;

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos blockpos = pos.relative(direction);
            BlockState blockstate = level.getBlockState(blockpos);
            FluidState fluidstate = blockstate.getFluidState();
            if (fluidstate.getType().isSame(this) && this.canPassThroughWall(direction, level, pos, blockState, blockpos, blockstate)) {
                if (fluidstate.isSource() && net.neoforged.neoforge.event.EventHooks.canCreateFluidSource(level, blockpos, blockstate)) {
                    j++;
                }

                i = Math.max(i, fluidstate.getAmount());
            }
        }

        if (j >= 2) {
            BlockState blockstate1 = level.getBlockState(pos.above());
            FluidState fluidstate1 = blockstate1.getFluidState();
            if (blockstate1.isSolid() || this.isSourceBlockOfThisType(fluidstate1)) {
                return this.getSource(false);
            }
        }

        BlockPos blockpos1 = pos.below();
        BlockState blockstate2 = level.getBlockState(blockpos1);
        FluidState fluidstate2 = blockstate2.getFluidState();
        if (!fluidstate2.isEmpty()
                && fluidstate2.getType().isSame(this)
                && this.canPassThroughWall(Direction.DOWN, level, pos, blockState, blockpos1, blockstate2)) {
            return this.getFlowing(8, true);
        } else {
            int k = i - this.getDropOff(level);
            return k <= 0 ? Fluids.EMPTY.defaultFluidState() : this.getFlowing(k, false);
        }
    }

    @Override
    protected int getSlopeDistance(LevelReader level, BlockPos spreadPos, int distance, Direction p_direction, BlockState currentSpreadState, BlockPos sourcePos, Short2ObjectMap<Pair<BlockState, FluidState>> stateCache, Short2BooleanMap waterHoleCache) {
        int i = 1000;

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (direction != p_direction) {
                BlockPos blockpos = spreadPos.relative(direction);
                short short1 = getCacheKey(sourcePos, blockpos);
                Pair<BlockState, FluidState> pair = stateCache.computeIfAbsent(short1, p_284932_ -> {
                    BlockState blockstate1 = level.getBlockState(blockpos);
                    return Pair.of(blockstate1, blockstate1.getFluidState());
                });
                BlockState blockstate = pair.getFirst();
                FluidState fluidstate = pair.getSecond();
                if (this.canPassThrough(level, this.getFlowing(), spreadPos, currentSpreadState, direction, blockpos, blockstate, fluidstate)) {
                    boolean flag = waterHoleCache.computeIfAbsent(short1, p_192912_ -> {
                        BlockPos blockpos1 = blockpos.above();
                        BlockState blockstate1 = level.getBlockState(blockpos1);
                        return this.isWaterHole(level, this.getFlowing(), blockpos, blockstate, blockpos1, blockstate1);
                    });
                    if (flag) {
                        return distance;
                    }

                    if (distance < this.getSlopeFindDistance(level)) {
                        int j = this.getSlopeDistance(level, blockpos, distance + 1, direction.getOpposite(), blockstate, sourcePos, stateCache, waterHoleCache);
                        if (j < i) {
                            i = j;
                        }
                    }
                }
            }
        }

        return i;
    }

    @Override
    protected boolean isWaterHole(BlockGetter level, Fluid fluid, BlockPos pos, BlockState state, BlockPos spreadPos, BlockState spreadState) {
        if (!this.canPassThroughWall(Direction.UP, level, pos, state, spreadPos, spreadState)) {
            return false;
        } else {
            return spreadState.getFluidState().getType().isSame(this) ? true : this.canHoldFluid(level, spreadPos, spreadState, fluid);
        }
    }

    @Override
    protected Map<Direction, FluidState> getSpread(Level level, BlockPos pos, BlockState state) {
        int i = 1000;
        Map<Direction, FluidState> map = Maps.newEnumMap(Direction.class);
        Short2ObjectMap<Pair<BlockState, FluidState>> short2objectmap = new Short2ObjectOpenHashMap<>();
        Short2BooleanMap short2booleanmap = new Short2BooleanOpenHashMap();

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos blockpos = pos.relative(direction);
            short short1 = getCacheKey(pos, blockpos);
            Pair<BlockState, FluidState> pair = short2objectmap.computeIfAbsent(short1, p_284929_ -> {
                BlockState otherState = level.getBlockState(blockpos);
                return Pair.of(otherState, otherState.getFluidState());
            });
            BlockState blockstate = pair.getFirst();
            FluidState fluidstate = pair.getSecond();
            FluidState newLiquid = this.getNewLiquid(level, blockpos, blockstate);
            if (this.canPassThrough(level, newLiquid.getType(), pos, state, direction, blockpos, blockstate, fluidstate)) {
                BlockPos abovePos = blockpos.above();
                boolean flag = short2booleanmap.computeIfAbsent(short1, p_255612_ -> {
                    BlockState aboveState = level.getBlockState(abovePos);
                    return this.isWaterHole(level, this.getFlowing(), blockpos, blockstate, abovePos, aboveState);
                });
                int j;
                if (flag) {
                    j = 0;
                } else {
                    j = this.getSlopeDistance(level, blockpos, 1, direction.getOpposite(), blockstate, pos, short2objectmap, short2booleanmap);
                }

                if (j < i) {
                    map.clear();
                }

                if (j <= i) {
                    map.put(direction, newLiquid);
                    i = j;
                }
            }
        }

        return map;
    }

    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid fluidIn, Direction direction) {
        return direction == Direction.UP && !isSame(fluidIn);
    }

    public static class Flowing extends GaseousFlowingFluid {
        public Flowing(Properties properties) {
            super(properties);
            registerDefaultState(getStateDefinition().any().setValue(LEVEL, 7));
        }

        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }

        @Override
        public boolean isSource(FluidState state) {
            return false;
        }
    }

    public static class Source extends GaseousFlowingFluid {
        public Source(Properties properties) {
            super(properties);
        }

        @Override
        public boolean isSource(FluidState state) {
            return true;
        }

        @Override
        public int getAmount(FluidState state) {
            return 8;
        }
    }
}
