/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonPrimitive
 *  it.unimi.dsi.fastutil.objects.Object2BooleanMap
 *  it.unimi.dsi.fastutil.objects.Object2BooleanMap$Entry
 *  it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap
 *  javax.annotation.Nullable
 */
package net.minecraft.predicate;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.CriterionProgress;
import net.minecraft.entity.Entity;
import net.minecraft.predicate.NumberRange;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerRecipeBook;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameMode;

public class PlayerPredicate {
    public static final PlayerPredicate ANY = new Builder().build();
    private final NumberRange.IntRange experienceLevel;
    private final GameMode gamemode;
    private final Map<Stat<?>, NumberRange.IntRange> stats;
    private final Object2BooleanMap<Identifier> recipes;
    private final Map<Identifier, AdvancementPredicate> advancements;

    private static AdvancementPredicate criterionFromJson(JsonElement json) {
        if (json.isJsonPrimitive()) {
            boolean bl = json.getAsBoolean();
            return new CompletedAdvancementPredicate(bl);
        }
        Object2BooleanOpenHashMap object2BooleanMap = new Object2BooleanOpenHashMap();
        JsonObject jsonObject = JsonHelper.asObject(json, "criterion data");
        jsonObject.entrySet().forEach(arg_0 -> PlayerPredicate.method_22502((Object2BooleanMap)object2BooleanMap, arg_0));
        return new AdvancementCriteriaPredicate((Object2BooleanMap<String>)object2BooleanMap);
    }

    private PlayerPredicate(NumberRange.IntRange experienceLevel, GameMode gamemode, Map<Stat<?>, NumberRange.IntRange> stats, Object2BooleanMap<Identifier> recipes, Map<Identifier, AdvancementPredicate> advancements) {
        this.experienceLevel = experienceLevel;
        this.gamemode = gamemode;
        this.stats = stats;
        this.recipes = recipes;
        this.advancements = advancements;
    }

    public boolean test(Entity entity) {
        if (this == ANY) {
            return true;
        }
        if (!(entity instanceof ServerPlayerEntity)) {
            return false;
        }
        ServerPlayerEntity lv = (ServerPlayerEntity)entity;
        if (!this.experienceLevel.test(lv.experienceLevel)) {
            return false;
        }
        if (this.gamemode != GameMode.NOT_SET && this.gamemode != lv.interactionManager.getGameMode()) {
            return false;
        }
        ServerStatHandler lv2 = lv.getStatHandler();
        for (Map.Entry<Stat<?>, NumberRange.IntRange> entry : this.stats.entrySet()) {
            int i = lv2.getStat(entry.getKey());
            if (entry.getValue().test(i)) continue;
            return false;
        }
        ServerRecipeBook lv3 = lv.getRecipeBook();
        for (Object2BooleanMap.Entry entry2 : this.recipes.object2BooleanEntrySet()) {
            if (lv3.contains((Identifier)entry2.getKey()) == entry2.getBooleanValue()) continue;
            return false;
        }
        if (!this.advancements.isEmpty()) {
            PlayerAdvancementTracker playerAdvancementTracker = lv.getAdvancementTracker();
            ServerAdvancementLoader lv5 = lv.getServer().getAdvancementLoader();
            for (Map.Entry<Identifier, AdvancementPredicate> entry3 : this.advancements.entrySet()) {
                Advancement lv6 = lv5.get(entry3.getKey());
                if (lv6 != null && entry3.getValue().test(playerAdvancementTracker.getProgress(lv6))) continue;
                return false;
            }
        }
        return true;
    }

    public static PlayerPredicate fromJson(@Nullable JsonElement json) {
        if (json == null || json.isJsonNull()) {
            return ANY;
        }
        JsonObject jsonObject = JsonHelper.asObject(json, "player");
        NumberRange.IntRange lv = NumberRange.IntRange.fromJson(jsonObject.get("level"));
        String string = JsonHelper.getString(jsonObject, "gamemode", "");
        GameMode lv2 = GameMode.byName(string, GameMode.NOT_SET);
        HashMap map = Maps.newHashMap();
        JsonArray jsonArray = JsonHelper.getArray(jsonObject, "stats", null);
        if (jsonArray != null) {
            for (JsonElement jsonElement2 : jsonArray) {
                JsonObject jsonObject2 = JsonHelper.asObject(jsonElement2, "stats entry");
                Identifier lv3 = new Identifier(JsonHelper.getString(jsonObject2, "type"));
                StatType<?> lv4 = Registry.STAT_TYPE.get(lv3);
                if (lv4 == null) {
                    throw new JsonParseException("Invalid stat type: " + lv3);
                }
                Identifier lv5 = new Identifier(JsonHelper.getString(jsonObject2, "stat"));
                Stat<?> lv6 = PlayerPredicate.getStat(lv4, lv5);
                NumberRange.IntRange lv7 = NumberRange.IntRange.fromJson(jsonObject2.get("value"));
                map.put(lv6, lv7);
            }
        }
        Object2BooleanOpenHashMap object2BooleanMap = new Object2BooleanOpenHashMap();
        JsonObject jsonObject3 = JsonHelper.getObject(jsonObject, "recipes", new JsonObject());
        for (Map.Entry entry : jsonObject3.entrySet()) {
            Identifier lv8 = new Identifier((String)entry.getKey());
            boolean bl = JsonHelper.asBoolean((JsonElement)entry.getValue(), "recipe present");
            object2BooleanMap.put((Object)lv8, bl);
        }
        HashMap map2 = Maps.newHashMap();
        JsonObject jsonObject4 = JsonHelper.getObject(jsonObject, "advancements", new JsonObject());
        for (Map.Entry entry2 : jsonObject4.entrySet()) {
            Identifier lv9 = new Identifier((String)entry2.getKey());
            AdvancementPredicate lv10 = PlayerPredicate.criterionFromJson((JsonElement)entry2.getValue());
            map2.put(lv9, lv10);
        }
        return new PlayerPredicate(lv, lv2, map, (Object2BooleanMap<Identifier>)object2BooleanMap, map2);
    }

