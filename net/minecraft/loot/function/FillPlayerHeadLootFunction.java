/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  com.mojang.authlib.GameProfile
 */
package net.minecraft.loot.function;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.authlib.GameProfile;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.JsonHelper;

public class FillPlayerHeadLootFunction
extends ConditionalLootFunction {
    private final LootContext.EntityTarget entity;

    public FillPlayerHeadLootFunction(LootCondition[] args, LootContext.EntityTarget arg) {
        super(args);
        this.entity = arg;
    }

    @Override
    public LootFunctionType getType() {
        return LootFunctionTypes.FILL_PLAYER_HEAD;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(this.entity.getParameter());
    }

    @Override
    public ItemStack process(ItemStack arg, LootContext arg2) {
        Entity lv;
        if (arg.getItem() == Items.PLAYER_HEAD && (lv = arg2.get(this.entity.getParameter())) instanceof PlayerEntity) {
            GameProfile gameProfile = ((PlayerEntity)lv).getGameProfile();
            arg.getOrCreateTag().put("SkullOwner", NbtHelper.fromGameProfile(new CompoundTag(), gameProfile));
        }
        return arg;
    }

    public static class Serializer
    extends ConditionalLootFunction.Serializer<FillPlayerHeadLootFunction> {
        @Override
        public void toJson(JsonObject jsonObject, FillPlayerHeadLootFunction arg, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, arg, jsonSerializationContext);
            jsonObject.add("entity", jsonSerializationContext.serialize((Object)arg.entity));
        }

        @Override
        public FillPlayerHeadLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] args) {
            LootContext.EntityTarget lv = JsonHelper.deserialize(jsonObject, "entity", jsonDeserializationContext, LootContext.EntityTarget.class);
            return new FillPlayerHeadLootFunction(args, lv);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] args) {
            return this.fromJson(jsonObject, jsonDeserializationContext, args);
        }
    }
}

