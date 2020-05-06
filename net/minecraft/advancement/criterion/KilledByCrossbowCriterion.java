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

    public void trigger(ServerPlayerEntity arg2, Collection<Entity> collection) {
        ArrayList list = Lists.newArrayList();
        HashSet set = Sets.newHashSet();
        for (Entity lv : collection) {
            set.add(lv.getType());
            list.add(EntityPredicate.createAdvancementEntityLootContext(arg2, lv));
        }
        this.test(arg2, arg -> arg.matches(list, set.size()));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        return this.conditionsFromJson(jsonObject, arg, arg2);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final EntityPredicate.Extended[] victims;
        private final NumberRange.IntRange uniqueEntityTypes;

        public Conditions(EntityPredicate.Extended arg, EntityPredicate.Extended[] args, NumberRange.IntRange arg2) {
            super(ID, arg);
            this.victims = args;
            this.uniqueEntityTypes = arg2;
        }

        public static Conditions create(EntityPredicate.Builder ... args) {
            EntityPredicate.Extended[] lvs = new EntityPredicate.Extended[args.length];
            for (int i = 0; i < args.length; ++i) {
                EntityPredicate.Builder lv = args[i];
                lvs[i] = EntityPredicate.Extended.ofLegacy(lv.build());
            }
            return new Conditions(EntityPredicate.Extended.EMPTY, lvs, NumberRange.IntRange.ANY);
        }

        public static Conditions create(NumberRange.IntRange arg) {
            EntityPredicate.Extended[] lvs = new EntityPredicate.Extended[]{};
            return new Conditions(EntityPredicate.Extended.EMPTY, lvs, arg);
        }

        public boolean matches(Collection<LootContext> collection, int i) {
            if (this.victims.length > 0) {
                ArrayList list = Lists.newArrayList(collection);
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
            return this.uniqueEntityTypes.test(i);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer arg) {
            JsonObject jsonObject = super.toJson(arg);
            jsonObject.add("victims", EntityPredicate.Extended.toPredicatesJsonArray(this.victims, arg));
            jsonObject.add("unique_entity_types", this.uniqueEntityTypes.toJson());
            return jsonObject;
        }
    }
}

