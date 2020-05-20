/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.village;

import javax.annotation.Nullable;
import net.minecraft.class_5304;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;

public class ZombieSiegeManager
implements class_5304 {
    private boolean spawned;
    private State state = State.SIEGE_DONE;
    private int remaining;
    private int countdown;
    private int startX;
    private int startY;
    private int startZ;

    @Override
    public int spawn(ServerWorld arg, boolean bl, boolean bl2) {
        if (arg.isDay() || !bl) {
            this.state = State.SIEGE_DONE;
            this.spawned = false;
            return 0;
        }
        float f = arg.getSkyAngle(0.0f);
        if ((double)f == 0.5) {
            State state = this.state = arg.random.nextInt(10) == 0 ? State.SIEGE_TONIGHT : State.SIEGE_DONE;
        }
        if (this.state == State.SIEGE_DONE) {
            return 0;
        }
        if (!this.spawned) {
            if (this.spawn(arg)) {
                this.spawned = true;
            } else {
                return 0;
            }
        }
        if (this.countdown > 0) {
            --this.countdown;
            return 0;
        }
        this.countdown = 2;
        if (this.remaining > 0) {
            this.trySpawnZombie(arg);
            --this.remaining;
        } else {
            this.state = State.SIEGE_DONE;
        }
        return 1;
    }

    private boolean spawn(ServerWorld arg) {
        for (PlayerEntity playerEntity : arg.getPlayers()) {
            BlockPos lv2;
            if (playerEntity.isSpectator() || !arg.isNearOccupiedPointOfInterest(lv2 = playerEntity.getBlockPos()) || arg.getBiome(lv2).getCategory() == Biome.Category.MUSHROOM) continue;
            for (int i = 0; i < 10; ++i) {
                float f = arg.random.nextFloat() * ((float)Math.PI * 2);
                this.startX = lv2.getX() + MathHelper.floor(MathHelper.cos(f) * 32.0f);
                this.startY = lv2.getY();
                this.startZ = lv2.getZ() + MathHelper.floor(MathHelper.sin(f) * 32.0f);
                if (this.getSpawnVector(arg, new BlockPos(this.startX, this.startY, this.startZ)) == null) continue;
                this.countdown = 0;
                this.remaining = 20;
                break;
            }
            return true;
        }
        return false;
    }

    /*
     * WARNING - void declaration
     */
    private void trySpawnZombie(ServerWorld arg) {
        void lv3;
        Vec3d lv = this.getSpawnVector(arg, new BlockPos(this.startX, this.startY, this.startZ));
        if (lv == null) {
            return;
        }
        try {
            ZombieEntity lv2 = new ZombieEntity(arg);
            lv2.initialize(arg, arg.getLocalDifficulty(lv2.getBlockPos()), SpawnReason.EVENT, null, null);
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return;
        }
        lv3.refreshPositionAndAngles(lv.x, lv.y, lv.z, arg.random.nextFloat() * 360.0f, 0.0f);
        arg.spawnEntity((Entity)lv3);
    }

    @Nullable
    private Vec3d getSpawnVector(ServerWorld arg, BlockPos arg2) {
        for (int i = 0; i < 10; ++i) {
            int k;
            int l;
            int j = arg2.getX() + arg.random.nextInt(16) - 8;
            BlockPos lv = new BlockPos(j, l = arg.getTopY(Heightmap.Type.WORLD_SURFACE, j, k = arg2.getZ() + arg.random.nextInt(16) - 8), k);
            if (!arg.isNearOccupiedPointOfInterest(lv) || !HostileEntity.canSpawnInDark(EntityType.ZOMBIE, arg, SpawnReason.EVENT, lv, arg.random)) continue;
            return Vec3d.ofBottomCenter(lv);
        }
        return null;
    }

    static enum State {
        SIEGE_CAN_ACTIVATE,
        SIEGE_TONIGHT,
        SIEGE_DONE;

    }
}

