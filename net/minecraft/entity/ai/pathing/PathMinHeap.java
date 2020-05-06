/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.pathing;

import net.minecraft.entity.ai.pathing.PathNode;

public class PathMinHeap {
    private PathNode[] pathNodes = new PathNode[128];
    private int count;

    public PathNode push(PathNode arg) {
        if (arg.heapIndex >= 0) {
            throw new IllegalStateException("OW KNOWS!");
        }
        if (this.count == this.pathNodes.length) {
            PathNode[] lvs = new PathNode[this.count << 1];
            System.arraycopy(this.pathNodes, 0, lvs, 0, this.count);
            this.pathNodes = lvs;
        }
        this.pathNodes[this.count] = arg;
        arg.heapIndex = this.count;
        this.shiftUp(this.count++);
        return arg;
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

    public void setNodeWeight(PathNode arg, float f) {
        float g = arg.heapWeight;
        arg.heapWeight = f;
        if (f < g) {
            this.shiftUp(arg.heapIndex);
        } else {
            this.shiftDown(arg.heapIndex);
        }
    }

    private void shiftUp(int i) {
        PathNode lv = this.pathNodes[i];
        float f = lv.heapWeight;
        while (i > 0) {
            int j = i - 1 >> 1;
            PathNode lv2 = this.pathNodes[j];
            if (!(f < lv2.heapWeight)) break;
            this.pathNodes[i] = lv2;
            lv2.heapIndex = i;
            i = j;
        }
        this.pathNodes[i] = lv;
        lv.heapIndex = i;
    }

    private void shiftDown(int i) {
        PathNode lv = this.pathNodes[i];
        float f = lv.heapWeight;
        do {
            float l;
            PathNode lv4;
            int j = 1 + (i << 1);
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
                this.pathNodes[i] = lv2;
                lv2.heapIndex = i;
                i = j;
                continue;
            }
            if (!(l < f)) break;
            this.pathNodes[i] = lv4;
            lv4.heapIndex = i;
            i = k;
        } while (true);
        this.pathNodes[i] = lv;
        lv.heapIndex = i;
    }

    public boolean isEmpty() {
        return this.count == 0;
    }
}

