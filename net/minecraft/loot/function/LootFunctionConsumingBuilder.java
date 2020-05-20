/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.loot.function;

import net.minecraft.loot.function.LootFunction;

public interface LootFunctionConsumingBuilder<T> {
    public T apply(LootFunction.Builder var1);

    public T getThis();
}

