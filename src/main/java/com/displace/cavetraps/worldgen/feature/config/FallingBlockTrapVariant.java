package com.displace.cavetraps.worldgen.feature.config;

import net.minecraft.util.RandomSource;

public enum FallingBlockTrapVariant {
    LAVA, SPIKE, TNT, SPIKE_AND_TNT, SPIKE_AND_LAVA;

    public static FallingBlockTrapVariant getRandom(RandomSource random) {
        return values()[random.nextInt(values().length)];
    }
}
