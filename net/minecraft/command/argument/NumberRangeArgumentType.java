/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 */
package net.minecraft.command.argument;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.predicate.NumberRange;
import net.minecraft.server.command.ServerCommandSource;

public interface NumberRangeArgumentType<T extends NumberRange<?>>
extends ArgumentType<T> {
    public static IntRangeArgumentType numberRange() {
        return new IntRangeArgumentType();
    }

    public static abstract class NumberSerializer<T extends NumberRangeArgumentType<?>>
    implements ArgumentSerializer<T> {
        @Override
        public void toPacket(T arg, PacketByteBuf arg2) {
        }

        @Override
        public void toJson(T arg, JsonObject jsonObject) {
        }
    }

    public static class FloatRangeArgumentType
    implements NumberRangeArgumentType<NumberRange.FloatRange> {
        private static final Collection<String> EXAMPLES = Arrays.asList("0..5.2", "0", "-5.4", "-100.76..", "..100");

        public NumberRange.FloatRange parse(StringReader stringReader) throws CommandSyntaxException {
            return NumberRange.FloatRange.parse(stringReader);
        }

        public Collection<String> getExamples() {
            return EXAMPLES;
        }

        public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
            return this.parse(stringReader);
        }

        public static class Serializer
        extends NumberSerializer<FloatRangeArgumentType> {
            @Override
            public FloatRangeArgumentType fromPacket(PacketByteBuf arg) {
                return new FloatRangeArgumentType();
            }

            @Override
            public /* synthetic */ ArgumentType fromPacket(PacketByteBuf arg) {
                return this.fromPacket(arg);
            }
        }
    }

    public static class IntRangeArgumentType
    implements NumberRangeArgumentType<NumberRange.IntRange> {
        private static final Collection<String> EXAMPLES = Arrays.asList("0..5", "0", "-5", "-100..", "..100");

        public static NumberRange.IntRange getRangeArgument(CommandContext<ServerCommandSource> commandContext, String string) {
            return (NumberRange.IntRange)commandContext.getArgument(string, NumberRange.IntRange.class);
        }

        public NumberRange.IntRange parse(StringReader stringReader) throws CommandSyntaxException {
            return NumberRange.IntRange.parse(stringReader);
        }

        public Collection<String> getExamples() {
            return EXAMPLES;
        }

        public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
            return this.parse(stringReader);
        }

        public static class Serializer
        extends NumberSerializer<IntRangeArgumentType> {
            @Override
            public IntRangeArgumentType fromPacket(PacketByteBuf arg) {
                return new IntRangeArgumentType();
            }

            @Override
            public /* synthetic */ ArgumentType fromPacket(PacketByteBuf arg) {
                return this.fromPacket(arg);
            }
        }
    }
}

