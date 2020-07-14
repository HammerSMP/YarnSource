/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonObject
 */
package net.minecraft.advancement.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.entity.Entity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class KilledByCrossbowCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("killed_by_crossbow");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        EntityPredicate.Extended[] lvs = EntityPredicate.Extended.requireInJson(jsonObject, "victims", arg2);
        NumberRange.IntRange lv = NumberRange.IntRange.fromJson(jsonObject.get("unique_entity_types"));
        return new Conditions(arg, lvs, lv);
    }

    public void trigger(ServerPlayerEntity player, Collection<Entity> piercingKilledEntities) {
        ArrayList list = Lists.newArrayList();
        HashSet set = Sets.newHashSet();
        for (Entity lv : piercingKilledEntities) {
            set.add(lv.getType());
            list.add(EntityPredicate.createAdvancementEntityLootContext(player, lv));
        }
        this.test(player, arg -> arg.matches(list, set.size()));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return this.conditionsFromJson(obj, playerPredicate, predicateDeserializer);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final EntityPredicate.Extended[] victims;
        private final NumberRange.IntRange uniqueEntityTypes;

        public Conditions(EntityPredicate.Extended player, EntityPredicate.Extended[] victims, NumberRange.IntRange uniqueEntityTypes) {
            super(ID, player);
            this.victims = victims;
            this.uniqueEntityTypes = uniqueEntityTypes;
        }

        public static Conditions create(EntityPredicate.Builder ... victimPredicates) {
            EntityPredicate.Extended[] lvs = new EntityPredicate.Extended[victimPredicates.length];
            for (int i = 0; i < victimPredicates.length; ++i) {
                EntityPredicate.Builder lv = victimPredicates[i];
                lvs[i] = EntityPredicate.Extended.ofLegacy(lv.build());
            }
            return new Conditions(EntityPredicate.Extended.EMPTY, lvs, NumberRange.IntRange.ANY);
        }

        public static Conditions create(NumberRange.IntRange uniqueEntityTypes) {
            EntityPredicate.Extended[] lvs = new EntityPredicate.Extended[]{};
            return new Conditions(EntityPredicate.Extended.EMPTY, lvs, uniqueEntityTypes);
        }

        public boolean matches(Collection<LootContext> victimContexts, int uniqueEntityTypeCount) {
            if (this.victims.length > 0) {
                ArrayList list = Lists.newArrayList(victimContexts);
                for (EntityPredicate.Extended lv : this.victims) {
                    boolean bl = false;
                    Iterator iterator = list.iterator();
                    while (iterator.hasNext()) {
                        LootContext lv2 = (LootContext)iterator.next();
                        if (!lv.test(lv2)) continue;
                        iterator.remove();
                        bl = true;
                        break;
                    }
                    if (bl) continue;
                    return false;
                }
            }
            return this.uniqueEntityTypes.test(uniqueEntityTypeCount);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.add("victims", EntityPredicate.Extended.toPredicatesJsonArray(this.victims, predicateSerializer));
            jsonObject.add("unique_entity_types", this.uniqueEntityTypes.toJson());
            return jsonObject;
        }
    }
}

