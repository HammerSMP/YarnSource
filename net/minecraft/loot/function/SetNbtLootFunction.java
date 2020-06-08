/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 */
package net.minecraft.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.JsonHelper;

public class SetNbtLootFunction
extends ConditionalLootFunction {
    private final CompoundTag tag;

    private SetNbtLootFunction(LootCondition[] args, CompoundTag arg) {
        super(args);
        this.tag = arg;
    }

    @Override
    public LootFunctionType getType() {
        return LootFunctionTypes.SET_NBT;
    }

    @Override
    public ItemStack process(ItemStack arg, LootContext arg2) {
        arg.getOrCreateTag().copyFrom(this.tag);
        return arg;
    }

    public static ConditionalLootFunction.Builder<?> builder(CompoundTag arg) {
        return SetNbtLootFunction.builder((LootCondition[] args) -> new SetNbtLootFunction((LootCondition[])args, arg));
    }

    public static class Serializer
    extends ConditionalLootFunction.Serializer<SetNbtLootFunction> {
        @Override
        public void toJson(JsonObject jsonObject, SetNbtLootFunction arg, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, arg, jsonSerializationContext);
            jsonObject.addProperty("tag", arg.tag.toString());
        }

        @Override
        public SetNbtLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] args) {
            try {
                CompoundTag lv = StringNbtReader.parse(JsonHelper.getString(jsonObject, "tag"));
                return new SetNbtLootFunction(args, lv);
            }
            catch (CommandSyntaxException commandSyntaxException) {
                throw new JsonSyntaxException(commandSyntaxException.getMessage());
            }
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] args) {
            return this.fromJson(jsonObject, jsonDeserializationContext, args);
        }
    }
}

