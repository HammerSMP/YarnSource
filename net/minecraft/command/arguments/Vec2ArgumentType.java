/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.ImmutableStringReader
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

import com.mojang.brigadier.ImmutableStringReader;
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
import net.minecraft.command.arguments.CoordinateArgument;
import net.minecraft.command.arguments.DefaultPosArgument;
import net.minecraft.command.arguments.PosArgument;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class Vec2ArgumentType
implements ArgumentType<PosArgument> {
    private static final Collection<String> EXAMPLES = Arrays.asList("0 0", "~ ~", "0.1 -0.5", "~1 ~-2");
    public static final SimpleCommandExceptionType INCOMPLETE_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("argument.pos2d.incomplete"));
    private final boolean centerIntegers;

    public Vec2ArgumentType(boolean centerIntegers) {
        this.centerIntegers = centerIntegers;
    }

    public static Vec2ArgumentType vec2() {
        return new Vec2ArgumentType(true);
    }

    public static Vec2f getVec2(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        Vec3d lv = ((PosArgument)context.getArgument(name, PosArgument.class)).toAbsolutePos((ServerCommandSource)context.getSource());
        return new Vec2f((float)lv.x, (float)lv.z);
    }

    public PosArgument parse(StringReader stringReader) throws CommandSyntaxException {
        int i = stringReader.getCursor();
        if (!stringReader.canRead()) {
            throw INCOMPLETE_EXCEPTION.createWithContext((ImmutableStringReader)stringReader);
        }
        CoordinateArgument lv = CoordinateArgument.parse(stringReader, this.centerIntegers);
        if (!stringReader.canRead() || stringReader.peek() != ' ') {
            stringReader.setCursor(i);
            throw INCOMPLETE_EXCEPTION.createWithContext((ImmutableStringReader)stringReader);
        }
        stringReader.skip();
        CoordinateArgument lv2 = CoordinateArgument.parse(stringReader, this.centerIntegers);
        return new DefaultPosArgument(lv, new CoordinateArgument(true, 0.0), lv2);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (context.getSource() instanceof CommandSource) {
            Collection<CommandSource.RelativePosition> collection2;
            String string = builder.getRemaining();
            if (!string.isEmpty() && string.charAt(0) == '^') {
                Set<CommandSource.RelativePosition> collection = Collections.singleton(CommandSource.RelativePosition.ZERO_LOCAL);
            } else {
                collection2 = ((CommandSource)context.getSource()).getPositionSuggestions();
            }
            return CommandSource.suggestColumnPositions(string, collection2, builder, CommandManager.getCommandValidator(this::parse));
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

