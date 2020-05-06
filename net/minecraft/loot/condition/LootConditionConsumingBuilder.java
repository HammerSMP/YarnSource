/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.loot.condition;

import net.minecraft.loot.condition.LootCondition;

public interface LootConditionConsumingBuilder<T> {
    public T withCondition(LootCondition.Builder var1);

    public T getThis();
}

