/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.recipe;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.registry.Registry;

public class BrewingRecipeRegistry {
    private static final List<Recipe<Potion>> POTION_RECIPES = Lists.newArrayList();
    private static final List<Recipe<Item>> ITEM_RECIPES = Lists.newArrayList();
    private static final List<Ingredient> POTION_TYPES = Lists.newArrayList();
    private static final Predicate<ItemStack> POTION_TYPE_PREDICATE = arg -> {
        for (Ingredient lv : POTION_TYPES) {
            if (!lv.test((ItemStack)arg)) continue;
            return true;
        }
        return false;
    };

    public static boolean isValidIngredient(ItemStack arg) {
        return BrewingRecipeRegistry.isItemRecipeIngredient(arg) || BrewingRecipeRegistry.isPotionRecipeIngredient(arg);
    }

    protected static boolean isItemRecipeIngredient(ItemStack arg) {
        int j = ITEM_RECIPES.size();
        for (int i = 0; i < j; ++i) {
            if (!((Recipe)ITEM_RECIPES.get(i)).ingredient.test(arg)) continue;
            return true;
        }
        return false;
    }

    protected static boolean isPotionRecipeIngredient(ItemStack arg) {
        int j = POTION_RECIPES.size();
        for (int i = 0; i < j; ++i) {
            if (!((Recipe)POTION_RECIPES.get(i)).ingredient.test(arg)) continue;
            return true;
        }
        return false;
    }

    public static boolean isBrewable(Potion arg) {
        int j = POTION_RECIPES.size();
        for (int i = 0; i < j; ++i) {
            if (((Recipe)POTION_RECIPES.get(i)).output != arg) continue;
            return true;
        }
        return false;
    }

    public static boolean hasRecipe(ItemStack arg, ItemStack arg2) {
        if (!POTION_TYPE_PREDICATE.test(arg)) {
            return false;
        }
        return BrewingRecipeRegistry.hasItemRecipe(arg, arg2) || BrewingRecipeRegistry.hasPotionRecipe(arg, arg2);
    }

    protected static boolean hasItemRecipe(ItemStack arg, ItemStack arg2) {
        Item lv = arg.getItem();
        int j = ITEM_RECIPES.size();
        for (int i = 0; i < j; ++i) {
            Recipe<Item> lv2 = ITEM_RECIPES.get(i);
            if (((Recipe)lv2).input != lv || !((Recipe)lv2).ingredient.test(arg2)) continue;
            return true;
        }
        return false;
    }

    protected static boolean hasPotionRecipe(ItemStack arg, ItemStack arg2) {
        Potion lv = PotionUtil.getPotion(arg);
        int j = POTION_RECIPES.size();
        for (int i = 0; i < j; ++i) {
            Recipe<Potion> lv2 = POTION_RECIPES.get(i);
            if (((Recipe)lv2).input != lv || !((Recipe)lv2).ingredient.test(arg2)) continue;
            return true;
        }
        return false;
    }

    public static ItemStack craft(ItemStack arg, ItemStack arg2) {
        if (!arg2.isEmpty()) {
            Potion lv = PotionUtil.getPotion(arg2);
            Item lv2 = arg2.getItem();
            int j = ITEM_RECIPES.size();
            for (int i = 0; i < j; ++i) {
                Recipe<Item> lv3 = ITEM_RECIPES.get(i);
                if (((Recipe)lv3).input != lv2 || !((Recipe)lv3).ingredient.test(arg)) continue;
                return PotionUtil.setPotion(new ItemStack((ItemConvertible)((Recipe)lv3).output), lv);
            }
            int l = POTION_RECIPES.size();
            for (int k = 0; k < l; ++k) {
                Recipe<Potion> lv4 = POTION_RECIPES.get(k);
                if (((Recipe)lv4).input != lv || !((Recipe)lv4).ingredient.test(arg)) continue;
                return PotionUtil.setPotion(new ItemStack(lv2), (Potion)((Recipe)lv4).output);
            }
        }
        return arg2;
    }

