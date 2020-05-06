/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package net.minecraft.data.client.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.function.Function;

public class VariantSetting<T> {
    private final String key;
    private final Function<T, JsonElement> writer;

    public VariantSetting(String string, Function<T, JsonElement> function) {
        this.key = string;
        this.writer = function;
    }

    public Value evaluate(T object) {
        return new Value(object);
    }

    public String toString() {
        return this.key;
    }

    public class Value {
        private final T value;

        public Value(T object) {
            this.value = object;
        }

        public void writeTo(JsonObject jsonObject) {
            jsonObject.add(VariantSetting.this.key, (JsonElement)VariantSetting.this.writer.apply(this.value));
        }

        public String toString() {
            return VariantSetting.this.key + "=" + this.value;
        }
    }
}

