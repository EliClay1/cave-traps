package com.displace.cavetraps.testing;

import com.displace.cavetraps.CaveTraps;
import com.displace.cavetraps.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CaveTraps.MODID);

    public static final Supplier<BlockEntityType<TestRenderBlockEntity>> TEST_RENDER_BE = BLOCK_ENTITIES.register(
            "test_render_block_entity", () -> new BlockEntityType<>(
                    TestRenderBlockEntity::new, false, ModBlocks.TEST_RENDER_BLOCK.get()
            )
    );

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
