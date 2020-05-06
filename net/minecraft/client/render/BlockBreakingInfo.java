/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public class BlockBreakingInfo
implements Comparable<BlockBreakingInfo> {
    private final int actorNetworkId;
    private final BlockPos pos;
    private int stage;
    private int lastUpdateTick;

    public BlockBreakingInfo(int i, BlockPos arg) {
        this.actorNetworkId = i;
        this.pos = arg;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public void setStage(int i) {
        if (i > 10) {
            i = 10;
        }
        this.stage = i;
    }

    public int getStage() {
        return this.stage;
    }

    public void setLastUpdateTick(int i) {
        this.lastUpdateTick = i;
    }

    public int getLastUpdateTick() {
        return this.lastUpdateTick;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        BlockBreakingInfo lv = (BlockBreakingInfo)object;
        return this.actorNetworkId == lv.actorNetworkId;
    }

    public int hashCode() {
        return Integer.hashCode(this.actorNetworkId);
    }

    @Override
    public int compareTo(BlockBreakingInfo arg) {
        if (this.stage != arg.stage) {
            return Integer.compare(this.stage, arg.stage);
        }
        return Integer.compare(this.actorNetworkId, arg.actorNetworkId);
    }

    @Override
    public /* synthetic */ int compareTo(Object object) {
        return this.compareTo((BlockBreakingInfo)object);
    }
}

