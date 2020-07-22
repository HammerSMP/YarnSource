/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.BoolArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 */
package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.argument.ColorArgumentType;
import net.minecraft.command.argument.ScoreHolderArgumentType;
import net.minecraft.command.argument.TeamArgumentType;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class TeamCommand {
    private static final SimpleCommandExceptionType ADD_DUPLICATE_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.team.add.duplicate"));
    private static final DynamicCommandExceptionType ADD_LONG_NAME_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.team.add.longName", object));
    private static final SimpleCommandExceptionType EMPTY_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.team.empty.unchanged"));
    private static final SimpleCommandExceptionType OPTION_NAME_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.team.option.name.unchanged"));
    private static final SimpleCommandExceptionType OPTION_COLOR_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.team.option.color.unchanged"));
    private static final SimpleCommandExceptionType OPTION_FRIENDLY_FIRE_ALREADY_ENABLED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.team.option.friendlyfire.alreadyEnabled"));
    private static final SimpleCommandExceptionType OPTION_FRIENDLY_FIRE_ALREADY_DISABLED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.team.option.friendlyfire.alreadyDisabled"));
    private static final SimpleCommandExceptionType OPTION_SEE_FRIENDLY_INVISIBLES_ALREADY_ENABLED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.team.option.seeFriendlyInvisibles.alreadyEnabled"));
    private static final SimpleCommandExceptionType OPTION_SEE_FRIENDLY_INVISIBLES_ALREADY_DISABLED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.team.option.seeFriendlyInvisibles.alreadyDisabled"));
    private static final SimpleCommandExceptionType OPTION_NAMETAG_VISIBILITY_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.team.option.nametagVisibility.unchanged"));
    private static final SimpleCommandExceptionType OPTION_DEATH_MESSAGE_VISIBILITY_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.team.option.deathMessageVisibility.unchanged"));
    private static final SimpleCommandExceptionType OPTION_COLLISION_RULE_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.team.option.collisionRule.unchanged"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("team").requires(arg -> arg.hasPermissionLevel(2))).then(((LiteralArgumentBuilder)CommandManager.literal("list").executes(commandContext -> TeamCommand.executeListTeams((ServerCommandSource)commandContext.getSource()))).then(CommandManager.argument("team", TeamArgumentType.team()).executes(commandContext -> TeamCommand.executeListMembers((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team")))))).then(CommandManager.literal("add").then(((RequiredArgumentBuilder)CommandManager.argument("team", StringArgumentType.word()).executes(commandContext -> TeamCommand.executeAdd((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString((CommandContext)commandContext, (String)"team")))).then(CommandManager.argument("displayName", TextArgumentType.text()).executes(commandContext -> TeamCommand.executeAdd((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString((CommandContext)commandContext, (String)"team"), TextArgumentType.getTextArgument((CommandContext<ServerCommandSource>)commandContext, "displayName"))))))).then(CommandManager.literal("remove").then(CommandManager.argument("team", TeamArgumentType.team()).executes(commandContext -> TeamCommand.executeRemove((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team")))))).then(CommandManager.literal("empty").then(CommandManager.argument("team", TeamArgumentType.team()).executes(commandContext -> TeamCommand.executeEmpty((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team")))))).then(CommandManager.literal("join").then(((RequiredArgumentBuilder)CommandManager.argument("team", TeamArgumentType.team()).executes(commandContext -> TeamCommand.executeJoin((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), Collections.singleton(((ServerCommandSource)commandContext.getSource()).getEntityOrThrow().getEntityName())))).then(CommandManager.argument("members", ScoreHolderArgumentType.scoreHolders()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).executes(commandContext -> TeamCommand.executeJoin((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), ScoreHolderArgumentType.getScoreboardScoreHolders((CommandContext<ServerCommandSource>)commandContext, "members"))))))).then(CommandManager.literal("leave").then(CommandManager.argument("members", ScoreHolderArgumentType.scoreHolders()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).executes(commandContext -> TeamCommand.executeLeave((ServerCommandSource)commandContext.getSource(), ScoreHolderArgumentType.getScoreboardScoreHolders((CommandContext<ServerCommandSource>)commandContext, "members")))))).then(CommandManager.literal("modify").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("team", TeamArgumentType.team()).then(CommandManager.literal("displayName").then(CommandManager.argument("displayName", TextArgumentType.text()).executes(commandContext -> TeamCommand.executeModifyDisplayName((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), TextArgumentType.getTextArgument((CommandContext<ServerCommandSource>)commandContext, "displayName")))))).then(CommandManager.literal("color").then(CommandManager.argument("value", ColorArgumentType.color()).executes(commandContext -> TeamCommand.executeModifyColor((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), ColorArgumentType.getColor((CommandContext<ServerCommandSource>)commandContext, "value")))))).then(CommandManager.literal("friendlyFire").then(CommandManager.argument("allowed", BoolArgumentType.bool()).executes(commandContext -> TeamCommand.executeModifyFriendlyFire((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), BoolArgumentType.getBool((CommandContext)commandContext, (String)"allowed")))))).then(CommandManager.literal("seeFriendlyInvisibles").then(CommandManager.argument("allowed", BoolArgumentType.bool()).executes(commandContext -> TeamCommand.executeModifySeeFriendlyInvisibles((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), BoolArgumentType.getBool((CommandContext)commandContext, (String)"allowed")))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("nametagVisibility").then(CommandManager.literal("never").executes(commandContext -> TeamCommand.executeModifyNametagVisibility((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), AbstractTeam.VisibilityRule.NEVER)))).then(CommandManager.literal("hideForOtherTeams").executes(commandContext -> TeamCommand.executeModifyNametagVisibility((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), AbstractTeam.VisibilityRule.HIDE_FOR_OTHER_TEAMS)))).then(CommandManager.literal("hideForOwnTeam").executes(commandContext -> TeamCommand.executeModifyNametagVisibility((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), AbstractTeam.VisibilityRule.HIDE_FOR_OWN_TEAM)))).then(CommandManager.literal("always").executes(commandContext -> TeamCommand.executeModifyNametagVisibility((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), AbstractTeam.VisibilityRule.ALWAYS))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("deathMessageVisibility").then(CommandManager.literal("never").executes(commandContext -> TeamCommand.executeModifyDeathMessageVisibility((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), AbstractTeam.VisibilityRule.NEVER)))).then(CommandManager.literal("hideForOtherTeams").executes(commandContext -> TeamCommand.executeModifyDeathMessageVisibility((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), AbstractTeam.VisibilityRule.HIDE_FOR_OTHER_TEAMS)))).then(CommandManager.literal("hideForOwnTeam").executes(commandContext -> TeamCommand.executeModifyDeathMessageVisibility((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), AbstractTeam.VisibilityRule.HIDE_FOR_OWN_TEAM)))).then(CommandManager.literal("always").executes(commandContext -> TeamCommand.executeModifyDeathMessageVisibility((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), AbstractTeam.VisibilityRule.ALWAYS))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("collisionRule").then(CommandManager.literal("never").executes(commandContext -> TeamCommand.executeModifyCollisionRule((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), AbstractTeam.CollisionRule.NEVER)))).then(CommandManager.literal("pushOwnTeam").executes(commandContext -> TeamCommand.executeModifyCollisionRule((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), AbstractTeam.CollisionRule.PUSH_OWN_TEAM)))).then(CommandManager.literal("pushOtherTeams").executes(commandContext -> TeamCommand.executeModifyCollisionRule((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), AbstractTeam.CollisionRule.PUSH_OTHER_TEAMS)))).then(CommandManager.literal("always").executes(commandContext -> TeamCommand.executeModifyCollisionRule((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), AbstractTeam.CollisionRule.ALWAYS))))).then(CommandManager.literal("prefix").then(CommandManager.argument("prefix", TextArgumentType.text()).executes(commandContext -> TeamCommand.executeModifyPrefix((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), TextArgumentType.getTextArgument((CommandContext<ServerCommandSource>)commandContext, "prefix")))))).then(CommandManager.literal("suffix").then(CommandManager.argument("suffix", TextArgumentType.text()).executes(commandContext -> TeamCommand.executeModifySuffix((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), TextArgumentType.getTextArgument((CommandContext<ServerCommandSource>)commandContext, "suffix"))))))));
    }

    private static int executeLeave(ServerCommandSource source, Collection<String> members) {
        ServerScoreboard lv = source.getMinecraftServer().getScoreboard();
        for (String string : members) {
            lv.clearPlayerTeam(string);
        }
        if (members.size() == 1) {
            source.sendFeedback(new TranslatableText("commands.team.leave.success.single", members.iterator().next()), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.team.leave.success.multiple", members.size()), true);
        }
        return members.size();
    }

    private static int executeJoin(ServerCommandSource source, Team team, Collection<String> members) {
        ServerScoreboard lv = source.getMinecraftServer().getScoreboard();
        for (String string : members) {
            ((Scoreboard)lv).addPlayerToTeam(string, team);
        }
        if (members.size() == 1) {
            source.sendFeedback(new TranslatableText("commands.team.join.success.single", members.iterator().next(), team.getFormattedName()), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.team.join.success.multiple", members.size(), team.getFormattedName()), true);
        }
        return members.size();
    }

    private static int executeModifyNametagVisibility(ServerCommandSource source, Team team, AbstractTeam.VisibilityRule visibility) throws CommandSyntaxException {
        if (team.getNameTagVisibilityRule() == visibility) {
            throw OPTION_NAMETAG_VISIBILITY_UNCHANGED_EXCEPTION.create();
        }
        team.setNameTagVisibilityRule(visibility);
        source.sendFeedback(new TranslatableText("commands.team.option.nametagVisibility.success", team.getFormattedName(), visibility.getTranslationKey()), true);
        return 0;
    }

    private static int executeModifyDeathMessageVisibility(ServerCommandSource source, Team team, AbstractTeam.VisibilityRule visibility) throws CommandSyntaxException {
        if (team.getDeathMessageVisibilityRule() == visibility) {
            throw OPTION_DEATH_MESSAGE_VISIBILITY_UNCHANGED_EXCEPTION.create();
        }
        team.setDeathMessageVisibilityRule(visibility);
        source.sendFeedback(new TranslatableText("commands.team.option.deathMessageVisibility.success", team.getFormattedName(), visibility.getTranslationKey()), true);
        return 0;
    }

    private static int executeModifyCollisionRule(ServerCommandSource source, Team team, AbstractTeam.CollisionRule collisionRule) throws CommandSyntaxException {
        if (team.getCollisionRule() == collisionRule) {
            throw OPTION_COLLISION_RULE_UNCHANGED_EXCEPTION.create();
        }
        team.setCollisionRule(collisionRule);
        source.sendFeedback(new TranslatableText("commands.team.option.collisionRule.success", team.getFormattedName(), collisionRule.getTranslationKey()), true);
        return 0;
    }

    private static int executeModifySeeFriendlyInvisibles(ServerCommandSource source, Team team, boolean allowed) throws CommandSyntaxException {
        if (team.shouldShowFriendlyInvisibles() == allowed) {
            if (allowed) {
                throw OPTION_SEE_FRIENDLY_INVISIBLES_ALREADY_ENABLED_EXCEPTION.create();
            }
            throw OPTION_SEE_FRIENDLY_INVISIBLES_ALREADY_DISABLED_EXCEPTION.create();
        }
        team.setShowFriendlyInvisibles(allowed);
        source.sendFeedback(new TranslatableText("commands.team.option.seeFriendlyInvisibles." + (allowed ? "enabled" : "disabled"), team.getFormattedName()), true);
        return 0;
    }

    private static int executeModifyFriendlyFire(ServerCommandSource source, Team team, boolean allowed) throws CommandSyntaxException {
        if (team.isFriendlyFireAllowed() == allowed) {
            if (allowed) {
                throw OPTION_FRIENDLY_FIRE_ALREADY_ENABLED_EXCEPTION.create();
            }
            throw OPTION_FRIENDLY_FIRE_ALREADY_DISABLED_EXCEPTION.create();
        }
        team.setFriendlyFireAllowed(allowed);
        source.sendFeedback(new TranslatableText("commands.team.option.friendlyfire." + (allowed ? "enabled" : "disabled"), team.getFormattedName()), true);
        return 0;
    }

    private static int executeModifyDisplayName(ServerCommandSource source, Team team, Text displayName) throws CommandSyntaxException {
        if (team.getDisplayName().equals(displayName)) {
            throw OPTION_NAME_UNCHANGED_EXCEPTION.create();
        }
        team.setDisplayName(displayName);
        source.sendFeedback(new TranslatableText("commands.team.option.name.success", team.getFormattedName()), true);
        return 0;
    }

    private static int executeModifyColor(ServerCommandSource source, Team team, Formatting color) throws CommandSyntaxException {
        if (team.getColor() == color) {
            throw OPTION_COLOR_UNCHANGED_EXCEPTION.create();
        }
        team.setColor(color);
        source.sendFeedback(new TranslatableText("commands.team.option.color.success", team.getFormattedName(), color.getName()), true);
        return 0;
    }

    private static int executeEmpty(ServerCommandSource source, Team team) throws CommandSyntaxException {
        ServerScoreboard lv = source.getMinecraftServer().getScoreboard();
        ArrayList collection = Lists.newArrayList(team.getPlayerList());
        if (collection.isEmpty()) {
            throw EMPTY_UNCHANGED_EXCEPTION.create();
        }
        for (String string : collection) {
            ((Scoreboard)lv).removePlayerFromTeam(string, team);
        }
        source.sendFeedback(new TranslatableText("commands.team.empty.success", collection.size(), team.getFormattedName()), true);
        return collection.size();
    }

    private static int executeRemove(ServerCommandSource source, Team team) {
        ServerScoreboard lv = source.getMinecraftServer().getScoreboard();
        lv.removeTeam(team);
        source.sendFeedback(new TranslatableText("commands.team.remove.success", team.getFormattedName()), true);
        return lv.getTeams().size();
    }

    private static int executeAdd(ServerCommandSource source, String team) throws CommandSyntaxException {
        return TeamCommand.executeAdd(source, team, new LiteralText(team));
    }

    private static int executeAdd(ServerCommandSource source, String team, Text displayName) throws CommandSyntaxException {
        ServerScoreboard lv = source.getMinecraftServer().getScoreboard();
        if (lv.getTeam(team) != null) {
            throw ADD_DUPLICATE_EXCEPTION.create();
        }
        if (team.length() > 16) {
            throw ADD_LONG_NAME_EXCEPTION.create((Object)16);
        }
        Team lv2 = lv.addTeam(team);
        lv2.setDisplayName(displayName);
        source.sendFeedback(new TranslatableText("commands.team.add.success", lv2.getFormattedName()), true);
        return lv.getTeams().size();
    }

    private static int executeListMembers(ServerCommandSource source, Team team) {
        Collection<String> collection = team.getPlayerList();
        if (collection.isEmpty()) {
            source.sendFeedback(new TranslatableText("commands.team.list.members.empty", team.getFormattedName()), false);
        } else {
            source.sendFeedback(new TranslatableText("commands.team.list.members.success", team.getFormattedName(), collection.size(), Texts.joinOrdered(collection)), false);
        }
        return collection.size();
    }

    private static int executeListTeams(ServerCommandSource source) {
        Collection<Team> collection = source.getMinecraftServer().getScoreboard().getTeams();
        if (collection.isEmpty()) {
            source.sendFeedback(new TranslatableText("commands.team.list.teams.empty"), false);
        } else {
            source.sendFeedback(new TranslatableText("commands.team.list.teams.success", collection.size(), Texts.join(collection, Team::getFormattedName)), false);
        }
        return collection.size();
    }

    private static int executeModifyPrefix(ServerCommandSource source, Team team, Text prefix) {
        team.setPrefix(prefix);
        source.sendFeedback(new TranslatableText("commands.team.option.prefix.success", prefix), false);
        return 1;
    }

    private static int executeModifySuffix(ServerCommandSource source, Team team, Text suffix) {
        team.setSuffix(suffix);
        source.sendFeedback(new TranslatableText("commands.team.option.suffix.success", suffix), false);
        return 1;
    }
}

