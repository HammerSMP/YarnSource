/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 */
package net.minecraft.server.command;

import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.HashSet;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;

public class TagCommand {
    private static final SimpleCommandExceptionType ADD_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.tag.add.failed"));
    private static final SimpleCommandExceptionType REMOVE_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.tag.remove.failed"));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("tag").requires(arg -> arg.hasPermissionLevel(2))).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.entities()).then(CommandManager.literal("add").then(CommandManager.argument("name", StringArgumentType.word()).executes(commandContext -> TagCommand.executeAdd((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"), StringArgumentType.getString((CommandContext)commandContext, (String)"name")))))).then(CommandManager.literal("remove").then(CommandManager.argument("name", StringArgumentType.word()).suggests((commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(TagCommand.getTags(EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets")), suggestionsBuilder)).executes(commandContext -> TagCommand.executeRemove((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"), StringArgumentType.getString((CommandContext)commandContext, (String)"name")))))).then(CommandManager.literal("list").executes(commandContext -> TagCommand.executeList((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"))))));
    }

    private static Collection<String> getTags(Collection<? extends Entity> collection) {
        HashSet set = Sets.newHashSet();
        for (Entity entity : collection) {
            set.addAll(entity.getScoreboardTags());
        }
        return set;
    }

    private static int executeAdd(ServerCommandSource arg, Collection<? extends Entity> collection, String string) throws CommandSyntaxException {
        int i = 0;
        for (Entity entity : collection) {
            if (!entity.addScoreboardTag(string)) continue;
            ++i;
        }
        if (i == 0) {
            throw ADD_FAILED_EXCEPTION.create();
        }
        if (collection.size() == 1) {
            arg.sendFeedback(new TranslatableText("commands.tag.add.success.single", string, collection.iterator().next().getDisplayName()), true);
        } else {
            arg.sendFeedback(new TranslatableText("commands.tag.add.success.multiple", string, collection.size()), true);
        }
        return i;
    }

    private static int executeRemove(ServerCommandSource arg, Collection<? extends Entity> collection, String string) throws CommandSyntaxException {
        int i = 0;
        for (Entity entity : collection) {
            if (!entity.removeScoreboardTag(string)) continue;
            ++i;
        }
        if (i == 0) {
            throw REMOVE_FAILED_EXCEPTION.create();
        }
        if (collection.size() == 1) {
            arg.sendFeedback(new TranslatableText("commands.tag.remove.success.single", string, collection.iterator().next().getDisplayName()), true);
        } else {
            arg.sendFeedback(new TranslatableText("commands.tag.remove.success.multiple", string, collection.size()), true);
        }
        return i;
    }

    private static int executeList(ServerCommandSource arg, Collection<? extends Entity> collection) {
        HashSet set = Sets.newHashSet();
        for (Entity entity : collection) {
            set.addAll(entity.getScoreboardTags());
        }
        if (collection.size() == 1) {
            Entity lv2 = collection.iterator().next();
            if (set.isEmpty()) {
                arg.sendFeedback(new TranslatableText("commands.tag.list.single.empty", lv2.getDisplayName()), false);
            } else {
                arg.sendFeedback(new TranslatableText("commands.tag.list.single.success", lv2.getDisplayName(), set.size(), Texts.joinOrdered(set)), false);
            }
        } else if (set.isEmpty()) {
            arg.sendFeedback(new TranslatableText("commands.tag.list.multiple.empty", collection.size()), false);
        } else {
            arg.sendFeedback(new TranslatableText("commands.tag.list.multiple.success", collection.size(), set.size(), Texts.joinOrdered(set)), false);
        }
        return set.size();
    }
}

