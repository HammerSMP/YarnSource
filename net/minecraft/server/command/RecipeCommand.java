/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.IdentifierArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

public class RecipeCommand {
    private static final SimpleCommandExceptionType GIVE_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.recipe.give.failed"));
    private static final SimpleCommandExceptionType TAKE_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.recipe.take.failed"));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("recipe").requires(arg -> arg.hasPermissionLevel(2))).then(CommandManager.literal("give").then(((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.players()).then(CommandManager.argument("recipe", IdentifierArgumentType.identifier()).suggests(SuggestionProviders.ALL_RECIPES).executes(commandContext -> RecipeCommand.executeGive((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), Collections.singleton(IdentifierArgumentType.getRecipeArgument((CommandContext<ServerCommandSource>)commandContext, "recipe")))))).then(CommandManager.literal("*").executes(commandContext -> RecipeCommand.executeGive((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getRecipeManager().values())))))).then(CommandManager.literal("take").then(((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.players()).then(CommandManager.argument("recipe", IdentifierArgumentType.identifier()).suggests(SuggestionProviders.ALL_RECIPES).executes(commandContext -> RecipeCommand.executeTake((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), Collections.singleton(IdentifierArgumentType.getRecipeArgument((CommandContext<ServerCommandSource>)commandContext, "recipe")))))).then(CommandManager.literal("*").executes(commandContext -> RecipeCommand.executeTake((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getRecipeManager().values()))))));
    }

    private static int executeGive(ServerCommandSource arg, Collection<ServerPlayerEntity> collection, Collection<Recipe<?>> collection2) throws CommandSyntaxException {
        int i = 0;
        for (ServerPlayerEntity lv : collection) {
            i += lv.unlockRecipes(collection2);
        }
        if (i == 0) {
            throw GIVE_FAILED_EXCEPTION.create();
        }
        if (collection.size() == 1) {
            arg.sendFeedback(new TranslatableText("commands.recipe.give.success.single", collection2.size(), collection.iterator().next().getDisplayName()), true);
        } else {
            arg.sendFeedback(new TranslatableText("commands.recipe.give.success.multiple", collection2.size(), collection.size()), true);
        }
        return i;
    }

    private static int executeTake(ServerCommandSource arg, Collection<ServerPlayerEntity> collection, Collection<Recipe<?>> collection2) throws CommandSyntaxException {
        int i = 0;
        for (ServerPlayerEntity lv : collection) {
            i += lv.lockRecipes(collection2);
        }
        if (i == 0) {
            throw TAKE_FAILED_EXCEPTION.create();
        }
        if (collection.size() == 1) {
            arg.sendFeedback(new TranslatableText("commands.recipe.take.success.single", collection2.size(), collection.iterator().next().getDisplayName()), true);
        } else {
            arg.sendFeedback(new TranslatableText("commands.recipe.take.success.multiple", collection2.size(), collection.size()), true);
        }
        return i;
    }
}

