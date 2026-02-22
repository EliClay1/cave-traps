package com.displace.cavetraps.entities;

import com.displace.cavetraps.CaveTraps;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister.Entities ENTITY_TYPES = DeferredRegister.createEntities(CaveTraps.MODID);

    public static final Supplier<EntityType<FallingTrapEntity>> FALLING_TRAP_ENTITY = ENTITY_TYPES.register(
            "falling_trap_entity",
            () -> EntityType.Builder.of(FallingTrapEntity::new, MobCategory.MISC)
                    .build(ResourceKey.create(
                            Registries.ENTITY_TYPE,
                            Identifier.fromNamespaceAndPath("cavetraps", "falling_trap_entity")
                    ))
    );

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
