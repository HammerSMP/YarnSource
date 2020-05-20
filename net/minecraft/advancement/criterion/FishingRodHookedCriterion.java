/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import java.util.Collection;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class FishingRodHookedCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("fishing_rod_hooked");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        ItemPredicate lv = ItemPredicate.fromJson(jsonObject.get("rod"));
        EntityPredicate.Extended lv2 = EntityPredicate.Extended.getInJson(jsonObject, "entity", arg2);
        ItemPredicate lv3 = ItemPredicate.fromJson(jsonObject.get("item"));
        return new Conditions(arg, lv, lv2, lv3);
    }

    public void trigger(ServerPlayerEntity arg, ItemStack arg2, FishingBobberEntity arg32, Collection<ItemStack> collection) {
        LootContext lv = EntityPredicate.createAdvancementEntityLootContext(arg, arg32.getHookedEntity() != null ? arg32.getHookedEntity() : arg32);
        this.test(arg, arg3 -> arg3.test(arg2, lv, collection));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        return this.conditionsFromJson(jsonObject, arg, arg2);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final ItemPredicate rod;
        private final EntityPredicate.Extended hookedEntity;
        private final ItemPredicate caughtItem;

        public Conditions(EntityPredicate.Extended arg, ItemPredicate arg2, EntityPredicate.Extended arg3, ItemPredicate arg4) {
            super(ID, arg);
            this.rod = arg2;
            this.hookedEntity = arg3;
            this.caughtItem = arg4;
        }

        public static Conditions create(ItemPredicate arg, EntityPredicate arg2, ItemPredicate arg3) {
            return new Conditions(EntityPredicate.Extended.EMPTY, arg, EntityPredicate.Extended.ofLegacy(arg2), arg3);
        }

        public boolean test(ItemStack arg, LootContext arg2, Collection<ItemStack> collection) {
            if (!this.rod.test(arg)) {
                return false;
            }
            if (!this.hookedEntity.test(arg2)) {
                return false;
            }
            if (this.caughtItem != ItemPredicate.ANY) {
                ItemEntity lv2;
                boolean bl = false;
                Entity lv = arg2.get(LootContextParameters.THIS_ENTITY);
                if (lv instanceof ItemEntity && this.caughtItem.test((lv2 = (ItemEntity)lv).getStack())) {
                    bl = true;
                }
                for (ItemStack lv3 : collection) {
                    if (!this.caughtItem.test(lv3)) continue;
                    bl = true;
                    break;
                }
                if (!bl) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer arg) {
            JsonObject jsonObject = super.toJson(arg);
            jsonObject.add("rod", this.rod.toJson());
            jsonObject.add("entity", this.hookedEntity.toJson(arg));
            jsonObject.add("item", this.caughtItem.toJson());
            return jsonObject;
        }
    }
}

