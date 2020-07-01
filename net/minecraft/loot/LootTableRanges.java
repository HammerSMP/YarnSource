/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSerializationContext
 */
package net.minecraft.loot;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.Map;
import net.minecraft.loot.BinomialLootTableRange;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.LootTableRange;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class LootTableRanges {
    private static final Map<Identifier, Class<? extends LootTableRange>> TYPES = Maps.newHashMap();

    public static LootTableRange fromJson(JsonElement jsonElement, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (jsonElement.isJsonPrimitive()) {
            return (LootTableRange)jsonDeserializationContext.deserialize(jsonElement, ConstantLootTableRange.class);
        }
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String string = JsonHelper.getString(jsonObject, "type", LootTableRange.UNIFORM.toString());
        Class<? extends LootTableRange> class_ = TYPES.get(new Identifier(string));
        if (class_ == null) {
            throw new JsonParseException("Unknown generator: " + string);
        }
        return (LootTableRange)jsonDeserializationContext.deserialize((JsonElement)jsonObject, class_);
    }

    public static JsonElement toJson(LootTableRange arg, JsonSerializationContext jsonSerializationContext) {
        JsonElement jsonElement = jsonSerializationContext.serialize((Object)arg);
        if (jsonElement.isJsonObject()) {
            jsonElement.getAsJsonObject().addProperty("type", arg.getType().toString());
        }
        return jsonElement;
    }

    static {
        TYPES.put(LootTableRange.UNIFORM, UniformLootTableRange.class);
        TYPES.put(LootTableRange.BINOMIAL, BinomialLootTableRange.class);
        TYPES.put(LootTableRange.CONSTANT, ConstantLootTableRange.class);
    }
}

