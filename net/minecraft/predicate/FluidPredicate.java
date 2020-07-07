/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  javax.annotation.Nullable
 */
package net.minecraft.predicate;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class FluidPredicate {
    public static final FluidPredicate ANY = new FluidPredicate(null, null, StatePredicate.ANY);
    @Nullable
    private final Tag<Fluid> tag;
    @Nullable
    private final Fluid fluid;
    private final StatePredicate state;

    public FluidPredicate(@Nullable Tag<Fluid> arg, @Nullable Fluid arg2, StatePredicate arg3) {
        this.tag = arg;
        this.fluid = arg2;
        this.state = arg3;
    }

    public boolean test(ServerWorld arg, BlockPos arg2) {
        if (this == ANY) {
            return true;
        }
        if (!arg.canSetBlock(arg2)) {
            return false;
        }
        FluidState lv = arg.getFluidState(arg2);
        Fluid lv2 = lv.getFluid();
        if (this.tag != null && !this.tag.contains(lv2)) {
            return false;
        }
        if (this.fluid != null && lv2 != this.fluid) {
            return false;
        }
        return this.state.test(lv);
    }

    public static FluidPredicate fromJson(@Nullable JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return ANY;
        }
        JsonObject jsonObject = JsonHelper.asObject(jsonElement, "fluid");
        Fluid lv = null;
        if (jsonObject.has("fluid")) {
            Identifier lv2 = new Identifier(JsonHelper.getString(jsonObject, "fluid"));
            lv = Registry.FLUID.get(lv2);
        }
        Tag<Fluid> lv3 = null;
        if (jsonObject.has("tag")) {
            Identifier lv4 = new Identifier(JsonHelper.getString(jsonObject, "tag"));
            lv3 = ServerTagManagerHolder.getTagManager().getFluids().getTag(lv4);
            if (lv3 == null) {
                throw new JsonSyntaxException("Unknown fluid tag '" + lv4 + "'");
            }
        }
        StatePredicate lv5 = StatePredicate.fromJson(jsonObject.get("state"));
        return new FluidPredicate(lv3, lv, lv5);
    }

    public JsonElement toJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        if (this.fluid != null) {
            jsonObject.addProperty("fluid", Registry.FLUID.getId(this.fluid).toString());
        }
        if (this.tag != null) {
            jsonObject.addProperty("tag", ServerTagManagerHolder.getTagManager().getFluids().getTagId(this.tag).toString());
        }
        jsonObject.add("state", this.state.toJson());
        return jsonObject;
    }
}

