/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ComparisonChain
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.entity.effect;

import com.google.common.collect.ComparisonChain;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.nbt.CompoundTag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StatusEffectInstance
implements Comparable<StatusEffectInstance> {
    private static final Logger LOGGER = LogManager.getLogger();
    private final StatusEffect type;
    private int duration;
    private int amplifier;
    private boolean splash;
    private boolean ambient;
    @Environment(value=EnvType.CLIENT)
    private boolean permanent;
    private boolean showParticles;
    private boolean showIcon;
    @Nullable
    private StatusEffectInstance hiddenEffect;

    public StatusEffectInstance(StatusEffect arg) {
        this(arg, 0, 0);
    }

    public StatusEffectInstance(StatusEffect arg, int i) {
        this(arg, i, 0);
    }

    public StatusEffectInstance(StatusEffect arg, int i, int j) {
        this(arg, i, j, false, true);
    }

    public StatusEffectInstance(StatusEffect arg, int i, int j, boolean bl, boolean bl2) {
        this(arg, i, j, bl, bl2, bl2);
    }

    public StatusEffectInstance(StatusEffect arg, int i, int j, boolean bl, boolean bl2, boolean bl3) {
        this(arg, i, j, bl, bl2, bl3, null);
    }

    public StatusEffectInstance(StatusEffect arg, int i, int j, boolean bl, boolean bl2, boolean bl3, @Nullable StatusEffectInstance arg2) {
        this.type = arg;
        this.duration = i;
        this.amplifier = j;
        this.ambient = bl;
        this.showParticles = bl2;
        this.showIcon = bl3;
        this.hiddenEffect = arg2;
    }

    public StatusEffectInstance(StatusEffectInstance arg) {
        this.type = arg.type;
        this.copyFrom(arg);
    }

    void copyFrom(StatusEffectInstance arg) {
        this.duration = arg.duration;
        this.amplifier = arg.amplifier;
        this.ambient = arg.ambient;
        this.showParticles = arg.showParticles;
        this.showIcon = arg.showIcon;
    }

    public boolean upgrade(StatusEffectInstance arg) {
        if (this.type != arg.type) {
            LOGGER.warn("This method should only be called for matching effects!");
        }
        boolean bl = false;
        if (arg.amplifier > this.amplifier) {
            if (arg.duration < this.duration) {
                StatusEffectInstance lv = this.hiddenEffect;
                this.hiddenEffect = new StatusEffectInstance(this);
                this.hiddenEffect.hiddenEffect = lv;
            }
            this.amplifier = arg.amplifier;
            this.duration = arg.duration;
            bl = true;
        } else if (arg.duration > this.duration) {
            if (arg.amplifier == this.amplifier) {
                this.duration = arg.duration;
                bl = true;
            } else if (this.hiddenEffect == null) {
                this.hiddenEffect = new StatusEffectInstance(arg);
            } else {
                this.hiddenEffect.upgrade(arg);
            }
        }
        if (!arg.ambient && this.ambient || bl) {
            this.ambient = arg.ambient;
            bl = true;
        }
        if (arg.showParticles != this.showParticles) {
            this.showParticles = arg.showParticles;
            bl = true;
        }
        if (arg.showIcon != this.showIcon) {
            this.showIcon = arg.showIcon;
            bl = true;
        }
        return bl;
    }

    public StatusEffect getEffectType() {
        return this.type;
    }

    public int getDuration() {
        return this.duration;
    }

    public int getAmplifier() {
        return this.amplifier;
    }

    public boolean isAmbient() {
        return this.ambient;
    }

    public boolean shouldShowParticles() {
        return this.showParticles;
    }

    public boolean shouldShowIcon() {
        return this.showIcon;
    }

    public boolean update(LivingEntity arg, Runnable runnable) {
        if (this.duration > 0) {
            if (this.type.canApplyUpdateEffect(this.duration, this.amplifier)) {
                this.applyUpdateEffect(arg);
            }
            this.updateDuration();
            if (this.duration == 0 && this.hiddenEffect != null) {
                this.copyFrom(this.hiddenEffect);
                this.hiddenEffect = this.hiddenEffect.hiddenEffect;
                runnable.run();
            }
        }
        return this.duration > 0;
    }

    private int updateDuration() {
        if (this.hiddenEffect != null) {
            this.hiddenEffect.updateDuration();
        }
        return --this.duration;
    }

    public void applyUpdateEffect(LivingEntity arg) {
        if (this.duration > 0) {
            this.type.applyUpdateEffect(arg, this.amplifier);
        }
    }

    public String getTranslationKey() {
        return this.type.getTranslationKey();
    }

    public String toString() {
        String string2;
        if (this.amplifier > 0) {
            String string = this.getTranslationKey() + " x " + (this.amplifier + 1) + ", Duration: " + this.duration;
        } else {
            string2 = this.getTranslationKey() + ", Duration: " + this.duration;
        }
        if (this.splash) {
            string2 = string2 + ", Splash: true";
        }
        if (!this.showParticles) {
            string2 = string2 + ", Particles: false";
        }
        if (!this.showIcon) {
            string2 = string2 + ", Show Icon: false";
        }
        return string2;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof StatusEffectInstance) {
            StatusEffectInstance lv = (StatusEffectInstance)object;
            return this.duration == lv.duration && this.amplifier == lv.amplifier && this.splash == lv.splash && this.ambient == lv.ambient && this.type.equals(lv.type);
        }
        return false;
    }

    public int hashCode() {
        int i = this.type.hashCode();
        i = 31 * i + this.duration;
        i = 31 * i + this.amplifier;
        i = 31 * i + (this.splash ? 1 : 0);
        i = 31 * i + (this.ambient ? 1 : 0);
        return i;
    }

    public CompoundTag toTag(CompoundTag arg) {
        arg.putByte("Id", (byte)StatusEffect.getRawId(this.getEffectType()));
        this.typelessToTag(arg);
        return arg;
    }

    private void typelessToTag(CompoundTag arg) {
        arg.putByte("Amplifier", (byte)this.getAmplifier());
        arg.putInt("Duration", this.getDuration());
        arg.putBoolean("Ambient", this.isAmbient());
        arg.putBoolean("ShowParticles", this.shouldShowParticles());
        arg.putBoolean("ShowIcon", this.shouldShowIcon());
        if (this.hiddenEffect != null) {
            CompoundTag lv = new CompoundTag();
            this.hiddenEffect.toTag(lv);
            arg.put("HiddenEffect", lv);
        }
    }

    public static StatusEffectInstance fromTag(CompoundTag arg) {
        byte i = arg.getByte("Id");
        StatusEffect lv = StatusEffect.byRawId(i);
        if (lv == null) {
            return null;
        }
        return StatusEffectInstance.fromTag(lv, arg);
    }

    private static StatusEffectInstance fromTag(StatusEffect arg, CompoundTag arg2) {
        byte i = arg2.getByte("Amplifier");
        int j = arg2.getInt("Duration");
        boolean bl = arg2.getBoolean("Ambient");
        boolean bl2 = true;
        if (arg2.contains("ShowParticles", 1)) {
            bl2 = arg2.getBoolean("ShowParticles");
        }
        boolean bl3 = bl2;
        if (arg2.contains("ShowIcon", 1)) {
            bl3 = arg2.getBoolean("ShowIcon");
        }
        StatusEffectInstance lv = null;
        if (arg2.contains("HiddenEffect", 10)) {
            lv = StatusEffectInstance.fromTag(arg, arg2.getCompound("HiddenEffect"));
        }
        return new StatusEffectInstance(arg, j, i < 0 ? (byte)0 : i, bl, bl2, bl3, lv);
    }

    @Environment(value=EnvType.CLIENT)
    public void setPermanent(boolean bl) {
        this.permanent = bl;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isPermanent() {
        return this.permanent;
    }

    @Override
    public int compareTo(StatusEffectInstance arg) {
        int i = 32147;
        if (this.getDuration() > 32147 && arg.getDuration() > 32147 || this.isAmbient() && arg.isAmbient()) {
            return ComparisonChain.start().compare(Boolean.valueOf(this.isAmbient()), Boolean.valueOf(arg.isAmbient())).compare(this.getEffectType().getColor(), arg.getEffectType().getColor()).result();
        }
        return ComparisonChain.start().compare(Boolean.valueOf(this.isAmbient()), Boolean.valueOf(arg.isAmbient())).compare(this.getDuration(), arg.getDuration()).compare(this.getEffectType().getColor(), arg.getEffectType().getColor()).result();
    }

    @Override
    public /* synthetic */ int compareTo(Object object) {
        return this.compareTo((StatusEffectInstance)object);
    }
}

