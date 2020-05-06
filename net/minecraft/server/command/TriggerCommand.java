/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 */
package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.arguments.ObjectiveArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

public class TriggerCommand {
    private static final SimpleCommandExceptionType FAILED_UMPRIMED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.trigger.failed.unprimed"));
    private static final SimpleCommandExceptionType FAILED_INVALID_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.trigger.failed.invalid"));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)CommandManager.literal("trigger").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("objective", ObjectiveArgumentType.objective()).suggests((commandContext, suggestionsBuilder) -> TriggerCommand.suggestObjectives((ServerCommandSource)commandContext.getSource(), suggestionsBuilder)).executes(commandContext -> TriggerCommand.executeSimple((ServerCommandSource)commandContext.getSource(), TriggerCommand.getScore(((ServerCommandSource)commandContext.getSource()).getPlayer(), ObjectiveArgumentType.getObjective((CommandContext<ServerCommandSource>)commandContext, "objective"))))).then(CommandManager.literal("add").then(CommandManager.argument("value", IntegerArgumentType.integer()).executes(commandContext -> TriggerCommand.executeAdd((ServerCommandSource)commandContext.getSource(), TriggerCommand.getScore(((ServerCommandSource)commandContext.getSource()).getPlayer(), ObjectiveArgumentType.getObjective((CommandContext<ServerCommandSource>)commandContext, "objective")), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"value")))))).then(CommandManager.literal("set").then(CommandManager.argument("value", IntegerArgumentType.integer()).executes(commandContext -> TriggerCommand.executeSet((ServerCommandSource)commandContext.getSource(), TriggerCommand.getScore(((ServerCommandSource)commandContext.getSource()).getPlayer(), ObjectiveArgumentType.getObjective((CommandContext<ServerCommandSource>)commandContext, "objective")), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"value")))))));
    }

    public static CompletableFuture<Suggestions> suggestObjectives(ServerCommandSource arg, SuggestionsBuilder suggestionsBuilder) {
        Entity lv = arg.getEntity();
        ArrayList list = Lists.newArrayList();
        if (lv != null) {
            ServerScoreboard lv2 = arg.getMinecraftServer().getScoreboard();
            String string = lv.getEntityName();
            for (ScoreboardObjective lv3 : lv2.getObjectives()) {
                ScoreboardPlayerScore lv4;
                if (lv3.getCriterion() != ScoreboardCriterion.TRIGGER || !lv2.playerHasObjective(string, lv3) || (lv4 = lv2.getPlayerScore(string, lv3)).isLocked()) continue;
                list.add(lv3.getName());
            }
        }
        return CommandSource.suggestMatching(list, suggestionsBuilder);
    }

    private static int executeAdd(ServerCommandSource arg, ScoreboardPlayerScore arg2, int i) {
        arg2.incrementScore(i);
        arg.sendFeedback(new TranslatableText("commands.trigger.add.success", arg2.getObjective().toHoverableText(), i), true);
        return arg2.getScore();
    }

    private static int executeSet(ServerCommandSource arg, ScoreboardPlayerScore arg2, int i) {
        arg2.setScore(i);
        arg.sendFeedback(new TranslatableText("commands.trigger.set.success", arg2.getObjective().toHoverableText(), i), true);
        return i;
    }

    private static int executeSimple(ServerCommandSource arg, ScoreboardPlayerScore arg2) {
        arg2.incrementScore(1);
        arg.sendFeedback(new TranslatableText("commands.trigger.simple.success", arg2.getObjective().toHoverableText()), true);
        return arg2.getScore();
    }

    private static ScoreboardPlayerScore getScore(ServerPlayerEntity arg, ScoreboardObjective arg2) throws CommandSyntaxException {
        String string;
        if (arg2.getCriterion() != ScoreboardCriterion.TRIGGER) {
            throw FAILED_INVALID_EXCEPTION.create();
        }
        Scoreboard lv = arg.getScoreboard();
        if (!lv.playerHasObjective(string = arg.getEntityName(), arg2)) {
            throw FAILED_UMPRIMED_EXCEPTION.create();
        }
        ScoreboardPlayerScore lv2 = lv.getPlayerScore(string, arg2);
        if (lv2.isLocked()) {
            throw FAILED_UMPRIMED_EXCEPTION.create();
        }
        lv2.setLocked(true);
        return lv2;
    }
}

