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

    private LightPredicate(NumberRange.IntRange arg) {
        this.range = arg;
    }

    public boolean test(ServerWorld arg, BlockPos arg2) {
        if (this == ANY) {
            return true;
        }
        if (!arg.canSetBlock(arg2)) {
            return false;
        }
        return this.range.test(arg.getLightLevel(arg2));
    }

    public JsonElement toJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("light", this.range.toJson());
        return jsonObject;
    }

    public static LightPredicate fromJson(@Nullable JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return ANY;
        }
        JsonObject jsonObject = JsonHelper.asObject(jsonElement, "light");
        NumberRange.IntRange lv = NumberRange.IntRange.fromJson(jsonObject.get("light"));
        return new LightPredicate(lv);
    }
}

