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
import net.minecraft.command.arguments.ColorArgumentType;
import net.minecraft.command.arguments.ScoreHolderArgumentType;
import net.minecraft.command.arguments.TeamArgumentType;
import net.minecraft.command.arguments.TextArgumentType;
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

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("team").requires(arg -> arg.hasPermissionLevel(2))).then(((LiteralArgumentBuilder)CommandManager.literal("list").executes(commandContext -> TeamCommand.executeListTeams((ServerCommandSource)commandContext.getSource()))).then(CommandManager.argument("team", TeamArgumentType.team()).executes(commandContext -> TeamCommand.executeListMembers((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team")))))).then(CommandManager.literal("add").then(((RequiredArgumentBuilder)CommandManager.argument("team", StringArgumentType.word()).executes(commandContext -> TeamCommand.executeAdd((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString((CommandContext)commandContext, (String)"team")))).then(CommandManager.argument("displayName", TextArgumentType.text()).executes(commandContext -> TeamCommand.executeAdd((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString((CommandContext)commandContext, (String)"team"), TextArgumentType.getTextArgument((CommandContext<ServerCommandSource>)commandContext, "displayName"))))))).then(CommandManager.literal("remove").then(CommandManager.argument("team", TeamArgumentType.team()).executes(commandContext -> TeamCommand.executeRemove((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team")))))).then(CommandManager.literal("empty").then(CommandManager.argument("team", TeamArgumentType.team()).executes(commandContext -> TeamCommand.executeEmpty((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team")))))).then(CommandManager.literal("join").then(((RequiredArgumentBuilder)CommandManager.argument("team", TeamArgumentType.team()).executes(commandContext -> TeamCommand.executeJoin((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), Collections.singleton(((ServerCommandSource)commandContext.getSource()).getEntityOrThrow().getEntityName())))).then(CommandManager.argument("members", ScoreHolderArgumentType.scoreHolders()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).executes(commandContext -> TeamCommand.executeJoin((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), ScoreHolderArgumentType.getScoreboardScoreHolders((CommandContext<ServerCommandSource>)commandContext, "members"))))))).then(CommandManager.literal("leave").then(CommandManager.argument("members", ScoreHolderArgumentType.scoreHolders()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).executes(commandContext -> TeamCommand.executeLeave((ServerCommandSource)commandContext.getSource(), ScoreHolderArgumentType.getScoreboardScoreHolders((CommandContext<ServerCommandSource>)commandContext, "members")))))).then(CommandManager.literal("modify").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("team", TeamArgumentType.team()).then(CommandManager.literal("displayName").then(CommandManager.argument("displayName", TextArgumentType.text()).executes(commandContext -> TeamCommand.executeModifyDisplayName((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), TextArgumentType.getTextArgument((CommandContext<ServerCommandSource>)commandContext, "displayName")))))).then(CommandManager.literal("color").then(CommandManager.argument("value", ColorArgumentType.color()).executes(commandContext -> TeamCommand.executeModifyColor((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), ColorArgumentType.getColor((CommandContext<ServerCommandSource>)commandContext, "value")))))).then(CommandManager.literal("friendlyFire").then(CommandManager.argument("allowed", BoolArgumentType.bool()).executes(commandContext -> TeamCommand.executeModifyFriendlyFire((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), BoolArgumentType.getBool((CommandContext)commandContext, (String)"allowed")))))).then(CommandManager.literal("seeFriendlyInvisibles").then(CommandManager.argument("allowed", BoolArgumentType.bool()).executes(commandContext -> TeamCommand.executeModifySeeFriendlyInvisibles((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), BoolArgumentType.getBool((CommandContext)commandContext, (String)"allowed")))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("nametagVisibility").then(CommandManager.literal("never").executes(commandContext -> TeamCommand.executeModifyNametagVisibility((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), AbstractTeam.VisibilityRule.NEVER)))).then(CommandManager.literal("hideForOtherTeams").executes(commandContext -> TeamCommand.executeModifyNametagVisibility((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), AbstractTeam.VisibilityRule.HIDE_FOR_OTHER_TEAMS)))).then(CommandManager.literal("hideForOwnTeam").executes(commandContext -> TeamCommand.executeModifyNametagVisibility((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), AbstractTeam.VisibilityRule.HIDE_FOR_OWN_TEAM)))).then(CommandManager.literal("always").executes(commandContext -> TeamCommand.executeModifyNametagVisibility((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), AbstractTeam.VisibilityRule.ALWAYS))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("deathMessageVisibility").then(CommandManager.literal("never").executes(commandContext -> TeamCommand.executeModifyDeathMessageVisibility((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), AbstractTeam.VisibilityRule.NEVER)))).then(CommandManager.literal("hideForOtherTeams").executes(commandContext -> TeamCommand.executeModifyDeathMessageVisibility((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), AbstractTeam.VisibilityRule.HIDE_FOR_OTHER_TEAMS)))).then(CommandManager.literal("hideForOwnTeam").executes(commandContext -> TeamCommand.executeModifyDeathMessageVisibility((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), AbstractTeam.VisibilityRule.HIDE_FOR_OWN_TEAM)))).then(CommandManager.literal("always").executes(commandContext -> TeamCommand.executeModifyDeathMessageVisibility((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), AbstractTeam.VisibilityRule.ALWAYS))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("collisionRule").then(CommandManager.literal("never").executes(commandContext -> TeamCommand.executeModifyCollisionRule((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), AbstractTeam.CollisionRule.NEVER)))).then(CommandManager.literal("pushOwnTeam").executes(commandContext -> TeamCommand.executeModifyCollisionRule((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), AbstractTeam.CollisionRule.PUSH_OWN_TEAM)))).then(CommandManager.literal("pushOtherTeams").executes(commandContext -> TeamCommand.executeModifyCollisionRule((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), AbstractTeam.CollisionRule.PUSH_OTHER_TEAMS)))).then(CommandManager.literal("always").executes(commandContext -> TeamCommand.executeModifyCollisionRule((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), AbstractTeam.CollisionRule.ALWAYS))))).then(CommandManager.literal("prefix").then(CommandManager.argument("prefix", TextArgumentType.text()).executes(commandContext -> TeamCommand.executeModifyPrefix((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), TextArgumentType.getTextArgument((CommandContext<ServerCommandSource>)commandContext, "prefix")))))).then(CommandManager.literal("suffix").then(CommandManager.argument("suffix", TextArgumentType.text()).executes(commandContext -> TeamCommand.executeModifySuffix((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam((CommandContext<ServerCommandSource>)commandContext, "team"), TextArgumentType.getTextArgument((CommandContext<ServerCommandSource>)commandContext, "suffix"))))))));
    }

    private static int executeLeave(ServerCommandSource arg, Collection<String> collection) {
        ServerScoreboard lv = arg.getMinecraftServer().getScoreboard();
        for (String string : collection) {
            lv.clearPlayerTeam(string);
        }
        if (collection.size() == 1) {
            arg.sendFeedback(new TranslatableText("commands.team.leave.success.single", collection.iterator().next()), true);
        } else {
            arg.sendFeedback(new TranslatableText("commands.team.leave.success.multiple", collection.size()), true);
        }
        return collection.size();
    }

    private static int executeJoin(ServerCommandSource arg, Team arg2, Collection<String> collection) {
        ServerScoreboard lv = arg.getMinecraftServer().getScoreboard();
        for (String string : collection) {
            ((Scoreboard)lv).addPlayerToTeam(string, arg2);
        }
        if (collection.size() == 1) {
            arg.sendFeedback(new TranslatableText("commands.team.join.success.single", collection.iterator().next(), arg2.getFormattedName()), true);
        } else {
            arg.sendFeedback(new TranslatableText("commands.team.join.success.multiple", collection.size(), arg2.getFormattedName()), true);
        }
        return collection.size();
    }

    private static int executeModifyNametagVisibility(ServerCommandSource arg, Team arg2, AbstractTeam.VisibilityRule arg3) throws CommandSyntaxException {
        if (arg2.getNameTagVisibilityRule() == arg3) {
            throw OPTION_NAMETAG_VISIBILITY_UNCHANGED_EXCEPTION.create();
        }
        arg2.setNameTagVisibilityRule(arg3);
        arg.sendFeedback(new TranslatableText("commands.team.option.nametagVisibility.success", arg2.getFormattedName(), arg3.getTranslationKey()), true);
        return 0;
    }

    private static int executeModifyDeathMessageVisibility(ServerCommandSource arg, Team arg2, AbstractTeam.VisibilityRule arg3) throws CommandSyntaxException {
        if (arg2.getDeathMessageVisibilityRule() == arg3) {
            throw OPTION_DEATH_MESSAGE_VISIBILITY_UNCHANGED_EXCEPTION.create();
        }
        arg2.setDeathMessageVisibilityRule(arg3);
        arg.sendFeedback(new TranslatableText("commands.team.option.deathMessageVisibility.success", arg2.getFormattedName(), arg3.getTranslationKey()), true);
        return 0;
    }

    private static int executeModifyCollisionRule(ServerCommandSource arg, Team arg2, AbstractTeam.CollisionRule arg3) throws CommandSyntaxException {
        if (arg2.getCollisionRule() == arg3) {
            throw OPTION_COLLISION_RULE_UNCHANGED_EXCEPTION.create();
        }
        arg2.setCollisionRule(arg3);
        arg.sendFeedback(new TranslatableText("commands.team.option.collisionRule.success", arg2.getFormattedName(), arg3.getTranslationKey()), true);
        return 0;
    }

    private static int executeModifySeeFriendlyInvisibles(ServerCommandSource arg, Team arg2, boolean bl) throws CommandSyntaxException {
        if (arg2.shouldShowFriendlyInvisibles() == bl) {
            if (bl) {
                throw OPTION_SEE_FRIENDLY_INVISIBLES_ALREADY_ENABLED_EXCEPTION.create();
            }
            throw OPTION_SEE_FRIENDLY_INVISIBLES_ALREADY_DISABLED_EXCEPTION.create();
        }
        arg2.setShowFriendlyInvisibles(bl);
        arg.sendFeedback(new TranslatableText("commands.team.option.seeFriendlyInvisibles." + (bl ? "enabled" : "disabled"), arg2.getFormattedName()), true);
        return 0;
    }

    private static int executeModifyFriendlyFire(ServerCommandSource arg, Team arg2, boolean bl) throws CommandSyntaxException {
        if (arg2.isFriendlyFireAllowed() == bl) {
            if (bl) {
                throw OPTION_FRIENDLY_FIRE_ALREADY_ENABLED_EXCEPTION.create();
            }
            throw OPTION_FRIENDLY_FIRE_ALREADY_DISABLED_EXCEPTION.create();
        }
        arg2.setFriendlyFireAllowed(bl);
        arg.sendFeedback(new TranslatableText("commands.team.option.friendlyfire." + (bl ? "enabled" : "disabled"), arg2.getFormattedName()), true);
        return 0;
    }

    private static int executeModifyDisplayName(ServerCommandSource arg, Team arg2, Text arg3) throws CommandSyntaxException {
        if (arg2.getDisplayName().equals(arg3)) {
            throw OPTION_NAME_UNCHANGED_EXCEPTION.create();
        }
        arg2.setDisplayName(arg3);
        arg.sendFeedback(new TranslatableText("commands.team.option.name.success", arg2.getFormattedName()), true);
        return 0;
    }

    private static int executeModifyColor(ServerCommandSource arg, Team arg2, Formatting arg3) throws CommandSyntaxException {
        if (arg2.getColor() == arg3) {
            throw OPTION_COLOR_UNCHANGED_EXCEPTION.create();
        }
        arg2.setColor(arg3);
        arg.sendFeedback(new TranslatableText("commands.team.option.color.success", arg2.getFormattedName(), arg3.getName()), true);
        return 0;
    }

    private static int executeEmpty(ServerCommandSource arg, Team arg2) throws CommandSyntaxException {
        ServerScoreboard lv = arg.getMinecraftServer().getScoreboard();
        ArrayList collection = Lists.newArrayList(arg2.getPlayerList());
        if (collection.isEmpty()) {
            throw EMPTY_UNCHANGED_EXCEPTION.create();
        }
        for (String string : collection) {
            ((Scoreboard)lv).removePlayerFromTeam(string, arg2);
        }
        arg.sendFeedback(new TranslatableText("commands.team.empty.success", collection.size(), arg2.getFormattedName()), true);
        return collection.size();
    }

    private static int executeRemove(ServerCommandSource arg, Team arg2) {
        ServerScoreboard lv = arg.getMinecraftServer().getScoreboard();
        lv.removeTeam(arg2);
        arg.sendFeedback(new TranslatableText("commands.team.remove.success", arg2.getFormattedName()), true);
        return lv.getTeams().size();
    }

    private static int executeAdd(ServerCommandSource arg, String string) throws CommandSyntaxException {
        return TeamCommand.executeAdd(arg, string, new LiteralText(string));
    }

    private static int executeAdd(ServerCommandSource arg, String string, Text arg2) throws CommandSyntaxException {
        ServerScoreboard lv = arg.getMinecraftServer().getScoreboard();
        if (lv.getTeam(string) != null) {
            throw ADD_DUPLICATE_EXCEPTION.create();
        }
        if (string.length() > 16) {
            throw ADD_LONG_NAME_EXCEPTION.create((Object)16);
        }
        Team lv2 = lv.addTeam(string);
        lv2.setDisplayName(arg2);
        arg.sendFeedback(new TranslatableText("commands.team.add.success", lv2.getFormattedName()), true);
        return lv.getTeams().size();
    }

    private static int executeListMembers(ServerCommandSource arg, Team arg2) {
        Collection<String> collection = arg2.getPlayerList();
        if (collection.isEmpty()) {
            arg.sendFeedback(new TranslatableText("commands.team.list.members.empty", arg2.getFormattedName()), false);
        } else {
            arg.sendFeedback(new TranslatableText("commands.team.list.members.success", arg2.getFormattedName(), collection.size(), Texts.joinOrdered(collection)), false);
        }
        return collection.size();
    }

    private static int executeListTeams(ServerCommandSource arg) {
        Collection<Team> collection = arg.getMinecraftServer().getScoreboard().getTeams();
        if (collection.isEmpty()) {
            arg.sendFeedback(new TranslatableText("commands.team.list.teams.empty"), false);
        } else {
            arg.sendFeedback(new TranslatableText("commands.team.list.teams.success", collection.size(), Texts.join(collection, Team::getFormattedName)), false);
        }
        return collection.size();
    }

    private static int executeModifyPrefix(ServerCommandSource arg, Team arg2, Text arg3) {
        arg2.setPrefix(arg3);
        arg.sendFeedback(new TranslatableText("commands.team.option.prefix.success", arg3), false);
        return 1;
    }

    private static int executeModifySuffix(ServerCommandSource arg, Team arg2, Text arg3) {
        arg2.setSuffix(arg3);
        arg.sendFeedback(new TranslatableText("commands.team.option.suffix.success", arg3), false);
        return 1;
    }
}

