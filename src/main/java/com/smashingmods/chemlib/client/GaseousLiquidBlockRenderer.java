package com.smashingmods.chemlib.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.textures.FluidSpriteCache;

/**
 * Copied from {@link net.minecraft.client.renderer.block.LiquidBlockRenderer}.
 * This class is basically just a copy of the vanilla LiquidBlockRenderer, except with the quad rendering upside down and occlusion logic inverted.
 */
public class GaseousLiquidBlockRenderer {

    private static boolean isNeighborStateHidingOverlay(FluidState selfState, BlockState otherState, Direction neighborFace) {
        return otherState.shouldHideAdjacentFluidFace(neighborFace, selfState);
    }

    private static boolean isFaceOccludedByState(BlockGetter level, Direction face, float height, BlockPos pos, BlockState state) {
        if (state.canOcclude()) {
            VoxelShape fluidShape = Shapes.box(0.0, 0.0, 0.0, 1.0, height, 1.0);
            VoxelShape occluderShape = state.getOcclusionShape(level, pos);
            return Shapes.blockOccudes(fluidShape, occluderShape, face);
        } else {
            return false;
        }
    }

    private static boolean isFaceOccludedByNeighbor(BlockGetter level, BlockPos pos, Direction side, float height, BlockState blockState) {
        return isFaceOccludedByState(level, side, height, pos.relative(side), blockState);
    }

    private static boolean isFaceOccludedBySelf(BlockGetter level, BlockPos pos, BlockState state, Direction face) {
        return isFaceOccludedByState(level, face.getOpposite(), 1.0F, pos, state);
    }

    public static boolean shouldRenderFace(
            BlockAndTintGetter level, BlockPos pos, FluidState fluidState, BlockState selfState, Direction direction, BlockState otherState
    ) {
        return !isFaceOccludedBySelf(level, pos, selfState, direction) && !isNeighborStateHidingOverlay(fluidState, otherState, direction.getOpposite());
    }

