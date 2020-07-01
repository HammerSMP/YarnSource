/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class LightningEntity
extends Entity {
    private int ambientTick;
    public long seed;
    private int remainingActions;
    private boolean cosmetic;
    @Nullable
    private ServerPlayerEntity channeler;

    public LightningEntity(EntityType<? extends LightningEntity> arg, World arg2) {
        super(arg, arg2);
        this.ignoreCameraFrustum = true;
        this.ambientTick = 2;
        this.seed = this.random.nextLong();
        this.remainingActions = this.random.nextInt(3) + 1;
    }

    public void method_29498(boolean bl) {
        this.cosmetic = bl;
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.WEATHER;
    }

    public void setChanneler(@Nullable ServerPlayerEntity arg) {
        this.channeler = arg;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.ambientTick == 2) {
            Difficulty lv = this.world.getDifficulty();
            if (lv == Difficulty.NORMAL || lv == Difficulty.HARD) {
                this.spawnFire(4);
            }
            this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 10000.0f, 0.8f + this.random.nextFloat() * 0.2f);
            this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.WEATHER, 2.0f, 0.5f + this.random.nextFloat() * 0.2f);
        }
        --this.ambientTick;
        if (this.ambientTick < 0) {
            if (this.remainingActions == 0) {
                this.remove();
            } else if (this.ambientTick < -this.random.nextInt(10)) {
                --this.remainingActions;
                this.ambientTick = 1;
                this.seed = this.random.nextLong();
                this.spawnFire(0);
            }
        }
        if (this.ambientTick >= 0) {
            if (!(this.world instanceof ServerWorld)) {
                this.world.setLightningTicksLeft(2);
            } else if (!this.cosmetic) {
                double d = 3.0;
                List<Entity> list = this.world.getEntities(this, new Box(this.getX() - 3.0, this.getY() - 3.0, this.getZ() - 3.0, this.getX() + 3.0, this.getY() + 6.0 + 3.0, this.getZ() + 3.0), Entity::isAlive);
                for (Entity lv2 : list) {
                    lv2.onStruckByLightning((ServerWorld)this.world, this);
                }
                if (this.channeler != null) {
                    Criteria.CHANNELED_LIGHTNING.trigger(this.channeler, list);
                }
            }
        }
    }

    private void spawnFire(int i) {
        if (this.cosmetic || this.world.isClient || !this.world.getGameRules().getBoolean(GameRules.DO_FIRE_TICK)) {
            return;
        }
        BlockPos lv = this.getBlockPos();
        BlockState lv2 = AbstractFireBlock.getState(this.world, lv);
        if (this.world.getBlockState(lv).isAir() && lv2.canPlaceAt(this.world, lv)) {
            this.world.setBlockState(lv, lv2);
        }
        for (int j = 0; j < i; ++j) {
            BlockPos lv3 = lv.add(this.random.nextInt(3) - 1, this.random.nextInt(3) - 1, this.random.nextInt(3) - 1);
            lv2 = AbstractFireBlock.getState(this.world, lv3);
            if (!this.world.getBlockState(lv3).isAir() || !lv2.canPlaceAt(this.world, lv3)) continue;
            this.world.setBlockState(lv3, lv2);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean shouldRender(double d) {
        double e = 64.0 * LightningEntity.getRenderDistanceMultiplier();
        return d < e * e;
    }

    @Override
    protected void initDataTracker() {
    }

    @Override
    protected void readCustomDataFromTag(CompoundTag arg) {
    }

    @Override
    protected void writeCustomDataToTag(CompoundTag arg) {
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}

