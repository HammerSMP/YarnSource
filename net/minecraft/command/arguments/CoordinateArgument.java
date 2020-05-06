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
package net.minecraft.command.arguments;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.arguments.Vec3ArgumentType;
import net.minecraft.text.TranslatableText;

public class CoordinateArgument {
    public static final SimpleCommandExceptionType MISSING_COORDINATE = new SimpleCommandExceptionType((Message)new TranslatableText("argument.pos.missing.double"));
    public static final SimpleCommandExceptionType MISSING_BLOCK_POSITION = new SimpleCommandExceptionType((Message)new TranslatableText("argument.pos.missing.int"));
    private final boolean relative;
    private final double value;

    public CoordinateArgument(boolean bl, double d) {
        this.relative = bl;
        this.value = d;
    }

    public double toAbsoluteCoordinate(double d) {
        if (this.relative) {
            return this.value + d;
        }
        return this.value;
    }

    public static CoordinateArgument parse(StringReader stringReader, boolean bl) throws CommandSyntaxException {
        if (stringReader.canRead() && stringReader.peek() == '^') {
            throw Vec3ArgumentType.MIXED_COORDINATE_EXCEPTION.createWithContext((ImmutableStringReader)stringReader);
        }
        if (!stringReader.canRead()) {
            throw MISSING_COORDINATE.createWithContext((ImmutableStringReader)stringReader);
        }
        boolean bl2 = CoordinateArgument.isRelative(stringReader);
        int i = stringReader.getCursor();
        double d = stringReader.canRead() && stringReader.peek() != ' ' ? stringReader.readDouble() : 0.0;
        String string = stringReader.getString().substring(i, stringReader.getCursor());
        if (bl2 && string.isEmpty()) {
            return new CoordinateArgument(true, 0.0);
        }
        if (!string.contains(".") && !bl2 && bl) {
            d += 0.5;
        }
        return new CoordinateArgument(bl2, d);
    }

    public static CoordinateArgument parse(StringReader stringReader) throws CommandSyntaxException {
        double e;
        if (stringReader.canRead() && stringReader.peek() == '^') {
            throw Vec3ArgumentType.MIXED_COORDINATE_EXCEPTION.createWithContext((ImmutableStringReader)stringReader);
        }
        if (!stringReader.canRead()) {
            throw MISSING_BLOCK_POSITION.createWithContext((ImmutableStringReader)stringReader);
        }
        boolean bl = CoordinateArgument.isRelative(stringReader);
        if (stringReader.canRead() && stringReader.peek() != ' ') {
            double d = bl ? stringReader.readDouble() : (double)stringReader.readInt();
        } else {
            e = 0.0;
        }
        return new CoordinateArgument(bl, e);
    }

    private static boolean isRelative(StringReader stringReader) {
        boolean bl2;
        if (stringReader.peek() == '~') {
            boolean bl = true;
            stringReader.skip();
        } else {
            bl2 = false;
        }
        return bl2;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof CoordinateArgument)) {
            return false;
        }
        CoordinateArgument lv = (CoordinateArgument)object;
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

