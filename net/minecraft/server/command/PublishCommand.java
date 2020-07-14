/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

public class PublishCommand {
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.publish.failed"));
    private static final DynamicCommandExceptionType ALREADY_PUBLISHED_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.publish.alreadyPublished", object));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("publish").requires(arg -> arg.hasPermissionLevel(4))).executes(commandContext -> PublishCommand.execute((ServerCommandSource)commandContext.getSource(), NetworkUtils.findLocalPort()))).then(CommandManager.argument("port", IntegerArgumentType.integer((int)0, (int)65535)).executes(commandContext -> PublishCommand.execute((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"port")))));
    }

    private static int execute(ServerCommandSource source, int port) throws CommandSyntaxException {
        if (source.getMinecraftServer().isRemote()) {
            throw ALREADY_PUBLISHED_EXCEPTION.create((Object)source.getMinecraftServer().getServerPort());
        }
        if (!source.getMinecraftServer().openToLan(source.getMinecraftServer().getDefaultGameMode(), false, port)) {
            throw FAILED_EXCEPTION.create();
        }
        source.sendFeedback(new TranslatableText("commands.publish.success", port), true);
        return port;
    }
}

