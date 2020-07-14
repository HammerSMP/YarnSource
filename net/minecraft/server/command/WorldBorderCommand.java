/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.FloatArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Locale;
import net.minecraft.command.arguments.Vec2ArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.border.WorldBorder;

public class WorldBorderCommand {
    private static final SimpleCommandExceptionType CENTER_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.worldborder.center.failed"));
    private static final SimpleCommandExceptionType SET_FAILED_NO_CHANGE_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.worldborder.set.failed.nochange"));
    private static final SimpleCommandExceptionType SET_FAILED_SMALL_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.worldborder.set.failed.small."));
    private static final SimpleCommandExceptionType SET_FAILED_BIG_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.worldborder.set.failed.big."));
    private static final SimpleCommandExceptionType WARNING_TIME_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.worldborder.warning.time.failed"));
    private static final SimpleCommandExceptionType WARNING_DISTANCE_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.worldborder.warning.distance.failed"));
    private static final SimpleCommandExceptionType DAMAGE_BUFFER_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.worldborder.damage.buffer.failed"));
    private static final SimpleCommandExceptionType DAMAGE_AMOUNT_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.worldborder.damage.amount.failed"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("worldborder").requires(arg -> arg.hasPermissionLevel(2))).then(CommandManager.literal("add").then(((RequiredArgumentBuilder)CommandManager.argument("distance", FloatArgumentType.floatArg((float)-6.0E7f, (float)6.0E7f)).executes(commandContext -> WorldBorderCommand.executeSet((ServerCommandSource)commandContext.getSource(), ((ServerCommandSource)commandContext.getSource()).getWorld().getWorldBorder().getSize() + (double)FloatArgumentType.getFloat((CommandContext)commandContext, (String)"distance"), 0L))).then(CommandManager.argument("time", IntegerArgumentType.integer((int)0)).executes(commandContext -> WorldBorderCommand.executeSet((ServerCommandSource)commandContext.getSource(), ((ServerCommandSource)commandContext.getSource()).getWorld().getWorldBorder().getSize() + (double)FloatArgumentType.getFloat((CommandContext)commandContext, (String)"distance"), ((ServerCommandSource)commandContext.getSource()).getWorld().getWorldBorder().getTargetRemainingTime() + (long)IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"time") * 1000L)))))).then(CommandManager.literal("set").then(((RequiredArgumentBuilder)CommandManager.argument("distance", FloatArgumentType.floatArg((float)-6.0E7f, (float)6.0E7f)).executes(commandContext -> WorldBorderCommand.executeSet((ServerCommandSource)commandContext.getSource(), FloatArgumentType.getFloat((CommandContext)commandContext, (String)"distance"), 0L))).then(CommandManager.argument("time", IntegerArgumentType.integer((int)0)).executes(commandContext -> WorldBorderCommand.executeSet((ServerCommandSource)commandContext.getSource(), FloatArgumentType.getFloat((CommandContext)commandContext, (String)"distance"), (long)IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"time") * 1000L)))))).then(CommandManager.literal("center").then(CommandManager.argument("pos", Vec2ArgumentType.vec2()).executes(commandContext -> WorldBorderCommand.executeCenter((ServerCommandSource)commandContext.getSource(), Vec2ArgumentType.getVec2((CommandContext<ServerCommandSource>)commandContext, "pos")))))).then(((LiteralArgumentBuilder)CommandManager.literal("damage").then(CommandManager.literal("amount").then(CommandManager.argument("damagePerBlock", FloatArgumentType.floatArg((float)0.0f)).executes(commandContext -> WorldBorderCommand.executeDamage((ServerCommandSource)commandContext.getSource(), FloatArgumentType.getFloat((CommandContext)commandContext, (String)"damagePerBlock")))))).then(CommandManager.literal("buffer").then(CommandManager.argument("distance", FloatArgumentType.floatArg((float)0.0f)).executes(commandContext -> WorldBorderCommand.executeBuffer((ServerCommandSource)commandContext.getSource(), FloatArgumentType.getFloat((CommandContext)commandContext, (String)"distance"))))))).then(CommandManager.literal("get").executes(commandContext -> WorldBorderCommand.executeGet((ServerCommandSource)commandContext.getSource())))).then(((LiteralArgumentBuilder)CommandManager.literal("warning").then(CommandManager.literal("distance").then(CommandManager.argument("distance", IntegerArgumentType.integer((int)0)).executes(commandContext -> WorldBorderCommand.executeWarningDistance((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"distance")))))).then(CommandManager.literal("time").then(CommandManager.argument("time", IntegerArgumentType.integer((int)0)).executes(commandContext -> WorldBorderCommand.executeWarningTime((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"time")))))));
    }

    private static int executeBuffer(ServerCommandSource source, float distance) throws CommandSyntaxException {
        WorldBorder lv = source.getWorld().getWorldBorder();
        if (lv.getBuffer() == (double)distance) {
            throw DAMAGE_BUFFER_FAILED_EXCEPTION.create();
        }
        lv.setBuffer(distance);
        source.sendFeedback(new TranslatableText("commands.worldborder.damage.buffer.success", String.format(Locale.ROOT, "%.2f", Float.valueOf(distance))), true);
        return (int)distance;
    }

    private static int executeDamage(ServerCommandSource source, float damagePerBlock) throws CommandSyntaxException {
        WorldBorder lv = source.getWorld().getWorldBorder();
        if (lv.getDamagePerBlock() == (double)damagePerBlock) {
            throw DAMAGE_AMOUNT_FAILED_EXCEPTION.create();
        }
        lv.setDamagePerBlock(damagePerBlock);
        source.sendFeedback(new TranslatableText("commands.worldborder.damage.amount.success", String.format(Locale.ROOT, "%.2f", Float.valueOf(damagePerBlock))), true);
        return (int)damagePerBlock;
    }

    private static int executeWarningTime(ServerCommandSource source, int time) throws CommandSyntaxException {
        WorldBorder lv = source.getWorld().getWorldBorder();
        if (lv.getWarningTime() == time) {
            throw WARNING_TIME_FAILED_EXCEPTION.create();
        }
        lv.setWarningTime(time);
        source.sendFeedback(new TranslatableText("commands.worldborder.warning.time.success", time), true);
        return time;
    }

    private static int executeWarningDistance(ServerCommandSource source, int distance) throws CommandSyntaxException {
        WorldBorder lv = source.getWorld().getWorldBorder();
        if (lv.getWarningBlocks() == distance) {
            throw WARNING_DISTANCE_FAILED_EXCEPTION.create();
        }
        lv.setWarningBlocks(distance);
        source.sendFeedback(new TranslatableText("commands.worldborder.warning.distance.success", distance), true);
        return distance;
    }

    private static int executeGet(ServerCommandSource source) {
        double d = source.getWorld().getWorldBorder().getSize();
        source.sendFeedback(new TranslatableText("commands.worldborder.get", String.format(Locale.ROOT, "%.0f", d)), false);
        return MathHelper.floor(d + 0.5);
    }

    private static int executeCenter(ServerCommandSource source, Vec2f pos) throws CommandSyntaxException {
        WorldBorder lv = source.getWorld().getWorldBorder();
        if (lv.getCenterX() == (double)pos.x && lv.getCenterZ() == (double)pos.y) {
            throw CENTER_FAILED_EXCEPTION.create();
        }
        lv.setCenter(pos.x, pos.y);
        source.sendFeedback(new TranslatableText("commands.worldborder.center.success", String.format(Locale.ROOT, "%.2f", Float.valueOf(pos.x)), String.format("%.2f", Float.valueOf(pos.y))), true);
        return 0;
    }

    private static int executeSet(ServerCommandSource source, double distance, long time) throws CommandSyntaxException {
        WorldBorder lv = source.getWorld().getWorldBorder();
        double e = lv.getSize();
        if (e == distance) {
            throw SET_FAILED_NO_CHANGE_EXCEPTION.create();
        }
        if (distance < 1.0) {
            throw SET_FAILED_SMALL_EXCEPTION.create();
        }
        if (distance > 6.0E7) {
            throw SET_FAILED_BIG_EXCEPTION.create();
        }
        if (time > 0L) {
            lv.interpolateSize(e, distance, time);
            if (distance > e) {
                source.sendFeedback(new TranslatableText("commands.worldborder.set.grow", String.format(Locale.ROOT, "%.1f", distance), Long.toString(time / 1000L)), true);
            } else {
                source.sendFeedback(new TranslatableText("commands.worldborder.set.shrink", String.format(Locale.ROOT, "%.1f", distance), Long.toString(time / 1000L)), true);
            }
        } else {
            lv.setSize(distance);
            source.sendFeedback(new TranslatableText("commands.worldborder.set.immediate", String.format(Locale.ROOT, "%.1f", distance)), true);
        }
        return (int)(distance - e);
    }
}

