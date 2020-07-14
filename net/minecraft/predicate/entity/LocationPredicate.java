/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.predicate.entity;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
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
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.StructureFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LocationPredicate {
    private static final Logger field_24732 = LogManager.getLogger();
    public static final LocationPredicate ANY = new LocationPredicate(NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, null, null, null, null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
    private final NumberRange.FloatRange x;
    private final NumberRange.FloatRange y;
    private final NumberRange.FloatRange z;
    @Nullable
    private final Biome biome;
    @Nullable
    private final StructureFeature<?> feature;
    @Nullable
    private final RegistryKey<World> dimension;
    @Nullable
    private final Boolean smokey;
    private final LightPredicate light;
    private final BlockPredicate block;
    private final FluidPredicate fluid;

    public LocationPredicate(NumberRange.FloatRange x, NumberRange.FloatRange y, NumberRange.FloatRange z, @Nullable Biome biome, @Nullable StructureFeature<?> feature, @Nullable RegistryKey<World> dimension, @Nullable Boolean smokey, LightPredicate light, BlockPredicate block, FluidPredicate fluid) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.biome = biome;
        this.feature = feature;
        this.dimension = dimension;
        this.smokey = smokey;
        this.light = light;
        this.block = block;
        this.fluid = fluid;
    }

    public static LocationPredicate biome(Biome biome) {
        return new LocationPredicate(NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, biome, null, null, null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
    }

    public static LocationPredicate dimension(RegistryKey<World> dimension) {
        return new LocationPredicate(NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, null, null, dimension, null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
    }

    public static LocationPredicate feature(StructureFeature<?> feature) {
        return new LocationPredicate(NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, null, feature, null, null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
    }

    public boolean test(ServerWorld world, double x, double y, double z) {
        return this.test(world, (float)x, (float)y, (float)z);
    }

    public boolean test(ServerWorld world, float x, float y, float z) {
        if (!this.x.test(x)) {
            return false;
        }
        if (!this.y.test(y)) {
            return false;
        }
        if (!this.z.test(z)) {
            return false;
        }
        if (this.dimension != null && this.dimension != world.getRegistryKey()) {
            return false;
        }
        BlockPos lv = new BlockPos(x, y, z);
        boolean bl = world.canSetBlock(lv);
        if (!(this.biome == null || bl && this.biome == world.getBiome(lv))) {
            return false;
        }
        if (!(this.feature == null || bl && world.getStructureAccessor().method_28388(lv, true, this.feature).hasChildren())) {
            return false;
        }
        if (!(this.smokey == null || bl && this.smokey == CampfireBlock.isLitCampfireInRange(world, lv))) {
            return false;
        }
        if (!this.light.test(world, lv)) {
            return false;
        }
        if (!this.block.test(world, lv)) {
            return false;
        }
        return this.fluid.test(world, lv);
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
            World.CODEC.encodeStart((DynamicOps)JsonOps.INSTANCE, this.dimension).resultOrPartial(((Logger)field_24732)::error).ifPresent(jsonElement -> jsonObject.add("dimension", jsonElement));
        }
        if (this.feature != null) {
            jsonObject.addProperty("feature", this.feature.getName());
        }
        if (this.biome != null) {
            jsonObject.addProperty("biome", BuiltinRegistries.BIOME.getId(this.biome).toString());
        }
        if (this.smokey != null) {
            jsonObject.addProperty("smokey", this.smokey);
        }
        jsonObject.add("light", this.light.toJson());
        jsonObject.add("block", this.block.toJson());
        jsonObject.add("fluid", this.fluid.toJson());
        return jsonObject;
    }

    public static LocationPredicate fromJson(@Nullable JsonElement json) {
        if (json == null || json.isJsonNull()) {
            return ANY;
        }
        JsonObject jsonObject = JsonHelper.asObject(json, "location");
        JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "position", new JsonObject());
        NumberRange.FloatRange lv = NumberRange.FloatRange.fromJson(jsonObject2.get("x"));
        NumberRange.FloatRange lv2 = NumberRange.FloatRange.fromJson(jsonObject2.get("y"));
        NumberRange.FloatRange lv3 = NumberRange.FloatRange.fromJson(jsonObject2.get("z"));
        RegistryKey lv4 = jsonObject.has("dimension") ? (RegistryKey)Identifier.CODEC.parse((DynamicOps)JsonOps.INSTANCE, (Object)jsonObject.get("dimension")).resultOrPartial(((Logger)field_24732)::error).map(arg -> RegistryKey.of(Registry.DIMENSION, arg)).orElse(null) : null;
        StructureFeature lv5 = jsonObject.has("feature") ? (StructureFeature)StructureFeature.STRUCTURES.get((Object)JsonHelper.getString(jsonObject, "feature")) : null;
        Biome lv6 = null;
        if (jsonObject.has("biome")) {
            Identifier lv7 = new Identifier(JsonHelper.getString(jsonObject, "biome"));
            lv6 = BuiltinRegistries.BIOME.getOrEmpty(lv7).orElseThrow(() -> new JsonSyntaxException("Unknown biome '" + lv7 + "'"));
        }
        Boolean boolean_ = jsonObject.has("smokey") ? Boolean.valueOf(jsonObject.get("smokey").getAsBoolean()) : null;
        LightPredicate lv8 = LightPredicate.fromJson(jsonObject.get("light"));
        BlockPredicate lv9 = BlockPredicate.fromJson(jsonObject.get("block"));
        FluidPredicate lv10 = FluidPredicate.fromJson(jsonObject.get("fluid"));
        return new LocationPredicate(lv, lv2, lv3, lv6, lv5, lv4, boolean_, lv8, lv9, lv10);
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
        private RegistryKey<World> dimension;
        @Nullable
        private Boolean smokey;
        private LightPredicate light = LightPredicate.ANY;
        private BlockPredicate block = BlockPredicate.ANY;
        private FluidPredicate fluid = FluidPredicate.ANY;

        public static Builder create() {
            return new Builder();
        }

        public Builder biome(@Nullable Biome biome) {
            this.biome = biome;
            return this;
        }

        public Builder block(BlockPredicate block) {
            this.block = block;
            return this;
        }

        public Builder smokey(Boolean smokey) {
            this.smokey = smokey;
            return this;
        }

        public LocationPredicate build() {
            return new LocationPredicate(this.x, this.y, this.z, this.biome, this.feature, this.dimension, this.smokey, this.light, this.block, this.fluid);
        }
    }
}

