package com.displace.cavetraps.testing;

import com.sun.jna.platform.win32.COM.EnumVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class TestRenderEntityBlock extends Block implements EntityBlock {
    public TestRenderEntityBlock(Properties props) {
        super(props);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new TestRenderBlockEntity(blockPos, blockState);
    }

    // this is particularly useful so that the surrounding blocks know when the block is removed. Not entirely
    // important for testing purposes, but it is nice to know that we can make all neighbors be affected by
    // this singular block.
    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }

    // The next two functions are 1. a helper to convert generics and 2. sends information here to the tick logic
    // in the custom BlockEntity class.
    private static <E extends BlockEntity, A extends BlockEntity> @Nullable BlockEntityTicker<A> createTickerHelper(
            BlockEntityType<A> type, BlockEntityType<E> checkedType, BlockEntityTicker<? super E> ticker) {
        return checkedType == type ? (BlockEntityTicker<A>) ticker: null;
        // TODO - fix this unchecked operation. Figure out how to check it.
    }
    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, ModBlockEntities.TEST_RENDER_BE.get(), TestRenderBlockEntity::tick);
    }
}
