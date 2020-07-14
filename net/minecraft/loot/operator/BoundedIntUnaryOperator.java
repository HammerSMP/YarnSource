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
 *  javax.annotation.Nullable
 */
package net.minecraft.loot.operator;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.function.IntUnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;

public class BoundedIntUnaryOperator
implements IntUnaryOperator {
    private final Integer min;
    private final Integer max;
    private final IntUnaryOperator operator;

    private BoundedIntUnaryOperator(@Nullable Integer min, @Nullable Integer max) {
        this.min = min;
        this.max = max;
        if (min == null) {
            if (max == null) {
                this.operator = i -> i;
            } else {
                int i2 = max;
                this.operator = j -> Math.min(i2, j);
            }
        } else {
            int j2 = min;
            if (max == null) {
                this.operator = j -> Math.max(j2, j);
            } else {
                int k2 = max;
                this.operator = k -> MathHelper.clamp(k, j2, k2);
            }
        }
    }

    public static BoundedIntUnaryOperator create(int min, int max) {
        return new BoundedIntUnaryOperator(min, max);
    }

    public static BoundedIntUnaryOperator createMin(int min) {
        return new BoundedIntUnaryOperator(min, null);
    }

    public static BoundedIntUnaryOperator createMax(int max) {
        return new BoundedIntUnaryOperator(null, max);
    }

    @Override
    public int applyAsInt(int value) {
        return this.operator.applyAsInt(value);
    }

    public static class Serializer
    implements JsonDeserializer<BoundedIntUnaryOperator>,
    JsonSerializer<BoundedIntUnaryOperator> {
        public BoundedIntUnaryOperator deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = JsonHelper.asObject(jsonElement, "value");
            Integer integer = jsonObject.has("min") ? Integer.valueOf(JsonHelper.getInt(jsonObject, "min")) : null;
            Integer integer2 = jsonObject.has("max") ? Integer.valueOf(JsonHelper.getInt(jsonObject, "max")) : null;
            return new BoundedIntUnaryOperator(integer, integer2);
        }

        public JsonElement serialize(BoundedIntUnaryOperator arg, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            if (arg.max != null) {
                jsonObject.addProperty("max", (Number)arg.max);
            }
            if (arg.min != null) {
                jsonObject.addProperty("min", (Number)arg.min);
            }
            return jsonObject;
        }

        public /* synthetic */ JsonElement serialize(Object op, Type type, JsonSerializationContext context) {
            return this.serialize((BoundedIntUnaryOperator)op, type, context);
        }

        public /* synthetic */ Object deserialize(JsonElement functionJson, Type unused, JsonDeserializationContext context) throws JsonParseException {
            return this.deserialize(functionJson, unused, context);
        }
    }
}

