/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.ai.pathing;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathMinHeap;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.PathNodeMaker;
import net.minecraft.entity.ai.pathing.TargetPathNode;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkCache;

public class PathNodeNavigator {
    private final PathNode[] successors = new PathNode[32];
    private final int range;
    private final PathNodeMaker pathNodeMaker;
    private final PathMinHeap minHeap = new PathMinHeap();

    public PathNodeNavigator(PathNodeMaker arg, int i) {
        this.pathNodeMaker = arg;
        this.range = i;
    }

    @Nullable
    public Path findPathToAny(ChunkCache arg2, MobEntity arg22, Set<BlockPos> set, float f, int i, float g) {
        this.minHeap.clear();
        this.pathNodeMaker.init(arg2, arg22);
        PathNode lv = this.pathNodeMaker.getStart();
        Map<TargetPathNode, BlockPos> map = set.stream().collect(Collectors.toMap(arg -> this.pathNodeMaker.getNode((double)arg.getX(), (double)arg.getY(), (double)arg.getZ()), Function.identity()));
        Path lv2 = this.findPathToAny(lv, map, f, i, g);
        this.pathNodeMaker.clear();
        return lv2;
    }

    @Nullable
    private Path findPathToAny(PathNode arg3, Map<TargetPathNode, BlockPos> map, float f, int i, float g) {
        Stream<Path> stream2;
        Set<TargetPathNode> set = map.keySet();
        arg3.penalizedPathLength = 0.0f;
        arg3.heapWeight = arg3.distanceToNearestTarget = this.calculateDistances(arg3, set);
        this.minHeap.clear();
        this.minHeap.push(arg3);
        HashSet set2 = Sets.newHashSet();
        int j = 0;
        int k = (int)((float)this.range * g);
        while (!this.minHeap.isEmpty() && ++j < k) {
            PathNode lv = this.minHeap.pop();
            lv.visited = true;
            set.stream().filter(arg2 -> lv.getManhattanDistance((PathNode)arg2) <= (float)i).forEach(TargetPathNode::markReached);
            if (set.stream().anyMatch(TargetPathNode::isReached)) break;
            if (lv.getDistance(arg3) >= f) continue;
            int l = this.pathNodeMaker.getSuccessors(this.successors, lv);
            for (int m = 0; m < l; ++m) {
                PathNode lv2 = this.successors[m];
                float h = lv.getDistance(lv2);
                lv2.pathLength = lv.pathLength + h;
                float n = lv.penalizedPathLength + h + lv2.penalty;
                if (!(lv2.pathLength < f) || lv2.isInHeap() && !(n < lv2.penalizedPathLength)) continue;
                lv2.previous = lv;
                lv2.penalizedPathLength = n;
                lv2.distanceToNearestTarget = this.calculateDistances(lv2, set) * 1.5f;
                if (lv2.isInHeap()) {
                    this.minHeap.setNodeWeight(lv2, lv2.penalizedPathLength + lv2.distanceToNearestTarget);
                    continue;
                }
                lv2.heapWeight = lv2.penalizedPathLength + lv2.distanceToNearestTarget;
                this.minHeap.push(lv2);
            }
        }
        if (set.stream().anyMatch(TargetPathNode::isReached)) {
            Stream<Path> stream = set.stream().filter(TargetPathNode::isReached).map(arg -> this.createPath(arg.getNearestNode(), (BlockPos)map.get(arg), true)).sorted(Comparator.comparingInt(Path::getLength));
        } else {
            stream2 = set.stream().map(arg -> this.createPath(arg.getNearestNode(), (BlockPos)map.get(arg), false)).sorted(Comparator.comparingDouble(Path::getManhattanDistanceFromTarget).thenComparingInt(Path::getLength));
        }
        Optional<Path> optional = stream2.findFirst();
        if (!optional.isPresent()) {
            return null;
        }
        Path lv3 = optional.get();
        return lv3;
    }

    private float calculateDistances(PathNode arg, Set<TargetPathNode> set) {
        float f = Float.MAX_VALUE;
        for (TargetPathNode lv : set) {
            float g = arg.getDistance(lv);
            lv.updateNearestNode(g, arg);
            f = Math.min(g, f);
        }
        return f;
    }

    private Path createPath(PathNode arg, BlockPos arg2, boolean bl) {
        ArrayList list = Lists.newArrayList();
        PathNode lv = arg;
        list.add(0, lv);
        while (lv.previous != null) {
            lv = lv.previous;
            list.add(0, lv);
        }
        return new Path(list, arg2, bl);
    }
}

