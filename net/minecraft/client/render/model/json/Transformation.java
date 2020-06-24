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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Quaternion;

@Environment(value=EnvType.CLIENT)
public class Transformation {
    public static final Transformation IDENTITY = new Transformation(new Vector3f(), new Vector3f(), new Vector3f(1.0f, 1.0f, 1.0f));
    public final Vector3f rotation;
    public final Vector3f translation;
    public final Vector3f scale;

    public Transformation(Vector3f arg, Vector3f arg2, Vector3f arg3) {
        this.rotation = arg.copy();
        this.translation = arg2.copy();
        this.scale = arg3.copy();
    }

    public void apply(boolean bl, MatrixStack arg) {
        if (this == IDENTITY) {
            return;
        }
        float f = this.rotation.getX();
        float g = this.rotation.getY();
        float h = this.rotation.getZ();
        if (bl) {
            g = -g;
            h = -h;
        }
        int i = bl ? -1 : 1;
        arg.translate((float)i * this.translation.getX(), this.translation.getY(), this.translation.getZ());
        arg.multiply(new Quaternion(f, g, h, true));
        arg.scale(this.scale.getX(), this.scale.getY(), this.scale.getZ());
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (this.getClass() == object.getClass()) {
            Transformation lv = (Transformation)object;
            return this.rotation.equals(lv.rotation) && this.scale.equals(lv.scale) && this.translation.equals(lv.translation);
        }
        return false;
    }

    public int hashCode() {
        int i = this.rotation.hashCode();
        i = 31 * i + this.translation.hashCode();
        i = 31 * i + this.scale.hashCode();
        return i;
    }

    @Environment(value=EnvType.CLIENT)
    public static class Deserializer
    implements JsonDeserializer<Transformation> {
        private static final Vector3f DEFAULT_ROTATION = new Vector3f(0.0f, 0.0f, 0.0f);
        private static final Vector3f DEFAULT_TRANSLATION = new Vector3f(0.0f, 0.0f, 0.0f);
        private static final Vector3f DEFAULT_SCALE = new Vector3f(1.0f, 1.0f, 1.0f);

        protected Deserializer() {
        }

        public Transformation deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Vector3f lv = this.parseVector3f(jsonObject, "rotation", DEFAULT_ROTATION);
            Vector3f lv2 = this.parseVector3f(jsonObject, "translation", DEFAULT_TRANSLATION);
            lv2.scale(0.0625f);
            lv2.clamp(-5.0f, 5.0f);
            Vector3f lv3 = this.parseVector3f(jsonObject, "scale", DEFAULT_SCALE);
            lv3.clamp(-4.0f, 4.0f);
            return new Transformation(lv, lv2, lv3);
        }

        private Vector3f parseVector3f(JsonObject jsonObject, String string, Vector3f arg) {
            if (!jsonObject.has(string)) {
                return arg;
            }
            JsonArray jsonArray = JsonHelper.getArray(jsonObject, string);
            if (jsonArray.size() != 3) {
                throw new JsonParseException("Expected 3 " + string + " values, found: " + jsonArray.size());
            }
            float[] fs = new float[3];
            for (int i = 0; i < fs.length; ++i) {
                fs[i] = JsonHelper.asFloat(jsonArray.get(i), string + "[" + i + "]");
            }
            return new Vector3f(fs[0], fs[1], fs[2]);
        }

        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }
}

