/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 */
package net.minecraft.loot.function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.arguments.NbtPathArgumentType;
import net.minecraft.entity.Entity;
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
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.util.JsonHelper;

public class CopyNbtLootFunction
extends ConditionalLootFunction {
    private final Source source;
    private final List<Operation> operations;
    private static final Function<Entity, Tag> ENTITY_TAG_GETTER = NbtPredicate::entityToTag;
    private static final Function<BlockEntity, Tag> BLOCK_ENTITY_TAG_GETTER = arg -> arg.toTag(new CompoundTag());

    private CopyNbtLootFunction(LootCondition[] args, Source arg, List<Operation> list) {
        super(args);
        this.source = arg;
        this.operations = ImmutableList.copyOf(list);
    }

    @Override
    public LootFunctionType getType() {
        return LootFunctionTypes.COPY_NBT;
    }

    private static NbtPathArgumentType.NbtPath parseNbtPath(String string) {
        try {
            return new NbtPathArgumentType().parse(new StringReader(string));
        }
        catch (CommandSyntaxException commandSyntaxException) {
            throw new IllegalArgumentException("Failed to parse path " + string, commandSyntaxException);
        }
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(this.source.parameter);
    }

    @Override
    public ItemStack process(ItemStack arg, LootContext arg2) {
        Tag lv = this.source.getter.apply(arg2);
        if (lv != null) {
            this.operations.forEach(arg3 -> arg3.execute(arg::getOrCreateTag, lv));
        }
        return arg;
    }

    public static Builder builder(Source arg) {
        return new Builder(arg);
    }

    static /* synthetic */ Function method_16851() {
        return ENTITY_TAG_GETTER;
    }

    static /* synthetic */ Function method_16854() {
        return BLOCK_ENTITY_TAG_GETTER;
    }

    public static class Serializer
    extends ConditionalLootFunction.Serializer<CopyNbtLootFunction> {
        @Override
        public void toJson(JsonObject jsonObject, CopyNbtLootFunction arg, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, arg, jsonSerializationContext);
            jsonObject.addProperty("source", ((CopyNbtLootFunction)arg).source.name);
            JsonArray jsonArray = new JsonArray();
            arg.operations.stream().map(Operation::toJson).forEach(((JsonArray)jsonArray)::add);
            jsonObject.add("ops", (JsonElement)jsonArray);
        }

        @Override
        public CopyNbtLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] args) {
            Source lv = Source.get(JsonHelper.getString(jsonObject, "source"));
            ArrayList list = Lists.newArrayList();
            JsonArray jsonArray = JsonHelper.getArray(jsonObject, "ops");
            for (JsonElement jsonElement : jsonArray) {
                JsonObject jsonObject2 = JsonHelper.asObject(jsonElement, "op");
                list.add(Operation.fromJson(jsonObject2));
            }
            return new CopyNbtLootFunction(args, lv, list);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] args) {
            return this.fromJson(jsonObject, jsonDeserializationContext, args);
        }
    }

    public static enum Source {
        THIS("this", LootContextParameters.THIS_ENTITY, CopyNbtLootFunction.method_16851()),
        KILLER("killer", LootContextParameters.KILLER_ENTITY, CopyNbtLootFunction.method_16851()),
        KILLER_PLAYER("killer_player", LootContextParameters.LAST_DAMAGE_PLAYER, CopyNbtLootFunction.method_16851()),
        BLOCK_ENTITY("block_entity", LootContextParameters.BLOCK_ENTITY, CopyNbtLootFunction.method_16854());

        public final String name;
        public final LootContextParameter<?> parameter;
        public final Function<LootContext, Tag> getter;

        private <T> Source(String string2, LootContextParameter<T> arg, Function<? super T, Tag> function) {
            this.name = string2;
            this.parameter = arg;
            this.getter = arg2 -> {
                Object object = arg2.get(arg);
                return object != null ? (Tag)function.apply((Object)object) : null;
            };
        }

        public static Source get(String string) {
            for (Source lv : Source.values()) {
                if (!lv.name.equals(string)) continue;
                return lv;
            }
            throw new IllegalArgumentException("Invalid tag source " + string);
        }
    }

    public static enum Operator {
        REPLACE("replace"){

            @Override
            public void merge(Tag arg, NbtPathArgumentType.NbtPath arg2, List<Tag> list) throws CommandSyntaxException {
                arg2.put(arg, ((Tag)Iterables.getLast(list))::copy);
            }
        }
        ,
        APPEND("append"){

            @Override
            public void merge(Tag arg2, NbtPathArgumentType.NbtPath arg22, List<Tag> list) throws CommandSyntaxException {
                List<Tag> list2 = arg22.getOrInit(arg2, ListTag::new);
                list2.forEach(arg -> {
                    if (arg instanceof ListTag) {
                        list.forEach(arg2 -> ((ListTag)arg).add(arg2.copy()));
                    }
                });
            }
        }
        ,
        MERGE("merge"){

            @Override
            public void merge(Tag arg2, NbtPathArgumentType.NbtPath arg22, List<Tag> list) throws CommandSyntaxException {
                List<Tag> list2 = arg22.getOrInit(arg2, CompoundTag::new);
                list2.forEach(arg -> {
                    if (arg instanceof CompoundTag) {
                        list.forEach(arg2 -> {
                            if (arg2 instanceof CompoundTag) {
                                ((CompoundTag)arg).copyFrom((CompoundTag)arg2);
                            }
                        });
                    }
                });
            }
        };

        private final String name;

        public abstract void merge(Tag var1, NbtPathArgumentType.NbtPath var2, List<Tag> var3) throws CommandSyntaxException;

        private Operator(String string2) {
            this.name = string2;
        }

        public static Operator get(String string) {
            for (Operator lv : Operator.values()) {
                if (!lv.name.equals(string)) continue;
                return lv;
            }
            throw new IllegalArgumentException("Invalid merge strategy" + string);
        }
    }

    public static class Builder
    extends ConditionalLootFunction.Builder<Builder> {
        private final Source source;
        private final List<Operation> operations = Lists.newArrayList();

        private Builder(Source arg) {
            this.source = arg;
        }

        public Builder withOperation(String string, String string2, Operator arg) {
            this.operations.add(new Operation(string, string2, arg));
            return this;
        }

        public Builder withOperation(String string, String string2) {
            return this.withOperation(string, string2, Operator.REPLACE);
        }

        @Override
        protected Builder getThisBuilder() {
            return this;
        }

        @Override
        public LootFunction build() {
            return new CopyNbtLootFunction(this.getConditions(), this.source, this.operations);
        }

        @Override
        protected /* synthetic */ ConditionalLootFunction.Builder getThisBuilder() {
            return this.getThisBuilder();
        }
    }

    static class Operation {
        private final String sourcePath;
        private final NbtPathArgumentType.NbtPath parsedSourcePath;
        private final String targetPath;
        private final NbtPathArgumentType.NbtPath parsedTargetPath;
        private final Operator operator;

        private Operation(String string, String string2, Operator arg) {
            this.sourcePath = string;
            this.parsedSourcePath = CopyNbtLootFunction.parseNbtPath(string);
            this.targetPath = string2;
            this.parsedTargetPath = CopyNbtLootFunction.parseNbtPath(string2);
            this.operator = arg;
        }

        public void execute(Supplier<Tag> supplier, Tag arg) {
            try {
                List<Tag> list = this.parsedSourcePath.get(arg);
                if (!list.isEmpty()) {
                    this.operator.merge(supplier.get(), this.parsedTargetPath, list);
                }
            }
            catch (CommandSyntaxException commandSyntaxException) {
                // empty catch block
            }
        }

        public JsonObject toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("source", this.sourcePath);
            jsonObject.addProperty("target", this.targetPath);
            jsonObject.addProperty("op", this.operator.name);
            return jsonObject;
        }

        public static Operation fromJson(JsonObject jsonObject) {
            String string = JsonHelper.getString(jsonObject, "source");
            String string2 = JsonHelper.getString(jsonObject, "target");
            Operator lv = Operator.get(JsonHelper.getString(jsonObject, "op"));
            return new Operation(string, string2, lv);
        }
    }
}

