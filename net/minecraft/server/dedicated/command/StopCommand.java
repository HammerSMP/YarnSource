/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 */
package net.minecraft.server.dedicated.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

public class StopCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("stop").requires(arg -> arg.hasPermissionLevel(4))).executes(commandContext -> {
            ((ServerCommandSource)commandContext.getSource()).sendFeedback(new TranslatableText("commands.stop.stopping"), true);
            ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().stop(false);
            return 1;
        }));
    }
}

