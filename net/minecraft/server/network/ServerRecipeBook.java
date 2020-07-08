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

    public int unlockRecipes(Collection<Recipe<?>> collection, ServerPlayerEntity arg) {
        ArrayList list = Lists.newArrayList();
        int i = 0;
        for (Recipe<?> lv : collection) {
            Identifier lv2 = lv.getId();
            if (this.recipes.contains(lv2) || lv.isIgnoredInRecipeBook()) continue;
            this.add(lv2);
            this.display(lv2);
            list.add(lv2);
            Criteria.RECIPE_UNLOCKED.trigger(arg, lv);
            ++i;
        }
        this.sendUnlockRecipesPacket(UnlockRecipesS2CPacket.Action.ADD, arg, list);
        return i;
    }

    public int lockRecipes(Collection<Recipe<?>> collection, ServerPlayerEntity arg) {
        ArrayList list = Lists.newArrayList();
        int i = 0;
        for (Recipe<?> lv : collection) {
            Identifier lv2 = lv.getId();
            if (!this.recipes.contains(lv2)) continue;
            this.remove(lv2);
            list.add(lv2);
            ++i;
        }
        this.sendUnlockRecipesPacket(UnlockRecipesS2CPacket.Action.REMOVE, arg, list);
        return i;
    }

    private void sendUnlockRecipesPacket(UnlockRecipesS2CPacket.Action arg, ServerPlayerEntity arg2, List<Identifier> list) {
        arg2.networkHandler.sendPacket(new UnlockRecipesS2CPacket(arg, list, Collections.emptyList(), this.getOptions()));
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

    public void fromTag(CompoundTag arg, RecipeManager arg2) {
        this.setOptions(RecipeBookOptions.fromTag(arg));
        ListTag lv = arg.getList("recipes", 8);
        this.handleList(lv, this::add, arg2);
        ListTag lv2 = arg.getList("toBeDisplayed", 8);
        this.handleList(lv2, this::display, arg2);
    }

    private void handleList(ListTag arg, Consumer<Recipe<?>> consumer, RecipeManager arg2) {
        for (int i = 0; i < arg.size(); ++i) {
            String string = arg.getString(i);
            try {
                Identifier lv = new Identifier(string);
                Optional<Recipe<?>> optional = arg2.get(lv);
                if (!optional.isPresent()) {
                    LOGGER.error("Tried to load unrecognized recipe: {} removed now.", (Object)lv);
                    continue;
                }
                consumer.accept(optional.get());
                continue;
            }
            catch (InvalidIdentifierException lv2) {
                LOGGER.error("Tried to load improperly formatted recipe: {} removed now.", (Object)string);
            }
        }
    }

    public void sendInitRecipesPacket(ServerPlayerEntity arg) {
        arg.networkHandler.sendPacket(new UnlockRecipesS2CPacket(UnlockRecipesS2CPacket.Action.INIT, this.recipes, this.toBeDisplayed, this.getOptions()));
    }
}

