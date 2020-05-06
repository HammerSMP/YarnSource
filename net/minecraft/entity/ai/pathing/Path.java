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

    public Path(List<PathNode> list, BlockPos arg, boolean bl) {
        this.nodes = list;
        this.target = arg;
        this.manhattanDistanceFromTarget = list.isEmpty() ? Float.MAX_VALUE : this.nodes.get(this.nodes.size() - 1).getManhattanDistance(this.target);
        this.reachesTarget = bl;
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

    public PathNode getNode(int i) {
        return this.nodes.get(i);
    }

    public List<PathNode> getNodes() {
        return this.nodes;
    }

    public void setLength(int i) {
        if (this.nodes.size() > i) {
            this.nodes.subList(i, this.nodes.size()).clear();
        }
    }

    public void setNode(int i, PathNode arg) {
        this.nodes.set(i, arg);
    }

    public int getLength() {
        return this.nodes.size();
    }

    public int getCurrentNodeIndex() {
        return this.currentNodeIndex;
    }

    public void setCurrentNodeIndex(int i) {
        this.currentNodeIndex = i;
    }

    public Vec3d getNodePosition(Entity arg, int i) {
        PathNode lv = this.nodes.get(i);
        double d = (double)lv.x + (double)((int)(arg.getWidth() + 1.0f)) * 0.5;
        double e = lv.y;
        double f = (double)lv.z + (double)((int)(arg.getWidth() + 1.0f)) * 0.5;
        return new Vec3d(d, e, f);
    }

    public Vec3d getNodePosition(Entity arg) {
        return this.getNodePosition(arg, this.currentNodeIndex);
    }

    public Vec3i getCurrentPosition() {
        PathNode lv = this.nodes.get(this.currentNodeIndex);
        return new Vec3i(lv.x, lv.y, lv.z);
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
    public static Path fromBuffer(PacketByteBuf arg) {
        boolean bl = arg.readBoolean();
        int i = arg.readInt();
        int j = arg.readInt();
        HashSet set = Sets.newHashSet();
        for (int k = 0; k < j; ++k) {
            set.add(TargetPathNode.fromBuffer(arg));
        }
        BlockPos lv = new BlockPos(arg.readInt(), arg.readInt(), arg.readInt());
        ArrayList list = Lists.newArrayList();
        int l = arg.readInt();
        for (int m = 0; m < l; ++m) {
            list.add(PathNode.fromBuffer(arg));
        }
        PathNode[] lvs = new PathNode[arg.readInt()];
        for (int n = 0; n < lvs.length; ++n) {
            lvs[n] = PathNode.fromBuffer(arg);
        }
        PathNode[] lvs2 = new PathNode[arg.readInt()];
        for (int o = 0; o < lvs2.length; ++o) {
            lvs2[o] = PathNode.fromBuffer(arg);
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

