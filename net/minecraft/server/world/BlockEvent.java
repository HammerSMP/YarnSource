/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.server.world;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

public class BlockEvent {
    private final BlockPos pos;
    private final Block block;
    private final int type;
    private final int data;

    public BlockEvent(BlockPos arg, Block arg2, int i, int j) {
        this.pos = arg;
        this.block = arg2;
        this.type = i;
        this.data = j;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public Block getBlock() {
        return this.block;
    }

    public int getType() {
        return this.type;
    }

    public int getData() {
        return this.data;
    }

    public boolean equals(Object object) {
        if (object instanceof BlockEvent) {
            BlockEvent lv = (BlockEvent)object;
            return this.pos.equals(lv.pos) && this.type == lv.type && this.data == lv.data && this.block == lv.block;
        }
        return false;
    }

    public int hashCode() {
        int i = this.pos.hashCode();
        i = 31 * i + this.block.hashCode();
        i = 31 * i + this.type;
        i = 31 * i + this.data;
        return i;
    }

    public String toString() {
        return "TE(" + this.pos + ")," + this.type + "," + this.data + "," + this.block;
    }
}

