/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.tree.CommandNode
 *  com.mojang.brigadier.tree.LiteralCommandNode
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.UUID;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.MessageArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

public class MessageCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode literalCommandNode = dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("msg").then(CommandManager.argument("targets", EntityArgumentType.players()).then(CommandManager.argument("message", MessageArgumentType.message()).executes(commandContext -> MessageCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), MessageArgumentType.getMessage((CommandContext<ServerCommandSource>)commandContext, "message"))))));
        dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("tell").redirect((CommandNode)literalCommandNode));
        dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("w").redirect((CommandNode)literalCommandNode));
    }

    private static int execute(ServerCommandSource source, Collection<ServerPlayerEntity> targets, Text message) {
        UUID uUID = source.getEntity() == null ? Util.NIL_UUID : source.getEntity().getUuid();
        for (ServerPlayerEntity lv : targets) {
            lv.sendSystemMessage(new TranslatableText("commands.message.display.incoming", source.getDisplayName(), message).formatted(Formatting.GRAY, Formatting.ITALIC), uUID);
            source.sendFeedback(new TranslatableText("commands.message.display.outgoing", lv.getDisplayName(), message).formatted(Formatting.GRAY, Formatting.ITALIC), false);
        }
        return targets.size();
    }
}

