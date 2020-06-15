/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.mojang.serialization.Codec
 *  javax.annotation.concurrent.Immutable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.util.math;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import java.util.stream.IntStream;
import javax.annotation.concurrent.Immutable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;

@Immutable
public class Vec3i
implements Comparable<Vec3i> {
    public static final Codec<Vec3i> field_25123 = Codec.INT_STREAM.comapFlatMap(intStream -> Util.toIntArray(intStream, 3).map(is -> new Vec3i(is[0], is[1], is[2])), arg -> IntStream.of(arg.getX(), arg.getY(), arg.getZ()));
    public static final Vec3i ZERO = new Vec3i(0, 0, 0);
    private int x;
    private int y;
    private int z;

    public Vec3i(int i, int j, int k) {
        this.x = i;
        this.y = j;
        this.z = k;
    }

    public Vec3i(double d, double e, double f) {
        this(MathHelper.floor(d), MathHelper.floor(e), MathHelper.floor(f));
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Vec3i)) {
            return false;
        }
        Vec3i lv = (Vec3i)object;
        if (this.getX() != lv.getX()) {
            return false;
        }
        if (this.getY() != lv.getY()) {
            return false;
        }
        return this.getZ() == lv.getZ();
    }

    public int hashCode() {
        return (this.getY() + this.getZ() * 31) * 31 + this.getX();
    }

    @Override
    public int compareTo(Vec3i arg) {
        if (this.getY() == arg.getY()) {
            if (this.getZ() == arg.getZ()) {
                return this.getX() - arg.getX();
            }
            return this.getZ() - arg.getZ();
        }
        return this.getY() - arg.getY();
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    protected void setX(int i) {
        this.x = i;
    }

    protected void setY(int i) {
        this.y = i;
    }

    protected void setZ(int i) {
        this.z = i;
    }

    public Vec3i down() {
        return this.down(1);
    }

    public Vec3i down(int i) {
        return this.offset(Direction.DOWN, i);
    }

    public Vec3i offset(Direction arg, int i) {
        if (i == 0) {
            return this;
        }
        return new Vec3i(this.getX() + arg.getOffsetX() * i, this.getY() + arg.getOffsetY() * i, this.getZ() + arg.getOffsetZ() * i);
    }

    public Vec3i crossProduct(Vec3i arg) {
        return new Vec3i(this.getY() * arg.getZ() - this.getZ() * arg.getY(), this.getZ() * arg.getX() - this.getX() * arg.getZ(), this.getX() * arg.getY() - this.getY() * arg.getX());
    }

    public boolean isWithinDistance(Vec3i arg, double d) {
        return this.getSquaredDistance(arg.getX(), arg.getY(), arg.getZ(), false) < d * d;
    }

    public boolean isWithinDistance(Position arg, double d) {
        return this.getSquaredDistance(arg.getX(), arg.getY(), arg.getZ(), true) < d * d;
    }

    public double getSquaredDistance(Vec3i arg) {
        return this.getSquaredDistance(arg.getX(), arg.getY(), arg.getZ(), true);
    }

    public double getSquaredDistance(Position arg, boolean bl) {
        return this.getSquaredDistance(arg.getX(), arg.getY(), arg.getZ(), bl);
    }

    public double getSquaredDistance(double d, double e, double f, boolean bl) {
        double g = bl ? 0.5 : 0.0;
        double h = (double)this.getX() + g - d;
        double i = (double)this.getY() + g - e;
        double j = (double)this.getZ() + g - f;
        return h * h + i * i + j * j;
    }

    public int getManhattanDistance(Vec3i arg) {
        float f = Math.abs(arg.getX() - this.getX());
        float g = Math.abs(arg.getY() - this.getY());
        float h = Math.abs(arg.getZ() - this.getZ());
        return (int)(f + g + h);
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("x", this.getX()).add("y", this.getY()).add("z", this.getZ()).toString();
    }

    @Environment(value=EnvType.CLIENT)
    public String toShortString() {
        return "" + this.getX() + ", " + this.getY() + ", " + this.getZ();
    }

    @Override
    public /* synthetic */ int compareTo(Object object) {
        return this.compareTo((Vec3i)object);
    }
}

