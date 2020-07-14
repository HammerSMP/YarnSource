/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 */
package net.minecraft.server.dedicated.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;

public class SaveOffCommand {
    private static final SimpleCommandExceptionType ALREADY_OFF_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.save.alreadyOff"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("save-off").requires(arg -> arg.hasPermissionLevel(4))).executes(commandContext -> {
            ServerCommandSource lv = (ServerCommandSource)commandContext.getSource();
            boolean bl = false;
            for (ServerWorld lv2 : lv.getMinecraftServer().getWorlds()) {
                if (lv2 == null || lv2.savingDisabled) continue;
                lv2.savingDisabled = true;
                bl = true;
            }
            if (!bl) {
                throw ALREADY_OFF_EXCEPTION.create();
            }
            lv.sendFeedback(new TranslatableText("commands.save.disabled"), true);
            return 1;
        }));
    }
}

