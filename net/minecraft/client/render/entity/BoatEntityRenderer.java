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
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.model.BoatEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;

@Environment(value=EnvType.CLIENT)
public class BoatEntityRenderer
extends EntityRenderer<BoatEntity> {
    private static final Identifier[] TEXTURES = new Identifier[]{new Identifier("textures/entity/boat/oak.png"), new Identifier("textures/entity/boat/spruce.png"), new Identifier("textures/entity/boat/birch.png"), new Identifier("textures/entity/boat/jungle.png"), new Identifier("textures/entity/boat/acacia.png"), new Identifier("textures/entity/boat/dark_oak.png")};
    protected final BoatEntityModel model = new BoatEntityModel();

    public BoatEntityRenderer(EntityRenderDispatcher arg) {
        super(arg);
        this.shadowRadius = 0.8f;
    }

    @Override
    public void render(BoatEntity arg, float f, float g, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        float k;
        arg2.push();
        arg2.translate(0.0, 0.375, 0.0);
        arg2.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0f - f));
        float h = (float)arg.getDamageWobbleTicks() - g;
        float j = arg.getDamageWobbleStrength() - g;
        if (j < 0.0f) {
            j = 0.0f;
        }
        if (h > 0.0f) {
            arg2.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(MathHelper.sin(h) * h * j / 10.0f * (float)arg.getDamageWobbleSide()));
        }
        if (!MathHelper.approximatelyEquals(k = arg.interpolateBubbleWobble(g), 0.0f)) {
            arg2.multiply(new Quaternion(new Vector3f(1.0f, 0.0f, 1.0f), arg.interpolateBubbleWobble(g), true));
        }
        arg2.scale(-1.0f, -1.0f, 1.0f);
        arg2.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90.0f));
        this.model.setAngles(arg, g, 0.0f, -0.1f, 0.0f, 0.0f);
        VertexConsumer lv = arg3.getBuffer(this.model.getLayer(this.getTexture(arg)));
        this.model.render(arg2, lv, i, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
        if (!arg.isSubmergedInWater()) {
            VertexConsumer lv2 = arg3.getBuffer(RenderLayer.getWaterMask());
            this.model.getBottom().render(arg2, lv2, i, OverlayTexture.DEFAULT_UV);
        }
        arg2.pop();
        super.render(arg, f, g, arg2, arg3, i);
    }

    @Override
    public Identifier getTexture(BoatEntity arg) {
        return TEXTURES[arg.getBoatType().ordinal()];
    }
}

