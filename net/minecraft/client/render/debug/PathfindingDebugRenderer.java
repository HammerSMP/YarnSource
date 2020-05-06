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
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Locale;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class PathfindingDebugRenderer
implements DebugRenderer.Renderer {
    private final Map<Integer, Path> paths = Maps.newHashMap();
    private final Map<Integer, Float> field_4617 = Maps.newHashMap();
    private final Map<Integer, Long> pathTimes = Maps.newHashMap();

    public void addPath(int i, Path arg, float f) {
        this.paths.put(i, arg);
        this.pathTimes.put(i, Util.getMeasuringTimeMs());
        this.field_4617.put(i, Float.valueOf(f));
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, double d, double e, double f) {
        if (this.paths.isEmpty()) {
            return;
        }
        long l = Util.getMeasuringTimeMs();
        for (Integer integer : this.paths.keySet()) {
            Path lv = this.paths.get(integer);
            float g = this.field_4617.get(integer).floatValue();
            PathfindingDebugRenderer.drawPath(lv, g, true, true, d, e, f);
        }
        for (Integer integer2 : this.pathTimes.keySet().toArray(new Integer[0])) {
            if (l - this.pathTimes.get(integer2) <= 5000L) continue;
            this.paths.remove(integer2);
            this.pathTimes.remove(integer2);
        }
    }

    public static void drawPath(Path arg, float f, boolean bl, boolean bl2, double d, double e, double g) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(0.0f, 1.0f, 0.0f, 0.75f);
        RenderSystem.disableTexture();
        RenderSystem.lineWidth(6.0f);
        PathfindingDebugRenderer.drawPathInternal(arg, f, bl, bl2, d, e, g);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }

    private static void drawPathInternal(Path arg, float f, boolean bl, boolean bl2, double d, double e, double g) {
        PathfindingDebugRenderer.drawPathLines(arg, d, e, g);
        BlockPos lv = arg.getTarget();
        if (PathfindingDebugRenderer.getManhattanDistance(lv, d, e, g) <= 80.0f) {
            DebugRenderer.drawBox(new Box((float)lv.getX() + 0.25f, (float)lv.getY() + 0.25f, (double)lv.getZ() + 0.25, (float)lv.getX() + 0.75f, (float)lv.getY() + 0.75f, (float)lv.getZ() + 0.75f).offset(-d, -e, -g), 0.0f, 1.0f, 0.0f, 0.5f);
            for (int i = 0; i < arg.getLength(); ++i) {
                PathNode lv2 = arg.getNode(i);
                if (!(PathfindingDebugRenderer.getManhattanDistance(lv2.getPos(), d, e, g) <= 80.0f)) continue;
                float h = i == arg.getCurrentNodeIndex() ? 1.0f : 0.0f;
                float j = i == arg.getCurrentNodeIndex() ? 0.0f : 1.0f;
                DebugRenderer.drawBox(new Box((float)lv2.x + 0.5f - f, (float)lv2.y + 0.01f * (float)i, (float)lv2.z + 0.5f - f, (float)lv2.x + 0.5f + f, (float)lv2.y + 0.25f + 0.01f * (float)i, (float)lv2.z + 0.5f + f).offset(-d, -e, -g), h, 0.0f, j, 0.5f);
            }
        }
        if (bl) {
            for (PathNode lv3 : arg.method_22881()) {
                if (!(PathfindingDebugRenderer.getManhattanDistance(lv3.getPos(), d, e, g) <= 80.0f)) continue;
                DebugRenderer.drawBox(new Box((float)lv3.x + 0.5f - f / 2.0f, (float)lv3.y + 0.01f, (float)lv3.z + 0.5f - f / 2.0f, (float)lv3.x + 0.5f + f / 2.0f, (double)lv3.y + 0.1, (float)lv3.z + 0.5f + f / 2.0f).offset(-d, -e, -g), 1.0f, 0.8f, 0.8f, 0.5f);
            }
            for (PathNode lv4 : arg.method_22880()) {
                if (!(PathfindingDebugRenderer.getManhattanDistance(lv4.getPos(), d, e, g) <= 80.0f)) continue;
                DebugRenderer.drawBox(new Box((float)lv4.x + 0.5f - f / 2.0f, (float)lv4.y + 0.01f, (float)lv4.z + 0.5f - f / 2.0f, (float)lv4.x + 0.5f + f / 2.0f, (double)lv4.y + 0.1, (float)lv4.z + 0.5f + f / 2.0f).offset(-d, -e, -g), 0.8f, 1.0f, 1.0f, 0.5f);
            }
        }
        if (bl2) {
            for (int k = 0; k < arg.getLength(); ++k) {
                PathNode lv5 = arg.getNode(k);
                if (!(PathfindingDebugRenderer.getManhattanDistance(lv5.getPos(), d, e, g) <= 80.0f)) continue;
                DebugRenderer.drawString(String.format("%s", new Object[]{lv5.type}), (double)lv5.x + 0.5, (double)lv5.y + 0.75, (double)lv5.z + 0.5, -1);
                DebugRenderer.drawString(String.format(Locale.ROOT, "%.2f", Float.valueOf(lv5.penalty)), (double)lv5.x + 0.5, (double)lv5.y + 0.25, (double)lv5.z + 0.5, -1);
            }
        }
    }

    public static void drawPathLines(Path arg, double d, double e, double f) {
        Tessellator lv = Tessellator.getInstance();
        BufferBuilder lv2 = lv.getBuffer();
        lv2.begin(3, VertexFormats.POSITION_COLOR);
        for (int i = 0; i < arg.getLength(); ++i) {
            PathNode lv3 = arg.getNode(i);
            if (PathfindingDebugRenderer.getManhattanDistance(lv3.getPos(), d, e, f) > 80.0f) continue;
            float g = (float)i / (float)arg.getLength() * 0.33f;
            int j = i == 0 ? 0 : MathHelper.hsvToRgb(g, 0.9f, 0.9f);
            int k = j >> 16 & 0xFF;
            int l = j >> 8 & 0xFF;
            int m = j & 0xFF;
            lv2.vertex((double)lv3.x - d + 0.5, (double)lv3.y - e + 0.5, (double)lv3.z - f + 0.5).color(k, l, m, 255).next();
        }
        lv.draw();
    }

    private static float getManhattanDistance(BlockPos arg, double d, double e, double f) {
        return (float)(Math.abs((double)arg.getX() - d) + Math.abs((double)arg.getY() - e) + Math.abs((double)arg.getZ() - f));
    }
}

