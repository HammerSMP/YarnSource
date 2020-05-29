/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.loot.condition;

import java.util.function.Predicate;
import net.minecraft.class_5330;
import net.minecraft.class_5335;
import net.minecraft.class_5341;
import net.minecraft.class_5342;
import net.minecraft.loot.condition.AlternativeLootCondition;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.DamageSourcePropertiesLootCondition;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.condition.EntityScoresLootCondition;
import net.minecraft.loot.condition.InvertedLootCondition;
import net.minecraft.loot.condition.KilledByPlayerLootCondition;
import net.minecraft.loot.condition.LocationCheckLootCondition;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.condition.RandomChanceWithLootingLootCondition;
import net.minecraft.loot.condition.ReferenceLootCondition;
import net.minecraft.loot.condition.SurvivesExplosionLootCondition;
import net.minecraft.loot.condition.TableBonusLootCondition;
import net.minecraft.loot.condition.TimeCheckLootCondition;
import net.minecraft.loot.condition.WeatherCheckLootCondition;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class LootConditions {
    public static final class_5342 INVERTED = LootConditions.method_29327("inverted", new InvertedLootCondition.Factory());
    public static final class_5342 ALTERNATIVE = LootConditions.method_29327("alternative", new AlternativeLootCondition.Factory());
    public static final class_5342 RANDOM_CHANCE = LootConditions.method_29327("random_chance", new RandomChanceLootCondition.Factory());
    public static final class_5342 RANDOM_CHANCE_WITH_LOOTING = LootConditions.method_29327("random_chance_with_looting", new RandomChanceWithLootingLootCondition.Factory());
    public static final class_5342 ENTITY_PROPERTIES = LootConditions.method_29327("entity_properties", new EntityPropertiesLootCondition.Factory());
    public static final class_5342 KILLED_BY_PLAYER = LootConditions.method_29327("killed_by_player", new KilledByPlayerLootCondition.Factory());
    public static final class_5342 ENTITY_SCORES = LootConditions.method_29327("entity_scores", new EntityScoresLootCondition.Factory());
    public static final class_5342 BLOCK_STATE_PROPERTY = LootConditions.method_29327("block_state_property", new BlockStatePropertyLootCondition.Factory());
    public static final class_5342 MATCH_TOOL = LootConditions.method_29327("match_tool", new MatchToolLootCondition.Factory());
    public static final class_5342 TABLE_BONUS = LootConditions.method_29327("table_bonus", new TableBonusLootCondition.Factory());
    public static final class_5342 SURVIVES_EXPLOSION = LootConditions.method_29327("survives_explosion", new SurvivesExplosionLootCondition.Factory());
    public static final class_5342 DAMAGE_SOURCE_PROPERTIES = LootConditions.method_29327("damage_source_properties", new DamageSourcePropertiesLootCondition.Factory());
    public static final class_5342 LOCATION_CHECK = LootConditions.method_29327("location_check", new LocationCheckLootCondition.Factory());
    public static final class_5342 WEATHER_CHECK = LootConditions.method_29327("weather_check", new WeatherCheckLootCondition.Factory());
    public static final class_5342 REFERENCE = LootConditions.method_29327("reference", new ReferenceLootCondition.Factory());
    public static final class_5342 TIME_CHECK = LootConditions.method_29327("time_check", new TimeCheckLootCondition.Factory());

    private static class_5342 method_29327(String string, class_5335<? extends class_5341> arg) {
        return Registry.register(Registry.field_25299, new Identifier(string), new class_5342(arg));
    }

    public static Object method_29326() {
        return class_5330.method_29306(Registry.field_25299, "condition", "condition", class_5341::method_29325).method_29307();
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
}