    private static <T> Stat<T> getStat(StatType<T> type, Identifier id) {
        Registry<T> lv = type.getRegistry();
        T object = lv.get(id);
        if (object == null) {
            throw new JsonParseException("Unknown object " + id + " for stat type " + Registry.STAT_TYPE.getId(type));
        }
        return type.getOrCreateStat(object);
    }

    private static <T> Identifier getStatId(Stat<T> stat) {
        return stat.getType().getRegistry().getId(stat.getValue());
    }

    public JsonElement toJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("level", this.experienceLevel.toJson());
        if (this.gamemode != GameMode.NOT_SET) {
            jsonObject.addProperty("gamemode", this.gamemode.getName());
        }
        if (!this.stats.isEmpty()) {
            JsonArray jsonArray = new JsonArray();
            this.stats.forEach((stat, arg2) -> {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("type", Registry.STAT_TYPE.getId(stat.getType()).toString());
                jsonObject.addProperty("stat", PlayerPredicate.getStatId(stat).toString());
                jsonObject.add("value", arg2.toJson());
                jsonArray.add((JsonElement)jsonObject);
            });
            jsonObject.add("stats", (JsonElement)jsonArray);
        }
        if (!this.recipes.isEmpty()) {
            JsonObject jsonObject2 = new JsonObject();
            this.recipes.forEach((id, present) -> jsonObject2.addProperty(id.toString(), present));
            jsonObject.add("recipes", (JsonElement)jsonObject2);
        }
        if (!this.advancements.isEmpty()) {
            JsonObject jsonObject3 = new JsonObject();
            this.advancements.forEach((id, arg2) -> jsonObject3.add(id.toString(), arg2.toJson()));
            jsonObject.add("advancements", (JsonElement)jsonObject3);
        }
        return jsonObject;
    }

    private static /* synthetic */ void method_22502(Object2BooleanMap object2BooleanMap, Map.Entry entry) {
        boolean bl = JsonHelper.asBoolean((JsonElement)entry.getValue(), "criterion test");
        object2BooleanMap.put(entry.getKey(), bl);
    }

    public static class Builder {
        private NumberRange.IntRange experienceLevel = NumberRange.IntRange.ANY;
        private GameMode gamemode = GameMode.NOT_SET;
        private final Map<Stat<?>, NumberRange.IntRange> stats = Maps.newHashMap();
        private final Object2BooleanMap<Identifier> recipes = new Object2BooleanOpenHashMap();
        private final Map<Identifier, AdvancementPredicate> advancements = Maps.newHashMap();

        public PlayerPredicate build() {
            return new PlayerPredicate(this.experienceLevel, this.gamemode, this.stats, this.recipes, this.advancements);
        }
    }

    static class AdvancementCriteriaPredicate
    implements AdvancementPredicate {
        private final Object2BooleanMap<String> criteria;

        public AdvancementCriteriaPredicate(Object2BooleanMap<String> criteria) {
            this.criteria = criteria;
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            this.criteria.forEach((arg_0, arg_1) -> ((JsonObject)jsonObject).addProperty(arg_0, arg_1));
            return jsonObject;
        }

        @Override
        public boolean test(AdvancementProgress arg) {
            for (Object2BooleanMap.Entry entry : this.criteria.object2BooleanEntrySet()) {
                CriterionProgress lv = arg.getCriterionProgress((String)entry.getKey());
                if (lv != null && lv.isObtained() == entry.getBooleanValue()) continue;
                return false;
            }
            return true;
        }

        @Override
        public /* synthetic */ boolean test(Object progress) {
            return this.test((AdvancementProgress)progress);
        }
    }

    static class CompletedAdvancementPredicate
    implements AdvancementPredicate {
        private final boolean done;

        public CompletedAdvancementPredicate(boolean done) {
            this.done = done;
        }

        @Override
        public JsonElement toJson() {
            return new JsonPrimitive(Boolean.valueOf(this.done));
        }

        @Override
        public boolean test(AdvancementProgress arg) {
            return arg.isDone() == this.done;
        }

        @Override
        public /* synthetic */ boolean test(Object progress) {
            return this.test((AdvancementProgress)progress);
        }
    }

    static interface AdvancementPredicate
    extends Predicate<AdvancementProgress> {
        public JsonElement toJson();
    }
}

