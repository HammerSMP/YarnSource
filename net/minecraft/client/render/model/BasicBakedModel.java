/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

@Environment(value=EnvType.CLIENT)
public class BasicBakedModel
implements BakedModel {
    protected final List<BakedQuad> quads;
    protected final Map<Direction, List<BakedQuad>> faceQuads;
    protected final boolean usesAo;
    protected final boolean hasDepth;
    protected final boolean isSideLit;
    protected final Sprite sprite;
    protected final ModelTransformation transformation;
    protected final ModelOverrideList itemPropertyOverrides;

    public BasicBakedModel(List<BakedQuad> list, Map<Direction, List<BakedQuad>> map, boolean bl, boolean bl2, boolean bl3, Sprite arg, ModelTransformation arg2, ModelOverrideList arg3) {
        this.quads = list;
        this.faceQuads = map;
        this.usesAo = bl;
        this.hasDepth = bl3;
        this.isSideLit = bl2;
        this.sprite = arg;
        this.transformation = arg2;
        this.itemPropertyOverrides = arg3;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState arg, @Nullable Direction arg2, Random random) {
        return arg2 == null ? this.quads : this.faceQuads.get(arg2);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return this.usesAo;
    }

    @Override
    public boolean hasDepth() {
        return this.hasDepth;
    }

    @Override
    public boolean isSideLit() {
        return this.isSideLit;
    }

    @Override
    public boolean isBuiltin() {
        return false;
    }

    @Override
    public Sprite getSprite() {
        return this.sprite;
    }

    @Override
    public ModelTransformation getTransformation() {
        return this.transformation;
    }

    @Override
    public ModelOverrideList getOverrides() {
        return this.itemPropertyOverrides;
    }

    @Environment(value=EnvType.CLIENT)
    public static class Builder {
        private final List<BakedQuad> quads = Lists.newArrayList();
        private final Map<Direction, List<BakedQuad>> faceQuads = Maps.newEnumMap(Direction.class);
        private final ModelOverrideList itemPropertyOverrides;
        private final boolean usesAo;
        private Sprite particleTexture;
        private final boolean isSideLit;
        private final boolean hasDepth;
        private final ModelTransformation transformation;

        public Builder(JsonUnbakedModel arg, ModelOverrideList arg2, boolean bl) {
            this(arg.useAmbientOcclusion(), arg.getGuiLight().isSide(), bl, arg.getTransformations(), arg2);
        }

        private Builder(boolean bl, boolean bl2, boolean bl3, ModelTransformation arg, ModelOverrideList arg2) {
            for (Direction lv : Direction.values()) {
                this.faceQuads.put(lv, Lists.newArrayList());
            }
            this.itemPropertyOverrides = arg2;
            this.usesAo = bl;
            this.isSideLit = bl2;
            this.hasDepth = bl3;
            this.transformation = arg;
        }

        public Builder addQuad(Direction arg, BakedQuad arg2) {
            this.faceQuads.get(arg).add(arg2);
            return this;
        }

        public Builder addQuad(BakedQuad arg) {
            this.quads.add(arg);
            return this;
        }

        public Builder setParticle(Sprite arg) {
            this.particleTexture = arg;
            return this;
        }

        public BakedModel build() {
            if (this.particleTexture == null) {
                throw new RuntimeException("Missing particle!");
            }
            return new BasicBakedModel(this.quads, this.faceQuads, this.usesAo, this.isSideLit, this.hasDepth, this.particleTexture, this.transformation, this.itemPropertyOverrides);
        }
    }
}

