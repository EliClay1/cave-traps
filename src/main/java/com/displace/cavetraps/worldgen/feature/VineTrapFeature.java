package com.displace.cavetraps.worldgen.feature;

import com.displace.cavetraps.worldgen.feature.config.VineTrapConfig;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class VineTrapFeature extends Feature<VineTrapConfig> {
    public VineTrapFeature(Codec<VineTrapConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<VineTrapConfig> featurePlaceContext) {
        return false;
    }
}
