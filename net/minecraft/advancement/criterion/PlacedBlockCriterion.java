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
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class PlacedBlockCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("placed_block");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        Block lv = PlacedBlockCriterion.getBlock(jsonObject);
        StatePredicate lv2 = StatePredicate.fromJson(jsonObject.get("state"));
        if (lv != null) {
            lv2.check(lv.getStateManager(), string -> {
                throw new JsonSyntaxException("Block " + lv + " has no property " + string + ":");
            });
        }
        LocationPredicate lv3 = LocationPredicate.fromJson(jsonObject.get("location"));
        ItemPredicate lv4 = ItemPredicate.fromJson(jsonObject.get("item"));
        return new Conditions(arg, lv, lv2, lv3, lv4);
    }

    @Nullable
    private static Block getBlock(JsonObject jsonObject) {
        if (jsonObject.has("block")) {
            Identifier lv = new Identifier(JsonHelper.getString(jsonObject, "block"));
            return Registry.BLOCK.getOrEmpty(lv).orElseThrow(() -> new JsonSyntaxException("Unknown block type '" + lv + "'"));
        }
        return null;
    }

    public void trigger(ServerPlayerEntity arg, BlockPos arg2, ItemStack arg3) {
        BlockState lv = arg.getServerWorld().getBlockState(arg2);
        this.test(arg, arg5 -> arg5.matches(lv, arg2, arg.getServerWorld(), arg3));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        return this.conditionsFromJson(jsonObject, arg, arg2);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final Block block;
        private final StatePredicate state;
        private final LocationPredicate location;
        private final ItemPredicate item;

        public Conditions(EntityPredicate.Extended arg, @Nullable Block arg2, StatePredicate arg3, LocationPredicate arg4, ItemPredicate arg5) {
            super(ID, arg);
            this.block = arg2;
            this.state = arg3;
            this.location = arg4;
            this.item = arg5;
        }

        public static Conditions block(Block arg) {
            return new Conditions(EntityPredicate.Extended.EMPTY, arg, StatePredicate.ANY, LocationPredicate.ANY, ItemPredicate.ANY);
        }

        public boolean matches(BlockState arg, BlockPos arg2, ServerWorld arg3, ItemStack arg4) {
            if (this.block != null && !arg.isOf(this.block)) {
                return false;
            }
            if (!this.state.test(arg)) {
                return false;
            }
            if (!this.location.test(arg3, arg2.getX(), arg2.getY(), arg2.getZ())) {
                return false;
            }
            return this.item.test(arg4);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer arg) {
            JsonObject jsonObject = super.toJson(arg);
            if (this.block != null) {
                jsonObject.addProperty("block", Registry.BLOCK.getId(this.block).toString());
            }
            jsonObject.add("state", this.state.toJson());
            jsonObject.add("location", this.location.toJson());
            jsonObject.add("item", this.item.toJson());
            return jsonObject;
        }
    }
}

