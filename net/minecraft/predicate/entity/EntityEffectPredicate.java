/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  javax.annotation.Nullable
 */
package net.minecraft.predicate.entity;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.predicate.NumberRange;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class EntityEffectPredicate {
    public static final EntityEffectPredicate EMPTY = new EntityEffectPredicate(Collections.emptyMap());
    private final Map<StatusEffect, EffectData> effects;

    public EntityEffectPredicate(Map<StatusEffect, EffectData> map) {
        this.effects = map;
    }

    public static EntityEffectPredicate create() {
        return new EntityEffectPredicate(Maps.newLinkedHashMap());
    }

    public EntityEffectPredicate withEffect(StatusEffect arg) {
        this.effects.put(arg, new EffectData());
        return this;
    }

    public boolean test(Entity arg) {
        if (this == EMPTY) {
            return true;
        }
        if (arg instanceof LivingEntity) {
            return this.test(((LivingEntity)arg).getActiveStatusEffects());
        }
        return false;
    }

    public boolean test(LivingEntity arg) {
        if (this == EMPTY) {
            return true;
        }
        return this.test(arg.getActiveStatusEffects());
    }

    public boolean test(Map<StatusEffect, StatusEffectInstance> map) {
        if (this == EMPTY) {
            return true;
        }
        for (Map.Entry<StatusEffect, EffectData> entry : this.effects.entrySet()) {
            StatusEffectInstance lv = map.get(entry.getKey());
            if (entry.getValue().test(lv)) continue;
            return false;
        }
        return true;
    }

    public static EntityEffectPredicate fromJson(@Nullable JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return EMPTY;
        }
        JsonObject jsonObject = JsonHelper.asObject(jsonElement, "effects");
        LinkedHashMap map = Maps.newLinkedHashMap();
        for (Map.Entry entry : jsonObject.entrySet()) {
            Identifier lv = new Identifier((String)entry.getKey());
            StatusEffect lv2 = Registry.STATUS_EFFECT.getOrEmpty(lv).orElseThrow(() -> new JsonSyntaxException("Unknown effect '" + lv + "'"));
            EffectData lv3 = EffectData.fromJson(JsonHelper.asObject((JsonElement)entry.getValue(), (String)entry.getKey()));
            map.put(lv2, lv3);
        }
        return new EntityEffectPredicate(map);
    }

    public JsonElement toJson() {
        if (this == EMPTY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        for (Map.Entry<StatusEffect, EffectData> entry : this.effects.entrySet()) {
            jsonObject.add(Registry.STATUS_EFFECT.getId(entry.getKey()).toString(), entry.getValue().toJson());
        }
        return jsonObject;
    }

    public static class EffectData {
        private final NumberRange.IntRange amplifier;
        private final NumberRange.IntRange duration;
        @Nullable
        private final Boolean ambient;
        @Nullable
        private final Boolean visible;

        public EffectData(NumberRange.IntRange arg, NumberRange.IntRange arg2, @Nullable Boolean boolean_, @Nullable Boolean boolean2) {
            this.amplifier = arg;
            this.duration = arg2;
            this.ambient = boolean_;
            this.visible = boolean2;
        }

        public EffectData() {
            this(NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, null, null);
        }

        public boolean test(@Nullable StatusEffectInstance arg) {
            if (arg == null) {
                return false;
            }
            if (!this.amplifier.test(arg.getAmplifier())) {
                return false;
            }
            if (!this.duration.test(arg.getDuration())) {
                return false;
            }
            if (this.ambient != null && this.ambient.booleanValue() != arg.isAmbient()) {
                return false;
            }
            return this.visible == null || this.visible.booleanValue() == arg.shouldShowParticles();
        }

        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("amplifier", this.amplifier.toJson());
            jsonObject.add("duration", this.duration.toJson());
            jsonObject.addProperty("ambient", this.ambient);
            jsonObject.addProperty("visible", this.visible);
            return jsonObject;
        }

        public static EffectData fromJson(JsonObject jsonObject) {
            NumberRange.IntRange lv = NumberRange.IntRange.fromJson(jsonObject.get("amplifier"));
            NumberRange.IntRange lv2 = NumberRange.IntRange.fromJson(jsonObject.get("duration"));
            Boolean boolean_ = jsonObject.has("ambient") ? Boolean.valueOf(JsonHelper.getBoolean(jsonObject, "ambient")) : null;
            Boolean boolean2 = jsonObject.has("visible") ? Boolean.valueOf(JsonHelper.getBoolean(jsonObject, "visible")) : null;
            return new EffectData(lv, lv2, boolean_, boolean2);
        }
    }
}

