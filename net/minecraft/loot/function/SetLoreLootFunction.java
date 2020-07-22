/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Streams
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  javax.annotation.Nullable
 */
package net.minecraft.loot.function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.loot.function.SetNameLootFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.text.Text;
import net.minecraft.util.JsonHelper;

public class SetLoreLootFunction
extends ConditionalLootFunction {
    private final boolean replace;
    private final List<Text> lore;
    @Nullable
    private final LootContext.EntityTarget entity;

    public SetLoreLootFunction(LootCondition[] conditions, boolean replace, List<Text> lore, @Nullable LootContext.EntityTarget entity) {
        super(conditions);
        this.replace = replace;
        this.lore = ImmutableList.copyOf(lore);
        this.entity = entity;
    }

    @Override
    public LootFunctionType getType() {
        return LootFunctionTypes.SET_LORE;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return this.entity != null ? ImmutableSet.of(this.entity.getParameter()) : ImmutableSet.of();
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        ListTag lv = this.getLoreForMerge(stack, !this.lore.isEmpty());
        if (lv != null) {
            if (this.replace) {
                lv.clear();
            }
            UnaryOperator<Text> unaryOperator = SetNameLootFunction.applySourceEntity(context, this.entity);
            this.lore.stream().map(unaryOperator).map(Text.Serializer::toJson).map(StringTag::of).forEach(lv::add);
        }
        return stack;
    }

    /*
     * WARNING - void declaration
     */
    @Nullable
    private ListTag getLoreForMerge(ItemStack stack, boolean otherLoreExists) {
        void lv6;
        void lv3;
        if (stack.hasTag()) {
            CompoundTag lv = stack.getTag();
        } else if (otherLoreExists) {
            CompoundTag lv2 = new CompoundTag();
            stack.setTag(lv2);
        } else {
            return null;
        }
        if (lv3.contains("display", 10)) {
            CompoundTag lv4 = lv3.getCompound("display");
        } else if (otherLoreExists) {
            CompoundTag lv5 = new CompoundTag();
            lv3.put("display", lv5);
        } else {
            return null;
        }
        if (lv6.contains("Lore", 9)) {
            return lv6.getList("Lore", 8);
        }
        if (otherLoreExists) {
            ListTag lv7 = new ListTag();
            lv6.put("Lore", lv7);
            return lv7;
        }
        return null;
    }

    public static class Serializer
    extends ConditionalLootFunction.Serializer<SetLoreLootFunction> {
        @Override
        public void toJson(JsonObject jsonObject, SetLoreLootFunction arg, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, arg, jsonSerializationContext);
            jsonObject.addProperty("replace", Boolean.valueOf(arg.replace));
            JsonArray jsonArray = new JsonArray();
            for (Text lv : arg.lore) {
                jsonArray.add(Text.Serializer.toJsonTree(lv));
            }
            jsonObject.add("lore", (JsonElement)jsonArray);
            if (arg.entity != null) {
                jsonObject.add("entity", jsonSerializationContext.serialize((Object)arg.entity));
            }
        }

        @Override
        public SetLoreLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] args) {
            boolean bl = JsonHelper.getBoolean(jsonObject, "replace", false);
            List list = (List)Streams.stream((Iterable)JsonHelper.getArray(jsonObject, "lore")).map(Text.Serializer::fromJson).collect(ImmutableList.toImmutableList());
            LootContext.EntityTarget lv = JsonHelper.deserialize(jsonObject, "entity", null, jsonDeserializationContext, LootContext.EntityTarget.class);
            return new SetLoreLootFunction(args, bl, list, lv);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
            return this.fromJson(json, context, conditions);
        }
    }
}

