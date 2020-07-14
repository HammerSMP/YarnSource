/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.arguments.MessageArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.network.MessageType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;

public class SayCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("say").requires(arg -> arg.hasPermissionLevel(2))).then(CommandManager.argument("message", MessageArgumentType.message()).executes(commandContext -> {
            Text lv = MessageArgumentType.getMessage((CommandContext<ServerCommandSource>)commandContext, "message");
            TranslatableText lv2 = new TranslatableText("chat.type.announcement", ((ServerCommandSource)commandContext.getSource()).getDisplayName(), lv);
            Entity lv3 = ((ServerCommandSource)commandContext.getSource()).getEntity();
            if (lv3 != null) {
                ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager().broadcastChatMessage(lv2, MessageType.CHAT, lv3.getUuid());
            } else {
                ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager().broadcastChatMessage(lv2, MessageType.SYSTEM, Util.NIL_UUID);
            }
            return 1;
        })));
    }
}

