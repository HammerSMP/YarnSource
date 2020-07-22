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

    public void addPath(int id, Path path, float f) {
        this.paths.put(id, path);
        this.pathTimes.put(id, Util.getMeasuringTimeMs());
        this.field_4617.put(id, Float.valueOf(f));
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        if (this.paths.isEmpty()) {
            return;
        }
        long l = Util.getMeasuringTimeMs();
        for (Integer integer : this.paths.keySet()) {
            Path lv = this.paths.get(integer);
            float g = this.field_4617.get(integer).floatValue();
            PathfindingDebugRenderer.drawPath(lv, g, true, true, cameraX, cameraY, cameraZ);
        }
        for (Integer integer2 : this.pathTimes.keySet().toArray(new Integer[0])) {
            if (l - this.pathTimes.get(integer2) <= 5000L) continue;
            this.paths.remove(integer2);
            this.pathTimes.remove(integer2);
        }
    }

    public static void drawPath(Path path, float nodeSize, boolean bl, boolean drawLabels, double cameraX, double cameraY, double cameraZ) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(0.0f, 1.0f, 0.0f, 0.75f);
        RenderSystem.disableTexture();
        RenderSystem.lineWidth(6.0f);
        PathfindingDebugRenderer.drawPathInternal(path, nodeSize, bl, drawLabels, cameraX, cameraY, cameraZ);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }

    private static void drawPathInternal(Path path, float nodeSize, boolean bl, boolean drawLabels, double cameraX, double cameraY, double cameraZ) {
        PathfindingDebugRenderer.drawPathLines(path, cameraX, cameraY, cameraZ);
        BlockPos lv = path.getTarget();
        if (PathfindingDebugRenderer.getManhattanDistance(lv, cameraX, cameraY, cameraZ) <= 80.0f) {
            DebugRenderer.drawBox(new Box((float)lv.getX() + 0.25f, (float)lv.getY() + 0.25f, (double)lv.getZ() + 0.25, (float)lv.getX() + 0.75f, (float)lv.getY() + 0.75f, (float)lv.getZ() + 0.75f).offset(-cameraX, -cameraY, -cameraZ), 0.0f, 1.0f, 0.0f, 0.5f);
            for (int i = 0; i < path.getLength(); ++i) {
                PathNode lv2 = path.getNode(i);
                if (!(PathfindingDebugRenderer.getManhattanDistance(lv2.getPos(), cameraX, cameraY, cameraZ) <= 80.0f)) continue;
                float h = i == path.getCurrentNodeIndex() ? 1.0f : 0.0f;
                float j = i == path.getCurrentNodeIndex() ? 0.0f : 1.0f;
                DebugRenderer.drawBox(new Box((float)lv2.x + 0.5f - nodeSize, (float)lv2.y + 0.01f * (float)i, (float)lv2.z + 0.5f - nodeSize, (float)lv2.x + 0.5f + nodeSize, (float)lv2.y + 0.25f + 0.01f * (float)i, (float)lv2.z + 0.5f + nodeSize).offset(-cameraX, -cameraY, -cameraZ), h, 0.0f, j, 0.5f);
            }
        }
        if (bl) {
            for (PathNode lv3 : path.method_22881()) {
                if (!(PathfindingDebugRenderer.getManhattanDistance(lv3.getPos(), cameraX, cameraY, cameraZ) <= 80.0f)) continue;
                DebugRenderer.drawBox(new Box((float)lv3.x + 0.5f - nodeSize / 2.0f, (float)lv3.y + 0.01f, (float)lv3.z + 0.5f - nodeSize / 2.0f, (float)lv3.x + 0.5f + nodeSize / 2.0f, (double)lv3.y + 0.1, (float)lv3.z + 0.5f + nodeSize / 2.0f).offset(-cameraX, -cameraY, -cameraZ), 1.0f, 0.8f, 0.8f, 0.5f);
            }
            for (PathNode lv4 : path.method_22880()) {
                if (!(PathfindingDebugRenderer.getManhattanDistance(lv4.getPos(), cameraX, cameraY, cameraZ) <= 80.0f)) continue;
                DebugRenderer.drawBox(new Box((float)lv4.x + 0.5f - nodeSize / 2.0f, (float)lv4.y + 0.01f, (float)lv4.z + 0.5f - nodeSize / 2.0f, (float)lv4.x + 0.5f + nodeSize / 2.0f, (double)lv4.y + 0.1, (float)lv4.z + 0.5f + nodeSize / 2.0f).offset(-cameraX, -cameraY, -cameraZ), 0.8f, 1.0f, 1.0f, 0.5f);
            }
        }
        if (drawLabels) {
            for (int k = 0; k < path.getLength(); ++k) {
                PathNode lv5 = path.getNode(k);
                if (!(PathfindingDebugRenderer.getManhattanDistance(lv5.getPos(), cameraX, cameraY, cameraZ) <= 80.0f)) continue;
                DebugRenderer.drawString(String.format("%s", new Object[]{lv5.type}), (double)lv5.x + 0.5, (double)lv5.y + 0.75, (double)lv5.z + 0.5, -1, 0.02f, true, 0.0f, true);
                DebugRenderer.drawString(String.format(Locale.ROOT, "%.2f", Float.valueOf(lv5.penalty)), (double)lv5.x + 0.5, (double)lv5.y + 0.25, (double)lv5.z + 0.5, -1, 0.02f, true, 0.0f, true);
            }
        }
    }

    public static void drawPathLines(Path path, double cameraX, double cameraY, double cameraZ) {
        Tessellator lv = Tessellator.getInstance();
        BufferBuilder lv2 = lv.getBuffer();
        lv2.begin(3, VertexFormats.POSITION_COLOR);
        for (int i = 0; i < path.getLength(); ++i) {
            PathNode lv3 = path.getNode(i);
            if (PathfindingDebugRenderer.getManhattanDistance(lv3.getPos(), cameraX, cameraY, cameraZ) > 80.0f) continue;
            float g = (float)i / (float)path.getLength() * 0.33f;
            int j = i == 0 ? 0 : MathHelper.hsvToRgb(g, 0.9f, 0.9f);
            int k = j >> 16 & 0xFF;
            int l = j >> 8 & 0xFF;
            int m = j & 0xFF;
            lv2.vertex((double)lv3.x - cameraX + 0.5, (double)lv3.y - cameraY + 0.5, (double)lv3.z - cameraZ + 0.5).color(k, l, m, 255).next();
        }
        lv.draw();
    }

    private static float getManhattanDistance(BlockPos pos, double x, double y, double z) {
        return (float)(Math.abs((double)pos.getX() - x) + Math.abs((double)pos.getY() - y) + Math.abs((double)pos.getZ() - z));
    }
}

