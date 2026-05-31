package com.displace.cavetraps.worldgen.feature;

import com.displace.cavetraps.block.ModBlocks;
import com.displace.cavetraps.blockentities.FallingTrapBlockEntity;
import com.displace.cavetraps.worldgen.feature.config.FallingBlockTrapConfig;
import com.displace.cavetraps.worldgen.feature.config.FallingBlockTrapVariant;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import java.util.HashSet;
import java.util.Set;

import static com.displace.cavetraps.worldgen.util.CaveScanUtil.*;
import static com.displace.cavetraps.worldgen.util.TrapPlacementUtil.*;

public class FallingBlockTrapFeature extends Feature<FallingBlockTrapConfig> {
    public FallingBlockTrapFeature(Codec<FallingBlockTrapConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<FallingBlockTrapConfig> featurePlaceContext) {
        WorldGenLevel level = featurePlaceContext.level();
        BlockPos origin = featurePlaceContext.origin();
        RandomSource random = featurePlaceContext.random();
        FallingBlockTrapConfig config = featurePlaceContext.config();

        // find floor below
        BlockPos floorPosition = findFloorBelow(level, origin, 7);
        if (floorPosition == null) {
            return false;
        } else {
            origin = floorPosition;
        }



        // exit if outside y-level
        int y = origin.getY();
        if (y < config.minY() || y > config.maxY()) return false;

        // find the cave floor
        if (!isCaveFloor(level, origin.below())) return false;

        BlockState decoStone = y > 8 ? Blocks.STONE.defaultBlockState() : Blocks.DEEPSLATE.defaultBlockState();
        BlockState lureOre = y > 8 ? Blocks.DIAMOND_ORE.defaultBlockState() : Blocks.DEEPSLATE_DIAMOND_ORE.defaultBlockState();

        // Quick checks for above air-space. Quick cancel for performance.
        if (!hasAirAbove(level, origin, 3)) return false;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) continue; //skips the center
                BlockPos neighbor = origin.offset(dx, 0, dz);
                if (!hasAirAbove(level, neighbor, 2)) return false;
            }
        }

        FallingBlockTrapVariant variant = FallingBlockTrapVariant.getRandom(random);
        // TODO - Create new config options
