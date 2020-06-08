/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 */
package net.minecraft.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;
import net.minecraft.util.math.BlockPos;

public class LocationCheckLootCondition
implements LootCondition {
    private final LocationPredicate predicate;
    private final BlockPos offset;

    private LocationCheckLootCondition(LocationPredicate arg, BlockPos arg2) {
        this.predicate = arg;
        this.offset = arg2;
    }

    @Override
    public LootConditionType getType() {
        return LootConditionTypes.LOCATION_CHECK;
    }

    @Override
    public boolean test(LootContext arg) {
        BlockPos lv = arg.get(LootContextParameters.POSITION);
        return lv != null && this.predicate.test(arg.getWorld(), lv.getX() + this.offset.getX(), lv.getY() + this.offset.getY(), lv.getZ() + this.offset.getZ());
    }

    public static LootCondition.Builder builder(LocationPredicate.Builder arg) {
        return () -> new LocationCheckLootCondition(arg.build(), BlockPos.ORIGIN);
    }

    @Override
    public /* synthetic */ boolean test(Object object) {
        return this.test((LootContext)object);
    }

    public static class Serializer
    implements JsonSerializer<LocationCheckLootCondition> {
        @Override
        public void toJson(JsonObject jsonObject, LocationCheckLootCondition arg, JsonSerializationContext jsonSerializationContext) {
            jsonObject.add("predicate", arg.predicate.toJson());
            if (arg.offset.getX() != 0) {
                jsonObject.addProperty("offsetX", (Number)arg.offset.getX());
            }
            if (arg.offset.getY() != 0) {
                jsonObject.addProperty("offsetY", (Number)arg.offset.getY());
            }
            if (arg.offset.getZ() != 0) {
                jsonObject.addProperty("offsetZ", (Number)arg.offset.getZ());
            }
        }

        @Override
        public LocationCheckLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            LocationPredicate lv = LocationPredicate.fromJson(jsonObject.get("predicate"));
            int i = JsonHelper.getInt(jsonObject, "offsetX", 0);
            int j = JsonHelper.getInt(jsonObject, "offsetY", 0);
            int k = JsonHelper.getInt(jsonObject, "offsetZ", 0);
            return new LocationCheckLootCondition(lv, new BlockPos(i, j, k));
        }

        @Override
        public /* synthetic */ Object fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return this.fromJson(jsonObject, jsonDeserializationContext);
        }
    }
}

