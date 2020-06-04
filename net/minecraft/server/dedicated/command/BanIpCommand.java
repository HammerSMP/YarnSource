/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  javax.annotation.Nullable
 */
package net.minecraft.server.dedicated.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.arguments.MessageArgumentType;
import net.minecraft.server.BannedIpEntry;
import net.minecraft.server.BannedIpList;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class BanIpCommand {
    public static final Pattern PATTERN = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
    private static final SimpleCommandExceptionType INVALID_IP_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.banip.invalid"));
    private static final SimpleCommandExceptionType ALREADY_BANNED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.banip.failed"));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("ban-ip").requires(arg -> arg.hasPermissionLevel(3))).then(((RequiredArgumentBuilder)CommandManager.argument("target", StringArgumentType.word()).executes(commandContext -> BanIpCommand.checkIp((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString((CommandContext)commandContext, (String)"target"), null))).then(CommandManager.argument("reason", MessageArgumentType.message()).executes(commandContext -> BanIpCommand.checkIp((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString((CommandContext)commandContext, (String)"target"), MessageArgumentType.getMessage((CommandContext<ServerCommandSource>)commandContext, "reason"))))));
    }

    private static int checkIp(ServerCommandSource arg, String string, @Nullable Text arg2) throws CommandSyntaxException {
        Matcher matcher = PATTERN.matcher(string);
        if (matcher.matches()) {
            return BanIpCommand.banIp(arg, string, arg2);
        }
        ServerPlayerEntity lv = arg.getMinecraftServer().getPlayerManager().getPlayer(string);
        if (lv != null) {
            return BanIpCommand.banIp(arg, lv.getIp(), arg2);
        }
        throw INVALID_IP_EXCEPTION.create();
    }

    private static int banIp(ServerCommandSource arg, String string, @Nullable Text arg2) throws CommandSyntaxException {
        BannedIpList lv = arg.getMinecraftServer().getPlayerManager().getIpBanList();
        if (lv.isBanned(string)) {
            throw ALREADY_BANNED_EXCEPTION.create();
        }
        List<ServerPlayerEntity> list = arg.getMinecraftServer().getPlayerManager().getPlayersByIp(string);
        BannedIpEntry lv2 = new BannedIpEntry(string, null, arg.getName(), null, arg2 == null ? null : arg2.getString());
        lv.add(lv2);
        arg.sendFeedback(new TranslatableText("commands.banip.success", string, lv2.getReason()), true);
        if (!list.isEmpty()) {
            arg.sendFeedback(new TranslatableText("commands.banip.info", list.size(), EntitySelector.getNames(list)), true);
        }
        for (ServerPlayerEntity lv3 : list) {
            lv3.networkHandler.disconnect(new TranslatableText("multiplayer.disconnect.ip_banned"));
        }
        return list.size();
    }
}

