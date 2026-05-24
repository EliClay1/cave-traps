package com.displace.cavetraps.worldgen.feature.config;

import com.displace.cavetraps.worldgen.feature.VineTrapFeature;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class VineTrapConfig implements FeatureConfiguration {
    public static final Codec<VineTrapConfig> CODEC = null;
    
//    public static final Codec<VineTrapConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
//    ).apply(instance, VineTrapConfig::new));
}
