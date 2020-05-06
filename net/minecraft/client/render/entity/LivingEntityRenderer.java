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

    public LivingEntityRenderer(EntityRenderDispatcher arg, M arg2, float f) {
        super(arg);
        this.model = arg2;
        this.shadowRadius = f;
    }

    protected final boolean addFeature(FeatureRenderer<T, M> arg) {
        return this.features.add(arg);
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
    protected RenderLayer getRenderLayer(T arg, boolean bl, boolean bl2, boolean bl3) {
        Identifier lv = this.getTexture(arg);
        if (bl2) {
            return RenderLayer.getEntityTranslucent(lv);
        }
        if (bl) {
            return ((Model)this.model).getLayer(lv);
        }
        if (bl3) {
            return RenderLayer.getOutline(lv);
        }
        return null;
    }

    public static int getOverlay(LivingEntity arg, float f) {
        return OverlayTexture.packUv(OverlayTexture.getU(f), OverlayTexture.getV(arg.hurtTime > 0 || arg.deathTime > 0));
    }

    protected boolean isVisible(T arg) {
        return !((Entity)arg).isInvisible();
    }

    private static float getYaw(Direction arg) {
        switch (arg) {
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

    protected boolean isShaking(T arg) {
        return false;
    }

    protected void setupTransforms(T arg, MatrixStack arg2, float f, float g, float h) {
        String string;
        EntityPose lv;
        if (this.isShaking(arg)) {
            g += (float)(Math.cos((double)((LivingEntity)arg).age * 3.25) * Math.PI * (double)0.4f);
        }
        if ((lv = ((Entity)arg).getPose()) != EntityPose.SLEEPING) {
            arg2.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0f - g));
        }
        if (((LivingEntity)arg).deathTime > 0) {
            float i = ((float)((LivingEntity)arg).deathTime + h - 1.0f) / 20.0f * 1.6f;
            if ((i = MathHelper.sqrt(i)) > 1.0f) {
                i = 1.0f;
            }
            arg2.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(i * this.getLyingAngle(arg)));
        } else if (((LivingEntity)arg).isUsingRiptide()) {
            arg2.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-90.0f - ((LivingEntity)arg).pitch));
            arg2.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(((float)((LivingEntity)arg).age + h) * -75.0f));
        } else if (lv == EntityPose.SLEEPING) {
            Direction lv2 = ((LivingEntity)arg).getSleepingDirection();
            float j = lv2 != null ? LivingEntityRenderer.getYaw(lv2) : g;
            arg2.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(j));
            arg2.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(this.getLyingAngle(arg)));
            arg2.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(270.0f));
        } else if ((((Entity)arg).hasCustomName() || arg instanceof PlayerEntity) && ("Dinnerbone".equals(string = Formatting.strip(((Entity)arg).getName().getString())) || "Grumm".equals(string)) && (!(arg instanceof PlayerEntity) || ((PlayerEntity)arg).isPartVisible(PlayerModelPart.CAPE))) {
            arg2.translate(0.0, ((Entity)arg).getHeight() + 0.1f, 0.0);
            arg2.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0f));
        }
    }

    protected float getHandSwingProgress(T arg, float f) {
        return ((LivingEntity)arg).getHandSwingProgress(f);
    }

    protected float getAnimationProgress(T arg, float f) {
        return (float)((LivingEntity)arg).age + f;
    }

    protected float getLyingAngle(T arg) {
        return 90.0f;
    }

    protected float getAnimationCounter(T arg, float f) {
        return 0.0f;
    }

    protected void scale(T arg, MatrixStack arg2, float f) {
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

