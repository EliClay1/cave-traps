package com.displace.cavetraps.block;

import com.displace.cavetraps.blockentities.ExplosiveTrapBlockEntity;
import com.displace.cavetraps.blockentities.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class ExplosiveTrapBlock extends Block implements EntityBlock {
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final Property<Boolean> TRIGGERED = BooleanProperty.create("triggered");
    // The base wooden box: spans the entire 16x16 block area up to a full block height (Y=16)
    private static final VoxelShape BASE_SHAPE = Block.box(0, 0, 0, 16, 16, 16);

    // The plunger (untriggered): narrow, sticks up an extra half-block (Y=16 to Y=24)
    private static final VoxelShape PLUNGER_UP = Block.box(5, 16, 5, 11, 24, 11);

    // The plunger (triggered): pushed down closer to the base (Y=16 to Y=18)
    private static final VoxelShape PLUNGER_DOWN = Block.box(5, 16, 5, 11, 18, 11);

    // Combine them using Shapes.or()
    private static final VoxelShape SHAPE_UNTRIGGERED = Shapes.or(BASE_SHAPE, PLUNGER_UP);
    private static final VoxelShape SHAPE_TRIGGERED = Shapes.or(BASE_SHAPE, PLUNGER_DOWN);

    public ExplosiveTrapBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(TRIGGERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, TRIGGERED);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(TRIGGERED) ? SHAPE_TRIGGERED : SHAPE_UNTRIGGERED;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(TRIGGERED) ? SHAPE_TRIGGERED : SHAPE_UNTRIGGERED;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return ModBlockEntities.EXPLOSIVE_TRAP_BLOCK_ENTITY.get().create(blockPos, blockState);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    @SuppressWarnings({"deprecation"})
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            activate(level, pos, state);
        }
        return InteractionResult.SUCCESS;
//        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean intersects) {
        if (level.isClientSide()) return;
        if (state.getValue(TRIGGERED)) return;
        if (!(entity instanceof Player player)) return;
        activate(level, pos, state);

        // Player coordinates
        double pX = player.getX();
        double pZ = player.getZ();
        double feetY = player.getY();

        // 1. Is the player roughly above the center of the block (the plunger)?
        // Plunger is roughly from 5/16 to 11/16 across the block.
        double minX = pos.getX() + (5.0 / 16.0);
        double maxX = pos.getX() + (11.0 / 16.0);
        double minZ = pos.getZ() + (5.0 / 16.0);
        double maxZ = pos.getZ() + (11.0 / 16.0);

        if (pX >= minX && pX <= maxX && pZ >= minZ && pZ <= maxZ) {

            // 2. Are they standing ON the plunger?
            // Plunger top is at Y=15 (which is pos.getY() + 15/16)
            double plungerTopY = pos.getY() + (15.0 / 16.0);

            // Give a generous vertical window just above the plunger
            if (feetY >= plungerTopY - 0.1 && feetY <= plungerTopY + 0.5) {
                activate(level, pos, state);
            }
        }

        super.entityInside(state, level, pos, entity, applier, intersects);
    }

    private void activate(Level level, BlockPos pos, BlockState state) {
        if (state.getValue(TRIGGERED)) {
            return;
        }
        level.setBlock(pos, state.setValue(TRIGGERED, true), 3);
        if (level.getBlockEntity(pos) instanceof ExplosiveTrapBlockEntity trap) {
            trap.activate();
        }
        level.scheduleTick(pos, this, 35);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.getValue(TRIGGERED)) {
            return;
        }
        level.removeBlock(pos, false);
        level.explode(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 4.0F, Level.ExplosionInteraction.BLOCK);
        super.tick(state, level, pos, random);
    }
}
