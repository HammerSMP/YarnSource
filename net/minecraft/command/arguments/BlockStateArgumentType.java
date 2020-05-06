/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 */
package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.arguments.BlockArgumentParser;
import net.minecraft.command.arguments.BlockStateArgument;
import net.minecraft.server.command.ServerCommandSource;

public class BlockStateArgumentType
implements ArgumentType<BlockStateArgument> {
    private static final Collection<String> EXAMPLES = Arrays.asList("stone", "minecraft:stone", "stone[foo=bar]", "foo{bar=baz}");

    public static BlockStateArgumentType blockState() {
        return new BlockStateArgumentType();
    }

    public BlockStateArgument parse(StringReader stringReader) throws CommandSyntaxException {
        BlockArgumentParser lv = new BlockArgumentParser(stringReader, false).parse(true);
        return new BlockStateArgument(lv.getBlockState(), lv.getBlockProperties().keySet(), lv.getNbtData());
    }

    public static BlockStateArgument getBlockState(CommandContext<ServerCommandSource> commandContext, String string) {
        return (BlockStateArgument)commandContext.getArgument(string, BlockStateArgument.class);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        StringReader stringReader = new StringReader(suggestionsBuilder.getInput());
        stringReader.setCursor(suggestionsBuilder.getStart());
        BlockArgumentParser lv = new BlockArgumentParser(stringReader, false);
        try {
            lv.parse(true);
        }
        catch (CommandSyntaxException commandSyntaxException) {
            // empty catch block
        }
        return lv.getSuggestions(suggestionsBuilder);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }
}

