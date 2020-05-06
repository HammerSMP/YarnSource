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
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class TridentEntityRenderer
extends EntityRenderer<TridentEntity> {
    public static final Identifier TEXTURE = new Identifier("textures/entity/trident.png");
    private final TridentEntityModel model = new TridentEntityModel();

    public TridentEntityRenderer(EntityRenderDispatcher arg) {
        super(arg);
    }

    @Override
    public void render(TridentEntity arg, float f, float g, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        arg2.push();
        arg2.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(MathHelper.lerp(g, arg.prevYaw, arg.yaw) - 90.0f));
        arg2.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(MathHelper.lerp(g, arg.prevPitch, arg.pitch) + 90.0f));
        VertexConsumer lv = ItemRenderer.getArmorVertexConsumer(arg3, this.model.getLayer(this.getTexture(arg)), false, arg.isEnchanted());
        this.model.render(arg2, lv, i, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
        arg2.pop();
        super.render(arg, f, g, arg2, arg3, i);
    }

    @Override
    public Identifier getTexture(TridentEntity arg) {
        return TEXTURE;
    }
}

