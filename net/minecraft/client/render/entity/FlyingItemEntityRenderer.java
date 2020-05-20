/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public class FlyingItemEntityRenderer<T extends Entity>
extends EntityRenderer<T> {
    private final ItemRenderer itemRenderer;
    private final float scale;
    private final boolean lit;

    public FlyingItemEntityRenderer(EntityRenderDispatcher arg, ItemRenderer arg2, float f, boolean bl) {
        super(arg);
        this.itemRenderer = arg2;
        this.scale = f;
        this.lit = bl;
    }

    public FlyingItemEntityRenderer(EntityRenderDispatcher arg, ItemRenderer arg2) {
        this(arg, arg2, 1.0f, false);
    }

    @Override
    protected int getBlockLight(T arg, BlockPos arg2) {
        return this.lit ? 15 : super.getBlockLight(arg, arg2);
    }

    @Override
    public void render(T arg, float f, float g, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        arg2.push();
        arg2.scale(this.scale, this.scale, this.scale);
        arg2.multiply(this.dispatcher.getRotation());
        arg2.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0f));
        this.itemRenderer.renderItem(((FlyingItemEntity)arg).getStack(), ModelTransformation.Mode.GROUND, i, OverlayTexture.DEFAULT_UV, arg2, arg3);
        arg2.pop();
        super.render(arg, f, g, arg2, arg3, i);
    }

    @Override
    public Identifier getTexture(Entity arg) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
    }
}

