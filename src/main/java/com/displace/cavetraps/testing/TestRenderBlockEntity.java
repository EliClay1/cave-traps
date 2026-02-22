package com.displace.cavetraps.testing;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class TestRenderBlockEntity extends BlockEntity {
    private int value;

    public TestRenderBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.TEST_RENDER_BE.get(), pos, blockState);
    }

    // this seems to be where we load additional state values. In this case we are loading this private value int above.
    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.value = input.getIntOr("value", 0);
    }

    // and this looks like it adds the option to save additional values. So there isn't a limit to how many
    // things I can save. I think.
    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("value", this.value);
    }

    // a place for values to be reset when the block gets removed. This doesn't seem like it would be used on something
    // like a shulker box because those maintain their state data even when removed or broken.
    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);
        this.value = 0;
    }

    // logic for the block ticking should be happening here. It isn't required, but it's good practice.
    public static void tick(Level level, BlockPos pos, BlockState state, TestRenderBlockEntity blockEntity) {

    }
}
