/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.BoolArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  javax.annotation.Nullable
 */
package net.minecraft.server.command;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.MobEffectArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

public class EffectCommand {
    private static final SimpleCommandExceptionType GIVE_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.effect.give.failed"));
    private static final SimpleCommandExceptionType CLEAR_EVERYTHING_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.effect.clear.everything.failed"));
    private static final SimpleCommandExceptionType CLEAR_SPECIFIC_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.effect.clear.specific.failed"));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("effect").requires(arg -> arg.hasPermissionLevel(2))).then(((LiteralArgumentBuilder)CommandManager.literal("clear").executes(commandContext -> EffectCommand.executeClear((ServerCommandSource)commandContext.getSource(), (Collection<? extends Entity>)ImmutableList.of((Object)((ServerCommandSource)commandContext.getSource()).getEntityOrThrow())))).then(((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.entities()).executes(commandContext -> EffectCommand.executeClear((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets")))).then(CommandManager.argument("effect", MobEffectArgumentType.mobEffect()).executes(commandContext -> EffectCommand.executeClear((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"), MobEffectArgumentType.getMobEffect((CommandContext<ServerCommandSource>)commandContext, "effect"))))))).then(CommandManager.literal("give").then(CommandManager.argument("targets", EntityArgumentType.entities()).then(((RequiredArgumentBuilder)CommandManager.argument("effect", MobEffectArgumentType.mobEffect()).executes(commandContext -> EffectCommand.executeGive((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"), MobEffectArgumentType.getMobEffect((CommandContext<ServerCommandSource>)commandContext, "effect"), null, 0, true))).then(((RequiredArgumentBuilder)CommandManager.argument("seconds", IntegerArgumentType.integer((int)1, (int)1000000)).executes(commandContext -> EffectCommand.executeGive((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"), MobEffectArgumentType.getMobEffect((CommandContext<ServerCommandSource>)commandContext, "effect"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"seconds"), 0, true))).then(((RequiredArgumentBuilder)CommandManager.argument("amplifier", IntegerArgumentType.integer((int)0, (int)255)).executes(commandContext -> EffectCommand.executeGive((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"), MobEffectArgumentType.getMobEffect((CommandContext<ServerCommandSource>)commandContext, "effect"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"seconds"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"amplifier"), true))).then(CommandManager.argument("hideParticles", BoolArgumentType.bool()).executes(commandContext -> EffectCommand.executeGive((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"), MobEffectArgumentType.getMobEffect((CommandContext<ServerCommandSource>)commandContext, "effect"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"seconds"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"amplifier"), !BoolArgumentType.getBool((CommandContext)commandContext, (String)"hideParticles"))))))))));
    }

    private static int executeGive(ServerCommandSource arg, Collection<? extends Entity> collection, StatusEffect arg2, @Nullable Integer integer, int i, boolean bl) throws CommandSyntaxException {
        int n;
        int j = 0;
        if (integer != null) {
            if (arg2.isInstant()) {
                int k = integer;
            } else {
                int l = integer * 20;
            }
        } else if (arg2.isInstant()) {
            boolean m = true;
        } else {
            n = 600;
        }
        for (Entity entity : collection) {
            StatusEffectInstance lv2;
            if (!(entity instanceof LivingEntity) || !((LivingEntity)entity).addStatusEffect(lv2 = new StatusEffectInstance(arg2, n, i, false, bl))) continue;
            ++j;
        }
        if (j == 0) {
            throw GIVE_FAILED_EXCEPTION.create();
        }
        if (collection.size() == 1) {
            arg.sendFeedback(new TranslatableText("commands.effect.give.success.single", arg2.getName(), collection.iterator().next().getDisplayName(), n / 20), true);
        } else {
            arg.sendFeedback(new TranslatableText("commands.effect.give.success.multiple", arg2.getName(), collection.size(), n / 20), true);
        }
        return j;
    }

    private static int executeClear(ServerCommandSource arg, Collection<? extends Entity> collection) throws CommandSyntaxException {
        int i = 0;
        for (Entity entity : collection) {
            if (!(entity instanceof LivingEntity) || !((LivingEntity)entity).clearStatusEffects()) continue;
            ++i;
        }
        if (i == 0) {
            throw CLEAR_EVERYTHING_FAILED_EXCEPTION.create();
        }
        if (collection.size() == 1) {
            arg.sendFeedback(new TranslatableText("commands.effect.clear.everything.success.single", collection.iterator().next().getDisplayName()), true);
        } else {
            arg.sendFeedback(new TranslatableText("commands.effect.clear.everything.success.multiple", collection.size()), true);
        }
        return i;
    }

    private static int executeClear(ServerCommandSource arg, Collection<? extends Entity> collection, StatusEffect arg2) throws CommandSyntaxException {
        int i = 0;
        for (Entity entity : collection) {
            if (!(entity instanceof LivingEntity) || !((LivingEntity)entity).removeStatusEffect(arg2)) continue;
            ++i;
        }
        if (i == 0) {
            throw CLEAR_SPECIFIC_FAILED_EXCEPTION.create();
        }
        if (collection.size() == 1) {
            arg.sendFeedback(new TranslatableText("commands.effect.clear.specific.success.single", arg2.getName(), collection.iterator().next().getDisplayName()), true);
        } else {
            arg.sendFeedback(new TranslatableText("commands.effect.clear.specific.success.multiple", arg2.getName(), collection.size()), true);
        }
        return i;
    }
}

