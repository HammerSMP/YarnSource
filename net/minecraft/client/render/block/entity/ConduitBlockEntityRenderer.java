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
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;

@Environment(value=EnvType.CLIENT)
public class ConduitBlockEntityRenderer
extends BlockEntityRenderer<ConduitBlockEntity> {
    public static final SpriteIdentifier BASE_TEXTURE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, new Identifier("entity/conduit/base"));
    public static final SpriteIdentifier CAGE_TEXTURE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, new Identifier("entity/conduit/cage"));
    public static final SpriteIdentifier WIND_TEXTURE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, new Identifier("entity/conduit/wind"));
    public static final SpriteIdentifier WIND_VERTICAL_TEXTURE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, new Identifier("entity/conduit/wind_vertical"));
    public static final SpriteIdentifier OPEN_EYE_TEXTURE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, new Identifier("entity/conduit/open_eye"));
    public static final SpriteIdentifier CLOSED_EYE_TEXTURE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, new Identifier("entity/conduit/closed_eye"));
    private final ModelPart field_20823 = new ModelPart(16, 16, 0, 0);
    private final ModelPart field_20824;
    private final ModelPart field_20825;
    private final ModelPart field_20826;

    public ConduitBlockEntityRenderer(BlockEntityRenderDispatcher arg) {
        super(arg);
        this.field_20823.addCuboid(-4.0f, -4.0f, 0.0f, 8.0f, 8.0f, 0.0f, 0.01f);
        this.field_20824 = new ModelPart(64, 32, 0, 0);
        this.field_20824.addCuboid(-8.0f, -8.0f, -8.0f, 16.0f, 16.0f, 16.0f);
        this.field_20825 = new ModelPart(32, 16, 0, 0);
        this.field_20825.addCuboid(-3.0f, -3.0f, -3.0f, 6.0f, 6.0f, 6.0f);
        this.field_20826 = new ModelPart(32, 16, 0, 0);
        this.field_20826.addCuboid(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f);
    }

    @Override
    public void render(ConduitBlockEntity arg, float f, MatrixStack arg2, VertexConsumerProvider arg3, int i, int j) {
        float g = (float)arg.ticks + f;
        if (!arg.isActive()) {
            float h = arg.getRotation(0.0f);
            VertexConsumer lv = BASE_TEXTURE.getVertexConsumer(arg3, RenderLayer::getEntitySolid);
            arg2.push();
            arg2.translate(0.5, 0.5, 0.5);
            arg2.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(h));
            this.field_20825.render(arg2, lv, i, j);
            arg2.pop();
            return;
        }
        float k = arg.getRotation(f) * 57.295776f;
        float l = MathHelper.sin(g * 0.1f) / 2.0f + 0.5f;
        l = l * l + l;
        arg2.push();
        arg2.translate(0.5, 0.3f + l * 0.2f, 0.5);
        Vector3f lv2 = new Vector3f(0.5f, 1.0f, 0.5f);
        lv2.normalize();
        arg2.multiply(new Quaternion(lv2, k, true));
        this.field_20826.render(arg2, CAGE_TEXTURE.getVertexConsumer(arg3, RenderLayer::getEntityCutoutNoCull), i, j);
        arg2.pop();
        int m = arg.ticks / 66 % 3;
        arg2.push();
        arg2.translate(0.5, 0.5, 0.5);
        if (m == 1) {
            arg2.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90.0f));
        } else if (m == 2) {
            arg2.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(90.0f));
        }
        VertexConsumer lv3 = (m == 1 ? WIND_VERTICAL_TEXTURE : WIND_TEXTURE).getVertexConsumer(arg3, RenderLayer::getEntityCutoutNoCull);
        this.field_20824.render(arg2, lv3, i, j);
        arg2.pop();
        arg2.push();
        arg2.translate(0.5, 0.5, 0.5);
        arg2.scale(0.875f, 0.875f, 0.875f);
        arg2.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(180.0f));
        arg2.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0f));
        this.field_20824.render(arg2, lv3, i, j);
        arg2.pop();
        Camera lv4 = this.dispatcher.camera;
        arg2.push();
        arg2.translate(0.5, 0.3f + l * 0.2f, 0.5);
        arg2.scale(0.5f, 0.5f, 0.5f);
        float n = -lv4.getYaw();
        arg2.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(n));
        arg2.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(lv4.getPitch()));
        arg2.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0f));
        float o = 1.3333334f;
        arg2.scale(1.3333334f, 1.3333334f, 1.3333334f);
        this.field_20823.render(arg2, (arg.isEyeOpen() ? OPEN_EYE_TEXTURE : CLOSED_EYE_TEXTURE).getVertexConsumer(arg3, RenderLayer::getEntityCutoutNoCull), i, j);
        arg2.pop();
    }
}

