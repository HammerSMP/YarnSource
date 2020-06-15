/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 */
package net.minecraft;

import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class class_5409
extends AbstractCriterion<class_5410> {
    private static final Identifier field_25699 = new Identifier("player_interacted_with_entity");

    @Override
    public Identifier getId() {
        return field_25699;
    }

    @Override
    protected class_5410 conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        ItemPredicate lv = ItemPredicate.fromJson(jsonObject.get("item"));
        EntityPredicate.Extended lv2 = EntityPredicate.Extended.getInJson(jsonObject, "entity", arg2);
        return new class_5410(arg, lv, lv2);
    }

    public void method_30097(ServerPlayerEntity arg, ItemStack arg2, Entity arg32) {
        LootContext lv = EntityPredicate.createAdvancementEntityLootContext(arg, arg32);
        this.test(arg, arg3 -> arg3.method_30100(arg2, lv));
    }

    @Override
    protected /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        return this.conditionsFromJson(jsonObject, arg, arg2);
    }

    public static class class_5410
    extends AbstractCriterionConditions {
        private final ItemPredicate field_25700;
        private final EntityPredicate.Extended field_25701;

        public class_5410(EntityPredicate.Extended arg, ItemPredicate arg2, EntityPredicate.Extended arg3) {
            super(field_25699, arg);
            this.field_25700 = arg2;
            this.field_25701 = arg3;
        }

        public static class_5410 method_30099(EntityPredicate.Extended arg, ItemPredicate.Builder arg2, EntityPredicate.Extended arg3) {
            return new class_5410(arg, arg2.build(), arg3);
        }

        public boolean method_30100(ItemStack arg, LootContext arg2) {
            if (!this.field_25700.test(arg)) {
                return false;
            }
            return this.field_25701.test(arg2);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer arg) {
            JsonObject jsonObject = super.toJson(arg);
            jsonObject.add("item", this.field_25700.toJson());
            jsonObject.add("entity", this.field_25701.toJson(arg));
            return jsonObject;
        }
    }
}

