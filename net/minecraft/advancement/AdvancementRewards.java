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

    public AdvancementRewards(int i, Identifier[] args, Identifier[] args2, CommandFunction.LazyContainer arg) {
        this.experience = i;
        this.loot = args;
        this.recipes = args2;
        this.function = arg;
    }

    public void apply(ServerPlayerEntity arg) {
        arg.addExperience(this.experience);
        LootContext lv = new LootContext.Builder(arg.getServerWorld()).put(LootContextParameters.THIS_ENTITY, arg).put(LootContextParameters.POSITION, arg.getBlockPos()).setRandom(arg.getRandom()).build(LootContextTypes.ADVANCEMENT_REWARD);
        boolean bl = false;
        for (Identifier lv2 : this.loot) {
            for (ItemStack lv3 : arg.server.getLootManager().getTable(lv2).getDrops(lv)) {
                if (arg.giveItemStack(lv3)) {
                    arg.world.playSound(null, arg.getX(), arg.getY(), arg.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2f, ((arg.getRandom().nextFloat() - arg.getRandom().nextFloat()) * 0.7f + 1.0f) * 2.0f);
                    bl = true;
                    continue;
                }
                ItemEntity lv4 = arg.dropItem(lv3, false);
                if (lv4 == null) continue;
                lv4.resetPickupDelay();
                lv4.setOwner(arg.getUuid());
            }
        }
        if (bl) {
            arg.playerScreenHandler.sendContentUpdates();
        }
        if (this.recipes.length > 0) {
            arg.unlockRecipes(this.recipes);
        }
        MinecraftServer minecraftServer = arg.server;
        this.function.get(minecraftServer.getCommandFunctionManager()).ifPresent(arg2 -> minecraftServer.getCommandFunctionManager().execute((CommandFunction)arg2, arg.getCommandSource().withSilent().withLevel(2)));
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

    public static AdvancementRewards fromJson(JsonObject jsonObject) throws JsonParseException {
        CommandFunction.LazyContainer lv2;
        int i = JsonHelper.getInt(jsonObject, "experience", 0);
        JsonArray jsonArray = JsonHelper.getArray(jsonObject, "loot", new JsonArray());
        Identifier[] lvs = new Identifier[jsonArray.size()];
        for (int j = 0; j < lvs.length; ++j) {
            lvs[j] = new Identifier(JsonHelper.asString(jsonArray.get(j), "loot[" + j + "]"));
        }
        JsonArray jsonArray2 = JsonHelper.getArray(jsonObject, "recipes", new JsonArray());
        Identifier[] lvs2 = new Identifier[jsonArray2.size()];
        for (int k = 0; k < lvs2.length; ++k) {
            lvs2[k] = new Identifier(JsonHelper.asString(jsonArray2.get(k), "recipes[" + k + "]"));
        }
        if (jsonObject.has("function")) {
            CommandFunction.LazyContainer lv = new CommandFunction.LazyContainer(new Identifier(JsonHelper.getString(jsonObject, "function")));
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

        public static Builder experience(int i) {
            return new Builder().setExperience(i);
        }

        public Builder setExperience(int i) {
            this.experience += i;
            return this;
        }

        public static Builder recipe(Identifier arg) {
            return new Builder().addRecipe(arg);
        }

        public Builder addRecipe(Identifier arg) {
            this.recipes.add(arg);
            return this;
        }

        public AdvancementRewards build() {
            return new AdvancementRewards(this.experience, this.loot.toArray(new Identifier[0]), this.recipes.toArray(new Identifier[0]), this.function == null ? CommandFunction.LazyContainer.EMPTY : new CommandFunction.LazyContainer(this.function));
        }
    }
}

