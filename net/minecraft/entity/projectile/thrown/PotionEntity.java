/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.EnvironmentInterface
 *  net.fabricmc.api.EnvironmentInterfaces
 */
package net.minecraft.entity.projectile.thrown;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

@EnvironmentInterfaces(value={@EnvironmentInterface(value=EnvType.CLIENT, itf=FlyingItemEntity.class)})
public class PotionEntity
extends ThrownItemEntity
implements FlyingItemEntity {
    public static final Predicate<LivingEntity> WATER_HURTS = LivingEntity::hurtByWater;

    public PotionEntity(EntityType<? extends PotionEntity> arg, World arg2) {
        super((EntityType<? extends ThrownItemEntity>)arg, arg2);
    }

    public PotionEntity(World world, LivingEntity owner) {
        super((EntityType<? extends ThrownItemEntity>)EntityType.POTION, owner, world);
    }

    public PotionEntity(World world, double x, double y, double z) {
        super((EntityType<? extends ThrownItemEntity>)EntityType.POTION, x, y, z, world);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.SPLASH_POTION;
    }

    @Override
    protected float getGravity() {
        return 0.05f;
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        if (this.world.isClient) {
            return;
        }
        ItemStack lv = this.getStack();
        Potion lv2 = PotionUtil.getPotion(lv);
        List<StatusEffectInstance> list = PotionUtil.getPotionEffects(lv);
        boolean bl = lv2 == Potions.WATER && list.isEmpty();
        Direction lv3 = blockHitResult.getSide();
        BlockPos lv4 = blockHitResult.getBlockPos();
        BlockPos lv5 = lv4.offset(lv3);
        if (bl) {
            this.extinguishFire(lv5, lv3);
            this.extinguishFire(lv5.offset(lv3.getOpposite()), lv3);
            for (Direction lv6 : Direction.Type.HORIZONTAL) {
                this.extinguishFire(lv5.offset(lv6), lv6);
            }
        }
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        boolean bl;
        super.onCollision(hitResult);
        if (this.world.isClient) {
            return;
        }
        ItemStack lv = this.getStack();
        Potion lv2 = PotionUtil.getPotion(lv);
        List<StatusEffectInstance> list = PotionUtil.getPotionEffects(lv);
        boolean bl2 = bl = lv2 == Potions.WATER && list.isEmpty();
        if (bl) {
            this.damageEntitiesHurtByWater();
        } else if (!list.isEmpty()) {
            if (this.isLingering()) {
                this.applyLingeringPotion(lv, lv2);
            } else {
                this.applySplashPotion(list, hitResult.getType() == HitResult.Type.ENTITY ? ((EntityHitResult)hitResult).getEntity() : null);
            }
        }
        int i = lv2.hasInstantEffect() ? 2007 : 2002;
        this.world.syncWorldEvent(i, this.getBlockPos(), PotionUtil.getColor(lv));
        this.remove();
    }

    private void damageEntitiesHurtByWater() {
        Box lv = this.getBoundingBox().expand(4.0, 2.0, 4.0);
        List<LivingEntity> list = this.world.getEntities(LivingEntity.class, lv, WATER_HURTS);
        if (!list.isEmpty()) {
            for (LivingEntity lv2 : list) {
                double d = this.squaredDistanceTo(lv2);
                if (!(d < 16.0) || !lv2.hurtByWater()) continue;
                lv2.damage(DamageSource.magic(lv2, this.getOwner()), 1.0f);
            }
        }
    }

    private void applySplashPotion(List<StatusEffectInstance> statusEffects, @Nullable Entity entity) {
        Box lv = this.getBoundingBox().expand(4.0, 2.0, 4.0);
        List<LivingEntity> list2 = this.world.getNonSpectatingEntities(LivingEntity.class, lv);
        if (!list2.isEmpty()) {
            for (LivingEntity lv2 : list2) {
                double d;
                if (!lv2.isAffectedBySplashPotions() || !((d = this.squaredDistanceTo(lv2)) < 16.0)) continue;
                double e = 1.0 - Math.sqrt(d) / 4.0;
                if (lv2 == entity) {
                    e = 1.0;
                }
                for (StatusEffectInstance lv3 : statusEffects) {
                    StatusEffect lv4 = lv3.getEffectType();
                    if (lv4.isInstant()) {
                        lv4.applyInstantEffect(this, this.getOwner(), lv2, lv3.getAmplifier(), e);
                        continue;
                    }
                    int i = (int)(e * (double)lv3.getDuration() + 0.5);
                    if (i <= 20) continue;
                    lv2.addStatusEffect(new StatusEffectInstance(lv4, i, lv3.getAmplifier(), lv3.isAmbient(), lv3.shouldShowParticles()));
                }
            }
        }
    }

    private void applyLingeringPotion(ItemStack stack, Potion potion) {
        AreaEffectCloudEntity lv = new AreaEffectCloudEntity(this.world, this.getX(), this.getY(), this.getZ());
        Entity lv2 = this.getOwner();
        if (lv2 instanceof LivingEntity) {
            lv.setOwner((LivingEntity)lv2);
        }
        lv.setRadius(3.0f);
        lv.setRadiusOnUse(-0.5f);
        lv.setWaitTime(10);
        lv.setRadiusGrowth(-lv.getRadius() / (float)lv.getDuration());
        lv.setPotion(potion);
        for (StatusEffectInstance lv3 : PotionUtil.getCustomPotionEffects(stack)) {
            lv.addEffect(new StatusEffectInstance(lv3));
        }
        CompoundTag lv4 = stack.getTag();
        if (lv4 != null && lv4.contains("CustomPotionColor", 99)) {
            lv.setColor(lv4.getInt("CustomPotionColor"));
        }
        this.world.spawnEntity(lv);
    }

    private boolean isLingering() {
        return this.getStack().getItem() == Items.LINGERING_POTION;
    }

    private void extinguishFire(BlockPos pos, Direction direction) {
        BlockState lv = this.world.getBlockState(pos);
        if (lv.isIn(BlockTags.FIRE)) {
            this.world.removeBlock(pos, false);
        } else if (CampfireBlock.isLitCampfire(lv)) {
            this.world.syncWorldEvent(null, 1009, pos, 0);
            CampfireBlock.extinguish(this.world, pos, lv);
            this.world.setBlockState(pos, (BlockState)lv.with(CampfireBlock.LIT, false));
        }
    }
}

