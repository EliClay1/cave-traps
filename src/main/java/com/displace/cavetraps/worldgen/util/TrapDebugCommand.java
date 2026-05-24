package com.displace.cavetraps.worldgen.util;

import com.displace.cavetraps.CaveTraps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import static net.minecraft.server.permissions.PermissionLevel.ADMINS;

public class TrapDebugCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("trapgen")
                .requires(source -> source.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.ADMINS)))
                .then(Commands.argument("trap_type", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            builder.suggest("explosive_trap");
                            builder.suggest("vine_trap");
                            builder.suggest("falling_block_trap");
                            return builder.buildFuture();
                        })
                        .executes(TrapDebugCommand::execute)
                )
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        BlockPos pos = BlockPos.containing(source.getPosition());
        String trapType = StringArgumentType.getString(context, "trap_type");

        // Construct the resource key matching your configured feature JSON
        ResourceKey<ConfiguredFeature<?, ?>> featureKey = ResourceKey.create(
                Registries.CONFIGURED_FEATURE,
                Identifier.fromNamespaceAndPath(CaveTraps.MODID, trapType)
        );

        Registry<ConfiguredFeature<?, ?>> registry = level.registryAccess().lookupOrThrow(Registries.CONFIGURED_FEATURE);
        ConfiguredFeature<?, ?> feature = registry.getValue(featureKey);

        if (feature == null) {
            source.sendFailure(Component.literal("Could not find configured feature: " + trapType));
            return 0;
        }

        // Attempt to place the feature at the player's exact position
        boolean success = feature.place(level, level.getChunkSource().getGenerator(), level.getRandom(), pos);

        if (success) {
            source.sendSuccess(() -> Component.literal("Successfully placed " + trapType + " at " + pos.toShortString()), true);
        } else {
            source.sendFailure(Component.literal("Failed to place " + trapType + " at " + pos.toShortString() + " (Environment checks failed)"));
        }

        return success ? 1 : 0;
    }
}
