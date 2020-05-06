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
import net.minecraft.util.math.MathHelper;

public class FloatTag
extends AbstractNumberTag {
    public static final FloatTag ZERO = new FloatTag(0.0f);
    public static final TagReader<FloatTag> READER = new TagReader<FloatTag>(){

        @Override
        public FloatTag read(DataInput dataInput, int i, PositionTracker arg) throws IOException {
            arg.add(96L);
            return FloatTag.of(dataInput.readFloat());
        }

        @Override
        public String getCrashReportName() {
            return "FLOAT";
        }

        @Override
        public String getCommandFeedbackName() {
            return "TAG_Float";
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
    private final float value;

    private FloatTag(float f) {
        this.value = f;
    }

    public static FloatTag of(float f) {
        if (f == 0.0f) {
            return ZERO;
        }
        return new FloatTag(f);
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeFloat(this.value);
    }

    @Override
    public byte getType() {
        return 5;
    }

    public TagReader<FloatTag> getReader() {
        return READER;
    }

    @Override
    public String toString() {
        return this.value + "f";
    }

    @Override
    public FloatTag copy() {
        return this;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        return object instanceof FloatTag && this.value == ((FloatTag)object).value;
    }

    public int hashCode() {
        return Float.floatToIntBits(this.value);
    }

    @Override
    public Text toText(String string, int i) {
        MutableText lv = new LiteralText("f").formatted(RED);
        return new LiteralText(String.valueOf(this.value)).append(lv).formatted(GOLD);
    }

    @Override
    public long getLong() {
        return (long)this.value;
    }

    @Override
    public int getInt() {
        return MathHelper.floor(this.value);
    }

    @Override
    public short getShort() {
        return (short)(MathHelper.floor(this.value) & 0xFFFF);
    }

    @Override
    public byte getByte() {
        return (byte)(MathHelper.floor(this.value) & 0xFF);
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
        return Float.valueOf(this.value);
    }

    @Override
    public /* synthetic */ Tag copy() {
        return this.copy();
    }
}

