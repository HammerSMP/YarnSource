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
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameRules;

public class GameRuleCommand {
    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        final LiteralArgumentBuilder literalArgumentBuilder = (LiteralArgumentBuilder)CommandManager.literal("gamerule").requires(arg -> arg.hasPermissionLevel(2));
        GameRules.forEachType(new GameRules.RuleTypeConsumer(){

            @Override
            public <T extends GameRules.Rule<T>> void accept(GameRules.RuleKey<T> arg, GameRules.RuleType<T> arg2) {
                literalArgumentBuilder.then(((LiteralArgumentBuilder)CommandManager.literal(arg.getName()).executes(commandContext -> GameRuleCommand.executeQuery((ServerCommandSource)commandContext.getSource(), arg))).then(arg2.argument("value").executes(commandContext -> GameRuleCommand.executeSet((CommandContext<ServerCommandSource>)commandContext, arg))));
            }
        });
        commandDispatcher.register(literalArgumentBuilder);
    }

    private static <T extends GameRules.Rule<T>> int executeSet(CommandContext<ServerCommandSource> commandContext, GameRules.RuleKey<T> arg) {
        ServerCommandSource lv = (ServerCommandSource)commandContext.getSource();
        T lv2 = lv.getMinecraftServer().getGameRules().get(arg);
        ((GameRules.Rule)lv2).set(commandContext, "value");
        lv.sendFeedback(new TranslatableText("commands.gamerule.set", arg.getName(), ((GameRules.Rule)lv2).toString()), true);
        return ((GameRules.Rule)lv2).getCommandResult();
    }

    private static <T extends GameRules.Rule<T>> int executeQuery(ServerCommandSource arg, GameRules.RuleKey<T> arg2) {
        T lv = arg.getMinecraftServer().getGameRules().get(arg2);
        arg.sendFeedback(new TranslatableText("commands.gamerule.query", arg2.getName(), ((GameRules.Rule)lv).toString()), false);
        return ((GameRules.Rule)lv).getCommandResult();
    }
}

