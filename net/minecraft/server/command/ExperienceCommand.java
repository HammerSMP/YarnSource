/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.tree.CommandNode
 *  com.mojang.brigadier.tree.LiteralCommandNode
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.ToIntFunction;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;

public class ExperienceCommand {
    private static final SimpleCommandExceptionType SET_POINT_INVALID_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.experience.set.points.invalid"));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        LiteralCommandNode literalCommandNode = commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("experience").requires(arg -> arg.hasPermissionLevel(2))).then(CommandManager.literal("add").then(CommandManager.argument("targets", EntityArgumentType.players()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("amount", IntegerArgumentType.integer()).executes(commandContext -> ExperienceCommand.executeAdd((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"amount"), Component.POINTS))).then(CommandManager.literal("points").executes(commandContext -> ExperienceCommand.executeAdd((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"amount"), Component.POINTS)))).then(CommandManager.literal("levels").executes(commandContext -> ExperienceCommand.executeAdd((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"amount"), Component.LEVELS))))))).then(CommandManager.literal("set").then(CommandManager.argument("targets", EntityArgumentType.players()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("amount", IntegerArgumentType.integer((int)0)).executes(commandContext -> ExperienceCommand.executeSet((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"amount"), Component.POINTS))).then(CommandManager.literal("points").executes(commandContext -> ExperienceCommand.executeSet((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"amount"), Component.POINTS)))).then(CommandManager.literal("levels").executes(commandContext -> ExperienceCommand.executeSet((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"amount"), Component.LEVELS))))))).then(CommandManager.literal("query").then(((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.player()).then(CommandManager.literal("points").executes(commandContext -> ExperienceCommand.executeQuery((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayer((CommandContext<ServerCommandSource>)commandContext, "targets"), Component.POINTS)))).then(CommandManager.literal("levels").executes(commandContext -> ExperienceCommand.executeQuery((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayer((CommandContext<ServerCommandSource>)commandContext, "targets"), Component.LEVELS))))));
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("xp").requires(arg -> arg.hasPermissionLevel(2))).redirect((CommandNode)literalCommandNode));
    }

    private static int executeQuery(ServerCommandSource arg, ServerPlayerEntity arg2, Component arg3) {
        int i = arg3.getter.applyAsInt(arg2);
        arg.sendFeedback(new TranslatableText("commands.experience.query." + arg3.name, arg2.getDisplayName(), i), false);
        return i;
    }

    private static int executeAdd(ServerCommandSource arg, Collection<? extends ServerPlayerEntity> collection, int i, Component arg2) {
        for (ServerPlayerEntity serverPlayerEntity : collection) {
            arg2.adder.accept(serverPlayerEntity, i);
        }
        if (collection.size() == 1) {
            arg.sendFeedback(new TranslatableText("commands.experience.add." + arg2.name + ".success.single", i, collection.iterator().next().getDisplayName()), true);
        } else {
            arg.sendFeedback(new TranslatableText("commands.experience.add." + arg2.name + ".success.multiple", i, collection.size()), true);
        }
        return collection.size();
    }

    private static int executeSet(ServerCommandSource arg, Collection<? extends ServerPlayerEntity> collection, int i, Component arg2) throws CommandSyntaxException {
        int j = 0;
        for (ServerPlayerEntity serverPlayerEntity : collection) {
            if (!arg2.setter.test(serverPlayerEntity, i)) continue;
            ++j;
        }
        if (j == 0) {
            throw SET_POINT_INVALID_EXCEPTION.create();
        }
        if (collection.size() == 1) {
            arg.sendFeedback(new TranslatableText("commands.experience.set." + arg2.name + ".success.single", i, collection.iterator().next().getDisplayName()), true);
        } else {
            arg.sendFeedback(new TranslatableText("commands.experience.set." + arg2.name + ".success.multiple", i, collection.size()), true);
        }
        return collection.size();
    }

    static enum Component {
        POINTS("points", PlayerEntity::addExperience, (arg, integer) -> {
            if (integer >= arg.getNextLevelExperience()) {
                return false;
            }
            arg.setExperiencePoints((int)integer);
            return true;
        }, arg -> MathHelper.floor(arg.experienceProgress * (float)arg.getNextLevelExperience())),
        LEVELS("levels", ServerPlayerEntity::addExperienceLevels, (arg, integer) -> {
            arg.setExperienceLevel((int)integer);
            return true;
        }, arg -> arg.experienceLevel);

        public final BiConsumer<ServerPlayerEntity, Integer> adder;
        public final BiPredicate<ServerPlayerEntity, Integer> setter;
        public final String name;
        private final ToIntFunction<ServerPlayerEntity> getter;

        private Component(String string2, BiConsumer<ServerPlayerEntity, Integer> biConsumer, BiPredicate<ServerPlayerEntity, Integer> biPredicate, ToIntFunction<ServerPlayerEntity> toIntFunction) {
            this.adder = biConsumer;
            this.name = string2;
            this.setter = biPredicate;
            this.getter = toIntFunction;
        }
    }
}

