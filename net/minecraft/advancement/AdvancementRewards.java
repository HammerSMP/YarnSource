/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  javax.annotation.Nullable
 */
package net.minecraft.advancement;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class AdvancementRewards {
    public static final AdvancementRewards NONE = new AdvancementRewards(0, new Identifier[0], new Identifier[0], CommandFunction.LazyContainer.EMPTY);
    private final int experience;
    private final Identifier[] loot;
    private final Identifier[] recipes;
    private final CommandFunction.LazyContainer function;

    public AdvancementRewards(int experience, Identifier[] loot, Identifier[] recipes, CommandFunction.LazyContainer function) {
        this.experience = experience;
        this.loot = loot;
        this.recipes = recipes;
        this.function = function;
    }

    public void apply(ServerPlayerEntity player) {
        player.addExperience(this.experience);
        LootContext lv = new LootContext.Builder(player.getServerWorld()).parameter(LootContextParameters.THIS_ENTITY, player).parameter(LootContextParameters.POSITION, player.getBlockPos()).random(player.getRandom()).build(LootContextTypes.ADVANCEMENT_REWARD);
        boolean bl = false;
        for (Identifier lv2 : this.loot) {
            for (ItemStack lv3 : player.server.getLootManager().getTable(lv2).generateLoot(lv)) {
                if (player.giveItemStack(lv3)) {
                    player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2f, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7f + 1.0f) * 2.0f);
                    bl = true;
                    continue;
                }
                ItemEntity lv4 = player.dropItem(lv3, false);
                if (lv4 == null) continue;
                lv4.resetPickupDelay();
                lv4.setOwner(player.getUuid());
            }
        }
        if (bl) {
            player.playerScreenHandler.sendContentUpdates();
        }
        if (this.recipes.length > 0) {
            player.unlockRecipes(this.recipes);
        }
        MinecraftServer minecraftServer = player.server;
        this.function.get(minecraftServer.getCommandFunctionManager()).ifPresent(arg2 -> minecraftServer.getCommandFunctionManager().execute((CommandFunction)arg2, player.getCommandSource().withSilent().withLevel(2)));
    }

    public String toString() {
        return "AdvancementRewards{experience=" + this.experience + ", loot=" + Arrays.toString(this.loot) + ", recipes=" + Arrays.toString(this.recipes) + ", function=" + this.function + '}';
    }

    public JsonElement toJson() {
        if (this == NONE) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        if (this.experience != 0) {
            jsonObject.addProperty("experience", (Number)this.experience);
        }
        if (this.loot.length > 0) {
            JsonArray jsonArray = new JsonArray();
            for (Identifier lv : this.loot) {
                jsonArray.add(lv.toString());
            }
            jsonObject.add("loot", (JsonElement)jsonArray);
        }
        if (this.recipes.length > 0) {
            JsonArray jsonArray2 = new JsonArray();
            for (Identifier lv2 : this.recipes) {
                jsonArray2.add(lv2.toString());
            }
            jsonObject.add("recipes", (JsonElement)jsonArray2);
        }
        if (this.function.getId() != null) {
            jsonObject.addProperty("function", this.function.getId().toString());
        }
        return jsonObject;
    }

    public static AdvancementRewards fromJson(JsonObject json) throws JsonParseException {
        CommandFunction.LazyContainer lv2;
        int i = JsonHelper.getInt(json, "experience", 0);
        JsonArray jsonArray = JsonHelper.getArray(json, "loot", new JsonArray());
        Identifier[] lvs = new Identifier[jsonArray.size()];
        for (int j = 0; j < lvs.length; ++j) {
            lvs[j] = new Identifier(JsonHelper.asString(jsonArray.get(j), "loot[" + j + "]"));
        }
        JsonArray jsonArray2 = JsonHelper.getArray(json, "recipes", new JsonArray());
        Identifier[] lvs2 = new Identifier[jsonArray2.size()];
        for (int k = 0; k < lvs2.length; ++k) {
            lvs2[k] = new Identifier(JsonHelper.asString(jsonArray2.get(k), "recipes[" + k + "]"));
        }
        if (json.has("function")) {
            CommandFunction.LazyContainer lv = new CommandFunction.LazyContainer(new Identifier(JsonHelper.getString(json, "function")));
        } else {
            lv2 = CommandFunction.LazyContainer.EMPTY;
        }
        return new AdvancementRewards(i, lvs, lvs2, lv2);
    }

    public static class Builder {
        private int experience;
        private final List<Identifier> loot = Lists.newArrayList();
        private final List<Identifier> recipes = Lists.newArrayList();
        @Nullable
        private Identifier function;

        public static Builder experience(int experience) {
            return new Builder().setExperience(experience);
        }

        public Builder setExperience(int experience) {
            this.experience += experience;
            return this;
        }

        public static Builder recipe(Identifier recipe) {
            return new Builder().addRecipe(recipe);
        }

        public Builder addRecipe(Identifier recipe) {
            this.recipes.add(recipe);
            return this;
        }

        public AdvancementRewards build() {
            return new AdvancementRewards(this.experience, this.loot.toArray(new Identifier[0]), this.recipes.toArray(new Identifier[0]), this.function == null ? CommandFunction.LazyContainer.EMPTY : new CommandFunction.LazyContainer(this.function));
        }
    }
}

