/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.projectile;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class ArrowEntity
extends PersistentProjectileEntity {
    private static final TrackedData<Integer> COLOR = DataTracker.registerData(ArrowEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private Potion potion = Potions.EMPTY;
    private final Set<StatusEffectInstance> effects = Sets.newHashSet();
    private boolean colorSet;

    public ArrowEntity(EntityType<? extends ArrowEntity> arg, World arg2) {
        super((EntityType<? extends PersistentProjectileEntity>)arg, arg2);
    }

    public ArrowEntity(World arg, double d, double e, double f) {
        super(EntityType.ARROW, d, e, f, arg);
    }

    public ArrowEntity(World arg, LivingEntity arg2) {
        super(EntityType.ARROW, arg2, arg);
    }

    public void initFromStack(ItemStack arg) {
        if (arg.getItem() == Items.TIPPED_ARROW) {
            int i;
            this.potion = PotionUtil.getPotion(arg);
            List<StatusEffectInstance> collection = PotionUtil.getCustomPotionEffects(arg);
            if (!collection.isEmpty()) {
                for (StatusEffectInstance lv : collection) {
                    this.effects.add(new StatusEffectInstance(lv));
                }
            }
            if ((i = ArrowEntity.getCustomPotionColor(arg)) == -1) {
                this.initColor();
            } else {
                this.setColor(i);
            }
        } else if (arg.getItem() == Items.ARROW) {
            this.potion = Potions.EMPTY;
            this.effects.clear();
            this.dataTracker.set(COLOR, -1);
        }
    }

    public static int getCustomPotionColor(ItemStack arg) {
        CompoundTag lv = arg.getTag();
        if (lv != null && lv.contains("CustomPotionColor", 99)) {
            return lv.getInt("CustomPotionColor");
        }
        return -1;
    }

    private void initColor() {
        this.colorSet = false;
        if (this.potion == Potions.EMPTY && this.effects.isEmpty()) {
            this.dataTracker.set(COLOR, -1);
        } else {
            this.dataTracker.set(COLOR, PotionUtil.getColor(PotionUtil.getPotionEffects(this.potion, this.effects)));
        }
    }

    public void addEffect(StatusEffectInstance arg) {
        this.effects.add(arg);
        this.getDataTracker().set(COLOR, PotionUtil.getColor(PotionUtil.getPotionEffects(this.potion, this.effects)));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(COLOR, -1);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.world.isClient) {
            if (this.inGround) {
                if (this.inGroundTime % 5 == 0) {
                    this.spawnParticles(1);
                }
            } else {
                this.spawnParticles(2);
            }
        } else if (this.inGround && this.inGroundTime != 0 && !this.effects.isEmpty() && this.inGroundTime >= 600) {
            this.world.sendEntityStatus(this, (byte)0);
            this.potion = Potions.EMPTY;
            this.effects.clear();
            this.dataTracker.set(COLOR, -1);
        }
    }

    private void spawnParticles(int i) {
        int j = this.getColor();
        if (j == -1 || i <= 0) {
            return;
        }
        double d = (double)(j >> 16 & 0xFF) / 255.0;
        double e = (double)(j >> 8 & 0xFF) / 255.0;
        double f = (double)(j >> 0 & 0xFF) / 255.0;
        for (int k = 0; k < i; ++k) {
            this.world.addParticle(ParticleTypes.ENTITY_EFFECT, this.getParticleX(0.5), this.getRandomBodyY(), this.getParticleZ(0.5), d, e, f);
        }
    }

    public int getColor() {
        return this.dataTracker.get(COLOR);
    }

    private void setColor(int i) {
        this.colorSet = true;
        this.dataTracker.set(COLOR, i);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        if (this.potion != Potions.EMPTY && this.potion != null) {
            arg.putString("Potion", Registry.POTION.getId(this.potion).toString());
        }
        if (this.colorSet) {
            arg.putInt("Color", this.getColor());
        }
        if (!this.effects.isEmpty()) {
            ListTag lv = new ListTag();
            for (StatusEffectInstance lv2 : this.effects) {
                lv.add(lv2.toTag(new CompoundTag()));
            }
            arg.put("CustomPotionEffects", lv);
        }
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        if (arg.contains("Potion", 8)) {
            this.potion = PotionUtil.getPotion(arg);
        }
        for (StatusEffectInstance lv : PotionUtil.getCustomPotionEffects(arg)) {
            this.addEffect(lv);
        }
        if (arg.contains("Color", 99)) {
            this.setColor(arg.getInt("Color"));
        } else {
            this.initColor();
        }
    }

    @Override
    protected void onHit(LivingEntity arg) {
        super.onHit(arg);
        for (StatusEffectInstance lv : this.potion.getEffects()) {
            arg.addStatusEffect(new StatusEffectInstance(lv.getEffectType(), Math.max(lv.getDuration() / 8, 1), lv.getAmplifier(), lv.isAmbient(), lv.shouldShowParticles()));
        }
        if (!this.effects.isEmpty()) {
            for (StatusEffectInstance lv2 : this.effects) {
                arg.addStatusEffect(lv2);
            }
        }
    }

    @Override
    protected ItemStack asItemStack() {
        if (this.effects.isEmpty() && this.potion == Potions.EMPTY) {
            return new ItemStack(Items.ARROW);
        }
        ItemStack lv = new ItemStack(Items.TIPPED_ARROW);
        PotionUtil.setPotion(lv, this.potion);
        PotionUtil.setCustomPotionEffects(lv, this.effects);
        if (this.colorSet) {
            lv.getOrCreateTag().putInt("CustomPotionColor", this.getColor());
        }
        return lv;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte b) {
        if (b == 0) {
            int i = this.getColor();
            if (i != -1) {
                double d = (double)(i >> 16 & 0xFF) / 255.0;
                double e = (double)(i >> 8 & 0xFF) / 255.0;
                double f = (double)(i >> 0 & 0xFF) / 255.0;
                for (int j = 0; j < 20; ++j) {
                    this.world.addParticle(ParticleTypes.ENTITY_EFFECT, this.getParticleX(0.5), this.getRandomBodyY(), this.getParticleZ(0.5), d, e, f);
                }
            }
        } else {
            super.handleStatus(b);
        }
    }
}

