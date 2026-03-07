package com.displace.cavetraps.blockentities;

import com.displace.cavetraps.block.FallingTrapBlock;
import com.displace.cavetraps.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class FallingTrapBlockEntity extends BlockEntity {
    // this is still a test of sorts, so I am going to document the process with comments.

    // This is defining the default block state so that the block will always render some kind of default.
    // Realistically, this should be its own block state, but there isn't a texture yet.

    // defaults to this, but that means that if it's air, it will default to air when "camo-ed". This may
    //   be easy to fix if we force the camo state to be disabled when it lands.
    private BlockState camoState = Blocks.AIR.defaultBlockState();

    public FallingTrapBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.FALLING_TRAP_BLOCK_ENTITY.get(), pos, blockState);
    }

    // These are setters and getter for the camo state so that other classes can access it.
    public BlockState getCamoState() {
        return this.camoState;
    }

    public void setCamoState(BlockState camo) {
        this.camoState = camo;
        if (level != null) {
            boolean hasCamo = camo != null && !camo.isAir();
            level.setBlock(getBlockPos(),
                    getBlockState().setValue(FallingTrapBlock.HAS_CAMO, hasCamo),
                    Block.UPDATE_CLIENTS);
        }
        setChanged();
    }

    // this saves the block state data so it can be references by other classes.
    // It uses a CODEC because this is doing stuff on the network level.
    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("camo_state", BlockState.CODEC, this.camoState);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.read("camo_state", BlockState.CODEC).ifPresent(blockState -> this.camoState = blockState);
    }

    // Next up is network syncing. This is especially important because the server can see and process everything,
    // but the client doesn't know how to do that without these functions.
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithFullMetadata(registries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
