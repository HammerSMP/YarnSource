/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SetDamageLootFunction
extends ConditionalLootFunction {
    private static final Logger LOGGER = LogManager.getLogger();
    private final UniformLootTableRange durabilityRange;

    private SetDamageLootFunction(LootCondition[] contents, UniformLootTableRange durabilityRange) {
        super(contents);
        this.durabilityRange = durabilityRange;
    }

    @Override
    public LootFunctionType getType() {
        return LootFunctionTypes.SET_DAMAGE;
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        if (stack.isDamageable()) {
            float f = 1.0f - this.durabilityRange.nextFloat(context.getRandom());
            stack.setDamage(MathHelper.floor(f * (float)stack.getMaxDamage()));
        } else {
            LOGGER.warn("Couldn't set damage of loot item {}", (Object)stack);
        }
        return stack;
    }

    public static ConditionalLootFunction.Builder<?> builder(UniformLootTableRange durabilityRange) {
        return SetDamageLootFunction.builder((LootCondition[] conditions) -> new SetDamageLootFunction((LootCondition[])conditions, durabilityRange));
    }

    public static class Serializer
    extends ConditionalLootFunction.Serializer<SetDamageLootFunction> {
        @Override
        public void toJson(JsonObject jsonObject, SetDamageLootFunction arg, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, arg, jsonSerializationContext);
            jsonObject.add("damage", jsonSerializationContext.serialize((Object)arg.durabilityRange));
        }

        @Override
        public SetDamageLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] args) {
            return new SetDamageLootFunction(args, JsonHelper.deserialize(jsonObject, "damage", jsonDeserializationContext, UniformLootTableRange.class));
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
            return this.fromJson(json, context, conditions);
        }
    }
}

