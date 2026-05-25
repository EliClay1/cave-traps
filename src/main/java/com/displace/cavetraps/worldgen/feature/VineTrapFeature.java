package com.displace.cavetraps.worldgen.feature;

import com.displace.cavetraps.block.ModBlocks;
import com.displace.cavetraps.worldgen.feature.config.VineTrapConfig;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import static com.displace.cavetraps.worldgen.util.CaveScanUtil.*;
import static com.displace.cavetraps.worldgen.util.TrapPlacementUtil.setTrapBlock;

public class VineTrapFeature extends Feature<VineTrapConfig> {
    public VineTrapFeature(Codec<VineTrapConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<VineTrapConfig> featurePlaceContext) {
        WorldGenLevel level = featurePlaceContext.level();
        BlockPos origin = featurePlaceContext.origin();
        VineTrapConfig config = featurePlaceContext.config();
        RandomSource random = featurePlaceContext.random();

        if (origin.getY() < config.minY() || origin.getY() > config.maxY()) {
            return false;
        }

        int targetGroupSize = config.groupMin();
        if (config.groupMax() > config.groupMin()) {
            targetGroupSize += random.nextInt(config.groupMax() - config.groupMin() + 1);
        }

        int placedCount = 0;
        int attempts = 0;

        while (attempts < config.maxPlacementAttempts() && placedCount < targetGroupSize) {
            attempts++;

            // Pick random horizontal offset within the spread radius
            int offsetX = random.nextInt(config.spreadRadius() * 2 + 1) - config.spreadRadius();
            int offsetZ = random.nextInt(config.spreadRadius() * 2 + 1) - config.spreadRadius();
            BlockPos searchStart = origin.offset(offsetX, 0, offsetZ);

            // Search for a ceiling anchor near the offset (narrow horizontal wiggle room)
            BlockPos airPos = findNearestCaveFloor(level, searchStart, 1, config.verticalSearchRadius());

            if (airPos == null) {
                continue;
            }

            // Validate that the trap has enough air clearance to sprout upwards
            // TODO - fix so that the json data makes it clear that it is checking ABOVE, not below.
            boolean hasClearance = true;
            for (int i = 1; i < config.minCeilingClearance(); i++) {
                if (!isAirLike(level, airPos.above(i))) {
                    hasClearance = false;
                    break;
                }
            }

            if (!hasClearance) {
                continue;
            }

            setTrapBlock(level, airPos, ModBlocks.VINE_TRAP_BLOCK.get().defaultBlockState());
            level.scheduleTick(airPos, ModBlocks.VINE_TRAP_BLOCK.get().defaultBlockState().getBlock(), 10);
            placedCount++;
        }

        return placedCount > 0;
    }
}
