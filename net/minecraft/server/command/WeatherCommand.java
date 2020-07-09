/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

public class WeatherCommand {
    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("weather").requires(arg -> arg.hasPermissionLevel(2))).then(((LiteralArgumentBuilder)CommandManager.literal("clear").executes(commandContext -> WeatherCommand.executeClear((ServerCommandSource)commandContext.getSource(), 6000))).then(CommandManager.argument("duration", IntegerArgumentType.integer((int)0, (int)1000000)).executes(commandContext -> WeatherCommand.executeClear((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"duration") * 20))))).then(((LiteralArgumentBuilder)CommandManager.literal("rain").executes(commandContext -> WeatherCommand.executeRain((ServerCommandSource)commandContext.getSource(), 6000))).then(CommandManager.argument("duration", IntegerArgumentType.integer((int)0, (int)1000000)).executes(commandContext -> WeatherCommand.executeRain((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"duration") * 20))))).then(((LiteralArgumentBuilder)CommandManager.literal("thunder").executes(commandContext -> WeatherCommand.executeThunder((ServerCommandSource)commandContext.getSource(), 6000))).then(CommandManager.argument("duration", IntegerArgumentType.integer((int)0, (int)1000000)).executes(commandContext -> WeatherCommand.executeThunder((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"duration") * 20)))));
    }

    private static int executeClear(ServerCommandSource arg, int i) {
        arg.getWorld().setWeather(i, 0, false, false);
        arg.sendFeedback(new TranslatableText("commands.weather.set.clear"), true);
        return i;
    }

    private static int executeRain(ServerCommandSource arg, int i) {
        arg.getWorld().setWeather(0, i, true, false);
        arg.sendFeedback(new TranslatableText("commands.weather.set.rain"), true);
        return i;
    }

    private static int executeThunder(ServerCommandSource arg, int i) {
        arg.getWorld().setWeather(0, i, true, true);
        arg.sendFeedback(new TranslatableText("commands.weather.set.thunder"), true);
        return i;
    }
}

