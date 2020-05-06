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
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Direction;

@Environment(value=EnvType.CLIENT)
public class ShulkerBoxBlockEntityRenderer
extends BlockEntityRenderer<ShulkerBoxBlockEntity> {
    private final ShulkerEntityModel<?> model;

    public ShulkerBoxBlockEntityRenderer(ShulkerEntityModel<?> arg, BlockEntityRenderDispatcher arg2) {
        super(arg2);
        this.model = arg;
    }

    @Override
    public void render(ShulkerBoxBlockEntity arg, float f, MatrixStack arg2, VertexConsumerProvider arg3, int i, int j) {
        SpriteIdentifier lv5;
        DyeColor lv3;
        BlockState lv2;
        Direction lv = Direction.UP;
        if (arg.hasWorld() && (lv2 = arg.getWorld().getBlockState(arg.getPos())).getBlock() instanceof ShulkerBoxBlock) {
            lv = lv2.get(ShulkerBoxBlock.FACING);
        }
        if ((lv3 = arg.getColor()) == null) {
            SpriteIdentifier lv4 = TexturedRenderLayers.SHULKER_TEXTURE_ID;
        } else {
            lv5 = TexturedRenderLayers.COLORED_SHULKER_BOXES_TEXTURES.get(lv3.getId());
        }
        arg2.push();
        arg2.translate(0.5, 0.5, 0.5);
        float g = 0.9995f;
        arg2.scale(0.9995f, 0.9995f, 0.9995f);
        arg2.multiply(lv.getRotationQuaternion());
        arg2.scale(1.0f, -1.0f, -1.0f);
        arg2.translate(0.0, -1.0, 0.0);
        VertexConsumer lv6 = lv5.getVertexConsumer(arg3, RenderLayer::getEntityCutoutNoCull);
        this.model.getBottomShell().render(arg2, lv6, i, j);
        arg2.translate(0.0, -arg.getAnimationProgress(f) * 0.5f, 0.0);
        arg2.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(270.0f * arg.getAnimationProgress(f)));
        this.model.getTopShell().render(arg2, lv6, i, j);
        arg2.pop();
    }
}

