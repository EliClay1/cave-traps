package com.displace.cavetraps.block;

import com.displace.cavetraps.CaveTraps;
import com.displace.cavetraps.entity.FallingTrapBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class FallingTrapBlock extends FallingBlock implements EntityBlock {

    // TODO - Make it so the block can only change render states one time. Then the player must break the block and replace it to change it's render state.
    //  follow the same code that used for stability neighbor detection. Use it for camo-mapping detection.

    public static final Property<Boolean> STABLE = BooleanProperty.create("stable");
    public final int initialFallTime = 10;

    public FallingTrapBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(STABLE, true));
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
//        if (!level.isClientSide()) {
//            BlockEntity blockEntity = level.getBlockEntity(pos);
//            if (blockEntity instanceof FallingTrapBlockEntity trapBlockEntity) {
//                trapBlockEntity.setCamoState(Blocks.DIAMOND_BLOCK.defaultBlockState());
//                CaveTraps.LOGGER.info("DEBUG: Set camo to diamond block at " + pos);
//            }
//        }
//        super.onPlace(state, level, pos, oldState, isMoving);
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
        if (!level.isClientSide()) {
            if (isGroupPowered(level, pos)) {
                return;
            }

        }
        if (state.getValue(STABLE)) {
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

    public boolean isGroupPowered(Level level, BlockPos startPos) {
        Queue<BlockPos> queue = new LinkedList<>();
        Set<BlockPos> visited = new HashSet<>();

        queue.add(startPos);
        visited.add(startPos);

        int MAX_SEARCH_SIZE = 2048;
        int searchedCount = 0;

        while (!queue.isEmpty()) {
            BlockPos currentPos = queue.poll();
            searchedCount++;

            if (level.hasNeighborSignal(currentPos)) {
                return true;
            }
            if (searchedCount >= MAX_SEARCH_SIZE) {
                break;
            }
            for (Direction direction : Direction.values()) {
                BlockPos neighborPos = currentPos.relative(direction);
                if (!visited.contains(neighborPos)) {
                    BlockState neighborState = level.getBlockState(neighborPos);
                    if (neighborState.getBlock() instanceof FallingTrapBlock) {
                        visited.add(neighborPos);
                        queue.add(neighborPos);
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected MapCodec<? extends FallingBlock> codec() {
        return null;
    }

    @Override
    public int getDustColor(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return 0;
    }

    @Override
    protected void falling(FallingBlockEntity entity) {
        BlockEntity blockEntity = entity.level().getBlockEntity(entity.blockPosition());
        if (blockEntity instanceof FallingTrapBlockEntity trapBlockEntity) {
            BlockState camo = trapBlockEntity.getCamoState();
            trapBlockEntity.saveWithFullMetadata(entity.level().registryAccess());
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STABLE);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new FallingTrapBlockEntity(blockPos, blockState);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return EntityBlock.super.getTicker(level, state, blockEntityType);
    }
}