    public static void tesselate(BlockAndTintGetter level, BlockPos pos, VertexConsumer buffer, BlockState blockState, FluidState fluidState) {
        TextureAtlasSprite[] fluidSprites = FluidSpriteCache.getFluidSprites(level, pos, fluidState);

        int tintColor = IClientFluidTypeExtensions.of(fluidState).getTintColor(fluidState, level, pos);
        float alpha = (float)(tintColor >> 24 & 255) / 255.0F;
        float red   = (float)(tintColor >> 16 & 0xFF) / 255.0F;
        float green = (float)(tintColor >> 8  & 0xFF) / 255.0F;
        float blue  = (float)(tintColor & 0xFF) / 255.0F;

        BlockState downBlock  = level.getBlockState(pos.relative(Direction.DOWN));
        FluidState downFluid  = downBlock.getFluidState();
        BlockState upBlock    = level.getBlockState(pos.relative(Direction.UP));
        FluidState upFluid    = upBlock.getFluidState();
        BlockState northBlock = level.getBlockState(pos.relative(Direction.NORTH));
        FluidState northFluid = northBlock.getFluidState();
        BlockState southBlock = level.getBlockState(pos.relative(Direction.SOUTH));
        FluidState southFluid = southBlock.getFluidState();
        BlockState westBlock  = level.getBlockState(pos.relative(Direction.WEST));
        FluidState westFluid  = westBlock.getFluidState();
        BlockState eastBlock  = level.getBlockState(pos.relative(Direction.EAST));
        FluidState eastFluid  = eastBlock.getFluidState();

        boolean renderTopFace = !isNeighborStateHidingOverlay(fluidState, upBlock, Direction.DOWN);
        boolean renderBottomFace = shouldRenderFace(level, pos, fluidState, blockState, Direction.UP, downBlock)
                && !isFaceOccludedByNeighbor(level, pos, Direction.UP, 0.8888889F, downBlock);
        boolean renderNorthFace  = shouldRenderFace(level, pos, fluidState, blockState, Direction.NORTH, northBlock);
        boolean renderSouthFace  = shouldRenderFace(level, pos, fluidState, blockState, Direction.SOUTH, southBlock);
        boolean renderWestFace   = shouldRenderFace(level, pos, fluidState, blockState, Direction.WEST, westBlock);
        boolean renderEastFace   = shouldRenderFace(level, pos, fluidState, blockState, Direction.EAST, eastBlock);

        if (renderBottomFace || renderTopFace || renderEastFace || renderWestFace || renderNorthFace || renderSouthFace) {
            float shadeDown  = level.getShade(Direction.DOWN, true);
            float shadeUp    = level.getShade(Direction.UP,   true);
            float shadeNorth = level.getShade(Direction.NORTH, true);
            float shadeWest  = level.getShade(Direction.WEST,  true);

            Fluid fluid = fluidState.getType();

            float centerHeight = getHeight(level, fluid, pos, blockState, fluidState);
            float heightNE, heightNW, heightSE, heightSW;

            if (centerHeight >= 1.0F) {
                heightNE = heightNW = heightSE = heightSW = 1.0F;
            } else {
                float northHeight = getHeight(level, fluid, pos.north(), northBlock, northFluid);
                float southHeight = getHeight(level, fluid, pos.south(), southBlock, southFluid);
                float eastHeight  = getHeight(level, fluid, pos.east(),  eastBlock,  eastFluid);
                float westHeight  = getHeight(level, fluid, pos.west(),  westBlock,  westFluid);

                heightNE = calculateAverageHeight(level, fluid, centerHeight, northHeight, eastHeight,  pos.relative(Direction.NORTH).relative(Direction.EAST));
                heightNW = calculateAverageHeight(level, fluid, centerHeight, northHeight, westHeight,  pos.relative(Direction.NORTH).relative(Direction.WEST));
                heightSE = calculateAverageHeight(level, fluid, centerHeight, southHeight, eastHeight,  pos.relative(Direction.SOUTH).relative(Direction.EAST));
                heightSW = calculateAverageHeight(level, fluid, centerHeight, southHeight, westHeight,  pos.relative(Direction.SOUTH).relative(Direction.WEST));
            }

            float localX = (float)(pos.getX() & 15);
            float localY = (float)(pos.getY() & 15);
            float localZ = (float)(pos.getZ() & 15);

            final float epsilon = 0.001F;


            float topYOffset = renderTopFace ? (1.0F - epsilon) : 1.0F;


            if (renderBottomFace && !isFaceOccludedByNeighbor(level, pos, Direction.DOWN,
                    Math.min(Math.min(heightNW, heightSW), Math.min(heightSE, heightNE)), downBlock)) {

                heightNW += epsilon;
                heightSW += epsilon;
                heightSE += epsilon;
                heightNE += epsilon;

                Vec3 flow = fluidState.getFlow(level, pos);

                float u0, u1, u2, u3;
                float v0, v1, v2, v3;
                if (flow.x == 0.0 && flow.z == 0.0) {
                    TextureAtlasSprite stillSprite = fluidSprites[0];
                    u0 = stillSprite.getU(0.0F);
                    v0 = stillSprite.getV(0.0F);
                    u1 = u0;
                    v1 = stillSprite.getV(1.0F);
                    u2 = stillSprite.getU(1.0F);
                    v2 = v1;
                    u3 = u2;
                    v3 = v0;
                } else {
                    TextureAtlasSprite flowSprite = fluidSprites[1];
                    float flowAngle = (float)Mth.atan2(flow.z, flow.x) - (float)(Math.PI / 2);
                    final float sin = Mth.sin(flowAngle) * 0.25F;
                    final float cos = Mth.cos(flowAngle) * 0.25F;
                    final float offset = 0.5F;
                    u0 = flowSprite.getU(offset + (-cos - sin));
                    v0 = flowSprite.getV(offset + -cos + sin);
                    u1 = flowSprite.getU(offset + -cos + sin);
                    v1 = flowSprite.getV(offset + cos + sin);
                    u2 = flowSprite.getU(offset + cos + sin);
                    v2 = flowSprite.getV(offset + (cos - sin));
                    u3 = flowSprite.getU(offset + (cos - sin));
                    v3 = flowSprite.getV(offset + (-cos - sin));
                }

                float avgU = (u0 + u1 + u2 + u3) / 4.0F;
                float avgV = (v0 + v1 + v2 + v3) / 4.0F;
                float shrinkRatio = fluidSprites[0].uvShrinkRatio();
                u0 = Mth.lerp(shrinkRatio, u0, avgU);
                u1 = Mth.lerp(shrinkRatio, u1, avgU);
                u2 = Mth.lerp(shrinkRatio, u2, avgU);
                u3 = Mth.lerp(shrinkRatio, u3, avgU);
                v0 = Mth.lerp(shrinkRatio, v0, avgV);
                v1 = Mth.lerp(shrinkRatio, v1, avgV);
                v2 = Mth.lerp(shrinkRatio, v2, avgV);
                v3 = Mth.lerp(shrinkRatio, v3, avgV);

                int lightColor = getLightColor(level, pos);
                float shadedRed   = shadeDown * red;
                float shadedGreen = shadeDown * green;
                float shadedBlue  = shadeDown * blue;

                vertex(buffer, localX + 0.0F, localY + 1.0F - heightNW, localZ + 0.0F, shadedRed, shadedGreen, shadedBlue, alpha, u0, v0, lightColor);
                vertex(buffer, localX + 1.0F, localY + 1.0F - heightNE, localZ + 0.0F, shadedRed, shadedGreen, shadedBlue, alpha, u3, v3, lightColor);
                vertex(buffer, localX + 1.0F, localY + 1.0F - heightSE, localZ + 1.0F, shadedRed, shadedGreen, shadedBlue, alpha, u2, v2, lightColor);
                vertex(buffer, localX + 0.0F, localY + 1.0F - heightSW, localZ + 1.0F, shadedRed, shadedGreen, shadedBlue, alpha, u1, v1, lightColor);

                if (fluidState.shouldRenderBackwardUpFace(level, pos.below())) {
                    vertex(buffer, localX + 0.0F, localY + 1.0F - heightNW, localZ + 0.0F, shadedRed, shadedGreen, shadedBlue, alpha, u0, v0, lightColor);
                    vertex(buffer, localX + 0.0F, localY + 1.0F - heightSW, localZ + 1.0F, shadedRed, shadedGreen, shadedBlue, alpha, u1, v1, lightColor);
                    vertex(buffer, localX + 1.0F, localY + 1.0F - heightSE, localZ + 1.0F, shadedRed, shadedGreen, shadedBlue, alpha, u2, v2, lightColor);
                    vertex(buffer, localX + 1.0F, localY + 1.0F - heightNE, localZ + 0.0F, shadedRed, shadedGreen, shadedBlue, alpha, u3, v3, lightColor);
                }
            }

            if (renderTopFace) {
                float bottomU0 = fluidSprites[0].getU0();
                float bottomU1 = fluidSprites[0].getU1();
                float bottomV0 = fluidSprites[0].getV0();
                float bottomV1 = fluidSprites[0].getV1();

                int lightColor = getLightColor(level, pos.above());
                float shadedRed   = shadeUp * red;
                float shadedGreen = shadeUp * green;
                float shadedBlue  = shadeUp * blue;

                vertex(buffer, localX + 1.0F, localY + topYOffset, localZ + 1.0F, shadedRed, shadedGreen, shadedBlue, alpha, bottomU1, bottomV1, lightColor);
                vertex(buffer, localX + 1.0F, localY + topYOffset, localZ,         shadedRed, shadedGreen, shadedBlue, alpha, bottomU1, bottomV0, lightColor);
                vertex(buffer, localX,         localY + topYOffset, localZ,         shadedRed, shadedGreen, shadedBlue, alpha, bottomU0, bottomV0, lightColor);
                vertex(buffer, localX,         localY + topYOffset, localZ + 1.0F, shadedRed, shadedGreen, shadedBlue, alpha, bottomU0, bottomV1, lightColor);
            }

            int sideLightColor = getLightColor(level, pos);

            for (Direction direction : Direction.Plane.HORIZONTAL) {
                float sideHeightStart;
                float sideHeightEnd;

                float xStart, xEnd;
                float zStart, zEnd;

                boolean shouldRenderSide;

                switch (direction) {
                    case NORTH:
                        sideHeightStart = heightNW;
                        sideHeightEnd   = heightNE;
                        xStart = localX;
                        zStart = localX + 1.0F;
                        xEnd   = localZ + epsilon;
                        zEnd   = localZ + epsilon;
                        shouldRenderSide = renderNorthFace;
                        break;
                    case SOUTH:
                        sideHeightStart = heightSE;
                        sideHeightEnd   = heightSW;
                        xStart = localX + 1.0F;
                        zStart = localX;
                        xEnd   = localZ + 1.0F - epsilon;
                        zEnd   = localZ + 1.0F - epsilon;
                        shouldRenderSide = renderSouthFace;
                        break;
                    case WEST:
                        sideHeightStart = heightSW;
                        sideHeightEnd   = heightNW;
                        xStart = localX + epsilon;
                        zStart = localX + epsilon;
                        xEnd   = localZ + 1.0F;
                        zEnd   = localZ;
                        shouldRenderSide = renderWestFace;
                        break;
                    default: // EAST
                        sideHeightStart = heightNE;
                        sideHeightEnd   = heightSE;
                        xStart = localX + 1.0F - epsilon;
                        zStart = localX + 1.0F - epsilon;
                        xEnd   = localZ;
                        zEnd   = localZ + 1.0F;
                        shouldRenderSide = renderEastFace;
                }

                if (shouldRenderSide && !isFaceOccludedByNeighbor(level, pos, direction,
                        Math.max(sideHeightStart, sideHeightEnd),
                        level.getBlockState(pos.relative(direction)))) {

                    BlockPos neighborPos = pos.relative(direction);
                    TextureAtlasSprite overlaySprite = fluidSprites[1];
                    if (fluidSprites[2] != null) {
                        if (level.getBlockState(neighborPos).shouldDisplayFluidOverlay(level, neighborPos, fluidState)) {
                            overlaySprite = fluidSprites[2];
                        }
                    }

                    float sideU0 = overlaySprite.getU(0.0F);
                    float sideU1 = overlaySprite.getU(0.5F);

                    float sideV0    = overlaySprite.getV(sideHeightStart * 0.5F);
                    float sideV1    = overlaySprite.getV(sideHeightEnd   * 0.5F);
                    float sideTopV  = overlaySprite.getV(0.0F);

                    float directionalShade = direction.getAxis() == Direction.Axis.Z ? shadeNorth : shadeWest;
                    float sideRed   = shadeDown * directionalShade * red;
                    float sideGreen = shadeDown * directionalShade * green;
                    float sideBlue  = shadeDown * directionalShade * blue;

                    vertex(buffer, xStart, localY + 1.0F - sideHeightStart, xEnd, sideRed, sideGreen, sideBlue, alpha, sideU0, sideV0,   sideLightColor);
                    vertex(buffer, xStart, localY + topYOffset,              xEnd, sideRed, sideGreen, sideBlue, alpha, sideU0, sideTopV, sideLightColor);
                    vertex(buffer, zStart, localY + topYOffset,              zEnd, sideRed, sideGreen, sideBlue, alpha, sideU1, sideTopV, sideLightColor);
                    vertex(buffer, zStart, localY + 1.0F - sideHeightEnd,   zEnd, sideRed, sideGreen, sideBlue, alpha, sideU1, sideV1,   sideLightColor);

                    if (overlaySprite != fluidSprites[2]) {
                        vertex(buffer, zStart, localY + 1.0F - sideHeightEnd,   zEnd, sideRed, sideGreen, sideBlue, alpha, sideU1, sideV1,   sideLightColor);
                        vertex(buffer, zStart, localY + topYOffset,              zEnd, sideRed, sideGreen, sideBlue, alpha, sideU1, sideTopV, sideLightColor);
                        vertex(buffer, xStart, localY + topYOffset,              xEnd, sideRed, sideGreen, sideBlue, alpha, sideU0, sideTopV, sideLightColor);
                        vertex(buffer, xStart, localY + 1.0F - sideHeightStart, xEnd, sideRed, sideGreen, sideBlue, alpha, sideU0, sideV0,   sideLightColor);
                    }
                }
            }
        }
    }

