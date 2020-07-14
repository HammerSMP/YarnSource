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
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.network.PacketByteBuf;

public class TargetPathNode
extends PathNode {
    private float nearestNodeDistance = Float.MAX_VALUE;
    private PathNode nearestNode;
    private boolean reached;

    public TargetPathNode(PathNode node) {
        super(node.x, node.y, node.z);
    }

    @Environment(value=EnvType.CLIENT)
    public TargetPathNode(int i, int j, int k) {
        super(i, j, k);
    }

    public void updateNearestNode(float distance, PathNode node) {
        if (distance < this.nearestNodeDistance) {
            this.nearestNodeDistance = distance;
            this.nearestNode = node;
        }
    }

    public PathNode getNearestNode() {
        return this.nearestNode;
    }

    public void markReached() {
        this.reached = true;
    }

    @Environment(value=EnvType.CLIENT)
    public static TargetPathNode fromBuffer(PacketByteBuf buffer) {
        TargetPathNode lv = new TargetPathNode(buffer.readInt(), buffer.readInt(), buffer.readInt());
        lv.pathLength = buffer.readFloat();
        lv.penalty = buffer.readFloat();
        lv.visited = buffer.readBoolean();
        lv.type = PathNodeType.values()[buffer.readInt()];
        lv.heapWeight = buffer.readFloat();
        return lv;
    }
}

