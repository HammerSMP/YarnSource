/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.text.TranslatableText;

public class CoordinateArgument {
    public static final SimpleCommandExceptionType MISSING_COORDINATE = new SimpleCommandExceptionType((Message)new TranslatableText("argument.pos.missing.double"));
    public static final SimpleCommandExceptionType MISSING_BLOCK_POSITION = new SimpleCommandExceptionType((Message)new TranslatableText("argument.pos.missing.int"));
    private final boolean relative;
    private final double value;

    public CoordinateArgument(boolean relative, double value) {
        this.relative = relative;
        this.value = value;
    }

    public double toAbsoluteCoordinate(double offset) {
        if (this.relative) {
            return this.value + offset;
        }
        return this.value;
    }

    public static CoordinateArgument parse(StringReader reader, boolean centerIntegers) throws CommandSyntaxException {
        if (reader.canRead() && reader.peek() == '^') {
            throw Vec3ArgumentType.MIXED_COORDINATE_EXCEPTION.createWithContext((ImmutableStringReader)reader);
        }
        if (!reader.canRead()) {
            throw MISSING_COORDINATE.createWithContext((ImmutableStringReader)reader);
        }
        boolean bl2 = CoordinateArgument.isRelative(reader);
        int i = reader.getCursor();
        double d = reader.canRead() && reader.peek() != ' ' ? reader.readDouble() : 0.0;
        String string = reader.getString().substring(i, reader.getCursor());
        if (bl2 && string.isEmpty()) {
            return new CoordinateArgument(true, 0.0);
        }
        if (!string.contains(".") && !bl2 && centerIntegers) {
            d += 0.5;
        }
        return new CoordinateArgument(bl2, d);
    }

    public static CoordinateArgument parse(StringReader reader) throws CommandSyntaxException {
        double e;
        if (reader.canRead() && reader.peek() == '^') {
            throw Vec3ArgumentType.MIXED_COORDINATE_EXCEPTION.createWithContext((ImmutableStringReader)reader);
        }
        if (!reader.canRead()) {
            throw MISSING_BLOCK_POSITION.createWithContext((ImmutableStringReader)reader);
        }
        boolean bl = CoordinateArgument.isRelative(reader);
        if (reader.canRead() && reader.peek() != ' ') {
            double d = bl ? reader.readDouble() : (double)reader.readInt();
        } else {
            e = 0.0;
        }
        return new CoordinateArgument(bl, e);
    }

    public static boolean isRelative(StringReader reader) {
        boolean bl2;
        if (reader.peek() == '~') {
            boolean bl = true;
            reader.skip();
        } else {
            bl2 = false;
        }
        return bl2;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CoordinateArgument)) {
            return false;
        }
        CoordinateArgument lv = (CoordinateArgument)o;
        if (this.relative != lv.relative) {
            return false;
        }
        return Double.compare(lv.value, this.value) == 0;
    }

    public int hashCode() {
        int i = this.relative ? 1 : 0;
        long l = Double.doubleToLongBits(this.value);
        i = 31 * i + (int)(l ^ l >>> 32);
        return i;
    }

    public boolean isRelative() {
        return this.relative;
    }
}

