/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package net.minecraft.block.entity;

import java.util.List;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.hud.BackgroundHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableInt;

public class BellBlockEntity
extends BlockEntity
implements Tickable {
    private long lastRingTime;
    public int ringTicks;
    public boolean ringing;
    public Direction lastSideHit;
    private List<LivingEntity> hearingEntities;
    private boolean resonating;
    private int field_19158;

    public BellBlockEntity() {
        super(BlockEntityType.BELL);
    }

    @Override
    public boolean onSyncedBlockEvent(int i, int j) {
        if (i == 1) {
            this.notifyMemoriesOfBell();
            this.field_19158 = 0;
            this.lastSideHit = Direction.byId(j);
            this.ringTicks = 0;
            this.ringing = true;
            return true;
        }
        return super.onSyncedBlockEvent(i, j);
    }

    @Override
    public void tick() {
        if (this.ringing) {
            ++this.ringTicks;
        }
        if (this.ringTicks >= 50) {
            this.ringing = false;
            this.ringTicks = 0;
        }
        if (this.ringTicks >= 5 && this.field_19158 == 0 && this.method_20523()) {
            this.resonating = true;
            this.playResonateSound();
        }
        if (this.resonating) {
            if (this.field_19158 < 40) {
                ++this.field_19158;
            } else {
                this.applyGlowToRaiders(this.world);
                this.applyParticlesToRaiders(this.world);
                this.resonating = false;
            }
        }
    }

    private void playResonateSound() {
        this.world.playSound(null, this.getPos(), SoundEvents.BLOCK_BELL_RESONATE, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }

    public void activate(Direction arg) {
        BlockPos lv = this.getPos();
        this.lastSideHit = arg;
        if (this.ringing) {
            this.ringTicks = 0;
        } else {
            this.ringing = true;
        }
        this.world.addSyncedBlockEvent(lv, this.getCachedState().getBlock(), 1, arg.getId());
    }

    private void notifyMemoriesOfBell() {
        BlockPos lv = this.getPos();
        if (this.world.getTime() > this.lastRingTime + 60L || this.hearingEntities == null) {
            this.lastRingTime = this.world.getTime();
            Box lv2 = new Box(lv).expand(48.0);
            this.hearingEntities = this.world.getNonSpectatingEntities(LivingEntity.class, lv2);
        }
        if (!this.world.isClient) {
            for (LivingEntity lv3 : this.hearingEntities) {
                if (!lv3.isAlive() || lv3.removed || !lv.isWithinDistance(lv3.getPos(), 32.0)) continue;
                lv3.getBrain().remember(MemoryModuleType.HEARD_BELL_TIME, this.world.getTime());
            }
        }
    }

    private boolean method_20523() {
        BlockPos lv = this.getPos();
        for (LivingEntity lv2 : this.hearingEntities) {
            if (!lv2.isAlive() || lv2.removed || !lv.isWithinDistance(lv2.getPos(), 32.0) || !lv2.getType().isIn(EntityTypeTags.RAIDERS)) continue;
            return true;
        }
        return false;
    }

    private void applyGlowToRaiders(World arg) {
        if (arg.isClient) {
            return;
        }
        this.hearingEntities.stream().filter(this::isRaiderEntity).forEach(this::glowEntity);
    }

    private void applyParticlesToRaiders(World arg) {
        if (!arg.isClient) {
            return;
        }
        BlockPos lv = this.getPos();
        MutableInt mutableInt = new MutableInt(16700985);
        int i = (int)this.hearingEntities.stream().filter(arg2 -> lv.isWithinDistance(arg2.getPos(), 48.0)).count();
        this.hearingEntities.stream().filter(this::isRaiderEntity).forEach(arg3 -> {
            float f = 1.0f;
            float g = MathHelper.sqrt((arg3.getX() - (double)lv.getX()) * (arg3.getX() - (double)lv.getX()) + (arg3.getZ() - (double)lv.getZ()) * (arg3.getZ() - (double)lv.getZ()));
            double d = (double)((float)lv.getX() + 0.5f) + (double)(1.0f / g) * (arg3.getX() - (double)lv.getX());
            double e = (double)((float)lv.getZ() + 0.5f) + (double)(1.0f / g) * (arg3.getZ() - (double)lv.getZ());
            int j = MathHelper.clamp((i - 21) / -2, 3, 15);
            for (int k = 0; k < j; ++k) {
                int l = mutableInt.addAndGet(5);
                double h = (double)BackgroundHelper.ColorMixer.getRed(l) / 255.0;
                double m = (double)BackgroundHelper.ColorMixer.getGreen(l) / 255.0;
                double n = (double)BackgroundHelper.ColorMixer.getBlue(l) / 255.0;
                arg.addParticle(ParticleTypes.ENTITY_EFFECT, d, (float)lv.getY() + 0.5f, e, h, m, n);
            }
        });
    }

    private boolean isRaiderEntity(LivingEntity arg) {
        return arg.isAlive() && !arg.removed && this.getPos().isWithinDistance(arg.getPos(), 48.0) && arg.getType().isIn(EntityTypeTags.RAIDERS);
    }

    private void glowEntity(LivingEntity arg) {
        arg.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 60));
    }
}

