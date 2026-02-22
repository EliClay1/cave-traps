package com.displace.cavetraps.testing;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TestRenderBlockEntity extends BlockEntity {
    public TestRenderBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.TEST_RENDER_BE.get(), pos, blockState);
    }
}
