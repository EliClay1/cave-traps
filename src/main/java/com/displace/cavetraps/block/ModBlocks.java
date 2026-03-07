package com.displace.cavetraps.block;

import com.displace.cavetraps.CaveTraps;
import com.displace.cavetraps.testing.TestBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CaveTraps.MODID);

    public static final DeferredBlock<FallingBlock> FALLING_TRAP_BLOCK = BLOCKS.register("falling_trap_block", registryName -> (
            new FallingTrapBlock(BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, registryName)).noOcclusion())
        ));

    public static final DeferredBlock<Block> TEST_BLOCK = BLOCKS.register("test_block", registryName -> (
            new TestBlock(BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, registryName)))
        ));

    public static final DeferredBlock<ExplosiveTrapBlock> EXPLOSIVE_TRAP_BLOCK = BLOCKS.register("explosive_trap_block", identifier -> (
            new ExplosiveTrapBlock(BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, identifier)).noOcclusion())
            ));

    // registers the Deferred register above. Passes into event bus.
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}