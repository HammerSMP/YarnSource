/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.ai.pathing;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.TargetPathNode;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class Path {
    private final List<PathNode> nodes;
    private PathNode[] field_57 = new PathNode[0];
    private PathNode[] field_55 = new PathNode[0];
    @Environment(value=EnvType.CLIENT)
    private Set<TargetPathNode> field_20300;
    private int currentNodeIndex;
    private final BlockPos target;
    private final float manhattanDistanceFromTarget;
    private final boolean reachesTarget;

    public Path(List<PathNode> nodes, BlockPos target, boolean reachesTarget) {
        this.nodes = nodes;
        this.target = target;
        this.manhattanDistanceFromTarget = nodes.isEmpty() ? Float.MAX_VALUE : this.nodes.get(this.nodes.size() - 1).getManhattanDistance(this.target);
        this.reachesTarget = reachesTarget;
    }

    public void next() {
        ++this.currentNodeIndex;
    }

    public boolean isFinished() {
        return this.currentNodeIndex >= this.nodes.size();
    }

    @Nullable
    public PathNode getEnd() {
        if (!this.nodes.isEmpty()) {
            return this.nodes.get(this.nodes.size() - 1);
        }
        return null;
    }

    public PathNode getNode(int index) {
        return this.nodes.get(index);
    }

    public List<PathNode> getNodes() {
        return this.nodes;
    }

    public void setLength(int length) {
        if (this.nodes.size() > length) {
            this.nodes.subList(length, this.nodes.size()).clear();
        }
    }

    public void setNode(int index, PathNode node) {
        this.nodes.set(index, node);
    }

    public int getLength() {
        return this.nodes.size();
    }

    public int getCurrentNodeIndex() {
        return this.currentNodeIndex;
    }

    public void setCurrentNodeIndex(int index) {
        this.currentNodeIndex = index;
    }

    public Vec3d getNodePosition(Entity entity, int index) {
        PathNode lv = this.nodes.get(index);
        double d = (double)lv.x + (double)((int)(entity.getWidth() + 1.0f)) * 0.5;
        double e = lv.y;
        double f = (double)lv.z + (double)((int)(entity.getWidth() + 1.0f)) * 0.5;
        return new Vec3d(d, e, f);
    }

    public Vec3d getNodePosition(Entity arg) {
        return this.getNodePosition(arg, this.currentNodeIndex);
    }

    public Vec3i getCurrentPosition() {
        PathNode lv = this.method_29301();
        return new Vec3i(lv.x, lv.y, lv.z);
    }

    public PathNode method_29301() {
        return this.nodes.get(this.currentNodeIndex);
    }

    public boolean equalsPath(@Nullable Path arg) {
        if (arg == null) {
            return false;
        }
        if (arg.nodes.size() != this.nodes.size()) {
            return false;
        }
        for (int i = 0; i < this.nodes.size(); ++i) {
            PathNode lv = this.nodes.get(i);
            PathNode lv2 = arg.nodes.get(i);
            if (lv.x == lv2.x && lv.y == lv2.y && lv.z == lv2.z) continue;
            return false;
        }
        return true;
    }

    public boolean reachesTarget() {
        return this.reachesTarget;
    }

    @Environment(value=EnvType.CLIENT)
    public PathNode[] method_22880() {
        return this.field_57;
    }

    @Environment(value=EnvType.CLIENT)
    public PathNode[] method_22881() {
        return this.field_55;
    }

    @Environment(value=EnvType.CLIENT)
    public static Path fromBuffer(PacketByteBuf buffer) {
        boolean bl = buffer.readBoolean();
        int i = buffer.readInt();
        int j = buffer.readInt();
        HashSet set = Sets.newHashSet();
        for (int k = 0; k < j; ++k) {
            set.add(TargetPathNode.fromBuffer(buffer));
        }
        BlockPos lv = new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());
        ArrayList list = Lists.newArrayList();
        int l = buffer.readInt();
        for (int m = 0; m < l; ++m) {
            list.add(PathNode.fromBuffer(buffer));
        }
        PathNode[] lvs = new PathNode[buffer.readInt()];
        for (int n = 0; n < lvs.length; ++n) {
            lvs[n] = PathNode.fromBuffer(buffer);
        }
        PathNode[] lvs2 = new PathNode[buffer.readInt()];
        for (int o = 0; o < lvs2.length; ++o) {
            lvs2[o] = PathNode.fromBuffer(buffer);
        }
        Path lv2 = new Path(list, lv, bl);
        lv2.field_57 = lvs;
        lv2.field_55 = lvs2;
        lv2.field_20300 = set;
        lv2.currentNodeIndex = i;
        return lv2;
    }

    public String toString() {
        return "Path(length=" + this.nodes.size() + ")";
    }

    public BlockPos getTarget() {
        return this.target;
    }

    public float getManhattanDistanceFromTarget() {
        return this.manhattanDistanceFromTarget;
    }
}