    public static void tesselateNormal(BlockAndTintGetter level, BlockPos pos, VertexConsumer buffer, BlockState blockState, FluidState fluidState) {
        TextureAtlasSprite[] fluidSprites = FluidSpriteCache.getFluidSprites(level, pos, fluidState);

        int tintColor = IClientFluidTypeExtensions.of(fluidState).getTintColor(fluidState, level, pos);
        float alpha = (float)(tintColor >> 24 & 255) / 255.0F;
        float red = (float)(tintColor >> 16 & 0xFF) / 255.0F;
        float green = (float)(tintColor >> 8 & 0xFF) / 255.0F;
        float blue = (float)(tintColor & 0xFF) / 255.0F;

        BlockState downBlock = level.getBlockState(pos.relative(Direction.DOWN));
        FluidState downFluid = downBlock.getFluidState();
        BlockState upBlock = level.getBlockState(pos.relative(Direction.UP));
        FluidState upFluid = upBlock.getFluidState();
        BlockState northBlock = level.getBlockState(pos.relative(Direction.NORTH));
        FluidState northFluid = northBlock.getFluidState();
        BlockState southBlock = level.getBlockState(pos.relative(Direction.SOUTH));
        FluidState southFluid = southBlock.getFluidState();
        BlockState westBlock = level.getBlockState(pos.relative(Direction.WEST));
        FluidState westFluid = westBlock.getFluidState();
        BlockState eastBlock = level.getBlockState(pos.relative(Direction.EAST));
        FluidState eastFluid = eastBlock.getFluidState();

        boolean renderTopFace = !isNeighborStateHidingOverlay(fluidState, upBlock, Direction.DOWN);
        boolean renderBottomFace = shouldRenderFace(level, pos, fluidState, blockState, Direction.DOWN, downBlock)
                && !isFaceOccludedByNeighbor(level, pos, Direction.DOWN, 0.8888889F, downBlock);
        boolean renderNorthFace = shouldRenderFace(level, pos, fluidState, blockState, Direction.NORTH, northBlock);
        boolean renderSouthFace = shouldRenderFace(level, pos, fluidState, blockState, Direction.SOUTH, southBlock);
        boolean renderWestFace = shouldRenderFace(level, pos, fluidState, blockState, Direction.WEST, westBlock);
        boolean renderEastFace = shouldRenderFace(level, pos, fluidState, blockState, Direction.EAST, eastBlock);

        if (renderTopFace || renderBottomFace || renderEastFace || renderWestFace || renderNorthFace || renderSouthFace) {
            float shadeDown = level.getShade(Direction.DOWN, true);
            float shadeUp = level.getShade(Direction.UP, true);
            float shadeNorth = level.getShade(Direction.NORTH, true);
            float shadeWest = level.getShade(Direction.WEST, true);
            Fluid fluid = fluidState.getType();
            float centerHeight = getHeight(level, fluid, pos, blockState, fluidState);
            float heightNE, heightNW, heightSE, heightSW;
            if (centerHeight >= 1.0F) {
                heightNE = heightNW = heightSE = heightSW = 1.0F;
            } else {
                float northHeight = getHeight(level, fluid, pos.north(), northBlock, northFluid);
                float southHeight = getHeight(level, fluid, pos.south(), southBlock, southFluid);
                float eastHeight = getHeight(level, fluid, pos.east(), eastBlock, eastFluid);
                float westHeight = getHeight(level, fluid, pos.west(), westBlock, westFluid);
                heightNE = calculateAverageHeight(level, fluid, centerHeight, northHeight, eastHeight, pos.relative(Direction.NORTH).relative(Direction.EAST));
                heightNW = calculateAverageHeight(level, fluid, centerHeight, northHeight, westHeight, pos.relative(Direction.NORTH).relative(Direction.WEST));
                heightSE = calculateAverageHeight(level, fluid, centerHeight, southHeight, eastHeight, pos.relative(Direction.SOUTH).relative(Direction.EAST));
                heightSW = calculateAverageHeight(level, fluid, centerHeight, southHeight, westHeight, pos.relative(Direction.SOUTH).relative(Direction.WEST));
            }

            float localX = (float)(pos.getX() & 15);
            float localY = (float)(pos.getY() & 15);
            float localZ = (float)(pos.getZ() & 15);

            // Slight offset to fight z-fighting
            final float epsilon = 0.001F;

            float bottomYOffset = renderBottomFace ? epsilon : 0.0F;
            if (renderTopFace && !isFaceOccludedByNeighbor(level, pos, Direction.UP, Math.min(Math.min(heightNW, heightSW), Math.min(heightSE, heightNE)), upBlock)) {
                heightNW -= epsilon;
                heightSW -= epsilon;
                heightSE -= epsilon;
                heightNE -= epsilon;

                Vec3 flow = fluidState.getFlow(level, pos);

                float u0, u1, u2, u3;
                float v0, v1, v2, v3;
                if (flow.x == 0.0 && flow.z == 0.0) {
                    TextureAtlasSprite stillSprite = fluidSprites[0];

                    u0 = stillSprite.getU(0.0F);
                    v0 = stillSprite.getV(0.0F);

                    u1 = u0;
                    v1 = stillSprite.getV(1.0F);

                    u2 = stillSprite.getU(1.0F);
                    v2 = v1;

                    u3 = u2;
                    v3 = v0;
                } else {
                    TextureAtlasSprite flowSprite = fluidSprites[1];
                    float flowAngle = (float)Mth.atan2(flow.z, flow.x) - (float) (Math.PI / 2);
                    final float sin = Mth.sin(flowAngle) * 0.25F;
                    final float cos = Mth.cos(flowAngle) * 0.25F;
                    final float offset = 0.5F;

                    u0 = flowSprite.getU(offset + (-cos - sin));
                    v0 = flowSprite.getV(offset + -cos + sin);

                    u1 = flowSprite.getU(offset + -cos + sin);
                    v1 = flowSprite.getV(offset + cos + sin);

                    u2 = flowSprite.getU(offset + cos + sin);
                    v2 = flowSprite.getV(offset + (cos - sin));

                    u3 = flowSprite.getU(offset + (cos - sin));
                    v3 = flowSprite.getV(offset + (-cos - sin));
                }

                float avgU = (u0 + u1 + u2 + u3) / 4.0F;
                float avgV = (v0 + v1 + v2 + v3) / 4.0F;

                float shrinkRatio = fluidSprites[0].uvShrinkRatio();

                u0 = Mth.lerp(shrinkRatio, u0, avgU);
                u1 = Mth.lerp(shrinkRatio, u1, avgU);
                u2 = Mth.lerp(shrinkRatio, u2, avgU);
                u3 = Mth.lerp(shrinkRatio, u3, avgU);

                v0 = Mth.lerp(shrinkRatio, v0, avgV);
                v1 = Mth.lerp(shrinkRatio, v1, avgV);
                v2 = Mth.lerp(shrinkRatio, v2, avgV);
                v3 = Mth.lerp(shrinkRatio, v3, avgV);

                int lightColor = getLightColor(level, pos);
                float shadedRed = shadeUp * red;
                float shadedGreen = shadeUp * green;
                float shadedBlue = shadeUp * blue;
                vertex(buffer, localX + 0.0F, localY + heightNW, localZ + 0.0F, shadedRed, shadedGreen, shadedBlue, alpha, u0, v0, lightColor);
                vertex(buffer, localX + 0.0F, localY + heightSW, localZ + 1.0F, shadedRed, shadedGreen, shadedBlue, alpha, u1, v1, lightColor);
                vertex(buffer, localX + 1.0F, localY + heightSE, localZ + 1.0F, shadedRed, shadedGreen, shadedBlue, alpha, u2, v2, lightColor);
                vertex(buffer, localX + 1.0F, localY + heightNE, localZ + 0.0F, shadedRed, shadedGreen, shadedBlue, alpha, u3, v3, lightColor);
                if (fluidState.shouldRenderBackwardUpFace(level, pos.above())) {
                    vertex(buffer, localX + 0.0F, localY + heightNW, localZ + 0.0F, shadedRed, shadedGreen, shadedBlue, alpha, u0, v0, lightColor);
                    vertex(buffer, localX + 1.0F, localY + heightNE, localZ + 0.0F, shadedRed, shadedGreen, shadedBlue, alpha, u3, v3, lightColor);
                    vertex(buffer, localX + 1.0F, localY + heightSE, localZ + 1.0F, shadedRed, shadedGreen, shadedBlue, alpha, u2, v2, lightColor);
                    vertex(buffer, localX + 0.0F, localY + heightSW, localZ + 1.0F, shadedRed, shadedGreen, shadedBlue, alpha, u1, v1, lightColor);
                }
            }

            if (renderBottomFace) {
                float bottomU0 = fluidSprites[0].getU0();
                float bottomU1 = fluidSprites[0].getU1();
                float bottomV0 = fluidSprites[0].getV0();
                float bottomV1 = fluidSprites[0].getV1();

                int lightColor = getLightColor(level, pos.below());
                float shadedRed = shadeDown * red;
                float shadedGreen = shadeDown * green;
                float shadedBlue = shadeDown * blue;
                vertex(buffer, localX, localY + bottomYOffset, localZ + 1.0F, shadedRed, shadedGreen, shadedBlue, alpha, bottomU0, bottomV1, lightColor);
                vertex(buffer, localX, localY + bottomYOffset, localZ, shadedRed, shadedGreen, shadedBlue, alpha, bottomU0, bottomV0, lightColor);
                vertex(buffer, localX + 1.0F, localY + bottomYOffset, localZ, shadedRed, shadedGreen, shadedBlue, alpha, bottomU1, bottomV0, lightColor);
                vertex(buffer, localX + 1.0F, localY + bottomYOffset, localZ + 1.0F, shadedRed, shadedGreen, shadedBlue, alpha, bottomU1, bottomV1, lightColor);
            }

            int sideLightColor = getLightColor(level, pos);

            for (Direction direction : Direction.Plane.HORIZONTAL) {
                float sideHeightStart;
                float sideHeightEnd;

                float xStart;
                float xEnd;

                float zStart;
                float zEnd;

                boolean shouldRenderSide;

                switch (direction) {
                    case NORTH:
                        sideHeightStart = heightNW;
                        sideHeightEnd = heightNE;

                        xStart = localX;
                        zStart = localX + 1.0F;

                        xEnd = localZ + epsilon;
                        zEnd = localZ + epsilon;

                        shouldRenderSide = renderNorthFace;
                        break;
                    case SOUTH:
                        sideHeightStart = heightSE;
                        sideHeightEnd = heightSW;

                        xStart = localX + 1.0F;
                        zStart = localX;

                        xEnd = localZ + 1.0F - epsilon;
                        zEnd = localZ + 1.0F - epsilon;

                        shouldRenderSide = renderSouthFace;
                        break;
                    case WEST:
                        sideHeightStart = heightSW;
                        sideHeightEnd = heightNW;

                        xStart = localX + epsilon;
                        zStart = localX + epsilon;

                        xEnd = localZ + 1.0F;
                        zEnd = localZ;

                        shouldRenderSide = renderWestFace;
                        break;
                    default:
                        sideHeightStart = heightNE;
                        sideHeightEnd = heightSE;

                        xStart = localX + 1.0F - epsilon;
                        zStart = localX + 1.0F - epsilon;

                        xEnd = localZ;
                        zEnd = localZ + 1.0F;

                        shouldRenderSide = renderEastFace;
                }

                if (shouldRenderSide && !isFaceOccludedByNeighbor(level, pos, direction, Math.max(sideHeightStart, sideHeightEnd), level.getBlockState(pos.relative(direction)))) {
                    BlockPos neighborPos = pos.relative(direction);
                    TextureAtlasSprite overlaySprite = fluidSprites[1];
                    if (fluidSprites[2] != null) {
                        if (level.getBlockState(neighborPos).shouldDisplayFluidOverlay(level, neighborPos, fluidState)) {
                            overlaySprite = fluidSprites[2];
                        }
                    }

                    float sideU0 = overlaySprite.getU(0.0F);
                    float sideU1 = overlaySprite.getU(0.5F);

                    float sideV0 = overlaySprite.getV((1.0F - sideHeightStart) * 0.5F);
                    float sideV1 = overlaySprite.getV((1.0F - sideHeightEnd) * 0.5F);

                    float sideBottomV = overlaySprite.getV(0.5F);

                    float directionalShade = direction.getAxis() == Direction.Axis.Z ? shadeNorth : shadeWest;

                    float sideRed = shadeUp * directionalShade * red;
                    float sideGreen = shadeUp * directionalShade * green;
                    float sideBlue = shadeUp * directionalShade * blue;

                    vertex(buffer, xStart, localY + sideHeightStart, xEnd, sideRed, sideGreen, sideBlue, alpha, sideU0, sideV0, sideLightColor);
                    vertex(buffer, zStart, localY + sideHeightEnd, zEnd, sideRed, sideGreen, sideBlue, alpha, sideU1, sideV1, sideLightColor);
                    vertex(buffer, zStart, localY + bottomYOffset, zEnd, sideRed, sideGreen, sideBlue, alpha, sideU1, sideBottomV, sideLightColor);
                    vertex(buffer, xStart, localY + bottomYOffset, xEnd, sideRed, sideGreen, sideBlue, alpha, sideU0, sideBottomV, sideLightColor);

                    if (overlaySprite != fluidSprites[2]) { // Neo: use custom fluid's overlay texture
                        vertex(buffer, xStart, localY + bottomYOffset, xEnd, sideRed, sideGreen, sideBlue, alpha, sideU0, sideBottomV, sideLightColor);
                        vertex(buffer, zStart, localY + bottomYOffset, zEnd, sideRed, sideGreen, sideBlue, alpha, sideU1, sideBottomV, sideLightColor);
                        vertex(buffer, zStart, localY + sideHeightEnd, zEnd, sideRed, sideGreen, sideBlue, alpha, sideU1, sideV1, sideLightColor);
                        vertex(buffer, xStart, localY + sideHeightStart, xEnd, sideRed, sideGreen, sideBlue, alpha, sideU0, sideV0, sideLightColor);
                    }
                }
            }
        }
    }

