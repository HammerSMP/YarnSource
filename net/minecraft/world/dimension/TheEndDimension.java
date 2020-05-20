/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.dimension;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.class_5217;
import net.minecraft.class_5268;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;

public class TheEndDimension
extends Dimension {
    public static final BlockPos SPAWN_POINT = new BlockPos(100, 50, 0);
    private final EnderDragonFight enderDragonFight;

    public TheEndDimension(World arg, DimensionType arg2) {
        super(arg, arg2, 0.0f);
        if (arg instanceof ServerWorld) {
            ServerWorld lv = (ServerWorld)arg;
            class_5217 lv2 = lv.getLevelProperties();
            if (lv2 instanceof class_5268) {
                CompoundTag lv3 = ((class_5268)lv2).getWorldData();
                this.enderDragonFight = new EnderDragonFight(lv, lv3.getCompound("DragonFight"));
            } else {
                this.enderDragonFight = null;
            }
        } else {
            this.enderDragonFight = null;
        }
    }

    @Override
    public float getSkyAngle(long l, float f) {
        return 0.0f;
    }

    @Override
    public boolean canPlayersSleep() {
        return false;
    }

    @Override
    public boolean hasVisibleSky() {
        return false;
    }

    @Override
    @Nullable
    public BlockPos getSpawningBlockInChunk(long l, ChunkPos arg, boolean bl) {
        Random random = new Random(l);
        BlockPos lv = new BlockPos(arg.getStartX() + random.nextInt(15), 0, arg.getEndZ() + random.nextInt(15));
        return this.world.getTopNonAirState(lv).getMaterial().blocksMovement() ? lv : null;
    }

    @Override
    public BlockPos getForcedSpawnPoint() {
        return SPAWN_POINT;
    }

    @Override
    @Nullable
    public BlockPos getTopSpawningBlockPosition(long l, int i, int j, boolean bl) {
        return this.getSpawningBlockInChunk(l, new ChunkPos(i >> 4, j >> 4), bl);
    }

    @Override
    public DimensionType getType() {
        return DimensionType.THE_END;
    }

    @Override
    public void saveWorldData(class_5268 arg) {
        CompoundTag lv = new CompoundTag();
        if (this.enderDragonFight != null) {
            lv.put("DragonFight", this.enderDragonFight.toTag());
        }
        arg.setWorldData(lv);
    }

    @Override
    public void update() {
        if (this.enderDragonFight != null) {
            this.enderDragonFight.tick();
        }
    }

    @Nullable
    public EnderDragonFight getEnderDragonFight() {
        return this.enderDragonFight;
    }
}

