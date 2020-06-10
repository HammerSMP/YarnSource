/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonObject
 */
package net.minecraft.advancement.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class AbstractCriterion<T extends AbstractCriterionConditions>
implements Criterion<T> {
    private final Map<PlayerAdvancementTracker, Set<Criterion.ConditionsContainer<T>>> progressions = Maps.newIdentityHashMap();

    @Override
    public final void beginTrackingCondition(PlayerAdvancementTracker arg2, Criterion.ConditionsContainer<T> arg22) {
        this.progressions.computeIfAbsent(arg2, arg -> Sets.newHashSet()).add(arg22);
    }

    @Override
    public final void endTrackingCondition(PlayerAdvancementTracker arg, Criterion.ConditionsContainer<T> arg2) {
        Set<Criterion.ConditionsContainer<T>> set = this.progressions.get(arg);
        if (set != null) {
            set.remove(arg2);
            if (set.isEmpty()) {
                this.progressions.remove(arg);
            }
        }
    }

    @Override
    public final void endTracking(PlayerAdvancementTracker arg) {
        this.progressions.remove(arg);
    }

    protected abstract T conditionsFromJson(JsonObject var1, EntityPredicate.Extended var2, AdvancementEntityPredicateDeserializer var3);

    @Override
    public final T conditionsFromJson(JsonObject jsonObject, AdvancementEntityPredicateDeserializer arg) {
        EntityPredicate.Extended lv = EntityPredicate.Extended.getInJson(jsonObject, "player", arg);
        return this.conditionsFromJson(jsonObject, lv, arg);
    }

    protected void test(ServerPlayerEntity arg, Predicate<T> predicate) {
        PlayerAdvancementTracker lv = arg.getAdvancementTracker();
        Set<Criterion.ConditionsContainer<T>> set = this.progressions.get(lv);
        if (set == null || set.isEmpty()) {
            return;
        }
        LootContext lv2 = EntityPredicate.createAdvancementEntityLootContext(arg, arg);
        List list = null;
        for (Criterion.ConditionsContainer<T> lv3 : set) {
            AbstractCriterionConditions lv4 = (AbstractCriterionConditions)lv3.getConditions();
            if (!lv4.getPlayerPredicate().test(lv2) || !predicate.test(lv4)) continue;
            if (list == null) {
                list = Lists.newArrayList();
            }
            list.add(lv3);
        }
        if (list != null) {
            for (Criterion.ConditionsContainer<Object> lv5 : list) {
                lv5.grant(lv);
            }
        }
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject jsonObject, AdvancementEntityPredicateDeserializer arg) {
        return this.conditionsFromJson(jsonObject, arg);
    }
}