    private static float calculateAverageHeight(BlockAndTintGetter level, Fluid fluid, float currentHeight, float height1, float height2, BlockPos pos) {
        if (!(height2 >= 1.0F) && !(height1 >= 1.0F)) {
            float[] runningAvgFraction = new float[2];
            if (height2 > 0.0F || height1 > 0.0F) {
                float f = getHeight(level, fluid, pos);
                if (f >= 1.0F) {
                    return 1.0F;
                }

                addWeightedHeight(runningAvgFraction, f);
            }

            addWeightedHeight(runningAvgFraction, currentHeight);
            addWeightedHeight(runningAvgFraction, height2);
            addWeightedHeight(runningAvgFraction, height1);
            return runningAvgFraction[0] / runningAvgFraction[1];
        } else {
            return 1.0F;
        }
    }

    private static void addWeightedHeight(float[] output, float height) {
        if (height >= 0.8F) {
            output[0] += height * 10.0F;
            output[1] += 10.0F;
        } else if (height >= 0.0F) {
            output[0] += height;
            output[1]++;
        }
    }

    private static float getHeight(BlockAndTintGetter level, Fluid fluid, BlockPos pos) {
        BlockState blockstate = level.getBlockState(pos);
        return getHeight(level, fluid, pos, blockstate, blockstate.getFluidState());
    }

