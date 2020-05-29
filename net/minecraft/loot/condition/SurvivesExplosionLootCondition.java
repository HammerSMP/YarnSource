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
import java.util.Random;
import java.util.Set;
import net.minecraft.class_5335;
import net.minecraft.class_5341;
import net.minecraft.class_5342;
import net.minecraft.loot.condition.LootConditions;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;

public class SurvivesExplosionLootCondition
implements class_5341 {
    private static final SurvivesExplosionLootCondition INSTANCE = new SurvivesExplosionLootCondition();

    private SurvivesExplosionLootCondition() {
    }

    @Override
    public class_5342 method_29325() {
        return LootConditions.SURVIVES_EXPLOSION;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootContextParameters.EXPLOSION_RADIUS);
    }

    @Override
    public boolean test(LootContext arg) {
        Float lv = arg.get(LootContextParameters.EXPLOSION_RADIUS);
        if (lv != null) {
            Random random = arg.getRandom();
            float f = 1.0f / lv.floatValue();
            return random.nextFloat() <= f;
        }
        return true;
    }

    public static class_5341.Builder builder() {
        return () -> INSTANCE;
    }

    @Override
    public /* synthetic */ boolean test(Object object) {
        return this.test((LootContext)object);
    }

    public static class Factory
    implements class_5335<SurvivesExplosionLootCondition> {
        @Override
        public void toJson(JsonObject jsonObject, SurvivesExplosionLootCondition arg, JsonSerializationContext jsonSerializationContext) {
        }

        @Override
        public SurvivesExplosionLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return INSTANCE;
        }

        @Override
        public /* synthetic */ Object fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return this.fromJson(jsonObject, jsonDeserializationContext);
        }
    }
}

