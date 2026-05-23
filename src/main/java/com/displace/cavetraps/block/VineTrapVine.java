package com.displace.cavetraps.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrowingPlantBodyBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import static com.displace.cavetraps.block.VineTrapVineHead.getInsideVineActions;

public class VineTrapVine extends GrowingPlantBodyBlock {
    private static final VoxelShape SHAPE = Block.column(8.0F, 0.0F, 16.0F);
    public static final MapCodec<VineTrapVine> CODEC = simpleCodec(VineTrapVine::new);

    public MapCodec<VineTrapVine> codec() {
        return CODEC;
    }

    public VineTrapVine(Properties properties) {
        super(properties , Direction.UP, SHAPE, false);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean intersects) {
        Vec3 vec3 = getInsideVineActions(level, entity);
        entity.makeStuckInBlock(state, vec3);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected GrowingPlantHeadBlock getHeadBlock() {
        return ModBlocks.VINE_TRAP_VINE_HEAD.get();
    }
}
