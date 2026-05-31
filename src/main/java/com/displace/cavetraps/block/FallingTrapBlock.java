package com.displace.cavetraps.block;

import com.displace.cavetraps.CaveTraps;
import com.displace.cavetraps.blockentities.FallingTrapBlockEntity;
import com.displace.cavetraps.entities.FallingTrapEntity;
import com.displace.cavetraps.entities.ModEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.redstone.Orientation;
import org.jspecify.annotations.Nullable;

import javax.management.Query;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;


public class FallingTrapBlock extends FallingBlock implements EntityBlock {

    public static final Property<Boolean> STABLE = BooleanProperty.create("stable");
    public static final Property<Boolean> HAS_CAMO = BooleanProperty.create("has_camo");
    public static final Property<Boolean> GENERATED = BooleanProperty.create("generated");
    public final int initialFallTime = 10;

    public FallingTrapBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(STABLE, true));
        this.registerDefaultState(this.getStateDefinition().any().setValue(HAS_CAMO, false));
        this.registerDefaultState(this.getStateDefinition().any().setValue(GENERATED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STABLE);
        builder.add(HAS_CAMO);
        builder.add(GENERATED);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {

        if (!level.isClientSide()) {
            if (!state.getValue(HAS_CAMO)) {
                level.scheduleTick(pos, this, 1);
            }
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, @Nullable Orientation orientation, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, orientation, movedByPiston);

        if (!level.isClientSide() && !state.getValue(HAS_CAMO)) {
            level.scheduleTick(pos, this, 1);
        }
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos pos,
                                     Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        return super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!level.isClientSide()) {
            if (isGroupPowered(level, pos)) {
                return;
            }

            if (state.getValue(STABLE)) {
                triggerGroupFall(level, pos);
            }
        }
        super.stepOn(level, pos, state, entity);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (isFree(level.getBlockState(pos.below())) && !state.getValue(STABLE) && pos.getY() >= level.getMinY()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            BlockState camo = ModBlocks.FALLING_TRAP_BLOCK.get().defaultBlockState();
            CompoundTag customData = new CompoundTag();

            if (blockEntity instanceof FallingTrapBlockEntity trapBlockEntity) {
                camo = trapBlockEntity.getCamoState();
                customData = trapBlockEntity.saveWithFullMetadata(level.registryAccess());
            }

            if (camo == null || camo.isAir()) {
                camo = state;
            }

            level.removeBlockEntity(pos);

            BlockState landingState = state.setValue(STABLE, true).setValue(HAS_CAMO, false);
            FallingTrapEntity.spawn(ModEntities.FALLING_TRAP_ENTITY.get(), level, pos, landingState, camo, customData);

        } else if (!state.getValue(HAS_CAMO)) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof FallingTrapBlockEntity trapBE && !trapBE.getHasBeenTriggered()) {
                tryAcquireGroupCamo(level, pos);
            }
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

    // TODO - convert this logic, and the other group logic into a "find connected blocks" function. Then pass that into onLand triggerFall, and groupCamo. That way all of those functionalities happen at the same time.
    public void tryAcquireGroupCamo(Level level, BlockPos startPos) {
        Queue<BlockPos> queue = new LinkedList<>();
        Set<BlockPos> visited = new HashSet<>();
        Set<BlockPos> trapsToUpdate = new HashSet<>();

        queue.add(startPos);
        visited.add(startPos);

        int MAX_SEARCH_SIZE = 2048;
        int searchedCount = 0;

        BlockState foundCamo = null;

        while (!queue.isEmpty()) {
            BlockPos currentPos = queue.poll();
            BlockState currentState = level.getBlockState(currentPos);

            if (!currentState.getValue(HAS_CAMO)) {
                trapsToUpdate.add(currentPos);
            }

            searchedCount++;

            if (foundCamo == null && currentState.getValue(HAS_CAMO)) {
                BlockEntity be = level.getBlockEntity(currentPos);
                if (be instanceof FallingTrapBlockEntity trapBE) {
                    BlockState camo = trapBE.getCamoState();
                    if (camo != null && !camo.isAir()) {
                        foundCamo = camo;
                    }
                }
            }

            if (foundCamo == null) {
                BlockPos northPos = currentPos.north();
                BlockState northState = level.getBlockState(northPos);

                if (!northState.isAir() && !(northState.getBlock() instanceof FallingTrapBlock)) {
                    foundCamo = northState;
                }
            }

            if (searchedCount >= MAX_SEARCH_SIZE) {
                break;
            }

            for (Direction direction : Direction.values()) {
                if (direction == Direction.UP || direction == Direction.DOWN) {
                    continue;
                }
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

        if (foundCamo != null) {
            for (BlockPos pos : trapsToUpdate) {
                BlockState state = level.getBlockState(pos);
                if (state.getBlock() instanceof FallingTrapBlock && !state.getValue(HAS_CAMO)) {
                    BlockEntity be = level.getBlockEntity(pos);
                    if (be instanceof FallingTrapBlockEntity trapBE) {
                        if (!trapBE.getHasBeenTriggered()) {
                            trapBE.setCamoState(foundCamo);
                            trapBE.setHasBeenTriggered(true);
                            level.setBlockAndUpdate(pos, state.setValue(HAS_CAMO, true));
                        }
                    }
                }
            }
        }
    }

    // logic is cloned from acquiring group camo. Could be fixed later.
    public void triggerGroupFall(Level level, BlockPos startPos) {
        Queue<BlockPos> queue = new LinkedList<>();
        Set<BlockPos> visited = new HashSet<>();
        queue.add(startPos);
        visited.add(startPos);

        int MAX_SEARCH_SIZE = 4096;
        int searchedCount = 0;

        while (!queue.isEmpty()) {
            BlockPos currentPos = queue.poll();
            searchedCount++;
            BlockState currentState = level.getBlockState(currentPos);

            if (currentState.getValue(STABLE)) {
                level.setBlock(currentPos, currentState.setValue(STABLE, false), 2);
                level.scheduleTick(currentPos, this, initialFallTime);
            }

            if (searchedCount >= MAX_SEARCH_SIZE) break;

            for (Direction direction : Direction.values()) {
                BlockPos neighborPos = currentPos.relative(direction);
                if (!visited.contains(neighborPos)) {
                    BlockState neighborState = level.getBlockState(neighborPos);
                    // Check if it's our block, regardless of its current STABLE state
                    if (neighborState.getBlock() instanceof FallingTrapBlock) {
                        visited.add(neighborPos);
                        queue.add(neighborPos);
                    }
                }
            }
        }
    }

    @Override
    public void onLand(Level level, BlockPos pos, BlockState state, BlockState replaceableState, FallingBlockEntity fallingBlock) {
        super.onLand(level, pos, state, replaceableState, fallingBlock);
        if (!level.isClientSide() && state.getValue(GENERATED)) {
            level.destroyBlock(pos, false);
        }
    }

    @Override
    protected @Nullable MapCodec<? extends FallingBlock> codec() {
        return null;
    }

    // TODO - change the dust color. Get rid of the particle, something.
    @Override
    public int getDustColor(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return 0;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new FallingTrapBlockEntity(blockPos, blockState);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
