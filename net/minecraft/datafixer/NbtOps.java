/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterators
 *  com.google.common.collect.Lists
 *  com.google.common.collect.PeekingIterator
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.types.DynamicOps
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.datafixer;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
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

    public Type<?> getType(Tag arg) {
        switch (arg.getType()) {
            case 0: {
                return DSL.nilType();
            }
            case 1: {
                return DSL.byteType();
            }
            case 2: {
                return DSL.shortType();
            }
            case 3: {
                return DSL.intType();
            }
            case 4: {
                return DSL.longType();
            }
            case 5: {
                return DSL.floatType();
            }
            case 6: {
                return DSL.doubleType();
            }
            case 7: {
                return DSL.list((Type)DSL.byteType());
            }
            case 8: {
                return DSL.string();
            }
            case 9: {
                return DSL.list((Type)DSL.remainderType());
            }
            case 10: {
                return DSL.compoundList((Type)DSL.remainderType(), (Type)DSL.remainderType());
            }
            case 11: {
                return DSL.list((Type)DSL.intType());
            }
            case 12: {
                return DSL.list((Type)DSL.longType());
            }
        }
        return DSL.remainderType();
    }

    public Optional<Number> getNumberValue(Tag arg) {
        if (arg instanceof AbstractNumberTag) {
            return Optional.of(((AbstractNumberTag)arg).getNumber());
        }
        return Optional.empty();
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

    public Optional<String> getStringValue(Tag arg) {
        if (arg instanceof StringTag) {
            return Optional.of(arg.asString());
        }
        return Optional.empty();
    }

    public Tag createString(String string) {
        return StringTag.of(string);
    }

    /*
     * WARNING - void declaration
     */
    public Tag mergeInto(Tag arg, Tag arg2) {
        void lv6;
        if (arg2 instanceof EndTag) {
            return arg;
        }
        if (arg instanceof CompoundTag) {
            if (arg2 instanceof CompoundTag) {
                CompoundTag lv = new CompoundTag();
                CompoundTag lv2 = (CompoundTag)arg;
                for (String string : lv2.getKeys()) {
                    lv.put(string, lv2.get(string));
                }
                CompoundTag lv3 = (CompoundTag)arg2;
                for (String string2 : lv3.getKeys()) {
                    lv.put(string2, lv3.get(string2));
                }
                return lv;
            }
            return arg;
        }
        if (arg instanceof EndTag) {
            throw new IllegalArgumentException("mergeInto called with a null input.");
        }
        if (!(arg instanceof AbstractListTag)) {
            return arg;
        }
        ListTag lv4 = new ListTag();
        AbstractListTag lv5 = (AbstractListTag)arg;
        lv4.addAll(lv5);
        lv6.add(arg2);
        return lv6;
    }

    /*
     * WARNING - void declaration
     */
    public Tag mergeInto(Tag arg, Tag arg2, Tag arg3) {
        void lv4;
        if (arg instanceof EndTag) {
            CompoundTag lv = new CompoundTag();
        } else if (arg instanceof CompoundTag) {
            CompoundTag lv2 = (CompoundTag)arg;
            CompoundTag lv3 = new CompoundTag();
            lv2.getKeys().forEach(string -> lv3.put((String)string, lv2.get((String)string)));
        } else {
            return arg;
        }
        lv4.put(arg2.asString(), arg3);
        return lv4;
    }

    public Tag merge(Tag arg, Tag arg2) {
        if (arg instanceof EndTag) {
            return arg2;
        }
        if (arg2 instanceof EndTag) {
            return arg;
        }
        if (arg instanceof CompoundTag && arg2 instanceof CompoundTag) {
            CompoundTag lv = (CompoundTag)arg;
            CompoundTag lv2 = (CompoundTag)arg2;
            CompoundTag lv3 = new CompoundTag();
            lv.getKeys().forEach(string -> lv3.put((String)string, lv.get((String)string)));
            lv2.getKeys().forEach(string -> lv3.put((String)string, lv2.get((String)string)));
            return lv3;
        }
        if (arg instanceof AbstractListTag && arg2 instanceof AbstractListTag) {
            ListTag lv4 = new ListTag();
            lv4.addAll((AbstractListTag)arg);
            lv4.addAll((AbstractListTag)arg2);
            return lv4;
        }
        throw new IllegalArgumentException("Could not merge " + arg + " and " + arg2);
    }

    public Optional<Map<Tag, Tag>> getMapValues(Tag arg) {
        if (arg instanceof CompoundTag) {
            CompoundTag lv = (CompoundTag)arg;
            return Optional.of(lv.getKeys().stream().map(string -> Pair.of((Object)this.createString((String)string), (Object)lv.get((String)string))).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
        }
        return Optional.empty();
    }

    public Tag createMap(Map<Tag, Tag> map) {
        CompoundTag lv = new CompoundTag();
        for (Map.Entry<Tag, Tag> entry : map.entrySet()) {
            lv.put(entry.getKey().asString(), entry.getValue());
        }
        return lv;
    }

    public Optional<Stream<Tag>> getStream(Tag arg2) {
        if (arg2 instanceof AbstractListTag) {
            return Optional.of(((AbstractListTag)arg2).stream().map(arg -> arg));
        }
        return Optional.empty();
    }

    public Optional<ByteBuffer> getByteBuffer(Tag arg) {
        if (arg instanceof ByteArrayTag) {
            return Optional.of(ByteBuffer.wrap(((ByteArrayTag)arg).getByteArray()));
        }
        return super.getByteBuffer((Object)arg);
    }

    public Tag createByteList(ByteBuffer byteBuffer) {
        return new ByteArrayTag(DataFixUtils.toArray((ByteBuffer)byteBuffer));
    }

    public Optional<IntStream> getIntStream(Tag arg) {
        if (arg instanceof IntArrayTag) {
            return Optional.of(Arrays.stream(((IntArrayTag)arg).getIntArray()));
        }
        return super.getIntStream((Object)arg);
    }

    public Tag createIntList(IntStream intStream) {
        return new IntArrayTag(intStream.toArray());
    }

    public Optional<LongStream> getLongStream(Tag arg) {
        if (arg instanceof LongArrayTag) {
            return Optional.of(Arrays.stream(((LongArrayTag)arg).getLongArray()));
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

    public /* synthetic */ Object remove(Object object, String string) {
        return this.remove((Tag)object, string);
    }

    public /* synthetic */ Object createLongList(LongStream longStream) {
        return this.createLongList(longStream);
    }

    public /* synthetic */ Optional getLongStream(Object object) {
        return this.getLongStream((Tag)object);
    }

    public /* synthetic */ Object createIntList(IntStream intStream) {
        return this.createIntList(intStream);
    }

    public /* synthetic */ Optional getIntStream(Object object) {
        return this.getIntStream((Tag)object);
    }

    public /* synthetic */ Object createByteList(ByteBuffer byteBuffer) {
        return this.createByteList(byteBuffer);
    }

    public /* synthetic */ Optional getByteBuffer(Object object) {
        return this.getByteBuffer((Tag)object);
    }

    public /* synthetic */ Object createList(Stream stream) {
        return this.createList(stream);
    }

    public /* synthetic */ Optional getStream(Object object) {
        return this.getStream((Tag)object);
    }

    public /* synthetic */ Object createMap(Map map) {
        return this.createMap(map);
    }

    public /* synthetic */ Optional getMapValues(Object object) {
        return this.getMapValues((Tag)object);
    }

    public /* synthetic */ Object merge(Object object, Object object2) {
        return this.merge((Tag)object, (Tag)object2);
    }

    public /* synthetic */ Object mergeInto(Object object, Object object2, Object object3) {
        return this.mergeInto((Tag)object, (Tag)object2, (Tag)object3);
    }

    public /* synthetic */ Object mergeInto(Object object, Object object2) {
        return this.mergeInto((Tag)object, (Tag)object2);
    }

    public /* synthetic */ Object createString(String string) {
        return this.createString(string);
    }

    public /* synthetic */ Optional getStringValue(Object object) {
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

    public /* synthetic */ Optional getNumberValue(Object object) {
        return this.getNumberValue((Tag)object);
    }

    public /* synthetic */ Type getType(Object object) {
        return this.getType((Tag)object);
    }

    public /* synthetic */ Object empty() {
        return this.empty();
    }
}

