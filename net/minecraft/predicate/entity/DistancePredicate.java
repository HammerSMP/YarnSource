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
import net.minecraft.predicate.NumberRange;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;

public class DistancePredicate {
    public static final DistancePredicate ANY = new DistancePredicate(NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY);
    private final NumberRange.FloatRange x;
    private final NumberRange.FloatRange y;
    private final NumberRange.FloatRange z;
    private final NumberRange.FloatRange horizontal;
    private final NumberRange.FloatRange absolute;

    public DistancePredicate(NumberRange.FloatRange x, NumberRange.FloatRange y, NumberRange.FloatRange z, NumberRange.FloatRange horizontal, NumberRange.FloatRange absolute) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.horizontal = horizontal;
        this.absolute = absolute;
    }

    public static DistancePredicate horizontal(NumberRange.FloatRange horizontal) {
        return new DistancePredicate(NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, horizontal, NumberRange.FloatRange.ANY);
    }

    public static DistancePredicate y(NumberRange.FloatRange y) {
        return new DistancePredicate(NumberRange.FloatRange.ANY, y, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY);
    }

    public boolean test(double x0, double y0, double z0, double x1, double y1, double z1) {
        float j = (float)(x0 - x1);
        float k = (float)(y0 - y1);
        float l = (float)(z0 - z1);
        if (!(this.x.test(MathHelper.abs(j)) && this.y.test(MathHelper.abs(k)) && this.z.test(MathHelper.abs(l)))) {
            return false;
        }
        if (!this.horizontal.testSqrt(j * j + l * l)) {
            return false;
        }
        return this.absolute.testSqrt(j * j + k * k + l * l);
    }

    public static DistancePredicate fromJson(@Nullable JsonElement json) {
        if (json == null || json.isJsonNull()) {
            return ANY;
        }
        JsonObject jsonObject = JsonHelper.asObject(json, "distance");
        NumberRange.FloatRange lv = NumberRange.FloatRange.fromJson(jsonObject.get("x"));
        NumberRange.FloatRange lv2 = NumberRange.FloatRange.fromJson(jsonObject.get("y"));
        NumberRange.FloatRange lv3 = NumberRange.FloatRange.fromJson(jsonObject.get("z"));
        NumberRange.FloatRange lv4 = NumberRange.FloatRange.fromJson(jsonObject.get("horizontal"));
        NumberRange.FloatRange lv5 = NumberRange.FloatRange.fromJson(jsonObject.get("absolute"));
        return new DistancePredicate(lv, lv2, lv3, lv4, lv5);
    }

    public JsonElement toJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("x", this.x.toJson());
        jsonObject.add("y", this.y.toJson());
        jsonObject.add("z", this.z.toJson());
        jsonObject.add("horizontal", this.horizontal.toJson());
        jsonObject.add("absolute", this.absolute.toJson());
        return jsonObject;
    }
}

