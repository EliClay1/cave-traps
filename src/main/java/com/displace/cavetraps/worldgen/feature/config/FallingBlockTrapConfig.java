package com.displace.cavetraps.worldgen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import java.util.List;

public record FallingBlockTrapConfig(
        int minY, int maxY, int upperSearchRadius, int upperVerticalSearchRadius, int lowerSearchRadius,
        int minGapToLowerCave, int maxGapToLowerCave, int minUpperCaveAir, int minLowerCaveAir, int maxAirChecks,
        int funnelTopRadius, int funnelBottomRadius, float spikeChance, int maxSpikePlacements, boolean carveFunnel
) implements FeatureConfiguration {
    public static final Codec<FallingBlockTrapConfig> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("min_y").forGetter(FallingBlockTrapConfig::minY),
                    Codec.INT.fieldOf("max_y").forGetter(FallingBlockTrapConfig::maxY),
                    Codec.INT.fieldOf("upper_search_radius").forGetter(FallingBlockTrapConfig::upperSearchRadius),
                    Codec.INT.fieldOf("upper_vertical_search_radius").forGetter(FallingBlockTrapConfig::upperVerticalSearchRadius),
                    Codec.INT.fieldOf("lower_search_radius").forGetter(FallingBlockTrapConfig::lowerSearchRadius),
                    Codec.INT.fieldOf("min_gap_to_lower_cave").forGetter(FallingBlockTrapConfig::minGapToLowerCave),
                    Codec.INT.fieldOf("max_gap_to_lower_cave").forGetter(FallingBlockTrapConfig::maxGapToLowerCave),
                    Codec.INT.fieldOf("min_upper_cave_air").forGetter(FallingBlockTrapConfig::minUpperCaveAir),
                    Codec.INT.fieldOf("min_lower_cave_air").forGetter(FallingBlockTrapConfig::minLowerCaveAir),
                    Codec.INT.fieldOf("max_air_checks").forGetter(FallingBlockTrapConfig::maxAirChecks),
                    Codec.INT.fieldOf("funnel_top_radius").forGetter(FallingBlockTrapConfig::funnelTopRadius),
                    Codec.INT.fieldOf("funnel_bottom_radius").forGetter(FallingBlockTrapConfig::funnelBottomRadius),
                    Codec.FLOAT.fieldOf("spike_chance").forGetter(FallingBlockTrapConfig::spikeChance),
                    Codec.INT.fieldOf("max_spike_placements").forGetter(FallingBlockTrapConfig::maxSpikePlacements),
                    Codec.BOOL.fieldOf("carve_funnel").forGetter(FallingBlockTrapConfig::carveFunnel)
            ).apply(instance, FallingBlockTrapConfig::new)
    );
}
