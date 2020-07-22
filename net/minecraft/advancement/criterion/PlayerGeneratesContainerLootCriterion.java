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

    public void test(ServerPlayerEntity player, Identifier id) {
        this.test(player, (T arg2) -> arg2.test(id));
    }

    @Override
    protected /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return this.conditionsFromJson(obj, playerPredicate, predicateDeserializer);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final Identifier lootTable;

        public Conditions(EntityPredicate.Extended entity, Identifier lootTable) {
            super(ID, entity);
            this.lootTable = lootTable;
        }

        public static Conditions create(Identifier lootTable) {
            return new Conditions(EntityPredicate.Extended.EMPTY, lootTable);
        }

        public boolean test(Identifier lootTable) {
            return this.lootTable.equals(lootTable);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.addProperty("loot_table", this.lootTable.toString());
            return jsonObject;
        }
    }
}

