/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterators
 *  com.mojang.serialization.Codec
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.util.math;

import com.google.common.collect.Iterators;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.entity.Entity;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3i;

public enum Direction implements StringIdentifiable
{
    DOWN(0, 1, -1, "down", AxisDirection.NEGATIVE, Axis.Y, new Vec3i(0, -1, 0)),
    UP(1, 0, -1, "up", AxisDirection.POSITIVE, Axis.Y, new Vec3i(0, 1, 0)),
    NORTH(2, 3, 2, "north", AxisDirection.NEGATIVE, Axis.Z, new Vec3i(0, 0, -1)),
    SOUTH(3, 2, 0, "south", AxisDirection.POSITIVE, Axis.Z, new Vec3i(0, 0, 1)),
    WEST(4, 5, 1, "west", AxisDirection.NEGATIVE, Axis.X, new Vec3i(-1, 0, 0)),
    EAST(5, 4, 3, "east", AxisDirection.POSITIVE, Axis.X, new Vec3i(1, 0, 0));

    private final int id;
    private final int idOpposite;
    private final int idHorizontal;
    private final String name;
    private final Axis axis;
    private final AxisDirection direction;
    private final Vec3i vector;
    private static final Direction[] ALL;
    private static final Map<String, Direction> NAME_MAP;
    private static final Direction[] VALUES;
    private static final Direction[] HORIZONTAL;
    private static final Long2ObjectMap<Direction> VECTOR_TO_DIRECTION;

    private Direction(int id, int idOpposite, int idHorizontal, String name, AxisDirection direction, Axis axis, Vec3i vector) {
        this.id = id;
        this.idHorizontal = idHorizontal;
        this.idOpposite = idOpposite;
        this.name = name;
        this.axis = axis;
        this.direction = direction;
        this.vector = vector;
    }

    public static Direction[] getEntityFacingOrder(Entity entity) {
        Direction lv3;
        float f = entity.getPitch(1.0f) * ((float)Math.PI / 180);
        float g = -entity.getYaw(1.0f) * ((float)Math.PI / 180);
        float h = MathHelper.sin(f);
        float i = MathHelper.cos(f);
        float j = MathHelper.sin(g);
        float k = MathHelper.cos(g);
        boolean bl = j > 0.0f;
        boolean bl2 = h < 0.0f;
        boolean bl3 = k > 0.0f;
        float l = bl ? j : -j;
        float m = bl2 ? -h : h;
        float n = bl3 ? k : -k;
        float o = l * i;
        float p = n * i;
        Direction lv = bl ? EAST : WEST;
        Direction lv2 = bl2 ? UP : DOWN;
        Direction direction = lv3 = bl3 ? SOUTH : NORTH;
        if (l > n) {
            if (m > o) {
                return Direction.method_10145(lv2, lv, lv3);
            }
            if (p > m) {
                return Direction.method_10145(lv, lv3, lv2);
            }
            return Direction.method_10145(lv, lv2, lv3);
        }
        if (m > p) {
            return Direction.method_10145(lv2, lv3, lv);
        }
        if (o > m) {
            return Direction.method_10145(lv3, lv, lv2);
        }
        return Direction.method_10145(lv3, lv2, lv);
    }

    private static Direction[] method_10145(Direction arg, Direction arg2, Direction arg3) {
        return new Direction[]{arg, arg2, arg3, arg3.getOpposite(), arg2.getOpposite(), arg.getOpposite()};
    }

    @Environment(value=EnvType.CLIENT)
    public static Direction transform(Matrix4f arg, Direction arg2) {
        Vec3i lv = arg2.getVector();
        Vector4f lv2 = new Vector4f(lv.getX(), lv.getY(), lv.getZ(), 0.0f);
        lv2.transform(arg);
        return Direction.getFacing(lv2.getX(), lv2.getY(), lv2.getZ());
    }

