package com.displace.cavetraps.worldgen.feature;

import com.displace.cavetraps.worldgen.feature.config.FallingBlockTrapConfig;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class FallingBlockTrapFeature extends Feature<FallingBlockTrapConfig> {
    public FallingBlockTrapFeature(Codec<FallingBlockTrapConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<FallingBlockTrapConfig> featurePlaceContext) {
        return false;
    }
}
