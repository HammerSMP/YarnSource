/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.bytes.ByteOpenHashSet
 *  it.unimi.dsi.fastutil.bytes.ByteSet
 */
package net.minecraft.nbt;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.minecraft.nbt.AbstractListTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.PositionTracker;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagReader;
import net.minecraft.nbt.TagReaders;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class ListTag
extends AbstractListTag<Tag> {
    public static final TagReader<ListTag> READER = new TagReader<ListTag>(){

        @Override
        public ListTag read(DataInput dataInput, int i, PositionTracker arg) throws IOException {
            arg.add(296L);
            if (i > 512) {
                throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
            }
            byte b = dataInput.readByte();
            int j = dataInput.readInt();
            if (b == 0 && j > 0) {
                throw new RuntimeException("Missing type on ListTag");
            }
            arg.add(32L * (long)j);
            TagReader<?> lv = TagReaders.of(b);
            ArrayList list = Lists.newArrayListWithCapacity((int)j);
            for (int k = 0; k < j; ++k) {
                list.add(lv.read(dataInput, i + 1, arg));
            }
            return new ListTag(list, b);
        }

        @Override
        public String getCrashReportName() {
            return "LIST";
        }

        @Override
        public String getCommandFeedbackName() {
            return "TAG_List";
        }

        @Override
        public /* synthetic */ Tag read(DataInput input, int depth, PositionTracker tracker) throws IOException {
            return this.read(input, depth, tracker);
        }
    };
    private static final ByteSet NBT_NUMBER_TYPES = new ByteOpenHashSet(Arrays.asList((byte)1, (byte)2, (byte)3, (byte)4, (byte)5, (byte)6));
    private final List<Tag> value;
    private byte type;

    private ListTag(List<Tag> list, byte type) {
        this.value = list;
        this.type = type;
    }

    public ListTag() {
        this(Lists.newArrayList(), 0);
    }

    @Override
    public void write(DataOutput output) throws IOException {
        this.type = this.value.isEmpty() ? (byte)0 : this.value.get(0).getType();
        output.writeByte(this.type);
        output.writeInt(this.value.size());
        for (Tag lv : this.value) {
            lv.write(output);
        }
    }

    @Override
    public byte getType() {
        return 9;
    }

    public TagReader<ListTag> getReader() {
        return READER;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("[");
        for (int i = 0; i < this.value.size(); ++i) {
            if (i != 0) {
                stringBuilder.append(',');
            }
            stringBuilder.append(this.value.get(i));
        }
        return stringBuilder.append(']').toString();
    }

    private void forgetTypeIfEmpty() {
        if (this.value.isEmpty()) {
            this.type = 0;
        }
    }

    @Override
    public Tag remove(int i) {
        Tag lv = this.value.remove(i);
        this.forgetTypeIfEmpty();
        return lv;
    }

    @Override
    public boolean isEmpty() {
        return this.value.isEmpty();
    }

    public CompoundTag getCompound(int index) {
        Tag lv;
        if (index >= 0 && index < this.value.size() && (lv = this.value.get(index)).getType() == 10) {
            return (CompoundTag)lv;
        }
        return new CompoundTag();
    }

    public ListTag getList(int index) {
        Tag lv;
        if (index >= 0 && index < this.value.size() && (lv = this.value.get(index)).getType() == 9) {
            return (ListTag)lv;
        }
        return new ListTag();
    }

    public short getShort(int index) {
        Tag lv;
        if (index >= 0 && index < this.value.size() && (lv = this.value.get(index)).getType() == 2) {
            return ((ShortTag)lv).getShort();
        }
        return 0;
    }

    public int getInt(int i) {
        Tag lv;
        if (i >= 0 && i < this.value.size() && (lv = this.value.get(i)).getType() == 3) {
            return ((IntTag)lv).getInt();
        }
        return 0;
    }

    public int[] getIntArray(int index) {
        Tag lv;
        if (index >= 0 && index < this.value.size() && (lv = this.value.get(index)).getType() == 11) {
            return ((IntArrayTag)lv).getIntArray();
        }
        return new int[0];
    }

    public double getDouble(int index) {
        Tag lv;
        if (index >= 0 && index < this.value.size() && (lv = this.value.get(index)).getType() == 6) {
            return ((DoubleTag)lv).getDouble();
        }
        return 0.0;
    }

    public float getFloat(int index) {
        Tag lv;
        if (index >= 0 && index < this.value.size() && (lv = this.value.get(index)).getType() == 5) {
            return ((FloatTag)lv).getFloat();
        }
        return 0.0f;
    }

    public String getString(int index) {
        if (index < 0 || index >= this.value.size()) {
            return "";
        }
        Tag lv = this.value.get(index);
        if (lv.getType() == 8) {
            return lv.asString();
        }
        return lv.toString();
    }

    @Override
    public int size() {
        return this.value.size();
    }

    @Override
    public Tag get(int i) {
        return this.value.get(i);
    }

    @Override
    public Tag set(int i, Tag arg) {
        Tag lv = this.get(i);
        if (!this.setTag(i, arg)) {
            throw new UnsupportedOperationException(String.format("Trying to add tag of type %d to list of %d", arg.getType(), this.type));
        }
        return lv;
    }

    @Override
    public void add(int i, Tag arg) {
        if (!this.addTag(i, arg)) {
            throw new UnsupportedOperationException(String.format("Trying to add tag of type %d to list of %d", arg.getType(), this.type));
        }
    }

    @Override
    public boolean setTag(int index, Tag tag) {
        if (this.canAdd(tag)) {
            this.value.set(index, tag);
            return true;
        }
        return false;
    }

    @Override
    public boolean addTag(int index, Tag tag) {
        if (this.canAdd(tag)) {
            this.value.add(index, tag);
            return true;
        }
        return false;
    }

    private boolean canAdd(Tag tag) {
        if (tag.getType() == 0) {
            return false;
        }
        if (this.type == 0) {
            this.type = tag.getType();
            return true;
        }
        return this.type == tag.getType();
    }

    @Override
    public ListTag copy() {
        List<Tag> iterable = TagReaders.of(this.type).isImmutable() ? this.value : Iterables.transform(this.value, Tag::copy);
        ArrayList list = Lists.newArrayList(iterable);
        return new ListTag(list, this.type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof ListTag && Objects.equals(this.value, ((ListTag)o).value);
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public Text toText(String indent, int depth) {
        if (this.isEmpty()) {
            return new LiteralText("[]");
        }
        if (NBT_NUMBER_TYPES.contains(this.type) && this.size() <= 8) {
            String string2 = ", ";
            LiteralText lv = new LiteralText("[");
            for (int j = 0; j < this.value.size(); ++j) {
                if (j != 0) {
                    lv.append(", ");
                }
                lv.append(this.value.get(j).toText());
            }
            lv.append("]");
            return lv;
        }
        LiteralText lv2 = new LiteralText("[");
        if (!indent.isEmpty()) {
            lv2.append("\n");
        }
        String string3 = String.valueOf(',');
        for (int k = 0; k < this.value.size(); ++k) {
            LiteralText lv3 = new LiteralText(Strings.repeat((String)indent, (int)(depth + 1)));
            lv3.append(this.value.get(k).toText(indent, depth + 1));
            if (k != this.value.size() - 1) {
                lv3.append(string3).append(indent.isEmpty() ? " " : "\n");
            }
            lv2.append(lv3);
        }
        if (!indent.isEmpty()) {
            lv2.append("\n").append(Strings.repeat((String)indent, (int)depth));
        }
        lv2.append("]");
        return lv2;
    }

    @Override
    public byte getElementType() {
        return this.type;
    }

    @Override
    public void clear() {
        this.value.clear();
        this.type = 0;
    }

    @Override
    public /* synthetic */ Tag copy() {
        return this.copy();
    }

    @Override
    public /* synthetic */ Object remove(int i) {
        return this.remove(i);
    }

    @Override
    public /* synthetic */ void add(int i, Object object) {
        this.add(i, (Tag)object);
    }

    @Override
    public /* synthetic */ Object set(int i, Object object) {
        return this.set(i, (Tag)object);
    }

    @Override
    public /* synthetic */ Object get(int i) {
        return this.get(i);
    }
}

