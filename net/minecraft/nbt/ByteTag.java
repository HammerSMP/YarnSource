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

public class ByteTag
extends AbstractNumberTag {
    public static final TagReader<ByteTag> READER = new TagReader<ByteTag>(){

        @Override
        public ByteTag read(DataInput dataInput, int i, PositionTracker arg) throws IOException {
            arg.add(72L);
            return ByteTag.of(dataInput.readByte());
        }

        @Override
        public String getCrashReportName() {
            return "BYTE";
        }

        @Override
        public String getCommandFeedbackName() {
            return "TAG_Byte";
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
    public static final ByteTag ZERO = ByteTag.of((byte)0);
    public static final ByteTag ONE = ByteTag.of((byte)1);
    private final byte value;

    private ByteTag(byte b) {
        this.value = b;
    }

    public static ByteTag of(byte b) {
        return Cache.VALUES[128 + b];
    }

    public static ByteTag of(boolean bl) {
        return bl ? ONE : ZERO;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeByte(this.value);
    }

    @Override
    public byte getType() {
        return 1;
    }

    public TagReader<ByteTag> getReader() {
        return READER;
    }

    @Override
    public String toString() {
        return this.value + "b";
    }

    @Override
    public ByteTag copy() {
        return this;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        return object instanceof ByteTag && this.value == ((ByteTag)object).value;
    }

    public int hashCode() {
        return this.value;
    }

    @Override
    public Text toText(String string, int i) {
        MutableText lv = new LiteralText("b").formatted(RED);
        return new LiteralText(String.valueOf(this.value)).append(lv).formatted(GOLD);
    }

    @Override
    public long getLong() {
        return this.value;
    }

    @Override
    public int getInt() {
        return this.value;
    }

    @Override
    public short getShort() {
        return this.value;
    }

    @Override
    public byte getByte() {
        return this.value;
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
        private static final ByteTag[] VALUES = new ByteTag[256];

        static {
            for (int i = 0; i < VALUES.length; ++i) {
                Cache.VALUES[i] = new ByteTag((byte)(i - 128));
            }
        }
    }
}

