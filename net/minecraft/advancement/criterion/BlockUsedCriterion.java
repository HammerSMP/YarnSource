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
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class BlockUsedCriterion
extends AbstractCriterion<Conditions> {
    private final Identifier id;

    public BlockUsedCriterion(Identifier arg) {
        this.id = arg;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        BlockPredicate lv = BlockPredicate.fromJson(jsonObject.get("block"));
        StatePredicate lv2 = StatePredicate.fromJson(jsonObject.get("state"));
        ItemPredicate lv3 = ItemPredicate.fromJson(jsonObject.get("item"));
        return new Conditions(this.id, arg, lv, lv2, lv3);
    }

    public void test(ServerPlayerEntity arg, BlockPos arg2, ItemStack arg3) {
        BlockState lv = arg.getServerWorld().getBlockState(arg2);
        this.test(arg, arg5 -> arg5.test(lv, arg.getServerWorld(), arg2, arg3));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        return this.conditionsFromJson(jsonObject, arg, arg2);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final BlockPredicate block;
        private final StatePredicate state;
        private final ItemPredicate item;

        public Conditions(Identifier arg, EntityPredicate.Extended arg2, BlockPredicate arg3, StatePredicate arg4, ItemPredicate arg5) {
            super(arg, arg2);
            this.block = arg3;
            this.state = arg4;
            this.item = arg5;
        }

        public static Conditions create(BlockPredicate.Builder arg, ItemPredicate.Builder arg2) {
            return new Conditions(Criteria.SAFELY_HARVEST_HONEY.id, EntityPredicate.Extended.EMPTY, arg.build(), StatePredicate.ANY, arg2.build());
        }

        public boolean test(BlockState arg, ServerWorld arg2, BlockPos arg3, ItemStack arg4) {
            if (!this.block.test(arg2, arg3)) {
                return false;
            }
            if (!this.state.test(arg)) {
                return false;
            }
            return this.item.test(arg4);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer arg) {
            JsonObject jsonObject = super.toJson(arg);
            jsonObject.add("block", this.block.toJson());
            jsonObject.add("state", this.state.toJson());
            jsonObject.add("item", this.item.toJson());
            return jsonObject;
        }
    }
}

