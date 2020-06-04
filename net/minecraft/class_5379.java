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
package net.minecraft;

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

public abstract class class_5379<T>
implements DynamicOps<T> {
    protected final DynamicOps<T> field_25503;

    protected class_5379(DynamicOps<T> dynamicOps) {
        this.field_25503 = dynamicOps;
    }

    public T empty() {
        return (T)this.field_25503.empty();
    }

    public <U> U convertTo(DynamicOps<U> dynamicOps, T object) {
        return (U)this.field_25503.convertTo(dynamicOps, object);
    }

    public DataResult<Number> getNumberValue(T object) {
        return this.field_25503.getNumberValue(object);
    }

    public T createNumeric(Number number) {
        return (T)this.field_25503.createNumeric(number);
    }

    public T createByte(byte b) {
        return (T)this.field_25503.createByte(b);
    }

    public T createShort(short s) {
        return (T)this.field_25503.createShort(s);
    }

    public T createInt(int i) {
        return (T)this.field_25503.createInt(i);
    }

    public T createLong(long l) {
        return (T)this.field_25503.createLong(l);
    }

    public T createFloat(float f) {
        return (T)this.field_25503.createFloat(f);
    }

    public T createDouble(double d) {
        return (T)this.field_25503.createDouble(d);
    }

    public DataResult<Boolean> getBooleanValue(T object) {
        return this.field_25503.getBooleanValue(object);
    }

    public T createBoolean(boolean bl) {
        return (T)this.field_25503.createBoolean(bl);
    }

    public DataResult<String> getStringValue(T object) {
        return this.field_25503.getStringValue(object);
    }

    public T createString(String string) {
        return (T)this.field_25503.createString(string);
    }

    public DataResult<T> mergeToList(T object, T object2) {
        return this.field_25503.mergeToList(object, object2);
    }

    public DataResult<T> mergeToList(T object, List<T> list) {
        return this.field_25503.mergeToList(object, list);
    }

    public DataResult<T> mergeToMap(T object, T object2, T object3) {
        return this.field_25503.mergeToMap(object, object2, object3);
    }

    public DataResult<T> mergeToMap(T object, MapLike<T> mapLike) {
        return this.field_25503.mergeToMap(object, mapLike);
    }

    public DataResult<Stream<Pair<T, T>>> getMapValues(T object) {
        return this.field_25503.getMapValues(object);
    }

    public DataResult<Consumer<BiConsumer<T, T>>> getMapEntries(T object) {
        return this.field_25503.getMapEntries(object);
    }

    public T createMap(Stream<Pair<T, T>> stream) {
        return (T)this.field_25503.createMap(stream);
    }

    public DataResult<MapLike<T>> getMap(T object) {
        return this.field_25503.getMap(object);
    }

    public DataResult<Stream<T>> getStream(T object) {
        return this.field_25503.getStream(object);
    }

    public DataResult<Consumer<Consumer<T>>> getList(T object) {
        return this.field_25503.getList(object);
    }

    public T createList(Stream<T> stream) {
        return (T)this.field_25503.createList(stream);
    }

    public DataResult<ByteBuffer> getByteBuffer(T object) {
        return this.field_25503.getByteBuffer(object);
    }

    public T createByteList(ByteBuffer byteBuffer) {
        return (T)this.field_25503.createByteList(byteBuffer);
    }

    public DataResult<IntStream> getIntStream(T object) {
        return this.field_25503.getIntStream(object);
    }

    public T createIntList(IntStream intStream) {
        return (T)this.field_25503.createIntList(intStream);
    }

    public DataResult<LongStream> getLongStream(T object) {
        return this.field_25503.getLongStream(object);
    }

    public T createLongList(LongStream longStream) {
        return (T)this.field_25503.createLongList(longStream);
    }

    public T remove(T object, String string) {
        return (T)this.field_25503.remove(object, string);
    }

    public boolean compressMaps() {
        return this.field_25503.compressMaps();
    }

    public ListBuilder<T> listBuilder() {
        return this.field_25503.listBuilder();
    }

    public RecordBuilder<T> mapBuilder() {
        return this.field_25503.mapBuilder();
    }
}

