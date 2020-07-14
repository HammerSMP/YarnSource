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
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("list").executes(commandContext -> ListCommand.executeNames((ServerCommandSource)commandContext.getSource()))).then(CommandManager.literal("uuids").executes(commandContext -> ListCommand.executeUuids((ServerCommandSource)commandContext.getSource()))));
    }

    private static int executeNames(ServerCommandSource source) {
        return ListCommand.execute(source, PlayerEntity::getDisplayName);
    }

    private static int executeUuids(ServerCommandSource source) {
        return ListCommand.execute(source, arg -> new TranslatableText("commands.list.nameAndId", arg.getName(), arg.getGameProfile().getId()));
    }

    private static int execute(ServerCommandSource source, Function<ServerPlayerEntity, Text> nameProvider) {
        PlayerManager lv = source.getMinecraftServer().getPlayerManager();
        List<ServerPlayerEntity> list = lv.getPlayerList();
        MutableText lv2 = Texts.join(list, nameProvider);
        source.sendFeedback(new TranslatableText("commands.list.players", list.size(), lv.getMaxPlayerCount(), lv2), false);
        return list.size();
    }
}

