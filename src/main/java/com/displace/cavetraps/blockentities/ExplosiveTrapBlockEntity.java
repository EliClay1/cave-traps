package com.displace.cavetraps.blockentities;

import com.displace.cavetraps.block.ExplosiveTrapBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ExplosiveTrapBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");

    private static final RawAnimation ACTIVATED = RawAnimation.begin().thenPlayXTimes("activated", 1).thenLoop("armed");

    public ExplosiveTrapBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.EXPLOSIVE_TRAP_BLOCK_ENTITY.get(), pos, blockState);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(state -> {
            boolean isTriggered = false;

            // Check the current block state to see if the block has been triggered
            if (this.getBlockState().hasProperty(ExplosiveTrapBlock.TRIGGERED)) {
                isTriggered = this.getBlockState().getValue(ExplosiveTrapBlock.TRIGGERED);
            }

            // Play the activated animation if triggered, otherwise sit idle
            return state.setAndContinue(isTriggered ? ACTIVATED : IDLE);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    public void activate() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

}
