/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 */
package net.minecraft.loot.condition;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.class_5335;
import net.minecraft.class_5341;
import net.minecraft.class_5342;
import net.minecraft.entity.Entity;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.condition.LootConditions;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.util.JsonHelper;

public class EntityScoresLootCondition
implements class_5341 {
    private final Map<String, UniformLootTableRange> scores;
    private final LootContext.EntityTarget target;

    private EntityScoresLootCondition(Map<String, UniformLootTableRange> map, LootContext.EntityTarget arg) {
        this.scores = ImmutableMap.copyOf(map);
        this.target = arg;
    }

    @Override
    public class_5342 method_29325() {
        return LootConditions.ENTITY_SCORES;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(this.target.getParameter());
    }

    @Override
    public boolean test(LootContext arg) {
        Entity lv = arg.get(this.target.getParameter());
        if (lv == null) {
            return false;
        }
        Scoreboard lv2 = lv.world.getScoreboard();
        for (Map.Entry<String, UniformLootTableRange> entry : this.scores.entrySet()) {
            if (this.entityScoreIsInRange(lv, lv2, entry.getKey(), entry.getValue())) continue;
            return false;
        }
        return true;
    }

    protected boolean entityScoreIsInRange(Entity arg, Scoreboard arg2, String string, UniformLootTableRange arg3) {
        ScoreboardObjective lv = arg2.getNullableObjective(string);
        if (lv == null) {
            return false;
        }
        String string2 = arg.getEntityName();
        if (!arg2.playerHasObjective(string2, lv)) {
            return false;
        }
        return arg3.contains(arg2.getPlayerScore(string2, lv).getScore());
    }

    @Override
    public /* synthetic */ boolean test(Object object) {
        return this.test((LootContext)object);
    }

    public static class Factory
    implements class_5335<EntityScoresLootCondition> {
        @Override
        public void toJson(JsonObject jsonObject, EntityScoresLootCondition arg, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject2 = new JsonObject();
            for (Map.Entry entry : arg.scores.entrySet()) {
                jsonObject2.add((String)entry.getKey(), jsonSerializationContext.serialize(entry.getValue()));
            }
            jsonObject.add("scores", (JsonElement)jsonObject2);
            jsonObject.add("entity", jsonSerializationContext.serialize((Object)arg.target));
        }

        @Override
        public EntityScoresLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            Set set = JsonHelper.getObject(jsonObject, "scores").entrySet();
            LinkedHashMap map = Maps.newLinkedHashMap();
            for (Map.Entry entry : set) {
                map.put(entry.getKey(), JsonHelper.deserialize((JsonElement)entry.getValue(), "score", jsonDeserializationContext, UniformLootTableRange.class));
            }
            return new EntityScoresLootCondition(map, JsonHelper.deserialize(jsonObject, "entity", jsonDeserializationContext, LootContext.EntityTarget.class));
        }

        @Override
        public /* synthetic */ Object fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return this.fromJson(jsonObject, jsonDeserializationContext);
        }
    }
}

