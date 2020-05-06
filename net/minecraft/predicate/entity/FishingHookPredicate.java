/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonPrimitive
 *  javax.annotation.Nullable
 */
package net.minecraft.predicate.entity;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.util.JsonHelper;

public class FishingHookPredicate {
    public static final FishingHookPredicate ANY = new FishingHookPredicate(false);
    private boolean inOpenWater;

    private FishingHookPredicate(boolean bl) {
        this.inOpenWater = bl;
    }

    public static FishingHookPredicate of(boolean bl) {
        return new FishingHookPredicate(bl);
    }

    public static FishingHookPredicate fromJson(@Nullable JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return ANY;
        }
        JsonObject jsonObject = JsonHelper.asObject(jsonElement, "fishing_hook");
        JsonElement jsonElement2 = jsonObject.get("in_open_water");
        if (jsonElement2 != null) {
            return new FishingHookPredicate(JsonHelper.asBoolean(jsonElement2, "in_open_water"));
        }
        return ANY;
    }

    public JsonElement toJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("in_open_water", (JsonElement)new JsonPrimitive(Boolean.valueOf(this.inOpenWater)));
        return jsonObject;
    }

    public boolean test(Entity arg) {
        if (this == ANY) {
            return true;
        }
        if (!(arg instanceof FishingBobberEntity)) {
            return false;
        }
        FishingBobberEntity lv = (FishingBobberEntity)arg;
        return this.inOpenWater == lv.isInOpenWater();
    }
}

