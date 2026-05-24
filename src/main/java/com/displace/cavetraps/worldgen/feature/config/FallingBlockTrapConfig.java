package com.displace.cavetraps.worldgen.feature.config;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class FallingBlockTrapConfig implements FeatureConfiguration {
    public static final Codec<FallingBlockTrapConfig> CODEC = null;

//    public static final Codec<FallingBlockTrapConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
//    ).apply(instance, FallingBlockTrapConfig::new));
}
