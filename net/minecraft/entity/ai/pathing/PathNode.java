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

    public PathNode(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.hashCode = PathNode.hash(x, y, z);
    }

    public PathNode copyWithNewPosition(int x, int y, int z) {
        PathNode lv = new PathNode(x, y, z);
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

    public static int hash(int x, int y, int z) {
        return y & 0xFF | (x & 0x7FFF) << 8 | (z & 0x7FFF) << 24 | (x < 0 ? Integer.MIN_VALUE : 0) | (z < 0 ? 32768 : 0);
    }

    public float getDistance(PathNode node) {
        float f = node.x - this.x;
        float g = node.y - this.y;
        float h = node.z - this.z;
        return MathHelper.sqrt(f * f + g * g + h * h);
    }

    public float getSquaredDistance(PathNode node) {
        float f = node.x - this.x;
        float g = node.y - this.y;
        float h = node.z - this.z;
        return f * f + g * g + h * h;
    }

    public float getManhattanDistance(PathNode node) {
        float f = Math.abs(node.x - this.x);
        float g = Math.abs(node.y - this.y);
        float h = Math.abs(node.z - this.z);
        return f + g + h;
    }

    public float getManhattanDistance(BlockPos pos) {
        float f = Math.abs(pos.getX() - this.x);
        float g = Math.abs(pos.getY() - this.y);
        float h = Math.abs(pos.getZ() - this.z);
        return f + g + h;
    }

    public BlockPos getPos() {
        return new BlockPos(this.x, this.y, this.z);
    }

    public boolean equals(Object o) {
        if (o instanceof PathNode) {
            PathNode lv = (PathNode)o;
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
    public static PathNode fromBuffer(PacketByteBuf buffer) {
        PathNode lv = new PathNode(buffer.readInt(), buffer.readInt(), buffer.readInt());
        lv.pathLength = buffer.readFloat();
        lv.penalty = buffer.readFloat();
        lv.visited = buffer.readBoolean();
        lv.type = PathNodeType.values()[buffer.readInt()];
        lv.heapWeight = buffer.readFloat();
        return lv;
    }
}

