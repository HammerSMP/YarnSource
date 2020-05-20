/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  org.apache.commons.lang3.ArrayUtils
 */
package net.minecraft.nbt;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.nbt.AbstractListTag;
import net.minecraft.nbt.AbstractNumberTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.PositionTracker;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagReader;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.ArrayUtils;

public class LongArrayTag
extends AbstractListTag<LongTag> {
    public static final TagReader<LongArrayTag> READER = new TagReader<LongArrayTag>(){

        @Override
        public LongArrayTag read(DataInput dataInput, int i, PositionTracker arg) throws IOException {
            arg.add(192L);
            int j = dataInput.readInt();
            arg.add(64L * (long)j);
            long[] ls = new long[j];
            for (int k = 0; k < j; ++k) {
                ls[k] = dataInput.readLong();
            }
            return new LongArrayTag(ls);
        }

        @Override
        public String getCrashReportName() {
            return "LONG[]";
        }

        @Override
        public String getCommandFeedbackName() {
            return "TAG_Long_Array";
        }

        @Override
        public /* synthetic */ Tag read(DataInput dataInput, int i, PositionTracker arg) throws IOException {
            return this.read(dataInput, i, arg);
        }
    };
    private long[] value;

    public LongArrayTag(long[] ls) {
        this.value = ls;
    }

    public LongArrayTag(LongSet longSet) {
        this.value = longSet.toLongArray();
    }

    public LongArrayTag(List<Long> list) {
        this(LongArrayTag.toArray(list));
    }

    private static long[] toArray(List<Long> list) {
        long[] ls = new long[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            Long lv = list.get(i);
            ls[i] = lv == null ? 0L : lv;
        }
        return ls;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(this.value.length);
        for (long l : this.value) {
            dataOutput.writeLong(l);
        }
    }

    @Override
    public byte getType() {
        return 12;
    }

    public TagReader<LongArrayTag> getReader() {
        return READER;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("[L;");
        for (int i = 0; i < this.value.length; ++i) {
            if (i != 0) {
                stringBuilder.append(',');
            }
            stringBuilder.append(this.value[i]).append('L');
        }
        return stringBuilder.append(']').toString();
    }

    @Override
    public LongArrayTag copy() {
        long[] ls = new long[this.value.length];
        System.arraycopy(this.value, 0, ls, 0, this.value.length);
        return new LongArrayTag(ls);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        return object instanceof LongArrayTag && Arrays.equals(this.value, ((LongArrayTag)object).value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.value);
    }

    @Override
    public Text toText(String string, int i) {
        MutableText lv = new LiteralText("L").formatted(RED);
        MutableText lv2 = new LiteralText("[").append(lv).append(";");
        for (int j = 0; j < this.value.length; ++j) {
            MutableText lv3 = new LiteralText(String.valueOf(this.value[j])).formatted(GOLD);
            lv2.append(" ").append(lv3).append(lv);
            if (j == this.value.length - 1) continue;
            lv2.append(",");
        }
        lv2.append("]");
        return lv2;
    }

    public long[] getLongArray() {
        return this.value;
    }

    @Override
    public int size() {
        return this.value.length;
    }

    @Override
    public LongTag get(int i) {
        return LongTag.of(this.value[i]);
    }

    @Override
    public LongTag set(int i, LongTag arg) {
        long l = this.value[i];
        this.value[i] = arg.getLong();
        return LongTag.of(l);
    }

    @Override
    public void add(int i, LongTag arg) {
        this.value = ArrayUtils.add((long[])this.value, (int)i, (long)arg.getLong());
    }

    @Override
    public boolean setTag(int i, Tag arg) {
        if (arg instanceof AbstractNumberTag) {
            this.value[i] = ((AbstractNumberTag)arg).getLong();
            return true;
        }
        return false;
    }

    @Override
    public boolean addTag(int i, Tag arg) {
        if (arg instanceof AbstractNumberTag) {
            this.value = ArrayUtils.add((long[])this.value, (int)i, (long)((AbstractNumberTag)arg).getLong());
            return true;
        }
        return false;
    }

    public LongTag method_10536(int i) {
        long l = this.value[i];
        this.value = ArrayUtils.remove((long[])this.value, (int)i);
        return LongTag.of(l);
    }

    @Override
    public byte getElementType() {
        return 4;
    }

    @Override
    public void clear() {
        this.value = new long[0];
    }

    @Override
    public /* synthetic */ Tag remove(int i) {
        return this.method_10536(i);
    }

    @Override
    public /* synthetic */ void add(int i, Tag arg) {
        this.add(i, (LongTag)arg);
    }

    @Override
    public /* synthetic */ Tag set(int i, Tag arg) {
        return this.set(i, (LongTag)arg);
    }

    @Override
    public /* synthetic */ Tag copy() {
        return this.copy();
    }

    @Override
    public /* synthetic */ Object remove(int i) {
        return this.method_10536(i);
    }

    @Override
    public /* synthetic */ void add(int i, Object object) {
        this.add(i, (LongTag)object);
    }

    @Override
    public /* synthetic */ Object set(int i, Object object) {
        return this.set(i, (LongTag)object);
    }

    @Override
    public /* synthetic */ Object get(int i) {
        return this.get(i);
    }
}

