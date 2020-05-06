/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class ConduitBlockEntity
extends BlockEntity
implements Tickable {
    private static final Block[] ACTIVATING_BLOCKS = new Block[]{Blocks.PRISMARINE, Blocks.PRISMARINE_BRICKS, Blocks.SEA_LANTERN, Blocks.DARK_PRISMARINE};
    public int ticks;
    private float ticksActive;
    private boolean active;
    private boolean eyeOpen;
    private final List<BlockPos> activatingBlocks = Lists.newArrayList();
    @Nullable
    private LivingEntity targetEntity;
    @Nullable
    private UUID targetUuid;
    private long nextAmbientSoundTime;

    public ConduitBlockEntity() {
        this(BlockEntityType.CONDUIT);
    }

    public ConduitBlockEntity(BlockEntityType<?> arg) {
        super(arg);
    }

    @Override
    public void fromTag(BlockState arg, CompoundTag arg2) {
        super.fromTag(arg, arg2);
        this.targetUuid = arg2.containsUuidNew("Target") ? arg2.getUuidNew("Target") : null;
    }

    @Override
    public CompoundTag toTag(CompoundTag arg) {
        super.toTag(arg);
        if (this.targetEntity != null) {
            arg.putUuidNew("Target", this.targetEntity.getUuid());
        }
        return arg;
    }

    @Override
    @Nullable
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(this.pos, 5, this.toInitialChunkDataTag());
    }

    @Override
    public CompoundTag toInitialChunkDataTag() {
        return this.toTag(new CompoundTag());
    }

    @Override
    public void tick() {
        ++this.ticks;
        long l = this.world.getTime();
        if (l % 40L == 0L) {
            this.setActive(this.updateActivatingBlocks());
            if (!this.world.isClient && this.isActive()) {
                this.givePlayersEffects();
                this.attackHostileEntity();
            }
        }
        if (l % 80L == 0L && this.isActive()) {
            this.playSound(SoundEvents.BLOCK_CONDUIT_AMBIENT);
        }
        if (l > this.nextAmbientSoundTime && this.isActive()) {
            this.nextAmbientSoundTime = l + 60L + (long)this.world.getRandom().nextInt(40);
            this.playSound(SoundEvents.BLOCK_CONDUIT_AMBIENT_SHORT);
        }
        if (this.world.isClient) {
            this.updateTargetEntity();
            this.spawnNautilusParticles();
            if (this.isActive()) {
                this.ticksActive += 1.0f;
            }
        }
    }

    private boolean updateActivatingBlocks() {
        this.activatingBlocks.clear();
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                for (int k = -1; k <= 1; ++k) {
                    BlockPos lv = this.pos.add(i, j, k);
                    if (this.world.isWater(lv)) continue;
                    return false;
                }
            }
        }
        for (int l = -2; l <= 2; ++l) {
            for (int m = -2; m <= 2; ++m) {
                for (int n = -2; n <= 2; ++n) {
                    int o = Math.abs(l);
                    int p = Math.abs(m);
                    int q = Math.abs(n);
                    if (o <= 1 && p <= 1 && q <= 1 || (l != 0 || p != 2 && q != 2) && (m != 0 || o != 2 && q != 2) && (n != 0 || o != 2 && p != 2)) continue;
                    BlockPos lv2 = this.pos.add(l, m, n);
                    BlockState lv3 = this.world.getBlockState(lv2);
                    for (Block lv4 : ACTIVATING_BLOCKS) {
                        if (!lv3.isOf(lv4)) continue;
                        this.activatingBlocks.add(lv2);
                    }
                }
            }
        }
        this.setEyeOpen(this.activatingBlocks.size() >= 42);
        return this.activatingBlocks.size() >= 16;
    }

    private void givePlayersEffects() {
        int m;
        int l;
        int i = this.activatingBlocks.size();
        int j = i / 7 * 16;
        int k = this.pos.getX();
        Box lv = new Box(k, l = this.pos.getY(), m = this.pos.getZ(), k + 1, l + 1, m + 1).expand(j).stretch(0.0, this.world.getHeight(), 0.0);
        List<PlayerEntity> list = this.world.getNonSpectatingEntities(PlayerEntity.class, lv);
        if (list.isEmpty()) {
            return;
        }
        for (PlayerEntity lv2 : list) {
            if (!this.pos.isWithinDistance(lv2.getBlockPos(), (double)j) || !lv2.isTouchingWaterOrRain()) continue;
            lv2.addStatusEffect(new StatusEffectInstance(StatusEffects.CONDUIT_POWER, 260, 0, true, true));
        }
    }

    private void attackHostileEntity() {
        LivingEntity lv = this.targetEntity;
        int i = this.activatingBlocks.size();
        if (i < 42) {
            this.targetEntity = null;
        } else if (this.targetEntity == null && this.targetUuid != null) {
            this.targetEntity = this.findTargetEntity();
            this.targetUuid = null;
        } else if (this.targetEntity == null) {
            List<LivingEntity> list = this.world.getEntities(LivingEntity.class, this.getAttackZone(), arg -> arg instanceof Monster && arg.isTouchingWaterOrRain());
            if (!list.isEmpty()) {
                this.targetEntity = list.get(this.world.random.nextInt(list.size()));
            }
        } else if (!this.targetEntity.isAlive() || !this.pos.isWithinDistance(this.targetEntity.getBlockPos(), 8.0)) {
            this.targetEntity = null;
        }
        if (this.targetEntity != null) {
            this.world.playSound(null, this.targetEntity.getX(), this.targetEntity.getY(), this.targetEntity.getZ(), SoundEvents.BLOCK_CONDUIT_ATTACK_TARGET, SoundCategory.BLOCKS, 1.0f, 1.0f);
            this.targetEntity.damage(DamageSource.MAGIC, 4.0f);
        }
        if (lv != this.targetEntity) {
            BlockState lv2 = this.getCachedState();
            this.world.updateListeners(this.pos, lv2, lv2, 2);
        }
    }

    private void updateTargetEntity() {
        if (this.targetUuid == null) {
            this.targetEntity = null;
        } else if (this.targetEntity == null || !this.targetEntity.getUuid().equals(this.targetUuid)) {
            this.targetEntity = this.findTargetEntity();
            if (this.targetEntity == null) {
                this.targetUuid = null;
            }
        }
    }

    private Box getAttackZone() {
        int i = this.pos.getX();
        int j = this.pos.getY();
        int k = this.pos.getZ();
        return new Box(i, j, k, i + 1, j + 1, k + 1).expand(8.0);
    }

    @Nullable
    private LivingEntity findTargetEntity() {
        List<LivingEntity> list = this.world.getEntities(LivingEntity.class, this.getAttackZone(), arg -> arg.getUuid().equals(this.targetUuid));
        if (list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    private void spawnNautilusParticles() {
        Random random = this.world.random;
        double d = MathHelper.sin((float)(this.ticks + 35) * 0.1f) / 2.0f + 0.5f;
        d = (d * d + d) * (double)0.3f;
        Vec3d lv = new Vec3d((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 1.5 + d, (double)this.pos.getZ() + 0.5);
        for (BlockPos lv2 : this.activatingBlocks) {
            if (random.nextInt(50) != 0) continue;
            float f = -0.5f + random.nextFloat();
            float g = -2.0f + random.nextFloat();
            float h = -0.5f + random.nextFloat();
            BlockPos lv3 = lv2.subtract(this.pos);
            Vec3d lv4 = new Vec3d(f, g, h).add(lv3.getX(), lv3.getY(), lv3.getZ());
            this.world.addParticle(ParticleTypes.NAUTILUS, lv.x, lv.y, lv.z, lv4.x, lv4.y, lv4.z);
        }
        if (this.targetEntity != null) {
            Vec3d lv5 = new Vec3d(this.targetEntity.getX(), this.targetEntity.getEyeY(), this.targetEntity.getZ());
            float i = (-0.5f + random.nextFloat()) * (3.0f + this.targetEntity.getWidth());
            float j = -1.0f + random.nextFloat() * this.targetEntity.getHeight();
            float k = (-0.5f + random.nextFloat()) * (3.0f + this.targetEntity.getWidth());
            Vec3d lv6 = new Vec3d(i, j, k);
            this.world.addParticle(ParticleTypes.NAUTILUS, lv5.x, lv5.y, lv5.z, lv6.x, lv6.y, lv6.z);
        }
    }

    public boolean isActive() {
        return this.active;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isEyeOpen() {
        return this.eyeOpen;
    }

    private void setActive(boolean bl) {
        if (bl != this.active) {
            this.playSound(bl ? SoundEvents.BLOCK_CONDUIT_ACTIVATE : SoundEvents.BLOCK_CONDUIT_DEACTIVATE);
        }
        this.active = bl;
    }

    private void setEyeOpen(boolean bl) {
        this.eyeOpen = bl;
    }

    @Environment(value=EnvType.CLIENT)
    public float getRotation(float f) {
        return (this.ticksActive + f) * -0.0375f;
    }

    public void playSound(SoundEvent arg) {
        this.world.playSound(null, this.pos, arg, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }
}

