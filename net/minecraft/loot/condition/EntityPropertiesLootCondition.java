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
import net.minecraft.entity.Entity;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;
import net.minecraft.util.math.Vec3d;

public class EntityPropertiesLootCondition
implements LootCondition {
    private final EntityPredicate predicate;
    private final LootContext.EntityTarget entity;

    private EntityPropertiesLootCondition(EntityPredicate predicate, LootContext.EntityTarget entity) {
        this.predicate = predicate;
        this.entity = entity;
    }

    @Override
    public LootConditionType getType() {
        return LootConditionTypes.ENTITY_PROPERTIES;
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

    public static LootCondition.Builder create(LootContext.EntityTarget entity) {
        return EntityPropertiesLootCondition.builder(entity, EntityPredicate.Builder.create());
    }

    public static LootCondition.Builder builder(LootContext.EntityTarget entity, EntityPredicate.Builder predicateBuilder) {
        return () -> new EntityPropertiesLootCondition(predicateBuilder.build(), entity);
    }

    public static LootCondition.Builder builder(LootContext.EntityTarget entity, EntityPredicate predicate) {
        return () -> new EntityPropertiesLootCondition(predicate, entity);
    }

    @Override
    public /* synthetic */ boolean test(Object context) {
        return this.test((LootContext)context);
    }

    public static class Serializer
    implements JsonSerializer<EntityPropertiesLootCondition> {
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
        public /* synthetic */ Object fromJson(JsonObject json, JsonDeserializationContext context) {
            return this.fromJson(json, context);
        }
    }
}

