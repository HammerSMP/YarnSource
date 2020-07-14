/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 */
package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.arguments.ObjectiveArgumentType;
import net.minecraft.command.arguments.ObjectiveCriteriaArgumentType;
import net.minecraft.command.arguments.OperationArgumentType;
import net.minecraft.command.arguments.ScoreHolderArgumentType;
import net.minecraft.command.arguments.ScoreboardSlotArgumentType;
import net.minecraft.command.arguments.TextArgumentType;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;

public class ScoreboardCommand {
    private static final SimpleCommandExceptionType OBJECTIVES_ADD_DUPLICATE_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.scoreboard.objectives.add.duplicate"));
    private static final SimpleCommandExceptionType OBJECTIVES_DISPLAY_ALREADY_EMPTY_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.scoreboard.objectives.display.alreadyEmpty"));
    private static final SimpleCommandExceptionType OBJECTIVES_DISPLAY_ALREADY_SET_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.scoreboard.objectives.display.alreadySet"));
    private static final SimpleCommandExceptionType PLAYERS_ENABLE_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.scoreboard.players.enable.failed"));
    private static final SimpleCommandExceptionType PLAYERS_ENABLE_INVALID_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.scoreboard.players.enable.invalid"));
    private static final Dynamic2CommandExceptionType PLAYERS_GET_NULL_EXCEPTION = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableText("commands.scoreboard.players.get.null", object, object2));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("scoreboard").requires(arg -> arg.hasPermissionLevel(2))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("objectives").then(CommandManager.literal("list").executes(commandContext -> ScoreboardCommand.executeListObjectives((ServerCommandSource)commandContext.getSource())))).then(CommandManager.literal("add").then(CommandManager.argument("objective", StringArgumentType.word()).then(((RequiredArgumentBuilder)CommandManager.argument("criteria", ObjectiveCriteriaArgumentType.objectiveCriteria()).executes(commandContext -> ScoreboardCommand.executeAddObjective((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString((CommandContext)commandContext, (String)"objective"), ObjectiveCriteriaArgumentType.getCriteria((CommandContext<ServerCommandSource>)commandContext, "criteria"), new LiteralText(StringArgumentType.getString((CommandContext)commandContext, (String)"objective"))))).then(CommandManager.argument("displayName", TextArgumentType.text()).executes(commandContext -> ScoreboardCommand.executeAddObjective((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString((CommandContext)commandContext, (String)"objective"), ObjectiveCriteriaArgumentType.getCriteria((CommandContext<ServerCommandSource>)commandContext, "criteria"), TextArgumentType.getTextArgument((CommandContext<ServerCommandSource>)commandContext, "displayName")))))))).then(CommandManager.literal("modify").then(((RequiredArgumentBuilder)CommandManager.argument("objective", ObjectiveArgumentType.objective()).then(CommandManager.literal("displayname").then(CommandManager.argument("displayName", TextArgumentType.text()).executes(commandContext -> ScoreboardCommand.executeModifyObjective((ServerCommandSource)commandContext.getSource(), ObjectiveArgumentType.getObjective((CommandContext<ServerCommandSource>)commandContext, "objective"), TextArgumentType.getTextArgument((CommandContext<ServerCommandSource>)commandContext, "displayName")))))).then(ScoreboardCommand.makeRenderTypeArguments())))).then(CommandManager.literal("remove").then(CommandManager.argument("objective", ObjectiveArgumentType.objective()).executes(commandContext -> ScoreboardCommand.executeRemoveObjective((ServerCommandSource)commandContext.getSource(), ObjectiveArgumentType.getObjective((CommandContext<ServerCommandSource>)commandContext, "objective")))))).then(CommandManager.literal("setdisplay").then(((RequiredArgumentBuilder)CommandManager.argument("slot", ScoreboardSlotArgumentType.scoreboardSlot()).executes(commandContext -> ScoreboardCommand.executeClearDisplay((ServerCommandSource)commandContext.getSource(), ScoreboardSlotArgumentType.getScoreboardSlot((CommandContext<ServerCommandSource>)commandContext, "slot")))).then(CommandManager.argument("objective", ObjectiveArgumentType.objective()).executes(commandContext -> ScoreboardCommand.executeSetDisplay((ServerCommandSource)commandContext.getSource(), ScoreboardSlotArgumentType.getScoreboardSlot((CommandContext<ServerCommandSource>)commandContext, "slot"), ObjectiveArgumentType.getObjective((CommandContext<ServerCommandSource>)commandContext, "objective")))))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("players").then(((LiteralArgumentBuilder)CommandManager.literal("list").executes(commandContext -> ScoreboardCommand.executeListPlayers((ServerCommandSource)commandContext.getSource()))).then(CommandManager.argument("target", ScoreHolderArgumentType.scoreHolder()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).executes(commandContext -> ScoreboardCommand.executeListScores((ServerCommandSource)commandContext.getSource(), ScoreHolderArgumentType.getScoreHolder((CommandContext<ServerCommandSource>)commandContext, "target")))))).then(CommandManager.literal("set").then(CommandManager.argument("targets", ScoreHolderArgumentType.scoreHolders()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(CommandManager.argument("objective", ObjectiveArgumentType.objective()).then(CommandManager.argument("score", IntegerArgumentType.integer()).executes(commandContext -> ScoreboardCommand.executeSet((ServerCommandSource)commandContext.getSource(), ScoreHolderArgumentType.getScoreboardScoreHolders((CommandContext<ServerCommandSource>)commandContext, "targets"), ObjectiveArgumentType.getWritableObjective((CommandContext<ServerCommandSource>)commandContext, "objective"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"score")))))))).then(CommandManager.literal("get").then(CommandManager.argument("target", ScoreHolderArgumentType.scoreHolder()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(CommandManager.argument("objective", ObjectiveArgumentType.objective()).executes(commandContext -> ScoreboardCommand.executeGet((ServerCommandSource)commandContext.getSource(), ScoreHolderArgumentType.getScoreHolder((CommandContext<ServerCommandSource>)commandContext, "target"), ObjectiveArgumentType.getObjective((CommandContext<ServerCommandSource>)commandContext, "objective"))))))).then(CommandManager.literal("add").then(CommandManager.argument("targets", ScoreHolderArgumentType.scoreHolders()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(CommandManager.argument("objective", ObjectiveArgumentType.objective()).then(CommandManager.argument("score", IntegerArgumentType.integer((int)0)).executes(commandContext -> ScoreboardCommand.executeAdd((ServerCommandSource)commandContext.getSource(), ScoreHolderArgumentType.getScoreboardScoreHolders((CommandContext<ServerCommandSource>)commandContext, "targets"), ObjectiveArgumentType.getWritableObjective((CommandContext<ServerCommandSource>)commandContext, "objective"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"score")))))))).then(CommandManager.literal("remove").then(CommandManager.argument("targets", ScoreHolderArgumentType.scoreHolders()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(CommandManager.argument("objective", ObjectiveArgumentType.objective()).then(CommandManager.argument("score", IntegerArgumentType.integer((int)0)).executes(commandContext -> ScoreboardCommand.executeRemove((ServerCommandSource)commandContext.getSource(), ScoreHolderArgumentType.getScoreboardScoreHolders((CommandContext<ServerCommandSource>)commandContext, "targets"), ObjectiveArgumentType.getWritableObjective((CommandContext<ServerCommandSource>)commandContext, "objective"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"score")))))))).then(CommandManager.literal("reset").then(((RequiredArgumentBuilder)CommandManager.argument("targets", ScoreHolderArgumentType.scoreHolders()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).executes(commandContext -> ScoreboardCommand.executeReset((ServerCommandSource)commandContext.getSource(), ScoreHolderArgumentType.getScoreboardScoreHolders((CommandContext<ServerCommandSource>)commandContext, "targets")))).then(CommandManager.argument("objective", ObjectiveArgumentType.objective()).executes(commandContext -> ScoreboardCommand.executeReset((ServerCommandSource)commandContext.getSource(), ScoreHolderArgumentType.getScoreboardScoreHolders((CommandContext<ServerCommandSource>)commandContext, "targets"), ObjectiveArgumentType.getObjective((CommandContext<ServerCommandSource>)commandContext, "objective"))))))).then(CommandManager.literal("enable").then(CommandManager.argument("targets", ScoreHolderArgumentType.scoreHolders()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(CommandManager.argument("objective", ObjectiveArgumentType.objective()).suggests((commandContext, suggestionsBuilder) -> ScoreboardCommand.suggestDisabled((ServerCommandSource)commandContext.getSource(), ScoreHolderArgumentType.getScoreboardScoreHolders((CommandContext<ServerCommandSource>)commandContext, "targets"), suggestionsBuilder)).executes(commandContext -> ScoreboardCommand.executeEnable((ServerCommandSource)commandContext.getSource(), ScoreHolderArgumentType.getScoreboardScoreHolders((CommandContext<ServerCommandSource>)commandContext, "targets"), ObjectiveArgumentType.getObjective((CommandContext<ServerCommandSource>)commandContext, "objective"))))))).then(CommandManager.literal("operation").then(CommandManager.argument("targets", ScoreHolderArgumentType.scoreHolders()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(CommandManager.argument("targetObjective", ObjectiveArgumentType.objective()).then(CommandManager.argument("operation", OperationArgumentType.operation()).then(CommandManager.argument("source", ScoreHolderArgumentType.scoreHolders()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(CommandManager.argument("sourceObjective", ObjectiveArgumentType.objective()).executes(commandContext -> ScoreboardCommand.executeOperation((ServerCommandSource)commandContext.getSource(), ScoreHolderArgumentType.getScoreboardScoreHolders((CommandContext<ServerCommandSource>)commandContext, "targets"), ObjectiveArgumentType.getWritableObjective((CommandContext<ServerCommandSource>)commandContext, "targetObjective"), OperationArgumentType.getOperation((CommandContext<ServerCommandSource>)commandContext, "operation"), ScoreHolderArgumentType.getScoreboardScoreHolders((CommandContext<ServerCommandSource>)commandContext, "source"), ObjectiveArgumentType.getObjective((CommandContext<ServerCommandSource>)commandContext, "sourceObjective")))))))))));
    }

    private static LiteralArgumentBuilder<ServerCommandSource> makeRenderTypeArguments() {
        LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = CommandManager.literal("rendertype");
        for (ScoreboardCriterion.RenderType lv : ScoreboardCriterion.RenderType.values()) {
            literalArgumentBuilder.then(CommandManager.literal(lv.getName()).executes(commandContext -> ScoreboardCommand.executeModifyRenderType((ServerCommandSource)commandContext.getSource(), ObjectiveArgumentType.getObjective((CommandContext<ServerCommandSource>)commandContext, "objective"), lv)));
        }
        return literalArgumentBuilder;
    }

    private static CompletableFuture<Suggestions> suggestDisabled(ServerCommandSource source, Collection<String> targets, SuggestionsBuilder builder) {
        ArrayList list = Lists.newArrayList();
        ServerScoreboard lv = source.getMinecraftServer().getScoreboard();
        for (ScoreboardObjective lv2 : lv.getObjectives()) {
            if (lv2.getCriterion() != ScoreboardCriterion.TRIGGER) continue;
            boolean bl = false;
            for (String string : targets) {
                if (lv.playerHasObjective(string, lv2) && !lv.getPlayerScore(string, lv2).isLocked()) continue;
                bl = true;
                break;
            }
            if (!bl) continue;
            list.add(lv2.getName());
        }
        return CommandSource.suggestMatching(list, builder);
    }

    private static int executeGet(ServerCommandSource source, String target, ScoreboardObjective objective) throws CommandSyntaxException {
        ServerScoreboard lv = source.getMinecraftServer().getScoreboard();
        if (!lv.playerHasObjective(target, objective)) {
            throw PLAYERS_GET_NULL_EXCEPTION.create((Object)objective.getName(), (Object)target);
        }
        ScoreboardPlayerScore lv2 = lv.getPlayerScore(target, objective);
        source.sendFeedback(new TranslatableText("commands.scoreboard.players.get.success", target, lv2.getScore(), objective.toHoverableText()), false);
        return lv2.getScore();
    }

    private static int executeOperation(ServerCommandSource source, Collection<String> targets, ScoreboardObjective targetObjective, OperationArgumentType.Operation operation, Collection<String> sources, ScoreboardObjective sourceObjectives) throws CommandSyntaxException {
        ServerScoreboard lv = source.getMinecraftServer().getScoreboard();
        int i = 0;
        for (String string : targets) {
            ScoreboardPlayerScore lv2 = lv.getPlayerScore(string, targetObjective);
            for (String string2 : sources) {
                ScoreboardPlayerScore lv3 = lv.getPlayerScore(string2, sourceObjectives);
                operation.apply(lv2, lv3);
            }
            i += lv2.getScore();
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableText("commands.scoreboard.players.operation.success.single", targetObjective.toHoverableText(), targets.iterator().next(), i), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.scoreboard.players.operation.success.multiple", targetObjective.toHoverableText(), targets.size()), true);
        }
        return i;
    }

    private static int executeEnable(ServerCommandSource source, Collection<String> targets, ScoreboardObjective objective) throws CommandSyntaxException {
        if (objective.getCriterion() != ScoreboardCriterion.TRIGGER) {
            throw PLAYERS_ENABLE_INVALID_EXCEPTION.create();
        }
        ServerScoreboard lv = source.getMinecraftServer().getScoreboard();
        int i = 0;
        for (String string : targets) {
            ScoreboardPlayerScore lv2 = lv.getPlayerScore(string, objective);
            if (!lv2.isLocked()) continue;
            lv2.setLocked(false);
            ++i;
        }
        if (i == 0) {
            throw PLAYERS_ENABLE_FAILED_EXCEPTION.create();
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableText("commands.scoreboard.players.enable.success.single", objective.toHoverableText(), targets.iterator().next()), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.scoreboard.players.enable.success.multiple", objective.toHoverableText(), targets.size()), true);
        }
        return i;
    }

    private static int executeReset(ServerCommandSource source, Collection<String> targets) {
        ServerScoreboard lv = source.getMinecraftServer().getScoreboard();
        for (String string : targets) {
            lv.resetPlayerScore(string, null);
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableText("commands.scoreboard.players.reset.all.single", targets.iterator().next()), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.scoreboard.players.reset.all.multiple", targets.size()), true);
        }
        return targets.size();
    }

    private static int executeReset(ServerCommandSource source, Collection<String> targets, ScoreboardObjective objective) {
        ServerScoreboard lv = source.getMinecraftServer().getScoreboard();
        for (String string : targets) {
            lv.resetPlayerScore(string, objective);
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableText("commands.scoreboard.players.reset.specific.single", objective.toHoverableText(), targets.iterator().next()), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.scoreboard.players.reset.specific.multiple", objective.toHoverableText(), targets.size()), true);
        }
        return targets.size();
    }

    private static int executeSet(ServerCommandSource source, Collection<String> targets, ScoreboardObjective objective, int score) {
        ServerScoreboard lv = source.getMinecraftServer().getScoreboard();
        for (String string : targets) {
            ScoreboardPlayerScore lv2 = lv.getPlayerScore(string, objective);
            lv2.setScore(score);
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableText("commands.scoreboard.players.set.success.single", objective.toHoverableText(), targets.iterator().next(), score), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.scoreboard.players.set.success.multiple", objective.toHoverableText(), targets.size(), score), true);
        }
        return score * targets.size();
    }

    private static int executeAdd(ServerCommandSource source, Collection<String> targets, ScoreboardObjective objective, int score) {
        ServerScoreboard lv = source.getMinecraftServer().getScoreboard();
        int j = 0;
        for (String string : targets) {
            ScoreboardPlayerScore lv2 = lv.getPlayerScore(string, objective);
            lv2.setScore(lv2.getScore() + score);
            j += lv2.getScore();
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableText("commands.scoreboard.players.add.success.single", score, objective.toHoverableText(), targets.iterator().next(), j), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.scoreboard.players.add.success.multiple", score, objective.toHoverableText(), targets.size()), true);
        }
        return j;
    }

    private static int executeRemove(ServerCommandSource source, Collection<String> targets, ScoreboardObjective objective, int score) {
        ServerScoreboard lv = source.getMinecraftServer().getScoreboard();
        int j = 0;
        for (String string : targets) {
            ScoreboardPlayerScore lv2 = lv.getPlayerScore(string, objective);
            lv2.setScore(lv2.getScore() - score);
            j += lv2.getScore();
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableText("commands.scoreboard.players.remove.success.single", score, objective.toHoverableText(), targets.iterator().next(), j), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.scoreboard.players.remove.success.multiple", score, objective.toHoverableText(), targets.size()), true);
        }
        return j;
    }

    private static int executeListPlayers(ServerCommandSource source) {
        Collection<String> collection = source.getMinecraftServer().getScoreboard().getKnownPlayers();
        if (collection.isEmpty()) {
            source.sendFeedback(new TranslatableText("commands.scoreboard.players.list.empty"), false);
        } else {
            source.sendFeedback(new TranslatableText("commands.scoreboard.players.list.success", collection.size(), Texts.joinOrdered(collection)), false);
        }
        return collection.size();
    }

    private static int executeListScores(ServerCommandSource source, String target) {
        Map<ScoreboardObjective, ScoreboardPlayerScore> map = source.getMinecraftServer().getScoreboard().getPlayerObjectives(target);
        if (map.isEmpty()) {
            source.sendFeedback(new TranslatableText("commands.scoreboard.players.list.entity.empty", target), false);
        } else {
            source.sendFeedback(new TranslatableText("commands.scoreboard.players.list.entity.success", target, map.size()), false);
            for (Map.Entry<ScoreboardObjective, ScoreboardPlayerScore> entry : map.entrySet()) {
                source.sendFeedback(new TranslatableText("commands.scoreboard.players.list.entity.entry", entry.getKey().toHoverableText(), entry.getValue().getScore()), false);
            }
        }
        return map.size();
    }

    private static int executeClearDisplay(ServerCommandSource source, int slot) throws CommandSyntaxException {
        ServerScoreboard lv = source.getMinecraftServer().getScoreboard();
        if (lv.getObjectiveForSlot(slot) == null) {
            throw OBJECTIVES_DISPLAY_ALREADY_EMPTY_EXCEPTION.create();
        }
        ((Scoreboard)lv).setObjectiveSlot(slot, null);
        source.sendFeedback(new TranslatableText("commands.scoreboard.objectives.display.cleared", Scoreboard.getDisplaySlotNames()[slot]), true);
        return 0;
    }

    private static int executeSetDisplay(ServerCommandSource source, int slot, ScoreboardObjective objective) throws CommandSyntaxException {
        ServerScoreboard lv = source.getMinecraftServer().getScoreboard();
        if (lv.getObjectiveForSlot(slot) == objective) {
            throw OBJECTIVES_DISPLAY_ALREADY_SET_EXCEPTION.create();
        }
        ((Scoreboard)lv).setObjectiveSlot(slot, objective);
        source.sendFeedback(new TranslatableText("commands.scoreboard.objectives.display.set", Scoreboard.getDisplaySlotNames()[slot], objective.getDisplayName()), true);
        return 0;
    }

    private static int executeModifyObjective(ServerCommandSource source, ScoreboardObjective objective, Text displayName) {
        if (!objective.getDisplayName().equals(displayName)) {
            objective.setDisplayName(displayName);
            source.sendFeedback(new TranslatableText("commands.scoreboard.objectives.modify.displayname", objective.getName(), objective.toHoverableText()), true);
        }
        return 0;
    }

    private static int executeModifyRenderType(ServerCommandSource source, ScoreboardObjective objective, ScoreboardCriterion.RenderType type) {
        if (objective.getRenderType() != type) {
            objective.setRenderType(type);
            source.sendFeedback(new TranslatableText("commands.scoreboard.objectives.modify.rendertype", objective.toHoverableText()), true);
        }
        return 0;
    }

    private static int executeRemoveObjective(ServerCommandSource source, ScoreboardObjective objective) {
        ServerScoreboard lv = source.getMinecraftServer().getScoreboard();
        lv.removeObjective(objective);
        source.sendFeedback(new TranslatableText("commands.scoreboard.objectives.remove.success", objective.toHoverableText()), true);
        return lv.getObjectives().size();
    }

    private static int executeAddObjective(ServerCommandSource source, String objective, ScoreboardCriterion criteria, Text displayName) throws CommandSyntaxException {
        ServerScoreboard lv = source.getMinecraftServer().getScoreboard();
        if (lv.getNullableObjective(objective) != null) {
            throw OBJECTIVES_ADD_DUPLICATE_EXCEPTION.create();
        }
        if (objective.length() > 16) {
            throw ObjectiveArgumentType.LONG_NAME_EXCEPTION.create((Object)16);
        }
        lv.addObjective(objective, criteria, displayName, criteria.getCriterionType());
        ScoreboardObjective lv2 = lv.getNullableObjective(objective);
        source.sendFeedback(new TranslatableText("commands.scoreboard.objectives.add.success", lv2.toHoverableText()), true);
        return lv.getObjectives().size();
    }

    private static int executeListObjectives(ServerCommandSource source) {
        Collection<ScoreboardObjective> collection = source.getMinecraftServer().getScoreboard().getObjectives();
        if (collection.isEmpty()) {
            source.sendFeedback(new TranslatableText("commands.scoreboard.objectives.list.empty"), false);
        } else {
            source.sendFeedback(new TranslatableText("commands.scoreboard.objectives.list.success", collection.size(), Texts.join(collection, ScoreboardObjective::toHoverableText)), false);
        }
        return collection.size();
    }
}

