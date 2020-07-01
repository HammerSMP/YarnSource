/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeAccess;

@Environment(value=EnvType.CLIENT)
public class BackgroundRenderer {
    private static float red;
    private static float green;
    private static float blue;
    private static int waterFogColor;
    private static int nextWaterFogColor;
    private static long lastWaterFogColorUpdateTime;

    public static void render(Camera arg, float f, ClientWorld arg2, int i2, float g) {
        FluidState lv = arg.getSubmergedFluidState();
        if (lv.isIn(FluidTags.WATER)) {
            long l = Util.getMeasuringTimeMs();
            int j2 = arg2.getBiome(new BlockPos(arg.getPos())).getWaterFogColor();
            if (lastWaterFogColorUpdateTime < 0L) {
                waterFogColor = j2;
                nextWaterFogColor = j2;
                lastWaterFogColorUpdateTime = l;
            }
            int k2 = waterFogColor >> 16 & 0xFF;
            int m = waterFogColor >> 8 & 0xFF;
            int n = waterFogColor & 0xFF;
            int o = nextWaterFogColor >> 16 & 0xFF;
            int p = nextWaterFogColor >> 8 & 0xFF;
            int q = nextWaterFogColor & 0xFF;
            float h = MathHelper.clamp((float)(l - lastWaterFogColorUpdateTime) / 5000.0f, 0.0f, 1.0f);
            float r = MathHelper.lerp(h, o, k2);
            float s = MathHelper.lerp(h, p, m);
            float t = MathHelper.lerp(h, q, n);
            red = r / 255.0f;
            green = s / 255.0f;
            blue = t / 255.0f;
            if (waterFogColor != j2) {
                waterFogColor = j2;
                nextWaterFogColor = MathHelper.floor(r) << 16 | MathHelper.floor(s) << 8 | MathHelper.floor(t);
                lastWaterFogColorUpdateTime = l;
            }
        } else if (lv.isIn(FluidTags.LAVA)) {
            red = 0.6f;
            green = 0.1f;
            blue = 0.0f;
            lastWaterFogColorUpdateTime = -1L;
        } else {
            float ae;
            float u = 0.25f + 0.75f * (float)i2 / 32.0f;
            u = 1.0f - (float)Math.pow(u, 0.25);
            Vec3d lv2 = arg2.method_23777(arg.getBlockPos(), f);
            float v = (float)lv2.x;
            float w = (float)lv2.y;
            float x = (float)lv2.z;
            float y = MathHelper.clamp(MathHelper.cos(arg2.method_30274(f) * ((float)Math.PI * 2)) * 2.0f + 0.5f, 0.0f, 1.0f);
            BiomeAccess lv3 = arg2.getBiomeAccess();
            Vec3d lv4 = arg.getPos().subtract(2.0, 2.0, 2.0).multiply(0.25);
            Vec3d lv5 = CubicSampler.sampleColor(lv4, (i, j, k) -> arg2.getSkyProperties().adjustSkyColor(Vec3d.unpackRgb(lv3.getBiomeForNoiseGen(i, j, k).getFogColor()), y));
            red = (float)lv5.getX();
            green = (float)lv5.getY();
            blue = (float)lv5.getZ();
            if (i2 >= 4) {
                float[] fs;
                float z = MathHelper.sin(arg2.getSkyAngleRadians(f)) > 0.0f ? -1.0f : 1.0f;
                Vector3f lv6 = new Vector3f(z, 0.0f, 0.0f);
                float aa = arg.getHorizontalPlane().dot(lv6);
                if (aa < 0.0f) {
                    aa = 0.0f;
                }
                if (aa > 0.0f && (fs = arg2.getSkyProperties().getSkyColor(arg2.method_30274(f), f)) != null) {
                    red = red * (1.0f - (aa *= fs[3])) + fs[0] * aa;
                    green = green * (1.0f - aa) + fs[1] * aa;
                    blue = blue * (1.0f - aa) + fs[2] * aa;
                }
            }
            red += (v - red) * u;
            green += (w - green) * u;
            blue += (x - blue) * u;
            float ab = arg2.getRainGradient(f);
            if (ab > 0.0f) {
                float ac = 1.0f - ab * 0.5f;
                float ad = 1.0f - ab * 0.4f;
                red *= ac;
                green *= ac;
                blue *= ad;
            }
            if ((ae = arg2.getThunderGradient(f)) > 0.0f) {
                float af = 1.0f - ae * 0.5f;
                red *= af;
                green *= af;
                blue *= af;
            }
            lastWaterFogColorUpdateTime = -1L;
        }
        double d = arg.getPos().y * arg2.getLevelProperties().getHorizonShadingRatio();
        if (arg.getFocusedEntity() instanceof LivingEntity && ((LivingEntity)arg.getFocusedEntity()).hasStatusEffect(StatusEffects.BLINDNESS)) {
            int ag = ((LivingEntity)arg.getFocusedEntity()).getStatusEffect(StatusEffects.BLINDNESS).getDuration();
            d = ag < 20 ? (d *= (double)(1.0f - (float)ag / 20.0f)) : 0.0;
        }
        if (d < 1.0 && !lv.isIn(FluidTags.LAVA)) {
            if (d < 0.0) {
                d = 0.0;
            }
            d *= d;
            red = (float)((double)red * d);
            green = (float)((double)green * d);
            blue = (float)((double)blue * d);
        }
        if (g > 0.0f) {
            red = red * (1.0f - g) + red * 0.7f * g;
            green = green * (1.0f - g) + green * 0.6f * g;
            blue = blue * (1.0f - g) + blue * 0.6f * g;
        }
        if (lv.isIn(FluidTags.WATER)) {
            float ah = 0.0f;
            if (arg.getFocusedEntity() instanceof ClientPlayerEntity) {
                ClientPlayerEntity lv7 = (ClientPlayerEntity)arg.getFocusedEntity();
                ah = lv7.getUnderwaterVisibility();
            }
            float ai = Math.min(1.0f / red, Math.min(1.0f / green, 1.0f / blue));
            red = red * (1.0f - ah) + red * ai * ah;
            green = green * (1.0f - ah) + green * ai * ah;
            blue = blue * (1.0f - ah) + blue * ai * ah;
        } else if (arg.getFocusedEntity() instanceof LivingEntity && ((LivingEntity)arg.getFocusedEntity()).hasStatusEffect(StatusEffects.NIGHT_VISION)) {
            float aj = GameRenderer.getNightVisionStrength((LivingEntity)arg.getFocusedEntity(), f);
            float ak = Math.min(1.0f / red, Math.min(1.0f / green, 1.0f / blue));
            red = red * (1.0f - aj) + red * ak * aj;
            green = green * (1.0f - aj) + green * ak * aj;
            blue = blue * (1.0f - aj) + blue * ak * aj;
        }
        RenderSystem.clearColor(red, green, blue, 0.0f);
    }

