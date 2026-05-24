package com.displace.cavetraps.worldgen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import java.util.List;

public record ExplosiveTrapConfig(
        int minY, int maxY, int horizontalSearchRadius, int verticalSearchRadius, int minAirAbove,
        List<ExplosiveTrapVariant> variants) implements FeatureConfiguration {

    public static final Codec<ExplosiveTrapConfig> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.INT.fieldOf("min_y").forGetter(ExplosiveTrapConfig::minY),
            Codec.INT.fieldOf("max_y").forGetter(ExplosiveTrapConfig::maxY),
            Codec.INT.fieldOf("horizontal_search_radius").forGetter(ExplosiveTrapConfig::horizontalSearchRadius),
            Codec.INT.fieldOf("vertical_search_radius").forGetter(ExplosiveTrapConfig::verticalSearchRadius),
            Codec.INT.fieldOf("min_air_above").forGetter(ExplosiveTrapConfig::minAirAbove),
            ExplosiveTrapVariant.CODEC.listOf().fieldOf("variants").forGetter(ExplosiveTrapConfig::variants)
    ).apply(inst, ExplosiveTrapConfig::new));
}