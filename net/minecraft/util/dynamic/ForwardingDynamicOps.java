/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.ListBuilder
 *  com.mojang.serialization.MapLike
 *  com.mojang.serialization.RecordBuilder
 */
package net.minecraft.util.dynamic;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.ListBuilder;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public abstract class ForwardingDynamicOps<T>
implements DynamicOps<T> {
    protected final DynamicOps<T> delegate;

    protected ForwardingDynamicOps(DynamicOps<T> dynamicOps) {
        this.delegate = dynamicOps;
    }

    public T empty() {
        return (T)this.delegate.empty();
    }

    public <U> U convertTo(DynamicOps<U> dynamicOps, T object) {
        return (U)this.delegate.convertTo(dynamicOps, object);
    }

    public DataResult<Number> getNumberValue(T object) {
        return this.delegate.getNumberValue(object);
    }

    public T createNumeric(Number number) {
        return (T)this.delegate.createNumeric(number);
    }

    public T createByte(byte b) {
        return (T)this.delegate.createByte(b);
    }

    public T createShort(short s) {
        return (T)this.delegate.createShort(s);
    }

    public T createInt(int i) {
        return (T)this.delegate.createInt(i);
    }

    public T createLong(long l) {
        return (T)this.delegate.createLong(l);
    }

    public T createFloat(float f) {
        return (T)this.delegate.createFloat(f);
    }

    public T createDouble(double d) {
        return (T)this.delegate.createDouble(d);
    }

    public DataResult<Boolean> getBooleanValue(T object) {
        return this.delegate.getBooleanValue(object);
    }

    public T createBoolean(boolean bl) {
        return (T)this.delegate.createBoolean(bl);
    }

    public DataResult<String> getStringValue(T object) {
        return this.delegate.getStringValue(object);
    }

    public T createString(String string) {
        return (T)this.delegate.createString(string);
    }

    public DataResult<T> mergeToList(T object, T object2) {
        return this.delegate.mergeToList(object, object2);
    }

    public DataResult<T> mergeToList(T object, List<T> list) {
        return this.delegate.mergeToList(object, list);
    }

    public DataResult<T> mergeToMap(T object, T object2, T object3) {
        return this.delegate.mergeToMap(object, object2, object3);
    }

    public DataResult<T> mergeToMap(T object, MapLike<T> mapLike) {
        return this.delegate.mergeToMap(object, mapLike);
    }

    public DataResult<Stream<Pair<T, T>>> getMapValues(T object) {
        return this.delegate.getMapValues(object);
    }

    public DataResult<Consumer<BiConsumer<T, T>>> getMapEntries(T object) {
        return this.delegate.getMapEntries(object);
    }

    public T createMap(Stream<Pair<T, T>> stream) {
        return (T)this.delegate.createMap(stream);
    }

    public DataResult<MapLike<T>> getMap(T object) {
        return this.delegate.getMap(object);
    }

    public DataResult<Stream<T>> getStream(T object) {
        return this.delegate.getStream(object);
    }

    public DataResult<Consumer<Consumer<T>>> getList(T object) {
        return this.delegate.getList(object);
    }

    public T createList(Stream<T> stream) {
        return (T)this.delegate.createList(stream);
    }

    public DataResult<ByteBuffer> getByteBuffer(T object) {
        return this.delegate.getByteBuffer(object);
    }

    public T createByteList(ByteBuffer byteBuffer) {
        return (T)this.delegate.createByteList(byteBuffer);
    }

    public DataResult<IntStream> getIntStream(T object) {
        return this.delegate.getIntStream(object);
    }

    public T createIntList(IntStream intStream) {
        return (T)this.delegate.createIntList(intStream);
    }

    public DataResult<LongStream> getLongStream(T object) {
        return this.delegate.getLongStream(object);
    }

    public T createLongList(LongStream longStream) {
        return (T)this.delegate.createLongList(longStream);
    }

    public T remove(T object, String string) {
        return (T)this.delegate.remove(object, string);
    }

    public boolean compressMaps() {
        return this.delegate.compressMaps();
    }

    public ListBuilder<T> listBuilder() {
        return this.delegate.listBuilder();
    }

    public RecordBuilder<T> mapBuilder() {
        return this.delegate.mapBuilder();
    }
}

