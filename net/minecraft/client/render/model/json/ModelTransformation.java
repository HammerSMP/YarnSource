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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.Transformation;

@Environment(value=EnvType.CLIENT)
public class ModelTransformation {
    public static final ModelTransformation NONE = new ModelTransformation();
    public final Transformation thirdPersonLeftHand;
    public final Transformation thirdPersonRightHand;
    public final Transformation firstPersonLeftHand;
    public final Transformation firstPersonRightHand;
    public final Transformation head;
    public final Transformation gui;
    public final Transformation ground;
    public final Transformation fixed;

    private ModelTransformation() {
        this(Transformation.IDENTITY, Transformation.IDENTITY, Transformation.IDENTITY, Transformation.IDENTITY, Transformation.IDENTITY, Transformation.IDENTITY, Transformation.IDENTITY, Transformation.IDENTITY);
    }

    public ModelTransformation(ModelTransformation arg) {
        this.thirdPersonLeftHand = arg.thirdPersonLeftHand;
        this.thirdPersonRightHand = arg.thirdPersonRightHand;
        this.firstPersonLeftHand = arg.firstPersonLeftHand;
        this.firstPersonRightHand = arg.firstPersonRightHand;
        this.head = arg.head;
        this.gui = arg.gui;
        this.ground = arg.ground;
        this.fixed = arg.fixed;
    }

    public ModelTransformation(Transformation arg, Transformation arg2, Transformation arg3, Transformation arg4, Transformation arg5, Transformation arg6, Transformation arg7, Transformation arg8) {
        this.thirdPersonLeftHand = arg;
        this.thirdPersonRightHand = arg2;
        this.firstPersonLeftHand = arg3;
        this.firstPersonRightHand = arg4;
        this.head = arg5;
        this.gui = arg6;
        this.ground = arg7;
        this.fixed = arg8;
    }

    public Transformation getTransformation(Mode arg) {
        switch (arg) {
            case THIRD_PERSON_LEFT_HAND: {
                return this.thirdPersonLeftHand;
            }
            case THIRD_PERSON_RIGHT_HAND: {
                return this.thirdPersonRightHand;
            }
            case FIRST_PERSON_LEFT_HAND: {
                return this.firstPersonLeftHand;
            }
            case FIRST_PERSON_RIGHT_HAND: {
                return this.firstPersonRightHand;
            }
            case HEAD: {
                return this.head;
            }
            case GUI: {
                return this.gui;
            }
            case GROUND: {
                return this.ground;
            }
            case FIXED: {
                return this.fixed;
            }
        }
        return Transformation.IDENTITY;
    }

    public boolean isTransformationDefined(Mode arg) {
        return this.getTransformation(arg) != Transformation.IDENTITY;
    }

    @Environment(value=EnvType.CLIENT)
    public static class Deserializer
    implements JsonDeserializer<ModelTransformation> {
        protected Deserializer() {
        }

        public ModelTransformation deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Transformation lv = this.parseModelTransformation(jsonDeserializationContext, jsonObject, "thirdperson_righthand");
            Transformation lv2 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, "thirdperson_lefthand");
            if (lv2 == Transformation.IDENTITY) {
                lv2 = lv;
            }
            Transformation lv3 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, "firstperson_righthand");
            Transformation lv4 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, "firstperson_lefthand");
            if (lv4 == Transformation.IDENTITY) {
                lv4 = lv3;
            }
            Transformation lv5 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, "head");
            Transformation lv6 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, "gui");
            Transformation lv7 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, "ground");
            Transformation lv8 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, "fixed");
            return new ModelTransformation(lv2, lv, lv4, lv3, lv5, lv6, lv7, lv8);
        }

        private Transformation parseModelTransformation(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject, String string) {
            if (jsonObject.has(string)) {
                return (Transformation)jsonDeserializationContext.deserialize(jsonObject.get(string), Transformation.class);
            }
            return Transformation.IDENTITY;
        }

        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Mode {
        NONE,
        THIRD_PERSON_LEFT_HAND,
        THIRD_PERSON_RIGHT_HAND,
        FIRST_PERSON_LEFT_HAND,
        FIRST_PERSON_RIGHT_HAND,
        HEAD,
        GUI,
        GROUND,
        FIXED;


        public boolean isFirstPerson() {
            return this == FIRST_PERSON_LEFT_HAND || this == FIRST_PERSON_RIGHT_HAND;
        }
    }
}

