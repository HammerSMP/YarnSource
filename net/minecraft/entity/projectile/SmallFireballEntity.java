/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.projectile;

import net.minecraft.block.AbstractFireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class SmallFireballEntity
extends AbstractFireballEntity {
    public SmallFireballEntity(EntityType<? extends SmallFireballEntity> arg, World arg2) {
        super((EntityType<? extends AbstractFireballEntity>)arg, arg2);
    }

    public SmallFireballEntity(World arg, LivingEntity arg2, double d, double e, double f) {
        super((EntityType<? extends AbstractFireballEntity>)EntityType.SMALL_FIREBALL, arg2, d, e, f, arg);
    }

    public SmallFireballEntity(World arg, double d, double e, double f, double g, double h, double i) {
        super((EntityType<? extends AbstractFireballEntity>)EntityType.SMALL_FIREBALL, d, e, f, g, h, i, arg);
    }

    @Override
    protected void onEntityHit(EntityHitResult arg) {
        super.onEntityHit(arg);
        if (this.world.isClient) {
            return;
        }
        Entity lv = arg.getEntity();
        if (!lv.isFireImmune()) {
            Entity lv2 = this.getOwner();
            int i = lv.getFireTicks();
            lv.setOnFireFor(5);
            boolean bl = lv.damage(DamageSource.fireball(this, lv2), 5.0f);
            if (!bl) {
                lv.setFireTicks(i);
            } else if (lv2 instanceof LivingEntity) {
                this.dealDamage((LivingEntity)lv2, lv);
            }
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult arg) {
        BlockHitResult lv2;
        BlockPos lv3;
        super.onBlockHit(arg);
        if (this.world.isClient) {
            return;
        }
        Entity lv = this.getOwner();
        if ((lv == null || !(lv instanceof MobEntity) || this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) && this.world.isAir(lv3 = (lv2 = arg).getBlockPos().offset(lv2.getSide()))) {
            this.world.setBlockState(lv3, AbstractFireBlock.getState(this.world, lv3));
        }
    }

    @Override
    protected void onCollision(HitResult arg) {
        super.onCollision(arg);
        if (!this.world.isClient) {
            this.remove();
        }
    }

    @Override
    public boolean collides() {
        return false;
    }

    @Override
    public boolean damage(DamageSource arg, float f) {
        return false;
    }
}

