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

    NeighborUpdateDebugRenderer(MinecraftClient arg) {
        this.client = arg;
    }

    public void addNeighborUpdate(long l, BlockPos arg) {
        Integer integer;
        HashMap map = this.neighborUpdates.get(l);
        if (map == null) {
            map = Maps.newHashMap();
            this.neighborUpdates.put(l, map);
        }
        if ((integer = (Integer)map.get(arg)) == null) {
            integer = 0;
        }
        map.put(arg, integer + 1);
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, double d, double e, double f) {
        long l = this.client.world.getTime();
        int i = 200;
        double g = 0.0025;
        HashSet set = Sets.newHashSet();
        HashMap map = Maps.newHashMap();
        VertexConsumer lv = arg2.getBuffer(RenderLayer.getLines());
        Iterator<Map.Entry<Long, Map<BlockPos, Integer>>> iterator = this.neighborUpdates.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, Map<BlockPos, Integer>> entry = iterator.next();
            Long lv2 = entry.getKey();
            Map<BlockPos, Integer> map2 = entry.getValue();
            long m = l - lv2;
            if (m > 200L) {
                iterator.remove();
                continue;
            }
            for (Map.Entry<BlockPos, Integer> entry2 : map2.entrySet()) {
                BlockPos lv3 = entry2.getKey();
                Integer integer = entry2.getValue();
                if (!set.add(lv3)) continue;
                Box lv4 = new Box(BlockPos.ORIGIN).expand(0.002).contract(0.0025 * (double)m).offset(lv3.getX(), lv3.getY(), lv3.getZ()).offset(-d, -e, -f);
                WorldRenderer.drawBox(arg, lv, lv4.x1, lv4.y1, lv4.z1, lv4.x2, lv4.y2, lv4.z2, 1.0f, 1.0f, 1.0f, 1.0f);
                map.put(lv3, integer);
            }
        }
        for (Map.Entry entry3 : map.entrySet()) {
            BlockPos lv5 = (BlockPos)entry3.getKey();
            Integer integer2 = (Integer)entry3.getValue();
            DebugRenderer.drawString(String.valueOf(integer2), lv5.getX(), lv5.getY(), lv5.getZ(), -1);
        }
    }
}

