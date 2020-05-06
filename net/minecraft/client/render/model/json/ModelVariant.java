/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.util.math.AffineTransformation;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

@Environment(value=EnvType.CLIENT)
public class ModelVariant
implements ModelBakeSettings {
    private final Identifier location;
    private final AffineTransformation rotation;
    private final boolean uvLock;
    private final int weight;

    public ModelVariant(Identifier arg, AffineTransformation arg2, boolean bl, int i) {
        this.location = arg;
        this.rotation = arg2;
        this.uvLock = bl;
        this.weight = i;
    }

    public Identifier getLocation() {
        return this.location;
    }

    @Override
    public AffineTransformation getRotation() {
        return this.rotation;
    }

    @Override
    public boolean isShaded() {
        return this.uvLock;
    }

    public int getWeight() {
        return this.weight;
    }

    public String toString() {
        return "Variant{modelLocation=" + this.location + ", rotation=" + this.rotation + ", uvLock=" + this.uvLock + ", weight=" + this.weight + '}';
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof ModelVariant) {
            ModelVariant lv = (ModelVariant)object;
            return this.location.equals(lv.location) && Objects.equals(this.rotation, lv.rotation) && this.uvLock == lv.uvLock && this.weight == lv.weight;
        }
        return false;
    }

    public int hashCode() {
        int i = this.location.hashCode();
        i = 31 * i + this.rotation.hashCode();
        i = 31 * i + Boolean.valueOf(this.uvLock).hashCode();
        i = 31 * i + this.weight;
        return i;
    }

    @Environment(value=EnvType.CLIENT)
    public static class Deserializer
    implements JsonDeserializer<ModelVariant> {
        public ModelVariant deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Identifier lv = this.deserializeModel(jsonObject);
            ModelRotation lv2 = this.deserializeRotation(jsonObject);
            boolean bl = this.deserializeUvLock(jsonObject);
            int i = this.deserializeWeight(jsonObject);
            return new ModelVariant(lv, lv2.getRotation(), bl, i);
        }

        private boolean deserializeUvLock(JsonObject jsonObject) {
            return JsonHelper.getBoolean(jsonObject, "uvlock", false);
        }

        protected ModelRotation deserializeRotation(JsonObject jsonObject) {
            int j;
            int i = JsonHelper.getInt(jsonObject, "x", 0);
            ModelRotation lv = ModelRotation.get(i, j = JsonHelper.getInt(jsonObject, "y", 0));
            if (lv == null) {
                throw new JsonParseException("Invalid BlockModelRotation x: " + i + ", y: " + j);
            }
            return lv;
        }

        protected Identifier deserializeModel(JsonObject jsonObject) {
            return new Identifier(JsonHelper.getString(jsonObject, "model"));
        }

        protected int deserializeWeight(JsonObject jsonObject) {
            int i = JsonHelper.getInt(jsonObject, "weight", 1);
            if (i < 1) {
                throw new JsonParseException("Invalid weight " + i + " found, expected integer >= 1");
            }
            return i;
        }

        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }
}

