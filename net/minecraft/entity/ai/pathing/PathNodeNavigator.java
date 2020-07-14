/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.ai.pathing;

import com.google.common.collect.ImmutableSet;
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

    public PathNodeNavigator(PathNodeMaker pathNodeMaker, int range) {
        this.pathNodeMaker = pathNodeMaker;
        this.range = range;
    }

    @Nullable
    public Path findPathToAny(ChunkCache world, MobEntity mob, Set<BlockPos> positions, float followRange, int distance, float rangeMultiplier) {
        this.minHeap.clear();
        this.pathNodeMaker.init(world, mob);
        PathNode lv = this.pathNodeMaker.getStart();
        Map<TargetPathNode, BlockPos> map = positions.stream().collect(Collectors.toMap(arg -> this.pathNodeMaker.getNode((double)arg.getX(), (double)arg.getY(), (double)arg.getZ()), Function.identity()));
        Path lv2 = this.findPathToAny(lv, map, followRange, distance, rangeMultiplier);
        this.pathNodeMaker.clear();
        return lv2;
    }

    @Nullable
    private Path findPathToAny(PathNode startNode, Map<TargetPathNode, BlockPos> positions, float followRange, int distance, float rangeMultiplier) {
        Optional<Path> optional;
        Set<TargetPathNode> set = positions.keySet();
        startNode.penalizedPathLength = 0.0f;
        startNode.heapWeight = startNode.distanceToNearestTarget = this.calculateDistances(startNode, set);
        this.minHeap.clear();
        this.minHeap.push(startNode);
        ImmutableSet set2 = ImmutableSet.of();
        int j = 0;
        HashSet set3 = Sets.newHashSetWithExpectedSize((int)set.size());
        int k = (int)((float)this.range * rangeMultiplier);
        while (!this.minHeap.isEmpty() && ++j < k) {
            PathNode lv = this.minHeap.pop();
            lv.visited = true;
            for (TargetPathNode lv2 : set) {
                if (!(lv.getManhattanDistance(lv2) <= (float)distance)) continue;
                lv2.markReached();
                set3.add(lv2);
            }
            if (!set3.isEmpty()) break;
            if (lv.getDistance(startNode) >= followRange) continue;
            int l = this.pathNodeMaker.getSuccessors(this.successors, lv);
            for (int m = 0; m < l; ++m) {
                PathNode lv3 = this.successors[m];
                float h = lv.getDistance(lv3);
                lv3.pathLength = lv.pathLength + h;
                float n = lv.penalizedPathLength + h + lv3.penalty;
                if (!(lv3.pathLength < followRange) || lv3.isInHeap() && !(n < lv3.penalizedPathLength)) continue;
                lv3.previous = lv;
                lv3.penalizedPathLength = n;
                lv3.distanceToNearestTarget = this.calculateDistances(lv3, set) * 1.5f;
                if (lv3.isInHeap()) {
                    this.minHeap.setNodeWeight(lv3, lv3.penalizedPathLength + lv3.distanceToNearestTarget);
                    continue;
                }
                lv3.heapWeight = lv3.penalizedPathLength + lv3.distanceToNearestTarget;
                this.minHeap.push(lv3);
            }
        }
        Optional<Path> optional2 = optional = !set3.isEmpty() ? set3.stream().map(arg -> this.createPath(arg.getNearestNode(), (BlockPos)positions.get(arg), true)).min(Comparator.comparingInt(Path::getLength)) : set.stream().map(arg -> this.createPath(arg.getNearestNode(), (BlockPos)positions.get(arg), false)).min(Comparator.comparingDouble(Path::getManhattanDistanceFromTarget).thenComparingInt(Path::getLength));
        if (!optional.isPresent()) {
            return null;
        }
        Path lv4 = optional.get();
        return lv4;
    }

    private float calculateDistances(PathNode node, Set<TargetPathNode> targets) {
        float f = Float.MAX_VALUE;
        for (TargetPathNode lv : targets) {
            float g = node.getDistance(lv);
            lv.updateNearestNode(g, node);
            f = Math.min(g, f);
        }
        return f;
    }

    private Path createPath(PathNode endNode, BlockPos target, boolean reachesTarget) {
        ArrayList list = Lists.newArrayList();
        PathNode lv = endNode;
        list.add(0, lv);
        while (lv.previous != null) {
            lv = lv.previous;
            list.add(0, lv);
        }
        return new Path(list, target, reachesTarget);
    }
}

