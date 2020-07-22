/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  com.mojang.datafixers.util.Either
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import net.minecraft.command.argument.FunctionArgumentType;
import net.minecraft.command.argument.TimeArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.FunctionCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.tag.Tag;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.world.timer.FunctionTagTimerCallback;
import net.minecraft.world.timer.FunctionTimerCallback;
import net.minecraft.world.timer.Timer;

public class ScheduleCommand {
    private static final SimpleCommandExceptionType SAME_TICK_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.schedule.same_tick"));
    private static final DynamicCommandExceptionType CLEARED_FAILURE_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.schedule.cleared.failure", object));
    private static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getSaveProperties().getMainWorldProperties().getScheduledEvents().method_22592(), suggestionsBuilder);

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("schedule").requires(arg -> arg.hasPermissionLevel(2))).then(CommandManager.literal("function").then(CommandManager.argument("function", FunctionArgumentType.function()).suggests(FunctionCommand.SUGGESTION_PROVIDER).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("time", TimeArgumentType.time()).executes(commandContext -> ScheduleCommand.execute((ServerCommandSource)commandContext.getSource(), FunctionArgumentType.getFunctionOrTag((CommandContext<ServerCommandSource>)commandContext, "function"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"time"), true))).then(CommandManager.literal("append").executes(commandContext -> ScheduleCommand.execute((ServerCommandSource)commandContext.getSource(), FunctionArgumentType.getFunctionOrTag((CommandContext<ServerCommandSource>)commandContext, "function"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"time"), false)))).then(CommandManager.literal("replace").executes(commandContext -> ScheduleCommand.execute((ServerCommandSource)commandContext.getSource(), FunctionArgumentType.getFunctionOrTag((CommandContext<ServerCommandSource>)commandContext, "function"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"time"), true))))))).then(CommandManager.literal("clear").then(CommandManager.argument("function", StringArgumentType.greedyString()).suggests(SUGGESTION_PROVIDER).executes(commandContext -> ScheduleCommand.method_22833((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString((CommandContext)commandContext, (String)"function"))))));
    }

    private static int execute(ServerCommandSource source, Pair<Identifier, Either<CommandFunction, Tag<CommandFunction>>> pair, int i, boolean bl) throws CommandSyntaxException {
        if (i == 0) {
            throw SAME_TICK_EXCEPTION.create();
        }
        long l = source.getWorld().getTime() + (long)i;
        Identifier lv = (Identifier)pair.getFirst();
        Timer<MinecraftServer> lv2 = source.getMinecraftServer().getSaveProperties().getMainWorldProperties().getScheduledEvents();
        ((Either)pair.getSecond()).ifLeft(arg4 -> {
            String string = lv.toString();
            if (bl) {
                lv2.method_22593(string);
            }
            lv2.setEvent(string, l, new FunctionTimerCallback(lv));
            source.sendFeedback(new TranslatableText("commands.schedule.created.function", lv, i, l), true);
        }).ifRight(arg4 -> {
            String string = "#" + lv.toString();
            if (bl) {
                lv2.method_22593(string);
            }
            lv2.setEvent(string, l, new FunctionTagTimerCallback(lv));
            source.sendFeedback(new TranslatableText("commands.schedule.created.tag", lv, i, l), true);
        });
        return (int)Math.floorMod(l, Integer.MAX_VALUE);
    }

    private static int method_22833(ServerCommandSource arg, String string) throws CommandSyntaxException {
        int i = arg.getMinecraftServer().getSaveProperties().getMainWorldProperties().getScheduledEvents().method_22593(string);
        if (i == 0) {
            throw CLEARED_FAILURE_EXCEPTION.create((Object)string);
        }
        arg.sendFeedback(new TranslatableText("commands.schedule.cleared.success", i, string), true);
        return i;
    }
}

