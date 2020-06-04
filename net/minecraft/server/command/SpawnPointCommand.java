/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class SpawnPointCommand {
    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("spawnpoint").requires(arg -> arg.hasPermissionLevel(2))).executes(commandContext -> SpawnPointCommand.execute((ServerCommandSource)commandContext.getSource(), Collections.singleton(((ServerCommandSource)commandContext.getSource()).getPlayer()), new BlockPos(((ServerCommandSource)commandContext.getSource()).getPosition())))).then(((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.players()).executes(commandContext -> SpawnPointCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), new BlockPos(((ServerCommandSource)commandContext.getSource()).getPosition())))).then(CommandManager.argument("pos", BlockPosArgumentType.blockPos()).executes(commandContext -> SpawnPointCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), BlockPosArgumentType.getBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"))))));
    }

    private static int execute(ServerCommandSource arg, Collection<ServerPlayerEntity> collection, BlockPos arg2) {
        RegistryKey<World> lv = arg.getWorld().getRegistryKey();
        for (ServerPlayerEntity lv2 : collection) {
            lv2.setSpawnPoint(lv, arg2, true, false);
        }
        String string = lv.getValue().toString();
        if (collection.size() == 1) {
            arg.sendFeedback(new TranslatableText("commands.spawnpoint.success.single", arg2.getX(), arg2.getY(), arg2.getZ(), string, collection.iterator().next().getDisplayName()), true);
        } else {
            arg.sendFeedback(new TranslatableText("commands.spawnpoint.success.multiple", arg2.getX(), arg2.getY(), arg2.getZ(), string, collection.size()), true);
        }
        return collection.size();
    }
}

