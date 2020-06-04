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
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;

public class GameModeCommand {
    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        LiteralArgumentBuilder literalArgumentBuilder = (LiteralArgumentBuilder)CommandManager.literal("gamemode").requires(arg -> arg.hasPermissionLevel(2));
        for (GameMode lv : GameMode.values()) {
            if (lv == GameMode.NOT_SET) continue;
            literalArgumentBuilder.then(((LiteralArgumentBuilder)CommandManager.literal(lv.getName()).executes(commandContext -> GameModeCommand.execute((CommandContext<ServerCommandSource>)commandContext, Collections.singleton(((ServerCommandSource)commandContext.getSource()).getPlayer()), lv))).then(CommandManager.argument("target", EntityArgumentType.players()).executes(commandContext -> GameModeCommand.execute((CommandContext<ServerCommandSource>)commandContext, EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "target"), lv))));
        }
        commandDispatcher.register(literalArgumentBuilder);
    }

    private static void setGameMode(ServerCommandSource arg, ServerPlayerEntity arg2, GameMode arg3) {
        TranslatableText lv = new TranslatableText("gameMode." + arg3.getName());
        if (arg.getEntity() == arg2) {
            arg.sendFeedback(new TranslatableText("commands.gamemode.success.self", lv), true);
        } else {
            if (arg.getWorld().getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK)) {
                arg2.sendSystemMessage(new TranslatableText("gameMode.changed", lv), Util.NIL_UUID);
            }
            arg.sendFeedback(new TranslatableText("commands.gamemode.success.other", arg2.getDisplayName(), lv), true);
        }
    }

    private static int execute(CommandContext<ServerCommandSource> commandContext, Collection<ServerPlayerEntity> collection, GameMode arg) {
        int i = 0;
        for (ServerPlayerEntity lv : collection) {
            if (lv.interactionManager.getGameMode() == arg) continue;
            lv.setGameMode(arg);
            GameModeCommand.setGameMode((ServerCommandSource)commandContext.getSource(), lv, arg);
            ++i;
        }
        return i;
    }
}

