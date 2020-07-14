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

    private List<Sound> deserializeSounds(JsonObject json) {
        ArrayList list = Lists.newArrayList();
        if (json.has("sounds")) {
            JsonArray jsonArray = JsonHelper.getArray(json, "sounds");
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

    private Sound deserializeSound(JsonObject json) {
        String string = JsonHelper.getString(json, "name");
        Sound.RegistrationType lv = this.deserializeType(json, Sound.RegistrationType.FILE);
        float f = JsonHelper.getFloat(json, "volume", 1.0f);
        Validate.isTrue((f > 0.0f ? 1 : 0) != 0, (String)"Invalid volume", (Object[])new Object[0]);
        float g = JsonHelper.getFloat(json, "pitch", 1.0f);
        Validate.isTrue((g > 0.0f ? 1 : 0) != 0, (String)"Invalid pitch", (Object[])new Object[0]);
        int i = JsonHelper.getInt(json, "weight", 1);
        Validate.isTrue((i > 0 ? 1 : 0) != 0, (String)"Invalid weight", (Object[])new Object[0]);
        boolean bl = JsonHelper.getBoolean(json, "preload", false);
        boolean bl2 = JsonHelper.getBoolean(json, "stream", false);
        int j = JsonHelper.getInt(json, "attenuation_distance", 16);
        return new Sound(string, f, g, i, lv, bl2, bl, j);
    }

    private Sound.RegistrationType deserializeType(JsonObject json, Sound.RegistrationType fallback) {
        Sound.RegistrationType lv = fallback;
        if (json.has("type")) {
            lv = Sound.RegistrationType.getByName(JsonHelper.getString(json, "type"));
            Validate.notNull((Object)((Object)lv), (String)"Invalid type", (Object[])new Object[0]);
        }
        return lv;
    }

    public /* synthetic */ Object deserialize(JsonElement functionJson, Type unused, JsonDeserializationContext context) throws JsonParseException {
        return this.deserialize(functionJson, unused, context);
    }
}

