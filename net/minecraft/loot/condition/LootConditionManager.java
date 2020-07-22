/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.loot.condition;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.loot.LootGsons;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.context.LootContext;
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
    public LootCondition get(Identifier id) {
        return this.conditions.get(id);
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> map, ResourceManager arg4, Profiler arg22) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        map.forEach((arg, jsonElement) -> {
            try {
                if (jsonElement.isJsonArray()) {
                    LootCondition[] lvs = (LootCondition[])GSON.fromJson(jsonElement, LootCondition[].class);
                    builder.put(arg, (Object)new AndCondition(lvs));
                } else {
                    LootCondition lv = (LootCondition)GSON.fromJson(jsonElement, LootCondition.class);
                    builder.put(arg, (Object)lv);
                }
            }
            catch (Exception exception) {
                LOGGER.error("Couldn't parse loot table {}", arg, (Object)exception);
            }
        });
        ImmutableMap map2 = builder.build();
        LootTableReporter lv = new LootTableReporter(LootContextTypes.GENERIC, ((Map)map2)::get, arg -> null);
        map2.forEach((arg2, arg3) -> arg3.validate(lv.withCondition("{" + arg2 + "}", (Identifier)arg2)));
        lv.getMessages().forEach((string, string2) -> LOGGER.warn("Found validation problem in " + string + ": " + string2));
        this.conditions = map2;
    }

    public Set<Identifier> getIds() {
        return Collections.unmodifiableSet(this.conditions.keySet());
    }

    static class AndCondition
    implements LootCondition {
        private final LootCondition[] terms;
        private final Predicate<LootContext> predicate;

        private AndCondition(LootCondition[] elements) {
            this.terms = elements;
            this.predicate = LootConditionTypes.joinAnd(elements);
        }

        @Override
        public final boolean test(LootContext arg) {
            return this.predicate.test(arg);
        }

        @Override
        public void validate(LootTableReporter reporter) {
            LootCondition.super.validate(reporter);
            for (int i = 0; i < this.terms.length; ++i) {
                this.terms[i].validate(reporter.makeChild(".term[" + i + "]"));
            }
        }

        @Override
        public LootConditionType getType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public /* synthetic */ boolean test(Object context) {
            return this.test((LootContext)context);
        }
    }
}

