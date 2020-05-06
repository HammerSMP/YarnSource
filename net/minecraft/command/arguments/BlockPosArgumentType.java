/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 */
package net.minecraft.command.arguments;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.arguments.DefaultPosArgument;
import net.minecraft.command.arguments.LookingPosArgument;
import net.minecraft.command.arguments.PosArgument;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class BlockPosArgumentType
implements ArgumentType<PosArgument> {
    private static final Collection<String> EXAMPLES = Arrays.asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "~0.5 ~1 ~-5");
    public static final SimpleCommandExceptionType UNLOADED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("argument.pos.unloaded"));
    public static final SimpleCommandExceptionType OUT_OF_WORLD_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("argument.pos.outofworld"));

    public static BlockPosArgumentType blockPos() {
        return new BlockPosArgumentType();
    }

    public static BlockPos getLoadedBlockPos(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
        BlockPos lv = ((PosArgument)commandContext.getArgument(string, PosArgument.class)).toAbsoluteBlockPos((ServerCommandSource)commandContext.getSource());
        if (!((ServerCommandSource)commandContext.getSource()).getWorld().isChunkLoaded(lv)) {
            throw UNLOADED_EXCEPTION.create();
        }
        ((ServerCommandSource)commandContext.getSource()).getWorld();
        if (!ServerWorld.method_24794(lv)) {
            throw OUT_OF_WORLD_EXCEPTION.create();
        }
        return lv;
    }

    public static BlockPos getBlockPos(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
        return ((PosArgument)commandContext.getArgument(string, PosArgument.class)).toAbsoluteBlockPos((ServerCommandSource)commandContext.getSource());
    }

    public PosArgument parse(StringReader stringReader) throws CommandSyntaxException {
        if (stringReader.canRead() && stringReader.peek() == '^') {
            return LookingPosArgument.parse(stringReader);
        }
        return DefaultPosArgument.parse(stringReader);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        if (commandContext.getSource() instanceof CommandSource) {
            Collection<CommandSource.RelativePosition> collection2;
            String string = suggestionsBuilder.getRemaining();
            if (!string.isEmpty() && string.charAt(0) == '^') {
                Set<CommandSource.RelativePosition> collection = Collections.singleton(CommandSource.RelativePosition.ZERO_LOCAL);
            } else {
                collection2 = ((CommandSource)commandContext.getSource()).getBlockPositionSuggestions();
            }
            return CommandSource.suggestPositions(string, collection2, suggestionsBuilder, CommandManager.getCommandValidator(this::parse));
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

