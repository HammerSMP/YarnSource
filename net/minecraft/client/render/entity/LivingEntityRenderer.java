/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.render.entity;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public abstract class LivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>>
extends EntityRenderer<T>
implements FeatureRendererContext<T, M> {
    private static final Logger LOGGER = LogManager.getLogger();
    protected M model;
    protected final List<FeatureRenderer<T, M>> features = Lists.newArrayList();

    public LivingEntityRenderer(EntityRenderDispatcher dispatcher, M model, float shadowRadius) {
        super(dispatcher);
        this.model = model;
        this.shadowRadius = shadowRadius;
    }

    protected final boolean addFeature(FeatureRenderer<T, M> feature) {
        return this.features.add(feature);
    }

    @Override
    public M getModel() {
        return this.model;
    }

    @Override
    public void render(T arg, float f, float g, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        Direction lv2;
        arg2.push();
        ((EntityModel)this.model).handSwingProgress = this.getHandSwingProgress(arg, g);
        ((EntityModel)this.model).riding = ((Entity)arg).hasVehicle();
        ((EntityModel)this.model).child = ((LivingEntity)arg).isBaby();
        float h = MathHelper.lerpAngleDegrees(g, ((LivingEntity)arg).prevBodyYaw, ((LivingEntity)arg).bodyYaw);
        float j = MathHelper.lerpAngleDegrees(g, ((LivingEntity)arg).prevHeadYaw, ((LivingEntity)arg).headYaw);
        float k = j - h;
        if (((Entity)arg).hasVehicle() && ((Entity)arg).getVehicle() instanceof LivingEntity) {
            LivingEntity lv = (LivingEntity)((Entity)arg).getVehicle();
            h = MathHelper.lerpAngleDegrees(g, lv.prevBodyYaw, lv.bodyYaw);
            k = j - h;
            float l = MathHelper.wrapDegrees(k);
            if (l < -85.0f) {
                l = -85.0f;
            }
            if (l >= 85.0f) {
                l = 85.0f;
            }
            h = j - l;
            if (l * l > 2500.0f) {
                h += l * 0.2f;
            }
            k = j - h;
        }
        float m = MathHelper.lerp(g, ((LivingEntity)arg).prevPitch, ((LivingEntity)arg).pitch);
        if (((Entity)arg).getPose() == EntityPose.SLEEPING && (lv2 = ((LivingEntity)arg).getSleepingDirection()) != null) {
            float n = ((Entity)arg).getEyeHeight(EntityPose.STANDING) - 0.1f;
            arg2.translate((float)(-lv2.getOffsetX()) * n, 0.0, (float)(-lv2.getOffsetZ()) * n);
        }
        float o = this.getAnimationProgress(arg, g);
        this.setupTransforms(arg, arg2, o, h, g);
        arg2.scale(-1.0f, -1.0f, 1.0f);
        this.scale(arg, arg2, g);
        arg2.translate(0.0, -1.501f, 0.0);
        float p = 0.0f;
        float q = 0.0f;
        if (!((Entity)arg).hasVehicle() && ((LivingEntity)arg).isAlive()) {
            p = MathHelper.lerp(g, ((LivingEntity)arg).lastLimbDistance, ((LivingEntity)arg).limbDistance);
            q = ((LivingEntity)arg).limbAngle - ((LivingEntity)arg).limbDistance * (1.0f - g);
            if (((LivingEntity)arg).isBaby()) {
                q *= 3.0f;
            }
            if (p > 1.0f) {
                p = 1.0f;
            }
        }
        ((EntityModel)this.model).animateModel(arg, q, p, g);
        ((EntityModel)this.model).setAngles(arg, q, p, o, k, m);
        MinecraftClient lv3 = MinecraftClient.getInstance();
        boolean bl = this.isVisible(arg);
        boolean bl2 = !bl && !((Entity)arg).isInvisibleTo(lv3.player);
        boolean bl3 = lv3.method_27022((Entity)arg);
        RenderLayer lv4 = this.getRenderLayer(arg, bl, bl2, bl3);
        if (lv4 != null) {
            VertexConsumer lv5 = arg3.getBuffer(lv4);
            int r = LivingEntityRenderer.getOverlay(arg, this.getAnimationCounter(arg, g));
            ((Model)this.model).render(arg2, lv5, i, r, 1.0f, 1.0f, 1.0f, bl2 ? 0.15f : 1.0f);
        }
        if (!((Entity)arg).isSpectator()) {
            for (FeatureRenderer<T, M> lv6 : this.features) {
                lv6.render(arg2, arg3, i, arg, q, p, g, o, k, m);
            }
        }
        arg2.pop();
        super.render(arg, f, g, arg2, arg3, i);
    }

    @Nullable
    protected RenderLayer getRenderLayer(T entity, boolean showBody, boolean translucent, boolean bl3) {
        Identifier lv = this.getTexture(entity);
        if (translucent) {
            return RenderLayer.getItemEntityTranslucentCull(lv);
        }
        if (showBody) {
            return ((Model)this.model).getLayer(lv);
        }
        if (bl3) {
            return RenderLayer.getOutline(lv);
        }
        return null;
    }

    public static int getOverlay(LivingEntity entity, float whiteOverlayProgress) {
        return OverlayTexture.packUv(OverlayTexture.getU(whiteOverlayProgress), OverlayTexture.getV(entity.hurtTime > 0 || entity.deathTime > 0));
    }

    protected boolean isVisible(T entity) {
        return !((Entity)entity).isInvisible();
    }

    private static float getYaw(Direction direction) {
        switch (direction) {
            case SOUTH: {
                return 90.0f;
            }
            case WEST: {
                return 0.0f;
            }
            case NORTH: {
                return 270.0f;
            }
            case EAST: {
                return 180.0f;
            }
        }
        return 0.0f;
    }

    protected boolean isShaking(T entity) {
        return false;
    }

    protected void setupTransforms(T entity, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta) {
        String string;
        EntityPose lv;
        if (this.isShaking(entity)) {
            bodyYaw += (float)(Math.cos((double)((LivingEntity)entity).age * 3.25) * Math.PI * (double)0.4f);
        }
        if ((lv = ((Entity)entity).getPose()) != EntityPose.SLEEPING) {
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0f - bodyYaw));
        }
        if (((LivingEntity)entity).deathTime > 0) {
            float i = ((float)((LivingEntity)entity).deathTime + tickDelta - 1.0f) / 20.0f * 1.6f;
            if ((i = MathHelper.sqrt(i)) > 1.0f) {
                i = 1.0f;
            }
            matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(i * this.getLyingAngle(entity)));
        } else if (((LivingEntity)entity).isUsingRiptide()) {
            matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-90.0f - ((LivingEntity)entity).pitch));
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(((float)((LivingEntity)entity).age + tickDelta) * -75.0f));
        } else if (lv == EntityPose.SLEEPING) {
            Direction lv2 = ((LivingEntity)entity).getSleepingDirection();
            float j = lv2 != null ? LivingEntityRenderer.getYaw(lv2) : bodyYaw;
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(j));
            matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(this.getLyingAngle(entity)));
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(270.0f));
        } else if ((((Entity)entity).hasCustomName() || entity instanceof PlayerEntity) && ("Dinnerbone".equals(string = Formatting.strip(((Entity)entity).getName().getString())) || "Grumm".equals(string)) && (!(entity instanceof PlayerEntity) || ((PlayerEntity)entity).isPartVisible(PlayerModelPart.CAPE))) {
            matrices.translate(0.0, ((Entity)entity).getHeight() + 0.1f, 0.0);
            matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0f));
        }
    }

    protected float getHandSwingProgress(T entity, float tickDelta) {
        return ((LivingEntity)entity).getHandSwingProgress(tickDelta);
    }

    protected float getAnimationProgress(T entity, float tickDelta) {
        return (float)((LivingEntity)entity).age + tickDelta;
    }

    protected float getLyingAngle(T entity) {
        return 90.0f;
    }

    protected float getAnimationCounter(T entity, float tickDelta) {
        return 0.0f;
    }

    protected void scale(T entity, MatrixStack matrices, float amount) {
    }

    @Override
    protected boolean hasLabel(T arg) {
        boolean bl;
        float f;
        double d = this.dispatcher.getSquaredDistanceToCamera((Entity)arg);
        float f2 = f = ((Entity)arg).isSneaky() ? 32.0f : 64.0f;
        if (d >= (double)(f * f)) {
            return false;
        }
        MinecraftClient lv = MinecraftClient.getInstance();
        ClientPlayerEntity lv2 = lv.player;
        boolean bl2 = bl = !((Entity)arg).isInvisibleTo(lv2);
        if (arg != lv2) {
            AbstractTeam lv3 = ((Entity)arg).getScoreboardTeam();
            AbstractTeam lv4 = lv2.getScoreboardTeam();
            if (lv3 != null) {
                AbstractTeam.VisibilityRule lv5 = lv3.getNameTagVisibilityRule();
                switch (lv5) {
                    case ALWAYS: {
                        return bl;
                    }
                    case NEVER: {
                        return false;
                    }
                    case HIDE_FOR_OTHER_TEAMS: {
                        return lv4 == null ? bl : lv3.isEqual(lv4) && (lv3.shouldShowFriendlyInvisibles() || bl);
                    }
                    case HIDE_FOR_OWN_TEAM: {
                        return lv4 == null ? bl : !lv3.isEqual(lv4) && bl;
                    }
                }
                return true;
            }
        }
        return MinecraftClient.isHudEnabled() && arg != lv.getCameraEntity() && bl && !((Entity)arg).hasPassengers();
    }
}

