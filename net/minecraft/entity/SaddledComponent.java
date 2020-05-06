/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity;

import java.util.Random;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.nbt.CompoundTag;

public class SaddledComponent {
    private final DataTracker dataTracker;
    private final TrackedData<Integer> boostTime;
    private final TrackedData<Boolean> saddled;
    public boolean boosted;
    public int field_23216;
    public int currentBoostTime;

    public SaddledComponent(DataTracker arg, TrackedData<Integer> arg2, TrackedData<Boolean> arg3) {
        this.dataTracker = arg;
        this.boostTime = arg2;
        this.saddled = arg3;
    }

    public void boost() {
        this.boosted = true;
        this.field_23216 = 0;
        this.currentBoostTime = this.dataTracker.get(this.boostTime);
    }

    public boolean boost(Random random) {
        if (this.boosted) {
            return false;
        }
        this.boosted = true;
        this.field_23216 = 0;
        this.currentBoostTime = random.nextInt(841) + 140;
        this.dataTracker.set(this.boostTime, this.currentBoostTime);
        return true;
    }

    public void toTag(CompoundTag arg) {
        arg.putBoolean("Saddle", this.isSaddled());
    }

    public void fromTag(CompoundTag arg) {
        this.setSaddled(arg.getBoolean("Saddle"));
    }

    public void setSaddled(boolean bl) {
        this.dataTracker.set(this.saddled, bl);
    }

    public boolean isSaddled() {
        return this.dataTracker.get(this.saddled);
    }
}

