/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  javax.annotation.Nullable
 */
package net.minecraft.predicate;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.JsonHelper;

public class DamagePredicate {
    public static final DamagePredicate ANY = Builder.create().build();
    private final NumberRange.FloatRange dealt;
    private final NumberRange.FloatRange taken;
    private final EntityPredicate sourceEntity;
    private final Boolean blocked;
    private final DamageSourcePredicate type;

    public DamagePredicate() {
        this.dealt = NumberRange.FloatRange.ANY;
        this.taken = NumberRange.FloatRange.ANY;
        this.sourceEntity = EntityPredicate.ANY;
        this.blocked = null;
        this.type = DamageSourcePredicate.EMPTY;
    }

    public DamagePredicate(NumberRange.FloatRange arg, NumberRange.FloatRange arg2, EntityPredicate arg3, @Nullable Boolean arg4, DamageSourcePredicate arg5) {
        this.dealt = arg;
        this.taken = arg2;
        this.sourceEntity = arg3;
        this.blocked = arg4;
        this.type = arg5;
    }

    public boolean test(ServerPlayerEntity arg, DamageSource arg2, float f, float g, boolean bl) {
        if (this == ANY) {
            return true;
        }
        if (!this.dealt.test(f)) {
            return false;
        }
        if (!this.taken.test(g)) {
            return false;
        }
        if (!this.sourceEntity.test(arg, arg2.getAttacker())) {
            return false;
        }
        if (this.blocked != null && this.blocked != bl) {
            return false;
        }
        return this.type.test(arg, arg2);
    }

    public static DamagePredicate fromJson(@Nullable JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return ANY;
        }
        JsonObject jsonObject = JsonHelper.asObject(jsonElement, "damage");
        NumberRange.FloatRange lv = NumberRange.FloatRange.fromJson(jsonObject.get("dealt"));
        NumberRange.FloatRange lv2 = NumberRange.FloatRange.fromJson(jsonObject.get("taken"));
        Boolean lv3 = jsonObject.has("blocked") ? Boolean.valueOf(JsonHelper.getBoolean(jsonObject, "blocked")) : null;
        EntityPredicate lv4 = EntityPredicate.fromJson(jsonObject.get("source_entity"));
        DamageSourcePredicate lv5 = DamageSourcePredicate.fromJson(jsonObject.get("type"));
        return new DamagePredicate(lv, lv2, lv4, lv3, lv5);
    }

    public JsonElement toJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("dealt", this.dealt.toJson());
        jsonObject.add("taken", this.taken.toJson());
        jsonObject.add("source_entity", this.sourceEntity.toJson());
        jsonObject.add("type", this.type.toJson());
        if (this.blocked != null) {
            jsonObject.addProperty("blocked", this.blocked);
        }
        return jsonObject;
    }

    public static class Builder {
        private NumberRange.FloatRange dealt = NumberRange.FloatRange.ANY;
        private NumberRange.FloatRange taken = NumberRange.FloatRange.ANY;
        private EntityPredicate sourceEntity = EntityPredicate.ANY;
        private Boolean blocked;
        private DamageSourcePredicate type = DamageSourcePredicate.EMPTY;

        public static Builder create() {
            return new Builder();
        }

        public Builder blocked(Boolean arg) {
            this.blocked = arg;
            return this;
        }

        public Builder type(DamageSourcePredicate.Builder arg) {
            this.type = arg.build();
            return this;
        }

        public DamagePredicate build() {
            return new DamagePredicate(this.dealt, this.taken, this.sourceEntity, this.blocked, this.type);
        }
    }
}

