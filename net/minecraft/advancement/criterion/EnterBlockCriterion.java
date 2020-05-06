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
            lv2.check(lv.getStateManager(), string -> {
                throw new JsonSyntaxException("Block " + lv + " has no property " + string);
            });
        }
        return new Conditions(arg, lv, lv2);
    }

    @Nullable
    private static Block getBlock(JsonObject jsonObject) {
        if (jsonObject.has("block")) {
            Identifier lv = new Identifier(JsonHelper.getString(jsonObject, "block"));
            return (Block)Registry.BLOCK.getOrEmpty(lv).orElseThrow(() -> new JsonSyntaxException("Unknown block type '" + lv + "'"));
        }
        return null;
    }

    public void trigger(ServerPlayerEntity arg, BlockState arg22) {
        this.test(arg, arg2 -> arg2.matches(arg22));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        return this.conditionsFromJson(jsonObject, arg, arg2);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final Block block;
        private final StatePredicate state;

        public Conditions(EntityPredicate.Extended arg, @Nullable Block arg2, StatePredicate arg3) {
            super(ID, arg);
            this.block = arg2;
            this.state = arg3;
        }

        public static Conditions block(Block arg) {
            return new Conditions(EntityPredicate.Extended.EMPTY, arg, StatePredicate.ANY);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer arg) {
            JsonObject jsonObject = super.toJson(arg);
            if (this.block != null) {
                jsonObject.addProperty("block", Registry.BLOCK.getId(this.block).toString());
            }
            jsonObject.add("state", this.state.toJson());
            return jsonObject;
        }

        public boolean matches(BlockState arg) {
            if (this.block != null && !arg.isOf(this.block)) {
                return false;
            }
            return this.state.test(arg);
        }
    }
}

