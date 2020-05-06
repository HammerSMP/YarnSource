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

    private BoundedIntUnaryOperator(@Nullable Integer integer, @Nullable Integer integer2) {
        this.min = integer;
        this.max = integer2;
        if (integer == null) {
            if (integer2 == null) {
                this.operator = i -> i;
            } else {
                int i2 = integer2;
                this.operator = j -> Math.min(i2, j);
            }
        } else {
            int j2 = integer;
            if (integer2 == null) {
                this.operator = j -> Math.max(j2, j);
            } else {
                int k2 = integer2;
                this.operator = k -> MathHelper.clamp(k, j2, k2);
            }
        }
    }

    public static BoundedIntUnaryOperator create(int i, int j) {
        return new BoundedIntUnaryOperator(i, j);
    }

    public static BoundedIntUnaryOperator createMin(int i) {
        return new BoundedIntUnaryOperator(i, null);
    }

    public static BoundedIntUnaryOperator createMax(int i) {
        return new BoundedIntUnaryOperator(null, i);
    }

    @Override
    public int applyAsInt(int i) {
        return this.operator.applyAsInt(i);
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

        public /* synthetic */ JsonElement serialize(Object object, Type type, JsonSerializationContext jsonSerializationContext) {
            return this.serialize((BoundedIntUnaryOperator)object, type, jsonSerializationContext);
        }

        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }
}

