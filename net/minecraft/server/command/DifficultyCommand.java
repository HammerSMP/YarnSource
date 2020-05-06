/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.Difficulty;

public class DifficultyCommand {
    private static final DynamicCommandExceptionType FAILURE_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.difficulty.failure", object));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = CommandManager.literal("difficulty");
        for (Difficulty lv : Difficulty.values()) {
            literalArgumentBuilder.then(CommandManager.literal(lv.getName()).executes(commandContext -> DifficultyCommand.execute((ServerCommandSource)commandContext.getSource(), lv)));
        }
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)literalArgumentBuilder.requires(arg -> arg.hasPermissionLevel(2))).executes(commandContext -> {
            Difficulty lv = ((ServerCommandSource)commandContext.getSource()).getWorld().getDifficulty();
            ((ServerCommandSource)commandContext.getSource()).sendFeedback(new TranslatableText("commands.difficulty.query", lv.getTranslatableName()), false);
            return lv.getId();
        }));
    }

    public static int execute(ServerCommandSource arg, Difficulty arg2) throws CommandSyntaxException {
        MinecraftServer minecraftServer = arg.getMinecraftServer();
        if (minecraftServer.method_27728().getDifficulty() == arg2) {
            throw FAILURE_EXCEPTION.create((Object)arg2.getName());
        }
        minecraftServer.setDifficulty(arg2, true);
        arg.sendFeedback(new TranslatableText("commands.difficulty.success", arg2.getTranslatableName()), true);
        return 0;
    }
}

