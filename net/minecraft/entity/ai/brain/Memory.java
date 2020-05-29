/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.entity.ai.brain;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;

public class Memory<T> {
    private final T value;
    private long expiry;

    public Memory(T object, long l) {
        this.value = object;
        this.expiry = l;
    }

    public void tick() {
        if (this.method_24914()) {
            --this.expiry;
        }
    }

    public static <T> Memory<T> method_28355(T object) {
        return new Memory<T>(object, Long.MAX_VALUE);
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

    public static <T> Codec<Memory<T>> method_28353(Codec<T> codec) {
        return RecordCodecBuilder.create(instance -> instance.group((App)codec.fieldOf("value").forGetter(arg -> arg.value), (App)Codec.LONG.optionalFieldOf("ttl").forGetter(arg -> arg.method_24914() ? Optional.of(arg.expiry) : Optional.empty())).apply((Applicative)instance, (object, optional) -> new Memory<Object>(object, optional.orElse(Long.MAX_VALUE))));
    }
}

