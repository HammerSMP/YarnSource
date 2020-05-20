/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.predicate.entity;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.minecraft.loot.LootGsons;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdvancementEntityPredicateDeserializer {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Identifier advancementId;
    private final LootConditionManager conditionManager;
    private final Gson gson = LootGsons.getConditionGsonBuilder().create();

    public AdvancementEntityPredicateDeserializer(Identifier arg, LootConditionManager arg2) {
        this.advancementId = arg;
        this.conditionManager = arg2;
    }

    public final LootCondition[] loadConditions(JsonArray jsonArray, String string, LootContextType arg2) {
        LootCondition[] lvs = (LootCondition[])this.gson.fromJson((JsonElement)jsonArray, LootCondition[].class);
        LootTableReporter lv = new LootTableReporter(arg2, this.conditionManager::get, arg -> null);
        for (LootCondition lv2 : lvs) {
            lv2.validate(lv);
            lv.getMessages().forEach((string2, string3) -> LOGGER.warn("Found validation problem in advancement trigger {}/{}: {}", (Object)string, string2, string3));
        }
        return lvs;
    }

    public Identifier getAdvancementId() {
        return this.advancementId;
    }
}

