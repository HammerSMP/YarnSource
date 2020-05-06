/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 */
package net.minecraft.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Random;
import net.minecraft.loot.LootTableRange;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public final class BinomialLootTableRange
implements LootTableRange {
    private final int n;
    private final float p;

    public BinomialLootTableRange(int i, float f) {
        this.n = i;
        this.p = f;
    }

    @Override
    public int next(Random random) {
        int i = 0;
        for (int j = 0; j < this.n; ++j) {
            if (!(random.nextFloat() < this.p)) continue;
            ++i;
        }
        return i;
    }

    public static BinomialLootTableRange create(int i, float f) {
        return new BinomialLootTableRange(i, f);
    }

    @Override
    public Identifier getType() {
        return BINOMIAL;
    }

    public static class Serializer
    implements JsonDeserializer<BinomialLootTableRange>,
    JsonSerializer<BinomialLootTableRange> {
        public BinomialLootTableRange deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = JsonHelper.asObject(jsonElement, "value");
            int i = JsonHelper.getInt(jsonObject, "n");
            float f = JsonHelper.getFloat(jsonObject, "p");
            return new BinomialLootTableRange(i, f);
        }

        public JsonElement serialize(BinomialLootTableRange arg, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("n", (Number)arg.n);
            jsonObject.addProperty("p", (Number)Float.valueOf(arg.p));
            return jsonObject;
        }

        public /* synthetic */ JsonElement serialize(Object object, Type type, JsonSerializationContext jsonSerializationContext) {
            return this.serialize((BinomialLootTableRange)object, type, jsonSerializationContext);
        }

        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }
}

