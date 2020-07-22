/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Ordering
 *  com.google.common.collect.Sets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.debug;

import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

@Environment(value=EnvType.CLIENT)
public class NeighborUpdateDebugRenderer
implements DebugRenderer.Renderer {
    private final MinecraftClient client;
    private final Map<Long, Map<BlockPos, Integer>> neighborUpdates = Maps.newTreeMap((Comparator)Ordering.natural().reverse());

    NeighborUpdateDebugRenderer(MinecraftClient client) {
        this.client = client;
    }

    public void addNeighborUpdate(long time, BlockPos pos) {
        Map map = this.neighborUpdates.computeIfAbsent(time, long_ -> Maps.newHashMap());
        int i = map.getOrDefault(pos, 0);
        map.put(pos, i + 1);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        long l = this.client.world.getTime();
        int i = 200;
        double g = 0.0025;
        HashSet set = Sets.newHashSet();
        HashMap map = Maps.newHashMap();
        VertexConsumer lv = vertexConsumers.getBuffer(RenderLayer.getLines());
        Iterator<Map.Entry<Long, Map<BlockPos, Integer>>> iterator = this.neighborUpdates.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, Map<BlockPos, Integer>> entry = iterator.next();
            Long long_ = entry.getKey();
            Map<BlockPos, Integer> map2 = entry.getValue();
            long m = l - long_;
            if (m > 200L) {
                iterator.remove();
                continue;
            }
            for (Map.Entry<BlockPos, Integer> entry2 : map2.entrySet()) {
                BlockPos lv2 = entry2.getKey();
                Integer integer = entry2.getValue();
                if (!set.add(lv2)) continue;
                Box lv3 = new Box(BlockPos.ORIGIN).expand(0.002).contract(0.0025 * (double)m).offset(lv2.getX(), lv2.getY(), lv2.getZ()).offset(-cameraX, -cameraY, -cameraZ);
                WorldRenderer.drawBox(matrices, lv, lv3.minX, lv3.minY, lv3.minZ, lv3.maxX, lv3.maxY, lv3.maxZ, 1.0f, 1.0f, 1.0f, 1.0f);
                map.put(lv2, integer);
            }
        }
        for (Map.Entry entry3 : map.entrySet()) {
            BlockPos lv4 = (BlockPos)entry3.getKey();
            Integer integer2 = (Integer)entry3.getValue();
            DebugRenderer.drawString(String.valueOf(integer2), lv4.getX(), lv4.getY(), lv4.getZ(), -1);
        }
    }
}

