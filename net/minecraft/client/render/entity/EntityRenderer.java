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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;

@Environment(value=EnvType.CLIENT)
public abstract class EntityRenderer<T extends Entity> {
    protected final EntityRenderDispatcher dispatcher;
    protected float shadowRadius;
    protected float shadowOpacity = 1.0f;

    protected EntityRenderer(EntityRenderDispatcher arg) {
        this.dispatcher = arg;
    }

    public final int getLight(T arg, float f) {
        return LightmapTextureManager.pack(this.getBlockLight(arg, f), ((Entity)arg).world.getLightLevel(LightType.SKY, new BlockPos(((Entity)arg).getCameraPosVec(f))));
    }

    protected int getBlockLight(T arg, float f) {
        if (((Entity)arg).isOnFire()) {
            return 15;
        }
        return ((Entity)arg).world.getLightLevel(LightType.BLOCK, new BlockPos(((Entity)arg).getCameraPosVec(f)));
    }

    public boolean shouldRender(T arg, Frustum arg2, double d, double e, double f) {
        if (!((Entity)arg).shouldRender(d, e, f)) {
            return false;
        }
        if (((Entity)arg).ignoreCameraFrustum) {
            return true;
        }
        Box lv = ((Entity)arg).getVisibilityBoundingBox().expand(0.5);
        if (lv.isValid() || lv.getAverageSideLength() == 0.0) {
            lv = new Box(((Entity)arg).getX() - 2.0, ((Entity)arg).getY() - 2.0, ((Entity)arg).getZ() - 2.0, ((Entity)arg).getX() + 2.0, ((Entity)arg).getY() + 2.0, ((Entity)arg).getZ() + 2.0);
        }
        return arg2.isVisible(lv);
    }

    public Vec3d getPositionOffset(T arg, float f) {
        return Vec3d.ZERO;
    }

    public void render(T arg, float f, float g, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        if (!this.hasLabel(arg)) {
            return;
        }
        this.renderLabelIfPresent(arg, ((Entity)arg).getDisplayName(), arg2, arg3, i);
    }

    protected boolean hasLabel(T arg) {
        return ((Entity)arg).shouldRenderName() && ((Entity)arg).hasCustomName();
    }

    public abstract Identifier getTexture(T var1);

    public TextRenderer getFontRenderer() {
        return this.dispatcher.getTextRenderer();
    }

    protected void renderLabelIfPresent(T arg, Text arg2, MatrixStack arg3, VertexConsumerProvider arg4, int i) {
        double d = this.dispatcher.getSquaredDistanceToCamera((Entity)arg);
        if (d > 4096.0) {
            return;
        }
        boolean bl = !((Entity)arg).isSneaky();
        float f = ((Entity)arg).getHeight() + 0.5f;
        int j = "deadmau5".equals(arg2.getString()) ? -10 : 0;
        arg3.push();
        arg3.translate(0.0, f, 0.0);
        arg3.multiply(this.dispatcher.getRotation());
        arg3.scale(-0.025f, -0.025f, 0.025f);
        Matrix4f lv = arg3.peek().getModel();
        float g = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25f);
        int k = (int)(g * 255.0f) << 24;
        TextRenderer lv2 = this.getFontRenderer();
        float h = -lv2.getStringWidth(arg2) / 2;
        lv2.draw(arg2, h, (float)j, 0x20FFFFFF, false, lv, arg4, bl, k, i);
        if (bl) {
            lv2.draw(arg2, h, (float)j, -1, false, lv, arg4, false, 0, i);
        }
        arg3.pop();
    }

    public EntityRenderDispatcher getRenderManager() {
        return this.dispatcher;
    }
}

