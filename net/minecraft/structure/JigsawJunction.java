/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.structure;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Map;
import net.minecraft.structure.pool.StructurePool;

public class JigsawJunction {
    private final int sourceX;
    private final int sourceGroundY;
    private final int sourceZ;
    private final int deltaY;
    private final StructurePool.Projection destProjection;

    public JigsawJunction(int sourceX, int sourceGroundY, int sourceZ, int deltaY, StructurePool.Projection destProjection) {
        this.sourceX = sourceX;
        this.sourceGroundY = sourceGroundY;
        this.sourceZ = sourceZ;
        this.deltaY = deltaY;
        this.destProjection = destProjection;
    }

    public int getSourceX() {
        return this.sourceX;
    }

    public int getSourceGroundY() {
        return this.sourceGroundY;
    }

    public int getSourceZ() {
        return this.sourceZ;
    }

    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.put(dynamicOps.createString("source_x"), dynamicOps.createInt(this.sourceX)).put(dynamicOps.createString("source_ground_y"), dynamicOps.createInt(this.sourceGroundY)).put(dynamicOps.createString("source_z"), dynamicOps.createInt(this.sourceZ)).put(dynamicOps.createString("delta_y"), dynamicOps.createInt(this.deltaY)).put(dynamicOps.createString("dest_proj"), dynamicOps.createString(this.destProjection.getId()));
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)builder.build()));
    }

    public static <T> JigsawJunction method_28873(Dynamic<T> dynamic) {
        return new JigsawJunction(dynamic.get("source_x").asInt(0), dynamic.get("source_ground_y").asInt(0), dynamic.get("source_z").asInt(0), dynamic.get("delta_y").asInt(0), StructurePool.Projection.getById(dynamic.get("dest_proj").asString("")));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        JigsawJunction lv = (JigsawJunction)o;
        if (this.sourceX != lv.sourceX) {
            return false;
        }
        if (this.sourceZ != lv.sourceZ) {
            return false;
        }
        if (this.deltaY != lv.deltaY) {
            return false;
        }
        return this.destProjection == lv.destProjection;
    }

    public int hashCode() {
        int i = this.sourceX;
        i = 31 * i + this.sourceGroundY;
        i = 31 * i + this.sourceZ;
        i = 31 * i + this.deltaY;
        i = 31 * i + this.destProjection.hashCode();
        return i;
    }

    public String toString() {
        return "JigsawJunction{sourceX=" + this.sourceX + ", sourceGroundY=" + this.sourceGroundY + ", sourceZ=" + this.sourceZ + ", deltaY=" + this.deltaY + ", destProjection=" + this.destProjection + '}';
    }
}

