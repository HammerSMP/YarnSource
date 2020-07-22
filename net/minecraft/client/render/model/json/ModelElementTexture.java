/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.JsonHelper;

@Environment(value=EnvType.CLIENT)
public class ModelElementTexture {
    public float[] uvs;
    public final int rotation;

    public ModelElementTexture(@Nullable float[] uvs, int rotation) {
        this.uvs = uvs;
        this.rotation = rotation;
    }

    public float getU(int rotation) {
        if (this.uvs == null) {
            throw new NullPointerException("uvs");
        }
        int j = this.getRotatedUVIndex(rotation);
        return this.uvs[j == 0 || j == 1 ? 0 : 2];
    }

    public float getV(int rotation) {
        if (this.uvs == null) {
            throw new NullPointerException("uvs");
        }
        int j = this.getRotatedUVIndex(rotation);
        return this.uvs[j == 0 || j == 3 ? 1 : 3];
    }

    private int getRotatedUVIndex(int rotation) {
        return (rotation + this.rotation / 90) % 4;
    }

    public int getDirectionIndex(int offset) {
        return (offset + 4 - this.rotation / 90) % 4;
    }

    public void setUvs(float[] uvs) {
        if (this.uvs == null) {
            this.uvs = uvs;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Deserializer
    implements JsonDeserializer<ModelElementTexture> {
        protected Deserializer() {
        }

        public ModelElementTexture deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            float[] fs = this.deserializeUVs(jsonObject);
            int i = this.deserializeRotation(jsonObject);
            return new ModelElementTexture(fs, i);
        }

        protected int deserializeRotation(JsonObject object) {
            int i = JsonHelper.getInt(object, "rotation", 0);
            if (i < 0 || i % 90 != 0 || i / 90 > 3) {
                throw new JsonParseException("Invalid rotation " + i + " found, only 0/90/180/270 allowed");
            }
            return i;
        }

        @Nullable
        private float[] deserializeUVs(JsonObject object) {
            if (!object.has("uv")) {
                return null;
            }
            JsonArray jsonArray = JsonHelper.getArray(object, "uv");
            if (jsonArray.size() != 4) {
                throw new JsonParseException("Expected 4 uv values, found: " + jsonArray.size());
            }
            float[] fs = new float[4];
            for (int i = 0; i < fs.length; ++i) {
                fs[i] = JsonHelper.asFloat(jsonArray.get(i), "uv[" + i + "]");
            }
            return fs;
        }

        public /* synthetic */ Object deserialize(JsonElement functionJson, Type unused, JsonDeserializationContext context) throws JsonParseException {
            return this.deserialize(functionJson, unused, context);
        }
    }
}

