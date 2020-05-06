/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.screen;

import net.minecraft.screen.PropertyDelegate;

public abstract class Property {
    private int oldValue;

    public static Property create(final PropertyDelegate arg, final int i) {
        return new Property(){

            @Override
            public int get() {
                return arg.get(i);
            }

            @Override
            public void set(int i2) {
                arg.set(i, i2);
            }
        };
    }

    public static Property create(final int[] is, final int i) {
        return new Property(){

            @Override
            public int get() {
                return is[i];
            }

            @Override
            public void set(int i2) {
                is[i] = i2;
            }
        };
    }

    public static Property create() {
        return new Property(){
            private int value;

            @Override
            public int get() {
                return this.value;
            }

            @Override
            public void set(int i) {
                this.value = i;
            }
        };
    }

    public abstract int get();

    public abstract void set(int var1);

    public boolean hasChanged() {
        int i = this.get();
        boolean bl = i != this.oldValue;
        this.oldValue = i;
        return bl;
    }
}