//        int depth = config.baseDepth;
        int depth = 6;
        // increased depth for height-based traps.
        if (variant == FallingBlockTrapVariant.LAVA || variant == FallingBlockTrapVariant.SPIKE || variant == FallingBlockTrapVariant.SPIKE_AND_LAVA) {
            depth *= 2;
        }

        // gets hold of the top layer of the blocks.
        Set<BlockPos> trapFootprint = new HashSet<>();

        trapFootprint.add(origin);

        int maxTerrainCurve = 4;
        int numBlobs = 4 + random.nextInt(3);
        int maxSpread = 3;
        float lureOreChange = 1f;

        for (int i = 0; i < numBlobs; i++) {
            // Force the very first blob to be at the exact origin to anchor the shape.
            // Subsequent blobs drift slightly from the center.
            int offsetX = (i == 0) ? 0 : random.nextInt(maxSpread * 2 + 1) - maxSpread;
            int offsetZ = (i == 0) ? 0 : random.nextInt(maxSpread * 2 + 1) - maxSpread;

            BlockPos blobCenter = origin.offset(offsetX, 0, offsetZ);

            float radius = 2.0f + (random.nextFloat() * 2.0f);
            float radiusSqr = radius * radius;
            int bounds = (int) Math.ceil(radius);

            for (int dx = -bounds; dx <= bounds; dx++) {
                for (int dz = -bounds; dz <= bounds; dz++) {
                    if (dx * dx + dz * dz <= radiusSqr) {
                        BlockPos piece = blobCenter.offset(dx, 0, dz);

                        // Ensure the terrain isn't too steep before adding
                        if (Math.abs(piece.getY() - origin.getY()) <= maxTerrainCurve) {
                            trapFootprint.add(piece);
                        }
                    }
                }
            }
        }

        // Mask footprint to the environment's terrain height
        Set<BlockPos> conformedFootprint = new HashSet<>();
        Set<Long> flatFootprint = new HashSet<>();
        for (BlockPos pos : trapFootprint) {
            for (int dy = maxTerrainCurve; dy >= -maxTerrainCurve; dy--) {
                BlockPos checkPos = new BlockPos(pos.getX(), origin.getY() + dy, pos.getZ());

                if (!isAirLike(level, checkPos) && isAirLike(level, checkPos.above())) {
                    conformedFootprint.add(checkPos);
                    flatFootprint.add(ChunkPos.asLong(pos.getX(), pos.getZ()));
                    break;
                }
            }
        }
        trapFootprint = conformedFootprint;


        // Carves the space out. This could be moved to a different helper function.
        BlockPos.MutableBlockPos carvePos = new BlockPos.MutableBlockPos();
        for (BlockPos topPos : trapFootprint) {
            for (int dy = 0; dy <= depth; dy++) {
                carvePos.setWithOffset(topPos, 0, -dy, 0);

                if (carvePos.getY() <= level.getMinY() + 1) continue;
                boolean isWallBlock = isEdgeOfFootprint(topPos, flatFootprint) || dy == depth;

                if (isWallBlock) {
                    level.setBlock(carvePos, decoStone, 2);
                } else {
                    level.setBlock(carvePos, Blocks.AIR.defaultBlockState(), 2);
                }
            }
        }

        for (BlockPos topPos : trapFootprint) {

            if (isEdgeOfFootprint(topPos, flatFootprint)) continue;

            // TODO - flatten the bottom
            BlockPos bottomPos = topPos.below(depth - 1);

            setTrapBlock(level, topPos, ModBlocks.FALLING_TRAP_BLOCK.get().defaultBlockState());
            BlockEntity block = level.getBlockEntity(topPos);
            if (block instanceof FallingTrapBlockEntity be) {
                be.setCamoState(decoStone);
            }
            // layers the trap to avoid holes.
            setTrapBlock(level, topPos.below(), ModBlocks.FALLING_TRAP_BLOCK.get().defaultBlockState());
            if (level.getBlockEntity(topPos.below()) instanceof FallingTrapBlockEntity be) {
                be.setCamoState(decoStone);
            }

            // TODO - introduce more complex variance here.
            switch(variant) {
                case LAVA:
                    level.setBlock(bottomPos, Blocks.LAVA.defaultBlockState(), 2);
                    break;
                case SPIKE:
                    level.setBlock(bottomPos, Blocks.POINTED_DRIPSTONE.defaultBlockState(), 2);
                    break;
                case TNT:
                    level.setBlock(bottomPos, Blocks.TNT.defaultBlockState(), 2);
                    level.setBlock(bottomPos.above(), decoStone, 2);
                    break;
                case SPIKE_AND_LAVA:
                    // leave as lava for now
                    level.setBlock(bottomPos, Blocks.LAVA.defaultBlockState(), 2);
                    break;
                case SPIKE_AND_TNT:
                    // leave as TNT for now.
                    level.setBlock(bottomPos, Blocks.TNT.defaultBlockState(), 2);
                    level.setBlock(bottomPos.above(), decoStone, 2);
                    break;
            }
        }

        BlockPos centerSurface = null;
        for (BlockPos pos : trapFootprint) {
            if (pos.getX() == origin.getX() && pos.getZ() == origin.getZ()) {
                centerSurface = pos;
                break;
            }
        }

        if (centerSurface != null && !isEdgeOfFootprint(centerSurface, flatFootprint) && random.nextFloat() < lureOreChange) {
            if (level.getBlockEntity(centerSurface) instanceof FallingTrapBlockEntity be) {
                be.setCamoState(lureOre);
            }

            level.setBlock(centerSurface.above(3), Blocks.SEA_LANTERN.defaultBlockState(), 2);
        }
        return true;
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
}
