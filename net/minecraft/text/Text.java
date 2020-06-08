/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonPrimitive
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 *  com.google.gson.TypeAdapterFactory
 *  com.google.gson.stream.JsonReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.text;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.stream.JsonReader;
import com.mojang.brigadier.Message;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.KeybindText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.NbtText;
import net.minecraft.text.ScoreText;
import net.minecraft.text.SelectorText;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.LowercaseEnumTypeAdapterFactory;
import net.minecraft.util.Util;

public interface Text
extends Message,
StringRenderable {
    public Style getStyle();

    public String asString();

    default public String getString() {
        StringBuilder stringBuilder = new StringBuilder();
        this.visit(string -> {
            stringBuilder.append(string);
            return Optional.empty();
        });
        return stringBuilder.toString();
    }

    default public String asTruncatedString(int i) {
        StringBuilder stringBuilder = new StringBuilder();
        this.visit(string -> {
            int j = i - stringBuilder.length();
            if (j <= 0) {
                return TERMINATE_VISIT;
            }
            stringBuilder.append(string.length() <= j ? string : string.substring(0, j));
            return Optional.empty();
        });
        return stringBuilder.toString();
    }

    public List<Text> getSiblings();

    public MutableText copy();

    public MutableText shallowCopy();

    @Override
    @Environment(value=EnvType.CLIENT)
    default public <T> Optional<T> visit(StringRenderable.StyledVisitor<T> arg, Style arg2) {
        Style lv = this.getStyle().withParent(arg2);
        Optional<T> optional = this.visitSelf(arg, lv);
        if (optional.isPresent()) {
            return optional;
        }
        for (Text lv2 : this.getSiblings()) {
            Optional<T> optional2 = lv2.visit(arg, lv);
            if (!optional2.isPresent()) continue;
            return optional2;
        }
        return Optional.empty();
    }

    @Override
    default public <T> Optional<T> visit(StringRenderable.Visitor<T> arg) {
        Optional<T> optional = this.visitSelf(arg);
        if (optional.isPresent()) {
            return optional;
        }
        for (Text lv : this.getSiblings()) {
            Optional<T> optional2 = lv.visit(arg);
            if (!optional2.isPresent()) continue;
            return optional2;
        }
        return Optional.empty();
    }

    @Environment(value=EnvType.CLIENT)
    default public <T> Optional<T> visitSelf(StringRenderable.StyledVisitor<T> arg, Style arg2) {
        return arg.accept(arg2, this.asString());
    }

    default public <T> Optional<T> visitSelf(StringRenderable.Visitor<T> arg) {
        return arg.accept(this.asString());
    }

    public static class Serializer
    implements JsonDeserializer<MutableText>,
    JsonSerializer<Text> {
        private static final Gson GSON = Util.make(() -> {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.disableHtmlEscaping();
            gsonBuilder.registerTypeHierarchyAdapter(Text.class, (Object)new Serializer());
            gsonBuilder.registerTypeHierarchyAdapter(Style.class, (Object)new Style.Serializer());
            gsonBuilder.registerTypeAdapterFactory((TypeAdapterFactory)new LowercaseEnumTypeAdapterFactory());
            return gsonBuilder.create();
        });
        private static final Field JSON_READER_POS = Util.make(() -> {
            try {
                new JsonReader((Reader)new StringReader(""));
                Field field = JsonReader.class.getDeclaredField("pos");
                field.setAccessible(true);
                return field;
            }
            catch (NoSuchFieldException noSuchFieldException) {
                throw new IllegalStateException("Couldn't get field 'pos' for JsonReader", noSuchFieldException);
            }
        });
        private static final Field JSON_READER_LINE_START = Util.make(() -> {
            try {
                new JsonReader((Reader)new StringReader(""));
                Field field = JsonReader.class.getDeclaredField("lineStart");
                field.setAccessible(true);
                return field;
            }
            catch (NoSuchFieldException noSuchFieldException) {
                throw new IllegalStateException("Couldn't get field 'lineStart' for JsonReader", noSuchFieldException);
            }
        });

        /*
         * WARNING - void declaration
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        public MutableText deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (jsonElement.isJsonPrimitive()) {
                return new LiteralText(jsonElement.getAsString());
            }
            if (jsonElement.isJsonObject()) {
                void lv13;
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                if (jsonObject.has("text")) {
                    LiteralText lv = new LiteralText(JsonHelper.getString(jsonObject, "text"));
                } else if (jsonObject.has("translate")) {
                    String string = JsonHelper.getString(jsonObject, "translate");
                    if (jsonObject.has("with")) {
                        JsonArray jsonArray = JsonHelper.getArray(jsonObject, "with");
                        Object[] objects = new Object[jsonArray.size()];
                        for (int i = 0; i < objects.length; ++i) {
                            LiteralText lv2;
                            objects[i] = this.deserialize(jsonArray.get(i), type, jsonDeserializationContext);
                            if (!(objects[i] instanceof LiteralText) || !(lv2 = (LiteralText)objects[i]).getStyle().isEmpty() || !lv2.getSiblings().isEmpty()) continue;
                            objects[i] = lv2.getRawString();
                        }
                        TranslatableText lv3 = new TranslatableText(string, objects);
                    } else {
                        TranslatableText lv4 = new TranslatableText(string);
                    }
                } else if (jsonObject.has("score")) {
                    JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "score");
                    if (!jsonObject2.has("name") || !jsonObject2.has("objective")) throw new JsonParseException("A score component needs a least a name and an objective");
                    ScoreText lv5 = new ScoreText(JsonHelper.getString(jsonObject2, "name"), JsonHelper.getString(jsonObject2, "objective"));
                } else if (jsonObject.has("selector")) {
                    SelectorText lv7 = new SelectorText(JsonHelper.getString(jsonObject, "selector"));
                } else if (jsonObject.has("keybind")) {
                    KeybindText lv8 = new KeybindText(JsonHelper.getString(jsonObject, "keybind"));
                } else {
                    if (!jsonObject.has("nbt")) throw new JsonParseException("Don't know how to turn " + (Object)jsonElement + " into a Component");
                    String string2 = JsonHelper.getString(jsonObject, "nbt");
                    boolean bl = JsonHelper.getBoolean(jsonObject, "interpret", false);
                    if (jsonObject.has("block")) {
                        NbtText.BlockNbtText lv9 = new NbtText.BlockNbtText(string2, bl, JsonHelper.getString(jsonObject, "block"));
                    } else if (jsonObject.has("entity")) {
                        NbtText.EntityNbtText lv10 = new NbtText.EntityNbtText(string2, bl, JsonHelper.getString(jsonObject, "entity"));
                    } else {
                        if (!jsonObject.has("storage")) throw new JsonParseException("Don't know how to turn " + (Object)jsonElement + " into a Component");
                        NbtText.StorageNbtText lv11 = new NbtText.StorageNbtText(string2, bl, new Identifier(JsonHelper.getString(jsonObject, "storage")));
                    }
                }
                if (jsonObject.has("extra")) {
                    JsonArray jsonArray2 = JsonHelper.getArray(jsonObject, "extra");
                    if (jsonArray2.size() <= 0) throw new JsonParseException("Unexpected empty array of components");
                    for (int j = 0; j < jsonArray2.size(); ++j) {
                        lv13.append(this.deserialize(jsonArray2.get(j), type, jsonDeserializationContext));
                    }
                }
                lv13.setStyle((Style)jsonDeserializationContext.deserialize(jsonElement, Style.class));
                return lv13;
            }
            if (!jsonElement.isJsonArray()) throw new JsonParseException("Don't know how to turn " + (Object)jsonElement + " into a Component");
            JsonArray jsonArray3 = jsonElement.getAsJsonArray();
            MutableText lv14 = null;
            for (JsonElement jsonElement2 : jsonArray3) {
                MutableText lv15 = this.deserialize(jsonElement2, jsonElement2.getClass(), jsonDeserializationContext);
                if (lv14 == null) {
                    lv14 = lv15;
                    continue;
                }
                lv14.append(lv15);
            }
            return lv14;
        }

        private void addStyle(Style arg, JsonObject jsonObject, JsonSerializationContext jsonSerializationContext) {
            JsonElement jsonElement = jsonSerializationContext.serialize((Object)arg);
            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject2 = (JsonObject)jsonElement;
                for (Map.Entry entry : jsonObject2.entrySet()) {
                    jsonObject.add((String)entry.getKey(), (JsonElement)entry.getValue());
                }
            }
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        public JsonElement serialize(Text arg, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            if (!arg.getStyle().isEmpty()) {
                this.addStyle(arg.getStyle(), jsonObject, jsonSerializationContext);
            }
            if (!arg.getSiblings().isEmpty()) {
                JsonArray jsonArray = new JsonArray();
                for (Text lv : arg.getSiblings()) {
                    jsonArray.add(this.serialize(lv, lv.getClass(), jsonSerializationContext));
                }
                jsonObject.add("extra", (JsonElement)jsonArray);
            }
            if (arg instanceof LiteralText) {
                jsonObject.addProperty("text", ((LiteralText)arg).getRawString());
                return jsonObject;
            } else if (arg instanceof TranslatableText) {
                TranslatableText lv2 = (TranslatableText)arg;
                jsonObject.addProperty("translate", lv2.getKey());
                if (lv2.getArgs() == null || lv2.getArgs().length <= 0) return jsonObject;
                JsonArray jsonArray2 = new JsonArray();
                for (Object object : lv2.getArgs()) {
                    if (object instanceof Text) {
                        jsonArray2.add(this.serialize((Text)object, object.getClass(), jsonSerializationContext));
                        continue;
                    }
                    jsonArray2.add((JsonElement)new JsonPrimitive(String.valueOf(object)));
                }
                jsonObject.add("with", (JsonElement)jsonArray2);
                return jsonObject;
            } else if (arg instanceof ScoreText) {
                ScoreText lv3 = (ScoreText)arg;
                JsonObject jsonObject2 = new JsonObject();
                jsonObject2.addProperty("name", lv3.getName());
                jsonObject2.addProperty("objective", lv3.getObjective());
                jsonObject.add("score", (JsonElement)jsonObject2);
                return jsonObject;
            } else if (arg instanceof SelectorText) {
                SelectorText lv4 = (SelectorText)arg;
                jsonObject.addProperty("selector", lv4.getPattern());
                return jsonObject;
            } else if (arg instanceof KeybindText) {
                KeybindText lv5 = (KeybindText)arg;
                jsonObject.addProperty("keybind", lv5.getKey());
                return jsonObject;
            } else {
                if (!(arg instanceof NbtText)) throw new IllegalArgumentException("Don't know how to serialize " + arg + " as a Component");
                NbtText lv6 = (NbtText)arg;
                jsonObject.addProperty("nbt", lv6.getPath());
                jsonObject.addProperty("interpret", Boolean.valueOf(lv6.shouldInterpret()));
                if (arg instanceof NbtText.BlockNbtText) {
                    NbtText.BlockNbtText lv7 = (NbtText.BlockNbtText)arg;
                    jsonObject.addProperty("block", lv7.getPos());
                    return jsonObject;
                } else if (arg instanceof NbtText.EntityNbtText) {
                    NbtText.EntityNbtText lv8 = (NbtText.EntityNbtText)arg;
                    jsonObject.addProperty("entity", lv8.getSelector());
                    return jsonObject;
                } else {
                    if (!(arg instanceof NbtText.StorageNbtText)) throw new IllegalArgumentException("Don't know how to serialize " + arg + " as a Component");
                    NbtText.StorageNbtText lv9 = (NbtText.StorageNbtText)arg;
                    jsonObject.addProperty("storage", lv9.getId().toString());
                }
            }
            return jsonObject;
        }

        public static String toJson(Text arg) {
            return GSON.toJson((Object)arg);
        }

        public static JsonElement toJsonTree(Text arg) {
            return GSON.toJsonTree((Object)arg);
        }

        @Nullable
        public static MutableText fromJson(String string) {
            return JsonHelper.deserialize(GSON, string, MutableText.class, false);
        }

        @Nullable
        public static MutableText fromJson(JsonElement jsonElement) {
            return (MutableText)GSON.fromJson(jsonElement, MutableText.class);
        }

        @Nullable
        public static MutableText fromLenientJson(String string) {
            return JsonHelper.deserialize(GSON, string, MutableText.class, true);
        }

        public static MutableText fromJson(com.mojang.brigadier.StringReader stringReader) {
            try {
                JsonReader jsonReader = new JsonReader((Reader)new StringReader(stringReader.getRemaining()));
                jsonReader.setLenient(false);
                MutableText lv = (MutableText)GSON.getAdapter(MutableText.class).read(jsonReader);
                stringReader.setCursor(stringReader.getCursor() + Serializer.getPosition(jsonReader));
                return lv;
            }
            catch (IOException | StackOverflowError throwable) {
                throw new JsonParseException(throwable);
            }
        }

        private static int getPosition(JsonReader jsonReader) {
            try {
                return JSON_READER_POS.getInt((Object)jsonReader) - JSON_READER_LINE_START.getInt((Object)jsonReader) + 1;
            }
            catch (IllegalAccessException illegalAccessException) {
                throw new IllegalStateException("Couldn't read position of JsonReader", illegalAccessException);
            }
        }

        public /* synthetic */ JsonElement serialize(Object object, Type type, JsonSerializationContext jsonSerializationContext) {
            return this.serialize((Text)object, type, jsonSerializationContext);
        }

        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }
}

