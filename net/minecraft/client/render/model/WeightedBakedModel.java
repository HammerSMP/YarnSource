/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.collection.WeightedPicker;
import net.minecraft.util.math.Direction;

@Environment(value=EnvType.CLIENT)
public class WeightedBakedModel
implements BakedModel {
    private final int totalWeight;
    private final List<Entry> models;
    private final BakedModel defaultModel;

    public WeightedBakedModel(List<Entry> list) {
        this.models = list;
        this.totalWeight = WeightedPicker.getWeightSum(list);
        this.defaultModel = list.get((int)0).model;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState arg, @Nullable Direction arg2, Random random) {
        return WeightedPicker.getAt(this.models, (int)(Math.abs((int)((int)random.nextLong())) % this.totalWeight)).model.getQuads(arg, arg2, random);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return this.defaultModel.useAmbientOcclusion();
    }

    @Override
    public boolean hasDepth() {
        return this.defaultModel.hasDepth();
    }

    @Override
    public boolean isSideLit() {
        return this.defaultModel.isSideLit();
    }

    @Override
    public boolean isBuiltin() {
        return this.defaultModel.isBuiltin();
    }

    @Override
    public Sprite getSprite() {
        return this.defaultModel.getSprite();
    }

    @Override
    public ModelTransformation getTransformation() {
        return this.defaultModel.getTransformation();
    }

    @Override
    public ModelOverrideList getOverrides() {
        return this.defaultModel.getOverrides();
    }

    @Environment(value=EnvType.CLIENT)
    static class Entry
    extends WeightedPicker.Entry {
        protected final BakedModel model;

        public Entry(BakedModel arg, int i) {
            super(i);
            this.model = arg;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Builder {
        private final List<Entry> models = Lists.newArrayList();

        public Builder add(@Nullable BakedModel arg, int i) {
            if (arg != null) {
                this.models.add(new Entry(arg, i));
            }
            return this;
        }

        @Nullable
        public BakedModel getFirst() {
            if (this.models.isEmpty()) {
                return null;
            }
            if (this.models.size() == 1) {
                return this.models.get((int)0).model;
            }
            return new WeightedBakedModel(this.models);
        }
    }
}