    public static void registerDefaults() {
        BrewingRecipeRegistry.registerPotionType(Items.POTION);
        BrewingRecipeRegistry.registerPotionType(Items.SPLASH_POTION);
        BrewingRecipeRegistry.registerPotionType(Items.LINGERING_POTION);
        BrewingRecipeRegistry.registerItemRecipe(Items.POTION, Items.GUNPOWDER, Items.SPLASH_POTION);
        BrewingRecipeRegistry.registerItemRecipe(Items.SPLASH_POTION, Items.DRAGON_BREATH, Items.LINGERING_POTION);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.WATER, Items.GLISTERING_MELON_SLICE, Potions.MUNDANE);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.WATER, Items.GHAST_TEAR, Potions.MUNDANE);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.WATER, Items.RABBIT_FOOT, Potions.MUNDANE);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.WATER, Items.BLAZE_POWDER, Potions.MUNDANE);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.WATER, Items.SPIDER_EYE, Potions.MUNDANE);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.WATER, Items.SUGAR, Potions.MUNDANE);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.WATER, Items.MAGMA_CREAM, Potions.MUNDANE);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.WATER, Items.GLOWSTONE_DUST, Potions.THICK);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.WATER, Items.REDSTONE, Potions.MUNDANE);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.WATER, Items.NETHER_WART, Potions.AWKWARD);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.AWKWARD, Items.GOLDEN_CARROT, Potions.NIGHT_VISION);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.NIGHT_VISION, Items.REDSTONE, Potions.LONG_NIGHT_VISION);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.NIGHT_VISION, Items.FERMENTED_SPIDER_EYE, Potions.INVISIBILITY);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.LONG_NIGHT_VISION, Items.FERMENTED_SPIDER_EYE, Potions.LONG_INVISIBILITY);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.INVISIBILITY, Items.REDSTONE, Potions.LONG_INVISIBILITY);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.AWKWARD, Items.MAGMA_CREAM, Potions.FIRE_RESISTANCE);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.FIRE_RESISTANCE, Items.REDSTONE, Potions.LONG_FIRE_RESISTANCE);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.AWKWARD, Items.RABBIT_FOOT, Potions.LEAPING);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.LEAPING, Items.REDSTONE, Potions.LONG_LEAPING);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.LEAPING, Items.GLOWSTONE_DUST, Potions.STRONG_LEAPING);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.LEAPING, Items.FERMENTED_SPIDER_EYE, Potions.SLOWNESS);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.LONG_LEAPING, Items.FERMENTED_SPIDER_EYE, Potions.LONG_SLOWNESS);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.SLOWNESS, Items.REDSTONE, Potions.LONG_SLOWNESS);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.SLOWNESS, Items.GLOWSTONE_DUST, Potions.STRONG_SLOWNESS);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.AWKWARD, Items.TURTLE_HELMET, Potions.TURTLE_MASTER);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.TURTLE_MASTER, Items.REDSTONE, Potions.LONG_TURTLE_MASTER);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.TURTLE_MASTER, Items.GLOWSTONE_DUST, Potions.STRONG_TURTLE_MASTER);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.SWIFTNESS, Items.FERMENTED_SPIDER_EYE, Potions.SLOWNESS);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.LONG_SWIFTNESS, Items.FERMENTED_SPIDER_EYE, Potions.LONG_SLOWNESS);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.AWKWARD, Items.SUGAR, Potions.SWIFTNESS);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.SWIFTNESS, Items.REDSTONE, Potions.LONG_SWIFTNESS);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.SWIFTNESS, Items.GLOWSTONE_DUST, Potions.STRONG_SWIFTNESS);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.AWKWARD, Items.PUFFERFISH, Potions.WATER_BREATHING);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.WATER_BREATHING, Items.REDSTONE, Potions.LONG_WATER_BREATHING);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.AWKWARD, Items.GLISTERING_MELON_SLICE, Potions.HEALING);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.HEALING, Items.GLOWSTONE_DUST, Potions.STRONG_HEALING);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.HEALING, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.STRONG_HEALING, Items.FERMENTED_SPIDER_EYE, Potions.STRONG_HARMING);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.HARMING, Items.GLOWSTONE_DUST, Potions.STRONG_HARMING);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.POISON, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.LONG_POISON, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.STRONG_POISON, Items.FERMENTED_SPIDER_EYE, Potions.STRONG_HARMING);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.AWKWARD, Items.SPIDER_EYE, Potions.POISON);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.POISON, Items.REDSTONE, Potions.LONG_POISON);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.POISON, Items.GLOWSTONE_DUST, Potions.STRONG_POISON);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.AWKWARD, Items.GHAST_TEAR, Potions.REGENERATION);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.REGENERATION, Items.REDSTONE, Potions.LONG_REGENERATION);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.REGENERATION, Items.GLOWSTONE_DUST, Potions.STRONG_REGENERATION);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.AWKWARD, Items.BLAZE_POWDER, Potions.STRENGTH);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.STRENGTH, Items.REDSTONE, Potions.LONG_STRENGTH);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.STRENGTH, Items.GLOWSTONE_DUST, Potions.STRONG_STRENGTH);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.WATER, Items.FERMENTED_SPIDER_EYE, Potions.WEAKNESS);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.WEAKNESS, Items.REDSTONE, Potions.LONG_WEAKNESS);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.AWKWARD, Items.PHANTOM_MEMBRANE, Potions.SLOW_FALLING);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.SLOW_FALLING, Items.REDSTONE, Potions.LONG_SLOW_FALLING);
    }

    private static void registerItemRecipe(Item arg, Item arg2, Item arg3) {
        if (!(arg instanceof PotionItem)) {
            throw new IllegalArgumentException("Expected a potion, got: " + Registry.ITEM.getId(arg));
        }
        if (!(arg3 instanceof PotionItem)) {
            throw new IllegalArgumentException("Expected a potion, got: " + Registry.ITEM.getId(arg3));
        }
        ITEM_RECIPES.add(new Recipe<Item>(arg, Ingredient.ofItems(arg2), arg3));
    }

    private static void registerPotionType(Item arg) {
        if (!(arg instanceof PotionItem)) {
            throw new IllegalArgumentException("Expected a potion, got: " + Registry.ITEM.getId(arg));
        }
        POTION_TYPES.add(Ingredient.ofItems(arg));
    }

    private static void registerPotionRecipe(Potion arg, Item arg2, Potion arg3) {
        POTION_RECIPES.add(new Recipe<Potion>(arg, Ingredient.ofItems(arg2), arg3));
    }

    static class Recipe<T> {
        private final T input;
        private final Ingredient ingredient;
        private final T output;

        public Recipe(T object, Ingredient arg, T object2) {
            this.input = object;
            this.ingredient = arg;
            this.output = object2;
        }
    }
}

