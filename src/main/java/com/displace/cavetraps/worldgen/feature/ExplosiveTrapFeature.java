package com.displace.cavetraps.worldgen.feature;

import com.displace.cavetraps.CaveTraps;
import com.displace.cavetraps.CaveTrapsClient;
import com.displace.cavetraps.block.ModBlocks;
import com.displace.cavetraps.worldgen.feature.config.ExplosiveTrapConfig;
import com.displace.cavetraps.worldgen.feature.config.ExplosiveTrapVariant;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import java.util.List;

import static com.displace.cavetraps.worldgen.util.CaveScanUtil.findNearestCaveFloor;
import static com.displace.cavetraps.worldgen.util.TrapPlacementUtil.*;

public class ExplosiveTrapFeature extends Feature<ExplosiveTrapConfig> {
    public ExplosiveTrapFeature(Codec<ExplosiveTrapConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<ExplosiveTrapConfig> featurePlaceContext) {
        WorldGenLevel level = featurePlaceContext.level();
        BlockPos origin = featurePlaceContext.origin();
        ExplosiveTrapConfig config = featurePlaceContext.config();
        RandomSource random = featurePlaceContext.random();

        if (origin.getY() < config.minY() || origin.getY() > config.maxY()) {
            return false;
        }

        BlockPos floorPos = findNearestCaveFloor(level, origin, config.horizontalSearchRadius(), config.verticalSearchRadius());
        if (floorPos == null) {
            return false;
        }

        if (!hasClearanceAbove(level, floorPos, config.minAirAbove())) {
            return false;
        }

        if (!canPlaceEmbeddedFloorTrap(level, floorPos)) {
            return false;
        }

        ExplosiveTrapVariant variant = pickVariant(random, config.variants());

        setTrapBlock(level, floorPos.above(), ModBlocks.EXPLOSIVE_TRAP_PLUNGER_BLOCK.get().defaultBlockState());
        setTrapBlock(level, floorPos, ModBlocks.EXPLOSIVE_TRAP_BLOCK.get().defaultBlockState());

        if (variant.explosiveLayers() > 0 && variant.radius() > 0) {
            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
            for (int yOffset = 2; yOffset <= variant.explosiveLayers(); yOffset++) {
                for (int xOffset = -variant.radius(); xOffset <= variant.radius(); xOffset++) {
                    for (int zOffset = -variant.radius(); zOffset <= variant.radius(); zOffset++) {
                        mutablePos.setWithOffset(floorPos, xOffset, -yOffset, zOffset);
                        setIfReplaceable(level, mutablePos, Blocks.TNT.defaultBlockState());
                    }
                }
            }
        }
        return true;
    }

    private ExplosiveTrapVariant pickVariant(RandomSource random, List<ExplosiveTrapVariant> variants) {
        if (variants == null || variants.isEmpty()) {
            throw new IllegalStateException("ExplosiveTrapFeature requires at least one variant in config.");
        }

        int totalWeight = 0;
        for (ExplosiveTrapVariant variant : variants) {
            totalWeight += Math.max(0, variant.weight());
        }

        if (totalWeight <= 0) {
            return variants.getFirst();
        }

        int roll = random.nextInt(totalWeight);
        for (ExplosiveTrapVariant variant : variants) {
            roll -= Math.max(0, variant.weight());
            if (roll < 0) {
                return variant;
            }
        }
        return variants.getLast();
    }
}
