/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Optional;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class KnowledgeBookItem
extends Item {
    private static final Logger LOGGER = LogManager.getLogger();

    public KnowledgeBookItem(Item.Settings arg) {
        super(arg);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack lv = user.getStackInHand(hand);
        CompoundTag lv2 = lv.getTag();
        if (!user.abilities.creativeMode) {
            user.setStackInHand(hand, ItemStack.EMPTY);
        }
        if (lv2 == null || !lv2.contains("Recipes", 9)) {
            LOGGER.error("Tag not valid: {}", (Object)lv2);
            return TypedActionResult.fail(lv);
        }
        if (!world.isClient) {
            ListTag lv3 = lv2.getList("Recipes", 8);
            ArrayList list = Lists.newArrayList();
            RecipeManager lv4 = world.getServer().getRecipeManager();
            for (int i = 0; i < lv3.size(); ++i) {
                String string = lv3.getString(i);
                Optional<Recipe<?>> optional = lv4.get(new Identifier(string));
                if (!optional.isPresent()) {
                    LOGGER.error("Invalid recipe: {}", (Object)string);
                    return TypedActionResult.fail(lv);
                }
                list.add(optional.get());
            }
            user.unlockRecipes(list);
            user.incrementStat(Stats.USED.getOrCreateStat(this));
        }
        return TypedActionResult.method_29237(lv, world.isClient());
    }
}

