/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world.chunk.light;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.light.ChunkBlockLightProvider;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import net.minecraft.world.chunk.light.ChunkLightingView;
import net.minecraft.world.chunk.light.ChunkSkyLightProvider;
import net.minecraft.world.chunk.light.LightingView;

public class LightingProvider
implements LightingView {
    @Nullable
    private final ChunkLightProvider<?, ?> blockLightProvider;
    @Nullable
    private final ChunkLightProvider<?, ?> skyLightProvider;

    public LightingProvider(ChunkProvider arg, boolean bl, boolean bl2) {
        this.blockLightProvider = bl ? new ChunkBlockLightProvider(arg) : null;
        this.skyLightProvider = bl2 ? new ChunkSkyLightProvider(arg) : null;
    }

    public void checkBlock(BlockPos arg) {
        if (this.blockLightProvider != null) {
            this.blockLightProvider.checkBlock(arg);
        }
        if (this.skyLightProvider != null) {
            this.skyLightProvider.checkBlock(arg);
        }
    }

    public void addLightSource(BlockPos arg, int i) {
        if (this.blockLightProvider != null) {
            this.blockLightProvider.addLightSource(arg, i);
        }
    }

    public boolean hasUpdates() {
        if (this.skyLightProvider != null && this.skyLightProvider.hasUpdates()) {
            return true;
        }
        return this.blockLightProvider != null && this.blockLightProvider.hasUpdates();
    }

    public int doLightUpdates(int i, boolean bl, boolean bl2) {
        if (this.blockLightProvider != null && this.skyLightProvider != null) {
            int j = i / 2;
            int k = this.blockLightProvider.doLightUpdates(j, bl, bl2);
            int l = i - j + k;
            int m = this.skyLightProvider.doLightUpdates(l, bl, bl2);
            if (k == 0 && m > 0) {
                return this.blockLightProvider.doLightUpdates(m, bl, bl2);
            }
            return m;
        }
        if (this.blockLightProvider != null) {
            return this.blockLightProvider.doLightUpdates(i, bl, bl2);
        }
        if (this.skyLightProvider != null) {
            return this.skyLightProvider.doLightUpdates(i, bl, bl2);
        }
        return i;
    }

    @Override
    public void updateSectionStatus(ChunkSectionPos arg, boolean bl) {
        if (this.blockLightProvider != null) {
            this.blockLightProvider.updateSectionStatus(arg, bl);
        }
        if (this.skyLightProvider != null) {
            this.skyLightProvider.updateSectionStatus(arg, bl);
        }
    }

    public void setLightEnabled(ChunkPos arg, boolean bl) {
        if (this.blockLightProvider != null) {
            this.blockLightProvider.setLightEnabled(arg, bl);
        }
        if (this.skyLightProvider != null) {
            this.skyLightProvider.setLightEnabled(arg, bl);
        }
    }

    public ChunkLightingView get(LightType arg) {
        if (arg == LightType.BLOCK) {
            if (this.blockLightProvider == null) {
                return ChunkLightingView.Empty.INSTANCE;
            }
            return this.blockLightProvider;
        }
        if (this.skyLightProvider == null) {
            return ChunkLightingView.Empty.INSTANCE;
        }
        return this.skyLightProvider;
    }

    @Environment(value=EnvType.CLIENT)
    public String method_22876(LightType arg, ChunkSectionPos arg2) {
        if (arg == LightType.BLOCK) {
            if (this.blockLightProvider != null) {
                return this.blockLightProvider.method_22875(arg2.asLong());
            }
        } else if (this.skyLightProvider != null) {
            return this.skyLightProvider.method_22875(arg2.asLong());
        }
        return "n/a";
    }

    public void queueData(LightType arg, ChunkSectionPos arg2, @Nullable ChunkNibbleArray arg3, boolean bl) {
        if (arg == LightType.BLOCK) {
            if (this.blockLightProvider != null) {
                this.blockLightProvider.setLightArray(arg2.asLong(), arg3, bl);
            }
        } else if (this.skyLightProvider != null) {
            this.skyLightProvider.setLightArray(arg2.asLong(), arg3, bl);
        }
    }

    public void setRetainData(ChunkPos arg, boolean bl) {
        if (this.blockLightProvider != null) {
            this.blockLightProvider.setRetainData(arg, bl);
        }
        if (this.skyLightProvider != null) {
            this.skyLightProvider.setRetainData(arg, bl);
        }
    }

    public int getLight(BlockPos arg, int i) {
        int j = this.skyLightProvider == null ? 0 : this.skyLightProvider.getLightLevel(arg) - i;
        int k = this.blockLightProvider == null ? 0 : this.blockLightProvider.getLightLevel(arg);
        return Math.max(k, j);
    }
}

