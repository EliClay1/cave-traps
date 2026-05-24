package com.displace.cavetraps.worldgen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record VineTrapConfig(
        int minY, int maxY, int groupMin, int groupMax, int spreadRadius, int verticalSearchRadius,
        int maxPlacementAttempts, int minCeilingClearance) implements FeatureConfiguration {

    public static final Codec<VineTrapConfig> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.INT.fieldOf("min_y").forGetter(VineTrapConfig::minY),
            Codec.INT.fieldOf("max_y").forGetter(VineTrapConfig::maxY),
            Codec.INT.fieldOf("group_min").forGetter(VineTrapConfig::groupMin),
            Codec.INT.fieldOf("group_max").forGetter(VineTrapConfig::groupMax),
            Codec.INT.fieldOf("spread_radius").forGetter(VineTrapConfig::spreadRadius),
            Codec.INT.fieldOf("vertical_search_radius").forGetter(VineTrapConfig::verticalSearchRadius),
            Codec.INT.fieldOf("max_placement_attempts").forGetter(VineTrapConfig::maxPlacementAttempts),
            Codec.INT.fieldOf("min_ceiling_clearance").forGetter(VineTrapConfig::minCeilingClearance)
    ).apply(inst, VineTrapConfig::new));
}