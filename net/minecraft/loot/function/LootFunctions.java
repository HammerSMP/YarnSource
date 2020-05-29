/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.loot.function;

import java.util.function.BiFunction;
import net.minecraft.class_5330;
import net.minecraft.class_5335;
import net.minecraft.class_5339;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.CopyNameLootFunction;
import net.minecraft.loot.function.CopyNbtLootFunction;
import net.minecraft.loot.function.CopyStateFunction;
import net.minecraft.loot.function.EnchantRandomlyLootFunction;
import net.minecraft.loot.function.EnchantWithLevelsLootFunction;
import net.minecraft.loot.function.ExplorationMapLootFunction;
import net.minecraft.loot.function.ExplosionDecayLootFunction;
import net.minecraft.loot.function.FillPlayerHeadLootFunction;
import net.minecraft.loot.function.FurnaceSmeltLootFunction;
import net.minecraft.loot.function.LimitCountLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootingEnchantLootFunction;
import net.minecraft.loot.function.SetAttributesLootFunction;
import net.minecraft.loot.function.SetContentsLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.function.SetDamageLootFunction;
import net.minecraft.loot.function.SetLootTableLootFunction;
import net.minecraft.loot.function.SetLoreLootFunction;
import net.minecraft.loot.function.SetNameLootFunction;
import net.minecraft.loot.function.SetNbtLootFunction;
import net.minecraft.loot.function.SetStewEffectLootFunction;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class LootFunctions {
    public static final BiFunction<ItemStack, LootContext, ItemStack> NOOP = (arg, arg2) -> arg;
    public static final class_5339 SET_COUNT = LootFunctions.method_29323("set_count", new SetCountLootFunction.Factory());
    public static final class_5339 ENCHANT_WITH_LEVELS = LootFunctions.method_29323("enchant_with_levels", new EnchantWithLevelsLootFunction.Factory());
    public static final class_5339 ENCHANT_RANDOMLY = LootFunctions.method_29323("enchant_randomly", new EnchantRandomlyLootFunction.Factory());
    public static final class_5339 SET_NBT = LootFunctions.method_29323("set_nbt", new SetNbtLootFunction.Builder());
    public static final class_5339 FURNACE_SMELT = LootFunctions.method_29323("furnace_smelt", new FurnaceSmeltLootFunction.class_5340());
    public static final class_5339 LOOTING_ENCHANT = LootFunctions.method_29323("looting_enchant", new LootingEnchantLootFunction.Factory());
    public static final class_5339 SET_DAMAGE = LootFunctions.method_29323("set_damage", new SetDamageLootFunction.Factory());
    public static final class_5339 SET_ATTRIBUTES = LootFunctions.method_29323("set_attributes", new SetAttributesLootFunction.Factory());
    public static final class_5339 SET_NAME = LootFunctions.method_29323("set_name", new SetNameLootFunction.Factory());
    public static final class_5339 EXPLORATION_MAP = LootFunctions.method_29323("exploration_map", new ExplorationMapLootFunction.Factory());
    public static final class_5339 SET_STEW_EFFECT = LootFunctions.method_29323("set_stew_effect", new SetStewEffectLootFunction.Factory());
    public static final class_5339 COPY_NAME = LootFunctions.method_29323("copy_name", new CopyNameLootFunction.Factory());
    public static final class_5339 SET_CONTENTS = LootFunctions.method_29323("set_contents", new SetContentsLootFunction.Factory());
    public static final class_5339 LIMIT_COUNT = LootFunctions.method_29323("limit_count", new LimitCountLootFunction.Factory());
    public static final class_5339 APPLY_BONUS = LootFunctions.method_29323("apply_bonus", new ApplyBonusLootFunction.Factory());
    public static final class_5339 SET_LOOT_TABLE = LootFunctions.method_29323("set_loot_table", new SetLootTableLootFunction.Factory());
    public static final class_5339 EXPLOSION_DECAY = LootFunctions.method_29323("explosion_decay", new ExplosionDecayLootFunction.Factory());
    public static final class_5339 SET_LORE = LootFunctions.method_29323("set_lore", new SetLoreLootFunction.Factory());
    public static final class_5339 FILL_PLAYER_HEAD = LootFunctions.method_29323("fill_player_head", new FillPlayerHeadLootFunction.Factory());
    public static final class_5339 COPY_NBT = LootFunctions.method_29323("copy_nbt", new CopyNbtLootFunction.Factory());
    public static final class_5339 COPY_STATE = LootFunctions.method_29323("copy_state", new CopyStateFunction.Factory());

    private static class_5339 method_29323(String string, class_5335<? extends LootFunction> arg) {
        return Registry.register(Registry.field_25294, new Identifier(string), new class_5339(arg));
    }

    public static Object method_29322() {
        return class_5330.method_29306(Registry.field_25294, "function", "function", LootFunction::method_29321).method_29307();
    }

    public static BiFunction<ItemStack, LootContext, ItemStack> join(BiFunction<ItemStack, LootContext, ItemStack>[] biFunctions) {
        switch (biFunctions.length) {
            case 0: {
                return NOOP;
            }
            case 1: {
                return biFunctions[0];
            }
            case 2: {
                BiFunction<ItemStack, LootContext, ItemStack> biFunction = biFunctions[0];
                BiFunction<ItemStack, LootContext, ItemStack> biFunction2 = biFunctions[1];
                return (arg, arg2) -> (ItemStack)biFunction2.apply((ItemStack)biFunction.apply((ItemStack)arg, (LootContext)arg2), (LootContext)arg2);
            }
        }
        return (arg, arg2) -> {
            for (BiFunction biFunction : biFunctions) {
                arg = (ItemStack)biFunction.apply(arg, arg2);
            }
            return arg;
        };
    }
}

