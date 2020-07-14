/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  javax.annotation.Nullable
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class EnterBlockCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("enter_block");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        Block lv = EnterBlockCriterion.getBlock(jsonObject);
        StatePredicate lv2 = StatePredicate.fromJson(jsonObject.get("state"));
        if (lv != null) {
            lv2.check(lv.getStateManager(), name -> {
                throw new JsonSyntaxException("Block " + lv + " has no property " + name);
            });
        }
        return new Conditions(arg, lv, lv2);
    }

    @Nullable
    private static Block getBlock(JsonObject obj) {
        if (obj.has("block")) {
            Identifier lv = new Identifier(JsonHelper.getString(obj, "block"));
            return Registry.BLOCK.getOrEmpty(lv).orElseThrow(() -> new JsonSyntaxException("Unknown block type '" + lv + "'"));
        }
        return null;
    }

    public void trigger(ServerPlayerEntity player, BlockState state) {
        this.test(player, arg2 -> arg2.matches(state));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return this.conditionsFromJson(obj, playerPredicate, predicateDeserializer);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final Block block;
        private final StatePredicate state;

        public Conditions(EntityPredicate.Extended player, @Nullable Block block, StatePredicate state) {
            super(ID, player);
            this.block = block;
            this.state = state;
        }

        public static Conditions block(Block block) {
            return new Conditions(EntityPredicate.Extended.EMPTY, block, StatePredicate.ANY);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            if (this.block != null) {
                jsonObject.addProperty("block", Registry.BLOCK.getId(this.block).toString());
            }
            jsonObject.add("state", this.state.toJson());
            return jsonObject;
        }

        public boolean matches(BlockState state) {
            if (this.block != null && !state.isOf(this.block)) {
                return false;
            }
            return this.state.test(state);
        }
    }
}

