/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.nbt;

import java.util.AbstractList;
import net.minecraft.nbt.Tag;

public abstract class AbstractListTag<T extends Tag>
extends AbstractList<T>
implements Tag {
    @Override
    public abstract T set(int var1, T var2);

    @Override
    public abstract void add(int var1, T var2);

    @Override
    public abstract T remove(int var1);

    public abstract boolean setTag(int var1, Tag var2);

    public abstract boolean addTag(int var1, Tag var2);

    public abstract byte getElementType();

    @Override
    public /* synthetic */ Object remove(int index) {
        return this.remove(index);
    }

    @Override
    public /* synthetic */ void add(int value, Object object) {
        this.add(value, (T)((Tag)object));
    }

    @Override
    public /* synthetic */ Object set(int index, Object object) {
        return this.set(index, (T)((Tag)object));
    }
}

