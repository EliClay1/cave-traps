package com.displace.cavetraps.worldgen.feature;

import com.displace.cavetraps.block.ModBlocks;
import com.displace.cavetraps.worldgen.feature.config.FallingBlockTrapConfig;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import static com.displace.cavetraps.worldgen.util.CaveScanUtil.*;
import static com.displace.cavetraps.worldgen.util.TrapPlacementUtil.*;
import static java.lang.Math.round;

public class FallingBlockTrapFeature extends Feature<FallingBlockTrapConfig> {
    public FallingBlockTrapFeature(Codec<FallingBlockTrapConfig> codec) {
        super(codec);
    }

    private record LowerCaveHit(BlockPos airCenter, BlockPos approximateFloor, int gap) {}

    @Override
    public boolean place(FeaturePlaceContext<FallingBlockTrapConfig> featurePlaceContext) {
        WorldGenLevel level = featurePlaceContext.level();
        BlockPos origin = featurePlaceContext.origin();
        RandomSource random = featurePlaceContext.random();
        FallingBlockTrapConfig config = featurePlaceContext.config();

        // Quick checks for above air-space. Quick cancel for performance.
        if (!hasAirAbove(level, origin, 3)) return false;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) continue; //skips the center
                BlockPos neighbor = origin.offset(dx, 0, dz);
                if (!hasAirAbove(level, neighbor, 2)) return false;
            }
        }


