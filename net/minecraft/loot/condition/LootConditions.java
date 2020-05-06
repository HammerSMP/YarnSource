/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 *  com.google.gson.JsonSyntaxException
 */
package net.minecraft.loot.condition;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.loot.condition.AlternativeLootCondition;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.DamageSourcePropertiesLootCondition;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.condition.EntityScoresLootCondition;
import net.minecraft.loot.condition.InvertedLootCondition;
import net.minecraft.loot.condition.KilledByPlayerLootCondition;
import net.minecraft.loot.condition.LocationCheckLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.condition.RandomChanceWithLootingLootCondition;
import net.minecraft.loot.condition.ReferenceLootCondition;
import net.minecraft.loot.condition.SurvivesExplosionLootCondition;
import net.minecraft.loot.condition.TableBonusLootCondition;
import net.minecraft.loot.condition.TimeCheckLootCondition;
import net.minecraft.loot.condition.WeatherCheckLootCondition;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class LootConditions {
    private static final Map<Identifier, LootCondition.Factory<?>> byId = Maps.newHashMap();
    private static final Map<Class<? extends LootCondition>, LootCondition.Factory<?>> byClass = Maps.newHashMap();

    public static <T extends LootCondition> void register(LootCondition.Factory<? extends T> arg) {
        Identifier lv = arg.getId();
        Class<T> lv2 = arg.getConditionClass();
        if (byId.containsKey(lv)) {
            throw new IllegalArgumentException("Can't re-register item condition name " + lv);
        }
        if (byClass.containsKey(lv2)) {
            throw new IllegalArgumentException("Can't re-register item condition class " + lv2.getName());
        }
        byId.put(lv, arg);
        byClass.put(lv2, arg);
    }

    public static LootCondition.Factory<?> get(Identifier arg) {
        LootCondition.Factory<?> lv = byId.get(arg);
        if (lv == null) {
            throw new IllegalArgumentException("Unknown loot item condition '" + arg + "'");
        }
        return lv;
    }

    public static <T extends LootCondition> LootCondition.Factory<T> getFactory(T arg) {
        LootCondition.Factory<?> lv = byClass.get(arg.getClass());
        if (lv == null) {
            throw new IllegalArgumentException("Unknown loot item condition " + arg);
        }
        return lv;
    }

    public static <T> Predicate<T> joinAnd(Predicate<T>[] predicates) {
        switch (predicates.length) {
            case 0: {
                return object -> true;
            }
            case 1: {
                return predicates[0];
            }
            case 2: {
                return predicates[0].and(predicates[1]);
            }
        }
        return object -> {
            for (Predicate predicate : predicates) {
                if (predicate.test(object)) continue;
                return false;
            }
            return true;
        };
    }

    public static <T> Predicate<T> joinOr(Predicate<T>[] predicates) {
        switch (predicates.length) {
            case 0: {
                return object -> false;
            }
            case 1: {
                return predicates[0];
            }
            case 2: {
                return predicates[0].or(predicates[1]);
            }
        }
        return object -> {
            for (Predicate predicate : predicates) {
                if (!predicate.test(object)) continue;
                return true;
            }
            return false;
        };
    }

    static {
        LootConditions.register(new InvertedLootCondition.Factory());
        LootConditions.register(new AlternativeLootCondition.Factory());
        LootConditions.register(new RandomChanceLootCondition.Factory());
        LootConditions.register(new RandomChanceWithLootingLootCondition.Factory());
        LootConditions.register(new EntityPropertiesLootCondition.Factory());
        LootConditions.register(new KilledByPlayerLootCondition.Factory());
        LootConditions.register(new EntityScoresLootCondition.Factory());
        LootConditions.register(new BlockStatePropertyLootCondition.Factory());
        LootConditions.register(new MatchToolLootCondition.Factory());
        LootConditions.register(new TableBonusLootCondition.Factory());
        LootConditions.register(new SurvivesExplosionLootCondition.Factory());
        LootConditions.register(new DamageSourcePropertiesLootCondition.Factory());
        LootConditions.register(new LocationCheckLootCondition.Factory());
        LootConditions.register(new WeatherCheckLootCondition.Factory());
        LootConditions.register(new ReferenceLootCondition.Factory());
        LootConditions.register(new TimeCheckLootCondition.Factory());
    }

    public static class Factory
    implements JsonDeserializer<LootCondition>,
    JsonSerializer<LootCondition> {
        /*
         * WARNING - void declaration
         */
        public LootCondition deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            void lv3;
            JsonObject jsonObject = JsonHelper.asObject(jsonElement, "condition");
            Identifier lv = new Identifier(JsonHelper.getString(jsonObject, "condition"));
            try {
                LootCondition.Factory<?> lv2 = LootConditions.get(lv);
            }
            catch (IllegalArgumentException illegalArgumentException) {
                throw new JsonSyntaxException("Unknown condition '" + lv + "'");
            }
            return lv3.fromJson(jsonObject, jsonDeserializationContext);
        }

        public JsonElement serialize(LootCondition arg, Type type, JsonSerializationContext jsonSerializationContext) {
            LootCondition.Factory<LootCondition> lv = LootConditions.getFactory(arg);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("condition", lv.getId().toString());
            lv.toJson(jsonObject, arg, jsonSerializationContext);
            return jsonObject;
        }

        public /* synthetic */ JsonElement serialize(Object object, Type type, JsonSerializationContext jsonSerializationContext) {
            return this.serialize((LootCondition)object, type, jsonSerializationContext);
        }

        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }
}