    public static void method_23792() {
        RenderSystem.fogDensity(0.0f);
        RenderSystem.fogMode(GlStateManager.FogMode.EXP2);
    }

    public static void applyFog(Camera arg, FogType arg2, float f, boolean bl) {
        boolean bl2;
        FluidState lv = arg.getSubmergedFluidState();
        Entity lv2 = arg.getFocusedEntity();
        boolean bl3 = bl2 = lv.getFluid() != Fluids.EMPTY;
        if (lv.isIn(FluidTags.WATER)) {
            float g = 1.0f;
            g = 0.05f;
            if (lv2 instanceof ClientPlayerEntity) {
                ClientPlayerEntity lv3 = (ClientPlayerEntity)lv2;
                g -= lv3.getUnderwaterVisibility() * lv3.getUnderwaterVisibility() * 0.03f;
                Biome lv4 = lv3.world.getBiome(lv3.getBlockPos());
                if (lv4 == Biomes.SWAMP || lv4 == Biomes.SWAMP_HILLS) {
                    g += 0.005f;
                }
            }
            RenderSystem.fogDensity(g);
            RenderSystem.fogMode(GlStateManager.FogMode.EXP2);
        } else {
            float w;
            float v;
            if (lv.isIn(FluidTags.LAVA)) {
                if (lv2 instanceof LivingEntity && ((LivingEntity)lv2).hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
                    float h = 0.0f;
                    float i = 3.0f;
                } else {
                    float j = 0.25f;
                    float k = 1.0f;
                }
            } else if (lv2 instanceof LivingEntity && ((LivingEntity)lv2).hasStatusEffect(StatusEffects.BLINDNESS)) {
                int l = ((LivingEntity)lv2).getStatusEffect(StatusEffects.BLINDNESS).getDuration();
                float m = MathHelper.lerp(Math.min(1.0f, (float)l / 20.0f), f, 5.0f);
                if (arg2 == FogType.FOG_SKY) {
                    float n = 0.0f;
                    float o = m * 0.8f;
                } else {
                    float p = m * 0.25f;
                    float q = m;
                }
            } else if (bl) {
                float r = f * 0.05f;
                float s = Math.min(f, 192.0f) * 0.5f;
            } else if (arg2 == FogType.FOG_SKY) {
                float t = 0.0f;
                float u = f;
            } else {
                v = f * 0.75f;
                w = f;
            }
            RenderSystem.fogStart(v);
            RenderSystem.fogEnd(w);
            RenderSystem.fogMode(GlStateManager.FogMode.LINEAR);
            RenderSystem.setupNvFogDistance();
        }
    }

    public static void setFogBlack() {
        RenderSystem.fog(2918, red, green, blue, 1.0f);
    }

    static {
        waterFogColor = -1;
        nextWaterFogColor = -1;
        lastWaterFogColorUpdateTime = -1L;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum FogType {
        FOG_SKY,
        FOG_TERRAIN;

    }
}

