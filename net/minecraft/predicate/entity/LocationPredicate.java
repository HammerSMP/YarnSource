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
package net.minecraft.predicate.entity;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.block.CampfireBlock;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.FluidPredicate;
import net.minecraft.predicate.LightPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;

public class LocationPredicate {
    public static final LocationPredicate ANY = new LocationPredicate(NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, null, null, null, null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
    private final NumberRange.FloatRange x;
    private final NumberRange.FloatRange y;
    private final NumberRange.FloatRange z;
    @Nullable
    private final Biome biome;
    @Nullable
    private final StructureFeature<?> feature;
    @Nullable
    private final DimensionType dimension;
    @Nullable
    private final Boolean smokey;
    private final LightPredicate light;
    private final BlockPredicate block;
    private final FluidPredicate fluid;

    public LocationPredicate(NumberRange.FloatRange arg, NumberRange.FloatRange arg2, NumberRange.FloatRange arg3, @Nullable Biome arg4, @Nullable StructureFeature<?> arg5, @Nullable DimensionType arg6, @Nullable Boolean arg7, LightPredicate arg8, BlockPredicate arg9, FluidPredicate arg10) {
        this.x = arg;
        this.y = arg2;
        this.z = arg3;
        this.biome = arg4;
        this.feature = arg5;
        this.dimension = arg6;
        this.smokey = arg7;
        this.light = arg8;
        this.block = arg9;
        this.fluid = arg10;
    }

    public static LocationPredicate biome(Biome arg) {
        return new LocationPredicate(NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, arg, null, null, null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
    }

    public static LocationPredicate dimension(DimensionType arg) {
        return new LocationPredicate(NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, null, null, arg, null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
    }

    public static LocationPredicate feature(StructureFeature<?> arg) {
        return new LocationPredicate(NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, null, arg, null, null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
    }

    public boolean test(ServerWorld arg, double d, double e, double f) {
        return this.test(arg, (float)d, (float)e, (float)f);
    }

    public boolean test(ServerWorld arg, float f, float g, float h) {
        if (!this.x.test(f)) {
            return false;
        }
        if (!this.y.test(g)) {
            return false;
        }
        if (!this.z.test(h)) {
            return false;
        }
        if (this.dimension != null && this.dimension != arg.method_27983()) {
            return false;
        }
        BlockPos lv = new BlockPos(f, g, h);
        boolean bl = arg.canSetBlock(lv);
        if (!(this.biome == null || bl && this.biome == arg.getBiome(lv))) {
            return false;
        }
        if (!(this.feature == null || bl && this.feature.isInsideStructure(arg.getStructureAccessor(), lv))) {
            return false;
        }
        if (!(this.smokey == null || bl && this.smokey == CampfireBlock.isLitCampfireInRange(arg, lv))) {
            return false;
        }
        if (!this.light.test(arg, lv)) {
            return false;
        }
        if (!this.block.test(arg, lv)) {
            return false;
        }
        return this.fluid.test(arg, lv);
    }

    public JsonElement toJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        if (!(this.x.isDummy() && this.y.isDummy() && this.z.isDummy())) {
            JsonObject jsonObject2 = new JsonObject();
            jsonObject2.add("x", this.x.toJson());
            jsonObject2.add("y", this.y.toJson());
            jsonObject2.add("z", this.z.toJson());
            jsonObject.add("position", (JsonElement)jsonObject2);
        }
        if (this.dimension != null) {
            jsonObject.addProperty("dimension", DimensionType.getId(this.dimension).toString());
        }
        if (this.feature != null) {
            jsonObject.addProperty("feature", (String)Feature.STRUCTURES.inverse().get(this.feature));
        }
        if (this.biome != null) {
            jsonObject.addProperty("biome", Registry.BIOME.getId(this.biome).toString());
        }
        if (this.smokey != null) {
            jsonObject.addProperty("smokey", this.smokey);
        }
        jsonObject.add("light", this.light.toJson());
        jsonObject.add("block", this.block.toJson());
        jsonObject.add("fluid", this.fluid.toJson());
        return jsonObject;
    }

    public static LocationPredicate fromJson(@Nullable JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return ANY;
        }
        JsonObject jsonObject = JsonHelper.asObject(jsonElement, "location");
        JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "position", new JsonObject());
        NumberRange.FloatRange lv = NumberRange.FloatRange.fromJson(jsonObject2.get("x"));
        NumberRange.FloatRange lv2 = NumberRange.FloatRange.fromJson(jsonObject2.get("y"));
        NumberRange.FloatRange lv3 = NumberRange.FloatRange.fromJson(jsonObject2.get("z"));
        DimensionType lv4 = jsonObject.has("dimension") ? DimensionType.byId(new Identifier(JsonHelper.getString(jsonObject, "dimension"))) : null;
        StructureFeature lv5 = jsonObject.has("feature") ? (StructureFeature)Feature.STRUCTURES.get((Object)JsonHelper.getString(jsonObject, "feature")) : null;
        Biome lv6 = null;
        if (jsonObject.has("biome")) {
            Identifier lv7 = new Identifier(JsonHelper.getString(jsonObject, "biome"));
            lv6 = Registry.BIOME.getOrEmpty(lv7).orElseThrow(() -> new JsonSyntaxException("Unknown biome '" + lv7 + "'"));
        }
        Boolean lv8 = jsonObject.has("smokey") ? Boolean.valueOf(jsonObject.get("smokey").getAsBoolean()) : null;
        LightPredicate lv9 = LightPredicate.fromJson(jsonObject.get("light"));
        BlockPredicate lv10 = BlockPredicate.fromJson(jsonObject.get("block"));
        FluidPredicate lv11 = FluidPredicate.fromJson(jsonObject.get("fluid"));
        return new LocationPredicate(lv, lv2, lv3, lv6, lv5, lv4, lv8, lv9, lv10, lv11);
    }

    public static class Builder {
        private NumberRange.FloatRange x = NumberRange.FloatRange.ANY;
        private NumberRange.FloatRange y = NumberRange.FloatRange.ANY;
        private NumberRange.FloatRange z = NumberRange.FloatRange.ANY;
        @Nullable
        private Biome biome;
        @Nullable
        private StructureFeature<?> feature;
        @Nullable
        private DimensionType dimension;
        @Nullable
        private Boolean smokey;
        private LightPredicate light = LightPredicate.ANY;
        private BlockPredicate block = BlockPredicate.ANY;
        private FluidPredicate fluid = FluidPredicate.ANY;

        public static Builder create() {
            return new Builder();
        }

        public Builder biome(@Nullable Biome arg) {
            this.biome = arg;
            return this;
        }

        public Builder block(BlockPredicate arg) {
            this.block = arg;
            return this;
        }

        public Builder smokey(Boolean arg) {
            this.smokey = arg;
            return this;
        }

        public LocationPredicate build() {
            return new LocationPredicate(this.x, this.y, this.z, this.biome, this.feature, this.dimension, this.smokey, this.light, this.block, this.fluid);
        }
    }
}

