/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.shorts.ShortList
 */
package net.minecraft.world;

import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.TickPriority;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;

public class ChunkTickScheduler<T>
implements TickScheduler<T> {
    protected final Predicate<T> shouldExclude;
    private final ChunkPos pos;
    private final ShortList[] scheduledPositions = new ShortList[16];

    public ChunkTickScheduler(Predicate<T> predicate, ChunkPos arg) {
        this(predicate, arg, new ListTag());
    }

    public ChunkTickScheduler(Predicate<T> predicate, ChunkPos arg, ListTag arg2) {
        this.shouldExclude = predicate;
        this.pos = arg;
        for (int i = 0; i < arg2.size(); ++i) {
            ListTag lv = arg2.getList(i);
            for (int j = 0; j < lv.size(); ++j) {
                Chunk.getList(this.scheduledPositions, i).add(lv.getShort(j));
            }
        }
    }

    public ListTag toNbt() {
        return ChunkSerializer.toNbt(this.scheduledPositions);
    }

    public void tick(TickScheduler<T> arg, Function<BlockPos, T> function) {
        for (int i = 0; i < this.scheduledPositions.length; ++i) {
            if (this.scheduledPositions[i] == null) continue;
            for (Short short_ : this.scheduledPositions[i]) {
                BlockPos lv = ProtoChunk.joinBlockPos(short_, i, this.pos);
                arg.schedule(lv, function.apply(lv), 0);
            }
            this.scheduledPositions[i].clear();
        }
    }

    @Override
    public boolean isScheduled(BlockPos arg, T object) {
        return false;
    }

    @Override
    public void schedule(BlockPos arg, T object, int i, TickPriority arg2) {
        Chunk.getList(this.scheduledPositions, arg.getY() >> 4).add(ProtoChunk.getPackedSectionRelative(arg));
    }

    @Override
    public boolean isTicking(BlockPos arg, T object) {
        return false;
    }
}

