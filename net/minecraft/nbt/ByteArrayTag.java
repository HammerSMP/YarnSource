/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ArrayUtils
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.nbt.AbstractListTag;
import net.minecraft.nbt.AbstractNumberTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.PositionTracker;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagReader;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.ArrayUtils;

public class ByteArrayTag
extends AbstractListTag<ByteTag> {
    public static final TagReader<ByteArrayTag> READER = new TagReader<ByteArrayTag>(){

        @Override
        public ByteArrayTag read(DataInput dataInput, int i, PositionTracker arg) throws IOException {
            arg.add(192L);
            int j = dataInput.readInt();
            arg.add(8L * (long)j);
            byte[] bs = new byte[j];
            dataInput.readFully(bs);
            return new ByteArrayTag(bs);
        }

        @Override
        public String getCrashReportName() {
            return "BYTE[]";
        }

        @Override
        public String getCommandFeedbackName() {
            return "TAG_Byte_Array";
        }

        @Override
        public /* synthetic */ Tag read(DataInput dataInput, int i, PositionTracker arg) throws IOException {
            return this.read(dataInput, i, arg);
        }
    };
    private byte[] value;

    public ByteArrayTag(byte[] bs) {
        this.value = bs;
    }

    public ByteArrayTag(List<Byte> list) {
        this(ByteArrayTag.toArray(list));
    }

    private static byte[] toArray(List<Byte> list) {
        byte[] bs = new byte[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            Byte lv = list.get(i);
            bs[i] = lv == null ? (byte)0 : lv;
        }
        return bs;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(this.value.length);
        dataOutput.write(this.value);
    }

    @Override
    public byte getType() {
        return 7;
    }

    public TagReader<ByteArrayTag> getReader() {
        return READER;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("[B;");
        for (int i = 0; i < this.value.length; ++i) {
            if (i != 0) {
                stringBuilder.append(',');
            }
            stringBuilder.append(this.value[i]).append('B');
        }
        return stringBuilder.append(']').toString();
    }

    @Override
    public Tag copy() {
        byte[] bs = new byte[this.value.length];
        System.arraycopy(this.value, 0, bs, 0, this.value.length);
        return new ByteArrayTag(bs);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        return object instanceof ByteArrayTag && Arrays.equals(this.value, ((ByteArrayTag)object).value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.value);
    }

    @Override
    public Text toText(String string, int i) {
        MutableText lv = new LiteralText("B").formatted(RED);
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

    public byte[] getByteArray() {
        return this.value;
    }

    @Override
    public int size() {
        return this.value.length;
    }

    @Override
    public ByteTag get(int i) {
        return ByteTag.of(this.value[i]);
    }

    @Override
    public ByteTag set(int i, ByteTag arg) {
        byte b = this.value[i];
        this.value[i] = arg.getByte();
        return ByteTag.of(b);
    }

    @Override
    public void add(int i, ByteTag arg) {
        this.value = ArrayUtils.add((byte[])this.value, (int)i, (byte)arg.getByte());
    }

    @Override
    public boolean setTag(int i, Tag arg) {
        if (arg instanceof AbstractNumberTag) {
            this.value[i] = ((AbstractNumberTag)arg).getByte();
            return true;
        }
        return false;
    }

    @Override
    public boolean addTag(int i, Tag arg) {
        if (arg instanceof AbstractNumberTag) {
            this.value = ArrayUtils.add((byte[])this.value, (int)i, (byte)((AbstractNumberTag)arg).getByte());
            return true;
        }
        return false;
    }

    @Override
    public ByteTag remove(int i) {
        byte b = this.value[i];
        this.value = ArrayUtils.remove((byte[])this.value, (int)i);
        return ByteTag.of(b);
    }

    @Override
    public void clear() {
        this.value = new byte[0];
    }

    @Override
    public /* synthetic */ Tag remove(int i) {
        return this.remove(i);
    }

    @Override
    public /* synthetic */ void add(int i, Tag arg) {
        this.add(i, (ByteTag)arg);
    }

    @Override
    public /* synthetic */ Tag set(int i, Tag arg) {
        return this.set(i, (ByteTag)arg);
    }

    @Override
    public /* synthetic */ Object remove(int i) {
        return this.remove(i);
    }

    @Override
    public /* synthetic */ void add(int i, Object object) {
        this.add(i, (ByteTag)object);
    }

    @Override
    public /* synthetic */ Object set(int i, Object object) {
        return this.set(i, (ByteTag)object);
    }

    @Override
    public /* synthetic */ Object get(int i) {
        return this.get(i);
    }
}

