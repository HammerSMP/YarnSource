/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementManager;
import net.minecraft.advancement.AdvancementPositioner;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerAdvancementLoader
extends JsonDataLoader {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().create();
    private AdvancementManager manager = new AdvancementManager();
    private final LootConditionManager conditionManager;

    public ServerAdvancementLoader(LootConditionManager arg) {
        super(GSON, "advancements");
        this.conditionManager = arg;
    }

    @Override
    protected void apply(Map<Identifier, JsonObject> map, ResourceManager arg2, Profiler arg22) {
        HashMap map2 = Maps.newHashMap();
        map.forEach((arg, jsonObject) -> {
            try {
                JsonObject jsonObject2 = JsonHelper.asObject((JsonElement)jsonObject, "advancement");
                Advancement.Task lv = Advancement.Task.fromJson(jsonObject2, new AdvancementEntityPredicateDeserializer((Identifier)arg, this.conditionManager));
                map2.put(arg, lv);
            }
            catch (JsonParseException | IllegalArgumentException runtimeException) {
                LOGGER.error("Parsing error loading custom advancement {}: {}", arg, (Object)runtimeException.getMessage());
            }
        });
        AdvancementManager lv = new AdvancementManager();
        lv.load(map2);
        for (Advancement lv2 : lv.getRoots()) {
            if (lv2.getDisplay() == null) continue;
            AdvancementPositioner.arrangeForTree(lv2);
        }
        this.manager = lv;
    }

    @Nullable
    public Advancement get(Identifier arg) {
        return this.manager.get(arg);
    }

    public Collection<Advancement> getAdvancements() {
        return this.manager.getAdvancements();
    }
}

