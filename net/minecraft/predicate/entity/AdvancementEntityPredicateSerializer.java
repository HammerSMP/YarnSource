/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 */
package net.minecraft.predicate.entity;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.class_5341;
import net.minecraft.loot.LootGsons;

public class AdvancementEntityPredicateSerializer {
    public static final AdvancementEntityPredicateSerializer INSTANCE = new AdvancementEntityPredicateSerializer();
    private final Gson gson = LootGsons.getConditionGsonBuilder().create();

    public final JsonElement conditionsToJson(class_5341[] args) {
        return this.gson.toJsonTree((Object)args);
    }
}

