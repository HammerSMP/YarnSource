/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.util.dynamic;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Map;
import java.util.Objects;
import net.minecraft.util.dynamic.DynamicSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

public final class GlobalPos
implements DynamicSerializable {
    private final DimensionType dimension;
    private final BlockPos pos;

    private GlobalPos(DimensionType arg, BlockPos arg2) {
        this.dimension = arg;
        this.pos = arg2;
    }

    public static GlobalPos create(DimensionType arg, BlockPos arg2) {
        return new GlobalPos(arg, arg2);
    }

    public static GlobalPos deserialize(Dynamic<?> dynamic) {
        return (GlobalPos)dynamic.get("dimension").map(DimensionType::deserialize).flatMap(arg -> dynamic.get("pos").map(BlockPos::deserialize).map(arg2 -> new GlobalPos((DimensionType)arg, (BlockPos)arg2))).orElseThrow(() -> new IllegalArgumentException("Could not parse GlobalPos"));
    }

    public DimensionType getDimension() {
        return this.dimension;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        GlobalPos lv = (GlobalPos)object;
        return Objects.equals(this.dimension, lv.dimension) && Objects.equals(this.pos, lv.pos);
    }

    public int hashCode() {
        return Objects.hash(this.dimension, this.pos);
    }

    @Override
    public <T> T serialize(DynamicOps<T> dynamicOps) {
        return (T)dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("dimension"), this.dimension.serialize(dynamicOps), (Object)dynamicOps.createString("pos"), this.pos.serialize(dynamicOps)));
    }

    public String toString() {
        return this.dimension.toString() + " " + this.pos;
    }
}

