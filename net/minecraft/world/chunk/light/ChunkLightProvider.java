/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package net.minecraft.world.chunk.light;

import java.util.Arrays;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkToNibbleArrayMap;
import net.minecraft.world.chunk.light.ChunkLightingView;
import net.minecraft.world.chunk.light.LevelPropagator;
import net.minecraft.world.chunk.light.LightStorage;
import org.apache.commons.lang3.mutable.MutableInt;

public abstract class ChunkLightProvider<M extends ChunkToNibbleArrayMap<M>, S extends LightStorage<M>>
extends LevelPropagator
implements ChunkLightingView {
    private static final Direction[] DIRECTIONS = Direction.values();
    protected final ChunkProvider chunkProvider;
    protected final LightType type;
    protected final S lightStorage;
    private boolean field_15794;
    protected final BlockPos.Mutable reusableBlockPos = new BlockPos.Mutable();
    private final long[] cachedChunkPositions = new long[2];
    private final BlockView[] cachedChunks = new BlockView[2];

    public ChunkLightProvider(ChunkProvider arg, LightType arg2, S arg3) {
        super(16, 256, 8192);
        this.chunkProvider = arg;
        this.type = arg2;
        this.lightStorage = arg3;
        this.clearChunkCache();
    }

    @Override
    protected void resetLevel(long l) {
        ((LightStorage)this.lightStorage).updateAll();
        if (((LightStorage)this.lightStorage).hasLight(ChunkSectionPos.fromGlobalPos(l))) {
            super.resetLevel(l);
        }
    }

    @Nullable
    private BlockView getChunk(int i, int j) {
        long l = ChunkPos.toLong(i, j);
        for (int k = 0; k < 2; ++k) {
            if (l != this.cachedChunkPositions[k]) continue;
            return this.cachedChunks[k];
        }
        BlockView lv = this.chunkProvider.getChunk(i, j);
        for (int m = 1; m > 0; --m) {
            this.cachedChunkPositions[m] = this.cachedChunkPositions[m - 1];
            this.cachedChunks[m] = this.cachedChunks[m - 1];
        }
        this.cachedChunkPositions[0] = l;
        this.cachedChunks[0] = lv;
        return lv;
    }

    private void clearChunkCache() {
        Arrays.fill(this.cachedChunkPositions, ChunkPos.MARKER);
        Arrays.fill(this.cachedChunks, null);
    }

    protected BlockState getStateForLighting(long l, @Nullable MutableInt mutableInt) {
        boolean bl;
        int j;
        if (l == Long.MAX_VALUE) {
            if (mutableInt != null) {
                mutableInt.setValue(0);
            }
            return Blocks.AIR.getDefaultState();
        }
        int i = ChunkSectionPos.getSectionCoord(BlockPos.unpackLongX(l));
        BlockView lv = this.getChunk(i, j = ChunkSectionPos.getSectionCoord(BlockPos.unpackLongZ(l)));
        if (lv == null) {
            if (mutableInt != null) {
                mutableInt.setValue(16);
            }
            return Blocks.BEDROCK.getDefaultState();
        }
        this.reusableBlockPos.set(l);
        BlockState lv2 = lv.getBlockState(this.reusableBlockPos);
        boolean bl2 = bl = lv2.isOpaque() && lv2.hasSidedTransparency();
        if (mutableInt != null) {
            mutableInt.setValue(lv2.getOpacity(this.chunkProvider.getWorld(), this.reusableBlockPos));
        }
        return bl ? lv2 : Blocks.AIR.getDefaultState();
    }

    protected VoxelShape getOpaqueShape(BlockState arg, long l, Direction arg2) {
        return arg.isOpaque() ? arg.getCullingFace(this.chunkProvider.getWorld(), this.reusableBlockPos.set(l), arg2) : VoxelShapes.empty();
    }

    public static int getRealisticOpacity(BlockView arg, BlockState arg2, BlockPos arg3, BlockState arg4, BlockPos arg5, Direction arg6, int i) {
        VoxelShape lv2;
        boolean bl2;
        boolean bl = arg2.isOpaque() && arg2.hasSidedTransparency();
        boolean bl3 = bl2 = arg4.isOpaque() && arg4.hasSidedTransparency();
        if (!bl && !bl2) {
            return i;
        }
        VoxelShape lv = bl ? arg2.getCullingShape(arg, arg3) : VoxelShapes.empty();
        VoxelShape voxelShape = lv2 = bl2 ? arg4.getCullingShape(arg, arg5) : VoxelShapes.empty();
        if (VoxelShapes.adjacentSidesCoverSquare(lv, lv2, arg6)) {
            return 16;
        }
        return i;
    }

    @Override
    protected boolean isMarker(long l) {
        return l == Long.MAX_VALUE;
    }

    @Override
    protected int recalculateLevel(long l, long m, int i) {
        return 0;
    }

    @Override
    protected int getLevel(long l) {
        if (l == Long.MAX_VALUE) {
            return 0;
        }
        return 15 - ((LightStorage)this.lightStorage).get(l);
    }

    protected int getCurrentLevelFromArray(ChunkNibbleArray arg, long l) {
        return 15 - arg.get(ChunkSectionPos.getLocalCoord(BlockPos.unpackLongX(l)), ChunkSectionPos.getLocalCoord(BlockPos.unpackLongY(l)), ChunkSectionPos.getLocalCoord(BlockPos.unpackLongZ(l)));
    }

    @Override
    protected void setLevel(long l, int i) {
        ((LightStorage)this.lightStorage).set(l, Math.min(15, 15 - i));
    }

    @Override
    protected int getPropagatedLevel(long l, long m, int i) {
        return 0;
    }

    public boolean hasUpdates() {
        return this.hasPendingUpdates() || ((LevelPropagator)this.lightStorage).hasPendingUpdates() || ((LightStorage)this.lightStorage).hasLightUpdates();
    }

    public int doLightUpdates(int i, boolean bl, boolean bl2) {
        if (!this.field_15794) {
            if (((LevelPropagator)this.lightStorage).hasPendingUpdates() && (i = ((LevelPropagator)this.lightStorage).applyPendingUpdates(i)) == 0) {
                return i;
            }
            ((LightStorage)this.lightStorage).updateLightArrays(this, bl, bl2);
        }
        this.field_15794 = true;
        if (this.hasPendingUpdates()) {
            i = this.applyPendingUpdates(i);
            this.clearChunkCache();
            if (i == 0) {
                return i;
            }
        }
        this.field_15794 = false;
        ((LightStorage)this.lightStorage).notifyChunkProvider();
        return i;
    }

    protected void setLightArray(long l, @Nullable ChunkNibbleArray arg, boolean bl) {
        ((LightStorage)this.lightStorage).setLightArray(l, arg, bl);
    }

    @Override
    @Nullable
    public ChunkNibbleArray getLightArray(ChunkSectionPos arg) {
        return ((LightStorage)this.lightStorage).getLightArray(arg.asLong());
    }

    @Override
    public int getLightLevel(BlockPos arg) {
        return ((LightStorage)this.lightStorage).getLight(arg.asLong());
    }

    @Environment(value=EnvType.CLIENT)
    public String method_22875(long l) {
        return "" + ((LightStorage)this.lightStorage).getLevel(l);
    }

    public void checkBlock(BlockPos arg) {
        long l = arg.asLong();
        this.resetLevel(l);
        for (Direction lv : DIRECTIONS) {
            this.resetLevel(BlockPos.offset(l, lv));
        }
    }

    public void addLightSource(BlockPos arg, int i) {
    }

    @Override
    public void updateSectionStatus(ChunkSectionPos arg, boolean bl) {
        ((LightStorage)this.lightStorage).updateSectionStatus(arg.asLong(), bl);
    }

    public void setLightEnabled(ChunkPos arg, boolean bl) {
        long l = ChunkSectionPos.withZeroZ(ChunkSectionPos.asLong(arg.x, 0, arg.z));
        ((LightStorage)this.lightStorage).setLightEnabled(l, bl);
    }

    public void setRetainData(ChunkPos arg, boolean bl) {
        long l = ChunkSectionPos.withZeroZ(ChunkSectionPos.asLong(arg.x, 0, arg.z));
        ((LightStorage)this.lightStorage).setRetainData(l, bl);
    }
}

