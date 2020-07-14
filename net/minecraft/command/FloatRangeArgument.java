/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  javax.annotation.Nullable
 */
package net.minecraft.command;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.predicate.NumberRange;
import net.minecraft.text.TranslatableText;

public class FloatRangeArgument {
    public static final FloatRangeArgument ANY = new FloatRangeArgument(null, null);
    public static final SimpleCommandExceptionType ONLY_INTS_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("argument.range.ints"));
    private final Float min;
    private final Float max;

    public FloatRangeArgument(@Nullable Float float_, @Nullable Float float2) {
        this.min = float_;
        this.max = float2;
    }

    @Nullable
    public Float getMin() {
        return this.min;
    }

    @Nullable
    public Float getMax() {
        return this.max;
    }

    public static FloatRangeArgument parse(StringReader reader, boolean allowFloats, Function<Float, Float> transform) throws CommandSyntaxException {
        Float float3;
        if (!reader.canRead()) {
            throw NumberRange.EXCEPTION_EMPTY.createWithContext((ImmutableStringReader)reader);
        }
        int i = reader.getCursor();
        Float float_ = FloatRangeArgument.mapFloat(FloatRangeArgument.parseFloat(reader, allowFloats), transform);
        if (reader.canRead(2) && reader.peek() == '.' && reader.peek(1) == '.') {
            reader.skip();
            reader.skip();
            Float float2 = FloatRangeArgument.mapFloat(FloatRangeArgument.parseFloat(reader, allowFloats), transform);
            if (float_ == null && float2 == null) {
                reader.setCursor(i);
                throw NumberRange.EXCEPTION_EMPTY.createWithContext((ImmutableStringReader)reader);
            }
        } else {
            if (!allowFloats && reader.canRead() && reader.peek() == '.') {
                reader.setCursor(i);
                throw ONLY_INTS_EXCEPTION.createWithContext((ImmutableStringReader)reader);
            }
            float3 = float_;
        }
        if (float_ == null && float3 == null) {
            reader.setCursor(i);
            throw NumberRange.EXCEPTION_EMPTY.createWithContext((ImmutableStringReader)reader);
        }
        return new FloatRangeArgument(float_, float3);
    }

    @Nullable
    private static Float parseFloat(StringReader reader, boolean allowFloats) throws CommandSyntaxException {
        int i = reader.getCursor();
        while (reader.canRead() && FloatRangeArgument.peekDigit(reader, allowFloats)) {
            reader.skip();
        }
        String string = reader.getString().substring(i, reader.getCursor());
        if (string.isEmpty()) {
            return null;
        }
        try {
            return Float.valueOf(Float.parseFloat(string));
        }
        catch (NumberFormatException numberFormatException) {
            if (allowFloats) {
                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidDouble().createWithContext((ImmutableStringReader)reader, (Object)string);
            }
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt().createWithContext((ImmutableStringReader)reader, (Object)string);
        }
    }

    private static boolean peekDigit(StringReader reader, boolean allowFloats) {
        char c = reader.peek();
        if (c >= '0' && c <= '9' || c == '-') {
            return true;
        }
        if (allowFloats && c == '.') {
            return !reader.canRead(2) || reader.peek(1) != '.';
        }
        return false;
    }

    @Nullable
    private static Float mapFloat(@Nullable Float float_, Function<Float, Float> function) {
        return float_ == null ? null : function.apply(float_);
    }
}

