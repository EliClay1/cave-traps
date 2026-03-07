package com.displace.cavetraps.block;

import com.displace.cavetraps.blockentities.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class ExplosiveTrapBlock extends Block implements EntityBlock {
    public ExplosiveTrapBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return ModBlockEntities.EXPLOSIVE_TRAP_BLOCK_ENTITY.get().create(blockPos, blockState);
    }
}
