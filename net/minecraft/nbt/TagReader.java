/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.IOException;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.PositionTracker;
import net.minecraft.nbt.Tag;

public interface TagReader<T extends Tag> {
    public T read(DataInput var1, int var2, PositionTracker var3) throws IOException;

    default public boolean isImmutable() {
        return false;
    }

    public String getCrashReportName();

    public String getCommandFeedbackName();

    public static TagReader<EndTag> createInvalid(final int i) {
        return new TagReader<EndTag>(){

            @Override
            public EndTag read(DataInput dataInput, int i2, PositionTracker arg) throws IOException {
                throw new IllegalArgumentException("Invalid tag id: " + i);
            }

            @Override
            public String getCrashReportName() {
                return "INVALID[" + i + "]";
            }

            @Override
            public String getCommandFeedbackName() {
                return "UNKNOWN_" + i;
            }

            @Override
            public /* synthetic */ Tag read(DataInput dataInput, int i2, PositionTracker arg) throws IOException {
                return this.read(dataInput, i2, arg);
            }
        };
    }
}