    private static void vertex(
            VertexConsumer buffer,
            float x,
            float y,
            float z,
            float r,
            float g,
            float b,
            float alpha,
            float u,
            float v,
            int light
    ) {
        buffer.addVertex(x, y, z)
                .setColor(r, g, b, alpha)
                .setUv(u, v)
                .setLight(light)
                .setNormal(0.0F, 1.0F, 0.0F);
    }

    private static float getHeight(BlockAndTintGetter level, Fluid fluid, BlockPos pos, BlockState blockState, FluidState fluidState) {
        if (fluid.isSame(fluidState.getType())) {
            BlockState blockstate = level.getBlockState(pos.below());
            return fluid.isSame(blockstate.getFluidState().getType()) ? 1.0F : fluidState.getOwnHeight();
        } else {
            return !blockState.isSolid() ? 0.0F : -1.0F;
        }
    }

    private static int getLightColor(BlockAndTintGetter level, BlockPos pos) {
        int lightColor = LevelRenderer.getLightColor(level, pos);
        int aboveBlockLight = LevelRenderer.getLightColor(level, pos.above());
        int skyLight = lightColor & 0xFF;
        int aboveSkyLight = aboveBlockLight & 0xFF;
        int blockEmission = lightColor >> 16 & 0xFF;
        int aboveBlockEmission = aboveBlockLight >> 16 & 0xFF;
        return (Math.max(skyLight, aboveSkyLight)) | (Math.max(blockEmission, aboveBlockEmission)) << 16;
    }
}
