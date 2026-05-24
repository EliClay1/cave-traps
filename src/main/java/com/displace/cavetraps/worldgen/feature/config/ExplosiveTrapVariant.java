package com.displace.cavetraps.worldgen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ExplosiveTrapVariant(int weight, int explosiveLayers, int radius, String debugName) {

    public static final Codec<ExplosiveTrapVariant> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.INT.fieldOf("weight").forGetter(ExplosiveTrapVariant::weight),
            Codec.INT.fieldOf("explosive_layers").forGetter(ExplosiveTrapVariant::explosiveLayers),
            Codec.INT.fieldOf("radius").forGetter(ExplosiveTrapVariant::radius),
            Codec.STRING.fieldOf("debug_name").forGetter(ExplosiveTrapVariant::debugName)
    ).apply(inst, ExplosiveTrapVariant::new));
}
