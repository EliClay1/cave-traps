package com.displace.cavetraps.blockentities;

import com.displace.cavetraps.CaveTraps;
import com.displace.cavetraps.block.ModBlocks;
import com.displace.cavetraps.testing.TestBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CaveTraps.MODID);

    public static final Supplier<BlockEntityType<FallingTrapBlockEntity>> FALLING_TRAP_BLOCK_ENTITY = BLOCK_ENTITIES.register(
            "falling_trap_block_entity", identifier -> new BlockEntityType<>(
                    FallingTrapBlockEntity::new, false, ModBlocks.FALLING_TRAP_BLOCK.get()
            )
    );

    public static final Supplier<BlockEntityType<TestBlockEntity>> TEST_BLOCK_ENTITY = BLOCK_ENTITIES.register(
            "test_block_entity", () -> new BlockEntityType<>(
                    TestBlockEntity::new, false, ModBlocks.TEST_BLOCK.get()
            )
    );

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
