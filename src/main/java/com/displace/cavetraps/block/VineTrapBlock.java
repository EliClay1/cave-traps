package com.displace.cavetraps.block;

import com.displace.cavetraps.CaveTraps;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class VineTrapBlock extends Block {
    public static final Property<Boolean> ACTIVATED = BooleanProperty.create("activated");

    public VineTrapBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVATED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVATED);
    }

    // TODO - player detection logic
    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        // begins ticking the block.
        if (!level.isClientSide()) level.scheduleTick(pos, this, 10);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.isClientSide()) return;

        double radius = 2.5;
        AABB detectionBox = new AABB(pos).inflate(radius);
        List<Player> players = level.getEntitiesOfClass(Player.class, detectionBox);
        if (!players.isEmpty() && !state.getValue(ACTIVATED)) activateTrap(state, level, pos, random);

        // auto ticks every half second (10 ticks)
        level.scheduleTick(pos, this, 10);
    }

    // TODO - Create vine blocks above
    private void activateTrap(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // TODO - update the state to active. Still not working for some reason
        level.setBlockAndUpdate(pos, state.setValue(ACTIVATED, true));

        for (int i = 1; i <= random.nextIntBetweenInclusive(2, 4); ++i) {
            BlockPos abovePosition =  pos.above(i);
            BlockState vine = ModBlocks.VINE_TRAP_VINE_HEAD.get().defaultBlockState();
            level.setBlock(abovePosition, vine, 3);
        }
    }




}
