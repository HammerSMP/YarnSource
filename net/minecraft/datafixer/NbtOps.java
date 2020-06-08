/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterators
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.PeekingIterator
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.MapLike
 *  com.mojang.serialization.RecordBuilder
 *  com.mojang.serialization.RecordBuilder$AbstractStringBuilder
 *  javax.annotation.Nullable
 */
package net.minecraft.datafixer;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.PeekingIterator;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.nbt.AbstractListTag;
import net.minecraft.nbt.AbstractNumberTag;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

public class NbtOps
implements DynamicOps<Tag> {
    public static final NbtOps INSTANCE = new NbtOps();

    protected NbtOps() {
    }

    public Tag empty() {
        return EndTag.INSTANCE;
    }

    public <U> U convertTo(DynamicOps<U> dynamicOps, Tag arg) {
        switch (arg.getType()) {
            case 0: {
                return (U)dynamicOps.empty();
            }
            case 1: {
                return (U)dynamicOps.createByte(((AbstractNumberTag)arg).getByte());
            }
            case 2: {
                return (U)dynamicOps.createShort(((AbstractNumberTag)arg).getShort());
            }
            case 3: {
                return (U)dynamicOps.createInt(((AbstractNumberTag)arg).getInt());
            }
            case 4: {
                return (U)dynamicOps.createLong(((AbstractNumberTag)arg).getLong());
            }
            case 5: {
                return (U)dynamicOps.createFloat(((AbstractNumberTag)arg).getFloat());
            }
            case 6: {
                return (U)dynamicOps.createDouble(((AbstractNumberTag)arg).getDouble());
            }
            case 7: {
                return (U)dynamicOps.createByteList(ByteBuffer.wrap(((ByteArrayTag)arg).getByteArray()));
            }
            case 8: {
                return (U)dynamicOps.createString(arg.asString());
            }
            case 9: {
                return (U)this.convertList(dynamicOps, arg);
            }
            case 10: {
                return (U)this.convertMap(dynamicOps, arg);
            }
            case 11: {
                return (U)dynamicOps.createIntList(Arrays.stream(((IntArrayTag)arg).getIntArray()));
            }
            case 12: {
                return (U)dynamicOps.createLongList(Arrays.stream(((LongArrayTag)arg).getLongArray()));
            }
        }
        throw new IllegalStateException("Unknown tag type: " + arg);
    }

    public DataResult<Number> getNumberValue(Tag arg) {
        if (arg instanceof AbstractNumberTag) {
            return DataResult.success((Object)((AbstractNumberTag)arg).getNumber());
        }
        return DataResult.error((String)"Not a number");
    }

    public Tag createNumeric(Number number) {
        return DoubleTag.of(number.doubleValue());
    }

    public Tag createByte(byte b) {
        return ByteTag.of(b);
    }

    public Tag createShort(short s) {
        return ShortTag.of(s);
    }

    public Tag createInt(int i) {
        return IntTag.of(i);
    }

    public Tag createLong(long l) {
        return LongTag.of(l);
    }

    public Tag createFloat(float f) {
        return FloatTag.of(f);
    }

    public Tag createDouble(double d) {
        return DoubleTag.of(d);
    }

    public Tag createBoolean(boolean bl) {
        return ByteTag.of(bl);
    }

    public DataResult<String> getStringValue(Tag arg) {
        if (arg instanceof StringTag) {
            return DataResult.success((Object)arg.asString());
        }
        return DataResult.error((String)"Not a string");
    }

    public Tag createString(String string) {
        return StringTag.of(string);
    }

    private static AbstractListTag<?> method_29144(byte b, byte c) {
        if (NbtOps.method_29145(b, c, (byte)4)) {
            return new LongArrayTag(new long[0]);
        }
        if (NbtOps.method_29145(b, c, (byte)1)) {
            return new ByteArrayTag(new byte[0]);
        }
        if (NbtOps.method_29145(b, c, (byte)3)) {
            return new IntArrayTag(new int[0]);
        }
        return new ListTag();
    }

    private static boolean method_29145(byte b, byte c, byte d) {
        return !(b != d && b != 0 || c != d && c != 0);
    }

    private static <T extends Tag> void method_29151(AbstractListTag<T> arg, Tag arg22, Tag arg3) {
        if (arg22 instanceof AbstractListTag) {
            AbstractListTag lv = (AbstractListTag)arg22;
            lv.forEach(arg2 -> arg.add(arg2));
        }
        arg.add(arg3);
    }

    private static <T extends Tag> void method_29150(AbstractListTag<T> arg, Tag arg22, List<Tag> list) {
        if (arg22 instanceof AbstractListTag) {
            AbstractListTag lv = (AbstractListTag)arg22;
            lv.forEach(arg2 -> arg.add(arg2));
        }
        list.forEach(arg2 -> arg.add(arg2));
    }

    public DataResult<Tag> mergeToList(Tag arg, Tag arg2) {
        if (!(arg instanceof AbstractListTag) && !(arg instanceof EndTag)) {
            return DataResult.error((String)("mergeToList called with not a list: " + arg), (Object)arg);
        }
        AbstractListTag<?> lv = NbtOps.method_29144(arg instanceof AbstractListTag ? ((AbstractListTag)arg).getElementType() : (byte)0, arg2.getType());
        NbtOps.method_29151(lv, arg, arg2);
        return DataResult.success(lv);
    }

    public DataResult<Tag> mergeToList(Tag arg, List<Tag> list) {
        if (!(arg instanceof AbstractListTag) && !(arg instanceof EndTag)) {
            return DataResult.error((String)("mergeToList called with not a list: " + arg), (Object)arg);
        }
        AbstractListTag<?> lv = NbtOps.method_29144(arg instanceof AbstractListTag ? ((AbstractListTag)arg).getElementType() : (byte)0, list.stream().findFirst().map(Tag::getType).orElse((byte)0));
        NbtOps.method_29150(lv, arg, list);
        return DataResult.success(lv);
    }

    public DataResult<Tag> mergeToMap(Tag arg, Tag arg2, Tag arg3) {
        if (!(arg instanceof CompoundTag) && !(arg instanceof EndTag)) {
            return DataResult.error((String)("mergeToMap called with not a map: " + arg), (Object)arg);
        }
        if (!(arg2 instanceof StringTag)) {
            return DataResult.error((String)("key is not a string: " + arg2), (Object)arg);
        }
        CompoundTag lv = new CompoundTag();
        if (arg instanceof CompoundTag) {
            CompoundTag lv2 = (CompoundTag)arg;
            lv2.getKeys().forEach(string -> lv.put((String)string, lv2.get((String)string)));
        }
        lv.put(arg2.asString(), arg3);
        return DataResult.success((Object)lv);
    }

    public DataResult<Tag> mergeToMap(Tag arg, MapLike<Tag> mapLike) {
        if (!(arg instanceof CompoundTag) && !(arg instanceof EndTag)) {
            return DataResult.error((String)("mergeToMap called with not a map: " + arg), (Object)arg);
        }
        CompoundTag lv = new CompoundTag();
        if (arg instanceof CompoundTag) {
            CompoundTag lv2 = (CompoundTag)arg;
            lv2.getKeys().forEach(string -> lv.put((String)string, lv2.get((String)string)));
        }
        ArrayList list = Lists.newArrayList();
        mapLike.entries().forEach(pair -> {
            Tag lv = (Tag)pair.getFirst();
            if (!(lv instanceof StringTag)) {
                list.add(lv);
                return;
            }
            lv.put(lv.asString(), (Tag)pair.getSecond());
        });
        if (!list.isEmpty()) {
            return DataResult.error((String)("some keys are not strings: " + list), (Object)lv);
        }
        return DataResult.success((Object)lv);
    }

    public DataResult<Stream<Pair<Tag, Tag>>> getMapValues(Tag arg) {
        if (!(arg instanceof CompoundTag)) {
            return DataResult.error((String)("Not a map: " + arg));
        }
        CompoundTag lv = (CompoundTag)arg;
        return DataResult.success(lv.getKeys().stream().map(string -> Pair.of((Object)this.createString((String)string), (Object)lv.get((String)string))));
    }

    public DataResult<Consumer<BiConsumer<Tag, Tag>>> getMapEntries(Tag arg) {
        if (!(arg instanceof CompoundTag)) {
            return DataResult.error((String)("Not a map: " + arg));
        }
        CompoundTag lv = (CompoundTag)arg;
        return DataResult.success(biConsumer -> lv.getKeys().forEach(string -> biConsumer.accept(this.createString((String)string), lv.get((String)string))));
    }

    public DataResult<MapLike<Tag>> getMap(Tag arg) {
        if (!(arg instanceof CompoundTag)) {
            return DataResult.error((String)("Not a map: " + arg));
        }
        final CompoundTag lv = (CompoundTag)arg;
        return DataResult.success((Object)new MapLike<Tag>(){

            @Nullable
            public Tag get(Tag arg) {
                return lv.get(arg.asString());
            }

            @Nullable
            public Tag get(String string) {
                return lv.get(string);
            }

            public Stream<Pair<Tag, Tag>> entries() {
                return lv.getKeys().stream().map(string -> Pair.of((Object)NbtOps.this.createString((String)string), (Object)lv.get((String)string)));
            }

            public String toString() {
                return "MapLike[" + lv + "]";
            }

            @Nullable
            public /* synthetic */ Object get(String string) {
                return this.get(string);
            }

            @Nullable
            public /* synthetic */ Object get(Object object) {
                return this.get((Tag)object);
            }
        });
    }

    public Tag createMap(Stream<Pair<Tag, Tag>> stream) {
        CompoundTag lv = new CompoundTag();
        stream.forEach(pair -> lv.put(((Tag)pair.getFirst()).asString(), (Tag)pair.getSecond()));
        return lv;
    }

    public DataResult<Stream<Tag>> getStream(Tag arg2) {
        if (arg2 instanceof AbstractListTag) {
            return DataResult.success(((AbstractListTag)arg2).stream().map(arg -> arg));
        }
        return DataResult.error((String)"Not a list");
    }

    public DataResult<Consumer<Consumer<Tag>>> getList(Tag arg) {
        if (arg instanceof AbstractListTag) {
            AbstractListTag lv = (AbstractListTag)arg;
            return DataResult.success(lv::forEach);
        }
        return DataResult.error((String)("Not a list: " + arg));
    }

    public DataResult<ByteBuffer> getByteBuffer(Tag arg) {
        if (arg instanceof ByteArrayTag) {
            return DataResult.success((Object)ByteBuffer.wrap(((ByteArrayTag)arg).getByteArray()));
        }
        return super.getByteBuffer((Object)arg);
    }

    public Tag createByteList(ByteBuffer byteBuffer) {
        return new ByteArrayTag(DataFixUtils.toArray((ByteBuffer)byteBuffer));
    }

    public DataResult<IntStream> getIntStream(Tag arg) {
        if (arg instanceof IntArrayTag) {
            return DataResult.success((Object)Arrays.stream(((IntArrayTag)arg).getIntArray()));
        }
        return super.getIntStream((Object)arg);
    }

    public Tag createIntList(IntStream intStream) {
        return new IntArrayTag(intStream.toArray());
    }

    public DataResult<LongStream> getLongStream(Tag arg) {
        if (arg instanceof LongArrayTag) {
            return DataResult.success((Object)Arrays.stream(((LongArrayTag)arg).getLongArray()));
        }
        return super.getLongStream((Object)arg);
    }

    public Tag createLongList(LongStream longStream) {
        return new LongArrayTag(longStream.toArray());
    }

    public Tag createList(Stream<Tag> stream) {
        PeekingIterator peekingIterator = Iterators.peekingIterator(stream.iterator());
        if (!peekingIterator.hasNext()) {
            return new ListTag();
        }
        Tag lv = (Tag)peekingIterator.peek();
        if (lv instanceof ByteTag) {
            ArrayList list = Lists.newArrayList((Iterator)Iterators.transform((Iterator)peekingIterator, arg -> ((ByteTag)arg).getByte()));
            return new ByteArrayTag(list);
        }
        if (lv instanceof IntTag) {
            ArrayList list2 = Lists.newArrayList((Iterator)Iterators.transform((Iterator)peekingIterator, arg -> ((IntTag)arg).getInt()));
            return new IntArrayTag(list2);
        }
        if (lv instanceof LongTag) {
            ArrayList list3 = Lists.newArrayList((Iterator)Iterators.transform((Iterator)peekingIterator, arg -> ((LongTag)arg).getLong()));
            return new LongArrayTag(list3);
        }
        ListTag lv2 = new ListTag();
        while (peekingIterator.hasNext()) {
            Tag lv3 = (Tag)peekingIterator.next();
            if (lv3 instanceof EndTag) continue;
            lv2.add(lv3);
        }
        return lv2;
    }

    public Tag remove(Tag arg, String string3) {
        if (arg instanceof CompoundTag) {
            CompoundTag lv = (CompoundTag)arg;
            CompoundTag lv2 = new CompoundTag();
            lv.getKeys().stream().filter(string2 -> !Objects.equals(string2, string3)).forEach(string -> lv2.put((String)string, lv.get((String)string)));
            return lv2;
        }
        return arg;
    }

    public String toString() {
        return "NBT";
    }

    public RecordBuilder<Tag> mapBuilder() {
        return new MapBuilder();
    }

    public /* synthetic */ Object remove(Object object, String string) {
        return this.remove((Tag)object, string);
    }

    public /* synthetic */ Object createLongList(LongStream longStream) {
        return this.createLongList(longStream);
    }

    public /* synthetic */ DataResult getLongStream(Object object) {
        return this.getLongStream((Tag)object);
    }

    public /* synthetic */ Object createIntList(IntStream intStream) {
        return this.createIntList(intStream);
    }

    public /* synthetic */ DataResult getIntStream(Object object) {
        return this.getIntStream((Tag)object);
    }

    public /* synthetic */ Object createByteList(ByteBuffer byteBuffer) {
        return this.createByteList(byteBuffer);
    }

    public /* synthetic */ DataResult getByteBuffer(Object object) {
        return this.getByteBuffer((Tag)object);
    }

    public /* synthetic */ Object createList(Stream stream) {
        return this.createList(stream);
    }

    public /* synthetic */ DataResult getList(Object object) {
        return this.getList((Tag)object);
    }

    public /* synthetic */ DataResult getStream(Object object) {
        return this.getStream((Tag)object);
    }

    public /* synthetic */ DataResult getMap(Object object) {
        return this.getMap((Tag)object);
    }

    public /* synthetic */ Object createMap(Stream stream) {
        return this.createMap(stream);
    }

    public /* synthetic */ DataResult getMapEntries(Object object) {
        return this.getMapEntries((Tag)object);
    }

    public /* synthetic */ DataResult getMapValues(Object object) {
        return this.getMapValues((Tag)object);
    }

    public /* synthetic */ DataResult mergeToMap(Object object, MapLike mapLike) {
        return this.mergeToMap((Tag)object, (MapLike<Tag>)mapLike);
    }

    public /* synthetic */ DataResult mergeToMap(Object object, Object object2, Object object3) {
        return this.mergeToMap((Tag)object, (Tag)object2, (Tag)object3);
    }

    public /* synthetic */ DataResult mergeToList(Object object, List list) {
        return this.mergeToList((Tag)object, (List<Tag>)list);
    }

    public /* synthetic */ DataResult mergeToList(Object object, Object object2) {
        return this.mergeToList((Tag)object, (Tag)object2);
    }

    public /* synthetic */ Object createString(String string) {
        return this.createString(string);
    }

    public /* synthetic */ DataResult getStringValue(Object object) {
        return this.getStringValue((Tag)object);
    }

    public /* synthetic */ Object createBoolean(boolean bl) {
        return this.createBoolean(bl);
    }

    public /* synthetic */ Object createDouble(double d) {
        return this.createDouble(d);
    }

    public /* synthetic */ Object createFloat(float f) {
        return this.createFloat(f);
    }

    public /* synthetic */ Object createLong(long l) {
        return this.createLong(l);
    }

    public /* synthetic */ Object createInt(int i) {
        return this.createInt(i);
    }

    public /* synthetic */ Object createShort(short s) {
        return this.createShort(s);
    }

    public /* synthetic */ Object createByte(byte b) {
        return this.createByte(b);
    }

    public /* synthetic */ Object createNumeric(Number number) {
        return this.createNumeric(number);
    }

    public /* synthetic */ DataResult getNumberValue(Object object) {
        return this.getNumberValue((Tag)object);
    }

    public /* synthetic */ Object convertTo(DynamicOps dynamicOps, Object object) {
        return this.convertTo(dynamicOps, (Tag)object);
    }

    public /* synthetic */ Object empty() {
        return this.empty();
    }

    class MapBuilder
    extends RecordBuilder.AbstractStringBuilder<Tag, CompoundTag> {
        protected MapBuilder() {
            super((DynamicOps)NbtOps.this);
        }

        protected CompoundTag initBuilder() {
            return new CompoundTag();
        }

        protected CompoundTag append(String string, Tag arg, CompoundTag arg2) {
            arg2.put(string, arg);
            return arg2;
        }

        protected DataResult<Tag> build(CompoundTag arg, Tag arg2) {
            if (arg2 == null || arg2 == EndTag.INSTANCE) {
                return DataResult.success((Object)arg);
            }
            if (arg2 instanceof CompoundTag) {
                CompoundTag lv = new CompoundTag(Maps.newHashMap(((CompoundTag)arg2).method_29143()));
                for (Map.Entry<String, Tag> entry : arg.method_29143().entrySet()) {
                    lv.put(entry.getKey(), entry.getValue());
                }
                return DataResult.success((Object)lv);
            }
            return DataResult.error((String)("mergeToMap called with not a map: " + arg2), (Object)arg2);
        }

        protected /* synthetic */ Object append(String string, Object object, Object object2) {
            return this.append(string, (Tag)object, (CompoundTag)object2);
        }

        protected /* synthetic */ DataResult build(Object object, Object object2) {
            return this.build((CompoundTag)object, (Tag)object2);
        }

        protected /* synthetic */ Object initBuilder() {
            return this.initBuilder();
        }
    }
}

