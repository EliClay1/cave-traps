package com.displace.cavetraps.entity;

import com.displace.cavetraps.CaveTraps;
import com.displace.cavetraps.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CaveTraps.MODID);

    public static final Supplier<BlockEntityType<FallingTrapBlockEntity>> FALLING_TRAP_BE = BLOCK_ENTITY_TYPES.register(
            "falling_block_entity", () -> new BlockEntityType<>(FallingTrapBlockEntity::new,
            false,
            ModBlocks.FALLING_TRAP_BLOCK.get()
    ));
}
