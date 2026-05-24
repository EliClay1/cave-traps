package com.displace.cavetraps.worldgen.feature;

import com.displace.cavetraps.worldgen.feature.config.ExplosiveTrapConfig;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class ExplosiveTrapFeature extends Feature<ExplosiveTrapConfig> {
    public ExplosiveTrapFeature(Codec<ExplosiveTrapConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<ExplosiveTrapConfig> featurePlaceContext) {
        return false;
    }
}
