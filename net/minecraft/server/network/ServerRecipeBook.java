/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.network;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.packet.s2c.play.UnlockRecipesS2CPacket;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.recipe.book.RecipeBookOptions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerRecipeBook
extends RecipeBook {
    private static final Logger LOGGER = LogManager.getLogger();

    public int unlockRecipes(Collection<Recipe<?>> recipes, ServerPlayerEntity player) {
        ArrayList list = Lists.newArrayList();
        int i = 0;
        for (Recipe<?> lv : recipes) {
            Identifier lv2 = lv.getId();
            if (this.recipes.contains(lv2) || lv.isIgnoredInRecipeBook()) continue;
            this.add(lv2);
            this.display(lv2);
            list.add(lv2);
            Criteria.RECIPE_UNLOCKED.trigger(player, lv);
            ++i;
        }
        this.sendUnlockRecipesPacket(UnlockRecipesS2CPacket.Action.ADD, player, list);
        return i;
    }

    public int lockRecipes(Collection<Recipe<?>> recipes, ServerPlayerEntity player) {
        ArrayList list = Lists.newArrayList();
        int i = 0;
        for (Recipe<?> lv : recipes) {
            Identifier lv2 = lv.getId();
            if (!this.recipes.contains(lv2)) continue;
            this.remove(lv2);
            list.add(lv2);
            ++i;
        }
        this.sendUnlockRecipesPacket(UnlockRecipesS2CPacket.Action.REMOVE, player, list);
        return i;
    }

    private void sendUnlockRecipesPacket(UnlockRecipesS2CPacket.Action action, ServerPlayerEntity player, List<Identifier> recipeIds) {
        player.networkHandler.sendPacket(new UnlockRecipesS2CPacket(action, recipeIds, Collections.emptyList(), this.getOptions()));
    }

    public CompoundTag toTag() {
        CompoundTag lv = new CompoundTag();
        this.getOptions().toTag(lv);
        ListTag lv2 = new ListTag();
        for (Identifier lv3 : this.recipes) {
            lv2.add(StringTag.of(lv3.toString()));
        }
        lv.put("recipes", lv2);
        ListTag lv4 = new ListTag();
        for (Identifier lv5 : this.toBeDisplayed) {
            lv4.add(StringTag.of(lv5.toString()));
        }
        lv.put("toBeDisplayed", lv4);
        return lv;
    }

    public void fromTag(CompoundTag tag, RecipeManager arg2) {
        this.setOptions(RecipeBookOptions.fromTag(tag));
        ListTag lv = tag.getList("recipes", 8);
        this.handleList(lv, this::add, arg2);
        ListTag lv2 = tag.getList("toBeDisplayed", 8);
        this.handleList(lv2, this::display, arg2);
    }

    private void handleList(ListTag list, Consumer<Recipe<?>> handler, RecipeManager arg2) {
        for (int i = 0; i < list.size(); ++i) {
            String string = list.getString(i);
            try {
                Identifier lv = new Identifier(string);
                Optional<Recipe<?>> optional = arg2.get(lv);
                if (!optional.isPresent()) {
                    LOGGER.error("Tried to load unrecognized recipe: {} removed now.", (Object)lv);
                    continue;
                }
                handler.accept(optional.get());
                continue;
            }
            catch (InvalidIdentifierException lv2) {
                LOGGER.error("Tried to load improperly formatted recipe: {} removed now.", (Object)string);
            }
        }
    }

    public void sendInitRecipesPacket(ServerPlayerEntity player) {
        player.networkHandler.sendPacket(new UnlockRecipesS2CPacket(UnlockRecipesS2CPacket.Action.INIT, this.recipes, this.toBeDisplayed, this.getOptions()));
    }
}