//        int y = origin.getY();
//        if (y < config.minY() || y > config.maxY()) return false;
//
//        BlockPos upperFloor = findNearestCaveFloor(level, origin, config.upperSearchRadius(), config.upperVerticalSearchRadius());
//        if (upperFloor == null) return false;
//
//        // Check if there is a 5x5x4 space of air above the spot. If it not, fail early and quickly. This is especially important for the upperFloor position. If there isn't 4 blocks of air above, cancel early.
//        int totalNeededAir = (config.upperSearchRadius() * config.upperVerticalSearchRadius() * config.upperSearchRadius());
//        int upperCaveAirCount = countAirLikeInBox(level, upperFloor, config.upperSearchRadius() - 1, config.upperVerticalSearchRadius(), config.upperSearchRadius() - 1, totalNeededAir);
//        if (upperCaveAirCount < (totalNeededAir / 4)) return false;
//
//        // check if there is a surrounding amount of floor around the drop point.
//        if (!locatedOnFlatSurface(level, upperFloor, config.funnelTopRadius(), config.funnelTopRadius())) return false;
//
//        // check if there are at least 3 blocks of wall leading down the side.
//        int minAmountOfWall = (config.funnelTopRadius() - 2) * 8;
//        if (!hasSurroundingWalls(level, upperFloor, config.funnelTopRadius() + 1, 4, minAmountOfWall)) return false;
//
//        LowerCaveHit lowerHit = findLowerCave(level, upperFloor, config);
//
//        if (config.carveFunnel()) {
//            if (lowerHit != null) {
//                carveFunnel(level, upperFloor, lowerHit.approximateFloor(), config);
//            }
//        }
//
////        setRadialTrapBlocks(level, upperFloor, ModBlocks.FALLING_TRAP_BLOCK.get().defaultBlockState(), config.funnelTopRadius());
//
//        if (lowerHit != null) {
//            // modify  the floor spike generation. Heavy, more centralized.
//            return placeSpikes(level, lowerHit.approximateFloor(), config, random);
//        }
        return false;
    }


    private LowerCaveHit findLowerCave(WorldGenLevel level, BlockPos upperFloor, FallingBlockTrapConfig config) {
        BlockPos.MutableBlockPos probe =  new BlockPos.MutableBlockPos();

        for (int dy = config.minGapToLowerCave(); dy <= config.maxGapToLowerCave(); dy++) {
            probe.setWithOffset(upperFloor, 0, -dy, 0);
            if (!isAirLike(level, probe)) continue;

            boolean lowerCaveValid = hasMinimumAirVolume(level, probe, config.lowerSearchRadius(), 2,
                    config.lowerSearchRadius(), config.minLowerCaveAir(), config.maxAirChecks());
            if (!lowerCaveValid) continue;

            BlockPos lowerFloor = findFloorBelow(level, probe, config.maxGapToLowerCave());
            if (lowerFloor == null) continue;

            level.setBlock(lowerFloor, Blocks.SEA_LANTERN.defaultBlockState(), 2);

            return new LowerCaveHit(probe.immutable(), lowerFloor, dy);
        }
        return null;
    }

    private BlockPos findFloorBelow(WorldGenLevel level, BlockPos startAir, int maxDown) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int i = 1; i <= maxDown; i++) {
            pos.setWithOffset(startAir, 0, -i, 0);
            if (isSolidSupport(level, pos)) {
                return pos.immutable();
            }
        }
        return null;
    }

    private void carveFunnel(WorldGenLevel level, BlockPos upperFloor, BlockPos lowerFloor, FallingBlockTrapConfig config) {
        int totalDepth = upperFloor.getY() - lowerFloor.getY();

        if (totalDepth <= 0) return;

        int maxCarved = 1024;
        int carved = 0;

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int dy = 1; dy < totalDepth && carved < maxCarved; dy++) {
            int currentY = upperFloor.getY() - dy;
            float progress = (float) dy / totalDepth;
            int radius = round(lerp(config.funnelTopRadius(), config.funnelBottomRadius(), progress));
            int radiusSqr = radius * radius;

            for (int dx = -radius; dx <= radius && carved < maxCarved; dx++) {
                for (int dz =  -radius; dz <= radius && carved < maxCarved; dz++) {

                    if (dx * dx + dz * dz > radiusSqr) continue;

                    pos.set(upperFloor.getX() + dx, currentY, upperFloor.getZ() + dz);
                    if (canCarve(level, pos)) {
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
                        carved++;
                    }
                }
            }
        }

        for (int dy = 1; dy <= totalDepth; dy++) {
            pos = lowerFloor.mutable();
            pos.setY(upperFloor.getY() - dy);
            level.setBlock(pos, Blocks.SEA_LANTERN.defaultBlockState(), 2);
        }

        setTrapBlock(level, upperFloor, Blocks.SEA_LANTERN.defaultBlockState());
    }

    private boolean canCarve(WorldGenLevel level, BlockPos pos) {
        var state = level.getBlockState(pos);
        if (state.isAir()) return false;
        if (isLiquid(state)) return false;
        if (state.is(Blocks.BEDROCK)) return false;
        if (state.hasBlockEntity()) return false;
        // Only carve stone-tagged blocks for safety.
        // TODO - make the block remove ores as well.
        return state.is(BlockTags.BASE_STONE_OVERWORLD)
                || state.is(BlockTags.STONE_ORE_REPLACEABLES)
                || state.is(BlockTags.DEEPSLATE_ORE_REPLACEABLES)
                || state.is(BlockTags.DIRT)
                || state.is(BlockTags.OVERWORLD_CARVER_REPLACEABLES)
                || canReplaceForTrap(level, pos);
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    private boolean placeSpikes(WorldGenLevel level, BlockPos lowerFloor, FallingBlockTrapConfig config, RandomSource random) {
        int placed = 0;
        int attempts = 0;
        int maxAttempts = config.maxSpikePlacements() * 4;

        BlockPos.MutableBlockPos candidate = new BlockPos.MutableBlockPos();
        int r = config.lowerSearchRadius();
        while (placed < config.maxSpikePlacements() &&  attempts < maxAttempts) {
            attempts++;

            int dx = random.nextInt(r  * 2 + 1) - r;
            int dz = random.nextInt(r * 2 + 1) - r;
            candidate.setWithOffset(lowerFloor, dx, 0, dz);

            if (isCaveFloor(level, candidate)) continue;
            if (random.nextFloat() >= config.spikeChance()) continue;

            BlockPos spikePos =  candidate.above().immutable();
            if (isAirLike(level, spikePos)) continue;

            setIfReplaceable(level, spikePos, Blocks.POINTED_DRIPSTONE.defaultBlockState());

            placed++;
        }
        return placed > 0;
    }
}
