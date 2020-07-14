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

    public void trigger(ServerPlayerEntity player, ZombieEntity zombie, VillagerEntity villager) {
        LootContext lv = EntityPredicate.createAdvancementEntityLootContext(player, zombie);
        LootContext lv2 = EntityPredicate.createAdvancementEntityLootContext(player, villager);
        this.test(player, arg3 -> arg3.matches(lv, lv2));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return this.conditionsFromJson(obj, playerPredicate, predicateDeserializer);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final EntityPredicate.Extended zombie;
        private final EntityPredicate.Extended villager;

        public Conditions(EntityPredicate.Extended player, EntityPredicate.Extended zombie, EntityPredicate.Extended villager) {
            super(ID, player);
            this.zombie = zombie;
            this.villager = villager;
        }

        public static Conditions any() {
            return new Conditions(EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.EMPTY);
        }

        public boolean matches(LootContext zombieContext, LootContext villagerContext) {
            if (!this.zombie.test(zombieContext)) {
                return false;
            }
            return this.villager.test(villagerContext);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.add("zombie", this.zombie.toJson(predicateSerializer));
            jsonObject.add("villager", this.villager.toJson(predicateSerializer));
            return jsonObject;
        }
    }
}

