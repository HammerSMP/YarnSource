/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonPrimitive
 *  com.google.gson.JsonSyntaxException
 *  com.google.gson.reflect.TypeToken
 *  com.google.gson.stream.JsonReader
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.StringUtils
 */
package net.minecraft.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.StringUtils;

public class JsonHelper {
    private static final Gson GSON = new GsonBuilder().create();

    public static boolean hasString(JsonObject jsonObject, String string) {
        if (!JsonHelper.hasPrimitive(jsonObject, string)) {
            return false;
        }
        return jsonObject.getAsJsonPrimitive(string).isString();
    }

    @Environment(value=EnvType.CLIENT)
    public static boolean isString(JsonElement jsonElement) {
        if (!jsonElement.isJsonPrimitive()) {
            return false;
        }
        return jsonElement.getAsJsonPrimitive().isString();
    }

    public static boolean isNumber(JsonElement jsonElement) {
        if (!jsonElement.isJsonPrimitive()) {
            return false;
        }
        return jsonElement.getAsJsonPrimitive().isNumber();
    }

    @Environment(value=EnvType.CLIENT)
    public static boolean hasBoolean(JsonObject jsonObject, String string) {
        if (!JsonHelper.hasPrimitive(jsonObject, string)) {
            return false;
        }
        return jsonObject.getAsJsonPrimitive(string).isBoolean();
    }

    public static boolean hasArray(JsonObject jsonObject, String string) {
        if (!JsonHelper.hasElement(jsonObject, string)) {
            return false;
        }
        return jsonObject.get(string).isJsonArray();
    }

    public static boolean hasPrimitive(JsonObject jsonObject, String string) {
        if (!JsonHelper.hasElement(jsonObject, string)) {
            return false;
        }
        return jsonObject.get(string).isJsonPrimitive();
    }

    public static boolean hasElement(JsonObject jsonObject, String string) {
        if (jsonObject == null) {
            return false;
        }
        return jsonObject.get(string) != null;
    }

    public static String asString(JsonElement jsonElement, String string) {
        if (jsonElement.isJsonPrimitive()) {
            return jsonElement.getAsString();
        }
        throw new JsonSyntaxException("Expected " + string + " to be a string, was " + JsonHelper.getType(jsonElement));
    }

    public static String getString(JsonObject jsonObject, String string) {
        if (jsonObject.has(string)) {
            return JsonHelper.asString(jsonObject.get(string), string);
        }
        throw new JsonSyntaxException("Missing " + string + ", expected to find a string");
    }

    public static String getString(JsonObject jsonObject, String string, String string2) {
        if (jsonObject.has(string)) {
            return JsonHelper.asString(jsonObject.get(string), string);
        }
        return string2;
    }

    public static Item asItem(JsonElement jsonElement, String string) {
        if (jsonElement.isJsonPrimitive()) {
            String string2 = jsonElement.getAsString();
            return (Item)Registry.ITEM.getOrEmpty(new Identifier(string2)).orElseThrow(() -> new JsonSyntaxException("Expected " + string + " to be an item, was unknown string '" + string2 + "'"));
        }
        throw new JsonSyntaxException("Expected " + string + " to be an item, was " + JsonHelper.getType(jsonElement));
    }

    public static Item getItem(JsonObject jsonObject, String string) {
        if (jsonObject.has(string)) {
            return JsonHelper.asItem(jsonObject.get(string), string);
        }
        throw new JsonSyntaxException("Missing " + string + ", expected to find an item");
    }

    public static boolean asBoolean(JsonElement jsonElement, String string) {
        if (jsonElement.isJsonPrimitive()) {
            return jsonElement.getAsBoolean();
        }
        throw new JsonSyntaxException("Expected " + string + " to be a Boolean, was " + JsonHelper.getType(jsonElement));
    }

    public static boolean getBoolean(JsonObject jsonObject, String string) {
        if (jsonObject.has(string)) {
            return JsonHelper.asBoolean(jsonObject.get(string), string);
        }
        throw new JsonSyntaxException("Missing " + string + ", expected to find a Boolean");
    }

    public static boolean getBoolean(JsonObject jsonObject, String string, boolean bl) {
        if (jsonObject.has(string)) {
            return JsonHelper.asBoolean(jsonObject.get(string), string);
        }
        return bl;
    }

