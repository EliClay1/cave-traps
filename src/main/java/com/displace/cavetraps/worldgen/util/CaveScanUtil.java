package com.displace.cavetraps.worldgen.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class CaveScanUtil {

    /**
     * Designed to check for air-like substances in cave spaces. Avoids liquids.
     */
    public static boolean isAirLike(WorldGenLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isAir() || state.is(Blocks.CAVE_VINES) || state.is(Blocks.CAVE_VINES_PLANT);
    }

    public static boolean isLiquid(BlockState blockState) {
        return blockState.is(Blocks.WATER) || blockState.is(Blocks.LAVA);
    }

    /**
     * Determines if a block is a solid surface that can hold a trap.
     */
    public static boolean isSolidSupport(WorldGenLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return !state.isAir() && !isLiquid(state) && state.isSolidRender();
    }

    public static boolean isCaveFloor(WorldGenLevel level, BlockPos floorPos) {
        return isSolidSupport(level, floorPos) && isAirLike(level, floorPos.above());
    }

    public static boolean isCaveCeilingAnchor(WorldGenLevel level, BlockPos airPos) {
        return isAirLike(level, airPos) && isSolidSupport(level, airPos.above());
    }

    /**
     * Scans a bounded box around the origin to find the nearest valid cave floor.
     * Returns null if no floor is found within the radius.
     */
    public static BlockPos findNearestCaveFloor(WorldGenLevel level, BlockPos origin, int horizontalRadius, int verticalRadius) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        BlockPos bestPos = null;
        double bestDistSquared = Double.MAX_VALUE;

        for (int y = -verticalRadius; y <= verticalRadius; y++) {
            for (int x = -horizontalRadius; x <= horizontalRadius; x++) {
                for (int z = -horizontalRadius; z <= horizontalRadius; z++) {

                    if (isCaveFloor(level, mutableBlockPos)) {
                        double distSquared = mutableBlockPos.distSqr(origin);
                        if (distSquared < bestDistSquared) {
                            bestDistSquared = distSquared;
                            bestPos = mutableBlockPos.immutable();
                        }
                    }
                }
            }
        }
        return bestPos;
    }

    /**
     * Scans a bounded box around the origin to find the nearest valid cave ceiling anchor.
     * Returns the position of the Airspace where the trap should go.
     */
    public static BlockPos findNearestCaveCeilingAnchor(WorldGenLevel level, BlockPos origin, int horizontalRadius, int verticalRadius) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        BlockPos bestPos = null;
        double bestDistSq = Double.MAX_VALUE;

        for (int y = -verticalRadius; y <= verticalRadius; y++) {
            for (int x = -horizontalRadius; x <= horizontalRadius; x++) {
                for (int z = -horizontalRadius; z <= horizontalRadius; z++) {
                    mutablePos.setWithOffset(origin, x, y, z);

                    if (isCaveCeilingAnchor(level, mutablePos)) {
                        double distSq = mutablePos.distSqr(origin);
                        if (distSq < bestDistSq) {
                            bestDistSq = distSq;
                            bestPos = mutablePos.immutable();
                        }
                    }
                }
            }
        }
        return bestPos;
    }

    /**
     * Counts the amount of air-like blocks in a designated bounding box.
     * Aborts early if maxChecks is reached to prevent lag spikes.
     */
    public static int countAirLikeInBox(WorldGenLevel level, BlockPos center, int radiusX, int radiusY, int radiusZ, int maxChecks) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        int airCount = 0;
        int checks = 0;

        for (int y = -radiusY; y <= radiusY; y++) {
            for (int x = -radiusX; x <= radiusX; x++) {
                for (int z = -radiusZ; z <= radiusZ; z++) {
                    if (checks++ >= maxChecks) {
                        return airCount; // Early exit guardrail
                    }
                    mutablePos.setWithOffset(center, x, y, z);
                    if (isAirLike(level, mutablePos)) {
                        airCount++;
                    }
                }
            }
        }
        return airCount;
    }

    /**
     * Validates if a specific volume contains at least a minimum threshold of air.
     * Short-circuits the moment the threshold is hit for maximum performance.
     */
    public static boolean hasMinimumAirVolume(WorldGenLevel level, BlockPos center, int radiusX, int radiusY, int radiusZ, int minAir, int maxChecks) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        int airCount = 0;
        int checks = 0;

        for (int y = -radiusY; y <= radiusY; y++) {
            for (int x = -radiusX; x <= radiusX; x++) {
                for (int z = -radiusZ; z <= radiusZ; z++) {
                    if (checks++ >= maxChecks) {
                        return false;
                    }
                    mutablePos.setWithOffset(center, x, y, z);
                    if (isAirLike(level, mutablePos)) {
                        airCount++;
                        if (airCount >= minAir) {
                            return true; // Fast exit!
                        }
                    }
                }
            }
        }
        return false;
    }
}
