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
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class PlayerGeneratesContainerLootCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("player_generates_container_loot");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    protected Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        Identifier lv = new Identifier(JsonHelper.getString(jsonObject, "loot_table"));
        return new Conditions(arg, lv);
    }

    public void test(ServerPlayerEntity arg, Identifier arg22) {
        this.test(arg, (T arg2) -> arg2.test(arg22));
    }

    @Override
    protected /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        return this.conditionsFromJson(jsonObject, arg, arg2);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final Identifier lootTable;

        public Conditions(EntityPredicate.Extended arg, Identifier arg2) {
            super(ID, arg);
            this.lootTable = arg2;
        }

        public static Conditions create(Identifier arg) {
            return new Conditions(EntityPredicate.Extended.EMPTY, arg);
        }

        public boolean test(Identifier arg) {
            return this.lootTable.equals(arg);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer arg) {
            JsonObject jsonObject = super.toJson(arg);
            jsonObject.addProperty("loot_table", this.lootTable.toString());
            return jsonObject;
        }
    }
}

