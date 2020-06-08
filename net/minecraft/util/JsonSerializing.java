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
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.datafixers.util.Pair
 *  javax.annotation.Nullable
 */
package net.minecraft.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import java.lang.reflect.Type;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializableType;
import net.minecraft.util.registry.Registry;

public class JsonSerializing {
    public static <E, T extends JsonSerializableType<E>> TypeHandler<E, T> createTypeHandler(Registry<T> arg, String string, String string2, Function<E, T> function) {
        return new TypeHandler(arg, string, string2, function);
    }

    public static interface CustomSerializer<T> {
        public JsonElement toJson(T var1, JsonSerializationContext var2);

        public T fromJson(JsonElement var1, JsonDeserializationContext var2);
    }

    static class GsonSerializer<E, T extends JsonSerializableType<E>>
    implements JsonDeserializer<E>,
    JsonSerializer<E> {
        private final Registry<T> registry;
        private final String rootFieldName;
        private final String idFieldName;
        private final Function<E, T> typeIdentification;
        @Nullable
        private final Pair<T, CustomSerializer<? extends E>> elementSerializer;

        private GsonSerializer(Registry<T> arg, String string, String string2, Function<E, T> function, @Nullable Pair<T, CustomSerializer<? extends E>> pair) {
            this.registry = arg;
            this.rootFieldName = string;
            this.idFieldName = string2;
            this.typeIdentification = function;
            this.elementSerializer = pair;
        }

        public E deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = JsonHelper.asObject(jsonElement, this.rootFieldName);
                Identifier lv = new Identifier(JsonHelper.getString(jsonObject, this.idFieldName));
                JsonSerializableType lv2 = (JsonSerializableType)this.registry.get(lv);
                if (lv2 == null) {
                    throw new JsonSyntaxException("Unknown type '" + lv + "'");
                }
                return (E)lv2.getJsonSerializer().fromJson(jsonObject, jsonDeserializationContext);
            }
            if (this.elementSerializer == null) {
                throw new UnsupportedOperationException("Object " + (Object)jsonElement + " can't be deserialized");
            }
            return (E)((CustomSerializer)this.elementSerializer.getSecond()).fromJson(jsonElement, jsonDeserializationContext);
        }

        public JsonElement serialize(E object, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonSerializableType lv = (JsonSerializableType)this.typeIdentification.apply(object);
            if (this.elementSerializer != null && this.elementSerializer.getFirst() == lv) {
                return ((CustomSerializer)this.elementSerializer.getSecond()).toJson(object, jsonSerializationContext);
            }
            if (lv == null) {
                throw new JsonSyntaxException("Unknown type: " + object);
            }
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(this.idFieldName, this.registry.getId(lv).toString());
            lv.getJsonSerializer().toJson(jsonObject, object, jsonSerializationContext);
            return jsonObject;
        }
    }

    public static class TypeHandler<E, T extends JsonSerializableType<E>> {
        private final Registry<T> registry;
        private final String rootFieldName;
        private final String idFieldName;
        private final Function<E, T> typeIdentification;
        @Nullable
        private Pair<T, CustomSerializer<? extends E>> customSerializer;

        private TypeHandler(Registry<T> arg, String string, String string2, Function<E, T> function) {
            this.registry = arg;
            this.rootFieldName = string;
            this.idFieldName = string2;
            this.typeIdentification = function;
        }

        public Object createGsonSerializer() {
            return new GsonSerializer(this.registry, this.rootFieldName, this.idFieldName, this.typeIdentification, this.customSerializer);
        }
    }
}

