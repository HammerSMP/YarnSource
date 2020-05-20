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
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ItemUsedOnBlockCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("item_used_on_block");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        LocationPredicate lv = LocationPredicate.fromJson(jsonObject.get("location"));
        ItemPredicate lv2 = ItemPredicate.fromJson(jsonObject.get("item"));
        return new Conditions(arg, lv, lv2);
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
        private final LocationPredicate location;
        private final ItemPredicate item;

        public Conditions(EntityPredicate.Extended arg, LocationPredicate arg2, ItemPredicate arg3) {
            super(ID, arg);
            this.location = arg2;
            this.item = arg3;
        }

        public static Conditions create(LocationPredicate.Builder arg, ItemPredicate.Builder arg2) {
            return new Conditions(EntityPredicate.Extended.EMPTY, arg.build(), arg2.build());
        }

        public boolean test(BlockState arg, ServerWorld arg2, BlockPos arg3, ItemStack arg4) {
            if (!this.location.test(arg2, (double)arg3.getX() + 0.5, (double)arg3.getY() + 0.5, (double)arg3.getZ() + 0.5)) {
                return false;
            }
            return this.item.test(arg4);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer arg) {
            JsonObject jsonObject = super.toJson(arg);
            jsonObject.add("location", this.location.toJson());
            jsonObject.add("item", this.item.toJson());
            return jsonObject;
        }
    }
}

