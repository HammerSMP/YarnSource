/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public abstract class PassiveEntity
extends MobEntityWithAi {
    private static final TrackedData<Boolean> CHILD = DataTracker.registerData(PassiveEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    protected int breedingAge;
    protected int forcedAge;
    protected int happyTicksRemaining;

    protected PassiveEntity(EntityType<? extends PassiveEntity> arg, World arg2) {
        super((EntityType<? extends MobEntityWithAi>)arg, arg2);
    }

    @Override
    public EntityData initialize(WorldAccess arg, LocalDifficulty arg2, SpawnReason arg3, @Nullable EntityData arg4, @Nullable CompoundTag arg5) {
        PassiveData lv;
        if (arg4 == null) {
            arg4 = new PassiveData();
        }
        if ((lv = (PassiveData)arg4).canSpawnBaby() && lv.getSpawnedCount() > 0 && this.random.nextFloat() <= lv.getBabyChance()) {
            this.setBreedingAge(-24000);
        }
        lv.countSpawned();
        return super.initialize(arg, arg2, arg3, arg4, arg5);
    }

    @Nullable
    public abstract PassiveEntity createChild(PassiveEntity var1);

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(CHILD, false);
    }

    public boolean isReadyToBreed() {
        return false;
    }

    public int getBreedingAge() {
        if (this.world.isClient) {
            return this.dataTracker.get(CHILD) != false ? -1 : 1;
        }
        return this.breedingAge;
    }

    public void growUp(int i, boolean bl) {
        int j;
        int k = j = this.getBreedingAge();
        if ((j += i * 20) > 0) {
            j = 0;
        }
        int l = j - k;
        this.setBreedingAge(j);
        if (bl) {
            this.forcedAge += l;
            if (this.happyTicksRemaining == 0) {
                this.happyTicksRemaining = 40;
            }
        }
        if (this.getBreedingAge() == 0) {
            this.setBreedingAge(this.forcedAge);
        }
    }

    public void growUp(int i) {
        this.growUp(i, false);
    }

    public void setBreedingAge(int i) {
        int j = this.breedingAge;
        this.breedingAge = i;
        if (j < 0 && i >= 0 || j >= 0 && i < 0) {
            this.dataTracker.set(CHILD, i < 0);
            this.onGrowUp();
        }
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        arg.putInt("Age", this.getBreedingAge());
        arg.putInt("ForcedAge", this.forcedAge);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        this.setBreedingAge(arg.getInt("Age"));
        this.forcedAge = arg.getInt("ForcedAge");
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> arg) {
        if (CHILD.equals(arg)) {
            this.calculateDimensions();
        }
        super.onTrackedDataSet(arg);
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (this.world.isClient) {
            if (this.happyTicksRemaining > 0) {
                if (this.happyTicksRemaining % 4 == 0) {
                    this.world.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), 0.0, 0.0, 0.0);
                }
                --this.happyTicksRemaining;
            }
        } else if (this.isAlive()) {
            int i = this.getBreedingAge();
            if (i < 0) {
                this.setBreedingAge(++i);
            } else if (i > 0) {
                this.setBreedingAge(--i);
            }
        }
    }

    protected void onGrowUp() {
    }

    @Override
    public boolean isBaby() {
        return this.getBreedingAge() < 0;
    }

    @Override
    public void setBaby(boolean bl) {
        this.setBreedingAge(bl ? -24000 : 0);
    }

    public static class PassiveData
    implements EntityData {
        private int spawnCount;
        private boolean babyAllowed = true;
        private float babyChance = 0.05f;

        public int getSpawnedCount() {
            return this.spawnCount;
        }

        public void countSpawned() {
            ++this.spawnCount;
        }

        public boolean canSpawnBaby() {
            return this.babyAllowed;
        }

        public void setBabyAllowed(boolean bl) {
            this.babyAllowed = bl;
        }

        public float getBabyChance() {
            return this.babyChance;
        }

        public void setBabyChance(float f) {
            this.babyChance = f;
        }
    }
}

