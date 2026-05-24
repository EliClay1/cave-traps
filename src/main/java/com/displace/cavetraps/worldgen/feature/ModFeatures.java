package com.displace.cavetraps.worldgen.feature;

import com.displace.cavetraps.CaveTraps;
import com.displace.cavetraps.worldgen.feature.config.ExplosiveTrapConfig;
import com.displace.cavetraps.worldgen.feature.config.FallingBlockTrapConfig;
import com.displace.cavetraps.worldgen.feature.config.VineTrapConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModFeatures {

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(BuiltInRegistries.FEATURE, CaveTraps.MODID);

    public static final Supplier<Feature<ExplosiveTrapConfig>> EXPLOSIVE_TRAP = FEATURES.register(
            "explosive_trap", () -> new ExplosiveTrapFeature(ExplosiveTrapConfig.CODEC));

    public static final Supplier<Feature<VineTrapConfig>> VINE_TRAP = FEATURES.register(
            "vine_trap", () -> new VineTrapFeature(VineTrapConfig.CODEC));

    public static final Supplier<Feature<FallingBlockTrapConfig>> FALLING_BLOCK_TRAP = FEATURES.register(
            "falling_block_trap", () -> new FallingBlockTrapFeature(FallingBlockTrapConfig.CODEC));

    public static void register(IEventBus eventBus) {
        FEATURES.register(eventBus);
    }

}
