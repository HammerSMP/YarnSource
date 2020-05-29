/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import net.minecraft.command.arguments.FunctionArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.text.TranslatableText;

public class FunctionCommand {
    public static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (commandContext, suggestionsBuilder) -> {
        CommandFunctionManager lv = ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getCommandFunctionManager();
        CommandSource.suggestIdentifiers(lv.method_29464(), suggestionsBuilder, "#");
        return CommandSource.suggestIdentifiers(lv.method_29463(), suggestionsBuilder);
    };

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("function").requires(arg -> arg.hasPermissionLevel(2))).then(CommandManager.argument("name", FunctionArgumentType.function()).suggests(SUGGESTION_PROVIDER).executes(commandContext -> FunctionCommand.execute((ServerCommandSource)commandContext.getSource(), FunctionArgumentType.getFunctions((CommandContext<ServerCommandSource>)commandContext, "name")))));
    }

    private static int execute(ServerCommandSource arg, Collection<CommandFunction> collection) {
        int i = 0;
        for (CommandFunction lv : collection) {
            i += arg.getMinecraftServer().getCommandFunctionManager().execute(lv, arg.withSilent().withMaxLevel(2));
        }
        if (collection.size() == 1) {
            arg.sendFeedback(new TranslatableText("commands.function.success.single", i, collection.iterator().next().getId()), true);
        } else {
            arg.sendFeedback(new TranslatableText("commands.function.success.multiple", i, collection.size()), true);
        }
        return i;
    }
}