    public static float asFloat(JsonElement jsonElement, String string) {
        if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isNumber()) {
            return jsonElement.getAsFloat();
        }
        throw new JsonSyntaxException("Expected " + string + " to be a Float, was " + JsonHelper.getType(jsonElement));
    }

    public static float getFloat(JsonObject jsonObject, String string) {
        if (jsonObject.has(string)) {
            return JsonHelper.asFloat(jsonObject.get(string), string);
        }
        throw new JsonSyntaxException("Missing " + string + ", expected to find a Float");
    }

    public static float getFloat(JsonObject jsonObject, String string, float f) {
        if (jsonObject.has(string)) {
            return JsonHelper.asFloat(jsonObject.get(string), string);
        }
        return f;
    }

    public static long asLong(JsonElement jsonElement, String string) {
        if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isNumber()) {
            return jsonElement.getAsLong();
        }
        throw new JsonSyntaxException("Expected " + string + " to be a Long, was " + JsonHelper.getType(jsonElement));
    }

    public static long getLong(JsonObject jsonObject, String string) {
        if (jsonObject.has(string)) {
            return JsonHelper.asLong(jsonObject.get(string), string);
        }
        throw new JsonSyntaxException("Missing " + string + ", expected to find a Long");
    }

    public static long getLong(JsonObject jsonObject, String string, long l) {
        if (jsonObject.has(string)) {
            return JsonHelper.asLong(jsonObject.get(string), string);
        }
        return l;
    }

    public static int asInt(JsonElement jsonElement, String string) {
        if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isNumber()) {
            return jsonElement.getAsInt();
        }
        throw new JsonSyntaxException("Expected " + string + " to be a Int, was " + JsonHelper.getType(jsonElement));
    }

    public static int getInt(JsonObject jsonObject, String string) {
        if (jsonObject.has(string)) {
            return JsonHelper.asInt(jsonObject.get(string), string);
        }
        throw new JsonSyntaxException("Missing " + string + ", expected to find a Int");
    }

    public static int getInt(JsonObject jsonObject, String string, int i) {
        if (jsonObject.has(string)) {
            return JsonHelper.asInt(jsonObject.get(string), string);
        }
        return i;
    }

    public static byte asByte(JsonElement jsonElement, String string) {
        if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isNumber()) {
            return jsonElement.getAsByte();
        }
        throw new JsonSyntaxException("Expected " + string + " to be a Byte, was " + JsonHelper.getType(jsonElement));
    }

    public static byte getByte(JsonObject jsonObject, String string, byte b) {
        if (jsonObject.has(string)) {
            return JsonHelper.asByte(jsonObject.get(string), string);
        }
        return b;
    }

    public static JsonObject asObject(JsonElement jsonElement, String string) {
        if (jsonElement.isJsonObject()) {
            return jsonElement.getAsJsonObject();
        }
        throw new JsonSyntaxException("Expected " + string + " to be a JsonObject, was " + JsonHelper.getType(jsonElement));
    }

    public static JsonObject getObject(JsonObject jsonObject, String string) {
        if (jsonObject.has(string)) {
            return JsonHelper.asObject(jsonObject.get(string), string);
        }
        throw new JsonSyntaxException("Missing " + string + ", expected to find a JsonObject");
    }

    public static JsonObject getObject(JsonObject jsonObject, String string, JsonObject jsonObject2) {
        if (jsonObject.has(string)) {
            return JsonHelper.asObject(jsonObject.get(string), string);
        }
        return jsonObject2;
    }

    public static JsonArray asArray(JsonElement jsonElement, String string) {
        if (jsonElement.isJsonArray()) {
            return jsonElement.getAsJsonArray();
        }
        throw new JsonSyntaxException("Expected " + string + " to be a JsonArray, was " + JsonHelper.getType(jsonElement));
    }

    public static JsonArray getArray(JsonObject jsonObject, String string) {
        if (jsonObject.has(string)) {
            return JsonHelper.asArray(jsonObject.get(string), string);
        }
        throw new JsonSyntaxException("Missing " + string + ", expected to find a JsonArray");
    }

    @Nullable
    public static JsonArray getArray(JsonObject jsonObject, String string, @Nullable JsonArray jsonArray) {
        if (jsonObject.has(string)) {
            return JsonHelper.asArray(jsonObject.get(string), string);
        }
        return jsonArray;
    }

    public static <T> T deserialize(@Nullable JsonElement jsonElement, String string, JsonDeserializationContext jsonDeserializationContext, Class<? extends T> class_) {
        if (jsonElement != null) {
            return (T)jsonDeserializationContext.deserialize(jsonElement, class_);
        }
        throw new JsonSyntaxException("Missing " + string);
    }

    public static <T> T deserialize(JsonObject jsonObject, String string, JsonDeserializationContext jsonDeserializationContext, Class<? extends T> class_) {
        if (jsonObject.has(string)) {
            return JsonHelper.deserialize(jsonObject.get(string), string, jsonDeserializationContext, class_);
        }
        throw new JsonSyntaxException("Missing " + string);
    }

    public static <T> T deserialize(JsonObject jsonObject, String string, T object, JsonDeserializationContext jsonDeserializationContext, Class<? extends T> class_) {
        if (jsonObject.has(string)) {
            return JsonHelper.deserialize(jsonObject.get(string), string, jsonDeserializationContext, class_);
        }
        return object;
    }

    public static String getType(JsonElement jsonElement) {
        String string = StringUtils.abbreviateMiddle((String)String.valueOf((Object)jsonElement), (String)"...", (int)10);
        if (jsonElement == null) {
            return "null (missing)";
        }
        if (jsonElement.isJsonNull()) {
            return "null (json)";
        }
        if (jsonElement.isJsonArray()) {
            return "an array (" + string + ")";
        }
        if (jsonElement.isJsonObject()) {
            return "an object (" + string + ")";
        }
        if (jsonElement.isJsonPrimitive()) {
            JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
            if (jsonPrimitive.isNumber()) {
                return "a number (" + string + ")";
            }
            if (jsonPrimitive.isBoolean()) {
                return "a boolean (" + string + ")";
            }
        }
        return string;
    }

    @Nullable
    public static <T> T deserialize(Gson gson, Reader reader, Class<T> class_, boolean bl) {
        try {
            JsonReader jsonReader = new JsonReader(reader);
            jsonReader.setLenient(bl);
            return (T)gson.getAdapter(class_).read(jsonReader);
        }
        catch (IOException iOException) {
            throw new JsonParseException((Throwable)iOException);
        }
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public static <T> T deserialize(Gson gson, Reader reader, TypeToken<T> typeToken, boolean bl) {
        try {
            JsonReader jsonReader = new JsonReader(reader);
            jsonReader.setLenient(bl);
            return (T)gson.getAdapter(typeToken).read(jsonReader);
        }
        catch (IOException iOException) {
            throw new JsonParseException((Throwable)iOException);
        }
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public static <T> T deserialize(Gson gson, String string, TypeToken<T> typeToken, boolean bl) {
        return JsonHelper.deserialize(gson, (Reader)new StringReader(string), typeToken, bl);
    }

    @Nullable
    public static <T> T deserialize(Gson gson, String string, Class<T> class_, boolean bl) {
        return JsonHelper.deserialize(gson, (Reader)new StringReader(string), class_, bl);
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public static <T> T deserialize(Gson gson, Reader reader, TypeToken<T> typeToken) {
        return JsonHelper.deserialize(gson, reader, typeToken, false);
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public static <T> T deserialize(Gson gson, String string, TypeToken<T> typeToken) {
        return JsonHelper.deserialize(gson, string, typeToken, false);
    }

    @Nullable
    public static <T> T deserialize(Gson gson, Reader reader, Class<T> class_) {
        return JsonHelper.deserialize(gson, reader, class_, false);
    }

    @Nullable
    public static <T> T deserialize(Gson gson, String string, Class<T> class_) {
        return JsonHelper.deserialize(gson, string, class_, false);
    }

    public static JsonObject deserialize(String string, boolean bl) {
        return JsonHelper.deserialize(new StringReader(string), bl);
    }

    public static JsonObject deserialize(Reader reader, boolean bl) {
        return JsonHelper.deserialize(GSON, reader, JsonObject.class, bl);
    }

    public static JsonObject deserialize(String string) {
        return JsonHelper.deserialize(string, false);
    }

    public static JsonObject deserialize(Reader reader) {
        return JsonHelper.deserialize(reader, false);
    }
}

