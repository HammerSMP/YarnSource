/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.loot;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Set;
import net.minecraft.loot.LootGsons;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootManager
extends JsonDataLoader {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = LootGsons.getTableGsonBuilder().create();
    private Map<Identifier, LootTable> tables = ImmutableMap.of();
    private final LootConditionManager conditionManager;

    public LootManager(LootConditionManager arg) {
        super(GSON, "loot_tables");
        this.conditionManager = arg;
    }

    public LootTable getTable(Identifier arg) {
        return this.tables.getOrDefault(arg, LootTable.EMPTY);
    }

    @Override
    protected void apply(Map<Identifier, JsonObject> map, ResourceManager arg4, Profiler arg22) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        JsonObject jsonObject2 = map.remove(LootTables.EMPTY);
        if (jsonObject2 != null) {
            LOGGER.warn("Datapack tried to redefine {} loot table, ignoring", (Object)LootTables.EMPTY);
        }
        map.forEach((arg, jsonObject) -> {
            try {
                LootTable lv = (LootTable)GSON.fromJson((JsonElement)jsonObject, LootTable.class);
                builder.put(arg, (Object)lv);
            }
            catch (Exception exception) {
                LOGGER.error("Couldn't parse loot table {}", arg, (Object)exception);
            }
        });
        builder.put((Object)LootTables.EMPTY, (Object)LootTable.EMPTY);
        ImmutableMap immutableMap = builder.build();
        LootTableReporter lv = new LootTableReporter(LootContextTypes.GENERIC, this.conditionManager::get, ((ImmutableMap)immutableMap)::get);
        immutableMap.forEach((arg2, arg3) -> LootManager.check(lv, arg2, arg3));
        lv.getMessages().forEach((string, string2) -> LOGGER.warn("Found validation problem in " + string + ": " + string2));
        this.tables = immutableMap;
    }

    public static void check(LootTableReporter arg, Identifier arg2, LootTable arg3) {
        arg3.check(arg.withContextType(arg3.getType()).withTable("{" + arg2 + "}", arg2));
    }

    public static JsonElement toJson(LootTable arg) {
        return GSON.toJsonTree((Object)arg);
    }

    public Set<Identifier> getTableIds() {
        return this.tables.keySet();
    }
}

