/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.decoration;

import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class EndCrystalEntity
extends Entity {
    private static final TrackedData<Optional<BlockPos>> BEAM_TARGET = DataTracker.registerData(EndCrystalEntity.class, TrackedDataHandlerRegistry.OPTIONAL_BLOCK_POS);
    private static final TrackedData<Boolean> SHOW_BOTTOM = DataTracker.registerData(EndCrystalEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public int endCrystalAge;

    public EndCrystalEntity(EntityType<? extends EndCrystalEntity> arg, World arg2) {
        super(arg, arg2);
        this.inanimate = true;
        this.endCrystalAge = this.random.nextInt(100000);
    }

    public EndCrystalEntity(World world, double x, double y, double z) {
        this((EntityType<? extends EndCrystalEntity>)EntityType.END_CRYSTAL, world);
        this.updatePosition(x, y, z);
    }

    @Override
    protected boolean canClimb() {
        return false;
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(BEAM_TARGET, Optional.empty());
        this.getDataTracker().startTracking(SHOW_BOTTOM, true);
    }

    @Override
    public void tick() {
        ++this.endCrystalAge;
        if (this.world instanceof ServerWorld) {
            BlockPos lv = this.getBlockPos();
            if (((ServerWorld)this.world).getEnderDragonFight() != null && this.world.getBlockState(lv).isAir()) {
                this.world.setBlockState(lv, AbstractFireBlock.getState(this.world, lv));
            }
        }
    }

    @Override
    protected void writeCustomDataToTag(CompoundTag tag) {
        if (this.getBeamTarget() != null) {
            tag.put("BeamTarget", NbtHelper.fromBlockPos(this.getBeamTarget()));
        }
        tag.putBoolean("ShowBottom", this.getShowBottom());
    }

    @Override
    protected void readCustomDataFromTag(CompoundTag tag) {
        if (tag.contains("BeamTarget", 10)) {
            this.setBeamTarget(NbtHelper.toBlockPos(tag.getCompound("BeamTarget")));
        }
        if (tag.contains("ShowBottom", 1)) {
            this.setShowBottom(tag.getBoolean("ShowBottom"));
        }
    }

    @Override
    public boolean collides() {
        return true;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (source.getAttacker() instanceof EnderDragonEntity) {
            return false;
        }
        if (!this.removed && !this.world.isClient) {
            this.remove();
            if (!source.isExplosive()) {
                this.world.createExplosion(null, this.getX(), this.getY(), this.getZ(), 6.0f, Explosion.DestructionType.DESTROY);
            }
            this.crystalDestroyed(source);
        }
        return true;
    }

    @Override
    public void kill() {
        this.crystalDestroyed(DamageSource.GENERIC);
        super.kill();
    }

    private void crystalDestroyed(DamageSource source) {
        EnderDragonFight lv;
        if (this.world instanceof ServerWorld && (lv = ((ServerWorld)this.world).getEnderDragonFight()) != null) {
            lv.crystalDestroyed(this, source);
        }
    }

    public void setBeamTarget(@Nullable BlockPos beamTarget) {
        this.getDataTracker().set(BEAM_TARGET, Optional.ofNullable(beamTarget));
    }

    @Nullable
    public BlockPos getBeamTarget() {
        return this.getDataTracker().get(BEAM_TARGET).orElse(null);
    }

    public void setShowBottom(boolean showBottom) {
        this.getDataTracker().set(SHOW_BOTTOM, showBottom);
    }

    public boolean getShowBottom() {
        return this.getDataTracker().get(SHOW_BOTTOM);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean shouldRender(double distance) {
        return super.shouldRender(distance) || this.getBeamTarget() != null;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}

