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

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode literalCommandNode = dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("experience").requires(arg -> arg.hasPermissionLevel(2))).then(CommandManager.literal("add").then(CommandManager.argument("targets", EntityArgumentType.players()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("amount", IntegerArgumentType.integer()).executes(commandContext -> ExperienceCommand.executeAdd((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"amount"), Component.POINTS))).then(CommandManager.literal("points").executes(commandContext -> ExperienceCommand.executeAdd((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"amount"), Component.POINTS)))).then(CommandManager.literal("levels").executes(commandContext -> ExperienceCommand.executeAdd((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"amount"), Component.LEVELS))))))).then(CommandManager.literal("set").then(CommandManager.argument("targets", EntityArgumentType.players()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("amount", IntegerArgumentType.integer((int)0)).executes(commandContext -> ExperienceCommand.executeSet((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"amount"), Component.POINTS))).then(CommandManager.literal("points").executes(commandContext -> ExperienceCommand.executeSet((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"amount"), Component.POINTS)))).then(CommandManager.literal("levels").executes(commandContext -> ExperienceCommand.executeSet((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"amount"), Component.LEVELS))))))).then(CommandManager.literal("query").then(((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.player()).then(CommandManager.literal("points").executes(commandContext -> ExperienceCommand.executeQuery((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayer((CommandContext<ServerCommandSource>)commandContext, "targets"), Component.POINTS)))).then(CommandManager.literal("levels").executes(commandContext -> ExperienceCommand.executeQuery((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayer((CommandContext<ServerCommandSource>)commandContext, "targets"), Component.LEVELS))))));
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("xp").requires(arg -> arg.hasPermissionLevel(2))).redirect((CommandNode)literalCommandNode));
    }

    private static int executeQuery(ServerCommandSource source, ServerPlayerEntity player, Component component) {
        int i = component.getter.applyAsInt(player);
        source.sendFeedback(new TranslatableText("commands.experience.query." + component.name, player.getDisplayName(), i), false);
        return i;
    }

    private static int executeAdd(ServerCommandSource source, Collection<? extends ServerPlayerEntity> targets, int amount, Component component) {
        for (ServerPlayerEntity serverPlayerEntity : targets) {
            component.adder.accept(serverPlayerEntity, amount);
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableText("commands.experience.add." + component.name + ".success.single", amount, targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.experience.add." + component.name + ".success.multiple", amount, targets.size()), true);
        }
        return targets.size();
    }

    private static int executeSet(ServerCommandSource source, Collection<? extends ServerPlayerEntity> targets, int amount, Component component) throws CommandSyntaxException {
        int j = 0;
        for (ServerPlayerEntity serverPlayerEntity : targets) {
            if (!component.setter.test(serverPlayerEntity, amount)) continue;
            ++j;
        }
        if (j == 0) {
            throw SET_POINT_INVALID_EXCEPTION.create();
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableText("commands.experience.set." + component.name + ".success.single", amount, targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.experience.set." + component.name + ".success.multiple", amount, targets.size()), true);
        }
        return targets.size();
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

        private Component(String name, BiConsumer<ServerPlayerEntity, Integer> adder, BiPredicate<ServerPlayerEntity, Integer> setter, ToIntFunction<ServerPlayerEntity> getter) {
            this.adder = adder;
            this.name = name;
            this.setter = setter;
            this.getter = getter;
        }
    }
}

