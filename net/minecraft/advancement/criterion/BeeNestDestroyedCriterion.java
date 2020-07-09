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
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class BeeNestDestroyedCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("bee_nest_destroyed");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        Block lv = BeeNestDestroyedCriterion.getBlock(jsonObject);
        ItemPredicate lv2 = ItemPredicate.fromJson(jsonObject.get("item"));
        NumberRange.IntRange lv3 = NumberRange.IntRange.fromJson(jsonObject.get("num_bees_inside"));
        return new Conditions(arg, lv, lv2, lv3);
    }

    @Nullable
    private static Block getBlock(JsonObject jsonObject) {
        if (jsonObject.has("block")) {
            Identifier lv = new Identifier(JsonHelper.getString(jsonObject, "block"));
            return Registry.BLOCK.getOrEmpty(lv).orElseThrow(() -> new JsonSyntaxException("Unknown block type '" + lv + "'"));
        }
        return null;
    }

    public void test(ServerPlayerEntity arg, Block arg2, ItemStack arg32, int i) {
        this.test(arg, arg3 -> arg3.test(arg2, arg32, i));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        return this.conditionsFromJson(jsonObject, arg, arg2);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        @Nullable
        private final Block block;
        private final ItemPredicate item;
        private final NumberRange.IntRange beeCount;

        public Conditions(EntityPredicate.Extended arg, @Nullable Block arg2, ItemPredicate arg3, NumberRange.IntRange arg4) {
            super(ID, arg);
            this.block = arg2;
            this.item = arg3;
            this.beeCount = arg4;
        }

        public static Conditions create(Block arg, ItemPredicate.Builder arg2, NumberRange.IntRange arg3) {
            return new Conditions(EntityPredicate.Extended.EMPTY, arg, arg2.build(), arg3);
        }

        public boolean test(Block arg, ItemStack arg2, int i) {
            if (this.block != null && arg != this.block) {
                return false;
            }
            if (!this.item.test(arg2)) {
                return false;
            }
            return this.beeCount.test(i);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer arg) {
            JsonObject jsonObject = super.toJson(arg);
            if (this.block != null) {
                jsonObject.addProperty("block", Registry.BLOCK.getId(this.block).toString());
            }
            jsonObject.add("item", this.item.toJson());
            jsonObject.add("num_bees_inside", this.beeCount.toJson());
            return jsonObject;
        }
    }
}

