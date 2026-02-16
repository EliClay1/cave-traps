package com.displace.cavetraps.block;

import com.displace.cavetraps.CaveTraps;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class FallingTrapBlock extends FallingBlock {

    public static final Property<Boolean> STABLE = BooleanProperty.create("stable");
    public final int initialFallTime = 10;

    public FallingTrapBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(STABLE, true));
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos pos,
                                     Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {

        if (!level.isClientSide()) {
            if (neighborState.getBlock() instanceof FallingTrapBlock && !neighborState.getValue(STABLE)) {
                return state.setValue(STABLE, false);
            }
        }
        return super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!level.isClientSide() && state.getValue(STABLE)) {
            level.setBlockAndUpdate(pos, state.setValue(STABLE, false));
            level.scheduleTick(pos, state.getBlock(), initialFallTime);
        }
        super.stepOn(level, pos, state, entity);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (isFree(level.getBlockState(pos.below())) && !state.getValue(STABLE) && pos.getY() >= level.getMinY()) {
            FallingBlockEntity fallingblockentity = FallingBlockEntity.fall(level, pos, state);
            this.falling(fallingblockentity);
        }
    }

//    private void triggerNeighbor(Level level, BlockPos pos) {
//        for (Direction d: Direction.Plane.HORIZONTAL) {
//            BlockPos neighborPos = pos.relative(d);
//            BlockState neighborState = level.getBlockState(neighborPos);
//            if (neighborState.getBlock() instanceof FallingTrapBlock && neighborState.getValue(STABLE)) {
//                level.setBlock(neighborPos, neighborState.setValue(STABLE, false), 3);
//                level.scheduleTick(neighborPos, this, 4);
//            }
//        }
//    }

    @Override
    protected MapCodec<? extends FallingBlock> codec() {
        return null;
    }

    @Override
    public int getDustColor(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return 0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STABLE);
    }
}
