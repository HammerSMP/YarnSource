/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.loot.condition;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.loot.LootGsons;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootConditionManager
extends JsonDataLoader {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = LootGsons.getConditionGsonBuilder().create();
    private Map<Identifier, LootCondition> conditions = ImmutableMap.of();

    public LootConditionManager() {
        super(GSON, "predicates");
    }

    @Nullable
    public LootCondition get(Identifier arg) {
        return this.conditions.get(arg);
    }

    @Override
    protected void apply(Map<Identifier, JsonObject> map, ResourceManager arg4, Profiler arg22) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        map.forEach((arg, jsonObject) -> {
            try {
                LootCondition lv = (LootCondition)GSON.fromJson((JsonElement)jsonObject, LootCondition.class);
                builder.put(arg, (Object)lv);
            }
            catch (Exception exception) {
                LOGGER.error("Couldn't parse loot table {}", arg, (Object)exception);
            }
        });
        ImmutableMap map2 = builder.build();
        LootTableReporter lv = new LootTableReporter(LootContextTypes.GENERIC, ((Map)map2)::get, arg -> null);
        map2.forEach((arg2, arg3) -> arg3.check(lv.withCondition("{" + arg2 + "}", (Identifier)arg2)));
        lv.getMessages().forEach((string, string2) -> LOGGER.warn("Found validation problem in " + string + ": " + string2));
        this.conditions = map2;
    }

    public Set<Identifier> getIds() {
        return Collections.unmodifiableSet(this.conditions.keySet());
    }
}

