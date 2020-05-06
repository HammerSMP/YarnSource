/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 */
package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

public class ObjectiveArgumentType
implements ArgumentType<String> {
    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "*", "012");
    private static final DynamicCommandExceptionType UNKNOWN_OBJECTIVE_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("arguments.objective.notFound", object));
    private static final DynamicCommandExceptionType READONLY_OBJECTIVE_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("arguments.objective.readonly", object));
    public static final DynamicCommandExceptionType LONG_NAME_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.scoreboard.objectives.add.longName", object));

    public static ObjectiveArgumentType objective() {
        return new ObjectiveArgumentType();
    }

    public static ScoreboardObjective getObjective(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
        String string2 = (String)commandContext.getArgument(string, String.class);
        ServerScoreboard lv = ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getScoreboard();
        ScoreboardObjective lv2 = lv.getNullableObjective(string2);
        if (lv2 == null) {
            throw UNKNOWN_OBJECTIVE_EXCEPTION.create((Object)string2);
        }
        return lv2;
    }

    public static ScoreboardObjective getWritableObjective(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
        ScoreboardObjective lv = ObjectiveArgumentType.getObjective(commandContext, string);
        if (lv.getCriterion().isReadOnly()) {
            throw READONLY_OBJECTIVE_EXCEPTION.create((Object)lv.getName());
        }
        return lv;
    }

    public String parse(StringReader stringReader) throws CommandSyntaxException {
        String string = stringReader.readUnquotedString();
        if (string.length() > 16) {
            throw LONG_NAME_EXCEPTION.create((Object)16);
        }
        return string;
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        if (commandContext.getSource() instanceof ServerCommandSource) {
            return CommandSource.suggestMatching(((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getScoreboard().getObjectiveNames(), suggestionsBuilder);
        }
        if (commandContext.getSource() instanceof CommandSource) {
            CommandSource lv = (CommandSource)commandContext.getSource();
            return lv.getCompletions(commandContext, suggestionsBuilder);
        }
        return Suggestions.empty();
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }
}

