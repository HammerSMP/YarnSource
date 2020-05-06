/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonPrimitive
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 */
package net.minecraft.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Random;
import net.minecraft.loot.LootTableRange;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public final class ConstantLootTableRange
implements LootTableRange {
    private final int value;

    public ConstantLootTableRange(int i) {
        this.value = i;
    }

    @Override
    public int next(Random random) {
        return this.value;
    }

    @Override
    public Identifier getType() {
        return CONSTANT;
    }

    public static ConstantLootTableRange create(int i) {
        return new ConstantLootTableRange(i);
    }

    public static class Serializer
    implements JsonDeserializer<ConstantLootTableRange>,
    JsonSerializer<ConstantLootTableRange> {
        public ConstantLootTableRange deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return new ConstantLootTableRange(JsonHelper.asInt(jsonElement, "value"));
        }

        public JsonElement serialize(ConstantLootTableRange arg, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive((Number)arg.value);
        }

        public /* synthetic */ JsonElement serialize(Object object, Type type, JsonSerializationContext jsonSerializationContext) {
            return this.serialize((ConstantLootTableRange)object, type, jsonSerializationContext);
        }

        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }
}

