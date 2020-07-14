/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.nbt;

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
import net.minecraft.nbt.TagReader;

public class TagReaders {
    private static final TagReader<?>[] VALUES = new TagReader[]{EndTag.READER, ByteTag.READER, ShortTag.READER, IntTag.READER, LongTag.READER, FloatTag.READER, DoubleTag.READER, ByteArrayTag.READER, StringTag.READER, ListTag.READER, CompoundTag.READER, IntArrayTag.READER, LongArrayTag.READER};

    public static TagReader<?> of(int type) {
        if (type < 0 || type >= VALUES.length) {
            return TagReader.createInvalid(type);
        }
        return VALUES[type];
    }
}

