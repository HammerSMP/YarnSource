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
import net.minecraft.entity.Entity;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;

public class EntityScoresLootCondition
implements LootCondition {
    private final Map<String, UniformLootTableRange> scores;
    private final LootContext.EntityTarget target;

    private EntityScoresLootCondition(Map<String, UniformLootTableRange> scores, LootContext.EntityTarget target) {
        this.scores = ImmutableMap.copyOf(scores);
        this.target = target;
    }

    @Override
    public LootConditionType getType() {
        return LootConditionTypes.ENTITY_SCORES;
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

    protected boolean entityScoreIsInRange(Entity entity, Scoreboard scoreboard, String objective, UniformLootTableRange scoreRange) {
        ScoreboardObjective lv = scoreboard.getNullableObjective(objective);
        if (lv == null) {
            return false;
        }
        String string2 = entity.getEntityName();
        if (!scoreboard.playerHasObjective(string2, lv)) {
            return false;
        }
        return scoreRange.contains(scoreboard.getPlayerScore(string2, lv).getScore());
    }

    @Override
    public /* synthetic */ boolean test(Object context) {
        return this.test((LootContext)context);
    }

    public static class Serializer
    implements JsonSerializer<EntityScoresLootCondition> {
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
        public /* synthetic */ Object fromJson(JsonObject json, JsonDeserializationContext context) {
            return this.fromJson(json, context);
        }
    }
}

