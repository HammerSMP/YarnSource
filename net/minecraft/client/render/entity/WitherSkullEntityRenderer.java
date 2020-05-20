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
import net.minecraft.client.render.entity.model.SkullEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class WitherSkullEntityRenderer
extends EntityRenderer<WitherSkullEntity> {
    private static final Identifier INVULNERABLE_TEXTURE = new Identifier("textures/entity/wither/wither_invulnerable.png");
    private static final Identifier TEXTURE = new Identifier("textures/entity/wither/wither.png");
    private final SkullEntityModel model = new SkullEntityModel();

    public WitherSkullEntityRenderer(EntityRenderDispatcher arg) {
        super(arg);
    }

    @Override
    protected int getBlockLight(WitherSkullEntity arg, BlockPos arg2) {
        return 15;
    }

    @Override
    public void render(WitherSkullEntity arg, float f, float g, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        arg2.push();
        arg2.scale(-1.0f, -1.0f, 1.0f);
        float h = MathHelper.lerpAngle(arg.prevYaw, arg.yaw, g);
        float j = MathHelper.lerp(g, arg.prevPitch, arg.pitch);
        VertexConsumer lv = arg3.getBuffer(this.model.getLayer(this.getTexture(arg)));
        this.model.method_2821(0.0f, h, j);
        this.model.render(arg2, lv, i, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
        arg2.pop();
        super.render(arg, f, g, arg2, arg3, i);
    }

    @Override
    public Identifier getTexture(WitherSkullEntity arg) {
        return arg.isCharged() ? INVULNERABLE_TEXTURE : TEXTURE;
    }
}

