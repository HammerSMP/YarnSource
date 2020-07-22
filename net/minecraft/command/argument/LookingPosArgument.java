/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Objects;
import net.minecraft.command.argument.CoordinateArgument;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class LookingPosArgument
implements PosArgument {
    private final double x;
    private final double y;
    private final double z;

    public LookingPosArgument(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public Vec3d toAbsolutePos(ServerCommandSource source) {
        Vec2f lv = source.getRotation();
        Vec3d lv2 = source.getEntityAnchor().positionAt(source);
        float f = MathHelper.cos((lv.y + 90.0f) * ((float)Math.PI / 180));
        float g = MathHelper.sin((lv.y + 90.0f) * ((float)Math.PI / 180));
        float h = MathHelper.cos(-lv.x * ((float)Math.PI / 180));
        float i = MathHelper.sin(-lv.x * ((float)Math.PI / 180));
        float j = MathHelper.cos((-lv.x + 90.0f) * ((float)Math.PI / 180));
        float k = MathHelper.sin((-lv.x + 90.0f) * ((float)Math.PI / 180));
        Vec3d lv3 = new Vec3d(f * h, i, g * h);
        Vec3d lv4 = new Vec3d(f * j, k, g * j);
        Vec3d lv5 = lv3.crossProduct(lv4).multiply(-1.0);
        double d = lv3.x * this.z + lv4.x * this.y + lv5.x * this.x;
        double e = lv3.y * this.z + lv4.y * this.y + lv5.y * this.x;
        double l = lv3.z * this.z + lv4.z * this.y + lv5.z * this.x;
        return new Vec3d(lv2.x + d, lv2.y + e, lv2.z + l);
    }

    @Override
    public Vec2f toAbsoluteRotation(ServerCommandSource source) {
        return Vec2f.ZERO;
    }

    @Override
    public boolean isXRelative() {
        return true;
    }

    @Override
    public boolean isYRelative() {
        return true;
    }

    @Override
    public boolean isZRelative() {
        return true;
    }

    public static LookingPosArgument parse(StringReader reader) throws CommandSyntaxException {
        int i = reader.getCursor();
        double d = LookingPosArgument.readCoordinate(reader, i);
        if (!reader.canRead() || reader.peek() != ' ') {
            reader.setCursor(i);
            throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext((ImmutableStringReader)reader);
        }
        reader.skip();
        double e = LookingPosArgument.readCoordinate(reader, i);
        if (!reader.canRead() || reader.peek() != ' ') {
            reader.setCursor(i);
            throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext((ImmutableStringReader)reader);
        }
        reader.skip();
        double f = LookingPosArgument.readCoordinate(reader, i);
        return new LookingPosArgument(d, e, f);
    }

    private static double readCoordinate(StringReader reader, int startingCursorPos) throws CommandSyntaxException {
        if (!reader.canRead()) {
            throw CoordinateArgument.MISSING_COORDINATE.createWithContext((ImmutableStringReader)reader);
        }
        if (reader.peek() != '^') {
            reader.setCursor(startingCursorPos);
            throw Vec3ArgumentType.MIXED_COORDINATE_EXCEPTION.createWithContext((ImmutableStringReader)reader);
        }
        reader.skip();
        return reader.canRead() && reader.peek() != ' ' ? reader.readDouble() : 0.0;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LookingPosArgument)) {
            return false;
        }
        LookingPosArgument lv = (LookingPosArgument)o;
        return this.x == lv.x && this.y == lv.y && this.z == lv.z;
    }

    public int hashCode() {
        return Objects.hash(this.x, this.y, this.z);
    }
}

