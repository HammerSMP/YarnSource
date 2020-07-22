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
import net.minecraft.predicate.NumberRange;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;

public class LightPredicate {
    public static final LightPredicate ANY = new LightPredicate(NumberRange.IntRange.ANY);
    private final NumberRange.IntRange range;

    private LightPredicate(NumberRange.IntRange range) {
        this.range = range;
    }

    public boolean test(ServerWorld world, BlockPos pos) {
        if (this == ANY) {
            return true;
        }
        if (!world.canSetBlock(pos)) {
            return false;
        }
        return this.range.test(world.getLightLevel(pos));
    }

    public JsonElement toJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("light", this.range.toJson());
        return jsonObject;
    }

    public static LightPredicate fromJson(@Nullable JsonElement json) {
        if (json == null || json.isJsonNull()) {
            return ANY;
        }
        JsonObject jsonObject = JsonHelper.asObject(json, "light");
        NumberRange.IntRange lv = NumberRange.IntRange.fromJson(jsonObject.get("light"));
        return new LightPredicate(lv);
    }
}

