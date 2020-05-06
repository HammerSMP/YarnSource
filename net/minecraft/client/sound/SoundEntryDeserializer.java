/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.Validate
 */
package net.minecraft.client.sound;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundEntry;
import net.minecraft.util.JsonHelper;
import org.apache.commons.lang3.Validate;

@Environment(value=EnvType.CLIENT)
public class SoundEntryDeserializer
implements JsonDeserializer<SoundEntry> {
    public SoundEntry deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = JsonHelper.asObject(jsonElement, "entry");
        boolean bl = JsonHelper.getBoolean(jsonObject, "replace", false);
        String string = JsonHelper.getString(jsonObject, "subtitle", null);
        List<Sound> list = this.deserializeSounds(jsonObject);
        return new SoundEntry(list, bl, string);
    }

    private List<Sound> deserializeSounds(JsonObject jsonObject) {
        ArrayList list = Lists.newArrayList();
        if (jsonObject.has("sounds")) {
            JsonArray jsonArray = JsonHelper.getArray(jsonObject, "sounds");
            for (int i = 0; i < jsonArray.size(); ++i) {
                JsonElement jsonElement = jsonArray.get(i);
                if (JsonHelper.isString(jsonElement)) {
                    String string = JsonHelper.asString(jsonElement, "sound");
                    list.add(new Sound(string, 1.0f, 1.0f, 1, Sound.RegistrationType.FILE, false, false, 16));
                    continue;
                }
                list.add(this.deserializeSound(JsonHelper.asObject(jsonElement, "sound")));
            }
        }
        return list;
    }

    private Sound deserializeSound(JsonObject jsonObject) {
        String string = JsonHelper.getString(jsonObject, "name");
        Sound.RegistrationType lv = this.deserializeType(jsonObject, Sound.RegistrationType.FILE);
        float f = JsonHelper.getFloat(jsonObject, "volume", 1.0f);
        Validate.isTrue((f > 0.0f ? 1 : 0) != 0, (String)"Invalid volume", (Object[])new Object[0]);
        float g = JsonHelper.getFloat(jsonObject, "pitch", 1.0f);
        Validate.isTrue((g > 0.0f ? 1 : 0) != 0, (String)"Invalid pitch", (Object[])new Object[0]);
        int i = JsonHelper.getInt(jsonObject, "weight", 1);
        Validate.isTrue((i > 0 ? 1 : 0) != 0, (String)"Invalid weight", (Object[])new Object[0]);
        boolean bl = JsonHelper.getBoolean(jsonObject, "preload", false);
        boolean bl2 = JsonHelper.getBoolean(jsonObject, "stream", false);
        int j = JsonHelper.getInt(jsonObject, "attenuation_distance", 16);
        return new Sound(string, f, g, i, lv, bl2, bl, j);
    }

    private Sound.RegistrationType deserializeType(JsonObject jsonObject, Sound.RegistrationType arg) {
        Sound.RegistrationType lv = arg;
        if (jsonObject.has("type")) {
            lv = Sound.RegistrationType.getByName(JsonHelper.getString(jsonObject, "type"));
            Validate.notNull((Object)((Object)lv), (String)"Invalid type", (Object[])new Object[0]);
        }
        return lv;
    }

    public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return this.deserialize(jsonElement, type, jsonDeserializationContext);
    }
}

