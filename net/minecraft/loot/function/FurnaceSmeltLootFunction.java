/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FurnaceSmeltLootFunction
extends ConditionalLootFunction {
    private static final Logger LOGGER = LogManager.getLogger();

    private FurnaceSmeltLootFunction(LootCondition[] args) {
        super(args);
    }

    @Override
    public LootFunctionType getType() {
        return LootFunctionTypes.FURNACE_SMELT;
    }

    @Override
    public ItemStack process(ItemStack arg, LootContext arg2) {
        ItemStack lv;
        if (arg.isEmpty()) {
            return arg;
        }
        Optional<SmeltingRecipe> optional = arg2.getWorld().getRecipeManager().getFirstMatch(RecipeType.SMELTING, new SimpleInventory(arg), arg2.getWorld());
        if (optional.isPresent() && !(lv = optional.get().getOutput()).isEmpty()) {
            ItemStack lv2 = lv.copy();
            lv2.setCount(arg.getCount());
            return lv2;
        }
        LOGGER.warn("Couldn't smelt {} because there is no smelting recipe", (Object)arg);
        return arg;
    }

    public static ConditionalLootFunction.Builder<?> builder() {
        return FurnaceSmeltLootFunction.builder(FurnaceSmeltLootFunction::new);
    }

    public static class Serializer
    extends ConditionalLootFunction.Serializer<FurnaceSmeltLootFunction> {
        @Override
        public FurnaceSmeltLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] args) {
            return new FurnaceSmeltLootFunction(args);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] args) {
            return this.fromJson(jsonObject, jsonDeserializationContext, args);
        }
    }
}

