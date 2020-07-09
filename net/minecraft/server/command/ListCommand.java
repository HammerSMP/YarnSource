/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.List;
import java.util.function.Function;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;

public class ListCommand {
    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("list").executes(commandContext -> ListCommand.executeNames((ServerCommandSource)commandContext.getSource()))).then(CommandManager.literal("uuids").executes(commandContext -> ListCommand.executeUuids((ServerCommandSource)commandContext.getSource()))));
    }

    private static int executeNames(ServerCommandSource arg) {
        return ListCommand.execute(arg, PlayerEntity::getDisplayName);
    }

    private static int executeUuids(ServerCommandSource arg2) {
        return ListCommand.execute(arg2, arg -> new TranslatableText("commands.list.nameAndId", arg.getName(), arg.getGameProfile().getId()));
    }

    private static int execute(ServerCommandSource arg, Function<ServerPlayerEntity, Text> function) {
        PlayerManager lv = arg.getMinecraftServer().getPlayerManager();
        List<ServerPlayerEntity> list = lv.getPlayerList();
        MutableText lv2 = Texts.join(list, function);
        arg.sendFeedback(new TranslatableText("commands.list.players", list.size(), lv.getMaxPlayerCount(), lv2), false);
        return list.size();
    }
}

