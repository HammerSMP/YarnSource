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
import net.minecraft.class_5335;
import net.minecraft.class_5341;
import net.minecraft.class_5342;
import net.minecraft.entity.Entity;
import net.minecraft.loot.condition.LootConditions;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Vec3d;

public class EntityPropertiesLootCondition
implements class_5341 {
    private final EntityPredicate predicate;
    private final LootContext.EntityTarget entity;

    private EntityPropertiesLootCondition(EntityPredicate arg, LootContext.EntityTarget arg2) {
        this.predicate = arg;
        this.entity = arg2;
    }

    @Override
    public class_5342 method_29325() {
        return LootConditions.ENTITY_PROPERTIES;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootContextParameters.POSITION, this.entity.getParameter());
    }

    @Override
    public boolean test(LootContext arg) {
        Entity lv = arg.get(this.entity.getParameter());
        Vec3d lv2 = arg.get(LootContextParameters.ORIGIN);
        return this.predicate.test(arg.getWorld(), lv2, lv);
    }

    public static class_5341.Builder create(LootContext.EntityTarget arg) {
        return EntityPropertiesLootCondition.builder(arg, EntityPredicate.Builder.create());
    }

    public static class_5341.Builder builder(LootContext.EntityTarget arg, EntityPredicate.Builder arg2) {
        return () -> new EntityPropertiesLootCondition(arg2.build(), arg);
    }

    public static class_5341.Builder builder(LootContext.EntityTarget arg, EntityPredicate arg2) {
        return () -> new EntityPropertiesLootCondition(arg2, arg);
    }

    @Override
    public /* synthetic */ boolean test(Object object) {
        return this.test((LootContext)object);
    }

    public static class Factory
    implements class_5335<EntityPropertiesLootCondition> {
        @Override
        public void toJson(JsonObject jsonObject, EntityPropertiesLootCondition arg, JsonSerializationContext jsonSerializationContext) {
            jsonObject.add("predicate", arg.predicate.toJson());
            jsonObject.add("entity", jsonSerializationContext.serialize((Object)arg.entity));
        }

        @Override
        public EntityPropertiesLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            EntityPredicate lv = EntityPredicate.fromJson(jsonObject.get("predicate"));
            return new EntityPropertiesLootCondition(lv, JsonHelper.deserialize(jsonObject, "entity", jsonDeserializationContext, LootContext.EntityTarget.class));
        }

        @Override
        public /* synthetic */ Object fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return this.fromJson(jsonObject, jsonDeserializationContext);
        }
    }
}

