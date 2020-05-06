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

    public TargetPathNode(PathNode arg) {
        super(arg.x, arg.y, arg.z);
    }

    @Environment(value=EnvType.CLIENT)
    public TargetPathNode(int i, int j, int k) {
        super(i, j, k);
    }

    public void updateNearestNode(float f, PathNode arg) {
        if (f < this.nearestNodeDistance) {
            this.nearestNodeDistance = f;
            this.nearestNode = arg;
        }
    }

    public PathNode getNearestNode() {
        return this.nearestNode;
    }

    public void markReached() {
        this.reached = true;
    }

    public boolean isReached() {
        return this.reached;
    }

    @Environment(value=EnvType.CLIENT)
    public static TargetPathNode fromBuffer(PacketByteBuf arg) {
        TargetPathNode lv = new TargetPathNode(arg.readInt(), arg.readInt(), arg.readInt());
        lv.pathLength = arg.readFloat();
        lv.penalty = arg.readFloat();
        lv.visited = arg.readBoolean();
        lv.type = PathNodeType.values()[arg.readInt()];
        lv.heapWeight = arg.readFloat();
        return lv;
    }
}

