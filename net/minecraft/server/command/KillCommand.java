/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 */
package net.minecraft.server.command;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

public class KillCommand {
    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("kill").requires(arg -> arg.hasPermissionLevel(2))).executes(commandContext -> KillCommand.execute((ServerCommandSource)commandContext.getSource(), (Collection<? extends Entity>)ImmutableList.of((Object)((ServerCommandSource)commandContext.getSource()).getEntityOrThrow())))).then(CommandManager.argument("targets", EntityArgumentType.entities()).executes(commandContext -> KillCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets")))));
    }

    private static int execute(ServerCommandSource arg, Collection<? extends Entity> collection) {
        for (Entity entity : collection) {
            entity.kill();
        }
        if (collection.size() == 1) {
            arg.sendFeedback(new TranslatableText("commands.kill.success.single", collection.iterator().next().getDisplayName()), true);
        } else {
            arg.sendFeedback(new TranslatableText("commands.kill.success.multiple", collection.size()), true);
        }
        return collection.size();
    }
}

