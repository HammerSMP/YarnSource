/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package net.minecraft.data.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.data.client.model.BlockStateSupplier;
import net.minecraft.data.client.model.BlockStateVariant;
import net.minecraft.data.client.model.When;
import net.minecraft.state.StateManager;

public class MultipartBlockStateSupplier
implements BlockStateSupplier {
    private final Block block;
    private final List<Multipart> multiparts = Lists.newArrayList();

    private MultipartBlockStateSupplier(Block arg) {
        this.block = arg;
    }

    @Override
    public Block getBlock() {
        return this.block;
    }

    public static MultipartBlockStateSupplier create(Block arg) {
        return new MultipartBlockStateSupplier(arg);
    }

    public MultipartBlockStateSupplier with(List<BlockStateVariant> list) {
        this.multiparts.add(new Multipart(list));
        return this;
    }

    public MultipartBlockStateSupplier with(BlockStateVariant arg) {
        return this.with((List<BlockStateVariant>)ImmutableList.of((Object)arg));
    }

    public MultipartBlockStateSupplier with(When arg, List<BlockStateVariant> list) {
        this.multiparts.add(new ConditionalMultipart(arg, list));
        return this;
    }

    public MultipartBlockStateSupplier with(When arg, BlockStateVariant ... args) {
        return this.with(arg, (List<BlockStateVariant>)ImmutableList.copyOf((Object[])args));
    }

    public MultipartBlockStateSupplier with(When arg, BlockStateVariant arg2) {
        return this.with(arg, (List<BlockStateVariant>)ImmutableList.of((Object)arg2));
    }

    @Override
    public JsonElement get() {
        StateManager<Block, BlockState> lv = this.block.getStateManager();
        this.multiparts.forEach(arg2 -> arg2.validate(lv));
        JsonArray jsonArray = new JsonArray();
        this.multiparts.stream().map(Multipart::get).forEach(((JsonArray)jsonArray)::add);
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("multipart", (JsonElement)jsonArray);
        return jsonObject;
    }

    @Override
    public /* synthetic */ Object get() {
        return this.get();
    }

    static class ConditionalMultipart
    extends Multipart {
        private final When when;

        private ConditionalMultipart(When arg, List<BlockStateVariant> list) {
            super(list);
            this.when = arg;
        }

        @Override
        public void validate(StateManager<?, ?> arg) {
            this.when.validate(arg);
        }

        @Override
        public void extraToJson(JsonObject jsonObject) {
            jsonObject.add("when", (JsonElement)this.when.get());
        }
    }

    static class Multipart
    implements Supplier<JsonElement> {
        private final List<BlockStateVariant> variants;

        private Multipart(List<BlockStateVariant> list) {
            this.variants = list;
        }

        public void validate(StateManager<?, ?> arg) {
        }

        public void extraToJson(JsonObject jsonObject) {
        }

        @Override
        public JsonElement get() {
            JsonObject jsonObject = new JsonObject();
            this.extraToJson(jsonObject);
            jsonObject.add("apply", BlockStateVariant.toJson(this.variants));
            return jsonObject;
        }

        @Override
        public /* synthetic */ Object get() {
            return this.get();
        }
    }
}

