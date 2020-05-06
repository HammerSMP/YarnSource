/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class InventoryChangedCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("inventory_changed");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "slots", new JsonObject());
        NumberRange.IntRange lv = NumberRange.IntRange.fromJson(jsonObject2.get("occupied"));
        NumberRange.IntRange lv2 = NumberRange.IntRange.fromJson(jsonObject2.get("full"));
        NumberRange.IntRange lv3 = NumberRange.IntRange.fromJson(jsonObject2.get("empty"));
        ItemPredicate[] lvs = ItemPredicate.deserializeAll(jsonObject.get("items"));
        return new Conditions(arg, lv, lv2, lv3, lvs);
    }

    public void trigger(ServerPlayerEntity arg, PlayerInventory arg2, ItemStack arg3) {
        int i = 0;
        int j = 0;
        int k = 0;
        for (int l = 0; l < arg2.size(); ++l) {
            ItemStack lv = arg2.getStack(l);
            if (lv.isEmpty()) {
                ++j;
                continue;
            }
            ++k;
            if (lv.getCount() < lv.getMaxCount()) continue;
            ++i;
        }
        this.trigger(arg, arg2, arg3, i, j, k);
    }

    private void trigger(ServerPlayerEntity arg, PlayerInventory arg2, ItemStack arg32, int i, int j, int k) {
        this.test(arg, arg3 -> arg3.matches(arg2, arg32, i, j, k));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        return this.conditionsFromJson(jsonObject, arg, arg2);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final NumberRange.IntRange occupied;
        private final NumberRange.IntRange full;
        private final NumberRange.IntRange empty;
        private final ItemPredicate[] items;

        public Conditions(EntityPredicate.Extended arg, NumberRange.IntRange arg2, NumberRange.IntRange arg3, NumberRange.IntRange arg4, ItemPredicate[] args) {
            super(ID, arg);
            this.occupied = arg2;
            this.full = arg3;
            this.empty = arg4;
            this.items = args;
        }

        public static Conditions items(ItemPredicate ... args) {
            return new Conditions(EntityPredicate.Extended.EMPTY, NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, args);
        }

        public static Conditions items(ItemConvertible ... args) {
            ItemPredicate[] lvs = new ItemPredicate[args.length];
            for (int i = 0; i < args.length; ++i) {
                lvs[i] = new ItemPredicate(null, args[i].asItem(), NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, EnchantmentPredicate.ARRAY_OF_ANY, EnchantmentPredicate.ARRAY_OF_ANY, null, NbtPredicate.ANY);
            }
            return Conditions.items(lvs);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer arg) {
            JsonObject jsonObject = super.toJson(arg);
            if (!(this.occupied.isDummy() && this.full.isDummy() && this.empty.isDummy())) {
                JsonObject jsonObject2 = new JsonObject();
                jsonObject2.add("occupied", this.occupied.toJson());
                jsonObject2.add("full", this.full.toJson());
                jsonObject2.add("empty", this.empty.toJson());
                jsonObject.add("slots", (JsonElement)jsonObject2);
            }
            if (this.items.length > 0) {
                JsonArray jsonArray = new JsonArray();
                for (ItemPredicate lv : this.items) {
                    jsonArray.add(lv.toJson());
                }
                jsonObject.add("items", (JsonElement)jsonArray);
            }
            return jsonObject;
        }

        public boolean matches(PlayerInventory arg, ItemStack arg22, int i, int j, int k) {
            if (!this.full.test(i)) {
                return false;
            }
            if (!this.empty.test(j)) {
                return false;
            }
            if (!this.occupied.test(k)) {
                return false;
            }
            int l = this.items.length;
            if (l == 0) {
                return true;
            }
            if (l == 1) {
                return !arg22.isEmpty() && this.items[0].test(arg22);
            }
            ObjectArrayList list = new ObjectArrayList((Object[])this.items);
            int m = arg.size();
            for (int n = 0; n < m; ++n) {
                if (list.isEmpty()) {
                    return true;
                }
                ItemStack lv = arg.getStack(n);
                if (lv.isEmpty()) continue;
                list.removeIf(arg2 -> arg2.test(lv));
            }
            return list.isEmpty();
        }
    }
}

