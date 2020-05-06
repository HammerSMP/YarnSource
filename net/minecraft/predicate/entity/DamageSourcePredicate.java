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
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Vec3d;

public class DamageSourcePredicate {
    public static final DamageSourcePredicate EMPTY = Builder.create().build();
    private final Boolean isProjectile;
    private final Boolean isExplosion;
    private final Boolean bypassesArmor;
    private final Boolean bypassesInvulnerability;
    private final Boolean bypassesMagic;
    private final Boolean isFire;
    private final Boolean isMagic;
    private final Boolean isLightning;
    private final EntityPredicate directEntity;
    private final EntityPredicate sourceEntity;

    public DamageSourcePredicate(@Nullable Boolean arg, @Nullable Boolean arg2, @Nullable Boolean arg3, @Nullable Boolean arg4, @Nullable Boolean arg5, @Nullable Boolean arg6, @Nullable Boolean arg7, @Nullable Boolean arg8, EntityPredicate arg9, EntityPredicate arg10) {
        this.isProjectile = arg;
        this.isExplosion = arg2;
        this.bypassesArmor = arg3;
        this.bypassesInvulnerability = arg4;
        this.bypassesMagic = arg5;
        this.isFire = arg6;
        this.isMagic = arg7;
        this.isLightning = arg8;
        this.directEntity = arg9;
        this.sourceEntity = arg10;
    }

    public boolean test(ServerPlayerEntity arg, DamageSource arg2) {
        return this.test(arg.getServerWorld(), arg.getPos(), arg2);
    }

    public boolean test(ServerWorld arg, Vec3d arg2, DamageSource arg3) {
        if (this == EMPTY) {
            return true;
        }
        if (this.isProjectile != null && this.isProjectile.booleanValue() != arg3.isProjectile()) {
            return false;
        }
        if (this.isExplosion != null && this.isExplosion.booleanValue() != arg3.isExplosive()) {
            return false;
        }
        if (this.bypassesArmor != null && this.bypassesArmor.booleanValue() != arg3.bypassesArmor()) {
            return false;
        }
        if (this.bypassesInvulnerability != null && this.bypassesInvulnerability.booleanValue() != arg3.isOutOfWorld()) {
            return false;
        }
        if (this.bypassesMagic != null && this.bypassesMagic.booleanValue() != arg3.isUnblockable()) {
            return false;
        }
        if (this.isFire != null && this.isFire.booleanValue() != arg3.isFire()) {
            return false;
        }
        if (this.isMagic != null && this.isMagic.booleanValue() != arg3.getMagic()) {
            return false;
        }
        if (this.isLightning != null && this.isLightning != (arg3 == DamageSource.LIGHTNING_BOLT)) {
            return false;
        }
        if (!this.directEntity.test(arg, arg2, arg3.getSource())) {
            return false;
        }
        return this.sourceEntity.test(arg, arg2, arg3.getAttacker());
    }

    public static DamageSourcePredicate fromJson(@Nullable JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return EMPTY;
        }
        JsonObject jsonObject = JsonHelper.asObject(jsonElement, "damage type");
        Boolean lv = DamageSourcePredicate.getBoolean(jsonObject, "is_projectile");
        Boolean lv2 = DamageSourcePredicate.getBoolean(jsonObject, "is_explosion");
        Boolean lv3 = DamageSourcePredicate.getBoolean(jsonObject, "bypasses_armor");
        Boolean lv4 = DamageSourcePredicate.getBoolean(jsonObject, "bypasses_invulnerability");
        Boolean lv5 = DamageSourcePredicate.getBoolean(jsonObject, "bypasses_magic");
        Boolean lv6 = DamageSourcePredicate.getBoolean(jsonObject, "is_fire");
        Boolean lv7 = DamageSourcePredicate.getBoolean(jsonObject, "is_magic");
        Boolean lv8 = DamageSourcePredicate.getBoolean(jsonObject, "is_lightning");
        EntityPredicate lv9 = EntityPredicate.fromJson(jsonObject.get("direct_entity"));
        EntityPredicate lv10 = EntityPredicate.fromJson(jsonObject.get("source_entity"));
        return new DamageSourcePredicate(lv, lv2, lv3, lv4, lv5, lv6, lv7, lv8, lv9, lv10);
    }

    @Nullable
    private static Boolean getBoolean(JsonObject jsonObject, String string) {
        return jsonObject.has(string) ? Boolean.valueOf(JsonHelper.getBoolean(jsonObject, string)) : null;
    }

    public JsonElement toJson() {
        if (this == EMPTY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        this.addProperty(jsonObject, "is_projectile", this.isProjectile);
        this.addProperty(jsonObject, "is_explosion", this.isExplosion);
        this.addProperty(jsonObject, "bypasses_armor", this.bypassesArmor);
        this.addProperty(jsonObject, "bypasses_invulnerability", this.bypassesInvulnerability);
        this.addProperty(jsonObject, "bypasses_magic", this.bypassesMagic);
        this.addProperty(jsonObject, "is_fire", this.isFire);
        this.addProperty(jsonObject, "is_magic", this.isMagic);
        this.addProperty(jsonObject, "is_lightning", this.isLightning);
        jsonObject.add("direct_entity", this.directEntity.toJson());
        jsonObject.add("source_entity", this.sourceEntity.toJson());
        return jsonObject;
    }

    private void addProperty(JsonObject jsonObject, String string, @Nullable Boolean arg) {
        if (arg != null) {
            jsonObject.addProperty(string, arg);
        }
    }

    public static class Builder {
        private Boolean isProjectile;
        private Boolean isExplosion;
        private Boolean bypassesArmor;
        private Boolean bypassesInvulnerability;
        private Boolean bypassesMagic;
        private Boolean isFire;
        private Boolean isMagic;
        private Boolean isLightning;
        private EntityPredicate directEntity = EntityPredicate.ANY;
        private EntityPredicate sourceEntity = EntityPredicate.ANY;

        public static Builder create() {
            return new Builder();
        }

        public Builder projectile(Boolean arg) {
            this.isProjectile = arg;
            return this;
        }

        public Builder lightning(Boolean arg) {
            this.isLightning = arg;
            return this;
        }

        public Builder directEntity(EntityPredicate.Builder arg) {
            this.directEntity = arg.build();
            return this;
        }

        public DamageSourcePredicate build() {
            return new DamageSourcePredicate(this.isProjectile, this.isExplosion, this.bypassesArmor, this.bypassesInvulnerability, this.bypassesMagic, this.isFire, this.isMagic, this.isLightning, this.directEntity, this.sourceEntity);
        }
    }
}

