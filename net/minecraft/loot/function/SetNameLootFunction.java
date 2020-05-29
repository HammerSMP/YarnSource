/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.loot.function;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Set;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.class_5339;
import net.minecraft.class_5341;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.JsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SetNameLootFunction
extends ConditionalLootFunction {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Text name;
    @Nullable
    private final LootContext.EntityTarget entity;

    private SetNameLootFunction(class_5341[] args, @Nullable Text arg, @Nullable LootContext.EntityTarget arg2) {
        super(args);
        this.name = arg;
        this.entity = arg2;
    }

    @Override
    public class_5339 method_29321() {
        return LootFunctions.SET_NAME;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return this.entity != null ? ImmutableSet.of(this.entity.getParameter()) : ImmutableSet.of();
    }

    public static UnaryOperator<Text> applySourceEntity(LootContext arg2, @Nullable LootContext.EntityTarget arg22) {
        Entity lv;
        if (arg22 != null && (lv = arg2.get(arg22.getParameter())) != null) {
            ServerCommandSource lv2 = lv.getCommandSource().withLevel(2);
            return arg3 -> {
                try {
                    return Texts.parse(lv2, arg3, lv, 0);
                }
                catch (CommandSyntaxException commandSyntaxException) {
                    LOGGER.warn("Failed to resolve text component", (Throwable)commandSyntaxException);
                    return arg3;
                }
            };
        }
        return arg -> arg;
    }

    @Override
    public ItemStack process(ItemStack arg, LootContext arg2) {
        if (this.name != null) {
            arg.setCustomName((Text)SetNameLootFunction.applySourceEntity(arg2, this.entity).apply(this.name));
        }
        return arg;
    }

    public static class Factory
    extends ConditionalLootFunction.Factory<SetNameLootFunction> {
        @Override
        public void toJson(JsonObject jsonObject, SetNameLootFunction arg, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, arg, jsonSerializationContext);
            if (arg.name != null) {
                jsonObject.add("name", Text.Serializer.toJsonTree(arg.name));
            }
            if (arg.entity != null) {
                jsonObject.add("entity", jsonSerializationContext.serialize((Object)arg.entity));
            }
        }

        @Override
        public SetNameLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_5341[] args) {
            MutableText lv = Text.Serializer.fromJson(jsonObject.get("name"));
            LootContext.EntityTarget lv2 = JsonHelper.deserialize(jsonObject, "entity", null, jsonDeserializationContext, LootContext.EntityTarget.class);
            return new SetNameLootFunction(args, lv, lv2);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_5341[] args) {
            return this.fromJson(jsonObject, jsonDeserializationContext, args);
        }
    }
}

