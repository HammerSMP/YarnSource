/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  javax.annotation.Nullable
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class BredAnimalsCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("bred_animals");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        EntityPredicate.Extended lv = EntityPredicate.Extended.getInJson(jsonObject, "parent", arg2);
        EntityPredicate.Extended lv2 = EntityPredicate.Extended.getInJson(jsonObject, "partner", arg2);
        EntityPredicate.Extended lv3 = EntityPredicate.Extended.getInJson(jsonObject, "child", arg2);
        return new Conditions(arg, lv, lv2, lv3);
    }

    public void trigger(ServerPlayerEntity arg, AnimalEntity arg2, AnimalEntity arg3, @Nullable PassiveEntity arg42) {
        LootContext lv = EntityPredicate.createAdvancementEntityLootContext(arg, arg2);
        LootContext lv2 = EntityPredicate.createAdvancementEntityLootContext(arg, arg3);
        LootContext lv3 = arg42 != null ? EntityPredicate.createAdvancementEntityLootContext(arg, arg42) : null;
        this.test(arg, arg4 -> arg4.matches(lv, lv2, lv3));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        return this.conditionsFromJson(jsonObject, arg, arg2);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final EntityPredicate.Extended parent;
        private final EntityPredicate.Extended partner;
        private final EntityPredicate.Extended child;

        public Conditions(EntityPredicate.Extended arg, EntityPredicate.Extended arg2, EntityPredicate.Extended arg3, EntityPredicate.Extended arg4) {
            super(ID, arg);
            this.parent = arg2;
            this.partner = arg3;
            this.child = arg4;
        }

        public static Conditions any() {
            return new Conditions(EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.EMPTY);
        }

        public static Conditions create(EntityPredicate.Builder arg) {
            return new Conditions(EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.ofLegacy(arg.build()));
        }

        public static Conditions method_29918(EntityPredicate arg, EntityPredicate arg2, EntityPredicate arg3) {
            return new Conditions(EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.ofLegacy(arg), EntityPredicate.Extended.ofLegacy(arg2), EntityPredicate.Extended.ofLegacy(arg3));
        }

        public boolean matches(LootContext arg, LootContext arg2, @Nullable LootContext arg3) {
            if (!(this.child == EntityPredicate.Extended.EMPTY || arg3 != null && this.child.test(arg3))) {
                return false;
            }
            return this.parent.test(arg) && this.partner.test(arg2) || this.parent.test(arg2) && this.partner.test(arg);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer arg) {
            JsonObject jsonObject = super.toJson(arg);
            jsonObject.add("parent", this.parent.toJson(arg));
            jsonObject.add("partner", this.partner.toJson(arg));
            jsonObject.add("child", this.child.toJson(arg));
            return jsonObject;
        }
    }
}

