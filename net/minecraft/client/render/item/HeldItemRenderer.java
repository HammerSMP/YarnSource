/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.item;

import com.google.common.base.MoreObjects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class HeldItemRenderer {
    private static final RenderLayer MAP_BACKGROUND = RenderLayer.getText(new Identifier("textures/map/map_background.png"));
    private static final RenderLayer MAP_BACKGROUND_CHECKERBOARD = RenderLayer.getText(new Identifier("textures/map/map_background_checkerboard.png"));
    private final MinecraftClient client;
    private ItemStack mainHand = ItemStack.EMPTY;
    private ItemStack offHand = ItemStack.EMPTY;
    private float equipProgressMainHand;
    private float prevEquipProgressMainHand;
    private float equipProgressOffHand;
    private float prevEquipProgressOffHand;
    private final EntityRenderDispatcher renderManager;
    private final ItemRenderer itemRenderer;

    public HeldItemRenderer(MinecraftClient client) {
        this.client = client;
        this.renderManager = client.getEntityRenderManager();
        this.itemRenderer = client.getItemRenderer();
    }

    public void renderItem(LivingEntity entity, ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (stack.isEmpty()) {
            return;
        }
        this.itemRenderer.renderItem(entity, stack, renderMode, leftHanded, matrices, vertexConsumers, entity.world, light, OverlayTexture.DEFAULT_UV);
    }

    private float getMapAngle(float tickDelta) {
        float g = 1.0f - tickDelta / 45.0f + 0.1f;
        g = MathHelper.clamp(g, 0.0f, 1.0f);
        g = -MathHelper.cos(g * (float)Math.PI) * 0.5f + 0.5f;
        return g;
    }

    private void renderArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Arm arm) {
        this.client.getTextureManager().bindTexture(this.client.player.getSkinTexture());
        PlayerEntityRenderer lv = (PlayerEntityRenderer)this.renderManager.getRenderer(this.client.player);
        matrices.push();
        float f = arm == Arm.RIGHT ? 1.0f : -1.0f;
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(92.0f));
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(45.0f));
        matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(f * -41.0f));
        matrices.translate(f * 0.3f, -1.1f, 0.45f);
        if (arm == Arm.RIGHT) {
            lv.renderRightArm(matrices, vertexConsumers, light, this.client.player);
        } else {
            lv.renderLeftArm(matrices, vertexConsumers, light, this.client.player);
        }
        matrices.pop();
    }

    private void renderMapInOneHand(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float equipProgress, Arm arm, float swingProgress, ItemStack stack) {
        float h = arm == Arm.RIGHT ? 1.0f : -1.0f;
        matrices.translate(h * 0.125f, -0.125, 0.0);
        if (!this.client.player.isInvisible()) {
            matrices.push();
            matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(h * 10.0f));
            this.renderArmHoldingItem(matrices, vertexConsumers, light, equipProgress, swingProgress, arm);
            matrices.pop();
        }
        matrices.push();
        matrices.translate(h * 0.51f, -0.08f + equipProgress * -1.2f, -0.75);
        float j = MathHelper.sqrt(swingProgress);
        float k = MathHelper.sin(j * (float)Math.PI);
        float l = -0.5f * k;
        float m = 0.4f * MathHelper.sin(j * ((float)Math.PI * 2));
        float n = -0.3f * MathHelper.sin(swingProgress * (float)Math.PI);
        matrices.translate(h * l, m - 0.3f * k, n);
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(k * -45.0f));
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(h * k * -30.0f));
        this.renderFirstPersonMap(matrices, vertexConsumers, light, stack);
        matrices.pop();
    }

    private void renderMapInBothHands(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float pitch, float equipProgress, float swingProgress) {
        float j = MathHelper.sqrt(swingProgress);
        float k = -0.2f * MathHelper.sin(swingProgress * (float)Math.PI);
        float l = -0.4f * MathHelper.sin(j * (float)Math.PI);
        matrices.translate(0.0, -k / 2.0f, l);
        float m = this.getMapAngle(pitch);
        matrices.translate(0.0, 0.04f + equipProgress * -1.2f + m * -0.5f, -0.72f);
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(m * -85.0f));
        if (!this.client.player.isInvisible()) {
            matrices.push();
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90.0f));
            this.renderArm(matrices, vertexConsumers, light, Arm.RIGHT);
            this.renderArm(matrices, vertexConsumers, light, Arm.LEFT);
            matrices.pop();
        }
        float n = MathHelper.sin(j * (float)Math.PI);
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(n * 20.0f));
        matrices.scale(2.0f, 2.0f, 2.0f);
        this.renderFirstPersonMap(matrices, vertexConsumers, light, this.mainHand);
    }

    private void renderFirstPersonMap(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int swingProgress, ItemStack stack) {
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0f));
        matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0f));
        matrices.scale(0.38f, 0.38f, 0.38f);
        matrices.translate(-0.5, -0.5, 0.0);
        matrices.scale(0.0078125f, 0.0078125f, 0.0078125f);
        MapState lv = FilledMapItem.getOrCreateMapState(stack, this.client.world);
        VertexConsumer lv2 = vertexConsumers.getBuffer(lv == null ? MAP_BACKGROUND : MAP_BACKGROUND_CHECKERBOARD);
        Matrix4f lv3 = matrices.peek().getModel();
        lv2.vertex(lv3, -7.0f, 135.0f, 0.0f).color(255, 255, 255, 255).texture(0.0f, 1.0f).light(swingProgress).next();
        lv2.vertex(lv3, 135.0f, 135.0f, 0.0f).color(255, 255, 255, 255).texture(1.0f, 1.0f).light(swingProgress).next();
        lv2.vertex(lv3, 135.0f, -7.0f, 0.0f).color(255, 255, 255, 255).texture(1.0f, 0.0f).light(swingProgress).next();
        lv2.vertex(lv3, -7.0f, -7.0f, 0.0f).color(255, 255, 255, 255).texture(0.0f, 0.0f).light(swingProgress).next();
        if (lv != null) {
            this.client.gameRenderer.getMapRenderer().draw(matrices, vertexConsumers, lv, false, swingProgress);
        }
    }

    private void renderArmHoldingItem(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float equipProgress, float swingProgress, Arm arm) {
        boolean bl = arm != Arm.LEFT;
        float h = bl ? 1.0f : -1.0f;
        float j = MathHelper.sqrt(swingProgress);
        float k = -0.3f * MathHelper.sin(j * (float)Math.PI);
        float l = 0.4f * MathHelper.sin(j * ((float)Math.PI * 2));
        float m = -0.4f * MathHelper.sin(swingProgress * (float)Math.PI);
        matrices.translate(h * (k + 0.64000005f), l + -0.6f + equipProgress * -0.6f, m + -0.71999997f);
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(h * 45.0f));
        float n = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
        float o = MathHelper.sin(j * (float)Math.PI);
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(h * o * 70.0f));
        matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(h * n * -20.0f));
        ClientPlayerEntity lv = this.client.player;
        this.client.getTextureManager().bindTexture(lv.getSkinTexture());
        matrices.translate(h * -1.0f, 3.6f, 3.5);
        matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(h * 120.0f));
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(200.0f));
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(h * -135.0f));
        matrices.translate(h * 5.6f, 0.0, 0.0);
        PlayerEntityRenderer lv2 = (PlayerEntityRenderer)this.renderManager.getRenderer(lv);
        if (bl) {
            lv2.renderRightArm(matrices, vertexConsumers, light, lv);
        } else {
            lv2.renderLeftArm(matrices, vertexConsumers, light, lv);
        }
    }

    private void applyEatOrDrinkTransformation(MatrixStack matrices, float tickDelta, Arm arm, ItemStack stack) {
        float g = (float)this.client.player.getItemUseTimeLeft() - tickDelta + 1.0f;
        float h = g / (float)stack.getMaxUseTime();
        if (h < 0.8f) {
            float i = MathHelper.abs(MathHelper.cos(g / 4.0f * (float)Math.PI) * 0.1f);
            matrices.translate(0.0, i, 0.0);
        }
        float j = 1.0f - (float)Math.pow(h, 27.0);
        int k = arm == Arm.RIGHT ? 1 : -1;
        matrices.translate(j * 0.6f * (float)k, j * -0.5f, j * 0.0f);
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)k * j * 90.0f));
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(j * 10.0f));
        matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float)k * j * 30.0f));
    }

    private void applySwingOffset(MatrixStack matrices, Arm arm, float swingProgress) {
        int i = arm == Arm.RIGHT ? 1 : -1;
        float g = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)i * (45.0f + g * -20.0f)));
        float h = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float)Math.PI);
        matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float)i * h * -20.0f));
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(h * -80.0f));
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)i * -45.0f));
    }

    private void applyEquipOffset(MatrixStack matrices, Arm arm, float equipProgress) {
        int i = arm == Arm.RIGHT ? 1 : -1;
        matrices.translate((float)i * 0.56f, -0.52f + equipProgress * -0.6f, -0.72f);
    }

    public void renderItem(float tickDelta, MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, ClientPlayerEntity player, int light) {
        float g = player.getHandSwingProgress(tickDelta);
        Hand lv = (Hand)((Object)MoreObjects.firstNonNull((Object)((Object)player.preferredHand), (Object)((Object)Hand.MAIN_HAND)));
        float h = MathHelper.lerp(tickDelta, player.prevPitch, player.pitch);
        boolean bl = true;
        boolean bl2 = true;
        if (player.isUsingItem()) {
            ItemStack lv4;
            Hand lv3;
            ItemStack lv2 = player.getActiveItem();
            if (lv2.getItem() == Items.BOW || lv2.getItem() == Items.CROSSBOW) {
                bl = player.getActiveHand() == Hand.MAIN_HAND;
                boolean bl3 = bl2 = !bl;
            }
            if ((lv3 = player.getActiveHand()) == Hand.MAIN_HAND && (lv4 = player.getOffHandStack()).getItem() == Items.CROSSBOW && CrossbowItem.isCharged(lv4)) {
                bl2 = false;
            }
        } else {
            ItemStack lv5 = player.getMainHandStack();
            ItemStack lv6 = player.getOffHandStack();
            if (lv5.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(lv5)) {
                boolean bl4 = bl2 = !bl;
            }
            if (lv6.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(lv6)) {
                bl = !lv5.isEmpty();
                bl2 = !bl;
            }
        }
        float j = MathHelper.lerp(tickDelta, player.lastRenderPitch, player.renderPitch);
        float k = MathHelper.lerp(tickDelta, player.lastRenderYaw, player.renderYaw);
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion((player.getPitch(tickDelta) - j) * 0.1f));
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((player.getYaw(tickDelta) - k) * 0.1f));
        if (bl) {
            float l = lv == Hand.MAIN_HAND ? g : 0.0f;
            float m = 1.0f - MathHelper.lerp(tickDelta, this.prevEquipProgressMainHand, this.equipProgressMainHand);
            this.renderFirstPersonItem(player, tickDelta, h, Hand.MAIN_HAND, l, this.mainHand, m, matrices, vertexConsumers, light);
        }
        if (bl2) {
            float n = lv == Hand.OFF_HAND ? g : 0.0f;
            float o = 1.0f - MathHelper.lerp(tickDelta, this.prevEquipProgressOffHand, this.equipProgressOffHand);
            this.renderFirstPersonItem(player, tickDelta, h, Hand.OFF_HAND, n, this.offHand, o, matrices, vertexConsumers, light);
        }
        vertexConsumers.draw();
    }

    private void renderFirstPersonItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        boolean bl = hand == Hand.MAIN_HAND;
        Arm lv = bl ? player.getMainArm() : player.getMainArm().getOpposite();
        matrices.push();
        if (item.isEmpty()) {
            if (bl && !player.isInvisible()) {
                this.renderArmHoldingItem(matrices, vertexConsumers, light, equipProgress, swingProgress, lv);
            }
        } else if (item.getItem() == Items.FILLED_MAP) {
            if (bl && this.offHand.isEmpty()) {
                this.renderMapInBothHands(matrices, vertexConsumers, light, pitch, equipProgress, swingProgress);
            } else {
                this.renderMapInOneHand(matrices, vertexConsumers, light, equipProgress, lv, swingProgress, item);
            }
        } else if (item.getItem() == Items.CROSSBOW) {
            int k;
            boolean bl2 = CrossbowItem.isCharged(item);
            boolean bl3 = lv == Arm.RIGHT;
            int n = k = bl3 ? 1 : -1;
            if (player.isUsingItem() && player.getItemUseTimeLeft() > 0 && player.getActiveHand() == hand) {
                this.applyEquipOffset(matrices, lv, equipProgress);
                matrices.translate((float)k * -0.4785682f, -0.094387f, 0.05731530860066414);
                matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-11.935f));
                matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)k * 65.3f));
                matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float)k * -9.785f));
                float l = (float)item.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - tickDelta + 1.0f);
                float m = l / (float)CrossbowItem.getPullTime(item);
                if (m > 1.0f) {
                    m = 1.0f;
                }
                if (m > 0.1f) {
                    float n2 = MathHelper.sin((l - 0.1f) * 1.3f);
                    float o = m - 0.1f;
                    float p = n2 * o;
                    matrices.translate(p * 0.0f, p * 0.004f, p * 0.0f);
                }
                matrices.translate(m * 0.0f, m * 0.0f, m * 0.04f);
                matrices.scale(1.0f, 1.0f, 1.0f + m * 0.2f);
                matrices.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion((float)k * 45.0f));
            } else {
                float q = -0.4f * MathHelper.sin(MathHelper.sqrt(swingProgress) * (float)Math.PI);
                float r = 0.2f * MathHelper.sin(MathHelper.sqrt(swingProgress) * ((float)Math.PI * 2));
                float s = -0.2f * MathHelper.sin(swingProgress * (float)Math.PI);
                matrices.translate((float)k * q, r, s);
                this.applyEquipOffset(matrices, lv, equipProgress);
                this.applySwingOffset(matrices, lv, swingProgress);
                if (bl2 && swingProgress < 0.001f) {
                    matrices.translate((float)k * -0.641864f, 0.0, 0.0);
                    matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)k * 10.0f));
                }
            }
            this.renderItem(player, item, bl3 ? ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND : ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND, !bl3, matrices, vertexConsumers, light);
        } else {
            boolean bl4;
            boolean bl2 = bl4 = lv == Arm.RIGHT;
            if (player.isUsingItem() && player.getItemUseTimeLeft() > 0 && player.getActiveHand() == hand) {
                int t = bl4 ? 1 : -1;
                switch (item.getUseAction()) {
                    case NONE: {
                        this.applyEquipOffset(matrices, lv, equipProgress);
                        break;
                    }
                    case EAT: 
                    case DRINK: {
                        this.applyEatOrDrinkTransformation(matrices, tickDelta, lv, item);
                        this.applyEquipOffset(matrices, lv, equipProgress);
                        break;
                    }
                    case BLOCK: {
                        this.applyEquipOffset(matrices, lv, equipProgress);
                        break;
                    }
                    case BOW: {
                        this.applyEquipOffset(matrices, lv, equipProgress);
                        matrices.translate((float)t * -0.2785682f, 0.18344387412071228, 0.15731531381607056);
                        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-13.935f));
                        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)t * 35.3f));
                        matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float)t * -9.785f));
                        float u = (float)item.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - tickDelta + 1.0f);
                        float v = u / 20.0f;
                        v = (v * v + v * 2.0f) / 3.0f;
                        if (v > 1.0f) {
                            v = 1.0f;
                        }
                        if (v > 0.1f) {
                            float w = MathHelper.sin((u - 0.1f) * 1.3f);
                            float x = v - 0.1f;
                            float y = w * x;
                            matrices.translate(y * 0.0f, y * 0.004f, y * 0.0f);
                        }
                        matrices.translate(v * 0.0f, v * 0.0f, v * 0.04f);
                        matrices.scale(1.0f, 1.0f, 1.0f + v * 0.2f);
                        matrices.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion((float)t * 45.0f));
                        break;
                    }
                    case SPEAR: {
                        this.applyEquipOffset(matrices, lv, equipProgress);
                        matrices.translate((float)t * -0.5f, 0.7f, 0.1f);
                        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-55.0f));
                        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)t * 35.3f));
                        matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float)t * -9.785f));
                        float z = (float)item.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - tickDelta + 1.0f);
                        float aa = z / 10.0f;
                        if (aa > 1.0f) {
                            aa = 1.0f;
                        }
                        if (aa > 0.1f) {
                            float ab = MathHelper.sin((z - 0.1f) * 1.3f);
                            float ac = aa - 0.1f;
                            float ad = ab * ac;
                            matrices.translate(ad * 0.0f, ad * 0.004f, ad * 0.0f);
                        }
                        matrices.translate(0.0, 0.0, aa * 0.2f);
                        matrices.scale(1.0f, 1.0f, 1.0f + aa * 0.2f);
                        matrices.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion((float)t * 45.0f));
                        break;
                    }
                }
            } else if (player.isUsingRiptide()) {
                this.applyEquipOffset(matrices, lv, equipProgress);
                int ae = bl4 ? 1 : -1;
                matrices.translate((float)ae * -0.4f, 0.8f, 0.3f);
                matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)ae * 65.0f));
                matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float)ae * -85.0f));
            } else {
                float af = -0.4f * MathHelper.sin(MathHelper.sqrt(swingProgress) * (float)Math.PI);
                float ag = 0.2f * MathHelper.sin(MathHelper.sqrt(swingProgress) * ((float)Math.PI * 2));
                float ah = -0.2f * MathHelper.sin(swingProgress * (float)Math.PI);
                int ai = bl4 ? 1 : -1;
                matrices.translate((float)ai * af, ag, ah);
                this.applyEquipOffset(matrices, lv, equipProgress);
                this.applySwingOffset(matrices, lv, swingProgress);
            }
            this.renderItem(player, item, bl4 ? ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND : ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND, !bl4, matrices, vertexConsumers, light);
        }
        matrices.pop();
    }

    public void updateHeldItems() {
        this.prevEquipProgressMainHand = this.equipProgressMainHand;
        this.prevEquipProgressOffHand = this.equipProgressOffHand;
        ClientPlayerEntity lv = this.client.player;
        ItemStack lv2 = lv.getMainHandStack();
        ItemStack lv3 = lv.getOffHandStack();
        if (ItemStack.areEqual(this.mainHand, lv2)) {
            this.mainHand = lv2;
        }
        if (ItemStack.areEqual(this.offHand, lv3)) {
            this.offHand = lv3;
        }
        if (lv.isRiding()) {
            this.equipProgressMainHand = MathHelper.clamp(this.equipProgressMainHand - 0.4f, 0.0f, 1.0f);
            this.equipProgressOffHand = MathHelper.clamp(this.equipProgressOffHand - 0.4f, 0.0f, 1.0f);
        } else {
            float f = lv.getAttackCooldownProgress(1.0f);
            this.equipProgressMainHand += MathHelper.clamp((this.mainHand == lv2 ? f * f * f : 0.0f) - this.equipProgressMainHand, -0.4f, 0.4f);
            this.equipProgressOffHand += MathHelper.clamp((float)(this.offHand == lv3 ? 1 : 0) - this.equipProgressOffHand, -0.4f, 0.4f);
        }
        if (this.equipProgressMainHand < 0.1f) {
            this.mainHand = lv2;
        }
        if (this.equipProgressOffHand < 0.1f) {
            this.offHand = lv3;
        }
    }

    public void resetEquipProgress(Hand hand) {
        if (hand == Hand.MAIN_HAND) {
            this.equipProgressMainHand = 0.0f;
        } else {
            this.equipProgressOffHand = 0.0f;
        }
    }
}

