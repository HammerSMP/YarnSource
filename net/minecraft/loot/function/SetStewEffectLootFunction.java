/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonPrimitive
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSyntaxException
 */
package net.minecraft.loot.function;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class SetStewEffectLootFunction
extends ConditionalLootFunction {
    private final Map<StatusEffect, UniformLootTableRange> effects;

    private SetStewEffectLootFunction(LootCondition[] args, Map<StatusEffect, UniformLootTableRange> map) {
        super(args);
        this.effects = ImmutableMap.copyOf(map);
    }

    @Override
    public LootFunctionType method_29321() {
        return LootFunctionTypes.SET_STEW_EFFECT;
    }

    @Override
    public ItemStack process(ItemStack arg, LootContext arg2) {
        if (arg.getItem() != Items.SUSPICIOUS_STEW || this.effects.isEmpty()) {
            return arg;
        }
        Random random = arg2.getRandom();
        int i = random.nextInt(this.effects.size());
        Map.Entry entry = (Map.Entry)Iterables.get(this.effects.entrySet(), (int)i);
        StatusEffect lv = (StatusEffect)entry.getKey();
        int j = ((UniformLootTableRange)entry.getValue()).next(random);
        if (!lv.isInstant()) {
            j *= 20;
        }
        SuspiciousStewItem.addEffectToStew(arg, lv, j);
        return arg;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Factory
    extends ConditionalLootFunction.Factory<SetStewEffectLootFunction> {
        @Override
        public void toJson(JsonObject jsonObject, SetStewEffectLootFunction arg, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, arg, jsonSerializationContext);
            if (!arg.effects.isEmpty()) {
                JsonArray jsonArray = new JsonArray();
                for (StatusEffect lv : arg.effects.keySet()) {
                    JsonObject jsonObject2 = new JsonObject();
                    Identifier lv2 = Registry.STATUS_EFFECT.getId(lv);
                    if (lv2 == null) {
                        throw new IllegalArgumentException("Don't know how to serialize mob effect " + lv);
                    }
                    jsonObject2.add("type", (JsonElement)new JsonPrimitive(lv2.toString()));
                    jsonObject2.add("duration", jsonSerializationContext.serialize(arg.effects.get(lv)));
                    jsonArray.add((JsonElement)jsonObject2);
                }
                jsonObject.add("effects", (JsonElement)jsonArray);
            }
        }

        @Override
        public SetStewEffectLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] args) {
            HashMap map = Maps.newHashMap();
            if (jsonObject.has("effects")) {
                JsonArray jsonArray = JsonHelper.getArray(jsonObject, "effects");
                for (JsonElement jsonElement : jsonArray) {
                    String string = JsonHelper.getString(jsonElement.getAsJsonObject(), "type");
                    StatusEffect lv = Registry.STATUS_EFFECT.getOrEmpty(new Identifier(string)).orElseThrow(() -> new JsonSyntaxException("Unknown mob effect '" + string + "'"));
                    UniformLootTableRange lv2 = JsonHelper.deserialize(jsonElement.getAsJsonObject(), "duration", jsonDeserializationContext, UniformLootTableRange.class);
                    map.put(lv, lv2);
                }
            }
            return new SetStewEffectLootFunction(args, map);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] args) {
            return this.fromJson(jsonObject, jsonDeserializationContext, args);
        }
    }

    public static class Builder
    extends ConditionalLootFunction.Builder<Builder> {
        private final Map<StatusEffect, UniformLootTableRange> map = Maps.newHashMap();

        @Override
        protected Builder getThisBuilder() {
            return this;
        }

        public Builder withEffect(StatusEffect arg, UniformLootTableRange arg2) {
            this.map.put(arg, arg2);
            return this;
        }

        @Override
        public LootFunction build() {
            return new SetStewEffectLootFunction(this.getConditions(), this.map);
        }

        @Override
        protected /* synthetic */ ConditionalLootFunction.Builder getThisBuilder() {
            return this.getThisBuilder();
        }
    }
}

