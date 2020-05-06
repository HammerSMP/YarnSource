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

    public HeldItemRenderer(MinecraftClient arg) {
        this.client = arg;
        this.renderManager = arg.getEntityRenderManager();
        this.itemRenderer = arg.getItemRenderer();
    }

    public void renderItem(LivingEntity arg, ItemStack arg2, ModelTransformation.Mode arg3, boolean bl, MatrixStack arg4, VertexConsumerProvider arg5, int i) {
        if (arg2.isEmpty()) {
            return;
        }
        this.itemRenderer.renderItem(arg, arg2, arg3, bl, arg4, arg5, arg.world, i, OverlayTexture.DEFAULT_UV);
    }

    private float getMapAngle(float f) {
        float g = 1.0f - f / 45.0f + 0.1f;
        g = MathHelper.clamp(g, 0.0f, 1.0f);
        g = -MathHelper.cos(g * (float)Math.PI) * 0.5f + 0.5f;
        return g;
    }

    private void renderArm(MatrixStack arg, VertexConsumerProvider arg2, int i, Arm arg3) {
        this.client.getTextureManager().bindTexture(this.client.player.getSkinTexture());
        PlayerEntityRenderer lv = (PlayerEntityRenderer)this.renderManager.getRenderer(this.client.player);
        arg.push();
        float f = arg3 == Arm.RIGHT ? 1.0f : -1.0f;
        arg.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(92.0f));
        arg.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(45.0f));
        arg.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(f * -41.0f));
        arg.translate(f * 0.3f, -1.1f, 0.45f);
        if (arg3 == Arm.RIGHT) {
            lv.renderRightArm(arg, arg2, i, this.client.player);
        } else {
            lv.renderLeftArm(arg, arg2, i, this.client.player);
        }
        arg.pop();
    }

    private void renderMapInOneHand(MatrixStack arg, VertexConsumerProvider arg2, int i, float f, Arm arg3, float g, ItemStack arg4) {
        float h = arg3 == Arm.RIGHT ? 1.0f : -1.0f;
        arg.translate(h * 0.125f, -0.125, 0.0);
        if (!this.client.player.isInvisible()) {
            arg.push();
            arg.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(h * 10.0f));
            this.renderArmHoldingItem(arg, arg2, i, f, g, arg3);
            arg.pop();
        }
        arg.push();
        arg.translate(h * 0.51f, -0.08f + f * -1.2f, -0.75);
        float j = MathHelper.sqrt(g);
        float k = MathHelper.sin(j * (float)Math.PI);
        float l = -0.5f * k;
        float m = 0.4f * MathHelper.sin(j * ((float)Math.PI * 2));
        float n = -0.3f * MathHelper.sin(g * (float)Math.PI);
        arg.translate(h * l, m - 0.3f * k, n);
        arg.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(k * -45.0f));
        arg.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(h * k * -30.0f));
        this.renderFirstPersonMap(arg, arg2, i, arg4);
        arg.pop();
    }

    private void renderMapInBothHands(MatrixStack arg, VertexConsumerProvider arg2, int i, float f, float g, float h) {
        float j = MathHelper.sqrt(h);
        float k = -0.2f * MathHelper.sin(h * (float)Math.PI);
        float l = -0.4f * MathHelper.sin(j * (float)Math.PI);
        arg.translate(0.0, -k / 2.0f, l);
        float m = this.getMapAngle(f);
        arg.translate(0.0, 0.04f + g * -1.2f + m * -0.5f, -0.72f);
        arg.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(m * -85.0f));
        if (!this.client.player.isInvisible()) {
            arg.push();
            arg.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90.0f));
            this.renderArm(arg, arg2, i, Arm.RIGHT);
            this.renderArm(arg, arg2, i, Arm.LEFT);
            arg.pop();
        }
        float n = MathHelper.sin(j * (float)Math.PI);
        arg.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(n * 20.0f));
        arg.scale(2.0f, 2.0f, 2.0f);
        this.renderFirstPersonMap(arg, arg2, i, this.mainHand);
    }

    private void renderFirstPersonMap(MatrixStack arg, VertexConsumerProvider arg2, int i, ItemStack arg3) {
        arg.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0f));
        arg.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0f));
        arg.scale(0.38f, 0.38f, 0.38f);
        arg.translate(-0.5, -0.5, 0.0);
        arg.scale(0.0078125f, 0.0078125f, 0.0078125f);
        MapState lv = FilledMapItem.getOrCreateMapState(arg3, this.client.world);
        VertexConsumer lv2 = arg2.getBuffer(lv == null ? MAP_BACKGROUND : MAP_BACKGROUND_CHECKERBOARD);
        Matrix4f lv3 = arg.peek().getModel();
        lv2.vertex(lv3, -7.0f, 135.0f, 0.0f).color(255, 255, 255, 255).texture(0.0f, 1.0f).light(i).next();
        lv2.vertex(lv3, 135.0f, 135.0f, 0.0f).color(255, 255, 255, 255).texture(1.0f, 1.0f).light(i).next();
        lv2.vertex(lv3, 135.0f, -7.0f, 0.0f).color(255, 255, 255, 255).texture(1.0f, 0.0f).light(i).next();
        lv2.vertex(lv3, -7.0f, -7.0f, 0.0f).color(255, 255, 255, 255).texture(0.0f, 0.0f).light(i).next();
        if (lv != null) {
            this.client.gameRenderer.getMapRenderer().draw(arg, arg2, lv, false, i);
        }
    }

    private void renderArmHoldingItem(MatrixStack arg, VertexConsumerProvider arg2, int i, float f, float g, Arm arg3) {
        boolean bl = arg3 != Arm.LEFT;
        float h = bl ? 1.0f : -1.0f;
        float j = MathHelper.sqrt(g);
        float k = -0.3f * MathHelper.sin(j * (float)Math.PI);
        float l = 0.4f * MathHelper.sin(j * ((float)Math.PI * 2));
        float m = -0.4f * MathHelper.sin(g * (float)Math.PI);
        arg.translate(h * (k + 0.64000005f), l + -0.6f + f * -0.6f, m + -0.71999997f);
        arg.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(h * 45.0f));
        float n = MathHelper.sin(g * g * (float)Math.PI);
        float o = MathHelper.sin(j * (float)Math.PI);
        arg.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(h * o * 70.0f));
        arg.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(h * n * -20.0f));
        ClientPlayerEntity lv = this.client.player;
        this.client.getTextureManager().bindTexture(lv.getSkinTexture());
        arg.translate(h * -1.0f, 3.6f, 3.5);
        arg.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(h * 120.0f));
        arg.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(200.0f));
        arg.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(h * -135.0f));
        arg.translate(h * 5.6f, 0.0, 0.0);
        PlayerEntityRenderer lv2 = (PlayerEntityRenderer)this.renderManager.getRenderer(lv);
        if (bl) {
            lv2.renderRightArm(arg, arg2, i, lv);
        } else {
            lv2.renderLeftArm(arg, arg2, i, lv);
        }
    }

    private void applyEatOrDrinkTransformation(MatrixStack arg, float f, Arm arg2, ItemStack arg3) {
        float g = (float)this.client.player.getItemUseTimeLeft() - f + 1.0f;
        float h = g / (float)arg3.getMaxUseTime();
        if (h < 0.8f) {
            float i = MathHelper.abs(MathHelper.cos(g / 4.0f * (float)Math.PI) * 0.1f);
            arg.translate(0.0, i, 0.0);
        }
        float j = 1.0f - (float)Math.pow(h, 27.0);
        int k = arg2 == Arm.RIGHT ? 1 : -1;
        arg.translate(j * 0.6f * (float)k, j * -0.5f, j * 0.0f);
        arg.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)k * j * 90.0f));
        arg.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(j * 10.0f));
        arg.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float)k * j * 30.0f));
    }

    private void applySwingOffset(MatrixStack arg, Arm arg2, float f) {
        int i = arg2 == Arm.RIGHT ? 1 : -1;
        float g = MathHelper.sin(f * f * (float)Math.PI);
        arg.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)i * (45.0f + g * -20.0f)));
        float h = MathHelper.sin(MathHelper.sqrt(f) * (float)Math.PI);
        arg.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float)i * h * -20.0f));
        arg.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(h * -80.0f));
        arg.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)i * -45.0f));
    }

    private void applyEquipOffset(MatrixStack arg, Arm arg2, float f) {
        int i = arg2 == Arm.RIGHT ? 1 : -1;
        arg.translate((float)i * 0.56f, -0.52f + f * -0.6f, -0.72f);
    }

    public void renderItem(float f, MatrixStack arg, VertexConsumerProvider.Immediate arg2, ClientPlayerEntity arg3, int i) {
        float g = arg3.getHandSwingProgress(f);
        Hand lv = (Hand)((Object)MoreObjects.firstNonNull((Object)((Object)arg3.preferredHand), (Object)((Object)Hand.MAIN_HAND)));
        float h = MathHelper.lerp(f, arg3.prevPitch, arg3.pitch);
        boolean bl = true;
        boolean bl2 = true;
        if (arg3.isUsingItem()) {
            ItemStack lv4;
            Hand lv3;
            ItemStack lv2 = arg3.getActiveItem();
            if (lv2.getItem() == Items.BOW || lv2.getItem() == Items.CROSSBOW) {
                bl = arg3.getActiveHand() == Hand.MAIN_HAND;
                boolean bl3 = bl2 = !bl;
            }
            if ((lv3 = arg3.getActiveHand()) == Hand.MAIN_HAND && (lv4 = arg3.getOffHandStack()).getItem() == Items.CROSSBOW && CrossbowItem.isCharged(lv4)) {
                bl2 = false;
            }
        } else {
            ItemStack lv5 = arg3.getMainHandStack();
            ItemStack lv6 = arg3.getOffHandStack();
            if (lv5.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(lv5)) {
                boolean bl4 = bl2 = !bl;
            }
            if (lv6.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(lv6)) {
                bl = !lv5.isEmpty();
                bl2 = !bl;
            }
        }
        float j = MathHelper.lerp(f, arg3.lastRenderPitch, arg3.renderPitch);
        float k = MathHelper.lerp(f, arg3.lastRenderYaw, arg3.renderYaw);
        arg.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion((arg3.getPitch(f) - j) * 0.1f));
        arg.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((arg3.getYaw(f) - k) * 0.1f));
        if (bl) {
            float l = lv == Hand.MAIN_HAND ? g : 0.0f;
            float m = 1.0f - MathHelper.lerp(f, this.prevEquipProgressMainHand, this.equipProgressMainHand);
            this.renderFirstPersonItem(arg3, f, h, Hand.MAIN_HAND, l, this.mainHand, m, arg, arg2, i);
        }
        if (bl2) {
            float n = lv == Hand.OFF_HAND ? g : 0.0f;
            float o = 1.0f - MathHelper.lerp(f, this.prevEquipProgressOffHand, this.equipProgressOffHand);
            this.renderFirstPersonItem(arg3, f, h, Hand.OFF_HAND, n, this.offHand, o, arg, arg2, i);
        }
        arg2.draw();
    }

    private void renderFirstPersonItem(AbstractClientPlayerEntity arg, float f, float g, Hand arg2, float h, ItemStack arg3, float i, MatrixStack arg4, VertexConsumerProvider arg5, int j) {
        boolean bl = arg2 == Hand.MAIN_HAND;
        Arm lv = bl ? arg.getMainArm() : arg.getMainArm().getOpposite();
        arg4.push();
        if (arg3.isEmpty()) {
            if (bl && !arg.isInvisible()) {
                this.renderArmHoldingItem(arg4, arg5, j, i, h, lv);
            }
        } else if (arg3.getItem() == Items.FILLED_MAP) {
            if (bl && this.offHand.isEmpty()) {
                this.renderMapInBothHands(arg4, arg5, j, g, i, h);
            } else {
                this.renderMapInOneHand(arg4, arg5, j, i, lv, h, arg3);
            }
        } else if (arg3.getItem() == Items.CROSSBOW) {
            int k;
            boolean bl2 = CrossbowItem.isCharged(arg3);
            boolean bl3 = lv == Arm.RIGHT;
            int n = k = bl3 ? 1 : -1;
            if (arg.isUsingItem() && arg.getItemUseTimeLeft() > 0 && arg.getActiveHand() == arg2) {
                this.applyEquipOffset(arg4, lv, i);
                arg4.translate((float)k * -0.4785682f, -0.094387f, 0.05731530860066414);
                arg4.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-11.935f));
                arg4.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)k * 65.3f));
                arg4.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float)k * -9.785f));
                float l = (float)arg3.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - f + 1.0f);
                float m = l / (float)CrossbowItem.getPullTime(arg3);
                if (m > 1.0f) {
                    m = 1.0f;
                }
                if (m > 0.1f) {
                    float n2 = MathHelper.sin((l - 0.1f) * 1.3f);
                    float o = m - 0.1f;
                    float p = n2 * o;
                    arg4.translate(p * 0.0f, p * 0.004f, p * 0.0f);
                }
                arg4.translate(m * 0.0f, m * 0.0f, m * 0.04f);
                arg4.scale(1.0f, 1.0f, 1.0f + m * 0.2f);
                arg4.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion((float)k * 45.0f));
            } else {
                float q = -0.4f * MathHelper.sin(MathHelper.sqrt(h) * (float)Math.PI);
                float r = 0.2f * MathHelper.sin(MathHelper.sqrt(h) * ((float)Math.PI * 2));
                float s = -0.2f * MathHelper.sin(h * (float)Math.PI);
                arg4.translate((float)k * q, r, s);
                this.applyEquipOffset(arg4, lv, i);
                this.applySwingOffset(arg4, lv, h);
                if (bl2 && h < 0.001f) {
                    arg4.translate((float)k * -0.641864f, 0.0, 0.0);
                    arg4.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)k * 10.0f));
                }
            }
            this.renderItem(arg, arg3, bl3 ? ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND : ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND, !bl3, arg4, arg5, j);
        } else {
            boolean bl4;
            boolean bl2 = bl4 = lv == Arm.RIGHT;
            if (arg.isUsingItem() && arg.getItemUseTimeLeft() > 0 && arg.getActiveHand() == arg2) {
                int t = bl4 ? 1 : -1;
                switch (arg3.getUseAction()) {
                    case NONE: {
                        this.applyEquipOffset(arg4, lv, i);
                        break;
                    }
                    case EAT: 
                    case DRINK: {
                        this.applyEatOrDrinkTransformation(arg4, f, lv, arg3);
                        this.applyEquipOffset(arg4, lv, i);
                        break;
                    }
                    case BLOCK: {
                        this.applyEquipOffset(arg4, lv, i);
                        break;
                    }
                    case BOW: {
                        this.applyEquipOffset(arg4, lv, i);
                        arg4.translate((float)t * -0.2785682f, 0.18344387412071228, 0.15731531381607056);
                        arg4.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-13.935f));
                        arg4.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)t * 35.3f));
                        arg4.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float)t * -9.785f));
                        float u = (float)arg3.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - f + 1.0f);
                        float v = u / 20.0f;
                        v = (v * v + v * 2.0f) / 3.0f;
                        if (v > 1.0f) {
                            v = 1.0f;
                        }
                        if (v > 0.1f) {
                            float w = MathHelper.sin((u - 0.1f) * 1.3f);
                            float x = v - 0.1f;
                            float y = w * x;
                            arg4.translate(y * 0.0f, y * 0.004f, y * 0.0f);
                        }
                        arg4.translate(v * 0.0f, v * 0.0f, v * 0.04f);
                        arg4.scale(1.0f, 1.0f, 1.0f + v * 0.2f);
                        arg4.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion((float)t * 45.0f));
                        break;
                    }
                    case SPEAR: {
                        this.applyEquipOffset(arg4, lv, i);
                        arg4.translate((float)t * -0.5f, 0.7f, 0.1f);
                        arg4.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-55.0f));
                        arg4.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)t * 35.3f));
                        arg4.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float)t * -9.785f));
                        float z = (float)arg3.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - f + 1.0f);
                        float aa = z / 10.0f;
                        if (aa > 1.0f) {
                            aa = 1.0f;
                        }
                        if (aa > 0.1f) {
                            float ab = MathHelper.sin((z - 0.1f) * 1.3f);
                            float ac = aa - 0.1f;
                            float ad = ab * ac;
                            arg4.translate(ad * 0.0f, ad * 0.004f, ad * 0.0f);
                        }
                        arg4.translate(0.0, 0.0, aa * 0.2f);
                        arg4.scale(1.0f, 1.0f, 1.0f + aa * 0.2f);
                        arg4.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion((float)t * 45.0f));
                        break;
                    }
                }
            } else if (arg.isUsingRiptide()) {
                this.applyEquipOffset(arg4, lv, i);
                int ae = bl4 ? 1 : -1;
                arg4.translate((float)ae * -0.4f, 0.8f, 0.3f);
                arg4.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)ae * 65.0f));
                arg4.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float)ae * -85.0f));
            } else {
                float af = -0.4f * MathHelper.sin(MathHelper.sqrt(h) * (float)Math.PI);
                float ag = 0.2f * MathHelper.sin(MathHelper.sqrt(h) * ((float)Math.PI * 2));
                float ah = -0.2f * MathHelper.sin(h * (float)Math.PI);
                int ai = bl4 ? 1 : -1;
                arg4.translate((float)ai * af, ag, ah);
                this.applyEquipOffset(arg4, lv, i);
                this.applySwingOffset(arg4, lv, h);
            }
            this.renderItem(arg, arg3, bl4 ? ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND : ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND, !bl4, arg4, arg5, j);
        }
        arg4.pop();
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

    public void resetEquipProgress(Hand arg) {
        if (arg == Hand.MAIN_HAND) {
            this.equipProgressMainHand = 0.0f;
        } else {
            this.equipProgressOffHand = 0.0f;
        }
    }
}

