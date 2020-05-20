/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 */
package net.minecraft.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Predicate;
import net.minecraft.loot.condition.AlternativeLootCondition;
import net.minecraft.loot.condition.InvertedLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextAware;
import net.minecraft.util.Identifier;

@FunctionalInterface
public interface LootCondition
extends LootContextAware,
Predicate<LootContext> {

    public static abstract class Factory<T extends LootCondition> {
        private final Identifier id;
        private final Class<T> conditionClass;

        protected Factory(Identifier arg, Class<T> arg2) {
            this.id = arg;
            this.conditionClass = arg2;
        }

        public Identifier getId() {
            return this.id;
        }

        public Class<T> getConditionClass() {
            return this.conditionClass;
        }

        public abstract void toJson(JsonObject var1, T var2, JsonSerializationContext var3);

        public abstract T fromJson(JsonObject var1, JsonDeserializationContext var2);
    }

    @FunctionalInterface
    public static interface Builder {
        public LootCondition build();

        default public Builder invert() {
            return InvertedLootCondition.builder(this);
        }

        default public AlternativeLootCondition.Builder or(Builder arg) {
            return AlternativeLootCondition.builder(this, arg);
        }
    }
}

