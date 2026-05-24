package com.displace.cavetraps.worldgen.feature.config;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class ExplosiveTrapConfig implements FeatureConfiguration {
    public static final Codec<ExplosiveTrapConfig> CODEC = null;

//    public static final Codec<ExplosiveTrapConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
//    ).apply(instance, ExplosiveTrapConfig::new));
}
