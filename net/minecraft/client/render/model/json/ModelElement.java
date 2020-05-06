/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
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

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelRotation;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ModelElement {
    public final Vector3f from;
    public final Vector3f to;
    public final Map<Direction, ModelElementFace> faces;
    public final ModelRotation rotation;
    public final boolean shade;

    public ModelElement(Vector3f arg, Vector3f arg2, Map<Direction, ModelElementFace> map, @Nullable ModelRotation arg3, boolean bl) {
        this.from = arg;
        this.to = arg2;
        this.faces = map;
        this.rotation = arg3;
        this.shade = bl;
        this.initTextures();
    }

    private void initTextures() {
        for (Map.Entry<Direction, ModelElementFace> entry : this.faces.entrySet()) {
            float[] fs = this.getRotatedMatrix(entry.getKey());
            entry.getValue().textureData.setUvs(fs);
        }
    }

    private float[] getRotatedMatrix(Direction arg) {
        switch (arg) {
            case DOWN: {
                return new float[]{this.from.getX(), 16.0f - this.to.getZ(), this.to.getX(), 16.0f - this.from.getZ()};
            }
            case UP: {
                return new float[]{this.from.getX(), this.from.getZ(), this.to.getX(), this.to.getZ()};
            }
            default: {
                return new float[]{16.0f - this.to.getX(), 16.0f - this.to.getY(), 16.0f - this.from.getX(), 16.0f - this.from.getY()};
            }
            case SOUTH: {
                return new float[]{this.from.getX(), 16.0f - this.to.getY(), this.to.getX(), 16.0f - this.from.getY()};
            }
            case WEST: {
                return new float[]{this.from.getZ(), 16.0f - this.to.getY(), this.to.getZ(), 16.0f - this.from.getY()};
            }
            case EAST: 
        }
        return new float[]{16.0f - this.to.getZ(), 16.0f - this.to.getY(), 16.0f - this.from.getZ(), 16.0f - this.from.getY()};
    }

    @Environment(value=EnvType.CLIENT)
    public static class Deserializer
    implements JsonDeserializer<ModelElement> {
        protected Deserializer() {
        }

        public ModelElement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Vector3f lv = this.deserializeFrom(jsonObject);
            Vector3f lv2 = this.deserializeTo(jsonObject);
            ModelRotation lv3 = this.deserializeRotation(jsonObject);
            Map<Direction, ModelElementFace> map = this.deserializeFacesValidating(jsonDeserializationContext, jsonObject);
            if (jsonObject.has("shade") && !JsonHelper.hasBoolean(jsonObject, "shade")) {
                throw new JsonParseException("Expected shade to be a Boolean");
            }
            boolean bl = JsonHelper.getBoolean(jsonObject, "shade", true);
            return new ModelElement(lv, lv2, map, lv3, bl);
        }

        @Nullable
        private ModelRotation deserializeRotation(JsonObject jsonObject) {
            ModelRotation lv = null;
            if (jsonObject.has("rotation")) {
                JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "rotation");
                Vector3f lv2 = this.deserializeVec3f(jsonObject2, "origin");
                lv2.scale(0.0625f);
                Direction.Axis lv3 = this.deserializeAxis(jsonObject2);
                float f = this.deserializeRotationAngle(jsonObject2);
                boolean bl = JsonHelper.getBoolean(jsonObject2, "rescale", false);
                lv = new ModelRotation(lv2, lv3, f, bl);
            }
            return lv;
        }

        private float deserializeRotationAngle(JsonObject jsonObject) {
            float f = JsonHelper.getFloat(jsonObject, "angle");
            if (f != 0.0f && MathHelper.abs(f) != 22.5f && MathHelper.abs(f) != 45.0f) {
                throw new JsonParseException("Invalid rotation " + f + " found, only -45/-22.5/0/22.5/45 allowed");
            }
            return f;
        }

        private Direction.Axis deserializeAxis(JsonObject jsonObject) {
            String string = JsonHelper.getString(jsonObject, "axis");
            Direction.Axis lv = Direction.Axis.fromName(string.toLowerCase(Locale.ROOT));
            if (lv == null) {
                throw new JsonParseException("Invalid rotation axis: " + string);
            }
            return lv;
        }

        private Map<Direction, ModelElementFace> deserializeFacesValidating(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
            Map<Direction, ModelElementFace> map = this.deserializeFaces(jsonDeserializationContext, jsonObject);
            if (map.isEmpty()) {
                throw new JsonParseException("Expected between 1 and 6 unique faces, got 0");
            }
            return map;
        }

        private Map<Direction, ModelElementFace> deserializeFaces(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
            EnumMap map = Maps.newEnumMap(Direction.class);
            JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "faces");
            for (Map.Entry entry : jsonObject2.entrySet()) {
                Direction lv = this.getDirection((String)entry.getKey());
                map.put(lv, jsonDeserializationContext.deserialize((JsonElement)entry.getValue(), ModelElementFace.class));
            }
            return map;
        }

        private Direction getDirection(String string) {
            Direction lv = Direction.byName(string);
            if (lv == null) {
                throw new JsonParseException("Unknown facing: " + string);
            }
            return lv;
        }

        private Vector3f deserializeTo(JsonObject jsonObject) {
            Vector3f lv = this.deserializeVec3f(jsonObject, "to");
            if (lv.getX() < -16.0f || lv.getY() < -16.0f || lv.getZ() < -16.0f || lv.getX() > 32.0f || lv.getY() > 32.0f || lv.getZ() > 32.0f) {
                throw new JsonParseException("'to' specifier exceeds the allowed boundaries: " + lv);
            }
            return lv;
        }

        private Vector3f deserializeFrom(JsonObject jsonObject) {
            Vector3f lv = this.deserializeVec3f(jsonObject, "from");
            if (lv.getX() < -16.0f || lv.getY() < -16.0f || lv.getZ() < -16.0f || lv.getX() > 32.0f || lv.getY() > 32.0f || lv.getZ() > 32.0f) {
                throw new JsonParseException("'from' specifier exceeds the allowed boundaries: " + lv);
            }
            return lv;
        }

        private Vector3f deserializeVec3f(JsonObject jsonObject, String string) {
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

