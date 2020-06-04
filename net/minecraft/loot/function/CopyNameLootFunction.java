/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 */
package net.minecraft.loot.function;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Nameable;

public class CopyNameLootFunction
extends ConditionalLootFunction {
    private final Source source;

    private CopyNameLootFunction(LootCondition[] args, Source arg) {
        super(args);
        this.source = arg;
    }

    @Override
    public LootFunctionType method_29321() {
        return LootFunctionTypes.COPY_NAME;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(this.source.parameter);
    }

    @Override
    public ItemStack process(ItemStack arg, LootContext arg2) {
        Nameable lv;
        Object object = arg2.get(this.source.parameter);
        if (object instanceof Nameable && (lv = (Nameable)object).hasCustomName()) {
            arg.setCustomName(lv.getDisplayName());
        }
        return arg;
    }

    public static ConditionalLootFunction.Builder<?> builder(Source arg) {
        return CopyNameLootFunction.builder((LootCondition[] args) -> new CopyNameLootFunction((LootCondition[])args, arg));
    }

    public static class Factory
    extends ConditionalLootFunction.Factory<CopyNameLootFunction> {
        @Override
        public void toJson(JsonObject jsonObject, CopyNameLootFunction arg, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, arg, jsonSerializationContext);
            jsonObject.addProperty("source", ((CopyNameLootFunction)arg).source.name);
        }

        @Override
        public CopyNameLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] args) {
            Source lv = Source.get(JsonHelper.getString(jsonObject, "source"));
            return new CopyNameLootFunction(args, lv);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] args) {
            return this.fromJson(jsonObject, jsonDeserializationContext, args);
        }
    }

    public static enum Source {
        THIS("this", LootContextParameters.THIS_ENTITY),
        KILLER("killer", LootContextParameters.KILLER_ENTITY),
        KILLER_PLAYER("killer_player", LootContextParameters.LAST_DAMAGE_PLAYER),
        BLOCK_ENTITY("block_entity", LootContextParameters.BLOCK_ENTITY);

        public final String name;
        public final LootContextParameter<?> parameter;

        private Source(String string2, LootContextParameter<?> arg) {
            this.name = string2;
            this.parameter = arg;
        }

        public static Source get(String string) {
            for (Source lv : Source.values()) {
                if (!lv.name.equals(string)) continue;
                return lv;
            }
            throw new IllegalArgumentException("Invalid name source " + string);
        }
    }
}

