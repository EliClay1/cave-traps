package com.displace.cavetraps.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ExplosiveTrapPlungerBlock extends Block {



    private static final VoxelShape PLUNGER_UP_COLLISION = Block.box(5, 0, 5, 11, 7.9, 11);
    private static final VoxelShape PLUNGER_UP_OUTLINE = Block.box(5, 0, 5, 11, 8, 11);
    private static final VoxelShape PLUNGER_DOWN_SHAPE = Block.box(5, 0, 5, 11, 2, 11);

    public ExplosiveTrapPlungerBlock(Properties properties) {
        super(properties);
    }

    private boolean isTrapTriggered(BlockGetter level, BlockPos pos) {
        BlockState stateBelow = level.getBlockState(pos.below());
        if (stateBelow.getBlock() instanceof ExplosiveTrapBlock) {
            return stateBelow.getValue(ExplosiveTrapBlock.TRIGGERED);
        }
        return false;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return isTrapTriggered(level, pos) ? PLUNGER_DOWN_SHAPE : PLUNGER_UP_OUTLINE;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return isTrapTriggered(level, pos) ? PLUNGER_DOWN_SHAPE : PLUNGER_UP_COLLISION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        BlockState stateBelow = level.getBlockState(pos.below());
        if (!level.isClientSide() && stateBelow.getBlock() instanceof ExplosiveTrapBlock trap) {
            trap.activate(level, pos, state);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean intersects) {
        if (level.isClientSide() || isTrapTriggered(level, pos) || !(entity instanceof Player player)) return;

        double minX = pos.getX() + (5.0 / 16.0);
        double maxX = pos.getX() + (11.0 / 16.0);
        double minZ = pos.getZ() + (5.0 / 16.0);
        double maxZ = pos.getZ() + (11.0 / 16.0);
        double minY = pos.getY() + 0.0;
        double maxY = pos.getY() + (8.0 / 16.0) + 0.1;

        AABB exactPlungerHitbox = new net.minecraft.world.phys.AABB(minX, minY, minZ, maxX, maxY, maxZ);

        if (player.getBoundingBox().intersects(exactPlungerHitbox)) {
            BlockPos basePos = pos.below();
            BlockState baseState = level.getBlockState(basePos);
            if (baseState.getBlock() instanceof ExplosiveTrapBlock trap) {
                trap.activate(level, basePos, baseState);
            }
        }
        super.entityInside(state, level, pos, entity, applier, intersects);
    }


    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState stateBelow =  level.getBlockState(pos.below());
        return stateBelow.is(ModBlocks.EXPLOSIVE_TRAP_BLOCK.get());
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        BlockPos basePos =  pos.below();
        BlockState baseState = level.getBlockState(basePos);
        if (baseState.getBlock() instanceof ExplosiveTrapBlock) {
            level.destroyBlock(basePos, false);
        }
        return super.playerWillDestroy(level, pos, state, player);
    }
}
