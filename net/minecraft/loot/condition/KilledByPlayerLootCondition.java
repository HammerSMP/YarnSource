/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 */
package net.minecraft.loot.condition;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.JsonSerializer;

public class KilledByPlayerLootCondition
implements LootCondition {
    private static final KilledByPlayerLootCondition INSTANCE = new KilledByPlayerLootCondition();

    private KilledByPlayerLootCondition() {
    }

    @Override
    public LootConditionType getType() {
        return LootConditionTypes.KILLED_BY_PLAYER;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootContextParameters.LAST_DAMAGE_PLAYER);
    }

    @Override
    public boolean test(LootContext arg) {
        return arg.hasParameter(LootContextParameters.LAST_DAMAGE_PLAYER);
    }

    public static LootCondition.Builder builder() {
        return () -> INSTANCE;
    }

    @Override
    public /* synthetic */ boolean test(Object object) {
        return this.test((LootContext)object);
    }

    public static class Serializer
    implements JsonSerializer<KilledByPlayerLootCondition> {
        @Override
        public void toJson(JsonObject jsonObject, KilledByPlayerLootCondition arg, JsonSerializationContext jsonSerializationContext) {
        }

        @Override
        public KilledByPlayerLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return INSTANCE;
        }

        @Override
        public /* synthetic */ Object fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return this.fromJson(jsonObject, jsonDeserializationContext);
        }
    }
}

