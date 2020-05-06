/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public class GameTestDebugRenderer
implements DebugRenderer.Renderer {
    private final Map<BlockPos, Marker> markers = Maps.newHashMap();

    public void addMarker(BlockPos arg, int i, String string, int j) {
        this.markers.put(arg, new Marker(i, string, Util.getMeasuringTimeMs() + (long)j));
    }

    @Override
    public void clear() {
        this.markers.clear();
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, double d, double e, double f) {
        long l = Util.getMeasuringTimeMs();
        this.markers.entrySet().removeIf(entry -> l > ((Marker)entry.getValue()).removalTime);
        this.markers.forEach((arg_0, arg_1) -> this.method_23111(arg_0, arg_1));
    }

    private void method_23111(BlockPos arg, Marker arg2) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        RenderSystem.color4f(0.0f, 1.0f, 0.0f, 0.75f);
        RenderSystem.disableTexture();
        DebugRenderer.drawBox(arg, 0.02f, arg2.method_23112(), arg2.method_23113(), arg2.method_23114(), arg2.method_23115());
        if (!arg2.message.isEmpty()) {
            double d = (double)arg.getX() + 0.5;
            double e = (double)arg.getY() + 1.2;
            double f = (double)arg.getZ() + 0.5;
            DebugRenderer.drawString(arg2.message, d, e, f, -1, 0.01f, true, 0.0f, true);
        }
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }

    @Environment(value=EnvType.CLIENT)
    static class Marker {
        public int color;
        public String message;
        public long removalTime;

        public Marker(int i, String string, long l) {
            this.color = i;
            this.message = string;
            this.removalTime = l;
        }

        public float method_23112() {
            return (float)(this.color >> 16 & 0xFF) / 255.0f;
        }

        public float method_23113() {
            return (float)(this.color >> 8 & 0xFF) / 255.0f;
        }

        public float method_23114() {
            return (float)(this.color & 0xFF) / 255.0f;
        }

        public float method_23115() {
            return (float)(this.color >> 24 & 0xFF) / 255.0f;
        }
    }
}

