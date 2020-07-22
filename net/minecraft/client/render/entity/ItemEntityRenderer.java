/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ItemEntityRenderer
extends EntityRenderer<ItemEntity> {
    private final ItemRenderer itemRenderer;
    private final Random random = new Random();

    public ItemEntityRenderer(EntityRenderDispatcher dispatcher, ItemRenderer itemRenderer) {
        super(dispatcher);
        this.itemRenderer = itemRenderer;
        this.shadowRadius = 0.15f;
        this.shadowOpacity = 0.75f;
    }

    private int getRenderedAmount(ItemStack stack) {
        int i = 1;
        if (stack.getCount() > 48) {
            i = 5;
        } else if (stack.getCount() > 32) {
            i = 4;
        } else if (stack.getCount() > 16) {
            i = 3;
        } else if (stack.getCount() > 1) {
            i = 2;
        }
        return i;
    }

    @Override
    public void render(ItemEntity arg, float f, float g, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        arg2.push();
        ItemStack lv = arg.getStack();
        int j = lv.isEmpty() ? 187 : Item.getRawId(lv.getItem()) + lv.getDamage();
        this.random.setSeed(j);
        BakedModel lv2 = this.itemRenderer.getHeldItemModel(lv, arg.world, null);
        boolean bl = lv2.hasDepth();
        int k = this.getRenderedAmount(lv);
        float h = 0.25f;
        float l = MathHelper.sin(((float)arg.getAge() + g) / 10.0f + arg.hoverHeight) * 0.1f + 0.1f;
        float m = lv2.getTransformation().getTransformation((ModelTransformation.Mode)ModelTransformation.Mode.GROUND).scale.getY();
        arg2.translate(0.0, l + 0.25f * m, 0.0);
        float n = arg.method_27314(g);
        arg2.multiply(Vector3f.POSITIVE_Y.getRadialQuaternion(n));
        float o = lv2.getTransformation().ground.scale.getX();
        float p = lv2.getTransformation().ground.scale.getY();
        float q = lv2.getTransformation().ground.scale.getZ();
        if (!bl) {
            float r = -0.0f * (float)(k - 1) * 0.5f * o;
            float s = -0.0f * (float)(k - 1) * 0.5f * p;
            float t = -0.09375f * (float)(k - 1) * 0.5f * q;
            arg2.translate(r, s, t);
        }
        for (int u = 0; u < k; ++u) {
            arg2.push();
            if (u > 0) {
                if (bl) {
                    float v = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    float w = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    float x = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    arg2.translate(v, w, x);
                } else {
                    float y = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f;
                    float z = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f;
                    arg2.translate(y, z, 0.0);
                }
            }
            this.itemRenderer.renderItem(lv, ModelTransformation.Mode.GROUND, false, arg2, arg3, i, OverlayTexture.DEFAULT_UV, lv2);
            arg2.pop();
            if (bl) continue;
            arg2.translate(0.0f * o, 0.0f * p, 0.09375f * q);
        }
        arg2.pop();
        super.render(arg, f, g, arg2, arg3, i);
    }

    @Override
    public Identifier getTexture(ItemEntity arg) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
    }
}

