/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.entity.Entity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ChanneledLightningCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("channeled_lightning");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        EntityPredicate.Extended[] lvs = EntityPredicate.Extended.requireInJson(jsonObject, "victims", arg2);
        return new Conditions(arg, lvs);
    }

    public void trigger(ServerPlayerEntity arg3, Collection<? extends Entity> collection) {
        List list = collection.stream().map(arg2 -> EntityPredicate.createAdvancementEntityLootContext(arg3, arg2)).collect(Collectors.toList());
        this.test(arg3, arg -> arg.matches(list));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        return this.conditionsFromJson(jsonObject, arg, arg2);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final EntityPredicate.Extended[] victims;

        public Conditions(EntityPredicate.Extended arg, EntityPredicate.Extended[] args) {
            super(ID, arg);
            this.victims = args;
        }

        public static Conditions create(EntityPredicate ... args) {
            return new Conditions(EntityPredicate.Extended.EMPTY, (EntityPredicate.Extended[])Stream.of(args).map(EntityPredicate.Extended::ofLegacy).toArray(EntityPredicate.Extended[]::new));
        }

        public boolean matches(Collection<? extends LootContext> collection) {
            for (EntityPredicate.Extended lv : this.victims) {
                boolean bl = false;
                for (LootContext lootContext : collection) {
                    if (!lv.test(lootContext)) continue;
                    bl = true;
                    break;
                }
                if (bl) continue;
                return false;
            }
            return true;
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer arg) {
            JsonObject jsonObject = super.toJson(arg);
            jsonObject.add("victims", EntityPredicate.Extended.toPredicatesJsonArray(this.victims, arg));
            return jsonObject;
        }
    }
}

