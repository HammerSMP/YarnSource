/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.entity.ai.brain;

import com.google.common.collect.Maps;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.util.dynamic.DynamicSerializable;

public class Memory<T>
implements DynamicSerializable {
    private final T value;
    private long expiry;

    public Memory(T object, long l) {
        this.value = object;
        this.expiry = l;
    }

    public Memory(T object) {
        this(object, Long.MAX_VALUE);
    }

    public Memory(Function<Dynamic<?>, T> function, Dynamic<?> dynamic) {
        this(function.apply((Dynamic<?>)dynamic.get("value").get().orElseThrow(RuntimeException::new)), dynamic.get("ttl").asLong(Long.MAX_VALUE));
    }

    public void method_24913() {
        if (this.method_24914()) {
            --this.expiry;
        }
    }

    public static <T> Memory<T> permanent(T object) {
        return new Memory<T>(object);
    }

    public static <T> Memory<T> timed(T object, long l) {
        return new Memory<T>(object, l);
    }

    public T getValue() {
        return this.value;
    }

    public boolean isExpired() {
        return this.expiry <= 0L;
    }

    public String toString() {
        return this.value.toString() + (this.method_24914() ? " (ttl: " + this.expiry + ")" : "");
    }

    public boolean method_24914() {
        return this.expiry != Long.MAX_VALUE;
    }

    @Override
    public <T> T serialize(DynamicOps<T> dynamicOps) {
        HashMap map = Maps.newHashMap();
        map.put(dynamicOps.createString("value"), ((DynamicSerializable)this.value).serialize(dynamicOps));
        if (this.method_24914()) {
            map.put(dynamicOps.createString("ttl"), dynamicOps.createLong(this.expiry));
        }
        return (T)dynamicOps.createMap((Map)map);
    }
}

