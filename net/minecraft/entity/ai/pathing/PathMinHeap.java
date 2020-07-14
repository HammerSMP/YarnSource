/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.pathing;

import net.minecraft.entity.ai.pathing.PathNode;

public class PathMinHeap {
    private PathNode[] pathNodes = new PathNode[128];
    private int count;

    public PathNode push(PathNode node) {
        if (node.heapIndex >= 0) {
            throw new IllegalStateException("OW KNOWS!");
        }
        if (this.count == this.pathNodes.length) {
            PathNode[] lvs = new PathNode[this.count << 1];
            System.arraycopy(this.pathNodes, 0, lvs, 0, this.count);
            this.pathNodes = lvs;
        }
        this.pathNodes[this.count] = node;
        node.heapIndex = this.count;
        this.shiftUp(this.count++);
        return node;
    }

    public void clear() {
        this.count = 0;
    }

    public PathNode pop() {
        PathNode lv = this.pathNodes[0];
        this.pathNodes[0] = this.pathNodes[--this.count];
        this.pathNodes[this.count] = null;
        if (this.count > 0) {
            this.shiftDown(0);
        }
        lv.heapIndex = -1;
        return lv;
    }

    public void setNodeWeight(PathNode node, float weight) {
        float g = node.heapWeight;
        node.heapWeight = weight;
        if (weight < g) {
            this.shiftUp(node.heapIndex);
        } else {
            this.shiftDown(node.heapIndex);
        }
    }

    private void shiftUp(int index) {
        PathNode lv = this.pathNodes[index];
        float f = lv.heapWeight;
        while (index > 0) {
            int j = index - 1 >> 1;
            PathNode lv2 = this.pathNodes[j];
            if (!(f < lv2.heapWeight)) break;
            this.pathNodes[index] = lv2;
            lv2.heapIndex = index;
            index = j;
        }
        this.pathNodes[index] = lv;
        lv.heapIndex = index;
    }

    private void shiftDown(int index) {
        PathNode lv = this.pathNodes[index];
        float f = lv.heapWeight;
        do {
            float l;
            PathNode lv4;
            int j = 1 + (index << 1);
            int k = j + 1;
            if (j >= this.count) break;
            PathNode lv2 = this.pathNodes[j];
            float g = lv2.heapWeight;
            if (k >= this.count) {
                Object lv3 = null;
                float h = Float.POSITIVE_INFINITY;
            } else {
                lv4 = this.pathNodes[k];
                l = lv4.heapWeight;
            }
            if (g < l) {
                if (!(g < f)) break;
                this.pathNodes[index] = lv2;
                lv2.heapIndex = index;
                index = j;
                continue;
            }
            if (!(l < f)) break;
            this.pathNodes[index] = lv4;
            lv4.heapIndex = index;
            index = k;
        } while (true);
        this.pathNodes[index] = lv;
        lv.heapIndex = index;
    }

    public boolean isEmpty() {
        return this.count == 0;
    }
}

