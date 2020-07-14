/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallBannerBlock;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BannerBlockEntityRenderer
extends BlockEntityRenderer<BannerBlockEntity> {
    private final ModelPart banner = BannerBlockEntityRenderer.createBanner();
    private final ModelPart pillar = new ModelPart(64, 64, 44, 0);
    private final ModelPart crossbar;

    public BannerBlockEntityRenderer(BlockEntityRenderDispatcher arg) {
        super(arg);
        this.pillar.addCuboid(-1.0f, -30.0f, -1.0f, 2.0f, 42.0f, 2.0f, 0.0f);
        this.crossbar = new ModelPart(64, 64, 0, 42);
        this.crossbar.addCuboid(-10.0f, -32.0f, -1.0f, 20.0f, 2.0f, 2.0f, 0.0f);
    }

    public static ModelPart createBanner() {
        ModelPart lv = new ModelPart(64, 64, 0, 0);
        lv.addCuboid(-10.0f, 0.0f, -2.0f, 20.0f, 40.0f, 1.0f, 0.0f);
        return lv;
    }

    @Override
    public void render(BannerBlockEntity arg, float f, MatrixStack arg2, VertexConsumerProvider arg3, int i, int j) {
        long m;
        List<Pair<BannerPattern, DyeColor>> list = arg.getPatterns();
        if (list == null) {
            return;
        }
        float g = 0.6666667f;
        boolean bl = arg.getWorld() == null;
        arg2.push();
        if (bl) {
            long l = 0L;
            arg2.translate(0.5, 0.5, 0.5);
            this.pillar.visible = true;
        } else {
            m = arg.getWorld().getTime();
            BlockState lv = arg.getCachedState();
            if (lv.getBlock() instanceof BannerBlock) {
                arg2.translate(0.5, 0.5, 0.5);
                float h = (float)(-lv.get(BannerBlock.ROTATION).intValue() * 360) / 16.0f;
                arg2.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(h));
                this.pillar.visible = true;
            } else {
                arg2.translate(0.5, -0.1666666716337204, 0.5);
                float k = -lv.get(WallBannerBlock.FACING).asRotation();
                arg2.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(k));
                arg2.translate(0.0, -0.3125, -0.4375);
                this.pillar.visible = false;
            }
        }
        arg2.push();
        arg2.scale(0.6666667f, -0.6666667f, -0.6666667f);
        VertexConsumer lv2 = ModelLoader.BANNER_BASE.getVertexConsumer(arg3, RenderLayer::getEntitySolid);
        this.pillar.render(arg2, lv2, i, j);
        this.crossbar.render(arg2, lv2, i, j);
        BlockPos lv3 = arg.getPos();
        float n = ((float)Math.floorMod((long)(lv3.getX() * 7 + lv3.getY() * 9 + lv3.getZ() * 13) + m, 100L) + f) / 100.0f;
        this.banner.pitch = (-0.0125f + 0.01f * MathHelper.cos((float)Math.PI * 2 * n)) * (float)Math.PI;
        this.banner.pivotY = -32.0f;
        BannerBlockEntityRenderer.method_29999(arg2, arg3, i, j, this.banner, ModelLoader.BANNER_BASE, true, list);
        arg2.pop();
        arg2.pop();
    }

    public static void method_29999(MatrixStack arg, VertexConsumerProvider arg2, int i, int j, ModelPart arg3, SpriteIdentifier arg4, boolean bl, List<Pair<BannerPattern, DyeColor>> list) {
        BannerBlockEntityRenderer.renderCanvas(arg, arg2, i, j, arg3, arg4, bl, list, false);
    }

    public static void renderCanvas(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, ModelPart canvas, SpriteIdentifier baseSprite, boolean isBanner, List<Pair<BannerPattern, DyeColor>> patterns, boolean bl2) {
        canvas.render(matrices, baseSprite.method_30001(vertexConsumers, RenderLayer::getEntitySolid, bl2), light, overlay);
        for (int k = 0; k < 17 && k < patterns.size(); ++k) {
            Pair<BannerPattern, DyeColor> pair = patterns.get(k);
            float[] fs = ((DyeColor)pair.getSecond()).getColorComponents();
            SpriteIdentifier lv = new SpriteIdentifier(isBanner ? TexturedRenderLayers.BANNER_PATTERNS_ATLAS_TEXTURE : TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE, ((BannerPattern)((Object)pair.getFirst())).getSpriteId(isBanner));
            canvas.render(matrices, lv.getVertexConsumer(vertexConsumers, RenderLayer::getEntityNoOutline), light, overlay, fs[0], fs[1], fs[2], 1.0f);
        }
    }
}

