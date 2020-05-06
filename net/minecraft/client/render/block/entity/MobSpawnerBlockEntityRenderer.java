/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.MobSpawnerLogic;

@Environment(value=EnvType.CLIENT)
public class MobSpawnerBlockEntityRenderer
extends BlockEntityRenderer<MobSpawnerBlockEntity> {
    public MobSpawnerBlockEntityRenderer(BlockEntityRenderDispatcher arg) {
        super(arg);
    }

    @Override
    public void render(MobSpawnerBlockEntity arg, float f, MatrixStack arg2, VertexConsumerProvider arg3, int i, int j) {
        arg2.push();
        arg2.translate(0.5, 0.0, 0.5);
        MobSpawnerLogic lv = arg.getLogic();
        Entity lv2 = lv.getRenderedEntity();
        if (lv2 != null) {
            float g = 0.53125f;
            float h = Math.max(lv2.getWidth(), lv2.getHeight());
            if ((double)h > 1.0) {
                g /= h;
            }
            arg2.translate(0.0, 0.4f, 0.0);
            arg2.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)MathHelper.lerp((double)f, lv.method_8279(), lv.method_8278()) * 10.0f));
            arg2.translate(0.0, -0.2f, 0.0);
            arg2.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-30.0f));
            arg2.scale(g, g, g);
            MinecraftClient.getInstance().getEntityRenderManager().render(lv2, 0.0, 0.0, 0.0, 0.0f, f, arg2, arg3, i);
        }
        arg2.pop();
    }
}

