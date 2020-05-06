/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class EnchantedItemCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("enchanted_item");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        ItemPredicate lv = ItemPredicate.fromJson(jsonObject.get("item"));
        NumberRange.IntRange lv2 = NumberRange.IntRange.fromJson(jsonObject.get("levels"));
        return new Conditions(arg, lv, lv2);
    }

    public void trigger(ServerPlayerEntity arg, ItemStack arg22, int i) {
        this.test(arg, arg2 -> arg2.matches(arg22, i));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        return this.conditionsFromJson(jsonObject, arg, arg2);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final ItemPredicate item;
        private final NumberRange.IntRange levels;

        public Conditions(EntityPredicate.Extended arg, ItemPredicate arg2, NumberRange.IntRange arg3) {
            super(ID, arg);
            this.item = arg2;
            this.levels = arg3;
        }

        public static Conditions any() {
            return new Conditions(EntityPredicate.Extended.EMPTY, ItemPredicate.ANY, NumberRange.IntRange.ANY);
        }

        public boolean matches(ItemStack arg, int i) {
            if (!this.item.test(arg)) {
                return false;
            }
            return this.levels.test(i);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer arg) {
            JsonObject jsonObject = super.toJson(arg);
            jsonObject.add("item", this.item.toJson());
            jsonObject.add("levels", this.levels.toJson());
            return jsonObject;
        }
    }
}

