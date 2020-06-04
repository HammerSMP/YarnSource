/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.loot.function;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextAware;
import net.minecraft.loot.function.LootFunctionType;

public interface LootFunction
extends LootContextAware,
BiFunction<ItemStack, LootContext, ItemStack> {
    public LootFunctionType method_29321();

    public static Consumer<ItemStack> apply(BiFunction<ItemStack, LootContext, ItemStack> biFunction, Consumer<ItemStack> consumer, LootContext arg) {
        return arg2 -> consumer.accept((ItemStack)biFunction.apply((ItemStack)arg2, arg));
    }

    public static interface Builder {
        public LootFunction build();
    }
}