    @Environment(value=EnvType.CLIENT)
    public Quaternion getRotationQuaternion() {
        Quaternion lv = Vector3f.POSITIVE_X.getDegreesQuaternion(90.0f);
        switch (this) {
            case DOWN: {
                return Vector3f.POSITIVE_X.getDegreesQuaternion(180.0f);
            }
            case UP: {
                return Quaternion.IDENTITY.copy();
            }
            case NORTH: {
                lv.hamiltonProduct(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0f));
                return lv;
            }
            case SOUTH: {
                return lv;
            }
            case WEST: {
                lv.hamiltonProduct(Vector3f.POSITIVE_Z.getDegreesQuaternion(90.0f));
                return lv;
            }
        }
        lv.hamiltonProduct(Vector3f.POSITIVE_Z.getDegreesQuaternion(-90.0f));
        return lv;
    }

    public int getId() {
        return this.id;
    }

    public int getHorizontal() {
        return this.idHorizontal;
    }

    public AxisDirection getDirection() {
        return this.direction;
    }

    public Direction getOpposite() {
        return Direction.byId(this.idOpposite);
    }

    public Direction rotateYClockwise() {
        switch (this) {
            case NORTH: {
                return EAST;
            }
            case EAST: {
                return SOUTH;
            }
            case SOUTH: {
                return WEST;
            }
            case WEST: {
                return NORTH;
            }
        }
        throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
    }

    public Direction rotateYCounterclockwise() {
        switch (this) {
            case NORTH: {
                return WEST;
            }
            case EAST: {
                return NORTH;
            }
            case SOUTH: {
                return EAST;
            }
            case WEST: {
                return SOUTH;
            }
        }
        throw new IllegalStateException("Unable to get CCW facing of " + this);
    }

    public int getOffsetX() {
        return this.vector.getX();
    }

    public int getOffsetY() {
        return this.vector.getY();
    }

    public int getOffsetZ() {
        return this.vector.getZ();
    }

    @Environment(value=EnvType.CLIENT)
    public Vector3f getUnitVector() {
        return new Vector3f(this.getOffsetX(), this.getOffsetY(), this.getOffsetZ());
    }

    public String getName() {
        return this.name;
    }

    public Axis getAxis() {
        return this.axis;
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public static Direction byName(@Nullable String name) {
        if (name == null) {
            return null;
        }
        return NAME_MAP.get(name.toLowerCase(Locale.ROOT));
    }

    public static Direction byId(int id) {
        return VALUES[MathHelper.abs(id % VALUES.length)];
    }

    public static Direction fromHorizontal(int value) {
        return HORIZONTAL[MathHelper.abs(value % HORIZONTAL.length)];
    }

    @Nullable
    public static Direction fromVector(int x, int y, int z) {
        return (Direction)VECTOR_TO_DIRECTION.get(BlockPos.asLong(x, y, z));
    }

    public static Direction fromRotation(double rotation) {
        return Direction.fromHorizontal(MathHelper.floor(rotation / 90.0 + 0.5) & 3);
    }

    public static Direction from(Axis axis, AxisDirection direction) {
        switch (axis) {
            case X: {
                return direction == AxisDirection.POSITIVE ? EAST : WEST;
            }
            case Y: {
                return direction == AxisDirection.POSITIVE ? UP : DOWN;
            }
        }
        return direction == AxisDirection.POSITIVE ? SOUTH : NORTH;
    }

    public float asRotation() {
        return (this.idHorizontal & 3) * 90;
    }

    public static Direction random(Random random) {
        return Util.getRandom(ALL, random);
    }

    public static Direction getFacing(double x, double y, double z) {
        return Direction.getFacing((float)x, (float)y, (float)z);
    }

    public static Direction getFacing(float x, float y, float z) {
        Direction lv = NORTH;
        float i = Float.MIN_VALUE;
        for (Direction lv2 : ALL) {
            float j = x * (float)lv2.vector.getX() + y * (float)lv2.vector.getY() + z * (float)lv2.vector.getZ();
            if (!(j > i)) continue;
            i = j;
            lv = lv2;
        }
        return lv;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public static Direction get(AxisDirection direction, Axis axis) {
        for (Direction lv : ALL) {
            if (lv.getDirection() != direction || lv.getAxis() != axis) continue;
            return lv;
        }
        throw new IllegalArgumentException("No such direction: " + (Object)((Object)direction) + " " + axis);
    }

    public Vec3i getVector() {
        return this.vector;
    }

    static {
        ALL = Direction.values();
        NAME_MAP = Arrays.stream(ALL).collect(Collectors.toMap(Direction::getName, arg -> arg));
        VALUES = (Direction[])Arrays.stream(ALL).sorted(Comparator.comparingInt(arg -> arg.id)).toArray(Direction[]::new);
        HORIZONTAL = (Direction[])Arrays.stream(ALL).filter(arg -> arg.getAxis().isHorizontal()).sorted(Comparator.comparingInt(arg -> arg.idHorizontal)).toArray(Direction[]::new);
        VECTOR_TO_DIRECTION = (Long2ObjectMap)Arrays.stream(ALL).collect(Collectors.toMap(arg -> new BlockPos(arg.getVector()).asLong(), arg -> arg, (arg, arg2) -> {
            throw new IllegalArgumentException("Duplicate keys");
        }, Long2ObjectOpenHashMap::new));
    }

    public static enum Type implements Iterable<Direction>,
    Predicate<Direction>
    {
        HORIZONTAL(new Direction[]{NORTH, EAST, SOUTH, WEST}, new Axis[]{Axis.X, Axis.Z}),
        VERTICAL(new Direction[]{UP, DOWN}, new Axis[]{Axis.Y});

        private final Direction[] facingArray;
        private final Axis[] axisArray;

        private Type(Direction[] directions, Axis[] args2) {
            this.facingArray = directions;
            this.axisArray = args2;
        }

        public Direction random(Random random) {
            return Util.getRandom(this.facingArray, random);
        }

        @Override
        public boolean test(@Nullable Direction arg) {
            return arg != null && arg.getAxis().getType() == this;
        }

        @Override
        public Iterator<Direction> iterator() {
            return Iterators.forArray((Object[])this.facingArray);
        }

        public Stream<Direction> stream() {
            return Arrays.stream(this.facingArray);
        }

        @Override
        public /* synthetic */ boolean test(@Nullable Object direction) {
            return this.test((Direction)direction);
        }
    }

    public static enum AxisDirection {
        POSITIVE(1, "Towards positive"),
        NEGATIVE(-1, "Towards negative");

        private final int offset;
        private final String desc;

        private AxisDirection(int offset, String description) {
            this.offset = offset;
            this.desc = description;
        }

        public int offset() {
            return this.offset;
        }

        public String toString() {
            return this.desc;
        }

        public AxisDirection getOpposite() {
            return this == POSITIVE ? NEGATIVE : POSITIVE;
        }
    }

    public static enum Axis implements StringIdentifiable,
    Predicate<Direction>
    {
        X("x"){

            @Override
            public int choose(int x, int y, int z) {
                return x;
            }

            @Override
            public double choose(double x, double y, double z) {
                return x;
            }

            @Override
            public /* synthetic */ boolean test(@Nullable Object object) {
                return super.test((Direction)object);
            }
        }
        ,
        Y("y"){

            @Override
            public int choose(int x, int y, int z) {
                return y;
            }

            @Override
            public double choose(double x, double y, double z) {
                return y;
            }

            @Override
            public /* synthetic */ boolean test(@Nullable Object object) {
                return super.test((Direction)object);
            }
        }
        ,
        Z("z"){

            @Override
            public int choose(int x, int y, int z) {
                return z;
            }

            @Override
            public double choose(double x, double y, double z) {
                return z;
            }

            @Override
            public /* synthetic */ boolean test(@Nullable Object object) {
                return super.test((Direction)object);
            }
        };

        private static final Axis[] field_23780;
        public static final Codec<Axis> field_25065;
        private static final Map<String, Axis> BY_NAME;
        private final String name;

        private Axis(String string2) {
            this.name = string2;
        }

        @Nullable
        public static Axis fromName(String name) {
            return BY_NAME.get(name.toLowerCase(Locale.ROOT));
        }

        public String getName() {
            return this.name;
        }

        public boolean isVertical() {
            return this == Y;
        }

        public boolean isHorizontal() {
            return this == X || this == Z;
        }

        public String toString() {
            return this.name;
        }

        public static Axis pickRandomAxis(Random random) {
            return Util.getRandom(field_23780, random);
        }

        @Override
        public boolean test(@Nullable Direction arg) {
            return arg != null && arg.getAxis() == this;
        }

        public Type getType() {
            switch (this) {
                case X: 
                case Z: {
                    return Type.HORIZONTAL;
                }
                case Y: {
                    return Type.VERTICAL;
                }
            }
            throw new Error("Someone's been tampering with the universe!");
        }

        @Override
        public String asString() {
            return this.name;
        }

        public abstract int choose(int var1, int var2, int var3);

        public abstract double choose(double var1, double var3, double var5);

        @Override
        public /* synthetic */ boolean test(@Nullable Object object) {
            return this.test((Direction)object);
        }

        static {
            field_23780 = Axis.values();
            field_25065 = StringIdentifiable.createCodec(Axis::values, Axis::fromName);
            BY_NAME = Arrays.stream(field_23780).collect(Collectors.toMap(Axis::getName, arg -> arg));
        }
    }
}

