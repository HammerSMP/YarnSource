/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 */
package net.minecraft.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextAware;
import net.minecraft.util.Identifier;

public interface LootFunction
extends LootContextAware,
BiFunction<ItemStack, LootContext, ItemStack> {
    public static Consumer<ItemStack> apply(BiFunction<ItemStack, LootContext, ItemStack> biFunction, Consumer<ItemStack> consumer, LootContext arg) {
        return arg2 -> consumer.accept((ItemStack)biFunction.apply((ItemStack)arg2, arg));
    }

    public static abstract class Factory<T extends LootFunction> {
        private final Identifier id;
        private final Class<T> functionClass;

        protected Factory(Identifier arg, Class<T> arg2) {
            this.id = arg;
            this.functionClass = arg2;
        }

        public Identifier getId() {
            return this.id;
        }

        public Class<T> getFunctionClass() {
            return this.functionClass;
        }

        public abstract void toJson(JsonObject var1, T var2, JsonSerializationContext var3);

        public abstract T fromJson(JsonObject var1, JsonDeserializationContext var2);
    }

    public static interface Builder {
        public LootFunction build();
    }
}

