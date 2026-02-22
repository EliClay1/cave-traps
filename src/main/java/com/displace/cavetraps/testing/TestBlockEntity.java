package com.displace.cavetraps.testing;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class TestBlockEntity extends BlockEntity {
    private ItemStack stack = ItemStack.EMPTY;
    private static float angle;

    public TestBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.TEST_BLOCK_ENTITY.get(), pos, blockState);
        this.stack = new ItemStack(Items.DIAMOND_PICKAXE);
    }

    public float getAngle() {
        return angle;
    }

    public ItemStack getStack() {
        return stack;
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
        setChanged();
    }

    // this seems to be where we load additional state values. In this case we are loading this private value int above.
    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
    }

    // and this looks like it adds the option to save additional values. So there isn't a limit to how many
    // things I can save. I think.
    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
    }

    // a place for values to be reset when the block gets removed. This doesn't seem like it would be used on something
    // like a shulker box because those maintain their state data even when removed or broken.
    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);
    }

    // logic for the block ticking should be happening here. It isn't required, but it's good practice.
    public static void tick(Level level, BlockPos pos, BlockState state, TestBlockEntity blockEntity) {
        angle += 2.0F; // degrees per tick
        if (angle >= 360.0F) angle -= 360.0F;
    }


    // Syncing functionality to ensure the client understands what the server is seeing.

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public void handleUpdateTag(ValueInput input) {
        super.handleUpdateTag(input);
    }

    // block sync happens with block updates.

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    // you can also run custom logic when the block updates. It can be done within this function.
    @Override
    public void onDataPacket(Connection net, ValueInput valueInput) {
        super.onDataPacket(net, valueInput);
    }
}
