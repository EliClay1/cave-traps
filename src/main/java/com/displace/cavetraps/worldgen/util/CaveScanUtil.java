package com.displace.cavetraps.worldgen.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.lwjgl.opengl.INTELBlackholeRender;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.ArrayList;
import java.util.List;

public class CaveScanUtil {

    /**
     * Designed to check for air-like substances in cave spaces. Avoids liquids.
     */
    public static boolean isAirLike(WorldGenLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isAir() || state.is(Blocks.CAVE_VINES) || state.is(Blocks.CAVE_VINES_PLANT) || state.is(Blocks.CAVE_AIR);
    }

    public static boolean isLiquid(BlockState blockState) {
        return blockState.is(Blocks.WATER) || blockState.is(Blocks.LAVA);
    }

    /**
     * Determines if a block is a solid surface that can hold a trap.
     */
    public static boolean isSolidSupport(WorldGenLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos.below());
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
        BlockPos bestPos = null;
        double bestDistSquared = Double.MAX_VALUE;

        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int y = -verticalRadius; y <= verticalRadius; y++) {
            for (int x = -horizontalRadius; x <= horizontalRadius; x++) {
                for (int z = -horizontalRadius; z <= horizontalRadius; z++) {
                    mutable.set(origin.getX() + x, origin.getY() + y, origin.getZ() + z);

                    if (isCaveFloor(level, mutable)) {
                        double distSquared = mutable.distSqr(origin);
                        if (distSquared < bestDistSquared) {
                            bestDistSquared = distSquared;
                            bestPos = mutable.immutable().below();
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

        // ensures that there is enough space directly above the trap spot.
        for (int y = 1; y <= radiusY; y++) {
            if (!isAirLike(level, center.above(y))) return airCount;
        }

        for (int y = 1; y <= radiusY; y++) {
            for (int x = -radiusX; x <= radiusX; x++) {
                for (int z = -radiusZ; z <= radiusZ; z++) {
                    if (checks++ > maxChecks) {
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
        if (minAir <= 0) return true;

        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        int airCount = 0;
        int checks = 0;

        // Use the largest radius to define our bounding box
        int maxRadius = Math.max(radiusX, Math.max(radiusY, radiusZ));

        // Scan outward in expanding concentric distances
        for (int r = 0; r <= maxRadius; r++) {
            for (int y = -r; y <= r; y++) {
                if (Math.abs(y) > radiusY) continue; // Respect specific Y radius

                for (int x = -r; x <= r; x++) {
                    if (Math.abs(x) > radiusX) continue; // Respect specific X radius

                    for (int z = -r; z <= r; z++) {
                        if (Math.abs(z) > radiusZ) continue; // Respect specific Z radius

                        // Only process the 'shell' of the current radius to avoid rescanning
                        if (Math.max(Math.abs(x), Math.max(Math.abs(y), Math.abs(z))) != r) continue;

                        if (checks >= maxChecks) return false;
                        checks++;

                        mutablePos.setWithOffset(center, x, y, z);

                        // Uncomment for debug visualization:
                        // level.setBlock(mutablePos, Blocks.SEA_LANTERN.defaultBlockState(), 2);

                        if (isAirLike(level, mutablePos)) {
                            airCount++;
                            if (airCount >= minAir) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * checks if there are surrounding walls going downward around the fall point. Prevents weird generations.
     */
    public static boolean hasSurroundingWalls(WorldGenLevel level, BlockPos center, int radiusX, int radiusY, int minimumWallAmount) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        int totalCount = 0;

        for (int z = -radiusX; z <= radiusX; z++) {
            for (int y = -radiusY; y <= 0; y++) {
                for (int x = -radiusX; x <= radiusX; x++) {
                    if (x == -radiusX || x == radiusX || z == -radiusX || z == radiusX) {
                        if(!isAirLike(level, mutablePos.setWithOffset(center, x, y, z))) totalCount++;
                        if (totalCount > minimumWallAmount) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean locatedOnFlatSurface(WorldGenLevel level, BlockPos center, int radiusX, int radiusZ) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (int z = -radiusZ; z <= radiusZ; z++) {
            for (int x = -radiusX; x <= radiusX; x++) {
                if (isAirLike(level, mutablePos.setWithOffset(center, x, center.getY(), z))) return false;
            }
        }
        return true;
    }

    // create a util function to ensure that explosive trap TNT isn't visible.
}
