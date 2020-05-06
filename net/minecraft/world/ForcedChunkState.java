/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 */
package net.minecraft.world;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.PersistentState;

public class ForcedChunkState
extends PersistentState {
    private LongSet chunks = new LongOpenHashSet();

    public ForcedChunkState() {
        super("chunks");
    }

    @Override
    public void fromTag(CompoundTag arg) {
        this.chunks = new LongOpenHashSet(arg.getLongArray("Forced"));
    }

    @Override
    public CompoundTag toTag(CompoundTag arg) {
        arg.putLongArray("Forced", this.chunks.toLongArray());
        return arg;
    }

    public LongSet getChunks() {
        return this.chunks;
    }
}

