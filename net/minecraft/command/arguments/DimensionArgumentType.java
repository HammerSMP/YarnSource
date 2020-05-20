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
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.class_5321;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;

public class DimensionArgumentType
implements ArgumentType<Identifier> {
    private static final Collection<String> EXAMPLES = Stream.of(DimensionType.field_24753, DimensionType.field_24754).map(arg -> arg.method_29177().toString()).collect(Collectors.toList());
    private static final DynamicCommandExceptionType INVALID_DIMENSION_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("argument.dimension.invalid", object));

    public Identifier parse(StringReader stringReader) throws CommandSyntaxException {
        return Identifier.fromCommandInput(stringReader);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        if (commandContext.getSource() instanceof CommandSource) {
            return CommandSource.suggestIdentifiers(((CommandSource)commandContext.getSource()).method_29038().method_29116().getIds().stream(), suggestionsBuilder);
        }
        return Suggestions.empty();
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public static DimensionArgumentType dimension() {
        return new DimensionArgumentType();
    }

    public static class_5321<DimensionType> getDimensionArgument(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
        Identifier lv = (Identifier)commandContext.getArgument(string, Identifier.class);
        class_5321<DimensionType> lv2 = class_5321.method_29179(Registry.DIMENSION_TYPE_KEY, lv);
        if (!((ServerCommandSource)commandContext.getSource()).getMinecraftServer().method_29174().method_29116().method_29112(lv2)) {
            throw INVALID_DIMENSION_EXCEPTION.create((Object)lv);
        }
        return lv2;
    }

    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }
}

