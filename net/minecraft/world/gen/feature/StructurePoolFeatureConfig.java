/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Map;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.FeatureConfig;

public class StructurePoolFeatureConfig
implements FeatureConfig {
    public final Identifier startPool;
    public final int size;

    public StructurePoolFeatureConfig(String string, int i) {
        this.startPool = new Identifier(string);
        this.size = i;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("start_pool"), (Object)dynamicOps.createString(this.startPool.toString()), (Object)dynamicOps.createString("size"), (Object)dynamicOps.createInt(this.size))));
    }

    public static <T> StructurePoolFeatureConfig deserialize(Dynamic<T> dynamic) {
        String string = dynamic.get("start_pool").asString("");
        int i = dynamic.get("size").asInt(6);
        return new StructurePoolFeatureConfig(string, i);
    }

    public int getSize() {
        return this.size;
    }

    public String getStartPool() {
        return this.startPool.toString();
    }
}

