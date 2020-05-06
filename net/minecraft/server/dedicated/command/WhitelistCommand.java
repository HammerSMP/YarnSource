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
import net.minecraft.server.Whitelist;
import net.minecraft.server.WhitelistEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;

public class WhitelistCommand {
    private static final SimpleCommandExceptionType ALREADY_ON_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.whitelist.alreadyOn"));
    private static final SimpleCommandExceptionType ALREADY_OFF_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.whitelist.alreadyOff"));
    private static final SimpleCommandExceptionType ADD_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.whitelist.add.failed"));
    private static final SimpleCommandExceptionType REMOVE_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.whitelist.remove.failed"));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("whitelist").requires(arg -> arg.hasPermissionLevel(3))).then(CommandManager.literal("on").executes(commandContext -> WhitelistCommand.executeOn((ServerCommandSource)commandContext.getSource())))).then(CommandManager.literal("off").executes(commandContext -> WhitelistCommand.executeOff((ServerCommandSource)commandContext.getSource())))).then(CommandManager.literal("list").executes(commandContext -> WhitelistCommand.executeList((ServerCommandSource)commandContext.getSource())))).then(CommandManager.literal("add").then(CommandManager.argument("targets", GameProfileArgumentType.gameProfile()).suggests((commandContext, suggestionsBuilder) -> {
            PlayerManager lv = ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager();
            return CommandSource.suggestMatching(lv.getPlayerList().stream().filter(arg2 -> !lv.getWhitelist().isAllowed(arg2.getGameProfile())).map(arg -> arg.getGameProfile().getName()), suggestionsBuilder);
        }).executes(commandContext -> WhitelistCommand.executeAdd((ServerCommandSource)commandContext.getSource(), GameProfileArgumentType.getProfileArgument((CommandContext<ServerCommandSource>)commandContext, "targets")))))).then(CommandManager.literal("remove").then(CommandManager.argument("targets", GameProfileArgumentType.gameProfile()).suggests((commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager().getWhitelistedNames(), suggestionsBuilder)).executes(commandContext -> WhitelistCommand.executeRemove((ServerCommandSource)commandContext.getSource(), GameProfileArgumentType.getProfileArgument((CommandContext<ServerCommandSource>)commandContext, "targets")))))).then(CommandManager.literal("reload").executes(commandContext -> WhitelistCommand.executeReload((ServerCommandSource)commandContext.getSource()))));
    }

    private static int executeReload(ServerCommandSource arg) {
        arg.getMinecraftServer().getPlayerManager().reloadWhitelist();
        arg.sendFeedback(new TranslatableText("commands.whitelist.reloaded"), true);
        arg.getMinecraftServer().kickNonWhitelistedPlayers(arg);
        return 1;
    }

    private static int executeAdd(ServerCommandSource arg, Collection<GameProfile> collection) throws CommandSyntaxException {
        Whitelist lv = arg.getMinecraftServer().getPlayerManager().getWhitelist();
        int i = 0;
        for (GameProfile gameProfile : collection) {
            if (lv.isAllowed(gameProfile)) continue;
            WhitelistEntry lv2 = new WhitelistEntry(gameProfile);
            lv.add(lv2);
            arg.sendFeedback(new TranslatableText("commands.whitelist.add.success", Texts.toText(gameProfile)), true);
            ++i;
        }
        if (i == 0) {
            throw ADD_FAILED_EXCEPTION.create();
        }
        return i;
    }

    private static int executeRemove(ServerCommandSource arg, Collection<GameProfile> collection) throws CommandSyntaxException {
        Whitelist lv = arg.getMinecraftServer().getPlayerManager().getWhitelist();
        int i = 0;
        for (GameProfile gameProfile : collection) {
            if (!lv.isAllowed(gameProfile)) continue;
            WhitelistEntry lv2 = new WhitelistEntry(gameProfile);
            lv.remove(lv2);
            arg.sendFeedback(new TranslatableText("commands.whitelist.remove.success", Texts.toText(gameProfile)), true);
            ++i;
        }
        if (i == 0) {
            throw REMOVE_FAILED_EXCEPTION.create();
        }
        arg.getMinecraftServer().kickNonWhitelistedPlayers(arg);
        return i;
    }

    private static int executeOn(ServerCommandSource arg) throws CommandSyntaxException {
        PlayerManager lv = arg.getMinecraftServer().getPlayerManager();
        if (lv.isWhitelistEnabled()) {
            throw ALREADY_ON_EXCEPTION.create();
        }
        lv.setWhitelistEnabled(true);
        arg.sendFeedback(new TranslatableText("commands.whitelist.enabled"), true);
        arg.getMinecraftServer().kickNonWhitelistedPlayers(arg);
        return 1;
    }

    private static int executeOff(ServerCommandSource arg) throws CommandSyntaxException {
        PlayerManager lv = arg.getMinecraftServer().getPlayerManager();
        if (!lv.isWhitelistEnabled()) {
            throw ALREADY_OFF_EXCEPTION.create();
        }
        lv.setWhitelistEnabled(false);
        arg.sendFeedback(new TranslatableText("commands.whitelist.disabled"), true);
        return 1;
    }

    private static int executeList(ServerCommandSource arg) {
        CharSequence[] strings = arg.getMinecraftServer().getPlayerManager().getWhitelistedNames();
        if (strings.length == 0) {
            arg.sendFeedback(new TranslatableText("commands.whitelist.none"), false);
        } else {
            arg.sendFeedback(new TranslatableText("commands.whitelist.list", strings.length, String.join((CharSequence)", ", strings)), false);
        }
        return strings.length;
    }
}

