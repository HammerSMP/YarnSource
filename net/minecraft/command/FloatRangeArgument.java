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

    public FloatRangeArgument(@Nullable Float arg, @Nullable Float arg2) {
        this.min = arg;
        this.max = arg2;
    }

    @Nullable
    public Float getMin() {
        return this.min;
    }

    @Nullable
    public Float getMax() {
        return this.max;
    }

    public static FloatRangeArgument parse(StringReader stringReader, boolean bl, Function<Float, Float> function) throws CommandSyntaxException {
        Float lv3;
        if (!stringReader.canRead()) {
            throw NumberRange.EXCEPTION_EMPTY.createWithContext((ImmutableStringReader)stringReader);
        }
        int i = stringReader.getCursor();
        Float lv = FloatRangeArgument.mapFloat(FloatRangeArgument.parseFloat(stringReader, bl), function);
        if (stringReader.canRead(2) && stringReader.peek() == '.' && stringReader.peek(1) == '.') {
            stringReader.skip();
            stringReader.skip();
            Float lv2 = FloatRangeArgument.mapFloat(FloatRangeArgument.parseFloat(stringReader, bl), function);
            if (lv == null && lv2 == null) {
                stringReader.setCursor(i);
                throw NumberRange.EXCEPTION_EMPTY.createWithContext((ImmutableStringReader)stringReader);
            }
        } else {
            if (!bl && stringReader.canRead() && stringReader.peek() == '.') {
                stringReader.setCursor(i);
                throw ONLY_INTS_EXCEPTION.createWithContext((ImmutableStringReader)stringReader);
            }
            lv3 = lv;
        }
        if (lv == null && lv3 == null) {
            stringReader.setCursor(i);
            throw NumberRange.EXCEPTION_EMPTY.createWithContext((ImmutableStringReader)stringReader);
        }
        return new FloatRangeArgument(lv, lv3);
    }

    @Nullable
    private static Float parseFloat(StringReader stringReader, boolean bl) throws CommandSyntaxException {
        int i = stringReader.getCursor();
        while (stringReader.canRead() && FloatRangeArgument.peekDigit(stringReader, bl)) {
            stringReader.skip();
        }
        String string = stringReader.getString().substring(i, stringReader.getCursor());
        if (string.isEmpty()) {
            return null;
        }
        try {
            return Float.valueOf(Float.parseFloat(string));
        }
        catch (NumberFormatException numberFormatException) {
            if (bl) {
                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidDouble().createWithContext((ImmutableStringReader)stringReader, (Object)string);
            }
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt().createWithContext((ImmutableStringReader)stringReader, (Object)string);
        }
    }

    private static boolean peekDigit(StringReader stringReader, boolean bl) {
        char c = stringReader.peek();
        if (c >= '0' && c <= '9' || c == '-') {
            return true;
        }
        if (bl && c == '.') {
            return !stringReader.canRead(2) || stringReader.peek(1) != '.';
        }
        return false;
    }

    @Nullable
    private static Float mapFloat(@Nullable Float arg, Function<Float, Float> function) {
        return arg == null ? null : function.apply(arg);
    }
}
