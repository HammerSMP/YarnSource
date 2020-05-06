/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.AbstractNumberTag;
import net.minecraft.nbt.PositionTracker;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagReader;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class LongTag
extends AbstractNumberTag {
    public static final TagReader<LongTag> READER = new TagReader<LongTag>(){

        @Override
        public LongTag read(DataInput dataInput, int i, PositionTracker arg) throws IOException {
            arg.add(128L);
            return LongTag.of(dataInput.readLong());
        }

        @Override
        public String getCrashReportName() {
            return "LONG";
        }

        @Override
        public String getCommandFeedbackName() {
            return "TAG_Long";
        }

        @Override
        public boolean isImmutable() {
            return true;
        }

        @Override
        public /* synthetic */ Tag read(DataInput dataInput, int i, PositionTracker arg) throws IOException {
            return this.read(dataInput, i, arg);
        }
    };
    private final long value;

    private LongTag(long l) {
        this.value = l;
    }

    public static LongTag of(long l) {
        if (l >= -128L && l <= 1024L) {
            return Cache.VALUES[(int)l + 128];
        }
        return new LongTag(l);
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeLong(this.value);
    }

    @Override
    public byte getType() {
        return 4;
    }

    public TagReader<LongTag> getReader() {
        return READER;
    }

    @Override
    public String toString() {
        return this.value + "L";
    }

    @Override
    public LongTag copy() {
        return this;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        return object instanceof LongTag && this.value == ((LongTag)object).value;
    }

    public int hashCode() {
        return (int)(this.value ^ this.value >>> 32);
    }

    @Override
    public Text toText(String string, int i) {
        MutableText lv = new LiteralText("L").formatted(RED);
        return new LiteralText(String.valueOf(this.value)).append(lv).formatted(GOLD);
    }

    @Override
    public long getLong() {
        return this.value;
    }

    @Override
    public int getInt() {
        return (int)(this.value & 0xFFFFFFFFFFFFFFFFL);
    }

    @Override
    public short getShort() {
        return (short)(this.value & 0xFFFFL);
    }

    @Override
    public byte getByte() {
        return (byte)(this.value & 0xFFL);
    }

    @Override
    public double getDouble() {
        return this.value;
    }

    @Override
    public float getFloat() {
        return this.value;
    }

    @Override
    public Number getNumber() {
        return this.value;
    }

    @Override
    public /* synthetic */ Tag copy() {
        return this.copy();
    }

    static class Cache {
        static final LongTag[] VALUES = new LongTag[1153];

        static {
            for (int i = 0; i < VALUES.length; ++i) {
                Cache.VALUES[i] = new LongTag(-128 + i);
            }
        }
    }
}

