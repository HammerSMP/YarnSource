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
import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;

public class GameModeCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder literalArgumentBuilder = (LiteralArgumentBuilder)CommandManager.literal("gamemode").requires(arg -> arg.hasPermissionLevel(2));
        for (GameMode lv : GameMode.values()) {
            if (lv == GameMode.NOT_SET) continue;
            literalArgumentBuilder.then(((LiteralArgumentBuilder)CommandManager.literal(lv.getName()).executes(commandContext -> GameModeCommand.execute((CommandContext<ServerCommandSource>)commandContext, Collections.singleton(((ServerCommandSource)commandContext.getSource()).getPlayer()), lv))).then(CommandManager.argument("target", EntityArgumentType.players()).executes(commandContext -> GameModeCommand.execute((CommandContext<ServerCommandSource>)commandContext, EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "target"), lv))));
        }
        dispatcher.register(literalArgumentBuilder);
    }

    private static void setGameMode(ServerCommandSource source, ServerPlayerEntity player, GameMode gameMode) {
        TranslatableText lv = new TranslatableText("gameMode." + gameMode.getName());
        if (source.getEntity() == player) {
            source.sendFeedback(new TranslatableText("commands.gamemode.success.self", lv), true);
        } else {
            if (source.getWorld().getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK)) {
                player.sendSystemMessage(new TranslatableText("gameMode.changed", lv), Util.NIL_UUID);
            }
            source.sendFeedback(new TranslatableText("commands.gamemode.success.other", player.getDisplayName(), lv), true);
        }
    }

    private static int execute(CommandContext<ServerCommandSource> context, Collection<ServerPlayerEntity> targets, GameMode gameMode) {
        int i = 0;
        for (ServerPlayerEntity lv : targets) {
            if (lv.interactionManager.getGameMode() == gameMode) continue;
            lv.setGameMode(gameMode);
            GameModeCommand.setGameMode((ServerCommandSource)context.getSource(), lv, gameMode);
            ++i;
        }
        return i;
    }
}

