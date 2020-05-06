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
    private static final SimpleCommandExceptionType SET_FAILED_NOCHANGE_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.worldborder.set.failed.nochange"));
    private static final SimpleCommandExceptionType SET_FAILED_SMALL_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.worldborder.set.failed.small."));
    private static final SimpleCommandExceptionType SET_FAILED_BIG_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.worldborder.set.failed.big."));
    private static final SimpleCommandExceptionType WARNING_TIME_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.worldborder.warning.time.failed"));
    private static final SimpleCommandExceptionType WARNING_DISTANCE_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.worldborder.warning.distance.failed"));
    private static final SimpleCommandExceptionType DAMAGE_BUFFER_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.worldborder.damage.buffer.failed"));
    private static final SimpleCommandExceptionType DAMAGE_AMOUNT_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.worldborder.damage.amount.failed"));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("worldborder").requires(arg -> arg.hasPermissionLevel(2))).then(CommandManager.literal("add").then(((RequiredArgumentBuilder)CommandManager.argument("distance", FloatArgumentType.floatArg((float)-6.0E7f, (float)6.0E7f)).executes(commandContext -> WorldBorderCommand.executeSet((ServerCommandSource)commandContext.getSource(), ((ServerCommandSource)commandContext.getSource()).getWorld().getWorldBorder().getSize() + (double)FloatArgumentType.getFloat((CommandContext)commandContext, (String)"distance"), 0L))).then(CommandManager.argument("time", IntegerArgumentType.integer((int)0)).executes(commandContext -> WorldBorderCommand.executeSet((ServerCommandSource)commandContext.getSource(), ((ServerCommandSource)commandContext.getSource()).getWorld().getWorldBorder().getSize() + (double)FloatArgumentType.getFloat((CommandContext)commandContext, (String)"distance"), ((ServerCommandSource)commandContext.getSource()).getWorld().getWorldBorder().getTargetRemainingTime() + (long)IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"time") * 1000L)))))).then(CommandManager.literal("set").then(((RequiredArgumentBuilder)CommandManager.argument("distance", FloatArgumentType.floatArg((float)-6.0E7f, (float)6.0E7f)).executes(commandContext -> WorldBorderCommand.executeSet((ServerCommandSource)commandContext.getSource(), FloatArgumentType.getFloat((CommandContext)commandContext, (String)"distance"), 0L))).then(CommandManager.argument("time", IntegerArgumentType.integer((int)0)).executes(commandContext -> WorldBorderCommand.executeSet((ServerCommandSource)commandContext.getSource(), FloatArgumentType.getFloat((CommandContext)commandContext, (String)"distance"), (long)IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"time") * 1000L)))))).then(CommandManager.literal("center").then(CommandManager.argument("pos", Vec2ArgumentType.vec2()).executes(commandContext -> WorldBorderCommand.executeCenter((ServerCommandSource)commandContext.getSource(), Vec2ArgumentType.getVec2((CommandContext<ServerCommandSource>)commandContext, "pos")))))).then(((LiteralArgumentBuilder)CommandManager.literal("damage").then(CommandManager.literal("amount").then(CommandManager.argument("damagePerBlock", FloatArgumentType.floatArg((float)0.0f)).executes(commandContext -> WorldBorderCommand.executeDamage((ServerCommandSource)commandContext.getSource(), FloatArgumentType.getFloat((CommandContext)commandContext, (String)"damagePerBlock")))))).then(CommandManager.literal("buffer").then(CommandManager.argument("distance", FloatArgumentType.floatArg((float)0.0f)).executes(commandContext -> WorldBorderCommand.executeBuffer((ServerCommandSource)commandContext.getSource(), FloatArgumentType.getFloat((CommandContext)commandContext, (String)"distance"))))))).then(CommandManager.literal("get").executes(commandContext -> WorldBorderCommand.executeGet((ServerCommandSource)commandContext.getSource())))).then(((LiteralArgumentBuilder)CommandManager.literal("warning").then(CommandManager.literal("distance").then(CommandManager.argument("distance", IntegerArgumentType.integer((int)0)).executes(commandContext -> WorldBorderCommand.executeWarningDistance((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"distance")))))).then(CommandManager.literal("time").then(CommandManager.argument("time", IntegerArgumentType.integer((int)0)).executes(commandContext -> WorldBorderCommand.executeWarningTime((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"time")))))));
    }

    private static int executeBuffer(ServerCommandSource arg, float f) throws CommandSyntaxException {
        WorldBorder lv = arg.getWorld().getWorldBorder();
        if (lv.getBuffer() == (double)f) {
            throw DAMAGE_BUFFER_FAILED_EXCEPTION.create();
        }
        lv.setBuffer(f);
        arg.sendFeedback(new TranslatableText("commands.worldborder.damage.buffer.success", String.format(Locale.ROOT, "%.2f", Float.valueOf(f))), true);
        return (int)f;
    }

    private static int executeDamage(ServerCommandSource arg, float f) throws CommandSyntaxException {
        WorldBorder lv = arg.getWorld().getWorldBorder();
        if (lv.getDamagePerBlock() == (double)f) {
            throw DAMAGE_AMOUNT_FAILED_EXCEPTION.create();
        }
        lv.setDamagePerBlock(f);
        arg.sendFeedback(new TranslatableText("commands.worldborder.damage.amount.success", String.format(Locale.ROOT, "%.2f", Float.valueOf(f))), true);
        return (int)f;
    }

    private static int executeWarningTime(ServerCommandSource arg, int i) throws CommandSyntaxException {
        WorldBorder lv = arg.getWorld().getWorldBorder();
        if (lv.getWarningTime() == i) {
            throw WARNING_TIME_FAILED_EXCEPTION.create();
        }
        lv.setWarningTime(i);
        arg.sendFeedback(new TranslatableText("commands.worldborder.warning.time.success", i), true);
        return i;
    }

    private static int executeWarningDistance(ServerCommandSource arg, int i) throws CommandSyntaxException {
        WorldBorder lv = arg.getWorld().getWorldBorder();
        if (lv.getWarningBlocks() == i) {
            throw WARNING_DISTANCE_FAILED_EXCEPTION.create();
        }
        lv.setWarningBlocks(i);
        arg.sendFeedback(new TranslatableText("commands.worldborder.warning.distance.success", i), true);
        return i;
    }

    private static int executeGet(ServerCommandSource arg) {
        double d = arg.getWorld().getWorldBorder().getSize();
        arg.sendFeedback(new TranslatableText("commands.worldborder.get", String.format(Locale.ROOT, "%.0f", d)), false);
        return MathHelper.floor(d + 0.5);
    }

    private static int executeCenter(ServerCommandSource arg, Vec2f arg2) throws CommandSyntaxException {
        WorldBorder lv = arg.getWorld().getWorldBorder();
        if (lv.getCenterX() == (double)arg2.x && lv.getCenterZ() == (double)arg2.y) {
            throw CENTER_FAILED_EXCEPTION.create();
        }
        lv.setCenter(arg2.x, arg2.y);
        arg.sendFeedback(new TranslatableText("commands.worldborder.center.success", String.format(Locale.ROOT, "%.2f", Float.valueOf(arg2.x)), String.format("%.2f", Float.valueOf(arg2.y))), true);
        return 0;
    }

    private static int executeSet(ServerCommandSource arg, double d, long l) throws CommandSyntaxException {
        WorldBorder lv = arg.getWorld().getWorldBorder();
        double e = lv.getSize();
        if (e == d) {
            throw SET_FAILED_NOCHANGE_EXCEPTION.create();
        }
        if (d < 1.0) {
            throw SET_FAILED_SMALL_EXCEPTION.create();
        }
        if (d > 6.0E7) {
            throw SET_FAILED_BIG_EXCEPTION.create();
        }
        if (l > 0L) {
            lv.interpolateSize(e, d, l);
            if (d > e) {
                arg.sendFeedback(new TranslatableText("commands.worldborder.set.grow", String.format(Locale.ROOT, "%.1f", d), Long.toString(l / 1000L)), true);
            } else {
                arg.sendFeedback(new TranslatableText("commands.worldborder.set.shrink", String.format(Locale.ROOT, "%.1f", d), Long.toString(l / 1000L)), true);
            }
        } else {
            lv.setSize(d);
            arg.sendFeedback(new TranslatableText("commands.worldborder.set.immediate", String.format(Locale.ROOT, "%.1f", d)), true);
        }
        return (int)(d - e);
    }
}

