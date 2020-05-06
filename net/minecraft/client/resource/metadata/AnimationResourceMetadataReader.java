/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.Validate
 */
package net.minecraft.client.resource.metadata;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.ArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.AnimationFrameResourceMetadata;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.JsonHelper;
import org.apache.commons.lang3.Validate;

@Environment(value=EnvType.CLIENT)
public class AnimationResourceMetadataReader
implements ResourceMetadataReader<AnimationResourceMetadata> {
    @Override
    public AnimationResourceMetadata fromJson(JsonObject jsonObject) {
        ArrayList list = Lists.newArrayList();
        int i = JsonHelper.getInt(jsonObject, "frametime", 1);
        if (i != 1) {
            Validate.inclusiveBetween((long)1L, (long)Integer.MAX_VALUE, (long)i, (String)"Invalid default frame time");
        }
        if (jsonObject.has("frames")) {
            try {
                JsonArray jsonArray = JsonHelper.getArray(jsonObject, "frames");
                for (int j = 0; j < jsonArray.size(); ++j) {
                    JsonElement jsonElement = jsonArray.get(j);
                    AnimationFrameResourceMetadata lv = this.readFrameMetadata(j, jsonElement);
                    if (lv == null) continue;
                    list.add(lv);
                }
            }
            catch (ClassCastException classCastException) {
                throw new JsonParseException("Invalid animation->frames: expected array, was " + (Object)jsonObject.get("frames"), (Throwable)classCastException);
            }
        }
        int k = JsonHelper.getInt(jsonObject, "width", -1);
        int l = JsonHelper.getInt(jsonObject, "height", -1);
        if (k != -1) {
            Validate.inclusiveBetween((long)1L, (long)Integer.MAX_VALUE, (long)k, (String)"Invalid width");
        }
        if (l != -1) {
            Validate.inclusiveBetween((long)1L, (long)Integer.MAX_VALUE, (long)l, (String)"Invalid height");
        }
        boolean bl = JsonHelper.getBoolean(jsonObject, "interpolate", false);
        return new AnimationResourceMetadata(list, k, l, i, bl);
    }

    private AnimationFrameResourceMetadata readFrameMetadata(int i, JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            return new AnimationFrameResourceMetadata(JsonHelper.asInt(jsonElement, "frames[" + i + "]"));
        }
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = JsonHelper.asObject(jsonElement, "frames[" + i + "]");
            int j = JsonHelper.getInt(jsonObject, "time", -1);
            if (jsonObject.has("time")) {
                Validate.inclusiveBetween((long)1L, (long)Integer.MAX_VALUE, (long)j, (String)"Invalid frame time");
            }
            int k = JsonHelper.getInt(jsonObject, "index");
            Validate.inclusiveBetween((long)0L, (long)Integer.MAX_VALUE, (long)k, (String)"Invalid frame index");
            return new AnimationFrameResourceMetadata(k, j);
        }
        return null;
    }

    @Override
    public String getKey() {
        return "animation";
    }

    @Override
    public /* synthetic */ Object fromJson(JsonObject jsonObject) {
        return this.fromJson(jsonObject);
    }
}

