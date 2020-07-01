/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.nbt;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.nbt.AbstractNumberTag;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.PositionTracker;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagReader;
import net.minecraft.nbt.TagReaders;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CompoundTag
implements Tag {
    public static final Codec<CompoundTag> field_25128 = Codec.PASSTHROUGH.comapFlatMap(dynamic -> {
        Tag lv = (Tag)dynamic.convert((DynamicOps)NbtOps.INSTANCE).getValue();
        if (lv instanceof CompoundTag) {
            return DataResult.success((Object)((CompoundTag)lv));
        }
        return DataResult.error((String)("Not a compound tag: " + lv));
    }, arg -> new Dynamic((DynamicOps)NbtOps.INSTANCE, arg));
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Pattern PATTERN = Pattern.compile("[A-Za-z0-9._+-]+");
    public static final TagReader<CompoundTag> READER = new TagReader<CompoundTag>(){

        @Override
        public CompoundTag read(DataInput dataInput, int i, PositionTracker arg) throws IOException {
            byte b;
            arg.add(384L);
            if (i > 512) {
                throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
            }
            HashMap map = Maps.newHashMap();
            while ((b = CompoundTag.readByte(dataInput, arg)) != 0) {
                String string = CompoundTag.readString(dataInput, arg);
                arg.add(224 + 16 * string.length());
                Tag lv = CompoundTag.read(TagReaders.of(b), string, dataInput, i + 1, arg);
                if (map.put(string, lv) == null) continue;
                arg.add(288L);
            }
            return new CompoundTag(map);
        }

        @Override
        public String getCrashReportName() {
            return "COMPOUND";
        }

        @Override
        public String getCommandFeedbackName() {
            return "TAG_Compound";
        }

        @Override
        public /* synthetic */ Tag read(DataInput dataInput, int i, PositionTracker arg) throws IOException {
            return this.read(dataInput, i, arg);
        }
    };
    private final Map<String, Tag> tags;

    protected CompoundTag(Map<String, Tag> map) {
        this.tags = map;
    }

    public CompoundTag() {
        this(Maps.newHashMap());
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        for (String string : this.tags.keySet()) {
            Tag lv = this.tags.get(string);
            CompoundTag.write(string, lv, dataOutput);
        }
        dataOutput.writeByte(0);
    }

    public Set<String> getKeys() {
        return this.tags.keySet();
    }

    @Override
    public byte getType() {
        return 10;
    }

    public TagReader<CompoundTag> getReader() {
        return READER;
    }

    public int getSize() {
        return this.tags.size();
    }

    @Nullable
    public Tag put(String string, Tag arg) {
        return this.tags.put(string, arg);
    }

    public void putByte(String string, byte b) {
        this.tags.put(string, ByteTag.of(b));
    }

    public void putShort(String string, short s) {
        this.tags.put(string, ShortTag.of(s));
    }

    public void putInt(String string, int i) {
        this.tags.put(string, IntTag.of(i));
    }

    public void putLong(String string, long l) {
        this.tags.put(string, LongTag.of(l));
    }

    public void putUuid(String string, UUID uUID) {
        this.tags.put(string, NbtHelper.fromUuid(uUID));
    }

    public UUID getUuid(String string) {
        return NbtHelper.toUuid(this.get(string));
    }

    public boolean containsUuid(String string) {
        Tag lv = this.get(string);
        return lv != null && lv.getReader() == IntArrayTag.READER && ((IntArrayTag)lv).getIntArray().length == 4;
    }

    public void putFloat(String string, float f) {
        this.tags.put(string, FloatTag.of(f));
    }

    public void putDouble(String string, double d) {
        this.tags.put(string, DoubleTag.of(d));
    }

    public void putString(String string, String string2) {
        this.tags.put(string, StringTag.of(string2));
    }

    public void putByteArray(String string, byte[] bs) {
        this.tags.put(string, new ByteArrayTag(bs));
    }

    public void putIntArray(String string, int[] is) {
        this.tags.put(string, new IntArrayTag(is));
    }

    public void putIntArray(String string, List<Integer> list) {
        this.tags.put(string, new IntArrayTag(list));
    }

    public void putLongArray(String string, long[] ls) {
        this.tags.put(string, new LongArrayTag(ls));
    }

    public void putLongArray(String string, List<Long> list) {
        this.tags.put(string, new LongArrayTag(list));
    }

    public void putBoolean(String string, boolean bl) {
        this.tags.put(string, ByteTag.of(bl));
    }

    @Nullable
    public Tag get(String string) {
        return this.tags.get(string);
    }

    public byte getType(String string) {
        Tag lv = this.tags.get(string);
        if (lv == null) {
            return 0;
        }
        return lv.getType();
    }

    public boolean contains(String string) {
        return this.tags.containsKey(string);
    }

    public boolean contains(String string, int i) {
        byte j = this.getType(string);
        if (j == i) {
            return true;
        }
        if (i == 99) {
            return j == 1 || j == 2 || j == 3 || j == 4 || j == 5 || j == 6;
        }
        return false;
    }

    public byte getByte(String string) {
        try {
            if (this.contains(string, 99)) {
                return ((AbstractNumberTag)this.tags.get(string)).getByte();
            }
        }
        catch (ClassCastException classCastException) {
            // empty catch block
        }
        return 0;
    }

    public short getShort(String string) {
        try {
            if (this.contains(string, 99)) {
                return ((AbstractNumberTag)this.tags.get(string)).getShort();
            }
        }
        catch (ClassCastException classCastException) {
            // empty catch block
        }
        return 0;
    }

    public int getInt(String string) {
        try {
            if (this.contains(string, 99)) {
                return ((AbstractNumberTag)this.tags.get(string)).getInt();
            }
        }
        catch (ClassCastException classCastException) {
            // empty catch block
        }
        return 0;
    }

    public long getLong(String string) {
        try {
            if (this.contains(string, 99)) {
                return ((AbstractNumberTag)this.tags.get(string)).getLong();
            }
        }
        catch (ClassCastException classCastException) {
            // empty catch block
        }
        return 0L;
    }

    public float getFloat(String string) {
        try {
            if (this.contains(string, 99)) {
                return ((AbstractNumberTag)this.tags.get(string)).getFloat();
            }
        }
        catch (ClassCastException classCastException) {
            // empty catch block
        }
        return 0.0f;
    }

    public double getDouble(String string) {
        try {
            if (this.contains(string, 99)) {
                return ((AbstractNumberTag)this.tags.get(string)).getDouble();
            }
        }
        catch (ClassCastException classCastException) {
            // empty catch block
        }
        return 0.0;
    }

    public String getString(String string) {
        try {
            if (this.contains(string, 8)) {
                return this.tags.get(string).asString();
            }
        }
        catch (ClassCastException classCastException) {
            // empty catch block
        }
        return "";
    }

    public byte[] getByteArray(String string) {
        try {
            if (this.contains(string, 7)) {
                return ((ByteArrayTag)this.tags.get(string)).getByteArray();
            }
        }
        catch (ClassCastException classCastException) {
            throw new CrashException(this.createCrashReport(string, ByteArrayTag.READER, classCastException));
        }
        return new byte[0];
    }

    public int[] getIntArray(String string) {
        try {
            if (this.contains(string, 11)) {
                return ((IntArrayTag)this.tags.get(string)).getIntArray();
            }
        }
        catch (ClassCastException classCastException) {
            throw new CrashException(this.createCrashReport(string, IntArrayTag.READER, classCastException));
        }
        return new int[0];
    }

    public long[] getLongArray(String string) {
        try {
            if (this.contains(string, 12)) {
                return ((LongArrayTag)this.tags.get(string)).getLongArray();
            }
        }
        catch (ClassCastException classCastException) {
            throw new CrashException(this.createCrashReport(string, LongArrayTag.READER, classCastException));
        }
        return new long[0];
    }

    public CompoundTag getCompound(String string) {
        try {
            if (this.contains(string, 10)) {
                return (CompoundTag)this.tags.get(string);
            }
        }
        catch (ClassCastException classCastException) {
            throw new CrashException(this.createCrashReport(string, READER, classCastException));
        }
        return new CompoundTag();
    }

    public ListTag getList(String string, int i) {
        try {
            if (this.getType(string) == 9) {
                ListTag lv = (ListTag)this.tags.get(string);
                if (lv.isEmpty() || lv.getElementType() == i) {
                    return lv;
                }
                return new ListTag();
            }
        }
        catch (ClassCastException classCastException) {
            throw new CrashException(this.createCrashReport(string, ListTag.READER, classCastException));
        }
        return new ListTag();
    }

    public boolean getBoolean(String string) {
        return this.getByte(string) != 0;
    }

    public void remove(String string) {
        this.tags.remove(string);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{");
        Collection<String> collection = this.tags.keySet();
        if (LOGGER.isDebugEnabled()) {
            ArrayList list = Lists.newArrayList(this.tags.keySet());
            Collections.sort(list);
            collection = list;
        }
        for (String string : collection) {
            if (stringBuilder.length() != 1) {
                stringBuilder.append(',');
            }
            stringBuilder.append(CompoundTag.escapeTagKey(string)).append(':').append(this.tags.get(string));
        }
        return stringBuilder.append('}').toString();
    }

    public boolean isEmpty() {
        return this.tags.isEmpty();
    }

    private CrashReport createCrashReport(String string, TagReader<?> arg, ClassCastException classCastException) {
        CrashReport lv = CrashReport.create(classCastException, "Reading NBT data");
        CrashReportSection lv2 = lv.addElement("Corrupt NBT tag", 1);
        lv2.add("Tag type found", () -> this.tags.get(string).getReader().getCrashReportName());
        lv2.add("Tag type expected", arg::getCrashReportName);
        lv2.add("Tag name", string);
        return lv;
    }

    @Override
    public CompoundTag copy() {
        HashMap map = Maps.newHashMap((Map)Maps.transformValues(this.tags, Tag::copy));
        return new CompoundTag(map);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        return object instanceof CompoundTag && Objects.equals(this.tags, ((CompoundTag)object).tags);
    }

    public int hashCode() {
        return this.tags.hashCode();
    }

    private static void write(String string, Tag arg, DataOutput dataOutput) throws IOException {
        dataOutput.writeByte(arg.getType());
        if (arg.getType() == 0) {
            return;
        }
        dataOutput.writeUTF(string);
        arg.write(dataOutput);
    }

    private static byte readByte(DataInput dataInput, PositionTracker arg) throws IOException {
        return dataInput.readByte();
    }

    private static String readString(DataInput dataInput, PositionTracker arg) throws IOException {
        return dataInput.readUTF();
    }

    private static Tag read(TagReader<?> arg, String string, DataInput dataInput, int i, PositionTracker arg2) {
        try {
            return arg.read(dataInput, i, arg2);
        }
        catch (IOException iOException) {
            CrashReport lv = CrashReport.create(iOException, "Loading NBT data");
            CrashReportSection lv2 = lv.addElement("NBT Tag");
            lv2.add("Tag name", string);
            lv2.add("Tag type", arg.getCrashReportName());
            throw new CrashException(lv);
        }
    }

    public CompoundTag copyFrom(CompoundTag arg) {
        for (String string : arg.tags.keySet()) {
            Tag lv = arg.tags.get(string);
            if (lv.getType() == 10) {
                if (this.contains(string, 10)) {
                    CompoundTag lv2 = this.getCompound(string);
                    lv2.copyFrom((CompoundTag)lv);
                    continue;
                }
                this.put(string, lv.copy());
                continue;
            }
            this.put(string, lv.copy());
        }
        return this;
    }

    protected static String escapeTagKey(String string) {
        if (PATTERN.matcher(string).matches()) {
            return string;
        }
        return StringTag.escape(string);
    }

    protected static Text prettyPrintTagKey(String string) {
        if (PATTERN.matcher(string).matches()) {
            return new LiteralText(string).formatted(AQUA);
        }
        String string2 = StringTag.escape(string);
        String string3 = string2.substring(0, 1);
        MutableText lv = new LiteralText(string2.substring(1, string2.length() - 1)).formatted(AQUA);
        return new LiteralText(string3).append(lv).append(string3);
    }

    @Override
    public Text toText(String string, int i) {
        if (this.tags.isEmpty()) {
            return new LiteralText("{}");
        }
        LiteralText lv = new LiteralText("{");
        Collection<String> collection = this.tags.keySet();
        if (LOGGER.isDebugEnabled()) {
            ArrayList list = Lists.newArrayList(this.tags.keySet());
            Collections.sort(list);
            collection = list;
        }
        if (!string.isEmpty()) {
            lv.append("\n");
        }
        Iterator iterator = collection.iterator();
        while (iterator.hasNext()) {
            String string2 = (String)iterator.next();
            MutableText lv2 = new LiteralText(Strings.repeat((String)string, (int)(i + 1))).append(CompoundTag.prettyPrintTagKey(string2)).append(String.valueOf(':')).append(" ").append(this.tags.get(string2).toText(string, i + 1));
            if (iterator.hasNext()) {
                lv2.append(String.valueOf(',')).append(string.isEmpty() ? " " : "\n");
            }
            lv.append(lv2);
        }
        if (!string.isEmpty()) {
            lv.append("\n").append(Strings.repeat((String)string, (int)i));
        }
        lv.append("}");
        return lv;
    }

    protected Map<String, Tag> method_29143() {
        return Collections.unmodifiableMap(this.tags);
    }

    @Override
    public /* synthetic */ Tag copy() {
        return this.copy();
    }
}

