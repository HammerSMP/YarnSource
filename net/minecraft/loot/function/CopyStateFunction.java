/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 */
package net.minecraft.loot.function;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class CopyStateFunction
extends ConditionalLootFunction {
    private final Block block;
    private final Set<Property<?>> properties;

    private CopyStateFunction(LootCondition[] args, Block arg, Set<Property<?>> properties) {
        super(args);
        this.block = arg;
        this.properties = properties;
    }

    @Override
    public LootFunctionType getType() {
        return LootFunctionTypes.COPY_STATE;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootContextParameters.BLOCK_STATE);
    }

    @Override
    protected ItemStack process(ItemStack stack, LootContext context) {
        BlockState lv = context.get(LootContextParameters.BLOCK_STATE);
        if (lv != null) {
            CompoundTag lv4;
            CompoundTag lv2 = stack.getOrCreateTag();
            if (lv2.contains("BlockStateTag", 10)) {
                CompoundTag lv3 = lv2.getCompound("BlockStateTag");
            } else {
                lv4 = new CompoundTag();
                lv2.put("BlockStateTag", lv4);
            }
            this.properties.stream().filter(lv::contains).forEach(arg3 -> lv4.putString(arg3.getName(), CopyStateFunction.method_21893(lv, arg3)));
        }
        return stack;
    }

    public static Builder getBuilder(Block arg) {
        return new Builder(arg);
    }

    private static <T extends Comparable<T>> String method_21893(BlockState arg, Property<T> arg2) {
        T comparable = arg.get(arg2);
        return arg2.name(comparable);
    }

    public static class Serializer
    extends ConditionalLootFunction.Serializer<CopyStateFunction> {
        @Override
        public void toJson(JsonObject jsonObject, CopyStateFunction arg2, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, arg2, jsonSerializationContext);
            jsonObject.addProperty("block", Registry.BLOCK.getId(arg2.block).toString());
            JsonArray jsonArray = new JsonArray();
            arg2.properties.forEach(arg -> jsonArray.add(arg.getName()));
            jsonObject.add("properties", (JsonElement)jsonArray);
        }

        @Override
        public CopyStateFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] args) {
            Identifier lv = new Identifier(JsonHelper.getString(jsonObject, "block"));
            Block lv2 = Registry.BLOCK.getOrEmpty(lv).orElseThrow(() -> new IllegalArgumentException("Can't find block " + lv));
            StateManager<Block, BlockState> lv3 = lv2.getStateManager();
            HashSet set = Sets.newHashSet();
            JsonArray jsonArray = JsonHelper.getArray(jsonObject, "properties", null);
            if (jsonArray != null) {
                jsonArray.forEach(jsonElement -> set.add(lv3.getProperty(JsonHelper.asString(jsonElement, "property"))));
            }
            return new CopyStateFunction(args, lv2, set);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
            return this.fromJson(json, context, conditions);
        }
    }

    public static class Builder
    extends ConditionalLootFunction.Builder<Builder> {
        private final Block block;
        private final Set<Property<?>> properties = Sets.newHashSet();

        private Builder(Block arg) {
            this.block = arg;
        }

        public Builder method_21898(Property<?> arg) {
            if (!this.block.getStateManager().getProperties().contains(arg)) {
                throw new IllegalStateException("Property " + arg + " is not present on block " + this.block);
            }
            this.properties.add(arg);
            return this;
        }

        @Override
        protected Builder getThisBuilder() {
            return this;
        }

        @Override
        public LootFunction build() {
            return new CopyStateFunction(this.getConditions(), this.block, this.properties);
        }

        @Override
        protected /* synthetic */ ConditionalLootFunction.Builder getThisBuilder() {
            return this.getThisBuilder();
        }
    }
}

