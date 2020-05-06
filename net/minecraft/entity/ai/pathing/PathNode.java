/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.ai.pathing;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class PathNode {
    public final int x;
    public final int y;
    public final int z;
    private final int hashCode;
    public int heapIndex = -1;
    public float penalizedPathLength;
    public float distanceToNearestTarget;
    public float heapWeight;
    public PathNode previous;
    public boolean visited;
    public float pathLength;
    public float penalty;
    public PathNodeType type = PathNodeType.BLOCKED;

    public PathNode(int i, int j, int k) {
        this.x = i;
        this.y = j;
        this.z = k;
        this.hashCode = PathNode.hash(i, j, k);
    }

    public PathNode copyWithNewPosition(int i, int j, int k) {
        PathNode lv = new PathNode(i, j, k);
        lv.heapIndex = this.heapIndex;
        lv.penalizedPathLength = this.penalizedPathLength;
        lv.distanceToNearestTarget = this.distanceToNearestTarget;
        lv.heapWeight = this.heapWeight;
        lv.previous = this.previous;
        lv.visited = this.visited;
        lv.pathLength = this.pathLength;
        lv.penalty = this.penalty;
        lv.type = this.type;
        return lv;
    }

    public static int hash(int i, int j, int k) {
        return j & 0xFF | (i & 0x7FFF) << 8 | (k & 0x7FFF) << 24 | (i < 0 ? Integer.MIN_VALUE : 0) | (k < 0 ? 32768 : 0);
    }

    public float getDistance(PathNode arg) {
        float f = arg.x - this.x;
        float g = arg.y - this.y;
        float h = arg.z - this.z;
        return MathHelper.sqrt(f * f + g * g + h * h);
    }

    public float getSquaredDistance(PathNode arg) {
        float f = arg.x - this.x;
        float g = arg.y - this.y;
        float h = arg.z - this.z;
        return f * f + g * g + h * h;
    }

    public float getManhattanDistance(PathNode arg) {
        float f = Math.abs(arg.x - this.x);
        float g = Math.abs(arg.y - this.y);
        float h = Math.abs(arg.z - this.z);
        return f + g + h;
    }

    public float getManhattanDistance(BlockPos arg) {
        float f = Math.abs(arg.getX() - this.x);
        float g = Math.abs(arg.getY() - this.y);
        float h = Math.abs(arg.getZ() - this.z);
        return f + g + h;
    }

    public BlockPos getPos() {
        return new BlockPos(this.x, this.y, this.z);
    }

    public boolean equals(Object object) {
        if (object instanceof PathNode) {
            PathNode lv = (PathNode)object;
            return this.hashCode == lv.hashCode && this.x == lv.x && this.y == lv.y && this.z == lv.z;
        }
        return false;
    }

    public int hashCode() {
        return this.hashCode;
    }

    public boolean isInHeap() {
        return this.heapIndex >= 0;
    }

    public String toString() {
        return "Node{x=" + this.x + ", y=" + this.y + ", z=" + this.z + '}';
    }

    @Environment(value=EnvType.CLIENT)
    public static PathNode fromBuffer(PacketByteBuf arg) {
        PathNode lv = new PathNode(arg.readInt(), arg.readInt(), arg.readInt());
        lv.pathLength = arg.readFloat();
        lv.penalty = arg.readFloat();
        lv.visited = arg.readBoolean();
        lv.type = PathNodeType.values()[arg.readInt()];
        lv.heapWeight = arg.readFloat();
        return lv;
    }
}

