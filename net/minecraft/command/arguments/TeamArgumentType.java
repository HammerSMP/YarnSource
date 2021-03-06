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
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

public class TeamArgumentType
implements ArgumentType<String> {
    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "123");
    private static final DynamicCommandExceptionType UNKNOWN_TEAM_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("team.notFound", object));

    public static TeamArgumentType team() {
        return new TeamArgumentType();
    }

    public static Team getTeam(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        String string2 = (String)context.getArgument(name, String.class);
        ServerScoreboard lv = ((ServerCommandSource)context.getSource()).getMinecraftServer().getScoreboard();
        Team lv2 = lv.getTeam(string2);
        if (lv2 == null) {
            throw UNKNOWN_TEAM_EXCEPTION.create((Object)string2);
        }
        return lv2;
    }

    public String parse(StringReader stringReader) throws CommandSyntaxException {
        return stringReader.readUnquotedString();
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (context.getSource() instanceof CommandSource) {
            return CommandSource.suggestMatching(((CommandSource)context.getSource()).getTeamNames(), builder);
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

