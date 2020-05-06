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
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class CuredZombieVillagerCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("cured_zombie_villager");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        EntityPredicate.Extended lv = EntityPredicate.Extended.getInJson(jsonObject, "zombie", arg2);
        EntityPredicate.Extended lv2 = EntityPredicate.Extended.getInJson(jsonObject, "villager", arg2);
        return new Conditions(arg, lv, lv2);
    }

    public void trigger(ServerPlayerEntity arg, ZombieEntity arg2, VillagerEntity arg32) {
        LootContext lv = EntityPredicate.createAdvancementEntityLootContext(arg, arg2);
        LootContext lv2 = EntityPredicate.createAdvancementEntityLootContext(arg, arg32);
        this.test(arg, arg3 -> arg3.matches(lv, lv2));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        return this.conditionsFromJson(jsonObject, arg, arg2);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final EntityPredicate.Extended zombie;
        private final EntityPredicate.Extended villager;

        public Conditions(EntityPredicate.Extended arg, EntityPredicate.Extended arg2, EntityPredicate.Extended arg3) {
            super(ID, arg);
            this.zombie = arg2;
            this.villager = arg3;
        }

        public static Conditions any() {
            return new Conditions(EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.EMPTY);
        }

        public boolean matches(LootContext arg, LootContext arg2) {
            if (!this.zombie.test(arg)) {
                return false;
            }
            return this.villager.test(arg2);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer arg) {
            JsonObject jsonObject = super.toJson(arg);
            jsonObject.add("zombie", this.zombie.toJson(arg));
            jsonObject.add("villager", this.villager.toJson(arg));
            return jsonObject;
        }
    }
}

