/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class LightmapTextureManager
implements AutoCloseable {
    private final NativeImageBackedTexture texture;
    private final NativeImage image;
    private final Identifier textureIdentifier;
    private boolean isDirty;
    private float field_21528;
    private final GameRenderer worldRenderer;
    private final MinecraftClient client;

    public LightmapTextureManager(GameRenderer arg, MinecraftClient arg2) {
        this.worldRenderer = arg;
        this.client = arg2;
        this.texture = new NativeImageBackedTexture(16, 16, false);
        this.textureIdentifier = this.client.getTextureManager().registerDynamicTexture("light_map", this.texture);
        this.image = this.texture.getImage();
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                this.image.setPixelColor(j, i, -1);
            }
        }
        this.texture.upload();
    }

    @Override
    public void close() {
        this.texture.close();
    }

    public void tick() {
        this.field_21528 = (float)((double)this.field_21528 + (Math.random() - Math.random()) * Math.random() * Math.random() * 0.1);
        this.field_21528 = (float)((double)this.field_21528 * 0.9);
        this.isDirty = true;
    }

    public void disable() {
        RenderSystem.activeTexture(33986);
        RenderSystem.disableTexture();
        RenderSystem.activeTexture(33984);
    }

    public void enable() {
        RenderSystem.activeTexture(33986);
        RenderSystem.matrixMode(5890);
        RenderSystem.loadIdentity();
        float f = 0.00390625f;
        RenderSystem.scalef(0.00390625f, 0.00390625f, 0.00390625f);
        RenderSystem.translatef(8.0f, 8.0f, 8.0f);
        RenderSystem.matrixMode(5888);
        this.client.getTextureManager().bindTexture(this.textureIdentifier);
        RenderSystem.texParameter(3553, 10241, 9729);
        RenderSystem.texParameter(3553, 10240, 9729);
        RenderSystem.texParameter(3553, 10242, 10496);
        RenderSystem.texParameter(3553, 10243, 10496);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableTexture();
        RenderSystem.activeTexture(33984);
    }

    public void update(float f) {
        float m;
        float i;
        if (!this.isDirty) {
            return;
        }
        this.isDirty = false;
        this.client.getProfiler().push("lightTex");
        ClientWorld lv = this.client.world;
        if (lv == null) {
            return;
        }
        float g = lv.method_23783(1.0f);
        if (lv.getLightningTicksLeft() > 0) {
            float h = 1.0f;
        } else {
            i = g * 0.95f + 0.05f;
        }
        float j = this.client.player.getUnderwaterVisibility();
        if (this.client.player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
            float k = GameRenderer.getNightVisionStrength(this.client.player, f);
        } else if (j > 0.0f && this.client.player.hasStatusEffect(StatusEffects.CONDUIT_POWER)) {
            float l = j;
        } else {
            m = 0.0f;
        }
        Vector3f lv2 = new Vector3f(g, g, 1.0f);
        lv2.lerp(new Vector3f(1.0f, 1.0f, 1.0f), 0.35f);
        float n = this.field_21528 + 1.5f;
        Vector3f lv3 = new Vector3f();
        for (int o = 0; o < 16; ++o) {
            for (int p = 0; p < 16; ++p) {
                float w;
                float r;
                float q = this.getBrightness(lv, o) * i;
                float s = r = this.getBrightness(lv, p) * n;
                float t = r * ((r * 0.6f + 0.4f) * 0.6f + 0.4f);
                float u = r * (r * r * 0.6f + 0.4f);
                lv3.set(s, t, u);
                if (lv.getSkyProperties().shouldRenderSky()) {
                    lv3.lerp(new Vector3f(0.99f, 1.12f, 1.0f), 0.25f);
                } else {
                    Vector3f lv4 = lv2.copy();
                    lv4.scale(q);
                    lv3.add(lv4);
                    lv3.lerp(new Vector3f(0.75f, 0.75f, 0.75f), 0.04f);
                    if (this.worldRenderer.getSkyDarkness(f) > 0.0f) {
                        float v = this.worldRenderer.getSkyDarkness(f);
                        Vector3f lv5 = lv3.copy();
                        lv5.multiplyComponentwise(0.7f, 0.6f, 0.6f);
                        lv3.lerp(lv5, v);
                    }
                }
                lv3.clamp(0.0f, 1.0f);
                if (m > 0.0f && (w = Math.max(lv3.getX(), Math.max(lv3.getY(), lv3.getZ()))) < 1.0f) {
                    float x = 1.0f / w;
                    Vector3f lv6 = lv3.copy();
                    lv6.scale(x);
                    lv3.lerp(lv6, m);
                }
                float y = (float)this.client.options.gamma;
                Vector3f lv7 = lv3.copy();
                lv7.modify(this::method_23795);
                lv3.lerp(lv7, y);
                lv3.lerp(new Vector3f(0.75f, 0.75f, 0.75f), 0.04f);
                lv3.clamp(0.0f, 1.0f);
                lv3.scale(255.0f);
                int z = 255;
                int aa = (int)lv3.getX();
                int ab = (int)lv3.getY();
                int ac = (int)lv3.getZ();
                this.image.setPixelColor(p, o, 0xFF000000 | ac << 16 | ab << 8 | aa);
            }
        }
        this.texture.upload();
        this.client.getProfiler().pop();
    }

    private float method_23795(float f) {
        float g = 1.0f - f;
        return 1.0f - g * g * g * g;
    }

    private float getBrightness(World arg, int i) {
        return arg.getDimension().method_28516(i);
    }

    public static int pack(int i, int j) {
        return i << 4 | j << 20;
    }

    public static int getBlockLightCoordinates(int i) {
        return i >> 4 & 0xFFFF;
    }

    public static int getSkyLightCoordinates(int i) {
        return i >> 20 & 0xFFFF;
    }
}

