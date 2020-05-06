/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 */
package net.minecraft.server.dedicated.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

public class SaveAllCommand {
    private static final SimpleCommandExceptionType SAVE_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.save.failed"));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("save-all").requires(arg -> arg.hasPermissionLevel(4))).executes(commandContext -> SaveAllCommand.saveAll((ServerCommandSource)commandContext.getSource(), false))).then(CommandManager.literal("flush").executes(commandContext -> SaveAllCommand.saveAll((ServerCommandSource)commandContext.getSource(), true))));
    }

    private static int saveAll(ServerCommandSource arg, boolean bl) throws CommandSyntaxException {
        arg.sendFeedback(new TranslatableText("commands.save.saving"), false);
        MinecraftServer minecraftServer = arg.getMinecraftServer();
        minecraftServer.getPlayerManager().saveAllPlayerData();
        boolean bl2 = minecraftServer.save(true, bl, true);
        if (!bl2) {
            throw SAVE_FAILED_EXCEPTION.create();
        }
        arg.sendFeedback(new TranslatableText("commands.save.success"), true);
        return 1;
    }
}

