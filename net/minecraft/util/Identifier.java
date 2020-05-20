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
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.StringUtils
 */
package net.minecraft.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.JsonHelper;
import org.apache.commons.lang3.StringUtils;

public class Identifier
implements Comparable<Identifier> {
    public static final Codec<Identifier> field_25139 = Codec.STRING.comapFlatMap(Identifier::method_29186, Identifier::toString).stable();
    private static final SimpleCommandExceptionType COMMAND_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("argument.id.invalid"));
    protected final String namespace;
    protected final String path;

    protected Identifier(String[] strings) {
        this.namespace = StringUtils.isEmpty((CharSequence)strings[0]) ? "minecraft" : strings[0];
        this.path = strings[1];
        if (!Identifier.isNamespaceValid(this.namespace)) {
            throw new InvalidIdentifierException("Non [a-z0-9_.-] character in namespace of location: " + this.namespace + ':' + this.path);
        }
        if (!Identifier.isPathValid(this.path)) {
            throw new InvalidIdentifierException("Non [a-z0-9/._-] character in path of location: " + this.namespace + ':' + this.path);
        }
    }

    public Identifier(String string) {
        this(Identifier.split(string, ':'));
    }

    public Identifier(String string, String string2) {
        this(new String[]{string, string2});
    }

    public static Identifier splitOn(String string, char c) {
        return new Identifier(Identifier.split(string, c));
    }

    @Nullable
    public static Identifier tryParse(String string) {
        try {
            return new Identifier(string);
        }
        catch (InvalidIdentifierException lv) {
            return null;
        }
    }

    protected static String[] split(String string, char c) {
        String[] strings = new String[]{"minecraft", string};
        int i = string.indexOf(c);
        if (i >= 0) {
            strings[1] = string.substring(i + 1, string.length());
            if (i >= 1) {
                strings[0] = string.substring(0, i);
            }
        }
        return strings;
    }

    private static DataResult<Identifier> method_29186(String string) {
        try {
            return DataResult.success((Object)new Identifier(string));
        }
        catch (InvalidIdentifierException lv) {
            return DataResult.error((String)("Not a valid resource location: " + string + " " + lv.getMessage()));
        }
    }

    public String getPath() {
        return this.path;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public String toString() {
        return this.namespace + ':' + this.path;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof Identifier) {
            Identifier lv = (Identifier)object;
            return this.namespace.equals(lv.namespace) && this.path.equals(lv.path);
        }
        return false;
    }

    public int hashCode() {
        return 31 * this.namespace.hashCode() + this.path.hashCode();
    }

    @Override
    public int compareTo(Identifier arg) {
        int i = this.path.compareTo(arg.path);
        if (i == 0) {
            i = this.namespace.compareTo(arg.namespace);
        }
        return i;
    }

    public static Identifier fromCommandInput(StringReader stringReader) throws CommandSyntaxException {
        int i = stringReader.getCursor();
        while (stringReader.canRead() && Identifier.isCharValid(stringReader.peek())) {
            stringReader.skip();
        }
        String string = stringReader.getString().substring(i, stringReader.getCursor());
        try {
            return new Identifier(string);
        }
        catch (InvalidIdentifierException lv) {
            stringReader.setCursor(i);
            throw COMMAND_EXCEPTION.createWithContext((ImmutableStringReader)stringReader);
        }
    }

    public static boolean isCharValid(char c) {
        return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == '_' || c == ':' || c == '/' || c == '.' || c == '-';
    }

    private static boolean isPathValid(String string) {
        for (int i = 0; i < string.length(); ++i) {
            if (Identifier.method_29184(string.charAt(i))) continue;
            return false;
        }
        return true;
    }

    private static boolean isNamespaceValid(String string) {
        for (int i = 0; i < string.length(); ++i) {
            if (Identifier.method_29185(string.charAt(i))) continue;
            return false;
        }
        return true;
    }

    private static boolean method_29184(char c) {
        return c == '_' || c == '-' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '/' || c == '.';
    }

    private static boolean method_29185(char c) {
        return c == '_' || c == '-' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '.';
    }

    @Environment(value=EnvType.CLIENT)
    public static boolean isValid(String string) {
        String[] strings = Identifier.split(string, ':');
        return Identifier.isNamespaceValid(StringUtils.isEmpty((CharSequence)strings[0]) ? "minecraft" : strings[0]) && Identifier.isPathValid(strings[1]);
    }

    @Override
    public /* synthetic */ int compareTo(Object object) {
        return this.compareTo((Identifier)object);
    }

    public static class Serializer
    implements JsonDeserializer<Identifier>,
    JsonSerializer<Identifier> {
        public Identifier deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return new Identifier(JsonHelper.asString(jsonElement, "location"));
        }

        public JsonElement serialize(Identifier arg, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(arg.toString());
        }

        public /* synthetic */ JsonElement serialize(Object object, Type type, JsonSerializationContext jsonSerializationContext) {
            return this.serialize((Identifier)object, type, jsonSerializationContext);
        }

        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }
}

