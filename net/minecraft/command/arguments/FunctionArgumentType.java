/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.datafixers.util.Either
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.tag.Tag;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class FunctionArgumentType
implements ArgumentType<FunctionArgument> {
    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "#foo");
    private static final DynamicCommandExceptionType UNKNOWN_FUNCTION_TAG_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("arguments.function.tag.unknown", object));
    private static final DynamicCommandExceptionType UNKNOWN_FUNCTION_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("arguments.function.unknown", object));

    public static FunctionArgumentType function() {
        return new FunctionArgumentType();
    }

    public FunctionArgument parse(StringReader stringReader) throws CommandSyntaxException {
        if (stringReader.canRead() && stringReader.peek() == '#') {
            stringReader.skip();
            final Identifier lv = Identifier.fromCommandInput(stringReader);
            return new FunctionArgument(){

                @Override
                public Collection<CommandFunction> getFunctions(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
                    Tag lv2 = FunctionArgumentType.getFunctionTag((CommandContext<ServerCommandSource>)commandContext, lv);
                    return lv2.values();
                }

                @Override
                public Pair<Identifier, Either<CommandFunction, Tag<CommandFunction>>> getFunctionOrTag(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
                    return Pair.of((Object)lv, (Object)Either.right((Object)FunctionArgumentType.getFunctionTag((CommandContext<ServerCommandSource>)commandContext, lv)));
                }
            };
        }
        final Identifier lv2 = Identifier.fromCommandInput(stringReader);
        return new FunctionArgument(){

            @Override
            public Collection<CommandFunction> getFunctions(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
                return Collections.singleton(FunctionArgumentType.getFunction((CommandContext<ServerCommandSource>)commandContext, lv2));
            }

            @Override
            public Pair<Identifier, Either<CommandFunction, Tag<CommandFunction>>> getFunctionOrTag(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
                return Pair.of((Object)lv2, (Object)Either.left((Object)FunctionArgumentType.getFunction((CommandContext<ServerCommandSource>)commandContext, lv2)));
            }
        };
    }

    private static CommandFunction getFunction(CommandContext<ServerCommandSource> commandContext, Identifier arg) throws CommandSyntaxException {
        return ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getCommandFunctionManager().getFunction(arg).orElseThrow(() -> UNKNOWN_FUNCTION_EXCEPTION.create((Object)arg.toString()));
    }

    private static Tag<CommandFunction> getFunctionTag(CommandContext<ServerCommandSource> commandContext, Identifier arg) throws CommandSyntaxException {
        Tag<CommandFunction> lv = ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getCommandFunctionManager().getTags().get(arg);
        if (lv == null) {
            throw UNKNOWN_FUNCTION_TAG_EXCEPTION.create((Object)arg.toString());
        }
        return lv;
    }

    public static Collection<CommandFunction> getFunctions(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
        return ((FunctionArgument)commandContext.getArgument(string, FunctionArgument.class)).getFunctions(commandContext);
    }

    public static Pair<Identifier, Either<CommandFunction, Tag<CommandFunction>>> getFunctionOrTag(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
        return ((FunctionArgument)commandContext.getArgument(string, FunctionArgument.class)).getFunctionOrTag(commandContext);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }

    public static interface FunctionArgument {
        public Collection<CommandFunction> getFunctions(CommandContext<ServerCommandSource> var1) throws CommandSyntaxException;

        public Pair<Identifier, Either<CommandFunction, Tag<CommandFunction>>> getFunctionOrTag(CommandContext<ServerCommandSource> var1) throws CommandSyntaxException;
    }
}

