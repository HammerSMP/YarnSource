/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.dimension;

import javax.annotation.Nullable;
import net.minecraft.class_5268;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.DimensionType;

public abstract class Dimension {
    public static final float[] MOON_PHASE_TO_SIZE = new float[]{1.0f, 0.75f, 0.5f, 0.25f, 0.0f, 0.25f, 0.5f, 0.75f};
    protected final World world;
    private final DimensionType type;
    protected final float[] lightLevelToBrightness = new float[16];

    public Dimension(World arg, DimensionType arg2, float f) {
        this.world = arg;
        this.type = arg2;
        for (int i = 0; i <= 15; ++i) {
            float g = (float)i / 15.0f;
            float h = g / (4.0f - 3.0f * g);
            this.lightLevelToBrightness[i] = MathHelper.lerp(f, h, 1.0f);
        }
    }

    public int getMoonPhase(long l) {
        return (int)(l / 24000L % 8L + 8L) % 8;
    }

    public float getBrightness(int i) {
        return this.lightLevelToBrightness[i];
    }

    public abstract float getSkyAngle(long var1, float var3);

    public WorldBorder createWorldBorder() {
        return new WorldBorder();
    }

    public abstract DimensionType getType();

    @Nullable
    public BlockPos getForcedSpawnPoint() {
        return null;
    }

    public void saveWorldData(class_5268 arg) {
    }

    public void update() {
    }

    @Nullable
    public abstract BlockPos getSpawningBlockInChunk(long var1, ChunkPos var3, boolean var4);

    @Nullable
    public abstract BlockPos getTopSpawningBlockPosition(long var1, int var3, int var4, boolean var5);

    public abstract boolean hasVisibleSky();

    public abstract boolean canPlayersSleep();
}

