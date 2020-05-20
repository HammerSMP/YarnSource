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
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class BlockStatePropertyLootCondition
implements LootCondition {
    private final Block block;
    private final StatePredicate properties;

    private BlockStatePropertyLootCondition(Block arg, StatePredicate arg2) {
        this.block = arg;
        this.properties = arg2;
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

    public static Builder builder(Block arg) {
        return new Builder(arg);
    }

    @Override
    public /* synthetic */ boolean test(Object object) {
        return this.test((LootContext)object);
    }

    public static class Factory
    extends LootCondition.Factory<BlockStatePropertyLootCondition> {
        protected Factory() {
            super(new Identifier("block_state_property"), BlockStatePropertyLootCondition.class);
        }

        @Override
        public void toJson(JsonObject jsonObject, BlockStatePropertyLootCondition arg, JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("block", Registry.BLOCK.getId(arg.block).toString());
            jsonObject.add("properties", arg.properties.toJson());
        }

        @Override
        public BlockStatePropertyLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            Identifier lv = new Identifier(JsonHelper.getString(jsonObject, "block"));
            Block lv2 = (Block)Registry.BLOCK.getOrEmpty(lv).orElseThrow(() -> new IllegalArgumentException("Can't find block " + lv));
            StatePredicate lv3 = StatePredicate.fromJson(jsonObject.get("properties"));
            lv3.check(lv2.getStateManager(), string -> {
                throw new JsonSyntaxException("Block " + lv2 + " has no property " + string);
            });
            return new BlockStatePropertyLootCondition(lv2, lv3);
        }

        @Override
        public /* synthetic */ LootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return this.fromJson(jsonObject, jsonDeserializationContext);
        }
    }

    public static class Builder
    implements LootCondition.Builder {
        private final Block block;
        private StatePredicate propertyValues = StatePredicate.ANY;

        public Builder(Block arg) {
            this.block = arg;
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

