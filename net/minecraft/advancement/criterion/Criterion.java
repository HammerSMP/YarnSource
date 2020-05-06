/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.util.Identifier;

public interface Criterion<T extends CriterionConditions> {
    public Identifier getId();

    public void beginTrackingCondition(PlayerAdvancementTracker var1, ConditionsContainer<T> var2);

    public void endTrackingCondition(PlayerAdvancementTracker var1, ConditionsContainer<T> var2);

    public void endTracking(PlayerAdvancementTracker var1);

    public T conditionsFromJson(JsonObject var1, AdvancementEntityPredicateDeserializer var2);

    public static class ConditionsContainer<T extends CriterionConditions> {
        private final T conditions;
        private final Advancement advancement;
        private final String id;

        public ConditionsContainer(T arg, Advancement arg2, String string) {
            this.conditions = arg;
            this.advancement = arg2;
            this.id = string;
        }

        public T getConditions() {
            return this.conditions;
        }

        public void grant(PlayerAdvancementTracker arg) {
            arg.grantCriterion(this.advancement, this.id);
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            ConditionsContainer lv = (ConditionsContainer)object;
            if (!this.conditions.equals(lv.conditions)) {
                return false;
            }
            if (!this.advancement.equals(lv.advancement)) {
                return false;
            }
            return this.id.equals(lv.id);
        }

        public int hashCode() {
            int i = this.conditions.hashCode();
            i = 31 * i + this.advancement.hashCode();
            i = 31 * i + this.id.hashCode();
            return i;
        }
    }
}

