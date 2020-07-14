/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 */
package net.minecraft.server.dedicated.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.command.arguments.GameProfileArgumentType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

public class OpCommand {
    private static final SimpleCommandExceptionType ALREADY_OPPED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.op.failed"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("op").requires(arg -> arg.hasPermissionLevel(3))).then(CommandManager.argument("targets", GameProfileArgumentType.gameProfile()).suggests((commandContext, suggestionsBuilder) -> {
            PlayerManager lv = ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager();
            return CommandSource.suggestMatching(lv.getPlayerList().stream().filter(arg2 -> !lv.isOperator(arg2.getGameProfile())).map(arg -> arg.getGameProfile().getName()), suggestionsBuilder);
        }).executes(commandContext -> OpCommand.op((ServerCommandSource)commandContext.getSource(), GameProfileArgumentType.getProfileArgument((CommandContext<ServerCommandSource>)commandContext, "targets")))));
    }

    private static int op(ServerCommandSource source, Collection<GameProfile> targets) throws CommandSyntaxException {
        PlayerManager lv = source.getMinecraftServer().getPlayerManager();
        int i = 0;
        for (GameProfile gameProfile : targets) {
            if (lv.isOperator(gameProfile)) continue;
            lv.addToOperators(gameProfile);
            ++i;
            source.sendFeedback(new TranslatableText("commands.op.success", targets.iterator().next().getName()), true);
        }
        if (i == 0) {
            throw ALREADY_OPPED_EXCEPTION.create();
        }
        return i;
    }
}

