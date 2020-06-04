/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 */
package net.minecraft.server.dedicated.command;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Collection;
import net.minecraft.server.BanEntry;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

public class BanListCommand {
    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("banlist").requires(arg -> arg.hasPermissionLevel(3))).executes(commandContext -> {
            PlayerManager lv = ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager();
            return BanListCommand.execute((ServerCommandSource)commandContext.getSource(), Lists.newArrayList((Iterable)Iterables.concat(lv.getUserBanList().values(), lv.getIpBanList().values())));
        })).then(CommandManager.literal("ips").executes(commandContext -> BanListCommand.execute((ServerCommandSource)commandContext.getSource(), ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager().getIpBanList().values())))).then(CommandManager.literal("players").executes(commandContext -> BanListCommand.execute((ServerCommandSource)commandContext.getSource(), ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager().getUserBanList().values()))));
    }

    private static int execute(ServerCommandSource arg, Collection<? extends BanEntry<?>> collection) {
        if (collection.isEmpty()) {
            arg.sendFeedback(new TranslatableText("commands.banlist.none"), false);
        } else {
            arg.sendFeedback(new TranslatableText("commands.banlist.list", collection.size()), false);
            for (BanEntry<?> lv : collection) {
                arg.sendFeedback(new TranslatableText("commands.banlist.entry", lv.toText(), lv.getSource(), lv.getReason()), false);
            }
        }
        return collection.size();
    }
}

