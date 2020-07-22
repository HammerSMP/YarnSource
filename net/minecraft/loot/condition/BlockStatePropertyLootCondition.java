/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSyntaxException
 */
package net.minecraft.loot.condition;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;
import net.minecraft.util.registry.Registry;

public class BlockStatePropertyLootCondition
implements LootCondition {
    private final Block block;
    private final StatePredicate properties;

    private BlockStatePropertyLootCondition(Block block, StatePredicate properties) {
        this.block = block;
        this.properties = properties;
    }

    @Override
    public LootConditionType getType() {
        return LootConditionTypes.BLOCK_STATE_PROPERTY;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootContextParameters.BLOCK_STATE);
    }

    @Override
    public boolean test(LootContext arg) {
        BlockState lv = arg.get(LootContextParameters.BLOCK_STATE);
        return lv != null && this.block == lv.getBlock() && this.properties.test(lv);
    }

    public static Builder builder(Block block) {
        return new Builder(block);
    }

    @Override
    public /* synthetic */ boolean test(Object context) {
        return this.test((LootContext)context);
    }

    public static class Serializer
    implements JsonSerializer<BlockStatePropertyLootCondition> {
        @Override
        public void toJson(JsonObject jsonObject, BlockStatePropertyLootCondition arg, JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("block", Registry.BLOCK.getId(arg.block).toString());
            jsonObject.add("properties", arg.properties.toJson());
        }

        @Override
        public BlockStatePropertyLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            Identifier lv = new Identifier(JsonHelper.getString(jsonObject, "block"));
            Block lv2 = Registry.BLOCK.getOrEmpty(lv).orElseThrow(() -> new IllegalArgumentException("Can't find block " + lv));
            StatePredicate lv3 = StatePredicate.fromJson(jsonObject.get("properties"));
            lv3.check(lv2.getStateManager(), string -> {
                throw new JsonSyntaxException("Block " + lv2 + " has no property " + string);
            });
            return new BlockStatePropertyLootCondition(lv2, lv3);
        }

        @Override
        public /* synthetic */ Object fromJson(JsonObject json, JsonDeserializationContext context) {
            return this.fromJson(json, context);
        }
    }

    public static class Builder
    implements LootCondition.Builder {
        private final Block block;
        private StatePredicate propertyValues = StatePredicate.ANY;

        public Builder(Block block) {
            this.block = block;
        }

        public Builder properties(StatePredicate.Builder arg) {
            this.propertyValues = arg.build();
            return this;
        }

        @Override
        public LootCondition build() {
            return new BlockStatePropertyLootCondition(this.block, this.propertyValues);
        }
    }
}

