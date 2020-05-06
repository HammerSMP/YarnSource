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
import net.minecraft.block.Blocks;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.TntMinecartEntityRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.TntEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class TntEntityRenderer
extends EntityRenderer<TntEntity> {
    public TntEntityRenderer(EntityRenderDispatcher arg) {
        super(arg);
        this.shadowRadius = 0.5f;
    }

    @Override
    public void render(TntEntity arg, float f, float g, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        arg2.push();
        arg2.translate(0.0, 0.5, 0.0);
        if ((float)arg.getFuseTimer() - g + 1.0f < 10.0f) {
            float h = 1.0f - ((float)arg.getFuseTimer() - g + 1.0f) / 10.0f;
            h = MathHelper.clamp(h, 0.0f, 1.0f);
            h *= h;
            h *= h;
            float j = 1.0f + h * 0.3f;
            arg2.scale(j, j, j);
        }
        arg2.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-90.0f));
        arg2.translate(-0.5, -0.5, 0.5);
        arg2.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90.0f));
        TntMinecartEntityRenderer.method_23190(Blocks.TNT.getDefaultState(), arg2, arg3, i, arg.getFuseTimer() / 5 % 2 == 0);
        arg2.pop();
        super.render(arg, f, g, arg2, arg3, i);
    }

    @Override
    public Identifier getTexture(TntEntity arg) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
    }
}

