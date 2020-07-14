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

    public StatusEffectInstance(StatusEffect type, int duration) {
        this(type, duration, 0);
    }

    public StatusEffectInstance(StatusEffect type, int duration, int amplifier) {
        this(type, duration, amplifier, false, true);
    }

    public StatusEffectInstance(StatusEffect type, int duration, int amplifier, boolean ambient, boolean visible) {
        this(type, duration, amplifier, ambient, visible, visible);
    }

    public StatusEffectInstance(StatusEffect type, int duration, int amplifier, boolean ambient, boolean showParticles, boolean showIcon) {
        this(type, duration, amplifier, ambient, showParticles, showIcon, null);
    }

    public StatusEffectInstance(StatusEffect type, int duration, int amplifier, boolean ambient, boolean showParticles, boolean showIcon, @Nullable StatusEffectInstance hiddenEffect) {
        this.type = type;
        this.duration = duration;
        this.amplifier = amplifier;
        this.ambient = ambient;
        this.showParticles = showParticles;
        this.showIcon = showIcon;
        this.hiddenEffect = hiddenEffect;
    }

    public StatusEffectInstance(StatusEffectInstance arg) {
        this.type = arg.type;
        this.copyFrom(arg);
    }

    void copyFrom(StatusEffectInstance that) {
        this.duration = that.duration;
        this.amplifier = that.amplifier;
        this.ambient = that.ambient;
        this.showParticles = that.showParticles;
        this.showIcon = that.showIcon;
    }

    public boolean upgrade(StatusEffectInstance that) {
        if (this.type != that.type) {
            LOGGER.warn("This method should only be called for matching effects!");
        }
        boolean bl = false;
        if (that.amplifier > this.amplifier) {
            if (that.duration < this.duration) {
                StatusEffectInstance lv = this.hiddenEffect;
                this.hiddenEffect = new StatusEffectInstance(this);
                this.hiddenEffect.hiddenEffect = lv;
            }
            this.amplifier = that.amplifier;
            this.duration = that.duration;
            bl = true;
        } else if (that.duration > this.duration) {
            if (that.amplifier == this.amplifier) {
                this.duration = that.duration;
                bl = true;
            } else if (this.hiddenEffect == null) {
                this.hiddenEffect = new StatusEffectInstance(that);
            } else {
                this.hiddenEffect.upgrade(that);
            }
        }
        if (!that.ambient && this.ambient || bl) {
            this.ambient = that.ambient;
            bl = true;
        }
        if (that.showParticles != this.showParticles) {
            this.showParticles = that.showParticles;
            bl = true;
        }
        if (that.showIcon != this.showIcon) {
            this.showIcon = that.showIcon;
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

    public boolean update(LivingEntity entity, Runnable overwriteCallback) {
        if (this.duration > 0) {
            if (this.type.canApplyUpdateEffect(this.duration, this.amplifier)) {
                this.applyUpdateEffect(entity);
            }
            this.updateDuration();
            if (this.duration == 0 && this.hiddenEffect != null) {
                this.copyFrom(this.hiddenEffect);
                this.hiddenEffect = this.hiddenEffect.hiddenEffect;
                overwriteCallback.run();
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

    public void applyUpdateEffect(LivingEntity entity) {
        if (this.duration > 0) {
            this.type.applyUpdateEffect(entity, this.amplifier);
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

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof StatusEffectInstance) {
            StatusEffectInstance lv = (StatusEffectInstance)o;
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

    public CompoundTag toTag(CompoundTag tag) {
        tag.putByte("Id", (byte)StatusEffect.getRawId(this.getEffectType()));
        this.typelessToTag(tag);
        return tag;
    }

    private void typelessToTag(CompoundTag tag) {
        tag.putByte("Amplifier", (byte)this.getAmplifier());
        tag.putInt("Duration", this.getDuration());
        tag.putBoolean("Ambient", this.isAmbient());
        tag.putBoolean("ShowParticles", this.shouldShowParticles());
        tag.putBoolean("ShowIcon", this.shouldShowIcon());
        if (this.hiddenEffect != null) {
            CompoundTag lv = new CompoundTag();
            this.hiddenEffect.toTag(lv);
            tag.put("HiddenEffect", lv);
        }
    }

    public static StatusEffectInstance fromTag(CompoundTag tag) {
        byte i = tag.getByte("Id");
        StatusEffect lv = StatusEffect.byRawId(i);
        if (lv == null) {
            return null;
        }
        return StatusEffectInstance.fromTag(lv, tag);
    }

    private static StatusEffectInstance fromTag(StatusEffect type, CompoundTag tag) {
        byte i = tag.getByte("Amplifier");
        int j = tag.getInt("Duration");
        boolean bl = tag.getBoolean("Ambient");
        boolean bl2 = true;
        if (tag.contains("ShowParticles", 1)) {
            bl2 = tag.getBoolean("ShowParticles");
        }
        boolean bl3 = bl2;
        if (tag.contains("ShowIcon", 1)) {
            bl3 = tag.getBoolean("ShowIcon");
        }
        StatusEffectInstance lv = null;
        if (tag.contains("HiddenEffect", 10)) {
            lv = StatusEffectInstance.fromTag(type, tag.getCompound("HiddenEffect"));
        }
        return new StatusEffectInstance(type, j, i < 0 ? (byte)0 : i, bl, bl2, bl3, lv);
    }

    @Environment(value=EnvType.CLIENT)
    public void setPermanent(boolean permanent) {
        this.permanent = permanent;
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
    public /* synthetic */ int compareTo(Object that) {
        return this.compareTo((StatusEffectInstance)that);
    }
}

