/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameMode;

public class DefaultGameModeCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder literalArgumentBuilder = (LiteralArgumentBuilder)CommandManager.literal("defaultgamemode").requires(arg -> arg.hasPermissionLevel(2));
        for (GameMode lv : GameMode.values()) {
            if (lv == GameMode.NOT_SET) continue;
            literalArgumentBuilder.then(CommandManager.literal(lv.getName()).executes(commandContext -> DefaultGameModeCommand.execute((ServerCommandSource)commandContext.getSource(), lv)));
        }
        dispatcher.register(literalArgumentBuilder);
    }

    private static int execute(ServerCommandSource source, GameMode defaultGameMode) {
        int i = 0;
        MinecraftServer minecraftServer = source.getMinecraftServer();
        minecraftServer.setDefaultGameMode(defaultGameMode);
        if (minecraftServer.shouldForceGameMode()) {
            for (ServerPlayerEntity lv : minecraftServer.getPlayerManager().getPlayerList()) {
                if (lv.interactionManager.getGameMode() == defaultGameMode) continue;
                lv.setGameMode(defaultGameMode);
                ++i;
            }
        }
        source.sendFeedback(new TranslatableText("commands.defaultgamemode.success", defaultGameMode.getTranslatableName()), true);
        return i;
    }
}

