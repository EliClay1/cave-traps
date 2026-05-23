package com.displace.cavetraps.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.TwistingVinesPlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VineTrapVine extends Block {
    private static final VoxelShape SHAPE = Block.column(8.0F, 0.0F, 16.0F);
    public static final MapCodec<TwistingVinesPlantBlock> CODEC = simpleCodec(TwistingVinesPlantBlock::new);

    public MapCodec<TwistingVinesPlantBlock> codec() {
        return CODEC;
    }

    public VineTrapVine(Properties properties) {
        super(properties);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean intersects) {
        Vec3 vec3 = new Vec3(0.25F, 0.05F, 0.25F);

        // TODO - add tick % 10 = -.5 health. (check cacti logic)

        // TODO - make climbable - This is done within the data

        // TODO - add creeper passthrough logic
//        if (p_58183_ instanceof LivingEntity livingentity) {
//            if (livingentity.hasEffect(MobEffects.WEAVING)) {
//                vec3 = new Vec3((double)0.5F, (double)0.25F, (double)0.5F);
//            }
//        }
        entity.makeStuckInBlock(state, vec3);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
