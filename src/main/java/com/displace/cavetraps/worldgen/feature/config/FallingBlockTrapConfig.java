package com.displace.cavetraps.worldgen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import java.util.List;

public record FallingBlockTrapConfig() implements FeatureConfiguration {
    public static final Codec<FallingBlockTrapConfig> CODEC = null;
}
