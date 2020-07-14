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

    public void trigger(ServerPlayerEntity player, Collection<? extends Entity> victims) {
        List list = victims.stream().map(arg2 -> EntityPredicate.createAdvancementEntityLootContext(player, arg2)).collect(Collectors.toList());
        this.test(player, arg -> arg.matches(list));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return this.conditionsFromJson(obj, playerPredicate, predicateDeserializer);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final EntityPredicate.Extended[] victims;

        public Conditions(EntityPredicate.Extended player, EntityPredicate.Extended[] victims) {
            super(ID, player);
            this.victims = victims;
        }

        public static Conditions create(EntityPredicate ... victims) {
            return new Conditions(EntityPredicate.Extended.EMPTY, (EntityPredicate.Extended[])Stream.of(victims).map(EntityPredicate.Extended::ofLegacy).toArray(EntityPredicate.Extended[]::new));
        }

        public boolean matches(Collection<? extends LootContext> victims) {
            for (EntityPredicate.Extended lv : this.victims) {
                boolean bl = false;
                for (LootContext lootContext : victims) {
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
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.add("victims", EntityPredicate.Extended.toPredicatesJsonArray(this.victims, predicateSerializer));
            return jsonObject;
        }
    }
}

