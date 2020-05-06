/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 */
package net.minecraft.command.arguments;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.arguments.CoordinateArgument;
import net.minecraft.command.arguments.PosArgument;
import net.minecraft.command.arguments.Vec3ArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class DefaultPosArgument
implements PosArgument {
    private final CoordinateArgument x;
    private final CoordinateArgument y;
    private final CoordinateArgument z;

    public DefaultPosArgument(CoordinateArgument arg, CoordinateArgument arg2, CoordinateArgument arg3) {
        this.x = arg;
        this.y = arg2;
        this.z = arg3;
    }

    @Override
    public Vec3d toAbsolutePos(ServerCommandSource arg) {
        Vec3d lv = arg.getPosition();
        return new Vec3d(this.x.toAbsoluteCoordinate(lv.x), this.y.toAbsoluteCoordinate(lv.y), this.z.toAbsoluteCoordinate(lv.z));
    }

    @Override
    public Vec2f toAbsoluteRotation(ServerCommandSource arg) {
        Vec2f lv = arg.getRotation();
        return new Vec2f((float)this.x.toAbsoluteCoordinate(lv.x), (float)this.y.toAbsoluteCoordinate(lv.y));
    }

    @Override
    public boolean isXRelative() {
        return this.x.isRelative();
    }

    @Override
    public boolean isYRelative() {
        return this.y.isRelative();
    }

    @Override
    public boolean isZRelative() {
        return this.z.isRelative();
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof DefaultPosArgument)) {
            return false;
        }
        DefaultPosArgument lv = (DefaultPosArgument)object;
        if (!this.x.equals(lv.x)) {
            return false;
        }
        if (!this.y.equals(lv.y)) {
            return false;
        }
        return this.z.equals(lv.z);
    }

    public static DefaultPosArgument parse(StringReader stringReader) throws CommandSyntaxException {
        int i = stringReader.getCursor();
        CoordinateArgument lv = CoordinateArgument.parse(stringReader);
        if (!stringReader.canRead() || stringReader.peek() != ' ') {
            stringReader.setCursor(i);
            throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext((ImmutableStringReader)stringReader);
        }
        stringReader.skip();
        CoordinateArgument lv2 = CoordinateArgument.parse(stringReader);
        if (!stringReader.canRead() || stringReader.peek() != ' ') {
            stringReader.setCursor(i);
            throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext((ImmutableStringReader)stringReader);
        }
        stringReader.skip();
        CoordinateArgument lv3 = CoordinateArgument.parse(stringReader);
        return new DefaultPosArgument(lv, lv2, lv3);
    }

    public static DefaultPosArgument parse(StringReader stringReader, boolean bl) throws CommandSyntaxException {
        int i = stringReader.getCursor();
        CoordinateArgument lv = CoordinateArgument.parse(stringReader, bl);
        if (!stringReader.canRead() || stringReader.peek() != ' ') {
            stringReader.setCursor(i);
            throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext((ImmutableStringReader)stringReader);
        }
        stringReader.skip();
        CoordinateArgument lv2 = CoordinateArgument.parse(stringReader, false);
        if (!stringReader.canRead() || stringReader.peek() != ' ') {
            stringReader.setCursor(i);
            throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext((ImmutableStringReader)stringReader);
        }
        stringReader.skip();
        CoordinateArgument lv3 = CoordinateArgument.parse(stringReader, bl);
        return new DefaultPosArgument(lv, lv2, lv3);
    }

    public static DefaultPosArgument zero() {
        return new DefaultPosArgument(new CoordinateArgument(true, 0.0), new CoordinateArgument(true, 0.0), new CoordinateArgument(true, 0.0));
    }

    public int hashCode() {
        int i = this.x.hashCode();
        i = 31 * i + this.y.hashCode();
        i = 31 * i + this.z.hashCode();
        return i;
    }
}

