package com.displace.cavetraps.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VineTrapVineHead extends GrowingPlantHeadBlock {
    private static final VoxelShape SHAPE = Block.column(8.0F, 0.0F, 16.0F);
    public static final MapCodec<VineTrapVineHead> CODEC = simpleCodec(VineTrapVineHead::new);

    public VineTrapVineHead(Properties properties) {
        super(properties, Direction.UP, SHAPE, false, 0.0);
    }

    public MapCodec<VineTrapVineHead> codec() {
        return CODEC;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean intersects) {
        Vec3 vec3 = getInsideEntityActions(level, entity);
        entity.makeStuckInBlock(state, vec3);
    }

    @Override
    protected Block getBodyBlock() {
        return ModBlocks.VINE_TRAP_VINE.get();
    }

    // required for override.
    @Override
    protected int getBlocksToGrowWhenBonemealed(RandomSource randomSource) {
        return 0;
    }

    @Override
    protected boolean canGrowInto(BlockState blockState) {
        return false;
    }

    public static Vec3 getInsideEntityActions(Level level, Entity entity) {
        if (level instanceof ServerLevel serverLevel && !(entity instanceof Creeper)) {
            entity.hurtServer(serverLevel, level.damageSources().cactus(), 1.0F);
        }

        Vec3 vec3 = new Vec3(0.25F, 0.05F, 0.25F);
        if (entity instanceof LivingEntity livingEntity) {
            if (livingEntity instanceof Creeper) {
                vec3 = new Vec3(1.5F, 1.5F, 1.5F);
            }
        }
        return vec3;
    }
}
