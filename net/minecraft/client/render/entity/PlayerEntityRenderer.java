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
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.render.entity.feature.Deadmau5FeatureRenderer;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.feature.ShoulderParrotFeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckStingersFeatureRenderer;
import net.minecraft.client.render.entity.feature.TridentRiptideFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class PlayerEntityRenderer
extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    public PlayerEntityRenderer(EntityRenderDispatcher arg) {
        this(arg, false);
    }

    public PlayerEntityRenderer(EntityRenderDispatcher dispatcher, boolean bl) {
        super(dispatcher, new PlayerEntityModel(0.0f, bl), 0.5f);
        this.addFeature(new ArmorFeatureRenderer(this, new BipedEntityModel(0.5f), new BipedEntityModel(1.0f)));
        this.addFeature(new HeldItemFeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>(this));
        this.addFeature(new StuckArrowsFeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>(this));
        this.addFeature(new Deadmau5FeatureRenderer(this));
        this.addFeature(new CapeFeatureRenderer(this));
        this.addFeature(new HeadFeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>(this));
        this.addFeature(new ElytraFeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>(this));
        this.addFeature(new ShoulderParrotFeatureRenderer<AbstractClientPlayerEntity>(this));
        this.addFeature(new TridentRiptideFeatureRenderer<AbstractClientPlayerEntity>(this));
        this.addFeature(new StuckStingersFeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>(this));
    }

    @Override
    public void render(AbstractClientPlayerEntity arg, float f, float g, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        this.setModelPose(arg);
        super.render(arg, f, g, arg2, arg3, i);
    }

    @Override
    public Vec3d getPositionOffset(AbstractClientPlayerEntity arg, float f) {
        if (arg.isInSneakingPose()) {
            return new Vec3d(0.0, -0.125, 0.0);
        }
        return super.getPositionOffset(arg, f);
    }

    private void setModelPose(AbstractClientPlayerEntity arg) {
        PlayerEntityModel lv = (PlayerEntityModel)this.getModel();
        if (arg.isSpectator()) {
            lv.setVisible(false);
            lv.head.visible = true;
            lv.helmet.visible = true;
        } else {
            lv.setVisible(true);
            lv.helmet.visible = arg.isPartVisible(PlayerModelPart.HAT);
            lv.jacket.visible = arg.isPartVisible(PlayerModelPart.JACKET);
            lv.leftPantLeg.visible = arg.isPartVisible(PlayerModelPart.LEFT_PANTS_LEG);
            lv.rightPantLeg.visible = arg.isPartVisible(PlayerModelPart.RIGHT_PANTS_LEG);
            lv.leftSleeve.visible = arg.isPartVisible(PlayerModelPart.LEFT_SLEEVE);
            lv.rightSleeve.visible = arg.isPartVisible(PlayerModelPart.RIGHT_SLEEVE);
            lv.sneaking = arg.isInSneakingPose();
            BipedEntityModel.ArmPose lv2 = PlayerEntityRenderer.getArmPose(arg, Hand.MAIN_HAND);
            BipedEntityModel.ArmPose lv3 = PlayerEntityRenderer.getArmPose(arg, Hand.OFF_HAND);
            if (lv2.method_30156()) {
                BipedEntityModel.ArmPose armPose = lv3 = arg.getOffHandStack().isEmpty() ? BipedEntityModel.ArmPose.EMPTY : BipedEntityModel.ArmPose.ITEM;
            }
            if (arg.getMainArm() == Arm.RIGHT) {
                lv.rightArmPose = lv2;
                lv.leftArmPose = lv3;
            } else {
                lv.rightArmPose = lv3;
                lv.leftArmPose = lv2;
            }
        }
    }

    private static BipedEntityModel.ArmPose getArmPose(AbstractClientPlayerEntity arg, Hand arg2) {
        ItemStack lv = arg.getStackInHand(arg2);
        if (lv.isEmpty()) {
            return BipedEntityModel.ArmPose.EMPTY;
        }
        if (arg.getActiveHand() == arg2 && arg.getItemUseTimeLeft() > 0) {
            UseAction lv2 = lv.getUseAction();
            if (lv2 == UseAction.BLOCK) {
                return BipedEntityModel.ArmPose.BLOCK;
            }
            if (lv2 == UseAction.BOW) {
                return BipedEntityModel.ArmPose.BOW_AND_ARROW;
            }
            if (lv2 == UseAction.SPEAR) {
                return BipedEntityModel.ArmPose.THROW_SPEAR;
            }
            if (lv2 == UseAction.CROSSBOW && arg2 == arg.getActiveHand()) {
                return BipedEntityModel.ArmPose.CROSSBOW_CHARGE;
            }
        } else if (!arg.handSwinging && lv.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(lv)) {
            return BipedEntityModel.ArmPose.CROSSBOW_HOLD;
        }
        return BipedEntityModel.ArmPose.ITEM;
    }

    @Override
    public Identifier getTexture(AbstractClientPlayerEntity arg) {
        return arg.getSkinTexture();
    }

    @Override
    protected void scale(AbstractClientPlayerEntity arg, MatrixStack arg2, float f) {
        float g = 0.9375f;
        arg2.scale(0.9375f, 0.9375f, 0.9375f);
    }

    @Override
    protected void renderLabelIfPresent(AbstractClientPlayerEntity arg, Text arg2, MatrixStack arg3, VertexConsumerProvider arg4, int i) {
        Scoreboard lv;
        ScoreboardObjective lv2;
        double d = this.dispatcher.getSquaredDistanceToCamera(arg);
        arg3.push();
        if (d < 100.0 && (lv2 = (lv = arg.getScoreboard()).getObjectiveForSlot(2)) != null) {
            ScoreboardPlayerScore lv3 = lv.getPlayerScore(arg.getEntityName(), lv2);
            super.renderLabelIfPresent(arg, new LiteralText(Integer.toString(lv3.getScore())).append(" ").append(lv2.getDisplayName()), arg3, arg4, i);
            this.getFontRenderer().getClass();
            arg3.translate(0.0, 9.0f * 1.15f * 0.025f, 0.0);
        }
        super.renderLabelIfPresent(arg, arg2, arg3, arg4, i);
        arg3.pop();
    }

    public void renderRightArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player) {
        this.renderArm(matrices, vertexConsumers, light, player, ((PlayerEntityModel)this.model).rightArm, ((PlayerEntityModel)this.model).rightSleeve);
    }

    public void renderLeftArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player) {
        this.renderArm(matrices, vertexConsumers, light, player, ((PlayerEntityModel)this.model).leftArm, ((PlayerEntityModel)this.model).leftSleeve);
    }

    private void renderArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve) {
        PlayerEntityModel lv = (PlayerEntityModel)this.getModel();
        this.setModelPose(player);
        lv.handSwingProgress = 0.0f;
        lv.sneaking = false;
        lv.leaningPitch = 0.0f;
        lv.setAngles(player, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        arm.pitch = 0.0f;
        arm.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(player.getSkinTexture())), light, OverlayTexture.DEFAULT_UV);
        sleeve.pitch = 0.0f;
        sleeve.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(player.getSkinTexture())), light, OverlayTexture.DEFAULT_UV);
    }

    @Override
    protected void setupTransforms(AbstractClientPlayerEntity arg, MatrixStack arg2, float f, float g, float h) {
        float i = arg.getLeaningPitch(h);
        if (arg.isFallFlying()) {
            super.setupTransforms(arg, arg2, f, g, h);
            float j = (float)arg.getRoll() + h;
            float k = MathHelper.clamp(j * j / 100.0f, 0.0f, 1.0f);
            if (!arg.isUsingRiptide()) {
                arg2.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(k * (-90.0f - arg.pitch)));
            }
            Vec3d lv = arg.getRotationVec(h);
            Vec3d lv2 = arg.getVelocity();
            double d = Entity.squaredHorizontalLength(lv2);
            double e = Entity.squaredHorizontalLength(lv);
            if (d > 0.0 && e > 0.0) {
                double l = (lv2.x * lv.x + lv2.z * lv.z) / (Math.sqrt(d) * Math.sqrt(e));
                double m = lv2.x * lv.z - lv2.z * lv.x;
                arg2.multiply(Vector3f.POSITIVE_Y.getRadialQuaternion((float)(Math.signum(m) * Math.acos(l))));
            }
        } else if (i > 0.0f) {
            super.setupTransforms(arg, arg2, f, g, h);
            float n = arg.isTouchingWater() ? -90.0f - arg.pitch : -90.0f;
            float o = MathHelper.lerp(i, 0.0f, n);
            arg2.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(o));
            if (arg.isInSwimmingPose()) {
                arg2.translate(0.0, -1.0, 0.3f);
            }
        } else {
            super.setupTransforms(arg, arg2, f, g, h);
        }
    }
}

