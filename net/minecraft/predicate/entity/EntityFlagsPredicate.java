/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  javax.annotation.Nullable
 */
package net.minecraft.predicate.entity;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.JsonHelper;

public class EntityFlagsPredicate {
    public static final EntityFlagsPredicate ANY = new Builder().build();
    @Nullable
    private final Boolean isOnFire;
    @Nullable
    private final Boolean isSneaking;
    @Nullable
    private final Boolean isSprinting;
    @Nullable
    private final Boolean isSwimming;
    @Nullable
    private final Boolean isBaby;

    public EntityFlagsPredicate(@Nullable Boolean arg, @Nullable Boolean arg2, @Nullable Boolean arg3, @Nullable Boolean arg4, @Nullable Boolean arg5) {
        this.isOnFire = arg;
        this.isSneaking = arg2;
        this.isSprinting = arg3;
        this.isSwimming = arg4;
        this.isBaby = arg5;
    }

    public boolean test(Entity arg) {
        if (this.isOnFire != null && arg.isOnFire() != this.isOnFire.booleanValue()) {
            return false;
        }
        if (this.isSneaking != null && arg.isInSneakingPose() != this.isSneaking.booleanValue()) {
            return false;
        }
        if (this.isSprinting != null && arg.isSprinting() != this.isSprinting.booleanValue()) {
            return false;
        }
        if (this.isSwimming != null && arg.isSwimming() != this.isSwimming.booleanValue()) {
            return false;
        }
        return this.isBaby == null || !(arg instanceof LivingEntity) || ((LivingEntity)arg).isBaby() == this.isBaby.booleanValue();
    }

    @Nullable
    private static Boolean nullableBooleanFromJson(JsonObject jsonObject, String string) {
        return jsonObject.has(string) ? Boolean.valueOf(JsonHelper.getBoolean(jsonObject, string)) : null;
    }

    public static EntityFlagsPredicate fromJson(@Nullable JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return ANY;
        }
        JsonObject jsonObject = JsonHelper.asObject(jsonElement, "entity flags");
        Boolean lv = EntityFlagsPredicate.nullableBooleanFromJson(jsonObject, "is_on_fire");
        Boolean lv2 = EntityFlagsPredicate.nullableBooleanFromJson(jsonObject, "is_sneaking");
        Boolean lv3 = EntityFlagsPredicate.nullableBooleanFromJson(jsonObject, "is_sprinting");
        Boolean lv4 = EntityFlagsPredicate.nullableBooleanFromJson(jsonObject, "is_swimming");
        Boolean lv5 = EntityFlagsPredicate.nullableBooleanFromJson(jsonObject, "is_baby");
        return new EntityFlagsPredicate(lv, lv2, lv3, lv4, lv5);
    }

    private void nullableBooleanToJson(JsonObject jsonObject, String string, @Nullable Boolean arg) {
        if (arg != null) {
            jsonObject.addProperty(string, arg);
        }
    }

    public JsonElement toJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        this.nullableBooleanToJson(jsonObject, "is_on_fire", this.isOnFire);
        this.nullableBooleanToJson(jsonObject, "is_sneaking", this.isSneaking);
        this.nullableBooleanToJson(jsonObject, "is_sprinting", this.isSprinting);
        this.nullableBooleanToJson(jsonObject, "is_swimming", this.isSwimming);
        this.nullableBooleanToJson(jsonObject, "is_baby", this.isBaby);
        return jsonObject;
    }

    public static class Builder {
        @Nullable
        private Boolean isOnFire;
        @Nullable
        private Boolean isSneaking;
        @Nullable
        private Boolean isSprinting;
        @Nullable
        private Boolean isSwimming;
        @Nullable
        private Boolean isBaby;

        public static Builder create() {
            return new Builder();
        }

        public Builder onFire(@Nullable Boolean arg) {
            this.isOnFire = arg;
            return this;
        }

        public EntityFlagsPredicate build() {
            return new EntityFlagsPredicate(this.isOnFire, this.isSneaking, this.isSprinting, this.isSwimming, this.isBaby);
        }
    }
}
