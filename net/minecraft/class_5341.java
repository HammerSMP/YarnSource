/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft;

import java.util.function.Predicate;
import net.minecraft.class_5342;
import net.minecraft.loot.condition.AlternativeLootCondition;
import net.minecraft.loot.condition.InvertedLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextAware;

public interface class_5341
extends LootContextAware,
Predicate<LootContext> {
    public class_5342 method_29325();

    @FunctionalInterface
    public static interface Builder {
        public class_5341 build();

        default public Builder invert() {
            return InvertedLootCondition.builder(this);
        }

        default public AlternativeLootCondition.Builder or(Builder arg) {
            return AlternativeLootCondition.builder(this, arg);
        }
    }
}

